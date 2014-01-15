/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 21/09/2010
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioTotalPeriodicosPorCNPqDAO;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosRelatorioTotalPeriodicosPorAreaCNPq;


/**
 * Bean que controla o relat�rio de total de peri�dicos por �rea do CNPq.
 *
 * @author Br�ulio (refactoring)
 */
@Component("relatorioTotalPeriodicosCNPqMBean")
@Scope("request")
public class RelatorioTotalPeriodicosPorCNPqMBean extends AbstractRelatorioBibliotecaMBean {
	
	/** JSP do relat�rio. */
	private static final String PAGINA_RELATORIO = "/biblioteca/controle_estatistico/relatorioPeriodicosCNPQAcervo.jsp";
	
	/** Os resultados parcial da primeira consulta */
	private List<Object[]> resultadosParcial;
	
	/** Os resultados retornado para o usu�rio */
	private List<DadosRelatorioTotalPeriodicosPorAreaCNPq> resultados;

	
	public RelatorioTotalPeriodicosPorCNPqMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	@Override
	public void configurar() {
		titulo = "Relat�rio de Total de Peri�dicos por �rea CNPq ";
		descricao = "<p>Este relat�rio lista a quantidade de peri�dicos no acervo, agrupados pela �reas de conhecimento do CNPq, peri�dicos nacionais e internacionias " +
		"e correntes ou n�o correntes .</p>" +
		"<p><strong>Observa��o 1:</strong> S�o recuperados apenas os fasc�culos cadatrados dentro do per�odo informado, contudo no c�lculo dos peri�dicos correntes e n�o correntes � considerado a data de registro no sistema do �ltimo fasc�culo da assinatura, independetemente se esse fasc�culo est� dentro do per�odo informado ou n�o. </p>"+
		"<p><strong>Observa��o 2:</strong> O filtro \"Tipo de Classifica��o\" determina que ser�o recuperadas as �reas do CNPq correspondentes a classifica��o escolhida. </p>";
		
		filtradoPorVariasBibliotecas = true;
		filtradoPorPeriodo = true;
		filtradoPorTipoDeTombamento = true;
		filtradoPorSituacoesDoMaterial = true;
		
		permitirTodosTiposDeTombamento = true;
		
		filtradoPorTipoClassificacao = true;
		
		campoBibliotecaObrigatorio = false;
		campoPeriodoObrigatorio = false;
		
		fimPeriodo = null;
		inicioPeriodo = null;
	}

	@Override
	public String gerarRelatorio() throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CONTROLE_ESTATISTICO);
		
		RelatorioTotalPeriodicosPorCNPqDAO dao = getDAO(RelatorioTotalPeriodicosPorCNPqDAO.class);
		super.configuraDaoRelatorio(dao); // fecha o dao depois do relat�rio automaticamente.
		
		/**
		 * Object[0] = id da assinatura
		 * Object[1] = se � internacional ou n�o
		 * Object[2] = a qtd de fasc�culos ativos no per�odo
		 * Object[3] = a �rea CNPQ
		 */
		resultadosParcial = dao.
		findPeriodicosAreaAcervoByTombamentoBiblioteca(UFRNUtils.toInteger(variasBibliotecas), 
				UFRNUtils.toInteger(situacoesMaterial), 
				inicioPeriodo, fimPeriodo, tipoDeTombamento, 
				classificacaoEscolhida);
		
		if ( resultadosParcial.isEmpty() ) { // (sempre vem pelo menos a coluna "Total")
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		
		
		List<Integer> idsAssinaturasRecuperadas = new ArrayList<Integer>();
		
		for (Object[] dados : resultadosParcial) {
			idsAssinaturasRecuperadas.add((Integer)dados[0]);
		}
		
		/**
		 * Object[0] = A quantidade de meses que o fasc�culo � considerado como "n�o corrente", pode ser 
		 *            nulo para assinatura antigas que n�o tem a periodicidade, nesta caso s�o consideradas 
		 *            como "correntes".
		 * Object[1] = a data de registro do �ltimo fasc�culo para verificar se j� exprirou
		 */
		List<Object[]> dadosFrequencia = dao.recuperaPeriodicidadeDataRegistroUltimoFasciculo(idsAssinaturasRecuperadas);
		
		
		resultados = new ArrayList<DadosRelatorioTotalPeriodicosPorAreaCNPq>();
		
		
		
		for (Object[] dados : resultadosParcial) {
			
			
			boolean corrente = false;
			boolean interNacional = (Boolean) dados[1];
			
			Object[] dadoFrequencia = recuperaDadoFrequenciAssinatura(dadosFrequencia, (Integer)dados[0]);
			
			if(dadoFrequencia != null){
				Short qtdMeses = (Short) dadoFrequencia[0];
				Date dataUltimoRegistro = (Date) dadoFrequencia[1];
				
				corrente = verificaSePeriodicoEstarCorrente(qtdMeses,dataUltimoRegistro);
				
				agrupaResultados(dados, corrente, interNacional);
			}
		}
		
		Collections.sort(resultados); // Ordena pela cole��o
		
		return forward(PAGINA_RELATORIO);
	}

	/**
	 * Recupera os dados da frequ�ncia da assinatura passada que s�o buscados no banco todos de uma 
	 * �nica vez para otimizar o n�mero de consultas ao banco.
	 *
	 * @param dadosFrequencia
	 * @param integer
	 * @return
	 */
	private Object[] recuperaDadoFrequenciAssinatura(List<Object[]> dadosFrequencia, Integer idAssinatura) {
		for (Object[] objects : dadosFrequencia) {
			if(( (Integer) objects[2]).equals(idAssinatura))
				return objects;
		}
		return null;
	}

	/**
	 * Verifica de acordo com o frequ�ncia de chegada dos fasc�culos se eles est�o correntes ou n�o
	 *
	 * @param corrente
	 * @param qtdMeses
	 * @param dataUltimoRegistro
	 * @return
	 */
	private boolean verificaSePeriodicoEstarCorrente(Short qtdMeses, Date dataUltimoRegistro) {
		if(qtdMeses != null && dataUltimoRegistro != null){  //  A assinatura tem a frequ�ncia, ent�o vamos calcular
			
			Date dataUltimoFasciculo = CalendarUtils.configuraTempoDaData(dataUltimoRegistro, 0, 0, 0, 0);
			Date hoje = CalendarUtils.configuraTempoDaData(new Date(), 0, 0, 0, 0);
			
			Date dataLimite = CalendarUtils.adicionaMeses(dataUltimoFasciculo, qtdMeses.intValue());
			
			if(dataLimite.before(hoje)){ // Corrente !!!!!
				return true;
			}
			
		}else{ // A assinatura n�o tem a frequ�ncia, ent�o � considerada corrente 
			return true;
		}
		return false;
	}

	
	/**
	 * Agrupa os resultados para o relat�rio.
	 * 
	 * @param dados
	 * @param corrente
	 * @param interNacional
	 */
	private void agrupaResultados(Object[] dados, boolean corrente, boolean interNacional) {
		
		DadosRelatorioTotalPeriodicosPorAreaCNPq resultadoTemp = new  DadosRelatorioTotalPeriodicosPorAreaCNPq( (String) dados[3] );
		
		if(resultados.contains(resultadoTemp)){
			resultadoTemp = resultados.get(resultados.indexOf(resultadoTemp));
		}else{
			resultados.add(resultadoTemp);
		}
		
		
		if(corrente && interNacional){
			resultadoTemp.adicionarQtdTitulosCorrenteInternacionais(1);  // Retorna Sempre 1 T�tulo por linha 
			resultadoTemp.adicionarQtdFasciculosCorrenteInternacionais(((BigInteger) dados[2]).intValue());
		}
		
		if(corrente && ! interNacional){
			resultadoTemp.adicionarQtdTitulosCorrenteNacionais(1);  // Retorna Sempre 1 T�tulo por linha 
			resultadoTemp.adicionarQtdFasciculosCorrenteNacionais(((BigInteger) dados[2]).intValue());
		}
		
		if(! corrente && interNacional){
			resultadoTemp.adicionarQtdTitulosNaoCorrenteInternacionais(1);  // Retorna Sempre 1 T�tulo por linha 
			resultadoTemp.adicionarQtdFasciculosNaoCorrenteInternacionais(((BigInteger) dados[2]).intValue());
		}
		
		if(! corrente && ! interNacional){
			resultadoTemp.adicionarQtdTitulosNaoCorrenteNacionais(1);  // Retorna Sempre 1 T�tulo por linha 
			resultadoTemp.adicionarQtdFasciculosNaoCorrenteNacionais(((BigInteger) dados[2]).intValue());
		}
	}
	
	
	public List<DadosRelatorioTotalPeriodicosPorAreaCNPq> getResultados() { return resultados; }


	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos1ComboBox()
	 */
	
	@Override
	public Collection<SelectItem> getAgrupamentos1ComboBox() {
		return null;
	}

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos2ComboBox()
	 */
	
	@Override
	public Collection<SelectItem> getAgrupamentos2ComboBox() {
		return null;
	}

}
