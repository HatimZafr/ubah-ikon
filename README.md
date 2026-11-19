# Ubah Ikon

Aplikasi Android satu fitur: ganti ikon launcher aplikasi dari dalam
aplikasi itu sendiri. Isinya cuma pilihan warna ikon, ketuk warna dan
ikon di layar utama langsung berubah.

## Cara kerja

Memakai `activity-alias` di AndroidManifest.xml. Setiap warna punya
satu alias dengan ikonnya sendiri, semuanya menunjuk ke MainActivity
yang sama. Pemilihan dilakukan lewat
`PackageManager.setComponentEnabledSetting` dengan `DONT_KILL_APP`.

## Build

Build hanya lewat GitHub Actions (lihat `.github/workflows/android.yml`).
Push ke branch `main`, workflow membangun debug APK dan mengunggahnya
sebagai artifact `app-debug`.

## Ikon

9 pilihan warna (Default, Merah, Oranye, Hijau, Toska, Biru, Ungu,
Pink, Coklat). PNG dibuat dengan Pillow, ada di semua densitas
mipmap.
