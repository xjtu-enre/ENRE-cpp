void modifyValue(int* ptr) {
    *ptr = 10;
}

void example() {
    int value = 5;
    modifyValue(&value);
}