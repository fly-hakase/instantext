package edorevia.com.instantext;

import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class InstantextMain extends AppCompatActivity {

    private ListView listView;
    InstantextDBHelper dbHelper;
    SimpleCursorAdapter cursorAdapter;
    int cursorPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instantext_main);

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#007AA3")));

        dbHelper = new InstantextDBHelper(this);

        listView = (ListView)findViewById(R.id.listView);
        refreshListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView listView, View view,
                                    int position, long id) {
                cursorPosition = position;
                Cursor itemCursor = (Cursor) InstantextMain.this.listView.getItemAtPosition(cursorPosition);
                String name = itemCursor.getString(itemCursor.getColumnIndex(InstantextDBHelper.TEXT_COLUMN_NAME));
                String phoneNo = itemCursor.getString(itemCursor.getColumnIndex(InstantextDBHelper.TEXT_COLUMN_NUMBER));
                String message = itemCursor.getString(itemCursor.getColumnIndex(InstantextDBHelper.TEXT_COLUMN_TEXT));
                Intent intent = new Intent(getApplicationContext(), InstantextSend.class);
                intent.putExtra("name", name);
                intent.putExtra("phoneNumber", phoneNo);
                intent.putExtra("messageText", message);
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                cursorPosition = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(InstantextMain.this);
                String items[] = new String[]{
                        "Edit",
                        "Delete"
                };
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        Cursor itemCursor;
                        int dbId;
                        switch (item) {
                            case 0:
                                itemCursor = (Cursor) InstantextMain.this.listView.getItemAtPosition(cursorPosition);
                                dbId = itemCursor.getInt(itemCursor.getColumnIndex(InstantextDBHelper.TEXT_COLUMN_ID));
                                Intent intent = new Intent(getApplicationContext(), InstantextCreator.class);
                                intent.putExtra("dbId", dbId);
                                startActivity(intent);
                                break;
                            case 1:
                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int item) {
                                        Cursor itemCursor;
                                        int dbId;
                                        switch (item) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                itemCursor = (Cursor) InstantextMain.this.listView.getItemAtPosition(cursorPosition);
                                                dbId = itemCursor.getInt(itemCursor.getColumnIndex(InstantextDBHelper.TEXT_COLUMN_ID));
                                                dbHelper.deleteText(dbId);
                                                InstantextWidget.deletedWidgetInfo(getApplicationContext(), AppWidgetManager.getInstance(getApplicationContext()), dbId);
                                                InstantextMain.this.refreshListView();
                                                break;
                                            case DialogInterface.BUTTON_NEGATIVE:
                                                break;
                                        }
                                    }
                                };
                                AlertDialog.Builder delBuilder = new AlertDialog.Builder(InstantextMain.this);
                                delBuilder.setMessage(R.string.delete_prompt).setPositiveButton(R.string.yes, dialogClickListener).setNegativeButton(R.string.no, dialogClickListener).show();
                                break;
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_instantext_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_about) {
            AlertDialog.Builder builder = new AlertDialog.Builder(InstantextMain.this);
            builder.setTitle("About " + getString(R.string.app_name))
                    .setMessage("Version 1.0\n" +
                            "© Edorevia 2015. \n\n" +
                            getString(R.string.app_name) + " was made to allow you to create and send " +
                            "quick, customized texts at the touch of a button.")
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // just close the dialog
                                }
                            }).show();
            return true;
        } else if (id == R.id.new_text) {
            Intent intent = new Intent(getApplicationContext(), InstantextCreator.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void refreshListView() {
        Cursor cursor = dbHelper.getAllTexts();
        String [] columns = new String[] {
                InstantextDBHelper.TEXT_COLUMN_NAME,
                InstantextDBHelper.TEXT_COLUMN_TEXT
        };

        int [] widgets = new int[] {
                R.id.text_name,
                R.id.message_text,
        };
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.text_info,
                cursor, columns, widgets, 0);
        listView.setAdapter(cursorAdapter);

        TextView noMessagePrompt = (TextView) findViewById(R.id.no_message_prompt);
        if (listView.getAdapter().getCount() > 0) {
            listView.setVisibility(View.VISIBLE);
            noMessagePrompt.setVisibility(View.GONE);
        } else {
            listView.setVisibility(View.GONE);
            noMessagePrompt.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshListView();
    }
}
