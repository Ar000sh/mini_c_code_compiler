#include <stdio.h>
#include <stdbool.h>

int n;
int i;
int main() {
  n = 1;
  i = 4;
  if (true)
    {i=n;}
  if (false)
    printf("%d\n",n);
  else 
    printf("%d\n",i);

    // print 1
    // True
}
