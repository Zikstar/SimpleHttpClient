import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class HttpClient {
    public final static int BUFFER_SIZE = 1024;
    static int port = 80;
    public final String FORMAT = "utf-8";
    public boolean isVerbose;
    private String[] headers;
    private String postBody;
    private boolean isGet;
    private boolean isPost;
    private boolean redirectionAllowed;
    private String host;
    private String path;
    private String query;
    private String file;
    private String outputFile;


    HttpClient(String url, String[] headers, boolean isVerbose, String postBody,
               String file, String outputFile, boolean redirectionAllowed) {
        this.isVerbose = isVerbose;
        this.file = file;
        this.outputFile = outputFile;
        this.redirectionAllowed = redirectionAllowed;
        this.headers = headers;
        this.postBody = postBody;
        parseUrl(url);

    }

    HttpClient(String url, String[] headers, boolean isVerbose) {
        this(url, headers, isVerbose, null, null, null, false);
    }

    public static boolean isRedirect(String[] response) {
        String[] headers = response[0].split("\r\n");
        String responseCode = headers[0].split("\\s+")[1];
        return responseCode.startsWith("3");
    }

    //Build GET Request and send to host
    public void get() {
        isGet = true;
        if (query == null) {
            query = "";
        }
        StringBuilder requestBuilder = new StringBuilder();
        requestBuilder.append("GET ").append(path).append("?").append(query).append(" HTTP/1.1\r\n")
                .append("Host: ").append(host).append("\r\n")
                .append("User-Agent: Concordia-HTTP/1.0\r\n");
        if (headers != null) {
            for (String header : headers) {
                requestBuilder.append(header.strip()).append("\r\n");
            }
        }

        //To end the request
        requestBuilder.append("\r\n");

        String request = requestBuilder.toString();

        System.out.println("===========GET REQUEST===========");
        System.out.println(request);
        System.out.println("===========RESPONSE===========");
        sendRequestToSocket(request, host);
    }

    private void sendRequestToSocket(String request, String host) {
        SocketAddress endpoint = new InetSocketAddress(host, port);
        try (SocketChannel socket = SocketChannel.open()) {
            socket.connect(endpoint);
            //write the request in
            byte[] bs = request.getBytes(StandardCharsets.UTF_8);
            ByteBuffer byteBuffer = ByteBuffer.wrap(bs);
            socket.write(byteBuffer);

            //read the response from the socket
            ByteBuffer responseBuff = ByteBuffer.allocate(BUFFER_SIZE);

            while (socket.read(responseBuff) > 0) {
                //We can trim the array before we decode
                byte[] responseBuffArr = responseBuff.array();
                //System.out.println(responseBuffArr);
                //Split response for verbosity
                String line = new String(responseBuffArr, StandardCharsets.UTF_8).trim();
                String[] lines = line.split("\r\n\r\n");
                if (isRedirect(lines) && redirectionAllowed) {
                    performRedirection(lines);
                    return;
                }
                if (lines.length >= 2) {
                    if (isVerbose) {
                        System.out.println(lines[0]);
                        System.out.println();
                    }
                    System.out.println(lines[1]);

                    if (outputFile != null) {
                        saveResponseIntoOutputFile(lines[1]);
                    }

                }

            }

            socket.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void saveResponseIntoOutputFile(String line) {
        try {
            Path directory = Files.createDirectories(Paths.get("Downloads"));
            Path filePath = Paths.get("Downloads" + "/" + outputFile);
            Path file;
            if (!Files.exists(filePath)) {
                file = Files.createFile(filePath);
            } else {
                file = filePath;
            }
            FileWriter fileWriter = new FileWriter(file.toString());
            fileWriter.write(line);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void performRedirection(String[] response) {
        String[] headers = response[0].split("\r\n");
        String locationPrefix = "Location: ";
        for (String headerLine : headers) {
            if (headerLine.contains(locationPrefix)) {
                int locationIndex = headerLine.indexOf(locationPrefix) + locationPrefix.length();
                String newUrl = headerLine.substring(locationIndex);
                //Debug statements
                System.out.println("=============================================");
                System.out.println("Redirection Detected: " + newUrl);
                System.out.println();
                parseUrl(newUrl);
                break;
            }
        }

        if (isGet) {
            get();
        } else if (isPost) {
            post();
        }

    }

    //Build Post Request and send to host
    public void post() {
        isPost = true;
        StringBuilder requestBuilder = new StringBuilder();
        if (query == null) {
            query = "";
        }
        requestBuilder.append("POST ").append(path).append("?").append(query).append(" HTTP/1.1\r\n")
                .append("Host: ").append(host).append("\r\n")
                .append("User-Agent: Concordia-HTTP/1.0\r\n");

        if (headers != null) {
            for (String header : headers) {
                requestBuilder.append(header.strip()).append("\r\n");
            }
        }

        if (file != null) {
            Path filePath = Path.of(file);
            try {
                //instead of file content save it back to file
                file = Files.readString(filePath)
                        .replace("\n", "");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        if(isPostingJsonData()){
//            if(postBody != null){
//                // Supposed to convert to Json and back to a String //to ensure its valid Json?
//                // postBody =
//            }
//            if(file != null){
//                //Do the same and convert to Json
//            }
//        }

        if (postBody != null) {
            requestBuilder.append("Content-Length: ").append(postBody.length()).append("\r\n");
        }
        if (file != null) {
            requestBuilder.append("Content-Length: ").append(file.length()).append("\r\n");
        }
        //Ending of the request header
        requestBuilder.append("\r\n");


        //Add the postBody
        if (postBody != null) {
            requestBuilder.append(postBody);
        }
        if (file != null) {
            requestBuilder.append(file);
        }

        System.out.println("===========POST REQUEST===========");
        String request = requestBuilder.toString();
        System.out.println(request);
        System.out.println();
        System.out.println("============RESPONSE==============");
        sendRequestToSocket(request, host);
    }

    public boolean isPostingJsonData() {
        return headers != null && Arrays.asList(headers).contains("application/json");
    }

    private void parseUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            host = url.getHost();
            path = url.getPath();
            query = url.getQuery();
            if (url.getPort() != -1) {
                port = url.getPort();
                host = host.split(":")[0];
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

}
