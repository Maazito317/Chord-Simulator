package CourseProject

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

import scala.collection.immutable
import akka.actor.{Actor, ActorSystem, Props}
import akka.util.Timeout
import java.util.concurrent.TimeUnit

import CourseProject.Chord._
import CourseProject.Main.{conf, system}
import org.slf4j.LoggerFactory
//import CourseProject.UserActor.{getNodeList, poke, readRequest}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.collection.mutable.Map
import scala.collection.immutable



final case class Movie(name: String, revenue: String)
final case class Movies(movies: immutable.Seq[Movie])



object Chord {  //define the receive methods (case classes) of the User Actor
  sealed trait Command
  case class addToNode(m: Movie) extends Command
  case class getEntry(title: String) extends Command
  case class createEntry(m: Movie) extends Command
  case class fixSuccList() extends Command
  case class checkPred(n: Node) extends Command
  case class checkSucc(n: Node) extends Command

  final case class CreateMovie(movie: Movie, replyTo: ActorRef[ActionPerformed]) extends Command
  final case class GetMovie(name: String, replyTo: ActorRef[GetResponse]) extends Command
  //final case class DeleteMovie(name: String, replyTo: ActorRef[ActionPerformed]) extends Command
  final case class GetMovies(replyTo: ActorRef[Movies]) extends Command
  final case class GetResponse(maybe: Option[Movie])
  final case class ActionPerformed(description: String)



  def apply(): Behavior[Command] = nodes(Set.empty)

  // define the nodes object as a set of functions that react to behavior commands
  // define the case classes and what they respond to
  private def nodes(movies: Set[Movie]): Behavior[Command] =
    Behaviors.receiveMessage {

      case CreateMovie(movie, replyTo) =>
        replyTo ! ActionPerformed(s"The movie - ${movie.name} - was created.")
        nodes(movies + movie)
        createEntry(movie)
        Behaviors.same

      case GetMovie(name, replyTo) =>
        replyTo ! GetResponse(movies.find(_.name == name))
        //replyTo ! GetResponse(getEntry(name))
        getEntry(name)
        Behaviors.same

      case GetMovies(replyTo) =>
        replyTo ! Movies(movies.toSeq)
        Behaviors.same

      //      case DeleteMovie(name, replyTo) =>
      //        replyTo ! ActionPerformed(s"The movie - $name - was deleted.")
      //        nodes(movies.filterNot(_.name == name))
    }



}

class Chord extends Actor {

  implicit val timeout = Timeout(5, TimeUnit.SECONDS)
  val logger = LoggerFactory.getLogger(this.getClass)

  private val numNodes = conf.getInt("numNodes")
  //val nodeList = new Array[Node](numNodes)

  val nodeList = (for { i <- 0 until numNodes } yield {
    new Node(i)
  }).toArray

  self ! fixSuccList()

  private var currIndex = 0

  def receive = {

    case addToNode(m: Movie) => {
      val currNode = nodeList(currIndex)
      if (currNode != null) {
        val hashID = Hasher.sha1HashInt(m.name)
        currNode.add(new Entry(hashID, m.name, m.revenue))
        //if index is last node in array, loop back to beginning
        if(currIndex >= numNodes-1)
          currIndex = 0
        else
          currIndex += 1
      }
      else
        logger.error("Current Node reference is null")
    }

    //When passed Movie Data
    case createEntry(m: Movie) => {
      val hashID = Hasher.sha1HashInt(m.name)
      //search for this movie entry in all nodes
      var movieFound = false
      for (n <- nodeList) {
        for(e <- n.getEntries()) {
          //if movie is found
          if (e.getKey() == hashID) {
            movieFound = true
          }
        }
      }
      if(!movieFound){
        self ! addToNode(m)
      }
    }

    case getEntry(title: String) => {
      val hashID = Hasher.sha1HashInt(title)
      for (n <- nodeList) {
        for (e <- n.getEntries()) {
          //if movie is found
          if (e.getKey == hashID) {
            sender ! Movie(e.getTitle().toString, e.getRevenue().toString)
            logger.error(">> CHORD - ENTRY FOUND")
          }
        }
      }
    }

    case fixSuccList() => {
      for(n <- nodeList) {
        var inc = 1
        for(i <- 0 until numNodes-1) {
          //if current node's next node is after the loopback
          if (n.getKey + inc >= numNodes - 1) {
            if (n.getKey+inc < numNodes) {
              n.getSuccList.addSuccessor(nodeList(n.getKey + inc))
              inc = -n.getKey
            }
            //boundary case for last node before loopback
            else if(n.getKey == numNodes-1) {
              n.getSuccList.addSuccessor(nodeList(0))
              inc = -n.getKey+1
            }
          }
          else {
            if (n.getKey+inc < numNodes) {
              n.getSuccList.addSuccessor(nodeList(n.getKey + inc))
              inc += 1
            }
          }
        }
      }
    }

    /*case checkPred(n: Node) => {
      val pred = n.getSuccList.getClosestPrecedingNode(n.getKey)
      val succ = n.getSuccList.getSuccessor()

      if (pred != null && !pred.equals(n)) {

        try {
          val check = n.self ? ping()
          Await.result(check, Duration.create(5, "seconds"))
          //Node is responsive - Do work
        }
        catch {
          case e:Exception =>
          //if predecessor is dead, and there are backup entries for it, transfer to successor
          logger.info("Node " + pred.getKey + " is non-responsive")
          //at this point, the dead node needs to removed from all instances of SuccessorList
          for(nd <- nodeList) {
            nd.getSuccList.removeSuccessor(pred)
          }
          //TODO: update finger table
          logger.info("Removing backup entries for Node " + pred.getKey + " and passing it to its successor")
          val backup = pred.getBackup
          if (backup != null) {
            pred.clearBackup()
            succ.setBackup(backup)
          }
        }
      }
    }

    case checkSucc(n: Node) => {
      val succ = n.getSuccList.getSuccessor

      if (succ != null && !succ.equals(n)) {
        //if successor is dead, update finger table + successor list
        try {
          val check = n.self ? ping()
          Await.result(check, Duration.create(5, "seconds"))
          //Node is responsive - Do work
          val backup = n.getBackup
          if (backup != null) {
            //move 'backup' to succ node
            succ.setBackup(backup)
          }
        }
        catch {
          case e:Exception =>
          logger.info("Node " + succ.getKey + " is non-responsive")
          //at this point, the dead node needs to removed from all instances of SuccessorList
          for(nd <- nodeList) {
            nd.getSuccList.removeSuccessor(succ)
          }
          //TODO: update finger table
        }
      }*/


    }//end receive

}//end Chord
