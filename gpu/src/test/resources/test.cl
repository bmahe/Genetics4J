
__kernel void sampleKernel(__global const float *input, __global const int *size, __global float *output) {
  int x = get_global_id(0);

  int lSize = size[0];

  int start = x * lSize;
  int end = start + lSize;
  float result = 0;
  for (int i = start ; i < end; i++) {
      result += input[i];
  }


  output[x] = (50 - result) * (50 - result) ;
}
