package fr.kaijiro.disco.bot.configuration;

public enum DiscoBotOption {

    CONFIGURATION_DIRECTORY("d", "directory"),
    BOT_TOKEN("t", "token"),
    HELP("h", "help");

    private String tinyOpt;
    private String longOpt;

    DiscoBotOption(String tinyOpt, String longOpt){
        this.tinyOpt = tinyOpt;
        this.longOpt = longOpt;
    }

    public String getTinyOpt() {
        return tinyOpt;
    }

    public void setTinyOpt(String tinyOpt) {
        this.tinyOpt = tinyOpt;
    }

    public String getLongOpt() {
        return longOpt;
    }

    public void setLongOpt(String longOpt) {
        this.longOpt = longOpt;
    }
}
