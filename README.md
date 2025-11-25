# 📷 KameraKu - Aplikasi Kamera Android

Aplikasi kamera sederhana yang dibangun menggunakan **CameraX** dan **Jetpack Compose** untuk praktikum mobile programming.

## ✨ Fitur

- 📸 **Preview Kamera Real-time** - Menampilkan pratinjau kamera secara langsung
- 🖼️ **Ambil Foto** - Capture foto dengan satu tombol
- 💾 **Simpan ke Gallery** - Otomatis menyimpan ke folder `Pictures/KameraKu`
- 🖼️ **Thumbnail Preview** - Menampilkan foto terakhir yang diambil
- ⚡ **Toggle Flash/Torch** - Nyalakan/matikan lampu flash
- 🔄 **Switch Camera** - Beralih antara kamera depan dan belakang
- 🔐 **Runtime Permission** - Pengelolaan izin kamera yang aman
- 📱 **Modern UI** - Interface menggunakan Material Design 3

## 🛠️ Teknologi yang Digunakan

- **Kotlin** - Bahasa pemrograman
- **Jetpack Compose** - UI Framework modern
- **CameraX** - API kamera yang konsisten
- **Material Design 3** - Design system
- **Coil** - Image loading library
- **Coroutines** - Asynchronous programming

## 📋 Persyaratan

- Android Studio Hedgehog (2023.1.1) atau lebih baru
- Minimum SDK: API 21 (Android 5.0)
- Target SDK: API 34 (Android 14)
- Kotlin 1.9+
- Gradle 8.0+

## 🚀 Cara Menjalankan

1. **Clone repository ini:**
   ```bash
   git clone https://github.com/username/KameraKu.git
   cd KameraKu
   ```

2. **Buka project di Android Studio:**
   - File → Open → Pilih folder project

3. **Sync Gradle:**
   - Tunggu hingga gradle sync selesai
   - Jika ada error, klik "Sync Now"

4. **Jalankan aplikasi:**
   - Hubungkan perangkat Android via USB (atau gunakan emulator)
   - Klik tombol Run ▶️ atau tekan `Shift + F10`

## 📱 Cara Menggunakan Aplikasi

1. **Berikan Izin Kamera** - Saat pertama kali dibuka, izinkan akses kamera
2. **Ambil Foto** - Tekan tombol lingkaran putih besar di tengah bawah
3. **Lihat Hasil** - Thumbnail foto muncul di atas tombol capture
4. **Toggle Flash** - Tekan ikon flash di kiri atas untuk nyalakan/matikan
5. **Ganti Kamera** - Tekan ikon switch di kanan atas untuk ganti kamera depan/belakang

## 📂 Struktur Project

```
KameraKu/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/kameraku/
│   │   │   │   └── MainActivity.kt
│   │   │   ├── res/
│   │   │   │   ├── values/
│   │   │   │   │   └── themes.xml
│   │   │   │   └── mipmap/
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   └── build.gradle.kts
├── gradle/
├── README.md
└── .gitignore
```

## 🔧 Konfigurasi CameraX

### Dependencies (build.gradle.kts)

```kotlin
val camerax_version = "1.3.4"

implementation("androidx.camera:camera-core:$camerax_version")
implementation("androidx.camera:camera-camera2:$camerax_version")
implementation("androidx.camera:camera-lifecycle:$camerax_version")
implementation("androidx.camera:camera-view:$camerax_version")
```

### Permissions (AndroidManifest.xml)

```xml
<uses-feature android:name="android.hardware.camera.any" />
<uses-permission android:name="android.permission.CAMERA" />
```

## 🎯 Alur Kerja Aplikasi

1. **Permission Request** - Meminta izin CAMERA saat pertama kali
2. **Setup Camera Provider** - Inisialisasi ProcessCameraProvider
3. **Bind Use Cases** - Mengikat Preview dan ImageCapture ke lifecycle
4. **Preview** - Menampilkan feed kamera melalui PreviewView
5. **Capture** - Mengambil foto dengan ImageCapture
6. **Save to MediaStore** - Menyimpan ke Pictures/KameraKu dengan scoped storage

## 💾 Penyimpanan Media

Aplikasi menggunakan **MediaStore API** untuk menyimpan foto:

- ✅ **Scoped Storage** - Tidak memerlukan `WRITE_EXTERNAL_STORAGE`
- 📁 **Lokasi**: `Pictures/KameraKu/`
- 📄 **Format**: JPEG
- 🏷️ **Nama File**: `IMG_[timestamp].jpg`
- 📊 **EXIF Data**: Otomatis menyimpan orientasi yang benar

## 🔍 Fitur Teknis

### Orientasi & Rotasi
- Otomatis mendeteksi rotasi device
- Menyimpan EXIF orientation metadata
- Foto tampil benar di galeri tanpa perlu rotasi manual

### Rasio Aspek
- Preview: `FILL_CENTER` untuk tampilan penuh
- Capture: `MINIMIZE_LATENCY` untuk kecepatan optimal

### Camera Control
- Flash/Torch: `camera.cameraControl.enableTorch()`
- Switch Camera: `CameraSelector.DEFAULT_BACK_CAMERA` / `DEFAULT_FRONT_CAMERA`

## 🐛 Troubleshooting

### Preview Hitam di Emulator
- **Solusi**: Gunakan perangkat fisik atau pastikan emulator memiliki webcam
- Pastikan izin kamera sudah diberikan

### Foto Tidak Muncul di Galeri
- **Solusi**: Tunggu beberapa detik untuk sistem indeks media
- Cek manual di folder `Pictures/KameraKu` via Files app

### App Crash Saat Capture
- **Solusi**: Pastikan izin kamera sudah granted sebelum akses
- Periksa logs di Logcat untuk detail error

### Rotasi Foto Salah
- **Solusi**: `targetRotation` sudah diset otomatis
- Verifikasi EXIF data di properties foto

## 📸 Screenshot

*(Tambahkan screenshot aplikasi Anda di sini)*

| Preview | Capture | Gallery |
|---------|---------|---------|
| ![Preview](screenshots/preview.png) | ![Capture](screenshots/capture.png) | ![Gallery](screenshots/gallery.png) |

## 👨‍💻 Author

**SILVANA ARAYUNDA**
- NIM : 235150201111076
- Kelas: PAPB - B
- Email: silvanarayunda@student.ub.ac.id

## 📄 Laporan Praktikum

### Pengujian yang Dilakukan

1. ✅ **Kualitas Foto** - Mode `MINIMIZE_LATENCY` menghasilkan foto cepat
2. ✅ **Orientasi EXIF** - Foto tampil benar di galeri dalam berbagai orientasi
3. ✅ **Rasio Aspek** - Tidak ada distorsi dengan `FILL_CENTER`
4. ✅ **Runtime Permission** - UX yang ramah saat izin ditolak
5. ✅ **MediaStore** - Berhasil simpan tanpa `WRITE_EXTERNAL_STORAGE`

### Hasil Analisis

| Aspek | Hasil | Keterangan |
|-------|-------|------------|
| Kecepatan Capture | ~200-500ms | Cukup responsif |
| Ukuran File | 2-5 MB | JPEG quality default |
| Orientasi | ✅ Benar | EXIF metadata berfungsi |
| Preview Quality | ✅ Bagus | Tidak ada lag |
| Flash | ✅ Works | On/Off responsive |

## 📚 Referensi

- [CameraX Documentation](https://developer.android.com/training/camerax)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [MediaStore API](https://developer.android.com/training/data-storage/shared/media)
- [Modul Praktikum](BAB_9_CameraX.pdf)
