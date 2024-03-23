class Base {
private:
    void func() {  }
};
class Derived : private Base {
public:
    using Base::func;
};