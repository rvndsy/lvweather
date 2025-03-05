#!/bin/bash

javac -d bin/ -cp lib/json.jar:lib/jsoup-1.13.1.jar:lib/mysql-connector-java-5.1.48.jar:lib/swingx-all-1.6.4.jar src/LVWeather/*.java &&
java -cp ./bin:lib/json.jar:lib/jsoup-1.13.1.jar:lib/mysql-connector-java-5.1.48.jar:lib/swingx-all-1.6.4.jar LVWeather.Main
