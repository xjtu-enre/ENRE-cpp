## Relation: Cast

Descriptions: `Cast Relation` represents the explicit type conversion from one data type to another in C++ programming. Cast operations are commonly used for converting variable types to make them compatible with other types in expressions or function calls.

### Supported Patterns

```yaml
name: Cast
```

#### Syntax: Cast    Declaration

```text
CastDeclaration :
    cast-expression
```

##### Examples

###### Static Cast

```CPP
int main() {
    float f = 3.14;
    int i = static_cast<int>(f);
}
```

```yaml
name: Static Cast
relation:
  type: Cast
  items:
    - from: Function:'main'
      to: Variable:'main::f'
      loc: file0:3:13
```

###### Dynamic Cast

```CPP
class Base {};
class Derived : public Base {};

int main() {
    Base* b = new Derived();
    Derived* d = dynamic_cast<Derived*>(b);
}
```

```yaml
name: Dynamic Cast
relation:
  type: Cast
  items:
    - from: Function:'main'
      to: Variable:'main::b'
      loc: file0:6:18
```

###### Reinterpret Cast

```CPP
int main() {
    int i = 42;
    char* c = reinterpret_cast<char*>(&i);
}
```

```yaml
name: Reinterpret Cast
relation:
  type: Cast
  items:
    - from: Function:'main'
      to: Variable:'main::i'
      loc: file0:3:25
```

###### C-style Cast
```CPP
int main() {
  double d = 3.14;
  int i = (int)d;
}
```

```yaml
name: C-style Cast
relation:
  type: Cast
  items:
    - from: Function:'main'
      to: Variable:'main::d'
      loc: file0:3:1
```