/*
 * Universidade Federal do Rio Grande do Norte

 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '03/11/2011'
 *
 */
package br.ufrn.sigaa.mobile.touch.jsf;

import static br.ufrn.arq.util.CalendarUtils.createDate;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.DocenteTurmaDao;
import br.ufrn.sigaa.arq.dao.ensino.FrequenciaAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.TopicoAulaDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.AulaExtra;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.dominio.NoticiaTurma;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.jsf.FrequenciaAlunoMBean;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.FrequenciaAluno;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.dominio.FrequenciaMov;
import br.ufrn.sigaa.ensino.util.TurmaUtil;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.portal.jsf.PortalDocenteMBean;

/**
 * MBean responsável por controlar operações ligadas as turmas do 
 * discente com acesso ao sistema via dispositivos móveis. 
 * 
 * @author Ilueny Santos
 * @author Fred Castro
 *
 */
@Component("portalDocenteTouch")
@Scope("session")
public class PortalDocenteTouchMBean extends TurmaVirtualTouchMBean<Servidor> {
	
	/** Lista de todas as frequências da turma em uma data. */
	private List<FrequenciaAluno> frequencias;
	
	/** Dia em que sera lançada a frequência. */
	private int dia;
	
	/** Indica se deve permanecer listando a data selecionada. */
	private boolean permanecerDataSelecionada = false;
	
	/** Data onde sera lançada a frequência. */
	private Date dataSelecionada;
	
	/** Número máximo de presenças para um determinado dia. */
	private short maxFaltas;
	
	/** A notícia a ser utilizada pelo portal do docente. */
	private NoticiaTurma noticia = new NoticiaTurma();
	
	/** Construtor Padrão.*/
	public PortalDocenteTouchMBean() {
		
	}

	/** 
	 * Carrega todas as turmas do discente logado.
	 * Este método não é chamado por JSP.
	 * @throws ArqException 
	 */
	public void carregarTurmas (boolean todas) throws ArqException {
		TurmaDao turmaDao = null;
		
		try {
			turmaDao = getDAO(TurmaDao.class);
			PortalDocenteMBean pdBean = getMBean("portalDocente");
			
			if (!todas){
				turmas = (List<Turma>) pdBean.getTurmasAbertas();
			} else {
				turmas = (List<Turma>) pdBean.getTurmas();
			}
			
			ordenarTurmas(turmas);
			
		} catch (DAOException e) {
			notifyError(e);
		} finally {
			if (turmaDao != null)
				turmaDao.close();
		}
	}

	/**
	 * Ordena lista de turmas encontradas.
	 * 
	 * @param turmas
	 */	
	private void ordenarTurmas(List<Turma> turmas) {
		//ordenando..
		if ( !isEmpty(turmas) ) {
			Collections.sort( ((List<Turma>) turmas), new Comparator<Turma>(){
				public int compare(Turma t1, Turma t2) {
					int result = 0;
					
					result = ((Character)t1.getDisciplina().getNivel()).compareTo(t2.getDisciplina().getNivel());
					
					if( result == 0 )
						result = ((Integer)t2.getAno()).compareTo(t1.getAno());
					
					if( result == 0 )
						result = ((Integer)t2.getPeriodo()).compareTo(t1.getPeriodo());
					
					if( result == 0 )
						result = t1.getDisciplina().getDetalhes().getNome().compareTo( t2.getDisciplina().getDetalhes().getNome() );
					
					return result;
				}
			});
		}
	}
	
	/**
	 * Direciona o usuário para a página de lançamento de frequência de uma
	 * turma.<br/><br/>
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war\mobile\touch\ava\calendarios.jsp</li>
	 *    <li>sigaa.war\mobile\touch\ava\form_frequencia.jsp</li>
	 * </ul>
	 * @return
	 */
	public String lancarFrequencia () {
		try{
			frequencias = loadFrequencias();		
			if (ValidatorUtil.isEmpty(getTurma().getHorarios())) {
				addMensagemErro("Esta turma não está com os horários definidos.");
				return null;
			}
			if (isSemAula()) {
				addMensagemErro("Aula Cancelada");
				return null;
			}

			return forward("/mobile/touch/ava/form_frequencia.jsp");
		}catch (DAOException e) {
			addMensagemErro("Erro ao verificar frequências.");
			return null;
		}
	}
	
	/**
	 * Retorna a lista de sub-turmas da turma atual para
	 * as quais o docente que está logado dá aula.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/mobile/touch/ava/subturmas.jsp</li>
	 *  </ul>
	 * @return
	 * @throws DAOException
	 */
	public List<Turma> getSubTurmasDocente() throws DAOException {
		
		List<Turma> turmas = new ArrayList<Turma>();
		List<Turma> subturmas = getTurma().getSubturmas();
		DocenteTurmaDao dao = getDAO(DocenteTurmaDao.class);
			
		Integer idServidor = getServidorUsuario() != null ? getServidorUsuario().getId() : null;
		Integer idDocenteExterno = getUsuarioLogado().getDocenteExterno() != null ? getUsuarioLogado().getDocenteExterno().getId() : null;
			
		for (Turma turma : subturmas) {
			List<DocenteTurma> dt = dao.findAllByDocenteTurma(idServidor, idDocenteExterno, turma.getId());
			if (isNotEmpty(dt)) turmas.add(turma);
		}
		
		return turmas;
	}
	
	/**
	 * Seleciona a subturma caso a turma da turma virtual seja agrupadora.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /mobile/touch/ava/subturmas.jsp
	 * @return
	 * @throws ArqException 
	 */
	public String escolherSubTurma() throws ArqException {
		setTurma(getDAO(TurmaDao.class).findAndFetch(getParameterInt("id"), Turma.class, "turmaAgrupadora"));
		getTurma().getDisciplina().setUnidade(getDAO(TurmaDao.class).refresh(getTurma().getDisciplina().getUnidade()));
		getTurma().setHorarios(getDAO(TurmaDao.class).findHorariosByTurma(getTurma()));
		
		this.frequencias = null;
		
		return exibirCalendarios();
	}
	
	/**
	 * Direciona para a página de exibição dos calendários de uma turma.
	 * <br/><br/>
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war\mobile\touch\ava\topico_aula_docente.jsp</li>
	 * </ul>
	 * @return
	 */
	public String exibirCalendarios () {
		FrequenciaAlunoMBean fBean = getMBean("frequenciaAluno");
		fBean.setTurma(getTurma());
		
		return forward(getPaginaCalendarios());
	}
	
	/**
	 * Direciona o usuário de volta à tela anterior à tela de calendários.
	 * <br/><br/>
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war\mobile\touch\ava\calendarios.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String sairCalendarios () throws DAOException {
		if (getTurma().isSubTurma() && getTurma().getTurmaAgrupadora() != null){
			TurmaDao dao = getDAO(TurmaDao.class);
			Turma turmaAgrupadora = dao.findByPrimaryKey(getTurma().getTurmaAgrupadora().getId(), Turma.class); 
					
			setTurma(turmaAgrupadora);
			
			return forward("/mobile/touch/ava/subturmas.jsp");
		}
		
		return exibirTopico();
	}
	
	/**
	 * Dá acesso ao lançamento de frequências em subturmas.
	 * 
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/mobile/touch/ava/topico_aula_docente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String acessarLancarFrequenciaST() {
		return forward("/mobile/touch/ava/subturmas.jsf");
	}
	
	/**
	 * Retorna todas as turmas abertas.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war/mobile/touch/login.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String acessarPortal () {
		if (ValidatorUtil.isNotEmpty(servidor) || ValidatorUtil.isNotEmpty(docenteExterno)) {
			getCurrentRequest().getSession(false).setAttribute("subsistema", SigaaSubsistemas.SIGAA_MOBILE);
			return forward(getPaginaPrincipal());
		}else {
			addMensagemErro("Erro ao acessar o portal do docente.");
		}
		return null;
	}
	
	/**
	 * Retorna o número de presenças máximo de um dia de aula da turma.
	 * <br />
	 * Método não invocado por JSP's
	 * 
	 * @return
	 */
	private short getPresencasDia (Turma turma, Date data) {
		
		int diaSemana = CalendarUtils.getDiaSemanaByData(data);
		
		short presencas = 0;
		for( HorarioTurma ht: turma.getHorarios())
			if(ht.getDataInicio().getTime() <= data.getTime() && ht.getDataFim().getTime() >= data.getTime() && diaSemana == Character.getNumericValue(ht.getDia()) )
				presencas++;

		return presencas;
	}
	
	/**
	 * Lista as frequências cadastradas para a data selecionada
	 * em um calendário.<br/><br/>
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war/mobile/touch/ava/form_frequencia.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public List <FrequenciaAluno> getFrequencias () {
		return frequencias;
	}
	
	/**
	 * Carrega lista de frequencias dos discentes da turma.
	 * Método Não é chamado por JSP(s):
	 * 
	 */	
	private List <FrequenciaAluno> loadFrequencias () {
		if (permanecerDataSelecionada || (getParameterInt("dia") != null && (frequencias == null || dia != getParameterInt("dia")))) {

			TurmaDao turmaDao = null;
			TopicoAulaDao topDao= null;
			
			if (!permanecerDataSelecionada)
				dataSelecionada = createDate(getParameterInt("dia"), getParameterInt("mes"), getParameterInt("ano"));
			
			maxFaltas = getPresencasDia(getTurma(), dataSelecionada);
			List<AulaExtra> aulasExtra = getDAO(TurmaVirtualDao.class).buscarAulasExtra(getTurma(), dataSelecionada);

			try {
				topDao = getDAO(TopicoAulaDao.class);
				List<TopicoAula> aulasCanceladas = topDao.findTopicosSemAula(getTurma().getId(),dataSelecionada);
				TopicoAula ultimaAulaCancelada = null;
				if (!aulasCanceladas.isEmpty()){
					maxFaltas = 0;
					ultimaAulaCancelada = aulasCanceladas.get(0);
				}
				if (aulasExtra != null) {
					for (AulaExtra aula : aulasExtra) {
						if (ultimaAulaCancelada == null || (ultimaAulaCancelada != null && aula.getCriadoEm().getTime() > ultimaAulaCancelada.getDataCadastro().getTime()))
							maxFaltas += aula.getNumeroAulas();
					}
				}
				
				if (maxFaltas == 0)
					maxFaltas = 10;
			

				turmaDao = getDAO(TurmaDao.class);
				frequencias = turmaDao.findFrequenciasByTurma(getTurma(), dataSelecionada);
				Collection<MatriculaComponente> matriculas = turmaDao.findMatriculasAConsolidar(getTurma());
				
				if ( frequencias != null ) {
					if ( matriculas != null && frequencias.size() < matriculas.size() ) {
						for ( MatriculaComponente m : matriculas ) {
							if (!frequencias.contains(new FrequenciaAluno(m.getDiscente().getDiscente(), dataSelecionada, getTurma()))) {
								FrequenciaAluno freq = new FrequenciaAluno();
								freq.setData(dataSelecionada);
								Pessoa p = m.getDiscente().getPessoa();
								freq.setDiscente(m.getDiscente().getDiscente());
								freq.getDiscente().setPessoa(p);
								freq.setTurma(getTurma());
								freq.setFaltas((short) 0);
								freq.setHorarios(maxFaltas);
								frequencias.add(freq);
							}
						}
					}
		
					Collections.sort(frequencias, new Comparator<FrequenciaAluno>() {
						public int compare(FrequenciaAluno o1, FrequenciaAluno o2) {
							return o1.getDiscente().getPessoa().getNomeAscii().compareToIgnoreCase(o2.getDiscente().getPessoa().getNomeAscii());
						}
					});
				}
				
				if (!permanecerDataSelecionada)
					dia = getParameterInt("dia");
	
				permanecerDataSelecionada = false;
				try {
					prepareMovimento(SigaaListaComando.LANCAR_FREQUENCIA);
					prepareMovimento(SigaaListaComando.REMOVER_FREQUENCIA);
				} catch (ArqException e) {
					throw new TurmaVirtualException(e);
				}
			} catch (DAOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if ( topDao != null )
					topDao.close();
				if (turmaDao != null)
					turmaDao.close();
			}
		} /*else
			maxFaltas = 10;*/

		return frequencias;
	}
	
	/**
	 * Retorna uma lista de possíveis dados para o lançamento
	 * de frequência: presença ou um número de faltas.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getPresencasCombo() throws DAOException {
		ArrayList<SelectItem> itens = new ArrayList<SelectItem>();
		for(Short i = 0; i <= maxFaltas; i++){
			if (i == 0)
				itens.add( new SelectItem(i, "Presente") );
			else
				itens.add( new SelectItem(i, i + " Falta" + (i != 1 ? "s" :"")) );
		}
		return itens;
	}
	
	/**
	 * Cadastra frequência para todos os alunos em um dia selecionado.<br/><br/>
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war/mobile/touch/ava/form_frequencia.jsp</li>
	 * </ul>
	 */
	public String cadastrarFrequencia () {
		try {

			if(dataSelecionada == null) {
				addMensagemErro("Selecione uma data para lançar a frequência.");
				registrarAcao(null, EntidadeRegistroAva.FREQUENCIA, AcaoAva.INICIAR_INSERCAO, getTurma().getId() );
				return null;
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			String data = sdf.format(dataSelecionada);
			registrarAcao(data, EntidadeRegistroAva.FREQUENCIA, AcaoAva.INICIAR_INSERCAO, getTurma().getId() );
			
			if(dataSelecionada.after(new Date())) {
				addMensagemErro("Não é possível cadastrar frequências para datas posteriores à atual.");				
				return null;
			}
			
			//atualizando frequencias...
			for (FrequenciaAluno freq : frequencias) {
				short faltas = getFaltas("mat_" + freq.getDiscente().getMatricula().toString() + "_idt_" + String.valueOf(freq.getTurma().getId()));
				freq.setFaltas(faltas);
			}
			
			if (getTurma().isAberta()){
				prepareMovimento(SigaaListaComando.LANCAR_FREQUENCIA);
				execute(new FrequenciaMov(frequencias), getCurrentRequest());
				FrequenciaAlunoMBean fBean = getMBean("frequenciaAluno");
				
				fBean.setCalendarios(null);
				registrarAcao(data, EntidadeRegistroAva.FREQUENCIA, AcaoAva.INSERIR, getTurma().getId() );
				addMensagemInformation("Operação realizada com sucesso.");

			} else
				addMensagemErro("Esta turma já está consolidada e não pode ser alterada.");
		} catch (Exception e) {
			notifyError(e);
			e.printStackTrace();
		}

		return exibirCalendarios();
	}
	
	
	/**
	 * Pega uma falta em request. Se a falta não existir, retorna zero.
	 * Usado pelo popularFaltas()
	 * 
	 * Não é chamado por JSPs.
	 * 
	 * @param paramName
	 * @return
	 * @throws ParseException
	 * @throws NegocioException 
	 */
	private short getFaltas(String paramName) throws ParseException {
		String param = getParameter(paramName);
		//Pega faltas em request. Se as faltas não existirem, retorna zero.
		if (param != null && !"".equals(param.trim())) {
			try{
				return Short.parseShort(param);
			}catch (Exception e) {	
				e.printStackTrace();
			}
		}
		return 0;
	}
	
	
	/**
	 * Remove todas as frequências lançadas em um determinado dia.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ava/FrequenciaAluno/form.jsp
	 */
	public String removerFrequencia () {
		
		if (getTurma().isAberta()){
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				String data = sdf.format(dataSelecionada);
				registrarAcao(data, EntidadeRegistroAva.FREQUENCIA, AcaoAva.INICIAR_REMOCAO, getTurma().getId() );
				
				prepareMovimento(SigaaListaComando.REMOVER_FREQUENCIA);
				FrequenciaMov mov = new FrequenciaMov(frequencias);
				mov.setCodMovimento(SigaaListaComando.REMOVER_FREQUENCIA);
				execute(mov, getCurrentRequest());
				addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
				frequencias = null;
				
				registrarAcao(data, EntidadeRegistroAva.FREQUENCIA, AcaoAva.REMOVER, getTurma().getId() );

				
				FrequenciaAlunoMBean fBean = getMBean ("frequenciaAluno");
				fBean.setCalendarios(null);
				
				permanecerDataSelecionada = true;
				
				return exibirCalendarios();
			} catch (Exception e) {
				notifyError(e);
				e.printStackTrace();
			}
		} else 
			addMensagemErro("Esta turma já está consolidada e não pode ser alterada.");

		return null;
	}
	
	/**
	 * Verifica se já foi lançada a frequência da data selecionada.
	 * 
	 * Método chamado pela seguinte JSP: /ava/FrequenciaAluno/form.jsp
	 * @throws DAOException 
	 */
	public boolean isDiaTemFrequencia () throws DAOException {
		FrequenciaAlunoDao fDao = null;
		try{
			fDao = getDAO( FrequenciaAlunoDao.class );
			return fDao.diaTemFrequencia(dataSelecionada, getTurma());
		}finally{
			if (fDao != null)
				fDao.close();
		}
	}
	
	/**
	 * Verifica se a aula do dia selecionado foi cancelada.
	 * Método chamado pela seguinte JSP: /ava/FrequenciaAluno/form.jsp
	 * @throws DAOException
	 * @return
	 */
	public boolean isSemAula () throws DAOException{

		List<Date> diasSemAulas = null;
		
		if ( getTurma().getTurmaAgrupadora() == null )
			diasSemAulas = TurmaUtil.getDatasCanceladas(getTurma().getId());
		else
			diasSemAulas = TurmaUtil.getDatasCanceladas(getTurma().getTurmaAgrupadora().getId());
		
		if ( dataSelecionada != null && diasSemAulas != null && !diasSemAulas.isEmpty() )
			for ( Date d : diasSemAulas ) {
				if ( dataSelecionada.compareTo(d) == 0 )
					return true;
			}
		return false;

	}
	
	@Override
	public String getPaginaPrincipal() {
		return "/mobile/touch/menu.jsp";
	}
	
	@Override
	public String getPaginaListarAulas() {
		return "/mobile/touch/ava/aulas_docente.jsf";
	}
	
	@Override
	public String getPaginaTurmas() {
		return "/mobile/touch/ava/turmas_docente.jsf";
	}
	
	@Override
	public String getPaginaTopicoAula() {
		return "/mobile/touch/ava/topico_aula_docente.jsf";
	}
	
	public String getPaginaCalendarios() {
		return "/mobile/touch/ava/calendarios.jsf";
	}

	public int getDia() {
		return dia;
	}

	public void setDia(int dia) {
		this.dia = dia;
	}

	public boolean isPermanecerDataSelecionada() {
		return permanecerDataSelecionada;
	}

	public void setPermanecerDataSelecionada(boolean permanecerDataSelecionada) {
		this.permanecerDataSelecionada = permanecerDataSelecionada;
	}

	public Date getDataSelecionada() {
		return dataSelecionada;
	}

	public void setDataSelecionada(Date dataSelecionada) {
		this.dataSelecionada = dataSelecionada;
	}

	public short getMaxFaltas() {
		return maxFaltas;
	}

	public void setMaxFaltas(short maxFaltas) {
		this.maxFaltas = maxFaltas;
	}

	public void setFrequencias(List<FrequenciaAluno> frequencias) {
		this.frequencias = frequencias;
	}
	
	public NoticiaTurma getNoticia() {
		return noticia;
	}

	public void setNoticia(NoticiaTurma noticia) {
		this.noticia = noticia;
	}
}