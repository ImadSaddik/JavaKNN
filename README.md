# K-Nearest Neighbors (KNN) Implementation in Java
This is a Java implementation of the K-Nearest Neighbors (KNN) algorithm, developed as a school project. The KNN algorithm is a widely used machine learning technique for classification and regression tasks. It is a simple yet effective algorithm that predicts the class of a given input based on its proximity to the k nearest neighbors in the training dataset.

# Features
KNN Algorithm: The implementation includes the core KNN algorithm that classifies input samples based on their neighbors' labels.
Flexible Distance Metric: You can choose from various distance metrics, such as Euclidean, Manhattan, Chebyshev, Minkowski, Canberra or choose auto to let the algorithm deside on which distance metric to use based on the type of features.
K-Fold Cross-Validation: Right now, the implementation doesn't support K-Fold cross-validation for evaluating the performance of the KNN classifier on different subsets of the training data.
Scalable: The implementation is slow when working with large datasets.

# Usage
To use this KNN implementation in your Java project, follow these steps:

1. Clone the repository or download the source code.
2. Import the project into your Java IDE.
3. Create an instance of the KNN classifier, specifying the desired parameters.
4. Load your training dataset into the classifier using the fit() method.
5. Provide a set of input samples for classification using the predict() method.
6. Retrieve the predicted labels for the input samples.
7. Evaluate the performance of the classifier using evaluation metrics like accuracy or precision (these metrics should be programmed manually).
