
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
