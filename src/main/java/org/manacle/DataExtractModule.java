package org.manacle;

import java.util.ArrayList;
import java.util.List;

public class DataExtractModule {

  public static final String fieldName = "NAME";
  public static final String fieldFather = "FATHER";
  public static final String fieldMother = "MOTHER";
  public static final String fieldOther = "OTHER";
  public static final String fieldHusband = "HUSBAND";
  public static final String fieldHouse = "HOUSE NUMBER";
  public static final String fieldAge = "AGE";
  public static final String fieldGender = "GENDER";
  public static final String fieldSeparator = "MALE";
  public static final String fieldExtra1 = "AVAILABLE";
  public static final String fieldExtra2 = "PHOTO";

  public List<Person> start(String data, String otherInfo) {
    //System.out.println(">> " + data);
    //remove waster characters
    String dataToProcess = removeInvalidWordsAndCharacters(data.toUpperCase());
    //System.out.println(">> " + dataToProcess);
    String[] persons = dataToProcess.split(fieldSeparator);
    //System.out.println("Got persons " + persons.length);
    //for(String temp: persons) System.out.println(" >> " + temp);
    //System.out.println("Got Names " + dataToProcess.split("NAME").length);
    String[] areaInfo = otherInfo.split("-");
    String constituency = areaInfo[0];
    int ward = 0 ; try { ward = Integer.parseInt(areaInfo[1]); } catch (Exception e){
      System.err.println("Error in extracting ward " + otherInfo);
    }
    List<Person> allPersons = new ArrayList<>();
    // boolean lastPersonVoterIDWasSet = true;
    //String latestVoterID = null;
    StringBuilder buff = new StringBuilder();
    for(String str : persons) {
      Person person = new Person(constituency, ward);
      int index = str.indexOf(fieldName);
      int nextIndex = -1;
      boolean father = false, mother = false, other = false, husband = false;
      //System.out.println("   >> index=" + index  + "  :" + str);
      if(index>=0) {
        buff.append(person.onlyAlphanumericWithSpaces(str.substring(0, index))).append(" ");
        /*latestVoterID = person.extractVoterID(str.substring(0, index));
        if(latestVoterID!=null) {
          if (lastPersonVoterIDWasSet) {
            person.setVoterID(latestVoterID);
          } else { // find last person and set voterID to him
            allPersons.get(allPersons.size()-1).setVoterID(latestVoterID);
          }
        } else {
          lastPersonVoterIDWasSet = false;
        }*/
        nextIndex = str.indexOf(fieldFather, index);
        if(nextIndex>0) {
          person.setName(str.substring(index, nextIndex)); father = true; index = nextIndex;
        } else {
          nextIndex = str.indexOf(fieldMother, index);
          if (nextIndex > 0) {
            person.setName(str.substring(index, nextIndex)); mother = true; index = nextIndex;
          } else {
            nextIndex = str.indexOf(fieldOther, index);
            if (nextIndex > 0) {
              person.setName(str.substring(index, nextIndex)); other = true; index = nextIndex;
            } else {
              nextIndex = str.indexOf(fieldHusband, index);
              if(nextIndex>0) {
                person.setName(str.substring(index, nextIndex)); husband = true; index = nextIndex;
              } else {
                System.err.println("Error in extracting name");
              }
            }
          }
        }
        nextIndex = str.indexOf(fieldHouse, index);
        if(nextIndex>0) {
          if (father) person.setFather(str.substring(index, nextIndex));
          if (mother) person.setMother(str.substring(index, nextIndex));
          if (other) person.setOther(str.substring(index, nextIndex));
          if (husband) person.setHusband(str.substring(index, nextIndex));
          index = nextIndex;
          nextIndex = str.indexOf(fieldAge, index);
          if(nextIndex>0) {
            person.setHouse(str.substring(index, nextIndex));
            index = nextIndex;
            nextIndex = str.indexOf(fieldGender, index);
            // System.err.println(" HERE " + str);
            if(nextIndex>0) {
              person.setAge(str.substring(index, nextIndex));
            }
            if (str.endsWith("FE")) {
              //System.err.println(" Female found " + str);
              person.setGender(Person.FEMALE);
            } else {
              person.setGender(Person.MALE);
            }
          } else {
            System.err.println("Error in extracting age");
          }
        } else {
          System.err.println("Error in extracting father or mother or other or husband name");
        }
        //System.out.println(person);
        allPersons.add(person);
      } else {
        System.out.println("Misc: " + str);
        buff.append(str.trim());
        //allPersons.get(allPersons.size()-1).setVoterID(str.trim());
        //lastPersonVoterIDWasSet = true;
      }
      //System.out.println("\n");
    }
    // System.out.println("VoterID data : " + buff);
    //System.out.println("--");
    //System.out.println(allPersons);
    mapPersonsWithVoterIds(allPersons, buff.toString().replaceAll("  "," "));
    //System.out.println("+++");
    //System.out.println(allPersons);
    return allPersons;
  }

  boolean isNextVoterID = false;

  private void mapPersonsWithVoterIds(List<Person> allPersons, String string) {
    //System.out.println("Total persons : " + allPersons.size());
    int index = 0; String temp = "";
    for(String str : string.split(" ")) {
      // System.out.println(str);
      if(index>=allPersons.size()) {
        System.err.println("Wrong index ");
        break;
      }
      if(!isNextVoterID) { // extension
        if(allPersons.get(index).setSerialNumber(str)) {
          isNextVoterID = true;
        };
      } else { // voterID
        if(str.length()!=10) {
          // save for next
          temp = str ;
          continue;
        }
        allPersons.get(index).setVoterID(temp + str);
        isNextVoterID = false;
        index++; // next person
      }
    }
  }

  private String removeInvalidWordsAndCharacters(String data) {
    // System.out.println(data);
    data = data
      .replaceAll(fieldExtra1, " ")
      .replaceAll(fieldExtra2, " ");
      //.replaceAll(":", "");
    return data.trim();
  }

}
