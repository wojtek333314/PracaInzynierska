package brotherhood.onboardcomputer.connection.enums;


import brotherhood.onboardcomputer.views.recognizeButton.RecognizeButton;

public enum ServiceType {
    GET_WIKIPEDIA_DATA;

    public static String getURL(ServiceType serviceType) {
        switch (serviceType) {

            case GET_WIKIPEDIA_DATA:
                return "https://" + RecognizeButton.LOCALE_LANGUAGE
                        + ".wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&";
        }
        return "Service path is invalid";
    }
}
