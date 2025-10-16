package cc.nekocc.aitextimage.model.language;

import cc.nekocc.aitextimage.model.UserSession;
import cc.nekocc.aitextimage.model.common.AiServiceException;
import cc.nekocc.aitextimage.model.common.TextAnalysisResult;
import cc.nekocc.aitextimage.model.language.common.OutputLanguage;
import cc.nekocc.aitextimage.model.language.common.SummaryLength;
import cc.nekocc.aitextimage.model.language.common.SummaryStyle;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public abstract class AbstractLanguageApiService implements LanguageService
{

    protected final String base_url;
    protected final double temperature;

    protected int maxKeywords;
    protected SummaryStyle summaryStyle = SummaryStyle.CONCISE;
    protected SummaryLength summaryLength = SummaryLength.MEDIUM;
    protected OutputLanguage outputLanguage = OutputLanguage.ENGLISH;

    public AbstractLanguageApiService(double temperature)
    {
        this.temperature = temperature;
        this.base_url = getApiUrl();
        this.maxKeywords = 5;
    }

    @Override
    public final CompletableFuture<TextAnalysisResult> analyzeText(String text)
    {
        return CompletableFuture.supplyAsync(() ->
        {
            try
            {
                HttpPost request = new HttpPost(getApiUrl());
                buildRequestDetails(request, text);
                try (CloseableHttpResponse response = UserSession.getInstance().getHttpClient().execute(request))
                {
                    int statusCode = response.getCode();
                    if (statusCode < 200 || statusCode >= 300)
                    {
                        String errorBody = response.getEntity() != null ? EntityUtils.toString(response.getEntity()) : "No response body";
                        throw new IOException("API请求失败: " + statusCode + " " + errorBody);
                    }
                    String responseBody = EntityUtils.toString(response.getEntity());
                    return parseSuccessResponse(responseBody);
                }
            } catch (Exception e)
            {
                throw new RuntimeException(new AiServiceException("请求或处理AI服务时出错", e));
            }
        }, Executors.newSingleThreadExecutor());
    }

    protected String buildAdvancedPrompt(String text)
    {
        String promptTemplate = """
                SYSTEM:
                You are a highly intelligent linguistic analyst. Your task is to return a structured JSON object with 'summary' and 'tags' keys. Your response must be ONLY the raw JSON object.

                USER:
                Analyze the text provided below.

                ## TASKS:
                1.  Generate a summary of the text.
                2.  Extract up to %d relevant keywords/tags from the text.

                ## CONSTRAINTS:
                - Summary Style: The summary MUST be written in a %s style.
                - Summary Length: The summary's length should be %s.
                - Output Language: The 'summary' and 'tags' MUST be in %s.
                - Terminology: You MUST keep universally recognized technical terms, acronyms, and proper nouns in their original English form.

                --- TEXT FOR ANALYSIS ---
                %s
                --- END OF TEXT ---

                Now, provide the JSON response:
                """;

        return String.format(promptTemplate,
                this.maxKeywords,
                this.summaryStyle.getStyleName(),
                this.summaryLength.getLengthDescription(),
                this.outputLanguage.getDisplayName(),
                text
        );
    }

    protected void buildRequestDetails(HttpPost request, String text)
    {
        String requestBodyJson = buildRequestBodyJson(text);
        StringEntity entity = new StringEntity(requestBodyJson, ContentType.APPLICATION_JSON);
        request.setHeader("Authorization", "Bearer " + getApiKey());
        request.setEntity(entity);
    }

    // --- 接口实现 ---
    @Override
    public void setMaxKeywords(int maxKeywords)
    {
        this.maxKeywords = maxKeywords;
    }

    @Override
    public int getMaxKeywords()
    {
        return this.maxKeywords;
    }

    @Override
    public void setSummaryStyle(SummaryStyle style)
    {
        this.summaryStyle = style;
    }

    @Override
    public SummaryStyle getSummaryStyle()
    {
        return this.summaryStyle;
    }

    @Override
    public void setSummaryLength(SummaryLength length)
    {
        this.summaryLength = length;
    }

    @Override
    public SummaryLength getSummaryLength()
    {
        return this.summaryLength;
    }

    @Override
    public void setOutputLanguage(OutputLanguage language)
    {
        this.outputLanguage = language;
    }

    @Override
    public OutputLanguage getOutputLanguage()
    {
        return this.outputLanguage;
    }

    protected abstract String getApiUrl();

    protected abstract String getApiKey();

    protected abstract String buildRequestBodyJson(String text);

    protected abstract TextAnalysisResult parseSuccessResponse(String jsonBody);
}