class CConnectionFailed : public std::runtime_error{};
void CallRPC()
{
    char* encodedURI = evhttp_uriencode();
    if (encodedURI) {} else {
        throw CConnectionFailed("uri-encode failed");
    }
}