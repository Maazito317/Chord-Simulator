/*
package CourseProject

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import scala.language.postfixOps


object NodeActor{
  case class addNode (node:Node)
  case object GetSuccessorList
  case object FindSuccessor
  case class Closestpreceding(node: Node)
  case object GenerateRequest
  case class removeSuccessor(node:Node)


}

class NodeActor(val succ: SuccessorList) extends Actor {
  import NodeActor._
  //val succ = new SuccessorList(node:Node)

  def receive: Receive = {

    case ping() => sender ! "pong!"

    case addNode(node:Node) =>{
      //println("Adding node ...")
      succ.addSuccessor(node)
      println("Node added!")

    }
    case GetSuccessorList =>{
      println("Getting current SuccessorList ...")
      succ.getList()
    }

    case FindSuccessor =>{
      println("Returning successor ....")
      val successor = succ.getSuccessor()
      if(successor == null){
        println("no successor found")
      }
      else
        println(s"Successor node: ${successor}")

    }
    case GenerateRequest =>{
      println("Request generated")
    }
    //case request_predecessor() => sender ! references.getPredecessor

    case removeSuccessor(node:Node) => {
      println(s"Removing node ${node} ...")
      succ.removeSuccessor(node)
      println("Node removed!")
    }

    case Closestpreceding(node:Node) =>{
      println("Closest preceding node..")
      val closestNode = succ.getClosestPrecedingNode(node.getKey)
      println(s"Closest node ${closestNode} found")
    }
  }

}

object MasterNode{
  case object StartRouting
  case object CreateNodeDetails
  case object Stabilization
  case class NewNode(node:Node)
}

class MasterNode(apollo:ActorRef) extends Actor {
  import MasterNode._
  def receive: Receive={
    case StartRouting =>{
      println("Start Routing ...")
      apollo ! NodeActor.GenerateRequest
    }

    case CreateNodeDetails=>{
      apollo ! NodeActor.FindSuccessor
    }

    case NewNode(node: Node) =>{
      println(s"Adding node ${node}")
      apollo ! NodeActor.addNode(node)
      //      apollo ! NodeActor.AddNode(node)}
    }

      //update others
    case Stabilization=>{
      println("stabilization")

    }
  }
}


object actor extends App {

  // Create the 'creation' actor system
  val system = ActorSystem("further")
  val apollo = system.actorOf(Props(classOf[NodeActor]))
  //  // Create the 'Zeus' actor
  val zeus = system.actorOf(Props(classOf[MasterNode],apollo), "zeus")


  zeus ! MasterNode.StartRouting

  zeus ! MasterNode.NewNode(new Node(3,"pulp fiction"))
  zeus ! MasterNode.CreateNodeDetails
  zeus ! MasterNode.NewNode(new Node(4, "forest gump"))
  zeus ! MasterNode.NewNode(new Node(4, "forest gump")) //node duplicated



  //shutdown system
  system.terminate()
}
*/
