package main_package;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import main_package.export.ExportToXML;
import main_package.model.InformationGenerator;
import main_package.model.JGraphXDraw;
import main_package.tools.Maintenance;
import main_package.tools.RevisionDifference;

import org.eclipse.jgit.api.errors.GitAPIException;

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
    private static Map<String, Integer> methodsComplexity;
    private static List<Map<String, Map<String, AtomicInteger>>> relationsList = new ArrayList<>();
    private static List<Map<String, Integer>> weightsList = new ArrayList<>();
    private static int comboControl = 0;
    private static RevisionDifference revisionDifference;

    public static void main(String[] args) {
        try {
            revisionDifference = new RevisionDifference("https://github.com/dawidkruczek/projectIO.git");
            revisionDifference.getRepository();
        } catch (GitAPIException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        startView();
    }

    private static void loadNewRepository(String url){
        try {
            revisionDifference = new RevisionDifference(url);
            revisionDifference.getRepository();
        } catch (GitAPIException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startView() {
        loadData();
        JPanel allContent = new JPanel(new BorderLayout());
        JGraphXDraw applet = new JGraphXDraw();
        applet.init();
        String[] optionStrings = {"Graf zależności między plikami", "Graf relacji między funkcjami(metodami)",
                "Graf relacji między modułami logicznymi", "Graf 1 i 2", "Graf 1 i 3", "Graf 2 i 3", "Wszystkie grafy", "Graf relacji między plikami, " +
                "a metodami(funkcjami)"};

        JLabel version = new JLabel("Version: " + Maintenance.VERSION_IDENTIFIER);

        JPanel downPanel = new JPanel(new BorderLayout());
        JButton exportButton = new JButton("Export selected graph!");
        exportButton.addActionListener(e -> {
            System.out.println(e.getActionCommand());
            ExportToXML exportGraph = new ExportToXML();
            if(comboControl==0||comboControl==1||comboControl==2||comboControl==3||comboControl==4||comboControl==5||comboControl==6)
                exportGraph.exportGraphToXML(relationsList, weightsList, optionStrings[comboControl]);
            else if (comboControl==7)
                exportGraph.exportGraphToXML(filesMethodsRelations, optionStrings[comboControl]);
            else
                System.out.println("Nothing to export!");
        });

        JButton changeSource = new JButton("Change source for another project");
        changeSource.addActionListener(e -> {
            if(e.getActionCommand().contains("Change source for another project")){
                changeSource.setText("Change source for your project");
                //Maintenance.MAIN_PATH = "path to another project";
                loadNewRepository("https://github.com/maciejsikora2302/Evolution-Generator.git");
                revisionDifference.setPATH("\\src");
                applet.getNewGraph().removeCells(applet.getNewGraph().getChildVertices(applet.getNewGraph().getDefaultParent()));
                loadData();
            }
            else{
                changeSource.setText("Change source for another project");
                //Maintenance.MAIN_PATH = "src/main/java";
                loadNewRepository("https://github.com/dawidkruczek/projectIO.git");
                revisionDifference.setPATH("\\src\\main\\java");
                applet.getNewGraph().removeCells(applet.getNewGraph().getChildVertices(applet.getNewGraph().getDefaultParent()));
                loadData();
            }
        });
        JPanel switchButtons = new JPanel(new BorderLayout());
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
                        relationsList.clear();
                        weightsList.clear();
                        relationsList.add(filesRelations);
                        weightsList.add(filesWeights);
                        applet.createGraphX(relationsList, weightsList, methodsComplexity, frame);
                        break;
                    case 1:
                        relationsList.clear();
                        weightsList.clear();
                        relationsList.add(methodsRelations);
                        weightsList.add(methodsWeights);
                        applet.createGraphX(relationsList, weightsList, methodsComplexity, frame);
                        break;
                    case 2:
                        relationsList.clear();
                        weightsList.clear();
                        relationsList.add(packagesRelations);
                        weightsList.add(packagesWeights);
                        applet.createGraphX(relationsList, weightsList, methodsComplexity, frame);
                        break;
                    case 3:
                        relationsList.clear();
                        weightsList.clear();
                        relationsList.add(filesRelations);
                        relationsList.add(methodsRelations);
                        weightsList.add(filesWeights);
                        weightsList.add(methodsWeights);
                        applet.createGraphX(relationsList, weightsList, methodsComplexity, frame);
                        break;
                    case 4:
                        relationsList.clear();
                        weightsList.clear();
                        relationsList.add(filesRelations);
                        relationsList.add(packagesRelations);
                        weightsList.add(filesWeights);
                        weightsList.add(packagesWeights);
                        applet.createGraphX(relationsList, weightsList, methodsComplexity, frame);
                        break;
                    case 5:
                        relationsList.clear();
                        weightsList.clear();
                        relationsList.add(methodsRelations);
                        relationsList.add(packagesRelations);
                        weightsList.add(methodsWeights);
                        weightsList.add(packagesWeights);
                        applet.createGraphX(relationsList, weightsList, methodsComplexity, frame);
                        break;
                    case 6:
                        relationsList.clear();
                        weightsList.clear();
                        relationsList.add(filesRelations);
                        relationsList.add(methodsRelations);
                        relationsList.add(packagesRelations);
                        weightsList.add(filesWeights);
                        weightsList.add(methodsWeights);
                        weightsList.add(packagesWeights);
                        applet.createGraphX(relationsList, weightsList, methodsComplexity, frame);
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
        switchButtons.add(changeSource, BorderLayout.LINE_START);
        switchButtons.add(exportButton, BorderLayout.LINE_END);
        downPanel.add(version, BorderLayout.LINE_START);
        downPanel.add(switchButtons, BorderLayout.LINE_END);
        allContent.add(downPanel, BorderLayout.SOUTH);
        frame = new JFrame();
        frame.setTitle("Projekt - Inżynieria oprogramowania");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(screenSize.width, screenSize.height);
        frame.add(allContent);
        frame.setContentPane(allContent);
        frame.setVisible(true);

    }
    private static void loadData(){
        InformationGenerator current = new InformationGenerator(revisionDifference.PATH);
        try {
            revisionDifference.goToPreviousMerge();
        } catch (GitAPIException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InformationGenerator old = new InformationGenerator(revisionDifference.PATH);


        methodsRelations = revisionDifference.findDifferences2(old.getMethodsDependency(), current.getMethodsDependency());
        methodsWeights = revisionDifference.findDifferences(old.getMethodsWeights(), current.getMethodsWeights());
        methodsComplexity = current.getMethodsComplexity();

        packagesRelations = revisionDifference.findDifferences2(old.getPackagesDependency(), current.getPackagesDependency());
        packagesWeights = revisionDifference.findDifferences(old.getPackagesWeights(), current.getPackagesWeights());

        filesRelations = revisionDifference.findDifferences2(old.getFilesDependency(), current.getFilesDependency());
        filesWeights = revisionDifference.findDifferences(old.getFilesWeights(), current.getFilesWeights());

        filesMethodsRelations = revisionDifference.findDifferences3(old.getFilesMethodsDependency(), current.getFilesMethodsDependency());
        current.printRelations();
        current.printWeigths();
        current.partition(2);
    }
}
