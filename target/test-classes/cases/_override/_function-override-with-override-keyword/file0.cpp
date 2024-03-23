class BaseClass
{
    virtual void funcA();
};
class DerivedClass: public BaseClass
{
    virtual void funcA() override;
}