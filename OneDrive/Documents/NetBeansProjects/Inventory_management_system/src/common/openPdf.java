/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package common;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author kasso
 */
public class openPdf {

    public static void open(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                } else {
                    System.out.println("Desktop is not supported on this platform.");
                }
            } else {
                // TODO: Implement better error handling here.
                System.out.println("File not found: " + filePath);
            }
        } catch (IOException e) {
            // TODO: Log this exception properly.
            e.printStackTrace();
        }
    }
}
