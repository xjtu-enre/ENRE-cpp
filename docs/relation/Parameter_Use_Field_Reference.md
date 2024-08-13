## Relation: Parameter Use Field Reference

Description:The `Parameter Use Field Reference` Relation in C++ captures the usage of a class or struct member field through a parameter within a function. This relation indicates that the parameter is utilized to access and potentially modify the member fields of a class or struct instance.

### Supported Patterns

```yaml
name: Parameter Use Field Reference
```

#### Syntax: Parameter Use Field Reference Declaration

```text
ParameterUseFieldReferenceDeclaration :
    function-body ( parameter.field )
```

##### Examples

###### Direct Field Reference from Parameter

```CPP
//// util_security.h
#ifndef D_UTIL_SECURITY_H
#define D_UTIL_SECURITY_H

namespace aria2 {

class HMAC {
public:

  HMAC(const std::string& algorithm, const char* secret, size_t length);

  static std::unique_ptr<HMAC> create(const char* secret, size_t length)
  {
    return create("sha-1", secret, length);
  }

};

} // namespace aria2

#endif // D_UTIL_SECURITY_H
```

```CPP
//// UtilSecurityTest.cc
#include "util_security.h"
namespace aria2 {

class SecurityTest : public CppUnit::TestFixture {

  CPPUNIT_TEST_SUITE(SecurityTest);

private:
public:
  void testPBKDF2();
};

static struct pbkdf2 {
  char pass[32];
  size_t pass_len;
} pbkdf2s[] = { };

void SecurityTest::testPBKDF2()
{
  for (const auto& test : pbkdf2s) {
    auto h = HMAC::create(test.pass, test.pass_len);
  }
}
} // namespace aria2
```

```yaml
name: Direct Field Reference from Parameter
relation:
  type: Parameter Use Field Reference
  items:
    - from: Function:'aria2::HMAAC::create'
      to: Variable:'aria2::pbkdf2::pass'
      loc: file1:22:27
    - from: Function:'aria2::HMAAC::create'
      to: Variable:'aria2::pbkdf2::pass_len'
      loc: file1:22:38
```
