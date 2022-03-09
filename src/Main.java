import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author liuchongwei
 * @email lcwliuchongwei@qq.com
 * @date 2022-03-09
 */
public class Main {
    public static void main(String[] args) {
        final Map<String, String> map = Stream.of(1, 2, 3, 4, 5, 6, 7, 8)
                .collect(Collectors.toMap(item -> "KEY_" + item, item -> "VALUE_" + item));
        System.out.println(map);
    }
}