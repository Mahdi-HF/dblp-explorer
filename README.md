# CitationNetworkAnalysis

Scientific papers cite other relevant articles. Junior scientists usually struggle to find the most relevant papers. In this assignment, you will help (for now, only the computer scientists).

Your program takes a (large) JSON file that contains the information about the papers, a keyword and an integer n. It first searches the articles with titles containing the keyword, then it finds the papers that cited the papers in this step. We call them tier-1 papers. For tire-2 papers, we find the papers that were cited by tire-1 papers. More generally, tier-K papers are the papers that are cited in tier-(K-1) papers.

The output should be information of the papers all tiers up to level n ranked in order of importance. You decide how to define importance.
