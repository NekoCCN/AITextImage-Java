package cc.nekocc.aitextimage.model.language.common;

/**
 * 定义了摘要的生成风格。
 */
public enum SummaryStyle
{
    FORMAL("Formal"),
    CONCISE("Concise"),
    COLLOQUIAL("Colloquial");

    private final String styleName;

    SummaryStyle(String styleName)
    {
        this.styleName = styleName;
    }

    public String getStyleName()
    {
        return styleName;
    }
}