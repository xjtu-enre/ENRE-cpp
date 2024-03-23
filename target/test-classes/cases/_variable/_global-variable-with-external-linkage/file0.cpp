// Declare a global variable with external linkage
extern int globalVar;

// Define the global variable in another file
int globalVar = 42;

int main() {
    // Print the value of the global variable
    std::cout << "globalVar = " << globalVar << std::endl;
    return 0;
}