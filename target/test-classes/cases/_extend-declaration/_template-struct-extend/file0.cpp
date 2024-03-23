template<class T>
struct Base<T>{};
struct Derived : public Base<std::string>{};