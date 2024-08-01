package org.manacle;

import javax.swing.*;

public class Communicator {

  public static boolean ask(String title, String query) {
    int result = JOptionPane.showConfirmDialog(null, query, title, JOptionPane.YES_NO_OPTION);
    return result==0;
  }

  public static String get(String query){
    return JOptionPane.showInputDialog(query);
  }

  public static void showNotice(String msg) {
    System.out.println("\n@@@ NOTICE @@@  " + msg);
    JOptionPane.showMessageDialog(null,msg,"NOTICE", JOptionPane.PLAIN_MESSAGE);
  }

  public static void showError(String error){
     System.out.println("\n@@@ ERROR @@@ " + error);
     JOptionPane.showMessageDialog(null,error,"ERROR", JOptionPane.ERROR_MESSAGE);
  }

  public static void showWarning(String msg) {
    System.out.println("\n@@@ WARNING @@@ " + msg);
    JOptionPane.showMessageDialog(null,msg,"WARNING", JOptionPane.WARNING_MESSAGE);
  }

}
