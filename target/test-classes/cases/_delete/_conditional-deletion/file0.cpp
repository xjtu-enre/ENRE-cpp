int main() {
    int* ptr = new int;
    if (ptr != nullptr) {
        delete ptr;
    }
}