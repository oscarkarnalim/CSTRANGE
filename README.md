# USTRANGE: User-oriented Similarity TRacker in Academia with Natural lanGuage Explanation

**USTRANGE** \(User-oriented Similarity TRacker in Academia with Natural lanGuage Explanation\) is an user-friendly tool to observe similarities among submissions with three levels of similarity: pseudo-semantic (type III code clone), syntactic (type II code clone), and surface (type I code clone). Type III similarity is measured via [JPlag](https://github.com/jplag/jplag) version 2.12.1 (which is the latest at the development stage of this tool). Type II similarity is measured via [STRANGE](https://github.com/oscarkarnalim/strange) version 1.1. Type I similarity is measured by simplified STRANGE. 

Unlike many existing tools, USTRANGE sorts similar programs based on the combination of three types of similarities. The tool is applicable for any assessments, even the trivial and the strongly directed ones (where all submissions are expected to be similar at pseudo-semantic level). If all programs share the same type III similarity, the instructor can rely on type II and type I similarities. The instructor is also able to prioritise programs which similarities tend to be verbatim (minor modifications even at surface level). 

USTRANGE is an expanded version of STRANGE by incorporating three types of similarity, optimising the comparison algorithm, and adding an user interface. Further details about STRANGE can be seen in [the corresponding paper](https://doi.org/10.1109/ACCESS.2021.3073703) published in IEEE Access. 
