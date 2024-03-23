## Entity: Global Variables

Description: "Modeling the Effects of Global Variables in Data-Flow Analysis for C/C++" Data sets and supplements to the underlying data
### Supported Patterns

```yaml
name: Global Variables
```

#### Syntax: External Global Variable Declaration and Definition
```CPP
//// @ext h
// extern Global var
extern int i;
```
```CPP
#include "file0.h"
// Global var
int i;
```
```yaml
    name: External Global Variable Declaration and Definition
    entity:
        items:
            -   name: i
                type: Variable
                loc: 3:5:3:5
```
#### Syntax: Static Inline Variable Initialization

```CPP
// Global var
static inline int j=1024;
// Static initialization
static int k = 42;
```
```yaml
    name: Static Inline Variable Initialization
    entity:
        items:
            -   name: j
                type: Variable
                loc: 2:19:2:19
            -   name: k
                type: Variable
                loc: 4:12:4:12
```

#### Syntax: Namespace Scoped Global Variables
```CPP
// Global variable in namespace
namespace ns {
    int l = 13;
}
// Anonymous namespace
namespace {
    int m = 9000;
}
```
```yaml
    name: Namespace Scoped Global Variables
    entity:
        items:
            -   name: ns
                type: namespace
            -   name: ns::l
                type: Variable
                loc: 3:9:3:9
            -   name: unnamed
                type: namespace
            -   name: m
                type: Variable
                loc: 8:9:8:9
```

#### Syntax: Static Class Member Definition and Global Variable Initialization
```CPP
//// @ext h
// Declaration of struct Point
struct Point {
    // Class member
    int a, b;
    // Static class member
    static int c;
    // Class constructor
    Point(int a, int b);
};
```

```CPP
#include "file0.h"
// Definition of static class member
int Point::c = 2;
// Global variable for class Point
Point p(42, 13);
```
```yaml
    name: Static Class Member Definition and Global Variable Initialization
    entity:
        items:
            -   name: j
                type: Variable
                loc: 2:19:2:19
```


#### Syntax: Destructor Implementation
```CPP
//// @ext h
// Declaration of struct Point
struct Point {
    // Static class member with inline initialization
    static inline int d = 73;
    // Class destructor
    ~Point();
};
```
```CPP
#include "file0.h"
// Class destructor implementation
Point::~Point() {
    printf("%d", d);
}
```
```yaml
    name: Destructor Implementation
    entity:
        items:
            -   name: j
                type: Variable
                loc: 2:19:2:19
```


#### Syntax: Utilizing Class Functions
```CPP
//// @ext h
// Declaration of struct Point
struct Point {
    // Class member
    int a, b;
    // Class constructor
    Point(int a, int b);
};
// Function declaration for getSingletonPoint()
Point &getSingletonPoint();
```
```CPP
#include "file0.h"
// Definition of getSingletonPoint() function
Point &getSingletonPoint() {
    // Local static variable
    static Point s(11, 22);
    return s;
}
```
```yaml
    name: Utilizing Class Functions
    entity:
        items:
            -   name: j
                type: Variable
                loc: 2:19:2:19
```

#### Syntax: Global Variable Initialization and Cleanup
```CPP
//// @ext h
// extern Global var
extern int i;
```
```CPP
#include "file0.h"
// Global constructor
__attribute__((constructor))
void onLoad() {
    i = 9001;
}
// Global destructor
__attribute__((destructor))
void onUnload() {
    i = 0;
}
```
```yaml
    name: Global Variable Initialization and Cleanup
    entity:
        items:
            -   name: j
                type: Variable
                loc: 2:19:2:19
```

#### Syntax: Class and Function Utilization
```CPP
//// @ext h
// Declaration of struct Point
struct Point {
    // Class member
    int a, b;
    // Class constructor
    Point(int a, int b);
};
Point &getSingletonPoint() {
    // Local static variable
    static Point s(11, 22);
    return s;
}
```
```CPP
#include "file0.h"
// Main function
int main() {
    // Call getSingletonPoint() function
    Point &q = getSingletonPoint();
    return 0;
}
```
```yaml
    name: Class and Function Utilization
    entity:
        items:
            -   name: j
                type: Variable
                loc: 2:19:2:19
```