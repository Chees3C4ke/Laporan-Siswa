# Laporan-Siswa

Aplikasi **Laporan Pengaduan Siswa** untuk Android — versi "anak sekolah" dari
[Laporan-Masyarakat](https://github.com/AzharRivaldi/Laporan-Masyarakat) karya
[Azhar Rivaldi](https://rivaldi48.blogspot.com/).

Siswa bisa melaporkan kejadian di lingkungan sekolah (perundungan, fasilitas
rusak, pelanggaran tata tertib, keamanan & keselamatan) lengkap dengan foto
bukti, lokasi, dan tanggal kejadian. Semua laporan tersimpan rapi di perangkat
(Room Database) dan bisa dilihat lagi lewat halaman riwayat.

## Fitur

- **4 kategori laporan**: Perundungan, Fasilitas Rusak, Pelanggaran Tata Tertib,
  Keamanan & Keselamatan — masing-masing dengan warna & ilustrasi sendiri.
- **Form pengaduan**: foto bukti (galeri lewat Photo Picker atau kamera),
  nama / kelas / NIS pelapor, telepon, pihak terlapor (opsional), lokasi kejadian
  (terisi otomatis dari GPS + reverse geocoding, tetap bisa diedit manual),
  tanggal kejadian (date picker), dan isi laporan.
- **Mode lapor anonim** — identitas pelapor disembunyikan dari riwayat.
- **Riwayat laporan** berbasis Room + LiveData, urut dari yang terbaru,
  dengan warna header sesuai kategori.
- **Filter kategori** di halaman riwayat lewat deretan chip (Semua / per kategori).
- **Export riwayat ke CSV** (menu di halaman riwayat) lewat Storage Access
  Framework — file bisa langsung dibuka di Excel atau dikirim ke guru/pihak
  sekolah. Tidak butuh permission storage.
- **Detail laporan** — foto bukti ukuran penuh + seluruh data + tombol hapus.
- **Hapus semua riwayat** dari menu halaman riwayat.
- Penyimpanan **100% offline** di perangkat (Room), foto dikompres ke Base64
  supaya hemat tempat.

## Kebutuhan Build

| Komponen      | Versi   |
|---------------|---------|
| minSdk        | **26** (Android 8.0 Oreo) |
| targetSdk / compileSdk | 34 (Android 14) |
| Gradle        | 8.7     |
| Android Gradle Plugin | 8.5.2 |
| JDK           | 17      |
| Bahasa        | Java    |
| Gradle script | Kotlin DSL + version catalog (`gradle/libs.versions.toml`) |

Package & applicationId: `com.pplgsmkn4.laporanmasyarakat`
(sama dengan template project Android Studio PPLG SMKN 4).

Butuh **Android Studio Koala (2024.1.1) atau lebih baru** karena AGP 8.5.

## Cara Menjalankan di Android Studio

1. `File` → `Open...` → pilih folder project ini (yang berisi `settings.gradle.kts`).
2. Tunggu Gradle sync selesai (sekalian download dependency).
3. Pilih emulator / device Android 8.0+, klik **Run ▶**.

Atau lewat terminal:

```bash
./gradlew assembleDebug      # Linux / macOS
gradlew.bat assembleDebug    # Windows
```

APK hasil build ada di `app/build/outputs/apk/debug/`.

## Struktur Project

```
app/src/main/java/com/pplgsmkn4/laporanmasyarakat/
├── dao/DatabaseDao.java            # query Room (insert, select, delete)
├── database/AppDatabase.java       # definisi database
├── database/DatabaseClient.java    # singleton Room
├── model/ModelLaporan.java         # entity tbl_laporan
├── ui/
│   ├── main/MainActivity.java      # kartu kategori + ambil lokasi GPS
│   ├── report/ReportActivity.java  # form pengaduan + foto bukti
│   ├── history/HistoryActivity.java# daftar riwayat laporan
│   ├── history/HistoryAdapter.java # adapter kartu riwayat
│   └── detail/DetailActivity.java  # detail satu laporan + hapus
├── utils/                          # BitmapManager, Constant, KategoriHelper, StatusBarUtil
└── viewmodel/                      # InputData / History / Detail ViewModel (RxJava3)

build.gradle.kts                    # plugin AGP (via version catalog)
settings.gradle.kts                 # repositories + include :app
gradle/libs.versions.toml           # semua versi dependency di satu tempat
app/build.gradle.kts                # namespace, SDK, dependencies
app/keepRules/rules.keep            # keep rules R8 (Room entity, Glide)
tools/verify_resources.py           # dev tool: cek statis referensi resource tanpa SDK
```

## Perbedaan Utama vs Repo Referensi

- minSdk dinaikkan ke **26** sesuai permintaan; targetSdk/compileSdk **34**.
- AGP 8.5.2 + Gradle 8.7 + JDK 17 (repo referensi: AGP 7.0.4 / Gradle 7.0.2).
- Kamera & galeri memakai **Activity Result API** (`TakePicture`, `PickVisualMedia`)
  sehingga tidak butuh permission storage sama sekali — aman untuk scoped storage
  Android 10+ (repo referensi masih `startActivityForResult` + `MEDIA_DATA`
  yang sudah deprecated).
- Lokasi memakai `FusedLocationProviderClient` (play-services-location),
  bukan library `SimpleLocation` dari JitPack.
- `ViewModelProviders` (deprecated) diganti `ViewModelProvider`.
- Ditambahkan: mode anonim, field kelas/NIS/terlapor, halaman detail laporan,
  filter kategori, export CSV, dan menu hapus semua riwayat.

## Kredit

Struktur & pola kode diadaptasi dari
[Laporan-Masyarakat](https://github.com/AzharRivaldi/Laporan-Masyarakat)
oleh **Azhar Rivaldi** (Apache License 2.0).
Ilustrasi banner kategori dibuat dengan AI untuk project ini.

## Lisensi

```
Copyright (C) Azhar Rivaldi (kode referensi)

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
```
