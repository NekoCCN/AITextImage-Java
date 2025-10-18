package cc.nekocc.aitextimage.model.language;

import cc.nekocc.aitextimage.model.common.AiServiceException;
import cc.nekocc.aitextimage.model.common.TextAnalysisResult;
import cc.nekocc.aitextimage.model.language.common.OutputLanguage;
import cc.nekocc.aitextimage.model.language.common.SummaryLength;
import cc.nekocc.aitextimage.model.language.common.SummaryStyle;

import java.util.concurrent.CompletableFuture;

/**
 * 定义了语言模型服务必须提供的核心功能。
 */
public interface LanguageService
{

    /**
     * 设置最多提取的关键字数量。
     *
     * @param maxKeywords 关键字的最大数量
     */
    void setMaxKeywords(int maxKeywords);

    int getMaxKeywords();

    void setSummaryStyle(SummaryStyle style);

    SummaryStyle getSummaryStyle();

    void setSummaryLength(SummaryLength length);

    SummaryLength getSummaryLength();

    void setOutputLanguage(OutputLanguage language);

    OutputLanguage getOutputLanguage();

    /**
     * 分析文本，生成摘要和关键字。
     *
     * @param text 待分析的文本
     * @return 包含摘要和关键字的结果对象
     * @throws AiServiceException 当API调用失败或解析错误时抛出
     */
    CompletableFuture<TextAnalysisResult> analyzeText(String text) throws AiServiceException;
}