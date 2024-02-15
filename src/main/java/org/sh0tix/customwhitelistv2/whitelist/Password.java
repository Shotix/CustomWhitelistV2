package org.sh0tix.customwhitelistv2.whitelist;

public class Password {
    private String passwordDescription;
    private String password;
    private String salt;
    
    public Password(String passwordDescription, String password, String salt) {
        this.passwordDescription = passwordDescription;
        this.password = password;
        this.salt = salt;
    }
    
    public String getPasswordDescription() {
        return passwordDescription;
    }
    
    public void setPasswordDescription(String passwordDescription) {
        this.passwordDescription = passwordDescription;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
