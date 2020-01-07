package CourseProject
import scala.util.control.Breaks._


class SuccessorList(val node:Node) {
  private var localNode = node
  private var succlist: List[Node] = List()
  private var size = 40

  //check whether a node is contained in the successor list
  def contains(node: Node):Boolean ={
    var boo = false
    if (node.equals(localNode)) return true
    this.succlist.synchronized {
      succlist.foreach(element => if (element.equals(node)) boo = true)
    }
    boo
  }

  def getSuccessor() = if (!succlist.isEmpty) succlist(0) else null

  def insert[T](list: List[T], i: Int, value: T) = {//used to break list and insert value
    val (front, back) = list.splitAt(i)
    front ++ List(value) ++ back
  }

  def addSuccessor(node:Node){
    if (node == null) throw new NullPointerException("Node can not be null")
    //use synchronized to prevent clashing of threads
    this.succlist.synchronized{
      if(succlist.isEmpty){
        succlist = succlist:+node
      }
      else if(!this.contains(node)){ //if not already contained
        var input = false
        if ((node.getKey > localNode.getKey) && (node.getKey < succlist.last.getKey)) {//if between the interval
          breakable{//allows for breaking of loop
            for(i <- 0 until succlist.length){
              if ((node.getKey > localNode.getKey) && (node.getKey < succlist(i).getKey)){
                succlist = insert(succlist,i,node)
                input = true
                break
              }
            }
          }
        }
        if(!input)
            succlist = succlist:+node
        }
      }

      if(succlist.size > size )
        succlist = succlist.dropRight(1)
    }

  def removeSuccessor(node:Node): Unit ={
    if (node == null) throw new NullPointerException("Node can not be null")
    this.succlist.synchronized{
      breakable{
        for (i <-0 until succlist.length){
          if (node == succlist(i)){
            succlist = succlist.dropRight(succlist.length-i):::succlist.drop(i+1)
            break
          }
        }

      }
    }
  }

  def getList() = succlist.toList

  def getClosestPrecedingNode (key:Int): Node ={ //if the key isn't found in the interval, it returns the last node in the interval
    var prec:Node = null
    this.succlist.synchronized{
      breakable{
        //if key is before the first successor's key, then the predecessor is the last successor in the list
        if(key == succlist.head.getKey-1)
        {
          prec = succlist.last
        }
        else {
          for (i <- 0 until succlist.length) {
            if (!(key >= localNode.getKey && key <= succlist(succlist.length - i - 1).getKey)) {
              prec = succlist(succlist.length - i - 1)
              break
            }
          }
        }
      }
    }
    prec
  }
  //get immediate successor (id)
  //get last
  //get random node

}
