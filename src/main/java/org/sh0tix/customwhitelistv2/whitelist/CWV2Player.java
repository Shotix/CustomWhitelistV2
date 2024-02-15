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
        UNKNOWN
    }
    
    String uuid;
    String name;
    Status status;
    
    Date lastUpdated;
    
    int numberOfTimesJoined;
    
    int numberOfWrongPasswordsEntered;

    public CWV2Player(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.status = Status.NOT_WHITELISTED;
        this.lastUpdated = getUpdatedTime();
        this.numberOfTimesJoined = 0;
        this.numberOfWrongPasswordsEntered = 0;
    }

    public CWV2Player(String uuid, String name, int numberOfTimesJoined) {
        this.uuid = uuid;
        this.name = name;
        this.status = Status.NOT_WHITELISTED;
        this.lastUpdated = getUpdatedTime();
        this.numberOfTimesJoined = numberOfTimesJoined;
        this.numberOfWrongPasswordsEntered = 0;
    }

    private static Date getUpdatedTime() {
        return new Date(System.currentTimeMillis());
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
