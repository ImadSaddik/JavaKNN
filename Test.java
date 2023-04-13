import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Test {
    public static void main(String[] args) {
        List<Integer> list1 = new ArrayList<>(Arrays.asList(1, 2, 3));
        List<Integer> list2 = new ArrayList<>(Arrays.asList(4, 5, 6));
        List<Integer> joinedList = Stream.concat(list1.stream(), list2.stream())
                .collect(Collectors.toList());

        List<Integer> list3 = new ArrayList<>();
        list3.addAll(list1);
        list3.addAll(list2);

        list1.addAll(list2);

        System.out.println(list1);
        System.out.println(list2);
        System.out.println(list3);
        System.out.println(joinedList);
    }
}
