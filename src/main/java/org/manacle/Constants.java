package org.manacle;

import java.io.File;

public class Constants {

  public static final boolean GENERATE = true;

  public static final boolean USE_ENHANCED_LOGIC = true;

  public static final String CONSTITUENCY_WARD = "JANGPURA-41";
  public static final String DELIMITER = "\t";

  private static final String BASE = "/Users/manish/Desktop/election" ;
  public static final String DATA_FOLDER_PATH = BASE + File.separator + CONSTITUENCY_WARD;
  private static final String GENERATED = "generated" ;
  public static final String IMAGE_FOLDER_PATH = BASE + File.separator + GENERATED + File.separator + "images";
  public static final String OUTPUT_FOLDER_PATH = BASE + File.separator + GENERATED + File.separator + "output" ;
  public static final String CSV_FOLDER_PATH = BASE + File.separator + GENERATED + File.separator + "csv" ;

  public static final String fieldName = "NAME";
  public static final String fieldFather = "FATHER";
  public static final String fieldMother = "MOTHER";
  public static final String fieldOther = "OTHER";
  public static final String fieldHusband = "HUSBAND";
  public static final String fieldHouse = "HOUSE NUMBER";
  public static final String fieldAge = "AGE";
  public static final String fieldGender = "GENDER";
  public static final String fieldGender1 = "GANDER";
  public static final String fieldSeparator = "MALE";
  public static final String fieldExtra1 = "AVAILABLE";
  public static final String fieldExtra2 = "PHOTO";

  public static final int FEMALE = 1 ;
  public static final int MALE  = 2 ;
  public static final int OTHERS = 3 ;

}
