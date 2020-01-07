package CourseProject

import scala.collection.mutable.ListBuffer

//case class Key(value:Int) //obtained from hash function, seperate class needed
class Entry(val id: BigInt, val name: Serializable, val rev: Serializable) extends Serializable{

  private var ID:BigInt = id
  private var title = name
  private var revenue = rev

  def getKey() = id
  def getTitle() = title
  def getRevenue() = revenue
  def setTitle(v1:Serializable){title = v1}
  def setRevenue(v1:Serializable){revenue = v1}
}

class Node(val ID: Int) extends Serializable{

  protected var nodeID: Int = ID
  //private var entries: Map[Int,Set[Entry]] = Map()
  private var entries = new ListBuffer[Entry]
  private var backup = new ListBuffer[Entry]
  private var succList = new SuccessorList(this)

  def getKey = nodeID
  def getSuccList = succList

  //To check we are not overwriting
  def equals(other: Node): Boolean = {
    if (other.getKey == nodeID ) true
    else false
  }

  //Functions for entries
  def numEntries():Int = this.entries.size
  //def getValues:List[Set[Entry]] = entries.valuesIterator.toList
  def getValues = entries.toList

  def add(entry: Entry): Unit ={
    this.entries.synchronized{
      entries = entries.addOne(entry)
      //entries = entries++Map(entry.getKey() -> Set(entry)) //adding to the end, can be made into a simple list as well
    }
  }

  /*
  def remove(entry: Entry): Unit ={
    this.entries.synchronized{
      if(entries.contains(entry.getID())){
        entries = entries++( if((entries(entry.getID())--Set(entry)).size>0) Map(entry.getID()->(entries(entry.getID())--Set(entry))) else Map() )
      }
    }
  }
   */

  def remove(key: BigInt): Unit ={
    this.entries.synchronized{
      val tempList = entries.map(e => e.getKey())
      if(tempList.contains(key))
        entries.remove(tempList.indexOf(key))
    }
  }

  def getEntries() = entries
  def getBackup() = backup

  def setEntries(to: ListBuffer[Entry]) = {
    entries = to
  }
  def setBackup(to: ListBuffer[Entry]) = {
    backup = to
  }

  def clearBackup() = {
    backup.clear()
  }

  def removeAll(toRemove:List[Entry]) = toRemove.foreach(x=>remove(x.getKey())) //for any set of entries (reshuffling)
  def addAll(toAdd:List[Entry]) = toAdd.foreach(x => this.add(x)) //for any set of entries (reshuffling)

}
