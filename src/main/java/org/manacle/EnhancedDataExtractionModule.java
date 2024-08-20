package org.manacle;

import org.manacle.entity.Info;
import org.manacle.entity.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnhancedDataExtractionModule {

  int totalPersons = 0 ;
  List<Person> persons;
  Info constituencyInfo;

  public List<Person> start(String data, Info constituencyInfo, int imageIndex) {
    this.constituencyInfo = constituencyInfo;
    String[] rawPersonsData = data.split(Constants.fieldExtra1);
    // System.out.println("Expected persons: " + rawPersonsData.length);
    persons = new ArrayList<>(rawPersonsData.length);
    for(String rawPerson: rawPersonsData) {
      if(rawPerson.isEmpty()) {
        // System.out.println("Blank line ");
        continue;
      }
      if(rawPerson.replaceAll(Constants.DELIMITER,"").trim().isEmpty()) {
        // System.out.println("Blank line with DELIMITER");
        continue;
      }
      // System.out.println(" " + totalPersons + ": " + rawPerson);
      String[] array = rawPerson.split(Constants.DELIMITER);
      for (String str : array) {
        try {
          if (!str.isEmpty()) process(totalPersons, str, imageIndex);
        } catch (Exception e){
          System.err.println("Error in person " + e.getMessage());
        }
      }
      totalPersons++ ;
    }
    return persons;
  }

  private void process(int personIndex, String str, int imageIndex) {
    // System.out.println("Index " + personIndex + ": " + str);
    Person person;
    if(persons.size()==personIndex) {
      persons.add(new Person(constituencyInfo, imageIndex));
    }
    person = persons.get(personIndex);
    if (str.contains(Constants.fieldName) && person.getName()==null) person.setName(str,0);
    else if (str.contains(Constants.fieldName1) && person.getName()==null) person.setName(str,1);
    else if (str.contains(Constants.fieldFather) && person.getFather()==null) person.setFather(str);
    else if (str.contains(Constants.fieldMother) && person.getMother()==null) person.setMother(str);
    else if (str.contains(Constants.fieldOther) && person.getOther()==null) person.setOther(str);
    else if (str.contains(Constants.fieldHusband) && person.getHusband()==null) person.setHusband(str);
    else if (str.contains(Constants.fieldHouse) && person.getHouse()==null) person.setHouse(str);
    else if (str.contains(Constants.fieldAge) && person.getAge()==0) person.setAge(str);
    else if (str.contains(Constants.fieldGender) && person.getGender() == null) person.setGender(str);
    else if (str.contains(Constants.fieldGender1) && person.getGender() == null) person.setGender(str);
    else if (str.contains(Constants.fieldExtra2)) {
      // System.out.println("Skipping " + str);
    }
    else if (person.getSerialNumber() == 0) {
      String[] arr = str.split(" ");
      int len = arr.length;
      if(len==1) {
        if(arr[0].length()==10) { // this could be voter ID instead of serial number
          person.setVoterID(arr[0]);
        } else if(arr[0].length()>=3 && containsAlphabets(arr[0].substring(0,3))) { // initial 3 chars are alphabets
          person.setVoterID(arr[0]);
        } else {
          person.setSerialNumber(arr[0]);
        }
      } else if (len==2) {
        if((arr[0].length()+arr[1].length())==10) { // this could be voter ID instead of serial number
          person.setVoterID(arr[0]+arr[1]);
        } else if(containsAlphabets(arr[0])) { // this could be voter ID instead of serial number
          person.setVoterID(arr[0]+arr[1]);
        } else {
          try {
            person.setSerialNumber(String.valueOf(Integer.parseInt(arr[0])));
            person.setVoterID(arr[1]);
          } catch (Exception e) {
            person.setVoterID(arr[0]+arr[1]);
          }
        }
      } else if (len>=3) {
        if(arr[2].length()==10) { // this could be voter ID instead of serial number
          person.setVoterID(arr[2]);
          person.setSerialNumber(arr[0]);
          System.err.println("Ignoring " + arr[1] + " there is additional box adjacent to serial number " + str + " in " + person.getShortInfo());
        } else if((arr[0].length()+arr[1].length())==10) { // this could be voter ID instead of serial number
          person.setVoterID(arr[0]+arr[1]);
          person.setSerialNumber(arr[2]);
        } else if(containsAlphabets(arr[0])) { // this could be voter ID instead of serial number
          person.setVoterID(arr[0]+arr[1]);
          person.setSerialNumber(arr[2]);
        } else {
          try {
            person.setSerialNumber(String.valueOf(Integer.parseInt(arr[0])));
            person.setVoterID(arr[1]+arr[2]);
          } catch (Exception e) {
            System.err.println("Parsing-error-01 [" + str + "] in " + person.getShortInfo() + "]  " + e.getMessage());
          }
        }
      } else {
        person.setSerialNumber(str);
      }
    } else if (person.getVoterID() == null) person.setVoterID(str);
    else {
      if(!str.contains(" ")) { // might be part of father or other name
        if(person.getFather()!=null) person.setFather(person.getFather()+" "+str);
        else if(person.getMother()!=null) person.setMother(person.getMother()+" "+str);
        else if(person.getHusband()!=null) person.setHusband(person.getHusband()+" "+str);
        else  System.err.println("Parsing-error-02 [" + str + "] in " + person.getShortInfo() + "]  ");
      } else {
        System.err.println("Parsing-error-03 [" + str + "] in " + person.getShortInfo() + "]  ");
      }
    }
  }

  Pattern pattern = Pattern.compile("[a-zA-Z]+");

  private boolean containsAlphabets(String str) {
    Matcher matcher = pattern.matcher(str);
    return matcher.matches();
  }
}

/*

 0: 13 XHC0009530		NAME + SUNITA MALIK		HUSBANDS NAME: VIJAY SINGH MALIK	HOUSE NUMBER : 108 PHOTO	AGE : 61 GENDER : FEMALE
 1: 		16 XHC1744415		NAME : ARJUN AGGARWAL		FATHERS NAME: MUKESH GUPTA	HOUSE NUMBER :- 11B PHOTO	AGE : 36 GENDER : MALE
 2: 		99 FSH0818120		NAME > RAM BAHADUR YADAV		FATHERS NAME: AMRITLAL YADAV	HOUSE NUMBER : 29 PHOTO	AGE : 47 GENDER : MALE
 3: 		22 XHC1637775		NAME : NARAYAN BAHADUR		FATHERS NAME: SHER BAHADUR	HOUSE NUMBER : 33-34 PHOTO	AGE : 51 GENDER : MALE
>>>  Persons added: 4
 4: 25		NAME : SUNIL KUMAR		FATHERS NAME: JOGINDER SINGH	HOUSE NUMBER : 40		AGE : 64 GENDER : MALE		XHC1087949		PHOTO
 5: 		28		NAME : NIDH| NARANG		HUSBANDS NAME: TARUN NARANG	HOUSE NUMBER : 51		AGE : 41 GENDER : FEMALE		XHC0000018		PHOTO
>>>  Persons added: 6
 6: 2 XHC1972552	NAME : MOHIT	FATHERS NAME: HIRA LAL	HOUSE NUMBER : 1 PHOTO	AGE : 33 GENDER : MALE
 7: 	5 XHC 1787100		NAME : DHEERAJ KUMAR UPADHYAY	FATHERS NAME: VIJAY KUMAR UPADHYAY	HOUSE NUMBER : 4/5		AGE : 28 GENDER : MALE		PHOTO
 8: 		8 XHC 1662773	NAME : ROOPA KHATRI	FATHERS NAME: RAMESH KUMAR KHATRI	HOUSE NUMBER : 4/5039 PHOTO	AGE : 29 GENDER : FEMALE
 9: 		11 XHC 1820562	NAME : ANISA GOWHAR	HUSBANDS NAME: SYED ISHTIYAQ AHMED	HOUSE NUMBER : 5/15 PHOTO		AGE ' 37 GENDER : FEMALE
>>>  Persons added: 10
 10: 14		NAME : NEHA MALIK	HUSBANDS NAME: SAMEER MALIK		XHC1460079		HOUSE NUMBER : 10-B PHOTO	AGE : 39 GENDER : FEMALE
 11: 	V7 XHC 1902229	NAME : AISHA KHATOON	FATHERS NAME: MOHAMMAD JAMEEL	ANSARI PHOTO	HOUSE NUMBER : 14	AGE : 46 GENDER : FEMALE
 12: 	20 XHC 1548304	NAME : GANGA DEVI	HUSBANDS NAME: NANDAN SINGH BISHT	HOUSE NUMBER : 29 PHOTO	AGE : 45 GENDER : FEMALE
 13: 	23 UBOQ1553593	NAME : DINESH KUMAR CHAURASIA	MOTHERS NAME: CHINTA DEVI CHAURASIA	HOUSE NUMBER : 38 PHOTO		AGE : 42 GENDER : MALE
>>>  Persons added: 14
 14: 15 XHC1447365		NAME : SHUBHRA GUPTA		FATHERS NAME: SUKIRTI CHANDER GUPTA	HOUSE NUMBER : 10-B PHOTO	AGE : 32 GENDER : FEMALE
 15: 		18 XHC 1892438		NAME : PARUL RASTOGI		HUSBANDS NAME: MUKUL RASTOGI	HOUSE NUMBER : 22-SF PHOTO	AGE : 38 GENDER : FEMALE
 16: 		21 XHC 1637750		NAME : DBHARAM BAHADUR THAPA		FATHERS NAME: KHARAK SINGH THAPA	HOUSE NUMBER : 33 PHOTO	AGE : 46 GENDER : MALE
 17: 		24 XHC2349355	NAME : GUDDU	FATHERS NAME: PRASAD BHAGWATI	HOUSE NUMBER : 38 PHOTO		AGE : 30 GENDER : MALE
>>>  Persons added: 18
 18: 27		NAME : K-KUMARI	HUSBANDS NAME: P-KUMAR	HOUSE NUMBER : 49		AGE : 36 GANDER : FEMALE		XHC1720770		PHOTO
 19: 		30		NAME : POONAM BENGANI	HUSBANDS NAME: AMIT BENGANI	HOUSE NUMBER : 71		AGE : 45 GENDER : FEMALE		XHC1087907		PHOTO
>>>  Persons added: 20
 20: 29 XHC1916187		NAME : ROHIT SINGH RAWAT		FATHERS NAME: CHANDAN SINGH RAWAT		HOUSE NUMBER : 59 PHOTO	AGE : 28 GENDER : MALE
>>>  Persons added: 21
 21: 1 XHC1903689		NAME : RANDEEP SINGH		FATHERS NAME: GURCHARAN SINGH	HOUSE NUMBER : A-1 PHOTO	AGE : 28 GENDER : MALE
 22: 		4 XHC 1645431		NAME : PRABHA DEVI UPADHYA		HUSBANDS NAME: VJAY KUMAR UPADHYA	HOUSE NUMBER : 4/5 PHOTO	AGE : 47 GENDER : FEMALE
 23: 		7 XHC1650745		NAME : REETA KHATRI		FATHERS NAME: RANESH KHATRI	HOUSE NUMBER : 4/5039 PHOTO	AGE : 32 GENDER : FEMALE
 24: 		10 XHC2314938		NAME : ANUJ KHATRI		FATHERS NAME: RAMESH KUMAR KHATRI	HOUSE NUMBER : 4/5039 PHOTO	AGE : 21 GENDER : MALE
>>>  Persons added: 25
 25: 3 XHC1645423		NAME : VIJAY KUMAR UPADHYA		FATHERS NAME: SHIV BACHAN UPADHYA	HOUSE NUMBER : 4/5 PHOTO	AGE : 57 GENDER : MALE
 26: 		6 XHC 1869336		NAME : CHUNNU		FATHERS NAME: SIYA RAM SHAH	HOUSE NUMBER : 4/50/39 PHOTO	AGE : 48 GENDER : MALE
 27: 		9 XHC2048098		NAME : RADHA KHATRI		FATHERS NAME: RAMESH KHATRI	HOUSE NUMBER : 4/5039 PHOTO	AGE : 27 GENDER : FEMALE
 28: 		12 XHC 1087998		NAME : RANJENDER NATH KATYAL	FATHERS NAME: JAGAN NATH		HOUSE NUMBER : 7 A MKT PHOTO	AGE : 75 GENDER : MALE
>>>  Persons added: 29
 */
