/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 26/03/2009
 */
package br.ufrn.services.populabd;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;

import br.ufrn.arq.migracao.MigraTabela;

/**
 * Classe para migração de banco de dados.
 * 
 * @author Gleydson Lima
 *
 */
public class SincronizaTabelas {

	static ResourceBundle config = ResourceBundle
			.getBundle("br.ufrn.services.populabd.bancos");

	static ArrayList<String> tabelasSIGAAOrigem = new ArrayList<String>();
	static ArrayList<String> tabelasADMOrigem = new ArrayList<String>();
	static ArrayList<String> tabelasCOMUMOrigem = new ArrayList<String>();
	
	static ArrayList<String> tabelasSIGAADestino = new ArrayList<String>();
	static ArrayList<String> tabelasADMDestino = new ArrayList<String>();
	static ArrayList<String> tabelasCOMUMDestino = new ArrayList<String>();
	
	static String dirBase = System.getProperty("user.dir")+"|services|br|ufrn|services|populabd|".replace("|", System.getProperty("file.separator"));

	static Scanner scan = new Scanner(System.in);
	
	public static void main(String[] args) throws Exception {

		System.out
				.println("=================================================================");
		System.out.println("Sincroniza Tabelas - Sistemas UFRN");
		System.out
				.println("=================================================================");

		
		String comando = null;
		if (!(args.length == 3)){
		do {
				System.out.println("Comandos:");
				System.out.println("\tMigraComum");
				System.out.println("\tMigraSIGAA");
				System.out.println("\tMigraADM");
				System.out.println("\tSAIR");
	
				System.out.print("> ");
	
				comando = scan.next();
	
				if (comando.equals("MigraComum")) {
					migraBanco("comum");
				} else if (comando.equals("MigraSIGAA")) {
					migraBanco("sigaa");
				} else if (comando.equals("MigraADM")) {
					migraBanco("administrativo");
				} else if (comando.equals("SAIR")) {
				} else {
					System.out.println("Comando inválido.");
				}
	
			} while (comando.equals("SAIR"));
		}else{
			migraBanco(args[0],args[1],args[2]);
		}

	}

	public static ArrayList<String> carregaTabelas(String banco, String extensaoArq) throws IOException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(dirBase + banco + extensaoArq)));
		String tabela = null;

		ArrayList<String> result = new ArrayList<String>();

		while ((tabela = reader.readLine()) != null) {
			result.add(tabela);
		}

		return result;

	}
	
	public static void migraBanco(String banco) throws Exception {
		System.out.print("Digite senha origem: ");
		String senhaOrigem = scan.next();

		System.out.print("Digite senha destino: ");
		String senhaDestino = scan.next();
		
		migraBanco(banco, senhaOrigem, senhaDestino);
	}
	
	public static void migraBanco(String banco, String senhaOrigem, String senhaDestino) throws Exception {
		
		
		String urlOrigem = config.getString("url_" + banco + "_origem");
		String urlDestino = config.getString("url_" + banco + "_destino");
		
		String userOrigem = config.getString("user_" + banco + "_origem");
		String userDestino = config.getString("user_" + banco + "_destino");
		
		System.out.println("Origem: " + urlOrigem + " user: " + userOrigem);
		System.out.println("Destino: " + urlDestino + " user: " + userDestino);
	
		if ( senhaOrigem.equals("-")) {
			senhaOrigem = userOrigem;
		}
		
		if ( senhaDestino.equals("-")) {
			senhaDestino = userDestino;
		}
		
		Class.forName("org.postgresql.Driver");
		Connection conOrigem = DriverManager.getConnection(urlOrigem, userOrigem, senhaOrigem);
		System.out.println("Origem -> OK");
		Connection conDestino = DriverManager.getConnection(urlDestino, userDestino, senhaDestino);
		System.out.println("Destino -> OK");
		
		if ( banco.equals("comum") ) {
			tabelasCOMUMOrigem = carregaTabelas("comum", ".origem.tabelas");
			tabelasCOMUMDestino = carregaTabelas("comum", ".destino.tabelas");
								
			if(tabelasCOMUMOrigem.size()  != tabelasCOMUMDestino.size())
				throw new Exception("Os arquivos \"comum.origem.tabelas\" e \"comum.destino.tabelas\" possuem número de tabelas diferentes, " +tabelasCOMUMOrigem.size() + " e " + tabelasCOMUMDestino.size() + " respectivamente. Devem ser iguais!");
			else
			{
				infoOrigemDestino(banco,tabelasCOMUMOrigem,tabelasCOMUMDestino);
				migraTabelas(conOrigem, conDestino, tabelasCOMUMOrigem, tabelasCOMUMDestino);
			}
		}
		
		if ( banco.equals("sigaa") ) {
			tabelasSIGAAOrigem = carregaTabelas("sigaa", ".origem.tabelas");
			tabelasSIGAADestino = carregaTabelas("sigaa", ".destino.tabelas");
						
			if(tabelasSIGAADestino.size() != tabelasSIGAAOrigem.size())
				throw new Exception("Os arquivos \"sigaa.origem.tabelas\" e \"sigaa.destino.tabelas\" possuem número de tabelas diferentes, " +tabelasSIGAADestino.size() + " e " + tabelasSIGAAOrigem.size() + " respectivamente. Devem ser iguais!");
			else
			{
				infoOrigemDestino(banco,tabelasSIGAAOrigem,tabelasSIGAADestino);
				migraTabelas(conOrigem, conDestino, tabelasSIGAAOrigem, tabelasSIGAADestino);
			}
				
		}
		
		if ( banco.equals("administrativo") ) {
			tabelasADMOrigem = carregaTabelas("administrativo", ".origem.tabelas");
			tabelasADMDestino = carregaTabelas("administrativo", ".destino.tabelas");
									
			if(tabelasADMDestino.size() != tabelasADMOrigem.size())
				throw new Exception("Os arquivos \"administrativo.origem.tabelas\" e \"administrativo.destino.tabelas\" possuem número de tabelas diferentes, " +tabelasADMOrigem.size() + " e " + tabelasADMDestino.size() + " respectivamente. Devem ser iguais!");
			else
			{
				infoOrigemDestino(banco,tabelasADMOrigem,tabelasADMDestino);
				migraTabelas(conOrigem, conDestino, tabelasADMOrigem, tabelasADMDestino);
			}
			
		}
		
		
	}
	
	static void infoOrigemDestino(String banco, ArrayList<String> tabelasOrigem, ArrayList<String> tabelasDestino)
	{
		System.out.println("\n-----------Fluxo da Importação----------------\n");
		for ( int i=0; i < tabelasOrigem.size(); i++ ) 
			System.out.println(tabelasOrigem.get(i) + " => " +tabelasDestino.get(i));
		System.out.println("\n----------------------------------------------\n");
	}
	
	static void migraTabelas(Connection origem, Connection destino, ArrayList<String> tabelasOrigem, ArrayList<String> tabelasDestino) throws Exception {
		
		for ( int i=0; i < tabelasOrigem.size(); i++ ) 
			migraTabela(origem, destino, tabelasOrigem.get(i), tabelasDestino.get(i));
		
	}
	
	
	public static void migraTabela(Connection origem, Connection destino, String tabelaOrigem, String tabelaDestino) throws Exception {
		
		MigraTabela migracao = new MigraTabela();
		migracao.setTabelaOrigem(tabelaOrigem);
		migracao.setTabelaDestino(tabelaDestino);
		migracao.setColunasGeradasAutomaticamente(true);
		
		if ( migracao.getCamposOrigem().contains("ativo")) {
			migracao.setWhereRestrict("ativo = trueValue()");
		}
		
		if ( migracao.getCamposOrigem().contains("inativo")) {
			migracao.setWhereRestrict("inativo = falseValue()");
		}
		
		migracao.migrarDados(origem, destino);
		
	}



}
