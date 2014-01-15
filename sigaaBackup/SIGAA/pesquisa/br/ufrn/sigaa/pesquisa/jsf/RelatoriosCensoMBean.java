/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/07/2007
 *
 */

package br.ufrn.sigaa.pesquisa.jsf;

import java.util.Map;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.pesquisa.RelatoriosPesquisaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;

/** 
 * Controller responsável por elaborar relatórios para o Censo do INEP
 * 
 * @author Leonardo Campos
 *
 */
@SuppressWarnings("unchecked")
public class RelatoriosCensoMBean extends SigaaAbstractController<Object> {

	/** Formulário de ano-base do relatório. */
	private final String JSP_ANO = "/pesquisa/relatorios/form_ano.jsp";
	/** Link do Relatório de Tempo de Dedicação à Pesquisa por Grau de Formação. */
	private final String JSP_REL_PROJETOS_DOCENTE = "/pesquisa/relatorios/relatorio_projetos_docente.jsp";
	/** Link do Relatório de Tempo de Dedicação à Pesquisa por Área de Conhecimento. */
	private final String JSP_REL_PROJETOS_AREA = "/pesquisa/relatorios/relatorio_projetos_area.jsp";
	/** Link do Relatório Analítico de Quantidade de Projetos por Docente agrupados por Área de Conhecimento. */
	private final String JSP_REL_PROJETOS_AREA_ANALITICO = "/pesquisa/relatorios/relatorio_projetos_area_analitico.jsp";
	
	/** Relatório de Tempo de Dedicação à Pesquisa por Grau de Formação. */
	private final int REL_PROJETOS_DOCENTE = 1;
	/** Relatório de Tempo de Dedicação à Pesquisa por Área de Conhecimento. */
	private final int REL_PROJETOS_AREA = 2;
	/** Relatório Analítico de Quantidade de Projetos por Docente agrupados por Área de Conhecimento. */
	private final int REL_PROJETOS_AREA_ANALITICO = 3;
	
	/** Ano base do relatório. */
	private int ano;
	
	/** Dados do relatório. */
	private Map relatorio;
	
	/** Indica qual relatório será processado. */
	private int tipoRelatorio;

	/** Retorna o relatório desejado.
	 * <br/>
	 * Chamado por /pesquisa/relatorios/form_ano.jsp
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	public String submeterAno() throws DAOException, SegurancaException, ArqException {
		checkRole(new int[]{SigaaPapeis.GESTOR_PESQUISA, SigaaPapeis.PORTAL_PLANEJAMENTO});
		ValidatorUtil.validaInt(ano, "Ano", erros);
		if(hasErrors()){
			return null;
		}
		RelatoriosPesquisaDao dao = getDAO(RelatoriosPesquisaDao.class);
		switch (tipoRelatorio) {
			case REL_PROJETOS_DOCENTE :
				relatorio = dao.findProjetosDocente(ano);
				return forward(JSP_REL_PROJETOS_DOCENTE);
			case REL_PROJETOS_AREA :
				relatorio = dao.findProjetosArea(ano);
				return forward(JSP_REL_PROJETOS_AREA);
			case REL_PROJETOS_AREA_ANALITICO :
				relatorio = dao.findProjetosAreaAnalitico(ano);
				return forward(JSP_REL_PROJETOS_AREA_ANALITICO);
		}
		return redirectMesmaPagina();
	}
	
	
	/** Retorna o Relatório de Tempo de Dedicação à Pesquisa por Grau de Formação. 
	 * <br/>
	 * Chamado pelas JSP´s:
	 * <ul>
	 * 	<li> /portais/rh_plan/abas/censo.jsp</li>
	 * 	<li> /portais/rh_plan/abas/pesquisa.jsp</li>
	 *  <li> /WEB-INF/jsp/pesquisa/menu/relatorio.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String relatorioProjetosDocente() throws SegurancaException{
		checkRole(new int[]{SigaaPapeis.GESTOR_PESQUISA, SigaaPapeis.PORTAL_PLANEJAMENTO});
		tipoRelatorio = REL_PROJETOS_DOCENTE;
		return forward(JSP_ANO);
	}
	
	/** Retorna o Relatório de Tempo de Dedicação à Pesquisa por Área de Conhecimento. 
	 * <br/>
	 * Chamado pelas JSP´s:
	 * <ul>
	 * 	<li> /portais/rh_plan/abas/censo.jsp</li>
	 * 	<li> /portais/rh_plan/abas/pesquisa.jsp</li>
	 * 	<li> /WEB-INF/jsp/pesquisa/menu/relatorio.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String relatorioProjetosArea() throws SegurancaException{
		checkRole(new int[]{SigaaPapeis.GESTOR_PESQUISA, SigaaPapeis.PORTAL_PLANEJAMENTO});
		tipoRelatorio = REL_PROJETOS_AREA;
		return forward(JSP_ANO);
	}
	
	/** Retorna o Relatório Analítico de Quantidade de Projetos por Docente agrupados por Área de Conhecimento.
	 * <br/>
	 * Chamado pelas JSP´s:
	 * <ul>
	 * 	<li> /portais/rh_plan/abas/censo.jsp</li>
	 *  <li> /portais/rh_plan/abas/pesquisa.jsp</li>
	 *  <li> /WEB-INF/jsp/pesquisa/menu/relatorio.jsp</li>
	 * </ul> 
	 * @return
	 * @throws SegurancaException
	 */
	public String relatorioProjetosAreaAnalitico() throws SegurancaException{
		checkRole(new int[]{SigaaPapeis.GESTOR_PESQUISA, SigaaPapeis.PORTAL_PLANEJAMENTO});
		tipoRelatorio = REL_PROJETOS_AREA_ANALITICO;
		return forward(JSP_ANO);
	}
	
	/** Retorna o ano base do relatório. 
	 * @return
	 */
	public int getAno() {
		return ano;
	}

	/** Seta o ano base do relatório.
	 * @param ano
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/** Retorna os dados do relatório. 
	 * @return
	 */
	public Map getRelatorio() {
		return relatorio;
	}

	/** Seta os dados do relatório.
	 * @param relatorio
	 */
	public void setRelatorio(Map relatorio) {
		this.relatorio = relatorio;
	}

	/** Indica qual relatório será processado. 
	 * @return
	 */
	public int getTipoRelatorio() {
		return tipoRelatorio;
	}

	/** Seta qual relatório será processado.
	 * @param tipoRelatorio
	 */
	public void setTipoRelatorio(int tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}
}
