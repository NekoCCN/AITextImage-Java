package cc.nekocc.aitextimage.model.vision.impl;

import cc.nekocc.aitextimage.model.UserSession;
import cc.nekocc.aitextimage.model.common.AiServiceException;
import cc.nekocc.aitextimage.model.common.TextAnalysisResult;
import cc.nekocc.aitextimage.model.language.dto.gemini.GeminiResponse;
import cc.nekocc.aitextimage.model.vision.VlmService;
import cc.nekocc.aitextimage.model.vision.dto.gemini.Content;
import cc.nekocc.aitextimage.model.vision.dto.gemini.GeminiVlmRequest;
import cc.nekocc.aitextimage.model.vision.dto.gemini.InlineData;
import cc.nekocc.aitextimage.model.vision.dto.gemini.Part;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GeminiVlmService implements VlmService
{

    private static final Executor VIRTUAL_THREAD_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();
    private final String apiKey;
    private final String model;
    private static final String API_URL_TEMPLATE = "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent";

    public GeminiVlmService(String apiKey, String model)
    {
        this.apiKey = apiKey;
        this.model = model;
    }

    @Override
    public CompletableFuture<TextAnalysisResult> describeImage(Path imagePath, String prompt)
    {
        return CompletableFuture.supplyAsync(() ->
        {
            try
            {
                byte[] imageBytes = Files.readAllBytes(imagePath);
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                String mimeType = Files.probeContentType(imagePath);

                Part promptPart = new Part(prompt);
                Part imagePart = new Part(new InlineData(mimeType, base64Image));
                Content content = new Content(List.of(promptPart, imagePart));
                GeminiVlmRequest payload = new GeminiVlmRequest(List.of(content));
                String jsonBody = UserSession.getInstance().getGson().toJson(payload);

                String apiUrl = String.format(API_URL_TEMPLATE, this.model) + "?key=" + this.apiKey;
                HttpPost request = new HttpPost(new URI(apiUrl));
                request.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));

                try (var response = UserSession.getInstance().getHttpClient().execute(request))
                {
                    String responseBody = EntityUtils.toString(response.getEntity());

                    GeminiResponse geminiResponse = UserSession.getInstance().getGson().fromJson(responseBody, GeminiResponse.class);
                    String contentJson = geminiResponse.candidates().getFirst().content().parts().get(0).text();

                    contentJson = contentJson.replace("```json", "").replace("```", "").trim();

                    return UserSession.getInstance().getGson().fromJson(contentJson, TextAnalysisResult.class);
                }
            } catch (Exception e)
            {
                throw new RuntimeException(new AiServiceException("图片描述失败", e));
            }
        }, VIRTUAL_THREAD_EXECUTOR);
    }
}