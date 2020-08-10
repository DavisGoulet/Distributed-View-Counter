# Distributed View Counter

Java application designed to keep a consistent view count for situations where one node is not able to process all requests. In this situation, the requests are split up and processed over many nodes. The challenge is to then make sure that views are not lost or double counted. The UI with this application was written in JavaFX with the application itself being written in Java. Socket communication was used to communicate between nodes.
#### Types of Nodes:

 - **Server Node**: Acts as the central database where the most up to date view counts are stored. Only one type of this node exists for a given application. The server node receives view count information from worker nodes and keeps a consistent view count. It also periodically sends out updated view count information to all the workers to keep them approximately synced if they return view count information with the requests (e.g. an application such as YouTube)
 - **Worker Node**: A node that processes requests and occasionally sends agglomerated view counts to the server node. Keeps two separate view counts to keep track of which views have already been accounted for by the server. Many worker nodes may exist for a given application.
- **Tester Node**: Simple node used to test the application. This type of node continually sends requests to worker nodes for given resources.

# Eventual Consistency

Eventual consistency is a consistency model used in situations where the application can be in an inconsistent state but eventually enters a consistency state if no new updates are made. In the context of this application, the central server may not have all views accounted for; some views may only be accounted for in worker nodes that have not sent updated information yet. If all worker nodes do not receive any requests however, the central server will get accurate view counts. No views were lost and no views were counted more than once. This is in comparison to something like a relational database satisfying the ACID properties.

# Application
