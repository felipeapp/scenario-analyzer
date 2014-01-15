/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 26/01/2009
 *
 */
package br.ufrn.sigaa.ensino.stricto.reuni.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.stricto.reuni.dao.EditalBolsasReuniDao;
import br.ufrn.sigaa.ensino.stricto.reuni.dao.SolicitacaoBolsasReuniDao;
import br.ufrn.sigaa.ensino.stricto.reuni.dominio.EditalBolsasReuni;
import br.ufrn.sigaa.ensino.stricto.reuni.dominio.PlanoTrabalhoReuni;
import br.ufrn.sigaa.ensino.stricto.reuni.dominio.SolicitacaoBolsasReuni;

/**
 * ManagedBean responsável pelo fluxo do cadastro e gerenciamento de solicitação 
 * de bolsas do REUNI. 
 * 
 * @author wendell
 *
 */
@Component("solicitacaoBolsasReuniBean") @Scope("request")
public class SolicitacaoBolsasReuniMBean extends SigaaAbstractController<SolicitacaoBolsasReuni>{
	/** Lista de Planos Reuni */
	private DataModel planosDataModel;
	/** Edital selecionado */
	private int edital;
	
	public SolicitacaoBolsasReuniMBean() {
		clear();
	}

	/**
	 * Instancia um novo obj
	 */
	private void clear() {
		obj = new SolicitacaoBolsasReuni();
		prepararListaPlanos();
	}

	/**
	 * Carregar a lista de planos.  
	 */
	private void prepararListaPlanos() {
		List<PlanoTrabalhoReuni> planos = new ArrayList<PlanoTrabalhoReuni>();
		if (!isEmpty(obj.getPlanos())) {
			planos = CollectionUtils.toList(obj.getPlanos());
		}
		
		planosDataModel = new ListDataModel( planos );
		obj.setPlanos( planos );
	}
	
	/**
	 * Inicia o cadastro ou alteração de uma solicitação
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarCadastro() throws ArqException {
		clear();
		checkRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG);
	
		// Verificar se o programa atual de trabalho está definido
		Unidade programa = getProgramaStricto();
		if (programa == null) {
			addMensagemErro("Ocorreu um problema ao acessar os dados do programa de pós-graduação. " +
					"Por favor, selecione o programa atual de trabalho para esta sessão.");
			return null;
		} else {
			obj.setPrograma(programa);
		}
				
		// Buscar e popular o edital da solicitação
		EditalBolsasReuniDao editalDao = getDAO(EditalBolsasReuniDao.class);
		Collection<EditalBolsasReuni> editais = editalDao.findAbertosSubmissao();
			
		if ( isEmpty(editais) ) {
			addMensagemErro("Não foram encontrados editais abertos para submissão de " +
					" propostas de solicitação de bolsas REUNI de assistência ao ensino. " +
					" Em caso de dúvidas, contacte a Pró-Reitoria de Pós-Graduação.");
			return null;
		}
		
		if (editais.size() > 1) {
			return forward(telaSelecaoEdital());
		}
		obj.setEdital( editais.iterator().next() );

		// Buscar se já existe uma proposta iniciada para o edital aberto
		if ( existeSolicitacaoSubmetida() ) {
			return null;
		}
		
		prepareMovimento(SigaaListaComando.SUBMETER_SOLICITACAO_BOLSAS_REUNI);
		return forward(getFormPage());
	}

	/**
	 * Verifica se já existe uma solicitação de bolsas submetida pelo programa para o edital selecionado
	 * 
	 * @return
	 * @throws DAOException 
	 */
	private boolean existeSolicitacaoSubmetida() throws DAOException {
		SolicitacaoBolsasReuniDao solicitacaoDao = getDAO(SolicitacaoBolsasReuniDao.class);
		SolicitacaoBolsasReuni solicitacao = solicitacaoDao.findByProgramaAndEdital(obj.getPrograma(), obj.getEdital());
		
		if (solicitacao == null) return false;
		
		if ( solicitacao.isSubmetida() ) {
			addMensagemErro("Atenção: sua solicitação de bolsas para o edital corrente já foi submetida!");
			return true;
		} else {
			obj = solicitacao;
			prepararListaPlanos();
			return false;
		}
	}

	/**
	 * Adiciona um plano de trabalho à lista de planos da solicitação
	 * <br/> 
	 * Método não chamado por JSP.
	 * @param planoTrabalho
	 * @return
	 * @throws ArqException 
	 */
	public String adicionarPlanoTrabalho(PlanoTrabalhoReuni planoTrabalho) throws ArqException {
		obj.adicionarPlanoTrabalho(planoTrabalho);
		
		addMensagemInformation("Plano de trabalho adicionado com sucesso!");
		return salvar();
	}

	/**
	 * Atualiza o plano de trabalho alterado pelo usuário na coleção de planos
	 * <br/>
	 * Método não chamado por JSP.
	 * @param plano
	 * @return
	 * @throws ArqException
	 */
	public String alterarPlanoTrabalho(PlanoTrabalhoReuni plano) throws ArqException {
		List<PlanoTrabalhoReuni> listaPlanos = (List<PlanoTrabalhoReuni>) obj.getPlanos(); 
		listaPlanos.set(listaPlanos.indexOf(plano), plano);
		plano.setSolicitacao(obj);

		addMensagemInformation("Plano de trabalho alterado com sucesso!");
		return salvar();
	}

	
	/**
	 * Remove um plano de trabalho da lista de planos da solicitação de Bolsa REUNI.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/solicitacao_bolsas_reuni/form_solicitacao.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String removerPlanoTrabalho() throws ArqException {
		@SuppressWarnings("unchecked")
		List<PlanoTrabalhoReuni> planos = (List<PlanoTrabalhoReuni>) planosDataModel.getWrappedData();
								
		// remove a indicação
		getGenericDAO().remove(planos.get(planosDataModel.getRowIndex()));					
		planos.remove( planosDataModel.getRowIndex() );		
		
		addMensagemInformation("Plano de trabalho excluído com sucesso!");
		return forward(getFormPage());
	}
	
	/**
	 * Submete a solicitação de bolsas e redireciona para o comprovante de solicitação de bolsa reune.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/solicitacao_bolsas_reuni/form_solicitacao.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String submeterProposta() throws ArqException {
		MovimentoCadastro mov = new MovimentoCadastro(obj, SigaaListaComando.SUBMETER_SOLICITACAO_BOLSAS_REUNI);
		
		try {
			execute(mov);
			addMensagemInformation("Solicitação submetida com sucesso!");
		} catch (NegocioException ne ) {
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		setReadOnly(true);
		return view();

	}

	/**
	 * Salva os dados da solicitação, mantendo-a aberta para alterações.
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/stricto/solicitacao_bolsas_reuni/form_solicitacao.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String salvar() throws ArqException {
		if (isPortalPpg()){
			apenasSalvar(SigaaListaComando.SUBMETER_SOLICITACAO_BOLSAS_REUNI);
			return forward(getViewPage());
		} else {
			apenasSalvar(SigaaListaComando.SALVAR_SOLICITACAO_BOLSAS_REUNI);
			return forward(getFormPage());
		}
	}
	
	/**
	 * Salva o objeto corrente.
	 * @throws ArqException
	 */
	private void apenasSalvar(Comando comando) throws ArqException{
		MovimentoCadastro mov = new MovimentoCadastro(obj, comando);
		prepareMovimento(mov.getCodMovimento());
		
		try {
			obj = (SolicitacaoBolsasReuni) execute(mov);
			prepararListaPlanos();
			prepareMovimento(SigaaListaComando.SUBMETER_SOLICITACAO_BOLSAS_REUNI);
		} catch (NegocioException ne ) {
			addMensagens(ne.getListaMensagens());
		}		
	}
	
	/**
	 * Muda o status da Solicitação - (Apenas para PPG)
	 * <br /> 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/stricto/solicitacao_bolsas_reuni/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String liberarSubmeter() throws ArqException, NegocioException{
		obj.setStatus((obj.getStatus() == SolicitacaoBolsasReuni.CADASTRADA ? SolicitacaoBolsasReuni.SUBMETIDA : SolicitacaoBolsasReuni.CADASTRADA));	
		apenasSalvar(SigaaListaComando.ALTERAR_STATUS_SOLICITACAO_BOLSAS_REUNI);
		return forward(getListPage());		
	}		

	/**
	 * Redireciona o usuário para a página de visualização dos dados da solicitação
	 * <br/><br/>
	 * Método Chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/stricto/solicitacao_bolsas_reuni/lista.jsp</li>
	 *   <li>/sigaa.war/stricto/solicitacao_bolsas_reuni/form_solicitacao.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String view() throws DAOException {
		populateObj();
		return forward(getViewPage());
	}
	
	/**
	 * 
	 * Utilizado para visualizar um arquivo anexado ao Edital.
	 * 
	 * Chamado por:
	 * sigaa.war/stricto/edital_bolsas_reuni/lista.jsp 
	 * 
	 * @return
	 */
	public String viewArquivo() {
		try {
			Integer idArquivo = obj.getEdital().getIdArquivoEdital();
			EnvioArquivoHelper.recuperaArquivo(getCurrentResponse(), idArquivo, false);
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Arquivo não encontrado!");
			return null;
		}
		FacesContext.getCurrentInstance().responseComplete();
		return null;
	}
	
	/**
	 * Seleciona um Edital informado.
	 * <br /> 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/stricto/solicitacao_bolsas_reuni/form_selecao_edital.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String selecionarEdital() throws ArqException{
		if (edital <= 0) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Edital");
			return null;
		}
		SolicitacaoBolsasReuniDao dao = getDAO(SolicitacaoBolsasReuniDao.class);
		obj.setEdital(dao.findByPrimaryKey(edital, EditalBolsasReuni.class));

		// Buscar se já existe uma proposta iniciada para o edital aberto
		if ( existeSolicitacaoSubmetida() ) {
			return null;
		}
		
		prepareMovimento(SigaaListaComando.SUBMETER_SOLICITACAO_BOLSAS_REUNI);	
		
		return forward(getFormPage());		
	}

	/**
	 * Realizar uma buscar nas solicitações de Bolsa Reuni.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/solicitacao_bolsas_reuni/consulta_solicitacoes.jsp</li>
	 * </ul>
	 */
	@Override
	public String buscar() throws Exception {
		if (edital <= 0) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Edital");
			return null;
		}
		SolicitacaoBolsasReuniDao dao = getDAO(SolicitacaoBolsasReuniDao.class);
		obj.setEdital(dao.findByPrimaryKey(edital, EditalBolsasReuni.class));
		if (isPortalCoordenadorStricto())
			all = dao.findAllByProgramaAndEdital(getProgramaStricto(), obj.getEdital());
		else
			all = dao.findByEdital(obj.getEdital());
		if (all.size() == 0) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		return forward(getListPage());
	}
	
	@Override
	public Collection<SolicitacaoBolsasReuni> getAll() throws ArqException {
		if (all != null) return all;
		
		SolicitacaoBolsasReuniDao dao = getDAO(SolicitacaoBolsasReuniDao.class);
		if ( isUserInRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS) && isPortalCoordenadorStricto() && getProgramaStricto() != null) {
			all = dao.findByPrograma(getProgramaStricto());
		} else if ( isUserInRole(SigaaPapeis.PPG) && isPortalPpg() ) {
			all = super.getAll();
		}
		
		return all;
	}
	
	/**
	 * Método que encaminha o usuário à listagem do caso de uso.
	 * <br /> 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/stricto/menu_coordenador.jsp</li>
	 *   <li>/stricto/menus/bolsas_reuni.jsp</li>
	 * </ul>
	 */
	@Override
	public String listar() throws ArqException {
		return forward(getListPage()); 
	}

	/**
	 * Lista as solicitações
	 * <br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/stricto/menus/bolsas_reuni.jsp</li>
	 *   <li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String listBuscar() throws ArqException {
		checkRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG, SigaaPapeis.SECRETARIA_POS);
		if( hasErrors() ) return null;
		return forward("/stricto/solicitacao_bolsas_reuni/consulta_solicitacoes.jsf"); 
	}
	
	@Override
	public String getFormPage() {
		return "/stricto/solicitacao_bolsas_reuni/form_solicitacao.jsf";
	}

	@Override
	public String getViewPage() {
		return "/stricto/solicitacao_bolsas_reuni/view_solicitacao.jsf";
	}
	
	@Override
	public String getListPage() {
		if ( isUserInRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS) && isPortalCoordenadorStricto() ) {
			return "/stricto/solicitacao_bolsas_reuni/lista_coordenacao.jsf";
		} else if (isUserInRole(SigaaPapeis.PPG) && isPortalPpg()){
			return "/stricto/solicitacao_bolsas_reuni/lista.jsf";
		} else {
			addMensagemErro("Você não possui permissão para consultas solicitações de bolsas.");
			return null;
		}
	}
	
	/**
	 * Fomulário de seleção do edital
	 * @return
	 */
	private String telaSelecaoEdital() {
		return "/stricto/solicitacao_bolsas_reuni/form_selecao_edital.jsf";
	}

	public DataModel getPlanosDataModel() {
		return planosDataModel;
	}

	public void setPlanosDataModel(DataModel planosDataModel) {
		this.planosDataModel = planosDataModel;
	}

	public int getEdital() {
		return edital;
	}

	public void setEdital(int edital) {
		this.edital = edital;
	}

}