package cc.nekocc.aitextimage.model.language.dto.gemini;

import java.util.List;

public record GeminiResponse(List<Candidate> candidates)
{  }