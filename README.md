# nyc-vehicle-accidents-clustering

<p>
  <img src="https://img.shields.io/badge/Scala-%202.12.18-green" alt="alternatetext">
  <img src="https://img.shields.io/badge/Spark-3.3.0-red" alt="alternatetext">
  <img src="https://img.shields.io/badge/Python-3.11.5-blue" alt="alternatetext">
</p>

Implementation of a distributed version of the DBSCAN clustering algorithm for the University of Bologna course of "Scalable and Cloud Programming".
The algorithm is applied and tested on the New York City vehicle accidents [dataset](https://data.cityofnewyork.us/Public-Safety/Motor-Vehicle-Collisions-Crashes/h9gi-nx95) provided by [NYC OpenData](https://data.cityofnewyork.us).

## Implementation
The DBSCAN algorithm is sensible to two input parameters: radius around each point (`epsilon`) and the minimum number of data points that should be around that point within that radius (`minPoints`). The clustering consists in the following phases:
- The algorithm proceeds by arbitrarily picking up a point in the dataset (until all points have been visited).
- If there are at least `minPoints` points within a radius of `epsilon` to the point then we consider all these points to be part of the same cluster.
- The clusters are then expanded by recursively repeating the neighborhood calculation for each neighboring point.
- The complexity of this algorithm is O(N<sup>2</sup>), where N is the number of points.

### Distributed version
In the traditional DBSCAN algorithm, one of the most computationally expensive operations is finding the neighbors of each data point.
The distributed version of this project aims to parallelize this computation over multiple workers and gain scalability through Scala and Spark.
Details of the implementation are explained in the [presentation](...).

## Google Cloud Platform (GCP) Deployment
The distributed algorithm exploits the Google Cloud Platform (GCP) Dataproc (for processing) and Buckets (for storage) services.
A verified GCP account must be created and a payment method added in order to utilize its services.

To build and deploy the current project the following GCP CLI commands (check the installation guide [here](https://cloud.google.com/sdk/docs/install)) must be executed.
Most commands have been included in different bash scripts in the `cli_commands/` folder. If the used operating system doesn't support bash scripts, copy paste the GCP commands and properly ajust the parameters.

### Bucket creation
```bash
gsutil mb -l $REGION gs://$BUCKET_NAME
```
After, edit the script in `cli_commands/` to fit your `$BUCKET` and `$CLUSTER_NAME`.

### Project compilation and storage in previously created bucket
Project JAR executable and dataset loading:
```bash
sbt package clean
```
To load the JAR executable and the dataset to the bucket:
```bash
cli_commands/load_jar.sh
cli_commands/load_data.sh <source_file>

```

### Dataproc cluster creation
```bash
cli_commands/create_cluster.sh
```

### Job submit to the cluster
```
cli_commands/submit_job.sh <filename> <partitions> <eps> <min_points>
```
Where `<filename>` is the name of the dataset saved in the bucket, the `<partitions>` are the number of partitions through which the job
will be distributed (suggested: #WORKERS * #CPU_PER_WORKER). Finally, `<eps>` and `<min_points>` are the algorithm arguments (notice that this version utilizes Haversine formula, therefore `<eps>` is intended in meters).

### Save results
```
cli_commands/save_data.sh <source_file>
```
The results are downloaded in a directory named as the `<soruce_file>`

## Visualization
A Python script provides results visualization on a map. (assuming Python 3.* and dependencies are installed)
```
python3 plot.py <source_file>
```
Where `<source_file>` is the same used when saving job results.




