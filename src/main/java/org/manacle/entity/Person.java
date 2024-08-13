package org.manacle.entity;

import org.manacle.Constants;

public class Person {

  private final Info constituencyInfo;

  private final String imageName;
  private String name;
  private String father;
  private String mother;
  private String other;
  private String husband;
  private String house;
  private int age;
  private String gender;
  private String voterID;
  private int serialNumber;

  public Person(Info constituencyInfo, String imageName) {
    this.constituencyInfo = constituencyInfo;
    this.imageName = imageName;
  }

  public void setName(String name) {
    try { name = name.substring(name.indexOf(":")+1).trim(); } catch (Exception e) {
      System.err.println("No delimiter : found in name " + name + " in " + imageName);
    }
    this.name = name.replaceAll(Constants.fieldName,  "").trim();
  }

  public void setFather(String father) {
    try { father = father.substring(father.indexOf(":")+1).trim(); } catch (Exception e) {
      System.err.println("No delimiter : found in father " + father + " in " + imageName);
    }
    this.father =  father.replaceAll(Constants.fieldFather,  "").trim();
  }

  public void setMother(String mother) {
    try { mother = mother.substring(mother.indexOf(":")+1).trim(); } catch (Exception e) {
      System.err.println("No delimiter : found in mother " + mother + " in " + imageName);
    }
    this.mother = mother.replaceAll(Constants.fieldMother,  "").trim();
  }

  public void setOther(String other) {
    try { other = other.substring(other.indexOf(":")+1).trim(); } catch (Exception e) {
      System.err.println("No delimiter : found in other " + other + " in " + imageName);
    }
    this.other = other.replaceAll(Constants.fieldOther,  "").trim();;
  }

  public void setHusband(String husband) {
    try { husband = husband.substring(husband.indexOf(":")+1).trim(); } catch (Exception e) {
      System.err.println("No delimiter : found in husband " + husband + " in " + imageName);
    }
    this.husband = husband.replaceAll(Constants.fieldHusband,  "").trim();;
  }

  public void setHouse(String house) {
    try {
      house = house.substring(house.indexOf(":")+1);
      house = house.replace(Constants.fieldExtra2, "").trim();
    } catch (Exception e) {
      System.err.println("No delimiter : found in house " + house + " in " + imageName);
    }
    this.house = house.replaceAll(Constants.fieldHouse,  "").trim();;
  }

  public void setAge(String str) {
    // System.out.println(str);
    try {
      this.age = onlyDigits(str);
    } catch (Exception e) {
      System.err.println("Error in age " + age + " in " + imageName + " " + e.getMessage());
    }
    // might be possible that GENDER info is also inside this
    if (str.contains(Constants.fieldGender) && getGender() == null) setGender(str);
    if (str.contains(Constants.fieldGender1) && getGender() == null) setGender(str);
  }

  public void setGender(String str) {
    int index = str.indexOf(Constants.fieldGender);
    if(index<0) index = str.indexOf(Constants.fieldGender1);
    if(index>0) {
      this.gender = onlyAlphabets(str.substring(index+Constants.fieldGender.length()));
    }
  }

  public String getGender() {
    return gender;
  }

  public void setVoterID(String voterID) {
    this.voterID = onlyAlphanumeric(voterID);
    if(this.voterID.length()!=10) {
      System.err.println("Invalid VoterID : " + this.voterID + " in " + imageName + " ");

    }
  }

  public void setSerialNumber(String serialNumber) {
    try {
      if(serialNumber==null || serialNumber.trim().isEmpty()) return;
      this.serialNumber = onlyDigits(serialNumber);
    } catch (Exception e) {
      System.err.println("Error in serial number " + serialNumber + " in " + imageName + " " + e.getLocalizedMessage());
    }
  }

  private int onlyDigits(String str) {
    return Integer.parseInt(str.replaceAll("[^0-9.]", ""));
  }

  private String onlyAlphabets(String str) {
    return str.replaceAll("[^a-z.A-Z]", "").trim();
  }

  private String onlyAlphabetsWithSpaces(String str) {
    return str.replaceAll("[^a-z.A-Z ]", "").trim();
  }

  String onlyAlphanumeric(String str) {
    return str.replaceAll("[^a-z.A-Z0-9]", "").trim();
  }

  /*String onlyAlphanumericWithSpaces(String str) {
    return str.replaceAll("[^a-z.A-Z0-9 ]", "").trim();
  }*/

  public static String getHeader(){
    return "\"" + "CONSTITUENCY" + "\"," +
      "\"" + "WARD" + "\"," +
      "\"" + "PART" + "\"," +
      "\"" + "IMAGE" + "\"," +
      "\"" + "NAME" + "\"," +
      "\"" + "FATHER" + "\"," +
      "\"" + "MOTHER" + "\"," +
      "\"" + "OTHER" + "\"," +
      "\"" + "HUSBAND" + "\"," +
      "\"" + "HOUSE" + "\"," +
      "\"" + "AGE" + "\"," +
      "\"" + "GENDER" + "\"," +
      "\"" + "VOTER-ID" + "\"," +
      "\"" + "SERIAL" + "\"," +
      "\"" + "EXTENSION" + "\"," +
      "\"" + "MODIFIED" + "\",";
  }

  @Override
  public String toString() {
    return (constituencyInfo != null ? constituencyInfo : "") +
      "\"" + (imageName != null ? imageName.replace(Constants.IMAGE_FOLDER_PATH,"") : "") + "\"," +
      "\"" + (name != null ? onlyAlphabetsWithSpaces(name) : "") + "\"," +
      "\"" + (father != null ? onlyAlphabetsWithSpaces(father) : "") + "\"," +
      "\"" + (mother != null ? onlyAlphabetsWithSpaces(mother) : "") + "\"," +
      "\"" + (other != null ? onlyAlphabetsWithSpaces(other) : "") + "\"," +
      "\"" + (husband != null ? onlyAlphabetsWithSpaces(husband) : "") + "\"," +
      "\"" + (house != null ? house : "") + "\"," +
      "\"" + (age > 0 ? age : "") + "\"," +
      "\"" + getGender() + "\"," +
      "\"" + (voterID != null ? voterID : "") + "\"," +
      "\"" + (serialNumber > 0 ? serialNumber : "") + "\"," ;
  }

  public String getName() {
    return name;
  }

  public String getFather() {
    return father;
  }

  public String getMother() {
    return mother;
  }

  public String getOther() {
    return other;
  }

  public String getHusband() {
    return husband;
  }

  public String getHouse() {
    return house;
  }

  public int getAge() {
    return age;
  }

  public String getVoterID() {
    return voterID;
  }

  public int getSerialNumber() {
    return serialNumber;
  }

  public String getShortInfo() {
    return voterID + " " + name + " in " + imageName;
  }

  /*public String extractVoterID(String str) {
    str =  onlyAlphanumeric(str, true);
    String extractedVoterID = null;
    try {
      System.out.println("str: " + str);
      int index = str.length()-10;
      extractedVoterID = str.substring(index);
      setSerialNumber(str.substring(0,index)); // there are no extension
    } catch (Exception e){
      System.err.println("-------->> Warning: Voter ID error " + str + " " + e.getMessage());
    }
    return extractedVoterID;
  }*/

  /*public String extractVoterIDOLD(String str) {
    // in case still left any then it is modified #2
    // System.out.println(str);
    if(str.contains("#")) {
      //System.out.println("Modified voter ID");
      modified = true;
    }
    str =  onlyAlphanumeric(str);
    //System.out.println(str);
    String extractedVoterID = null;
    try {
      // #2 231 1 XHC2301653
      // 950] | 8
      // TZD1722890  953] | 8
      // 958 3 XHC2418994
      //System.out.println(str);
      String[] array = str.split(" ");
      // biggest one is voterID
      for(int i=array.length-1;i>=0;i--) {
        String temp = array[i];
        if(temp.isEmpty()) continue;
        //System.out.println(temp);
        if(temp.length()>=8) {
          // this is voter id
          extractedVoterID = temp;
          continue;
        }
        if(serialExtension==0) {
          setSerialExtension(temp);
          continue;
        }
        if(serialNumber==0) {
          setSerialNumber(temp);
          continue;
        }
      }
    } catch (Exception e){
      System.err.println("-------->> Warning: Voter ID error " + str + " " + e.getMessage());
    }
    return extractedVoterID;
  }*/
}
