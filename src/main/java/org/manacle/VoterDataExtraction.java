package org.manacle;

import org.apache.commons.io.FileUtils;
import org.manacle.entity.Info;
import org.manacle.entity.Person;

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

  static boolean linuxOrMac = true;

  public static void main(String[] args) {
    PrintStream psOutput = null, psErr = null;
    JOptionPane.showMessageDialog(null,"Starting program...\n\nPlease select folder containing images","NOTICE", JOptionPane.PLAIN_MESSAGE);
    try {
      File dataFolder = null;
      JFileChooser fc = new JFileChooser("", FileSystemView.getFileSystemView());
      fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      Frame frame = getFrame();
      int option = fc.showOpenDialog(frame);
      if (option == JFileChooser.APPROVE_OPTION) {
        dataFolder = fc.getSelectedFile();
      } else {
        JOptionPane.showMessageDialog(null,"No folder selected. Please try again","ERROR", JOptionPane.ERROR_MESSAGE);
        System.exit(0);
      }
      if (!dataFolder.exists() || Objects.requireNonNull(dataFolder.listFiles()).length == 0) {
        JOptionPane.showMessageDialog(null,"Data Folder not found or empty .... " + dataFolder.getAbsolutePath(),"ERROR", JOptionPane.ERROR_MESSAGE);
        System.exit(0);
      }
      Info constituencyInfo = new Info(dataFolder.getAbsolutePath() + File.separator + "params.txt");
      try { // Create a FileOutputStream to write to a file
        // FileOutputStream fosOutput = new FileOutputStream("output.txt");
        FileOutputStream fosErr = new FileOutputStream(dataFolder.getAbsolutePath() + File.separator + "err.txt");
        // Create a PrintStream that wraps the FileOutputStream
        // psOutput = new PrintStream(fosOutput);
        psErr = new PrintStream(fosErr);
        // Redirect System.out to the PrintStream
        //System.setOut(psOutput);
        System.setErr(psErr);
      } catch (FileNotFoundException e) {
        System.err.println("Error: Output file not found");
      }

      String os = System.getProperty("os.name") ;
      // System.out.println(os);
      if(!os.toLowerCase().contains("mac")) {
        linuxOrMac = false;
      }

      Constants.IMAGE_FOLDER_PATH = dataFolder + File.separator + GENERATED + File.separator + "images";
      Constants.OUTPUT_FOLDER_PATH = dataFolder + File.separator + GENERATED + File.separator + "output";
      Constants.CSV_FOLDER_PATH = dataFolder + File.separator + GENERATED + File.separator + "csv";

      File imageFolder = new File(Constants.IMAGE_FOLDER_PATH);
      if (!imageFolder.exists()) {
        if (!imageFolder.mkdirs()) {
          JOptionPane.showMessageDialog(null,"Image folder not found or created .... " + imageFolder.getAbsolutePath(),"ERROR", JOptionPane.ERROR_MESSAGE);
          System.exit(0);
        }
      }
      File outputFolder = new File(Constants.OUTPUT_FOLDER_PATH);
      if (!outputFolder.exists()) {
        if (!outputFolder.mkdirs()) {
          JOptionPane.showMessageDialog(null,"Output folder not found or created .... " + outputFolder.getAbsolutePath(),"ERROR", JOptionPane.ERROR_MESSAGE);
          System.exit(0);
        }
      }
      File csvFolder = new File(Constants.CSV_FOLDER_PATH);
      if (!csvFolder.exists()) {
        if (!csvFolder.mkdirs()) {
          JOptionPane.showMessageDialog(null,"CSV folder not found or created .... " + outputFolder.getAbsolutePath(),"ERROR", JOptionPane.ERROR_MESSAGE);
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

      System.out.println("Total images are " + allImagesInfo.size());

      // cut single page image into three
      int answer = JOptionPane.showConfirmDialog(null, "Are all \"Single Page\" images need to be cut in 3 parts ?", "Cut Image Into Three Parts", JOptionPane.YES_NO_OPTION);
      if (answer==0) {
        List<String> allCutImagesInfo = new ArrayList<>();
        ImageCutter imageCutter = new ImageCutter();
        for (String imageInfo : allImagesInfo) {
          allCutImagesInfo.addAll(imageCutter.cutImageIntoParts(imageInfo));
        }
        allImagesInfo = allCutImagesInfo; // this is very important as new images must be used
      } else {
        System.err.println("------->> IMAGE CUTTING PROCESS SKIPPED AS NOT SINGLE PAGE IMAGES \n\n");
      }
      // extract text from images
      answer = JOptionPane.showConfirmDialog(null, "Do you want to generate TEXT files from images ?", "Generate", JOptionPane.YES_NO_OPTION);
      if (answer==0) {
        for (String imageInfo : allImagesInfo) {
          voterDataExtraction.generateTextFileByScanningImage(imageInfo);
          System.out.println( " " + (--count) + ": extracted data from " + imageInfo);
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
          int imageIndex = getImageIndex(datafile.getAbsolutePath());
          assert data != null;
          List<Person> persons = new EnhancedDataExtractionModule().start(data.toUpperCase(), constituencyInfo, imageIndex);
          if (persons != null && !persons.isEmpty()) allPersons.addAll(persons);
        } else {
          System.err.println("File not exists " + datafile.getAbsolutePath());
        }
      }
      if(!allPersons.isEmpty()) {
        try {
          String csvFile = new ExcelGenerator(constituencyInfo).write(allPersons);
          System.out.println("Program terminated successfully. Opening generated EXCEL sheet " + csvFile);
          answer = JOptionPane.showConfirmDialog(null, "Do you want to open generated EXCEL file ?", "OPEN", JOptionPane.YES_NO_OPTION);
          if (answer==0) {
            openCSVFile(csvFile);
          }
        } catch (Exception e) {
          System.err.println("Error opening file " + e.getMessage());
        }
        // for all persons create excel sheet
        int extractedVoters = allPersons.size();
        int expectedVoters = constituencyInfo.getTotal();
        int missed = expectedVoters - extractedVoters;
        if (missed > 0) {
          System.out.println("Voters  expected=" + expectedVoters + "  extracted=" + extractedVoters + "  missed=" + missed);
        } else {
          System.out.println("All Voters Extracted " + extractedVoters);
        }
        System.out.println("Total males " + constituencyInfo.getMales() + " found " + constituencyInfo.getCountMales());
        System.out.println("Total females " + constituencyInfo.getFemales() + " found " + constituencyInfo.getCountFemales());
        System.out.println("Total others " + constituencyInfo.getOthers() + " found " + constituencyInfo.getCountOthers());
        System.err.println("\n\nList of missing serial numbers");
        Map<Integer, Boolean> map = constituencyInfo.getSerialNumberStats();
        int count = 0 ;
        for(Integer serialNumber : map.keySet()) {
          if(!map.get(serialNumber)) { count++; System.err.println(" " + serialNumber); }
        }
        System.err.println("Total missing : " + count + ", check duplicates");
      } else {
        System.err.println("All persons empty");
      }
    } catch (Exception e){
      System.err.println("Error major " + e.getMessage());
    } finally {
      // Close the PrintStream to release resources
      //if(psOutput!=null) psOutput.close();
      if(psErr!=null) psErr.close();
    }
    System.exit(0);
  }

  private static int getImageIndex(String path) {
    //File copied = new File(Constants.IMAGE_FOLDER_PATH + File.separator + (count++) + "_" + filename + ".png");
    try {
      int index = path.lastIndexOf(File.separator);
      if (index >= 0) {
        int index2 = path.indexOf("_", index);
        if (index2 >= 0) {
          return Integer.parseInt(path.substring(index + 1, index2));
        }
      }
    } catch (Exception e){
      System.err.println("Error extracting image index " + path);
    }
    return 0;
  }

  private static void openCSVFile(String csvFile) {
    String[] command = { linuxOrMac?"open":"excel", csvFile };
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
      String[] command = { "tesseract", imageInfo, getOutputTextFileName(imageInfo, false), "--psm", "11" };
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

