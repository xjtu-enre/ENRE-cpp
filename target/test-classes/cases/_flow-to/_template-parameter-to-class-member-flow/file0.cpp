template<typename T>
class Container {
    T data;
    void setData(T newData) {
        data = newData; // Flow from 'newData' to 'data'
    }
};