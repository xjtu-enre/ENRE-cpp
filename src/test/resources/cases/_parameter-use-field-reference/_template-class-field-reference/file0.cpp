template<typename T>
class Box {
public:
    T content;
};

template<typename T>
void fillBox(Box<T>& box, T item) {
    box.content = item;
}

void useBox() {
    Box<int> intBox;
    fillBox(intBox, 100);
}