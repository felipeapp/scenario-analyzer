/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 13/09/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.autenticacao.AutenticacaoUtil;
import br.ufrn.arq.seguranca.autenticacao.TipoDocumentoAutenticado;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dao.PerfilPessoaDAO;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.sigaa.agenda.jsf.AgendaTurmaMBean;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ead.FichaAvaliacaoEadDao;
import br.ufrn.sigaa.arq.dao.ensino.DocenteTurmaDao;
import br.ufrn.sigaa.arq.dao.ensino.HorarioDao;
import br.ufrn.sigaa.arq.dao.ensino.MovimentacaoAlunoDao;
import br.ufrn.sigaa.arq.dao.graduacao.IndiceAcademicoDao;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ava.dao.AvaliacaoDao;
import br.ufrn.sigaa.ava.dao.TarefaTurmaDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.DataAvaliacao;
import br.ufrn.sigaa.ava.dominio.RegistroAtividadeTurma;
import br.ufrn.sigaa.ava.dominio.TarefaTurma;
import br.ufrn.sigaa.cv.jsf.ComunidadeVirtualMBean;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.dominio.VinculoUsuario;
import br.ufrn.sigaa.ead.dominio.FichaAvaliacaoEad;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.IndiceAcademicoDiscente;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.medio.dao.TurmaMedioDao;
import br.ufrn.sigaa.ensino.medio.dao.TurmaSerieAnoDao;
import br.ufrn.sigaa.ensino.medio.dominio.SituacaoMatriculaSerie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerieAno;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.mensagens.MensagensGerais;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.portal.dao.PortalDiscenteDao;

/**
 * Managed Bean de controle do portal discente do ensino m�dio.
 * 
 * @author Rafael Gomes Rodrigues
 *
 */

@Component("portalDiscenteMedio") @Scope("session")
public class PortalDiscenteMedioMBean extends SigaaAbstractController<Discente> {

	/** Constante que define a p�gina principal do discente. */
	public static final String PORTAL_DISCENTE = "/portais/discente/medio/discente_medio.jsp";

	/** Define os dados do perfil do usu�rio discente */
	private PerfilPessoa perfil;

	// usado para exibir com JSTLR
	/** Define o nome da turma selecionada pelo discente */
	private String nomeTurma;

	/** Define a lista de disciplinas abertas exibidas na p�gina principal do discente de m�dio. */
	private List<TurmaSerieAno> disciplinasAbertas;
	
	/** Define a lista de turmas abertas exibidas na p�gina principal do discente. */
	private Collection<Turma> turmasAbertas;

	/** Define a lista de turmas associadas ao discente. */
	private List<Turma> turmas;
	
	/** Define a lista de turmas virtuais associadas ao discente. */
	private List<Turma> turmasVirtuaisHabilitadas;

	/** Define a lista das avalia��es associadas ao discente na p�gina principal do discente. */
	public List <DataAvaliacao> atividades;
	
	/** Define se o discente possui permiss�o para visualizar atestados de matr�cula */
	private Boolean passivelEmissaoAtestadoMatricula;
	
	/** Define se deve ser exibido o relat�rio de �ndices para o discente */
	private Boolean passivelEmissaoRelatoriosIndices;

	/** Define a lista de �ndice Acad�mico associado ao discente. */
	private Collection<IndiceAcademicoDiscente> indicesAcademicosDiscente;
	
	/** Armazena as ultimas atividades realizadas nas turmas virtuais do discente. */
	Collection<RegistroAtividadeTurma> ultimasAtividadesTurma;

	public PortalDiscenteMedioMBean() throws DAOException {
		VinculoUsuario vinculoAtivo = getUsuarioLogado().getVinculoAtivo();
		if (vinculoAtivo != null && vinculoAtivo.isVinculoDiscente() && vinculoAtivo.getDiscente() != null) 
			obj = vinculoAtivo.getDiscente().getDiscente();
		if (vinculoAtivo != null && vinculoAtivo.isVinculoDiscente() && vinculoAtivo.getDiscente().getIdPerfil() != null)
			perfil = PerfilPessoaDAO.getDao().get(vinculoAtivo.getDiscente().getIdPerfil());
	}

	/**
	 * Reseta a vari�vel que indica as turmas abertas.
	 * <br />
	 * M�todo n�o invocado por JSP.
	 * 
	 */
	public void clear(){
		turmasAbertas =  null;
		ultimasAtividadesTurma = null;
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

	/**
	 * Retorna todas as turmas abertas.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war\portais\discente\medio\discente_medio.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<TurmaSerieAno> getDisciplinasAbertas() throws DAOException {
		
		int ano = 0;
		
		if ( disciplinasAbertas == null && getDiscenteUsuario() != null && isPassivelEmissaoAtestadoMatricula()) {
			disciplinasAbertas = new ArrayList<TurmaSerieAno>();

			CalendarioAcademico cal = getCalendarioVigente();

			// Evitar erros para unidades sem par�metros
			if ( cal == null ) return disciplinasAbertas;

			ano = cal.getAno();
			
			TurmaSerieAnoDao tsaDao = getDAO(TurmaSerieAnoDao.class);
			disciplinasAbertas = tsaDao.findByDiscenteAno(getDiscenteUsuario().getDiscente(),SituacaoMatricula.getSituacoesAtivas(), SituacaoMatriculaSerie.getSituacoesMatriculadoOuConcluido(), ano, 
					new SituacaoTurma(SituacaoTurma.ABERTA), new SituacaoTurma(SituacaoTurma.A_DEFINIR_DOCENTE), new SituacaoTurma(SituacaoTurma.CONSOLIDADA));

			// recuperando os hor�rios das turmas que permitem hor�rio flex�vel
			if (!isEmpty(disciplinasAbertas)) {
				HorarioDao horarioDao = getDAO(HorarioDao.class);
				for (TurmaSerieAno tsa : disciplinasAbertas) {
					if (tsa.getTurma().getDisciplina().isPermiteHorarioFlexivel()) {
						Turma turma = horarioDao.refresh(tsa.getTurma());
						tsa.getTurma().setHorarios(turma.getHorarios());
						tsa.getTurma().getHorarios().iterator();
					}
				}
			}
			
			DocenteTurmaDao daoDocenteTurma = getDAO(DocenteTurmaDao.class);
			UsuarioDao daoUsuario = getDAO(UsuarioDao.class);
			for (Iterator<TurmaSerieAno> it = disciplinasAbertas.iterator(); it.hasNext(); ) {
				TurmaSerieAno tsa = it.next();
				
				List<DocenteTurma> docentes = daoDocenteTurma.findDocentesByTurma(tsa.getTurma());
				tsa.getTurma().setDocentesTurmas(CollectionUtils.toSet(docentes));
				for (DocenteTurma dt : tsa.getTurma().getDocentesTurmas()) {
					Usuario usuario = null;
					
					if (dt.getDocenteExterno() == null) {
						usuario = daoUsuario.findByPessoaParaEmail(dt.getDocente().getPessoa());
					} else {
						usuario = daoUsuario.findByPessoaParaEmail(dt.getDocenteExterno().getPessoa());
					}
					
					dt.setUsuario(usuario);
				}
			}
			
			// Carrega o bean que auxilia a exibi��o da agenda de hor�rios de uam turma.
			AgendaTurmaMBean agendaBean = getMBean("agendaTurmaBean");
			agendaBean.setTurmasAbertas(turmasAbertas);	
		}
		return disciplinasAbertas;
	}
	
	
	/**-----------------------------------*/
	
	/**
	 * Carrega numa collection as turmas do aluno.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *		<li>sigaa.war\portais\discente\medio\turmas.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> getTurmas() throws DAOException {
		if (turmas == null) {
			TurmaMedioDao turmaDao = getDAO(TurmaMedioDao.class);
			turmas = turmaDao.findAllByDiscente(getDiscenteUsuario().getDiscente(), SituacaoMatricula.getSituacoesMatriculadoOuConcluido().toArray(new SituacaoMatricula[0]), new SituacaoTurma[] { new SituacaoTurma(SituacaoTurma.CONSOLIDADA), new SituacaoTurma(SituacaoTurma.ABERTA), new SituacaoTurma(SituacaoTurma.A_DEFINIR_DOCENTE) });
			
			setDirBase("/portais/turma/");
		}
		return turmas;
	}

	/**
	 * Retorna todas as turmas abertas.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war\portais\turma\cabecalho.jsp</li>
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

			turmasAbertas = getDAO(PortalDiscenteDao.class).findTurmasMatriculado(obj, cal);

			AgendaTurmaMBean agendaBean = getMBean("agendaTurmaBean");
			agendaBean.setTurmasAbertas(turmasAbertas);	
		}
		return turmasAbertas;
	}
	
	/**
	 * Como o bean est� em session, � preciso limpar a vari�vel que lista as turmas para
	 * que esta seja atualizada.
	 * <br />
	 * M�todo n�o invocado por JSP.
	 * 
	 */
	public void resetTurmasAbertas() {
		turmasAbertas = null;
	}

	/**
	 * Retorna todas as turmas abertas.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Retorna as pr�ximas atividades das turmas que o discente est� envolvido.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
			
			try {
				
				dao = getDAO(AvaliacaoDao.class);
				tDao = getDAO(TarefaTurmaDao.class);
				
				List <DataAvaliacao> datasAvaliacoes = (List<DataAvaliacao>) dao.findAvaliacoesData(
						Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosGraduacao.QTD_DIAS_TRAS_ATIVIDADES)),
						Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosGraduacao.QTD_DIAS_FRENTE_ATIVIDADES)),
						getDiscenteUsuario().getDiscente(), getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo() );
				List <TarefaTurma> tarefas = tDao.findTarefasData(
						Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosGraduacao.QTD_DIAS_TRAS_ATIVIDADES)),
						Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosGraduacao.QTD_DIAS_FRENTE_ATIVIDADES)),
						getDiscenteUsuario().getDiscente(), getUsuarioLogado(), getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo() );
				
				int i = 0;
				int j = 0;
				
				// Se n�o h� avalia��es, adiciona todas as tarefas
				if (datasAvaliacoes.isEmpty()){
					for (TarefaTurma tt : tarefas){
						DataAvaliacao da = new DataAvaliacao ();
	
						da.setData(tt.getDataEntrega());
						da.setTurma(tt.getAula().getTurma());
						da.setIdTarefa(tt.getId());
						da.setDescricao(tt.getNome());
						
						if (!tt.getRespostas().isEmpty())
							da.setConcluida(true);
						
						atividades.add(da);
					}
					
				// Se n�o h� tarefas, adiciona todas as avalia��es
				} else if (tarefas.isEmpty()){
					atividades.addAll(datasAvaliacoes);
					
				// Se h� tanto avalia��es quanto tarefas, adiciona na ordem da data.
				} else
					while (i < datasAvaliacoes.size() || j < tarefas.size()){
						
						if (j >= tarefas.size() || i < datasAvaliacoes.size() && datasAvaliacoes.get(i).getData().getTime() <= tarefas.get(j).getDataEntrega().getTime())
							atividades.add(datasAvaliacoes.get(i++));
						else {
							TarefaTurma tt = tarefas.get(j++);
							DataAvaliacao da = new DataAvaliacao ();
	
							da.setData(tt.getDataEntrega());
							da.setTurma(tt.getAula().getTurma());
							da.setIdTarefa(tt.getId());
							da.setDescricao(tt.getNome());

							if (!tt.getRespostas().isEmpty())
								da.setConcluida(true);
							
							atividades.add(da);
						}
					}
				
			} catch (Exception e) {
				tratamentoErroPadrao(e);
			} finally {
				if (dao != null)
					dao.close();
			}
		}
		}
		return atividades;
	}

	/**
	* Carrega em uma collection as atividades de determinada turma.
	* <br />
	* M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Emite o atestado de matr�cula.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
			addMensagemWarning( "Voc� n�o est� acessando com um v�nculo de discente!");
			return null;
		}
		
		return geracaoAtestadoMatricula(discente);
	}

	/**
	 * Gera��o do Atestando de matr�cula para os gestores da Assist�ncia ao Estudante. 
	 *
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * M�todo respons�vel pela gera��o do atestado de matr�cula dos discentes
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	private String geracaoAtestadoMatricula(DiscenteAdapter discente)
			throws DAOException, SegurancaException {
		if ( discente.isMedio() )  {
			AtestadoMatriculaMedioMBean atestado = (AtestadoMatriculaMedioMBean) getMBean("atestadoMatriculaMedio");
			getCurrentSession().setAttribute("atestadoLiberado", discente.getId());
			atestado.setDiscente(discente);
			return atestado.selecionaDiscente();
		}
		
		addMensagemErro("Ainda n�o � poss�vel visualizar o atestado de matr�cula para o seu n�vel de ensino.");
		return null;
	}

	/**
	 * Gera o hist�rico do discente logado.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li> sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws Exception
	 */
	public String historico() throws Exception {
		HistoricoMedioMBean historico = (HistoricoMedioMBean) getMBean("historicoMedio");
		return historico.selecionaDiscenteForm();
	}

	/**
	 * Retorna uma lista de Fichas de Avalia��o de um dado discente 
	 * da modalidade de ensino EaD.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * referente a um determinado usu�rio.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li> sigaa.war/portais/discente/discente.jsp </li>
	 * </ul>
	 */
	public List<Turma> getTurmasVirtuaisHabilitadas() throws DAOException {
		if (turmasVirtuaisHabilitadas == null) {
			TurmaVirtualDao dao = getDAO(TurmaVirtualDao.class);
			turmasVirtuaisHabilitadas = dao.findTurmasHabilitadasByPessoa(getUsuarioLogado().getPessoa());
			
			Collections.sort (turmasVirtuaisHabilitadas, new Comparator<Turma>() {  
	            public int compare(Turma t1, Turma t2) {      		                 
					return new CompareToBuilder()
					.append(t2.getAnoPeriodo(), t1.getAnoPeriodo())
					.append(t1.getNome(), t2.getNome())
					.toComparison();    		                 
	            }
	       });              		            			
		}
		
		return turmasVirtuaisHabilitadas;
	}

	/**
	* Inicializa a entrada em uma comunidade virtual.
	* <br />
	* M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Carrega dados dos alunos da p�s-gradua��o.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/portais/discente/discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getCarregaDadosAlunoPos() throws DAOException {

		if ( getUsuarioLogado().getDiscenteAtivo().isStricto() ) {

			GenericDAO dao = getGenericDAO();
			DiscenteStricto ds = (DiscenteStricto) getUsuarioLogado().getDiscenteAtivo();

			dao.initialize(ds);

			if ( ds.getArea() != null)
				ds.getArea().getDenominacao();
			if ( ds.getLinha() != null )
				ds.getLinha().getDenominacao();

			// Carregar informa��es de orienta��o
			OrientacaoAcademicaDao orientacaoDao = getDAO(OrientacaoAcademicaDao.class);
			ds.setOrientacao( orientacaoDao.findOrientadorAtivoByDiscente(ds.getId()) );
		}

		return "";

	}
	
	/**
	 * Verifica se o discente possui permiss�o para visualizar atestados de matr�cula
	 * <br />
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/portais/discente/discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public boolean isPassivelEmissaoAtestadoMatricula() throws DAOException {
		if (passivelEmissaoAtestadoMatricula == null) {
			if ( getUsuarioLogado().getDiscenteAtivo() != null)  {
				passivelEmissaoAtestadoMatricula =  AutenticacaoUtil.isDocumentoLiberado(TipoDocumentoAutenticado.ATESTADO, getUsuarioLogado().getDiscenteAtivo().getNivel());
			} else {
				passivelEmissaoAtestadoMatricula = false;
			}
		}
		return passivelEmissaoAtestadoMatricula;
	}
	
	/**
	 * Verifica se deve ser exibido o relat�rio de �ndices para o discente
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/portais/discente/discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public boolean isPassivelEmissaoRelatorioIndices() throws DAOException {
		if (passivelEmissaoRelatoriosIndices == null) {
			DiscenteAdapter discente = getDiscenteUsuario();
			if(discente != null) {
				MovimentacaoAluno movSaida = getDAO(MovimentacaoAlunoDao.class).findConclusaoByDiscente(discente.getId());
				discente.setMovimentacaoSaida(movSaida);
				passivelEmissaoRelatoriosIndices =  discente.isGraduacao() && ((DiscenteGraduacao) discente).isPassivelCalculoIndices();
			} else {
				passivelEmissaoRelatoriosIndices = false;
			}
		}
		
		return passivelEmissaoRelatoriosIndices;
	}

	/**
	 * Retorna uma cole��o de �ndice Acad�mico
	 * referente a um dado discente.
	* <br />
	* M�todo chamado pela(s) seguinte(s) JSP(s):
	* <ul>
	* 		<li>/sigaa.war/portais/discente/discente.jsp</li>
	* </ul>
	 */
	public Collection<IndiceAcademicoDiscente> getIndicesAcademicoDicente() throws DAOException {
		if (indicesAcademicosDiscente == null && getUsuarioLogado().getDiscenteAtivo() != null) {
			indicesAcademicosDiscente = getDAO(IndiceAcademicoDao.class).findIndicesAcademicoDiscente(getUsuarioLogado().getDiscenteAtivo());
		}
		return indicesAcademicosDiscente;
	}
	
	/**
	 * Indica se o portal discente ser� exibido no modo reduzido (sem exibir todas as informa��es,
	 * como foruns, turmas do semestre, etc.).
	* <br />
	* M�todo chamado pela(s) seguinte(s) JSP(s):
	* <ul>
	* 		<li>/sigaa.war/portais/discente/discente.jsp</li>
	* </ul>
	 * @return
	 */
	public boolean isModoReduzido() {
		return ParametroHelper.getInstance().getParametroBoolean(ParametrosGerais.PORTAL_DISCENTE_MODO_REDUZIDO);
	}
	
	/**
	 * Carrega as �ltimas Atividades das Turmas Abertas.
	 *
	 *  <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
 	 * 		<li>N�o Invocado por JSP.</li>
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
	 * M�todo respons�vel por verificar a regulariza��o do CPF do discente.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
 	 * 	 <li>N�o Invocado por JSP.</li>
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
	 * Retorna o setor respons�vel pela altera��o completa dos dados pessoais do aluno.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
 	 * 	 <li>N�o Invocado por JSP.</li>
 	 * </ul>
	 */ 
	public String responsavelAlteracaoDadosPessoaisAluno(){
		char nivel = getDiscenteUsuario().getDiscente().getNivel();
		ParametroHelper param = ParametroHelper.getInstance();
		String coordenacao = "a Coordena��o do Curso";
		String responsavel = coordenacao;
		nivel = NivelEnsino.getNiveisStricto().contains(nivel) ? NivelEnsino.STRICTO : nivel;
		switch (nivel) {
		case NivelEnsino.GRADUACAO:
			responsavel = param.getParametroBoolean(ParametrosGraduacao.PERMITE_ALTERACAO_COMPLETA_DADOS_PESSOAIS) ?
					coordenacao : "ao "+param.getParametro(ParametrosGraduacao.SIGLA_NOME_ADM_ESCOLAR); 
			break;
		case NivelEnsino.STRICTO:
			responsavel = coordenacao;
			break;
		case NivelEnsino.LATO:
			responsavel = coordenacao;
			break;
		case NivelEnsino.TECNICO:
			responsavel = coordenacao;
			break;	
		case NivelEnsino.MEDIO:
			responsavel = coordenacao;
			break;	
		default:
			responsavel = coordenacao;
			break;
		}
		return responsavel;
	}

}
