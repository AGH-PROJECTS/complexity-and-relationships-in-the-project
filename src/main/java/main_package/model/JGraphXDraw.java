package model;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class JGraphXDraw extends JApplet {
    private static Map<String, Map<String, Integer>> fileUsageMap;
    private static Map<String,Long> filesInformation;
    private static final Dimension DEFAULT_SIZE = new Dimension(530, 320);
    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private static JFrame frame = new JFrame();
    private static mxGraph myGraph;
    private static Object parent;
    private mxCircleLayout layout;

    public void createGraphX(Map<String, Map<String, Integer>> filesMap, Map<String, Long> filesInfo) {
        JGraphXDraw applet = new JGraphXDraw();
        fileUsageMap = filesMap;
        filesInformation = filesInfo;
        applet.init();

        JPanel allContent = new JPanel(new BorderLayout());


        JPanel panel = new JPanel(new FlowLayout());
        panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);


        JButton btn1 = new JButton("Historia 1");
        btn1.addActionListener(e ->  {
            myGraph.getModel().beginUpdate();
            history1Graph(myGraph, parent);
            myGraph.getModel().endUpdate();

            SwingUtilities.updateComponentTreeUI(frame);
        });
       // btn1.setPreferredSize(new Dimension(50,50));
        panel.add(btn1,BorderLayout.PAGE_START);

        JButton btn2 = new JButton("Historia 2");
        btn2.addActionListener(e ->  {
            myGraph.getModel().beginUpdate();
            history2Graph(myGraph, parent);
            myGraph.getModel().endUpdate();
            SwingUtilities.updateComponentTreeUI(frame);
        });
       // btn2.setPreferredSize(new Dimension(50,50));
        panel.add(btn2,BorderLayout.CENTER);

        JButton btn3 = new JButton("Historia 3");
        btn3.addActionListener(e ->  {
            myGraph.getModel().beginUpdate();
            history2Graph(myGraph, parent);
            myGraph.getModel().endUpdate();
            SwingUtilities.updateComponentTreeUI(frame);
        });
       // btn3.setPreferredSize(new Dimension(50,50));
        panel.add(btn3,BorderLayout.LINE_END);

        allContent.add(panel, BorderLayout.PAGE_START);
        allContent.add(applet,BorderLayout.CENTER);

        frame = new JFrame();
        frame.setTitle("Inżynieria oprogramowania - rozpoczęcie projektu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(screenSize.width, screenSize.height);
        frame.setResizable(true);
        frame.add(allContent);
        frame.setContentPane(allContent);
        frame.setVisible(true);

    }

    @Override
    public void init() {
        myGraph = new mxGraph();

        parent = myGraph.getDefaultParent();

        mxGraphComponent graphComponent = new mxGraphComponent(myGraph);

        getContentPane().add(graphComponent);

    }
    private void history1Graph(mxGraph myGraph, Object myParent){
        ArrayList<mxCell> vertexList = new ArrayList<>();
        ArrayList<mxCell> vertexList2;

        Set<Map.Entry<String, Map<String, Integer>>> entrySet = fileUsageMap.entrySet();
        for (Map.Entry<String, Map<String, Integer>> entry : entrySet) {
            long size = filesInformation.get(entry.getKey() + ".java");
            vertexList.add((mxCell) myGraph.insertVertex(myParent, null, entry.getKey(), 0, 0, size, size));
        }
        vertexList2 = vertexList;
        for (Map.Entry<String, Map<String, Integer>> entry : entrySet) {
            Set<Map.Entry<String, Integer>> littleEntrySet = entry.getValue().entrySet();
            for (Map.Entry<String, Integer> littleEntry : littleEntrySet) {

                if (littleEntry.getValue() > 0) {
                    for (Object o : vertexList) {
                        for (Object o2 : vertexList2) {
                            if (o.toString().contains(entry.getKey())) {
                                if (o2.toString().contains(littleEntry.getKey())) {
                                    myGraph.insertEdge(myParent, null, littleEntry.getValue(), o, o2);
                                }
                            }
                        }
                    }
                }
            }
        }
        setCircleLayout(myGraph);
    }
    private void history2Graph(mxGraph myGraph, Object myParent){
        ArrayList<mxCell> vertexList = new ArrayList<>();
        ArrayList<mxCell> vertexList2;
        ArrayList<String> allNodes = new ArrayList<>();
        Set<Map.Entry<String, Map<String, Integer>>> entrySet = fileUsageMap.entrySet();

        for (Map.Entry<String, Map<String, Integer>> entry : entrySet) {
            Set<Map.Entry<String, Integer>> littleEntrySet = entry.getValue().entrySet();
            allNodes.add(entry.getKey());
            for(Map.Entry<String, Integer> littleEntry : littleEntrySet){
                allNodes.add(littleEntry.getKey());
            }
        }
        ArrayList<String> uniqueNodes = new ArrayList<>(new HashSet<>(allNodes));

        for(String unique: uniqueNodes){
            vertexList.add((mxCell) myGraph.insertVertex(myParent, null, unique, 0, 0, 50, 50));
        }


        vertexList2 = vertexList;
        for (Map.Entry<String, Map<String, Integer>> entry : entrySet) {
            Set<Map.Entry<String, Integer>> littleEntrySet = entry.getValue().entrySet();
            for (Map.Entry<String, Integer> littleEntry : littleEntrySet) {

                if (littleEntry.getValue() > 0) {
                    for (Object o : vertexList) {
                        for (Object o2 : vertexList2) {
                            if (o.toString().contains(entry.getKey())) {
                                if (o2.toString().contains(littleEntry.getKey())) {
                                    myGraph.insertEdge(myParent, null, littleEntry.getValue(), o, o2);
                                }
                            }
                        }
                    }
                }
            }
        }
        setCircleLayout(myGraph);
    }
    private void setCircleLayout(mxGraph myGraph){
        layout = new mxCircleLayout(myGraph);
        int radius = 100;
        layout.setX0(DEFAULT_SIZE.width/2);
        layout.setY0(DEFAULT_SIZE.height/2);
        layout.setRadius(radius);
        layout.setMoveCircle(true);

        layout.execute(parent);
    }
}
