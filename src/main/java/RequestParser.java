import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestParser {
    protected Request createRequest (List<String> lines) throws IllegalArgumentException {
        final String[] requestLine = lines.remove(0).split(" ");

        //System.out.println("requestLine: " + requestLine[0] + "\n" + requestLine[1] + "\n" + requestLine[2]);
        if (requestLine.length != 3) {
            throw new IllegalArgumentException("Invalid request line found");
        }

        final String method = requestLine[0];
        //System.out.println(method);

        final String protocol = requestLine[2];
        //System.out.println(protocol);

        String[] pathSplit = requestLine[1].split("\\?");
        //System.out.println("pathSplit: " + pathSplit[0] + " " + pathSplit[1]);
        final String path = pathSplit[0];
        //System.out.println(path);

        List<NameValuePair> queryParameters = null;
        if (pathSplit.length > 1) {
            queryParameters = URLEncodedUtils.parse(pathSplit[1], Charset.defaultCharset());
        }
        System.out.println("Query Params" + queryParameters);
        Map<String, String> headers = lines.stream().takeWhile(line -> !line.equals(""))
                .collect(Collectors.toMap(
                        line -> line.split(":")[0].trim(),
                        line -> line.split(":")[1].trim()));
        //System.out.println("Headers: " + headers);

        List<String> body = lines.stream().dropWhile(line -> !line.equals("")).collect(Collectors.toList());
        //System.out.println("Body: " + body);

        if (!body.isEmpty()) {
            body.remove(0);
        }

        return new Request(method, protocol, path, headers, queryParameters, body);
    }
}
