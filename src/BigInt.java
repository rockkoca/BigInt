import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BigInt implements Comparable<BigInt> {

	// digits of the binary representation are stored in nodes
	private class Node {
		public int data;
		public Node next;
		public Node prev;

		public Node(int data) {
			if (data != 0 && data != 1) {
				throw new IllegalArgumentException("data = " + data);
			}
			this.data = data;
		}
	}

	// beginning and end of the doubly linked list
	private Node head, tail;
	// number of nodes in the linked list
	private int size;

	// BigInt(String val) Construct a BigInt object and initialize it with the
	// integer represented by the String. Throw an appropriate exception
	// (BigIntFormatException) if the string does not represent a signed integer
	// (i.e. contains illegal characters)
	public BigInt(String val) throws BigIntFormatException {
		val = val.trim();
		String[] parts = val.split("\\s+");
		// System.out.println("val = " + val + ", parts = "
		// + Arrays.toString(parts));
		// a valid number comes in 1 or 2 pieces
		if (parts.length == 0 || parts.length > 2 || parts[0].length() == 0) {
			throw new BigIntFormatException();
		}
		// if 2 pieces, the first piece must be + or -
		if (parts.length == 2 && !parts[0].equals("-") && !parts[0].equals("+")) {
			throw new BigIntFormatException();
		}

		String number = parts[0] + ((parts.length > 1) ? parts[1] : "");
		int sign = +1;
		if (number.charAt(0) == '-') {
			sign = -1;
		}
		if (number.charAt(0) == '+' || number.charAt(0) == '-') {
			number = number.substring(1); // drop the sign
		}

		// only digits in number?
		for (int i = 0; i < number.length(); i++) {
			char c = number.charAt(i);
			// if (!(c >= '0' && c <= '9')) {
			if (!Character.isDigit(c)) {
				throw new BigIntFormatException();
			}
		}

		// construct the linked list
		while (!number.equals("0")) {
			int d = mod2(number);
			Node n = new Node(d);
			if (d == 0) {
			}
			if (tail == null) {
				tail = n;
				head = n;
			} else {
				n.next = head;
				head.prev = n;
				head = n;
			}
			size++;
			number = divideBy2(number);
		}
		// add a 0 for the sign (we will take the 2's complement
		// later if negative)
		Node n = new Node(0);
		n.next = head;
		if (head != null) {
			head.prev = n;
		} else {
			tail = n;
		}
		head = n;
		size++;

		if (sign == -1) {
			// 1's complement
			Node p = head;
			while (p != null) {
				p.data = 1 - p.data;
				p = p.next;
			}
			// 2's complement: just add 1
			int carry = 1;
			p = tail;
			while (p != null) {
				p.data += carry;
				carry = p.data / 2;
				p.data %= 2;
				p = p.prev;
			}
		}

		// remove any leading 0's and 1's (except the last 0 or 1)
		removeLeading0sor1s();
	}

	// BigInt(BigInt val) This is the copy constructor. It should make a deep
	// copy of val. Making a deep copy is not strictly necessary since as
	// designed a BigInt is immutable, but it is good practice.
	public BigInt(BigInt val) throws BigIntFormatException {
		Node valNext = val.head;
		Node next = head = new Node(valNext.data);
		Node temp;
		valNext = valNext.next;
		while (valNext != null) {
			temp = new Node(valNext.data);
			temp.prev = next;
			next.next = temp;
			next = temp;
			valNext = valNext.next;
		}
		tail = next;
		size = val.size;
	}

	// BigInt(long val)
	// Construct a BigInt object and intitialize it wth the value stored in val
	public BigInt(long val) throws BigIntFormatException {
		this(val + "");
	}

	// BigInt add(BigInt val) Returns a BigInt whose value is (this + val)
	public BigInt add(BigInt val) throws BigIntFormatException {
		int maxSize = Math.max(size, val.size) + 1;
		BigInt local = this;
		BigInt that = val;
		local.fitSize(maxSize);
		that.fitSize(maxSize);
		Node p = local.tail;
		Node q = that.tail;
		int carry = 0;
		BigInt answer = new BigInt("0");
		answer.tail = null;
		answer.head = null;
		Node ans;
		answer.size = 0;
		while (true) {

			carry = p.data + q.data + carry;
			if (carry > 2) {
				ans = new Node(1);
				carry = 1;
			} else if (carry == 2) {
				ans = new Node(0);
				carry = 1;
			} else if (carry == 1) {
				ans = new Node(1);
				carry = 0;
			} else {
				ans = new Node(0);
				carry = 0;
			}

			if (answer.tail == null) {
				answer.tail = ans;
				answer.head = ans;
			} else {
				ans.next = answer.head;
				answer.head.prev = ans;
				answer.head = ans;
			}

			answer.size++;
			if (answer.size == maxSize) {
				break;
			}
			p = p.prev;
			q = q.prev;

		}

		answer.removeLeading0sor1s();
		return answer;
	}

	// BigInt addm(BigInt val) IGNORE OVERFOLLOW
	// Returns a BigInt whose value is (this + val)
	public BigInt addM(BigInt val) throws BigIntFormatException {
		int maxSize = size;
		BigInt local = this;
		BigInt that = val;
		Node p = local.tail;
		Node q = that.tail;
		int carry = 0;
		BigInt answer = new BigInt("0");
		answer.tail = null;
		answer.head = null;
		Node ans;
		answer.size = 0;
		while (true) {

			carry = p.data + q.data + carry;
			if (carry > 2) {
				ans = new Node(1);
				carry = 1;
			} else if (carry == 2) {
				ans = new Node(0);
				carry = 1;
			} else if (carry == 1) {
				ans = new Node(1);
				carry = 0;
			} else {
				ans = new Node(0);
				carry = 0;
			}

			if (answer.tail == null) {
				answer.tail = ans;
				answer.head = ans;
			} else {
				ans.next = answer.head;
				answer.head.prev = ans;
				answer.head = ans;
			}

			answer.size++;
			if (answer.size == maxSize) {
				break;
			}
			p = p.prev;
			q = q.prev;

		}

		return answer;
	}

	// BigInt multiply(BigInt val)
	// Returns a BigInt whose value is (this * val)
	// Using Booth's algorithm
	public BigInt multiply(BigInt val) throws BigIntFormatException {

		int maxSize = size + val.size;
		// maxSize = 100;
		BigInt multiplicand;
		BigInt multiplicandMinus; // to speed up the subtract and save mermory
									// theoretically
		BigInt prUpper = new BigInt("0");
		prUpper.fitSize(maxSize);
		BigInt prLower;
		int current;
		int pre = 0;
		BigInt local = this;
		BigInt that = new BigInt(val);
		local.fitSize(maxSize);
		that.fitSize(maxSize);
		multiplicand = local;
		multiplicandMinus = local.flip();

		prLower = that;
		current = prLower.tail.data;
		Node hold;
		for (int i = 0; i < maxSize; i++) {

			if (current != pre) {
				if (current == 0) {
					prUpper = prUpper.addM(multiplicand);

				} else {
					prUpper = prUpper.addM(multiplicandMinus);
				}
				prUpper.fitSize(maxSize);
			}

			// right shift pr

			// prLower tail to prUpper HEAD
			hold = prLower.tail;
			hold.next = prUpper.head;
			prUpper.head.prev = hold;
			prUpper.head = hold;
			// prLower.tail TO NULL
			prLower.tail.prev.next = null;
			prLower.tail = prLower.tail.prev;

			hold = prUpper.tail;
			hold.next = prLower.head;
			prLower.head.prev = hold;
			prLower.head = hold;

			prUpper.tail.prev.next = null;
			prUpper.tail = prUpper.tail.prev;

			pre = current;
			current = prLower.tail.data;
		}
		// System.out.println(add_count + ":" + maxSize);
		prLower.removeLeading0sor1s();
		return prLower;
	}

	// BigInt subtract(BigInt val) Returns a BigInt whose value is (this - val)
	public BigInt subtract(BigInt val) throws BigIntFormatException {
		return this.add(val.flip());
	}

	// BigInt factorial() Returns a BigInt whose value is this!
	public BigInt factorial() throws BigIntFormatException {
		BigInt answer = new BigInt("1");
		BigInt i = new BigInt("1");
		BigInt one = new BigInt("1");
		BigInt end = this.add(one);

		while (i.compareTo(end) < 0) {
			answer = answer.multiply(i);
			// System.out.println(answer + ":" + i);
			i = i.add(one);
		}
		answer.removeLeading0sor1s();
		return answer;
	}

	// int compareTo(BigInt) Have the BigInt class implement the Comparable
	// interface.
	public int compareTo(BigInt b) {
		if (this.head.data < b.head.data)
			return 1;
		else if (this.head.data > b.head.data)
			return -1;
		// make copy
		BigInt local = this;
		BigInt that = b;

		if (this.size != b.size) {
			int maxSize = Math.max(size, b.size);

			local.fitSize(maxSize);
			that.fitSize(maxSize);
		}
		Node next = local.head;
		Node bNext = that.head;
		if (this.head.data == 1) {
			while (true) {
				if (next.data < bNext.data) {
					return -1;
				} else if (next.data > bNext.data) {
					return 1;
				}
				next = next.next;
				bNext = bNext.next;
				if (next == null)
					break;
			}

		} else if (local.head.data == 0) {

			while (true) {
				if (next.data > bNext.data) {
					return 1;
				} else if (next.data < bNext.data) {
					return -1;
				}
				next = next.next;
				bNext = bNext.next;
				if (next == null)
					break;
			}
		}

		return 0;
	}

	// boolean equals(Object)
	// Override the equals() method from Object.
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof BigInt) {
			BigInt b = (BigInt) obj;
			BigInt local = this;

			BigInt that = b;

			if (size != b.size) {
				int maxSize = Math.max(size, b.size);
				local.fitSize(maxSize);
				that.fitSize(maxSize);

			}
			Node p = local.head;
			Node q = that.head;
			while (p != null) {
				if (p.data != q.data) {
					return false;
				}
				p = p.next;
				q = q.next;
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param hex
	 *            the string hex
	 * @return string decimal
	 */
	private String hexToDecimal(String hex) {
		List<Integer> dec = new ArrayList<Integer>();
		dec.add(0);
		Map<Character, Integer> exToDTable = new HashMap<Character, Integer>();
		exToDTable.put('0', 0);
		exToDTable.put('1', 1);
		exToDTable.put('2', 2);
		exToDTable.put('3', 3);
		exToDTable.put('4', 4);
		exToDTable.put('5', 5);
		exToDTable.put('6', 6);
		exToDTable.put('7', 7);
		exToDTable.put('8', 8);
		exToDTable.put('9', 9);
		exToDTable.put('A', 10);
		exToDTable.put('B', 11);
		exToDTable.put('C', 12);
		exToDTable.put('D', 13);
		exToDTable.put('E', 14);
		exToDTable.put('F', 15);
		int hexlen = hex.length();
		for (int i = 0; i < hexlen; i++) {
			int carry = exToDTable.get(hex.charAt(i));
			for (int j = dec.size() - 1; j >= 0; j--) {
				int val = dec.get(j) * 16 + carry;
				dec.set(j, val % 10);
				carry = val / 10;
			}
			while (carry > 0) {
				dec.add(0, carry % 10);
				carry /= 10;
			}
		}
		StringBuffer d = new StringBuffer();

		for (int s : dec) {
			d.append(s);

		}
		return d.toString();
	}

	/**
	 * convert to hex string first, and then convert to decimal string
	 * 
	 * @return the decimal format of the big integer
	 */
	public String toString() {
		StringBuffer sb = null;
		try {
			sb = new StringBuffer(toString2());
		} catch (BigIntFormatException e) {
			e.printStackTrace();
		}
		if (sb.length() % 4 != 0) {
			while (sb.length() % 4 != 0) {
				sb.insert(0, "0");
			}
		}

		boolean negtive = (head.data == 1);
		StringBuffer hex = new StringBuffer();
		StringBuffer key = new StringBuffer();
		int count = 0;
		Map<String, String> bToHexTable = new HashMap<String, String>();
		bToHexTable.put("0000", "0");
		bToHexTable.put("0001", "1");
		bToHexTable.put("0010", "2");
		bToHexTable.put("0011", "3");
		bToHexTable.put("0100", "4");
		bToHexTable.put("0101", "5");
		bToHexTable.put("0110", "6");
		bToHexTable.put("0111", "7");
		bToHexTable.put("1000", "8");
		bToHexTable.put("1001", "9");
		bToHexTable.put("1010", "A");
		bToHexTable.put("1011", "B");
		bToHexTable.put("1100", "C");
		bToHexTable.put("1101", "D");
		bToHexTable.put("1110", "E");
		bToHexTable.put("1111", "F");

		for (int i = 0; i < sb.length(); i++) {
			key.append(sb.charAt(i));
			count++;
			if (count == 4) {
				hex.append(bToHexTable.get(key.toString()));
				key.setLength(0);
				count = 0;
			}
		}
		if (negtive)
			return "-" + hexToDecimal(hex.toString());
		else
			return hexToDecimal(hex.toString());
	}

	// String toString2s() Returns the 2's complement representation of this
	// BigInt as a String using the minimum number of digits necessary (e.g. 0
	// is "0", -1 is "1", 2 is "010", -2 is "10", etc).
	public String toString2s() {
		StringBuffer sb = new StringBuffer();
		sb.append(head.data);
		Node p = head.next;
		while (p != null) {
			sb.append(p.data);
			p = p.next;
		}
		return sb.toString();

	}

	// TO BINARY
	public String toString2() throws BigIntFormatException {
		if (head.data == 0) {
			return toString2s();
		} else {
			// short code but need to create new BigInt
			return flip().toString2s();
		}

		// the following code no need to create new BigInt for negative BigInt

		// String s = toString2s();
		// char[] chars = s.toCharArray();
		// if (chars[0] == '0')
		// return s;
		// //minus one
		// if (chars[chars.length - 1] == '1')
		// chars[chars.length - 1] = '0';
		// else {
		// // boolean borrowed = false;
		// for (int i = chars.length - 1; i >= 0; i--) {
		//
		// if (chars[i] == '0')
		// chars[i] = '1';
		// else {
		// chars[i] = '0';
		// break;
		// }
		//
		// }
		// }
		// // flip 1 and 0
		// for (int i = chars.length - 1; i >= 0; i--) {
		//
		// if (chars[i] == '0')
		// chars[i] = '1';
		// else {
		// chars[i] = '0';
		// }
		//
		// }
		// return new String(chars);
	}

	// divides by 2
	private String divideBy2(String s) {
		String q = "";
		int carry = 0;
		for (int i = 0; i < s.length(); i++) {
			int d = s.charAt(i) - '0';
			q += (d + 10 * carry) / 2;
			carry = d % 2;
		}

		// remove any leading 0's (except if q is "0")
		if (q.charAt(0) == '0' && q.length() > 1) {
			int i = 1;
			while (i < q.length() && q.charAt(i) == '0') {
				i++;
			}
			q = q.substring(i);
			if (q.length() == 0) {
				q = "0";
			}
		}

		return q;
	}

	// mods 2
	private int mod2(String s) {
		int d = s.charAt(s.length() - 1) - '0';
		return d % 2;
	}

	private void removeLeading0sor1s() {
		int data = head.data;
		Node p = head.next;
		while (p != null && p.data == data) {
			head = p;
			head.prev = null;
			size--;
			p = p.next;
		}
	}

	/**
	 * set the size of nodes, add 0 or 1 before the head.
	 * 
	 * @param size
	 */
	public void fitSize(int size) {
		int gap = size - this.size;
		if (gap < 0) {
			while (size < this.size) {
				head.next.prev = null;
				head = head.next;
			}

		} else {
			Node pre;
			int com = 1;
			if (head.data == 0)
				com = 0;

			for (int i = 0; i < gap; i++) {
				pre = new Node(com);
				pre.next = this.head;
				this.head.prev = pre;
				this.head = pre;
				pre = pre.prev;
			}

		}
		this.size = size;

	}

	public int getSize() {
		return size;
	}

	// flip sign
	/**
	 * 
	 * @return new BigInt with different sign
	 * @throws BigIntFormatException
	 */
	private BigInt flip() throws BigIntFormatException {
		BigInt local = new BigInt(this);
		Node temp;
		// minus 1
		if (local.tail.data == 1) {
			if (local.tail.prev != null)
				local.tail.data = 0;
			else {
				local.tail.data = 0;
				local.tail.prev = new Node(1);
				local.head = local.tail.prev;
				local.head.next = local.tail;
			}
		} else {
			temp = local.tail;
			while (temp != null) {
				if (temp.data == 0) {
					temp.data = 1;
				} else {
					temp.data = 0;
					break;
				}
				temp = temp.prev;
			}
		}
		temp = local.head;
		// flip 1 and 0
		while (temp != null) {
			if (temp.data == 0)
				temp.data = 1;
			else
				temp.data = 0;
			temp = temp.next;
		}
		;
		return local;

	}

}