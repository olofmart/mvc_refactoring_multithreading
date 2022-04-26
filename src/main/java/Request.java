import org.apache.http.NameValuePair;

import java.util.List;
import java.util.Map;

public class Request {
    private final String method;
    private final String protocol;
    private final String path;
    private final Map<String, String> headers;
    private List<NameValuePair> queryParameters;
    private final List<String> body;


    Request (String method, String protocol, String path, Map<String, String> headers,
             List<NameValuePair> queryParameters, List<String> body) {
        this.method = method;
        this.protocol = protocol;
        this.path = path;
        this.headers = headers;
        this.queryParameters = queryParameters;
        this.body = body;
        //System.out.println(getQueryParam("value"));
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

    public List<NameValuePair> getQueryParameters() throws NullPointerException {
        return queryParameters;
    }

    public String getQueryParam(String name) {
       return queryParameters.stream()
               .filter(p -> p.getName().equals(name))
               .map(p -> p.getValue().toString())
               .findFirst().get();
    }
}
