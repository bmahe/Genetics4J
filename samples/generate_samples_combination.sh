#!/bin/bash -x


SOURCE_DATA="data-0.csv"
SOURCE_CLUSTERS="clusters-0.csv"

NUMBER_GENERATIONS="500"


for COMBINATION_ARITHMETIC in `seq 0 5`;
do
	 for COMBINATION_CROSSOVER in `seq 0 5`;
	 do

		  OUTPUT_FILE="output-combination-${COMBINATION_ARITHMETIC}-${COMBINATION_CROSSOVER}.csv"

		  ARGS="\
            --output=\"${OUTPUT_FILE}\"                          \
            --source-clusters="${SOURCE_CLUSTERS}"               \
            --source-data="${SOURCE_DATA}"                       \
            --fixed-termination=${NUMBER_GENERATIONS}            \
            --combination-arithmetic=${COMBINATION_ARITHMETIC}   \
            --combination-crossover=${COMBINATION_CROSSOVER}     \
        "

		  mvn compile exec:java \
				-Dexec.cleanupDaemonThreads=false  \
				-Dexec.mainClass="net.bmahe.genetics4j.samples.clustering.Clustering" \
				-Dexec.args="${ARGS}"
	 done
done




