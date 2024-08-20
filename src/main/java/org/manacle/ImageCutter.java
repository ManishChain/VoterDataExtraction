package org.manacle;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.*;
import java.awt.*;
import java.util.ArrayList;

public class ImageCutter {

  public static void main(String[] args) throws IOException {
    new ImageCutter().cutImageIntoParts("/Users/manish/Desktop/test.png");
  }

  public ArrayList<String> cutImageIntoParts(String imageInfo) throws IOException {
    InputStream is = Files.newInputStream(Paths.get(imageInfo));
    BufferedImage image = ImageIO.read(is);
    ArrayList<String> allCutImagesInfo = new ArrayList<>();
    // initializing rows and columns
    int rows = 1;
    int columns = 3;

    // initializing array to hold sub-images
    BufferedImage imgs[] = new BufferedImage[3];

    // Equally dividing original image into subimages
    int subimage_Width = image.getWidth() / columns;
    int subimage_Height = image.getHeight() / rows;

    int current_img = 0;

    // iterating over rows and columns for each sub-image
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        // Creating sub image
        imgs[current_img] = new BufferedImage(subimage_Width, subimage_Height, image.getType());
        Graphics2D img_creator = imgs[current_img].createGraphics();

        // coordinates of source image
        int src_first_x = subimage_Width * j;
        int src_first_y = subimage_Height * i;

        // coordinates of sub-image
        int dst_corner_x = subimage_Width * j + subimage_Width;
        int dst_corner_y = subimage_Height * i + subimage_Height;

        img_creator.drawImage(image, 0, 0, subimage_Width, subimage_Height, src_first_x, src_first_y, dst_corner_x, dst_corner_y, null);
        current_img++;
      }
    }

    //writing sub-images into image files
    String[] info = getImageInfo(imageInfo);
    for (int i = 0; i < 3; i++) {
      File outputFile = new File(info[0] + File.separator + info[1] + "_" + i + ".png");
      ImageIO.write(imgs[i], "png", outputFile);
      allCutImagesInfo.add(outputFile.getAbsolutePath());
    }
    System.out.println( " Cut into 3 parts : " + imageInfo);
    // rename original image
    File fileToMove = new File(imageInfo);
    boolean isMoved = fileToMove.renameTo(new File(imageInfo+".etc"));
    if (!isMoved) {
      throw new FileSystemException(imageInfo+".etc");
    }
    return allCutImagesInfo;
  }

  private String[] getImageInfo(String imageInfo) {
    String[] temp = new String[2];
    int index = imageInfo.lastIndexOf(File.separator);
    temp[0] = imageInfo.substring(0,index); // directory
    temp[1] = imageInfo.substring(index).replace(".png",""); // image name
    return temp;
  }

}

