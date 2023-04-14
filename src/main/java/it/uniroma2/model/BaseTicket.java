package it.uniroma2.model;

import java.util.List;

import it.uniroma2.exception.TicketException;

public class BaseTicket {

    private final String key;
    private final ReleaseMeta ov;
    private final ReleaseMeta fv;
    private final List<ReleaseMeta> av; // could be empty if no affected versions in jira

    public BaseTicket(String key, ReleaseMeta ov, ReleaseMeta fv, List<ReleaseMeta> av) {
        this.key = key;
        this.ov = ov;
        this.fv = fv;
        this.av = av;
    }

    public String getKey() {
        return key;
    }

    public List<ReleaseMeta> getAv() {
        return av;
    }

    public boolean isValid(ReleaseMeta firstRelease) { //Assuming the releases are ordered by date
        if (ov != null && fv != null) {
            return !ov.isAfter(this.fv) && ov.isAfter(firstRelease); // Be sure that ov is not after fv and that ov is after the first release
        }
        return false;
    }

    public boolean hasValidIV() throws TicketException {
        if(ov == null || fv == null){
            throw new TicketException("Must be a valid ticket to check injected version");
        }
        return !av.isEmpty() && av.get(0).isBefore(ov);
    }

    public ReleaseMeta getIV() throws TicketException{
        if (!hasValidIV()) {
            throw new TicketException("Must have a valid IV to compute");
        }
        return av.get(0);
    }

    public ReleaseMeta getOv() {
        return ov;
    }

    public ReleaseMeta getFv() {
        return fv;
    }

}
