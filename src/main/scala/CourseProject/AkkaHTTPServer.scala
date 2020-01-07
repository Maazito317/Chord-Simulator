package CourseProject

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.adapter._
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import scala.util.Failure
import scala.util.Success

object AkkaHTTPServer {

  private def startHttpServer(routes: Route, system: ActorSystem[_]): Unit = {

    implicit val classicSystem: akka.actor.ActorSystem = system.toClassic
    import system.executionContext

    val bind = Http().bindAndHandle(routes, "localhost", 6789)

    bind.onComplete {
      case Success(binding) =>
        val url = binding.localAddress
        system.log.info("HTTP Server online at http://{}:{}/", url.getHostString, url.getPort)
      case Failure(ex) =>
        system.log.error("Failed to bind Server, stopping ...", ex)
        system.terminate()
    }
  }

  // we can also use this class to fire up our simulation and other actors
  // nodeRegistry is basically our node master
  def main(): Unit = {

    val rootBehavior = Behaviors.setup[Nothing] { context =>
      //val nodeRegistryActor = context.spawn(NodeRegistry(), "NodeActor")
      val nodeRegistryActor = context.spawn(Chord(), "NodeActor")

      context.watch(nodeRegistryActor)

      //create route based on a nodeRegistry class's Actor
      //the route will process REST URLs and call the appropriate functions
      val routes = new Routes(nodeRegistryActor)(context.system)
      //call the function to start the server with the route you created
      startHttpServer(routes.Routes, context.system)

      Behaviors.empty
    }
    val system = ActorSystem[Nothing](rootBehavior, "AkkaServer")

  }
}
