/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package recipesharingapp;

/**
 *
 * @author ADMIN
 */
import javax.swing.*;
import java.awt.*;
public class createLabel {
    public static JLabel label(String text, Color color, int horizontalAlignment, int verticalAlignment){
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setHorizontalAlignment(horizontalAlignment);
        label.setVerticalAlignment(verticalAlignment);
        return label;
    }
}
