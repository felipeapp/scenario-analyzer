/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 08/11/2006 
 *
 */
package br.ufrn.sigaa.portal.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.autenticacao.AutenticacaoUtil;
import br.ufrn.arq.seguranca.autenticacao.TipoDocumentoAutenticado;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dao.PerfilPessoaDAO;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.agenda.jsf.AgendaTurmaMBean;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ead.FichaAvaliacaoEadDao;
import br.ufrn.sigaa.arq.dao.ensino.MovimentacaoAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.graduacao.IndiceAcademicoDao;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ava.dao.AvaliacaoDao;
import br.ufrn.sigaa.ava.dao.PermissaoAvaDao;
import br.ufrn.sigaa.ava.dao.TarefaTurmaDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.DataAvaliacao;
import br.ufrn.sigaa.ava.dominio.RegistroAtividadeTurma;
import br.ufrn.sigaa.ava.dominio.TarefaTurma;
import br.ufrn.sigaa.ava.negocio.DataAvaliacaoHelper;
import br.ufrn.sigaa.ava.questionario.dao.QuestionarioTurmaDao;
import br.ufrn.sigaa.ava.questionarios.dominio.QuestionarioTurma;
import br.ufrn.sigaa.cv.jsf.ComunidadeVirtualMBean;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.VinculoUsuario;
import br.ufrn.sigaa.ead.dominio.FichaAvaliacaoEad;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.IndiceAcademicoDiscente;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.jsf.AtestadoMatriculaMBean;
import br.ufrn.sigaa.ensino.jsf.HistoricoMBean;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.mensagens.MensagensGerais;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.portal.dao.PortalDiscenteDao;

/**
 * Managed Bean de controle do portal discente
 *
 * @author David Ricardo
 *
 */
@Component("portalDiscente") @Scope("session")
public class PortalDiscenteMBean extends SigaaAbstractController<DiscenteAdapter> {

	/** Constante que define a página principal do discente. */
	public static final String PORTAL_DISCENTE = "/portais/discente/discente.jsp";

	/** Define os dados do perfil do usuário discente */
	private PerfilPessoa perfil;

	// usado para exibir com JSTL
	/** Define o nome da turma selecionada pelo discente */
	private String nomeTurma;

	/** Define a lista de turmas abertas exibidas na página principal do discente. */
	private Collection<Turma> turmasAbertas;

	/** Define a lista de turmas associadas ao discente. */
	private List<Turma> turmas;
	
	/** Define a lista de turmas virtuais associadas ao discente. */
	private List<Turma> turmasVirtuaisHabilitadas;

	/** Define a lista das avaliações associadas ao discente na página principal do discente. */
	public List <DataAvaliacao> atividades;
	
	/** Define se o discente possui permissão para visualizar atestados de matrícula */
	private Boolean passivelEmissaoAtestadoMatricula;
	
	/** Define se deve ser exibido o relatório de índices para o discente */
	private Boolean passivelEmissaoRelatoriosIndices;

	/** Define a lista de Índice Acadêmico associado ao discente. */
	private Collection<IndiceAcademicoDiscente> indicesAcademicosDiscente;
	
	/** Armazena as ultimas atividades realizadas nas turmas virtuais do discente. */
	Collection<RegistroAtividadeTurma> ultimasAtividadesTurma;

	public PortalDiscenteMBean() throws DAOException {
		VinculoUsuario vinculoAtivo = getUsuarioLogado().getVinculoAtivo();
		if (vinculoAtivo != null && vinculoAtivo.isVinculoDiscente()) {
			obj = vinculoAtivo.getDiscente();
			
			if (obj.getIdPerfil() != null) {
				perfil = PerfilPessoaDAO.getDao().get(obj.getIdPerfil());
			}
		}
	}

	/**
	 * Reseta a variável que indica as turmas abertas.
	 * <br />
	 * Método não invocado por JSP.
	 * 
	 */
	public void clear(){
		turmasAbertas =  null;
		ultimasAtividadesTurma = null;
	}

	/**
	 * Carrega numa collection as turmas do aluno.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *		<li>sigaa.war\portais\discente\turmas.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> getTurmas() throws DAOException {
		if (turmas == null) {
			TurmaDao turmaDao = getDAO(TurmaDao.class);
			turmas = turmaDao.findAllByDiscente(getDiscenteUsuario().getDiscente(), SituacaoMatricula.getSituacoesMatriculadoOuConcluido().toArray(new SituacaoMatricula[0]), new SituacaoTurma[] { new SituacaoTurma(SituacaoTurma.CONSOLIDADA), new SituacaoTurma(SituacaoTurma.ABERTA), new SituacaoTurma(SituacaoTurma.A_DEFINIR_DOCENTE) });
			
			// Passa por todas as turmas do aluno, trocando as subturmas por turmas agrupadoras.
			List <Turma> turmasARemover = new ArrayList <Turma> ();
			List <Turma> turmasATrocar = new ArrayList <Turma> ();
			List <Integer> indices = new ArrayList <Integer> ();
			
			for (int i = 0; i < turmas.size(); i++){
				Turma t = turmas.get(i);
				
				if (t.getTurmaAgrupadora() != null){
					indices.add(i);
					turmasARemover.add(t);
					turmasATrocar.add(turmaDao.findByPrimaryKeyOtimizado(t.getTurmaAgrupadora().getId()));
				}
			}
			
			if (!turmasATrocar.isEmpty()){
				for (int i = 0; i < turmasATrocar.size(); i++){
					turmas.remove(turmasARemover.get(i));
					turmas.add(indices.get(i), turmasATrocar.get(i));
				}
			}	
			
			setDirBase("/portais/turma/");
		}
		return turmas;
	}

	/**
	 * Retorna todas as turmas abertas.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war\portais\turma\cabecalho.jsp</li>
	 *    <li>sigaa.war\portais\discente\discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> getTurmasAbertas() throws DAOException {
		if (turmasAbertas == null && getDiscenteUsuario() != null && isPassivelEmissaoAtestadoMatricula()) {
			turmasAbertas = new ArrayList<Turma>();

			CalendarioAcademico cal = getCalendarioVigente();
				
			if ( cal == null ) return turmasAbertas;
			if (obj.isInfantil()) cal.setPeriodo(0);		
			
			turmasAbertas = getDAO(PortalDiscenteDao.class).findTurmasMatriculado(obj, cal);

			AgendaTurmaMBean agendaBean = getMBean("agendaTurmaBean");
			agendaBean.setTurmasAbertas(turmasAbertas);	
		}
		return turmasAbertas;
	}
	
	/**
	 * Como o bean está em session, é preciso limpar a variável que lista as turmas para
	 * que esta seja atualizada.
	 * <br />
	 * Método não invocado por JSP.
	 * 
	 */
	public void resetTurmasAbertas() {
		turmasAbertas = null;
	}

	/**
	 * Retorna todas as turmas abertas.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war\ensino\falta_docente\form.jsp</li>
	 * </ul> 
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getTurmasAbertasCombo()	{
		try	{
			return toSelectItems(getTurmasAbertas(), "id", "disciplina.nome");
		}
		catch(DAOException e) {
			notifyError(e);
		}
		return null;
	}

	/**
	 * Retorna as próximas atividades das turmas que o discente está envolvido.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li> sigaa.war/portais/discente/discente.jsp </li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<DataAvaliacao> getProximasAtividades() throws DAOException {
		
		if (atividades == null){
			atividades = new ArrayList <DataAvaliacao> ();
		
		if (getDiscenteUsuario() != null) {
			
			AvaliacaoDao dao = null;
			TarefaTurmaDao tDao = null;
			QuestionarioTurmaDao qDao = null;
			
			try {
				
				dao = getDAO(AvaliacaoDao.class);
				tDao = getDAO(TarefaTurmaDao.class);
				qDao = getDAO(QuestionarioTurmaDao.class);
				
				List <DataAvaliacao> datasAvaliacoes = (List<DataAvaliacao>) dao.findAvaliacoesData(
						Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosGraduacao.QTD_DIAS_TRAS_ATIVIDADES)),
						Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosGraduacao.QTD_DIAS_FRENTE_ATIVIDADES)),
						getDiscenteUsuario().getDiscente(), getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo() );
				List <TarefaTurma> tarefas = tDao.findTarefasData(
						Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosGraduacao.QTD_DIAS_TRAS_ATIVIDADES)),
						Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosGraduacao.QTD_DIAS_FRENTE_ATIVIDADES)),
						getDiscenteUsuario().getDiscente(), getUsuarioLogado(), getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo() );			
				List<Integer> tarefasRespondidas = tDao.findTarefasRespondidasByUsuario(tarefas,getDiscenteUsuario().getDiscente(), getUsuarioLogado());
				List<QuestionarioTurma> questionarios = qDao.findQuestionariosData(
						Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosGraduacao.QTD_DIAS_TRAS_ATIVIDADES)),
						Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosGraduacao.QTD_DIAS_FRENTE_ATIVIDADES)),
						getDiscenteUsuario().getDiscente(), getUsuarioLogado(), getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo());
				
				// Índice das avaliações
				int i = 0;
				// Índice das tarefas
				int j = 0;
				// Índice dos questionários
				int k = 0;
				
				// Booleanos que indicam se é possível percorrer as listas de atividades 
				boolean a = !isEmpty(datasAvaliacoes); 
				boolean t = !isEmpty(tarefas);
				boolean q = !isEmpty(questionarios);
				
				while ( a || t || q ){
					
					a = i < datasAvaliacoes.size();
					t = j < tarefas.size();
					q = k < questionarios.size();
					
					// Data do término das atividades, usada para ordenação
					Long aTime = a ? datasAvaliacoes.get(i).getData().getTime() : null;
					Long tTime = t ? tarefas.get(j).getDataEntrega().getTime() : null;
					Long qTime = q ? questionarios.get(k).getFim().getTime() : null;
				
					// Booleanos que indicam qual atividade será adicionada.
					boolean aAdd = aTime != null;
					boolean tAdd = tTime != null;
					boolean qAdd = qTime != null;
					
					// Verifica qual atividade deve ser adicionada; Verifica nulls para eliminar warnings
					if (aAdd && tAdd && aTime != null && tTime != null){
						if (aTime >= tTime)	tAdd = false;
						else aAdd = false;
					}
					
					if (aAdd && qAdd && aTime != null && qTime != null){
						if (aTime >= qTime)	qAdd = false;
						else aAdd = false;
					}
					
					if (tAdd && qAdd && tTime != null && qTime != null){
						if (tTime >= qTime)	tAdd = false;
						else qAdd = false;
					}
					
					// Adiciona as atividades na lista e incrementa o index;
					if (aAdd){
						atividades.add(datasAvaliacoes.get(i));
						i++;
					} else if (tAdd){
						DataAvaliacao da = DataAvaliacaoHelper.criar(tarefas.get(j),tarefasRespondidas);
						atividades.add(da);
						j++;
					} else if (qAdd){
						DataAvaliacao da = DataAvaliacaoHelper.criar(questionarios.get(k));
						atividades.add(da);
						k++;
					} else
						break;
				}
						
			} catch (Exception e) {
				tratamentoErroPadrao(e);
			} finally {
				if (dao != null)
					dao.close();
				if (tDao != null)
					tDao.close();
				if (qDao != null)
					qDao.close();
			}
		}
		}
		return atividades;
	}

	/**
	* Carrega em uma collection as atividades de determinada turma.
	* <br />
	* Método chamado pela(s) seguinte(s) JSP(s):
	* <ul>
	* 		<li> sigaa.war/portais/discente/discente.jsp </li>
	* </ul>
	* 
	* @return
	* @throws DAOException
	* 
	* */	 
	public Collection<RegistroAtividadeTurma> getAtividadesTurmas() throws DAOException {

		TurmaVirtualDao dao = getDAO(TurmaVirtualDao.class);
		return dao.findAtividadesTurmas(getDiscenteUsuario().getDiscente(),
				getCalendarioVigente().getAno(),
				getCalendarioVigente().getPeriodo());

	}

	/**
	 * Emite o atestado de matrícula.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
 	 * 		<li> sigaa.war/portais/discente/menu_discente.jsp </li>
 	 * </ul>
 	 * 
	 * @return
	 * @throws DAOException
	 * @throws IOException
	 * @throws SegurancaException
	 */
	public String atestadoMatricula() throws DAOException, IOException, SegurancaException {
		DiscenteAdapter discente = getDiscenteUsuario();
		
		if (discente == null) {
			addMensagemWarning( "Você não está acessando com um vínculo de discente!");
			return null;
		}
		
		return geracaoAtestadoMatricula(discente);
	}

	/**
	 * Geração do Atestando de matrícula para os gestores da Assistência ao Estudante. 
	 *
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
 	 * 		<li> /SIGAA/app/sigaa.ear/sigaa.war/sae/relatorios/view_discentes.jsp </li>
 	 * </ul>
 	 * 
	 * @return
	 * @throws DAOException
	 * @throws IOException
	 * @throws SegurancaException
	 */
	public String atestadoMatriculaGestorSae() throws DAOException, IOException, SegurancaException {
		int idDiscente = getParameterInt("idDiscente", 0);
		DiscenteDao dao = getDAO(DiscenteDao.class);
		DiscenteAdapter discente;
		
		try {
			discente = dao.findByPK(idDiscente);
		} finally {
			dao.close();
		}
		
		return geracaoAtestadoMatricula(discente);
	}
	
	/**
	 * Emite o atestado de matrícula de turmas de férias.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
 	 * 		<li> sigaa.war/portais/discente/menu_discente.jsp </li>
 	 * </ul>
 	 * 
	 * @return
	 * @throws DAOException
	 * @throws IOException
	 * @throws SegurancaException
	 */
	public String atestadoMatriculaTurmaFerias() throws DAOException, IOException, SegurancaException {
		DiscenteAdapter discente = getDiscenteUsuario();
		
		if (discente == null) {
			addMensagemWarning( "Você não está acessando com um vínculo de discente!");
			return null;
		}
		
		if ( discente.isGraduacao() || discente.isStricto() || discente.isLato() 
				|| discente.isTecnico() || discente.isResidencia() || discente.isFormacaoComplementar() )  {
				AtestadoMatriculaMBean atestado = (AtestadoMatriculaMBean) getMBean("atestadoMatricula");
				getCurrentSession().setAttribute("atestadoLiberado", discente.getId());
				atestado.setDiscente(discente);
				atestado.setAtestadoMatriculaFerias(true);
				return atestado.selecionaDiscente();
		}
			
		addMensagemErro("Ainda não é possível visualizar o atestado de matrícula para o seu nível de ensino.");
		return null;
		
	}
	
	/**
	 * Método responsável pela geração do atestado de matrícula dos discentes
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	private String geracaoAtestadoMatricula(DiscenteAdapter discente)
			throws DAOException, SegurancaException {
		if ( discente.isGraduacao() || discente.isStricto() || discente.isLato() 
			|| discente.isTecnico() || discente.isResidencia() || discente.isFormacaoComplementar() )  {
			AtestadoMatriculaMBean atestado = (AtestadoMatriculaMBean) getMBean("atestadoMatricula");
			getCurrentSession().setAttribute("atestadoLiberado", discente.getId());
			atestado.setDiscente(discente);
			return atestado.selecionaDiscente();
		}
		
		addMensagemErro("Ainda não é possível visualizar o atestado de matrícula para o seu nível de ensino.");
		return null;
	}

	/**
	 * Gera o histórico do discente logado.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li> sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws Exception
	 */
	public String historico() throws Exception {
		HistoricoMBean historico = (HistoricoMBean) getMBean("historico");
		return historico.selecionaDiscente();
	}

	/**
	 * Retorna uma lista de Fichas de Avaliação de um dado discente 
	 * da modalidade de ensino EaD.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li> sigaa.war/portais/discente/discente.jsp </li>
	 * </ul>
	 */
	public List<FichaAvaliacaoEad> getFichasAvaliacaoDiscente() throws DAOException {
		FichaAvaliacaoEadDao dao = getDAO(FichaAvaliacaoEadDao.class);
		return dao.findFichasAvaliacaoByDiscente(getDiscenteUsuario().getDiscente());
	}
	
	/**
	 * Retorna uma lista de Turmas Virtuais habilitadas
	 * referente a um determinado usuário.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li> sigaa.war/portais/discente/discente.jsp </li>
	 * </ul>
	 */
	public List<Turma> getTurmasVirtuaisHabilitadas() throws DAOException {
		if (turmasVirtuaisHabilitadas == null) {
			PermissaoAvaDao dao = getDAO(PermissaoAvaDao.class);
			turmasVirtuaisHabilitadas = dao.findTurmasHabilitadasByPessoaOtimizado(getUsuarioLogado().getPessoa());          		            			
		}
		
		return turmasVirtuaisHabilitadas;
	}

	/**
	* Inicializa a entrada em uma comunidade virtual.
	* <br />
	* Método chamado pela(s) seguinte(s) JSP(s):
	* <ul>
	* 		<li> sigaa.war/portais/discente/discente.jsp </li>
	* </ul>
	 * @throws ArqException 
	* 
	* */
	public String entrarComunidadeVirtual() throws ArqException {
		ComunidadeVirtualMBean comunidadeVirtualMBean = getMBean("comunidadeVirtualMBean");
		return comunidadeVirtualMBean.entrar();
	}
	
	public boolean isAptoPreTesteAvaliacao() {
		return getDiscenteUsuario().isAtivo() && getDiscenteUsuario().isGraduacao() && !getDiscenteUsuario().isDiscenteEad();
	}

	/**
	 * Carrega dados dos alunos da pós-graduação.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/portais/discente/discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getCarregaDadosAlunoPos() throws DAOException {

		if ( getUsuarioLogado().getDiscenteAtivo().isStricto() ) {

			DiscenteStricto ds = (DiscenteStricto) getUsuarioLogado().getDiscenteAtivo();
			getGenericDAO().initialize(ds);

			if ( ds.getArea() != null)
				ds.getArea().getDenominacao();
			if ( ds.getLinha() != null )
				ds.getLinha().getDenominacao();

			// Carregar informações de orientação
			OrientacaoAcademicaDao orientacaoDao = getDAO(OrientacaoAcademicaDao.class);
			ds.setOrientacao( orientacaoDao.findOrientadorAtivoByDiscente(ds.getId()) );
		}

		return "";

	}
	
	/**
	 * Verifica se o discente possui permissão para visualizar atestados de matrícula
	 * <br />
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/portais/discente/discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public boolean isPassivelEmissaoAtestadoMatricula() throws DAOException {
		if (passivelEmissaoAtestadoMatricula == null) {
			passivelEmissaoAtestadoMatricula = false;
			if ( getUsuarioLogado().getDiscenteAtivo() != null)  {
				passivelEmissaoAtestadoMatricula =  AutenticacaoUtil.isDocumentoLiberado(TipoDocumentoAutenticado.ATESTADO, obj.getNivel());
			} 
		}
		return passivelEmissaoAtestadoMatricula;
	}
	
	/**
	 * Verifica se deve ser exibido o relatório de índices para o discente
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/portais/discente/discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public boolean isPassivelEmissaoRelatorioIndices() throws DAOException {
		if (passivelEmissaoRelatoriosIndices == null) {
			if(obj != null) {
				MovimentacaoAluno movSaida = getDAO(MovimentacaoAlunoDao.class).findConclusaoByDiscente(obj.getId());
				obj.setMovimentacaoSaida(movSaida);
				passivelEmissaoRelatoriosIndices =  obj.isGraduacao() && ((DiscenteGraduacao) obj).isPassivelCalculoIndices();
			} else {
				passivelEmissaoRelatoriosIndices = false;
			}
		}
		
		return passivelEmissaoRelatoriosIndices;
	}

	/**
	 * Retorna uma coleção de Índice Acadêmico
	 * referente a um dado discente.
	* <br />
	* Método chamado pela(s) seguinte(s) JSP(s):
	* <ul>
	* 		<li>/sigaa.war/portais/discente/discente.jsp</li>
	* </ul>
	 */
	public Collection<IndiceAcademicoDiscente> getIndicesAcademicoDicente() throws DAOException {
		if (indicesAcademicosDiscente == null && obj != null) {
			indicesAcademicosDiscente = getDAO(IndiceAcademicoDao.class).findIndicesAcademicoDiscente(obj);
		}
		return indicesAcademicosDiscente;
	}
	
	/**
	 * Indica se o portal discente será exibido no modo reduzido (sem exibir todas as informações,
	 * como foruns, turmas do semestre, etc.).
	* <br />
	* Método chamado pela(s) seguinte(s) JSP(s):
	* <ul>
	* 		<li>/sigaa.war/portais/discente/discente.jsp</li>
	* </ul>
	 * @return
	 */
	public boolean isModoReduzido() {
		return ParametroHelper.getInstance().getParametroBoolean(ParametrosGerais.PORTAL_DISCENTE_MODO_REDUZIDO);
	}
	
	/**
	 * Carrega as últimas Atividades das Turmas Abertas.
	 *
	 *  <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
 	 * 		<li>Não Invocado por JSP.</li>
 	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<RegistroAtividadeTurma> getUltimasAtividadesTurmas() throws DAOException {
		TurmaVirtualDao dao = getDAO(TurmaVirtualDao.class);
		
		try {
			Collection<Turma> turmasDiscente = getTurmasAbertas();
			if(!isEmpty(turmasDiscente) && isEmpty(ultimasAtividadesTurma))
				ultimasAtividadesTurma = dao.findUltimasAtividadesTurmas(turmasDiscente);
		} finally {
			dao.close();
		}
		
		return ultimasAtividadesTurma == null ? new ArrayList<RegistroAtividadeTurma>() : ultimasAtividadesTurma;
	}

	/** 
	 * Método responsável por verificar a regularização do CPF do discente.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
 	 * 	 <li>Não Invocado por JSP.</li>
 	 * </ul>
	 */
	public void validacaoCpfDiscente(){
		if ( !getDiscenteUsuario().getDiscente().getPessoa().isInternacional() ) {
			ListaMensagens erros = new ListaMensagens();
			ValidatorUtil.validateRequired(getDiscenteUsuario().getDiscente().getPessoa().getCpf_cnpj(), "CPF", erros);
			if( getDiscenteUsuario().getDiscente().getPessoa().getCpf_cnpj() != null )
				ValidatorUtil.validateCPF_CNPJ(getDiscenteUsuario().getDiscente().getPessoa().getCpf_cnpj(), "CPF", erros);
			if (!erros.isEmpty()) {
				addMensagem(MensagensGerais.CPF_DISCENTE_INVALIDO, TipoMensagemUFRN.WARNING, 
						getDiscenteUsuario().getDiscente().getPessoa().getCpf_cnpj(),	
						responsavelAlteracaoDadosPessoaisAluno());
				return;
			}
		}	
	}
	
	/** 
	 * Retorna o setor responsável pela alteração completa dos dados pessoais do aluno.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
 	 * 	 <li>Não Invocado por JSP.</li>
 	 * </ul>
	 */ 
	public String responsavelAlteracaoDadosPessoaisAluno(){
 		String responsavel = "a Coordenação do Curso";
 		
		 ParametroHelper parametroHelper = ParametroHelper.getInstance();
		 if (getDiscenteUsuario().getDiscente().getNivel() == NivelEnsino.GRADUACAO 
		 		&& !parametroHelper.getParametroBoolean(ParametrosGraduacao.PERMITE_ALTERACAO_COMPLETA_DADOS_PESSOAIS)) {
		 	responsavel = "ao "+parametroHelper.getParametro(ParametrosGraduacao.SIGLA_NOME_ADM_ESCOLAR);
		 }
 
		return responsavel;
	}
	
	public PerfilPessoa getPerfil() {
		return perfil;
	}

	public void setPerfil(PerfilPessoa perfil) {
		this.perfil = perfil;
	}

	public String getNomeTurma() {
		return nomeTurma;
	}

	public void setNomeTurma(String nomeTurma) {
		this.nomeTurma = nomeTurma;
	}
	public boolean isRedeUfrnAtiva(){
		return Sistema.isSistemaAtivo(Sistema.REDE_SOCIAL);
	}
}
