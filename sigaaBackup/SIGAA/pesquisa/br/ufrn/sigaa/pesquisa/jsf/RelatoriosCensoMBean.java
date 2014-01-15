/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Controller respons�vel por elaborar relat�rios para o Censo do INEP
 * 
 * @author Leonardo Campos
 *
 */
@SuppressWarnings("unchecked")
public class RelatoriosCensoMBean extends SigaaAbstractController<Object> {

	/** Formul�rio de ano-base do relat�rio. */
	private final String JSP_ANO = "/pesquisa/relatorios/form_ano.jsp";
	/** Link do Relat�rio de Tempo de Dedica��o � Pesquisa por Grau de Forma��o. */
	private final String JSP_REL_PROJETOS_DOCENTE = "/pesquisa/relatorios/relatorio_projetos_docente.jsp";
	/** Link do Relat�rio de Tempo de Dedica��o � Pesquisa por �rea de Conhecimento. */
	private final String JSP_REL_PROJETOS_AREA = "/pesquisa/relatorios/relatorio_projetos_area.jsp";
	/** Link do Relat�rio Anal�tico de Quantidade de Projetos por Docente agrupados por �rea de Conhecimento. */
	private final String JSP_REL_PROJETOS_AREA_ANALITICO = "/pesquisa/relatorios/relatorio_projetos_area_analitico.jsp";
	
	/** Relat�rio de Tempo de Dedica��o � Pesquisa por Grau de Forma��o. */
	private final int REL_PROJETOS_DOCENTE = 1;
	/** Relat�rio de Tempo de Dedica��o � Pesquisa por �rea de Conhecimento. */
	private final int REL_PROJETOS_AREA = 2;
	/** Relat�rio Anal�tico de Quantidade de Projetos por Docente agrupados por �rea de Conhecimento. */
	private final int REL_PROJETOS_AREA_ANALITICO = 3;
	
	/** Ano base do relat�rio. */
	private int ano;
	
	/** Dados do relat�rio. */
	private Map relatorio;
	
	/** Indica qual relat�rio ser� processado. */
	private int tipoRelatorio;

	/** Retorna o relat�rio desejado.
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
	
	
	/** Retorna o Relat�rio de Tempo de Dedica��o � Pesquisa por Grau de Forma��o. 
	 * <br/>
	 * Chamado pelas JSP�s:
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
	
	/** Retorna o Relat�rio de Tempo de Dedica��o � Pesquisa por �rea de Conhecimento. 
	 * <br/>
	 * Chamado pelas JSP�s:
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
	
	/** Retorna o Relat�rio Anal�tico de Quantidade de Projetos por Docente agrupados por �rea de Conhecimento.
	 * <br/>
	 * Chamado pelas JSP�s:
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
	
	/** Retorna o ano base do relat�rio. 
	 * @return
	 */
	public int getAno() {
		return ano;
	}

	/** Seta o ano base do relat�rio.
	 * @param ano
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/** Retorna os dados do relat�rio. 
	 * @return
	 */
	public Map getRelatorio() {
		return relatorio;
	}

	/** Seta os dados do relat�rio.
	 * @param relatorio
	 */
	public void setRelatorio(Map relatorio) {
		this.relatorio = relatorio;
	}

	/** Indica qual relat�rio ser� processado. 
	 * @return
	 */
	public int getTipoRelatorio() {
		return tipoRelatorio;
	}

	/** Seta qual relat�rio ser� processado.
	 * @param tipoRelatorio
	 */
	public void setTipoRelatorio(int tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}
}
