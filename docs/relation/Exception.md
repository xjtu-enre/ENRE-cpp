## Relation: Exception

Descriptions: An `Exception Relation` is an error condition, possibly outside the program's control, that prevents the program from continuing along its regular execution path. Certain operations, including object creation, file input/output, and function calls made from other modules, are all potential sources of exceptions, even when your program is running correctly. 

### Supported Patterns

```yaml
name: Exception Declaration
```
#### Syntax: Catch Exception

```text
noexcept noexcept(true) throw()
```

##### Examples

###### Try Catch Exception
```cpp
class DbNotFoundError : public std::exception{
    using std::exception::exception;
};
std::optional<bilingual_str> LoadAddrman(){
    try {    } catch (const DbNotFoundError&) {
    } 
    return std::nullopt;
}
```

```yaml
name: Exception
relation:
    type: Catch Exception
    items:
        -   from: Function:'LoadAddrman'
            to: Class:'DbNotFoundError'
            loc: file0:5:17
```

###### Throw Exception
```cpp
class CConnectionFailed : public std::runtime_error{};
void CallRPC()
{
    char* encodedURI = evhttp_uriencode();
    if (encodedURI) {} else {
        throw CConnectionFailed("uri-encode failed");
    }
}
```

```yaml
name: Throw Exception
relation:
    type: Throw Exception
    items:
        -   from: Function:'CallRPC'
            to: Class:'CConnectionFailed'
            loc: file0:6:15
```