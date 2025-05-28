#!/bin/bash

output_file="readings"

while true
do
    echo "$(date)" >> "$output_file"

    sudo iwlist wlp0s20f3 scan >> "$output_file"

    echo "$(date)" >> "$output_file"

    sleep 1
done
