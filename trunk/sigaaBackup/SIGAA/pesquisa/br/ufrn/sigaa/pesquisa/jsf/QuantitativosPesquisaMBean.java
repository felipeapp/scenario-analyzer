/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 10/04/2007
 * 
 */
package br.ufrn.sigaa.pesquisa.jsf;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.faces.event.ValueChangeEvent;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoPesquisaDao;
import br.ufrn.sigaa.arq.dao.prodocente.ProducaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pesquisa.relatorios.MesAno;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Managed-Bean para tratar os quantitativos de projetos e o detalhamento do
 * mesmo
 *
 * @author Gleydson
 *
 */
public class QuantitativosPesquisaMBean extends SigaaAbstractController<Object> {

	/** Quantitativo dos projetos internos cadastrados */
	private long projetosInternosCadastrados;

	/** Quantitativo dos projetos externos cadastrados */
	private long projetosExternosCadastrados;

	/** Quantitativo dos projetos internos enviados */
	private long projetosInternosEnviados;

	/** Quantitativo dos projetos externos enviados */
	private long projetosExternosEnviados;

	/** Quantitativo dos projetos avaliados */
	private long avaliados;

	/** Quantitativo das cotas solicitadas */
	private long cotasSolicitadas;

	/** Quantitativo das bases de Pesquisa */
	private static long basesPesquisa;

	/** Bolsistas de Pesquisa */
	private Map<String, Integer> bolsistas = new Hashtable<String, Integer>();

	/** Número do edital  */
	private int idEdital;
	
	/** Campos que servem como filtro */
	private Integer anoInicial, anoFinal, mesInicial, mesFinal;

	/** Unidade referente a quantidade de Pesquisa */
	private Unidade unidade = new Unidade();
	
	/** Responsável pela geração do relatório */
	private Map<String, Map<MesAno, Long>> relatorio;

	/**
	 * É realizado um refresh nos dados dos projetos internos(cadastrados e enviados) 
	 * e dos externos (cadastrados e enviados) 
  	 * <br>
   	 * JSP: <ul>
	 * 		  <li>/SIGAA/app/sigaa.ear/sigaa.war/menu_pesquisa.jsp</li>
	 *      </ul>
	 * @param evt
	 * @throws DAOException
	 */
	public void refreshDados(ValueChangeEvent evt) throws DAOException {

		Integer idEdital = 0;
		try {
			idEdital = new Integer(evt.getNewValue().toString());
		} catch (Exception e) {
			notifyError(e);
		}

		if (idEdital != null && idEdital != 0) {

			ProjetoPesquisaDao dao = getDAO(ProjetoPesquisaDao.class);

			projetosInternosCadastrados = dao.getTotalByStatus(TipoSituacaoProjeto.CADASTRADO, idEdital, true);

			projetosExternosCadastrados = dao.getTotalByStatus(TipoSituacaoProjeto.CADASTRADO, idEdital, false);

			projetosInternosEnviados = dao.getTotalByStatus(TipoSituacaoProjeto.SUBMETIDO, idEdital, true);

			projetosInternosEnviados = dao.getTotalByStatus(TipoSituacaoProjeto.SUBMETIDO, idEdital, false);

		}

	}

	public static long getBasesPesquisa() {
		return basesPesquisa;
	}

	public static void setBasesPesquisa(long basesPesquisa) {
		QuantitativosPesquisaMBean.basesPesquisa = basesPesquisa;
	}

	public long getAvaliados() {
		return avaliados;
	}

	public void setAvaliados(long avaliados) {
		this.avaliados = avaliados;
	}

	public Map<String, Integer> getBolsistas() {
		return bolsistas;
	}

	public void setBolsistas(Map<String, Integer> bolsistas) {
		this.bolsistas = bolsistas;
	}

	public long getCotasSolicitadas() {
		return cotasSolicitadas;
	}

	public void setCotasSolicitadas(long cotasSolicitadas) {
		this.cotasSolicitadas = cotasSolicitadas;
	}

	public long getProjetosExternosCadastrados() {
		return projetosExternosCadastrados;
	}

	public void setProjetosExternosCadastrados(long projetosExternosCadastrados) {
		this.projetosExternosCadastrados = projetosExternosCadastrados;
	}

	public long getProjetosExternosEnviados() {
		return projetosExternosEnviados;
	}

	public void setProjetosExternosEnviados(long projetosExternosEnviados) {
		this.projetosExternosEnviados = projetosExternosEnviados;
	}

	public long getProjetosInternosCadastrados() {
		return projetosInternosCadastrados;
	}

	public void setProjetosInternosCadastrados(long projetosInternosCadastrados) {
		this.projetosInternosCadastrados = projetosInternosCadastrados;
	}

	public long getProjetosInternosEnviados() {
		return projetosInternosEnviados;
	}

	public void setProjetosInternosEnviados(long projetosInternosEnviados) {
		this.projetosInternosEnviados = projetosInternosEnviados;
	}

	public int getIdEdital() {
		return idEdital;
	}

	public void setIdEdital(int idEdital) {
		this.idEdital = idEdital;
	}

	/**
	 * Método invocado quando se tem o intuito de visualizar o formulário
  	 * <br>
   	 * JSP: <ul>
	 * 		  <li>/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/pesquisa/menu/prodocente.jsp</li>
	 *      </ul>
	 * @return
	 */
	public String verFormulario(){
		anoFinal = CalendarUtils.getAnoAtual();
		mesFinal = getMesAtual();
		anoInicial = anoFinal -1;
		mesInicial = mesFinal;
		return forward("/pesquisa/relatorios/quantitativo/form.jsp");
	}
	
	/**
	 * Método responsável pela geração do relatório quantitativo pesquisa.
  	 * <br>
   	 * JSP: <ul>
	 * 		  <li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/relatorios/quantitativo/form.jsp</li>
	 *      </ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorio() throws DAOException{
		if (getAnoInicial() > getAnoFinal()) {
			addMensagemErro("Por favor, escolha um intervalo de anos correto.");
			return null;
		} else if (getAnoInicial().equals(getAnoFinal())) {
			if (getMesInicial() > getMesFinal()){
				addMensagemErro("Por favor, escolha um intervalo de meses correto.");
				return null;
			}
		}
		
		ProducaoDao dao = getDAO(ProducaoDao.class);
		if (unidade.getId() != -1) {
			unidade = dao.findByPrimaryKey(unidade.getId(), Unidade.class);
		} else {
			unidade.setNome(RepositorioDadosInstitucionais.get("siglaInstituicao"));
		}
		
		// Construindo as datas inicial e final para passar como parâmetros pro DAO
		Calendar cal = Calendar.getInstance();		
		cal.set(Calendar.YEAR, getAnoInicial());
		cal.set(Calendar.MONTH, getMesInicial() -1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		Date dataInicial = cal.getTime();
		
		cal.set(Calendar.YEAR, getAnoFinal());
		cal.set(Calendar.MONTH, getMesFinal()-1);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date dataFinal = cal.getTime();
		
		relatorio = dao.generateQuantitativosPesquisa(dataInicial, dataFinal, unidade.getId());
		
		return forward("/pesquisa/relatorios/quantitativo/resultado.jsp");
	}

	/**
	 * Serve para gerar o cabeçalho que será utilizado no relatório.
	 * 
	 * @return
	 */
	public String getCabecalho() {
		StringBuilder linha1 = new StringBuilder();
		StringBuilder linha2 = new StringBuilder();
		Map<MesAno, Long> mapa = relatorio.values().iterator().next();
		Map<Integer, Integer> mesesPorAno = new HashMap<Integer, Integer>();
		
		linha2.append("<tr><th>Descrição</th>");
		for(MesAno m: mapa.keySet()){
			mesesPorAno.put(m.getAno(), mesesPorAno.get(m.getAno()) == null ? 1 : mesesPorAno.get(m.getAno()) + 1);
			linha2.append("<th>"+m.getMesDesc()+"</th>\n");
		}
		linha2.append("<th>Total</th></tr>");
		
		linha1.append("<tr><th>&nbsp;</th>");
		for(Integer k: mesesPorAno.keySet()){
			linha1.append("<th colspan=\"" + mesesPorAno.get(k) + "\">" + k + "</th>");
		}
		linha1.append("<th>&nbsp;</th></tr>");
		
		return linha1.toString() + linha2.toString();
	}
	
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

	public Integer getMesInicial() {
		return mesInicial;
	}

	public void setMesInicial(Integer mesInicial) {
		this.mesInicial = mesInicial;
	}

	public Integer getMesFinal() {
		return mesFinal;
	}

	public void setMesFinal(Integer mesFinal) {
		this.mesFinal = mesFinal;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public Map<String, Map<MesAno, Long>> getRelatorio() {
		return relatorio;
	}

	public void setRelatorio(Map<String, Map<MesAno, Long>> relatorio) {
		this.relatorio = relatorio;
	}

	public String getDescricaoPeriodo(){
		return CalendarUtils.getMesAbreviado(mesInicial)+"/"+anoInicial+" a "+CalendarUtils.getMesAbreviado(mesFinal)+"/"+anoFinal;
	}

}