// Created By Blake Sartor
/* This program serves as the server of DNS query.
 ** Written in Java. */

import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DNSServer {
    public static void main(String[] args) throws Exception {
        int port = 5001;
        ServerSocket sSock = null;

        try {
            sSock = new ServerSocket(5001); // Try to open server socket 5001.
        }
        catch (Exception e) {
            System.out.println("Error: cannot open socket");
            System.exit(1); // Handle exceptions.
        }

        System.out.println("Server is listening...");
        new monitorQuit().start(); // Start a new thread to monitor exit signal.

        while (true) {
            new dnsQuery(sSock.accept()).start();
        }
    }
}

class dnsQuery extends Thread {
    Socket sSock = null;
    dnsQuery(Socket sSock) {
        this.sSock = sSock;
    }

    //This function returns a random IP from a list of IP's
    public String IPselection(String[] ipList){
        //if there is only one IP address, return the IP address
        if (ipList.length == 2) {
            return ipList[1];
        }
        //if there are multiple IP addresses, select one and return.
        else {
            int rnd = new Random().nextInt(ipList.length-1);
            return ipList[rnd+1];
        }
    }

    /*
     public String IPselection(String[] ipList){
     //if there is only one IP address, return the IP address
     if (ipList.length == 2) {
     return ipList[1];
     }
     //if there are multiple IP addresses, select one and return.
     else {
     long  = System.currentTimeMillis();
     Inet
     }
     }
     */
    @Override public void run(){
        BufferedReader inputStream;
        PrintWriter outStream;
        String query = "";
        try {
            //Open an input stream and an output stream for the socket
            //Read requested query from socket input stream
            //Parse input from the input stream
            //Check the requested query

            // Open Output stream for the socket
            outStream = new PrintWriter(sSock.getOutputStream(), true);
            // Open input stream for the socket
            inputStream = new BufferedReader(
                                             new InputStreamReader(sSock.getInputStream()));
            //Parse input from input stream
            String data = inputStream.readLine();
            // Print input
            System.out.println("Received: '" + data + "'\n");
            boolean hostFound = false;
            try {

                // Some inits
                String localCache = "DNS_Mapping.txt";
                String line = null;
                String response = null;
                String cachedHostIP = null;

                //check the DNS_mapping.txt to see if the host name exists
                //set local file cache to predetermined file.

                try {
                    FileReader filereader = new FileReader(localCache);
                    BufferedReader bufferedReader = new BufferedReader(filereader);

                    //if it does exist, read the file line by line to look for a
                    //match with the query sent from the client
                    while((line = bufferedReader.readLine()) != null) {
                        //If match, use the entry in cache.
                        //However, we may get multiple IP addresses in cache, so call dnsSelection to select one.
                        if (line.split(":")[0].equals(data)) {
                            //System.out.println("MATCH");
                            String []cachedHosts = (line.split(":"));
                            cachedHostIP = IPselection(cachedHosts);
                            hostFound = true;
                        }
                    }
                    bufferedReader.close();
                }
                //create file if it doesn't exist
                catch(FileNotFoundException ex) {
                    new PrintWriter("DNS_Mapping.txt", "UTF-8");
                    System.out.println("DNS Cache Created...");
                }
                // Used to match format of user input
                final String URL_REGEX = "^[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
                Pattern p = Pattern.compile(URL_REGEX);
                Matcher m = p.matcher(data);
                // If incorrect format set repsonse to 'Incorrect format'
                if(!m.find()) {
                    response = "Incorrect Format...";
                }
                //If match, use the entry in cache.
                else if (hostFound) {
                    response = "Local DNS:" + data + ":" + cachedHostIP;
                }
                //If no lines match, query the local machine DNS lookup to get the IP resolution

                else {
                    InetAddress ip = InetAddress.getByName(data);
                    String hostIp = ip.getHostAddress();
                    response = "Root DNS:" + data + ":" + hostIp;
                    //write the response in DNS_mapping.txt
                    BufferedWriter writer = new BufferedWriter(new FileWriter("DNS_Mapping.txt", true));
                    writer.write(data + ":" + hostIp);
                    writer.newLine();
                    writer.flush();
                    writer.close();
                }

                //print response to the terminal
                System.out.println(response);
                //send the response back to the client
                outStream.println(response);
                //Close the server socket.
                sSock.close();


            }
            catch (Exception e) {
                System.out.println("exception: " + e);
                outStream.println("Host Not Found");
                BufferedWriter writer = new BufferedWriter(new FileWriter("DNS_Mapping.txt", true));
                writer.write(data + ":" + "Host Not Found");
                writer.newLine();
                writer.flush();
                writer.close();
            }
            //Close the input and output streams.
            inputStream.close();
            outStream.close();

        }
        catch (IOException e) {
            e.printStackTrace();
            System.err.println("Host not found.\n" + e);
        }
    }
}

class monitorQuit extends Thread {
    @Override
    public void run() {
        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(System.in)); // Get input from user.
        String st = null;
        while(true){
            try{
                st = inFromClient.readLine();
            } catch (IOException e) {
            }
            if(st.equalsIgnoreCase("exit")){
                System.exit(0);
            }
        }
    }
}
