package ru.innopolis.course3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Danil Popov
 */
public class Main {

    private static AtomicInteger summ = new AtomicInteger();

    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();
        for(String file: getResourcesList()) {
            Thread t = new Thread(new ReadCount(file));
            threads.add(t);
        }
        startThreads(threads);
    }

    private static void startThreads(List<Thread> threads) {
        for(Thread t: threads) {
            startThread(t::start);
        }
    }

    private static void startThread(Action t) {
        t.execute();
    }

    private static List<String> getResourcesList() {
        List<String> files = new ArrayList<>();
        files.add(System.getProperty("user.dir") + "/src/main/resources/" + "file1.txt");
        files.add(System.getProperty("user.dir") + "/src/main/resources/" + "file2.txt");
        files.add(System.getProperty("user.dir") + "/src/main/resources/" + "file3.txt");

        return files;
    }

    static void add(int i) {
        System.out.println(summ.addAndGet(i));
    }
}

class ReadCount implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(ReadCount.class);

    private String fileName;

    ReadCount(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void run() {
        try (InputStream is = new FileInputStream(fileName);
             BufferedInputStream bis = new BufferedInputStream(is);
             InputStreamReader inputStreamReader = new InputStreamReader(bis)) {
            char[] chars = new char[8096];
            int i = 0;
            int count = 0;
            while ((i = inputStreamReader.read(chars)) > 0){
                count += i;
            }
            char[] chars1 = new char[count];
            System.arraycopy(chars, 0, chars1, 0, count);
            String[] strings = new String(chars1).split(" ");

            List<String> list = Arrays.asList(strings);

            Main.add(list.stream()
                    .map(Integer::parseInt)
                    .filter(integer -> integer > 0 && integer % 2 == 0)
                    .reduce(0, Integer::sum)
            );

        } catch (IOException e) {
            logger.error("Read-Count thread io exception", e);
        }
    }
}

@FunctionalInterface
interface Action {
    void execute();
}
