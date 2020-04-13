package framework;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
public class Request {

    private final String method;
    private final String uri;
    private final String protocol;
    private Map<String, String> parameters = new HashMap<>();

    Request(String requestWholeText) {
        String[] requestHeader = requestWholeText.split("\n")[0].split(" ");
        this.method = requestHeader[0];
        this.protocol = requestHeader[2];

        if ("GET".equals(method)) {
            uri = parseUri(requestHeader[1]);
            parseUriAndParametersForGET(requestHeader[1]);
        } else {
            uri = "TEMPORARY";
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

    private void parseUriAndParametersForGET(String wholeUri) {
        Optional<String> parametersStringOptional = getParametersStringFromUri(wholeUri);
        parametersStringOptional.ifPresent(this::parseParameters);
    }

    private void parseParameters(String parametersString) {
        String[] parameters = parametersString.split("&");
        for (String parameter: parameters) {
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

    private static Optional<String> getParametersStringFromUri(String uri) {
        int i;
        for (i = 0; i < uri.length(); i++) {
            if (uri.charAt(i) == '?') {
                break;
            }
        }
        return i == uri.length() ? Optional.empty() : Optional.of(uri.substring(i + 1));
    }
}
