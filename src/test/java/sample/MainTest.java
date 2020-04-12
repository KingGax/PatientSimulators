package sample;//import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class MainTest {
    private Object invokeMethod(Object obj, Method method, Object... parameters){
        Object out = 0;
        try {
            method.setAccessible(true);
            out = method.invoke(obj, parameters);
        } catch (Exception e){
            System.out.println(e);
        }
        return out;
    }

    @Test
    public void TestgetFileExtension() {
        sample.Main main = new sample.Main();
        Method method = null;
        try{
            method = main.getClass().getDeclaredMethod("getFileExtension", String.class);
        } catch (Exception e){}
        String ext1 = (String) invokeMethod(main, method, "a.txt");
        assertEquals("txt", ext1);
        String ext2 = (String) invokeMethod(main, method, "b.");
        assertEquals("", ext2);
        String ext3 = (String) invokeMethod(main, method, "b.aaaaaaaaaaaaaaaaaaaaaaaa");
        assertEquals("aaaaaaaaaaaaaaaaaaaaaaaa", ext3);
        String ext4 = (String) invokeMethod(main, method, "b\\a.png");
        assertEquals("png", ext4);
        String ext5 = (String) invokeMethod(main, method, "b/a.q");
        assertEquals("q", ext5);
        String ext6 = (String) invokeMethod(main, method, "b/b\\b/b.1");
        assertEquals("1", ext6);
        String ext7 = (String) invokeMethod(main, method, "b.b/b.a");
        assertEquals("a", ext7);
    }

    @Test
    public void TesttimeToFloat() {
        sample.Main main = new sample.Main();
        Method method = null;
        try{
            method = main.getClass().getDeclaredMethod("timeToFloat", String.class);
        } catch (Exception e){}
        Float ext1 = (Float) invokeMethod(main, method, "00:00:00");
        assertEquals(0f, ext1, 0.0001f);
        Float ext2 = (Float) invokeMethod(main, method, "0:00:00");
        assertEquals(-1f, ext2, 0.0001f);
        Float ext3 = (Float) invokeMethod(main, method, "00:0:00");
        assertEquals(-1f, ext3, 0.0001f);
        Float ext4 = (Float) invokeMethod(main, method, "00:00:0");
        assertEquals(-1f, ext4, 0.0001f);
        Float ext5 = (Float) invokeMethod(main, method, "0a:00:00");
        assertEquals(-1f, ext5, 0.0001f);
        Float ext6 = (Float) invokeMethod(main, method, "999:00:00");
        assertEquals(3596400f, ext6, 0.0001f);
        Float ext7 = (Float) invokeMethod(main, method, "10:01:11");
        assertEquals(36071f, ext7, 0.0001f);
        Float ext8 = (Float) invokeMethod(main, method, "-1:01:11");
        assertEquals(-1f, ext8, 0.0001f);
        Float ext9 = (Float) invokeMethod(main, method, "-01:99:99");
        assertEquals(-1f, ext9, 0.0001f);
    }

    @Test
    public void TestdateTimeToFloat() {
        sample.Main main = new sample.Main();
        Method method = null;
        try{
            method = main.getClass().getDeclaredMethod("dateTimeToFloat", String.class);
        } catch (Exception e){}
        Float ext1 = (Float) invokeMethod(main, method, "0000-00-00 00:00:00");
        assertEquals(0f, ext1, 0.0001f);
        Float ext2 = (Float) invokeMethod(main, method, "2000-00-00 00:00:00");
        assertEquals(63072000000f, ext2, 0.0001f);
        Float ext3 = (Float) invokeMethod(main, method, "200-00-00 00:00:00");
        assertEquals(-1f, ext3, 0.0001f);
        Float ext4 = (Float) invokeMethod(main, method, "200a-00-00 00:00:00");
        assertEquals(-1f, ext4, 0.0001f);
        Float ext5 = (Float) invokeMethod(main, method, "-2000-00-00 00:00:00");
        assertEquals(-1f, ext5, 0.0001f);
        Float ext6 = (Float) invokeMethod(main, method, "2000-0a-00 00:00:00");
        assertEquals(-1f, ext6, 0.0001f);
        Float ext7 = (Float) invokeMethod(main, method, "2000-00-a0 00:00:00");
        assertEquals(-1f, ext7, 0.0001f);
        Float ext8 = (Float) invokeMethod(main, method, "2000-00-00 0a:00:00");
        assertEquals(-1f, ext8, 0.0001f);
        Float ext9 = (Float) invokeMethod(main, method, "2000-00-00 -00:00:00");
        assertEquals(-1f, ext9, 0.0001f);
        Float ext10 = (Float) invokeMethod(main, method, "0000-00-01 00:00:01");
        assertEquals(86401f, ext10, 0.0001f);
    }

    @Test
    public void TestroundToDP(){
        sample.Main main = new sample.Main();
        Method method = null;
        try{
            method = main.getClass().getDeclaredMethod("roundToDP", float.class, int.class);
        } catch (Exception e){}
        float ext1 = (float) invokeMethod(main, method, 5.0f, 0);
        assertEquals(5.0f, ext1, 0.0001f);
        float ext2 = (float) invokeMethod(main, method, -5.3f, 0);
        assertEquals(-5.0f, ext2, 0.0001f);
        float ext3 = (float) invokeMethod(main, method, 5.7f, 1);
        assertEquals(5.7f, ext3, 0.0001f);
        float ext4 = (float) invokeMethod(main, method, 10.34f, 2);
        assertEquals(10.34f, ext4, 0.0001f);
        float ext5 = (float) invokeMethod(main, method, 1013213141.34f, 3);
        assertEquals(1013213141.340f, ext5, 0.0001f);
        float ext6 = (float) invokeMethod(main, method, 7.000001f, 6);
        assertEquals(7.000001f, ext6, 0.0000001f);
    }

    @Test
    public void TestcosineInterpolate(){
        sample.Main main = new sample.Main();
        Method method = null;
        try{
            method = main.getClass().getDeclaredMethod("cosineInterpolate", float.class, float.class, float.class);
        } catch (Exception e){}
        float ext1 = (float) invokeMethod(main, method, 1f, 2f, 0f);
        assertEquals(1f, ext1, 0.0001f);
        float ext2 = (float) invokeMethod(main, method, 1f, 2f, 1f);
        assertEquals(2f, ext2, 0.0001f);
        float ext3 = (float) invokeMethod(main, method, 1f, 2f, 0.5f);
        assertEquals(1.5f, ext3, 0.0001f);
        float ext4 = (float) invokeMethod(main, method, 3f, 1f, 0.5f);
        assertEquals(2f, ext4, 0.0001f);
        float ext5 = (float) invokeMethod(main, method, 1f, 2f, 0.25f);
        assertEquals(1.1464466094f, ext5, 0.0001f);
    }

}
