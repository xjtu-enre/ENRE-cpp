class classA{
public:
    void funcA(){
        printf("base class function!");
    }
};
class classB : classA{};
class classC{
    friend void classA::funcA();
    friend classB;
};