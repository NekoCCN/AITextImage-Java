package cc.nekocc.aitextimage.model.vision.dto.gemini;

import java.util.List;

public record GeminiVlmRequest(List<Content> contents)
{  }