
## UIC CS 441 - Course Project
####  Authors: 
Lucas Cepiel,
  Riley Tonkin,
  Joshua Rowan,
  Chieh-Hsi Lin,
  Mohammad Maaz Khan

### Instructions: how to install and deploy simulator

Download or clone the repo onto your machine. Open git bash or a terminal window in the directory of the project.

Do the following command to run the program, which will start a simulation that is 5 minutes long, creates 5 user actors, and 10 nodes. An Akka HTTP server will also start up, and in a few moments the user actors will start automatically generating read and write requests against the "cloud" of nodes. The nodes are arrayed in a Chord Algorithm, which operates and store entries based on the Chord paper discussed in class.

    sbt clean compile run
    
To change those variables (like duration of the simulation, number of users, read/write ratio, etc) you can find them in the directory
    
    src/main/resources/application.conf

To run the tests, try the following command. If that doesn't work (or says 0 tests found, which was a bug we encountered...), when you open the project in intellij IDEA, you can right click the 'test' file found at this directory and click 'run tests'

    sbt clean compile test
    
    src/test/scala/CourseProject/tests

### Docker Hub image:

Docker Hub Desktop was downloaded after upgrading system to Windows 10 Pro (doesn't work on Windows 10 Home). The dockerfile was generated 
from a generic scala dockerfile and generated the image of the current program. command for pulling image is:

    docker pull jdrowan87/lastdock:ld
    
    
### Using Akka HTTP

These work in a terminal window while the main project is running, and they're also used by the program, for the user actors to "interact" with the akka http server

To Create a Movie entry: 

    curl -H "Content-type: application/json" -X POST -d '{"name": "Harry Potter", "revenue": "99"}' http://localhost:6789/movies

To Get 1 Movie entry:

    curl http://localhost:6789/movies/Harry+Potter
    
To Get all entries:

    curl http://localhost:6789/movies

    




### Analysis: 

create a word doc analysis of our multiple simulations

### Overview:

The chord algorithm is a protocol for peer to peer distributed hashtables. A hashtable stores key value pairs by assigning keys to different computers(known as nodes). These nodes have their own IDs and the key-value pairs are stored on their respective nodes using these IDs.

The chord algorithm specifies how the keys are assigned to these nodes and how a node can discover the value for a key that is queried by first locating a node that is responsible for that key.

The algorithm also caters to nodes entering and leaving the system by utilizing various functions such as stabilize. The purpose of these functions is to rearrange successor lists, finger tables, and the entries stored on the nodes.

The nodes and keys are assigned an m-bit identifier using consistent hashing. Consistent hashing caters to the resizing of the hash table in which only K/n keys need to be rearranged on average, where K is the number of keys and n is the number of nodes. The base hashing function for consistent hashing is the SHA-1 algorithm.


### Querying: 

A basic approach to querying is that when a node is queried, it passes the query on to it's successor until the corresponding key-value pair is found. To speed up this process, a finger table is used. The closest preceding node to the id is checked and if found, it returns the key-value. If not, the request is forwarded to the last node in the finger table and it's table is checked. 


### Node entering and leaving: 

As an example, when a node enters the network, it is assigned an ID. This node finds it's relevant successor and predecessor and informs them of it's position. Then the keys held by the successor and predecessor are rearranged so that the newly added node can be provided with entries whose keys respond to their IDs.

Once this is done, the successor lists and finger tables are updated so as to store the ID of the node and establish it's presence. Hence, stabilizing the chord ring. A similar process is followed for any node that chooses to leave the system. 
