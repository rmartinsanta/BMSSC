# [Strategic oscillation for the balanced minimum sum-of-squares clustering problem](https://doi.org/10.1016/j.ins.2021.11.048)

## Abstract 
In the age of connectivity, every person is constantly producing large amounts of data every minute: social networks, information about trips, work connections, etc. These data will only become useful information if we are able to analyze and extract the most relevant features from it, which depends on the field of analysis. This task is usually performed by clustering data into similar groups with the aim of finding similarities and differences among them. However, the vast amount of data available makes traditional analysis obsolete for real-life datasets. This paper addresses the problem of dividing a set of elements into a predefined number of equally-sized clusters. In order to do so, we propose a Strategic Oscillation approach combined with a Greedy Randomized Adaptive Search Procedure. The computational experiments section firstly tunes the parameters of the algorithm and studies the influence of the proposed strategies. Then, the best variant is compared with the current state-of-the-art method over the same set of instances. The obtained results show the superiority of the proposal using two different clustering metrics: MSE (Mean Square Error) and Davies-Bouldin index.


## Authors
Raúl Martín Santamaría
Jesus Sánchez-Oro Calvo
Sergio Pérez Peló
Abraham Duarte Muñoz

## Datasets

Instances are categorized in different datasets inside the 'resources/instances' folder. All instances are from the [UCI Machine Learning Repository](https://archive.ics.uci.edu/ml/index.php)

## Executing

You can just run the BMMSC.jar as follows. For easy of use there is an already compiled JAR inside the target folder.

```
java -jar BMSSC.jar indexfile
```

Example: running with only new proposed instances:
```
java -jar BMSSC.jar resources/instances/new/index
```

The index file contains a list of the instances to solve, with all the required data. Example:

```
##########################################################
#                                                        #
#            ALGORITHM INPUT INSTANCES                   #
#    folder/instancefile,nPoints,nDimensions,nClusters   #
##########################################################

body.csv,507,5,2
breast_cancer.csv,569,30,2
glass.csv,214,10,7
glass.csv,214,9,7
image_segmentation.csv,2310,19,7
ionosphere.csv,351,34,2
iris.csv,150,4,3
libra.csv,360,90,15
multiple_features_reduced.csv,2000,240,7
synthetic_control.csv,600,60,6
thyroid.csv,215,5,3
user_knowledge.csv,403,5,4
vehicle.csv,846,18,6
vowel.csv,871,3,3
water.csv,527,38,13
wine.csv,178,13,3
yeast.csv,1484,8,10
```

There are two indexfiles already present in the instances folder, one for the new instances and another one for the instances used previously in the LIMA proposal. 

## Cite

Consider citing our paper if used in your own work:

### DOI
https://doi.org/10.1016/j.ins.2021.11.048

### Bibtex
```bibtex
@article{MARTINSANTAMARIA2022529,
title = {Strategic oscillation for the balanced minimum sum-of-squares clustering problem},
journal = {Information Sciences},
volume = {585},
pages = {529-542},
year = {2022},
issn = {0020-0255},
doi = {https://doi.org/10.1016/j.ins.2021.11.048},
url = {https://www.sciencedirect.com/science/article/pii/S0020025521011701},
author = {R. Martín-Santamaría and J. Sánchez-Oro and S. Pérez-Peló and A. Duarte},
keywords = {Balanced clustering, Metaheuristics, Strategic oscillation, GRASP, Infeasibility},
abstract = {In the age of connectivity, every person is constantly producing large amounts of data every minute: social networks, information about trips, work connections, etc. These data will only become useful information if we are able to analyze and extract the most relevant features from it, which depends on the field of analysis. This task is usually performed by clustering data into similar groups with the aim of finding similarities and differences among them. However, the vast amount of data available makes traditional analysis obsolete for real-life datasets. This paper addresses the problem of dividing a set of elements into a predefined number of equally-sized clusters. In order to do so, we propose a Strategic Oscillation approach combined with a Greedy Randomized Adaptive Search Procedure. The computational experiments section firstly tunes the parameters of the algorithm and studies the influence of the proposed strategies. Then, the best variant is compared with the current state-of-the-art method over the same set of instances. The obtained results show the superiority of the proposal using two different clustering metrics: MSE (Mean Square Error) and Davies-Bouldin index.}
}
```
