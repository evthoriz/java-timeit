package me.evthoriz.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by evtHoriz on 15/6/9.
 * Email: evthoriz@gmail.com
 */
public class TimeitCore {
    public static Boolean multi = false;
    public static ThreadPoolExecutor executor = null;
    private static List<Envelope> list = new ArrayList<Envelope>();
    private static Object obj = null;

    private static TimeitCore init(Class clazz) throws Exception {
        obj = clazz.newInstance();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            Annotation annotation = method.getAnnotation(Timeit.class);
            if (annotation != null) {
                list.add(new Envelope(method, (Timeit) annotation));
            }
        }
        return new TimeitCore();
    }

    public static void start(Class clazz) {
        try {
            init(clazz);
            if (multi) {
                runMulti();
            } else {
                runSerial();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void setMutithread(Boolean flag) {
        multi = flag;
    }

    public static void tiktok(Envelope env) throws Exception {
        int count = env.Anno.count();
        for (int i = 0; i < count; i++) {
            env.method.invoke(obj);
        }
    }

    public static void runSerial() {
        for (final Envelope env : list) {
            Thread t = new TaskThread(env);
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void runMulti() throws InterruptedException {
        for (final Envelope env : list) {
            Thread t = new TaskThread(env);
            t.start();
        }

    }

    public static class TaskThread extends Thread {
        private Envelope env;
        private TimeoutThread tt;
        private volatile Boolean finish = false;
        private volatile Boolean timeout = false;

        public TaskThread(Envelope env) {
            this.env = env;
            this.tt = new TimeoutThread(this);
            this.tt.setDaemon(true);
        }

        @Override
        public void run() {
            tt.start();
            StringBuilder result = new StringBuilder();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            result.append(",\t Method: ").append(env.method.getName()).append("\t, Count: ")
                    .append(env.Anno.count()).append("\t, Timeout: ").append(env.Anno.timeout()).append("\t, Elapsed: ");
            try {
                long startTime = System.currentTimeMillis();
                tiktok(env);
                finish = true;
                long elapsed = System.currentTimeMillis() - startTime;
                result.append(elapsed + " ms.");
            } catch (Exception e) {
                if (timeout) {
                    result.append("- timeout -");
                } else {
                    e.printStackTrace();
                }
            } finally {
                result.insert(0, sdf.format(new Date()));
                System.out.println(result);
            }

        }

        public class TimeoutThread extends Thread {
            private TaskThread taskThread;

            public TimeoutThread(TaskThread taskThread) {
                this.taskThread = taskThread;
            }

            @Override
            public void run() {
                try {
                    Thread.sleep(env.Anno.timeout());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!finish) {
                    timeout = true;
                    taskThread.stop();
                    //taskThread.interrupt();
                }
            }
        }

    }


    private static class Envelope {
        final Method method;
        final Timeit Anno;

        public Envelope(Method method, Timeit anno) {
            this.method = method;
            this.Anno = anno;
        }
    }
}
