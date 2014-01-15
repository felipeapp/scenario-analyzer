/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 26/08/2010
 *
 */
package br.ufrn.sigaa.extensao.relatorio.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.sigaa.ensino.graduacao.relatorios.jsf.AbstractRelatorioGraduacaoMBean;

/**
 * 
 * MBean Respons�vel por relat�rio quantitativo de membros participantes de a��es de extens�o
 * @author geyson karlos
 *
 */
@Component("relatorioMembrosAcoes") @Scope("request")
public class RelatorioQuantitativoMembrosAcoesMBean extends  AbstractRelatorioGraduacaoMBean {
	
	private final String CONTEXTO ="/extensao/RelatorioQuantitativoMembrosAcoes/";
	private final String JSP_RELATORIO = "relatorio";
	private final String JSP_SELECIONA = "seleciona";
	
	/** Filtros usado na busca para a gera��o do relat�rio. */
	private boolean filtroAno = false;

	/** Lista de dados do relat�rio. */
	private List<Map<String,Object>> lista = new ArrayList<Map<String,Object>>();
	
	

	/** Construtor padr�o */ 
	public RelatorioQuantitativoMembrosAcoesMBean(){
		
	}
	
	/**
	 * Faz o redirecionamento para a tela de preenchimento dos dados para a gera��o do relat�rio
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>
	 *   sigga/extesao/menu.jsp
	 *  </li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarRelatorioAcoesEdital() throws SegurancaException{
		checkListRole();
		return forward(CONTEXTO+JSP_SELECIONA); 
	}
	
	public String gerarRelatorio() throws DAOException {
		
		return forward(CONTEXTO+JSP_RELATORIO);
	}
	
	public List<Map<String, Object>> getLista() {
		return lista;
	}

	public void setLista(List<Map<String, Object>> lista) {
		this.lista = lista;
	}

	public void setFiltroAno(boolean filtroAno) {
		this.filtroAno = filtroAno;
	}

	public boolean isFiltroAno() {
		return filtroAno;
	}
}
