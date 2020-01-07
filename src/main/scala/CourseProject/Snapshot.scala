package CourseProject

import java.io.{File, PrintWriter}

import CourseProject.Main.{conf, logger}
import CourseProject._


// "As part of testing, you must capture the global state of the system in the JSON or the XML format and dump it.
// The time during which the dump occurs is defined as the input to the simulator program. In your simulated world,
// the simulator has the power to freeze the system and walk over all actors to obtain their local states and combine
// them into the global state that it can save into a file whose location is defined as part of the input.
// After dumping the state into the file, the simulator resumes the process."
object Snapshot {

  //TODO
  def Main(): Unit = {
    logger.info("\n>>> Taking Snapshot of Simulation, exporting current status to a file\n")

    //walk over all actors to obtain their local states



    //combine states into a dataset or something



    //save to a file which is also defined in input
    val snapshotFilepath =conf.getString("snapshotFilepath")
    val pw = new PrintWriter(new File(snapshotFilepath))
    pw.write("Test")
    //write data


    pw.close

  }



}
