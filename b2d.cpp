#include <iostream>
#include <math.h>
using namespace std;
/*int main()
{ //decimal to binary
    int n;
    cout << "Enter the number you want to convert" << endl;
    cin >> n;
    cout << "Enter the number you want to convert" << endl;
    cin >> n;
    int ans = 0;
    int i = 0;
    while (n != 0)
    {
        int digit = n & 1;
        ans = (digit * pow(10, i)) + ans;
        n = n >> 1;
        i++;

    }
    cout << "Answer is " << ans << endl;
}*/
int main()
{ // binary to decimal
    int n;
    cout << "Enter the number you want to convert" << endl;
    cin >> n;
    int ans = 0;
    int i = 0;
    while (n != 0)
    {
        int bit = n % 2;
        ans = (bit * pow(2, i)) + ans;
        n = n / 10;
        i++;
    }
    cout << "Answer is " << ans << endl;
}