package main_package.model;


import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;

import org.antlr.v4.runtime.tree.Tree;

import java.awt.HeadlessException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    public void createGraphX(List<Map<String, Map<String, AtomicInteger>>> dataRelationsList, List<Map<String, Integer>> dataWeightsList, Map<String, Integer> methodsComplexity, JFrame frame) {
        if (dataRelationsList == null || dataWeightsList == null) {
            throw new NullPointerException();
        }

        newGraph.removeCells(newGraph.getChildVertices(newGraph.getDefaultParent()));
        newGraph.getModel().beginUpdate();

        Set<mxCell> vertexList = new HashSet<>();
        Set<mxCell> vertexList2;

        int i = 0;
        String graphColor;

        for (Map<String, Integer> projectInfo : dataWeightsList) {
            vertexList.clear();
            if (i == 0) {
                graphColor = "#9dcfe1";
            } else if (i == 1) {
                graphColor = "#ff6666";
            } else {
                graphColor = "#6cf96c";
            }
            Set<Map.Entry<String, Integer>> infoEntrySet = projectInfo.entrySet();
            createVertex(newGraph, infoEntrySet, vertexList, graphColor);
            vertexList2 = vertexList;

            for (Map<String, Map<String, AtomicInteger>> projectStructure : dataRelationsList) {
                Set<Map.Entry<String, Map<String, AtomicInteger>>> entrySet = projectStructure.entrySet();
                createEdges(newGraph, entrySet, vertexList, vertexList2, graphColor, graphColor);
            }
            i++;
        }
        writeComplexity(newGraph, methodsComplexity);
        setLayout();
        newGraph.getModel().endUpdate();
        SwingUtilities.updateComponentTreeUI(frame);
    }

    public void createGraphX(Map<String, String> graphStructure, JFrame frame) {
        if (graphStructure == null) {
            throw new NullPointerException();
        }
        newGraph.removeCells(newGraph.getChildVertices(newGraph.getDefaultParent()));

        newGraph.getModel().beginUpdate();

        Set<mxCell> vertexMethods = new HashSet<>();
        Set<String> vertexFilesNames = new HashSet<>();
        Set<mxCell> vertexFiles = new HashSet<>();

        Set<Map.Entry<String, String>> entrySet = graphStructure.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            vertexFilesNames.add(entry.getValue());
        }
        for (Map.Entry<String, String> entry : entrySet) {
            if (entry.getKey().contains("*NEW*")) {
                vertexMethods.add((mxCell) newGraph.insertVertex(parent, null, entry.getKey(), 25, 0, 100, 50, "fillColor=#368fb0"));
            } else {
                vertexMethods.add((mxCell) newGraph.insertVertex(parent, null, entry.getKey(), 25, 0, 100, 50, "fillColor=#9dcfe1"));
            }
        }
        for (String vertexName : vertexFilesNames) {
            vertexFiles.add((mxCell) newGraph.insertVertex(parent, null, vertexName, 25, 0, 100, 50));
        }
        for (Map.Entry<String, String> entry : entrySet) {
            for (mxCell o : vertexFiles) {
                for (mxCell o2 : vertexMethods) {
                    if (o.getValue().equals(entry.getValue())) {
                        if (o2.getValue().equals(entry.getKey())) {
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

    private void setLayout() {
        mxHierarchicalLayout layout2 = new mxHierarchicalLayout(newGraph);
        layout2.setIntraCellSpacing(75);
        layout2.execute(parent);
    }

    private void createEdges(mxGraph myGraph, Set<Map.Entry<String, Map<String, AtomicInteger>>> entrySet, Set<mxCell> vertexList1, Set<mxCell> vertexList2, String edgeColor, String graphId) {
        for (Map.Entry<String, Map<String, AtomicInteger>> entry : entrySet) {
            Set<Map.Entry<String, AtomicInteger>> littleEntrySet = entry.getValue().entrySet();
            for (Map.Entry<String, AtomicInteger> littleEntry : littleEntrySet) {
                if (littleEntry.getValue().get() > 0) {
                    for (mxCell o : vertexList1) {
                        for (mxCell o2 : vertexList2) {
                            if (o.getValue().equals(entry.getKey())) {
                                if (o2.getValue().equals(littleEntry.getKey())) {
                                    myGraph.insertEdge(parent, graphId, littleEntry.getValue(), o, o2, "strokeColor=" + edgeColor);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void createVertex(mxGraph myGraph, Set<Map.Entry<String, Integer>> vertexEntrySet, Set<mxCell> vertexList1, String vertexColor) {
        String basicColor = vertexColor;
        for (Map.Entry<String, Integer> entry : vertexEntrySet) {
            if (entry.getKey().contains("*NEW*")) {
                if (vertexColor.contains("#9dcfe1"))
                    vertexColor = "#368fb0";
                if (vertexColor.contains("#ff6666"))
                    vertexColor = "#ff1a1a";
                if (vertexColor.contains("#6cf96c"))
                    vertexColor = "#23f623";
            } else {
                vertexColor = basicColor;
            }
            vertexList1.add((mxCell) myGraph.insertVertex(parent, null, entry.getKey(), 25, 0, entry.getValue() * 75, entry.getValue() * 35, "fillColor=" + vertexColor));
        }
    }

    private void writeComplexity(mxGraph myGraph, Map<String, Integer> methodsComplexity) {
        Object[] graphVertexes = myGraph.getChildVertices(myGraph.getDefaultParent());
        for (Object graphVertex : graphVertexes) {
            if (((mxCell) graphVertex).getValue().toString().contains("*NEW*")) {
                String[] parts = ((mxCell) graphVertex).getValue().toString().split("\\*");
                if (methodsComplexity.containsKey(parts[2])) {
                    String vertexInformation = "*NEW*" + parts[2] +
                            "\n" +
                            "Complexity: " +
                            methodsComplexity.get(parts[2]);
                    ((mxCell) graphVertex).setValue(vertexInformation);
                }
            } else {
                if (methodsComplexity.containsKey(((mxCell) graphVertex).getValue().toString())) {
                    String vertexInformation = ((mxCell) graphVertex).getValue().toString() +
                            "\n" +
                            "Complexity: " +
                            methodsComplexity.get(((mxCell) graphVertex).getValue().toString());
                    ((mxCell) graphVertex).setValue(vertexInformation);
                }
            }
        }
    }
}
