import java.util.*;

public class KNN {
    public static final String WEIGHTED_VOTING = "WEIGHTED";
    public static final String MAJORITY_VOTING = "MAJORITY";
    public static final String EUCLIDEAN = "EUCLIDEAN";
    public static final String MANHATTAN = "MANHATTAN";
    public static final String CHEBYSHEV = "CHEBYSHEV";
    public static final String MINKOWSKI = "MINKOWSKI";
    public static final String CANBERRA = "CANBERRA";
    public static final String AUTO_DISTANCE = "AUTO";
    private int k;
    private double p;
    private String votingMethod;
    private String defaultDistance;
    private ArrayList<ArrayList<String>> xTrain;
    private ArrayList<Integer> yTrain;
    private ArrayList<String> corpusDescription;

    public KNN(int k, String votingMethod, String defaultDistance, ArrayList<String> corpusDescription) {
        this.k = k;
        this.p = 2;
        this.defaultDistance = defaultDistance;
        this.votingMethod = votingMethod;
        this.corpusDescription = corpusDescription;
    }
    public KNN(int k, double p, String votingMethod, String defaultDistance, ArrayList<String> corpusDescription) {
        this.k = k;
        this.p = p;
        this.votingMethod = votingMethod;
        this.defaultDistance = defaultDistance;
        this.corpusDescription = corpusDescription;
    }
    public void fit(ArrayList<ArrayList<String>> xTrain, ArrayList<Integer> yTrain) {
        this.xTrain = xTrain;
        this.yTrain = yTrain;
    }
    public ArrayList<Integer> predict(ArrayList<ArrayList<String>> xTest) {
        ArrayList<Integer> yPrediction = new ArrayList<>();
        ArrayList<ArrayList<String>> xTrainCopy = new ArrayList<>(this.xTrain);
        ArrayList<ArrayList<String>> xTestCopy = new ArrayList<>(xTest);

        stringToNumber(xTrainCopy, xTestCopy);
        // TODO: Add a possibility to normalize the data based on its type
        normalize(xTrainCopy, xTestCopy);
        List<List<List<Double>>> distanceLabelList = computeDistances(xTrainCopy, xTestCopy);
        List<List<List<Double>>> sortedDistanceLabelList = sortDistances(distanceLabelList);

        for (List<List<Double>> distanceFromPointToTrainData : sortedDistanceLabelList) {
            List<List<Double>> kNeighbourPoints = new ArrayList<>();
            for (int i = 0 ; i < k ; i++) {
                kNeighbourPoints.add(distanceFromPointToTrainData.get(i));
            }
            if (this.votingMethod.equals(MAJORITY_VOTING)) {
                yPrediction.add(majorityVoting(kNeighbourPoints));
            } else {
                yPrediction.add(weightedVoting(kNeighbourPoints));
            }
        }
        return yPrediction;
    }
    private Integer weightedVoting(List<List<Double>> kNeighbourPoints) {
        for (List<Double> kNeighbourPoint : kNeighbourPoints) {
            int distanceIndex = 0;
            double weight = 1.0 / kNeighbourPoint.get(distanceIndex);
            kNeighbourPoint.set(distanceIndex, weight);
        }
        Map<Double, Double> weightedSumMap = new HashMap<>();
        for (List<Double> kNeighbourPoint : kNeighbourPoints) {
            int weightIndex = 0;
            int classIndex = 1;
            if (weightedSumMap.containsKey(kNeighbourPoint.get(classIndex))) {
                double newValue = weightedSumMap.get(kNeighbourPoint.get(classIndex)) + kNeighbourPoint.get(weightIndex);
                weightedSumMap.put(kNeighbourPoint.get(classIndex), newValue);
            } else {
                weightedSumMap.put(kNeighbourPoint.get(classIndex), kNeighbourPoint.get(weightIndex));
            }
        }
        double maxWeight = Double.MIN_VALUE;
        Double mostCommonClass = null;
        for (Map.Entry<Double, Double> entry : weightedSumMap.entrySet()) {
            if (entry.getValue() > maxWeight) {
                mostCommonClass = entry.getKey();
                maxWeight = entry.getValue();
            }
        }
        return mostCommonClass.intValue();
    }
    private void stringToNumber(ArrayList<ArrayList<String>> xTrain, ArrayList<ArrayList<String>> xTest) {
        Set<String> columnSet = new HashSet<>();
        ArrayList<ArrayList<String>> xJoined = new ArrayList<>();
        xJoined.addAll(xTrain);
        xJoined.addAll(xTest);

        for (int j = 0 ; j < corpusDescription.size() ; j++) {
            if (!corpusDescription.get(j).equals(DescribeCorpus.CATEGORICAL)) continue;

            for (ArrayList<String> row : xJoined) {
                columnSet.add(row.get(j));
            }
        }
        ArrayList<String> setToList = new ArrayList<>(columnSet);
        for (int j = 0 ; j < corpusDescription.size() ; j++) {
            if (!corpusDescription.get(j).equals(DescribeCorpus.CATEGORICAL)) continue;

            for (ArrayList<String> row : xJoined) {
                row.set(j, String.valueOf(setToList.indexOf(row.get(j))));
            }
        }
    }
    private Integer majorityVoting(List<List<Double>> kNeighbourPoints) {
        Map<Double, Integer> classCounts = new HashMap<>();
        for (List<Double> trainingPoint : kNeighbourPoints) {
            int classIndex = 1;
            Double trainingPointClass = trainingPoint.get(classIndex);
            if (classCounts.containsKey(trainingPointClass)) {
                classCounts.put(trainingPointClass, classCounts.get(trainingPointClass) + 1);
            } else {
                classCounts.put(trainingPointClass, 1);
            }
        }
        Double mostCommonClass = null;
        int maxCount = -1;
        for (Map.Entry<Double, Integer> entry : classCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                mostCommonClass = entry.getKey();
                maxCount = entry.getValue();
            }
        }
        return mostCommonClass.intValue();
    }
    private List<List<List<Double>>> sortDistances(List<List<List<Double>>> distanceLabelList) {
        List<List<List<Double>>> sortedDistanceLabelList = new ArrayList<>();
        for (List<List<Double>> distances : distanceLabelList) {
            List<List<Double>> sortedDistances = new ArrayList<>();
            for (List<Double> distance : distances) {
                sortedDistances.add(new ArrayList<>(distance));
            }
            sortedDistances.sort(new Comparator<List<Double>>() {
                @Override
                public int compare(List<Double> o1, List<Double> o2) {
                    return o1.get(0).compareTo(o2.get(0));
                }
            });
            sortedDistanceLabelList.add(sortedDistances);
        }
        return sortedDistanceLabelList;
    }
    private List<List<List<Double>>> computeDistances(ArrayList<ArrayList<String>> xTrain, ArrayList<ArrayList<String>> xTest) {
        List<List<List<Double>>> distancesList = new ArrayList<>();
        List<List<Double>> distanceLabelList;

        for (int testRowIndex = 0 ; testRowIndex < xTest.size() ; testRowIndex++) {
            distanceLabelList = new ArrayList<>();
            for (int trainRowIndex = 0 ; trainRowIndex < xTrain.size() ; trainRowIndex++) {
                List<Double> distanceLabelPair = new ArrayList<>();
                switch (defaultDistance) {
                    case EUCLIDEAN ->
                            distanceLabelPair.add(euclideanDistance(xTrain.get(trainRowIndex), xTest.get(testRowIndex)));
                    case MANHATTAN ->
                            distanceLabelPair.add(manhattanDistance(xTrain.get(trainRowIndex), xTest.get(testRowIndex)));
                    case CHEBYSHEV ->
                            distanceLabelPair.add(chebyshevDistance(xTrain.get(trainRowIndex), xTest.get(testRowIndex)));
                    case MINKOWSKI ->
                            distanceLabelPair.add(minkowskiDistance(xTrain.get(trainRowIndex), xTest.get(testRowIndex)));
                    case CANBERRA ->
                            distanceLabelPair.add(canberraDistance(xTrain.get(trainRowIndex), xTest.get(testRowIndex)));
                    default -> {
                        double distance = 0;
                        for (int columnIndex = 0; columnIndex < this.corpusDescription.size(); columnIndex++) {
                            ArrayList<String> xTrainCell = new ArrayList<>();
                            ArrayList<String> xTestCell = new ArrayList<>();
                            xTrainCell.add(xTrain.get(trainRowIndex).get(columnIndex));
                            xTestCell.add(xTest.get(testRowIndex).get(columnIndex));

                            if (corpusDescription.get(columnIndex).equals(DescribeCorpus.CONTINUOUS)) {
                                distance += euclideanDistance(xTrainCell, xTestCell);
                            } else {
                                distance += manhattanDistance(xTrainCell, xTestCell);
                            }
                        }
                        distanceLabelPair.add(Math.round(distance * 100.0) / 100.0);
                    }
                }
                distanceLabelPair.add(Double.valueOf(this.yTrain.get(trainRowIndex)));
                distanceLabelList.add(distanceLabelPair);
            }
            distancesList.add(distanceLabelList);
        }
        return distancesList;
    }
    private void normalize(ArrayList<ArrayList<String>> xTrain, ArrayList<ArrayList<String>> xTest) {
        for (int j = 0 ; j < corpusDescription.size() ; j++) {
            if (corpusDescription.get(j).equals(DescribeCorpus.CATEGORICAL)) continue;
            ArrayList<ArrayList<String>> xJoined = new ArrayList<>();
            xJoined.addAll(xTrain);
            xJoined.addAll(xTest);

            double max = getMaxValue(j, xJoined);
            double min = getMinValue(j, xJoined);

            for (int i = 0 ; i < xTrain.size() ; i++) {
                try {
                    double newCellValue = (Double.parseDouble(xTrain.get(i).get(j)) - min) / (max - min);
                    xTrain.get(i).set(j, String.valueOf(Math.round(newCellValue * 100.0) / 100.0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0 ; i < xTest.size() ; i++) {
                try {
                    double newCellValue = (Double.parseDouble(xTest.get(i).get(j)) - min) / (max - min);
                    xTest.get(i).set(j, String.valueOf(Math.round(newCellValue * 100.0) / 100.0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
    private double getMinValue(int j, ArrayList<ArrayList<String>> xJoined) {
        double min = Double.MAX_VALUE;
        for (ArrayList<String> row : xJoined) {
            try {
                double cellValue = Double.parseDouble(row.get(j));
                if (min > cellValue) {
                    min = cellValue;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return min;
    }
    private double getMaxValue(int j, ArrayList<ArrayList<String>> xJoined) {
        double max = Double.MIN_VALUE;
        for (ArrayList<String> row : xJoined) {
            try {
                double cellValue = Double.parseDouble(row.get(j));
                if (max < cellValue) {
                    max = cellValue;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return max;
    }
    private double canberraDistance(ArrayList<String> xTrainRow, ArrayList<String> xTestRow) {
        double distance = 0;
        for (int columnIndex = 0 ; columnIndex < xTrainRow.size() ; columnIndex++) {
            try {
                double numerator = Math.abs(Double.parseDouble(xTestRow.get(columnIndex)) - Double.parseDouble(xTrainRow.get(columnIndex)));
                double denominator = Math.abs(Double.parseDouble(xTestRow.get(columnIndex))) + Math.abs(Double.parseDouble(xTrainRow.get(columnIndex)));
                distance += numerator / denominator;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Math.pow(distance, 1.0 / this.p);
    }
    private double minkowskiDistance(ArrayList<String> xTrainRow, ArrayList<String> xTestRow) {
        double distance = 0;
        for (int columnIndex = 0 ; columnIndex < xTrainRow.size() ; columnIndex++) {
            try {
                distance += Math.pow(Math.abs(Double.parseDouble(xTestRow.get(columnIndex)) - Double.parseDouble(xTrainRow.get(columnIndex))), this.p);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Math.pow(distance, 1.0 / this.p);
    }
    private double chebyshevDistance(ArrayList<String> xTrainRow, ArrayList<String> xTestRow) {
        double maxDifference = Double.MIN_VALUE;
        for (int columnIndex = 0 ; columnIndex < xTrainRow.size() ; columnIndex++) {
            double distance = 0;
            try {
                distance = Math.abs(Double.parseDouble(xTestRow.get(columnIndex)) - Double.parseDouble(xTrainRow.get(columnIndex)));
                if (maxDifference < distance) {
                    maxDifference = distance;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return maxDifference;
    }
    private double euclideanDistance(ArrayList<String> xTrainRow, ArrayList<String> xTestRow) {
        double distance = 0;
        for (int columnIndex = 0 ; columnIndex < xTrainRow.size() ; columnIndex++) {
            try {
                distance += Math.pow((Double.parseDouble(xTestRow.get(columnIndex)) - Double.parseDouble(xTrainRow.get(columnIndex))), 2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Math.sqrt(distance);
    }
    private double manhattanDistance(ArrayList<String> xTrainRow, ArrayList<String> xTestRow) {
        double distance = 0;
        for (int columnIndex = 0 ; columnIndex < xTrainRow.size() ; columnIndex++) {
            try {
                distance += Math.abs(Double.parseDouble(xTestRow.get(columnIndex)) - Double.parseDouble(xTrainRow.get(columnIndex)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return distance;
    }
}
