#include <stdio.h>

int n;
int i;
int main() {
  n = 1;
  for (i=0; i<3; i=i+1) {
    n=2*n;
  }
  for (i=0; i<3; i=i+1) {
    n=2*n;
  }
  printf("%d\n",n);
  // True
}
