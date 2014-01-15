/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 14/12/2010
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.graduacao.CadastramentoDiscenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.graduacao.dao.ImportacaoDiscenteOutrosConcursosDao;
import br.ufrn.sigaa.ensino.graduacao.dominio.CancelamentoConvocacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.MotivoCancelamentoConvocacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.OpcaoCadastramento;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoCadastramentoDiscente;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivo;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivoDiscente;
import br.ufrn.sigaa.vestibular.dominio.ImportacaoDiscenteOutrosConcursos;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;

/**
 * Controlador responsável por gerenciar o cadastramento dos discentes aprovados no vestibular.
 * 
 * @author Leonardo Campos
 * 
 */
@Component("cadastramentoDiscente") @Scope("request")
public class CadastramentoDiscenteMBean extends SigaaAbstractController<Object> {

	/** Constante com o endereço da view do formulário. */
	private static final String JSP_FORM = "/graduacao/cadastramento_discente/form_cadastramento.jsp";
	/** Constante com o endereço da view do resumo. */
	private static final String JSP_RESUMO = "/graduacao/cadastramento_discente/resumo_cadastramento.jsp";

	/** Ano de referência trabalhado na operação. */
	private ProcessoSeletivoVestibular processoSeletivo;
	/** Matriz curricular cujos alunos serão buscados. */
	private MatrizCurricular matriz;
	/** Discentes retornados pela busca. */
	private Collection<ConvocacaoProcessoSeletivoDiscente> convocacoes;
	/** Discentes confirmados para serem cadastrados. */
	private Collection<Discente> cadastrados;
	/** Discentes confirmados para serem cancelados. */
	private Collection<Discente> cancelados;
	/** Cancelamentos gerados para aqueles discentes que não foram confirmados. */
	private Collection<CancelamentoConvocacao> cancelamentos;
	/** Matrizes curriculares com oferta de vagas no vestibular para o ano de referência considerado. */
	private Collection<MatrizCurricular> matrizes;
	/** Indica que a confirmação do cadastramento é para os discentes que foram importados de outros processos seletivos. */
	private boolean discentesImportados;
	/** Processo de importação que será trabalho. */
	private ImportacaoDiscenteOutrosConcursos importacaoDiscente;
	/** Lista de discente que foram importados. */
	private Collection<DiscenteGraduacao> discentes;
	/** Status padrão para os discente encontrados na busca. */
	private int idStatusTodosDiscentes;
	
	/** Construtor padrão. */
	public CadastramentoDiscenteMBean() {
		clear();
	}
	
	/**
	 * Inicializa as informações utilizadas em todo o caso de uso.
	 */
	private void clear(){
		processoSeletivo = new ProcessoSeletivoVestibular();
		matriz = new MatrizCurricular();
		convocacoes = new ArrayList<ConvocacaoProcessoSeletivoDiscente>();
		cadastrados = new ArrayList<Discente>();
		cancelados = new ArrayList<Discente>();
		cancelamentos = new ArrayList<CancelamentoConvocacao>();
		importacaoDiscente = new ImportacaoDiscenteOutrosConcursos();
		discentesImportados = false;
	}
	
	/**
	 * Popula as informações necessárias e inicia o caso de uso. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menus/administracao.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 */
	public String iniciar() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_CONVOCACOES_PROCESSOS_SELETIVOS);
		clear();
		
		return telaFormulario();
	}
	
	/**
	 * Popula as informações necessárias e inicia o caso de uso. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menus/administracao.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 */
	public String iniciarDiscentesImportados() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_CONVOCACOES_PROCESSOS_SELETIVOS);
		clear();
		discentesImportados = true;
		return telaFormulario();
	}
	
	/**
	 * Carrega as matrizes curriculares de acordo com o processo seletivo selecionado. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/cadastramento_discente/form_cadastramento.jsp</li>
	 * </ul>
	 * @param evt
	 * @throws DAOException
	 */
	public void changeProcessoSeletivo(ValueChangeEvent evt) throws DAOException {
		CadastramentoDiscenteDao dao = getDAO(CadastramentoDiscenteDao.class);
		Integer idProcessoSeletivo = (Integer) evt.getNewValue();
		if(idProcessoSeletivo != null && idProcessoSeletivo > 0) {
			ProcessoSeletivoVestibular ps = dao.findByPrimaryKey(idProcessoSeletivo, ProcessoSeletivoVestibular.class);
			matrizes = dao.findMatrizes(ps);
		}
	}
	
	/**
	 * Busca os discentes de acordo com os dados informados. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/cadastramento_discente/form_cadastramento.jsp</li>
	 * </ul>
	 */
	public String buscar() throws HibernateException, DAOException {
		ValidatorUtil.validateRequiredId(processoSeletivo.getId(), "Processo Seletivo Vestibular", erros);
		ValidatorUtil.validateRequiredId(matriz.getId(), "Matriz Curricular", erros);
		
		if(!hasErrors()){
			CadastramentoDiscenteDao dao = getDAO(CadastramentoDiscenteDao.class);
			processoSeletivo = dao.findByPrimaryKey(processoSeletivo.getId(), ProcessoSeletivoVestibular.class);
			matriz = dao.findByPrimaryKey(matriz.getId(), MatrizCurricular.class);
			convocacoes = dao.findConvocacoes(matriz.getId(), processoSeletivo.getId(), 0, 0);
			if(ValidatorUtil.isEmpty(convocacoes))
				addMensagemErro("Não foram encontrados discentes de acordo com os parâmetros de busca informados.");
		}
		this.idStatusTodosDiscentes = OpcaoCadastramento.IGNORAR.ordinal();
		setOperacaoAtiva(SigaaListaComando.CONFIRMAR_CADASTRAMENTO.getId());
		return null;
	}
	
	/**
	 * Busca os discentes de acordo com os dados informados. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/cadastramento_discente/form_cadastramento.jsp</li>
	 * </ul>
	 */
	public String buscarDiscentesImportados() throws HibernateException, DAOException {
		ValidatorUtil.validateRequiredId(importacaoDiscente.getId(), "Processo de Importação", erros);
		ValidatorUtil.validateRequiredId(matriz.getId(), "Matriz Curricular", erros);
		
		if(!hasErrors()){
			ImportacaoDiscenteOutrosConcursosDao dao = getDAO(ImportacaoDiscenteOutrosConcursosDao.class);
			importacaoDiscente = dao.findByPrimaryKey(importacaoDiscente.getId(), ImportacaoDiscenteOutrosConcursos.class);
			matriz = dao.findByPrimaryKey(matriz.getId(), MatrizCurricular.class);
			discentes = dao.findDiscentesImportados(importacaoDiscente.getId(), matriz.getId());
			if(ValidatorUtil.isEmpty(discentes))
				addMensagemErro("Não foram encontrados discentes de acordo com os parâmetros de busca informados.");
		}
		setOperacaoAtiva(SigaaListaComando.CONFIRMAR_CADASTRAMENTO.getId());
		return null;
	}
	
	/**
	 * Processa as informações fornecidas encaminhando para a tela de confirmação. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/cadastramento_discente/form_cadastramento.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String processar() throws ArqException {
		if (!checkOperacaoAtiva(SigaaListaComando.CONFIRMAR_CADASTRAMENTO.getId()))
			return null;
		if(ValidatorUtil.isEmpty(convocacoes)){
			addMensagemErro("Não há discentes a processar.");
			return null;
		}
		
		prepareMovimento(SigaaListaComando.CONFIRMAR_CADASTRAMENTO);
		
		cadastrados.clear();
		cancelamentos.clear();
		
		CadastramentoDiscenteDao dao = getDAO(CadastramentoDiscenteDao.class);
		DiscenteDao discenteDao = getDAO( DiscenteDao.class );
		
		Map<Integer, MovimentacaoAluno> mapaMovimentacoesCancelamento = dao.findMapaMovimentacoesCancelamento(convocacoes);
		
		for (ConvocacaoProcessoSeletivoDiscente c: convocacoes) {
			
			if  (c.getCancelamento() != null) {
				if (c.getCancelamento().getMotivo() == null) {
					addMensagemErro("O discente " + c.getDiscente().getNome() + " não possui motivo de cancelamento registrado.");
					continue;
				} else if (c.getCancelamento().getMotivo().getId() != MotivoCancelamentoConvocacao.RECONVOCACAO_PRIMEIRA_OPCAO.getId()) {
					continue;
				}
			}
			Discente d = c.getDiscente().getDiscente();
			DiscenteGraduacao dg = c.getDiscente();
			ConvocacaoProcessoSeletivoDiscente convocacao = copiaConvocacao(c);
			Discente discente = convocacao.getDiscente().getDiscente();
			
			if(OpcaoCadastramento.PRESENTE.verificar(dg) && d.isPendenteCadastro()){
				// verifica se possui convocação anterior não cancelada associada à um discente ativo
				Integer statusAtivos[] = StatusDiscente.getStatusComVinculo().toArray(new Integer[StatusDiscente.getStatusComVinculo().size()]);
				int statusAnterior = dg.getStatus();
				dg.setStatus(StatusDiscente.CADASTRADO);
				Collection<DiscenteGraduacao> discentes = discenteDao.findByPessoaSituacao( dg.getPessoa().getId(), statusAtivos);
				dg.setStatus(statusAnterior);
				if( !isEmpty(discentes) ){
					addMensagemWarning("O discente " + discente.getPessoa().getNome() + " possui outro vínculo ativo associado." );
				} 
				// Compareceu ao cadastramento
				discente.setStatus(StatusDiscente.CADASTRADO);
				cadastrados.add(discente);
			} else if (OpcaoCadastramento.PRESENTE.verificar(dg) && d.isCancelado()) {
				// Aluno cancelado deve ser desmarcado
				addMensagemErro("Aluno com status CANCELADO não pode ter seu cadastramento confirmado. " +
						"Desmarque o aluno para que a convocação do mesmo seja cancelada.");
				return null;
			} else if (OpcaoCadastramento.AUSENTE.verificar(dg) && d.isPendenteCadastro()) {
				// Não compareceu ao cadastramento
				discente.setStatus(StatusDiscente.EXCLUIDO);
				adicionarCancelamento(convocacao, MotivoCancelamentoConvocacao.NAO_COMPARECIMENTO_CADASTRO, mapaMovimentacoesCancelamento);
			} else if (OpcaoCadastramento.AUSENTE.verificar(dg) && d.isCancelado()) {
				// Aluno cancelado previamente cuja convocação deve ser cancelada
//				if(!ValidatorUtil.isEmpty(dao.findByExactField(CancelamentoConvocacao.class, "discente.id", discente.getId()))){
//					addMensagemErro("O aluno "+ discente.getMatriculaNome() + " já possui um cancelamento de convocação registrado.");
//					return null;
//				}
				adicionarCancelamento(convocacao, MotivoCancelamentoConvocacao.REGRA_REGULAMENTO, mapaMovimentacoesCancelamento);
			}
		}
		
		return telaResumo();
	}
	
	/**
	 * Processa as informações fornecidas encaminhando para a tela de confirmação. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/cadastramento_discente/form_cadastramento.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws InstantiationException 
	 * @throws IllegalAccessException 
	 */
	public String processarDiscentesImportados() throws ArqException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		if (!checkOperacaoAtiva(SigaaListaComando.CONFIRMAR_CADASTRAMENTO.getId()))
			return null;
		if(ValidatorUtil.isEmpty(discentes)){
			addMensagemErro("Não há discentes a processar.");
			return null;
		}
		prepareMovimento(SigaaListaComando.CONFIRMAR_CADASTRAMENTO);
		
		cadastrados.clear();
		cancelados.clear();
		
		for (DiscenteGraduacao dg: discentes) {
			Discente discente = (Discente) BeanUtils.cloneBean(dg.getDiscente());
			
			if(OpcaoCadastramento.PRESENTE.verificar(dg) && dg.getDiscente().isPendenteCadastro()){
				// Compareceu ao cadastramento
				discente.setStatus(StatusDiscente.CADASTRADO);
				cadastrados.add(discente);
			} else if (OpcaoCadastramento.PRESENTE.verificar(dg) && dg.getDiscente().isCancelado()) {
				// Aluno cancelado deve ser desmarcado
				addMensagemErro("Aluno com status CANCELADO não pode ter seu cadastramento confirmado. " +
						"Desmarque o aluno para que a convocação do mesmo seja cancelada.");
				return null;
			} else if (OpcaoCadastramento.AUSENTE.verificar(dg) && dg.getDiscente().isPendenteCadastro()) {
				// Não compareceu ao cadastramento
				discente.setStatus(StatusDiscente.EXCLUIDO);
				cancelados.add(discente);
			} else if (OpcaoCadastramento.AUSENTE.verificar(dg) && dg.getDiscente().isCancelado()) {
				addMensagemErro("Aluno com status CANCELADO não pode ter seu cadastramento confirmado. " +
						"Desmarque o aluno para que a convocação do mesmo seja cancelada.");
			}
		}
		
		return telaResumo();
	}

	/**
	 * Acrescenta um cancelamento de convocação na coleção de cancelamentos.
	 * @param mapaInscricoes
	 * @param discente
	 */
	private void adicionarCancelamento(ConvocacaoProcessoSeletivoDiscente convocacao, MotivoCancelamentoConvocacao motivo, Map<Integer, MovimentacaoAluno> mapaMovimentacoesCancelamento) {
		CancelamentoConvocacao c = new CancelamentoConvocacao();
		c.setConvocacao(convocacao);
		c.setMotivo(motivo);
		c.setMovimentacaoCancelamento(mapaMovimentacoesCancelamento.get(convocacao.getDiscente().getId()));
		cancelamentos.add(c);
	}
	
	/**
	 * Retorna uma nova instância de Discente com uma cópia das informações utilizadas no caso de uso. 
	 * @param d
	 * @return
	 */
	private ConvocacaoProcessoSeletivoDiscente copiaConvocacao(ConvocacaoProcessoSeletivoDiscente c) {
		ConvocacaoProcessoSeletivoDiscente copia = new ConvocacaoProcessoSeletivoDiscente(c.getId());
		
		DiscenteGraduacao d = new DiscenteGraduacao();
		d.setId(c.getDiscente().getId());
		d.getDiscente().setId(c.getDiscente().getId());
		d.setMatricula(c.getDiscente().getMatricula());
		d.setNivel(c.getDiscente().getNivel());
		d.getDiscente().getPessoa().setNome(c.getDiscente().getPessoa().getNome());
		d.setStatus(c.getDiscente().getStatus());
		d.setAnoIngresso(c.getDiscente().getAnoIngresso());
		d.setPeriodoIngresso(c.getDiscente().getPeriodoIngresso());
		
		ConvocacaoProcessoSeletivo cd = new ConvocacaoProcessoSeletivo();
		cd.setDescricao(c.getConvocacaoProcessoSeletivo().getDescricao());
		
		copia.setDiscente(d);
		copia.setConvocacaoProcessoSeletivo(cd);
		copia.setCancelamento(c.getCancelamento());
		return copia;
	}

	/**
	 * Invoca o processador para persistir as informações do cadastramento. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/cadastramento_discente/resumo_cadastramento.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String confirmar() throws ArqException {
		if (!checkOperacaoAtiva(SigaaListaComando.CONFIRMAR_CADASTRAMENTO.getId()))
			return null;
		MovimentoCadastramentoDiscente mov = new MovimentoCadastramentoDiscente();
		mov.setCodMovimento(getUltimoComando());
		mov.setCadastrados(cadastrados);
		mov.setCancelados(cancelados);
		mov.setCancelamentos(cancelamentos);
		mov.setDiscentesImportados(discentesImportados);
		
		try {
			execute(mov);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
		
		addMensagemInformation("Cadastramento para a matriz curricular ["+ matriz.getDescricao() +"] realizado com sucesso! <br/>" +
				"Foram realizadas "+ cadastrados.size() + " confirmações e "+ cancelamentos.size() +" cancelamentos de convocação.");
		
		convocacoes = new ArrayList<ConvocacaoProcessoSeletivoDiscente>();
		discentes = new ArrayList<DiscenteGraduacao>();
		cancelados = new ArrayList<Discente>();
		if (discentesImportados)
			buscarDiscentesImportados();
		else
			buscar();
		return telaFormulario();
	}
	
	/**
	 * Encaminha para a tela do formulário.
	 *  <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/cadastramento_discente/resumo_cadastramento.jsp</li>
	 * </ul>
	 * @return
	 */
	public String telaFormulario() {
		if (discentesImportados)
			return forward("/graduacao/discente/importacao/gerencia_cadastramento.jsp");
		else
			return forward(JSP_FORM);
	}
	
	/**
	 * Encaminha para a tela de resumo.
	 *  <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/cadastramento_discente/form_cadastramento.jsp</li>
	 * </ul>
	 * @return
	 */
	public String telaResumo() {
		if (discentesImportados)
			return forward("/graduacao/discente/importacao/confirma_cadastramento.jsp");
		else
			return forward(JSP_RESUMO);
	}

	/**
	 * Retorna uma coleção de itens com os anos de referência para utilização na view.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAnosReferenciaCombo() throws DAOException {
		return toSelectItems(getDAO(CadastramentoDiscenteDao.class).findAnosProcessosSeletivosVestibular(), "anoEntrada", "anoEntrada");
	}
	
	/**
	 * Retorna uma coleção de itens com as matrizes curriculares manipuladas pelo caso de uso.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getMatrizesCombo() throws DAOException {
		Collection<SelectItem> itens = new ArrayList<SelectItem>();
		if (matrizes != null) {
			for (MatrizCurricular matriz : matrizes) {
				itens.add(new SelectItem(matriz.getId(), matriz.getDescricao()));
			}
		}
		return itens;
	}

	public MatrizCurricular getMatriz() {
		return matriz;
	}


	public void setMatriz(MatrizCurricular matriz) {
		this.matriz = matriz;
	}

	public Collection<Discente> getCadastrados() {
		return cadastrados;
	}

	public void setCadastrados(Collection<Discente> cadastrados) {
		this.cadastrados = cadastrados;
	}

	public Collection<CancelamentoConvocacao> getCancelamentos() {
		return cancelamentos;
	}

	public void setCancelamentos(Collection<CancelamentoConvocacao> cancelamentos) {
		this.cancelamentos = cancelamentos;
	}

	public ProcessoSeletivoVestibular getProcessoSeletivo() {
		return processoSeletivo;
	}

	public void setProcessoSeletivo(ProcessoSeletivoVestibular processoSeletivo) {
		this.processoSeletivo = processoSeletivo;
	}

	public Collection<ConvocacaoProcessoSeletivoDiscente> getConvocacoes() {
		return convocacoes;
	}

	public void setConvocacoes(
			Collection<ConvocacaoProcessoSeletivoDiscente> convocacoes) {
		this.convocacoes = convocacoes;
	}

	/**
	 * Retorna uma lista de opções de cadastramento para ser utilizada na view.
	 * @return
	 */
	public List<SelectItem> getOpcoesCadastramentoCombo(){
		List<SelectItem> itens = new ArrayList<SelectItem>();
		for(OpcaoCadastramento op: OpcaoCadastramento.values()){
			SelectItem it = new SelectItem();
			it.setLabel(op.getLabel());
			it.setValue(op.ordinal());
			itens.add(it);
		}
		return itens;
	}

	public boolean isDiscentesImportados() {
		return discentesImportados;
	}

	public void setDiscentesImportados(boolean discentesImportados) {
		this.discentesImportados = discentesImportados;
	}

	public ImportacaoDiscenteOutrosConcursos getImportacaoDiscente() {
		return importacaoDiscente;
	}

	public void setImportacaoDiscente(
			ImportacaoDiscenteOutrosConcursos importacaoDiscente) {
		this.importacaoDiscente = importacaoDiscente;
	}

	public Collection<DiscenteGraduacao> getDiscentes() {
		return discentes;
	}

	public Collection<Discente> getCancelados() {
		return cancelados;
	}

	public int getIdStatusTodosDiscentes() {
		return idStatusTodosDiscentes;
	}

	public void setIdStatusTodosDiscentes(int idStatusTodosDiscentes) {
		this.idStatusTodosDiscentes = idStatusTodosDiscentes;
	}
}
