package it.uniroma2.model;

import java.util.List;

public class JiraTicket {
    private String key;
    private ReleaseMeta iv;
    private ReleaseMeta ov;
    private List<ReleaseMeta> av;
    private ReleaseMeta fv;

    public JiraTicket(String key, ReleaseMeta ov, List<ReleaseMeta> av, ReleaseMeta fv) {
        this.key = key;
        this.iv = null;
        this.ov = ov;
        this.av = av;
        this.fv = fv;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ReleaseMeta getIv() {
        return iv;
    }

    public void setIv(ReleaseMeta iv) {
        this.iv = iv;
    }

    public ReleaseMeta getOv() {
        return ov;
    }

    public void setOv(ReleaseMeta ov) {
        this.ov = ov;
    }

    public List<ReleaseMeta> getAv() {
        return av;
    }

    public void setAv(List<ReleaseMeta> av) {
        this.av = av;
    }

    public ReleaseMeta getFv() {
        return fv;
    }

    public void setFv(ReleaseMeta fv) {
        this.fv = fv;
    }
}
