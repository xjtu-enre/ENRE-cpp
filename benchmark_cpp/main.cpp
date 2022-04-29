
#include <ios>

// Dependency: Include
#include "case.h"

// Entity: Macro
#define F(...) f(0 __VA_OPT__(,) __VA_ARGS__)
#define BOOST_FIXTURE_TEST_SUITE(a, b)
BOOST_FIXTURE_TEST_SUITE(txrequest_tests, BasicTestingSetup)
using namespace std;

extern "C" int printf( const char *fmt, ... );

// Entity: Class
class classA
{
public:
    using flags = std::ios_base::fmtflags;
    template<class T>
    using ptr = T*;
    ptr<int> x;
    void funcA(){
        printf("base class function!");
    }
};

// Entity: Class with final specifier
class BaseClass final
{

};

// Dependency: Extend
class classB : classA
{
public:
    // Dependency: Override
    void funcA(){
        printf("extend class function!");
    }
};

class classC{
    // Dependency: Friend
    friend void classA::funcA();
    friend classB;
    friend class classD;
};

class classD{
    
};

class DbNotFoundError : public std::exception
{
    using std::exception::exception;
};


// Entity: Namespace
namespace foo {
    namespace bar {
        namespace baz {
            int qux = 42;
        }
    }
}

// Entity: anonymous namespace
namespace {
    struct Runner{
        TxRequestTracker txrequest;
    };
}

// Entity: Alias
namespace fbz = foo::bar::baz;

// Entity: Struct
struct MyStruct {
    int value;
};

// Entity: Union
union month
{
    std::uint16_t s[2]; 
    std::uint8_t c;     
};




// Entity: Enum Class
enum class Handle {
    Invalid = 0;
    Valid = 1;
};

enum Month{
    April = 4;
    May = 5;
}

// Entity: Struct Template
template<typename T>
struct A{
    void f() {}
};

// Entity: Function Template
template <typename T>
T copy_object(T& obj) noexcept(std::is_pod<T>) {...}


struct Complex {
    Complex( double r, double i ) : re(r), im(i) {}
    // Entity: Function, operator overloads
    Complex operator+( Complex &other );
    void Display( ) { cout << re << ", " << im << endl; }
    private:
        double re, im;
};


class dog
{
public:
    dog(){
        _legs = 4;
        _bark = true;
    }
    void setDogSize(string dogSize){
        _dogSize = dogSize;
    }
    // Entity: Virtual Function
    virtual void setEars(string type) {
        _earType = type;
    }
private:
    string _dogSize, _earType;
    int _legs;
    bool _bark;
};

class breed : public dog{
public:
    breed( string color, string size){
        _color = color;
        setDogSize(size);
    }
    string getColor(){
        return _color;
    }
    // Entity: Virtual function redefined
    void setEars(string length, string type){
        _earLength = length;
        _earType = type;
    }
protected:
    string _color, _earLength, _earType;
};


class Box {
public:
    // Default constructor
    Box() {}
    // Initialize a Box with equal dimensions (i.e. a cube)
    Box(int i) : Box(i, i, i) // delegating constructor
    {}
    // Initialize a Box with custom dimensions
    Box(int width, int length, int height): m_width(width), m_length(length), m_height(height)
    {}
};

//Dependency: Multi Inheritance/Extend
class Collection {

};
class Book {};
class CollectionOfBook : public Book, public Collection {
    
};

class Buf
{
public:
    Buf( char* szBuffer, size_t sizeOfBuffer );
    Buf& operator=( const Buf & );

    void * operator new[] (size_t) {
        return 0;
    }
    void operator delete[] (void*) {}

    void Display() { cout << buffer << endl; }
private:
    char* buffer;
    size_t sizeOfBuffer;
};
Buf::Buf( char* szBuffer, size_t sizeOfBuffer )
{
    sizeOfBuffer++; // account for a NULL terminator
    buffer = new char[ sizeOfBuffer ];
    if (buffer)
    {
        strcpy_s( buffer, sizeOfBuffer, szBuffer );
        sizeOfBuffer = sizeOfBuffer;
    }
}
Buf& Buf::operator=( const Buf &otherbuf )
{
    if( &otherbuf != this )
    {
        if (buffer)
            delete [] buffer;
        sizeOfBuffer = strlen( otherbuf.buffer ) + 1;
        buffer = new char[sizeOfBuffer];
    strcpy_s( buffer, sizeOfBuffer, otherbuf.buffer );
    }
    return *this;
}

void abssort(float* x, unsigned n) {
    // Lambda expression
    std::sort(x, x + n, [](float a, float b) {
        return (std::abs(a) < std::abs(b));
    });
}

// Entity: Function
int print(string s)
{
    cout << s << endl;
    return cout.good();
}
// Dependency: Overload
int print(double dvalue)
{
    cout << dvalue << endl;
    return cout.good();
}

// Entity: Inline Function
inline double Account::GetBalance()
{
    return balance;
}


int foo(int i, std::string s){
    int value {i};
    MyClass mc;
    if(strcmp(s, "default") != 0)
    {
        value = mc.do_something(i);
    }
    return value;
}


// Entity: Function
int max(int a, int b, int c)
{
    int m = (a > b) ? a : b;
    return (m > c) ? m : c;
}

constexpr float exp(float x, int n)
{
    return n == 0 ? 1 :
    n % 2 == 0 ? exp(x * x, n / 2) :
    exp(x * x, (n - 1) / 2) * x;
};

int main(){
    // Entity: typedef
    typedef int int_t;
    typedef int A[];
    A b = {3,4,5};
    A a = {1, 2};

    int variable;
    variable = 1;

    // Dependency: Modify
    variable++;

    // Entity: typedef
    typedef struct { double hi, lo; } range;
    range z, *zp;

    // Entity: lambda Object
    auto glambda = [](auto a, auto&& b) { return a < b; };

    // Dependency: call
    max(a[0], b[0], b[1]);


    // Dependency: Exception
    MyData md;
    try {
        // Code that could throw an exception
        md = GetNetworkResource();
    }
    catch (const networkIOException& e) {
        cerr << e.what();
    }
    catch (const myDataFormatException& e) {
        cerr << e.what();
    }
    // The following syntax shows a throw expression
    MyData GetNetworkResource()
    {
        if (IOSuccess == false) throw networkIOException("Unable to connect");
        if (readError) throw myDataFormatException("Format error");
    }

}

