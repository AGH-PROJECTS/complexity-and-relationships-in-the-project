package main_package.model;


import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;

import org.antlr.v4.runtime.tree.Tree;

import java.awt.HeadlessException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class JGraphXDraw extends JApplet {
    private static Object parent;
    private mxGraph newGraph;

    public mxGraph getNewGraph() {
        return newGraph;
    }

    public JGraphXDraw() throws HeadlessException {
        init();
    }
    public void createGraphX(List<Map<String, Map<String, AtomicInteger>>> dataRelationsList, List<Map<String, Integer>> dataWeightsList, JFrame frame) {
        if(dataRelationsList == null || dataWeightsList == null){
            throw new NullPointerException();
        }

        newGraph.removeCells(newGraph.getChildVertices(newGraph.getDefaultParent()));

        newGraph.getModel().beginUpdate();

        ArrayList<mxCell> vertexList = new ArrayList<>();
        ArrayList<mxCell> vertexList2;
        int i=0;
        String graphColor;
        for(Map<String, Integer> projectInfo: dataWeightsList){
            vertexList.clear();

            if(i == 0){
                graphColor = "#add8e6";
            }
            else if(i == 1){
                graphColor = "#ff5252";
            }
            else{
                graphColor = "#98FB98";
            }
            Set<Map.Entry<String, Integer>> infoEntrySet = projectInfo.entrySet();
            createVertex(newGraph, infoEntrySet, vertexList, graphColor);
            vertexList2 = vertexList;

            for(Map<String, Map<String, AtomicInteger>> projectStructure: dataRelationsList){
                Set<Map.Entry<String, Map<String, AtomicInteger>>> entrySet = projectStructure.entrySet();
                createEdges(newGraph, entrySet, vertexList, vertexList2, graphColor);
            }
            i++;
        }

        setLayout();
        newGraph.getModel().endUpdate();
        SwingUtilities.updateComponentTreeUI(frame);
    }
    public void createGraphX(Map<String, String> graphStructure, JFrame frame){
        newGraph.removeCells(newGraph.getChildVertices(newGraph.getDefaultParent()));

        newGraph.getModel().beginUpdate();

        Set<mxCell> vertexMethods = new HashSet<>();
        Set<String> vertexFilesNames = new HashSet<>();
        Set<mxCell> vertexFiles = new HashSet<>();

        Set<Map.Entry<String, String>> entrySet = graphStructure.entrySet();
        for(Map.Entry<String, String> entry: entrySet){
            vertexFilesNames.add(entry.getValue());
        }
        for(Map.Entry<String, String> entry: entrySet) {
            if(entry.getKey().contains("*NEW*")) {
                vertexMethods.add((mxCell) newGraph.insertVertex(parent, null, entry.getKey(), 25, 0, 100, 50, "fillColor=#e6c35c"));
            } else {
                vertexMethods.add((mxCell) newGraph.insertVertex(parent, null, entry.getKey(), 25, 0, 100, 50, "fillColor=#add8e6"));
            }
        }
        for(String vertexName: vertexFilesNames){
            vertexFiles.add((mxCell) newGraph.insertVertex(parent, null, vertexName, 25, 0,100,50));
        }
        for(Map.Entry<String, String> entry: entrySet) {
            for(mxCell o : vertexFiles){
                for(mxCell o2: vertexMethods){
                    if(o.getValue().equals(entry.getValue())){
                        if(o2.getValue().equals(entry.getKey())){
                            newGraph.insertEdge(parent, null, "", o2, o);
                        }
                    }
                }
            }
        }
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
    private void createEdges(mxGraph myGraph, Set<Map.Entry<String, Map<String, AtomicInteger>>> entrySet, ArrayList<mxCell> vertexList1, ArrayList<mxCell> vertexList2, String edgeColor){
        for (Map.Entry<String, Map<String, AtomicInteger>> entry : entrySet) {
            Set<Map.Entry<String, AtomicInteger>> littleEntrySet = entry.getValue().entrySet();
            for (Map.Entry<String, AtomicInteger> littleEntry : littleEntrySet) {
                if (littleEntry.getValue().get() > 0) {
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
        for(Map.Entry<String, Integer> entry : vertexEntrySet){
            if(entry.getKey().contains("*NEW*")){
                vertexList1.add((mxCell) myGraph.insertVertex(parent, null, entry.getKey(), 25, 0, entry.getValue()*75, entry.getValue()*35, "fillColor=#e6c35c"));
            } else {
                vertexList1.add((mxCell) myGraph.insertVertex(parent, null, entry.getKey(), 25, 0, entry.getValue() * 75, entry.getValue() * 35, "fillColor=" + vertexColor));
            }
        }
    }
}
