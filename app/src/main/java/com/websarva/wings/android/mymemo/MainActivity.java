package com.websarva.wings.android.mymemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ListView mLvMemo;
    //アダプタ用リスト
    private List<Map<String, String>> mMemoList;
    //アダプタ用定数配列
    private final String[] FROM = {"title","shortContent"};
    private final int[] TO = {android.R.id.text1, android.R.id.text2};

    //データベースヘルパー
    private DatabaseHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
            ListViewのリスナ設定をする
            （アダプター生成、セット等）
         */
        //リストビュー取得
        mLvMemo = findViewById(R.id.lvMemo);
        //リスナ登録
        mLvMemo.setOnItemClickListener(new ListItemClickListener());
        //ヘルパー生成
        mHelper = new DatabaseHelper(MainActivity.this);
    }

    @Override
    public void onResume(){
        super.onResume();
        //DBからデータ取得、ListViewにセット
        //アダプタ用リスト作成
        mMemoList = new ArrayList<Map<String, String>>();
        Map<String, String> memo = null;
        //DBからデータ取得し、mListに格納する
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String sqlSelect = "SELECT * FROM memos";
        Cursor cursor = db.rawQuery(sqlSelect, null);

        //結果セットをアダプター用メモリストに格納する
        while(cursor.moveToNext()){
            memo = new HashMap<String, String>();
            memo.put("memoId"          , cursor.getString(0));
            memo.put("title"       , cursor.getString(1));
            memo.put("content"     , cursor.getString(2));
            memo.put("shortContent", cursor.getString(2));
            memo.put("updDate"     , cursor.getString(3));
            //改行または10文字以上連続の場合、１行目以降「・・・」で表示
            if(cursor.getString(2).contains("\n")){
                int firstPos = cursor.getString(2).indexOf("\n");
                if(firstPos > 15){
                    memo.put("shortContent", cursor.getString(2).substring(0,15) + " ...");
                }else{
                    memo.put("shortContent", cursor.getString(2).substring(0,firstPos) + " ...");
                }
            }else if(cursor.getString(2).length() > 15){
                memo.put("shortContent", cursor.getString(2).substring(0,15) + " ...");
            }

            mMemoList.add(memo);
        }
        //mMemoListをupdDateで降順にソート
        Collections.sort(mMemoList, (map1,map2) -> {
            return map2.get("updDate").compareTo(map1.get("updDate"));
        });
        //アダプタ生成
        SimpleAdapter adapter = new SimpleAdapter(
                MainActivity.this,
                mMemoList,
                android.R.layout.simple_list_item_2,
                FROM,
                TO
        );
        //ListViewにアダプタをセット
        mLvMemo = findViewById(R.id.lvMemo);
        mLvMemo.setAdapter(adapter);
    }


    //リストがタップされたときの処理（リスナ）
    private class ListItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            //該当データ取得
            Map<String, String> memo = (Map<String, String>)parent.getItemAtPosition(position);
            String memoId = memo.get("memoId");
            String title = memo.get("title");
            String content = memo.get("content");

            Intent intent = new Intent(MainActivity.this, EditActivity.class);
            intent.putExtra("memoId", memoId);
            intent.putExtra("title", title);
            intent.putExtra("content", content);
            startActivity(intent);
        }
    }

    //メニューインフレート
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    //メニュー選択処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.menu_add:
                //新規追加なので、単純に画面遷移する
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected  void onDestroy(){
        mHelper.close();
        super.onDestroy();
    }
}