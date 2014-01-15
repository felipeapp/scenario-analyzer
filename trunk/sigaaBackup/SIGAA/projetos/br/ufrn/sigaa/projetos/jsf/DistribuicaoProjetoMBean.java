/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 * 
 * Created on 11/10/2010
 * 
 */
package br.ufrn.sigaa.projetos.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.util.Collection;
import java.util.List;

import javax.faces.component.UIData;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.projetos.DistribuicaoProjetoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.projetos.dominio.Avaliacao;
import br.ufrn.sigaa.projetos.dominio.AvaliadorProjeto;
import br.ufrn.sigaa.projetos.dominio.DistribuicaoAvaliacao;
import br.ufrn.sigaa.projetos.dominio.ModeloAvaliacao;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoAvaliador;
import br.ufrn.sigaa.projetos.negocio.DistribuicaoProjetosFactory;
import br.ufrn.sigaa.projetos.negocio.EstrategiaDistribuicaoProjetos;

/**
 * Controlador respons�vel por:
 * 1. Cadastrar e configurar uma distribui��o de projetos para avalia��o;
 * 2. Distribuir projetos para avalia��o;
 * 3. Consolidar as avalia��es distribu�das.
 * 
 * @author Ilueny Santos
 * @author Leonardo Campos
 *
 */
@Component("distribuicaoProjetoMbean") 
@Scope("session")
public class DistribuicaoProjetoMBean extends SigaaAbstractController<DistribuicaoAvaliacao>  {

	/** Estrat�gia de distribui��o utilizada */
	private EstrategiaDistribuicaoProjetos estrategia;

	/** Representa��o na view das avalia��es */
	private UIData avaliacoes = new HtmlDataTable();
	/** Representa��o na view dos poss�veis avaliadores */
	private UIData avaliadoresPossiveis = new HtmlDataTable();
	/** Representa��o na view dos projetos pass�veis de distribui��o */
	private UIData projetosPossiveis = new HtmlDataTable();
	/** Representa��o na view das distribui��es cadastradas */
	private UIData distribuicoesCadastradas = new HtmlDataTable();

	/** Total de avalia��es realizadas, utilizado como filtro para a busca de projetos a ser distribu�dos. */
	private Integer totalAvaliacoesRealizadas = null;
	/** Quantidade de avaliadores para os quais se deseja destinar os projetos na distribui��o autom�tica */
	private Integer numeroAvaliadoresProjeto = null;


	/**
	 * Construtor padr�o.
	 */
	public DistribuicaoProjetoMBean() {
		obj = new DistribuicaoAvaliacao();
	}

	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		resetBean();
		obj = new DistribuicaoAvaliacao();
		return super.preCadastrar();
	}

	@Override
	public String getDirBase() {     
		return "/projetos/Avaliacoes/Distribuicao";
	}

	/**
	 * Combo para TipoAvaliacao de avalia��es
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li></li>
	 * </ul>
	 */
	public Collection<SelectItem> getAllTipoAvaliadorCombo() {
		return getAll(TipoAvaliador.class, "id", "descricao");
	}

	/**
	 * Lista todas as distribui��es do ativas cadastradas. <br/>
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	  <li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 * 
	 */
	@Override
	public String listar() throws ArqException {
		checkChangeRole();
		return forward(ConstantesNavegacaoProjetos.DISTRIBUIR_PROJETOS_LISTA);
	}

	/**
	 * Remove uma distribui��o j� cadastrada. <br/>
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/projetos/Avaliacoes/Distribuicao/lista.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 * 
	 */
	@Override
	public String inativar() throws ArqException, NegocioException {	
		obj = (DistribuicaoAvaliacao) getDistribuicoesCadastradas().getRowData();
		MovimentoCadastro mov = new MovimentoCadastro(obj, ArqListaComando.DESATIVAR);
		try {
			execute(mov);
			addMensagem(OPERACAO_SUCESSO);
			all = getGenericDAO().findAllAtivos(DistribuicaoAvaliacao.class, "id");
			return forward(ConstantesNavegacaoProjetos.DISTRIBUIR_PROJETOS_LISTA);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return forward(getFormPage());
		} catch (Exception e) {
			tratamentoErroPadrao(e);
			return forward(getFormPage());
		}
	}

	@Override
	public String forwardCadastrar() {
		try {
			all = getGenericDAO().findAllAtivos(DistribuicaoAvaliacao.class, "id");
			return ConstantesNavegacaoProjetos.DISTRIBUIR_PROJETOS_LISTA;
		} catch (DAOException e) {
			tratamentoErroPadrao(e);
			return null;
		}
	}

	/**
	 * Seleciona o a distribui��o e exibe o formul�rio para altera��o da mesma. <br/>
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/projetos/Avaliacoes/Distribuicao/lista.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 * 
	 */
	public String alterar() throws ArqException {
		checkChangeRole();
		obj = (DistribuicaoAvaliacao) getDistribuicoesCadastradas().getRowData();
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), DistribuicaoAvaliacao.class);
		setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
		prepareMovimento(ArqListaComando.ALTERAR);
		setReadOnly(false);
		setConfirmButton("Alterar");
		return forward(ConstantesNavegacaoProjetos.DISTRIBUIR_PROJETOS_FORM);	
	}


	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO, SigaaPapeis.GESTOR_PESQUISA);
		super.checkChangeRole();
	}

	/**
	 * Retorna lista de distribui��es ativas.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 	<li>sigaa.war/projetos/Avaliacoes/Distribuicao/lista.jsp</li>
	 * </ul> 
	 * 
	 * @param evt
	 */
	public List<DistribuicaoAvaliacao> getDistribuicoesAtivas() throws DAOException{
		return getDAO(DistribuicaoProjetoDao.class).findByDistribuicoesAtivas(getTipoEdital());
		
	}

	/**
	 * Utilizado para exibir o question�rio durante o cadastro de uma distribui��o.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 	<li>sigaa.war/projetos/Avaliacoes/Distribuicao/form.jsp</li>
	 * </ul> 
	 * 
	 * @param evt
	 */
	public void changeModeloAvaliacao(ValueChangeEvent evt) {	
		try {
			int id = (Integer) evt.getNewValue();
			ModeloAvaliacao modelo =  getGenericDAO().findByPrimaryKey(id, ModeloAvaliacao.class);
			if (!ValidatorUtil.isEmpty(modelo)) {
				obj.setModeloAvaliacao(modelo);
				obj.getModeloAvaliacao().getQuestionario().getItensAvaliacao().iterator();
			}else {
				obj.setModeloAvaliacao(new ModeloAvaliacao());
			}
		} catch (Exception e) {
			notifyError(e);
		}
	}



	/**
	 * Seleciona a distribui��o e exibe o formul�rio para sele��o de projetos. <br/>
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/projetos/Avaliacoes/Distribuicao/lista.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 * @throws ArqException 
	 * 
	 */
	public String selecionarProjetos() throws SegurancaException, DAOException, NegocioException {
		checkChangeRole();
		obj = (DistribuicaoAvaliacao) getDistribuicoesCadastradas().getRowData();
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), DistribuicaoAvaliacao.class);
		estrategia = DistribuicaoProjetosFactory.getInstance().getEstrategia(obj);
		return forward(estrategia.formularioProjetos());
	}


	/**
	 * Redireciona para p�gina com lista de projetos para serem distribuidos
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/projetos/Avaliacoes/Distribuicao/avaliadores_manual.jsp</li>
	 * </ul>
	 * 
	 */
	public String distribuirNovoProjeto() throws SegurancaException {
		checkChangeRole();
		return forward(estrategia.formularioProjetos());		
	}

	/**
	 * Gera uma nova avalia��o para o projeto com o avaliador selecionado. <br/>
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/projetos/Avaliacoes/Distribuicao/avaliadores.jsp</li>
	 * </ul>
	 * @throws NegocioException 
	 * 
	 */
	public String adicionarAvaliacao() throws DAOException, SegurancaException {
		try {
			checkChangeRole();
			Usuario avaliador = ((AvaliadorProjeto) getAvaliadoresPossiveis().getRowData()).getUsuario();
			estrategia.adicionarAvaliacao(avaliador);	
		}catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
		return null;
	}

	/**
	 * Remove a avalia��o selecionada do projeto. <br/>
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/projetos/Avaliacoes/Distribuicao/avaliadores.jsp</li>
	 * </ul>
	 * @throws NegocioException 
	 * 
	 */
	public String removerAvaliacao() throws DAOException, SegurancaException, NegocioException {
		checkChangeRole();
		Avaliacao avaliacao = (Avaliacao) getAvaliacoes().getRowData();
		estrategia.removerAvaliacao(avaliacao);
		return null;
	}


	/**
	 * Lista dos avaliadores dispon�veis.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/projetos/Avaliacoes/Distribuicao/avaliadores.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 * @throws NegocioException 
	 * 
	 */
	public List<AvaliadorProjeto> getAvaliadoresDisponiveis() throws SegurancaException, DAOException, NegocioException {
		checkChangeRole();
		return estrategia.getAvaliadoresDisponiveis();		
	}

	/**
	 * Lista dos projetos dispon�veis.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/projetos/Avaliacoes/Distribuicao/projetos_manual.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 * @throws NegocioException 
	 * 
	 */
	public List<Projeto> getProjetosDisponiveis() throws SegurancaException, DAOException, NegocioException {
		checkChangeRole();
		estrategia.setTotalAvaliacoesRealizadas(totalAvaliacoesRealizadas);
		return estrategia.getProjetosParaDistribuir();		
	}
	
	/**
	 * Lista dos projetos selecionados.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/projetos/Avaliacoes/Distribuicao/avaliadores_automatica.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 * @throws NegocioException 
	 * 
	 */
	public List<Projeto> getProjetosSelecionados() throws SegurancaException, DAOException, NegocioException {
		checkChangeRole();
		return estrategia.getProjetos();
	}


	/**
	 * Seleciona os projetos de acordo com a estrat�gia adotada e redireciona para p�gina dos avaliadores. <br/>
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/projetos/Avaliacoes/Distribuicao/projetos_manual.jsp</li>
	 * 	<li>sigaa.war/projetos/Avaliacoes/Distribuicao/projetos_automatica.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 * @throws NegocioException 
	 * 
	 */
	public String preDistribuir() throws ArqException, NegocioException {
		checkChangeRole();
		estrategia.setNumeroAvaliadoresProjeto(numeroAvaliadoresProjeto);
		estrategia.selecionarProjetos(getProjetosPossiveis());
		prepareMovimento(estrategia.getComandoDistribuicao());
		return forward(estrategia.formularioAvaliadores());
	}


	/**
	 * Confirma a distribui��o do projeto para os avaliadores. <br/>
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/projetos/Avaliacoes/Distribuicao/avaliadores.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 * @throws DAOException 
	 * 
	 */
	public String distribuir() throws ArqException {
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjAuxiliar(estrategia);
			mov.setCodMovimento(estrategia.getComandoDistribuicao());	
			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			return forward(estrategia.formularioProjetos()); 
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());		    
		} 
		return null;
	}


	
	
	
	/**
	 * Lista todas as distribui��es ativas com avalia��es pendentes de finaliza��o. <br/>
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	  <li>sigaa.war/projetos/menu.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 * 
	 */
	public String listaConsolidarAvaliacoes() throws ArqException {
		checkChangeRole();
		totalAvaliacoesRealizadas = null;
		return forward(ConstantesNavegacaoProjetos.CONSOLIDACAO_AVALIACAO_PROJETOS_LISTA);
	}


	/**
	 * Retorna a lista de distribui��es que ainda tem avalia��es pendentes de finaliza��o.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/projetos/Avaliacoes/Consolidacao/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<DistribuicaoAvaliacao> getDistribuicoesNaoConsolidadas() throws DAOException{
		return getDAO(DistribuicaoProjetoDao.class).findByDistribuicoesAtivas(getTipoEdital());
	}

	/**
	 * Seleciona o a distribui��o e exibe o formul�rio para finaliza��o das avalia��es dos projetos. <br/>
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/projetos/Avaliacoes/Consolidacao/lista.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 * @throws NegocioException 
	 * 
	 */
	public String consolidarAvaliacoes() throws ArqException, NegocioException {
		checkChangeRole();
		prepareMovimento(SigaaListaComando.CONSOLIDAR_AVALIACOES_PROJETO);
		obj = (DistribuicaoAvaliacao) getDistribuicoesCadastradas().getRowData();
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), DistribuicaoAvaliacao.class);
		estrategia = DistribuicaoProjetosFactory.getInstance().getEstrategia(obj);
		setTotalAvaliacoesRealizadas(null); //retorna todas as avalia��es registradas
		return forward(ConstantesNavegacaoProjetos.CONSOLIDACAO_AVALIACAO_PROJETOS_FORM);		
	}

	/**
	 * Lista dos projetos dispon�veis para finaliza��o da avalia��o.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/projetos/Avaliacoes/Distribuicao/projetos.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 * @throws NegocioException 
	 * 
	 */
	public List<Projeto> getProjetosAvaliados() throws SegurancaException, DAOException, NegocioException {
		checkChangeRole();
		estrategia.setTotalAvaliacoesRealizadas(totalAvaliacoesRealizadas);
		return estrategia.getProjetosAvaliados();
	}

	/**
	 * Confirma a finaliza��o das avalia��es do projeto. <br/>
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/projetos/Avaliacoes/Distribuicao/avaliadores.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 * @throws DAOException 
	 * 
	 */
	public String confirmarConsolidarAvaliacoes() throws ArqException {
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			//Finalizar avalia��es de todos os projetos com avalia��es realizadas para a distribui��o informada.
			estrategia.setTotalAvaliacoesRealizadas(null);
			mov.setColObjMovimentado(estrategia.getProjetosAvaliados());  
			mov.setObjAuxiliar(obj);
			mov.setCodMovimento(SigaaListaComando.CONSOLIDAR_AVALIACOES_PROJETO);	//TODO: fazer dependendo do tipo de distribui��o selecionada	
			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			return forward(ConstantesNavegacaoProjetos.CONSOLIDACAO_AVALIACAO_PROJETOS_LISTA); 
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());		    
		} 
		return null;
	}

	/**
	 * Permite a atualiza��o da lista de a��es com avalia��es realizadas.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>M�todo n�o chamado por JSP.</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException 
	 */
	public String atualizar() throws SegurancaException {
		checkChangeRole();
		return null;
	}

	public Projeto getProjeto() {
		return estrategia.getProjeto();
	}

	public EstrategiaDistribuicaoProjetos getEstrategia() {
		return estrategia;
	}

	public void setEstrategia(EstrategiaDistribuicaoProjetos estrategia) {
		this.estrategia = estrategia;
	}

	public UIData getAvaliacoes() {
		return avaliacoes;
	}

	public void setAvaliacoes(UIData avaliacoes) {
		this.avaliacoes = avaliacoes;
	}

	public UIData getAvaliadoresPossiveis() {
		return avaliadoresPossiveis;
	}

	public void setAvaliadoresPossiveis(UIData avaliadoresPossiveis) {
		this.avaliadoresPossiveis = avaliadoresPossiveis;
	}

	public UIData getProjetosPossiveis() {
		return projetosPossiveis;
	}

	public void setProjetosPossiveis(UIData projetosPossiveis) {
		this.projetosPossiveis = projetosPossiveis;
	}

	public UIData getDistribuicoesCadastradas() {
		return distribuicoesCadastradas;
	}

	public void setDistribuicoesCadastradas(UIData distribuicoesCadastradas) {
		this.distribuicoesCadastradas = distribuicoesCadastradas;
	}

	public Integer getTotalAvaliacoesRealizadas() {
		return totalAvaliacoesRealizadas;
	}

	public void setTotalAvaliacoesRealizadas(Integer totalAvaliacoesRealizadas) {
		this.totalAvaliacoesRealizadas = totalAvaliacoesRealizadas;
	}

	public Integer getNumeroAvaliadoresProjeto() {
		return numeroAvaliadoresProjeto;
	}

	public void setNumeroAvaliadoresProjeto(Integer numeroAvaliadoresProjeto) {
		this.numeroAvaliadoresProjeto = numeroAvaliadoresProjeto;
	}

}
