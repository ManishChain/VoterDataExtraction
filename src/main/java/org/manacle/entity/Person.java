package org.manacle.entity;

import org.manacle.Constants;

public class Person {

  private final Info constituencyInfo;

  private final int imageIndex;
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
  private int expectedSerialNumber;

  private static int currentIndex = 0 ;

  public Person(Info constituencyInfo, int imageIndex, int pageNumber, int columnNumber) {
    this.constituencyInfo = constituencyInfo;
    this.imageIndex = imageIndex;
    if(pageNumber>0) {
      // set extension which can be used as serial number
      expectedSerialNumber = ((pageNumber-1)*30) + (columnNumber+1) + (currentIndex*3) ;
      // System.out.println("expectedSerialNumber = " + expectedSerialNumber + "  >>  " + pageNumber + "  " + columnNumber + "  " + currentIndex);
      currentIndex++;
      if(currentIndex==10) currentIndex=0; // reset
    }
  }

  public void setName(String name, int signal) {
    int index = name.indexOf(":");
    if(index>=0) {
      name = name.substring(index+1).trim();
    } else {
      index = name.indexOf("?");
      if(index>=0){
        name = name.substring(index+1).trim();
      }
    }
    if(signal==1) {
      this.name = name.replaceAll(Constants.fieldName1, "").trim();
    } else {
      this.name = name.replaceAll(Constants.fieldName, "").trim();
    }
  }

  public void setFather(String father) {
    try { father = father.substring(father.indexOf(":")+1).trim(); } catch (Exception e) {
      System.err.println("No delimiter : found in father [" + father + "] in " + getShortInfo());
    }
    this.father =  father.replaceAll(Constants.fieldFather,  "").trim();
  }

  public void setMother(String mother) {
    try { mother = mother.substring(mother.indexOf(":")+1).trim(); } catch (Exception e) {
      System.err.println("No delimiter : found in mother [" + mother + "] in " + getShortInfo());
    }
    this.mother = mother.replaceAll(Constants.fieldMother,  "").trim();
  }

  public void setOther(String other) {
    try { other = other.substring(other.indexOf(":")+1).trim(); } catch (Exception e) {
      System.err.println("No delimiter : found in other [" + other + "] in " + getShortInfo());
    }
    this.other = other.replaceAll(Constants.fieldOther,  "").trim();;
  }

  public void setHusband(String husband) {
    try { husband = husband.substring(husband.indexOf(":")+1).trim(); } catch (Exception e) {
      System.err.println("No delimiter : found in husband [" + husband + "] in " + getShortInfo());
    }
    this.husband = husband.replaceAll(Constants.fieldHusband,  "").trim();;
  }

  public void setHouse(String house) {
    try {
      house = house.substring(house.indexOf(":")+1);
      house = house.replace(Constants.fieldExtra2, "").trim();
    } catch (Exception e) {
      System.err.println("No delimiter : found in house [" + house + "] in  " + getShortInfo());
    }
    this.house = house.replaceAll(Constants.fieldHouse,  "").trim();;
  }

  public void setAge(String str) {
    // System.out.println(str);
    try {
      this.age = onlyDigits(str);
    } catch (Exception e) {
      this.age = 0;
      System.err.println("Error in age [" + str + "] in " + getShortInfo() + " " + e.getMessage());
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
      this.voterID = null ;
      System.err.println(" ---> Ignoring VoterID [" + voterID + "] in " + getShortInfo() + " ");
    }
  }

  public void setSerialNumber(String serialNumber) {
    try {
      if(serialNumber==null || serialNumber.trim().isEmpty()) return;
      this.serialNumber = onlyDigits(serialNumber);
      if(!constituencyInfo.updateSerialNumberStats(this.serialNumber)){
        System.err.println("Duplicate serial number found [" + this.serialNumber + "] in " + getShortInfo());
      }
    } catch (Exception e) {
      this.serialNumber = 0;
      System.err.println("----> Ignoring serial number [" + serialNumber + "] in " + getShortInfo() + " " + e.getLocalizedMessage());
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
      "\"" + (imageIndex>0 ? imageIndex : "") + "\"," +
      "\"" + (name != null ? onlyAlphabetsWithSpaces(name) : "") + "\"," +
      "\"" + (father != null ? onlyAlphabetsWithSpaces(father) : "") + "\"," +
      "\"" + (mother != null ? onlyAlphabetsWithSpaces(mother) : "") + "\"," +
      "\"" + (other != null ? onlyAlphabetsWithSpaces(other) : "") + "\"," +
      "\"" + (husband != null ? onlyAlphabetsWithSpaces(husband) : "") + "\"," +
      "\"" + (house != null ? house : "") + "\"," +
      "\"" + (age > 0 ? age : "") + "\"," +
      "\"" + getGender() + "\"," +
      "\"" + (voterID != null ? voterID : "") + "\"," +
      "\"" + (serialNumber > 0 ? serialNumber : "") + "\"," +
      "\"" + expectedSerialNumber + "\"," ;
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
    return " VoterID=" + voterID + " Name=" + name + " \t\t N=" + imageIndex;
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
