package Models;

import java.util.Date;

public class UserProfile {
    private int userId;
    private String name;
    private String email;
    private String password;
    private String sex;
    private Date dob;
    private float heightInMeter;
    private float weightInKG;
    private String units;

    public UserProfile(String name, String email, String sex, Date dob,
            float heightInMeter, float weightInKG, String units) {
              this.name = name;
              this.email = email;
              this.sex = sex;
              this.dob = dob;
              this.heightInMeter = heightInMeter;
              this.weightInKG = weightInKG;
              this.units = units;
}

    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getSex() { return sex; }
    public Date getDob() { return dob; }
    public float getHeight() { return heightInMeter; }
    public float getWeight() { return weightInKG; }
    public String getUnits() { return units; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setSex(String sex) { this.sex = sex; }
    public void setDob(Date dob) { this.dob = dob; }
    public void setHeightInMeter(float heightInMeter) { this.heightInMeter = heightInMeter; }
    public void setWeightInKG(float weightInKG) { this.weightInKG = weightInKG; }
    public void setUnits(String units) { this.units = units; }
    }
