package sample;//import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class MainTest {
    private Object invokeMethod(Object obj, String methodName, Object... parameters){
        Object out = 0;
        try {
            Method method = obj.getClass().getDeclaredMethod(methodName, String.class);
            method.setAccessible(true);
            out = method.invoke(obj, parameters);
        } catch (Exception e){
            System.out.println("iowpiwfpoiwpowjkpow");
            System.out.println(e);
        }
        return out;
    }

    @Test
    public void TestgetFileExtension() {
        sample.Main main = new sample.Main();
        String ext1 = (String) invokeMethod(main, "getFileExtension", "a.txt");
        assertEquals("txt", ext1);
        String ext2 = (String) invokeMethod(main, "getFileExtension", "b.");
        assertEquals("", ext2);
        String ext3 = (String) invokeMethod(main, "getFileExtension", "b.aaaaaaaaaaaaaaaaaaaaaaaa");
        assertEquals("aaaaaaaaaaaaaaaaaaaaaaaa", ext3);
        String ext4 = (String) invokeMethod(main, "getFileExtension", "b\\a.png");
        assertEquals("png", ext4);
        String ext5 = (String) invokeMethod(main, "getFileExtension", "b/a.q");
        assertEquals("q", ext5);
        String ext6 = (String) invokeMethod(main, "getFileExtension", "b/b\\b/b.1");
        assertEquals("1", ext6);
        String ext7 = (String) invokeMethod(main, "getFileExtension", "b.b/b.a");
        assertEquals("a", ext7);
    }

    @Test
    public void TesttimeToFloat() {
        sample.Main main = new sample.Main();
        Float ext1 = (Float) invokeMethod(main, "timeToFloat", "00:00:00");
        assertEquals(0f, ext1, 0.0001f);
        Float ext2 = (Float) invokeMethod(main, "timeToFloat", "0:00:00");
        assertEquals(-1f, ext2, 0.0001f);
        Float ext3 = (Float) invokeMethod(main, "timeToFloat", "00:0:00");
        assertEquals(-1f, ext3, 0.0001f);
        Float ext4 = (Float) invokeMethod(main, "timeToFloat", "00:00:0");
        assertEquals(-1f, ext4, 0.0001f);
        Float ext5 = (Float) invokeMethod(main, "timeToFloat", "0a:00:00");
        assertEquals(-1f, ext5, 0.0001f);
        Float ext6 = (Float) invokeMethod(main, "timeToFloat", "999:00:00");
        assertEquals(3596400f, ext6, 0.0001f);
        Float ext7 = (Float) invokeMethod(main, "timeToFloat", "10:01:11");
        assertEquals(36071f, ext7, 0.0001f);
        Float ext8 = (Float) invokeMethod(main, "timeToFloat", "-1:01:11");
        assertEquals(-1f, ext8, 0.0001f);
        Float ext9 = (Float) invokeMethod(main, "timeToFloat", "-01:99:99");
        assertEquals(-1f, ext9, 0.0001f);
    }

    @Test
    public void TestdateTimeToFloat() {
        sample.Main main = new sample.Main();
        Float ext1 = (Float) invokeMethod(main, "dateTimeToFloat", "0000-00-00 00:00:00");
        assertEquals(0f, ext1, 0.0001f);
        Float ext2 = (Float) invokeMethod(main, "dateTimeToFloat", "2000-00-00 00:00:00");
        assertEquals(63072000000f, ext2, 0.0001f);
        Float ext3 = (Float) invokeMethod(main, "dateTimeToFloat", "200-00-00 00:00:00");
        assertEquals(-1f, ext3, 0.0001f);
        Float ext4 = (Float) invokeMethod(main, "dateTimeToFloat", "200a-00-00 00:00:00");
        assertEquals(-1f, ext4, 0.0001f);
        Float ext5 = (Float) invokeMethod(main, "dateTimeToFloat", "-2000-00-00 00:00:00");
        assertEquals(-1f, ext5, 0.0001f);
        Float ext6 = (Float) invokeMethod(main, "dateTimeToFloat", "2000-0a-00 00:00:00");
        assertEquals(-1f, ext6, 0.0001f);
        Float ext7 = (Float) invokeMethod(main, "dateTimeToFloat", "2000-00-a0 00:00:00");
        assertEquals(-1f, ext7, 0.0001f);
        Float ext8 = (Float) invokeMethod(main, "dateTimeToFloat", "2000-00-00 0a:00:00");
        assertEquals(-1f, ext8, 0.0001f);
        Float ext9 = (Float) invokeMethod(main, "dateTimeToFloat", "2000-00-00 -00:00:00");
        assertEquals(-1f, ext9, 0.0001f);
        Float ext10 = (Float) invokeMethod(main, "dateTimeToFloat", "0000-00-01 00:00:01");
        assertEquals(86401f, ext10, 0.0001f);
    }

}
