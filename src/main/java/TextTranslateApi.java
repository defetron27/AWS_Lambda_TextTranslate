import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.translate.model.TranslationsListResponse;
import com.google.api.services.translate.model.TranslationsResource;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;

import static java.nio.charset.StandardCharsets.UTF_8;

public class TextTranslateApi implements RequestStreamHandler
{
    private JSONParser jsonParser = new JSONParser();

    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException
    {
        String response;

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        try
        {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(bufferedReader);

            if (jsonObject.get("inputText") != null)
            {
                String inputText = (String) jsonObject.get("inputText");

                if (inputText != null && !inputText.equals(""))
                {
                    if (jsonObject.get("sourceLanguage") != null)
                    {
                        String sourceLanguage = (String) jsonObject.get("sourceLanguage");

                        if (sourceLanguage != null && !sourceLanguage.equals(""))
                        {
                            if (jsonObject.get("targetLanguage") != null)
                            {
                                String targetLanguage = (String) jsonObject.get("targetLanguage");

                                if (targetLanguage != null && !targetLanguage.equals(""))
                                {
                                    // now start coding to translate

                                    Translate translate = TranslateOptions.getDefaultInstance().getService();

                                    Translation translation = translate.translate(inputText,
                                            Translate.TranslateOption.sourceLanguage(sourceLanguage),
                                            Translate.TranslateOption.targetLanguage(targetLanguage));

                                    JSONObject jsonInputText = getJsonResponse(translation.getTranslatedText());

                                    response = jsonInputText.toJSONString();
                                }
                                else
                                {
                                    JSONObject jsonInputText = getJsonResponse("null");

                                    response = jsonInputText.toJSONString();
                                }
                            }
                            else
                            {
                                JSONObject jsonInputText = getJsonResponse("null");

                                response = jsonInputText.toJSONString();
                            }
                        }
                        else
                        {
                            JSONObject jsonInputText = getJsonResponse("null");

                            response = jsonInputText.toJSONString();
                        }
                    }
                    else
                    {
                        JSONObject jsonInputText = getJsonResponse("null");

                        response = jsonInputText.toJSONString();
                    }
                }
                else
                {
                    JSONObject jsonInputText = getJsonResponse("null");

                    response = jsonInputText.toJSONString();
                }
            }
            else
            {
                JSONObject jsonInputText = getJsonResponse("null");

                response = jsonInputText.toJSONString();
            }
        }
        catch (ParseException e)
        {
            JSONObject jsonObject = getJsonResponse("null");

            response = jsonObject.toJSONString();
        }

        OutputStreamWriter writer = new OutputStreamWriter(outputStream,UTF_8);
        writer.write(response);
        writer.close();
    }

    private JSONObject getJsonResponse(String translatedText)
    {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("translatedText",translatedText);

        return jsonObject;
    }
}
