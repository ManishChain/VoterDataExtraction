package org.manacle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExcelGenerator {

  private final String CONSTITUENCY_WARD;

  public ExcelGenerator(String cw) {
    CONSTITUENCY_WARD = cw;
  }

  public String write(List<Person> allPersons) throws IOException {
    FileWriter writer = null;
    try {
      String fileName = Constants.CSV_FOLDER_PATH
        + File.separator
        + "Voters_" + CONSTITUENCY_WARD + "_"
        + (System.currentTimeMillis()%1000000) + ".csv" ;
      writer = new FileWriter(fileName);
      writer.write(Person.getHeader());
      writer.write("\n");
      for(Person person : allPersons) {
        writer.write(person.toString());
        writer.write("\n");
      }
      return fileName;
    } catch (Exception e) {
      System.err.println("Error in creating CSV file");
    } finally {
      writer.close();
    }
    return null;
  }

}
