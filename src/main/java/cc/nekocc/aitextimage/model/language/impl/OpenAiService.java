package cc.nekocc.aitextimage.model.language.impl;

import cc.nekocc.aitextimage.model.UserSession;
import cc.nekocc.aitextimage.model.common.TextAnalysisResult;
import cc.nekocc.aitextimage.model.language.AbstractLanguageApiService;
import cc.nekocc.aitextimage.model.language.dto.openai.Message;
import cc.nekocc.aitextimage.model.language.dto.openai.OpenAiChatRequest;
import cc.nekocc.aitextimage.model.language.dto.openai.OpenAiChatResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OpenAiService extends AbstractLanguageApiService
{
    private final String api_key;
    private final String model;
    private static final String _API_URL = "https://api.openai.com/v1/chat/completions";

    public OpenAiService(String apiKey, String model, double temperature)
    {
        super(temperature);
        this.api_key = apiKey;
        this.model = model;
    }

    @Override
    protected String getApiUrl()
    {
        return _API_URL;
    }

    @Override
    protected String getApiKey()
    {
        return this.api_key;
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
    protected TextAnalysisResult parseSuccessResponse(String json_body)
    {
        OpenAiChatResponse response = UserSession.getInstance().getGson().fromJson(json_body, OpenAiChatResponse.class);
        String content_json = response.choices.getFirst().message.content();
        return UserSession.getInstance().getGson().fromJson(content_json, TextAnalysisResult.class);
    }
}