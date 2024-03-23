class MyClass {
public:
    int data;
};

void processData(int* dataPtr) {
    // Process data
}

void classExample() {
    MyClass obj;
    processData(&obj.data);
}