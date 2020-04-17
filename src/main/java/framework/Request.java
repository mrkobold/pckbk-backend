package framework;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Request {
    private final HttpMethod method;
    private final String uri;
    private final String protocol;
    private final Map<String, String> parameters = new HashMap<>();

    Request(String requestWholeText) {
        String[] requestHeader = requestWholeText.split("\n")[0].split(" ");
        this.method = HttpMethod.valueOf(requestHeader[0]);
        this.protocol = requestHeader[2];

        switch (method) {
            case GET:
                uri = parseUri(requestHeader[1]);
                parseUriAndParametersForGET(uri.length(), requestHeader[1]);
                break;
            case POST:
                uri = requestHeader[1];
                break;
            default:
                uri = "";
        }
    }

    private static String parseUri(String wholeUriText) {
        int i;
        for (i = 0; i < wholeUriText.length(); i++) {
            if (wholeUriText.charAt(i) == '?') {
                break;
            }
        }
        return wholeUriText.substring(0, i);
    }

    private void parseUriAndParametersForGET(int offset, String wholeUri) {
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
