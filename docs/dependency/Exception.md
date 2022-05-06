# Exception

## Supported pattern
```yaml
name: Exception Declaration
```
### Syntax: Catch Exception

#### Examples: 

- Example1
```cpp
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

```yaml
name: Exception
entity:
    items:
        -   id: 0
            name: DbNotFoundError
            loc: [ 1, 7 ]
            category: Class
        -   id: 1
            name: LoadAddrman
            loc: [ 6, 30 ]
            category: Function
relation:
    items:
        -   category: Catch Exception
            src: 0
            dest: 1
            loc: [8, 20]
            r:
                d: x
                e: x
                s: xtype use
                u: .

```

### Syntax: Throw Exception

#### Examples: 

- Example2
```cpp
class CConnectionFailed : public std::runtime_error{};
static UniValue CallRPC()
{
    char* encodedURI = evhttp_uriencode();
    if (encodedURI) {} else {
        throw CConnectionFailed("uri-encode failed");
    }
}
```

```yaml
name: Throw Exception
entity:
    items:
        -   id: 0
            name: CConnectionFailed
            category: Class
        -   id: 1
            name: CallRPC
            category: Function
relation:
    items:
        -   category: Throw Exception
            src: 1
            dest: 0
            loc: [5, 15]
            r:
                d: xCall
                e: xCall
                s: x
                u: .
```