# USTRANGE: User-oriented Similarity TRacker in Academia with Natural lanGuage Explanation

**USTRANGE** \(User-oriented Similarity TRacker in Academia with Natural lanGuage Explanation\) is an user-friendly tool to observe similarities among submissions with three levels of similarity: pseudo-semantic (type III code clone), syntactic (type II code clone), and surface (type I code clone). Type III similarity is measured via [JPlag](https://github.com/jplag/jplag) version 2.12.1 (which is the latest at the development stage of this tool). Type II similarity is measured via [STRANGE](https://github.com/oscarkarnalim/strange) version 1.1. Type I similarity is measured by simplified STRANGE. 

Unlike many existing tools, USTRANGE is applicable for any assessments, even the trivial and the strongly directed ones. If all programs share the same pseudo-semantic similarity, the instructor can rely on type II and type I similarities. The instructor is also able to prioritise programs which similarities tend to be verbatim (minor modifications even at surface level). The tool sorts similar programs based on the combination of three types of similarities.

USTRANGE is an expanded version of STRANGE by incorporating three types of similarity, optimising the comparison algorithm, and adding an user interface. Further details about STRANGE can be seen in [the corresponding paper](https://doi.org/10.1109/ACCESS.2021.3073703) published in IEEE Access. USTRANGE can exclude template code and common code. Template code removal is adapted from STRANGE while common code removal is adapted from [Common Code Segment Selector](https://github.com/oscarkarnalim/c2s2).

Currently, the tool supports Java and Python submissions. The tool can also act as a simple user interface for JPlag, allowing five more acceptable languages of submissions: C, C++, C#, and Text.


