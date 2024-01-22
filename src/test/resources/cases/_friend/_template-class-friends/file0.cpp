template<class T> class A {}; // primary
template<> class A<int> {}; // full
class X {
    friend class A<int>; 
};