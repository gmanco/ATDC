# ATDC
Java source code for the ATDC algorithm. This is an implementation of the Top-Down Parameter-free Clustering algorihtm. 
The algorithm implements a parameter-free, fully-automatic approach to clustering high-dimensional categorical data. 
The algorithm is based on a two-phase iterative procedure, which attempts to improve the overall quality of the whole partition. 
In the first phase, cluster assignments are given, and a new cluster is added to the partition by identifying and splitting a 
low-quality cluster. In the second phase, the number of clusters is fixed, and an attempt to optimize cluster assignments is done. 
On the basis of such features, the algorithm attempts to improve the overall quality of the whole partition and finds clusters in 
the data, whose number is naturally established on the basis of the inherent features of the underlying data set rather than 
being previously specified.
For details see

Eugenio Cesario, Giuseppe Manco, Riccardo Ortale, "Top-Down Parameter-Free Clustering of High-Dimensional Categorical Data", IEEE Transactions on Knowledge & Data Engineering, vol.19, no. 12, pp. 1607-1624, December 2007, doi:10.1109/TKDE.2007.190649




