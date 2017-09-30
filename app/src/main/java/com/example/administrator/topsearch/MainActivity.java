package com.example.administrator.topsearch;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.administrator.topsearch.adapter.SearchAdapter;
import com.example.administrator.topsearch.bean.Bean;
import com.example.administrator.topsearch.myview.SearchView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements SearchView.SearchViewListener {
    private String josnData =
            "[{\"iconId\":22222,\"title\":\"松仁玉米88\",\"content\":\"玉米\",\"comments\":\"1\"}," +
            "{\"iconId\":2222,\"title\":\"松仁玉米1\",\"content\":\"松仁玉米\",\"comments\":\"2\"}," +
            "{\"iconId\":22221,\"title\":\"松仁玉米2\",\"content\":\"松仁玉米\",\"comments\":\"3\"}," +
            "{\"iconId\":22222,\"title\":\"松仁玉米3\",\"content\":\"松仁玉米\",\"comments\":\"4\"}," +
            "{\"iconId\":22223,\"title\":\"松仁玉米4\",\"content\":\"松仁玉米\",\"comments\":\"5\"}," +
            "{\"iconId\":22224,\"title\":\"松仁玉米5\",\"content\":\"松仁玉米\",\"comments\":\"6\"}," +
            "{\"iconId\":22224,\"title\":\"松仁玉米6\",\"content\":\"松仁玉米\",\"comments\":\"7\"}," +
            "{\"iconId\":22224,\"title\":\"松仁玉米7\",\"content\":\"松仁玉米\",\"comments\":\"8\"}," +
            "{\"iconId\":22224,\"title\":\"松仁玉米8\",\"content\":\"松仁玉米\",\"comments\":\"9\"}," +
            "{\"iconId\":22224,\"title\":\"松仁玉米9\",\"content\":\"松仁玉米\",\"comments\":\"10\"}," +
            "{\"iconId\":22224,\"title\":\"松仁玉米11\",\"content\":\"松仁玉米\",\"comments\":\"11\"}]";
    private ListView lvResults;//搜索结果列表项
    private SearchView searchView;
    private ArrayAdapter<String> hintAdapter;
    private ArrayAdapter<String> autoCompleteAdapter;
    private SearchAdapter resultAdapter;
    private List<Bean> dbData;
    private List<String> hintData;//热搜版数据
    private List<String> autoCompleteData;
    private List<Bean> resultData;
    private static int DEFAULT_HINT_SIZE = 4;//默认提示框想是项的个数
    private static int hintSize = DEFAULT_HINT_SIZE;
    public static void setHintSize(int hintSize) {
        MainActivity.hintSize = hintSize;
    }//设置提示框显示项的个数
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
            initData();

        initViews();
    }

    private void initViews() {
        lvResults = (ListView) findViewById(R.id.main_lv_search_results);
        searchView = (SearchView) findViewById(R.id.main_search_layout);
        praseFromJosn(josnData);
        registerForContextMenu(lvResults);
        //设置监听
        searchView.setSearchViewListener(this);
        //设置adapter
        searchView.setTipsHintAdapter(hintAdapter);
        searchView.setAutoCompleteAdapter(autoCompleteAdapter);

        lvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(MainActivity.this, position + "", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public List praseFromJosn(String jsonData) {
        Type ListType = new TypeToken<ArrayList<Bean>>() {
        }.getType();
        Gson gson = new Gson();
        dbData = gson.fromJson(jsonData, ListType);
        return dbData;

    }


    private void initData()  {
        getAutoCompleteData(null);
        getHintData();
        getResultData(null);
    }


    /**
     * 获取热搜版data 和adapter
     */
    private void getHintData() {
        hintData = new ArrayList<>(hintSize);
        for (int i = 1; i <= hintSize; i++) {
            hintData.add("热搜版" + i + "：Android自定义View");
        }
        hintAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, hintData);
    }

    private void getAutoCompleteData(String text) {
        if (autoCompleteData == null) {
            //初始化
            autoCompleteData = new ArrayList<>(hintSize);
        } else {
            // 根据text 获取auto data
            autoCompleteData.clear();
            for (int i = 0, count = 0; i < dbData.size()
                    && count < hintSize; i++) {
                if (dbData.get(i).getTitle().contains(text.trim())) {
                    autoCompleteData.add(dbData.get(i).getTitle());
                    count++;
                }
            }
        }
        if (autoCompleteAdapter == null) {
            autoCompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, autoCompleteData);
        } else {
            autoCompleteAdapter.notifyDataSetChanged();
        }
    }

    private void getResultData(String text) {
        if (resultData == null) {
            // 初始化
            resultData = new ArrayList<>();
        } else {
            resultData.clear();
            for (int i = 0; i < dbData.size(); i++) {
                if (dbData.get(i).getTitle().contains(text.trim())) {
                    resultData.add(dbData.get(i));
                }
            }
        }
        if (resultAdapter == null) {
            resultAdapter = new SearchAdapter(this, resultData, R.layout.item_bean_list);
        } else {
            resultAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onRefreshAutoComplete(String text) {//文本改变时，自动更新数据
        //更新数据
        getAutoCompleteData(text);
    }

    @Override
    public void onSearch(String text) {//点击搜索键时触发
        //更新result数据
        getResultData(text);
        lvResults.setVisibility(View.VISIBLE);
        //第一次获取结果 还未配置适配器
        if (lvResults.getAdapter() == null) {
            //获取搜索数据 设置适配器
            lvResults.setAdapter(resultAdapter);
        } else {
            //更新搜索数据
            resultAdapter.notifyDataSetChanged();
        }
        Toast.makeText(this, "完成搜素", Toast.LENGTH_SHORT).show();
    }

}