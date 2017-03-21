package com.example.wen.filemanager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager = null;
    private TabLayout tabs = null;
    private ArrayList<View> viewContainter = null;
    private ArrayList<String> titleContainter = null;

    private LinearLayout tab_gridView = null;
    private GridView gridView=null;
    private SimpleAdapter gridView_adapter = null;

    private RoundProgressBar roundProgressBar=null;

    private RelativeLayout tab_listView = null;
    private TabLayout tab_navigation=null;
    private ListView listView=null;

    private MyListViewAdapter myListViewAdapter=null;

    private GridView bootomGridView=null;
    private MyBootomGridViewAdapter bootomGridViewAdapter = null;
    private int posi=0;     //listview的位置
    private boolean isGridPagerBack=false;
    private int listViewItemStatus=0;
    private boolean isClearPaths=true;
    private int copyOrMove=0;

    private ArrayList<String> paths=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabs = (TabLayout) findViewById(R.id.tabs);
        viewContainter = new ArrayList<View>();
        titleContainter = new ArrayList<String>();
        paths=new ArrayList<>();
        getPermission();
        initActivity();
    }

    @Override
    public void onBackPressed() {
        int i=viewPager.getCurrentItem();
        switch (i){
            case 0:
                onGridPagerBack();
                break;
            case 1:
                onListPagerBack();
                break;
            default:
                super.onBackPressed();
                break;
        }
    }

    private void initActivity() {

        initGridViewPager();
        initListViewPager();
        initBootomGridView();
        //初始化PageView和TabLyout
        viewContainter.add(tab_gridView);
        viewContainter.add(tab_listView);
        titleContainter.add("分类");
        titleContainter.add("本地");
        tabs.setTabMode(TabLayout.MODE_FIXED);
        tabs.addTab(tabs.newTab().setText(titleContainter.get(0)));
        tabs.addTab(tabs.newTab().setText(titleContainter.get(1)));
        MyPagerAdapter myAdapter = new MyPagerAdapter(viewContainter, titleContainter);
        viewPager.setAdapter(myAdapter);
        tabs.setupWithViewPager(viewPager);

    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            gridView.setAdapter(gridView_adapter);
        }
    };

    void initGridViewPager(){
        tab_gridView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.tab_catagory, null);
        gridView=(GridView)tab_gridView.findViewById(R.id.tab_catagory_gridview);
        roundProgressBar=(RoundProgressBar)tab_gridView.findViewById(R.id.roundProgressBar);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isGridPagerBack=true;
                List<HashMap<String, Object>> data=null;
                String type=((TextView)view.findViewById(R.id.text)).getText().toString();
                switch (type){
                    case "图片":
                        data=FileUtils.getImageInfo(MainActivity.this);
                        break;
                    case "视频":
                        data=FileUtils.getVideoInfo(MainActivity.this);
                        break;
                    case "音乐":
                        data=FileUtils.getMusicInfo(MainActivity.this);
                        break;
                    case "文档":
                        data=FileUtils.getTextInfo(MainActivity.this);
                        break;
                    case "压缩包":
                        data=FileUtils.getZipInfo(MainActivity.this);
                        break;
                    case "Word":
                        data=FileUtils.getWordInfo(MainActivity.this);
                }
                tab_gridView.removeAllViews();
                RelativeLayout l=(RelativeLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.tab_local, null);
                ListView view1=(ListView)l.findViewById(R.id.myListView);
                view1.setAdapter(new SimpleAdapter(MainActivity.this,data, R.layout.item_listview, new String[]{"image1_item", "file_name", "file_count", "image2_item"}, new int[]{R.id.image1_item, R.id.file_name, R.id.file_count, R.id.image2_item}));
                tab_gridView.addView(l);

                view1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SimpleAdapter a=(SimpleAdapter) parent.getAdapter();
                        String path= ((HashMap)a.getItem(position)).get("path").toString();
                        String type=((HashMap)a.getItem(position)).get("type").toString();

                        Intent intent = new Intent("android.intent.action.VIEW");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Uri uri=Uri.fromFile(new File(path));
                        intent.setDataAndType(uri,type);
                        startActivity(intent);
                    }
                });
            }
        });
        handler.post(updateGrid);
    }

    void initListViewPager(){
        //初始化listView
        List<HashMap<String, Object>> data_listView=FileUtils.getStorageList(MainActivity.this);
        tab_listView = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_local, null);
        tab_navigation=(TabLayout)tab_listView.findViewById(R.id.tab_navigation);
        listView = (ListView)tab_listView.findViewById(R.id.myListView);
        bootomGridView=(GridView)tab_listView.findViewById(R.id.bottomGridView);
        initBootomGridView();

        myListViewAdapter=new MyListViewAdapter(this,data_listView);
        listView.setAdapter(myListViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyListViewAdapter a = (MyListViewAdapter) parent.getAdapter();
                String path = ((HashMap) a.getItem(position)).get("path").toString();
                if(listViewItemStatus==1){
                    myListViewAdapter.changeImage2At(position);
                    savePaths(path);
                }
                else {
                    String name = ((HashMap) a.getItem(position)).get("file_name").toString();
                    String type = ((HashMap) a.getItem(position)).get("type").toString();
                    if (type != "Folder") {
                        Intent intent = new Intent("android.intent.action.VIEW");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Uri uri = Uri.fromFile(new File(path));
                        intent.setDataAndType(uri, type);
                        startActivity(intent);
                    } else {
                        if (tab_navigation.getTabCount() == 0) {
                            ViewGroup.LayoutParams params = tab_navigation.getLayoutParams();
                            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                            tab_navigation.setLayoutParams(params);

                            ViewGroup.LayoutParams p=bootomGridView.getLayoutParams();
                            p.height=ViewGroup.LayoutParams.WRAP_CONTENT;
                            bootomGridView.setLayoutParams(p);

                        }
                        tab_navigation.addTab(tab_navigation.newTab().setText(name).setTag(new Object[]{path, listView.getFirstVisiblePosition()}), true);
                        LinearLayout linearLayout = (LinearLayout) tab_navigation.getChildAt(0);
                        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE | LinearLayout.SHOW_DIVIDER_END);
                        linearLayout.setDividerDrawable(ContextCompat.getDrawable(MainActivity.this, R.mipmap.icon_arrow1));
                        List<HashMap<String, Object>> list = FileUtils.getFilesByPath(MainActivity.this, path);
                        myListViewAdapter.setData(list);
                        //listView.setAdapter(myListViewAdapter);
                    }
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                paths.clear();
                listViewItemStatus=1;
                MyListViewAdapter a=(MyListViewAdapter) parent.getAdapter();
                savePaths(((HashMap) a.getItem(position)).get("path").toString());
                a.changeImage2(position);
                //listView.setAdapter(a);
                listView.setSelection(posi);
                initBootomGridView();
                return true;
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    posi=listView.getFirstVisiblePosition();
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        tab_navigation.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                List<HashMap<String, Object>> list=null;
                Object[] info=(Object[])tab.getTag();
                list = FileUtils.getFilesByPath(MainActivity.this, (String)info[0]);
                int index=(int)info[1];
                for(int i=tab.getPosition()+1,j=0;i<tab_navigation.getTabCount();){
                    if(j==0){
                        j++;
                        Object[] o = (Object[]) tab_navigation.getTabAt(i).getTag();
                        index=(int)o[1];
                    }
                    tab_navigation.removeTabAt(i);
                }
                myListViewAdapter.setData(list);

                listView.setAdapter(myListViewAdapter);
                listView.setSelection(index);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void initBootomGridView(){
        List<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();
        String[] tStrings=null;
        int[] icons=null;
        if(listViewItemStatus==0) {
            tStrings = new String[]{"添加", "搜索", "刷新", "取消", "粘贴"};
            icons = new int[]{R.mipmap.icon_add, R.mipmap.icon_serach, R.mipmap.icon_refresh, R.mipmap.icon_cancle, R.mipmap.icon_menu};

        }else if(listViewItemStatus==1){
            tStrings = new String[]{"复制", "移动", "删除", "全选", "重命名"};
            icons = new int[]{R.mipmap.icon_copy, R.mipmap.icon_move, R.mipmap.icon_trash, R.mipmap.icon_check_all, R.mipmap.icon_rename};
        }
        for (int i = 0; i < tStrings.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("image_bootom", icons[i]);
            map.put("text_bootom", tStrings[i]);
            dataList.add(map);
        }
        bootomGridViewAdapter = new MyBootomGridViewAdapter(MainActivity.this,dataList);
        bootomGridView.setAdapter(bootomGridViewAdapter);
        bootomGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text=((TextView)view.findViewById(R.id.text_bootom)).getText().toString();
                switch (text){
                    case "添加":
                        onAddBtn();
                        break;
                    case "搜索":
                        onSerchBtn();
                        break;
                    case "刷新":
                        onRefreshBtn();
                        break;
                    case "取消":
                        onCancleBtn();
                        break;
                    case "粘贴":
                        onPasteBtn();
                        break;
                    case "复制":
                        onCopyBtn();
                        break;
                    case "移动":
                        onMoveBtn();
                        break;
                    case "删除":
                        onDeleteBtn();
                        break;
                    case "全选":
                        break;
                    case "重命名":
                        onRenameBtn();
                        break;
                }
            }
        });
    }

    //更新gridView适配器
    Runnable updateGrid = new Runnable() {
        @Override
        public void run() {
            List<HashMap<String, Object>> dataList = FileUtils.getFilesCountInfo(MainActivity.this);
            gridView_adapter = new SimpleAdapter(MainActivity.this, dataList, R.layout.item_gridview, new String[]{"image", "text", "count"}, new int[]{R.id.image, R.id.text, R.id.count});
            handler.sendEmptyMessage(0);
            setStorageProcess();
        }
    };

   void setStorageProcess(){
       StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
       long totalBlocks = stat.getBlockCountLong();
       long availableBlocks = stat.getAvailableBlocksLong();
       final int index=(int)(100.0*(totalBlocks-availableBlocks)/totalBlocks);
       roundProgressBar.setMax(100);
       //roundProgressBar.setProgress(index);
       new Thread(new Runnable() {
           @Override
           public void run() {
               for(int i=0;i<=index;i++){
                   roundProgressBar.setProgress(i);
                   try {
                       Thread.sleep(20);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
               }
           }
       }).start();

   }

    void onGridPagerBack(){
        if(isGridPagerBack){
            tab_gridView.removeAllViews();
            tab_gridView.addView(gridView);
            tab_gridView.addView(roundProgressBar);
            isGridPagerBack=false;
            handler.post(updateGrid);
        }
        else{
            finish();
        }
    }

    void onListPagerBack(){
        if(isClearPaths) {
            paths.clear();
        }
        int i=posi;
        if(listViewItemStatus==1){
            listViewItemStatus=0;
            initBootomGridView();
            myListViewAdapter.changeImage2Next();
        }
        else {
            i = tab_navigation.getTabCount();
            List<HashMap<String, Object>> list = null;
            if (0 == i) {
                finish();
            } else if (1 == i) {
                tab_navigation.removeAllTabs();
                list = FileUtils.getStorageList(MainActivity.this);
                ViewGroup.LayoutParams params = tab_navigation.getLayoutParams();
                params.height = 0;
                tab_navigation.setLayoutParams(params);
                i = 0;

                ViewGroup.LayoutParams p=bootomGridView.getLayoutParams();
                p.height=0;
                bootomGridView.setLayoutParams(p);

            } else {
                Object[] info = (Object[]) tab_navigation.getTabAt(tab_navigation.getTabCount() - 2).getTag();
                String path = (String) info[0];
                info = (Object[]) tab_navigation.getTabAt(tab_navigation.getTabCount() - 1).getTag();
                i = (int) info[1];
                tab_navigation.removeTabAt(tab_navigation.getTabCount() - 1);
                list = FileUtils.getFilesByPath(MainActivity.this, path);
            }
            myListViewAdapter.setData(list);
        }
        listView.setAdapter(myListViewAdapter);
        listView.setSelection(i);
    }

    void onSerchBtn(){
        final LinearLayout dialogView=(LinearLayout) LayoutInflater.from(this).inflate(R.layout.serch_dialog_view,null);
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this).setTitle("搜索");
        builder.setView(dialogView);
        builder.setPositiveButton("搜索", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RadioGroup radioGroup=(RadioGroup) dialogView.findViewById(R.id.radioGroup);
                RadioButton rBtn=(RadioButton)radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
                String type=rBtn.getText().toString();
                String[] searchInfo=new String[2];
                searchInfo[0]=((EditText)dialogView.findViewById(R.id.file_name)).getText().toString();
                if(searchInfo[0].equals("")){
                    Toast.makeText(MainActivity.this,"请输入要搜索文件名字",Toast.LENGTH_SHORT).show();
                    return;
                }
                Object[] info=(Object[])tab_navigation.getTabAt(tab_navigation.getTabCount()-1).getTag();
                searchInfo[1]=(String)info[0];
                if(type.equals("所有文件夹")){
                    searchInfo[1]="";
                }
                Toast.makeText(MainActivity.this,"开始搜索,搜索完成后将提示",Toast.LENGTH_LONG).show();
                Observable.just(searchInfo)
                        .map(new Func1<String[], List<HashMap<String, Object>>>() {
                            @Override
                            public List<HashMap<String, Object>> call(String[] str){
                                return FileUtils.serachFiles(str[0],str[1]);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<List<HashMap<String, Object>>>() {
                            @Override
                            public void call(final List<HashMap<String, Object>> dataList) {
                                //Toast.makeText(MainActivity.this,"搜索结束",Toast.LENGTH_SHORT).show();
                                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("搜索完成")
                                        .setMessage("搜索结果:"+dataList.size()+"个文件,是否显示?")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                myListViewAdapter.setData(dataList);
                                            }
                                        })
                                        .setNegativeButton("取消",null).show();
                            }
                        });

            }
        }).setNegativeButton("取消",null);
        builder.show();
    }

    void onAddBtn(){
        final LinearLayout dialogView=(LinearLayout) LayoutInflater.from(this).inflate(R.layout.create_dialog_view,null);
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this).setTitle("新建");
        builder.setView(dialogView);
        builder.setPositiveButton("创建", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RadioGroup radioGroup=(RadioGroup) dialogView.findViewById(R.id.radioGroup);
                RadioButton rBtn=(RadioButton)radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
                String type=rBtn.getText().toString();
                String name=((EditText)dialogView.findViewById(R.id.file_name)).getText().toString();
                if(name.equals("")){
                    Toast.makeText(MainActivity.this,"请输入文件名字",Toast.LENGTH_SHORT).show();
                    return;
                }
                Object[] info=(Object[])tab_navigation.getTabAt(tab_navigation.getTabCount()-1).getTag();
                String path=(String)info[0];
                File file=null;
                if(type.equals("文件夹")){
                    file=new File(path+ File.separator+name);
                    if(file.exists()){
                        Toast.makeText(MainActivity.this,"该文件已存在",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    file.mkdir();
                }
                else {
                    file=new File(path+ File.separator+name+".txt");
                    if(file.exists()){
                        Toast.makeText(MainActivity.this,"该文件已存在",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(MainActivity.this,"文件创建成功",Toast.LENGTH_SHORT).show();
                onRefreshBtn();
            }
        }).setNegativeButton("取消",null);
        builder.show();

    }

    void onCopyBtn(){
        if(paths.size()<1){
            Toast.makeText(MainActivity.this,"请选择要复制的文件",Toast.LENGTH_SHORT).show();
            return;
        }
        isClearPaths=false;
        copyOrMove=0;
        onRefreshBtn();
        Toast.makeText(MainActivity.this,"请选择要复制到的文件夹",Toast.LENGTH_SHORT).show();
    }

    void onMoveBtn(){
        if(paths.size()<1){
            Toast.makeText(MainActivity.this,"请选择要移动的文件",Toast.LENGTH_SHORT).show();
            return;
        }
        isClearPaths=false;
        copyOrMove=1;
        onRefreshBtn();
        Toast.makeText(MainActivity.this,"请选择要移动到的文件夹",Toast.LENGTH_SHORT).show();
    }

    void onPasteBtn(){
        if(paths.size()<1){
            Toast.makeText(MainActivity.this,"没有选择文件",Toast.LENGTH_SHORT).show();
            return;
        }
        if(copyOrMove==0){
            Object[] info=(Object[])tab_navigation.getTabAt(tab_navigation.getTabCount()-1).getTag();
            FileUtils.copyFiles(paths,(String)info[0]);
            Toast.makeText(MainActivity.this,"已复制",Toast.LENGTH_SHORT).show();
        }
        else if(copyOrMove==1){
            Object[] info=(Object[])tab_navigation.getTabAt(tab_navigation.getTabCount()-1).getTag();
            FileUtils.moveFiles(paths,(String)info[0]);
            Toast.makeText(MainActivity.this,"已移动",Toast.LENGTH_SHORT).show();
        }
        paths.clear();
        isClearPaths=true;
        onRefreshBtn();
    }

    void onCancleBtn(){
        if(paths.size()>0) {
            paths.clear();
            //onRefreshBtn();
            Toast.makeText(MainActivity.this, "已取消", Toast.LENGTH_SHORT).show();
        }
    }

    void onDeleteBtn(){
        if(paths.size()==0){
            Toast.makeText(MainActivity.this,"请选择要删除的文件",Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("删除")
                .setMessage("确定删除文件?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File[] files=new File[paths.size()];
                        for(int i=0;i<paths.size();i++){
                            files[i]=new File(paths.get(i));
                        }
                        deleteFiles(files);
                        paths.clear();
                        onRefreshBtn();
                        //initBootomGridView();
                        Toast.makeText(MainActivity.this,"已删除",Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消",null).show();
    }

    void onSelectAllBtn(){

    }

    void onRenameBtn(){
        if(paths.size()!=1){
            Toast.makeText(MainActivity.this,"请选择要重命名的文件",Toast.LENGTH_SHORT).show();
            return;
        }
        final AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        final EditText e=new EditText(MainActivity.this);
        builder.setTitle("重命名");
        builder.setView(e);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final File file=new File(paths.get(0));
                final String newPath=file.getParentFile().getPath()+File.separator+e.getText().toString();
                if(new File(newPath).exists()){
                    new AlertDialog.Builder(MainActivity.this).setTitle("提示!")
                            .setMessage("文件已存在,是否覆盖?")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    file.renameTo(new File(newPath));
                                    Toast.makeText(MainActivity.this,"已重命名",Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("取消",null).show();
                }else{
                    file.renameTo(new File(newPath));
                    Toast.makeText(MainActivity.this,"已重命名",Toast.LENGTH_SHORT).show();
                }
                paths.clear();
                onRefreshBtn();
                //initBootomGridView();
            }
        });
        builder.setNegativeButton("取消",null).show();
    }

    void deleteFiles(File[] files){
        for(File f:files){
            if(!f.exists()){continue;}
            if(f.isDirectory()){
                deleteFiles(f.listFiles());
            }

            f.setExecutable(true,false);
            if(f.delete()){
            }
            Intent media = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(f));
            MainActivity.this.sendBroadcast(media);
        }
    }

    void onRefreshBtn(){
        listViewItemStatus=0;
        List<HashMap<String, Object>> list=null;
        Object[] info=(Object[])tab_navigation.getTabAt(tab_navigation.getTabCount()-1).getTag();
        list = FileUtils.getFilesByPath(MainActivity.this, (String)info[0]);
        myListViewAdapter.setData(list);
        listView.setAdapter(myListViewAdapter);
        initBootomGridView();
    }


    void savePaths(String path){
        if(paths.contains(path)){
            paths.remove(path);
        }else{
            paths.add(path);
        }
    }

    private void getPermission(){
        String[] p=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS};
        int i1=ContextCompat.checkSelfPermission(MainActivity.this,p[0] );
        int i2=ContextCompat.checkSelfPermission(MainActivity.this,p[1] );
        int i3=ContextCompat.checkSelfPermission(MainActivity.this,p[2] );
        if(i1!= PackageManager.PERMISSION_GRANTED || i2!= PackageManager.PERMISSION_GRANTED ||i3!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,p,1);
        }
    }
}

