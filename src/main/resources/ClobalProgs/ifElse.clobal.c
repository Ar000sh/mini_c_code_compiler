#include <stdio.h>

int n;
int i;
int main() {
  n = 1;
  i = 4;
  if (n<i)
    {n=1;}
  else 
    {i=4;}
    // n = 1, i = 4
    // print 1
  if (n<i)
    printf("%d\n",n);
  else 
    printf("%d\n",i);
    // True
}
