package br.ufrn.sigaa.portal.reitoria.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.extensao.RelatorioPlanejamentoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.jsf.HistoricoMBean;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Classe managed bean respons�vel controlar a gera��o do   
 * relat�rio de acompanhamento acad�mico de servidores que s�o alunos de p�s-gradua��o.
 * @author M�rio Rizzi
 */
@Component("relatorioDesempenhoServidorDiscente") @Scope("request")
public class RelatorioDesempenhoServidorDiscenteMBean extends SigaaAbstractController<Object>{

	/** Define a JSP que realiza a gera��o do relat�rio */
	private static final String JSP_FORM_REL_SERVIDOR_DISCENTE_CR = "form_desempenho_servidor_discente.jsp"; 
	/** Define a JSP que exibe o relat�rio */
	private static final String JSP_REL_SERVIDOR_DISCENTE_CR = "relatorio_desempenho_servidor_discente.jsp"; 

	/** Resultado da consulta */
	List<Map<String, Object>> lista;
	
	/**
	 * Inicializa os atributos envolvidos na classe 
	 * M�todo n�o invocado por JSP.
	 */
	public RelatorioDesempenhoServidorDiscenteMBean(){
		
		if( lista == null )
			lista = new ArrayList<Map<String,Object>>();
		
	}
	
	/**
	 * 	M�todo n�o invocado por JSP.
	 * @see br.ufrn.sigaa.arq.jsf.SigaaAbstractController#checkChangeRole()
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		isUserInRole(SigaaPapeis.ACOMPANHA_DESEMPENHO_SERVIDORES_NA_POSGRADUACAO);
	}
	
	/**
	 * M�todo n�o invocado por JSP.
	 * @see br.ufrn.sigaa.arq.jsf.SigaaAbstractController#getDirBase()
	 */
	@Override
	public String getDirBase() {
		// TODO Auto-generated method stub
		return "/portais/rh_plan/relatorios/";
	}
	
	/**
	 * M�todo n�o invocado por JSP.
	 * @see br.ufrn.sigaa.arq.jsf.SigaaAbstractController#getFormPage()
	 */
	@Override
	public String getFormPage() {
		return getDirBase() + JSP_FORM_REL_SERVIDOR_DISCENTE_CR;
	}
	
	/**
	 * Realiza a consulta do relat�rio e visualiza.<br/>
     * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/portais/rh_plan/relatorios/form_servidor_discente_cr.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorio() throws DAOException{
		lista = getDAO(RelatorioPlanejamentoDao.class).findDesempenhoServidoresDiscentesPos();
		return forward( getDirBase() + JSP_REL_SERVIDOR_DISCENTE_CR );
	}
	
	/**
	 * Redireciona para o MBean que exibe o hist�rico.
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/portais/rh_plan/relatorios/form_servidor_discente_cr.jsp</li>
	 * </ul> 
	 * @return
	 * @throws ArqException
	 */
	public String verHistorico() throws ArqException {
		int id = getParameterInt("idDiscente", 0);
		if (id > 0){
			Discente d = new Discente(id);
			HistoricoMBean historico = new HistoricoMBean();
			historico.setDiscente(getGenericDAO().refresh(d));
			return historico.selecionaDiscente();
		} else 
			return null;

	}

	/**
	 * Retorna uma lista contendo todos os servidores discentes com seus respecitvos coeficientes de rendimento.
     * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/portais/rh_plan/relatorios/form_servidor_discente_cr.jsp</li>
	 *  <li>/sigaa.war/portais/rh_plan/relatorios/relatorio_servidor_discente_cr.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<Map<String, Object>> getLista() {
		return lista;
	}

	/**
	 * M�todo n�o invocado por JSP.
	 * @param lista
	 */
	public void setLista(List<Map<String, Object>> lista) {
		this.lista = lista;
	}
	
}
