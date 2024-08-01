package org.manacle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExcelGenerator {

  // name of generated csv
  final String CSV_LOCATION = "Voters_" + VoterDataExtraction.CONSTITUENCY_WARD + "_" ;

  public void write(List<Person> allPersons) throws IOException {
    FileWriter writer = null;
    try {
      String fileName = VoterDataExtraction.BASE
        + File.separator
        + CSV_LOCATION
        + (System.currentTimeMillis()%1000000) + ".csv" ;
      writer = new FileWriter(fileName);
      writer.write(Person.getHeader());
      writer.write("\n");
      for(Person person : allPersons) {
        writer.write(person.toString());
        writer.write("\n");
      }
    } catch (Exception e) {
      System.err.println("Error in creating CSV file");
    } finally {
      writer.close();
    }
  }

}
