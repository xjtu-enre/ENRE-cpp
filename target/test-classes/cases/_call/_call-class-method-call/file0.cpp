class A{
public:
    int func(){
        printf("Print test");
    }
}
int main(){
    A test = new A();
    test.func();
}