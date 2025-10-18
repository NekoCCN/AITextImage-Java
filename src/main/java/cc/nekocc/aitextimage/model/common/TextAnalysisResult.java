package cc.nekocc.aitextimage.model.common;

import java.util.List;

/**
 * 封装文本分析结果的通用数据记录。
 */
public record TextAnalysisResult(String summary, List<String> tags)
{  }