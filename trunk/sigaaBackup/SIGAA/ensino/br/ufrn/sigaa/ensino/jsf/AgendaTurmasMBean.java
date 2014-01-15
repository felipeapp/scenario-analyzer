/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 03/11/2010
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.sigaa.agenda.dominio.Agenda;
import br.ufrn.sigaa.agenda.dominio.Evento;
import br.ufrn.sigaa.agenda.jsf.AgendaDataModel;
import br.ufrn.sigaa.agenda.negocio.AgendaFactory;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;

/** Controller respons�vel por exibir a agenda de turmas de um docente.
 * @author �dipo Elder F. Melo
 *
 */
@Component("agendaTurmasBean") 
@Scope("session")
public class AgendaTurmasMBean extends SigaaAbstractController<Turma> {

	/** Modelo de agenda com os hor�rios do docente para as turmas abertas que o docente leciona. */
	private AgendaDataModel turmaAgendaModel;
	
	/** Cole��o de turmas abertas que o docente leciona.
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public Collection<Turma> getTurmas() throws DAOException, SegurancaException {
		if (resultadosBusca == null) {
			TurmaDao turmaDao = getDAO(TurmaDao.class);
			if( getAcessoMenu().isDocenteUFRN() ){
				resultadosBusca = turmaDao.findByDocente(getServidorUsuario(), null, SituacaoTurma.ABERTA, null, false, false);
			} else if ( getAcessoMenu().isDocente() ){
				resultadosBusca = turmaDao.findByDocenteExterno(getUsuarioLogado().getDocenteExterno(), null, null, null, null,
						false, false, SituacaoTurma.ABERTA);
			} 
			
			if (resultadosBusca != null) {
				for (Turma turma : resultadosBusca)
					for (DocenteTurma dt : turma.getDocentesTurmas())
						if (isUsuarioDocenteTurma(dt))
								dt.getHorarios().iterator();
			}			
		}
		return resultadosBusca;
	}

	/** Verifica se o usu�rio logado � docente da turma.
	 * @param dt
	 * @return
	 */
	private boolean isUsuarioDocenteTurma(DocenteTurma dt) {
		return (dt.getDocente() != null && getServidorUsuario() != null && dt.getDocente().getId() == getServidorUsuario().getId()) ||
			(dt.getDocenteExterno() != null && getUsuarioLogado().getDocenteExterno() != null && dt.getDocenteExterno().getId() == getUsuarioLogado().getDocenteExterno().getId());
	}	
	
	/** Retorna o modelo de agenda com os hor�rios do docente para as turmas abertas que o docente leciona.
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public AgendaDataModel getTurmasAgendaModel() throws DAOException, SegurancaException {
		turmaAgendaModel = null;
		if (turmaAgendaModel == null) {
			turmaAgendaModel = new AgendaDataModel();
			Collection<Agenda> agendas = new ArrayList<Agenda>();
			for (Turma turma : resultadosBusca) {
				for (DocenteTurma dt : turma.getDocentesTurmas()) {
					if ((getAcessoMenu().isDocenteUFRN() && dt.getDocente().getId() == getServidorUsuario().getId()) ||
						(getAcessoMenu().isDocente() && dt.getDocenteExterno().getId() == getUsuarioLogado().getDocenteExterno().getId())) {
						agendas.add(AgendaFactory.getInstance().createFromHorarioDocente(turma, dt));
					}
				}
			}
			Agenda agendaGeral = new Agenda();
			if (agendas != null) {
				for (Agenda agenda : agendas) {
					for (Evento evento : agenda.getEventos()) {
						agendaGeral.addEvento(evento);
					}
				}
				turmaAgendaModel = new AgendaDataModel(agendaGeral);
			}
		}
		return turmaAgendaModel;
	}

	/**
	 * Redireciona o docente para a visualiza��o da agenda com os hor�rios das
	 * turmas. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/portais/docente/docente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String viewAgendaTurmas() throws DAOException, SegurancaException {
		if (isEmpty(getTurmas()))
			addMensagemErro("N�o h� turmas lecionadas no per�odo atual.");
		return forward("/portais/agenda/agenda_turmas.jsp");
	}
}
