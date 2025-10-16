package cc.nekocc.aitextimage.model.language.dto.gemini;

import java.util.List;

public record GeminiRequest(List<Content> contents, GenerationConfig generationConfig)
{  }