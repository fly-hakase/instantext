package edorevia.com.instantext;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * The configuration screen for the {@link InstantextWidget InstantextWidget} AppWidget.
 */
public class InstantextWidgetConfigureActivity extends Activity {

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private static final String PREFS_NAME = "edorevia.com.instantext.InstantextWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    private ListView listView;
    InstantextDBHelper dbHelper;

    public InstantextWidgetConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.instantext_widget_configure);

        dbHelper = new InstantextDBHelper(this);

        final Cursor cursor = dbHelper.getAllTexts();
        String [] columns = new String[] {
                InstantextDBHelper.TEXT_COLUMN_NAME,
                InstantextDBHelper.TEXT_COLUMN_TEXT
        };

        int [] widgets = new int[] {
                R.id.text_name,
                R.id.message_text
        };

        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.text_info,
                cursor, columns, widgets, 0);
        listView = (ListView)findViewById(R.id.listView2);
        listView.setAdapter(cursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView listView, View view,
                                    int position, long id) {
                final Context context = InstantextWidgetConfigureActivity.this;

                // When the button is clicked, store the string locally
                Cursor itemCursor = (Cursor) InstantextWidgetConfigureActivity.this.listView.getItemAtPosition(position);
                int widgetDbId = itemCursor.getInt(itemCursor.getColumnIndex(InstantextDBHelper.TEXT_COLUMN_ID));
                String widgetName = itemCursor.getString(itemCursor.getColumnIndex(InstantextDBHelper.TEXT_COLUMN_NAME));
                String widgetNumber = itemCursor.getString(itemCursor.getColumnIndex(InstantextDBHelper.TEXT_COLUMN_NUMBER));
                String widgetText = itemCursor.getString(itemCursor.getColumnIndex(InstantextDBHelper.TEXT_COLUMN_TEXT));

                saveIdPref(context, mAppWidgetId, widgetDbId, "dbId");
                saveTitlePref(context, mAppWidgetId, widgetName, "name");
                saveTitlePref(context, mAppWidgetId, widgetNumber, "number");
                saveTitlePref(context, mAppWidgetId, widgetText, "text");

                // It is the responsibility of the configuration activity to update the app widget
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                InstantextWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

                // Make sure we pass back the original appWidgetId
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            }
        });

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text, String prefToSave) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId + prefToSave, text);
        prefs.commit();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId, String prefToLoad) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId + prefToLoad, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return "NO VALUE FOUND";
        }
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveIdPref(Context context, int appWidgetId, int id, String prefToSave) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY + appWidgetId + prefToSave, id);
        prefs.commit();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static int loadIdPref(Context context, int appWidgetId, String prefToLoad) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        int titleValue = prefs.getInt(PREF_PREFIX_KEY + appWidgetId + prefToLoad, -1);
        if (titleValue != -1) {
            return titleValue;
        } else {
            return -1;
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.commit();
    }
}

