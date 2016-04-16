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
		// System.out.println(test.size());
		// System.out.println("countTest" + countTest);
		// System.out.println(result.size());
		// System.out.println("countResult" + countResult);

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
		System.out.println("hello:    " + spilter[0]);
		System.out.println(segmenter[0]);
		if (spilter[0].equals(segmenter[0])) {
			// System.out.println("hello");
			if (spilter[1].equals(segmenter[1])) {
				System.out.println(spilter[2]);
				System.out.println(segmenter[2]);
				if (spilter[2].equals(segmenter[2])) {
					System.out.println("alan");
					System.out.print(".  Test file is : " + spilter[3].toUpperCase());
					System.out.print(".  Result file is : " + segmenter[3].toUpperCase());
					if (temName.equals(segmenter[3].toUpperCase())) {
						System.out.println(".  Therefore, They match: " + segmenter[2]);
					} else {
						System.out.println(".  Therefore, They do not match each other:" + segmenter[2]);
					}
					countTest++;
					countResult++;
					start();
				}
			} else {
				String addUp = "";
				String[] temResult = new String[4];
				temResult[0] = spilter[0];
				countResult++;
				String s2 = result.get(countResult);
				String[] tem = s2.split("\t");
				temResult[1] = tem[1];
				System.out.println(tem[2]);

				if (tem[2].equals(",") || tem[2].equals("-") || checking == 1) {
					tem[2] = tem[2];
					tem[3] = segmenter[3];
					if (tem[2].equals("-"))
						checking = 1;
				} else {
					tem[2] = " " + tem[2];
				}
				temResult[2] = segmenter[2] + tem[2];
				if (segmenter[3].equals(tem[3])) {
					// System.out.println("i am here");
					temResult[3] = tem[3];
					addUp = temResult[0] + "\t" + temResult[1] + "\t" + temResult[2] + "\t" + temResult[3];
					System.out.println(
							temResult[0] + "\t" + temResult[1] + "\t" + temResult[2] + "\t" + temResult[3] + "\n");

				} else {
					System.out.println("false");
					countResult++;
					countTest++;
					start();

				}
				// System.out.println("help me");
				// System.out.println(tester + " " + addUp);

				compare(tester, addUp);

				// System.out.println("abc");
			}
		} else {
			// System.out.println("what is going on");
			countResult++;
			start();

		}

	}

	public static void main(String args[]) throws IOException {
		String path = "/Users/AlanHo/Documents/DissertationLibrary/Programming testing/";
		new Comparison(path + "(formatted)test-8.tsv", path + "(formatted)result8.features.txt").readFile();

	}
}