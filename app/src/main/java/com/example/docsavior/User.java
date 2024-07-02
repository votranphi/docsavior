package com.example.docsavior;

public class User {
    private String username;

    private String email;

    private String phoneNumber;

    private boolean isActive;

    private String fullName;

    private String birthDate;

    private boolean gender; // male is 1 and female is 0

    private String avatarData;

    private String avatarName;

    private String avatarExtension;

    public User(String username, String email, String phoneNumber, boolean isActive, String fullName, String birthDate, boolean gender, String avatarData, String avatarName, String avatarExtension) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isActive = isActive;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.avatarData = avatarData;
        this.avatarName = avatarName;
        this.avatarExtension = avatarExtension;
    }

    public User(String username, String avatarData, String avatarName, String avatarExtension) {
        this.username = username;
        this.avatarData = avatarData;
        this.avatarName = avatarName;
        this.avatarExtension = avatarExtension;
    }

    public User() {
        username = "";
        email = "";
        phoneNumber = "";
        isActive = false;
        fullName = "";
        birthDate = "";
        gender = false;
        avatarData = "";
        avatarName = "";
        avatarExtension = "";
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public String getFullName() {
        return fullName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public boolean getGender() {
        return gender;
    }

    public String getAvatarData() {
        return avatarData;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public String getAvatarExtension() {
        return avatarExtension;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public void setAvatarData(String avatarData) {
        this.avatarData = avatarData;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public void setAvatarExtension(String avatarExtension) {
        this.avatarExtension = avatarExtension;
    }
}
