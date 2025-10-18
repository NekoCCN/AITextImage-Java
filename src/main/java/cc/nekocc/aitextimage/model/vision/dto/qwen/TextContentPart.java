package cc.nekocc.aitextimage.model.vision.dto.qwen;

public record TextContentPart(String type, String text) implements ContentPart
{
    public TextContentPart(String text)
    {
        this("text", text);
    }
}