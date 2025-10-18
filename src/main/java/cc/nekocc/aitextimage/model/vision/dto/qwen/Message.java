package cc.nekocc.aitextimage.model.vision.dto.qwen;

import java.util.List;

public record Message(String role, List<ContentPart> content)
{  }