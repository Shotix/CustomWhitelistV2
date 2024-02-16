package org.sh0tix.customwhitelistv2.whitelist;

import net.kyori.adventure.text.Component;

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
    
    private static class TempBannedOrKicked {
        Date dateOfBanOrKick;
        Date dateOfUnbanOrUnkick;
        Component reason;
        int numberOfTimesBannedOrKicked;
        Date lastTimeBannedOrKicked;
        
        public TempBannedOrKicked() {
            this.dateOfBanOrKick = null;
            this.dateOfUnbanOrUnkick = null;
            this.reason = null;
            this.numberOfTimesBannedOrKicked = 0;
            this.lastTimeBannedOrKicked = null;
        }

        public Date getDateOfBanOrKick() {
            return dateOfBanOrKick;
        }

        public void setDateOfBanOrKick(Date dateOfBanOrKick) {
            this.dateOfBanOrKick = dateOfBanOrKick;
        }

        public Date getDateOfUnbanOrUnkick() {
            return dateOfUnbanOrUnkick;
        }

        public void setDateOfUnbanOrUnkick(Date dateOfUnbanOrUnkick) {
            this.dateOfUnbanOrUnkick = dateOfUnbanOrUnkick;
        }

        public Component getReason() {
            return reason;
        }

        public void setReason(Component reason) {
            this.reason = reason;
        }

        public int getNumberOfTimesBannedOrKicked() {
            return numberOfTimesBannedOrKicked;
        }

        public void setNumberOfTimesBannedOrKicked(int numberOfTimesBannedOrKicked) {
            this.numberOfTimesBannedOrKicked = numberOfTimesBannedOrKicked;
        }

        public Date getLastTimeBannedOrKicked() {
            return lastTimeBannedOrKicked;
        }

        public void setLastTimeBannedOrKicked(Date lastTimeBannedOrKicked) {
            this.lastTimeBannedOrKicked = lastTimeBannedOrKicked;
        }
    }
    
    String uuid;
    String username;
    Status status;
    
    Date lastUpdated;
    
    int numberOfTimesJoined;
    
    int numberOfWrongPasswordsEntered;
    TempBannedOrKicked tempBannedOrKicked;

    public CWV2Player(String uuid, String username) {
        this.uuid = uuid;
        this.username = username;
        this.status = Status.NOT_WHITELISTED;
        this.lastUpdated = getUpdatedTime();
        this.numberOfTimesJoined = 0;
        this.numberOfWrongPasswordsEntered = 0;
        this.tempBannedOrKicked = null;
    }

    public CWV2Player(String uuid, String username, int numberOfTimesJoined) {
        this.uuid = uuid;
        this.username = username;
        this.status = Status.NOT_WHITELISTED;
        this.lastUpdated = getUpdatedTime();
        this.numberOfTimesJoined = numberOfTimesJoined;
        this.numberOfWrongPasswordsEntered = 0;
        this.tempBannedOrKicked = null;
    }
    
    private void initTempBannedOrKicked() {
        this.tempBannedOrKicked = new TempBannedOrKicked();
    }
    
    public Date getDateOfBanOrKick() {
        if (this.tempBannedOrKicked == null) {
            return null;
        }
        return this.tempBannedOrKicked.getDateOfBanOrKick();
    }
    
    public Date getDateOfUnbanOrUnkick() {
        if (this.tempBannedOrKicked == null) {
            return null;
        }
        return this.tempBannedOrKicked.getDateOfUnbanOrUnkick();
    }
    
    public Component getReason() {
        if (this.tempBannedOrKicked == null) {
            return null;
        }
        return this.tempBannedOrKicked.getReason();
    }
    
    public int getNumberOfTimesBannedOrKicked() {
        if (this.tempBannedOrKicked == null) {
            return 0;
        }
        return this.tempBannedOrKicked.getNumberOfTimesBannedOrKicked();
    }
    
    public Date getLastTimeBannedOrKicked() {
        if (this.tempBannedOrKicked == null) {
            return null;
        }
        return this.tempBannedOrKicked.getLastTimeBannedOrKicked();
    }
    
    public void setTempBannedOrKicked(Date dateOfUnbanOrUnkick, Component reason) {
        if (this.tempBannedOrKicked == null) {
            initTempBannedOrKicked();
        }
        
        this.tempBannedOrKicked.setDateOfBanOrKick(getUpdatedTime());
        this.tempBannedOrKicked.setDateOfUnbanOrUnkick(dateOfUnbanOrUnkick);
        this.tempBannedOrKicked.setReason(reason);
        this.tempBannedOrKicked.setNumberOfTimesBannedOrKicked(this.tempBannedOrKicked.getNumberOfTimesBannedOrKicked() + 1);
        this.tempBannedOrKicked.setLastTimeBannedOrKicked(getUpdatedTime());
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
