/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '15/04/2009'
 *
 */
package br.ufrn.sigaa.prodocente.relatorios.jsf;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.pesquisa.MembroGrupoPesquisaDao;
import br.ufrn.sigaa.arq.dao.prodocente.ProducaoDao;
import br.ufrn.sigaa.pesquisa.dominio.GrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.MembroGrupoPesquisa;
import br.ufrn.sigaa.prodocente.producao.jsf.AbstractControllerProdocente;
import br.ufrn.sigaa.prodocente.relatorios.dominio.LinhaRelatorioGrupoPesquisa;
import br.ufrn.sigaa.prodocente.relatorios.dominio.RelatorioAvaliacaoGrupoPesquisa;

/**
 * MBean respons�vel por gerar relat�rio para avalia��o dos grupos de pesquisa
 * 
 * @author Leonardo Campos
 *
 */

@Component @Scope("request")
public class RelatorioAvaliacaoGrupoPesquisaMBean extends
		AbstractControllerProdocente<RelatorioAvaliacaoGrupoPesquisa> {

	/** Constante de visualiza��o do resultado */
	private final String JSP_RESULTADO = "/prodocente/producao/relatorios/avaliacao_grupo_pesquisa/resultado.jsp";
	/** Constante de visualiza��o do relat�rio */
	private final String JSP_RELATORIO = "/prodocente/producao/relatorios/avaliacao_grupo_pesquisa/relatorio.jsp";
	/** Vari�veis temporal para a gera��o dos relat�rios. */
	private Integer anoInicial, anoFinal, mesInicial, mesFinal;
	/** Grupo de Pesquisa utilizado na gera��o do relat�rio */
	private GrupoPesquisa grupo;
	/** Colle��o dos grupos de pesquisa. */
	Collection<MembroGrupoPesquisa> membroGrupoPesquisa = new ArrayList<MembroGrupoPesquisa>();
	
	/** Construtor padr�o  */
	public RelatorioAvaliacaoGrupoPesquisaMBean() {
		obj = new RelatorioAvaliacaoGrupoPesquisa();
		grupo = new GrupoPesquisa();
	}
	
	/**
	 * Inicia a opera��o, populando os dados e redirecionando para o formul�rio. 
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>/WEB-INF/jsp/pesquisa/menu/prodocente.jsp</li>
	 *	</ul>
	 * @return
	 */
	public String iniciar() {
		anoFinal = CalendarUtils.getAnoAtual();
		anoInicial = anoFinal -1;
		return forward(getParameter("relatorio"));
	}
	
	/**
	 * Gera o relat�rio com todos os grupos, considerando apenas a data inicial e final.
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/producao/relatorios/avaliacao_grupo_pesquisa/form-geral.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioTodosGrupos() throws DAOException {

		if (getAnoInicial() > getAnoFinal() || getMesInicial() > getMesFinal()) {
			addMensagem(MensagensArquitetura.DATA_INICIO_MENOR_FIM, "Per�odo a considerar");
			return null;
		}

		ProducaoDao dao = getDAO(ProducaoDao.class);
		Map<String, Integer> docentes = dao.findByDocentesPermanentes(0);
		if(docentes == null || docentes.isEmpty()){
			addMensagemErro("N�o h� docentes permanentes.");
			return null;
		}
		
		MembroGrupoPesquisaDao dao2 = getDAO(MembroGrupoPesquisaDao.class);
		membroGrupoPesquisa = dao2.findByIds(docentes.values());

		// Construindo as datas inicial e final para passar como par�metros pro DAO
		Calendar cal = Calendar.getInstance();		
		cal.set(Calendar.YEAR, getAnoInicial());
		cal.set(Calendar.MONTH, getMesInicial() -1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		Date dataInicial = cal.getTime();
		
		cal.set(Calendar.YEAR, getAnoFinal());
		cal.set(Calendar.MONTH, getMesFinal()-1);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date dataFinal = cal.getTime();		

		obj = dao.generateAvaliacaoGrupoPesquisa(dataInicial, dataFinal, docentes, true);

		return forward(JSP_RELATORIO);
	}
	
	
	/**
	 * Chama a consulta que efetua a gera��o do relat�rio e redireciona para a exibi��o.
	 *
 	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/producao/relatorios/avaliacao_grupo_pesquisa/form.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorio() throws DAOException {

		if (getAnoInicial() > getAnoFinal()) {
			addMensagemErro("Por favor, escolha um intervalo de ano base correto.");
			return null;
		}

		ProducaoDao dao = getDAO(ProducaoDao.class);
		if (grupo.getId() > 0) {
			grupo = dao.findByPrimaryKey(grupo.getId(), GrupoPesquisa.class);
		} else {
			addMensagemErro("Por favor, selecione um grupo de pesquisa.");
			return null;
		}
		
		Map<String, Integer> docentes = dao.findByDocentesPermanentes(grupo.getId());
		if(docentes == null || docentes.isEmpty()){
			addMensagemErro("N�o h� docentes permanentes no grupo de pesquisa selecionado.");
			return null;
		}
		
		// Construindo as datas inicial e final para passar como par�metros pro DAO
		Calendar cal = Calendar.getInstance();		
		cal.set(Calendar.YEAR, getAnoInicial());
		cal.set(Calendar.MONTH, getMesInicial() -1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		Date dataInicial = cal.getTime();
		
		cal.set(Calendar.YEAR, getAnoFinal());
		cal.set(Calendar.MONTH, getMesFinal()-1);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date dataFinal = cal.getTime();		

		obj = dao.generateAvaliacaoGrupoPesquisa(dataInicial, dataFinal, docentes, false);

		return forward(JSP_RESULTADO);
	}

	/**
	 * M�todo auxiliar utilizado na view do relat�rio para construir o cabe�alho da tabela.
	 * 
 	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>N�o invocado por JSP.</li>
	 *	</ul>
	 * @return
	 */
	public String getCabecalho(){
		StringBuilder cabecalho = new StringBuilder();
		if(obj != null && obj.getDocentes() != null){
			for(int i = 1; i <= obj.getDocentes().size(); i++)
				cabecalho.append("<td style=\"text-align: right; background-color: #DEDFE3\">"+UFRNUtils.completaZeros(i, 2)+"</td>");
		}
		return cabecalho.toString();
	}
	
	/**
	 * M�todo auxiliar utilizado na view do relat�rio para construir o rodap� da tabela com os totais por docente.
	 *
 	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>N�o invocado por JSP.</li>
	 *	</ul>
	 * @return
	 */
	public String getRodape(){
		StringBuilder rodape = new StringBuilder();
		if(obj != null && obj.getDocentes() != null){
			Long totalLinha = 0L;
			Long totalLinhaSemTeto = 0L;
			for(String nome: obj.getDocentes()){
				Long totalDocente = calculaTotalDocente(nome);
				Long totalDocenteSemTeto = calculaTotalDocenteSemTeto(nome);
				rodape.append("<td style=\"text-align: right\">"+totalDocente+(totalDocenteSemTeto > totalDocente ? "("+totalDocenteSemTeto+")":"")+"</td>");
				totalLinha += totalDocente;
				totalLinhaSemTeto += totalDocenteSemTeto;
			}
			rodape.append("<td style=\"text-align: right\">"+totalLinha+(totalLinhaSemTeto > totalLinha ? "("+totalLinhaSemTeto+")":"")+"</td>");
			rodape.append("<td style=\"text-align: center\"> - </td>");
		}
		return rodape.toString();
	}

	/**
	 * M�todo auxiliar utilizado no relat�rio para construir a tabela com a informa��o do grupo de pesquisa.
	 *
 	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>N�o invocado por JSP.</li>
	 *	</ul>
	 * @return
	 */
	public String getRodapeGeral(){

	    StringBuilder rodape = new StringBuilder();
		
		Iterator<MembroGrupoPesquisa> it = membroGrupoPesquisa.iterator();
		String grupoAtual = null;			
		while (it.hasNext()) {
			MembroGrupoPesquisa membroGrupo = it.next();
			Long totalDocente = calculaTotalDocente(membroGrupo.getServidor().getNome());
				
			if (membroGrupo.getGrupoPesquisa().getNome() != grupoAtual && membroGrupo.getGrupoPesquisa().getNome() != null &&
					totalDocente <= 1) {
				rodape.append("<tr class=\"header\" style=\"padding-top:20px;\">");
					rodape.append("<td colspan=4; style=\"background-color: #DEDFE3; text-align: left;\">Grupo de Pesquisa: " 
							+ membroGrupo.getGrupoPesquisa().getNome() +" </td>");
				rodape.append("</tr>");
				rodape.append("<tr class=\"header\" style=\"padding-top:20px;\">");
					rodape.append("<td style=\"background-color: #DEDFE3; text-align: left;\">Docente:  </td>");
					rodape.append("<td style=\"background-color: #DEDFE3; text-align: left;\">Centro:  </td>");
					rodape.append("<td style=\"background-color: #DEDFE3; text-align: left;\">Departamento:  </td>");
					rodape.append("<td style=\"background-color: #DEDFE3; text-align: left;\">Total:  </td>");
				rodape.append("</tr>");
			}
				
			if (totalDocente <= 1) {
				rodape.append("<tr class=\"componentes\">");
					rodape.append("<td style=\"text-align: left\">" + membroGrupo.getServidor().getNome()+ " </td>");
					rodape.append("<td style=\"text-align: left\">" + membroGrupo.getServidor().getUnidade().getGestora()+ " </td>");
					rodape.append("<td style=\"text-align: left\">" + membroGrupo.getServidor().getUnidade().getNome() + " </td>");
					rodape.append("<td style=\"text-align: right\">" + totalDocente+"</td>");
				rodape.append("</tr>");
				
				grupoAtual = membroGrupo.getGrupoPesquisa().getNome();
			}
		   }
		return rodape.toString();
	}
	
	/**
	 * M�todo auxiliar utilizado na view do relat�rio para construir o rodap� da tabela com as pontua��es totais por docente.
	 * 
 	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>N�o invocado por JSP.</li>
	 *	</ul>
	 * @return
	 */
	public String getRodapePontuacao(){
		StringBuilder rodape = new StringBuilder();
		if(obj != null && obj.getDocentes() != null){
			Long totalLinha = 0L;
			Long totalLinhaSemTeto = 0L;
			for(String nome: obj.getDocentes()){
				Long totalDocente = calculaPontuacaoTotalDocente(nome);
				Long totalDocenteSemTeto = calculaPontuacaoTotalDocenteSemTeto(nome);
				rodape.append("<td style=\"text-align: right\">"+totalDocente+(totalDocenteSemTeto > totalDocente ? "("+totalDocenteSemTeto+")":"")+"</td>");
				totalLinha += totalDocente;
				totalLinhaSemTeto += totalDocenteSemTeto;
			}
			rodape.append("<td style=\"text-align: center\"> - </td>");
			rodape.append("<td style=\"text-align: right\">"+totalLinha+(totalLinhaSemTeto > totalLinha ? "("+totalLinhaSemTeto+")":"")+"</td>");
		}
		return rodape.toString();
	}
	
	/**
	 * M�todo auxiliar para calcular a pontua��o total obtida por um determinado docente,
	 * respeitando o teto definido por cada item do relat�rio.
	 * 
	 * @param nome
	 * @return
	 */
	private Long calculaTotalDocente(String nome) {
		Long total = 0L;
		for(Integer item: RelatorioAvaliacaoGrupoPesquisa.getItensRelatorio()){
			total += calculaTeto(obj.getResultado().get(item).getDocentes().get(nome), item);
		}
		return total;
	}
	
	/**
	 * M�todo auxiliar para calcular a pontua��o total obtida por um determinado docente,
	 * sem considerar o teto definido por cada item do relat�rio.
	 * @param nome
	 * @return
	 */
	private Long calculaTotalDocenteSemTeto(String nome) {
		Long total = 0L;
		for(Integer item: RelatorioAvaliacaoGrupoPesquisa.getItensRelatorio()){
			total += obj.getResultado().get(item).getDocentes().get(nome);
		}
		return total;
	}
	
	/**
	 * M�todo auxiliar para calcular a pontua��o total obtida por um determinado docente,
	 * respeitando o teto definido por cada item do relat�rio.
	 * @param nome
	 * @return
	 */
	private Long calculaPontuacaoTotalDocente(String nome) {
		Long total = 0L;
		for(Integer item: RelatorioAvaliacaoGrupoPesquisa.getItensRelatorio()){
			total += obj.getResultado().get(item).getPontuacao() * calculaTeto(obj.getResultado().get(item).getDocentes().get(nome), item);
		}
		return total;
	}
	
	/**
	 * M�todo auxiliar para calcular a pontua��o total obtida por um determinado docente,
	 * sem considerar o teto definido por cada item do relat�rio.
	 * @param nome
	 * @return
	 */
	private Long calculaPontuacaoTotalDocenteSemTeto(String nome) {
		Long total = 0L;
		for(Integer item: RelatorioAvaliacaoGrupoPesquisa.getItensRelatorio()){
			total += obj.getResultado().get(item).getPontuacao() * obj.getResultado().get(item).getDocentes().get(nome);
		}
		return total;
	}

	/**
	 * M�todo auxiliar para na view para construir uma linha da tabela que apresenta o relat�rio.
	 * @param item
	 * @return
	 */
	private String buildLinha(int item){
		StringBuilder row = new StringBuilder();
		LinhaRelatorioGrupoPesquisa linha = obj.getResultado().get(item);
		row.append("<td style=\"text-align: right\">"+ linha.getPontuacao() +"</td>");
		long totalLinha = 0;
		long totalLinhaSemTeto = 0;
		for(String docente: obj.getDocentes()){
			Long quantSemTeto = linha.getDocentes().get(docente);
			Long quant = calculaTeto(quantSemTeto, item);
			row.append("<td style=\"text-align: right\">"+ quant + (quantSemTeto > quant? "("+quantSemTeto+")" : "" ) +"</td>");
			totalLinhaSemTeto += quantSemTeto;
			totalLinha += quant;
		}
		row.append("<td style=\"text-align: right\">"+ totalLinha + (totalLinhaSemTeto > totalLinha? "("+totalLinhaSemTeto+")" : "" ) +"</td>");
		row.append("<td style=\"text-align: right\">"+ linha.getPontuacao()* totalLinha + (totalLinhaSemTeto > totalLinha? "("+linha.getPontuacao()*totalLinhaSemTeto+")" : "" ) +"</td>");
		return row.toString();
	}
	
	/**
	 * M�todo auxiliar que retorna a quantidade de um item limitada pelo seu teto (quantidade m�xima daquele item que deve ser pontuada).
	 * @param quant
	 * @param item
	 * @return
	 */
	private Long calculaTeto(Long quant, int item) {
		int teto = RelatorioAvaliacaoGrupoPesquisa.getTeto(item);
		return quant > teto ? teto : quant;
	}

	/**
	 * Cancela a opera��o na qual est� realizando.
 	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/producao/relatorios/avaliacao_grupo_pesquisa/form-geral.jsp</li>
	 *      <li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/producao/relatorios/avaliacao_grupo_pesquisa/form.jsp</li>
	 *	</ul>
	 */
	@Override
	public String cancelar() {
		return forward("/pesquisa/menu.jsp");
	}
	
	/* M�todos chamados na view para constru��o da linha referente a cada item do relat�rio */
	
	public String getArtigoCompletoPeriodicoIndexadoNacional(){
		return buildLinha(RelatorioAvaliacaoGrupoPesquisa.ART_COMP_PER_IND_NAC);
	}
	
	public String getArtigoCompletoPeriodicoIndexadoInternacional(){
		return buildLinha(RelatorioAvaliacaoGrupoPesquisa.ART_COMP_PER_IND_INT);
	}
	
	public String getAnaisEventosResumoLocal(){
		return buildLinha(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_LOC);
	}
	
	public String getAnaisEventosResumoRegional(){
		return buildLinha(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_REG);
	}
	
	public String getAnaisEventosResumoNacional(){
		return buildLinha(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_NAC);
	}
	
	public String getAnaisEventosResumoInternacional(){
		return buildLinha(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_INT);
	}
	
	public String getAnaisEventosResumoExpandidoLocal(){
		return buildLinha(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_EX_LOC);
	}
	
	public String getAnaisEventosResumoExpandidoRegional(){
		return buildLinha(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_EX_REG);
	}
	
	public String getAnaisEventosResumoExpandidoNacional(){
		return buildLinha(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_EX_NAC);
	}
	
	public String getAnaisEventosResumoExpandidoInternacional(){
		return buildLinha(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_EX_INT);
	}
	
	public String getAnaisEventosTrabalhoCompletoLocal(){
		return buildLinha(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_TC_LOC);
	}
	
	public String getAnaisEventosTrabalhoCompletoRegional(){
		return buildLinha(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_TC_REG);
	}
	
	public String getAnaisEventosTrabalhoCompletoNacional(){
		return buildLinha(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_TC_NAC);
	}
	
	public String getAnaisEventosTrabalhoCompletoInternacional(){
		return buildLinha(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_TC_INT);
	}
	
	public String getLivroIsbnNacional(){
		return buildLinha(RelatorioAvaliacaoGrupoPesquisa.LIVRO_ISBN_NAC);
	}
	
	public String getLivroIsbnInternacional(){
		return buildLinha(RelatorioAvaliacaoGrupoPesquisa.LIVRO_ISBN_INT);
	}
	
	public String getCapituloLivroIsbnNacional(){
		return buildLinha(RelatorioAvaliacaoGrupoPesquisa.CAP_LIVRO_ISBN_NAC);
	}
	
	public String getCapituloLivroIsbnInternacional(){
		return buildLinha(RelatorioAvaliacaoGrupoPesquisa.CAP_LIVRO_ISBN_INT);
	}
	
	/* M�todos acessores dos par�metros do relat�rio */
	
	public Integer getAnoInicial() {
		return anoInicial;
	}

	public void setAnoInicial(Integer anoInicial) {
		this.anoInicial = anoInicial;
	}

	public Integer getAnoFinal() {
		return anoFinal;
	}

	public void setAnoFinal(Integer anoFinal) {
		this.anoFinal = anoFinal;
	}

	public String getMesInicialString() {
		return CalendarUtils.getMesAbreviado(mesInicial);
	}
	
	public Integer getMesInicial() {
		return mesInicial;
	}

	public void setMesInicial(Integer mesInicial) {
		this.mesInicial = mesInicial;
	}

	public String getMesFinalString() {
		return CalendarUtils.getMesAbreviado(mesFinal);
	}
	
	public Integer getMesFinal() {
		return mesFinal;
	}

	public void setMesFinal(Integer mesFinal) {
		this.mesFinal = mesFinal;
	}

	public GrupoPesquisa getGrupo() {
		return grupo;
	}

	public void setGrupo(GrupoPesquisa grupo) {
		this.grupo = grupo;
	}

	public Collection<MembroGrupoPesquisa> getServidor() {
		return membroGrupoPesquisa;
	}

	public void setServidor(Collection<MembroGrupoPesquisa> servidor) {
		this.membroGrupoPesquisa = servidor;
	}
	
}