import java.util.List;
import java.util.Map;

public class Request {
    private final String method;
    private final String protocol;
    private final String path;
    private final Map<String, String> headers;
    private final List<String> body;


    Request (String method, String protocol, String path, Map<String, String> headers, List<String> body) {
        this.method = method;
        this.protocol = protocol;
        this.path = path;
        this.headers = headers;
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
