package org.manacle;

import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.List;

public class VoterDataExtraction {

  private final static String GENERATED = "generated" ;

  private static Frame getFrame() {
    JFrame frame = new JFrame("Voter Data Extraction Tool Ver 1.0");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(560, 200);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);

    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout());

    JButton button = new JButton("Close");
    final JLabel label = new JLabel("CLOSE");
    panel.add(button);
    panel.add(label);

    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        frame.dispose();
      }
    });

    frame.getContentPane().add(panel, BorderLayout.CENTER);
    return frame;
  }

  public static void main(String[] args) {
    String os = System.getProperty("os.name") ;
    // System.out.println(os);
    if(!os.toLowerCase().contains("mac")) {
      Communicator.showError("This program can run only on MAC");
      System.exit(0);
    }
    Communicator.showNotice("Starting program...\n\nPlease select folder containing images");

    try {

      File dataFolder = null;

      JFileChooser fc = new JFileChooser("", FileSystemView.getFileSystemView());
      fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      Frame frame = getFrame();
      int option = fc.showOpenDialog(frame);
      if (option == JFileChooser.APPROVE_OPTION) {
        dataFolder = fc.getSelectedFile();
      } else {
        Communicator.showError("No folder selected. Please try again");
        System.exit(0);
      }

      if (!dataFolder.exists() || Objects.requireNonNull(dataFolder.listFiles()).length == 0) {
        Communicator.showError("Data Folder not found or empty .... " + dataFolder.getAbsolutePath());
        System.exit(0);
      }

      Constants.IMAGE_FOLDER_PATH = dataFolder + File.separator + GENERATED + File.separator + "images";
      Constants.OUTPUT_FOLDER_PATH = dataFolder + File.separator + GENERATED + File.separator + "output";
      Constants.CSV_FOLDER_PATH = dataFolder + File.separator + GENERATED + File.separator + "csv";

      String CONSTITUENCY_WARD = getConstituencyAndWard(dataFolder.getAbsolutePath());

      File imageFolder = new File(Constants.IMAGE_FOLDER_PATH);
      if (!imageFolder.exists()) {
        if (!imageFolder.mkdirs()) {
          Communicator.showError("Image folder not found or created .... " + imageFolder.getAbsolutePath());
          System.exit(0);
        }
      }
      File outputFolder = new File(Constants.OUTPUT_FOLDER_PATH);
      if (!outputFolder.exists()) {
        if (!outputFolder.mkdirs()) {
          Communicator.showError("Output folder not found or created .... " + outputFolder.getAbsolutePath());
          System.exit(0);
        }
      }
      File csvFolder = new File(Constants.CSV_FOLDER_PATH);
      if (!csvFolder.exists()) {
        if (!csvFolder.mkdirs()) {
          Communicator.showError("CSV folder not found or created .... " + outputFolder.getAbsolutePath());
          System.exit(0);
        }
      }

      System.out.println("---------------");
      System.out.println("Program starting ... ");
      System.out.println("DATA FOLDER : " + dataFolder.getAbsolutePath());
      System.out.println("IMAGE FILES FOLDER : " + imageFolder.getAbsolutePath());
      System.out.println("TEXT FILES FOLDER : " + outputFolder.getAbsolutePath());
      System.out.println("CSV FILES FOLDER : " + csvFolder.getAbsolutePath());

      VoterDataExtraction voterDataExtraction = new VoterDataExtraction();
      List<String> allImagesInfo = new VoterDataExtraction().moveAllImagesFromDataFolderIntoImageFolder(dataFolder);

      if (Communicator.ask("Generate", "Do you want to generate TEXT files from images ?")) {
        for (String imageInfo : allImagesInfo) {
          voterDataExtraction.generateTextFileByScanningImage(imageInfo);
          System.out.println("TEXT GENERATION DONE: " + imageInfo);
        }
      } else {
        System.err.println("------->> TEXT GENERATION SKIPPED AS ALREADY DONE\n\n");
      }

      // process extracted data
      List<Person> allPersons = new ArrayList<>();
      for (String imageInfo : allImagesInfo) {
        //System.out.println(imageInfo);
        File datafile = new File(getOutputTextFileName(imageInfo, true));
        if (datafile.exists()) {
          String data = voterDataExtraction.getData(datafile);
          // System.out.println("---> Processing : " + imageInfo.getPath());
          List<Person> persons;
          if (Constants.USE_ENHANCED_LOGIC) {
            //System.out.println(".........");
            persons = new EnhancedDataExtractionModule().start(data.toUpperCase(), CONSTITUENCY_WARD, imageInfo);
          } else {
            persons = new DataExtractModule().start(data, CONSTITUENCY_WARD);
          }
          if (persons != null && !persons.isEmpty()) allPersons.addAll(persons);
        } else {
          System.err.println("File not exists " + datafile.getAbsolutePath());
        }
      }
      // for all persons create excel sheet
      System.out.println("Total Voters Extracted : " + allPersons.size());
      // System.out.println(allPersons);
      try {
        String csvFile = new ExcelGenerator(CONSTITUENCY_WARD).write(allPersons) ;
        System.out.println("Program terminated successfully. Opening generated EXCEL sheet " + csvFile);
        openCSVFile(csvFile);
      } catch (Exception e) {
        System.err.println("Error " + e.getMessage());
      }
    } catch (Exception e){
      System.err.println("Error " + e.getMessage());
    }
    System.exit(0);
  }

  private static void openCSVFile(String csvFile) {
    String[] command = { "open", csvFile };
    // System.out.println(Arrays.toString(command));
    Runtime run = Runtime.getRuntime();
    try {
      Process process = Runtime.getRuntime().exec(command);
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      StringBuilder data = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        data.append(line);
      }
      reader.close();
      // System.out.println(data);
    } catch(Exception e) {
      System.err.println("Error opening CSV File: " + e.getMessage());    }
  }

  private static String getConstituencyAndWard(String dataFolderPath) {
    int index = dataFolderPath.lastIndexOf(File.separator);
    int ward = 0 ;
    if(index>0){
      int index2 = dataFolderPath.indexOf("-",index);
      if(index2>0){
        try { ward = Integer.parseInt(dataFolderPath.substring(index2+1)); } catch (Exception ignored) { }
        return dataFolderPath.substring(index+1,index2) + "-" + ward;
      }
    }
    return "UNKNOWN-0";
  }

  /*private static boolean getValuesFromProperties() {
    try {
      File properties = new File("init.txt");
      System.out.println("Properties file: " + properties.getAbsolutePath());
      if(properties.exists()) {
        Properties prop = new Properties();
        InputStream stream = new FileInputStream(properties);
        prop.load(stream);
        Object temp = prop.get("BASE");
        if (temp == null) {
          System.out.println("@@@ Error : BASE_FOLDER not defined in properties file");
          System.exit(0);
        } else {
          dataFolder = temp.toString();
        }
        return true;
      }
    } catch (Exception e) {
      Communicator.showError(e.getMessage());
    }
    return false;
  }*/

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
        // ignore if folder name starts with generated
        if(file.getAbsolutePath().toLowerCase().contains(GENERATED)) continue;
        List<String> images = moveAllImagesFromDataFolderIntoImageFolder(file);
        if(!images.isEmpty()) allImages.addAll(images);
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
      File copied = new File(Constants.IMAGE_FOLDER_PATH + File.separator + (count++) + "_" + filename + ".png");
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

