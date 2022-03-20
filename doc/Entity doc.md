# Doc


## Entity 
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
    - 把template变成了DataAggregateEntity的一个属性，判断时只往父类节点找一层，判断struct，class，function三种实体是否为template属性的实体，在输出时判断是否具有template属性，加上一层名字
    - deal with specialization(具体化)
        - explicit specialization
        - partial specialization
    - TODO：依赖型模板（https://www.cnblogs.com/yyxt/p/5150449.html）


- typedef
    - declSpecifier = declaration.getSpecifier()
    - declSpecifier.getStorageClass() == IASTDeclSpecifier.sc_typedef

- function
    - operator 重载：去掉多余的空格


### Understand perhaps bugs

1) und class name final-> final is just a specifier 是说这个类不能派生的，understand在这里把它当成了类的名字

2) enum class -> 其实只是给没有作用域的enum类规定了作用域，understand在这里把它当成了class来判断


## perhaps bugs

1) cdt cannot solve Structured binding declaration - auto (C++ 17), 


