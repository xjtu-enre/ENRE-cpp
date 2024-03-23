template<typename S>
int func(S &s){
    return 0;
}
template<typename S>
void func2(S &s){
    func(s);
}