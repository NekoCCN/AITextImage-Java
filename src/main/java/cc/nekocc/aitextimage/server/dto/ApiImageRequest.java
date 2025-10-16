package cc.nekocc.aitextimage.server.dto;

public record ApiImageRequest(
        String imagePath,
        String prompt
) {}