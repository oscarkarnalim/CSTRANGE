# USTRANGE: User-oriented Similarity TRacker in Academia with Natural lanGuage Explanation

**USTRANGE** \(User-oriented Similarity TRacker in Academia with Natural lanGuage Explanation\) is an user-friendly tool to observe similarities among programming submissions with three levels of similarity: pseudo-semantic (type III code clone), syntactic (type II code clone), and surface (type I code clone). Type III similarity is measured via [JPlag](https://github.com/jplag/jplag) version 2.12.1 (which is the latest at the development stage of this tool). Type II similarity is measured via [STRANGE](https://github.com/oscarkarnalim/strange) version 1.1 published in [IEEE Access](https://doi.org/10.1109/ACCESS.2021.3073703). Type I similarity is measured by simplified STRANGE. **You can see this guideline in Indonesian at the end of this file.** 

Unlike many existing tools, USTRANGE is applicable for any assessments, even the trivial and the strongly directed ones. If many programs share the same similarity, the instructor can rely on shallower levels of similarity to identify the suspicious ones. The instructor is also able to prioritise programs at which some similarities tend to be verbatim. The tool sorts similar programs based on the combination of three types of similarities.

USTRANGE expands STRANGE by incorporating three types of similarity, optimising the comparison algorithm, and adding an user interface. USTRANGE can exclude template code and common code. Template code removal is adapted from STRANGE while common code removal is adapted from [Common Code Segment Selector](https://github.com/oscarkarnalim/c2s2) published at [52nd ACM Technical Symposium on Computer Science Education (SIGCSE 2021)](https://dl.acm.org/doi/10.1145/3408877.3432436).

Currently, the tool supports Java and Python submissions. The tool can also act as a simple user interface for JPlag, allowing five more acceptable languages of submissions: C, C++, C#, and Text.

## Input 
### Assignment path
This refers to a directory containing student submissions as program files, sub-directories or zip files.

### Submission type
Single file: each submission is represented with either a program file or a sub-directory with one program file.  
Multiple files in a directory: each submission is represented with a sub-directory containing multiple files. All files will be concatenated prior comparison.  
Multiple files in a zip: each submission is represented with a zip. The zio will be unzipped and all of its files will be concatenated prior comparison.  

### Submission language
The language of the submissions. All USTRANGE features work on Java and Python. Other languages are only supported by JPlag (pseudo-semantic similarity).

### Explanation language
The language of similarity explanation. This is STRANGE's core feature to help instructors or higher officials understand the reported similarities. The options are English and Indonesian.

### Minimum similarity threshold
The minimum percentage of similarity for a program pair to be reported. The value is from 0 to 100 inclusive. The threshold will be applied to the highest level of similarity. For example, type III will be selected if all types of similarities are considered. Type II will be selected if only type I and type II similarities are considered.

### Minimum matching length
It defines how many similar tokens ('words') are required for a code segment to be reported. Larger value will mitigate the occurrence of coincidental similarity, but it will make the tool less resilient to program disguises.

### Template directory path
This refers to a directory containing tenplate code stored as program files. The tool will try to remove all template code in student submissions prior comparison.

### Common code
If this feature is turned on, the tool will try to remove all similar code segments that are common among student submissions. This might result in longer processing time.

### Reported similarities
The instructor can select reported types of similarity. If the submission language is other than Java and Python, only JPlag should be selected.

## Acknowledgments
This tool uses [STRANGE](https://github.com/oscarkarnalim/strange) to measure type II and type I similarities, [JPlag](https://github.com/jplag/jplag) to measure type III similarity, [ANTLR](https://www.antlr.org/) to tokenise given programs, [Google Prettify](https://github.com/google/code-prettify) to display source code, and [JSoup](https://jsoup.org/) to parse JPlag's HTML pages. 

# Indonesian Guideline for USTRANGE
**USTRANGE** \(User-oriented Similarity TRacker in Academia with Natural lanGuage Explanation\) adalah sebuah kakas ramah pengguna untuk mengobservasi kesamaan tugas pemrograman berdasarkan tiga level kesamaan: pseudo-semantic (tipe III klasifikasi code clone), syntactic (tipe II klasifikasi code clone), dan surface (tipe I klasifikasi code clone). Tipe III diukur menggunakan [JPlag](https://github.com/jplag/jplag) versi 2.12.1 (terbaru pada saat pengembangan kakas ini). Tipe II diukur menggunakan [STRANGE](https://github.com/oscarkarnalim/strange) versi 1.1 yang dipublikasikan di [IEEE Access](https://doi.org/10.1109/ACCESS.2021.3073703). Tipe I diukur menggunakan STRANGE yang disimplifikasi. **You can see this guideline in English at the beginning of the file.** 

Berbeda dengan kakas-kakas lainnya, USTRANGE dapat digunakan untuk tugas pemrograman apapun, bahkan yang mudah atau penyelesaiannya sangat diarahkan. Jika banyak program memiliki nilai kesamaan yang sama, instruktur dapat menggunakan level kesamaan yang lebih dangkal untuk mengindentifikasi program-program yang mencurigakan. Instruktur juga dapat memprioritaskan program-program yang sebagian kesamaannya cenderung sama persis. Kakas ini mengurutkan program-program berdasarkan kombinasi nilai dari tiga tipe kesamaan.

USTRANGE diekspansi dari STRANGE dengan memperhitungkan tiga tipe kesamaan, mengoptimasi algoritma perbandingan, dan menambahkan antarmuka pengguna. USTRANGE dapat mengabaikan kode template dan kode umum. Kode template diabaikan dengan teknik yang diadaptasi dari STRANGE sedangkan kode umum diabaikan dengan teknik yang diadaptasi dari [Common Code Segment Selector](https://github.com/oscarkarnalim/c2s2) yang dipublikasikan di [52nd ACM Technical Symposium on Computer Science Education (SIGCSE 2021)](https://dl.acm.org/doi/10.1145/3408877.3432436).

Pada saat ini, kakas menerima tugas pemrograman dalam bahasa Java dan Python. Kakas ini juga dapat berperan sebagai antarmuka sederhana untuk JPlag, memungkinkan lima bahasa tambahan untuk tugas: C, C++, C#, and Text.

## Masukan 
### Assignment path (path tugas)
Ini mengarah pada sebuah direktori berisi kumpulan tugas siswa dalam bentuk file kode program, sub-direktori, atau file zip.

### Submission type (jenis tugas yang dikumpulkan)
Single file: setiap tugas siswa direpresentasikan dengan sebuah file kode program atau sebuah sub-direktori berisi satu file kode program.  
Multiple files in a directory: setiap tugas siswa direpresentasikan dengan sub-direktori berisi beberapa file kode program. Semua file tersebut akan dikonkatenasi sebelum dibandingkan satu sama lain.  
Multiple files in a zip: setiap tugas siswa direpresentasikan dengan sebuah file zip. File tersebut akan diekstrak dan semua kode program didalamnya akan dikonkatenasi sebelum dibandingkan satu sama lain.

### Submission language (bahasa tugas)
Bahasa dari tugas siswa yang dikumpulkan. Semua fitur USTRANGE berfungsi untuk Java dan Python. Bahasa lainnya hanya didukung oleh JPlag (pseudo-semantic similarity).

### Explanation language (bahasa penjelasan)
Bahasa dari penjelasan kesamaan. Ini adalah fitur utama dari STRANGE untuk membantu instruktur atau pejabat institusi yang lebih tinggi untuk mengerti kesamaan yang dilaporkan. Pilihan yang ada adalah Inggris dan Indonesia.  

### Minimum similarity threshold
The minimum percentage of similarity for a program pair to be reported. The value is from 0 to 100 inclusive. The threshold will be applied to the highest level of similarity. For example, type III will be selected if all types of similarities are considered. Type II will be selected if only type I and type II similarities are considered.

### Minimum matching length
It defines how many similar tokens ('words') are required for a code segment to be reported. Larger value will mitigate the occurrence of coincidental similarity, but it will make the tool less resilient to program disguises.

### Template directory path
This refers to a directory containing tenplate code stored as program files. The tool will try to remove all template code in student submissions prior comparison.

### Common code
If this feature is turned on, the tool will try to remove all similar code segments that are common among student submissions. This might result in longer processing time.

### Reported similarities
The instructor can select reported types of similarity. If the submission language is other than Java and Python, only JPlag should be selected.
