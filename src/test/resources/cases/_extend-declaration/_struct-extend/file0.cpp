struct Base {
    int a, b, c;
};
struct Derived : Base {
    int b;
};
struct Derived2 : Derived {
    int c;
};