package org.manacle;

import java.util.ArrayList;
import java.util.List;

public class DataExtractModule {

  public List<Person> start(String data, String otherInfo) {
    //System.out.println(">> " + data);
    //remove waster characters
    String dataToProcess = removeInvalidWordsAndCharacters(data.toUpperCase());
    //System.out.println(">> " + dataToProcess);
    String[] persons = dataToProcess.split(Constants.fieldSeparator);
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
      Person person = new Person(constituency, ward, "");
      int index = str.indexOf(Constants.fieldName);
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
        nextIndex = str.indexOf(Constants.fieldFather, index);
        if(nextIndex>0) {
          person.setName(str.substring(index, nextIndex)); father = true; index = nextIndex;
        } else {
          nextIndex = str.indexOf(Constants.fieldMother, index);
          if (nextIndex > 0) {
            person.setName(str.substring(index, nextIndex)); mother = true; index = nextIndex;
          } else {
            nextIndex = str.indexOf(Constants.fieldOther, index);
            if (nextIndex > 0) {
              person.setName(str.substring(index, nextIndex)); other = true; index = nextIndex;
            } else {
              nextIndex = str.indexOf(Constants.fieldHusband, index);
              if(nextIndex>0) {
                person.setName(str.substring(index, nextIndex)); husband = true; index = nextIndex;
              } else {
                System.err.println("Error in extracting name");
              }
            }
          }
        }
        nextIndex = str.indexOf(Constants.fieldHouse, index);
        if(nextIndex>0) {
          if (father) person.setFather(str.substring(index, nextIndex));
          if (mother) person.setMother(str.substring(index, nextIndex));
          if (other) person.setOther(str.substring(index, nextIndex));
          if (husband) person.setHusband(str.substring(index, nextIndex));
          index = nextIndex;
          nextIndex = str.indexOf(Constants.fieldAge, index);
          if(nextIndex>0) {
            person.setHouse(str.substring(index, nextIndex));
            index = nextIndex;
            nextIndex = str.indexOf(Constants.fieldGender, index);
            // System.err.println(" HERE " + str);
            if(nextIndex>0) {
              person.setAge(str.substring(index, nextIndex));
            }
            if (str.endsWith("FE")) {
              //System.err.println(" Female found " + str);
              person.setGender(Constants.FEMALE);
            } else {
              person.setGender(Constants.MALE);
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
        //System.out.println("Misc: " + str);
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
      .replaceAll(Constants.fieldExtra1, " ")
      .replaceAll(Constants.fieldExtra2, " ");
      //.replaceAll(":", "");
    return data.trim();
  }

}
