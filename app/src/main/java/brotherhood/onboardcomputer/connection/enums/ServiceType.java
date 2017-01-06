package brotherhood.onboardcomputer.connection.enums;


import brotherhood.onboardcomputer.ui.views.recognizeButton.RecognizeButton;

public enum ServiceType {
    GET_WIKIPEDIA_DATA,
    GET_DUCKDUCK_GO_DATA;

    public static String getURL(ServiceType serviceType) {
        switch (serviceType) {

            case GET_WIKIPEDIA_DATA:
                return "https://" + RecognizeButton.LOCALE_LANGUAGE
                        + ".wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&";
            case GET_DUCKDUCK_GO_DATA:
                return "http://api.duckduckgo.com/?format=json";
        }
        return "Service path is invalid";
    }
}
