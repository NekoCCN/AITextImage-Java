package cc.nekocc.aitextimage.model;

public class AppSettings
{

    private LanguageSettings languageSettings = new LanguageSettings();
    private SyncSettings syncSettings = new SyncSettings();
    private VlmSettings vlmSettings = new VlmSettings();
    private ServerSettings serverSettings = new ServerSettings();

    public LanguageSettings getLanguageSettings()
    {
        return languageSettings;
    }

    public void setLanguageSettings(LanguageSettings languageSettings)
    {
        this.languageSettings = languageSettings;
    }

    public SyncSettings getSyncSettings()
    {
        return syncSettings;
    }

    public void setSyncSettings(SyncSettings syncSettings)
    {
        this.syncSettings = syncSettings;
    }

    public VlmSettings getVlmSettings()
    {
        return vlmSettings;
    }

    public void setVlmSettings(VlmSettings vlmSettings)
    {
        this.vlmSettings = vlmSettings;
    }

    public ServerSettings getServerSettings()
    {
        return serverSettings;
    }
    public void setServerSettings(ServerSettings serverSettings)
    {
        this.serverSettings = serverSettings;
    }
}