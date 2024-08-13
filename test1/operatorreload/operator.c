#include <iostream>

class Complex {
public:
    Complex(double real, double imag) : real_(real), imag_(imag) {}

    // 重载 += 运算符
    Complex& operator+=(const Complex& rhs) {
        addComplex(rhs);
        return *this;
    }

    void print() const {
        std::cout << "Complex(" << real_ << ", " << imag_ << "i)" << std::endl;
    }

private:
    double real_;
    double imag_;

    // 辅助函数，用于实现复数加法
    void addComplex(const Complex& rhs) {
        real_ += rhs.real_;
        imag_ += rhs.imag_;
    }
};

int main() {
    Complex c1(1.0, 2.0);
    Complex c2(3.0, 4.0);

    c1 += c2;

    c1.print(); // 输出 Complex(4.0, 6.0i)

    return 0;
}
