ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.18"

lazy val root = (project in file("."))
  .settings(
    name := "SaCP2023",
    libraryDependencies += "org.apache.spark" %% "spark-core" % "3.3.0",
    libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.3.0",
    Compile / mainClass := Some("dbscan_distributed.Main"),
    artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
      artifact.name + "_" + sv.binary + "-" + "3.3.0" + "_" + module.revision + "." + artifact.extension
    }
  )