package cc.nekocc.aitextimage.model.vision.dto.gemini;

public record Part(String text, InlineData inline_data)
{
    public Part(String text)
    {
        this(text, null);
    }

    public Part(InlineData inlineData)
    {
        this(null, inlineData);
    }
}