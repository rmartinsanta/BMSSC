package other;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConversionUtil {

    public static final Function<String, String> IRIS_CONVERTER = line -> line.substring(0, line.lastIndexOf(",")) + "\n";

    public static final Function<String, String> WINE_CONVERTER = line -> line.substring(line.indexOf(",") + 1) + "\n";

    public static final Function<String, String> GLASS_CONVERTER = line -> line.substring(line.indexOf(',') + 1, line.lastIndexOf(',')) + "\n";

    public static final Function<String, String> IONOSPHERE_CONVERTER = line -> line.substring(0, line.lastIndexOf(',')) + "\n";

    public static final Function<String, String> LIBRA_CONVERTER = line -> line.substring(0, line.lastIndexOf(',')) + "\n";

    public static final Function<String, String> USER_KNOWLEDGE_CONVERTER = line -> line.replace(',', '.').replace('\t', ',') + "\n";

    public static final Function<String, String> BREAST_CONVERTER = line -> line.substring(Math.max(line.indexOf('M'), line.indexOf('B')) + 2) + "\n";

    public static final Function<String, String> SYNTHETIC_CONTROL_CONVERTER = line -> Arrays.stream(line.split(" ")).filter(l -> !l.isEmpty()).collect(Collectors.joining(",")) + "\n";

    public static final Function<String, String> VEHICLE_CONVERTER = line -> line.substring(0, line.lastIndexOf(" ")).replace(' ', ',') + "\n";

    public static final Function<String, String> VOWEL_RECOGNITION_CONVERTER = line -> line + "\n";

    public static final Function<String, String> YEAST_CONVERTER = line -> line + "\n";

    public static final Function<String, String> MULTIPLE_FEATURES_CONVERTER = line -> line.replace("  ", ",") + "\n";

    public static final Function<String, String> IMAGE_SEGMENTATION_CONVERTER = line -> line.substring(0, line.lastIndexOf(' ')).replace(' ', ',') + "\n";

    public static final Function<String, String> ACC_GYRO_CONVERTER = l -> Character.isDigit(l.charAt(0)) ? removeColums(l, 1, 4) : "";

    public static final Function<String, String> ACC_GYRO_REDUCER = l -> removeColums(l, 2, 0);

    public static final Function<String, String> INTERNET_ADS_CONVERTER = l -> removeColums(l, 3, 1).replace('?', '0');

    private static String removeColums(String l, int start, int end) {
        for (int i = 0; i < start; i++) {
            l = l.substring(l.indexOf(',') + 1);
        }

        for (int i = 0; i < end; i++) {
            l = l.substring(0, l.lastIndexOf(','));
        }

        return l + "\n";
    }

    /**
     * Standarizes a datafile
     * Examples:
     * other.ConversionUtil.convertFile("IrisFile.data", "output.data", other.ConversionUtil.IRIS_CONVERTER);
     * other.ConversionUtil.convertFile("File1", "File2", line -> line.substring(1,line.length));
     *
     * @param input    Datafile to convert
     * @param output   Where should we store the converted datafile
     * @param strategy How should the datafile be converted
     */
    public static void convertFile(String input, String output, Function<String, String> strategy) {
        convertFile(input, output, strategy, Integer.MAX_VALUE);
    }

    public static void convertFile(String input, String output, Function<String, String> strategy, int limit) {
        try (
                Stream<String> lines = Files.lines(Paths.get(input));
                BufferedWriter bw = Files.newBufferedWriter(Paths.get(output))
        ) {
            lines
                    .limit(limit)
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .forEach(line -> writeLine(bw, strategy.apply(line)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void writeLine(BufferedWriter bw, String line) {
        try {
            bw.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void fixMissingDimensions(File f, File out){
        int max = Integer.MIN_VALUE;

        try(BufferedReader reader = new BufferedReader(new FileReader(f))){
            String s;
            while((s = reader.readLine()) != null){
                int n = s.split(",").length;
                max = Math.max(max, n);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (
                BufferedReader reader = new BufferedReader(new FileReader(f));
                BufferedWriter writer = new BufferedWriter(new FileWriter(out))
        ) {
            String s;
            while ((s = reader.readLine()) != null) {
                int n = s.split(",").length;
                for (int i = 0; i < max - n; i++) {
                    s = s + ",0";
                }
                writer.write(s);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
