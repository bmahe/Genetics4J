#!/bin/bash -x


SOURCE_DATA="data-0.csv"
SOURCE_CLUSTERS="clusters-0.csv"

NUMBER_GENERATIONS="500"


for RANDOM_MUTATION_RATE in `seq -w 0 10 99`;
do
	 for CREEP_MUTATION_RATE in `seq -w 0 10 99`;
	 do
		  for CREEP_MUTATION_STD_DEV in `seq 0 5 30`;
		  do

				OUTPUT_FILE="output-${RANDOM_MUTATION_RATE}-${CREEP_MUTATION_RATE}-${CREEP_MUTATION_STD_DEV}.csv"

				ARGS="\
					 --output=\"${OUTPUT_FILE}\"                          \
					 --source-clusters="${SOURCE_CLUSTERS}"               \
					 --source-data="${SOURCE_DATA}"                       \
					 --fixed-termination=${NUMBER_GENERATIONS}            \
					 --creep-mutation-rate=0.${CREEP_MUTATION_RATE}       \
					 --creep-mutation-std-dev=${CREEP_MUTATION_STD_DEV}   \
					 --random-mutation-rate=0.${RANDOM_MUTATION_RATE}     \
				"


				mvn compile exec:java \
					 -Dexec.cleanupDaemonThreads=false  \
					 -Dexec.mainClass="net.bmahe.genetics4j.samples.clustering.Clustering" \
					 -Dexec.args="${ARGS}"
		  done
	 done
done





