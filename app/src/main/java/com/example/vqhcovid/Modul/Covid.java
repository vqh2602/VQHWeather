package com.example.vqhcovid.Modul;

public class Covid {
    private String lastUpdatedAtSource;

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getRecovered() {
        return recovered;
    }

    public void setRecovered(int recovered) {
        this.recovered = recovered;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getCases() {
        return cases;
    }

    public void setCases(int cases) {
        this.cases = cases;
    }

    private int active;
    private int recovered;
    private int deaths;
    private int cases;

    public String getLastUpdatedAtSource() {
        return lastUpdatedAtSource;
    }

    public void setLastUpdatedAtSource(String lastUpdatedAtSource) {
        this.lastUpdatedAtSource = lastUpdatedAtSource;
    }


}
