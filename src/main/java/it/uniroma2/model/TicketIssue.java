package it.uniroma2.model;

import java.util.List;

public class TicketIssue extends BaseTicket {

    private ReleaseMeta iv;

    public TicketIssue(String key, ReleaseMeta ov, ReleaseMeta fv, List<ReleaseMeta> av, ReleaseMeta iv) {
        super(key, ov, fv, av);
        this.iv = iv;
    }

    

}
