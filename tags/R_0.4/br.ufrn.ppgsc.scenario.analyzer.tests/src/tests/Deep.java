package tests;

import javax.swing.JOptionPane;

import org.junit.Test;

public class Deep {
	
	public static long itr = 0;
	
	@Test
	public static void main(String[] args) {

		long largura = Long.parseLong(JOptionPane.showInputDialog("Digite a largura: "));
		long profundidade = Long.parseLong(JOptionPane.showInputDialog("Digite a profundidade: "));
		
		System.out.println("Antes...");
		Deep.test(largura, profundidade);
		System.out.println("Depois...");

	}

	public static void test(long largura, long profundidade) {
		if (profundidade > 0) {
			for (int i = 0; i < largura; i++) {
				if (itr++ % 1000000 == 0) System.out.println(itr);
				test(largura, profundidade - 1);
			}
		}
	}

}
