package br.ufrn.sigaa.pesquisa.jsf;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIData;
import javax.faces.event.ActionEvent;

import org.richfaces.component.html.HtmlDataTable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.Formatador;
import br.ufrn.sigaa.arq.dao.pesquisa.MembroGrupoPesquisaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoApoioGrupoPesquisaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.ElementoDespesa;
import br.ufrn.sigaa.extensao.dominio.ResumoOrcamentoDetalhado;
import br.ufrn.sigaa.parametros.dominio.ParametrosPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.GrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.MembroGrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoApoioGrupoPesquisa;
import br.ufrn.sigaa.pesquisa.negocio.ProjetoApoioGrupoPesquisaValidator;
import br.ufrn.sigaa.projetos.dominio.OrcamentoDetalhado;

/**
 * Controlador responsável por registrar as informações referentes aos Projetos 
 * de Apoio aos grupos de Pesquisa.
 * 
 * @author Jean Guerethes
 */
@Component
@Scope("session")
public class ProjetoApoioGrupoPesquisaMBean extends SigaaAbstractController<ProjetoApoioGrupoPesquisa> {

    /** Armazena um orçamento detalhado. **/
    private OrcamentoDetalhado orcamento;

    /** Armazena um orçamento detalhado. **/
    private OrcamentoDetalhado orcamentoRemocao;
    
    /** Armazena a tabela orçamentária. **/
    private Map<ElementoDespesa, ResumoOrcamentoDetalhado> tabelaOrcamentaria;

    /** Projetos que o docente faz parte */
    private Collection<ProjetoApoioGrupoPesquisa> projetos;
    
	/** Membro do grupo de pesquisa */
	private MembroGrupoPesquisa membroPainel = new MembroGrupoPesquisa();
    
	/** UIData utilizado para a manipulação da listagem dos membros permanentes */
	private UIData membros = new HtmlDataTable();
	
	/** Construtor padrão. */
	public ProjetoApoioGrupoPesquisaMBean() {
		clear();
	}

	/**
	 * Reponsável por inicializar todos os atributos que serão utilizados
	 */
	private void clear() {
		obj = new ProjetoApoioGrupoPesquisa();
		orcamento = new OrcamentoDetalhado();
		orcamento.setElementoDespesa(new ElementoDespesa());
		tabelaOrcamentaria = new HashMap<ElementoDespesa, ResumoOrcamentoDetalhado>();
		orcamentoRemocao = new OrcamentoDetalhado();
	}

	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		clear();
		MembroGrupoPesquisaDao dao = getDAO(MembroGrupoPesquisaDao.class);
		boolean coordenador = false;
		try {
			coordenador = dao.isCoordenadorGrupoPesquisa( getUsuarioLogado().getPessoa().getId() );
		} finally {
			dao.close();
		}
		if (coordenador)
			return super.preCadastrar();
		 else {
			 addMensagemErro("Somente o Coordenador do Grupo pode submeter novos Projetos de Apoio a Grupos de Pesquisa.");
			 return null;
		 }
	}
	
	@Override
	public String getDirBase() {
		return "/pesquisa/ProjetoApoioGrupoPesquisa";
	}

	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		
		if (obj.getGrupoPesquisa().getId() == 0)
			addMensagem(MensagensArquitetura.AUTOCOMPLETE_OBRIGATORIO, "Grupo de Pesquisa");
		
		float valorTotal = 0;
		for (OrcamentoDetalhado orcamento : obj.getOrcamentosDetalhados())
			 valorTotal += orcamento.getValorTotal();
		
		int valorMaximo =  ParametroHelper.getInstance().getParametroInt(ParametrosPesquisa.VALOR_MAXIMO_ORCAMENTO_PROJ_APOIO_GRUPO_PESQUISA);
		if (valorTotal > valorMaximo)
			addMensagemErro("O valor máximo permitido para o orçamento é de R$ " + Formatador.getInstance().formatarMoeda(valorMaximo));
		
		addMensagens(obj.validate());
		if (hasErrors())
			return null;
		
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setUsuarioLogado(getUsuarioLogado());
			if ( obj.getId() > 0 ) {
				mov.setCodMovimento(SigaaListaComando.ALTERAR_PROJETO_APOIO_GRUPO_PESQUISA);
				prepareMovimento(SigaaListaComando.ALTERAR_PROJETO_APOIO_GRUPO_PESQUISA);
			} else {
				mov.setCodMovimento(SigaaListaComando.CADASTRAR_PROJETO_APOIO_GRUPO_PESQUISA);
				prepareMovimento(SigaaListaComando.CADASTRAR_PROJETO_APOIO_GRUPO_PESQUISA);
			}
			execute(mov);
			if (isReprepare())
				prepareMovimento(SigaaListaComando.CADASTRAR_PROJETO_APOIO_GRUPO_PESQUISA);
			
			addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Projeto de Apoio");
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
		
		if (hasErrors()) {
			return null;
		} else {
			return listar();
		}
	}
	
	/**
	 * Carrega as informações necessárias para a visualização
	 * @return
	 * @throws DAOException
	 */
	private ProjetoApoioGrupoPesquisa carregarInformacoes() throws DAOException{
		setId();
		ProjetoApoioGrupoPesquisaDao dao = getDAO(ProjetoApoioGrupoPesquisaDao.class);
		try {
			setObj( dao.carregarProjetoApoio(obj.getId()) );
			
			obj.getProjeto().getOrcamento().addAll(
					dao.findByExactField(OrcamentoDetalhado.class, "projeto.id", obj.getProjeto().getId()) );
			
			recalculaTabelaOrcamentaria(obj.getProjeto().getOrcamento());
			
			if ( obj == null ) {
				addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
				return null;
			}
		} finally {
			dao.close();
		}
		return obj;
	}
	
	/**
	 * Carrega as informações neceesárias e direciona para a tela da visualização.
	 * 
     * <br />
     *  Método chamado pela(s) seguinte(s) JSP(s):
     *  <ul>
     *   <li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/ProjetoApoioGrupoPesquisa/lista.jsp</li>
     * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String view() throws DAOException {
		carregarInformacoes();
		return forward(getViewPage());
	}
	
	/**
	 * Carrega as informações necessárias para a atualização e direciona o usuário para esse tela em questão.
	 * 
     * <br />
     *  Método chamado pela(s) seguinte(s) JSP(s):
     *  <ul>
     *   <li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/ProjetoApoioGrupoPesquisa/lista.jsp</li>
     * </ul>
	 * @return
	 * @throws ArqException
	 */
	@Override
	public String atualizar() throws ArqException {
		clear();
		setId();
		ProjetoApoioGrupoPesquisaDao dao = getDAO(ProjetoApoioGrupoPesquisaDao.class);
		try {
			setObj( dao.findAndFetch(obj.getId(), ProjetoApoioGrupoPesquisa.class, "editalPesquisa", "projeto", "grupoPesquisa") );
			recalculaTabelaOrcamentaria(obj.getProjeto().getOrcamento());
		} finally {
			dao.close();
		}
		setConfirmButton("Alterar");
		return forward(getFormPage());
	}
	
	@Override
	public String remover() throws ArqException {
		try {
			setId();
			setObj( getDAO(ProjetoApoioGrupoPesquisaDao.class).findLeve(obj.getId()) );
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setUsuarioLogado(getUsuarioLogado());
			mov.setCodMovimento(SigaaListaComando.REMOVER_PROJETO_APOIO_GRUPO_PESQUISA);
			prepareMovimento(SigaaListaComando.REMOVER_PROJETO_APOIO_GRUPO_PESQUISA);
			execute(mov);
			if (isReprepare())
				prepareMovimento(SigaaListaComando.REMOVER_PROJETO_APOIO_GRUPO_PESQUISA);
			addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listar();
	}
	
    /**
     * Adiciona um orçamento à lista.
     * <br />
     *  Método chamado pela(s) seguinte(s) JSP(s):
     *  <ul>
     *   <li>sigaa.war/extensao/Atividade/orcamento.jsp</li>
     * </ul>
     * @return Retorna para mesma tela permitindo inclusão de novo elemento.
     */
	public String adicionaOrcamento() {

		try {
			ListaMensagens mensagens = new ListaMensagens();
			Integer idElementoDespesa = getParameterInt("idElementoDespesa", 0);
			orcamento.setElementoDespesa(getGenericDAO().findByPrimaryKey(idElementoDespesa, ElementoDespesa.class));
			orcamento.setAtivo(true);
			
			// Mantem o botão precionado
			getCurrentRequest().getSession().setAttribute("idElementoDespesa", orcamento.getElementoDespesa().getId());
			
			if ((idElementoDespesa == null) || (idElementoDespesa == 0)) {
				addMensagemErro("Elemento de Despesa é Obrigatório: Selecione um elemento de despesa");
				return null;
			}
			
			if ( orcamento.getValorUnitario() == null ){
				addMensagem(MensagensArquitetura.VALOR_MAIOR_IGUAL_ZERO, "Valor Unitário");
				return null;
			}

			ProjetoApoioGrupoPesquisaValidator.validaAdicionaOrcamento(orcamento, mensagens);
			if (!mensagens.isEmpty()) {
				addMensagens(mensagens);
				return null;
			}

		} catch (Exception e) {
			if ( e.getMessage() == null ) {
				addMensagemErro("Selecione o Elemento de Despesa.");
				return null;
			} else {
				addMensagemErro(e.getMessage());
				return null;
			}
		}

		obj.addOrcamentoDetalhado(orcamento);

		if (orcamento.getElementoDespesa() == null)
			orcamento.setElementoDespesa( new ElementoDespesa() );
		
		// Prepara para novo item do orçamento
		getCurrentSession().setAttribute("idElementoDespesa", orcamento.getElementoDespesa().getId());
		orcamento = new OrcamentoDetalhado();
		orcamento.setElementoDespesa(new ElementoDespesa());
		recalculaTabelaOrcamentaria(obj.getOrcamentosDetalhados());
		return redirectMesmaPagina();
	}

    /**
     * Facilita a exibição da tabela de orçamentos da ação que está sendo cadastrada/alterada.
     * 
     * @param orcamentos {@link Collection} de {@link OrcamentoDetalhado} com itens do orçamento da ação atual.
     */
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

	/**
	 * Remove o orçamento do projeto de Apoio a Grupo de Pesquisa.
	 * 
     * <br />
     *  Método chamado pela(s) seguinte(s) JSP(s):
     *  <ul>
     *   <li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/ProjetoApoioGrupoPesquisa/form.jsp</li>
     * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String removeOrcamento() throws DAOException {
		List<OrcamentoDetalhado> orcamentos = obj.getOrcamentosDetalhados();
		interacao : for (int i = 0; i < orcamentos.size(); i++) {
			OrcamentoDetalhado orcamentoLinha = orcamentos.get(i);
			if ( orcamentoLinha.getElementoDespesa().getId() == orcamentoRemocao.getElementoDespesa().getId() 
					&& orcamentoLinha.getQuantidade() == orcamentoRemocao.getQuantidade() ){
				orcamentos.remove(i);
				break interacao;
			}
		}
		obj.setOrcamentosDetalhados(orcamentos);
		orcamentoRemocao = new OrcamentoDetalhado();
		recalculaTabelaOrcamentaria(obj.getOrcamentosDetalhados());

		return redirectMesmaPagina();
	}

	@Override
	public String listar() throws ArqException {
		ProjetoApoioGrupoPesquisaDao dao = getDAO(ProjetoApoioGrupoPesquisaDao.class);
		try {
			projetos = dao.projetosUsuarioParticipa(getUsuarioLogado().getPessoa().getId());
		} finally {
			dao.close();
		}
		return forward(getListPage());
	}
	
	/**
	 * Serve para carregar os membros dos grupo de pesquisa, selecionado. 
	 * 
     * <br />
     *  Método chamado pela(s) seguinte(s) JSP(s):
     *  <ul>
     *   <li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/ProjetoApoioGrupoPesquisa/form.jsp</li>
     * </ul>
	 * @param event
	 * @throws DAOException
	 */
	public void carregaMembros(ActionEvent event) throws DAOException {
		GrupoPesquisa grupo = (GrupoPesquisa) event.getComponent().getAttributes().get("grupoPesqu");
		MembroGrupoPesquisaDao dao = getDAO(MembroGrupoPesquisaDao.class);
		try {
			obj.setGrupoPesquisa( 
					dao.findAndFetch(grupo.getId(), GrupoPesquisa.class, "viceCoordenador", "areaConhecimentoCnpq", "coordenador", 
							"usuarioCriacao", "equipesGrupoPesquisa", "equipesGrupoPesquisa.pessoa") );
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Serve para carregar o lattes do docente para alterção.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/membros.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public void carregarPainel( ActionEvent event ) {
		membroPainel = (MembroGrupoPesquisa) membros.getRowData();
	}

	public void adicionarLattes( ActionEvent event ) throws ArqException{
		getGenericDAO().updateField(MembroGrupoPesquisa.class, membroPainel.getId(), "enderecoLattes", membroPainel.getEnderecoLattes());
		redirectMesmaPagina();
	}
	
	public OrcamentoDetalhado getOrcamento() {
		return orcamento;
	}

	public void setOrcamento(OrcamentoDetalhado orcamento) {
		this.orcamento = orcamento;
	}

	public Map<ElementoDespesa, ResumoOrcamentoDetalhado> getTabelaOrcamentaria() {
		return tabelaOrcamentaria;
	}

	public void setTabelaOrcamentaria(
			Map<ElementoDespesa, ResumoOrcamentoDetalhado> tabelaOrcamentaria) {
		this.tabelaOrcamentaria = tabelaOrcamentaria;
	}

	public OrcamentoDetalhado getOrcamentoRemocao() {
		return orcamentoRemocao;
	}

	public void setOrcamentoRemocao(OrcamentoDetalhado orcamentoRemocao) {
		this.orcamentoRemocao = orcamentoRemocao;
	}

	public Collection<ProjetoApoioGrupoPesquisa> getProjetos() {
		return projetos;
	}

	public void setProjetos(Collection<ProjetoApoioGrupoPesquisa> projetos) {
		this.projetos = projetos;
	}

	public MembroGrupoPesquisa getMembroPainel() {
		return membroPainel;
	}

	public void setMembroPainel(MembroGrupoPesquisa membroPainel) {
		this.membroPainel = membroPainel;
	}

	public UIData getMembros() {
		return membros;
	}

	public void setMembros(UIData membros) {
		this.membros = membros;
	}
	
}