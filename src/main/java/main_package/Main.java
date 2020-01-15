package main_package;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import main_package.model.InformationGenerator;
import main_package.model.JGraphXDraw;
import main_package.tools.Maintenance;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class Main {
    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private static JFrame frame;
    private static Map<String, Map<String, AtomicInteger>> filesRelations;
    private static Map<String, Map<String, AtomicInteger>> methodsRelations;
    private static Map<String, Map<String, AtomicInteger>> packagesRelations;
    private static Map<String, String> filesMethodsRelations;
    private static Map<String, Integer> filesWeights;
    private static Map<String, Integer> methodsWeights;
    private static Map<String, Integer> packagesWeights;
    private static int comboControl = 0;

    public static void main(String[] args) {
        System.out.println(Maintenance.getVersionIdentifier());
        startView();

       /* try {
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

    private static void startView() {
        InformationGenerator informationGenerator = new InformationGenerator();
        methodsRelations = informationGenerator.getMethodsDependency();
        methodsWeights = informationGenerator.getMethodsWeights();

        packagesRelations = informationGenerator.getPackagesDependency();
        packagesWeights = informationGenerator.getPackagesWeights();

        filesRelations = informationGenerator.getFilesDependency();
        filesWeights = informationGenerator.getFilesWeights();

        filesMethodsRelations = informationGenerator.getFilesMethodsDependency();

        JPanel allContent = new JPanel(new BorderLayout());

        JGraphXDraw applet = new JGraphXDraw();

        String[] optionStrings = {"Graf zależności między plikami", "Graf relacji między funkcjami/metodami",
                "Graf relacji między modułami logicznymi", "Graf 1 i 2", "Graf 1 i 3", "Graf 2 i 3", "Wszystkie grafy", "Graf relacji między plikami," +
                "a metodami/funkcjami"};

        JLabel version = new JLabel("Version: " + Maintenance.VERSION_IDENTIFIER);

        JPanel downPanel = new JPanel(new BorderLayout());
        JButton exportButton = new JButton("Export selected graph!");
        exportButton.setEnabled(false);


        JComboBox<String> optionList = new JComboBox(optionStrings);
        optionList.setLightWeightPopupEnabled(false);
        optionList.setSelectedIndex(-1);
        optionList.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                exportButton.setEnabled(true);
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
                    case 7:
                        applet.createGraphX(filesMethodsRelations, frame);
                        break;
                    default:
                        System.out.println("Nothing to show!");
                        break;
                }
            }
        });
        allContent.add(optionList, BorderLayout.NORTH);
        allContent.add(applet, BorderLayout.CENTER);
        downPanel.add(version, BorderLayout.LINE_START);
        downPanel.add(exportButton, BorderLayout.LINE_END);
        allContent.add(downPanel, BorderLayout.SOUTH);
        frame = new JFrame();
        frame.setTitle("Projekt - Inżynieria oprogramowania");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(screenSize.width, screenSize.height);
        frame.setResizable(false);
        frame.add(allContent);
        frame.setContentPane(allContent);
        frame.setVisible(true);
    }
}
