package main_package.model;


import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;

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

        Set<Map.Entry<String, Integer>> infoEntrySet = projectInfo.entrySet();
        createVertex(newGraph, infoEntrySet, vertexList, "#add8e6");
        vertexList2 = vertexList;

        Set<Map.Entry<String, Map<String, Integer>>> entrySet = projectStructure.entrySet();
        createEdges(newGraph, entrySet, vertexList, vertexList2, "#add8e6");

        setLayout();
        newGraph.getModel().endUpdate();
        SwingUtilities.updateComponentTreeUI(frame);
    }
    public void createGraphX(Map<String, Map<String, Integer>> projectStructure1, Map<String, Map<String, Integer>> projectStructure2, Map<String, Integer> projectInfo1, Map<String, Integer> projectInfo2, JFrame frame) {
        newGraph.removeCells(newGraph.getChildVertices(newGraph.getDefaultParent()));

        newGraph.getModel().beginUpdate();

        ArrayList<mxCell> vertexList = new ArrayList<>();
        ArrayList<mxCell> vertexList2;

        Set<Map.Entry<String, Integer>> infoEntrySet = projectInfo1.entrySet();
        createVertex(newGraph, infoEntrySet, vertexList, "#add8e6");

        infoEntrySet = projectInfo2.entrySet();
        createVertex(newGraph, infoEntrySet, vertexList, "#ff5252");

        vertexList2 = vertexList;

        Set<Map.Entry<String, Map<String, Integer>>> entrySet = projectStructure1.entrySet();
        createEdges(newGraph, entrySet, vertexList, vertexList2, "#add8e6");

        entrySet = projectStructure2.entrySet();
        createEdges(newGraph, entrySet, vertexList, vertexList2, "#ff5252");

        setLayout();
        newGraph.getModel().endUpdate();
        SwingUtilities.updateComponentTreeUI(frame);
    }
    public void createGraphX(Map<String, Map<String, Integer>> projectStructure1, Map<String, Map<String, Integer>> projectStructure2, Map<String, Map<String, Integer>> projectStructure3, Map<String, Integer> projectInfo1, Map<String, Integer> projectInfo2, Map<String, Integer> projectInfo3, JFrame frame) {
        newGraph.removeCells(newGraph.getChildVertices(newGraph.getDefaultParent()));

        newGraph.getModel().beginUpdate();

        ArrayList<mxCell> vertexList = new ArrayList<>();
        ArrayList<mxCell> vertexList2;

        Set<Map.Entry<String, Integer>> infoEntrySet = projectInfo1.entrySet();
        createVertex(newGraph, infoEntrySet, vertexList, "#add8e6");

        infoEntrySet = projectInfo2.entrySet();
        createVertex(newGraph, infoEntrySet, vertexList, "#ff5252");

        infoEntrySet = projectInfo3.entrySet();
        createVertex(newGraph, infoEntrySet, vertexList, "#98FB98");

        vertexList2 = vertexList;

        Set<Map.Entry<String, Map<String, Integer>>> entrySet = projectStructure1.entrySet();
        createEdges(newGraph, entrySet, vertexList, vertexList2, "#add8e6");

        entrySet = projectStructure2.entrySet();
        createEdges(newGraph, entrySet, vertexList, vertexList2, "#ff5252");

        entrySet = projectStructure3.entrySet();
        createEdges(newGraph, entrySet, vertexList, vertexList2, "#98FB98");

        setLayout();
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

    private void setLayout(){
        mxHierarchicalLayout layout2 = new mxHierarchicalLayout(newGraph);
        layout2.execute(parent);
    }
    private void createEdges(mxGraph myGraph, Set<Map.Entry<String, Map<String, Integer>>> entrySet, ArrayList<mxCell> vertexList1, ArrayList<mxCell> vertexList2, String edgeColor){
        for (Map.Entry<String, Map<String, Integer>> entry : entrySet) {
            Set<Map.Entry<String, Integer>> littleEntrySet = entry.getValue().entrySet();
            for (Map.Entry<String, Integer> littleEntry : littleEntrySet) {
                if (littleEntry.getValue() > 0) {
                    for (mxCell o : vertexList1) {
                        for (mxCell o2 : vertexList2) {
                            if (o.getValue().equals(entry.getKey())) {
                                if (o2.getValue().equals(littleEntry.getKey())) {
                                    myGraph.insertEdge(parent, null, littleEntry.getValue(), o, o2, "strokeColor=" + edgeColor);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private void createVertex(mxGraph myGraph, Set<Map.Entry<String, Integer>> vertexEntrySet, ArrayList<mxCell> vertexList1, String vertexColor){
        for(Map.Entry<String, Integer> entrySet : vertexEntrySet){
            vertexList1.add((mxCell) myGraph.insertVertex(parent, null, entrySet.getKey(), 25, 0, entrySet.getValue()*75, entrySet.getValue()*35, "fillColor=" + vertexColor));
        }
    }
}
