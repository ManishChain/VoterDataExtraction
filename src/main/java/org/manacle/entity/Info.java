package org.manacle.entity;

import org.manacle.Constants;

import javax.swing.*;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Info {

  /*
CONSTITUENCY=KALKAJI
WARD=174
PART=1
SERIAL_START=1
SERIAL_END=1435
MALE=806
FEMALE=612
OTHER=0
TOTAL=1418
 */

  private String constituency;
  private int ward = 0;
  private int part = 0;
  private int serialStart = 0;
  private int serialEnd = 0;
  private int males = 0;
  private int females = 0;
  private int others = 0;
  private int total = 0;
  private int countMales = 0;
  private int countFemales = 0;
  private int countOthers = 0;
  private Map<Integer,Boolean> serialNumberStats;

  public Info(String paramsFilePath) {
    try {
      File paramsFile = new File(paramsFilePath);
      if(paramsFile.exists()) {
        Properties prop = new Properties();
        InputStream stream = Files.newInputStream(paramsFile.toPath());
        prop.load(stream);

        Object temp = prop.get("CONSTITUENCY"); //
        if(temp==null) {
          System.err.println("CONSTITUENCY not defined in properties file");
          temp = JOptionPane.showInputDialog("Please input CONSTITUENCY");
        }
        constituency = temp.toString();

        temp = prop.get("WARD"); //
        if(temp==null) {
          System.err.println("WARD not defined in properties file");
          temp = JOptionPane.showInputDialog("Please input WARD");
        }
        ward = Integer.parseInt(temp.toString());

        temp = prop.get("PART"); //
        if(temp==null) {
          System.err.println("PART not defined in properties file");
          temp = JOptionPane.showInputDialog("Please input PART");
        }
        part = Integer.parseInt(temp.toString());

        temp = prop.get("SERIAL_START"); //
        if(temp==null) {
          System.err.println("SERIAL_START not defined in properties file");
          temp = JOptionPane.showInputDialog("Please input SERIAL_START");
        }
        serialStart = Integer.parseInt(temp.toString());

        temp = prop.get("SERIAL_END"); //
        if(temp==null) {
          System.err.println("SERIAL_END not defined in properties file");
          temp = JOptionPane.showInputDialog("Please input SERIAL_END");
        }
        serialEnd = Integer.parseInt(temp.toString());

        generateHolderForSerialNumberStats();

        temp = prop.get("MALE"); //
        if(temp==null) {
          System.err.println("MALE not defined in properties file");
          temp = JOptionPane.showInputDialog("Please input MALE");
        }
        males = Integer.parseInt(temp.toString());

        temp = prop.get("FEMALE"); //
        if(temp==null) {
          System.err.println("FEMALE not defined in properties file");
          temp = JOptionPane.showInputDialog("Please input FEMALE");
        }
        females = Integer.parseInt(temp.toString());

        temp = prop.get("OTHER"); //
        if(temp==null) {
          System.err.println("OTHER not defined in properties file");
          temp = JOptionPane.showInputDialog("Please input OTHER");
        }
        others = Integer.parseInt(temp.toString());

        temp = prop.get("TOTAL"); //
        if(temp==null) {
          System.err.println("TOTAL not defined in properties file");
          temp = JOptionPane.showInputDialog("Please input TOTAL");
        }
        total = Integer.parseInt(temp.toString());

      } else {
        throw new Exception("Params file not found");
      }
    } catch (Exception e) {
      System.err.println("Error reading params file " +e.getMessage());
    }
  }

  private void generateHolderForSerialNumberStats() {
    serialNumberStats = new HashMap<Integer,Boolean>();
    for(int i=serialStart; i<=serialEnd; i++){
      serialNumberStats.put(i, false);
    }
  }
  public boolean updateSerialNumberStats(int serialNumber) {
    boolean b = serialNumberStats.get(serialNumber);
    if(b){
      return false;
    } else {
      serialNumberStats.put(serialNumber, true);
      return true;
    }
  }
  public Map<Integer, Boolean> getSerialNumberStats(){
    return  serialNumberStats;
  }

  @Override
  public String toString() {
    return
      "\"" + (constituency != null ? constituency : "") + "\"," +
      "\"" + (ward > 0 ? ward : "") + "\"," +
      "\"" + (part > 0 ? part : "") + "\"," ;
  }

  public String getConstituency() {
    return constituency;
  }

  public int getWard() {
    return ward;
  }

  public int getPart() {
    return part;
  }

  public int getSerialStart() {
    return serialStart;
  }

  public int getSerialEnd() {
    return serialEnd;
  }

  public int getMales() {
    return males;
  }

  public int getFemales() {
    return females;
  }

  public int getOthers() {
    return others;
  }

  public int getTotal() {
    return total;
  }

  public void addMale(){
    this.countMales++;
  }
  public void addFemale(){
    this.countFemales++;
  }
  public void addOther(){
    this.countOthers++;
  }

  public int getCountMales() {
    return countMales;
  }

  public int getCountFemales() {
    return countFemales;
  }

  public int getCountOthers() {
    return countOthers;
  }
}

