# Include

## Supported pattern
```
name: Source file inclusion
```
### Syntax: 
``` cpp
#include < h-char-sequence > new-line	(1)	
#include " q-char-sequence " new-line	(2)	
#include pp-tokens new-line	(3)	

__has_include ( " q-char-sequence " )
__has_include ( < h-char-sequence > )	(4)	(since C++17)

__has_include ( string-literal )
__has_include ( < h-pp-tokens > )	(5)	(since C++17)
```

#### Examples: 

``` cpp
// in fileA.cpp
#if __has_include(<optional>)
    #include <optional>
    #define has_optional 1
    template<class T> using optional_t = std::optional<T>;
#elif __has_include(<experimental/optional>)
    #include <experimental/optional>
    #define has_optional -1
    template<class T> using optional_t = std::experimental::optional<T>;
#else
    #define has_optional 0
    #include <utility>
```

``` 
entities:
    items:
        -   id: 0
            name: fileA.cpp
            kind: File
        -   id: 1
            name: optional
            kind: File
        -   id: 2
            name: experimental/optional
            kind: File
        -   id: 3
            name: utility
            kind: File
dependencies:
    items:
        -   kind: Include
            src: 0
            dest: 1
        -   kind: Include
            src: 0
            dest: 2
        -   kind: Include
            src: 0
            dest: 3
```

# Reference
- https://en.cppreference.com/w/cpp/preprocessor/include