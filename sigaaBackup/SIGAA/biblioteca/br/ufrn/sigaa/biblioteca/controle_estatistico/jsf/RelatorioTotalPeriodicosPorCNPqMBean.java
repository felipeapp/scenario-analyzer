/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Bean que controla o relatório de total de periódicos por área do CNPq.
 *
 * @author Bráulio (refactoring)
 */
@Component("relatorioTotalPeriodicosCNPqMBean")
@Scope("request")
public class RelatorioTotalPeriodicosPorCNPqMBean extends AbstractRelatorioBibliotecaMBean {
	
	/** JSP do relatório. */
	private static final String PAGINA_RELATORIO = "/biblioteca/controle_estatistico/relatorioPeriodicosCNPQAcervo.jsp";
	
	/** Os resultados parcial da primeira consulta */
	private List<Object[]> resultadosParcial;
	
	/** Os resultados retornado para o usuário */
	private List<DadosRelatorioTotalPeriodicosPorAreaCNPq> resultados;

	
	public RelatorioTotalPeriodicosPorCNPqMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	@Override
	public void configurar() {
		titulo = "Relatório de Total de Periódicos por Área CNPq ";
		descricao = "<p>Este relatório lista a quantidade de periódicos no acervo, agrupados pela áreas de conhecimento do CNPq, periódicos nacionais e internacionias " +
		"e correntes ou não correntes .</p>" +
		"<p><strong>Observação 1:</strong> São recuperados apenas os fascículos cadatrados dentro do período informado, contudo no cálculo dos periódicos correntes e não correntes é considerado a data de registro no sistema do último fascículo da assinatura, independetemente se esse fascículo está dentro do período informado ou não. </p>"+
		"<p><strong>Observação 2:</strong> O filtro \"Tipo de Classificação\" determina que serão recuperadas as áreas do CNPq correspondentes a classificação escolhida. </p>";
		
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
		super.configuraDaoRelatorio(dao); // fecha o dao depois do relatório automaticamente.
		
		/**
		 * Object[0] = id da assinatura
		 * Object[1] = se é internacional ou não
		 * Object[2] = a qtd de fascículos ativos no período
		 * Object[3] = a área CNPQ
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
		 * Object[0] = A quantidade de meses que o fascículo é considerado como "não corrente", pode ser 
		 *            nulo para assinatura antigas que não tem a periodicidade, nesta caso são consideradas 
		 *            como "correntes".
		 * Object[1] = a data de registro do último fascículo para verificar se já exprirou
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
		
		Collections.sort(resultados); // Ordena pela coleção
		
		return forward(PAGINA_RELATORIO);
	}

	/**
	 * Recupera os dados da frequência da assinatura passada que são buscados no banco todos de uma 
	 * única vez para otimizar o número de consultas ao banco.
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
	 * Verifica de acordo com o frequência de chegada dos fascículos se eles estão correntes ou não
	 *
	 * @param corrente
	 * @param qtdMeses
	 * @param dataUltimoRegistro
	 * @return
	 */
	private boolean verificaSePeriodicoEstarCorrente(Short qtdMeses, Date dataUltimoRegistro) {
		if(qtdMeses != null && dataUltimoRegistro != null){  //  A assinatura tem a frequência, então vamos calcular
			
			Date dataUltimoFasciculo = CalendarUtils.configuraTempoDaData(dataUltimoRegistro, 0, 0, 0, 0);
			Date hoje = CalendarUtils.configuraTempoDaData(new Date(), 0, 0, 0, 0);
			
			Date dataLimite = CalendarUtils.adicionaMeses(dataUltimoFasciculo, qtdMeses.intValue());
			
			if(dataLimite.before(hoje)){ // Corrente !!!!!
				return true;
			}
			
		}else{ // A assinatura não tem a frequência, então é considerada corrente 
			return true;
		}
		return false;
	}

	
	/**
	 * Agrupa os resultados para o relatório.
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
			resultadoTemp.adicionarQtdTitulosCorrenteInternacionais(1);  // Retorna Sempre 1 Título por linha 
			resultadoTemp.adicionarQtdFasciculosCorrenteInternacionais(((BigInteger) dados[2]).intValue());
		}
		
		if(corrente && ! interNacional){
			resultadoTemp.adicionarQtdTitulosCorrenteNacionais(1);  // Retorna Sempre 1 Título por linha 
			resultadoTemp.adicionarQtdFasciculosCorrenteNacionais(((BigInteger) dados[2]).intValue());
		}
		
		if(! corrente && interNacional){
			resultadoTemp.adicionarQtdTitulosNaoCorrenteInternacionais(1);  // Retorna Sempre 1 Título por linha 
			resultadoTemp.adicionarQtdFasciculosNaoCorrenteInternacionais(((BigInteger) dados[2]).intValue());
		}
		
		if(! corrente && ! interNacional){
			resultadoTemp.adicionarQtdTitulosNaoCorrenteNacionais(1);  // Retorna Sempre 1 Título por linha 
			resultadoTemp.adicionarQtdFasciculosNaoCorrenteNacionais(((BigInteger) dados[2]).intValue());
		}
	}
	
	
	public List<DadosRelatorioTotalPeriodicosPorAreaCNPq> getResultados() { return resultados; }


	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos1ComboBox()
	 */
	
	@Override
	public Collection<SelectItem> getAgrupamentos1ComboBox() {
		return null;
	}

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos2ComboBox()
	 */
	
	@Override
	public Collection<SelectItem> getAgrupamentos2ComboBox() {
		return null;
	}

}
