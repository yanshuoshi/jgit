package com.yss.jgit.util;

import java.io.*;

/**
 * 文件操作
 *
 * @param
 * @author:Shuoshi.Yan
 * @date: 2020/12/31 10:08
 */

public class FileUtils {
    /**
     * 利用FileoutStream构造方法的每二个参数实现内容的追加
     *
     * @param f       文件
     * @param context 所要追加的内容
     */
    public static void append01(File f, String context) {
        try {
            BufferedWriter br = new BufferedWriter(new OutputStreamWriter
                    (new FileOutputStream(f, true)));
            br.write(context);
            br.flush();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 利用FileWriter构造方法中的第二个参数实现内容的追加
     *
     * @param f       文件
     * @param context 内容
     */
    public static void append02(File f, String context) {
        try {
            FileWriter fw = new FileWriter(f, true);
            fw.write(context);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 利用RandomAccessFile的seek()方法，
     * 将写文件指针移至文件末尾，实现内容的追加
     *
     * @param f       文件
     * @param context 内容
     */
    public static void append03(File f, String context) {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(f, "rw");
            raf.seek(raf.length());//将写文件指针移至文件末尾
            raf.writeBytes(context);
            raf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * copy文件
     *
     * @param source 源文件
     * @param dest   目标文件
     * @author:Shuoshi.Yan
     * @date: 2020/12/31 10:08
     */
    public static void copyFileUsingFileStreams(File source, File dest) {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(source);
            output = new FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) != -1) {
                output.write(buf, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void deleteFiles(File file){
        if (file.isDirectory()) {
            File[] files=file.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteFiles(files[i]);
                }else {
                    files[i].delete();
                }
                files[i].delete();
            }
        }
//        file.delete();
    }

    public static void main(String[] args) {
//        new File("D:/b/c").delete();
        FileUtils.deleteFiles(new File("D:/b/c"));
    }
}
