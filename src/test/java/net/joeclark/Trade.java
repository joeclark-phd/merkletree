package net.joeclark;

import java.io.Serializable;

/** A serializable class for use in HashHelperTest */
public class Trade implements Serializable {
    private static final long serialVersionUID = 1L;
    private String sender;
    private String receiver;
    private int amount;
    public Trade(String sender, String receiver, int amount) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
    }
}
