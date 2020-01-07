package CourseProject

import akka.http.scaladsl.server.Directives.{get, _}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route

import scala.concurrent.Future
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout
//import CourseProject.NodeRegistry._
import CourseProject.Chord._

class Routes(nodeRegistry: ActorRef[Chord.Command])(implicit val system: ActorSystem[_]) {

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._

  // If ask takes more time than this to complete the request is failed
  private implicit val timeout = Timeout.create(system.settings.config.getDuration("Master.timeout"))  // 5s


  //these functions basically forward the request to the nodeRegistry class and replaces the + symbols with spaces for multi word titles
  def createMovie(movie: Movie): Future[ActionPerformed] =
    nodeRegistry.ask(CreateMovie(movie, _))

  def getMovie(name: String): Future[GetResponse] =
    nodeRegistry.ask(GetMovie(name.replace('+', ' '), _))

    def getMovies(): Future[Movies] =
      nodeRegistry.ask(GetMovies)

  //  def deleteMovie(name: String): Future[ActionPerformed] =
  //    nodeRegistry.ask(DeleteMovie(name.replace('+', ' '), _))



  val Routes: Route =
    pathPrefix("movies") {  //url prefix must have this at the end    /movies
      concat(
        pathEnd {
          concat(
            post {  //this creates 1 movie.
              //use cmd:   curl -H "Content-type: application/json" -X POST -d '{"name": "Harry Potter", "revenue": "99"}' http://localhost:6789/movies
              entity(as[Movie]) { movie =>
                onSuccess(createMovie(movie)) { performed =>
                  complete((StatusCodes.Created, performed))
                }
              }
            },
                        get {   //this gets all movies.
                          //use cmd:    curl http://localhost:6789/movies
                          complete(getMovies())
                        }
          )
        },
        path(Segment) { name =>
          concat(
//                        delete {  //this is for deleting entries based off the title. (must have + for spaces)
//                          //use cmd:    curl -X DELETE http://localhost:6789/movies/Harry+Potter
//                          onSuccess(deleteMovie(name)) { performed =>
//                            complete((StatusCodes.OK, performed))
//                          }
//                        },
            get {   //retrieves 1 entry based off the title. Here it uses the whole segment to catch multi-word titles (must have + for spaces)
              //use cmd:     curl http://localhost:6789/movies/Harry+Potter
              rejectEmptyResponse {
                onSuccess(getMovie(name)) { response =>
                  complete(response.maybe)
                }
              }
            })
        })
    }
}