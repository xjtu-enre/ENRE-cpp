## Entity: Variable

Description: `Variable Entity` can be explicitly created by definitions, new-expressions, throw-expressions, changing the active member of a union and evaluating expressions that require temporary Variable.
### Supported Patterns

```yaml
name: Variable
```

#### Syntax: Variable Declaration
```text
- call to following allocating functions, in which case such Variables are created in the allocated storage:
  - operator new
  - operator new[]
  - std::malloc
  - std::calloc
  - std::realloc
  - std::aligned_alloc(since C++17)
- call to following Variable representation copying functions, in which case such Variables are created in the destination region of storage or the result:
  - std::memcpy
  - std::memmove
  - std::bit_cast(since C++20)
```

##### Examples

###### Point Variable
```CPP
int a = 0;
int *p = &a;
```

```yaml
    name: Point Variable
    entity:
        items:
            -   name: p
                type: Variable
                loc: 2:6:2:6
```

###### Allocate space for Pointer
```CPP
int *p = new int;
```

```yaml
    name: Allocate space for Pointer
    entity:
        items:
            -   name: p
                type: Variable
                loc: 1:6:1:6
```

###### Dynamic Array
```CPP
int* parray = new int [10];
```

```yaml
    name: Dynamic Array
    entity:
        items:
            -   name: parray
                type: Variable
                loc: 1:6:1:13
```

###### built-in Variable
```CPP
int x;
```

```yaml
    name: built-in Variable
    entity:
        items:
            -   name: x
                type: Variable
                loc: 1:5:1:5
```

###### Parameter
```CPP
int func(int x){}
```

```yaml
    name: Parameter
    entity:
        items:
            -   name: x
                type: Variable
                loc: 1:14:1:14
```

###### Member Object
```CPP
class c{
private:
    int member;
}
```

```yaml
    name: Member Object
    entity:
        items:
            -   name: member
                type: Variable
                loc: 3:9:3:14
```


###### Global Variable with External Linkage
```CPP
// Declare a global variable with external linkage
extern int globalVar;

// Define the global variable in another file
int globalVar = 42;

int main() {
    // Print the value of the global variable
    std::cout << "globalVar = " << globalVar << std::endl;
    return 0;
}
```

```yaml
name: Global Variable with External Linkage
entity:
  items:
    - name: globalVar
      loc: 5:1:5:16
      type: variable
      storage_class: external
    - name: main
      loc: 7:1:10:2
      type: function
      visibility: public
```

###### Static Variable
```CPP
void increment() {
    static int count = 0;
    count++;
    std::cout << "count = " << count << std::endl;
}

int main() {
    for (int i = 0; i < 5; i++) {
        increment();
    }
    return 0;
}
```

```yaml
name: Static Variable
entity:
  items:
    - name: increment
      loc: 1:1:5:2
      type: function
      visibility: public
    - name: count
      loc: 2:12:2:16
      type: variable
      storage_class: static
    - name: main
      loc: 7:1:11:2
      type: function
      visibility: public
```


###### Auto Keyword
```CPP
#include <iostream>

int main() {
    auto x = 42;
    std::cout << "x = " << x << std::endl;
    return 0;
}
```

```yaml
name: Auto Keyword
entity:
  items:
    - name: main
      loc: 3:1:7:2
      type: function
      visibility: Public
    - name: x
      loc: 4:10:4:11
      type: variable
      storage_class: auto
```

###### Register Keyword
```CPP
int main() {
    register int x = 42;
    std::cout << "x = " << x << std::endl;
    return 0;
}
```

```yaml
name: Register Keyword
entity:
  items:
    - name: main
      loc: 1:1:4:2
      type: function
      visibility: public
    - name: x
      loc: 2:16:2:17
      type: variable
      storage_class: register
```
