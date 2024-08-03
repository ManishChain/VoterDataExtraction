package org.manacle;

import java.io.File;

public class ImageInfo {
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
    this.textFileName = Constants.OUTPUT_FOLDER_PATH
      + File.separator
      + folder.replaceAll("-","_") + "_" + name;
  }

  /*@Override
  public String toString() {
    return "ImageInfo{" +
      "folder='" + folder + '\'' +
      ", name='" + name + '\'' +
      ", extension='" + extension + '\'' +
      ", path='" + path + '\'' +
      ", textFileName='" + textFileName + '\'' +
      '}';
  }*/
}
