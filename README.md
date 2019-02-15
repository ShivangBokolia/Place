# Place
On April Fool's Day 2017, the popular social news and discussion website Reddit released Place, 
an interactive canvas that allowed any registered user to change a pixel to one of 16 colors every 5 minutes.
For this project we have recreated the magic by implementing our own socket based, 
distributed client-server model application purely in Java.

#Objectives:
Implement a threaded, socket based server that allows a client to log in and then change tiles on the board that will be communicated to all active clients.
Implement a Plain Text UI (PTUI) client that follows the Model-View-Controller pattern and uses threads and sockets to communicate with the server.
Implement a JavaFX GUI client that also follows the MVC pattern with the same thread and socket design as the PTUI client to communicate with the server.
Gain experience using Java serialization to send and receive objects over the network.

