package main_package;
import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import main_package.model.InformationGenerator;
import main_package.model.JGraphXDraw;

public class Main {
    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private static JFrame frame;
    private static Map<String, Map<String, Integer>> filesRelations;
    private static Map<String, Map<String, Integer>> methodsRelations;
    private static Map<String, Map<String, Integer>> packagesRelations;
    private static Map<String, Integer> filesWeights;
    private static Map<String, Integer> methodsWeights;
    private static Map<String, Integer> packagesWeights;
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

        JButton btn1 = new JButton("Historia 1");
        btn1.addActionListener(e ->  applet.createGraphX(filesRelations, filesWeights, frame));
        panel.add(btn1,BorderLayout.PAGE_START);

        JButton btn2 = new JButton("Historia 2");
        btn2.addActionListener(e -> applet.createGraphX(methodsRelations, methodsWeights, frame));
        panel.add(btn2,BorderLayout.CENTER);

        JButton btn3 = new JButton("Historia 3");
        btn3.addActionListener(e -> applet.createGraphX(packagesRelations, packagesWeights, frame));
        panel.add(btn3,BorderLayout.LINE_END);

        allContent.add(panel, BorderLayout.PAGE_START);
        allContent.add(applet,BorderLayout.CENTER);

        frame = new JFrame();
        frame.setTitle("Projekt - Inżynieria oprogramowania");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(screenSize.width, screenSize.height);
        frame.setResizable(true);
        frame.add(allContent);
        frame.setContentPane(allContent);
        frame.setVisible(true);
    }

    public static Map<String, Map<String, Integer>> getMethodsRelations() {
        return methodsRelations;
    }
}
