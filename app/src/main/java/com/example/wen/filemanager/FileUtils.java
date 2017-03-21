package com.example.wen.filemanager;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by wen on 2017/2/25.
 */

public class FileUtils {

    public static List<HashMap<String, Object>> getFilesCountInfo(Context context){

        List<HashMap<String, Object>> dataList=new ArrayList<>();
        String[] tStrings=new String[]{"图片","音乐","视频","Word","文档","压缩包"};
        String[] counts=new String[]{"(0)","(0)","(0)","(0)","(0)","(0)"};
        int[] icons=new int[]{R.mipmap.icon_pictures,R.mipmap.icon_music,R.mipmap.icon_movie,R.mipmap.icon_word,R.mipmap.icon_document,R.mipmap.icon_zip_folder};

        //获取图片数
        Cursor curson = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID}, null, null, null);
        if(curson!=null){
            counts[0]="("+curson.getCount()+")";
        }
        //获取音乐数
        curson = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,new String[]{MediaStore.Audio.Media._ID}, null, null, MediaStore.Audio.Media.DATA);
        if(curson!=null){
            counts[1]="("+curson.getCount()+")";
        }
        //获取视频
        curson = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Video.Media._ID}, null, null, null);
        if(curson!=null){
            counts[2]="("+curson.getCount()+")";
        }
        //获取Word
        String select="("+ MediaStore.Files.FileColumns.DATA+" LIKE '%.doc' or ";
        select+=MediaStore.Files.FileColumns.DATA+" LIKE '%.ppt' or ";
        select+=MediaStore.Files.FileColumns.DATA+" LIKE '%.xlsx' or ";
        select+=MediaStore.Files.FileColumns.DATA+" LIKE '%.xls' )";
        curson = context.getContentResolver().query(MediaStore.Files.getContentUri("external"), new String[]{MediaStore.Files.FileColumns._ID}, select, null, null);
        if(curson!=null){
            counts[3]="("+curson.getCount()+")";
        }
        //获取文档
        select="("+ MediaStore.Files.FileColumns.DATA+" LIKE '%.txt')";
        curson = context.getContentResolver().query(MediaStore.Files.getContentUri("external"), new String[]{MediaStore.Files.FileColumns._ID}, select, null, null);
        if(curson!=null){
            counts[4]="("+curson.getCount()+")";
        }
        //获取压缩包
        select="("+ MediaStore.Files.FileColumns.DATA+" LIKE '%.zip' or "+MediaStore.Files.FileColumns.DATA+" LIKE '%.rar' )";
        curson = context.getContentResolver().query(MediaStore.Files.getContentUri("external"), new String[]{MediaStore.Files.FileColumns._ID}, select, null, null);
        if(curson!=null){
            counts[5]="("+curson.getCount()+")";
        }

        curson.close();
        for(int i=0;i<tStrings.length;i++){
            HashMap<String, Object> map=new HashMap<String, Object>();
            map.put("image", icons[i]);
            map.put("text", tStrings[i]);
            map.put("count", counts[i]);
            dataList.add(map);
        }
        return dataList;
    }
    public static List<HashMap<String, Object>> getImageInfo(Context context){
        Cursor cursor= context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null, null, null, null);
        if(cursor==null){
            return null;
        }
        List<HashMap<String, Object>> dataList=new ArrayList<>();
        while(cursor.moveToNext()){
            HashMap<String, Object> map=new HashMap<String, Object>();
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
            String type=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE));
            Long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));

            map.put("image1_item", R.mipmap.icon_jpg);
            map.put("file_name", cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)));
            map.put("file_count", Formatter.formatFileSize(context,size));
            map.put("image2_item", null);
            map.put("path",path);
            map.put("type",type);
            dataList.add(map);
        }
        return dataList;
    }
    public static List<HashMap<String, Object>> getMusicInfo(Context context){
        Cursor cursor= context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null, null, null, null);
        if(cursor==null){
            return null;
        }

        List<HashMap<String, Object>> dataList=new ArrayList<>();
        while(cursor.moveToNext()){
            HashMap<String, Object> map=new HashMap<String, Object>();
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            String type=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE));

            long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
            map.put("image1_item", R.mipmap.icon_mp3);
            map.put("file_name", cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
            map.put("file_count", Formatter.formatFileSize(context,size));
            map.put("image2_item", null);
            map.put("path",path);
            map.put("type",type);
            dataList.add(map);
        }
        return dataList;
    }
    public static List<HashMap<String, Object>> getVideoInfo(Context context){
        Cursor cursor= context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,null, null, null, null);
        if(cursor==null){
            return null;
        }

        List<HashMap<String, Object>> dataList=new ArrayList<>();
        while(cursor.moveToNext()){
            HashMap<String, Object> map=new HashMap<String, Object>();
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
            String type=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));

            long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
            map.put("image1_item", R.mipmap.icon_movie);
            map.put("file_name", cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)));
            map.put("file_count", Formatter.formatFileSize(context,size));
            map.put("image2_item", null);
            map.put("path",path);
            map.put("type",type);
            dataList.add(map);
        }
        return dataList;
    }
    public static List<HashMap<String, Object>> getWordInfo(Context context){
        String select="("+ MediaStore.Files.FileColumns.DATA+" LIKE '%.doc' or ";
        select+=MediaStore.Files.FileColumns.DATA+" LIKE '%.ppt' or ";
        select+=MediaStore.Files.FileColumns.DATA+" LIKE '%.xlsx' or ";
        select+=MediaStore.Files.FileColumns.DATA+" LIKE '%.xls' )";
        return getInfo(context,select);
    }
    public static List<HashMap<String, Object>> getTextInfo(Context context){
        String select="("+ MediaStore.Files.FileColumns.DATA+" LIKE '%.txt')";
        return getInfo(context,select);
    }
    public static List<HashMap<String, Object>> getZipInfo(Context context){
        String select="("+ MediaStore.Files.FileColumns.DATA+" LIKE '%.zip')";
        return getInfo(context,select);
    }
    private static List<HashMap<String, Object>> getInfo(Context context,String select){
        Cursor cursor = context.getContentResolver().query(MediaStore.Files.getContentUri("external"), null, select, null, null);
        if(cursor==null){
            return null;
        }
        List<HashMap<String, Object>> dataList=new ArrayList<>();
        while(cursor.moveToNext()){
            HashMap<String, Object> map=new HashMap<String, Object>();
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
            String type=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE));
            Long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE));
            map.put("image1_item", getResourceIdByName(path));
            map.put("file_name", path.substring(path.lastIndexOf("/")+1));
            map.put("file_count", Formatter.formatFileSize(context,size));
            map.put("image2_item", null);
            map.put("path",path);
            map.put("type",type);
            dataList.add(map);
        }
        return dataList;
    }
    public static List<HashMap<String,Object>> serachFiles(String name,String dir){
        List<HashMap<String, Object>> dataList=new ArrayList<>();
        File[] files=null;
        if(dir==null||dir.equals("")){
            File f=Environment.getExternalStorageDirectory();
            files=f.listFiles();
        }
        else{
            File f=new File(dir);
            if(f.exists()&&f.canRead()){
                files=f.listFiles();
            }
        }
        serachFiles(files,name,dataList);
        return dataList;
    }
    private static void serachFiles(File[] files,String name,List<HashMap<String, Object>> dataList){
        for(File f:files){
            if(f.getName().contains(name)){
                HashMap<String,Object> map=new HashMap<>();
                if(f.isDirectory()){
                    int[] counts=getChildCount(f);
                    map.put("image1_item", R.mipmap.icon_folder);
                    map.put("file_count", "文件:"+counts[1]+" 文件夹"+counts[0]);
                    map.put("image2_item", R.mipmap.icon_arrow);
                    map.put("type","Folder");
                    map.put("path",f.getPath());
                    map.put("file_name", f.getName());
                    dataList.add(map);
                    serachFiles(f.listFiles(),name,dataList);
                }
                else{
                    map.put("image1_item", getResourceIdByName(f.getName()));
                    map.put("file_count","");
                    map.put("image2_item", null);
                    map.put("type",getMimeType(f.getName()));
                    map.put("path",f.getPath());
                    map.put("file_name", f.getName());
                    dataList.add(map);
                }
            }else if(f.isDirectory()){
                serachFiles(f.listFiles(),name,dataList);
            }
        }
    }
    public static List<HashMap<String,Object>> getFilesByPath(Context context,String path){
        List<HashMap<String, Object>> dataList=new ArrayList<>();
        List<HashMap<String, Object>> dataList1=new ArrayList<>();
        List<HashMap<String, Object>> dataList2=new ArrayList<>();
        File file=new File(path);
//        if(!file.canRead()){
//            file.setReadable(true);
//        }
        File[] files=file.listFiles();
        for (File f:files) {
            HashMap<String, Object> map=new HashMap<String, Object>();
            if(f.isDirectory()){

                int[] counts=getChildCount(f);
                map.put("image1_item", R.mipmap.icon_folder);
                map.put("file_count", "文件:"+counts[1]+" 文件夹"+counts[0]);
                map.put("image2_item", R.mipmap.icon_arrow);
                map.put("type","Folder");
                map.put("path",f.getPath());
                map.put("file_name", f.getName());
                dataList1.add(map);
            }
            else{
                map.put("image1_item", getResourceIdByName(f.getName()));
                map.put("file_count",Formatter.formatFileSize(context,f.length()));
                map.put("image2_item", null);
                map.put("type",getMimeType(f.getName()));
                map.put("path",f.getPath());
                map.put("file_name", f.getName());
                dataList2.add(map);
            }
        }
        Collections.sort(dataList1,new AscSortByName("file_name"));
        Collections.sort(dataList2,new AscSortByName("file_name"));
        dataList.addAll(dataList1);
        dataList.addAll(dataList2);
        return dataList;
    }

    public static  List<HashMap<String,Object>> getStorageList(Context context)  {
        StorageManager storageManager = (StorageManager) context.getApplicationContext().getSystemService(Context.STORAGE_SERVICE);
        String []pathes=null;
        try {
            Class<?>[] paramClasses = {};
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", paramClasses);
            getVolumePathsMethod.setAccessible(true);
            Object[] params = {};
            pathes = (String[]) getVolumePathsMethod.invoke(storageManager, params);
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        List<HashMap<String,Object>> dataList=new ArrayList<HashMap<String, Object>>();
        int i=0;
        for (String p:pathes) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            String[] sizes=getSDCardSizeInfo(context,p);
            map.put("image1_item", R.mipmap.icon_disk);
            map.put("file_name", "存储空间"+i++);
            map.put("file_count", "总共:"+sizes[0]+"  可用:"+sizes[1]);
            map.put("image2_item", R.mipmap.icon_arrow);
            map.put("path",p);
            map.put("type","Folder");
            dataList.add(map);
        }
        return dataList;
    }

    public static int getResourceIdByName(String name){
        int index=name.lastIndexOf(".");
        int resId=R.mipmap.icon_no;
        String resourcsName="";
        if(index!=-1){
            resourcsName="icon_"+name.substring(index + 1).toLowerCase(Locale.US);
        }else {
            return resId;
        }
        Class mipmap = R.mipmap.class;
        try {
            Field field = mipmap.getField(resourcsName);
            resId = field.getInt(resourcsName);
        } catch (NoSuchFieldException e) {//如果没有在"mipmap"下找到imageName,将会返回0

        } catch (IllegalAccessException e) {

        }
        return resId;
    }

    public static void copyFiles(ArrayList<String> srcPaths,String destPath){
        File[] files=new File[srcPaths.size()];
        for(int i=0;i<srcPaths.size();i++){
            files[i]=new File(srcPaths.get(i));
        }
        copyFiles(files,new File(destPath));

    }

    public static void copyFiles(File[] srcFiles,File destFile){
        if(!destFile.exists()){
            return;
        }
        for(File f:srcFiles){
            if(!f.exists()){continue;}
            if(f.isDirectory()){
                File file=new File(destFile.getPath()+File.separator+f.getName());
                file.mkdir();
                copyFiles(f.listFiles(),file);
            }else{
                FileChannel fcin=null;
                FileChannel fcout=null;
                try {
                    fcin=new FileInputStream(f).getChannel();
                    fcout=new FileOutputStream(new File(destFile,f.getName())).getChannel();
                    long size=fcin.size();
                    fcin.transferTo(0,size,fcout);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    if(fcin!=null){
                        try {
                            fcin.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(fcout!=null){
                        try {
                            fcout.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }

        }
    }

    public static void moveFiles(ArrayList<String> srcPaths,String destPath){
        File[] files=new File[srcPaths.size()];
        for(int i=0;i<srcPaths.size();i++){
            files[i]=new File(srcPaths.get(i));
        }
        moveFiles(files,new File(destPath));

    }

    public static void moveFiles(File[] srcFiles,File destFile){
        if(!destFile.exists()){
            return;
        }
        for(File f:srcFiles){
            if(!f.exists()){continue;}
            if(f.isDirectory()){
                File file=new File(destFile.getPath()+File.separator+f.getName());
                file.mkdir();
                moveFiles(f.listFiles(),file);
            }else{
                FileChannel fcin=null;
                FileChannel fcout=null;
                try {
                    fcin=new FileInputStream(f).getChannel();
                    fcout=new FileOutputStream(new File(destFile,f.getName())).getChannel();
                    long size=fcin.size();
                    fcin.transferTo(0,size,fcout);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    if(fcin!=null){
                        try {
                            fcin.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(fcout!=null){
                        try {
                            fcout.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            f.setExecutable(true,false);
            f.delete();
        }
    }


    public static String[] getSDCardSizeInfo(Context content,String path){
        String[] info=new String[2];
        StatFs stat = new StatFs(path);
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        info[0] = Formatter.formatFileSize(content, blockSize * totalBlocks);
        long availableBlocks = stat.getAvailableBlocks();
        info[1] = Formatter.formatFileSize(content, blockSize * availableBlocks);
        return info;
    }
    public static String getMimeType(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileName.substring(index + 1).toLowerCase(Locale.US));
            if (type != null ) {
                return type;
            }
        }
        return "text/*";

    }
    private static int[] getChildCount(File file){
        int[] c=new int[]{0,0};
        File[] files=file.listFiles();
        for (File f:files) {
            if (f.isDirectory()){
                c[0]++;
            }else{
                c[1]++;
            }
        }
        return c;
    }

    private static class AscSortByName implements Comparator<HashMap<String,Object>>{
        private String id;
        public AscSortByName(String id){
            this.id=id;
        }
        @Override
        public int compare(HashMap<String, Object> lhs, HashMap<String, Object> rhs) {
            String lname=(String)lhs.get(id);
            String rname=(String)rhs.get(id);
            return lname.compareTo(rname);
        }
    }





}
