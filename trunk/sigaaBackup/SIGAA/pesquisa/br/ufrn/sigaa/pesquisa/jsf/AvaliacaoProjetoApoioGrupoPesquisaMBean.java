/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 10/10/2012 
 */
package br.ufrn.sigaa.pesquisa.jsf;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIData;
import javax.faces.model.SelectItem;

import org.richfaces.component.html.HtmlDataTable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.pesquisa.EditalPesquisaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoApoioGrupoPesquisaDao;
import br.ufrn.sigaa.arq.dao.projetos.AvaliacaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.ElementoDespesa;
import br.ufrn.sigaa.extensao.dominio.ResumoOrcamentoDetalhado;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.GrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoApoioGrupoPesquisa;
import br.ufrn.sigaa.projetos.dominio.Avaliacao;
import br.ufrn.sigaa.projetos.dominio.OrcamentoDetalhado;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoAvaliacao;
import br.ufrn.sigaa.projetos.negocio.AvaliacaoProjetosFactory;

/**
 * @author Leonardo
 *
 */
@Component("avaliacaoProjetoApoioGrupoPesquisaMBean")
@Scope("request")
public class AvaliacaoProjetoApoioGrupoPesquisaMBean extends
		SigaaAbstractController<Avaliacao> {

	private ProjetoApoioGrupoPesquisa projeto;
	
	/** variável para armazenar uma coleção de projetos**/
	private Collection<ProjetoApoioGrupoPesquisa> projetos;
	
	/** tabela com os orçamentos*/
	private Map<ElementoDespesa, ResumoOrcamentoDetalhado> tabelaOrcamentaria;
	
	/** flag para saber se a busca deve ser feita pelo edital ou não **/
	private boolean filtroEdital;
	
	/** variável para armazenar uma coleção de avaliações**/
	private Collection<Avaliacao> avaliacoes;
	
	/** UIData utilizado para a manipulação da listagem dos membros permanentes */
	private UIData membros = new HtmlDataTable();
	
	/**Variável para armazenar o id do edital de uma avaliação **/
	private Integer idEdital = null;
	
	/** construtor padrão*/
	public AvaliacaoProjetoApoioGrupoPesquisaMBean() {
		obj = new Avaliacao();
		tabelaOrcamentaria = new HashMap<ElementoDespesa, ResumoOrcamentoDetalhado>();
	}	
	
	/** Lista os projetos a serem avaliados 
	 * 
	 * É invocado pela seguinte JSP
	 * 
	 * <ui>
	 * <li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ui>**/
	public String listarProjetos() throws DAOException {
		ProjetoApoioGrupoPesquisaDao dao = getDAO(ProjetoApoioGrupoPesquisaDao.class);
		
		projetos = dao.projetosParaAvaliar(null);
		return forward("/pesquisa/ProjetoApoioGrupoPesquisa/lista_avaliacao.jsp");
	}

	/** Método utilizado para iniciar o processo de avaliação de um projeto
	 * 
	 * Método invocado pela seguinte JSP:
	 * <ul>
	 * <li>sigaa.war/pesquisa/ProjetoApoioGrupoPesquisa/lista_avaliacao.jsp</li></ul>	 
	 * **/
	public String iniciarCriarAvaliacao() throws ArqException{
		prepareMovimento(SigaaListaComando.AVALIAR_PROJETO);
		ProjetoApoioGrupoPesquisaDao dao = getDAO(ProjetoApoioGrupoPesquisaDao.class);
		obj = new Avaliacao();
		tabelaOrcamentaria = new HashMap<ElementoDespesa, ResumoOrcamentoDetalhado>();
		projeto = dao.carregarProjetoApoio(getParameterInt("id"));
		obj.setProjeto(projeto.getProjeto());
		obj.setAvaliador(getUsuarioLogado());
		System.out.println(dao.distribuicaoMaisRecente());
		obj.setDistribuicao(dao.distribuicaoMaisRecente());
		if(obj.getDistribuicao() != null)
			obj.getDistribuicao().getModeloAvaliacao().getTipoAvaliacao().getId();
		obj.setSituacao(new TipoSituacaoAvaliacao(TipoSituacaoAvaliacao.PENDENTE));
		
		projeto.getProjeto().getOrcamento().addAll(
				dao.findByExactField(OrcamentoDetalhado.class, "projeto.id", projeto.getProjeto().getId()) );
		
		recalculaTabelaOrcamentaria(projeto.getProjeto().getOrcamento());
		
		projeto.setGrupoPesquisa( dao.findByPrimaryKey(projeto.getGrupoPesquisa().getId(), GrupoPesquisa.class) );
		
		AvaliacaoDao avaDao = getDAO(AvaliacaoDao.class);
		avaliacoes = avaDao.findByProjeto(projeto.getProjeto());
		
		return forward("/pesquisa/ProjetoApoioGrupoPesquisa/form_avaliacao.jsp");
	}
	
	/** Método utilizado para recalcula a Tabela Orcamentaria ao iniciar um processo de avaliação de um projeto
	 * 
	 * Método não é invocado por nenhuma JSP:
	 * 	 
	 * **/
	private void recalculaTabelaOrcamentaria(Collection<OrcamentoDetalhado> orcamentos) {
		tabelaOrcamentaria.clear();

		for (OrcamentoDetalhado orca : orcamentos) {
			ResumoOrcamentoDetalhado resumo = tabelaOrcamentaria.get(orca
					.getElementoDespesa());
			if (resumo == null) {
				resumo = new ResumoOrcamentoDetalhado();
			}
			resumo.getOrcamentos().add(orca);
			tabelaOrcamentaria.put(orca.getElementoDespesa(), resumo);
		}
	}
	
	/** Método utilizado para avaliar um projeto
	 * 
	 * É invocado na seguinte JSP:
	 * <ui>
	 * <li>sigaa.war/pesquisa/ProjetoApoioGrupoPesquisa/form_avaliacao.jsp </li>
	 * </ui>
	 * **/
	public String avaliar() throws ArqException {
		obj.calcularMedia();
		obj.setDataAvaliacao(new Date());
		ValidatorUtil.validateRequired(obj.getParecer(), "Parecer", erros);
		ValidatorUtil.validateMaxLength(obj.getParecer(), 2000, "Parecer", erros);
		
		if(hasErrors()){
			return null;
		}
		
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.AVALIAR_PROJETO);
			mov.setObjMovimentado(obj);
			mov.setObjAuxiliar(AvaliacaoProjetosFactory.getInstance().getEstrategia(obj.getProjeto()));
			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			return listarProjetos();
		} catch(NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
		return forward("/pesquisa/ProjetoApoioGrupoPesquisa/form_avaliacao.jsp");
	}
	
	public Collection<ProjetoApoioGrupoPesquisa> getProjetos() {
		return projetos;
	}

	public void setProjetos(Collection<ProjetoApoioGrupoPesquisa> projetos) {
		this.projetos = projetos;
	}

	public ProjetoApoioGrupoPesquisa getProjeto() {
		return projeto;
	}

	public void setProjeto(ProjetoApoioGrupoPesquisa projeto) {
		this.projeto = projeto;
	}

	public Map<ElementoDespesa, ResumoOrcamentoDetalhado> getTabelaOrcamentaria() {
		return tabelaOrcamentaria;
	}

	public void setTabelaOrcamentaria(
			Map<ElementoDespesa, ResumoOrcamentoDetalhado> tabelaOrcamentaria) {
		this.tabelaOrcamentaria = tabelaOrcamentaria;
	}

	public Collection<Avaliacao> getAvaliacoes() {
		return avaliacoes;
	}

	public void setAvaliacoes(Collection<Avaliacao> avaliacoes) {
		this.avaliacoes = avaliacoes;
	}
	
	public boolean isFiltroEdital() {
		return filtroEdital;
	}

	public void setFiltroEdital(boolean filtroEdital) {
		this.filtroEdital = filtroEdital;
	}
	
	public Integer getIdEdital() {
		return idEdital;
	}


	public void setIdEdital(Integer idEdital) {
		this.idEdital = idEdital;
	}


	/** Método que retorna os tipos de editais disponíveis 
	 * 
	 * Método invocado pela seguinte JSP:
	 * <ul>
	 * <li>sigaa.war/pesquisa/ProjetoApoioGrupoPesquisa/lista_avaliacao.jsp</li></ul>	 
	 * **/
	public Collection<SelectItem> getEditaisDisponiveis() throws DAOException{
		return toSelectItems(getDAO(EditalPesquisaDao.class).findAllEditais(), "id", "descricao");
	}
	

	/** Método que releazia a busca de avaliações por edital ou busca por todos os editais 
	 * 
	 * Método invocado pela seguinte JSP:
	 * <ul>
	 * <li>sigaa.war/pesquisa/ProjetoApoioGrupoPesquisa/lista_avaliacao.jsp</li></ul>	 
	 * **/
	public String buscar() throws DAOException{
		if(isFiltroEdital())
			ValidatorUtil.validateRequired(idEdital, "Edital", erros);
		
		if(hasErrors())
			return null;
		
		if(isFiltroEdital()) {
			ProjetoApoioGrupoPesquisaDao dao = getDAO(ProjetoApoioGrupoPesquisaDao.class);
			EditalPesquisa edital = new EditalPesquisa(); edital.setId(idEdital);
			projetos.clear();
			projetos.addAll(dao.projetosParaAvaliar(edital));
		} else{
			return listarProjetos();
		}
		
		if(projetos.isEmpty()){
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		}
		
		return null;
	}
	
	public UIData getMembros() {
		return membros;
	}

	public void setMembros(UIData membros) {
		this.membros = membros;
	}
}
