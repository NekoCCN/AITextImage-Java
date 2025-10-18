package cc.nekocc.aitextimage.model.language.dto.openai;

import java.util.List;

public record OpenAiChatRequest(String model, List<Message> messages, double temperature)
{  }