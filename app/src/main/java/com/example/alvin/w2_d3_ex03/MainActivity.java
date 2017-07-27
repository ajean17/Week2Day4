package com.example.alvin.w2_d3_ex03;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.alvin.w2_d3_ex03.FeedReaderContract.FeedEntry;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = MainActivity.class.getSimpleName() + "_TAG";//For Debugging Purposes
    private DBHelper helper;
    private SQLiteDatabase database;

    EditText titleET;
    EditText subtitleET;
    EditText newtitleET;
    TextView resultTV;
    private Button saveBtn;
    private Button readBtn;
    private Button updateBtn;
    private Button deleteBtn;
    //Lookup breakpoints
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titleET = (EditText) findViewById(R.id.et_title);
        subtitleET = (EditText) findViewById(R.id.et_subtitle);
        newtitleET = (EditText) findViewById(R.id.et_newtitle);
        resultTV = (TextView) findViewById(R.id.tv_result);

        saveBtn = (Button) findViewById(R.id.btn_save);
        saveBtn.setOnClickListener(this);
        readBtn = (Button) findViewById(R.id.btn_read);
        readBtn.setOnClickListener(this);
        updateBtn = (Button) findViewById(R.id.btn_update);
        updateBtn.setOnClickListener(this);
        deleteBtn = (Button) findViewById(R.id.btn_delete);
        deleteBtn.setOnClickListener(this);

        helper = new DBHelper(this);
        database = helper.getWritableDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //updateRecord();
        //deleteRecord();
        //saveRecord();
        //readRecord();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
    }

    private void saveRecord() {
        String title = titleET.getText().toString(); //"Record title";
        String subtitle = subtitleET.getText().toString(); //"Record subtitle";

        ContentValues values = new ContentValues();//Prevents things like SQL injection, while allowing for prepared statements;
        values.put(FeedEntry.COLUMN_NAME_TITLE, title);
        values.put(FeedEntry.COLUMN_NAME_SUBTITLE, subtitle);

        long recordID = database.insert(
                FeedEntry.TABLE_NAME,
                null,
                values
        );
        if(recordID > 0) {
            Log.d(TAG, "saveRecord: Record saved.");
        } else {
            Log.d(TAG, "saveRecord: Record not saved.");
        }
        titleET.setText("");
        subtitleET.setText("");
    }

    private void readRecord() {
        resultTV.setText(R.string.lbl_read_result);
        String[] projection = {
                FeedEntry._ID,
                FeedEntry.COLUMN_NAME_TITLE,
                FeedEntry.COLUMN_NAME_SUBTITLE
        };
        String selection = FeedEntry.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArg = {
                "Record title"
        };
        String sortOrder = FeedEntry.COLUMN_NAME_SUBTITLE + "DESC";
        Cursor cursor = database.query( //**Requires 7 parameters**
                FeedEntry.TABLE_NAME,   //Table
                projection,             //Projection
                null,                   //Selection (WHERE)
                null,                   //Values for selection
                null,                   //Group by
                null,                   //Filters
                null                    //Sort Order
        );

        while(cursor.moveToNext()) {
            long entryID = cursor.getLong(cursor.getColumnIndexOrThrow(FeedEntry._ID));
            String entryTitle = cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TITLE));
            String entrySubtitle = cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_SUBTITLE));
            String entryResult = "\nRecord ID: " + entryID + "   Title: " + entryTitle + "   Subtitle: " + entrySubtitle;
            Log.d(TAG, entryResult);
            resultTV.append(String.format(getString(R.string.lbl_result), entryResult));
        }
    }

    public void deleteRecord() {
        resultTV.setText(R.string.lbl_read_result);
        String selection = FeedEntry.COLUMN_NAME_TITLE + " LIKE ?";
        String selectionArgs[] = {
            titleET.getText().toString()
        };
        int deleted = database.delete(
                FeedEntry.TABLE_NAME,
                selection,
                selectionArgs
        );
        String deleteResult;
        if(deleted > 0) {
            deleteResult = "deleteRecord: Record deleted.";
            Log.d(TAG, deleteResult);
        } else {
            deleteResult = "deleteRecord: Record not deleted.";
            Log.d(TAG, deleteResult);
        }
        resultTV.append(String.format(getString(R.string.lbl_result), deleteResult));
    }

    private void updateRecord() {
        resultTV.setText(R.string.lbl_read_result);
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_TITLE, newtitleET.getText().toString());

        String selection = FeedEntry.COLUMN_NAME_TITLE + " LIKE ?";
        String [] selectionArgs = {
          titleET.getText().toString()
        };

        int count = database.update(
                FeedEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
        String updatedResult;
        if(count > 0) {
            updatedResult = "updateRecord: Updated records: " + count;
            Log.d(TAG, updatedResult);
        } else {
            updatedResult = "updateRecord: Records not updated.";
            Log.d(TAG, updatedResult);
        }
        newtitleET.setText("");
        resultTV.append(String.format(getString(R.string.lbl_result), updatedResult));
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_save:
                saveRecord();
                break;
            case R.id.btn_read:
                readRecord();
                break;
            case R.id.btn_update:
                updateRecord();
                break;
            case R.id.btn_delete:
                deleteRecord();
                break;
        }
    }
}
