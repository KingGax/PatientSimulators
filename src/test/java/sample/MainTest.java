package sample;//import static org.junit.jupiter.api.Assertions.assertEquals;

import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.junit.Assert.*;

//@TODO package method and default value into class (maybe?)
public class MainTest {
    sample.Main main;

    private Object invokeMethod(Object obj, Method method, Object def, Object... parameters){
        Object out = def;
        try {
            method.setAccessible(true);
            out = method.invoke(obj, parameters);
        } catch (Exception e){
            System.out.println("Exception encountered in method '"+method.getName()+"', using default value.");
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
        String ext1 = (String) invokeMethod(main, method, "", "a.txt");
        assertEquals("txt", ext1);
        String ext2 = (String) invokeMethod(main, method,"", "b.");
        assertEquals("", ext2);
        String ext3 = (String) invokeMethod(main, method,"", "b.aaaaaaaaaaaaaaaaaaaaaaaa");
        assertEquals("aaaaaaaaaaaaaaaaaaaaaaaa", ext3);
        String ext4 = (String) invokeMethod(main, method,"", "b\\a.png");
        assertEquals("png", ext4);
        String ext5 = (String) invokeMethod(main, method, "","b/a.q");
        assertEquals("q", ext5);
        String ext6 = (String) invokeMethod(main, method, "", "b/b\\b/b.1");
        assertEquals("1", ext6);
        String ext7 = (String) invokeMethod(main, method, "","b.b/b.a");
        assertEquals("a", ext7);
    }

    @Test
    public void TesttimeToFloat() {
        sample.Main main = new sample.Main();
        Method method = null;
        try{
            method = main.getClass().getDeclaredMethod("timeToFloat", String.class);
        } catch (Exception e){}
        float ext1 = (float) invokeMethod(main, method, -1f, "00:00:00");
        assertEquals(0f, ext1, 0.0001f);
        float ext2 = (float) invokeMethod(main, method, -1f, "0:00:00");
        assertEquals(-1f, ext2, 0.0001f);
        float ext3 = (float) invokeMethod(main, method,-1f, "00:0:00");
        assertEquals(-1f, ext3, 0.0001f);
        float ext4 = (float) invokeMethod(main, method, -1f,"00:00:0");
        assertEquals(-1f, ext4, 0.0001f);
        float ext5 = (float) invokeMethod(main, method, -1f,"0a:00:00");
        assertEquals(-1f, ext5, 0.0001f);
        float ext6 = (float) invokeMethod(main, method, -1f,"999:00:00");
        assertEquals(3596400f, ext6, 0.0001f);
        float ext7 = (float) invokeMethod(main, method, -1f,"10:01:11");
        assertEquals(36071f, ext7, 0.0001f);
        float ext8 = (float) invokeMethod(main, method, -1f,"-1:01:11");
        assertEquals(-1f, ext8, 0.0001f);
        float ext9 = (float) invokeMethod(main, method, -1f,"-01:99:99");
        assertEquals(-1f, ext9, 0.0001f);
    }

    @Test
    public void TestdateTimeToFloat() {
        sample.Main main = new sample.Main();
        Method method = null;
        try{
            method = main.getClass().getDeclaredMethod("dateTimeToFloat", String.class);
        } catch (Exception e){}
        float ext1 = (float) invokeMethod(main, method, -1f,"0000-00-00 00:00:00");
        assertEquals(0f, ext1, 0.0001f);
        float ext2 = (float) invokeMethod(main, method, -1f,"2000-00-00 00:00:00");
        assertEquals(63072000000f, ext2, 0.0001f);
        float ext3 = (float) invokeMethod(main, method, -1f,"200-00-00 00:00:00");
        assertEquals(-1f, ext3, 0.0001f);
        float ext4 = (float) invokeMethod(main, method, -1f,"200a-00-00 00:00:00");
        assertEquals(-1f, ext4, 0.0001f);
        float ext5 = (float) invokeMethod(main, method, -1f,"-2000-00-00 00:00:00");
        assertEquals(-1f, ext5, 0.0001f);
        float ext6 = (float) invokeMethod(main, method, -1f,"2000-0a-00 00:00:00");
        assertEquals(-1f, ext6, 0.0001f);
        float ext7 = (float) invokeMethod(main, method, -1f,"2000-00-a0 00:00:00");
        assertEquals(-1f, ext7, 0.0001f);
        float ext8 = (float) invokeMethod(main, method, -1f,"2000-00-00 0a:00:00");
        assertEquals(-1f, ext8, 0.0001f);
        float ext9 = (float) invokeMethod(main, method, -1f,"2000-00-00 -00:00:00");
        assertEquals(-1f, ext9, 0.0001f);
        float ext10 = (float) invokeMethod(main, method, -1f,"0000-00-01 00:00:01");
        assertEquals(86401f, ext10, 0.0001f);
    }

    @Test
    public void TestroundToDP(){
        sample.Main main = new sample.Main();
        Method method = null;
        try{
            method = main.getClass().getDeclaredMethod("roundToDP", float.class, int.class);
        } catch (Exception e){}
        float ext1 = (float) invokeMethod(main, method, 5.0f, 5.0f, 0);
        assertEquals(5.0f, ext1, 0.0001f);
        float ext2 = (float) invokeMethod(main, method, -5.3f, -5.3f,0);
        assertEquals(-5.0f, ext2, 0.0001f);
        float ext3 = (float) invokeMethod(main, method, 5.7f, 5.7f, 1);
        assertEquals(5.7f, ext3, 0.0001f);
        float ext4 = (float) invokeMethod(main, method, 10.34f, 10.34f, 2);
        assertEquals(10.34f, ext4, 0.0001f);
        float ext5 = (float) invokeMethod(main, method, 1013213141.34f,1013213141.34f, 3);
        assertEquals(1013213141.340f, ext5, 0.0001f);
        float ext6 = (float) invokeMethod(main, method, 7.000001f,7.000001f, 6);
        assertEquals(7.000001f, ext6, 0.0000001f);
    }

    @Test
    public void TestcosineInterpolate(){
        sample.Main main = new sample.Main();
        Method method = null;
        try{
            method = main.getClass().getDeclaredMethod("cosineInterpolate", float.class, float.class, float.class);
        } catch (Exception e){}
        float ext1 = (float) invokeMethod(main, method, 0f,1f, 2f,  0f);
        assertEquals(1f, ext1, 0.0001f);
        float ext2 = (float) invokeMethod(main, method, 0f,1f, 2f, 1f);
        assertEquals(2f, ext2, 0.0001f);
        float ext3 = (float) invokeMethod(main, method, 0f,1f, 2f, 0.5f);
        assertEquals(1.5f, ext3, 0.0001f);
        float ext4 = (float) invokeMethod(main, method, 0f,3f, 1f, 0.5f);
        assertEquals(2f, ext4, 0.0001f);
        float ext5 = (float) invokeMethod(main, method, 0f,1f, 2f, 0.25f);
        assertEquals(1.1464466094f, ext5, 0.0001f);
    }

    @Test
    public void TestvalidateDouble(){
        sample.Main main = new sample.Main();
        Method method = null;
        try{
            method = main.getClass().getDeclaredMethod("validateDouble", String.class, boolean.class);
        } catch (Exception e){
            fail();
        }
        boolean ext1 = (boolean) invokeMethod(main, method, false, "", true);
        assertFalse(ext1);
        boolean ext2 = (boolean) invokeMethod(main, method, false, "7.0",  true);
        assertTrue(ext2);
        boolean ext3 = (boolean) invokeMethod(main, method, false, "8",  true);
        assertTrue(ext3);
        boolean ext4 = (boolean) invokeMethod(main, method, false, "-4",  true);
        assertTrue(ext4);
        boolean ext5 = (boolean) invokeMethod(main, method, false, "-12.6",  true);
        assertTrue(ext5);
        boolean ext6 = (boolean) invokeMethod(main, method, false, "10", false);
        assertFalse(ext6);
    }

    @Test
    public void TestfillDataArray(){
        sample.Main main = new sample.Main();
        Method fillDataArray = null;
        Method countRows = null;
        try{
            countRows = main.getClass().getDeclaredMethod("countRows", File.class);
            fillDataArray = main.getClass().getDeclaredMethod("fillDataArray", String[].class, BufferedReader.class);
        } catch (Exception e){}
        File file1 = new File(getClass().getClassLoader().getResource("testFiles/Data/2019-10-28_1030_PhysiologicalDataLog.csv").getFile());
        try {
            String[] inArr = {"HR","SBP","SpO2", "C-SPINE Y"};
            BufferedReader reader = new BufferedReader(new FileReader(file1));
            invokeMethod(main, countRows, null, file1);
            float[][] outArr = (float[][]) invokeMethod(main, fillDataArray, null, inArr, reader);
            assertEquals(71f, outArr[0][0], 0.00001f);
            assertEquals(103f, outArr[155][0], 0.00001f);
            assertEquals(102f, outArr[154][0], 0.00001f);
            assertEquals(116f, outArr[0][1], 0.00001f);
            assertEquals(98f, outArr[5][2], 0.00001f);
            assertEquals(0f, outArr[53][3], 0.00001f);
        } catch (Exception e){
            System.out.println("Exception encountered during test: "+e);
        }
        File file2 = new File(getClass().getClassLoader().getResource("testFiles/Data/TestData1.csv").getFile());
        try {
            float[][] outArr;
            String[] inArr = {"HR","SBP","SpO2", "C-SPINE Y"};
            BufferedReader reader = new BufferedReader(new FileReader(file1));
            invokeMethod(main, countRows, null, file1);
            outArr = (float[][]) invokeMethod(main, fillDataArray, null, inArr, reader);
            assertEquals(71f, outArr[0][0], 0.00001f);
            assertEquals(116f, outArr[0][1], 0.00001f);
        } catch (Exception e){
            System.out.println("Exception encountered during test: "+e);
        }
    }

//    @Test
//    public void TestopenEventLog(){
//        sample.Main main = new sample.Main();
//        Method openEventLog = null;
//        try{
//            openEventLog = main.getClass().getDeclaredMethod("openEventLog", BufferedReader.class);
//        } catch (Exception e){}
//        File file1 = new File(getClass().getClassLoader().getResource("testFiles/Events/2019-10-28_SimulationEventsLog.csv").getFile());
//        try{
//            BufferedReader reader = new BufferedReader(new FileReader(file1));
//            ArrayList<Main.eventData> arr = (ArrayList<Main.eventData>) invokeMethod(main, openEventLog, null, reader);
//            assertEquals("SCE was started", arr.get(0).getEvent());
//            assertEquals(0, arr.get(0).getTime());
//            assertEquals("Scenario 'Haemorrhage_REV' started", arr.get(1).getEvent());
//            assertEquals(0, arr.get(1).getTime());
//            assertEquals("SCE was stopped", arr.get(8).getEvent());
//            assertEquals(778, arr.get(8).getTime());
//        } catch (Exception e){
//            System.out.println("Exception encountered during test: "+e);
//        }
//        File file2 = new File(getClass().getClassLoader().getResource("testFiles/Events/TestEvents1.csv").getFile());
//        try{
//            BufferedReader reader = new BufferedReader(new FileReader(file2));
//            ArrayList<Main.eventData> arr = (ArrayList<Main.eventData>) invokeMethod(main, openEventLog, null, reader);
//            assertEquals("Test1", arr.get(0).getEvent());
//            assertEquals(0, arr.get(0).getTime());
//            assertEquals("Test2", arr.get(1).getEvent());
//            assertEquals(0, arr.get(1).getTime());
//            assertEquals("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", arr.get(2).getEvent());
//            assertEquals(72, arr.get(2).getTime());
//        } catch (Exception e){
//            System.out.println("Exception encountered during test: "+e);
//        }
//    }

}
