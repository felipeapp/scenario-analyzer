/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 20/12/2010 
 *
 */ 

package br.ufrn.sigaa.processamento.batch;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.vestibular.InscricaoVestibularDao;
import br.ufrn.sigaa.vestibular.dominio.InscricaoVestibular;
import br.ufrn.sigaa.vestibular.dominio.Inscrito;
import br.ufrn.sigaa.vestibular.dominio.Opcao;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;
import br.ufrn.sigaa.vestibular.dominio.Prova;
import br.ufrn.sigaa.vestibular.dominio.Vestibular;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Classe que armazena a lista de candidatos inscritos no vestibular 
 * a serem processadas e importados para a base de dados do Sistema.
 * 
 * @author Rafael G. Rodrigues
 *
 */
public class ListaInscritosVestibular {

	/** array de Inscritos */
	private static Stack<Inscrito> inscritos = new Stack<Inscrito>();

	/** processamento sendo realizado */
	private static boolean bloqueada;

	private static int totalInscritos = 0;

	private static int totalProcessados = 0;
	
	private static Exception erro;

	public static synchronized boolean possuiInscritos() {
		return inscritos.size() > 0;
	}

	/** entrega o próximo inscrito para ser processada */
	public static synchronized Inscrito getProximoInscrito() {
		Inscrito inscrito = inscritos.pop();
		bloqueada = !inscritos.isEmpty();
		return inscrito;
	}

	public static synchronized void registraProcessada() {
		totalProcessados++;
	}

	/**
	 * Carrega os inscritos do vestibular a partir do arquivo xml de importação dos dados do vestibular.
	 * @param ano
	 * @param periodo
	 * @param rematricula 
	 * @param nivel 
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	public static synchronized void carregarInscritos(ProcessoSeletivoVestibular processoSeletivo, File xml) throws DAOException, ArqException, NegocioException {
		if (bloqueada)
			throw new NegocioException(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
		bloqueada = true;
		InscricaoVestibularDao dao = new InscricaoVestibularDao();
		Vestibular vestibular = new Vestibular();
		
		try {
			inscritos.clear();
			vestibular = lerXml(xml);
			if (isEmpty(vestibular) || isEmpty(vestibular.getContent()))
				throw new NegocioException("Não foi possível interpretar os dados do arquivo XML. Por favor, verifique se o formato do arquivo está correto.");
			List<Inscrito> inscritosBuscados = vestibular.getContent();
			
			List<Integer> listaNumInscricao = new ArrayList<Integer>();
			List<Integer> listaNumInscricao2 = new ArrayList<Integer>();
			
			// Inserindo em um lista todos os números de inscrição importados pelo XML.
			// Para ser utilizado na consulta em banco para população das inscrições de vestibular.
			for (Inscrito inscrito : inscritosBuscados) {
				listaNumInscricao.add(inscrito.getNumeroInscricao());
				listaNumInscricao2.add(inscrito.getNumeroInscricao());
			}
			
			/* Armazena a lista de números de inscrição que já possua ResultadoClassificacaoCandidato cadastrado,
			 * para evitar duplicidade. */
			Collection<Integer> listNumInscricaoCadastrados = dao.findByListaInscricaoCadastradas(listaNumInscricao, processoSeletivo);
			
			/* Armazena a lista de inscrições de vestibular para ser vinculado ao cadastro de um novo 
			 * resultado de vestibular, sem ter a necessidade de consultar ao banco de maneira excessiva.*/
			Collection<InscricaoVestibular> listInscricaoVestibular = dao.findByListaInscricao(listaNumInscricao2, processoSeletivo);
					
			//Inserção as inscrições de vestibular para cada candidato importado através do xml.
			for (Inscrito inscrito : inscritosBuscados) {
				if ( !ValidatorUtil.isEmpty(inscrito.getNumeroInscricao()) ){
					inscrito.setInscricaoVestibular(populaInscricaoVestibular(inscrito.getNumeroInscricao(), listInscricaoVestibular));
					inscrito.setExisteResultadoClassificacao(listNumInscricaoCadastrados.contains(inscrito.getNumeroInscricao()));
				}	
			}
			
			for ( Inscrito t : inscritosBuscados )
				inscritos.add(t);

			totalInscritos = inscritos.size();
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Método responsável por realizar a leitura do arquivo xml, 
	 * assim como, popular e retornar o objeto Vestibular com 
	 * todo o resultado do processo seletivo.
	 * 
	 * @param xml
	 * @return
	 * @throws FileNotFoundException
	 */
	private static Vestibular lerXml(File xml) {
		Vestibular vestibular = null;
		try {
			XStream xstream = new XStream(new DomDriver());
			Reader leitor = null;
			FileInputStream arquivoXML = new FileInputStream(xml);
			leitor = new InputStreamReader(arquivoXML);
			
			xstream.alias("vestibular", Vestibular.class);
			xstream.alias("inscrito", Inscrito.class);
			xstream.alias("opcao", Opcao.class);
			xstream.alias("prova", Prova.class);
			vestibular = (Vestibular) xstream.fromXML(leitor);
		} catch (Exception e) {
		}
		return vestibular;
	}	
	
	/**
	 * Método auxiliar para retornar o objeto de inscrição do 
	 * vestibular referente ao número de inscrição informado.
	 * @param numeroInscricao
	 * @param listInscricaoVestibular
	 * @return
	 */
	private static InscricaoVestibular populaInscricaoVestibular(int numeroInscricao, Collection<InscricaoVestibular> listInscricaoVestibular ){
		if (ValidatorUtil.isEmpty(listInscricaoVestibular)) return null;
		for (InscricaoVestibular inscricaoVestibular : listInscricaoVestibular) {
			if ( inscricaoVestibular.getNumeroInscricao() == numeroInscricao )
				return inscricaoVestibular;
		}
		return null;
	}

	public static int getTotalInscritos() {
		return totalInscritos;
	}

	public static int getTotalProcessados() {
		return totalProcessados;
	}

	public static void reset() {
		erro = null;
		bloqueada = false;
		inscritos = new Stack<Inscrito>();
		totalInscritos = 0;
		totalProcessados = 0;
	}

	public static Exception getErro() {
		return erro;
	}

	public static void setErro(Exception erro) {
		ListaInscritosVestibular.erro = erro;
	}

	public static boolean isBloqueada() {
		return bloqueada;
	}
}
