package xyz.shabrr.iconchanger

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

/**
 * Satu pilihan ikon. aliasClass null berarti ikon default, yaitu MainActivity
 * sendiri sebagai launcher. Selain itu, setiap warna memakai satu
 * activity-alias di AndroidManifest.xml.
 */
data class AppIcon(
    val id: String,
    val label: String,
    val color: Long,
    val aliasClass: String?,
)

val APP_ICONS = listOf(
    AppIcon("default", "Default", 0xFF003435, null),
    AppIcon("red", "Merah", 0xFFE53935, ".IconRedAlias"),
    AppIcon("orange", "Oranye", 0xFFF57C00, ".IconOrangeAlias"),
    AppIcon("green", "Hijau", 0xFF43A047, ".IconGreenAlias"),
    AppIcon("teal", "Toska", 0xFF00897B, ".IconTealAlias"),
    AppIcon("blue", "Biru", 0xFF1E88E5, ".IconBlueAlias"),
    AppIcon("purple", "Ungu", 0xFF8E24AA, ".IconPurpleAlias"),
    AppIcon("pink", "Pink", 0xFFD81B60, ".IconPinkAlias"),
    AppIcon("brown", "Coklat", 0xFF6D4C41, ".IconBrownAlias"),
)

private const val PREFS = "icon_prefs"
private const val KEY_ICON_ID = "icon_id"

/** Ikon yang sedang aktif menurut pilihan terakhir yang tersimpan. */
fun getCurrentIconId(context: Context): String {
    val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    return prefs.getString(KEY_ICON_ID, "default") ?: "default"
}

/**
 * Mengganti ikon launcher: mengaktifkan komponen yang dipilih dan
 * menonaktifkan semua komponen launcher lainnya, tanpa mematikan aplikasi
 * yang sedang berjalan (DONT_KILL_APP).
 */
fun applyIcon(context: Context, icon: AppIcon) {
    val pm = context.packageManager
    val pkg = context.packageName
    val allComponents = listOf(".MainActivity") + APP_ICONS.mapNotNull { it.aliasClass }
    for (name in allComponents) {
        val enabled = if (icon.aliasClass == null) {
            name == ".MainActivity"
        } else {
            name == icon.aliasClass
        }
        pm.setComponentEnabledSetting(
            ComponentName(pkg, pkg + name),
            if (enabled) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            },
            PackageManager.DONT_KILL_APP,
        )
    }
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        .edit()
        .putString(KEY_ICON_ID, icon.id)
        .apply()
}
