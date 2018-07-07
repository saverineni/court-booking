import java.util.Arrays;

/**
 * Created by suresh.averineni on 27/05/2018.
 */
public class ReduceExample {

    public static void main(String args[]) {
        Arrays.asList(2, 3, 1, 4, 2, 3, 4)
                .stream()
                .reduce((number1, number2) -> number1 + number2).ifPresent(System.out::println);

        Integer reduce = Arrays.asList(2, 3, 1, 4, 2, 3, 4)
                .stream()
                .reduce(200, (number1, number2) -> number1 + number2);
        System.out.print(reduce);

    }
}
