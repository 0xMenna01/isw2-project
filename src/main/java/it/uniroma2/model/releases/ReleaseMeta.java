package it.uniroma2.model.releases;

import java.util.Date;

public class ReleaseMeta {

    protected Integer id;
    protected String name;
    protected Date date;

    public ReleaseMeta(int id, String name, Date date) {
        this.id = id;
        this.name = name;
        this.date = date;
    }

    public ReleaseMeta(int id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isAfter(ReleaseMeta rel) {
        return this.date.after(rel.getDate());
    }

    public boolean isBefore(ReleaseMeta rel) {
        return this.date.before(rel.getDate());
    }
}
