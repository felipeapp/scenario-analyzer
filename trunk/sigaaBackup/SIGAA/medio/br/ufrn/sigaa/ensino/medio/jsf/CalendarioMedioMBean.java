/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 09/06/2011
 */
package br.ufrn.sigaa.ensino.medio.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.ensino.CalendarioAcademicoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dao.RegraNotaDao;
import br.ufrn.sigaa.ensino.dominio.RegraNota;
import br.ufrn.sigaa.ensino.dominio.TipoNota;
import br.ufrn.sigaa.ensino.medio.dao.CalendarioRegraDao;
import br.ufrn.sigaa.ensino.medio.dominio.CalendarioRegra;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.negocio.CalendarioMov;

/**
 * MBean responsável pelas operações com calendários acadêmicos do nível médio
 * 
 * @author Arlindo
 */
@Component("calendarioMedioMBean") @Scope("request")
public class CalendarioMedioMBean extends SigaaAbstractController<CalendarioAcademico> {
	
	/**
	 * lista de calendários carregados de uma unidade para o usuário escolher
	 * qual manipular
	 */
	private Collection<CalendarioAcademico> calendarios = new HashSet<CalendarioAcademico>();
	
	/**
	 * Lista de Regra de notas
	 */
	private Collection<RegraNota> regras = new ArrayList<RegraNota>();
	
	/** Calendário letivo das regras */
	private Collection<CalendarioRegra> calendarioRegra = new ArrayList<CalendarioRegra>();
	
	/**
	 * Inicializa os objetos.
	 */
	private void initObj() {
		if (obj == null)
			obj = new CalendarioAcademico();
		if (obj.getUnidade() == null)
			obj.setUnidade(new Unidade());
		if (obj.getCurso() == null)
			obj.setCurso(new Curso());
		obj.setConvenio(null);
		obj.setModalidade(null);
		obj.setEventosExtra(null);
		obj.setNivel(NivelEnsino.MEDIO);
	}
	
	/** Construtor padrão */
	public CalendarioMedioMBean() {
		initObj();
	}
	
	/**
	 * Inicia o cadastro de calendário acadêmico do nível médio
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/medio/menus/curso.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String iniciar() throws ArqException{
		
		checkRole(SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO);
		
		CursoDao dao = null;
		try {
			dao = getDAO(CursoDao.class);
			obj.setUnidade(dao.findByPrimaryKey(getUnidadeGestora(), Unidade.class, "id", "nome"));
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward(getFormPage());
	}
	
	/**
	 * Inicia a visualização do cadastro de calendário acadêmico referente ao curso selecionado
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/medio/calendario/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 * @throws SegurancaException 
	 */
	public String iniciarCalendario() throws DAOException, SegurancaException{
		checkRole(SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO);
		
		CalendarioAcademicoDao dao = getDAO(CalendarioAcademicoDao.class);
		RegraNotaDao regraDao = getDAO(RegraNotaDao.class);
		try {
			
			Unidade uCal = obj.getUnidade();
			if (obj.getCurso() != null){
				Curso c = dao.findByPrimaryKey(obj.getCurso().getId(),Curso.class);
				uCal = c.getUnidade();
			}
			calendarios = dao.findCalendariosByParametros(uCal, NivelEnsino.MEDIO, null, null, obj.getCurso());
			
			CalendarioAcademico calAtual = null;
			if (!ValidatorUtil.isEmpty(calendarios)){
				
				for (CalendarioAcademico cal : calendarios){
					if (cal.isVigente()){
						calAtual = UFRNUtils.deepCopy( cal );
						break;
					}
				}
				
				if (calAtual != null){					
					obj = dao.refresh( calAtual );	
					setConfirmButton("Alterar Calendário");
				}
				
			} else {
				if (!ValidatorUtil.isEmpty(obj.getCurso())){
					Curso c = dao.findByPrimaryKey(obj.getCurso().getId(), Curso.class, "id", "nome");
					obj.setCurso(c);
				}
			}
			
			if (ValidatorUtil.isNotEmpty(obj.getCurso()))
				regras = regraDao.findByCurso(obj.getCurso(), TipoNota.REGULAR.ordinal());
			else
				regras = regraDao.findByCurso(null, TipoNota.REGULAR.ordinal());
			
			if (ValidatorUtil.isEmpty(regras)){
				addMensagemErro("Não foram definidas as regras de consolidação.");
				return null;
			}			
			
			if (ValidatorUtil.isEmpty(calendarios) || ValidatorUtil.isEmpty(obj)){
				Unidade u = UFRNUtils.deepCopy( obj.getUnidade() );
				Curso c = UFRNUtils.deepCopy( obj.getCurso() );
				obj = new CalendarioAcademico();
				obj.setUnidade(u);
				obj.setCurso(c);
				obj.setAno(CalendarUtils.getAnoAtual());				
			}
			
			initObj();
			
			if (obj.getId() == 0)
				setConfirmButton("Cadastrar");
			else
				setConfirmButton("Alterar Calendário");
			
			carregarCalendarioRegras();
			
		} finally {
			if (dao != null)
				dao.close();
			if (regraDao != null)
				regraDao.close();
		}
		
		return forward(getFormCalendario());		
	}
	
	/**
	 * Carrega os calendários das regras de notas
	 * @throws DAOException
	 */
	private void carregarCalendarioRegras() throws DAOException {
		
		CalendarioRegraDao calRegDao = getDAO(CalendarioRegraDao.class); 
		try {
			calendarioRegra = calRegDao.findByCalendario(obj);
			
			if (ValidatorUtil.isEmpty(calendarioRegra) && ValidatorUtil.isNotEmpty(regras)){
				calendarioRegra = new ArrayList<CalendarioRegra>();
				
				for (RegraNota r : regras){
					CalendarioRegra calRegra = new CalendarioRegra();
					calRegra.setCalendario(obj);
					calRegra.setRegra(r);					
					calendarioRegra.add(calRegra);
				}
				
			}	
		} finally {
			if (calRegDao != null)
				calRegDao.close();			
		}
		
	}
	
	/**
	 * Carrega o calendário selecionado.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/medio/calendario/form_calendario.jsp</li>
	 * </ul>
	 * @param evento
	 * @throws DAOException
	 */
	public void carregarCalendario(ValueChangeEvent evento) throws DAOException {
		GenericDAO dao = getGenericDAO();
		int id = Integer.parseInt(evento.getNewValue().toString());
		if (id == 0) {
			setConfirmButton("Cadastrar Calendário");
			Curso c = obj.getCurso();
			obj = new CalendarioAcademico(obj.getUnidade(), obj.getNivel());
			initObj();
			if (!ValidatorUtil.isEmpty(c))
				obj.setCurso(c);
		} else {
			obj = dao.findByPrimaryKey(id, CalendarioAcademico.class);
			obj.getEventosExtra().iterator();
			setConfirmButton("Alterar Calendário");
		}
		
		if (ValidatorUtil.isEmpty(obj.getCurso()))
			obj.setCurso(new Curso());
		
		carregarCalendarioRegras();
	}	
	
	/**
	 * Confirma o cadastro.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/medio/calendario/form_calendario.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String confirmar() throws ArqException {
		erros = new ListaMensagens();
		ListaMensagens lista = obj.validate();
		
		if (ValidatorUtil.isEmpty( obj.getInicioPeriodoLetivo() ) || 
				ValidatorUtil.isEmpty( obj.getFimPeriodoLetivo() ))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Período Letivo");

		if (lista != null) 
			erros.addAll(lista.getMensagens());
		
		if (hasErrors())
			return null;

		Comando comando = null;
		String msg = "Calendário alterado com sucesso!";
		if (obj.getId() == 0) {
			comando = SigaaListaComando.CADASTRAR_CALENDARIO_MEDIO;
			msg = "Calendário criado com sucesso!";
			if (isEmpty(calendarios))
				obj.setVigente(true);
			obj.setAtivo(true);			
		} else {
			comando = SigaaListaComando.ALTERAR_CALENDARIO_MEDIO;
		}
		
		if (ValidatorUtil.isEmpty(obj.getCurso()))
			obj.setCurso(null);
		else
			obj.setModalidade(obj.getCurso().getModalidadeEducacao());
		
		try {
			prepareMovimento(comando);
			CalendarioMov mov = new CalendarioMov();
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(comando);
			mov.setObjAuxiliar(calendarioRegra);
			obj = (CalendarioAcademico) executeWithoutClosingSession(mov, getCurrentRequest());
			CalendarioAcademico cal;
			if (obj.isVigente())
				cal = obj;
			else 
				// retorna o calendário do curso ativo, senão retorna o calendário do usuário logado.
				cal = getCalendarioVigenteAtivo();
			
			getCurrentSession().setAttribute("calendarioAcademico", cal);
			
			erros = new ListaMensagens();
			addMessage(msg, TipoMensagemUFRN.INFORMATION);

		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}

		return cancelar();
	}	
	
	/**
	 * Retorna o calendário vigente que está ativo no momento.<br/>
	 * @return
	 * @throws DAOException
	 */
	public CalendarioAcademico getCalendarioVigenteAtivo() throws DAOException{
		CalendarioAcademicoDao dao = getDAO(CalendarioAcademicoDao.class);
		try {
			Curso curso = (Curso) getCurrentSession().getAttribute(SigaaAbstractController.CURSO_ATUAL);
			CalendarioAcademico cal;
			if (curso != null && curso.getId() > 0)		
				cal = dao.findByCurso(null, null, curso, getNivelEnsino());
			else 
				cal = CalendarioAcademicoHelper.getCalendario(getUsuarioLogado());
			
			//Evita setar um calendário nulo na sessão. 
			//No caso, o calendário da sessão vai permanescer o geral do nível de ensino, 
			//setado anteriormente ao Entrar na area Médio(EntrarMedioAction).
			if(cal != null)		
				getCurrentSession().setAttribute("calendarioAcademico", cal);
			
			return cal;
		} finally {
			if (dao != null)
				dao.close();
		}
	}	
	
	/** Retorna o caminho do formulário */
	@Override
	public String getFormPage() {
		return "/medio/calendario/form.jsp";
	}

	/** Retorna o caminho da tela de */
	public String getFormCalendario() {
		return "/medio/calendario/form_calendario.jsp";
	}

	public Collection<CalendarioAcademico> getCalendarios() {
		return calendarios;
	}

	public void setCalendarios(Collection<CalendarioAcademico> calendarios) {
		this.calendarios = calendarios;
	}
	
	public Collection<SelectItem> getCalendariosCombo() {
		return toSelectItems(getCalendarios(), "id", "anoVigente");
	}

	public Collection<RegraNota> getRegras() {
		return regras;
	}

	public void setRegras(Collection<RegraNota> regras) {
		this.regras = regras;
	}

	public Collection<CalendarioRegra> getCalendarioRegra() {
		return calendarioRegra;
	}

	public void setCalendarioRegra(Collection<CalendarioRegra> calendarioRegra) {
		this.calendarioRegra = calendarioRegra;
	}	
}
