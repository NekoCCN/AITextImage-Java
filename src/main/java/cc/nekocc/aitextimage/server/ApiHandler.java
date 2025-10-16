package cc.nekocc.aitextimage.server;

import cc.nekocc.aitextimage.model.AppSettings;
import cc.nekocc.aitextimage.model.UserSession;
import cc.nekocc.aitextimage.model.VlmSettings;
import cc.nekocc.aitextimage.model.common.TextAnalysisResult;
import cc.nekocc.aitextimage.model.language.impl.DeepSeekService;
import cc.nekocc.aitextimage.model.ServerSettings;
import cc.nekocc.aitextimage.model.vision.impl.GeminiVlmService;
import cc.nekocc.aitextimage.server.dto.ApiImageRequest;
import cc.nekocc.aitextimage.server.dto.ApiTextRequest;
import com.google.gson.Gson;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class ApiHandler extends SimpleChannelInboundHandler<FullHttpRequest>
{

    private final LlamaTaskQueue llamaTaskQueue = LlamaTaskQueue.getInstance();
    private final Gson gson = UserSession.getInstance().getGson();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request)
    {
        String uri = request.uri();
        HttpMethod method = request.method();

        if (method != HttpMethod.POST)
        {
            sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
            return;
        }

        try
        {
            String jsonPayload = request.content().toString(StandardCharsets.UTF_8);
            CompletableFuture<TextAnalysisResult> future;

            switch (uri)
            {
                case "/api/text/cloud":
                    future = handleApiTextAnalysis(gson.fromJson(jsonPayload, ApiTextRequest.class));
                    break;
                case "/api/text/local":
                    future = handleLocalTextAnalysis(gson.fromJson(jsonPayload, ApiTextRequest.class));
                    break;
                case "/api/image/cloud":
                    future = handleApiImageAnalysis(gson.fromJson(jsonPayload, ApiImageRequest.class));
                    break;
                default:
                    sendError(ctx, HttpResponseStatus.NOT_FOUND);
                    return;
            }

            future.whenComplete((result, throwable) ->
            {
                if (throwable != null)
                {
                    sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, throwable.getMessage());
                } else
                {
                    sendOk(ctx, gson.toJson(result));
                }
            });

        } catch (Exception e)
        {
            sendError(ctx, HttpResponseStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private CompletableFuture<TextAnalysisResult> handleApiTextAnalysis(ApiTextRequest request)
    {
        ServerSettings settings = UserSession.getInstance().getAppSettings().getServerSettings();

        DeepSeekService service = new DeepSeekService(settings.serverDashScopeApiKey, "deepseek-chat", request.temperature() != null ? request.temperature() : 0.7);

        if (request.maxKeywords() != null)
            service.setMaxKeywords(request.maxKeywords());
        if (request.summaryStyle() != null)
            service.setSummaryStyle(request.summaryStyle());
        if (request.summaryLength() != null)
            service.setSummaryLength(request.summaryLength());
        if (request.outputLanguage() != null)
            service.setOutputLanguage(request.outputLanguage());

        return service.analyzeText(request.text());
    }

    private CompletableFuture<TextAnalysisResult> handleLocalTextAnalysis(ApiTextRequest request)
    {
        return llamaTaskQueue.submit(request);
    }

    private CompletableFuture<TextAnalysisResult> handleApiImageAnalysis(ApiImageRequest request)
    {
        VlmSettings settings = UserSession.getInstance().getAppSettings().getVlmSettings();
        GeminiVlmService service = new GeminiVlmService(settings.geminiApiKey, settings.geminiModel);
        return service.describeImage(Path.of(request.imagePath()), request.prompt());
    }

    private void sendResponse(ChannelHandlerContext ctx, HttpResponseStatus status, String content, String contentType)
    {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                Unpooled.copiedBuffer(content, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void sendOk(ChannelHandlerContext ctx, String jsonContent)
    {
        sendResponse(ctx, HttpResponseStatus.OK, jsonContent, "application/json; charset=UTF-8");
    }

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status, String message)
    {
        String content = String.format("{\"error\":\"%s\", \"message\":\"%s\"}", status.reasonPhrase(), message);
        sendResponse(ctx, status, content, "application/json; charset=UTF-8");
    }

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status)
    {
        sendError(ctx, status, "");
    }
}