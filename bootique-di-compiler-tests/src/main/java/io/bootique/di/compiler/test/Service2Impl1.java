package io.bootique.di.compiler.test;

public class Service2Impl1 implements Service2 {

    private Service1 service1;

    public Service2Impl1(Service1 service1) {
        this.service1 = service1;
    }

    @Override
    public String doIt() {
        return "Service2Impl1_" + service1.doIt();
    }
}
