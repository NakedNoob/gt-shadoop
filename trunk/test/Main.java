import java.io.IOException;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		System.out.printf("Hello!\n");

		String outputName = "";
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
				outputName = query.runWordCount(fileName);
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
					outputName = query.runRangeQuery(num[0], num[1], num[2],
							num[3]);
				} catch (InterruptedException e) {
					System.err.printf("\nError: %s", e.getMessage());
					outputName = "";
				}
				break;
			default: // do nothing
				break;
			}

			System.out.printf("\nResults stored in: %s", outputName);
			outputName = "";
		}
		input.close();
	}
}
