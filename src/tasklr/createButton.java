/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tasklr;

/**
 *
 * @author ADMIN
 */
import javax.swing.*;
import java.awt.*;
import javax.swing.border.Border;
public class createButton {
    
    public static JButton button(String text, Color bgColor, Color txtColor, Border border, boolean focus){
        
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(txtColor);
        button.setBorder(border);
        button.setFocusable(focus);
        return button;
    }
    
//    JButton newBtn = createButton.button("new", Color.BLACK, Color.WHITE, 0, 0, null);
}
