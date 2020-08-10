# Distributed View Counter

Java application designed to keep a consistent view count for situations where one node is not able to process all requests. In this situation, the requests are split up and processed over many nodes. The challenge is to then make sure that views are not lost or double counted. The UI with this application was written in JavaFX with the application itself being written in Java. Socket communication was used to communicate between nodes.
#### Types of Nodes:

 - **Server Node**: Acts as the central database where the most up to date view counts are stored. Only one type of this node exists for a given application. The server node recieves view count information from worker nodes and keeps a consistent view count. It also preiodically sends out updated view count information to all the workers to keep them approximately synced if they return view count information with the requests (e.g. an application such as YouTube)
 - **Worker Node**: A node that processes requests and occasionally sends agglomerated view counts to the server node. Keeps two seperate view counts to keep tract of which views have already been accounted for by the server. Many worker nodes may exist for a given application.
- **Tester Node**: Simple node used to test the application. This type of node continually sends requests to worker nodes for given resources.

# Eventual Consistency

Eventual consistency is a consistency model used in situation where the applciation can be in an inconsistent state but eventually enters a consistency state if no new updates are made. In the context of this application, the central server may not have all views accounted for; some views may only been accounted in worker nodes that have not sent updated information yet. If all worker nodes do not recieve any requests however, the central server will get accurate view counts. No views were lost and no views were counted more then once. This is in comparison to something like a relational database satisfying the ACID properties.

# Application
