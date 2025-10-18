package cc.nekocc.aitextimage.model.vision.dto.qwen;

import com.google.gson.annotations.SerializedName;

public sealed interface ContentPart permits TextContentPart, ImageUrlContentPart
{  }