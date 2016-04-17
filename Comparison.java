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
	int countTest = 0;
	int countResult = 0;
	private int checking = 0;

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
			start();

			System.out.println(test.size());
			System.out.println(result.size());
		}
	}

	public void start() throws IOException {
		if (countTest < test.size() && countResult < result.size()) {
			checking = 0;
			String input = test.get(countTest);
			String insert = result.get(countResult);
			compare(input, insert);
		}
	}

	public void compare(String tester, String comparer) throws IOException {
		// System.out.println(tester);
		// System.out.println(comparer);
		String[] spilter = tester.split("\t");
		String temName = "B-" + spilter[3].toUpperCase();
		String[] segmenter = comparer.split("\t");
		spilter[2] = spilter[2].replaceAll("\\s+", " ");
		spilter[2] = spilter[2].replaceAll("－ ", "－");
		// Compare the beginning number
		if (spilter[0].equals(segmenter[0])) {
			// Compare the ending number
			if (spilter[1].equals(segmenter[1])) {
				// Compare the terms
				if (spilter[2].equals(segmenter[2])) {
					System.out.print("Test file is : " + spilter[3].toUpperCase());
					System.out.print(";\tResult file is : " + segmenter[3].toUpperCase());
					// Compare the annotation
					if (temName.equals(segmenter[3].toUpperCase())) {
						System.out.println(";\tTherefore, They match: " + segmenter[2]);
					} else {
						System.out.println(";\tTherefore, They do not match each other: " + segmenter[2]);
					}
					countTest++;
					countResult++;
					start();

				} else {
					// compare the terms
					countTest++;
					countResult++;
					System.out.println(countResult + "Citation can not be detected!");
					start();
				}
			} else {
				// compare the ending of the term
				String addUp = "";
				String[] temResult = new String[4];
				temResult[0] = spilter[0];
				countResult++;
				String newSen = result.get(countResult);
				String[] newStr = newSen.split("\t");
				temResult[1] = newStr[1];
				if (newStr[2].matches(".*\\p{Punct}")) {
					newStr[2] = newStr[2];
					newStr[3] = segmenter[3];
				} else {
					newStr[2] = " " + newStr[2];
				}

				temResult[2] = segmenter[2] + newStr[2];
				temResult[2] = temResult[2].replaceAll("- ", "-");
				temResult[2] = temResult[2].replaceAll("\\s*&\\s*", " & ");
				// if two terms have different annotations, this would cause
				// problem
				if (segmenter[3].equals(newStr[3])) {
					// System.out.println("i am here");
					temResult[3] = newStr[3];
					addUp = temResult[0] + "\t" + temResult[1] + "\t" + temResult[2] + "\t" + temResult[3];
				} else {
					System.out.println("Two different annotations in one terms");
					countResult++;
					countTest++;
					start();

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