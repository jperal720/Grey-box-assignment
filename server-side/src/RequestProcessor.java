import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.*;

import org.json.simple.JSONObject;

class RequestProcessor implements Runnable {
    private Socket socket = null;
    private OutputStream os = null;
    private BufferedReader in = null;
    private DataInputStream dis = null;
    private String msgToClient = "HTTP/1.1 200 OK\n"
            + "Server: HTTP server/0.1\n"
            + "Access-Control-Allow-Origin: *\n\n";
    private JSONObject jsonObject = new JSONObject();
    public RequestProcessor(Socket Socket) {
        super();
        System.out.println("RequestProcessor Constructor");
        try {
            socket = Socket;
            in = new BufferedReader(new
                    InputStreamReader(socket.getInputStream()));
            os = socket.getOutputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run() {
    //write your code here

    InputStream input = this.getClass().getClassLoader()
            .getResourceAsStream("index.html");
    String html = new BufferedReader(new InputStreamReader(input)).lines()
            .collect(Collectors.joining("\n"));
        System.out.println(html);

        final String CRLF = "\r\n";

        String response =
                "HTTP/1.1 200 OK" + CRLF +
                "Content-Length: " + html.getBytes().length + CRLF +
                    CRLF +
                    html +
                    CRLF + CRLF;

//        os.write(response.getBytes());

    //end of your code

        try {
//            String response = msgToClient + jsonObject.toString();
            os.write(response.getBytes());

//            Scanner scan = new Scanner(new File("index.html"));
//            String html = scan.useDelimiter("\n").next();
//            scan.close();
//            os.write(html.getBytes("UTF-8"));
//            os.write("\r\n\r\n".getBytes());

            os.flush();
            socket.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    //Method that returns connection permission to client(handshake)
    public void handShake(BufferedReader in , OutputStream os){
        String header = "";//Variable declaration in header
        String key = "";//Variable declaration of websocket key
        try {
            while (!(header = in.readLine()).equals("")) {//Substitute the header obtained from the input stream into the string and loop all lines.
                System.out.println(header);//Show header contents on console line by line
                String[] spLine = header.split(":");//One line ":And put it in the array
                if (spLine[0].equals("Sec-WebSocket-Key")) {//Sec-WebSocket-Key line
                    key = spLine[1].trim();//Trim blanks and get websocket key
                }
            }
            key +="258EAFA5-E914-47DA-95CA-C5AB0DC85B11";//Add a mysterious string to the key
            byte[] keyUtf8=key.getBytes("UTF-8");//Key to "UTF-Convert to a byte array of "8"
            MessageDigest md = MessageDigest.getInstance("SHA-1");//Returns an object that implements the specified digest algorithm
            byte[] keySha1=md.digest(keyUtf8);//Key(UTF-8)Do a digest calculation using
            Encoder encoder = Base64.getEncoder();//Base64 encoder available
            byte[] keyBase64 = encoder.encode(keySha1);//Key(SHA-1)Is Base64 encoded
            String keyNext = new String(keyBase64);//Key(Base64)To String
            byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                    + "Connection: Upgrade\r\n"
                    + "Upgrade: websocket\r\n"
                    + "Sec-WebSocket-Accept: "
                    + keyNext
                    + "\r\n\r\n")
                    .getBytes("UTF-8");//Create HTTP response
            os.write(response);//Send HTTP response
        } catch (IOException e) {
            System.err.println("An error has occurred: " + e);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("An error has occurred: " + e);
        }
    }
}
