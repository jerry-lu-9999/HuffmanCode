
import java.io.*;
import java.util.*;

// Author: Linzan Ye, Jiahao Lu

public class HuffmanSubmit implements Huffman {

	// Instance variables
	static HashMap<Character, Integer> freq = null;		// Store characters and frequency for encode method
	static HashMap<Character, Integer> freqIn = null;	// Store characters and frequency for decode method
	static HashMap<String, Character> huffCode = null;	// How to traverse to the character in huffman tree
	static ArrayList<Character> freqArr;		// Store what characters are in the file for encode method
	static ArrayList<Character> freqArrDec;		// Store what characters are in the file for decode method 
	HuffTree htEnc;				// HuffTree created for encode method
	HuffTree htDec;				// HuffTree created for decode method
	HuffTrees<Character> htNode;		// HuffTrees instance for encode method
	HuffTrees<Character> htNodeDec;		// HuffTrees instance for decode method

	// Main method
	public static void main(String[] args) {
		
		HuffmanSubmit huffman = new HuffmanSubmit();		// Creating an instance of HuffmanSubmit to use inner classes
		
		// Encode & decode methods
		huffman.encode("alice30.txt", "alice30.enc", "freq.txt");
//		huffman.encode("abc.txt", "abc.enc", "freqabc.txt");
		huffman.decode("alice30.enc", "alice30dec.txt", "freq.txt");
//		huffman.decode("abc.enc", "abcdec.txt", "freqabc.txt");
		
		
		/*huffman.encode("ur.jpg", "urenc.txt", "freq.txt");
		huffman.decode("urenc.txt", "urdec.jpg", "freq.txt");*/
		
		System.out.println("\n\n----------Test----------");
		System.out.println(huffman.htEnc.code.get('l'));
		System.out.println(huffman.htEnc.code.get('e'));

		// After decoding, both ur.jpg and ur_dec.jpg should be the same.
		// On linux and mac, you can use `diff' command to check if they are the same.
	}

	public void encode(String inputFile, String outputFile, String freqFile) {
		BinaryIn in = new BinaryIn(inputFile);		// Initialize binary in
		freqArr = new ArrayList<Character>();		// Initialize freqArr each time the method is called
		scanFrequency(in);						// Updating the hashmap which stores the characters and their frequency
		createFreqFile(freq, freqFile);			// Create frequency file through the hashmap freq
		htEnc = new HuffTree();					// Initialize htEnc each time the method is called
		htEnc.readHashFreq(freqArr, freq);		// Create minheap
		// htEnc.printHeap();	// print out minheap
		htNode = htEnc.buildTree();				// Build huffman tree from the minheap
		htEnc.establishCode(htNode.root, "");	// Get huffman code for each character
		// htEnc.traverse(htNode.root);			// print out huffman tree
		createOutputFile(inputFile, outputFile, htEnc);		// Generate encrypted file
	}

	public void decode(String inputFile, String outputFile, String freqFile) {
		BinaryOut out = new BinaryOut(outputFile);		// Initialize binary in
		freqArrDec = new ArrayList<Character>();		// Initialize freqArrDec each time the method is called
		readFreqFile(freqFile);					// Get characters and their frequency from the frequency file
		htDec = new HuffTree();					// Initialize htDec each time the method is called
		htDec.readHashFreq(freqArrDec, freqIn);	// Build minheap according to the characters and frequency
		// htDec.printHeap();	// print out minheap	
		htNodeDec = htDec.buildTree();			// 
		htDec.establishCode(htNodeDec.root, "");
		putHuffCode(htDec.code);
		
		String binaryChar = "";
		BinaryIn in = new BinaryIn(inputFile);
		do {
			if (huffCode.containsKey(binaryChar)) {
				out.write(huffCode.get(binaryChar));
				System.out.print("BinChar:");
				System.out.println(binaryChar);
				System.out.print("letter: ");
				System.out.println(huffCode.get(binaryChar));
				binaryChar = "";
			} else {
				char c = in.readChar();
				binaryChar += Character.toString(c);
				System.out.println(binaryChar);
			}
		} while((!in.isEmpty()));

		// Check one more time
		if (huffCode.containsKey(binaryChar)) {
			out.write(huffCode.get(binaryChar));
		}
		out.flush();
	}
	
	public void createOutputFile(String inputFile, String output, HuffTree ht) {
		BinaryIn in = new BinaryIn(inputFile);
		BinaryOut out = new BinaryOut(output);

		while (!in.isEmpty()) {
			char c = in.readChar();
			out.write(ht.code.get(c));
		}
		out.flush();

	}

	public void scanFrequency(BinaryIn in) {
		freq = new HashMap<Character, Integer>();
		while (!in.isEmpty()) {
			char c = in.readChar();
			if (freq.containsKey(c)) {
				freq.put(c, freq.get(c) + 1);
			} else {
				freqArr.add(c);
				freq.put(c, 1);
			}
		}	
	}
	public void initializeExtendedAscii() {
		freq.put('Ç', 0);
		freq.put('ü', 0);
		freq.put('é', 0);
	}

	public void createFreqFile(HashMap<Character, Integer> a, String file) {
		BinaryOut out = new BinaryOut(file);
		for (int i = 0; i < freqArr.size(); i++) {
			System.out.println("freqarr: " + freqArr.get(i).toString());
			System.out.println("ascii: " + asciiToBinary(freqArr.get(i).toString()));
			out.write(asciiToBinary(freqArr.get(i).toString()));
			out.write(": ");
			System.out.println(freq.get(freqArr.get(i)));
			out.write(freq.get(freqArr.get(i)).toString());
			out.write(System.getProperty("line.separator"));
		}
		out.flush();
	}

	public static String asciiToBinary(String asciiString){
	       
	       byte[] bytes = asciiString.getBytes();
	       StringBuilder binary = new StringBuilder();
	       for (byte b : bytes)
	       {
	          int val = b;
	          for (int i = 0; i < 8; i++)
	          {
	             binary.append((val & 128) == 0 ? 0 : 1);
	             val <<= 1;
	          }
	         // binary.append(' ');
	       }
	       return binary.toString();
	   }
	
	public void readFreqFile(String freqFile) {
		Scanner in = null;
		try {
			File freq = new File(freqFile);
			in = new Scanner(freq);
		} catch (Exception e) {
			System.out.println("Error getting the file.");
			return;
		}
		// Initialize hashmap
		freqIn = new HashMap<Character, Integer>();
		// Initialize variable to store readLine
        String line = "";
        // Initialize array to store ASCII input from txt file
        String[] ascChar = {""};
        char charIn;
        try {
			// Continue reading the file till reaching EOF mark or the end of the file.
			while(in.hasNextLine()) {
				// Split command into an array of strings
				line = in.nextLine();
				ascChar = line.split("\\: ");
				charIn = (char) binaryDecimal(ascChar[0]);
				
				freqIn.put(charIn, Integer.parseInt(ascChar[1]));
				freqArrDec.add(charIn);
			}
			
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Error: array index out of bound");
		} catch (Exception e) {
			System.out.println("Error somewhere in method 'readFreqFile'.");
			e.printStackTrace();
		} finally {
			in.close();
		}
	}
	public void putHuffCode(HashMap<Character, String> code) {
		huffCode = new HashMap<String, Character>();
		for (int i = 0; i < freqArrDec.size(); i++) {
			char c = freqArrDec.get(i);
			huffCode.put(code.get(c), c);
		}
	}

	public static int binaryDecimal (String a) {
		int decimal = 0;
		for (int i = 0; i < a.length(); i++) {
			if (a.charAt(i) == '1') {
				decimal = (int) (decimal + Math.pow(2, (a.length() - i - 1)));
			}
		}
		return decimal;
	}
	public static int decimalBinary(int a) {
		String binary = "";
		do {
			if (a == 1) {
				binary = binary + "1";
				a = 0;
			} else if (a % 2 == 0) {
				binary = binary + "0";
				a = a / 2;
			} else if (a % 2 == 1) {
				binary = binary + "1";
				a = a / 2;
			} 
		} while (a > 0);
		String temp = ""; int t = 0;
		for (int i = 0; i < binary.length(); i++) {
			temp += binary.charAt(binary.length()-1-i);
		}
		return t = Integer.parseInt(temp);
	}
	
	class HuffTrees<E> implements Comparable<HuffTrees<E>> {

		private HuffNode<E> root;

		public HuffTrees(E el, int wt) {
			root = new HuffLeaf<E>(el, wt);
		}

		public HuffTrees(HuffNode<E> l, HuffNode<E> r, int wt) {
			root = new HuffInternalNode<E>(l, r, wt);
		}

		public int weight() {
			if (root.isLeaf()) {
				return ((HuffLeaf) root).weight;
			} else {
				return ((HuffInternalNode) root).weight;
			}
		}

		public int compareTo(HuffTrees<E> ht) {
			if (weight() < ht.weight()) {
				return -1;
			} else if (weight() > ht.weight()) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	interface HuffNode<E> {
		int weight = 0;

		public E value();

		public boolean isLeaf();
	}

	class HuffLeaf<E> implements HuffNode<E> {
		private int weight;
		private E value;

		public HuffLeaf(E el, int wt) {
			value = el;
			weight = wt;
		}

		public boolean isLeaf() {
			return true;
		}

		public int weight() {
			return weight;
		}

		public E value() {
			return value;
		}

	}

	class HuffInternalNode<E> implements HuffNode<E> {
		private int weight = 0;
		private HuffNode<E> left;
		private HuffNode<E> right;

		public HuffInternalNode(HuffNode<E> l, HuffNode<E> r, int wt) {
			left = l;
			right = r;
			weight = wt;
		}

		public boolean isLeaf() {
			return false;
		}

		public E value() {
			return null;
		}

		public int weight() {
			return weight;
		}

		public HuffNode<E> left() {
			return left;
		}

		public HuffNode<E> right() {
			return right;
		}
	}

	class MinHeap<E extends Comparable<? super E>> {
		private int n; // Number of elements
		private E[] heap;

		public MinHeap(E[] arr, int count) {
			n = count;
			heap = arr;
			heapify();
		}

		public void heapify() {
			for (int i = n / 2 - 1; i >= 0; i--) {
				siftdown(i);
			}
		}

		public void insert(E el) {
			heap[n] = el;
			siftUp(n++);
		}

		public int heapSize() {
			return n;
		}

		public int parent(int pos) {
			assert pos > 0 : "No parent";
			return (pos - 1) / 2;
		}

		public int left(int pos) {
			assert pos < n / 2 : "No child";
			return pos * 2 + 1;
		}

		public int right(int pos) {
			assert pos < (n - 1) / 2 : "No child";
			return pos * 2 + 2;
		}

		public boolean isLeaf(int pos) {
			if (pos >= n / 2 && pos < n) {
				return true;
			}
			return false;
		}

		private void siftdown(int pos) {
			assert (pos >= 0) && (pos < n) : "illegal position";
			while (!isLeaf(pos)) {
				int j = left(pos);
				if ((j < n - 1) && (heap[j].compareTo(heap[j + 1])) > 0) {
					j++;
				}
				if (heap[pos].compareTo(heap[j]) <= 0) {
					return;
				}
				swap(heap, pos, j);
				pos = j;
			}
		}

		private void siftUp(int pos) {
			assert (pos >= 0) && (pos < n) : "illegal position";
			while (pos != 0 && heap[pos].compareTo(heap[parent(pos)]) < 0) {
				swap(heap, parent(pos), pos);
				pos = parent(pos);
			}
		}

		private E extractMin() {
			assert n > 0 : "Empty heap";
			swap(heap, 0, --n);
			if (n != 0) {
				siftdown(0);
			}
			return heap[n];
		}

		private void swap(E[] arr, int a, int b) {
			E temp = arr[a];
			arr[a] = arr[b];
			arr[b] = temp;
		}
	}

	class HuffTree {
		private MinHeap<HuffTrees<Character>> mHeap;
		private HuffTrees<Character>[] treeArr;
		public HashMap<Character, String> code;
		
		public HuffTrees<Character> buildTree() {
			HuffTrees<Character> h1, h2, h3 = null;
			while (mHeap.heapSize() > 1) {
				h1 = mHeap.extractMin(); // In the later while loops, a tree with children is returned.
				h2 = mHeap.extractMin();
				h3 = new HuffTrees<Character>(h1.root, h2.root, h1.weight() + h2.weight());
				mHeap.insert(h3);
			}
			return h3;
		}
		
		public void setTreeArr(ArrayList<Character> arr) {
			treeArr = new HuffTrees[arr.size()];
		}

		public void traverse(HuffNode<Character> hn) {
			if (hn.isLeaf()) {
				System.out.print(((HuffLeaf) hn).value() + " ");
			} else {
				traverse(((HuffInternalNode) hn).left);
				traverse(((HuffInternalNode) hn).right);
			}
		}

		public void printHeap() {
			System.out.println("The elements of the array are: ");
			int temp = 1;
			for (int i = 0; i < mHeap.heapSize(); i++) {
				System.out.print(mHeap.heap[i].weight());
				if (i != temp * 2) {
					System.out.print(" ");
				}
				if (i == 0) {
					System.out.println();
				} else if (i == temp * 2) {
					System.out.println();
					temp = i + 1;
				}
			}
			System.out.println();
		}

		public void readHashFreq(ArrayList<Character> freqA, HashMap<Character, Integer> freqH) {
			setTreeArr(freqA);
			for (int i = 0; i < freqA.size(); i++) {
				char c = freqA.get(i); // Read char
				int fr = freqH.get(c);
				treeArr[i] = new HuffTrees<Character>(c, fr);
			}
			mHeap = new MinHeap<HuffTrees<Character>>(treeArr, freqA.size());
		}
		
		public void establishCode(HuffNode<Character> node, String binary) {
			initializeCode();
			getBinaryCode(node, binary);
		}
		public void initializeCode() {
			code = new HashMap<Character, String>();
		}

		public void getBinaryCode(HuffNode<Character> node, String binary) {
			if (node.isLeaf()) {
				System.out.println(((HuffLeaf<Character>) node).value() + "\t" + binary + "\t"
						+ ((HuffLeaf<Character>) node).weight);
				code.put(((HuffLeaf<Character>) node).value(), binary);
			} else {
				getBinaryCode(((HuffInternalNode<Character>) node).left, binary + "0");
				getBinaryCode(((HuffInternalNode<Character>) node).right, binary + "1");
			}

		}
	}
}
