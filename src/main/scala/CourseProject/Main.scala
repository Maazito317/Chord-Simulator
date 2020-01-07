package CourseProject

//imports for the config stuff and logging stuff

import java.io.File
import CourseProject.Main.{conf, system}
import CourseProject.UserActor._
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

//all the akka imports

import akka.actor.ActorSystem
import akka.actor._
import scala.language.postfixOps
import scala.concurrent.duration._
import scala.language.postfixOps

import CourseProject._


/*
UIC CS 441, Course Project

Authors:
  Lucas Cepiel
  Riley Tonkin
  Joshua Rowan
  Chieh-Hsi Lin
  Mohammad Maaz Khan

 */


//the main object spins up our simulation, initializes the user and cloud actors, and calls the http server to start

object Main extends App {

  import system.dispatcher

  //initialize logger
  val logger = LoggerFactory.getLogger(this.getClass)
  //initialize config file with variables
  val c = ConfigFactory.load()
  val conf = c.getConfig("Master")

  val simulationDuration =conf.getInt("simulationDuration")
  val system = ActorSystem("main")
  //val scheduler = system.scheduler
  //implicit val executor = system.dispatcher

  //fire up the Akka HTTP server
  AkkaHTTPServer.main()


  val nodeMaster = system.actorOf(Props(new Chord()))


  val numUser = conf.getInt("numUsers")
  val minRequestsPerMin =conf.getInt("minRequestsPerMinute")
  val maxRequestsPerMin =conf.getInt("maxRequestsPerMinute")
  val ratioWrite =conf.getInt("ratioWrite")
  val ratioRead =conf.getInt("ratioRead")
  val snapshotTime =conf.getInt("snapshotTime")

  //create the number of user actors

  val users = (for { i <- 0 until numUser } yield {
    val NodeRef = system.actorOf(Props(new UserActor(i+1)))
    (i, NodeRef)
  }).toMap


  Thread.sleep(3000) // wait for 3 seconds so akka HTTP can fire up before you start sending requests

//  users(0) ! writeRequest()
//  Thread.sleep(1000)
//  users(0) ! writeRequest()
//  Thread.sleep(1000)
//  users(0) ! readRequest()
//  Thread.sleep(1000)
//  users(0) ! readRequest()
//
//  Thread.sleep(3000)



  //loop through users and randomize the reads and writes for the next minute

  for(i <- 0 to users.size-1){

      //for each user, give them a random number of requests (between the min and max requests per minute variables)
      val rand = new scala.util.Random
      val numRequests = minRequestsPerMin + rand.nextInt( (maxRequestsPerMin - minRequestsPerMin) + 1 )

      logger.info("USER " + (i+1) + " - Number Requests/minute - "+numRequests)

      for(x <- 1 to numRequests){
        var delay = (60/maxRequestsPerMin)*x
        //val delay = 5
        //this means if numRequests to do in that minute were 10, divide 60 seconds by 10 to get 6 second delay between requests,
        // and for each request, extend this, to stretch it over the minute.
        // so execute at 0s, 6s, 12s, 18s, 24s, 30s, 36s, 42s, 48s, 60s   - gets you 10 executions over the minute

        //for each request, randomize (within the ratio) if it should be a read or write.
        //ie if ratio is 4, it means 4:1, so do 4 reads for each write
        val odds = rand.nextInt(100)+1   //generate 1-100
        val n = ratioRead+ratioWrite    // 1+4 = 5, so carve 100 into 4/5 and 1/5
        val w = (100/n)*ratioWrite      // 4/5 of 100 is 80
        val writeLow = 100-w            // so 100-80 =20. So 20 to 100 means write
        val readHigh=writeLow-1         // so 1 to 19 means read


        //do the request every 60 seconds at a fixed rate. this is what will keep the simulation running automatically over time
        if(odds<=readHigh){
          system.scheduler.scheduleAtFixedRate(delay seconds, 60 seconds, users(i), readRequest())
          //logger.info("read request")
        }else if(odds >= writeLow){
          system.scheduler.scheduleAtFixedRate(delay seconds, 60 seconds, users(i), writeRequest())
          //logger.info("write request")
        }
        else{ logger.info("> Error in probability calcs!")}

        delay+=delay  //increase the delay so that they don't try to do all their requests for the minute at once
      }

    } // end the looping through users


  Thread.sleep(2000)
  //pause before actors start doing commands
  // UNSURE why this is neccessary, but without it, the program has an akka one-for-one error. Can't figure out what that means or why it does that



  //snapshotTime
  val start = System.currentTimeMillis
  val end = start + 60 * 1000 * simulationDuration  // simulationDuration
  val snapshot = start + 60 * 1000 * snapshotTime   // at this time, call the function to take a snapshot of the simulation and export to a JSON or XML file

  // simulationDuration is in minutes, so it goes for 1 minute if simulationDuration=1
  logger.info("\n>>> START SIMULATION TIMER - Simulation will run for "+simulationDuration+" minutes\n")
  //run the simulation for X minutes
  while (System.currentTimeMillis < end) {
    //basically don't need anything in here, since actors will now automatically do their requests
    //this is just to keep the simulation running for the duration
    if(System.currentTimeMillis == snapshot){
      Snapshot.Main()   //call function to take a snapshot of the program and the actors states and write to a file
    }
  }





  // the timer is over, so the simulation is at an end.
  logger.info("\n>>> SIMULATION COMPLETE \n")

  sys.exit(0)

}

