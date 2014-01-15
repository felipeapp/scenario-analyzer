package br.ufrn.sigaa.ensino.tecnico.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.tecnico.dao.DiscenteTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dao.TransferenciaTurmaEntradaDao;
import br.ufrn.sigaa.ensino.tecnico.dao.TurmaEntradaTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.CursoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;
import br.ufrn.sigaa.ensino.tecnico.negocio.MovimentoTransferenciaTurmaTurmaEntrada;
import br.ufrn.sigaa.ensino.tecnico.relatorios.LinhaAuxiliarTransfTurmaEntrada;

/**
 * MBean responsável pela transferência dos discentes entre turmas e turma de entrada.
 * @author Jean Guerethes
 */
@SuppressWarnings("serial") 
@Component @Scope("session")
public class TransferenciaTurmaEntradaMBean extends SigaaAbstractController<Object> {

	/** Curso dos discentes */
	private CursoTecnico curso;
	/** Turma de entrada de origem dos discentes */
	private TurmaEntradaTecnico turmaEntradaOrigem;
	/** Turma de entrada de detino dos discentes */
	private TurmaEntradaTecnico turmaEntradaDestino;
	/** Discente que vão mudar de turma de entrada */
	private Collection<DiscenteAdapter> discentes;
	/** Turmas de entrada encontrada para o curso selecionado */
	private Collection<TurmaEntradaTecnico> turmasEntrada;
	/** Informções dos discetes e suas respectivas turmas */
	private Map<DiscenteAdapter, Collection<LinhaAuxiliarTransfTurmaEntrada>> turmas;
	
	/** Construtor padrão */
	public TransferenciaTurmaEntradaMBean() {
		clear();
	}
	
	/** Inicializa todos as atributos a serem utilizados na transferência do(s) discente(s) */
	private void clear() {
		curso = new CursoTecnico();
		turmaEntradaOrigem = new TurmaEntradaTecnico();
		turmaEntradaDestino = new TurmaEntradaTecnico();
		discentes = new ArrayList<DiscenteAdapter>();
		turmasEntrada = new ArrayList<TurmaEntradaTecnico>();
		turmas = new HashMap<DiscenteAdapter, Collection<LinhaAuxiliarTransfTurmaEntrada>>();
	}

	/**
	 * Método chamando para inicilizar a transferência de turma do(s) discente(s)
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String iniciarTransfTurmaEntrada() throws DAOException {
		clear();
		return telaTurmaEntrada();
	}
	
	/**
	 * Submete o usuário para a tela de seleção da turma de entrada de origem.
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/transferenciaTurmaEntrada/sel_turma_entrada_destino.jsp</li>
	 * </ul>
	 */
	public String telaTurmaEntrada() throws DAOException {
		if ( curso.getId() > 0 )
			carregarTurmasEntrada(curso.getId());
		return forward("/ensino/tecnico/transferenciaTurmaEntrada/sel_turma_entrada.jsf");
	}

	/**
	 * Submete o usuário para a tela de seleção da turma de entrada de destino.
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/transferenciaTurmaEntrada/matriculas.jsp</li>
	 * </ul>
	 */
	public String telaTurmaDestino() {
		return forward("/ensino/tecnico/transferenciaTurmaEntrada/sel_turma_entrada_destino.jsf");
	}

	/**
	 * Responsável por realizar as validações nos dados informados e direcionar o usuário a 
	 * tela ded seleção da turma de entrada de destino.
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/transferenciaTurmaEntrada/sel_turma_entrada.jsp</li>
	 * </ul>
	 */
	public String submeterTurmaEntrada() throws DAOException {
		ValidatorUtil.validateRequiredId(curso.getId(), "Curso", erros);
		ValidatorUtil.validateRequiredId(turmaEntradaOrigem.getId(), "Turma de Entrada", erros);
		
		if (hasOnlyErrors())
			return null;
		
		TurmaEntradaTecnicoDao dao = getDAO(TurmaEntradaTecnicoDao.class);
		try {
			curso =  dao.findByPrimaryKey(curso.getId(), CursoTecnico.class);
			turmaEntradaOrigem = dao.findByPrimaryKey(turmaEntradaOrigem.getId(), TurmaEntradaTecnico.class);
			turmasEntrada.clear();
			turmasEntrada.addAll(dao.findByCursoTecnico(curso.getId(), turmaEntradaOrigem.getId(), turmaEntradaOrigem.getAnoReferencia(), turmaEntradaOrigem.getPeriodoReferencia()));
		} finally {
			dao.close();
		}
		
		Collection<DiscenteAdapter> discentesSelecionados = new ArrayList<DiscenteAdapter>();
		for (DiscenteAdapter dis : discentes) {
			if ( dis.isMatricular()  )
				discentesSelecionados.add(dis);
		}
		
		if ( discentesSelecionados.isEmpty() ) {
			addMensagemErro("É necessário informar pelo menos um discente.");
			return null;
		} else {
			discentes.clear();
			discentes.addAll(discentesSelecionados);
		}
		
		return forward("/ensino/tecnico/transferenciaTurmaEntrada/sel_turma_entrada_destino.jsf");
	}
	
	/**
	 * Responsável por carregar as turma de entrada do curso selecionado.
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/transferenciaTurmaEntrada/sel_turma_entrada.jsp</li>
	 * </ul>
	 */
	public void carregarTurmasEntrada(ValueChangeEvent e) throws DAOException {
		curso.setId( (Integer) e.getNewValue() );
		carregarTurmasEntrada(curso.getId());
	}

	/**
	 * Responsável por carregar as turmas de entrada do curso selecionado.
	 */
	private void carregarTurmasEntrada(int idCurso) throws DAOException {
		TurmaEntradaTecnicoDao dao = getDAO(TurmaEntradaTecnicoDao.class);
		try {
			turmasEntrada = new ArrayList<TurmaEntradaTecnico>();
			turmasEntrada.addAll(dao.findByCursoTecnico(curso.getId(), 0, null, null));
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Responsável por carregar os discentes da turma de entrada selecionada.
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/transferenciaTurmaEntrada/sel_turma_entrada.jsp</li>
	 * </ul>
	 */
	public void carregarDiscentes(ValueChangeEvent e) throws ArqException {
		DiscenteTecnicoDao dao = getDAO(DiscenteTecnicoDao.class);
		try {
			turmaEntradaOrigem.setId( (Integer) e.getNewValue() );
			discentes = dao.findByTurmaEntrada(turmaEntradaOrigem.getId(), getUnidadeGestora(), NivelEnsino.TECNICO, true);
			if ( !hasOnlyErrors() && discentes.isEmpty() )
				addMensagemErro("Não foram encontrados discentes para a turma de entrada selecionada.");
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Responsável por validar e carregar as possíveis turmas de destino caso o discente esteja matriculado em alguma turma.
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/transferenciaTurmaEntrada/sel_turma_entrada_destino.jsp</li>
	 * </ul>
	 */
	public String submeterTurmaDestino() throws HibernateException, ArqException {
		TransferenciaTurmaEntradaDao dao = getDAO(TransferenciaTurmaEntradaDao.class);
		try {
			if ( turmaEntradaDestino.getId() == turmaEntradaOrigem.getId() ) {
				addMensagemErro("A Turma de Entrada de Destino não pode ser igual a Turma de Entrada de Origem.");
				return null;
			}
			
			CalendarioAcademico calendario =  getCalendarioVigente();
			turmaEntradaDestino = getGenericDAO().findByPrimaryKey(turmaEntradaDestino.getId(), TurmaEntradaTecnico.class);
			turmas = dao.findTurmasMatriculadasByDiscente(discentes, calendario.getAno(), calendario.getPeriodo(), turmaEntradaDestino);
			setOperacaoAtiva( SigaaListaComando.EFETUAR_TRANSFERENCIA_ENTRE_TURMAS.getId() );
		} finally {
			dao.close();
		}
		return forward("/ensino/tecnico/transferenciaTurmaEntrada/matriculas.jsf");
	}
	
	/**
	 * Responsável por realizar as transferências entre turmas de entrada do(s) discente(s) selecionados.
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/transferenciaTurmaEntrada/matriculas.jsp</li>
	 * </ul>
	 */
	public String efeturarTransferencia() throws ArqException {
		checkOperacaoAtiva(SigaaListaComando.EFETUAR_TRANSFERENCIA_ENTRE_TURMAS.getId());
		if (!hasOnlyErrors()) {
			try {
				MovimentoTransferenciaTurmaTurmaEntrada mov = new MovimentoTransferenciaTurmaTurmaEntrada();
				mov.setTurmaEntradaOrigem(turmaEntradaOrigem);
				mov.setTurmaEntradaDestino(turmaEntradaDestino);
				mov.setTurmas(turmas);
				mov.setCodMovimento(SigaaListaComando.EFETUAR_TRANSFERENCIA_ENTRE_TURMAS);
				prepareMovimento(SigaaListaComando.EFETUAR_TRANSFERENCIA_ENTRE_TURMAS);
				execute(mov);
				removeOperacaoAtiva();
				addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "Discente(s)");
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				return null;
			}
		}
		
        return iniciarTransfTurmaEntrada();
	}
	
	public TurmaEntradaTecnico getTurmaEntradaOrigem() {
		return turmaEntradaOrigem;
	}

	public void setTurmaEntradaOrigem(TurmaEntradaTecnico turmaEntradaOrigem) {
		this.turmaEntradaOrigem = turmaEntradaOrigem;
	}

	public TurmaEntradaTecnico getTurmaEntradaDestino() {
		return turmaEntradaDestino;
	}

	public void setTurmaEntradaDestino(TurmaEntradaTecnico turmaEntradaDestino) {
		this.turmaEntradaDestino = turmaEntradaDestino;
	}

	public CursoTecnico getCurso() {
		return curso;
	}

	public void setCurso(CursoTecnico curso) {
		this.curso = curso;
	}

	public Collection<DiscenteAdapter> getDiscentes() {
		return discentes;
	}

	public void setDiscentes(Collection<DiscenteAdapter> discentes) {
		this.discentes = discentes;
	}

	public Collection<SelectItem> getComboTurmasEntrada() {
		return toSelectItems((turmasEntrada), "id", "descricao");
	}

	public Collection<TurmaEntradaTecnico> getTurmasEntrada() {
		return turmasEntrada;
	}

	public void setTurmasEntrada(Collection<TurmaEntradaTecnico> turmasEntrada) {
		this.turmasEntrada = turmasEntrada;
	}

	public Map<DiscenteAdapter, Collection<LinhaAuxiliarTransfTurmaEntrada>> getTurmas() {
		return turmas;
	}

	public void setTurmas(
			Map<DiscenteAdapter, Collection<LinhaAuxiliarTransfTurmaEntrada>> turmas) {
		this.turmas = turmas;
	}
	
}