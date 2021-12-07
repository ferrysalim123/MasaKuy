package id.ac.umn.masakuy;

import java.io.Serializable;

public class User implements Serializable {
    private int user_id;
    private String name;
    private String email;
    private String phone_number;
    private String profile_pict;

    public User(int user_id, String name, String email, String phone_number, String profile_pict) {
        this.user_id = user_id;
        this.name = name;
        this.email = email;
        this.phone_number = phone_number;
        this.profile_pict = profile_pict;
    }

    public int getUserId() {
        return this.user_id;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPhoneNumber() {
        return this.phone_number;
    }

    public String getProfilePict() {
        return this.profile_pict;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setPhoneNumber(String phone_number){
        this.phone_number = phone_number;
    }

    public void setProfilePict(String profile_pict){
        this.profile_pict = profile_pict;
    }
}
