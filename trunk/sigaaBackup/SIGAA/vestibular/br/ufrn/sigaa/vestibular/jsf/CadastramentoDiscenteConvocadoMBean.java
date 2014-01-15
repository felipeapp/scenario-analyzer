/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 06/11/2012
 *
 */
package br.ufrn.sigaa.vestibular.jsf;

import static br.ufrn.arq.util.JasperReportsUtil.getReportSIGAA;
import static br.ufrn.arq.util.ValidatorUtil.isAllEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateCPF_CNPJ;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.graduacao.CadastramentoDiscenteDao;
import br.ufrn.sigaa.arq.dao.vestibular.ConvocacaoProcessoSeletivoDao;
import br.ufrn.sigaa.arq.dao.vestibular.DocumentosDiscentesConvocadosDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dao.PlanoMatriculaIngressantesDao;
import br.ufrn.sigaa.ensino.dominio.PlanoMatriculaIngressantes;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.jsf.AtestadoMatriculaMBean;
import br.ufrn.sigaa.ensino.jsf.PlanoMatriculaIngressantesMBean;
import br.ufrn.sigaa.jsf.DadosPessoaisMBean;
import br.ufrn.sigaa.jsf.OperacaoDadosPessoais;
import br.ufrn.sigaa.jsf.OperadorDadosPessoais;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivo;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivoDiscente;
import br.ufrn.sigaa.vestibular.dominio.LinhaAuxiliar;
import br.ufrn.sigaa.vestibular.dominio.LinhaImpressaoDocumentosConvocados;
import br.ufrn.sigaa.vestibular.dominio.PessoaVestibular;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;

/**
 * Controller respons�vel pelo cadastramento de discentes convocados em um Processo Seletivo.
 * 
 * @author �dipo Elder F. de Melo
 *
 */
@Component("cadastramentoDiscenteConvocadoMBean")
@Scope("session")
public class CadastramentoDiscenteConvocadoMBean extends SigaaAbstractController<ConvocacaoProcessoSeletivoDiscente> implements OperadorDadosPessoais {
	
	/** Restringe a busca discentes pendentes de cadastro por CPF. */
	private boolean buscaCPF;
	/** Restringe a busca discentes pendentes de cadastro por nome. */
	private boolean buscaNome;
	/** Restringe a busca discentes pendentes de cadastro por convoca��o. */
	private ConvocacaoProcessoSeletivo convocacaoProcessoSeletivo;
	/** Plano de Matr�cula o qual o discente ser� matriculado. */
	private PlanoMatriculaIngressantes planoMatriculaIngressante;
	/** Cole��o de {@link SelectItem} de convoca��es de um determinado Processo Seletivo. */
	private Collection<SelectItem> convocacaoProcessoSeletivoCombo;
	/** Cole��o de {@link StatusDiscente} que o discente poder� ter ao final do processo. */
	private Collection<SelectItem> statusDiscentesCombo;
	/** Cole��o de {@link PlanoMatriculaIngressantes} com as turmas em que o discente poder� ser matriculado. */
	private Collection<SelectItem> planoMatriculaIngressanteCombo;
	/** Outros v�nculos de discentes ativos que o candidato pode ter. */
	private Collection<DiscenteGraduacao> outrosVinculos;
	/** Indica se o discente possui matr�cula em componentes curriculares no ano-per�odo de ingresso.*/
	private boolean matriculado;
	
	/** Construtor padr�o. */
	public CadastramentoDiscenteConvocadoMBean() {
	}
	
	/** Inicializa os atributos necess�rios para o cadastramento.
	 * @throws ArqException
	 */
	private void init() throws ArqException {
		obj = new ConvocacaoProcessoSeletivoDiscente();
		resultadosBusca = null;
		buscaCPF = false;
		buscaNome = false;
		if (convocacaoProcessoSeletivoCombo == null)
			convocacaoProcessoSeletivoCombo = new LinkedList<SelectItem>();
		if (convocacaoProcessoSeletivo == null) {
			convocacaoProcessoSeletivo = new ConvocacaoProcessoSeletivo();
			convocacaoProcessoSeletivo.setProcessoSeletivo(new ProcessoSeletivoVestibular());
		}
		statusDiscentesCombo = null;
		planoMatriculaIngressante = new PlanoMatriculaIngressantes();
		matriculado = false;
	}
	
	/** Inicia o cadastramento do discente.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menus/aluno.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCadastramento() throws ArqException {
		checkChangeRole();
		init();
		
		if (!getCalendarioVigente().isPeriodoCadastramentoDiscente()) {
			
			if ( isAllEmpty(getCalendarioVigente().getInicioCadastramentoDiscente(), getCalendarioVigente().getFimCadastramentoDiscente()) ) {
				addMensagemErro("O per�odo de cadastramento de discente n�o foi definido no calend�rio acad�mico.");
				return null;
			}
			
			addMensagemErro("O per�odo de cadastramento de discente � de " + Formatador.getInstance().formatarData(getCalendarioVigente().getInicioCadastramentoDiscente()) + " at� " + Formatador.getInstance().formatarData((getCalendarioVigente().getFimCadastramentoDiscente())));
			return null;
		}
		
		return formSelecionaConvocado();
	}
	
	/** Busca por convoca��es de discentes por CPF ou nome.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/CadastramentoDiscentes/seleciona_convocado.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#buscar()
	 */
	public String buscar() throws HibernateException, DAOException, SegurancaException {
		checkChangeRole();
		validateRequired(convocacaoProcessoSeletivo.getProcessoSeletivo(), "Processo Seletivo", erros);
		validateRequired(convocacaoProcessoSeletivo, "Convoca��o", erros);
		CadastramentoDiscenteDao dao = getDAO(CadastramentoDiscenteDao.class);
		PessoaVestibular pessoa = obj.getInscricaoVestibular().getPessoa();
		String nome = null;
		Long cpf = null;
		if (buscaCPF) {
			cpf = pessoa.getCpf_cnpj();
			validateCPF_CNPJ(cpf, "CPF", erros);
		}
		if (buscaNome) {
			nome = pessoa.getNome();
			validateRequired(nome, "Nome", erros);
		}
		if (isEmpty(nome) && isEmpty(cpf))
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
		if (hasErrors())
			return null;
		resultadosBusca = dao.findConvocacaoDiscenteByNomeCPF(nome, cpf, convocacaoProcessoSeletivo.getId());
		if (isEmpty(resultadosBusca))
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		return forward("/vestibular/CadastramentoDiscentes/seleciona_convocado.jsp");
	}

	/** Seleciona uma convoca��o para cadastrar o discente.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/CadastramentoDiscentes/seleciona_convocado.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String selecionarCadastro() throws ArqException {
		checkChangeRole();
		obj = new ConvocacaoProcessoSeletivoDiscente();
		populateObj(true);
		if (isEmpty(obj) || isEmpty(obj.getDiscente())) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return null;
		}
		if (obj.getDiscente().getStatus() != StatusDiscente.PENDENTE_CADASTRO) {
			addMensagemErro("O discente j� foi cadastrado anteriormente.");
			return null;
		} else if (obj.getDentroNumeroVagas() != null) {
			if (obj.getDentroNumeroVagas()) 
				obj.getDiscente().setStatus(StatusDiscente.CADASTRADO);
			else 
				obj.getDiscente().setStatus(StatusDiscente.PRE_CADASTRADO);
		}
		carregaPlanosMatricula();
		statusDiscentesCombo = null;
		outrosVinculos = buscaOutrosVinculosAtivos(obj.getDiscente());
		prepareMovimento(SigaaListaComando.CADASTRAR_DISCENTE_CONVOCADO);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_DISCENTE_CONVOCADO.getId());
		return formDadosPessoais();
	}
	
	@Override
	public void setDadosPessoais(Pessoa pessoa) {
		obj.getDiscente().setPessoa(pessoa);
	}

	/** Seleciona uma convoca��o para cadastrar o discente.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/CadastramentoDiscentes/dados_pessoais.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	@Override
	public String submeterDadosPessoais()  {
		if (!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_DISCENTE_CONVOCADO.getId()))
			return cancelar();
		return formStatusMatricula();
	}
	
	/** Carrega os planos de matr�cula para o discente selecionado.
	 * @throws DAOException
	 */
	private void carregaPlanosMatricula() throws DAOException {
		planoMatriculaIngressanteCombo = new LinkedList<SelectItem>();
		planoMatriculaIngressante = new PlanoMatriculaIngressantes();
		// se o discente n�o estiver dentro do n�mero de vagas, n�o exibir plano de matr�cula
		if (!isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE) && obj.getDentroNumeroVagas() != null && !obj.getDentroNumeroVagas())
			return;
		PlanoMatriculaIngressantesDao dao = getDAO(PlanoMatriculaIngressantesDao.class);
		DiscenteGraduacao discente = obj.getDiscente();
		Collection<PlanoMatriculaIngressantes> planos = dao.findByMatrizCurricular(discente.getMatrizCurricular().getId(),
				discente.getAnoIngresso(), discente.getPeriodoIngresso());
		if (!isEmpty(planos)) {
			for (PlanoMatriculaIngressantes plano : planos) {
				SelectItem selectItem = new SelectItem(plano.getId(), 
						plano.getMatrizCurricular().getDescricao() + " - Plano " + plano.getDescricao()
						+ " (" + plano.getAno() + "." + plano.getPeriodo() +" / "
						+ plano.getVagas() + " vagas)");
				selectItem.setDisabled(!plano.hasVagas());
				planoMatriculaIngressanteCombo.add(selectItem);
				// seleciona automaticamente o primeiro plano que possui vagas. {
				if (plano.hasVagas() && planoMatriculaIngressante.getId() == 0) {
					planoMatriculaIngressante = plano;
					plano.getTurmas().iterator();
				}
			}
		}
	}
	
	/** Busca os dados do plao de matr�cula selecioando pelo usu�rio<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/CadastramentoDiscentes/status_matricula.jsp</li>
	 * </ul>
	 * @param evt
	 * @return
	 * @throws DAOException 
	 */
	public String carregaDadosPlanoMatricula(ValueChangeEvent evt) throws DAOException {
		planoMatriculaIngressante.setId((Integer) evt.getNewValue());
		if (planoMatriculaIngressante.getId() == 0)
			planoMatriculaIngressante = new PlanoMatriculaIngressantes();
		else
			planoMatriculaIngressante = getGenericDAO().refresh(planoMatriculaIngressante);
		return null;
	}
	
	/** Seleciona uma convoca��o para imprimir a documenta��o de cadastramento e comprovante de matr�cula.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/CadastramentoDiscentes/seleciona_convocado.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String selecionarDocumentacao() throws DAOException, SegurancaException {
		checkChangeRole();
		obj = new ConvocacaoProcessoSeletivoDiscente();
		populateObj(true);
		if (isEmpty(obj)) {
			addMensagemErro("N�o foi poss�vel selecionar o discente.");
		} else if (obj.getDiscente().getStatus() == StatusDiscente.PENDENTE_CADASTRO) {
			addMensagemErro("O discente n�o est� cadastrado");
		}
		if (hasErrors()) return null;
		PlanoMatriculaIngressantesDao dao = getDAO(PlanoMatriculaIngressantesDao.class);
		planoMatriculaIngressante = dao.findByDiscente(obj.getDiscente().getId());
		checkDiscenteMatriculadoComponente(obj.getDiscente());
		return forward("/vestibular/CadastramentoDiscentes/comprovantes.jsp");
	}
	
	/** Submete o status do discente e o plano de matr�cula.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/CadastramentoDiscentes/status_matricula.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String confirmar() throws DAOException, SegurancaException {
		checkChangeRole();
		if (!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_DISCENTE_CONVOCADO.getId()))
			return cancelar();
		validateRequiredId(obj.getDiscente().getStatus(), "Status", erros);
		if (hasErrors()) return null;
		if (obj.getDentroNumeroVagas() != null && !obj.getDentroNumeroVagas().booleanValue() && obj.getDiscente().getStatus() == StatusDiscente.CADASTRADO) {
			addMensagemWarning("O candidato, quando convocado, estava al�m do n�mero de vagas. Certifique-se que o status do discente dever� ser CADASTRADO.");
		}
		GenericDAO dao = getGenericDAO();
		if (obj.getDiscente().getDiscente().isPreCadastro())
			planoMatriculaIngressante = new PlanoMatriculaIngressantes();
		if (!isEmpty(planoMatriculaIngressante)){
			planoMatriculaIngressante = dao.refresh(planoMatriculaIngressante);
			// repassa o plano de matr�cula para o mbean auxiliar na exibi��o do quadro de hor�rio
			PlanoMatriculaIngressantesMBean mBean = getMBean("planoMatriculaIngressantesMBean");
			mBean.init();
			mBean.setObj(planoMatriculaIngressante);
		}
		return forward("/vestibular/CadastramentoDiscentes/confirma.jsp");
	}
	
	/** Redireciona o usu�rio para o formul�rio de busca de convoca��es.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/CadastramentoDiscentes/status_matricula.jsp</li>
	 * </ul>
	 * @return
	 */
	public String formSelecionaConvocado() {
		return forward("/vestibular/CadastramentoDiscentes/seleciona_convocado.jsp");
	}
	
	/** Redireciona o usu�rio para o formul�rio de sele��o de status e plano de matr�cula.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/CadastramentoDiscentes/confirma.jsp</li>
	 * </ul>
	 * @return
	 */
	public String formStatusMatricula() {
		if (!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_DISCENTE_CONVOCADO.getId()))
			return cancelar();
		return forward("/vestibular/CadastramentoDiscentes/status_matricula.jsp");
	}
	
	/** Redireciona o usu�rio para o formul�rio de sele��o de status e plano de matr�cula.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/CadastramentoDiscentes/status_matricula.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 * @throws SegurancaException 
	 */
	public String formDadosPessoais() throws DAOException, SegurancaException {
		if (!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_DISCENTE_CONVOCADO.getId()))
			return cancelar();
		if (isEmpty(obj) || isEmpty(obj.getDiscente()) || isEmpty(obj.getDiscente().getPessoa())) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return null;
		}
		Pessoa pessoa = obj.getDiscente().getPessoa();
		pessoa.prepararDados();
		// forma o carregamento dos atributos lazy
		pessoa.getEstadoCivil().getDescricao();
		pessoa.getTipoRaca().getDescricao();
		pessoa.getPais().getNome();
		// invoca o MBean para tratar os dados pessoais
		DadosPessoaisMBean dadosPessoaisMBean = (DadosPessoaisMBean) getMBean("dadosPessoais");
		dadosPessoaisMBean.setCodigoOperacao(OperacaoDadosPessoais.CADASTRAMENTO_DISCENTE_CONVOCADO);
		dadosPessoaisMBean.carregarMunicipios();
		dadosPessoaisMBean.setSubmitButton("Confirmar altera��o");
		dadosPessoaisMBean.setPassivelAlterarCpf(false);
		dadosPessoaisMBean.setPermiteAlterarIdentidade(true);
		dadosPessoaisMBean.setPermiteAlterarNome(true);
		dadosPessoaisMBean.setOrdemBotoes(false);
		dadosPessoaisMBean.setObj(obj.getDiscente().getPessoa());
		String retorno = dadosPessoaisMBean.popular(false); 
		dadosPessoaisMBean.setExibirPainel(false);
		return retorno;
	}
	
	/** Cadastra o discente e matricula nas turmas do plano de matr�cula escolhido.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/CadastramentoDiscentes/confirma.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		checkChangeRole();
		if (!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_DISCENTE_CONVOCADO.getId()))
			return cancelar();
		MovimentoCadastro mov = new MovimentoCadastro(obj);
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_DISCENTE_CONVOCADO);
		mov.setObjAuxiliar(planoMatriculaIngressante);
		DiscenteGraduacao discente = (DiscenteGraduacao) execute(mov);
		if (!isEmpty(discente.getMatricula()))
			addMensagemInformation("Discente " + discente.getMatriculaNome() + " cadastrado com sucesso!");
		else
			addMensagemInformation("Status do candidato " + discente.getNome() + " atualizado com sucesso!");
		removeOperacaoAtiva();
		getGenericDAO().initialize(obj.getDiscente());
		checkDiscenteMatriculadoComponente(obj.getDiscente());
		return forward("/vestibular/CadastramentoDiscentes/comprovantes.jsp");
	}

	private void checkDiscenteMatriculadoComponente(DiscenteGraduacao discente)
			throws DAOException {
		MatriculaComponenteDao matDao = getDAO(MatriculaComponenteDao.class);
		matriculado = matDao.countMatriculasByDiscente(discente, discente.getAnoIngresso(), discente.getPeriodoIngresso(), SituacaoMatricula.getSituacoesAtivasArray()) > 0;
	}

	/** Atualiza a cole��o de SelectItem com as convoca��es do Processo Seletivo escolhido pelo usu�rio.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/CadastramentoDiscentes/seleciona_convocado.jsp</li>
	 * </ul> 
	 * @throws DAOException
	 */
	public String processoSeletivoListener(ValueChangeEvent evt) throws DAOException {
//		int id = convocacaoProcessoSeletivo.getProcessoSeletivo().getId();
		int id =  (Integer) evt.getNewValue();
		convocacaoProcessoSeletivoCombo = new LinkedList<SelectItem>();
		ConvocacaoProcessoSeletivoDao dao = getDAO(ConvocacaoProcessoSeletivoDao.class);
		Collection<ConvocacaoProcessoSeletivo> lista = dao.findByProcessoSeletivo(id);
		if (!isEmpty(lista)) {
			for (ConvocacaoProcessoSeletivo cps : lista)
				convocacaoProcessoSeletivoCombo.add(new SelectItem(cps.getId(), cps.getDescricaoCompleta()));
		}
		return null;
	}
	
	/** Imprime o atestado de matr�cula do discente.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/CadastramentoDiscentes/comprovantes.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String atestadoMatricula() throws DAOException, SegurancaException {
		checkChangeRole();
		if (obj == null && obj.getDiscente() == null) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return null;
		}
		if (obj.getDiscente().getStatus() == StatusDiscente.CADASTRADO || obj.getDiscente().getStatus() == StatusDiscente.ATIVO) {
			AtestadoMatriculaMBean atestado = (AtestadoMatriculaMBean) getMBean("atestadoMatricula");
			getCurrentSession().setAttribute("atestadoLiberado", obj.getDiscente().getId());
			atestado.setDiscente(obj.getDiscente());
			atestado.setAno(obj.getAno());
			atestado.setPeriodo(obj.getPeriodo());
			
			return atestado.selecionaDiscente();
		} else {
			addMensagemWarning("O discente n�o est� cadastrado ou ativo.");
			return null;
		}
	}
	
	/** Imprime a documenta��o utilizada no cadastramento do discente.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/CadastramentoDiscentes/comprovantes.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String comprovantesCadastramento() throws DAOException, SegurancaException {
		checkChangeRole();
		if (obj.getDiscente().getStatus() == StatusDiscente.CADASTRADO ||
				obj.getDiscente().getStatus() == StatusDiscente.PRE_CADASTRADO ||
				obj.getDiscente().getStatus() == StatusDiscente.ATIVO) {
			Map<String, Object> hs = new HashMap<String, Object>();
			hs.put("SUBREPORT_DIR", JasperReportsUtil.PATH_RELATORIOS_SIGAA);
			JRDataSource jrds = null;
			JasperPrint prt = null;
			String nomeArquivo = "documentos_cadastramento_" + obj.getDiscente().getPessoa().getCpf_cnpj() + ".pdf";
			ProcessoSeletivoVestibular ps = obj.getConvocacaoProcessoSeletivo().getProcessoSeletivo();
			DocumentosDiscentesConvocadosDao dao = getDAO(DocumentosDiscentesConvocadosDao.class);
			Collection<LinhaImpressaoDocumentosConvocados>  discentesConvocados = dao.findAllCandidatosConvocados(null, obj.getDiscente().getId(), ps.getId(),
					obj.getConvocacaoProcessoSeletivo().getId(), DocumentosDiscentesConvocadosMBean.DOCUMENTOS_PARA_CADASTRO_DE_DISCENTES);
			if (obj.getDiscente().getStatus() == StatusDiscente.PRE_CADASTRADO) {
				hs.put("comprovante", getReportSIGAA("comprovante_comparecimento_2012.jasper"));
			} else {
				hs.put("comprovante", getReportSIGAA("comprovante_2012.jasper"));
			}
			hs.put("discentesConvocados", discentesConvocados);
			hs.put("processoSeletivo", ps.getNome());
			hs.put("ano", new Integer(ps.getAnoEntrada()));
			checkDiscenteMatriculadoComponente(obj.getDiscente());
			hs.put("matriculado", new Boolean(matriculado));
			Collection<LinhaAuxiliar> linhas = new LinkedList<LinhaAuxiliar>();
			for (LinhaImpressaoDocumentosConvocados linhaImpressao : discentesConvocados) {
				LinhaAuxiliar linhaAux = new LinhaAuxiliar();
				linhaAux.getLinha().add(linhaImpressao);
				linhas.add(linhaAux);
			}
			try {
				jrds = new JRBeanCollectionDataSource(linhas);
				prt = JasperFillManager.fillReport(getReportSIGAA("relatorios_convocacao_2012.jasper"), hs, jrds);
				if (prt == null || prt.getPages().size() == 0) {
					addMensagemWarning("N�o h� alunos convocados.");
					return null;
				}
				JasperReportsUtil.exportar(prt, nomeArquivo, getCurrentRequest(), getCurrentResponse(), "pdf");
			} catch (Exception e) {
				e.printStackTrace();
				addMensagemErroPadrao();
				notifyError(e);
			}
			FacesContext.getCurrentInstance().responseComplete();
		} else {
			addMensagemWarning("O discente n�o � cadastrado/ativo.");
		}
		return null;
	}
	
	/** Busca por outros v�nculos ativos de gradua��o do discente.
	 * @param discente
	 * @return
	 * @throws LimiteResultadosException
	 * @throws DAOException
	 */
	private Collection<DiscenteGraduacao> buscaOutrosVinculosAtivos(DiscenteGraduacao discente) throws LimiteResultadosException, DAOException {
		DiscenteDao dao = getDAO(DiscenteDao.class);
		int[] tiposValidos = {Discente.REGULAR};
		int[] statusValidos = { StatusDiscente.ATIVO, StatusDiscente.FORMANDO,
				StatusDiscente.TRANCADO, StatusDiscente.CADASTRADO, StatusDiscente.ATIVO_DEPENDENCIA };
		@SuppressWarnings("unchecked")
		Collection<DiscenteGraduacao> discentes = (Collection<DiscenteGraduacao>) dao.findOtimizado(discente.getPessoa().getCpf_cnpj(), null, null, null, null, null, statusValidos, tiposValidos, 0, NivelEnsino.GRADUACAO, false);
		if (!isEmpty(discentes)) {
			Iterator<DiscenteGraduacao> iterator = discentes.iterator();
			while (iterator.hasNext()) {
				DiscenteGraduacao vinculoAnterior = iterator.next();
				if (vinculoAnterior.getId() == discente.getId())
					iterator.remove();
				else
					dao.initialize(vinculoAnterior.getMatrizCurricular());
			}
		}
		return discentes;
	}
	
	public Collection<SelectItem> getConvocacaoProcessoSeletivoCombo(){
		return convocacaoProcessoSeletivoCombo;
	}
	
	/** Retorna uma cole��o de SelectItem com status que o discente pode ter.
	 * @return
	 */
	public Collection<SelectItem> getStatusDiscentesCombo(){
		if (statusDiscentesCombo == null) {
			statusDiscentesCombo = new LinkedList<SelectItem>();
			if (obj.getDentroNumeroVagas() == null || isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE)) {
				int statusCombo[] = {StatusDiscente.CADASTRADO, StatusDiscente.PRE_CADASTRADO};
				statusDiscentesCombo.add(new SelectItem(0,"-- SELECIONE --"));
				for (Integer status : statusCombo)
					statusDiscentesCombo.add(new SelectItem(status, StatusDiscente.getDescricao(status)));
			} else if ( obj.getDentroNumeroVagas().booleanValue()) {
				statusDiscentesCombo.add(new SelectItem(StatusDiscente.CADASTRADO, StatusDiscente.getDescricao(StatusDiscente.CADASTRADO)));
			} else {
				statusDiscentesCombo.add(new SelectItem(StatusDiscente.PRE_CADASTRADO, StatusDiscente.getDescricao(StatusDiscente.PRE_CADASTRADO)));
			}
		}
		return statusDiscentesCombo;
	}
	
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.DAE, SigaaPapeis.CADASTRA_DISCENTE_GRADUACAO);
	}
	
	public Collection<SelectItem> getPlanoMatriculaIngressanteCombo(){
		return planoMatriculaIngressanteCombo;
	}
	
	public boolean isBuscaCPF() {
		return buscaCPF;
	}

	public void setBuscaCPF(boolean buscaCPF) {
		this.buscaCPF = buscaCPF;
	}

	public boolean isBuscaNome() {
		return buscaNome;
	}

	public void setBuscaNome(boolean buscaNome) {
		this.buscaNome = buscaNome;
	}

	public ConvocacaoProcessoSeletivo getConvocacaoProcessoSeletivo() {
		return convocacaoProcessoSeletivo;
	}

	public void setConvocacaoProcessoSeletivo(
			ConvocacaoProcessoSeletivo convocacaoProcessoSeletivo) {
		this.convocacaoProcessoSeletivo = convocacaoProcessoSeletivo;
	}

	public PlanoMatriculaIngressantes getPlanoMatriculaIngressante() {
		return planoMatriculaIngressante;
	}

	public void setPlanoMatriculaIngressante(
			PlanoMatriculaIngressantes planoMatriculaIngressante) {
		this.planoMatriculaIngressante = planoMatriculaIngressante;
	}

	public Collection<DiscenteGraduacao> getOutrosVinculos() {
		return outrosVinculos;
	}

	public boolean isMatriculado() {
		return matriculado;
	}

	public void setMatriculado(boolean matriculado) {
		this.matriculado = matriculado;
	}

}
