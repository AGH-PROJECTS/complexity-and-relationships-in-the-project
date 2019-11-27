package main_package;
import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import javax.swing.*;

import com.jamesmurty.utils.XMLBuilder2;
import main_package.model.InformationGenerator;
import main_package.model.JGraphXDraw;
import main_package.export.XMLCreator;

public class Main {
    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private static JFrame frame;
    private static Map<String, Map<String, Integer>> filesRelations;
    private static Map<String, Map<String, Integer>> methodsRelations;
    private static Map<String, Map<String, Integer>> packagesRelations;
    private static Map<String, Integer> filesWeights;
    private static Map<String, Integer> methodsWeights;
    private static Map<String, Integer> packagesWeights;
    private static String graphOption;
    public static void main(String[] args) {
        try {
            InformationGenerator informationGenerator = new InformationGenerator();

            methodsRelations =  informationGenerator.getMethodsRelations();
            methodsWeights = informationGenerator.getMethodsWeights();

            packagesRelations = informationGenerator.getPackagesRelations();
            packagesWeights = informationGenerator.getPackagesWeights();

            filesRelations = informationGenerator.getFilesRelations();
            filesWeights = informationGenerator.getFilesWeights();

        } catch (IOException e) {
            e.printStackTrace();
        }

        JPanel allContent = new JPanel(new BorderLayout());

        JGraphXDraw applet = new JGraphXDraw();

        JPanel panel = new JPanel(new FlowLayout());
        panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        String[] optionStrings = { "Historia 1", "Historia 2", "Historia 3", "Historia 1 i 2", "Historia 1 i 3", "Historia 2 i 3", "Wszystkie historie" };

        JComboBox optionList = new JComboBox(optionStrings);

        graphOption = "Historia 1";
        optionList.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED){
                graphOption = optionList.getSelectedItem().toString();
                switch (graphOption){
                    case "Historia 1":
                        applet.createGraphX(filesRelations, filesWeights, frame);
                        break;
                    case "Historia 2":
                        applet.createGraphX(methodsRelations, methodsWeights, frame);
                        break;
                    case "Historia 3":
                        applet.createGraphX(packagesRelations, packagesWeights, frame);
                        break;
                    case "Historia 1 i 2":
                        applet.createGraphX(filesRelations, methodsRelations, filesWeights, methodsWeights, frame);
                        break;
                    case "Historia 1 i 3":
                        applet.createGraphX(filesRelations, packagesRelations, filesWeights, packagesWeights, frame);
                        break;
                    case "Historia 2 i 3":
                        applet.createGraphX(methodsRelations, packagesRelations, methodsWeights, packagesWeights, frame);
                        break;
                    case "Wszystkie historie":
                        applet.createGraphX(filesRelations, methodsRelations, packagesRelations, filesWeights, methodsWeights, packagesWeights, frame);
                        break;
                    default:
                        System.out.println("Nothing to show!");
                        break;
                }
            }
        });
        allContent.add(optionList, BorderLayout.PAGE_START);
        allContent.add(applet,BorderLayout.CENTER);

        frame = new JFrame();
        frame.setTitle("Projekt - Inżynieria oprogramowania");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(screenSize.width, screenSize.height);
        frame.setResizable(true);
        frame.add(allContent);
        frame.setContentPane(allContent);
        frame.setVisible(true);
        try {
            XMLCreator xml = new XMLCreator();
            xml.addElements(packagesRelations, packagesWeights);
            XMLBuilder2 builder = xml.getExportedXML();
            PrintWriter writer = new PrintWriter("package.xml");
            Properties properties = xml.getProperties();
            builder.toWriter(writer, properties);
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
        try {
            XMLCreator xml = new XMLCreator();
            xml.addElements(filesRelations, filesWeights);
            XMLBuilder2 builder = xml.getExportedXML();
            PrintWriter writer = new PrintWriter("files.xml");
            Properties properties = xml.getProperties();
            builder.toWriter(writer, properties);
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
        try {
            XMLCreator xml = new XMLCreator();
            xml.addElements(methodsRelations, methodsWeights);
            XMLBuilder2 builder = xml.getExportedXML();
            PrintWriter writer = new PrintWriter("methods.xml");
            Properties properties = xml.getProperties();
            builder.toWriter(writer, properties);
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }

    }

    public static Map<String, Map<String, Integer>> getMethodsRelations() {
        return methodsRelations;
    }
}
