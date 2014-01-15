/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 09/07/2010
 * 
 */
package br.ufrn.sigaa.pesquisa.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.dao.pesquisa.RelatoriosPesquisaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.TipoBolsaPesquisaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pesquisa.relatorios.LinhaRelatorioBolsaPesquisa;
import br.ufrn.sigaa.pesquisa.relatorios.ParametrosRelatorioBolsaPesquisa;

/**
 * MBean para a geração de relatórios quantitativos para bolsas de pesquisa ativas.
 *
 * @author Thalisson Muriel
 */

@Component("relatorioBolsasPesquisaMBean") @Scope("request")
public class RelatorioBolsasPesquisaMBean extends SigaaAbstractController {
	
	/** Link do Relatório de Bolsas de Pesquisa. */
	public static final String JSP_REL_BOLSA_PESQUISA = "/pesquisa/relatorios/relatorio_bolsas_pesquisa.jsp";

	/** Indica por qual campo será ordenado o relarório. */
	private int ordenarPor;
	
	/** Indica por qual centro será filtrado o relátorio. */
	private int filtrarPor;
	
	/** Número de registros encontrados no relatório. */
	private int registrosEncontrados;
	
	/** Responsável por armazenar quais tipos de bolsas serão utilizadas no relatório. */	
	List<Integer> tiposBolsaSelecionados = new ArrayList<Integer>();
	
	/**Representa os parâmetros passados para realizar a consulta no banco de dados*/
	ParametrosRelatorioBolsaPesquisa param = new ParametrosRelatorioBolsaPesquisa();
	
	/** Lista de dados do relatório. */
	private Map<Integer, LinhaRelatorioBolsaPesquisa> lista = new TreeMap<Integer, LinhaRelatorioBolsaPesquisa>();
		
	/** Contrutor Padrão */
	public RelatorioBolsasPesquisaMBean() {
		
	}
	
	/**
	 * Retorna uma coleção de itens dos Centros.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/pesquisa/relatorios/form_bolsas_pesquisa.jsp</li>
	 *	</ul>
	 * @throws ArqException 
	 */
	public Collection<SelectItem> getCentros() throws ArqException{
		UnidadeDao dao = getDAO(UnidadeDao.class);
		return toSelectItems(dao.findByTipoUnidadeAcademica(TipoUnidadeAcademica.CENTRO), "id", "nome");
	}
	
	/**
	 * Retorna o relatório das bolsas de pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/pesquisa/relatorios/form_bolsas_pesquisa.jsp</li>
	 *	</ul>
	 * @throws SegurancaException 
	 * 
	 */
	public Collection<SelectItem> getTiposBolsa()  {
		return toSelectItems(getDAO(TipoBolsaPesquisaDao.class).findTiposBolsa());
	}

	/**
	 * Retorna o relatório das bolsas de pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/pesquisa/relatorios/form_bolsas_pesquisa.jsp</li>
	 *	</ul>
	 * @throws SegurancaException 
	 * 
	 */
	public String gerarRelatorio() throws DAOException, SegurancaException{
		checkRole(new int[]{SigaaPapeis.GESTOR_PESQUISA,SigaaPapeis.PORTAL_PLANEJAMENTO});
		
		RelatoriosPesquisaDao dao = getDAO(RelatoriosPesquisaDao.class);
		lista = dao.findBolsasPesquisa(tiposBolsaSelecionados, param, ordenarPor, filtrarPor);
		
		registrosEncontrados = lista.size();
		
		return forward(JSP_REL_BOLSA_PESQUISA);
	}
	
	public int getOrdenarPor() {
		return ordenarPor;
	}

	public void setOrdenarPor(int ordenarPor) {
		this.ordenarPor = ordenarPor;
	}

	public int getFiltrarPor() {
		return filtrarPor;
	}

	public void setFiltrarPor(int filtrarPor) {
		this.filtrarPor = filtrarPor;
	}

	public Map<Integer, LinhaRelatorioBolsaPesquisa> getLista() {
		return lista;
	}

	public void setLista(Map<Integer, LinhaRelatorioBolsaPesquisa> lista) {
		this.lista = lista;
	}

	public int getRegistrosEncontrados() {
		return registrosEncontrados;
	}

	public void setRegistrosEncontrados(int registrosEncontrados) {
		this.registrosEncontrados = registrosEncontrados;
	}

	public List<Integer> getTiposBolsaSelecionados() {
		return tiposBolsaSelecionados;
	}

	public void setTiposBolsaSelecionados(List<Integer> tiposBolsaSelecionados) {
		this.tiposBolsaSelecionados = tiposBolsaSelecionados;
	}

	public ParametrosRelatorioBolsaPesquisa getParam() {
		return param;
	}

	public void setParam(ParametrosRelatorioBolsaPesquisa param) {
		this.param = param;
	}
	
}