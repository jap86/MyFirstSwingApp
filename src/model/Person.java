package model;

import java.io.Serializable;

public class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int count = 1;

    private int id;
    private String name;
    private String occupation;
    private AgeCategory ageCategory;
    private MaritalStatus maritalStatus;
    private Boolean isClubMember;
    private String memberID;
    private Gender gender;

    public Person(String name, String occupation, AgeCategory ageCategory, MaritalStatus maritalStatus,
                  Boolean isClubMember, String memberID, Gender gender) {
        this.name = name;
        this.occupation = occupation;
        this.ageCategory = ageCategory;
        this.maritalStatus = maritalStatus;
        this.isClubMember = isClubMember;
        this.memberID = memberID;
        this.gender = gender;

        this.id = count++;
    }

    public Person(int id, String name, String occupation, AgeCategory ageCategory,
                  MaritalStatus maritalStatus, Boolean isClubMember, String memberID, Gender gender) {
        this(name, occupation, ageCategory, maritalStatus, isClubMember, memberID, gender);

        this.id = id;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", occupation='" + occupation + '\'' +
                ", ageCategory=" + ageCategory +
                ", maritalStatus=" + maritalStatus +
                ", isClubMember=" + isClubMember +
                ", memberID='" + memberID + '\'' +
                ", gender=" + gender +
                '}';
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOccupation() {
        return occupation;
    }

    public AgeCategory getAgeCategory() {
        return ageCategory;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public Boolean getIsClubMember() {
        return isClubMember;
    }

    public String getMemberID() {
        return memberID;
    }

    public Gender getGender() {
        return gender;
    }
}
