import java.util.Scanner;

public class MainClass {

	/**
	 * @param args
	 * @throws BigIntFormatException
	 */

	public static void main(String[] args) throws BigIntFormatException {
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		String line;

		System.out.println("Type q to quit. ");
		System.out.println("+, -, *, ! are legal operations.");
		System.out.print("Enter here >> ");
		Controller c;
		while (!(line = scan.nextLine().trim().replace(" ", "").toUpperCase())
				.equals("Q")) {
			c = new Controller(); // release memory
			if (line.equals("H")) {
				System.out.println(c.getHelp());
			} else {
				if (c.run(line)) {
					System.out.println(c.getInformation());

				} else {
					System.out.println(c.getInformation());

				}
			}
			System.out.print("Enter here >> ");

		}
		System.out.println("Bye");

	}

}
