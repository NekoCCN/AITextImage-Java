package cc.nekocc.aitextimage.model.language.dto.openai;

import java.util.List;

public class OpenAiChatResponse
{
    public List<Choice> choices;

    public static class Choice
    {
        public Message message;
    }
}