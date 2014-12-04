package net.nycjava;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

import static java.util.Comparator.comparingLong;

public class Main {

    public static void main(String[] args) throws IOException {
        timeIt("serial", b -> b.count());
        timeIt("parallel", b -> b.parallel().count());
        timeIt("serial", b -> b.count());
        timeIt("parallel", b -> b.parallel().count());

        timeIt("serial most pop. girls name in CA in 2010",
                b -> b.filter(e -> e.year == 2010).
                        filter(e -> e.gender == 'F').
                        filter(e -> e.state.equals("CA")).
                        sorted(comparingLong(BabyName::getNumber).reversed()).
                        findFirst().
                        get());
        timeIt("parallel most pop. girls name in CA in 2010",
                b -> b.parallel().
                        filter(e -> e.year == 2010).
                        filter(e -> e.gender == 'F').
                        filter(e -> e.state.equals("CA")).
                        sorted(comparingLong(BabyName::getNumber).reversed()).
                        findFirst().
                        get());
    }

    public static <T> void timeIt(
            String description,
            Function<Stream<BabyName>, T> function)
            throws IOException {

        final Stream<BabyName> babyNameStream = getBabyNameStream();

        final long startTime = System.currentTimeMillis();
        final T result = function.apply(babyNameStream);
        final long endTime = System.currentTimeMillis();

        System.out.println(String.format(
                "ran %s in %d ms; result = %s",
                description,
                endTime - startTime,
                result));
    }

    public static Stream<BabyName> getBabyNameStream()
            throws IOException {
        // data from http://catalog.data.gov/dataset/baby-names-from-social-security-card-applications-data-by-state-and-district-of-
        final ZipFile zipFile = new ZipFile("C:\\namesbystate.zip");

        final Stream<BabyName> babyNameStream = zipFile.
                stream().
                filter(e -> e.getName().endsWith(".TXT")).
                flatMap(e -> {
                    try {
                        final BufferedReader br =
                                new BufferedReader(
                                        new InputStreamReader(
                                                zipFile.getInputStream(e)));
                        return br.lines().map(line -> {
                            final String[] parts = line.split(",");
                            return new BabyName(
                                    parts[3],
                                    parts[0],
                                    Integer.parseInt(parts[2]),
                                    parts[1].charAt(0),
                                    Long.parseLong(parts[4]));
                        });
                    } catch (IOException e1) {
                        throw new RuntimeException(e1);
                    }
                });

        return babyNameStream;
    }
}
