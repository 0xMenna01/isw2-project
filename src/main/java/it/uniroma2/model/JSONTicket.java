package it.uniroma2.model;

import java.util.Date;
import java.util.List;

public class JSONTicket {
    
    private final String key;
    private final Date creationDate;
    private final Date resolutionDate;
    private final List<ReleaseMeta> av;
    
    public JSONTicket(String key, Date creationDate, Date resolutionDate, List<ReleaseMeta> av) {
        this.key = key;
        this.creationDate = creationDate;
        this.resolutionDate = resolutionDate;
        this.av = av;
    }
    
}
