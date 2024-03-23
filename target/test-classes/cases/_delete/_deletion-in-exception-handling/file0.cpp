int main() {
    int* ptr = nullptr;
    try {
        ptr = new int;
        // Some operations
    } catch(...) {
        delete ptr;
        throw;
    }
}