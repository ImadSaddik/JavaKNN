import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        ArrayList<ArrayList<String>> x = new ArrayList<>();
        ArrayList<Integer> y = new ArrayList<>();
        Set<String> classesSet = new HashSet<>();
        load_data(x, y, classesSet);

        ArrayList<ArrayList<String>> xTrain = new ArrayList<>(), xTest = new ArrayList<>();
        ArrayList<Integer> yTrain = new ArrayList<>(), yTest = new ArrayList<>();
        double testSplit = 0.1;
        testTrainSplit(x, y, testSplit, xTrain, xTest, yTrain, yTest);

        ArrayList<String> corpusDescription = DescribeCorpus.describeDataset(x);
        KNN knnClassifier = new KNN(5, KNN.WEIGHTED_VOTING, KNN.AUTO_DISTANCE, corpusDescription);
        knnClassifier.fit(xTrain, yTrain);
        ArrayList<Integer> yPrediction = knnClassifier.predict(xTest);

        int counter = 0;
        for (int i = 0 ; i < yPrediction.size() ; i++) {
            if (yPrediction.get(i) == yTest.get(i)) {
                counter++;
            }
        }

        System.out.println("Accuracy: " + Math.round(counter * 100.0 / yTest.size()) / 100.0);
    }
    private static void testTrainSplit(ArrayList<ArrayList<String>> x,
                                       ArrayList<Integer> y,
                                       double testSplit,
                                       ArrayList<ArrayList<String>> xTrain,
                                       ArrayList<ArrayList<String>> xTest,
                                       ArrayList<Integer> yTrain,
                                       ArrayList<Integer> yTest) {

        for (int i = 0 ; i < Math.round((1 - testSplit) * x.size()) ; i++) {
            xTrain.add(x.get(i));
            yTrain.add(y.get(i));
        }

        for (int i = (int) Math.round((1 - testSplit) * x.size()); i < x.size() ; i++) {
            xTest.add(x.get(i));
            yTest.add(y.get(i));
        }
    }

    private static void load_data(ArrayList<ArrayList<String>> x, ArrayList<Integer> y, Set<String> classesSet) {
        try {
            Scanner scanner = new Scanner(new File("Coropra/ten_features.csv"));
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split(",");
                ArrayList<String> features = new ArrayList<>(Arrays.asList(line).subList(0, line.length - 1));
                x.add(features);
                classesSet.add(line[line.length - 1]);
                y.add(new ArrayList<>(classesSet).indexOf(line[line.length - 1]));
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
