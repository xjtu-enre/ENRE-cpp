# Entity Doc



support entity:

1) file
2) function
3) class/struct/union
4) enum/enumerator
5) class/function template
6) macro
7) variable



- lambda expression

    [ captures ] ( params ) specs requires(optional) { body }	(1)	

    [ captures ] { body }	(2)	(until C++23)

    [ captures ] specs { body }	(2)	(since C++23)

    [ captures ] < tparams > requires(optional) ( params ) specs requires(optional) { body }	(3)	(since C++20)

    [ captures ] < tparams > requires(optional) { body }	(4)	(since C++20) (until C++23)

    [ captures ] < tparams > requires(optional) specs { body }	(4)	(since C++23)

    - captures: getCaptureDefault()获取到的是默认的取值方式，使用 capture-default 时，仅捕获 lambda 正文中提到的变量
        ```
        [&](){n = 10;}();             // BY_REFERENCE
        [=]() mutable {n = 20;}();    // BY_COPY
        [&total](int x){total += x;}  // UNSPECIFIED
        ```
        
    - tparams 为参数列表

    - lambda function的处理方法：
        - 均为[unnamed]形式，具体区分需要依靠define关系的位置
        - lambda function往往与function call相结合，也有可能将自身地址赋值给一个变量，需要结合起来进行判断
        - lambda function本身的属性有：default-captures，parameter

- enum
  - 作用域内枚举 enum class/enum struct

- template

    - 区分class template & function template


## perhaps bugs

1) cdt cannot solve Structured binding declaration - auto (C++ 17), 
