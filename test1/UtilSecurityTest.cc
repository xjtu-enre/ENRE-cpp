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
