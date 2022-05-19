package com.example.byteplus_effects_plugin.resource.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created on 2021/11/24 10:35
 */
public class FileUtils {


    public static String removeFileNameExtension(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }


    public static boolean unzipFile(String filePath, File dstDir) {
        try {
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(new File(filePath)));
            boolean ret = unzipFile(zipInputStream, dstDir);
//            LogUtils.d("unzipFile ret =" + ret);
            return ret;

        } catch (Exception e) {
            e.printStackTrace();
        }
//        LogUtils.d("unzipFile ret =" + false);

        return false;
    }

    public static boolean unzipFile(ZipInputStream zipInputStream, File dstDir) {

        try {
            if (dstDir.exists()) {
                dstDir.delete();
            }
            dstDir.mkdirs();
            if (null == zipInputStream) {
                return false;
            }
            ZipEntry entry;
            String name;
            do {
                entry = zipInputStream.getNextEntry();
                if (null != entry) {
                    name = entry.getName();
                    if (entry.isDirectory()) {
                        name = name.substring(0, name.length() - 1);
                        File folder = new File(dstDir, name);
                        folder.mkdirs();

                    } else {
                        //   {zh} 否则创建文件,并输出文件的内容     {en} Otherwise create the file and output the contents of the file
                        File file = new File(dstDir, name);
                        file.getParentFile().mkdirs();
                        file.createNewFile();
                        FileOutputStream out = new FileOutputStream(file);
                        int len;
                        byte[] buffer = new byte[1024];
                        while ((len = zipInputStream.read(buffer)) != -1) {
                            out.write(buffer, 0, len);
                            out.flush();
                        }
                        out.close();

                    }
                }

            } while (null != entry);

        } catch (Exception e) {
            e.printStackTrace();
            return false;

        } finally {
            if (zipInputStream != null) {
                try {
                    zipInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

        }
        return true;

    }


    public static boolean checkFileMD5(String strMD5, String filePath) {
        try {
            String string = getHash(filePath);
            return string == strMD5;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static char[] hexChar = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String getHash(String fileName)
            throws Exception {
        InputStream fis;
        fis = new FileInputStream(fileName);
        byte[] buffer = new byte[1024];
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        int numRead = 0;
        while ((numRead = fis.read(buffer)) > 0) {
            md5.update(buffer, 0, numRead);
        }
        fis.close();
        return toHexString(md5.digest());
    }

    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
            sb.append(hexChar[b[i] & 0x0f]);
        }
        return sb.toString();
    }


    public static boolean validateFileMD5(String strMD5, String filePath) {
        File fileToCheck = new File(filePath);
        if (strMD5.isEmpty() || !fileToCheck.exists()) {
//            LogUtils.e("MD5 string empty or File not exists");
            return false;
        }
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
        InputStream is;
        try {
            is = new FileInputStream(fileToCheck);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
//            LogUtils.e("Exception while getting FileInputStream");
            return false;
        }
        byte[] buffer = new byte[8192];
        int read;
        String calculatedDigest = null;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            calculatedDigest = bigInt.toString(16);
            calculatedDigest = String.format("%32s", calculatedDigest).replace(' ', '0');
        } catch (IOException e) {
            e.printStackTrace();
//            LogUtils.e("Unable to process file for ");
            return false;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
//                LogUtils.e("Exception on closing inputstream:");
            }
        }
        if (calculatedDigest.isEmpty()) {
//            LogUtils.d("calculatedDigest null");
            return false;
        }
        return calculatedDigest.equalsIgnoreCase(strMD5);
    }

    public static boolean prepareFilePath(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (!file.delete()) {
                return false;
            }
        }

        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                return false;
            }
        }

        return true;
    }
}
