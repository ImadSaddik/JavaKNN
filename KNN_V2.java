import java.util.*;

public class KNN_V2 {
    private int k;
    private ArrayList<ArrayList<Float>> xTrain;
    private ArrayList<Integer> yTrain;
    private ArrayList<ArrayList<Float>> xTest;
    private ArrayList<Integer> yTest;

    public KNN_V2(int k) {
        this.k = k;
    }

    public void fit(ArrayList<ArrayList<Float>> x, ArrayList<Integer> y, float testSplit) {
        ArrayList<ArrayList<Float>> xList = new ArrayList<>();
        ArrayList<Integer> yList = new ArrayList<>();
        for (int i = 0 ; i < Math.round((1 - testSplit) * x.size()) ; i++) {
            xList.add(x.get(i));
            yList.add(y.get(i));
        }
        this.xTrain = xList;
        this.yTrain = yList;

        xList = new ArrayList<>();
        yList = new ArrayList<>();
        for (int i = Math.round((1 - testSplit) * x.size()) ; i < x.size() ; i++) {
            xList.add(x.get(i));
            yList.add(y.get(i));
        }
        this.xTest = xList;
        this.yTest = yList;
    }

    public ArrayList<Integer> predict(ArrayList<ArrayList<Float>> xTest) {
        ArrayList<Integer> yPredictionList = new ArrayList<>();

        for (int i = 0 ; i < xTest.size() ; i++) {
            HashMap<Float, Integer> distances = new HashMap<>();
            ArrayList<Integer> counterList = fillWithZeros();
            for (int j = 0 ; j < this.xTrain.size() ; j++) {
                distances.put(euclideanDistance(this.xTrain.get(j), xTest.get(i)), j);
            }
            TreeMap<Float, Integer> sortedDistances = sortMap(distances);
            ArrayList<Integer> classesPrediction = new ArrayList<>();
            int counter = 1;
            for (Map.Entry<Float, Integer> entry : sortedDistances.entrySet()) {
                classesPrediction.add(this.yTrain.get(entry.getValue()));
                if (counter == this.k) break;
                counter++;
            }
            for (Integer myClass : classesPrediction) {
                counterList.set(myClass, counterList.get((myClass)) + 1);
            }

            int max = -1;
            int index = -1;
            for (int k = 0 ; k < counterList.size() ; k++) {
                if (max < counterList.get(k)) {
                    max = counterList.get(k);
                    index = k;
                }
            }

            yPredictionList.add(index);
        }

        return yPredictionList;
    }

    public ArrayList<ArrayList<Float>> getxTest() {
        return xTest;
    }

    public ArrayList<Integer> getyTest() {
        return yTest;
    }

    private ArrayList<Integer> fillWithZeros() {
        ArrayList<Integer> array = new ArrayList<>();
        ArrayList<Integer> classArray = new ArrayList<>();

        for (Integer classVal : this.yTrain) {
            if (!classArray.contains(classVal)) {
                classArray.add(classVal);
                array.add(0);
            }
        }

        for (Integer classVal : this.yTest) {
            if (!classArray.contains(classVal)) {
                classArray.add(classVal);
                array.add(0);
            }
        }
        return array;
    }
    private TreeMap<Float, Integer> sortMap(HashMap<Float, Integer> distances) {
        TreeMap<Float, Integer> sortedDistances = new TreeMap<>(new Comparator<Float>() {
            @Override
            public int compare(Float o1, Float o2) {
                return Float.compare(o1, o2);
            }
        });

        sortedDistances.putAll(distances);
        return sortedDistances;
    }

    private float euclideanDistance(ArrayList<Float> xTrainRow, ArrayList<Float> xTestRow) {
        float distance = 0.f;
        for (int i = 0 ; i < xTrainRow.size() ; i++) {
            distance += Math.pow((xTestRow.get(i) - xTrainRow.get(i)), 2);
        }

        return (float) Math.sqrt(distance);
    }
}
