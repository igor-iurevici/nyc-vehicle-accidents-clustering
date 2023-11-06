#!/bin/bash


if [ "$#" -ne 1 ]; then
  echo "Usage: $0 <source_file>"
  exit 1
fi

SOURCE="$1"
BUCKET="gs://nyc-accidents-bucket/"

save_data="gsutil cp -r ${BUCKET}${SOURCE} ./"

eval $save_data
