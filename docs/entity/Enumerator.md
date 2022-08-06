## Entity:  Enumerator

Description: Enumeration consist of a set of named integral constants that are known as `enumerators`.

### Supported Patterns

```yaml
name: Enumerator 
```
#### Syntax: Enumerator Declaration

```text
enum-key attr(optional) enum-head-name(optional) enum-base(optional) {enumerator-list(optional)	(1)	
enum-key attr(optional) enum-head-name enum-base(optional) ;	(2)	
enum-key can be one of enum, enum class, or enum struct (since C++11).
```

##### Examples

###### Enumerator

```cpp
    enum Color { 
        red, 
        green, 
        blue 
    };
```

```yaml
    name: Enumerator
    entity:
        items:
            -   name: Color::red
                loc: 2:9:2:11
                type: Enumerator
            -   name: Color::green
                loc: 2:9:2:12
                type: Enumerator
```