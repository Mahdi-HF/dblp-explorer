# Citation Network Analysis implementation explanation

The data I get from the user is like:

- Enter the filepath of the dblp document:
- Enter a keyword:
- Enter an integer (number of tiers):
- Enter a limit for parsing (-1 for complete):

And at last I will output the following data For each paper:

- id
- title
- year
- n_citation
- publisher

## design information

In this implementation, for every paper, I store the id and references, along with some additional information. (in the FormattedOutput.java file)

First I get every line from the file, during this process, I create the following data structures:

a map<Long, Long> with the id as both the key and value. This map hold papers containing the keyword: named currentPapers
a list, the long represents the id of every file: named allPapers
a map<Long, Paper> with the ID as the key, and a paper object as the value: named paperMap
The Paper class contains the references and ID of that paper. As well as a FormattedPaper object that contains other information, this is used for output and uses less memory than the json string.

While I create these data structures I output the papers containing the keyword. (I log them in the terminal)

Next, to create the tier lists, I simply search every file in allPapers and check whether any of the refernces exist in the currentPaper map. I do this using a parallel stream. The list is sorted, this the tier list will be sorted by id. We then output the list. After this, I then update currentPapers with a new map.
