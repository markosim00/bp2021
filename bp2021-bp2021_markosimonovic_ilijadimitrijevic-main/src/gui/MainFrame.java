package gui;

import app.AppCore;
import app.Main;
import compiler.QueryCompiler;
import gui.controller.EnterAction;
import lombok.Data;
import observer.Notification;
import observer.Subscriber;
import observer.enums.NotificationCode;
import resource.implementation.InformationResource;
import validator.ValidatorImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.Vector;

@Data
public class MainFrame extends JFrame implements Subscriber {

    private static MainFrame instance = null;

    private AppCore appCore;
    private JTable jTable;
    private JScrollPane jsp;
    private JPanel bottomStatus;
    private JTextArea textArea;
    private JTextArea textArea2;
    private JPanel jPanel;
    private JButton btn = new JButton("Enter");


    private MainFrame() {

    }

    public static MainFrame getInstance(){
        if (instance==null){
            instance=new MainFrame();
            instance.initialise();
        }
        return instance;
    }


    /*private void initialise() {

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        this.setSize(screenWidth/2, screenHeight/4);
        this.setTitle("SQL QueryBuilder");
        jTable = new JTable();
        jTable.setPreferredScrollableViewportSize(new Dimension(500,400));
        jTable.setFillsViewportHeight(true);
        jPanel = new JPanel();
        textArea = new JTextArea();
        textArea2 = new JTextArea();
        textArea.setPreferredSize(new Dimension(700,100));
        textArea2.setPreferredSize(new Dimension(300,100));
        this.add(textArea, BorderLayout.NORTH);
        //this.add(textArea2, BorderLayout.CENTER);
        this.add(jTable, BorderLayout.CENTER);
        btn.addActionListener(new EnterAction());
        jPanel.add(btn);
        this.add(jPanel, BorderLayout.SOUTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        
        this.pack();
//        this.setLocationRelativeTo(null);
        this.setVisible(true);


    }*/
    
    private void initialise() {
    	setTitle("SQL Query Builder");
    	setPreferredSize(new Dimension(900, 250));
    	setLayout(new BorderLayout());
    	setLocationRelativeTo(null);
    	jTable = new JTable();
    	jTable.setPreferredScrollableViewportSize(new Dimension(500,400));
    	jTable.setFillsViewportHeight(true);
    	jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        textArea = new JTextArea();
    	textArea2 = new JTextArea();
    	textArea.setPreferredSize(new Dimension(100, 50));
    	textArea2.setPreferredSize(new Dimension(100, 50));
    	textArea.setLineWrap(true);
    	textArea2.setLineWrap(true);
    	btn.addActionListener(new EnterAction());
    	jPanel.add(textArea);
        jPanel.add(btn);
    	jPanel.add(textArea2);
    	add(jPanel);
    	//add(jTable, BorderLayout.CENTER);
    	pack();
    }

    public void setAppCore(AppCore appCore) {
        this.appCore = appCore;
        this.appCore.addSubscriber(this);
        this.jTable.setModel(appCore.getTableModel());
        this.setVisible(true);
    }


    @Override
    public void update(Notification notification) {

        if (notification.getCode() == NotificationCode.RESOURCE_LOADED){
            System.out.println((InformationResource)notification.getData());
        }

        else{
            jTable.setModel((TableModel) notification.getData());
        }

    }

}
