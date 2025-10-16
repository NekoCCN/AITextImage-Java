package cc.nekocc.aitextimage.server.dto;

import cc.nekocc.aitextimage.model.language.common.OutputLanguage;
import cc.nekocc.aitextimage.model.language.common.SummaryLength;
import cc.nekocc.aitextimage.model.language.common.SummaryStyle;

public record ApiTextRequest(
        String text,
        Double temperature,
        Integer maxKeywords,
        SummaryStyle summaryStyle,
        SummaryLength summaryLength,
        OutputLanguage outputLanguage,
        Integer topK,
        Float topP,
        Float minP,
        Integer nPredict,
        Float repeatPenalty
)
{  }