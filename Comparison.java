package Comparison;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Comparison {
	private String testPath;
	private String resultPath;
	private ArrayList<String> test = new ArrayList<String>();
	private ArrayList<String> result = new ArrayList<String>();
	private int countTest = 0;
	private int countResult = 0;
	private int truePositive = 0;
	private int falseNegative = 0;
	private int numberOfAnnotation = 0;

	private int number;
	private int nerBegin = 0;

	public Comparison(String test, String result) {
		resultPath = result;
		testPath = test;
	}

	public void readFile() throws IOException {
		BufferedReader brT = new BufferedReader(new FileReader(testPath));
		BufferedReader brR = new BufferedReader(new FileReader(resultPath));
		try {
			String lineT = brT.readLine();
			String lineR = brR.readLine();
			while (lineT != null) {
				// System.out.println(line);
				test.add(lineT);
				lineT = brT.readLine();
			}

			while (lineR != null) {
				// System.out.println(line);
				result.add(lineR);
				lineR = brR.readLine();
			}
		} finally {
			brT.close();
			brR.close();
			indexMatch();
			countResult = 0;
			countTest = 0;
			numOfAnno();
			start();
			int falsePositive = numberOfAnnotation - truePositive;
			double precision = (truePositive / ((double) truePositive + (double) (numberOfAnnotation - truePositive)));
			double recall = (truePositive / ((double) truePositive + (double) falseNegative));
			System.out.println("The number of Annotations: " + numberOfAnnotation);
			System.out.println("True Positive: " + truePositive);
			System.out.println("False Positive " + falsePositive);
			System.out.println("False Negative: " + falseNegative);
			System.out.println("Precision: " + precision);
			System.out.println("Recall: " + recall);
			System.out.println("F-Measure: " + ((2 * precision * recall) / (precision + recall)));
			System.out.println(test.size());
			System.out.println(result.size());
		}
	}

	public void indexMatch() throws IOException {

		// checking if they are the same terms

		if (countTest < test.size() && countResult < result.size()) {
			String[] nerSep = result.get(countResult).split("\t");
			String[] goldSep = test.get(countTest).split("\t");
			// System.out.println("initial checking" + test.get(countTest));
			goldSep[2] = goldSep[2].replaceAll("\\s+", " ").trim();
			int goldBegin = Integer.parseInt(goldSep[0]);

			if (!goldSep[3].equalsIgnoreCase("citation")) {
				// System.out.println(goldSep[2].trim() + " and " +
				// nerSep[2].trim());
				if (goldSep[2].trim().startsWith(nerSep[2].trim())) {
					nerBegin = Integer.parseInt(nerSep[0]);
					// System.out.println("initial checking" + nerBegin);
				}

				if (goldSep[2].contains(nerSep[2]) && (nerBegin - goldBegin) <= 40 && (goldBegin - nerBegin) <= 40) {

					String[] formatedIndex = new String[4];
					if (goldSep[2].endsWith(".")) {
						goldSep[2] = goldSep[2].substring(0, goldSep[2].length() - 1);

					}
					formatedIndex[0] = "" + nerBegin + "";
					// System.out.println("initial checking" +
					// formatedIndex[0]);
					formatedIndex[2] = goldSep[2];
					formatedIndex[3] = goldSep[3];
					// this is a sentence
					if (goldSep[2].matches(".*\\p{Punct}") || goldSep[2].contains(" ") || goldSep[2].contains("-")
							|| goldSep[2].contains("&")) {

						if (goldSep[2].trim().endsWith(nerSep[2].trim())) {
							// System.out.println("sentence checking");
							formatedIndex[1] = nerSep[1];

							String element = formatedIndex[0] + "\t" + formatedIndex[1] + "\t" + formatedIndex[2] + "\t"
									+ formatedIndex[3];
							System.out.println("Sentence: " + formatedIndex[0] + " " + formatedIndex[1] + " "
									+ formatedIndex[2] + " " + formatedIndex[3]);
							test.set(countTest, element);
							if (countTest < test.size() - 1) {
								String[] verifyDouble = test.get(countTest + 1).split("\t");

								if (!goldSep[0].equals(verifyDouble[0]) && !goldSep[1].equals(verifyDouble[1])) {

									countTest++;
									countResult++;
									System.out.println();
									indexMatch();
								} else {
									int space = 0;
									for (int i = 1; i < goldSep[2].length(); i++) {
										if (goldSep[2].charAt(i) == ' '
												|| goldSep[2].substring(i - 1, i).matches(".*\\p{Punct}"))
											space++;
									}

									countTest++;
									countResult = countResult - (space + 1);
									System.out.println();
									indexMatch();

								}
							}
						} else {

							countResult++;
							indexMatch();
						}
						// if this's not sentence, then it is single term
					} else {

						if ((goldSep[2].trim()).equals(nerSep[2].trim())) {
							formatedIndex[1] = nerSep[1];
							String element = formatedIndex[0] + "\t" + formatedIndex[1] + "\t" + formatedIndex[2] + "\t"
									+ formatedIndex[3];
							System.out.println("Single term: " + formatedIndex[0] + " " + formatedIndex[1] + " "
									+ formatedIndex[2] + " " + formatedIndex[3]);
							test.set(countTest, element);
							if (countTest < test.size() - 1) {
								String[] verifyDouble = test.get(countTest + 1).split("\t");

								if (!goldSep[0].equals(verifyDouble[0]) && !goldSep[1].equals(verifyDouble[1])) {
									// System.out.println("single checking");
									// System.out.println(test.get(countTest));
									countTest++;
									// System.out.println(test.get(countTest));
									countResult++;
									// System.out.println();
									indexMatch();
								} else {

									countTest++;
									countResult = countResult - 1;
									System.out.println();
									indexMatch();
								}

							}
						} else {

							countResult++;
							indexMatch();
						}
					}

				} else {
					countResult++;
					indexMatch();
				}
			} else {
				System.out.println("it is citation");
				countTest++;
				// System.out.println(test.get(countTest));
				indexMatch();
			}
		}
	}

	public void start() throws IOException {
		if (countTest < test.size() && countResult < result.size()) {
			String input = test.get(countTest);
			String insert = result.get(countResult);
			compare(input, insert);

		}
	}

	public void numOfAnno() throws IOException {

		for (String check : result) {
			// System.out.println(check);
			if (check.contains("B-taxon") || check.contains("B-habitat") || check.contains("B-anatomicalEntity"))
				numberOfAnnotation++;

		}

	}

	public void compare(String tester, String comparer) throws IOException {
		// word pre-processing (format)
		String[] spilter = tester.split("\t");
		if (spilter[3].equalsIgnoreCase("citation")) {
			countTest++;
			start();
		}
		String temName = "B-" + spilter[3].toUpperCase();
		String[] segmenter = comparer.split("\t");
		spilter[2] = spilter[2].replaceAll("\\s+", " ");
		spilter[2] = spilter[2].replaceAll("\\s*－\\s*", "－");
		// System.out.println(spilter[0] + " " + spilter[2]);
		// System.out.println(segmenter[0] + " " + segmenter[2]);
		// Compare the beginning index

		if (spilter[0].equals(segmenter[0])) {
			// System.out.println("the first layer");
			// Compare the ending index
			if (spilter[1].equals(segmenter[1])) {
				// System.out.println("the second layer");

				// Compare the terms
				if (spilter[2].equals(segmenter[2])) {
					// System.out.println("the third layer");
					System.out.print("Test file is : " + spilter[3].toUpperCase());
					System.out.print(";\tResult file is : " + segmenter[3].toUpperCase());

					// Compare the annotation
					if (temName.equals(segmenter[3].toUpperCase())) {

						System.out.println(";\tThey match: " + segmenter[2] + "\n");

						// Calculating the True Positive
						if (temName.equalsIgnoreCase("B-anatomicalEntity") || temName.equalsIgnoreCase("B-taxon")
								|| temName.equalsIgnoreCase("B-habitat")) {
							truePositive++;
						} else {
							countTest++;
							start();
						}

					} else {
						System.out.println(";\tTherefore, They don't match each other: " + segmenter[2] + "\n");
						if (temName.equalsIgnoreCase("B-anatomicalEntity") || temName.equalsIgnoreCase("B-taxon")
								|| temName.equalsIgnoreCase("B-habitat"))
							falseNegative++;

					}
					if (countTest < test.size() - 1) {
						String[] verifyDouble = test.get(countTest + 1).split("\t");
						// System.out.println("index checking");
						if (!spilter[0].equals(verifyDouble[0]) && !spilter[1].equals(verifyDouble[1])) {
							// System.out.println("not matching");
							// System.out.println(countTest);
							countTest++;
							// System.out.println(countTest);
							countResult++;
							start();
						} else {

							int space = 0;
							for (int i = 1; i < spilter[2].length(); i++) {
								if (spilter[2].charAt(i) == ' ')
									space++;
							}
							// System.out.println("index checking");
							countTest++;
							countResult = countResult - (space + 1);
							System.out.println();
							start();

						}
					}
				} else {
					// The terms are different from golden standard
					countTest++;
					countResult++;
					System.out.println("Citation can not be detected: " + segmenter[2] + "\n");
					start();
				}
			} else {

				// There are more than one words that making up a term
				String addUp = "";
				String[] temResult = new String[4];
				temResult[0] = spilter[0];

				// get the new entity from the resultText and make up a new term
				countResult++;
				String newSen = result.get(countResult);
				String[] newStr = newSen.split("\t");
				temResult[1] = newStr[1];

				// remove all the duplicate punctuation
				if (newStr[2].matches(".*\\p{Punct}")) {
					newStr[2] = newStr[2];
					newStr[3] = segmenter[3];
				} else {
					newStr[2] = " " + newStr[2];
				}

				// pre-processing
				temResult[2] = segmenter[2] + newStr[2];
				temResult[2] = temResult[2].replaceAll("\\s+", " ");
				temResult[2] = temResult[2].replaceAll("\\s*-\\s*", "-");
				temResult[2] = temResult[2].replaceAll("\\s*&\\s*", " & ");

				// two terms with different annotations would cause problem
				if (segmenter[3].equals(newStr[3])) {

					temResult[3] = newStr[3];
					addUp = temResult[0] + "\t" + temResult[1] + "\t" + temResult[2] + "\t" + temResult[3];

					// Not the end of the loop, so there is no need to add TP/FN
				} else {
					System.out.println("Two different annotations in one terms: " + segmenter[2] + newStr[2] + "\n");
					if (countTest < test.size() - 1) {
						String[] verifyDouble = test.get(countTest + 1).split("\t");

						if (!temResult[0].equals(verifyDouble[0]) && !temResult[1].equals(verifyDouble[1])) {

							countTest++;
							countResult++;
							start();
						} else {
							int space = 0;
							for (int i = 1; i < spilter[2].length(); i++) {
								if (spilter[2].charAt(i) == ' ')
									space++;
							}

							countTest++;
							countResult = countResult - (space + 1);
							System.out.println();
							start();

						}

					}
					// calculating the false negative
					if (newStr[3].equalsIgnoreCase("B-anatomicalEntity") || newStr[3].equalsIgnoreCase("B-taxon")
							|| newStr[3].equalsIgnoreCase("B-habitat"))
						falseNegative++;
				}
				compare(tester, addUp);
			}
		} else {
			countResult++;
			start();
		}

	}

	public static void main(String args[]) throws IOException {
		String path = "/Users/AlanHo/Documents/DissertationLibrary/Programming testing/";
		new Comparison(path + "Pair3.tsv", path + "Pair3.txt").readFile();

	}
}