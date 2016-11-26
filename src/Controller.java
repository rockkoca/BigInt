public class Controller {
	private String[] op = { "*", "+", "-", "!" };
	private String math;
	private int operator;
	private String information = "";
	private BigInt result;
	private BigInt first;
	private BigInt second;
	private String help = "+, -, *, ! are legal operations, such as 2+5, 2*5, -2*5, 2!, 2-5";

	public Controller() {
	}

	// CHECK THE input OF INPUT, if has valid operator then return the
	// compute status. Otherwise, return false.
	public boolean run(String math) {
		setMath(math);
		for (int i = 0; i < op.length; i++) {
			if (math.contains(op[i])) {
				operator = i;
				return compute();
			}
		}
		information = "No operator was found. Try again!";
		return false;
	}

	/**
	 * compute the user's input
	 * 
	 * @return true or false indicates has a result or not.
	 * 
	 *         the result BigInt will store into the result.
	 */
	public boolean compute() {
		if (operator < 0)
			return false;
		// +-*
		if (operator < 3) {
			String[] elements = math.split(String.format("\\%s", op[operator]));
			if (elements.length != 2) {
				information = "Bad inputs!";
				return false;
			} else {
				try {
					first = new BigInt(elements[0]);
				} catch (BigIntFormatException e) {
					information = "First part input is wrong!";
					return false;
				}
				try {
					second = new BigInt(elements[1]);
				} catch (BigIntFormatException e) {
					information = "Second part input is wrong!";
					return false;
				}

				if (operator == 1) {
					try {
						result = first.add(second);
						information = String.format("%s+%s \n= %s", first,
								second, result);
						return true;
					} catch (BigIntFormatException e) {
						information = "Internal error!";
						return false;
					}
				} else if (operator == 2) {
					try {
						result = first.subtract(second);
						information = String.format("%s-%s \n= %s", first,
								second, result);
						return true;
					} catch (BigIntFormatException e) {
						information = "Internal error!";
						return false;
					}
				} else if (operator == 0) {
					try {
						result = first.multiply(second);
						information = String.format("%s*%s \n= %s", first,
								second, result);
						return true;
					} catch (BigIntFormatException e) {
						information = "Internal error!";
						return false;
					}
				}

			}

		} else if (operator == 3) {// factorial
			if (!math.endsWith("!")) {
				information = "Bad inputs! Are you trying to do factorial?";
				return false;
			} else {
				try {
					first = new BigInt(math.substring(0, math.length() - 1));
				} catch (BigIntFormatException e) {
					information = "First part input is wrong!";
					return false;
				}

				try {
					result = first.factorial();
					information = String.format("%s! \n= %s", first, result);
					return true;
				} catch (BigIntFormatException e) {
					information = "Internal error!";
					return false;
				}

			}
		} else {
			information = "Too much operators!";
			return false;

		}

		return true;
	}

	public BigInt getResult() {
		return result;
	}

	public String getMath() {
		return math;
	}

	public void setMath(String math) {
		this.math = math;
	}

	public String[] getOp() {
		return op;
	}

	public String getInformation() {
		return information;
	}
	
	public String getHelp() {
		return help;
	}
}
