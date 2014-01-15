/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 15/06/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.jsf;

import static br.ufrn.arq.util.ValidatorUtil.validateMaxValue;
import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoTurma;
import br.ufrn.sigaa.ensino.graduacao.jsf.ComponenteCurricularMBean;
import br.ufrn.sigaa.ensino.jsf.DocenteTurmaMBean;
import br.ufrn.sigaa.ensino.jsf.HorarioTurmaMBean;
import br.ufrn.sigaa.ensino.jsf.TurmaMBean;
import br.ufrn.sigaa.ensino.latosensu.dominio.RegistroAlteracaoLato;
import br.ufrn.sigaa.ensino.medio.dao.TurmaMedioDao;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaComponenteMedio;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerie;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.OperacaoTurmaValidator;
import br.ufrn.sigaa.ensino.negocio.TurmaValidator;
import br.ufrn.sigaa.ensino.negocio.dominio.TurmaMov;

/**
 * MBean responsável por operações com Turma de Ensino Médio.
 * 
 * @author Rafael Gomes
 *
 */
@Component("turmaMedio") @Scope("session") 
public class TurmaMedioMBean extends TurmaMBean{

	/** Define o link para o formulário de dados gerais. */
	public static final String JSP_DADOS_GERAIS = "/medio/turma/dados_gerais.jsp";
	/** Define o link para o resumo dos dados da turma. */
	public static final String JSP_RESUMO = "/medio/turma/resumo.jsp";
	/** Define o link para o resumo dos dados da turma em formato de relatório. */
	public static final String JSP_VIEW = "/medio/disciplina/view.jsp";
	/** Define o link de cadastro efetuado com sucesso. */
	public static final String JSP_LISTA_TURMA = "/medio/turmaSerie/lista.jsp";
	/** Define o link de confirmação de remoção. */
	public static final String JSP_CONFIRMA_REMOCAO = "/medio/turma/confirma_remocao.jsp";
	/** Define o link para a listagem dos alunos da turma. */
	public static final String JSP_DISCENTES = "/medio/turma/discentes.jsp";
	
	/** Objeto utilizado para armazenar a turmaSerie da disciplina utilizada pelo Objeto do MBean.*/
	private TurmaSerie turmaSerie = new TurmaSerie();
	/** Lista de alunos Matriculados na turma */
	private Collection<MatriculaComponenteMedio> matriculados;
	/** Registro de alterações efetuadas na turma em relação às pré-aprovadas na proposta do curso de ensino médio. */
	private RegistroAlteracaoLato registroAlteracao;
	/** Indica que a operação é de remover turma. * */
	private boolean remover;
	
	
	
	/**
	 * Checa a regra para atualização da turma
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 */
	@Override
	public void checkRoleAtualizarTurma() throws ArqException {
		this.remover = false;
		if( !obj.isAberta()){
			addMensagemErro("Não é possível alterar turmas que não estão abertas.");
			obj = new Turma();
			return;
		}
	}
	
	/**
	 * Verifica se a turma está passível de edição
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/medio/turma/dados_gerais.jsp</li>
	 * </ul>
	 */
	@Override
	public boolean isPassivelEdicao() throws DAOException {
		if ( isReadOnly() ) {
			return false;
		}
		return obj.isAberta() ;
	}
	
	/** Redireciona o usuário para a tela de resumo do cadastro.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 */
	@Override
	public String formConfirmacao() {
		return forward(JSP_RESUMO);
	}
	
	/** Redireciona o usuário para a tela de cadastro com sucesso.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 */
	public String formListaTurma() {
		return forward(JSP_LISTA_TURMA);
	}

	/**
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/medio/turma/resumo.jsp</li>
	 *	</ul>
	 */
	@Override
	public String formDadosGerais() {
		return forward(JSP_DADOS_GERAIS);
	}

	/**
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/medio/turma/dados_gerais.jsp</li>
	 *	</ul>
	 */
	@Override
	public String formSelecaoComponente() {
		try {
			ComponenteCurricularMBean mBean = getMBean("componenteCurricular");
			mBean.setSelecionaUnidade(false);
			Unidade unidade = getUsuarioLogado().getVinculoAtivo().getUnidade();
			return mBean.buscarComponente(this, "Cadastro de Turmas", unidade, false, false, TipoComponenteCurricular.DISCIPLINA, TipoComponenteCurricular.MODULO, TipoComponenteCurricular.ATIVIDADE);
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
			return null;
		}
	}

	@Override
	public char getNivelEnsino() {
		return NivelEnsino.MEDIO;
	}

	@Override
	public boolean isDefineDocentes() {
		return true;
	}

	@Override
	public boolean isDefineHorario() {
		return true;
	}

	@Override
	public boolean isPodeAlterarHorarios() {
		return obj.isAberta() || (obj.getId() == 0 || !isMatriculada());
	}
	
	/**
	 * Checa as regras para a remoção.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 */
	@Override
	public void checkRolePreRemover() throws ArqException {
		if( !obj.isAberta() ){
			addMensagemErro("Não é possível remover a turma que não está aberta.");
		} else if (obj.getQtdMatriculados() > 0) {
			addMensagemErro("Não pode remover uma turma com discentes matriculados.");
		}
		remover = true;
	}
	
	/**
	 * Realiza a validação nos dados gerais
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 */
	@Override
	public void validaDadosGerais(ListaMensagens erros) throws DAOException {
		if( obj.getDisciplina() == null || obj.getDisciplina().getId() <= 0 ){
			addMensagemErro("Selecione um componente curricular.");
		} else 	if (!obj.getDisciplina().isAtivo()) {
			addMensagemErro("O componente curricular " + obj.getDisciplina().getDescricaoResumida() + " não está ativo.");
		}
		
		validateMinValue(obj.getDataInicio(), turmaSerie.getDataInicio(), "Início", erros);
		validateMaxValue(obj.getDataFim(), turmaSerie.getDataFim(), "Fim", erros);
		
		if(hasErrors()) return;

		TurmaDao dao = getDAO(TurmaDao.class);
		obj.setDisciplina( dao.findByPrimaryKey(obj.getDisciplina().getId(), ComponenteCurricular.class) );

		TurmaValidator.validaDadosBasicos(obj, getUsuarioLogado(), erros);

		if (obj.getCurso() != null && obj.getCurso().getId() > 0)
			obj.setCurso(dao.findByPrimaryKey(obj.getCurso().getId(), Curso.class));

		// valida se a data informada / alterada pelo Gestor de Ensino médio está dentro do período de férias
		if (obj.isTurmaFerias() && getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_MEDIO)){
			CalendarioAcademico calTurma = CalendarioAcademicoHelper.getCalendario(obj);
			validateMinValue(obj.getDataInicio(), calTurma.getInicioFerias(), "Início", erros);
			validateMaxValue(obj.getDataFim(), calTurma.getFimFerias(), "Fim", erros);
		}
		
		if (hasOnlyErrors())
			return;

	}

	@Override
	public void beforeAtualizarTurma() throws ArqException {
	}

	@Override
	public void beforeConfirmacao() throws ArqException {
	}

	@Override
	public void beforeDadosGerais() throws ArqException {
	}

	@Override
	public void beforeDefinirDocentesTurma() throws ArqException {
	}

	@Override
	public void beforeSelecionarComponente() throws ArqException {
	}

	/**
	 * Realiza a operação de alteração da disciplina, possibilitando inserir novas informações.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/medio/turmaSerie/lista.jsp</li>
	 * </ul>
	 */
	public String alterarDisciplina() throws ArqException  {
		turmaSerie = getGenericDAO().findByPrimaryKey(getParameterInt("idTurmaSerie"), TurmaSerie.class);
		return atualizar();
	}
	
	@Override
	public String formHorarios() {
		HorarioTurmaMBean horarioBean = getMBean("horarioTurmaBean");
		return horarioBean.populaHorarioTurma(this, "Cadastro de Disciplina", obj);
	}
	
	@Override
	public String formDocentes(){
		try {
			beforeDefinirDocentesTurma();
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
			return null;
		}
		DocenteTurmaMBean horarioBean = getMBean("docenteTurmaBean");
		return horarioBean.populaDocentesTurma(this, "Cadastro de Disciplina", obj, isTurmaEad());
	}
	
	@Override
	public String cadastrar() throws ArqException {
		if (getConfirmButton().equalsIgnoreCase("remover")) {
			return remover();
		} else {
			
			Comando cmd = SigaaListaComando.CADASTRAR_TURMA;
			TurmaMov mov = new TurmaMov();
			if (getConfirmButton().equalsIgnoreCase("alterar")) {
				cmd = SigaaListaComando.ALTERAR_TURMA;
				mov.setAlteracaoTurma(getAlteracaoTurma());
			}
			if (obj.getCurso() != null && obj.getCurso().getId() == 0)
				obj.setCurso(null);
			
			mov.setCodMovimento(cmd);
			mov.setTurma(obj);
			mov.setSolicitacaoEnsinoIndividualOuFerias(null);
			mov.setSolicitacoes( new HashSet<SolicitacaoTurma>() );
			mov.setRegistroAlteracaoLato(registroAlteracao);

			for( ReservaCurso r : obj.getReservas() ){
				mov.getSolicitacoes().add( r.getSolicitacao() );
			}
			try {
				execute(mov, getCurrentRequest());
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				e.printStackTrace();
				return null;
			}
			if (cmd.equals(SigaaListaComando.CADASTRAR_TURMA)) {
				if( obj.getDisciplina().isSubUnidade() )
					addMessage("ATENÇÃO! A turma criada foi de uma subunidade de um bloco e só estará disponível para matrícula após todas as subunidades do bloco terem sido criadas.", TipoMensagemUFRN.INFORMATION);
				addMessage("Disciplina " + getDescricaoDisciplinaMedio() + " cadastrada com sucesso!", TipoMensagemUFRN.INFORMATION);
			} else {
				addMessage("Disciplina " + getDescricaoDisciplinaMedio() + " alterada com sucesso!", TipoMensagemUFRN.INFORMATION);
			}
			removeOperacaoAtiva();
			TurmaSerieMBean tsMBean = getMBean("turmaSerie");
			return tsMBean.listarTurmasAnual(turmaSerie, JSP_LISTA_TURMA);
		}
	}

	/** 
	 * Seleciona a turma e lista os alunos matriculados na turma.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/medio/turma_serie/form.jsp</li>
	 * </ul>
	 * @return sigaa.war/ensino/turma/discentes.jsp
	 * @throws DAOException
	 */
	public String listaAlunosMatriculados() throws DAOException {
		TurmaMedioDao dao = getDAO(TurmaMedioDao.class);
		setId();
		obj = dao.findByPrimaryKey(obj.getId(), Turma.class);
		Collection<SituacaoMatricula> situacoes = SituacaoMatricula.getSituacoesAtivas();
		turmaSerie = dao.findByPrimaryKey(getParameterInt("idTurmaSerie"), TurmaSerie.class);
		matriculados = dao.findMatriculasByTurma( obj.getId(), situacoes.toArray(new SituacaoMatricula[situacoes.size()]) );
		return forward(JSP_DISCENTES);
	}
	
	/**
	 * Reabre uma turma já consolidada.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/ensino/turma/busca_turma.jsp</li>
	 * </ul>
	 * 
	 * @return menuGraduacao
	 * @throws ArqException
	 */
	public String reabrirTurma() throws ArqException{
		Integer id = getParameterInt("id");
		turmaSerie = getGenericDAO().findByPrimaryKey(getParameterInt("idTurmaSerie",0), TurmaSerie.class);
		if(id == null){
			addMensagemErro("Informe a turma.");
			return null;
		}
		
		Turma turma = new Turma(id);
		turma = getGenericDAO().findByPrimaryKey(turma.getId(), Turma.class);
		
		if (!OperacaoTurmaValidator.isPermiteReabrirTurma(turma)) {
			throw new SegurancaException("Usuário não autorizado a realizar esta operação.");
		}		
		
		prepareMovimento(SigaaListaComando.REABRIR_TURMA);

		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.REABRIR_TURMA);
		mov.setObjMovimentado(turma);

		try {
			execute(mov);
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		}
		addMessage("Turma reaberta com sucesso!", TipoMensagemUFRN.INFORMATION);
		
		return listarTurmas();
	}
	
	/**
	 * Carrega as informações da turma e redireciona para o formulário de
	 * visualização.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/medio/disciplina/busca_disciplina.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String view() throws DAOException{
		int id = getParameterInt("id");
		TurmaDao tDao = getDAO(TurmaDao.class);
		MatriculaComponenteDao mDao = getDAO(MatriculaComponenteDao.class);
		obj = tDao.findAndFetch(id, Turma.class,
				"reservas", "docentesTurmas");
		obj.setQtdMatriculados(tDao.findQtdAlunosPorTurma( obj.getId(), 
				SituacaoMatricula.MATRICULADO, SituacaoMatricula.EM_ESPERA, 
				SituacaoMatricula.TRANCADO, SituacaoMatricula.APROVADO, 
				SituacaoMatricula.REPROVADO, SituacaoMatricula.REPROVADO_FALTA));
		obj.setMatriculasDisciplina(mDao.findAtivasByTurma(obj));
		return forward(JSP_VIEW);
	}
	
	/** 
	 * Redireciona o usuário para a lista de Turmas.
	 * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/medio/turma/dados_gerais.jsp</li>
	 * </ul>
	 
	 * @return
	 * @throws DAOException 
	 */
	public String listarTurmas() throws DAOException {
		TurmaSerieMBean tsBean = getMBean("turmaSerie");
		tsBean.setObj(turmaSerie);
		return tsBean.listarTurmasAnual();
	}
	
	/**
	 * Retorna uma descrição completa da Disciplina, sem os nomes dos docentes e código do componente.
	 * 
	 * @return
	 */
	public String getDescricaoDisciplinaMedio() {
		String descricao = ( obj.getDisciplina() != null ? obj.getDisciplina().getNome() : "")
			+ (obj.getCodigo() != null ? " - Turma: " + obj.getCodigo() : "");
		return descricao;
	}	
	
	@Override
	public void checkRoleCadastroTurmaSemSolicitacao(ListaMensagens erros)
			throws SegurancaException {
	}

	/** 
	 * Indica se a operação corrente é de edição do código da turma.
	 */
	public boolean isEditarCodigoTurma() {
		return obj.getId() != 0;
	}
	
	@Override
	public String formConfirmacaoRemover() {
		return null;
	}

	@Override
	public void validaDocentesTurma(ListaMensagens erros) throws DAOException {
	}

	@Override
	public void validaHorariosTurma(ListaMensagens erros) throws DAOException {
	}

	@Override
	public ListaMensagens validarSelecaoComponenteCurricular(
			ComponenteCurricular componente) throws ArqException {
		return null;
	}

	public RegistroAlteracaoLato getRegistroAlteracao() {
		return registroAlteracao;
	}
	public void setRegistroAlteracao(RegistroAlteracaoLato registroAlteracao) {
		this.registroAlteracao = registroAlteracao;
	}
	public boolean isRemover() {
		return remover;
	}
	public void setRemover(boolean remover) {
		this.remover = remover;
	}
	public TurmaSerie getTurmaSerie() {
		return turmaSerie;
	}
	public void setTurmaSerie(TurmaSerie turmaSerie) {
		this.turmaSerie = turmaSerie;
	}
	public Collection<MatriculaComponenteMedio> getMatriculados() {
		return matriculados;
	}
	public void setMatriculados(Collection<MatriculaComponenteMedio> matriculados) {
		this.matriculados = matriculados;
	}

	@Override
	public String retornarSelecaoComponente() {
		// TODO redirecionar o usuário para o formulário que invocou a seleção de componentes.
		return cancelar();
	}

	@Override
	public String definePeriodosTurma(Date inicio, Date fim)
			throws ArqException {
		// TODO Auto-generated method stub
		return null;
	}
}
