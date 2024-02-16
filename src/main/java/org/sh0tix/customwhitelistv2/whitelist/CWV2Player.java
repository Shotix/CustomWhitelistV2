package org.sh0tix.customwhitelistv2.whitelist;

import java.util.Date;

public class CWV2Player {

    public enum Status {
        WHITELISTED,
        NOT_WHITELISTED,
        BANNED,
        KICKED,
        TEMP_BANNED,
        TEMP_KICKED,
        REMOVED,
        UNKNOWN
    }
    
    String uuid;
    String username;
    Status status;
    
    Date lastUpdated;
    
    int numberOfTimesJoined;
    
    int numberOfWrongPasswordsEntered;

    public CWV2Player(String uuid, String username) {
        this.uuid = uuid;
        this.username = username;
        this.status = Status.NOT_WHITELISTED;
        this.lastUpdated = getUpdatedTime();
        this.numberOfTimesJoined = 0;
        this.numberOfWrongPasswordsEntered = 0;
    }

    public CWV2Player(String uuid, String username, int numberOfTimesJoined) {
        this.uuid = uuid;
        this.username = username;
        this.status = Status.NOT_WHITELISTED;
        this.lastUpdated = getUpdatedTime();
        this.numberOfTimesJoined = numberOfTimesJoined;
        this.numberOfWrongPasswordsEntered = 0;
    }

    public static Date getUpdatedTime() {
        return new Date(System.currentTimeMillis());
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getNumberOfTimesJoined() {
        return numberOfTimesJoined;
    }

    public void setNumberOfTimesJoined(int numberOfTimesJoined) {
        this.numberOfTimesJoined = numberOfTimesJoined;
    }

    public int getNumberOfWrongPasswordsEntered() {
        return numberOfWrongPasswordsEntered;
    }

    public void setNumberOfWrongPasswordsEntered(int numberOfWrongPasswordsEntered) {
        this.numberOfWrongPasswordsEntered = numberOfWrongPasswordsEntered;
    }
}
