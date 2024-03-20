## Relation: Delete

Descriptions:`Delete Relation` in C++ represents the deletion of dynamically allocated memory using the `delete` or `delete[]` operators. This relation is crucial to prevent memory leaks and manage resources effectively in a program.

### Supported Patterns

```yaml
name: Delete
```

#### Syntax: Delete Declaration

```text
DeleteDeclaration :
    delete-expression
```

##### Examples

###### Single Object Deletion

```CPP
class MyClass {};

int main() {
    MyClass* obj = new MyClass();
    delete obj;
}
```

```yaml
name: Single Object Deletion
relation:
  type: Delete
  items:
    - from: Function:'main'
      to: Variable:'obj'
      loc: file0:4:11
```

###### Array Deletion

```CPP
int main() {
    int* arr = new int[10];
    delete[] arr;
}
```

```yaml
name: Array Deletion
relation:
  type: Delete
  items:
    - from: Function:'main'
      to: Variable:'arr'
      loc: file0:3:14
```

###### Nested Deletion

```CPP
class Container {
public:
    int* data;
    Container() : data(new int[10]) {}
    ~Container() { delete[] data; }
};

int main() {
    Container* container = new Container();
    delete container;
}
```

```yaml
name: Nested Deletion
relation:
  type: Delete
  items:
    - from: Function:'main'
      to: Variable:'container'
      loc: file0:8:19
    - from: Method:'Container::~Container'
      to: Variable:'data'
      loc: file0:5:24
```

###### Conditional Deletion


```CPP
int main() {
    int* ptr = new int;
    if (ptr != nullptr) {
        delete ptr;
    }
}
```

```yaml
name: Conditional Deletion
relation:
  type: Delete
  items:
    - from: Function:'main'
      to: Variable:'ptr'
      loc: file0:4:16
```

###### Deletion in Exception Handling
```CPP
int main() {
    int* ptr = nullptr;
    try {
        ptr = new int;
        // Some operations
    } catch(...) {
        delete ptr;
        throw;
    }
}
```

```yaml
name: Deletion in Exception Handling
relation:
  type: Delete
  items:
    - from: Catch Block:'main'
      to: Variable:'ptr'
      loc: file0:7:16
```
