package fr.kaijiro.disco.bot.model;

import java.util.Date;

public class ChuckFact {
    private String id;
    private String fact;
    private Date date;
    private int vote;
    private int points;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFact() {
        return this.fact;
    }

    public void setFact(String fact) {
        this.fact = fact;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getVote() {
        return this.vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    public int getPoints() {
        return this.points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
