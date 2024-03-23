class Device {
public:
    int batteryLevel;
};

void checkBattery(Device& device) {
    auto batteryCheck = [&device]() {
        if (device.batteryLevel < 20) {
            cout << "Battery low";
        }
    };
    batteryCheck();
}