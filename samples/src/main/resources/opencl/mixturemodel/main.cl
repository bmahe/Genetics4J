
__kernel void mixtureModelKernel(__global const float *samples,
                                 __global const int *parameters,
                                 __global const int *generationAndPopSize,
                                 __global const float *population,
                                 __global const float *densityDataHelper,
                                 __global float *maximumLogLikelyhoods) {
  int sampleIndex = get_global_id(0);

  int distributionNumParameters = parameters[0];
  int maxPossibleDistributions = parameters[1];
  int chromosomeSize = parameters[2];
  int sampleSize = parameters[3];

  int generation = generationAndPopSize[0];
  int populationSize = generationAndPopSize[1];

  float2 sampleOrig =
      (float2)(samples[2 * sampleIndex], samples[2 * sampleIndex + 1]);

  int basePopulationIndex = 0;
  for (int p = 0; p < populationSize; p++) {
    float2 sample = sampleOrig;

    float mlle = 0;

    float sumAlpha = 0.0f;
    for (int distributionIndex = 0; distributionIndex < chromosomeSize;
         distributionIndex += distributionNumParameters) {
      float alpha = population[basePopulationIndex + distributionIndex + 0];
      sumAlpha += alpha;
    }

    int distributionCounter = 0;
    int distributionValid = 0;
    for (int distributionIndex = 0; distributionIndex < chromosomeSize;
         distributionIndex += distributionNumParameters, distributionCounter++) {
      float alpha = population[basePopulationIndex + distributionIndex + 0] / sumAlpha;
      float meanX = population[basePopulationIndex + distributionIndex + 1];
      float meanY = population[basePopulationIndex + distributionIndex + 2];
      float2 mean = (float2) (meanX, meanY);
      /*
      float covariance_00 = population[basePopulationIndex + distributionIndex + 3];
      float covariance_01 = population[basePopulationIndex + distributionIndex + 4];
      float covariance_10 = population[basePopulationIndex + distributionIndex + 5];
      float covariance_11 = population[basePopulationIndex + distributionIndex + 6];
      */
      int baseDensityDataHelper = p * maxPossibleDistributions * 6 + distributionCounter * 6;
      float dataHelpValid = densityDataHelper[baseDensityDataHelper + 0] ;
      if(dataHelpValid > 2) {

        float covarianceDeterminant = densityDataHelper[baseDensityDataHelper + 1];
        float inverse_covariance_00 = densityDataHelper[baseDensityDataHelper + 2];
        float inverse_covariance_01 = densityDataHelper[baseDensityDataHelper + 3];
        float inverse_covariance_10 = densityDataHelper[baseDensityDataHelper + 4];
        float inverse_covariance_11 = densityDataHelper[baseDensityDataHelper + 5];

        float2 centered = sample - mean;
        float2 centeredXInverse = (float2)(centered.x * inverse_covariance_00 + centered.y * inverse_covariance_10,
                                        centered.x * inverse_covariance_01 + centered.y * inverse_covariance_11);
        float2 centeredXInverseXcentered = centered * centeredXInverse;
        float density = (0.5f * M_1_PI_F) * pow(covarianceDeterminant, -0.5f) * exp(-0.5f * (centeredXInverseXcentered.x + centeredXInverseXcentered.y));
        if(isfinite(density) != 0) {
          mlle += alpha * density;
          distributionValid++;
        }
      }
    }

      maximumLogLikelyhoods[p * sampleSize + sampleIndex] = distributionValid > 0 ? log(mlle) : -100.f;
    basePopulationIndex += chromosomeSize;
  }
}
