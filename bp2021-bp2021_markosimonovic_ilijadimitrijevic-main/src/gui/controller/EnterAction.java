package gui.controller;

import app.Main;
import gui.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EnterAction implements ActionListener {


    @Override
    public void actionPerformed(ActionEvent e) {

        String value = MainFrame.getInstance().getTextArea().getText();
        MainFrame.getInstance().getAppCore().getValidator().validateText(value);
        
    }
}
