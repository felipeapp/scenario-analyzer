/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 28/04/2010
 *
 */
package br.ufrn.sigaa.extensao.relatorio.jsf;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.extensao.RelatorioTotalAcaoExtensaoEditalDao;
import br.ufrn.sigaa.ensino.graduacao.relatorios.jsf.AbstractRelatorioGraduacaoMBean;
import br.ufrn.sigaa.extensao.dominio.AreaTematica;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * 
 * MBean Responsável por relatório de total de ações de extensão que concorreram a editais públicos.
 * @author Geyson
 *
 */
@Component("relatorioTotalAcaoEdital") @Scope("request")
public class RelatorioTotalAcaoExtensaoEditalMBean extends  AbstractRelatorioGraduacaoMBean {

	private final String CONTEXTO ="/extensao/RelatorioTotalAcaoExtensaoEdital/";
	private final String JSP_SELECIONA = "seleciona";
	private final String JSP_RELATORIO = "relatorio";
	
	/** Lista de dados do relatório. */
	private List<Map<String,Object>> lista = new ArrayList<Map<String,Object>>();
	
	/** Construtor padrão. */
	public RelatorioTotalAcaoExtensaoEditalMBean(){
		area = new AreaTematica();
		situacao = new TipoSituacaoProjeto();
	}
	
	/** Recebem valores selecionados na busca */
	private Date dataInicio;
	private Date dataFim;
	private AreaTematica area;
	private TipoSituacaoProjeto situacao;
	
	/** Filtros usado na busca para a geração do relatório. */
	private boolean filtroAreaTematica = false;
	private boolean filtroSituacaoAcao = false;
	private boolean filtroPeriodo = false;
	
	
	
	
	/**
	 * Faz o redirecionamento para a tela de preenchimento dos dados para a geração do relatório
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	
	/**
	 * Gera relatório do total de ações de extensão que concorreram a editais públicos.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>
	 *   sigga/e/extensao/RelatorioTotalAcaoExtensaoEdital/relatorio.jsp
	 *  </li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 * @throws DAOException
	 */
	public String gerarRelatorio() throws DAOException {
		RelatorioTotalAcaoExtensaoEditalDao dao = getDAO(RelatorioTotalAcaoExtensaoEditalDao.class);

		if(filtroPeriodo){
			if(dataInicio != null || dataFim != null ){
				ValidatorUtil.validaData(Formatador.getInstance().formatarData(this.dataInicio), "Data Inicial", erros);
				ValidatorUtil.validaData(Formatador.getInstance().formatarData(this.dataFim), "Data Final ", erros);
				ValidatorUtil.validaDataAnteriorIgual(this.dataInicio, this.dataFim, "Data Inicial", erros);
			}
			else{
				addMensagemErro("Período da Excução da Ação: Preencha campo selecionado. ");
			}
		}else{
			dataInicio = null;
			dataFim = null;
		}

		if(filtroAreaTematica){
			if(area.getId() <= 0 ){
				addMensagemErro("Área Temática: Preencha campo selecionado. ");
			}
			else{
				getGenericDAO().initialize(area);
			}
		}else{
			area.setId(0);
		}

		if(filtroSituacaoAcao){
			if(situacao.getId() <= 0){
				addMensagemErro("Situação da Ação: Preencha campo selecionado. ");
			}else{
				getGenericDAO().initialize(situacao);
			}
		}else{
			situacao.setId(0);
		}

		if(!hasErrors()){
			lista = dao.relatorioAcaoEdital(area.getId(), situacao.getId(), this.dataInicio, this.dataFim);
			if (lista.size() > 0) {
				return forward(JSP_RELATORIO);
			}
			else
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		return null;
	}
	
	
	public List<Map<String, Object>> getLista() {
		return lista;
	}
	public void setLista(List<Map<String, Object>> lista) {
		this.lista = lista;
	}
	public boolean isFiltroPeriodo() {
		return filtroPeriodo;
	}

	public void setFiltroPeriodo(boolean filtroPeriodo) {
		this.filtroPeriodo = filtroPeriodo;
	}

	public boolean isFiltroAreaTematica() {
		return filtroAreaTematica;
	}
	public void setFiltroAreaTematica(boolean filtroAreaTematica) {
		this.filtroAreaTematica = filtroAreaTematica;
	}
	public boolean isFiltroSituacaoAcao() {
		return filtroSituacaoAcao;
	}
	public void setFiltroSituacaoAcao(boolean filtroSituacaoAcao) {
		this.filtroSituacaoAcao = filtroSituacaoAcao;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public AreaTematica getArea() {
		return area;
	}

	public void setArea(AreaTematica area) {
		this.area = area;
	}

	public TipoSituacaoProjeto getSituacao() {
		return situacao;
	}

	public void setSituacao(TipoSituacaoProjeto situacao) {
		this.situacao = situacao;
	}

	
	
}
