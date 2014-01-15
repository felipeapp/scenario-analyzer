/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 26/09/2011
 *
 */
package br.ufrn.sigaa.portal.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateMaxLength;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.agenda.jsf.AgendaTurmaMBean;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ensino.DocenteTurmaDao;
import br.ufrn.sigaa.arq.dao.ensino.FrequenciaAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.HorarioDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.vinculo.dao.VinculosDao;
import br.ufrn.sigaa.ava.dao.AvaliacaoDao;
import br.ufrn.sigaa.ava.dao.TarefaTurmaDao;
import br.ufrn.sigaa.ava.dominio.DataAvaliacao;
import br.ufrn.sigaa.ava.dominio.TarefaTurma;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dao.RegraNotaDao;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.FrequenciaAluno;
import br.ufrn.sigaa.ensino.dominio.RegraNota;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.medio.dao.MatriculaDiscenteSerieDao;
import br.ufrn.sigaa.ensino.medio.dao.ObservacaoDiscenteSerieDao;
import br.ufrn.sigaa.ensino.medio.dao.TurmaSerieAnoDao;
import br.ufrn.sigaa.ensino.medio.dominio.DiscenteMedio;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaDiscenteSerie;
import br.ufrn.sigaa.ensino.medio.dominio.ObservacaoDiscenteSerie;
import br.ufrn.sigaa.ensino.medio.dominio.SituacaoMatriculaSerie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerieAno;
import br.ufrn.sigaa.ensino.medio.dominio.UsuarioFamiliar;
import br.ufrn.sigaa.ensino.medio.jsf.BoletimMedioMBean;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.util.TurmaUtil;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * MBean que controla o acesso aos dados do portal do familiar do discente.
 * 
 * @author Arlindo Rodrigues
 */
@Component("portalFamiliar") @Scope("session")
public class PortalFamiliarMBean extends SigaaAbstractController<UsuarioFamiliar> {
	
	/** Define a lista de disciplinas abertas exibidas na p�gina principal do discente de m�dio. */
	private List<TurmaSerieAno> disciplinasAbertas;	
	/** Caso o acesso seja feito pelo discente: Lista de todas as frequ�ncias de um aluno numa data. */
	private List<FrequenciaAluno> frequencias;
	/** Lista de observa��es de discente */
	private List<ObservacaoDiscenteSerie> observacoes;
	/** Lista de atividades */
	private Collection<DataAvaliacao> atividades;
	/** Discentes vinculados ao familiar */
	private List<DiscenteMedio> discentes;
	/** Discente atual selecionado */
	private DiscenteAdapter discente;
	/** indica se o discente � estrangeiro ou nao */
	private boolean internacional;
	/** Respons�veis do discente selecionado */
	private List<UsuarioFamiliar> responsaveis;
	
	/** Inicializa o objeto */
	private void initObj(){
		obj = new UsuarioFamiliar();
		obj.setDiscenteMedio(new DiscenteMedio());
		obj.getDiscenteMedio().setDiscente(new Discente());
		obj.setUsuario(new Usuario());
	}
	
	/**
	 * Emite o boletim do discente
	 * <br><br>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/portais/familiar/index.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String emitirBoletim() throws ArqException{
		
		checkRole(SigaaPapeis.FAMILIAR_MEDIO);
		
		BoletimMedioMBean bean = getMBean("boletimMedioMBean");
		bean.setDiscente(discente);
		
		MatriculaDiscenteSerieDao dao = getDAO(MatriculaDiscenteSerieDao.class);
		RegraNotaDao nDao = getDAO(RegraNotaDao.class); 
		try {
			
			CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(discente);
			
			if (cal == null){
				addMensagemErro("N�o foi definido um calend�rio acad�mico para o curso do discente.");
				return null;
			}
			
			// retorna as matriculas em s�rie do ano atual
			List<MatriculaDiscenteSerie> matriculasSerie = dao.findAllMatriculasByDiscente((DiscenteMedio) discente, 
					cal.getAno(), SituacaoMatriculaSerie.APROVADO, SituacaoMatriculaSerie.CANCELADO, 
					SituacaoMatriculaSerie.MATRICULADO, SituacaoMatriculaSerie.REPROVADO, SituacaoMatriculaSerie.TRANCADO);
			
			if (ValidatorUtil.isEmpty(matriculasSerie)){
				addMensagemErro("O discente selecionado n�o possui nenhuma matr�cula em s�rie cadastrada no ano atual.");
				return null;
			}
			
			List<RegraNota> regras = nDao.findByCurso(discente.getCurso());
			if (ValidatorUtil.isEmpty(regras)){
				addMensagemErro("N�o foram definidas as regras de consolida��o.");
				return null;
			}				
			
			bean.getObj().setRegraNotas(regras);
			bean.setMatriculasSerie(matriculasSerie);
			
			if (matriculasSerie.size() == 1){
				MatriculaDiscenteSerie matricula = matriculasSerie.get(0);
				bean.getObj().setMatriculaSerie(matricula);
				return bean.exibirBoletim();
			}
			
		} finally {
			if (dao != null)
				dao.close();
		}		
		
		return forward( bean.getListPage() );
	}
	
	/**
	 * D� acesso ao visualizar a frequ�ncia.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/portais/familiar/index.jsp</li>
	 * </ul>
	 *  
	 * @return
	 * @throws Exception 
	 */
	public String acessarFrequencia() throws Exception {
		
		checkRole(SigaaPapeis.FAMILIAR_MEDIO);
		
		TurmaDao tDao = null;
		FrequenciaAlunoDao fDao = null;
		
		try {
			int idTurma = getParameterInt("idTurma",0);

			if (idTurma == 0) 
				addMensagemErro("Turma n�o selecionada");
			
			if (hasErrors())
				return null;
			
			tDao = getDAO(TurmaDao.class);
			Turma turma = tDao.findByPrimaryKeyOtimizado(idTurma);
			
			// Se a turma que o aluno est� for agrupadora, deve buscar a frequ�ncia dele na subturma em que ele est� matriculado.
			if (turma.isAgrupadora()){
				turma = tDao.findSubturmaByTurmaDiscente(turma, discente);
			}	
				
			fDao = getDAO(FrequenciaAlunoDao.class);
			frequencias = fDao.findFrequenciasByDiscente(discente.getDiscente(), turma);
							
			//buscar os dias que s�o feriados
			List<Date> feriadosTurma = TurmaUtil.getFeriados(turma);
			
			Iterator<FrequenciaAluno> itrFrequencias = frequencias.iterator();
			Iterator<Date> itrFeriados = feriadosTurma.iterator();
						
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			c.add(Calendar.YEAR, -1);
			
			//remove os feriados de anos inferiores ao ano passado.
			while (itrFeriados.hasNext()) {
				Date d = itrFeriados.next();
				if (CalendarUtils.getAno(d) < c.get(Calendar.YEAR)) {
					itrFeriados.remove();
				}
				
			}
						
			//para cada possivel dia de aula
			while (itrFrequencias.hasNext()) {
				FrequenciaAluno aux = itrFrequencias.next();
				Date d = aux.getData();
				
				itrFeriados = feriadosTurma.iterator();
				//caso o possivel dia de aula seja um feriado remova-o da lista.
				while (itrFeriados.hasNext() ){
					if (CalendarUtils.isSameDay(d,itrFeriados.next())) {
						itrFrequencias.remove();
						break;
					}
					
				}
						
			}
		} finally {
			if (tDao != null)
				tDao.close();
			if (fDao != null)
				fDao.close();
		}
		return forward("/portais/familiar/mapa.jsp");
	}	
	
	/**
	 * Exibe as observa��es do discente
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/portais/familiar/index.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 * @throws Exception 
	 */	
	public String acessarObservacoes() throws DAOException, SegurancaException{
		
		checkRole(SigaaPapeis.FAMILIAR_MEDIO);
		
		ObservacaoDiscenteSerieDao dao = getDAO(ObservacaoDiscenteSerieDao.class);
		try {
			
			//Carrega as observa��es do discente na s�rie
			observacoes = dao.findByDiscenteAndSerie((DiscenteMedio) discente,null);
			
			if (ValidatorUtil.isEmpty(observacoes)){
				addMensagemErro("Nenhum observa��o cadastrada");
				return null;
			}
				
			return forward("/portais/familiar/observacoes.jsf");
			
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	/**
	 * Retorna todas as turmas abertas.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>/sigaa.war/portais/familiar/index.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException 
	 */
	public List<TurmaSerieAno> getDisciplinasAbertas() throws DAOException, SegurancaException {
		
		checkRole(SigaaPapeis.FAMILIAR_MEDIO);
		
		int ano = 0;
		
		if ( disciplinasAbertas == null && discente != null ) {
			disciplinasAbertas = new ArrayList<TurmaSerieAno>();

			CalendarioAcademico cal = getCalendarioVigente();

			// Evitar erros para unidades sem par�metros
			if ( cal == null ) return disciplinasAbertas;

			ano = cal.getAno();
			
			TurmaSerieAnoDao tsaDao = getDAO(TurmaSerieAnoDao.class);
			disciplinasAbertas = tsaDao.findByDiscenteAno(discente.getDiscente(),SituacaoMatricula.getSituacoesAtivas(), SituacaoMatriculaSerie.getSituacoesPositivas(), ano, 
					new SituacaoTurma(SituacaoTurma.ABERTA), new SituacaoTurma(SituacaoTurma.A_DEFINIR_DOCENTE), new SituacaoTurma(SituacaoTurma.CONSOLIDADA));			

			List<Turma> turmasAbertas = new ArrayList<Turma>();
			
			// recuperando os hor�rios das turmas que permitem hor�rio flex�vel
			if (!isEmpty(disciplinasAbertas)) {
				HorarioDao horarioDao = getDAO(HorarioDao.class);
				for (TurmaSerieAno tsa : disciplinasAbertas) {
					if (tsa.getTurma().getDisciplina().isPermiteHorarioFlexivel()) {
						Turma turma = horarioDao.refresh(tsa.getTurma());
						tsa.getTurma().setHorarios(turma.getHorarios());
						tsa.getTurma().getHorarios().iterator();
					}
					turmasAbertas.add(tsa.getTurma());
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
	
	/**
	 * Retorna as pr�ximas atividades das turmas que o discente est� envolvido.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li> sigaa.war/portais/familiar/index.jsp </li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<DataAvaliacao> getProximasAtividades() throws DAOException {
		
		if (atividades == null){
			
			atividades = new ArrayList <DataAvaliacao> ();
		
		if (discente != null) {
			
			AvaliacaoDao dao = null;
			TarefaTurmaDao tDao = null;
			
			try {
				
				dao = getDAO(AvaliacaoDao.class);
				tDao = getDAO(TarefaTurmaDao.class);
				
				List <DataAvaliacao> datasAvaliacoes = (List<DataAvaliacao>) dao.findAvaliacoesData(
						Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosGraduacao.QTD_DIAS_TRAS_ATIVIDADES)),
						Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosGraduacao.QTD_DIAS_FRENTE_ATIVIDADES)),
						discente.getDiscente(), getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo() );
				List <TarefaTurma> tarefas = tDao.findTarefasData(
						Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosGraduacao.QTD_DIAS_TRAS_ATIVIDADES)),
						Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosGraduacao.QTD_DIAS_FRENTE_ATIVIDADES)),
						discente.getDiscente(), getUsuarioLogado(), getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo() );
				
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
	 * Seleciona outro discente 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/portais/familiar/index.jsp </li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String selecionarDiscente() throws DAOException{
		
		int id = getParameterInt("idDiscente", 0);
		
		if (id == 0){
			addMensagemErro("Discente n�o selecionado.");
			return null;
		}
			
		DiscenteDao dao = getDAO(DiscenteDao.class);
		try {			
			// se selecionar o mesmo discente n�o faz nada.
			if (discente != null && discente.getId() == id)
				return null;
			
			Discente d = new Discente(id);
			d.setNivel(getNivelEnsino());
			
			DiscenteAdapter discente = dao.findDetalhesByDiscente(d);
			if (discente != null){
				setDiscente(discente);
				atividades = null;
				disciplinasAbertas = null;
				responsaveis = null;
			}
		} finally {			
			if (dao != null)
				dao.close();
		}
		
		return redirectJSF(getSubSistema().getLink());
	}
	
	/**
	 * Adicionar novo v�nculo de familiar com outro discente
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/portais/familiar/index.jsp </li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String adicionarVinculo() throws ArqException{
		
		initObj();
		
		obj.setUsuario(getUsuarioLogado());
		
		prepareMovimento(SigaaListaComando.CADASTRAR_NOVO_VINCULO);
		
		return forward("/portais/familiar/novo_vinculo.jsp"); 
	}
	
	/**
	 * Sobrescreve o cancelar para n�o resetar o bean da sess�o
	 */
	@Override
	public String cancelar() {
		
		try {
			initObj();
			redirectJSF(getSubSistema().getLink());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Confirma cadastro do v�nculo
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/portais/familiar/index.jsp </li>
	 * </ul>
	 * @return
	 */
	public String cadastrarVinculo(){
		
		validar();
		
		if (hasErrors())
			return null;
		
		if (internacional){
			discente.getPessoa().setCpf_cnpj(null);
			discente.getPessoa().getIdentidade().setNumero(null);
		}
		
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setObjAuxiliar(internacional);
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_NOVO_VINCULO);
			
			obj = executeWithoutClosingSession(mov, getCurrentRequest());
			
			addMensagemInformation("Novo v�nculo adicionado com sucesso.");
			discentes = null;
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			e.printStackTrace();
			return null;
		}		
		
		return redirectJSF(getSubSistema().getLink());
	}
	
	/**
	 * Respons�vel por validar os campos obrigat�rios para o cadastro de Discentes.
	 * 
	 * @return
	 */
	private boolean validar() {

		if (obj.getDiscenteMedio().getPessoa().getNome() == null
				|| "".equals(obj.getDiscenteMedio().getPessoa().getNome().trim()))
			addMensagemErro("� obrigat�rio informar o nome do aluno.");

		if (obj.getDiscenteMedio().getDiscente().getMatricula() == null
				|| obj.getDiscenteMedio().getDiscente().getMatricula() <= 0) 
			addMensagemErro("� obrigat�rio informar a matr�cula.");

		if (obj.getDiscenteMedio().getPessoa().getDataNascimento() == null) 
			addMensagemErro("� obrigat�rio informar a data de nascimento.");

		if (!internacional) {
			Long cpf  = obj.getDiscenteMedio().getPessoa().getCpf_cnpj();
			ValidatorUtil.validateCPF_CNPJ(cpf == null ? 0 : cpf, "CPF do Aluno", erros);
			validateRequired(obj.getDiscenteMedio().getPessoa().getIdentidade(), "RG", erros);
		} else {
			validateRequired(obj.getDiscenteMedio().getPessoa().getPassaporte(), "Passaporte do Aluno", erros);
			validateMaxLength(obj.getDiscenteMedio().getPessoa().getPassaporte(), 20, "Passaporte do Aluno", erros);
		}

		if (obj.getDiscenteMedio().getDiscente().getAnoIngresso() == null
				|| obj.getDiscenteMedio().getDiscente().getAnoIngresso() <= 0) 
			addMensagemErro("� obrigat�rio informar o ano de ingresso.");
		
		obj.getDiscenteMedio().getPessoa().setNome(StringUtils.removeEspacosRepetidos(obj.getDiscenteMedio().getNome()));
		
		return erros.isEmpty();
	}		

	/**
	 * Retorna os discente vinculados ao familiar logado
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li> sigaa.war/portais/familiar/index.jsp </li>
	 * </ul>
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<DiscenteMedio> getDiscentes() throws HibernateException, DAOException {
		if (discentes == null){
			discentes = new ArrayList<DiscenteMedio>();
			VinculosDao dao = getDAO(VinculosDao.class);
			try {
				List<UsuarioFamiliar> usuarios = dao.findFamiliarDiscentes(getUsuarioLogado());
				if (ValidatorUtil.isNotEmpty(usuarios) && usuarios.size() > 1){
					for (UsuarioFamiliar u : usuarios)
						discentes.add(u.getDiscenteMedio());
				}
			} finally {
				if (dao != null)
					dao.close();
			}
		}
		return discentes;
	}

	public void setDiscentes(List<DiscenteMedio> discentes) {
		this.discentes = discentes;
	}

	public List<FrequenciaAluno> getFrequencias() {
		return frequencias;
	}

	public void setFrequencias(List<FrequenciaAluno> frequencias) {
		this.frequencias = frequencias;
	}

	public List<ObservacaoDiscenteSerie> getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(List<ObservacaoDiscenteSerie> observacoes) {
		this.observacoes = observacoes;
	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}

	public boolean isInternacional() {
		return internacional;
	}

	public void setInternacional(boolean internacional) {
		this.internacional = internacional;
	}

	public List<UsuarioFamiliar> getResponsaveis() throws DAOException {
		if (responsaveis == null){
			responsaveis = (List<UsuarioFamiliar>) getGenericDAO().findByExactField(UsuarioFamiliar.class, 
								"discenteMedio.id", discente.getId());
		}
		return responsaveis;
	}

	public void setResponsaveis(List<UsuarioFamiliar> responsaveis) {
		this.responsaveis = responsaveis;
	}
}
