int funcA(){
    return -1;
}
int funcB(int i){
    return 0;
}
void run_benchmark(){
    funcB(funcA());
}