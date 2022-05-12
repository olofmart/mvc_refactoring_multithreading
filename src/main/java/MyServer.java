
import org.apache.commons.fileupload.FileUploadException;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class MyServer {
    final List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css",
            "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
    private final int poolSize = 64;
    private final ExecutorService threadPool;
    private final Map<String, Handler> getHandlers;
    private final Map<String, Handler> postHandlers;

    public MyServer () {
        threadPool = Executors.newFixedThreadPool(poolSize);
        getHandlers = new ConcurrentHashMap<>();
        postHandlers = new ConcurrentHashMap<>();
    }

    public void listen (int port) {
        try (final ServerSocket serverSocket = new ServerSocket(port)) {
            while(true) {
                final Socket socket = serverSocket.accept();
                threadPool.submit(() -> connection(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void connection (Socket socket) {
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             final BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream())) {

            final Request request;
            RequestParser parser = new RequestParser();

            try {
                request = parser.createRequest(inputStreamReading(in));
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                socket.close();
                return;
            }

            if (!validPaths.contains(request.getPath())) {
                out.write((
                        "HTTP/1.1 404 Not Found\r\n" +
                                "Content-Length: 0\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.flush();
                return;
            }

            if (request.getMethod().equals("GET")) {
                getHandlers.get(request.getPath()).handle(request, out);
            }
            if (request.getMethod().equals("POST")) {
                postHandlers.get(request.getPath()).handle(request, out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    private Request createRequest (List<String> lines) throws IllegalArgumentException {
//        final String[] requestLine = lines.remove(0).split(" ");
//
//        //System.out.println("requestLine: " + requestLine[0] + "\n" + requestLine[1] + "\n" + requestLine[2]);
//        if (requestLine.length != 3) {
//            throw new IllegalArgumentException("Invalid request line found");
//        }
//
//        final String method = requestLine[0];
//        //System.out.println(method);
//
//        final String protocol = requestLine[2];
//        //System.out.println(protocol);
//
//        String[] pathSplit = requestLine[1].split("\\?");
//        //System.out.println("pathSplit: " + pathSplit[0] + " " + pathSplit[1]);
//        final String path = pathSplit[0];
//        //System.out.println(path);
//
//        List<NameValuePair> queryParameters = null;
//        if (pathSplit.length > 1) {
//            queryParameters = URLEncodedUtils.parse(pathSplit[1], Charset.defaultCharset());
//        }
//        //System.out.println("Query Params" + queryParameters);
//        Map<String, String> headers = lines.stream().takeWhile(line -> !line.equals(""))
//                        .collect(Collectors.toMap(
//                                line -> line.split(":")[0].trim(),
//                                line -> line.split(":")[1].trim()));
//        //System.out.println("Headers: " + headers);
//
//        List<String> body = lines.stream().dropWhile(line -> !line.equals("")).collect(Collectors.toList());
//        //System.out.println("Body: " + body);
//
//        if (!body.isEmpty()) {
//            body.remove(0);
//        }
//
//        return new Request(method, protocol, path, headers, queryParameters, body);
//    }


    private List<String> inputStreamReading(BufferedReader in) throws IOException {
        int bufferSize = 1024;
        int readed;
        List<String> list;
        CharBuffer buffer = CharBuffer.allocate(bufferSize);
        StringBuilder builder = new StringBuilder();

        while ((readed = in.read(buffer)) > 0) {
            buffer.flip();
            char[] dst = new char[readed];
            buffer.get(dst);
            builder.append(dst);
            if (readed != bufferSize) {
                break;
            }
            buffer.clear();
        }

        list = new ArrayList<>(Arrays.asList(builder.toString().split("\r\n")));

        return list;
    }


    public void addHandler(String method, String path, Handler handler) {
        if (method.equals("GET")) {
            getHandlers.put(path, handler);
        }

        if (method.equals("POST")) {
            postHandlers.put(path, handler);
        }
    }
}