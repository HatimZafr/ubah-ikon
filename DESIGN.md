# DESIGN.md — Ubah Ikon

Aplikasi Android satu fitur: mengganti ikon launcher aplikasi dari dalam
aplikasi itu sendiri.

## Cara kerja

Android mengizinkan aplikasi punya beberapa entri launcher lewat
`activity-alias` di AndroidManifest.xml. Setiap alias punya ikon sendiri
dan menunjuk ke MainActivity yang sama. Saat pengguna memilih warna,
aplikasi mengaktifkan alias warna itu dan menonaktifkan semua komponen
launcher lainnya lewat
`PackageManager.setComponentEnabledSetting(..., DONT_KILL_APP)`.
Ikon di layar utama berubah tanpa install ulang.

## Pilihan ikon (9)

Semuanya kotak membulat berisi bintang putih, beda warna saja:

| ID | Label | Warna |
|----|-------|-------|
| default | Default | #003435 (toska gelap) |
| red | Merah | #E53935 |
| orange | Oranye | #F57C00 |
| green | Hijau | #43A047 |
| teal | Toska | #00897B |
| blue | Biru | #1E88E5 |
| purple | Ungu | #8E24AA |
| pink | Pink | #D81B60 |
| brown | Coklat | #6D4C41 |

Ikon digambar sebagai PNG di semua densitas (mdpi sampai xxxhdpi),
dibuat dengan Pillow dari `/tmp/gen_icons.py`.

## Layar

Satu layar, Jetpack Compose Material 3, bahasa Indonesia:

- Judul "Ubah Ikon" + penjelasan singkat.
- Grid 3 kolom berisi kotak warna (96dp, sudut membulat).
- Warna aktif ditandai centang putih.
- Ketuk warna: ikon langsung diganti + toast "Ikon diubah ke ...".
- Catatan kaki: kalau ikon di layar utama belum berubah, tunggu
  beberapa detik atau kembali ke Home (sebagian launcher butuh waktu
  refresh).

Pilihan terakhir disimpan di SharedPreferences (`icon_prefs`), supaya
tanda centang tetap benar saat aplikasi dibuka lagi.

## Batasan yang jujur

- Ikon yang tersedia hanya yang sudah disiapkan di dalam aplikasi,
  tidak bisa memakai gambar sembarang dari galeri.
- Sebagian launcher (terutama launcher bawaan yang agresif) butuh
  beberapa detik untuk menampilkan ikon baru; kadang perlu kembali
  ke Home dulu.
- Setelah ganti ikon, mengetuk ikon tetap membuka aplikasi yang sama.

## Teknis

- Kotlin native + Jetpack Compose, minSdk 26, targetSdk 34.
- Package `xyz.shabrr.iconchanger`, aplikasi "Ubah Ikon".
- Build hanya lewat GitHub Actions (`.github/workflows/android.yml`),
  tidak pernah memakai Android SDK lokal.
