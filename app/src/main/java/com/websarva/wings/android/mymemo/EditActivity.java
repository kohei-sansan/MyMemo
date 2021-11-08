package com.websarva.wings.android.mymemo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
　メモ帳の編集画面
 */
public class EditActivity extends AppCompatActivity {
    //編集画面のタイトル、内容
    private EditText etTitle;
    private EditText etContent;
    //更新前MAP
    Map<String, String> dataMapBefore;
    Map<String, String> dataMapAfter;
    //データベースヘルパー
    private DatabaseHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        //戻るボタン有効化
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //ヘルパー生成
        mHelper = new DatabaseHelper(EditActivity.this);
    }

    //戻るボタン押下時処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        boolean returnVal = true;

        switch(item.getItemId()){
            case android.R.id.home:
                //入力チェック
                if(etTitle.getText().toString().isEmpty()){
                    //警告を表示する
                    Toast.makeText(EditActivity.this, R.string.emptyAlert, Toast.LENGTH_SHORT).show();
                    return true;
                }
                //新規作成の場合
                if(dataMapBefore.get("memoId") == null){
                    //新規登録
                    dataMapAfter = new HashMap<String, String>();
                    String currentDate = new Date().toString();
                    dataMapAfter.put("title", etTitle.getText().toString());
                    dataMapAfter.put("content", etContent.getText().toString());

                    SQLiteDatabase db = mHelper.getWritableDatabase();
                    String sqlInsert = "INSERT INTO memos VALUES(?,?,?,?)";

                    SQLiteStatement stmt = db.compileStatement(sqlInsert);
                    stmt.bindString(1, currentDate);
                    stmt.bindString(2, dataMapAfter.get("title"));
                    stmt.bindString(3, dataMapAfter.get("content"));
                    stmt.bindString(4, currentDate);
                    stmt.executeInsert();

                    finish();
                }else{
                    //DB更新処理
                    String currentDate = new Date().toString();
                    dataMapAfter = new HashMap<String, String>();
                    dataMapAfter.put("title", etTitle.getText().toString());
                    dataMapAfter.put("content", etContent.getText().toString());

                    if(dataMapBefore.get("title").equals(dataMapAfter.get("title"))
                            && dataMapBefore.get("content").equals(dataMapAfter.get("content"))){
                        finish();
                    }
                    SQLiteDatabase db = mHelper.getWritableDatabase();
                    String sqlUpdate = "UPDATE memos SET title = ?, content = ?, upddate = ? WHERE _id = ?";
                    SQLiteStatement stmt = db.compileStatement(sqlUpdate);
                    stmt.bindString(1, dataMapAfter.get("title"));
                    stmt.bindString(2, dataMapAfter.get("content"));
                    stmt.bindString(3, currentDate);
                    stmt.bindString(4, dataMapBefore.get("memoId"));
                    stmt.executeUpdateDelete();

                    finish();
                }
                break;
            case R.id.edit_delete:
                //削除ボタン押下時処理
                SQLiteDatabase db = mHelper.getWritableDatabase();
                String sqlUpdate = "DELETE FROM memos WHERE _id = ?";
                SQLiteStatement stmt = db.compileStatement(sqlUpdate);
                stmt.bindString(1, dataMapBefore.get("memoId"));
                stmt.executeUpdateDelete();
                //削除メッセージを表示する
                Toast.makeText(EditActivity.this, R.string.deleteMsg, Toast.LENGTH_SHORT).show();

                finish();
                break;
            default:
                returnVal = super.onOptionsItemSelected(item);
        }
        return returnVal;
    }
    //メニューインフレート
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit, menu);
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        //リストビュー押下での遷移はメモの内容を表示する
        Intent intent = getIntent();

        dataMapBefore = new HashMap<String, String>();
        dataMapBefore.put("memoId",intent.getStringExtra("memoId"));
        dataMapBefore.put("title"  ,intent.getStringExtra("title"));
        dataMapBefore.put("content",intent.getStringExtra("content"));

        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        //リストビュー押下後の場合は内容をセット
        if(dataMapBefore.get("memoId") != null){
            etTitle.setText(dataMapBefore.get("title"));
            etContent.setText(dataMapBefore.get("content"));
        }else{
            etTitle.setText("");
            etContent.setText("");
        }
    }

    @Override
    protected  void onDestroy(){
        mHelper.close();
        super.onDestroy();
    }
}