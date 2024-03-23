void modify(int* ptr) {}

int main() {
    const int val = 10;
    modify(const_cast<int*>(&val));
}