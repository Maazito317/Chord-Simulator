package CourseProject

import java.util

import CourseProject.Main.system
import akka.actor.Actor
import org.slf4j.LoggerFactory
import akka.actor.Timers

import scala.language.postfixOps
import scala.language.postfixOps
import scala.sys.process._
import com.google.gson.Gson
import org.apache.http.client.methods.HttpPost

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks.{break, breakable}
import java.io._
import java.net.{HttpURLConnection, URL}

import org.apache.commons._

import scala.io.Source
import org.apache.http._
import org.apache.http.client._
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import java.util.ArrayList

import org.apache.http.message.BasicNameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import com.google.gson.Gson


object UserActor {  //define the receive methods (case classes) of the User Actor
  sealed trait Command
  case class readRequest() extends Command
  case class writeRequest() extends Command
}


class UserActor(id: Int) extends Actor with Timers{
  import system.dispatcher

  import UserActor._
  val ID = id
  var writtenMovies = new ArrayBuffer[String]()
  val logger = LoggerFactory.getLogger(this.getClass)


  def receive = {


    case readRequest() =>
      val rand = scala.util.Random
      //this picks a random movie that they've written
      // (since we don't want them to randomly pick ANY movie in the csv file, because they'd get null for most of them, because most of those probably haven't been added)
      var i=0
      if (writtenMovies.isEmpty) {
        i =0
        logger.info("> ERROR - tried to read but hasn't written anything yet" )
        //break
      }
      else {
        i = rand.nextInt(writtenMovies.length)

        val string = writtenMovies(i).replace(' ', '+')

        //needs this format -     curl http://localhost:6789/movies/Harry+Potter
        var s = "http://localhost:6789/movies/"
        s= s.concat(string)
        val cmd = Seq("curl", s)

        logger.info("> User #"+ID+": READ Request: "+cmd.mkString)

        val result = cmd.!!
        logger.info("> User #"+ID+": READ Result: "+result)

      }


    case writeRequest() =>
      val rand = scala.util.Random

      val lines = Source.fromResource("Movies.csv").getLines.toList
      //the following is the columns of the movies.csv file
      //Rank,Title,Genre,Description,Director,Actors,Year,Runtime (Minutes),Rating,Votes,Revenue (Millions),Metascore

      val headerLine = lines.drop(1)  //drop first line, which is column headers
      val i = rand.nextInt(999)+1      //get random integer within the lines of the csv file, to pick a random line
      val randomLine = lines(i)
      var row = new Array[String](30)

      row = randomLine.split(",").map(_.trim) //drop the commas and split them into the row array
      val title = row(1)


      //since the number of rows in this file varies, because the actors field also has commas in it (!)
      // we find the first empty element and subtract 2 to get the revenue
      var j = 0
      breakable {
        for (i <- row) {
          if (i == null) {
            break
          }
          else
            j += 1
        }
      }
      val revenue = row(j-2)

      //this stores the written movie as a title, so this user can search for that title in the future
      writtenMovies+=title

      //do curl request against the http server
      val t = new Movie(title, revenue)
      val data = new Gson().toJson(t)

      //Http("http://localhost:6789/movies").postForm(Seq("name" -> title, "revenue" -> revenue)).asString

      val url = "http://localhost:6789/movies"
      val conn: HttpURLConnection = new URL(url).openConnection().asInstanceOf[HttpURLConnection]
      conn.setRequestMethod("POST")
      conn.setRequestProperty("Content-Type", "application/json")
      conn.setRequestProperty("Accept", "application/json")
      conn.setDoOutput(true)
      conn.connect()

      val wr = new DataOutputStream(conn.getOutputStream)
      wr.writeBytes(data)
      wr.flush()
      wr.close()

      val responseCode = conn.getResponseCode

      logger.info("> User #"+ID+": SENT " + data + " to " + url + " - RECEIVED " + responseCode)



  }//end receive()

}
