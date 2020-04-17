package server;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Getter
@Slf4j
public class Request {
    private final HttpMethod method;
    private final String uri;
    private final String protocol;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> parameters = new HashMap<>();

    Request(String requestWholeText) throws Exception {

        String[] firstLine = null;
        int i;
        for (i = 1; i < requestWholeText.length(); i++) {
            if (requestWholeText.charAt(i - 1) == '\r' && requestWholeText.charAt(i) == '\n') {
                firstLine = requestWholeText.substring(0, i - 1).split(" ");
                break;
            }
        }
        if (firstLine == null) {
            log.warn("Invalid HTTP Request, has no first line");
            throw new Exception("Invalid HTTP Request");
        }

        this.method = HttpMethod.valueOf(firstLine[0]);
        this.protocol = firstLine[2];

        i = parseHeaders(i, requestWholeText);

        switch (method) {
            case GET:
                uri = firstLine[1].split("\\?")[0];
                parseParameters(uri.length(), firstLine[1]);
                break;
            case POST:
                uri = firstLine[1];
                parseParameters(i - 1, requestWholeText);
                break;
            default:
                uri = "";
        }
    }

    private int parseHeaders(int baseOffset, String requestWholeText) {
        int i;
        for (i = baseOffset + 1; i < requestWholeText.length(); i++) {
            if (requestWholeText.charAt(i - 1) == '\r' && requestWholeText.charAt(i) == '\n') {
                if (i - baseOffset < 3) {
                    break;
                }
                String[] currentHeader = requestWholeText.substring(baseOffset + 1, i - 1).split(": ");
                headers.put(currentHeader[0], currentHeader[1]);
                baseOffset = i;
            }
        }
        return i + 1;
    }

    private void parseParameters(int offset, String wholeUri) {
        if (offset + 1 >= wholeUri.length()) {
            return;
        }
        String parametersString = wholeUri.substring(offset + 1);
        parseParameters(parametersString);
    }

    private void parseParameters(String parametersString) {
        String[] parameters = parametersString.split("&");
        for (String parameter : parameters) {
            addParameter(parameter);
        }
    }

    private void addParameter(String parameterString) {
        int i;
        for (i = 0; i < parameterString.length(); i++) {
            if (parameterString.charAt(i) == '=') {
                break;
            }
        }
        parameters.put(parameterString.substring(0, i), parameterString.substring(i + 1));
    }
}
