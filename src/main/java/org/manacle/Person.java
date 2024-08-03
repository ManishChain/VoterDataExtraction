package org.manacle;

public class Person {

  private final String imageName;
  private String name;
  private String father;
  private String mother;
  private String other;
  private String husband;
  private String house;
  private int age;
  private int gender;
  private String genderLabel;
  private String voterID;
  private int serialNumber;
  private int serialExtension = 0;
  private final String constituency;
  private int ward = 0;
  private final boolean modified = false;

  public Person(String constituency, int ward, String imageName) {
    this.constituency = constituency;
    this.ward = ward;
    this.imageName = imageName;
  }

  public void setName(String name) {
    try { name = name.substring(name.indexOf(":")+1).trim(); } catch (Exception e) {
      System.err.println("No : in name");
    }
    this.name = name.replaceAll(Constants.fieldName,  "").trim();
  }

  public void setFather(String father) {
    try { father = father.substring(father.indexOf(":")+1).trim(); } catch (Exception e) {
      System.err.println("No : in father");
    }
    this.father =  father.replaceAll(Constants.fieldFather,  "").trim();
  }

  public void setMother(String mother) {
    try { mother = mother.substring(mother.indexOf(":")+1).trim(); } catch (Exception e) {
      System.err.println("No : in mother");
    }
    this.mother = mother.replaceAll(Constants.fieldMother,  "").trim();
  }

  public void setOther(String other) {
    try { other = other.substring(other.indexOf(":")+1).trim(); } catch (Exception e) {
      System.err.println("No : in other");
    }
    this.other = other.replaceAll(Constants.fieldOther,  "").trim();;
  }

  public void setHusband(String husband) {
    try { husband = husband.substring(husband.indexOf(":")+1).trim(); } catch (Exception e) {
      System.err.println("No : in husband");
    }
    this.husband = husband.replaceAll(Constants.fieldHusband,  "").trim();;
  }

  public void setHouse(String house) {
    try {
      house = house.substring(house.indexOf(":")+1);
      house = house.replace(Constants.fieldExtra2, "").trim();
    } catch (Exception e) {
      System.err.println("No : in house");
    }
    this.house = house.replaceAll(Constants.fieldHouse,  "").trim();;
  }

  public void setAge(String str) {
    // System.out.println(str);
    try {
      this.age = onlyDigits(str);
    } catch (Exception e) {
      System.err.println("Error in age " + age + " " + e.getMessage());
    }
    // might be possible that GENDER info is also inside this
    if (str.contains(Constants.fieldGender) && getGenderLabel() == null) setGenderLabel(str);
    if (str.contains(Constants.fieldGender1) && getGenderLabel() == null) setGenderLabel(str);
  }

  public void setGenderLabel(String str) {
    int index = str.indexOf(Constants.fieldGender);
    if(index<0) index = str.indexOf(Constants.fieldGender1);
    if(index>0) {
      this.genderLabel = onlyAlphabets(str.substring(index+Constants.fieldGender.length()));
    }
  }

  public String getGenderLabel() {
    return genderLabel;
  }

  public void setGender(int gender) {
    this.gender = gender;
  }

  public String getGender() {
    return switch (gender) {
      case Constants.MALE -> "MALE";
      case Constants.FEMALE -> "FEMALE";
      default -> "-";
    };
  }

  public void setVoterID(String voterID) {
    this.voterID = onlyAlphanumeric(voterID);
    if(this.voterID.length()!=10) {
      System.err.println("Invalid VoterID : " + this.voterID);
    }
  }

  public boolean setSerialNumber(String serialNumber) {
    try {
      this.serialNumber = onlyDigits(serialNumber);
      return true;
    } catch (Exception e) {
      System.err.println("Error in serial number " + serialNumber + "  " + e.getMessage());
    }
    return false;
  }

  public void setSerialExtension(String serialExtension) {
    try {
    int value = onlyDigits(serialExtension);
    if(value>100){
      this.serialNumber = value; // most probably it is serial number
    } else {
      this.serialExtension = value;
    }
    } catch (Exception e) {
      System.err.println("Error in serial extension " + serialExtension + "  " + e.getMessage());
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

  String onlyAlphanumericWithSpaces(String str) {
    return str.replaceAll("[^a-z.A-Z0-9 ]", "").trim();
  }

  public static String getHeader(){
    return "\"" + "CONSTITUENCY" + "\"," +
      "\"" + "WARD" + "\"," +
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
    return "\"" + (constituency != null ? constituency : "") + "\"," +
      "\"" + (ward > 0 ? ward : "") + "\"," +
      "\"" + (imageName != null ? imageName.replace(Constants.IMAGE_FOLDER_PATH,"") : "") + "\"," +
      "\"" + (name != null ? onlyAlphabetsWithSpaces(name) : "") + "\"," +
      "\"" + (father != null ? onlyAlphabetsWithSpaces(father) : "") + "\"," +
      "\"" + (mother != null ? onlyAlphabetsWithSpaces(mother) : "") + "\"," +
      "\"" + (other != null ? onlyAlphabetsWithSpaces(other) : "") + "\"," +
      "\"" + (husband != null ? onlyAlphabetsWithSpaces(husband) : "") + "\"," +
      "\"" + (house != null ? house : "") + "\"," +
      "\"" + (age > 0 ? age : "") + "\"," +
      "\"" + (Constants.USE_ENHANCED_LOGIC?getGenderLabel():getGender()) + "\"," +
      "\"" + (voterID != null ? voterID : "") + "\"," +
      "\"" + (serialNumber > 0 ? serialNumber : "") + "\"," +
      "\"" + (serialExtension > 0 ? serialExtension : "") + "\"," +
      "\"" + (modified ? "YES" : "") + "\",";
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
