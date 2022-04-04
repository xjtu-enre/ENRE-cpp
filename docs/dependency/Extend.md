# Extend

## Supported pattern
```
name: Extend Declaration
```
### Syntax: Catch Exception

#### Examples: 

``` cpp
class DbNotFoundError : public std::exception
{
    using std::exception::exception;
};

std::optional<bilingual_str> LoadAddrman()
{
    try {

    } catch (const DbNotFoundError&) {
    } 
    return std::nullopt;
}
```

``` 
entities:
    items:
        -   id: 0
            name: DbNotFoundError
            loc: [ 1, 7 ]
            kind: Class
        -   id: 1
            name: LoadAddrman
            loc: [ 6, 30 ]
            kind: Function

dependencies:
    items:
        -   kind: Catch Exception
            src: 0
            dest: 1
            loc: [8, 20]

```

### Syntax: Throw Exception

#### Examples: 

``` cpp
class CConnectionFailed : public std::runtime_error{};
static UniValue CallRPC()
{
    char* encodedURI = evhttp_uriencode();
    if (encodedURI) {} else {
        throw CConnectionFailed("uri-encode failed");
    }
}
```

``` 
entities:
    items:
        -   id: 0
            name: CConnectionFailed
            kind: Class
        -   id: 1
            name: CallRPC
            kind: Function
dependencies:
    items:
        -   kind: Throw Exception
            src: 0
            dest: 1
            loc: [5, 15]
```