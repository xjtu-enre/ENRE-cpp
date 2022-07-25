#include <iostream>
int main() {
    using namespace std;
    goto Test2;

    cout << "testing" << endl;

    Test2:
    cerr << "At Test2 label." << endl;

    cout << "testing" << endl;
}