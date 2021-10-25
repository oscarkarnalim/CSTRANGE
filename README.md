# USTRANGE: User-oriented Similarity TRacker in Academia with Natural lanGuage Explanation

**USTRANGE** \(User-oriented Similarity TRacker in Academia with Natural lanGuage Explanation\) is an user-friendly tool to observe similarities among submissions with three levels of similarity: pseudo-semantic (type III code clone), syntactic (type II code clone), and surface (type I code clone). Type III similarity is measured via [JPlag](https://github.com/jplag/jplag) version 2.12.1 (which is the latest at the development stage of this tool). Type II similarity is measured via [STRANGE](https://github.com/oscarkarnalim/strange) version 1.1 published in [IEEE Access](https://doi.org/10.1109/ACCESS.2021.3073703). Type I similarity is measured by simplified STRANGE. 

Unlike many existing tools, USTRANGE is applicable for any assessments, even the trivial and the strongly directed ones. If many programs share the same similarity, the instructor can rely on shallower levels of similarity to identify the suspicious ones. The instructor is also able to prioritise programs at which some similarities tend to be verbatim. The tool sorts similar programs based on the combination of three types of similarities.

USTRANGE is an expanded version of STRANGE by incorporating three types of similarity, optimising the comparison algorithm, and adding an user interface. USTRANGE can exclude template code and common code. Template code removal is adapted from STRANGE while common code removal is adapted from [Common Code Segment Selector](https://github.com/oscarkarnalim/c2s2) published in [published at 52nd ACM Technical Symposium on Computer Science Education (SIGCSE 2021)](https://dl.acm.org/doi/10.1145/3408877.3432436).

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
The language of similarity explanation. This is STRANGE's core feature to help instructors or higher officials understand the reported similarities. The oprions are English and Indonesian.

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
This tool uses [STRANGE](https://github.com/oscarkarnalim/strange) to measure type II and type I similarities, [JPlag](https://github.com/jplag/jplag) to measure type III similarity, [ANTLR](https://www.antlr.org/) to tokenise given programs, [Apache Lucene](https://lucene.apache.org/) to identify stop words, [Google Prettify](https://github.com/google/code-prettify) to display source code, [google-java-format](https://github.com/google/google-java-format) to reformat Java code, [YAPF](https://github.com/google/yapf) to reformat Python code, and [JSoup](https://jsoup.org/) to parse JPlag's index page. It also adapts [arunjeyapal's implementation of RKR-GST](https://github.com/arunjeyapal/GreedyStringTiling) and [AayushChaturvedi's implementation of string alignment](https://www.geeksforgeeks.org/sequence-alignment-problem/).
