package com.xiaoxian.jykz.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.xiaoxian.jykz.R;
import com.xiaoxian.jykz.util.Utils;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchShopActivity extends AppCompatActivity {

    private ImageView imgReturnSearch;
    private EditText editSearch;
    private Button btnSearch;
    private ListView listSearch;
    private Button btnClearHistory;
    private ArrayList<String> dataList = null;
    private String history = null;
    private SharedPreferences sharedPreferences = null;

    //private ArrayAdapter<String> adapter=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setStatusBarColor(this, getResources().getColor(R.color.colorPrimary));
        setContentView(R.layout.activity_search_shop);
        initData();
        initView();

    }

    private void initView(){
        imgReturnSearch=(ImageView)findViewById(R.id.img_return_search);
        imgReturnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editSearch=(EditText)findViewById(R.id.edit_search_serach);
        btnSearch=(Button)findViewById(R.id.btn_search_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(editSearch.getText().toString().trim());
            }
        });

        listSearch=(ListView)findViewById(R.id.list_search_search);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchShopActivity.this, android.R.layout.simple_list_item_1, dataList);
        listSearch.setAdapter(adapter);
        listSearch .setTextFilterEnabled(true);
        listSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                search(dataList.get(position));
            }
        });

        btnClearHistory=(Button)findViewById(R.id.btn_clear_history_search);
        btnClearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                dataList.clear();
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void initData(){
        sharedPreferences= getSharedPreferences("data", MODE_PRIVATE);
        history = sharedPreferences.getString("history", "");
        String[] pre = history.split("\n");
        dataList = new ArrayList<String>();
        for (int i=0; i<pre.length; i++) {
            if (!pre[i].isEmpty()) {
                dataList.add(pre[i]);
            }
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchShopActivity.this, android.R.layout.simple_list_item_1, dataList);
        ListView listView = (ListView)findViewById(R.id.list_search_search);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                search(dataList.get(position));
            }
        });
    }
    private void search(final String content) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String name = content;
                if (!name.isEmpty()) {
                    boolean flag = true;
                    for (int i=0; i<dataList.size(); i++) {
                        if (name.equals(dataList.get(i))) {
                            flag = false;
                            break;
                        }
                    }

                    if (flag) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        if (history.isEmpty()) {
                            editor.putString("history", name);
                        } else {
                            editor.putString("history", name + "\n" + history);
                        }
                        editor.apply();
                    }
                }
                //Toast.makeText(SearchShopActivity.this, "name"+name, Toast.LENGTH_SHORT).show();
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SearchShopActivity.this, "加载中", Toast.LENGTH_SHORT).show();
                        }
                    });

                    Intent intent=new Intent(SearchShopActivity.this,CommodityListActivity.class);
                    intent.putExtra( "searchInfo",name );
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
