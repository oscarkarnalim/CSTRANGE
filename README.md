# CSTRANGE: Comprehensive Similarity TRacker in Academia with Natural lanGuage Explanation

**CSTRANGE** \(Comprehensive Similarity TRacker in Academia with Natural lanGuage Explanation\) is a comprehensive tool to observe similarities among programming submissions with three levels of similarity: pseudo-semantic (type III code clone), syntactic (type II code clone), and surface (type I code clone).

**Upon acceptance of the relevant publication, CSTRANGE has been updated with a more recent JPlag (v 3.0.0), better JPlag report formatting, and more efficient STRANGE reporting mechanism**

Type III similarity is measured via [JPlag](https://github.com/jplag/jplag) version 2.12.1 (which is the latest at the development stage of this tool). Type II similarity is measured via [STRANGE](https://github.com/oscarkarnalim/strange) version 1.1 published in [IEEE Access](https://doi.org/10.1109/ACCESS.2021.3073703). Type I similarity is measured by simplified STRANGE. **You can see this guideline in Indonesian at the end of this file.** 

Unlike many existing tools, CSTRANGE is applicable for any assessments, even the trivial and the strongly directed ones. If many submissions share the same similarity, the instructor can rely on shallower levels of similarity to identify the suspicious ones. The instructor is also able to prioritise submissions at which some similarities tend to be verbatim. The tool sorts similar submissions based on the combination of three types of similarities.

CSTRANGE expands STRANGE by incorporating three types of similarity, optimising the comparison algorithm, and adding an user interface. CSTRANGE can exclude template code and common code. Template code removal is adapted from STRANGE while common code removal is adapted from [Common Code Segment Selector](https://github.com/oscarkarnalim/c2s2) published at [52nd ACM Technical Symposium on Computer Science Education (SIGCSE 2021)](https://dl.acm.org/doi/10.1145/3408877.3432436).

Currently, the tool supports Java and Python submissions. The tool can also act as a simple user interface for JPlag, allowing five more acceptable languages of submissions: C, C++, C#, and Text.

## User Interface
### Main Layout
<p align="center">
<img width="60%" src="https://github.com/oscarkarnalim/CSTRANGE/blob/main/UI_01.png?raw=true">
</p>

### Investigation report
<p align="center">
<img width="80%" src="https://github.com/oscarkarnalim/CSTRANGE/blob/main/UI_02.png?raw=true">
</p>

### Comparison report
<p align="center">
<img width="80%" src="https://github.com/oscarkarnalim/CSTRANGE/blob/main/UI_03.png?raw=true">
</p>

## Input 
### Assignment path
This refers to a directory containing student submissions as submission files, sub-directories or zip files.

### Submission type
Single file: each submission is represented with either a file or a sub-directory with one file.  
Multiple files in a directory: each submission is represented with a sub-directory containing multiple files. All files will be concatenated prior comparison.  
Multiple files in a zip: each submission is represented with a zip. The zip will be unzipped and all of its files will be concatenated prior comparison.  

### Submission language
The language of the submissions. All CSTRANGE features work on Java and Python. Other languages are only supported by JPlag (pseudo-semantic similarity).

### Explanation language
The language of similarity explanation. This is STRANGE's core feature to help instructors or higher officials understand the reported similarities. The options are English and Indonesian.

### Minimum similarity threshold
The minimum percentage of similarity for a submission pair to be reported. The value is from 0 to 100 inclusive. The threshold will be applied to the deepest level of similarity. For example, type III will be selected if all types of similarities are considered. Type II will be selected if only type I and type II similarities are considered.

### Maximum reported submission pairs
The maximum number of reported submission pairs with high similarity. Larger value will display more submission pairs for manual check but will make the execution runs slower.

### Minimum matching length
It defines how many similar tokens ('words') are required for a part of the content to be reported. Larger value will mitigate the occurrence of coincidental similarity, but it will make the tool less resilient to disguises.

### Template directory path
This refers to a directory containing tenplate content stored as submission files. The tool will try to remove all template content in student submissions prior comparison.

### Common content
If this feature is turned on, the tool will try to remove all similar contents that are common among student submissions. This might result in longer processing time.

### Reported similarities
The instructor can select reported types of similarity. If the submission language is other than Java and Python, only JPlag should be selected.

## Acknowledgments
This tool uses [STRANGE](https://github.com/oscarkarnalim/strange) to measure type II and type I similarities, [JPlag](https://github.com/jplag/jplag) to measure type III similarity, [ANTLR](https://www.antlr.org/) to tokenise given submissions, [Google Prettify](https://github.com/google/code-prettify) to display source code, and [JSoup](https://jsoup.org/) to parse JPlag's HTML pages. 

# Indonesian Guideline for CSTRANGE
**CSTRANGE** \(Comprehensive Similarity TRacker in Academia with Natural lanGuage Explanation\) adalah sebuah kakas komprehensif untuk observasi kesamaan tugas pemrograman berdasarkan tiga level kesamaan: pseudo-semantic (tipe III klasifikasi code clone), syntactic (tipe II klasifikasi code clone), dan surface (tipe I klasifikasi code clone). Tipe III diukur menggunakan [JPlag](https://github.com/jplag/jplag) versi 2.12.1 (terbaru pada saat pengembangan kakas ini). Tipe II diukur menggunakan [STRANGE](https://github.com/oscarkarnalim/strange) versi 1.1 yang dipublikasikan di [IEEE Access](https://doi.org/10.1109/ACCESS.2021.3073703). Tipe I diukur menggunakan STRANGE yang disimplifikasi. **You can see this guideline in English at the beginning of the file.** 

Berbeda dengan kakas-kakas lainnya, CSTRANGE dapat digunakan untuk tugas apapun, bahkan yang mudah atau penyelesaiannya sangat diarahkan. Jika banyak hasil pekerjaan memiliki nilai kesamaan yang sama, instruktur dapat menggunakan level kesamaan yang lebih dangkal untuk mengindentifikasi hasil pekerjaan yang mencurigakan. Instruktur juga dapat memprioritaskan hasil pekerjaan yang sebagian kesamaannya cenderung sama persis. Kakas ini mengurutkan hasil pekerjaan berdasarkan kombinasi nilai dari tiga tipe kesamaan.

CSTRANGE diekspansi dari STRANGE dengan memperhitungkan tiga tipe kesamaan, mengoptimasi algoritma perbandingan, dan menambahkan antarmuka pengguna. CSTRANGE dapat mengabaikan kode template dan kode umum. Kode template diabaikan dengan teknik yang diadaptasi dari STRANGE sedangkan kode umum diabaikan dengan teknik yang diadaptasi dari [Common Code Segment Selector](https://github.com/oscarkarnalim/c2s2) yang dipublikasikan di [52nd ACM Technical Symposium on Computer Science Education (SIGCSE 2021)](https://dl.acm.org/doi/10.1145/3408877.3432436).

Pada saat ini, kakas menerima tugas pemrograman dalam bahasa Java dan Python. Kakas ini juga dapat berperan sebagai antarmuka sederhana untuk JPlag, memungkinkan lima bahasa tambahan untuk tugas: C, C++, C#, and Text.

**Note :** Mengingat JPlag yang digunakan pada kakas ini tidak didesain khusus sebagai library pihak ketiga, CSTRANGE bisa berhenti berjalan jika JPlag tidak berhasil mengenali dua atau lebih tugas siswa atau minimum matching length nya terlalu besar. Jika itu terjadi, mohon pastikan bahwa paling tidak ada dua tugas siswa dan/atau menurunkan nilai minimum matching length.

## Masukan 
### Assignment path (lokasi tugas)
Ini mengarah pada sebuah direktori berisi kumpulan hasil pekerjaan dalam bentuk kumpulan file, sub-direktori, atau zip.

### Submission type (jenis hasil pekerjaan yang dikumpulkan)
Single file: setiap tugas siswa direpresentasikan dengan sebuah file atau sebuah sub-direktori berisi satu file hasil pekerjaan.  
Multiple files in a directory: setiap tugas siswa direpresentasikan dengan sub-direktori berisi beberapa file. Semua file tersebut akan dikonkatenasi sebelum dibandingkan satu sama lain.  
Multiple files in a zip: setiap tugas siswa direpresentasikan dengan sebuah file zip. File tersebut akan diekstrak dan semua file didalamnya akan dikonkatenasi sebelum dibandingkan satu sama lain.

### Submission language (bahasa hasil pekerjaan)
Bahasa dari hasil pekerjaan yang dikumpulkan. Semua fitur CSTRANGE berfungsi untuk Java dan Python. Bahasa lainnya hanya didukung oleh JPlag (pseudo-semantic similarity).

### Explanation language (bahasa penjelasan)
Bahasa dari penjelasan kesamaan. Ini adalah fitur utama dari STRANGE untuk membantu instruktur atau pejabat institusi yang lebih tinggi untuk mengerti kesamaan yang dilaporkan. Pilihan yang ada adalah Inggris dan Indonesia.  

### Minimum similarity threshold (batas kesamaan minimum)
Persentasi minimum dari kesamaan hasil pekerjaan agar dilaporkan. Nilainya diantara 0 hingga 100 secara inklusif. Batas ini akan diterapkan pada level kesamaan terdalam. Sebagai contoh, tipe III akan dipilih jika semua tipe kesamaan dipertimbangkan. Tipe II akan dipilih jika hanya tipe I dan tipe II yang dipertimbangkan.  

### Maximum reported submission pairs (batas maksimum pasangan tugas yang dilaporkan)
Jumlah maksimum dari pasangan hasil pekerjaan dengan kesamaan tinggi yang dilaporkan. Nilai besar akan menampilkan lebih banyak pasangan tugas untuk cek manual tapi akan membuat eksekusi menjadi lebih lambat.

### Minimum matching length (panjang kesamaan minimum)
Ini menentukan seberapa banyak token ('kata') yang dibutuhkan agar sebuah bagian konten dilaporkan. Nilai yang lebih besar akan mengurangi kemunculan kesamaan tidak disengaja, namun akan membuat kakas semakin rentan dengan penyamaran.

### Template directory path (lokasi direktori template)
Ini mengarah pada lokasi direktori berisi konten template yang disimpan dalam file-file. Kakas akan mencoba untuk membuang semua konten template dari tugas-tugas siswa sebelum dibandingkan satu sama lain.

### Common content (pertimbangan kode umum)
Jika fitur ini diaktifkan, kakas akan mencoba untuk membuang semua konten sama yang umum ditemukan di hasil pekerjaan siswa. Ini mungkin akan menyebabkan waktu proses yang lebih lama.

### Reported similarities (kesamaan-kesamaan yang dilaporkan)
Instruktur dapat memilih tipe kesamaan mana saja yang perlu dilaporkan. Jika bahasa hasil pekerjaan selain Java dan Python, maka hanya kesamaan JPlag yang bisa dipilih.
