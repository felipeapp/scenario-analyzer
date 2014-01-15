/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/07/2008
 *
 */
package br.ufrn.sigaa.vestibular.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConstantesErro;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.comum.gru.dominio.CodigoRecolhimentoGRU;
import br.ufrn.comum.gru.dominio.ConfiguracaoGRU;
import br.ufrn.comum.gru.dominio.GrupoEmissaoGRU;
import br.ufrn.comum.gru.dominio.TipoArrecadacao;
import br.ufrn.comum.gru.negocio.ConfiguracaoGRUFactory;
import br.ufrn.comum.gru.negocio.GuiaRecolhimentoUniaoHelper;
import br.ufrn.sigaa.arq.dao.questionario.QuestionarioDao;
import br.ufrn.sigaa.arq.dao.vestibular.ProcessoSeletivoVestibularDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.graduacao.negocio.vestibular.EstrategiaConvocacaoCandidatosVestibular;
import br.ufrn.sigaa.ensino.graduacao.negocio.vestibular.EstrategiaConvocacaoCandidatosVestibular2013;
import br.ufrn.sigaa.questionario.dominio.Questionario;
import br.ufrn.sigaa.questionario.dominio.TipoQuestionario;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;
import br.ufrn.sigaa.vestibular.negocio.MovimentoProcessoSeletivoVestibular;

/** Controller respons�vel pelas opera��es sobre Processos Seletivos.
 * @author �dipo Elder F. Melo
 *
 */
@Component("processoSeletivoVestibular")
@Scope("request")
public class ProcessoSeletivoVestibularMBean extends SigaaAbstractController<ProcessoSeletivoVestibular> {

	/** Lista de question�rios dispon�veis para uso. */
	private ArrayList<SelectItem> possiveisQuestionarios;
	
	/** Arquivo do Edital. */
	private UploadedFile edital;
	/**Arquivo do Manual do Candidato. */
	private UploadedFile manualCandidato;
	/** Configura��o da GRU para recolhimento da taxa de inscri��o. */
	private ConfiguracaoGRU configuracaoGRU;
	/** Lista de SelectItens com os poss�veis c�digos de recolhimento. */
	private ArrayList<SelectItem> codigosRecolhimento;
	/** Tipo de Arrecada��o da GRU. */
	private int idTipoArrecadacao;
	/** Lista de processos seletivos internos � institui��o. */
	private Collection<ProcessoSeletivoVestibular> allInterno;
	/** Lista de processos seletivos externos � institui��o. */
	private Collection<ProcessoSeletivoVestibular> allExterno;
	/** Lista SelecItens de processos seletivos internos � institui��o. */
	private Collection<SelectItem> allInternoCombo;
	/** Lista SelecItens de processos seletivos externos � institui��o. */
	private Collection<SelectItem> allExternoCombo;

	/** Construtor padr�o. */
	public ProcessoSeletivoVestibularMBean() {
		this.obj = new ProcessoSeletivoVestibular();
	}

	/**
	 * Cadastra / atualiza o processo seletivo.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/ProcessoSeletivoVestibular/form.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		erros.addAll(obj.validate());
		if (hasErrors())
			return null;
		if (getConfirmButton().equalsIgnoreCase("remover")) {
			return remover();
		} 
		beforeCadastrarAfterValidate();
		prepareMovimento(SigaaListaComando.CADASTRAR_PROCESSO_SELETIVO_VESTIBULAR);
		MovimentoProcessoSeletivoVestibular movimento = new MovimentoProcessoSeletivoVestibular();
		movimento.setProcessoSeletivo(obj);
		movimento.setEdital(edital);
		movimento.setManualCandidato(manualCandidato);
		movimento.setCodMovimento(SigaaListaComando.CADASTRAR_PROCESSO_SELETIVO_VESTIBULAR);
		try {
			execute(movimento);
		} catch (NegocioException e) {
			addMensagemErroPadrao();
			e.printStackTrace();
			return null;
		}
		if (getConfirmButton().equalsIgnoreCase("alterar")){
			addMensagemInformation("Processo Seletivo alterado com sucesso!");
			return forward(getListPage());
		} else {
			addMensagemInformation("Processo Seletivo cadastrado com sucesso!");
			return cancelar();
		}
	}
	
	/**
	 * M�todo Utilizado para carregar o Processo Seletivo do Vestibular selecionado pelo discente
	 * para a realiza��o das opera��es desejadas.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/ProcessoSeletivoVestibular/form.jsp</li>
	 * </ul>
	 */
	@Override
	public String atualizar() throws ArqException {

		try {
			setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
			prepareMovimento(ArqListaComando.ALTERAR);
	
			setId();
			setReadOnly(false);
			setObj( getGenericDAO().findByPrimaryKey(obj.getId(), ProcessoSeletivoVestibular.class) );
			if (obj.getIdConfiguracaoGRU() != null)
				configuracaoGRU = ConfiguracaoGRUFactory.getInstance().getConfiguracaoGRUById(obj.getIdConfiguracaoGRU());
			if (configuracaoGRU == null) {
				configuracaoGRU = new ConfiguracaoGRU();
			} else {
				idTipoArrecadacao = configuracaoGRU.getTipoArrecadacao().getId(); 
			}
			// setando valores n�o nulos para cadstros antigos.
			if (configuracaoGRU.getGrupoEmissaoGRU() == null)
				configuracaoGRU.setGrupoEmissaoGRU(new GrupoEmissaoGRU());
			if (configuracaoGRU.getTipoArrecadacao().getCodigoRecolhimento() == null)
				configuracaoGRU.getTipoArrecadacao().setCodigoRecolhimento(new CodigoRecolhimentoGRU());
			if (configuracaoGRU.getTipoArrecadacao() == null)
				configuracaoGRU.setTipoArrecadacao(new TipoArrecadacao());
			obj.setConfiguracaoGRU(configuracaoGRU);
			setConfirmButton("Alterar");
			afterAtualizar();
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
		}
		
		return forward(getFormPage());
	}
	
	/** Evita que ocorra um NullPointerException ao setar o question�rio.
	 * <br/>M�todo n�o invocado por JSP�s
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#afterAtualizar()
	 */
	@Override
	public void afterAtualizar() throws ArqException {
		if (obj.getQuestionario() == null){
			obj.setQuestionario(new Questionario());
		}
		if (obj.getFormaIngresso() == null) {
			obj.setFormaIngresso(new FormaIngresso());
		}
	}
	
	/** Retorna uma cole��o de processos seletivos ativos.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllAtivoCombo() throws DAOException {
		return getAllAtivo(ProcessoSeletivoVestibular.class, "id", "nome");
	}

	/** Retorna uma cole��o de processos seletivos que est�o no per�odo de inscri��o de candidatos ao vestibular.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllPeriodoInscricaoCandidatoCombo()
			throws DAOException {
		Collection<SelectItem> lista = new ArrayList<SelectItem>();
		ProcessoSeletivoVestibularDao dao = getDAO(ProcessoSeletivoVestibularDao.class);
		for (ProcessoSeletivoVestibular vest : dao
				.findByInscricaoCandidatoAtivo()) {
			lista.add(new SelectItem(vest.getId() + "", vest.getNome()));
		}
		return lista;
	}

	/** Retorna uma cole��o de processos seletivos que est�o no per�odo de inscri��o de fiscais.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllPeriodoInscricaoFiscalCombo()
			throws DAOException {
		ProcessoSeletivoVestibularDao dao = getDAO(ProcessoSeletivoVestibularDao.class);
		return toSelectItems(dao.findInscricaoFiscalAtivo(), "id", "nome");
	}

	/** Retorna uma cole��o de processos seletivos.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAll()
	 */
	@Override
	public Collection<ProcessoSeletivoVestibular> getAll() throws ArqException {
		if (all == null) {
			ProcessoSeletivoVestibularDao dao = getDAO(ProcessoSeletivoVestibularDao.class);
			all = dao.findAllOrderByAnoPeriodo();
		}
		return all;
	}
	
	/** Retorna uma cole��o de processos seletivos internos.
	 * @return
	 * @throws ArqException
	 */
	public Collection<ProcessoSeletivoVestibular> getAllInternos() throws ArqException {
		if (allInterno == null) {
			allInterno = new ArrayList<ProcessoSeletivoVestibular>();
			for (ProcessoSeletivoVestibular ps : getAll())
				if (!ps.isProcessoExterno())
					allInterno.add(ps);
		}
		return allInterno;
	}
	
	/** Retorna uma cole��o de processos seletivos internos.
	 * @return
	 * @throws ArqException
	 */
	public Collection<SelectItem> getAllInternosCombo() throws ArqException {
		if (allInternoCombo == null) {
			allInternoCombo = new ArrayList<SelectItem>();
			for (ProcessoSeletivoVestibular ps : getAll())
				if (!ps.isProcessoExterno())
					allInternoCombo.add(new SelectItem(ps.getId(), ps.getNome()));
		}
		return allInternoCombo;
	}
	
	/** Retorna uma cole��o de processos seletivos internos.
	 * @return
	 * @throws ArqException
	 */
	public Collection<ProcessoSeletivoVestibular> getAllExternos() throws ArqException {
		if (allExterno == null) {
			allExterno = new ArrayList<ProcessoSeletivoVestibular>();
			for (ProcessoSeletivoVestibular ps : getAll())
				if (ps.isProcessoExterno())
					allExterno.add(ps);
		}
		return allExterno;
	}
	
	/** Retorna uma cole��o de processos seletivos internos.
	 * @return
	 * @throws ArqException
	 */
	public Collection<SelectItem> getAllExternosCombo() throws ArqException {
		if (allExternoCombo == null) {
			allExternoCombo = new ArrayList<SelectItem>();
			for (ProcessoSeletivoVestibular ps : getAll())
				if (ps.isProcessoExterno() && ps.isAtivo())
					allExternoCombo.add(new SelectItem(ps.getId(), ps.getNome()));
		}
		return allExternoCombo;
	}

	/**
	 * Retorna o link para a listagem de processos seletivos.<br>
	 * M�todo n�o invocado por JSP�s.
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		return "/vestibular/ProcessoSeletivoVestibular/lista.jsf";
	}
	
	/**
	 * Prepara para a opera��o de cadastro.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/menus/cadastros.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preCadastrar()
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		this.obj = new ProcessoSeletivoVestibular();
		this.configuracaoGRU = new ConfiguracaoGRU();
		// setando valores n�o nulos 
		this.configuracaoGRU.setGrupoEmissaoGRU(new GrupoEmissaoGRU());
		this.configuracaoGRU.getTipoArrecadacao().setCodigoRecolhimento(new CodigoRecolhimentoGRU());
		this.configuracaoGRU.setTipoArrecadacao(new TipoArrecadacao());
		carregarQuestionarios();
		return super.preCadastrar();
	}
	
	/** Define a taxa de inscri��o e data de vencimento baseado na configura��o da GRU escolhida.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/ProcessoSeletivoVestibular/form.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 */
	public void atualizaConfiguracaoGRU() throws DAOException {
		configuracaoGRU = GuiaRecolhimentoUniaoHelper.getConfiguracaoGRUByTipoArrecadacao(idTipoArrecadacao, null);
		obj.setConfiguracaoGRU(configuracaoGRU);
	}
	
	/** Carrega a lista de question�rios a serem utilizados na inscri��o.
	 * @throws DAOException
	 */
	private void carregarQuestionarios() throws DAOException {
		possiveisQuestionarios = new ArrayList<SelectItem>();
		possiveisQuestionarios.add(new SelectItem(0, "N�O APLICAR QUESTION�RIOS ESPEC�FICOS"));
		Collection<Questionario> questionarios = getDAO(QuestionarioDao.class).findByTipo(TipoQuestionario.QUESTIONARIO_VESTIBULAR);
		if ( !isEmpty(questionarios) ) {
			possiveisQuestionarios.addAll( toSelectItems(questionarios, "id", "titulo") );
		}
	}

	/**
	 * Retorna uma lista de SelectItem de poss�veis question�rios a serem
	 * utilizados durante a inscri��o de candidatos ao vestibular.
	 * 
	 * @return
	 */
	public ArrayList<SelectItem> getPossiveisQuestionarios() {
		if (possiveisQuestionarios == null)
			try {
				carregarQuestionarios();
			} catch (DAOException e) {
				e.printStackTrace();
			}
		return possiveisQuestionarios;
	}
	
	/**
	 * Retorna uma lista de Sc�digos de recolhimento da GRU.
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public ArrayList<SelectItem> getCodigosRecolhimento() throws DAOException {
		if (codigosRecolhimento == null) {
			codigosRecolhimento = new ArrayList<SelectItem>();
			for (CodigoRecolhimentoGRU codigo: GuiaRecolhimentoUniaoHelper.getAllCodigoRecolhimentoGRU()){
				codigosRecolhimento.add(new SelectItem(codigo.getId(), codigo.getCodigo() + " ("+codigo.getDescricao()+")"));
			}
		}
		return codigosRecolhimento;
	}

	/**
	 * Seta uma lista de SelectItem de poss�veis question�rios a serem
	 * utilizados durante a inscri��o de candidatos ao vestibular.
	 * 
	 * @param possiveisQuestionarios
	 */
	public void setPossiveisQuestionarios(
			ArrayList<SelectItem> possiveisQuestionarios) {
		this.possiveisQuestionarios = possiveisQuestionarios;
	}
	
	/** Seta para nulos, os objetos com ID = 0.
	 * <br/>M�todo n�o invocado por JSP�s
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#beforeCadastrarAfterValidate()
	 */
	@Override
	public void beforeCadastrarAfterValidate() throws NegocioException,	SegurancaException, DAOException {
		setaNulos();
	}

	/** Seta atributos para nulo quando id � igual a 0 (zero).
	 * 
	 */
	private void setaNulos() {
		// seta null quando n�o h� question�rio escolhido
		if (this.obj.getQuestionario() != null && this.obj.getQuestionario().getId() == 0) {
			this.obj.setQuestionario(null);
		}
	}	
	
	/** M�todo invocado ap�s remover um processo seletivo.
	 * <br/>M�todo n�o invocado por JSP�s
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#beforeRemover()
	 */
	@Override
	public void beforeRemover() throws DAOException {
		setaNulos();
		super.beforeRemover();
	}
	
	/** M�todo invocado ap�s cadastrar um processo seletivo.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#afterCadastrar()
	 */
	@Override
	protected void afterCadastrar() throws ArqException {
		this.obj = new ProcessoSeletivoVestibular();
	}

	/**
	 * Prepara para opera��o de remo��o de um processo seletivo.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/ProcessoSeletivoVestibular/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String prepareRemover() throws ArqException {
		prepareMovimento(ArqListaComando.REMOVER);
		GenericDAO dao = getGenericDAO();
		this.obj = new ProcessoSeletivoVestibular();
		setId();
		this.obj = dao.findByPrimaryKey(obj.getId(), ProcessoSeletivoVestibular.class);
		if (this.obj == null)
			throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA,"");

		if (this.obj.getQuestionario() == null)
			this.obj.setQuestionario(new Questionario());
		if (this.obj.getIdConfiguracaoGRU() != null)
			configuracaoGRU = ConfiguracaoGRUFactory.getInstance().getConfiguracaoGRUById(this.obj.getIdConfiguracaoGRU());
		if (configuracaoGRU == null) {
			configuracaoGRU = new ConfiguracaoGRU();
		} else {
			idTipoArrecadacao = configuracaoGRU.getTipoArrecadacao().getId(); 
		}
		// setando valores n�o nulos para cadstros antigos.
		if (configuracaoGRU.getGrupoEmissaoGRU() == null)
			configuracaoGRU.setGrupoEmissaoGRU(new GrupoEmissaoGRU());
		if (configuracaoGRU.getTipoArrecadacao().getCodigoRecolhimento() == null)
			configuracaoGRU.getTipoArrecadacao().setCodigoRecolhimento(new CodigoRecolhimentoGRU());
		if (configuracaoGRU.getTipoArrecadacao() == null)
			configuracaoGRU.setTipoArrecadacao(new TipoArrecadacao());
		this.obj.setConfiguracaoGRU(configuracaoGRU);
		
		setReadOnly(true);
		setConfirmButton("Remover");
		carregarQuestionarios();
		return forward(getFormPage());
	}
	
	/**
	 * Retorna a p�gina p�blica de informa��es sobre o processo seletivo.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/processo_seletivo/lista_vestibular.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String viewPublico() throws DAOException{
		int id = Integer.parseInt(getCurrentRequest().getParameter("id"));
		obj = getGenericDAO().findByPrimaryKey(id, ProcessoSeletivoVestibular.class);
		return forward("/public/vestibular/view.jsp");
	}
	
	/** Retorna o arquivo do Edital. 
	 * @return 
	 */
	public UploadedFile getEdital() {
		return edital;
	}

	/** Seta o arquivo do Edital. 
	 * @param edital 
	 */
	public void setEdital(UploadedFile edital) {
		this.edital = edital;
	}

	/** Retorna o arquivo do Manual do Candidato.
	 * @return 
	 */
	public UploadedFile getManualCandidato() {
		return manualCandidato;
	}

	/** Seta o arquivo do Manual do Candidato.
	 * @param manualCandidato 
	 */
	public void setManualCandidato(UploadedFile manualCandidato) {
		this.manualCandidato = manualCandidato;
	}
	
	public ConfiguracaoGRU getConfiguracaoGRU() {
		return configuracaoGRU;
	}

	public void setConfiguracaoGRU(ConfiguracaoGRU configuracaoGRU) {
		this.configuracaoGRU = configuracaoGRU;
	}

	public int getIdTipoArrecadacao() {
		return idTipoArrecadacao;
	}

	public void setIdTipoArrecadacao(int idTipoArrecadacao) {
		this.idTipoArrecadacao = idTipoArrecadacao;
	}
	
	/** Retorna uma cole��o de selectItens de estrat�gias de convoca��o de candidatos aprovados que um processo seletivo pode implementar.
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@SuppressWarnings("rawtypes")
	public Collection<SelectItem> getEstrategiasConvocacaoCombo() throws InstantiationException, IllegalAccessException {
		Collection<SelectItem> estrategias = new LinkedList<SelectItem>();
		Class[] classes = {EstrategiaConvocacaoCandidatosVestibular2013.class};
		for (Class classe : classes) {
			EstrategiaConvocacaoCandidatosVestibular estrategia = (EstrategiaConvocacaoCandidatosVestibular) classe.newInstance(); 
			estrategias.add(new SelectItem(classe.getName(), estrategia.getDescricaoCurta()));
		}
		return estrategias;
	}
}
