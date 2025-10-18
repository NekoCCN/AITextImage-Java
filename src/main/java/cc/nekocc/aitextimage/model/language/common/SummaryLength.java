package cc.nekocc.aitextimage.model.language.common;

/**
 * 定义了摘要的期望长度。
 */
public enum SummaryLength
{
    SHORT("Short (approx. 1-2 sentences)"),
    MEDIUM("Medium (approx. 3-5 sentences)"),
    LONG("Long (a detailed paragraph)");

    private final String lengthDescription;

    SummaryLength(String lengthDescription)
    {
        this.lengthDescription = lengthDescription;
    }

    public String getLengthDescription()
    {
        return lengthDescription;
    }
}
