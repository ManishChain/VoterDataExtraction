package org.manacle;

import org.manacle.entity.Info;
import org.manacle.entity.Person;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExcelGenerator {

  Info constituencyInfo ;

  public ExcelGenerator(Info cw) {
    this.constituencyInfo = cw;
  }

  public String write(List<Person> allPersons) throws IOException {
    FileWriter writer = null;
    try {
      String fileName = Constants.CSV_FOLDER_PATH
        + File.separator
        + "Voters_" + constituencyInfo.getConstituency() + "_" + constituencyInfo.getWard() + "_" + constituencyInfo.getPart() + "_"
        + (System.currentTimeMillis()%1000000) + ".csv" ;
      writer = new FileWriter(fileName);
      writer.write(Person.getHeader());
      writer.write("\n");
      for(Person person : allPersons) {
        writer.write(person.toString());
        writer.write("\n");
        updateGenderStats(person.getGender());
        int serialNumber = person.getSerialNumber();
        if(serialNumber>0 && !(serialNumber>=constituencyInfo.getSerialStart() && serialNumber<=constituencyInfo.getSerialEnd())) {
          System.err.println("\nSerial number not in valid range " + serialNumber + " in " + person.getShortInfo());
        }
      }
      return fileName;
    } catch (Exception e) {
      System.err.println("Error in creating CSV file");
    } finally {
      assert writer != null;
      writer.close();
    }
    return null;
  }


  private void updateGenderStats(String gender) {
    if(gender==null || gender.isEmpty()) return;
    gender = gender.toLowerCase();
    if(gender.startsWith("ma")) constituencyInfo.addMale();
    else if(gender.startsWith("fe")) constituencyInfo.addFemale();
    else constituencyInfo.addOther();
  }

}
