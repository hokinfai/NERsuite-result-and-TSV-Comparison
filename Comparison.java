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
			falsePositive();
			start();
			int falsePositive = numberOfAnnotation - truePositive;
			double precision = (truePositive / ((double) truePositive + (double) (numberOfAnnotation - truePositive)));
			double recall = (truePositive / ((double) truePositive + (double) falseNegative));
			System.out.println("True Positive: " + truePositive);
			System.out.println("False Negative: " + falseNegative);
			System.out.println("False Positive " + falsePositive);
			System.out.println("The number of Annotations: " + numberOfAnnotation);
			System.out.println("Precision: " + precision);
			System.out.println("Recall: " + recall);
			System.out.println("F-Measure: " + ((2 * precision * recall) / (precision + recall)));
			System.out.println(test.size());
			System.out.println(result.size());
		}
	}

	public void start() throws IOException {
		if (countTest < test.size() && countResult < result.size()) {
			String input = test.get(countTest);
			String insert = result.get(countResult);
			compare(input, insert);

		}
	}

	public void falsePositive() throws IOException {

		for (String check : result) {
			System.out.println(check);
			if (check.contains("B-taxon") || check.contains("B-habitat") || check.contains("B-anatomicalEntity"))
				numberOfAnnotation++;

		}

	}

	public void compare(String tester, String comparer) throws IOException {
		// word pre-processing (format)
		String[] spilter = tester.split("\t");
		String temName = "B-" + spilter[3].toUpperCase();
		String[] segmenter = comparer.split("\t");
		spilter[2] = spilter[2].replaceAll("\\s+", " ");
		spilter[2] = spilter[2].replaceAll("\\s*－\\s*", "－");

		// Compare the beginning index
		if (spilter[0].equals(segmenter[0])) {

			// Compare the ending index
			if (spilter[1].equals(segmenter[1])) {

				// Compare the terms
				if (spilter[2].equals(segmenter[2])) {
					System.out.print("Test file is : " + spilter[3].toUpperCase());
					System.out.print(";\tResult file is : " + segmenter[3].toUpperCase());

					// Compare the annotation
					if (temName.equals(segmenter[3].toUpperCase())) {
						System.out.println(";\tThey match: " + segmenter[2] + "\n");
						// Calculating the True Positive
						if (temName.equalsIgnoreCase("B-anatomicalEntity") || temName.equalsIgnoreCase("B-taxon")
								|| temName.equalsIgnoreCase("B-habitat"))
							truePositive++;
					} else {
						System.out.println(";\tTherefore, They don't match each other: " + segmenter[2] + "\n");
						if (temName.equalsIgnoreCase("B-anatomicalEntity") || temName.equalsIgnoreCase("B-taxon")
								|| temName.equalsIgnoreCase("B-habitat"))
							falseNegative++;
					}
					countTest++;
					countResult++;
					start();

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
					countResult++;
					countTest++;
					start();

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
		new Comparison(path + "(formatted)test-8.tsv", path + "(formatted)result8.features.txt").readFile();

	}
}