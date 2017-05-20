package edorevia.com.instantext;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link InstantextWidgetConfigureActivity InstantextWidgetConfigureActivity}
 */
public class InstantextWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        //super.onUpdate(context, appWidgetManager, appWidgetIds);

        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            InstantextWidgetConfigureActivity.deleteTitlePref(context, appWidgetIds[i]);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        try {
            int dbId = InstantextWidgetConfigureActivity.loadIdPref(context, appWidgetId, "dbId");
            if (dbId != -1) {
                InstantextDBHelper dbHelper = new InstantextDBHelper(context);
                Cursor dbCursor = dbHelper.getText(dbId);
                dbCursor.moveToFirst();

                String widgetName = dbCursor.getString(dbCursor.getColumnIndex(InstantextDBHelper.TEXT_COLUMN_NAME));
                String widgetNumber = dbCursor.getString(dbCursor.getColumnIndex(InstantextDBHelper.TEXT_COLUMN_NUMBER));
                String widgetText = dbCursor.getString(dbCursor.getColumnIndex(InstantextDBHelper.TEXT_COLUMN_TEXT));

                // Construct the RemoteViews object
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.instantext_widget);

                views.setTextViewText(R.id.appwidget_name, widgetName);
                views.setTextViewText(R.id.appwidget_text, widgetText);

                // Create pending intent so widget on click brings up InstantextSend activity
                Intent intent = new Intent(context, InstantextSend.class);
                intent.putExtra(AppWidgetManager.
                        EXTRA_APPWIDGET_ID, appWidgetId);
                intent.putExtra("name", widgetName);
                intent.putExtra("phoneNumber", widgetNumber);
                intent.putExtra("messageText", widgetText);

                PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);

                // Instruct the widget manager to update the widget
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void updateWidgetByDbId(Context context, AppWidgetManager appWidgetManager, int dbId) {
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, InstantextWidget.class));
        int widgetDbId;
        for (int i=0; i < appWidgetIds.length; i++) {
            widgetDbId = InstantextWidgetConfigureActivity.loadIdPref(context, appWidgetIds[i], "dbId");
            if (widgetDbId == dbId) {
                updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
            }
        }
    }

    static void deletedWidgetInfo(Context context, AppWidgetManager appWidgetManager, int dbId) {
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, InstantextWidget.class));
        int widgetDbId;
        for (int i=0; i < appWidgetIds.length; i++) {
            widgetDbId = InstantextWidgetConfigureActivity.loadIdPref(context, appWidgetIds[i], "dbId");
            if (widgetDbId == dbId) {
                InstantextWidgetConfigureActivity.deleteTitlePref(context, appWidgetIds[i]);
            }
        }
    }
}

