/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 28/10/2010
 * 
 */
package br.ufrn.sigaa.pesquisa.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.pesquisa.EditalPesquisaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.RelatoriosPesquisaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.TipoBolsaPesquisaDao;
import br.ufrn.sigaa.arq.dao.prodocente.BolsaObtidaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pesquisa.relatorios.LinhaRelatorioResumoCotas;
import br.ufrn.sigaa.pesquisa.relatorios.ParametrosRelatorioBolsaPesquisa;

/**
 * MBean para a geração de relatório quantitativo de Cotas de Bolsas.
 *
 * @author Thalisson Muriel
 */

@Component("relatorioResumoCotaMBean") @Scope("request")
public class RelatorioResumoCotas extends SigaaAbstractController {
	
	/** Link do Relatório de Bolsas de Pesquisa. */
	private static final String JSP_REL_RESUMO_COTAS = "/pesquisa/relatorios/relatorio_resumo_cotas.jsp";
	
	/** Número de registros encontrados no relatório. */
	private int registrosEncontrados;
	
	/** Responsável por armazenar quais tipos de bolsas serão utilizadas no relatório. */	
	private List<Integer> tiposBolsaSelecionados = new ArrayList<Integer>();
	
	/** Responsável por armazenar os parâmetros passados para realizar a consulta no banco de dados. */
	ParametrosRelatorioBolsaPesquisa param = new ParametrosRelatorioBolsaPesquisa();
	
	/** Responsável por armazenar o edital utilizado no relatório. */	
	private int editalSelecionado = 0;
	
	/** Lista de dados do relatório. */
	private Map<Integer, LinhaRelatorioResumoCotas> lista = new TreeMap<Integer, LinhaRelatorioResumoCotas>();
		
	public RelatorioResumoCotas(){
		
	}
	
	/**
	 * Retorna uma coleção de itens dos Editais.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/pesquisa/relatorios/form_resumo_cotas.jsp</li>
	 *	</ul>
	 * @throws ArqException 
	 */
	public Collection<SelectItem> getEditais() throws ArqException{
		EditalPesquisaDao dao = getDAO(EditalPesquisaDao.class);
		return toSelectItems(dao.findAllEditais(), "id", "edital.descricao");
	}
	
	/**
	 * Retorna uma coleção de itens de Bolsas de Pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/pesquisa/relatorios/form_resumo_cotas.jsp</li>
	 *	</ul>
	 * @throws SegurancaException 
	 * 
	 */
	public Collection<SelectItem> getTiposBolsa()  {
		return toSelectItems(getDAO(TipoBolsaPesquisaDao.class).findTiposBolsa());
	}
	
	/**
	 * Retorna o relatório de cotas por docente.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/pesquisa/relatorios/form_resumo_cotas.jsp</li>
	 *	</ul>
	 * @throws SegurancaException 
	 * 
	 */
	public String gerarRelatorio() throws DAOException, SegurancaException{
		checkRole(new int[]{SigaaPapeis.GESTOR_PESQUISA,SigaaPapeis.PORTAL_PLANEJAMENTO});
				
		erros = new ListaMensagens();
		ValidatorUtil.validateRequired(editalSelecionado, "Edital", erros);
		ValidatorUtil.validateRequired(tiposBolsaSelecionados, "Tipo da Bolsa", erros);
		if (!erros.isEmpty()) {
			addMensagens(erros);
			return null;
		}
		
		RelatoriosPesquisaDao dao = getDAO(RelatoriosPesquisaDao.class);
		BolsaObtidaDao bolsaDao = getDAO(BolsaObtidaDao.class);
		try {
			lista = dao.findResumoCotas(editalSelecionado, tiposBolsaSelecionados, param);
			Collection<Integer> bolsaObt = bolsaDao.bolsistasProdutividade();
			Set<Integer> relatorio = lista.keySet();
			for (Integer linha : relatorio) {
				if ( bolsaObt.contains(lista.get(linha).getIdDocente()) )
					lista.get(linha).setDocenteProdutividade(true);
			}
		} finally {
			dao.close();
			bolsaDao.close();
		}
		
		registrosEncontrados = lista.size();
		if(registrosEncontrados < 1){
			addMensagemErro("Nenhum registro encontrado de acordo com os critérios de busca informados.");
			return null;
		}
			
		return forward(JSP_REL_RESUMO_COTAS);
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

	public int getEditalSelecionado() {
		return editalSelecionado;
	}

	public void setEditalSelecionado(int editalSelecionado) {
		this.editalSelecionado = editalSelecionado;
	}

	public Map<Integer, LinhaRelatorioResumoCotas> getLista() {
		return lista;
	}

	public void setLista(Map<Integer, LinhaRelatorioResumoCotas> lista) {
		this.lista = lista;
	}

	public ParametrosRelatorioBolsaPesquisa getParam() {
		return param;
	}

	public void setParam(ParametrosRelatorioBolsaPesquisa param) {
		this.param = param;
	}

}

