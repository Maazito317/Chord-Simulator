//package CourseProject
//import scala.util.control.Breaks._
//import java.math.BigInteger
//
//import scala.collection.mutable.ArrayBuffer
//import scala.math._
//
//class FingerTable(val key: Int, val node: Node, val succL: SuccessorList) {
//  private val succlist = succL
//  private val size = succlist.getList().length
//  private val localNode = node
//  var m = ceil((math.log(size + 1) / math.log(2))).toInt
//  val fingerTableSize = (((log(m) / log(2)).toInt)/2 )+1
//  private val localKey = key
//  private var fingerTable = new ArrayBuffer[Node](fingerTableSize) //has to be equal to number of bits in key
//
//  def contains(node:Node):Boolean={
//    if(node == null) throw new NullPointerException("Reference to proxy may not be null!")
//    var boo = false
//    this.fingerTable.synchronized{
//      for(i <- 0 until fingerTable.length){
//        if(node.equals(fingerTable(i)))
//          boo = true
//      }
//    }
//    boo
//  }
//
//  def addFinger(node:Node) {
//
//  } //needs hasher function to write
//
//  def removeFinger(node: Node){
//    if(node == null) new NullPointerException()
//
//    else{
//      this.fingerTable.synchronized{
//        breakable{
//          for(i <- 0 until fingerTable.length){
//            if(node.equals(fingerTable(fingerTable.length - 1 - i))){
//              fingerTable(fingerTable.length - 1 - i) = fingerTable(fingerTable.length-i) //replace with the entry ahead of it
//              break
//            }
//          }
//        }
//        //use successorList to fill holes
//        var successors = succlist.getList
//        successors.foreach{ x => if(x!=null && !node.equals(x))  this.addFinger(x) }
//      }
//    }
//  }
//
//  def getClosestPrecedingNode(key:Int):Node = {
//    var prec:Node = null
//    this.fingerTable.synchronized{
//      breakable{
//        var l:Int = fingerTable.length - 1
//        for(i <-0 to l)
//          if(fingerTable(l - i) != null && fingerTable(l - i).getKey >= this.localKey && fingerTable(l - i).getKey <= key)
//            prec = fingerTable(l-i)
//            break
//      }
//    }
//    prec
//  }
//
//  def getFingers:Set[Node]={
//    var fingers:Set[Node] = Set()
//    this.fingerTable.synchronized{
//      fingers = fingerTable.toSet
//    }
//    fingers
//  }
//
//
//}
