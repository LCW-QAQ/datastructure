import java.util.LinkedList;

/**
 * @author liuchongwei
 * @email lcwliuchongwei@qq.com
 * @date 2022-03-09
 */
public class Main {
    public static void main(String[] args) {
        final LinkedList<Integer> list = new LinkedList<>();
        list.add(0);
        System.out.println(list);
        list.add(1, 10);
        System.out.println(list);
    }
}