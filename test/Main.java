import java.io.IOException;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		System.out.printf("Hello!\n");

		Query query = null;
		try {
			query = new Query();
		} catch (IOException e) {
			System.err.printf("\nError creating query: %s", e.getMessage());
			System.exit(0);
		}
 
		/*
		 * Simple menu system
		 */

		Scanner input = new Scanner(System.in);
		int choice = -1;
		while (choice != 0) {
			System.out.printf("\nChoose your option:");
			System.out.printf("\n\n1) Run a wordcount on a text file");
			System.out.printf("\n2) Perform a range query");
			System.out.printf("\n\n0) Exit");
			System.out.printf("\n\nYour Selection: ");

			choice = input.nextInt();

			switch (choice) {
			case 1:
				System.out.printf("\n\nEnter a text filename: ");
				String fileName = input.next();
				query.runWordCount(fileName);
				break;
			case 2:
				System.out
						.printf("\n\nInput four integers separated by commas: ");
				String coords = input.next();
				String[] split = coords.split(",");
				Integer[] num = new Integer[4];
				int i = 0;
				for (String tmp : split) {
					num[i] = Integer.parseInt(tmp.trim());
					i++;
				}
				try {
					query.runRangeQuery(num[0], num[1], num[2], num[3]);
				} catch (InterruptedException e) {
					System.err.printf("\nError: %s", e.getMessage());
				}
				break;
			default: // do nothing
				break;
			}

		}

		/*
		 * Code above this is testing
		 */

		// query.runWordCount("babbage.txt");
		// TODO - remove this after testing

		// try {
		// if (query.runRangeQuery(0, 0, 130, 130)) {
		// System.out.printf("\nQuery succeeded!");
		// } else {
		// System.err.printf("\nQuery failed.");
		// }
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}
}
