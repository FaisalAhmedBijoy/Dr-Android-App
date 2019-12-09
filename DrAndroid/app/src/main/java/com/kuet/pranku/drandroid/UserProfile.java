package com.kuet.pranku.drandroid;
public class UserProfile {

    public String userName;
    public String userEmail;
    public String userAge;
    public String userPhone;
    public String userAddress;
    public String userSpecialistAt;
    public String userUid;

    public UserProfile(String userName, String userEmail, String userAge, String userPhone, String userAddress, String userSpecialistAt, String userUid) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userAge = userAge;
        this.userPhone = userPhone;
        this.userAddress = userAddress;
        this.userSpecialistAt = userSpecialistAt;
        this.userUid = userUid;
    }

    public UserProfile(){
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAge() {
        return userAge;
    }

    public void setUserAge(String userAge) {
        this.userAge = userAge;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserSpecialistAt() {
        return userSpecialistAt;
    }

    public void setUserSpecialistAt(String userSpecialistAt) {
        this.userSpecialistAt = userSpecialistAt;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userProfileImageUrl) {
        this.userUid = userProfileImageUrl;
    }
}
