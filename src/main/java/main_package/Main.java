package main_package;

import com.jamesmurty.utils.XMLBuilder2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Properties;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import main_package.export.XMLCreator;
import main_package.model.InformationGenerator;
import main_package.model.JGraphXDraw;
import main_package.tools.Constants;

public class Main {
    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private static JFrame frame;
    private static Map<String, Map<String, Integer>> filesRelations;
    private static Map<String, Map<String, Integer>> methodsRelations;
    private static Map<String, Map<String, Integer>> packagesRelations;
    private static Map<String, Integer> filesWeights;
    private static Map<String, Integer> methodsWeights;
    private static Map<String, Integer> packagesWeights;
    private static int comboControl = 0;

    private static void mainTest() {
        System.out.println("Test w Main");
    }
    public static void main(String[] args) {
        InformationGenerator informationGenerator = new InformationGenerator();
        Constants constants = new Constants();
        informationGenerator.test();
        informationGenerator.test();
        informationGenerator.test2();
        mainTest();
        /*methodsRelations = informationGenerator.getMethodsRelations();
        methodsWeights = informationGenerator.getMethodsWeights();

        packagesRelations = informationGenerator.getPackagesRelations();
        packagesWeights = informationGenerator.getPackagesWeights();

        filesRelations = informationGenerator.getFilesRelations();
        filesWeights = informationGenerator.getFilesWeights();

        JPanel allContent = new JPanel(new BorderLayout());

        JGraphXDraw applet = new JGraphXDraw();
        applet.setBackground(Color.DARK_GRAY);
        String[] optionStrings = {"Graf zależności między plikami", "Graf relacji między funkcjami/metodami",
                "Graf relacji między modułami logicznymi", "Graf 1 i 2", "Graf 1 i 3", "Graf 2 i 3", "Wszystkie grafy"};

        JComboBox optionList = new JComboBox(optionStrings);
        optionList.setSelectedIndex(-1);
        optionList.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                comboControl = optionList.getSelectedIndex();
                switch (comboControl) {
                    case 0:
                        applet.createGraphX(filesRelations, filesWeights, frame);
                        break;
                    case 1:
                        applet.createGraphX(methodsRelations, methodsWeights, frame);
                        break;
                    case 2:
                        applet.createGraphX(packagesRelations, packagesWeights, frame);
                        break;
                    case 3:
                        applet.createGraphX(filesRelations, methodsRelations, filesWeights, methodsWeights, frame);
                        break;
                    case 4:
                        applet.createGraphX(filesRelations, packagesRelations, filesWeights, packagesWeights, frame);
                        break;
                    case 5:
                        applet.createGraphX(methodsRelations, packagesRelations, methodsWeights, packagesWeights, frame);
                        break;
                    case 6:
                        applet.createGraphX(filesRelations, methodsRelations, packagesRelations, filesWeights, methodsWeights, packagesWeights, frame);
                        break;
                    default:
                        System.out.println("Nothing to show!");
                        break;
                }
            }
        });
        allContent.add(optionList, BorderLayout.PAGE_START);
        allContent.add(applet, BorderLayout.CENTER);
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            XMLCreator xml = new XMLCreator();
            xml.addElements(filesRelations, filesWeights);
            XMLBuilder2 builder = xml.getExportedXML();
            PrintWriter writer = new PrintWriter("files.xml");
            Properties properties = xml.getProperties();
            builder.toWriter(writer, properties);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            XMLCreator xml = new XMLCreator();
            xml.addElements(methodsRelations, methodsWeights);
            XMLBuilder2 builder = xml.getExportedXML();
            PrintWriter writer = new PrintWriter("methods.xml");
            Properties properties = xml.getProperties();
            builder.toWriter(writer, properties);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
*/
    }
}
