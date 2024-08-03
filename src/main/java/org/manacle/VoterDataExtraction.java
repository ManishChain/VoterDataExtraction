package org.manacle;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class VoterDataExtraction {

  public static void main(String[] args) {
    File dataFolder = new File(Constants.DATA_FOLDER_PATH);
    if(!dataFolder.exists()) {
      Communicator.showError("Data file not found .... " + dataFolder + " in folder: " + new File(".").getAbsolutePath() );
      System.exit(0);
    }
    File imageFolder = new File(Constants.IMAGE_FOLDER_PATH);
    if(!imageFolder.exists()) {
      if(!imageFolder.mkdirs()){
        Communicator.showError("Image folder not found or created .... " + imageFolder.getAbsolutePath() );
        System.exit(0);
      }
    }
    File outputFolder = new File(Constants.OUTPUT_FOLDER_PATH);
    if(!outputFolder.exists()) {
      if(!outputFolder.mkdirs()){
        Communicator.showError("Output folder not found or created .... " + outputFolder.getAbsolutePath() );
        System.exit(0);
      }
    }
    File csvFolder = new File(Constants.CSV_FOLDER_PATH);
    if(!csvFolder.exists()) {
      if(!csvFolder.mkdirs()){
        Communicator.showError("CSV folder not found or created .... " + outputFolder.getAbsolutePath() );
        System.exit(0);
      }
    }
    System.out.println("---------------");
    System.out.println("Program starting ... ");
    VoterDataExtraction voterDataExtraction = new VoterDataExtraction();
    List<String> allImagesInfo = new VoterDataExtraction().moveAllImagesFromDataFolderIntoImageFolder(dataFolder);

    if(Constants.GENERATE) {
      for (String imageInfo : allImagesInfo) {
        voterDataExtraction.generateTextFileByScanningImage(imageInfo);
        System.out.println("TEXT GENERATION DONE: " + imageInfo);
      }
    } else {
      System.err.println("------->> TEXT GENERATION SKIPPED AS ALREADY DONE\n\n");
    }

    // process extracted data
    List<Person> allPersons = new ArrayList<>();
    for(String imageInfo :  allImagesInfo) {
      //System.out.println(imageInfo);
      File datafile = new File(getOutputTextFileName(imageInfo,true));
      if (datafile.exists()) {
        String data = voterDataExtraction.getData(datafile);
        // System.out.println("---> Processing : " + imageInfo.getPath());
        List<Person> persons;
        if(Constants.USE_ENHANCED_LOGIC) {
          //System.out.println(".........");
          persons = new EnhancedDataExtractionModule().start(data.toUpperCase(), Constants.CONSTITUENCY_WARD, imageInfo);
        } else {
          persons = new DataExtractModule().start(data, Constants.CONSTITUENCY_WARD);
        }
        if(persons!=null && !persons.isEmpty()) allPersons.addAll(persons);
      } else {
        System.err.println("File not exists " + datafile.getAbsolutePath());
      }
    }
    // for all persons create excel sheet
    // System.out.println("---------------");
    System.out.println("\n\nTotal Voters Extracted : " + allPersons.size());
    // System.out.println(allPersons);
    try {
      new ExcelGenerator().write(allPersons);
      System.out.println("\n\nProgram terminated successfully. Please check generated EXCEL sheet");
    } catch (Exception e) {
      System.err.println("Error " + e.getMessage());
    }
  }

  private String getData(File datafile) {
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(datafile)));
      StringBuilder data = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        data.append(line);
        data.append(Constants.DELIMITER);
      }
      return data.toString();
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
    return null;
  }

  private List<String> moveAllImagesFromDataFolderIntoImageFolder(File folder){
    List<String> allImages = new ArrayList<>();
    for(File file : Objects.requireNonNull(folder.listFiles())) {
      if(file.isDirectory()) { // get images recursively
        List<String> images = moveAllImagesFromDataFolderIntoImageFolder(file);
        allImages.addAll(images);
      } else {
        String imageInfo = null;
        try {
          imageInfo = validImage(file);
        } catch (Exception e) {
          System.err.println("Error in reading Image " + file.getAbsolutePath() + "  >> " + e.getMessage());
        }
        if(imageInfo!=null){
          allImages.add(imageInfo);
        }
      }
    }
    return allImages;
  }

  static int count = 1 ;

  private String validImage(File file) throws IOException {
    String filePath = file.getAbsolutePath();
    String[] array = filePath.split(String.valueOf(File.separator));
    int length = array.length;
    String filename = array[length-1];
    String folderName = array[length-2];
    // find the last occurrence of '.' in the filename
    int dotIndex = filename.lastIndexOf('.');
    String extension = (dotIndex > 0) ? filename.substring(dotIndex + 1) : "";
    filename = filename.substring(0,dotIndex);
    if(extension.equalsIgnoreCase("png")) {
      // copy image into image folder
      File original = new File(filePath);
      File copied = new File(Constants.IMAGE_FOLDER_PATH + File.separator + (count++) + ".png");
      FileUtils.copyFile(original, copied);
      return copied.getAbsolutePath();
    }
    return null;
  }

  private void generateTextFileByScanningImage(String imageInfo){
    try {
      String[] command = { "tesseract", imageInfo, getOutputTextFileName(imageInfo, false) };
      Process process = Runtime.getRuntime().exec(command);
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      StringBuilder data = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        data.append(line);
      }
      reader.close();
      // System.out.println("Executed command: " + Arrays.toString(command));
      // System.out.println("Result: " + data.toString());
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }

  private static String getOutputTextFileName(String imageInfo, boolean forReading) {
    String name = Constants.OUTPUT_FOLDER_PATH + imageInfo.substring(imageInfo.lastIndexOf(File.separator)).replace(".png","");
    if(forReading) {
      return name + ".txt" ;
    } else {
      return name;
    }
  }


}

