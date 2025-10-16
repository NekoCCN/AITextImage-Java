package cc.nekocc.aitextimage.model.language.impl;

import cc.nekocc.aitextimage.model.UserSession;
import cc.nekocc.aitextimage.model.common.TextAnalysisResult;
import cc.nekocc.aitextimage.model.language.AbstractLanguageApiService;
import cc.nekocc.aitextimage.model.language.dto.gemini.*;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

public class GeminiService extends AbstractLanguageApiService
{

    private final String apiKey;
    private final String model;
    private static final String PROD_API_URL_TEMPLATE = "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent";

    public GeminiService(String apiKey, String model, double temperature)
    {
        super(temperature);
        this.apiKey = apiKey;
        this.model = model;
    }

    @Override
    protected String getApiUrl()
    {
        return String.format(PROD_API_URL_TEMPLATE, this.model);
    }

    @Override
    protected String getApiKey()
    {
        return apiKey;
    }

    @Override
    protected void buildRequestDetails(HttpPost request, String text)
    {
        try
        {
            String urlWithKey = getApiUrl() + "?key=" + getApiKey();
            request.setUri(new URI(urlWithKey));

            String request_body_json = buildRequestBodyJson(text);
            StringEntity entity = new StringEntity(request_body_json, ContentType.APPLICATION_JSON);
            request.setEntity(entity);
        }
        catch (URISyntaxException e)
        {
            throw new RuntimeException("创建Gemini API URI失败", e);
        }
    }

    @Override
    protected String buildRequestBodyJson(String text)
    {
        String prompt = buildAdvancedPrompt(text);

        GeminiRequest requestPayload = new GeminiRequest(
                List.of(new Content(List.of(new Part(prompt)))),
                new GenerationConfig(this.temperature, 2048, ResponseMimeType.APPLICATION_JSON)
        );
        return UserSession.getInstance().getGson().toJson(requestPayload);
    }

    @Override
    protected TextAnalysisResult parseSuccessResponse(String jsonBody)
    {
        GeminiResponse response = UserSession.getInstance().getGson().fromJson(jsonBody, GeminiResponse.class);
        String contentJson = response.candidates().getFirst().content().parts().get(0).text();
        return UserSession.getInstance().getGson().fromJson(contentJson, TextAnalysisResult.class);
    }
}