# NERsuite-result-and-TSV-Comparison
This program is used to measure the true positive, false positive and false negative by comparing the results of NERsuite (A Named Entity Recognition toolkit) and XMI gold standard.  The input format for both files must look like the following:
"beginningIndex<tab>endingIndex<tab>terms<tab>annotation".  For example, "0 8 STANDLEY  Person".  Each line can only contain one record.

PS. I have another program that used to formalise the format of the result.

I understand the beginning index and ending index of each named entity from gold standard may be different from NERsuite output.  Therefore, the program has a function called "indexMatch()" that would change the index in gold standard according to NER result.  Once the index is computerised to be consistent.  It will call a function "comparison" automatically to measure the TP, FP, and FN.

The rules of obtaining TP, FP and FN are described as follow:
True Positive: both terms have the same annotation.
False Positive: the terms found in NERsuite but not in gold standard.
False Negative: the terms found in gold standard but not in NERsuite or the terms have different annotation.


Please notice that terms may contain more than one word, sometimes it can be refered to a sentence.
