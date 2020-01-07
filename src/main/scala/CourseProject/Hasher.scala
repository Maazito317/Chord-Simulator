package CourseProject
import java.security.MessageDigest
import java.math.BigInteger

// Primary Hasher and ID generator and reader.

// MD5 is the primary hash, returns BigInteger object.
// addID: Adds 3 digits to the end of the hash
// getID: Reads last 3 digits of the hash
// getHashNoID: Reads a hash with ID and returns it without the ID
// hashDigits: Reads hash up to a certain number of digits for comparisons
object Hasher extends App { // code based from Alvin Alexander 10-24-2018 MD5 Hasher

  def md5HashInt(s: String): BigInteger ={  // Uses MD5 hashing to create an integer version of the hash.
    val md = MessageDigest.getInstance("MD5")   //Main hasher function. SHA-1 not in use
    val digest = md.digest(s.getBytes)
    val bigInt = new BigInteger(1,digest)
    bigInt
  }

  def sha1HashInt(s: String): BigInteger ={ //Uses SHA-1 hashing if we decide to use SHA-1 hashing later on for any reason
    val md = MessageDigest.getInstance(  "SHA-1") // curently not in use
    val digest = md.digest(s.getBytes)
    val bigInt = new BigInteger (1,digest)
    bigInt
  }

  //addID takes in the BigInteger hash and an ID up to 3 digits. If a smaller number is received, will add zeros on
  //to make it 3 digits. Hard coded without loops.
  def addID(b: BigInteger, d: Integer): BigInteger = {
    val base = b.toString()
    if (d<10){
      val c = "00"+d
      val newString = base + c
      val bigInt = new BigInteger(newString)
      bigInt
    }else if(d<100){
      val c = "0"+d
      val newString = base + c
      val bigInt = new BigInteger(newString)
      bigInt
    }else{
      val c = "" + d
      val newString = base + c
      val bigInt = new BigInteger(newString)
      bigInt
    }


  }

  //Pulls in BigInteger hash code (assumed to have ID tag on the end) and returns Integer of id tag.
  //If ID tag is less than 3 digits, it will not return extra zeros, ie 003 tag returns 3
  def getID(q: BigInteger): Integer ={
    val base = q.toString()
    val i = base.length()
    val st: String = "" + base.charAt(i-3) + base.charAt(i-2) + base.charAt(i-1)

    val ret = st.toInt
    ret
  }

  //Same as getID, only returns hash code, not ID. If there is no ID tag it returns just a shorter hash
  def getHashNoID(b: BigInteger): BigInteger = {
    val base = b.toString()
    val newString = base.dropRight(3)
    val bigInt = new BigInteger(newString)
    bigInt
  }

  //Takes in the Hash code and an integer and returns the first specified number of digits
  //useful for comparing first few digits of hash instead of whole thing
  def hashDigits(b: BigInteger, d: Integer): BigInteger = {
    val base = b.toString()
    val newString = base.substring(0,d)
    val bigInt = new BigInteger(newString)
    bigInt
  }


  /* hashers that return string. No longer in use

  def md5HashString(s: String): String = { // old string hasher, not in use

    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(s.getBytes)
    val bigInt = new BigInteger(1,digest)
    val hashedString = bigInt.toString(16)
    hashedString
  }
  def sha1HashString(s: String): String = {   //old string hasher, not in use

    val md = MessageDigest.getInstance(  "SHA-1")
    val digest = md.digest(s.getBytes)
    val bigInt = new BigInteger (1,digest)
    val hashedString = bigInt.toString(16)
    hashedString
  }
   */
}
