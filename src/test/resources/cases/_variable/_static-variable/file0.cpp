void increment() {
    static int count = 0;
    count++;
    std::cout << "count = " << count << std::endl;
}

int main() {
    for (int i = 0; i < 5; i++) {
        increment();
    }
    return 0;
}