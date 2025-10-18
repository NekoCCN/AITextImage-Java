package cc.nekocc.aitextimage.model.language.common;


public enum OutputLanguage
{
    CHINESE("Chinese"),
    ENGLISH("English"),
    JAPANESE("Japanese"),
    RUSSIAN("Russian"),
    FRENCH("French"),
    ITALIAN("Italian");

    private final String displayName;

    OutputLanguage(String displayName)
    {
        this.displayName = displayName;
    }

    public String getDisplayName()
    {
        return displayName;
    }
}
