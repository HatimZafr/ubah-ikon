package xyz.shabrr.iconchanger

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast

/**
 * Widget home screen "Ubah Ikon": grid warna 3x3. Ketuk satu warna di widget
 * langsung mengganti ikon launcher, tanpa membuka aplikasi. Tanda centang
 * menunjukkan warna yang sedang aktif.
 */
class IconWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) {
            updateWidget(context, appWidgetManager, id)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_CHANGE_ICON) {
            val iconId = intent.getStringExtra(EXTRA_ICON_ID) ?: return
            val icon = APP_ICONS.firstOrNull { it.id == iconId } ?: return
            applyIcon(context, icon)
            updateAllWidgets(context)
            Toast.makeText(context, "Ikon diubah ke ${icon.label}", Toast.LENGTH_SHORT).show()
            return
        }
        super.onReceive(context, intent)
    }

    companion object {
        const val ACTION_CHANGE_ICON = "xyz.shabrr.iconchanger.ACTION_CHANGE_ICON"
        const val EXTRA_ICON_ID = "icon_id"

        /** Segarkan semua widget yang terpasang. Dipanggil juga dari MainActivity. */
        fun updateAllWidgets(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                ComponentName(context, IconWidgetProvider::class.java),
            )
            for (id in ids) {
                updateWidget(context, manager, id)
            }
        }

        private fun updateWidget(context: Context, manager: AppWidgetManager, widgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_icon_changer)
            val currentId = getCurrentIconId(context)
            for ((index, icon) in APP_ICONS.withIndex()) {
                val (cellId, checkId) = CELL_IDS[icon.id] ?: continue
                val intent = Intent(context, IconWidgetProvider::class.java).apply {
                    action = ACTION_CHANGE_ICON
                    putExtra(EXTRA_ICON_ID, icon.id)
                }
                val pending = PendingIntent.getBroadcast(
                    context,
                    index,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )
                views.setOnClickPendingIntent(cellId, pending)
                views.setViewVisibility(
                    checkId,
                    if (icon.id == currentId) View.VISIBLE else View.GONE,
                )
            }
            manager.updateAppWidget(widgetId, views)
        }

        private val CELL_IDS = mapOf(
            "default" to (R.id.cell_default to R.id.check_default),
            "red" to (R.id.cell_red to R.id.check_red),
            "orange" to (R.id.cell_orange to R.id.check_orange),
            "green" to (R.id.cell_green to R.id.check_green),
            "teal" to (R.id.cell_teal to R.id.check_teal),
            "blue" to (R.id.cell_blue to R.id.check_blue),
            "purple" to (R.id.cell_purple to R.id.check_purple),
            "pink" to (R.id.cell_pink to R.id.check_pink),
            "brown" to (R.id.cell_brown to R.id.check_brown),
        )
    }
}
