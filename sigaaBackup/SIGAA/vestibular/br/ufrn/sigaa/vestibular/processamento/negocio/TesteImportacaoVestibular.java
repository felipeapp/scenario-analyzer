/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 08/12/2010
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.vestibular.processamento.negocio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.ufrn.sigaa.vestibular.dominio.Inscrito;
import br.ufrn.sigaa.vestibular.dominio.Opcao;
import br.ufrn.sigaa.vestibular.dominio.Prova;
import br.ufrn.sigaa.vestibular.dominio.Vestibular;

import com.thoughtworks.xstream.XStream;

/**
 * @author Rafael Gomes
 *
 */
public class TesteImportacaoVestibular {

	public static void main(String[] args) throws ClassNotFoundException, SQLException, FileNotFoundException {
		criarXml();
		//lerXml();
	}
	
	public void persistirCandidatos(){
		
	}
	
	public static void lerXml() throws FileNotFoundException{
		
		XStream xstream = new XStream();
		
		File arquivo = new File("R:/SINFO/Desenvolvimento/vestibular/vestibular20111.xml");
		Reader leitor = null;
		FileInputStream arquivoXML = new FileInputStream(arquivo);
		leitor = new InputStreamReader(arquivoXML);
		
		xstream.alias("vestibular", Vestibular.class);
		xstream.alias("inscrito", Inscrito.class);
		xstream.alias("opcao", Opcao.class);
		xstream.alias("prova", Prova.class);
		
		Vestibular vestibular = (Vestibular) xstream.fromXML(leitor);
		
		for (Inscrito i : vestibular.getContent()) {
			System.out.println("\nNúmero	: " + i.getNumeroInscricao());
			System.out.println("Situação: " + i.getSituacao());
			System.out.println("Opção de Aprovação: " + i.getOpcaoAprovacao());
			System.out.println("Semestre de Aprovação: " + i.getSemestreAprovacao());
			
			for (Opcao o : i.getOpcoes()) {
				System.out.println("\nOrdem Opção	: " + o.getOrdemOpcao());
				System.out.println("Classificação	: " + o.getClassificacao());
				System.out.println("Argumento Final	: " + o.getArgumentoFinal());
				System.out.println("Arg. Final sem Benefício: " + o.getArgumentoFinalSemBeneficio());
			}
			
			for (Prova prova : i.getProvas()) {
				System.out.println("\nProva	: " + prova.getDescricaoProva());
				System.out.println("Argumento: " + prova.getArgumento());
			}
		}
		
	}
	
	public static void criarXml() throws ClassNotFoundException, SQLException {
		Vestibular vestibular = new Vestibular();
		Inscrito inscrito =  new Inscrito();
		Opcao opcao = new Opcao();
		
		
		List<Inscrito> lista;
		try {
			lista = (List<Inscrito>) findInscritosVestibular();
		
		
			for ( Inscrito i : lista ) {
				inscrito.setNumeroInscricao(i.getNumeroInscricao());
				inscrito.setOpcaoAprovacao((int) (Math.random() * 2));
				inscrito.setSemestreAprovacao(1 + (int) (Math.random() * 2));
				inscrito.setSituacao('R');
				
				for (Opcao o : i.getOpcoes()) {
					opcao.setOrdemOpcao(o.getOrdemOpcao());
					opcao.setClassificacao(1 + (int) (Math.random() * 50));
					opcao.setArgumentoFinal(500 + (int) (Math.random() * 900));
					opcao.setArgumentoFinalSemBeneficio(500 + (int) (Math.random() * 760));
					opcao.setMatrizCurricular(o.getMatrizCurricular());
					inscrito.addOpcao(opcao);
					opcao = new Opcao();
				}
				
				Prova prova = new Prova();
				prova.setDescricaoProva("PORTUGUÊS");
				prova.setArgumento(500 + (int) (Math.random() * 450));
				prova.setFase(1);
				inscrito.addProvas(prova);
				
				prova = new Prova();
				prova.setDescricaoProva("MATEMÁTICA");
				prova.setArgumento(500 + (int) (Math.random() * 450));
				prova.setFase(1);
				inscrito.addProvas(prova);
				
				prova = new Prova();
				prova.setDescricaoProva("GEOGRAFIA");
				prova.setFase(1);
				prova.setArgumento(500 + (int) (Math.random() * 450));
				inscrito.addProvas(prova);
				
				prova = new Prova();
				prova.setDescricaoProva("QUÍMICA");
				prova.setArgumento(500 + (int) (Math.random() * 450));
				prova.setFase(1);
				inscrito.addProvas(prova);
				
				prova = new Prova();
				prova.setDescricaoProva("BIOLOGIA");
				prova.setArgumento(500 + (int) (Math.random() * 450));
				prova.setFase(1);
				inscrito.addProvas(prova);
				
				prova = new Prova();
				prova.setDescricaoProva("INGLÊS");
				prova.setArgumento(500 + (int) (Math.random() * 450));
				prova.setFase(1);
				inscrito.addProvas(prova);
				
				prova = new Prova();
				prova.setDescricaoProva("HISTÓRIA");
				prova.setArgumento(500 + (int) (Math.random() * 450));
				prova.setFase(1);
				inscrito.addProvas(prova);
				
				prova = new Prova();
				prova.setDescricaoProva("FÍSICA");
				prova.setArgumento(500 + (int) (Math.random() * 450));
				prova.setFase(1);
				inscrito.addProvas(prova);
				
				prova = new Prova();
				prova.setDescricaoProva("PORTUGUÊS DISCURSIVA");
				prova.setArgumento(500 + (int) (Math.random() * 450));
				prova.setFase(2);
				inscrito.addProvas(prova);
				
				prova = new Prova();
				prova.setDescricaoProva("INGLÊS DISCURSIVA");
				prova.setArgumento(500 + (int) (Math.random() * 450));
				prova.setFase(2);
				inscrito.addProvas(prova);
				
				prova = new Prova();
				prova.setDescricaoProva("HISTÓRIA DISCURSIVA");
				prova.setArgumento(500 + (int) (Math.random() * 450));
				prova.setFase(2);
				inscrito.addProvas(prova);
				
				prova = new Prova();
				prova.setDescricaoProva("GEOGRAFIA DISCURSIVA");
				prova.setArgumento(500 + (int) (Math.random() * 450));
				prova.setFase(2);
				inscrito.addProvas(prova);
				
				vestibular.add(inscrito);
				inscrito = new Inscrito();
			}
		} finally {
			
		}
		
		XStream xstream = new XStream();
		xstream.alias("vestibular", Vestibular.class);
		xstream.alias("inscrito", Inscrito.class);
		xstream.alias("opcao", Opcao.class);
		xstream.alias("prova", Prova.class);
		String xml = xstream.toXML(vestibular);
 
		System.out.println("XML Criado com Sucesso");
		
		FileWriter writer;
		try {
			writer = new FileWriter("R:/SINFO/Desenvolvimento/vestibular/vestibular20111.xml");
			PrintWriter saida = new PrintWriter(writer); 
			saida.println("<?xml version='1.0' encoding='ISO-8859-1'?>");  
			saida.println(xml);
			saida.close();  
			writer.close(); 
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	}

	
	public static Collection<Inscrito> findInscritosVestibular() throws ClassNotFoundException, SQLException{ 
		Class.forName("org.postgresql.Driver");
		
		Connection con = null;
		Collection<Inscrito> inscricoes = new ArrayList<Inscrito>();
		Inscrito i = new Inscrito();
		try {
			con = DriverManager.getConnection("jdbc:postgresql://bddesenv.info.ufrn.br:5432/sigaa_desenv_20100909", "sigaa", "sigaa");
			con.setAutoCommit(false);
			
			String sql = " SELECT i.numero_inscricao, o.ordem, o.id_matriz_curricular FROM vestibular.inscricao_vestibular i" +
					" inner join vestibular.opcao_candidato o using(id_inscricao_vestibular) " +
					" where not exists ( select id_inscricao_vestibular from vestibular.resultado_classificacao_candidato where id_inscricao_vestibular = i.id_inscricao_vestibular )" +
					" order by 1 desc, 2 " +
					" limit 20";
			
			Statement st = con.createStatement();
			ResultSet resul = st.executeQuery(sql);
			int nAnterior = 0;
			int nOpcao = 0;
			while(resul.next()){
				if(nAnterior == 0) nAnterior = resul.getInt("numero_inscricao");
				if (resul.getInt("numero_inscricao") != nAnterior && resul.getRow() != 1){
					inscricoes.add(i);
					i = new Inscrito();
					nOpcao = 0;
					nAnterior = resul.getInt("numero_inscricao");
				}
				i.setNumeroInscricao(resul.getInt("numero_inscricao"));
				Opcao opcao = new Opcao();
				opcao.setOrdemOpcao(resul.getInt("ordem"));
				opcao.setMatrizCurricular(resul.getInt("id_matriz_curricular"));
				nOpcao++;
				i.addOpcao(opcao);
				
			}
			//cadastra o último registro
			inscricoes.add(i);
			i = new Inscrito();
			nOpcao = 0;

		} finally {
		}
		return inscricoes;
	}		
}
