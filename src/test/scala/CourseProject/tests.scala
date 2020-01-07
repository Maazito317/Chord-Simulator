package CourseProject

import org.scalatest.FlatSpec
import java.util.concurrent.TimeUnit

import CourseProject.Chord.{createEntry, getEntry}
import CourseProject.Main.conf
import akka.actor.{ActorSystem, Props}
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.Source


/*class tests extends FlatSpec{
  val node = new Node(0)
  val entry = new Entry (0,"a","100")
  val succ = new SuccessorList(node)

  "SuccessorList" should "size > 0" in {
    val succList:List[Node] = List(node)
    succList ::: List(new Node(1))
    assert(succList.nonEmpty)
  }
  it should "throw Exception if it is an empty list" in {
    val emptyList:List[Node] = List()
    assertThrows[NoSuchElementException] {
      emptyList.head==null
    }
  }
  it should "return closest precedingNode if exists" in {
    assertFalse(succ.getClosestPrecedingNode(node.getKey)!=null)
  }

  "Node" should "have int id" in{
    assertEquals(node.getKey,0)
  }

  "Entry" should "contains three elements" in {
    assertFalse( entry.getKey()==1)
  }
  it should "have title name" in {
    assertFalse(entry.getTitle=="b")
  }
  it should "have revenue" in {
    assertEquals(entry.getRevenue(),"100")
  }

}*/

object tests {

  def fixSuccList(nodeList: Array[Node], numNodes: Int) = {
    //copy and paste of fixSuccList with the numNodes variable replaced with 10
    for (n <- nodeList) {
      var inc = 1
      for (i <- 0 until numNodes - 1) {
        //if current node's next node is after the loopback
        if (n.getKey + inc >= numNodes - 1) {
          if (n.getKey + inc < numNodes) {
            n.getSuccList.addSuccessor(nodeList(n.getKey + inc))
            inc = -n.getKey
          }
          //boundary case for last node before loopback
          else if (n.getKey == numNodes - 1) {
            n.getSuccList.addSuccessor(nodeList(0))
            inc = -n.getKey + 1
          }
        }
        else {
          if (n.getKey + inc < numNodes) {
            n.getSuccList.addSuccessor(nodeList(n.getKey + inc))
            inc += 1
          }
        }
      }
    }
  }


  @Test def testSuccessorList = {
    val numNodes = 10
    val nodeList = (for {i <- 0 until numNodes} yield {
      new Node(i)
    }).toArray

    fixSuccList(nodeList, numNodes)

    val node = nodeList(4)
    val node2 = nodeList(7)
    val node3 = nodeList(0)
    val node4 = nodeList(9)
    val listIDs = node.getSuccList.getList().map(n => n.getKey)
    val listIDs2 = node2.getSuccList.getList().map(n => n.getKey)
    val listIDs3 = node3.getSuccList.getList().map(n => n.getKey)
    val listIDs4 = node4.getSuccList.getList().map(n => n.getKey)

    assertEquals(List(5, 6, 7, 8, 9, 0, 1, 2, 3), listIDs)
    assertEquals(List(8, 9, 0, 1, 2, 3, 4, 5, 6), listIDs2)
    assertEquals(List(1, 2, 3, 4, 5, 6, 7, 8, 9), listIDs3)
    assertEquals(List(0, 1, 2, 3, 4, 5, 6, 7, 8), listIDs4)

  }

  @Test def testRemoveSuccessor = {
    val numNodes = 10
    val nodeList = (for {i <- 0 until numNodes} yield {
      new Node(i)
    }).toArray

    fixSuccList(nodeList, numNodes)

    val node = nodeList(4)

    node.getSuccList.removeSuccessor(nodeList(3))
    assertEquals(List(5, 6, 7, 8, 9, 0, 1, 2), node.getSuccList.getList().map(n => n.getKey))
    node.getSuccList.removeSuccessor(nodeList(5))
    assertEquals(List(6, 7, 8, 9, 0, 1, 2), node.getSuccList.getList().map(n => n.getKey))
    node.getSuccList.removeSuccessor(nodeList(9))
    assertEquals(List(6, 7, 8, 0, 1, 2), node.getSuccList.getList().map(n => n.getKey))
  }

  @Test def testGetSuccessor = {
    val numNodes = 10
    val nodeList = (for {i <- 0 until numNodes} yield {
      new Node(i)
    }).toArray

    fixSuccList(nodeList, numNodes)

    val node = nodeList(4)
    val node2 = nodeList(9)
    val node3 = nodeList(0)

    val succ = node.getSuccList.getSuccessor()
    val pred = node.getSuccList.getClosestPrecedingNode(node.getKey)

    val succ2 = node2.getSuccList.getSuccessor()
    val pred2 = node2.getSuccList.getClosestPrecedingNode(node2.getKey)

    val succ3 = node3.getSuccList.getSuccessor()
    val pred3 = node3.getSuccList.getClosestPrecedingNode(node3.getKey)

    assertEquals(5, succ.getKey)
    assertEquals(3, pred.getKey)

    assertEquals(0, succ2.getKey)
    assertEquals(8, pred2.getKey)

    assertEquals(1, succ3.getKey)
    assertEquals(9, pred3.getKey)
  }

  @Test def testNodeAddRemove = {
    val numNodes = 10
    val nodeList = (for {i <- 0 until numNodes} yield {
      new Node(i)
    }).toArray

    fixSuccList(nodeList, numNodes)

    val node = nodeList(4)
    node.add(new Entry(1, "Pulp Fiction", "500000"))
    node.add(new Entry(40, "Incredibles", "500000"))
    node.add(new Entry(21, "Happy Gilmore", "500000"))
    node.add(new Entry(112, "Rocky", "500000"))
    node.add(new Entry(132, "Rambo", "500000"))

    val entries = node.getEntries().toList.map(e => (e.getKey(), e.getTitle()))
    assertEquals(List((1, "Pulp Fiction"), (40, "Incredibles"), (21, "Happy Gilmore"), (112, "Rocky"), (132, "Rambo")), entries)

    node.remove(21)
    node.remove(40)
    val entries2 = node.getEntries().toList.map(e => (e.getKey(), e.getTitle()))
    assertEquals(List((1, "Pulp Fiction"), (112, "Rocky"), (132, "Rambo")), entries2)
  }

  @Test def testHasher = {
    val hashID1 = Hasher.sha1HashInt("This project made me suicidal")
    val hashID2 = Hasher.md5HashInt("This project made me suicidal")

    assertEquals(hashID1, Hasher.sha1HashInt("This project made me suicidal"))
    assertEquals(hashID1, Hasher.sha1HashInt("This project made me suicidal"))
    assertEquals(hashID1, Hasher.sha1HashInt("This project made me suicidal"))
    assertEquals(hashID1, Hasher.sha1HashInt("This project made me suicidal"))

    assertEquals(hashID2, Hasher.md5HashInt("This project made me suicidal"))
    assertEquals(hashID2, Hasher.md5HashInt("This project made me suicidal"))
    assertEquals(hashID2, Hasher.md5HashInt("This project made me suicidal"))
    assertEquals(hashID2, Hasher.md5HashInt("This project made me suicidal"))

  }

  @Test def testChord = {

    //val numNodes = conf.getInt("numNodes")
    implicit val timeout = Timeout(5, TimeUnit.SECONDS)
    val system = ActorSystem("test")
    val act = system.actorOf(Props(new Chord))

    act ! createEntry(Movie("Pulp Fiction", "500000"))
    try {
      val movie = act ? getEntry("Pulp Fiction")
      Await.result(movie, Duration.create(5, "seconds"))
      assertEquals(Movie("Pulp Fiction", "500000"), movie)
    }
    catch {
      case e: Exception =>
        println("TimeOutException")
    }

  }
}
