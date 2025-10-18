package cc.nekocc.aitextimage.model.language.impl;

import cc.nekocc.aitextimage.model.UserSession;
import cc.nekocc.aitextimage.model.common.TextAnalysisResult;
import cc.nekocc.aitextimage.model.language.AbstractLanguageApiService;
import cc.nekocc.aitextimage.model.language.dto.openai.Message;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DeepSeekService extends AbstractLanguageApiService
{
    private final String apiKey;
    private final String model;
    private static final String PROD_API_URL = "https://api.deepseek.com/chat/completions";

    public DeepSeekService(String apiKey, String model, double temperature)
    {
        super(temperature);
        this.apiKey = apiKey;
        this.model = model;
    }

    @Override
    protected String getApiUrl()
    {
        return PROD_API_URL;
    }

    @Override
    protected String getApiKey()
    {
        return apiKey;
    }

    @Override
    protected String buildRequestBodyJson(String text)
    {
        String prompt = buildAdvancedPrompt(text);

        Map<String, Object> requestPayload = Map.of(
                "model", this.model,
                "messages", List.of(new Message("user", prompt)),
                "temperature", this.temperature,
                "response_format", Map.of("type", "json_object")
        );
        return UserSession.getInstance().getGson().toJson(requestPayload);
    }

    @Override
    protected TextAnalysisResult parseSuccessResponse(String jsonBody)
    {
        JsonObject response = JsonParser.parseString(jsonBody).getAsJsonObject();
        String contentJson = response.get("choices").getAsJsonArray()
                .get(0).getAsJsonObject()
                .get("message").getAsJsonObject()
                .get("content").getAsString();

        return UserSession.getInstance().getGson().fromJson(contentJson, TextAnalysisResult.class);
    }
}