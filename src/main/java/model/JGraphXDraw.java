package model;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import javax.swing.JApplet;
import javax.swing.JFrame;
//Main
public class JGraphXDraw extends JApplet {
    private static Map<String, Map<String, Integer>> fileUsageMap;
    private static Map<String,Long> filesInformation;
    private static final Dimension DEFAULT_SIZE = new Dimension(530, 320);
    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    public static void createGraphX(Map<String, Map<String, Integer>> filesMap, Map<String,Long> filesInfo) {
        JGraphXDraw applet = new JGraphXDraw();
        fileUsageMap = filesMap;
        filesInformation = filesInfo;
        applet.init();

        JFrame frame = new JFrame();
        frame.getContentPane().add(applet);
        frame.setTitle("Inżynieria oprogramowania - rozpoczęcie projektu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(screenSize.width, screenSize.height);
        frame.setResizable(true);
        frame.setVisible(true);
    }

    @Override
    public void init() {
        mxGraph graf = new mxGraph();

        Object parent = graf.getDefaultParent();

        ArrayList<mxCell> vertexList = new ArrayList<>();
        ArrayList<mxCell> vertexList2;


        graf.getModel().beginUpdate();
        try {
            Set<Map.Entry<String, Map<String, Integer>>> entrySet = fileUsageMap.entrySet();
            for (Map.Entry<String, Map<String, Integer>> entry : entrySet) {
                long size = filesInformation.get(entry.getKey() + ".java");
                vertexList.add((mxCell) graf.insertVertex(parent, null, entry.getKey(), 0, 0, size*2, size*2));
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
                                        graf.insertEdge(parent, null, littleEntry.getValue(), o, o2);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } finally {
            graf.getModel().endUpdate();
        }
        mxGraphComponent graphComponent = new mxGraphComponent(graf);
        mxCircleLayout layout = new mxCircleLayout(graf);
        int radius = 100;
        layout.setX0((DEFAULT_SIZE.width / 2.0) - radius);
        layout.setY0((DEFAULT_SIZE.height / 2.0) - radius);
        layout.setRadius(radius);
        layout.setMoveCircle(true);

        layout.execute(graf.getDefaultParent());

        getContentPane().add(graphComponent);

    }

}
