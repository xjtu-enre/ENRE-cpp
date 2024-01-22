class Base {
private:
    int value = 0;
};
class Derived : private Base {
public:
    using Base::value;
};