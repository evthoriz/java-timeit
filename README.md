# java-timeit
A library to record the time your functions run, base on annotation and reflection.
## Usage
Use ```@Timeit``` to specify the functions you need to time. Every function you Annotated is put in a new thread, which will be stoped immediately when reaches timeout.

``` java
    @Timeit(count = 2, timeout = 3000)
    public void timeout1() {
        while (true);
    }
```

```TimeitCore.start()``` takes class as parameter, setMutithread true if you want run the functions you annotated in the mean time.

```
    try {
        TimeitCore.setMutithread(true);
        TimeitCore.start(YourClass.class);
    } catch (Exception e) {
        e.printStackTrace();
    }

```
