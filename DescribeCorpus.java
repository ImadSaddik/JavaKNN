import javax.swing.plaf.IconUIResource;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class DescribeCorpus {
    public static final String CONTINUOUS = "CONTINUOUS";
    public static final String DISCRETE = "DISCRETE";
    public static final String CATEGORICAL = "CATEGORICAL";
    private ArrayList<ArrayList<String>> x = new ArrayList<>();
    private ArrayList<Integer> y = new ArrayList<>();
    public DescribeCorpus(ArrayList<ArrayList<String>> x, ArrayList<Integer> y) {
        this.x = x;
        this.y = y;
    }
    public static ArrayList<String> describeDataset(ArrayList<ArrayList<String>> x) {
        ArrayList<String> corpus_description = new ArrayList<>();


        for (int j = 0 ; j < x.get(0).size() ; j++) {
            boolean isColumnInteger = true;
            for (int i = 0 ; i < x.size() ; i++) {
                try {
                    Integer.parseInt(x.get(i).get(j));
                } catch (Exception e1) {
                    try {
                        Float.parseFloat(x.get(i).get(j));
                        corpus_description.add(j, CONTINUOUS);
                        isColumnInteger = false;
                        break;
                    } catch (Exception e2) {
                        try {
                            if (x.get(i).get(j) instanceof String) {
                                corpus_description.add(j, CATEGORICAL);
                                isColumnInteger = false;
                                break;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
            if (isColumnInteger) corpus_description.add(j, DISCRETE);
        }
        return corpus_description;
    }
}
