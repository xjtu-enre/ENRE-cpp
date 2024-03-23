class Container {
public:
    int* data;
    Container() : data(new int[10]) {}
    ~Container() { delete[] data; }
};

int main() {
    Container* container = new Container();
    delete container;
}