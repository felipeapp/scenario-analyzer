package br.ufrn.sigaa.biblioteca.testes;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.marc.Record;


/**
 *    Classe de testes que lê os dados de um arquivo no padrão MARC e os imprime no console. 
 *
 * @author jadson
 * @since 15/09/2009
 * @version 1.0 criacao da classe
 *
 */
public class LerArquivoFormatoMARC {

	
	public static void main(String[] args) throws IOException {
		
		///////////  Coloque o caminho para o arquivo aqui ///////////////
		File arquivo = new File("/home/sinfo/Desktop/RN_TESTE_COOPERACAO.CC");
		
		FileInputStream fis = new FileInputStream(arquivo);
		
		
		BufferedReader r = new BufferedReader(new InputStreamReader(fis));
		
		String texto = "";
		
		String linha = null;
		while ((linha  = r.readLine()) != null)
			texto += linha;
		
		MarcReader reader  = new MarcStreamReader(new ByteArrayInputStream(texto.getBytes()));
		
		Record record = null;
		
		int contador = 0;
		
		while(reader.hasNext()){
			
			System.out.println(" \n\n\n----------------------  Registro: "+ ++contador+"  ---------------------");
			
			record = reader.next();
			System.out.println(record.toString());
			
		}
		
	}
	
}
