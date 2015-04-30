package test;

public class subtest {
	public subtest(){
		
	}

	public static void main(String[] args) {
		subtest subTest = new subtest();
		String input = "54321";
		String output = input.substring(0, input.length()-1);
		System.out.println(output);
	}
}
