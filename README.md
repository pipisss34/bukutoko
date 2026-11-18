# Buku Toko

Aplikasi pembukuan sederhana untuk toko sembako rumahan. Dicatat dari HP, jalan **offline**, dan semua data tersimpan di perangkat.

Dibuat oleh **pims-dev**. Aplikasi ini open source, jadi siapa pun boleh memasang dan menyesuaikannya.

## Fitur

**Uang dan stok**
- **Laci uang yang bisa diatur sendiri.** Bawaannya Laci Sembako, Laci Jajan, dan Laci Rokok. Judul dan modal awal bisa diubah, laci bisa ditambah atau dihapus (ikon ⚙️ di kanan atas).
- **Kategori barang yang bisa diatur sendiri.** Tiap kategori masuk ke satu laci, jadi uang penjualan otomatis masuk ke laci yang tepat.
- **Stok barang** dengan foto, harga jual, dan harga modal. Tombol − (terjual) dan + (kulak). Modal dihitung rata-rata saat harga kulak berubah.
- Pencarian barang, tombol kategori, **Populer** (barang paling laku 30 hari terakhir), dan **Menipis**.
- **Buku Kas**: uang masuk, keluar, dan pengeluaran pribadi, lengkap dengan koreksi uang laci.
- **Bon pelanggan**: bon berupa barang (stok ikut berkurang), bon nominal, bon sebagian (sebagian dibayar di muka), dan pembayaran bon.
- **Tabungan** terpisah dengan angka tersembunyi dan riwayat.
- **Rekap** harian, mingguan, bulanan, dan tahunan dengan grafik balok atau garis.
- **Total harta toko**: gabungan uang laci, nilai stok, dan bon.

**Struk dan nota**
- Struk **PPOB** (pulsa, paket data, token listrik, tagihan, dan lainnya), **E-wallet**, **Transfer bank**, dan **Nota belanja**.
- Database pelanggan yang dikelola manual (nama dan nomor tujuan) dengan pencarian.
- **Riwayat** semua struk yang pernah dibuat.
- Struk bisa **disimpan sebagai gambar**, **dibagikan**, **disalin sebagai teks**, atau **dicetak** (lebar 58 mm, 80 mm, atau penuh).

**Tampilan**
- Sembilan tema: Buku, Lembut, Neon, Sage, Biru, Ceria, Piksel, Imut, dan Persona 5. Warna bisa disesuaikan.
- Teks besar dan tebal agar mudah dibaca.
- Profil toko (nama dan logo), tutorial di awal, dan cadangan data (salin teks atau simpan ke file).

## Pemasangan

1. Buka halaman **Releases** di repo ini dan unduh APK terbaru (`BukuToko-vX.Y.apk`).
2. Buka file APK dari HP dan izinkan pemasangan dari sumber ini.
3. Persyaratan: **Android 10 (API 29) atau lebih baru**.

APK baru langsung menimpa versi lama tanpa menghapus data, selama berasal dari rilis resmi repo ini.

## Data dan cadangan

- Data disimpan di penyimpanan aplikasi pada perangkat. Aplikasi berjalan offline dan tidak mengirim data ke server.
- Menghapus aplikasi atau data aplikasi akan menghapus semua catatan. **Buat cadangan secara berkala** di **Menu → Cadangan data**:
  - **Salin cadangan** atau **Simpan ke file** (tersimpan di `Download/BukuToko`).
  - **Pulihkan dari teks** atau **Pilih file** untuk memulihkan.
- Struk yang disimpan sebagai gambar masuk ke Galeri (`Pictures/BukuToko`).

## Struktur proyek

```
app/src/main/assets/index.html                      # seluruh aplikasi (HTML, CSS, JavaScript)
app/src/main/java/com/bukutoko/app/MainActivity.java # pembungkus WebView dan jembatan ke Android
app/build.gradle                                    # konfigurasi build dan tanda tangan
.github/workflows/build.yml                         # build dan rilis otomatis
```

Aplikasinya satu file `index.html` tanpa library eksternal. Data disimpan di `localStorage` dengan kunci `tokoku_v1`.

### Jembatan ke Android (`window.TokoApp`)

| Metode | Fungsi |
|---|---|
| `saveImage(dataUrl, nama)` | Simpan gambar struk ke Galeri |
| `shareImage(dataUrl, nama)` | Bagikan gambar struk lewat menu kirim |
| `saveText(teks, nama)` | Simpan file cadangan `.json` ke Download |
| `printImage(dataUrl, nama, lebarMm)` | Cetak struk lewat layanan cetak Android |

Di browser biasa, fitur-fitur ini memakai cadangan bawaan browser (unduh file, `navigator.share`, atau fitur cetak browser).

## Build dan rilis

Build berjalan otomatis lewat **GitHub Actions** setiap ada push ke `main` dan setiap ada tag `v*`. Tag `v*` juga membuat **GitHub Release** berisi APK.

Membuat rilis baru:

```bash
# salin index.html terbaru ke app/src/main/assets/ bila ada perubahan
git add .
git commit -m "Deskripsi perubahan"
git push
git tag v1.X          # nomor harus baru
git push origin v1.X
```

`versionCode` naik otomatis dari nomor run GitHub, dan `versionName` mengikuti nama tag.

### Tanda tangan APK

Update hanya bisa menimpa aplikasi lama kalau APK ditandatangani dengan **kunci yang sama**. Kunci disimpan di **GitHub Secrets** pada repo:

| Secret | Isi |
|---|---|
| `KEYSTORE_BASE64` | Berkas keystore `.jks` dalam format base64 |
| `KEYSTORE_PASSWORD` | Password keystore |
| `KEY_ALIAS` | Alias kunci |
| `KEY_PASSWORD` | Password kunci |

Membuat keystore baru:

```bash
keytool -genkeypair -keystore bukutoko-release.jks -alias bukutoko \
  -keyalg RSA -keysize 2048 -validity 36500 -dname "CN=pims-dev"
base64 -w0 bukutoko-release.jks > ks.b64
```

**Simpan berkas `.jks` dan passwordnya di tempat aman.** Kalau hilang, rilis berikutnya tidak bisa menimpa aplikasi yang sudah terpasang. Jangan pernah memasukkan berkas `.jks` atau password ke repo.

Kalau secret kosong, build tetap jalan memakai kunci debug (APK-nya tidak bisa menimpa rilis bertanda tangan tetap).

## Menyesuaikan untuk toko lain

Tidak semua toko butuh laci terpisah. Buka **⚙️ Atur tata letak** untuk menambah, menghapus, atau mengganti judul laci dan kategori sesuai kebutuhan. Laci yang dihapus memindahkan catatan, bon, dan kategorinya ke laci pilihanmu.

## Lisensi

Lihat berkas [LICENSE](LICENSE).
