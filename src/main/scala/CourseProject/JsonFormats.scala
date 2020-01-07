package CourseProject

import CourseProject.Chord.ActionPerformed
import spray.json.DefaultJsonProtocol

object JsonFormats  {
  // import the default encoders for basic types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  // these definitions define how many parameters to receive from REST JSON bodies
  implicit val movieJsonFormat = jsonFormat2(Movie)   //jsonFormat2 means it takes 2 parameters, jsonFormat3 takes 3 params, etc.
  implicit val moviesJsonFormat = jsonFormat1(Movies)
  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
}
