TARGET="/Users/igor/Desktop/nyc-vehicle-accidents-clustering/target/scala-2.12/sacp2023_2.12-3.3.0_0.1.0-SNAPSHOT.jar"
#TARGET="/Users/igor/IdeaProjects/SaCP2023/target/scala-2.12/sacp2023_2.12-3.3.0_0.1.0-SNAPSHOT.jar"
BUCKET="gs://nyc-accidents-bucket/main.jar"

load_jar="gsutil cp $TARGET $BUCKET"

eval $load_jar
