//package CourseProject
//
//import akka.actor.typed.ActorRef
//import akka.actor.typed.Behavior
//import akka.actor.typed.scaladsl.Behaviors
//import scala.collection.immutable
//
////final case class Movie(name: String, revenue: String)
////final case class Movies(movies: immutable.Seq[Movie])
//
//// this code will likely be in the node class, or node creator class?
//object NodeRegistry {
//  sealed trait Command
//
//  // define the case classes, AKA the functions that the HTTP service calls
//  final case class CreateMovie(movie: Movie, replyTo: ActorRef[ActionPerformed]) extends Command
//  final case class DeleteMovie(name: String, replyTo: ActorRef[ActionPerformed]) extends Command
//  final case class GetMovie(name: String, replyTo: ActorRef[GetResponse]) extends Command
//  final case class GetMovies(replyTo: ActorRef[Movies]) extends Command
//
//  final case class GetResponse(maybe: Option[Movie])
//  final case class ActionPerformed(description: String)
//
//  def apply(): Behavior[Command] = nodes(Set.empty)
//
//  // define the nodes object as a set of functions that react to behavior commands
//  // define the case classes and what they respond to
//  private def nodes(movies: Set[Movie]): Behavior[Command] =
//    Behaviors.receiveMessage {
//
//      case GetMovies(replyTo) =>
//        replyTo ! Movies(movies.toSeq)
//        Behaviors.same
//
//      case CreateMovie(movie, replyTo) =>
//        replyTo ! ActionPerformed(s"The movie - ${movie.name} - was created.")
//        nodes(movies + movie)
//
//      case GetMovie(name, replyTo) =>
//        replyTo ! GetResponse(movies.find(_.name == name))
//        Behaviors.same
//
//      case DeleteMovie(name, replyTo) =>
//        replyTo ! ActionPerformed(s"The movie - $name - was deleted.")
//        nodes(movies.filterNot(_.name == name))
//    }
//
//
//}
