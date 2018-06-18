Spring 2018 CSci4211: Introduction to Computer Networks - Project 1
Blake Sartor - sarto019

readme.txt

  The Server attempts to open a socket on port 5001, and uses try and catches
  to handle exceptions. A thread is created for each client that connects
  to the server. This allows multiple clients to request a DNS from the
  server at once. 


Funcitons:
  public String IPselection(String[] ipList);
    This function returns, at random, an IP from a list of IPS.

  run()
    This function does most of the work.
    An input and output stream are opened for the sockets and the input
    is streamed into a data variable.

    A DNS cache file is created if it does not already exist.
    The program then searches the DNS file for the data that was
    retrieved from the client through the socket. If the server find the
    hostname in the DNS Cache, hostfound is set to true;
    If hostfound = true; the program sets the response variable to the
    value from the hostfile. This allows the server not to look up
    a hostname every time is it requested.
    If hostname = false; the program does a nslookup using
    InetAddress ip = InetAddress.getByName(data); (JAVA);
    Uses String hostIp = ip.getHostAddress() to strip only the IP address.
    Then sets the IP address to the response variable with the proper formatting
    The response is then written to the DNS cache file for later retrieval.

    The program then sends the response using the proper formatting
    to the socket for the client to receive using outStream.println(response);

    The socket is closed, as well as the input and output streams.

I have also included a output.txt file from running my program using the script
command. 


