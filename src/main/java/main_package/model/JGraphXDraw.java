package main_package.model;

import com.mxgraph.io.mxCodec;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;

import org.w3c.dom.Node;

import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class JGraphXDraw extends JApplet {
    private static Object parent;
    private mxGraph newGraph;

    public JGraphXDraw() throws HeadlessException {
        init();
    }

    public void createGraphX(Map<String, Map<String, Integer>> projectStructure, Map<String, Integer> projectInfo, JFrame frame) {
        newGraph.removeCells(newGraph.getChildVertices(newGraph.getDefaultParent()));

        newGraph.getModel().beginUpdate();

        ArrayList<mxCell> vertexList = new ArrayList<>();
        ArrayList<mxCell> vertexList2;
        ArrayList<Object> sss = new ArrayList<>();
        System.out.println(projectStructure);
        System.out.println(projectInfo);
        Set<Map.Entry<String, Integer>> infoEntrySet = projectInfo.entrySet();
        for(Map.Entry<String, Integer> infoEntry : infoEntrySet){
            vertexList.add((mxCell) newGraph.insertVertex(parent, null, infoEntry.getKey(), 0, 0, infoEntry.getValue()*45, infoEntry.getValue()*45));
        }

        Set<Map.Entry<String, Map<String, Integer>>> entrySet = projectStructure.entrySet();
        vertexList2 = vertexList;
        for (Map.Entry<String, Map<String, Integer>> entry : entrySet) {
            Set<Map.Entry<String, Integer>> littleEntrySet = entry.getValue().entrySet();
            for (Map.Entry<String, Integer> littleEntry : littleEntrySet) {

                if (littleEntry.getValue() > 0) {
                    for (mxCell o : vertexList) {
                        for (mxCell o2 : vertexList2) {
                            if (o.getValue().equals(entry.getKey())) {
                                if (o2.getValue().equals(littleEntry.getKey())) {
                                    sss.add(newGraph.insertEdge(parent, null, littleEntry.getValue(), o, o2));
                                    System.out.println(sss);
                                }
                            }
                        }
                    }
                }
                System.out.println(littleEntry.getValue());
            }
        }
        exportGraph(newGraph);
        setCircleLayout();
        newGraph.getModel().endUpdate();
        SwingUtilities.updateComponentTreeUI(frame);
    }

    @Override
    public void init() {
        newGraph = new mxGraph();
        parent = newGraph.getDefaultParent();
        mxGraphComponent graphComponent = new mxGraphComponent(newGraph);
        getContentPane().add(graphComponent);
    }

    private void setCircleLayout(){
        mxCircleLayout layout = new mxCircleLayout(newGraph);
        layout.setX0(10);
        layout.setY0(10);
        layout.setMoveCircle(true);

        layout.execute(parent);
    }
    private void exportGraph(mxGraph myGraph){
        mxCodec codec = new mxCodec();
        Node node = codec.encode(myGraph.getModel());
        String xml = mxUtils.getXml(node);
        //System.out.println(xml);
    }

}
