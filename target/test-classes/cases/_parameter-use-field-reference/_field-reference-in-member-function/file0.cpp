class Car {
public:
    int speed;

    void setSpeed(int s) {
        speed = s;
    }
};

void testCar() {
    Car myCar;
    myCar.setSpeed(60);
}