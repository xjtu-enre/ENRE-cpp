class classA{
public:
    void funcA(){
        printf("base class function!");
    }
};
template<class T> class A {
    friend void classA::funcA();
}; 