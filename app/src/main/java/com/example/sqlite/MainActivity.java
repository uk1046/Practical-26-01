package com.example.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

/** @noinspection ALL*/
public class MainActivity extends AppCompatActivity {

    private EditText editTextName, editTextAge, editTextCity;
    private Button buttonAdd;
    private ListView listView;
    private ArrayAdapter<String> adapter;

    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextName = findViewById(R.id.editTextName);
        editTextAge = findViewById(R.id.editTextAge);
        editTextCity = findViewById(R.id.editTextCity);
        buttonAdd = findViewById(R.id.buttonAdd);
        listView = findViewById(R.id.listView);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Execute AsyncTask to insert data into database
                new InsertDataTask().execute(
                        editTextName.getText().toString(),
                        editTextAge.getText().toString(),
                        editTextCity.getText().toString()
                );
            }
        });
        new LoadDataTask().execute();
    }

    private class InsertDataTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_NAME, params[0]);
            values.put(DatabaseHelper.COLUMN_AGE, params[1]);
            values.put(DatabaseHelper.COLUMN_CITY, params[2]);
            database.insert(DatabaseHelper.TABLE_NAME, null, values);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new LoadDataTask().execute();
        }
    }
    private class LoadDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            adapter.clear();
            Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, null);
            if (cursor != null) {
                try {
                    while (cursor.moveToNext()) {
                        String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
                        String age = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_AGE));
                        String city = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CITY));
                        adapter.add("Name: " + name + ", Age: " + age + ", City: " + city);
                    }
                } finally {
                    cursor.close();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.notifyDataSetChanged();
        }
    }
}
