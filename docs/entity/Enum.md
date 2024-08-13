## Entity: Enum
Description: An `enumeration` is a user-defined type that consists of a set of named integral constants that are known as enumerators.

### Supported Patterns

```yaml
name: Enumeration
```

#### Syntax: Enumeration Declaration
```text
(1) enumeration
enum-key attr(optional) enum-head-name(optional) enum-base(optional) {enumerator-list(optional)	(1)	
enum-key attr(optional) enum-head-name enum-base(optional) ;	(2)	
enum-key can be one of enum, enum class, or enum struct (since C++11).
(2) Scoped enumerations
enum struct|class name { enumerator = constexpr , enumerator = constexpr , ... }	(1)	
enum struct|class name : type { enumerator = constexpr , enumerator = constexpr , ... }	(2)	
enum struct|class name ;	(3)	
enum struct|class name : type ;	(4)	
```
##### Examples


###### Enum Declaration
```CPP
enum A : int;
```

```yaml
    name: Enum Declaration
    entity:
      items:
        -   name: A
            loc: 1:6:1:6
            type: Enum
```

###### Enum
```CPP
enum Color {};
```

```yaml
    name: Enum
    entity:
      items:
        -   name: Color
            loc: 1:6:1:10
            type: Enum
```

###### Enum Class
```CPP
enum class Handle {};
```

```yaml
    name: Enum Class
    entity:
      items:
        -   name: Handle
            loc: 1:12:1:17
            type: Enum
```

###### Enum Struct
```CPP
enum struct Handle {};
```

```yaml
    name: Enum Struct
    entity:
      items:
        -   name: Handle
            loc: 1:12:1:17
            type: Enum
```


###### Typedef Enum
```CPP
typedef enum{
    Monday = 1,
    Tuesday
} Weekday;
```

```yaml
    name: Typedef Enum
    entity:
      items:
        -   name: Weekday
            loc: 4:3:4:9
            type: Enum
```

###### Enums with no enumerators
```CPP
enum class byte : unsigned char { };
enum class E : int { };
E e1{ 0 };
E e2 = E{ 0 };
```

```yaml
    name: Enums with no enumerators
    entity:
      items:
        -   name: byte
            loc: 1:12:1:15
            type: Enum
        -   name: E
            loc: 2:12:2:12
            type: Enum
```