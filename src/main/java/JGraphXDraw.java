import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DirectedWeightedPseudograph;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import javax.swing.JApplet;
import javax.swing.JFrame;

public class JGraphXDraw extends JApplet {
    private static Map<String, Map<String, Integer>> fileUsageMap;
    private static final Dimension DEFAULT_SIZE = new Dimension(530, 320);
    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    public static void createGraphX(Map<String, Map<String, Integer>> filesMap){
        JGraphXDraw applet = new JGraphXDraw();
        fileUsageMap = filesMap;
        applet.init();

        JFrame frame = new JFrame();
        frame.getContentPane().add(applet);
        frame.setTitle("Inżynieria oprogramowania - rozpoczęcie projektu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(screenSize.width, screenSize.height);
        frame.setResizable(false);
        frame.setVisible(true);
    }
    @Override
    public void init(){
        ListenableGraph<String, DefaultEdge> g =
                new DefaultListenableGraph<>(new DirectedWeightedPseudograph<>(DefaultEdge.class));

        mxGraph graf = new mxGraph();

        Object parent = graf.getDefaultParent();

        ArrayList<Object> vertexList = new ArrayList<>();

        graf.getModel().beginUpdate();
        try
        {
            Set<Map.Entry<String, Map<String, Integer>>> entrySet = fileUsageMap.entrySet();
            for(Map.Entry<String, Map<String, Integer>> entry: entrySet){
                vertexList.add(graf.insertVertex(parent, null, entry.getKey(), 0, 0, 80, 30));
                Set<Map.Entry<String, Integer>> littleEntrySet = entry.getValue().entrySet();
                for(Map.Entry<String, Integer> littleEntry: littleEntrySet){
                    if(littleEntry.getValue() > 0){
                      for(Object o: vertexList){
                          if(o.toString().contains(littleEntry.getKey())){
                              System.out.println(o.toString());
                              graf.insertEdge(parent, null, "Edge", o, o);
                          }
                      }
                    }
                }
            }


        }
        finally
        {
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
