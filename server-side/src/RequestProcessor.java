import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.*;

import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.*;


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
        System.out.println("html running");

        final String CRLF = "\r\n";

        String response =
                "HTTP/1.1 200 OK" + CRLF +
                "Content-Length: " + html.getBytes().length + CRLF +
                    CRLF +
                    html +
                    CRLF + CRLF;

    //end of your code

        try {
            LinkedList<String> operators = new LinkedList<String>();
            String line;
            JSONObject json = new JSONObject();
            String responseJSON;
            while(!(line = in.readLine()).isEmpty()){
                if (line.toLowerCase().contains("calculate")) {
                    System.out.println(line);
                    line = "http://localhost:8080" + line.substring(4, line.length() - 8).trim();
                    System.out.println("Debug: " + line);
                    Map<String, List<String>> map = splitQuery(new URL(line));
                    for(Map.Entry<String, List<String>> entry : map.entrySet()){
                        operators.addLast(entry.getValue().get(0));
                        System.out.println(entry.getValue().get(0).getClass());
                    }
                    json.put("Expression", operators.get(0) + " " + operators.get(2) + " " + operators.get(1));


                    System.out.println("This is the operator: " + operators.get(2));

                    //Checking for the operator
                    if(operators.get(2).equals("+")) {
                        System.out.println("Debug------>: " + (int) (Integer.parseInt(operators.get(0)) + Integer.parseInt(operators.get(1))));
                        json.put("Result", (int)(Integer.parseInt(operators.get(0)) + Integer.parseInt(operators.get(1))));
                    }
                    else if(operators.get(2).equals("-"))
                        json.put("Result", Integer.parseInt(operators.get(0)) - Integer.parseInt(operators.get(1)));
                    else if(operators.get(2).equals("*"))
                        json.put("Result", Integer.parseInt(operators.get(0)) * Integer.parseInt(operators.get(1)));
                    else if(operators.get(2).equals("/"))
                        json.put("Result", Integer.parseInt(operators.get(0)) / Integer.parseInt(operators.get(1)));
                    else if(operators.get(2).equals("%"))
                        json.put("Result", Integer.parseInt(operators.get(0)) % Integer.parseInt(operators.get(1)));

                    responseJSON =
                            "HTTP/1.1 200 OK" + CRLF +
                                    "Content-Type: " + "application/json" + CRLF +
                                    "Content-Length: " + json.toString().getBytes().length + CRLF +
                                    CRLF +
                                    json +
                                    CRLF + CRLF;

                    System.out.println("JSONFILE: " + responseJSON);
                    os.write(responseJSON.getBytes());
                    System.out.println("Debug: ----->" + json.toString());
                    System.out.println("end of the line!");

                }
            }

            os.write(response.getBytes());

            os.flush();
            socket.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    //Reference: https://stackoverflow.com/questions/13592236/parse-a-uri-string-into-name-value-collection
    public static Map<String, List<String>> splitQuery(URL url) throws UnsupportedEncodingException {
        final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
        final String[] pairs = url.getQuery().split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
            if (!query_pairs.containsKey(key)) {
                query_pairs.put(key, new LinkedList<String>());
            }
            final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
            query_pairs.get(key).add(value);
        }
        return query_pairs;
    }
}
