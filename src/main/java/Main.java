import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        MyServer server = new MyServer();

        server.addHandler("GET", "/index.html", (request, out) -> {
            System.out.println(request.getPath());
            final Path filePath = Path.of(".", "public", request.getPath());
            final String mimeType = Files.probeContentType(filePath);
            final long length = Files.size(filePath);
            System.out.println(request.getPath());
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            Files.copy(filePath, out);
            out.flush();
        });

        server.addHandler("GET", "/spring.png", (request, out) -> {
            System.out.println(request.getPath());
            final Path filePath = Path.of(".", "public", request.getPath());
            final String mimeType = Files.probeContentType(filePath);
            final long length = Files.size(filePath);
            System.out.println(request.getPath());
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            Files.copy(filePath, out);
            out.flush();
        });

        server.addHandler("POST", "/messages", ((request, out) -> {
            System.out.println(request.getPath());
            out.write(("HTTP/1.1 200 OK\r\n" +
                    "Content-Length: 43\r\n" +
                    "Connection: close\r\n" +
                    "Content-Type: text/html\r\n" +
                    "\r\n" +
                    "<html><head></head><body>POST</body></html>\n").getBytes());
            out.flush();
        }));

        server.listen(9999);
    }
}
