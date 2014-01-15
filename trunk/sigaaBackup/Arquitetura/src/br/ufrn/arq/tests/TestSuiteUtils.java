/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 21/06/2010
 */
package br.ufrn.arq.tests;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Classe para funções auxiliares dos testes 
 *
 * @author Mayron Cachina
 * @author Gleydson Lima
 *
 */
public class TestSuiteUtils {

	/**
	 * Tempor de duração de reload de página 
	 */
	public static final String WAITELOAD = "3000";
	
	/**
	 * Cria um arquivo com o nome específico
	 * @param nome
	 * @return
	 * @throws IOException
	 */
	public static String criarArquivo(String nome) throws IOException {
		File f = new File("tests/br/ufrn/admin/jsf/tests/files/" + nome);
		
		FileWriter fw = new FileWriter(f);
		fw.write("teste");
		fw.close();
		return f.getAbsolutePath();
	}
	
	
	/**
	 * Gera palavras com um determinado tamanho
	 * @param tamanho
	 * @return
	 */
	public static String palavras(int tamanho){
	    char c;
        char[] chars = new char[52]; //52, pois o alfabeto tem 26 letras. 26x2= 52 (maiúsculas e minúsculas)
        int i = 0;      
        int size = tamanho; 
        String pass = "";
        
        for(c = 'a'; c <= 'z'; c++) {
            chars[i] = c;
            i++;
        }
        
        for(c = 'A'; c <= 'Z'; c++) {
            chars[i] = c;
            i++;
        }
        
        Random rand = new Random();
        
        int pos;
        for(pos = 0; pos < size; pos++) {
            if(rand.nextBoolean()) {
                pass += rand.nextInt(9);
            } else {
                pass += chars[rand.nextInt(51)];
            }
        }
        
        return pass;
	}
	
	/**
	 * Gera sequência de números com um determinado tamanho
	 * @param tamanho
	 * @return
	 */
	public static int numero(int tamanho){
		Random rand = new Random();

		// No. 1 Gerando Inteiros aleatorios
		int randnum = rand.nextInt();
		// Mais inteiros podem ser gerados executando  
		// iterativamente a linha de código acima...

		// No. 2 Gerando inteiros aleatoriamente de 0 a 10
		int n = 10000000;
		randnum = rand.nextInt(n+1);

		return randnum;
	}
	
	
	public static String dataAleatoria(){
		int mes = 1+(int)( 12*Math.random() );
		int dia = 1+(int)( 30*Math.random() );
		String mesString;
		if(mes < 10)
				mesString = "0" + String.valueOf(mes);
		else
				mesString = String.valueOf(mes);
		return String.valueOf(dia) + "/" + mesString + "/2010";
	}
	
	public static void main(String[] args) throws IOException {
//		System.out.println(criarArquivo("teste.jpg"));
//		System.out.println(palavras(8));
//		System.out.println(numero(10));
		
		System.out.println();
	}
}
