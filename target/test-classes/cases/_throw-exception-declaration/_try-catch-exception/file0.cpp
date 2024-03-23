class DbNotFoundError : public std::exception{
    using std::exception::exception;
};
std::optional<bilingual_str> LoadAddrman(){
    try {    } catch (const DbNotFoundError&) {
    } 
    return std::nullopt;
}