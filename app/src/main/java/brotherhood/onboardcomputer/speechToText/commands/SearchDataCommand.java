package brotherhood.onboardcomputer.speechToText.commands;

import android.speech.tts.TextToSpeech;

import org.json.JSONException;
import org.json.JSONObject;

import brotherhood.onboardcomputer.connection.ServerRequest;
import brotherhood.onboardcomputer.connection.enums.ServiceType;
import brotherhood.onboardcomputer.connection.parameters.Parameters;
import brotherhood.onboardcomputer.speechToText.Command;

public class SearchDataCommand extends Command {
    private static final String[] SEARCH = new String[]{"szukaj", "poszukaj", "wyszukaj", "znajdź"};
    private static final String[] INFORMATION = new String[]{"informacji", "informację", "danych", "w wikipedii", "informację o"};

    public SearchDataCommand(TextToSpeech speaker) {
        super(speaker);
    }

    @Override
    protected void initWords() {
        runWords = getPermutedArray(SEARCH, INFORMATION);
    }

    @Override
    protected void onInput(String sentence, boolean firstRun) {
        System.out.println("x:" + getSentenceAfterRunWords());
        new ServerRequest(ServiceType.GET_WIKIPEDIA_DATA
                , new Parameters().addParam("titles", getSentenceAfterRunWords()))
                .setServerRequestListener(new ServerRequest.ServerRequestListener() {
                    @Override
                    public void onSuccess(String json) {
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            JSONObject extract = jsonObject.getJSONObject("query")
                                    .getJSONObject("pages");
                            String key = extract.keys().next();
                            System.out.println(key);
                            String value = extract.getJSONObject(key).getString("extract");
                            speak(value);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            speak("Brak danych");
                        }
                    }

                    @Override
                    public void onError(int code, String description) {

                    }
                }).execute();
    }

    @Override
    protected void cancel() {

    }
}
