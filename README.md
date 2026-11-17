# Buku Toko

Aplikasi Android sederhana untuk mencatat kas dan stok toko, serta membuat struk PPOB dan nota belanja. Dibangun dari satu file HTML yang dibungkus WebView, dan semuanya dikerjakan dari HP lewat Termux.

## Fitur

- Pencatatan kas dan stok toko
- Pembuatan **struk PPOB** dan **nota belanja** sebagai gambar PNG
- **Simpan gambar** struk ke Galeri (album `Pictures/BukuToko`)
- **Bagikan gambar** struk langsung ke WhatsApp atau aplikasi lain
- Data tersimpan lokal di perangkat, tanpa server dan tanpa izin internet
- Backup data lewat fitur ekspor di pengaturan aplikasi

## Unduh

Ambil APK terbaru di halaman [Releases](../../releases), lalu install. Aktifkan izin "install dari sumber tidak dikenal" jika diminta.

Kebutuhan: Android 10 (API 29) atau lebih baru.

## Cara build

Build otomatis lewat GitHub Actions, tidak perlu Android SDK di perangkat.

1. Taruh `icon.png` dan `splash.png` di folder Download, lalu jalankan `bash prepare.sh` di Termux (menyalin icon, splash, dan `index.html` terbaru ke proyek).
2. Commit dan push ke branch `main`.
3. Buka tab **Actions**, tunggu build selesai, lalu unduh artifact `BukuToko-apk`.

APK ditandatangani dengan kunci debug, cukup untuk pemakaian pribadi.

## Rilis versi baru

1. Naikkan `versionCode` dan `versionName` di `app/build.gradle`.
2. Commit dan push, lalu buat tag:

```
git tag v1.0
git push origin v1.0
```

3. GitHub Actions akan membangun APK dan membuat Release `v1.0` otomatis, lengkap dengan file `BukuToko-v1.0.apk`.

## Struktur proyek

```
app/src/main/assets/index.html     # seluruh isi aplikasi
app/src/main/java/.../MainActivity.java   # WebView + jembatan simpan/bagikan gambar
app/src/main/res/                  # icon dan splash
.github/workflows/build.yml        # build dan release otomatis
prepare.sh                         # salin icon/splash/html dari folder Download
```

## Lisensi

[MIT](LICENSE)
