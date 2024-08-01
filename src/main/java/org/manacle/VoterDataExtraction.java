package org.manacle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class VoterDataExtraction {

  public static final String CONSTITUENCY_WARD = "KALKAJI-51";

  public static final String BASE = "/Users/manish/Desktop/election/NIKHIL" ;
  public static final String dataFolderPath = BASE + File.separator + CONSTITUENCY_WARD;
  public static final String outputFolderPath = BASE + File.separator + "output" ;

  private static final boolean GENERATE = true;

  public static void main(String[] args) {
    File dataFolder = new File(dataFolderPath);
    if(!dataFolder.exists()) {
      Communicator.showError("Data file not found .... " + dataFolder + " in folder: " + new File(".").getAbsolutePath() );
      System.exit(0);
    }
    File outputFolder = new File(outputFolderPath);
    if(!outputFolder.exists()) {
      if(!outputFolder.mkdirs()){
        Communicator.showError("Output folder not found or created .... " + outputFolder.getAbsolutePath() );
        System.exit(0);
      }
    }
    System.out.println("---------------");
    System.out.println("Program starting ... ");
    VoterDataExtraction voterDataExtraction = new VoterDataExtraction();
    // Communicator.showNotice("Starting processing with folder ...\n" + folder.getAbsolutePath());
    List<ImageInfo> allImagesInfo = new VoterDataExtraction().getImagesInDataFolder(dataFolder);
    if(GENERATE) {
      for (ImageInfo imageInfo : allImagesInfo) {
        voterDataExtraction.generateTextFileByScanningImage(imageInfo);
      }
    } else {
      System.err.println("------->> TEXT GENERATION SKIPPED AS ALREADY DONE");
    }
    // process extracted data
    List<Person> allPersons = new ArrayList<>();
    for(ImageInfo imageInfo :  allImagesInfo) {
      //System.out.println(imageInfo);
      File datafile = new File(imageInfo.getTextFileName(true));
      if (datafile.exists()) {
        String data = voterDataExtraction.getData(datafile);
        System.out.println("---> Processing : " + imageInfo.getPath());
        List<Person> persons = new DataExtractModule().start(data, imageInfo.getFolder());
        if(!persons.isEmpty()) allPersons.addAll(persons);
      } else {
        System.err.println("File not exists " + datafile.getAbsolutePath());
      }
    }
    // for all persons create excel sheet
    System.out.println("---------------");
    try {
      new ExcelGenerator().write(allPersons);
      System.out.println("Program terminated successfully. Please check generated EXCEL sheet");
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
      }
      return data.toString();
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
    return null;
  }

  private List<ImageInfo> getImagesInDataFolder(File folder){
    List<ImageInfo> allImages = new ArrayList<>();
    for(File file : Objects.requireNonNull(folder.listFiles())) {
      if(file.isDirectory()) { // get images recursively
        List<ImageInfo> images = getImagesInDataFolder(file);
        allImages.addAll(images);
      } else {
        ImageInfo imageInfo = validImage(file);
        if(imageInfo!=null){
          allImages.add(imageInfo);
        }
      }
    }
    return allImages;
  }

  private ImageInfo validImage(File file) {
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
      return new ImageInfo(folderName, filename, extension, filePath);
    }
    return null;
  }

  private void generateTextFileByScanningImage(ImageInfo imageInfo){
    try {
      String command = "tesseract " + imageInfo.getPath() +  " " + imageInfo.getTextFileName(false) + " ";
      Process process = Runtime.getRuntime().exec(command);
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      StringBuilder data = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        data.append(line);
      }
      reader.close();
      // System.out.println("Executed command: " + command);
      // System.out.println("Result: " + data.toString());
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }
}

class ImageInfo {
  private final String folder;
  private final String name;
  private final String extension;
  private final String path;
  private String textFileName;
  ImageInfo(String folder, String name, String extension, String path){
    this.folder = folder;
    this.name = name;
    this.path = path;
    this.extension =  extension;
    setTextFileName();
  }
  public String getFolder() {
    return folder;
  }
  public String getName() {
    return name;
  }
  public String getPath() {
    return path;
  }
  public String getExtension() {
    return extension;
  }
  public String getTextFileName(boolean forReading) {
    return textFileName + (forReading?".txt":"") ;
  }
  public void setTextFileName() {
    this.textFileName = VoterDataExtraction.outputFolderPath
              + File.separator
              + folder.replaceAll("-","_") + "_" + name;
  }

  @Override
  public String toString() {
    return "ImageInfo{" +
      "folder='" + folder + '\'' +
      ", name='" + name + '\'' +
      ", extension='" + extension + '\'' +
      ", path='" + path + '\'' +
      ", textFileName='" + textFileName + '\'' +
      '}';
  }
}
