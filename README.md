# Nosey Drive

## Background

This was a quick and dirty, spur of the moment task to locate a pesky Wireless Access Point (WAP) that was booming at 2.4 GHz

This repo has 3 primary components:
1. An Android application used to annotate the latitude and longitude during the survey
2. A bash script that spams `iwlist` to get local WAP beacons and their configuration.
3. A Jupyter Notebook used to fuse the output of the first two components and render in a pretty plotly map.

## Android Component

![Android](./Resources/android_app.png){width=300}

The android app requests the location service ever few 200 miliseconds. Once it has the service, it writes the latitude, longitude, and time to a file under downloads.

The application only requires the permission: `android.permission.ACCESS_FINE_LOCATION`

## Bash Component

The bash component is the following script:

```bash
#!/bin/bash

output_file="readings"

while true
do
    echo "$(date)" >> "$output_file"

    sudo iwlist wlp0s20f3 scan >> "$output_file"

    echo "$(date)" >> "$output_file"

    sleep 1
done
```

**Note**: It is unclear if `iwlist` is passive or active. Some online sources indicate it is passive while others indicate it is active.

## Jupyter Component

The jupyter notebook in the `notebook` directory reads in the `location.txt` from the android application and the `readings` file from the bash script and combines them into a pandas dataframe object.

From there, the dataframe is used in conjunction with plotly to create an interative mapping of WAP detections, power, encryption, and location.

![Android](./Resources/wifi_map.png){width=300}