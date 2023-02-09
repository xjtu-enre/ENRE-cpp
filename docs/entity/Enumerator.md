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

###### Normal Enumerator

```cpp
    enum Color { 
        red, 
        green, 
        blue 
    };
```

```yaml
    name: Normal Enumerator
    entity:
        items:
            -   name: Color::red
                loc: 2:9:2:11
                type: Enumerator
            -   name: Color::green
                loc: 3:9:3:13
                type: Enumerator
            -   name: Color::blue
                loc: 4:9:4:12
                type: Enumerator
```

###### Enumerator with default value

```cpp
enum DAY {
    saturday,
    sunday = 0,
    monday,
    tuesday,
    wednesday, 
    thursday,
    friday
} workday;
```

```yaml
    name: Enumerator with default value
    entity:
        items:
            -   name: DAY::saturday
                loc: 2:5:2:12
                type: Enumerator
            -   name: DAY::sunday
                loc: 3:5:3:10
                type: Enumerator
            -   name: DAY::monday
                loc: 4:5:4:10
                type: Enumerator
            -   name: DAY::tuesday
                loc: 5:5:5:11
                type: Enumerator
            -   name: DAY::wednesday
                loc: 6:5:6:13
                type: Enumerator
            -   name: DAY::thursday
                loc: 7:5:7:12
                type: Enumerator
            -   name: DAY::friday
                loc: 8:5:8:10
                type: Enumerator
```


###### Enumerator in enum class

```cpp
namespace CardGame_Scoped
{
    enum class Suit { Diamonds, Hearts, Clubs, Spades };
}
```

```yaml
    name: Enumerator in enum class
    entity:
        items:
            -   name: Diamonds
                loc: 2:23:2:30
                type: Enumerator
            -   name: Hearts
                loc: 2:33:2:38
                type: Enumerator
            -   name: Clubs
                loc: 2:41:2:45
                type: Enumerator
            -   name: Spades
                loc: 2:48:2:53
                type: Enumerator
```