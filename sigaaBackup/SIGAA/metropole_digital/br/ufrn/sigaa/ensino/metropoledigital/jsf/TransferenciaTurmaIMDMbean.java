package br.ufrn.sigaa.ensino.metropoledigital.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.metropoledigital.dao.TransferenciaTurmaIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.TurmaEntradaTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dao.DiscenteTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dao.TransferenciaTurmaEntradaDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.CursoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;
import br.ufrn.sigaa.ensino.tecnico.negocio.MovimentoTransferenciaTurmaTurmaEntrada;
import br.ufrn.sigaa.ensino.tecnico.relatorios.LinhaAuxiliarTransfTurmaEntrada;
import br.ufrn.sigaa.parametros.dominio.ParametrosTecnico;

/**
 * MBean respons�vel pela transfer�ncia dos discentes entre turmas do IMD.
 * 
 * @author Rafael Silva
 */
@SuppressWarnings("serial") 
@Component("transferenciaTurmaIMD") @Scope("session")
public class TransferenciaTurmaIMDMbean extends SigaaAbstractController<Object> {

	/** Curso dos discentes */
	private CursoTecnico curso;
	/** Turma de entrada de origem dos discentes */
	private TurmaEntradaTecnico turmaEntradaOrigem;
	/** Turma de entrada de detino dos discentes */
	private TurmaEntradaTecnico turmaEntradaDestino;
	/** Discente que v�o mudar de turma de entrada */
	private Collection<DiscenteAdapter> discentes;
	/** Turmas de entrada encontrada para o curso selecionado */
	private Collection<TurmaEntradaTecnico> turmasEntrada;
	/** Turmas de entrada do mesmos m�dulo/cronograma da turma de entrada selecionada */
	private Collection<TurmaEntradaTecnico> turmasEntradaDestino;
	/** Inform��es dos discetes e suas respectivas turmas */
	private Map<DiscenteAdapter, Collection<LinhaAuxiliarTransfTurmaEntrada>> turmas;
	/**Listagem dos cursos no combobox*/
	private List<SelectItem> cursosCombo = new ArrayList<SelectItem>();
	
	
	/** Construtor padr�o 
	 * @throws ArqException 
	 * @throws DAOException */
	public TransferenciaTurmaIMDMbean() throws DAOException, ArqException {
		clear();
		CursoDao cursoDao = getDAO(CursoDao.class);
		
		Unidade imd = getGenericDAO().findByPrimaryKey(ParametroHelper.getInstance().getParametroInt(
				ParametrosTecnico.ID_UNIDADE_INSTITUTO_METROPOLE_DIGITAL), Unidade.class);
		
		//cursosCombo = toSelectItems(cursoDao.findByNivel(getNivelEnsino(), true, null, new Unidade(getUnidadeGestora())), "id", "descricao");
		cursosCombo = toSelectItems(cursoDao.findByNivel(getNivelEnsino(), true, null, imd), "id", "descricao");
	}
	
	/** Inicializa todos as atributos a serem utilizados na transfer�ncia do(s) discente(s) */
	private void clear() {
		curso = new CursoTecnico();
		turmaEntradaOrigem = new TurmaEntradaTecnico();
		turmaEntradaDestino = new TurmaEntradaTecnico();
		discentes = new ArrayList<DiscenteAdapter>();
		turmasEntrada = new ArrayList<TurmaEntradaTecnico>();
		turmasEntradaDestino = new ArrayList<TurmaEntradaTecnico>();
		turmas = new HashMap<DiscenteAdapter, Collection<LinhaAuxiliarTransfTurmaEntrada>>();
	}

	/**
	 * M�todo chamando para inicilizar a transfer�ncia de turma do(s) discente(s)
	 * 
	 * M�todo chamado pelas seguintes JSPs:
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
	 * Submete o usu�rio para a tela de sele��o da turma de entrada de origem.
	 * 
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/transferenciaTurmaEntrada/sel_turma_entrada_destino.jsp</li>
	 * </ul>
	 */
	public String telaTurmaEntrada() throws DAOException {
		if ( curso.getId() > 0 )
			carregarTurmasEntrada(curso.getId());
			
		return redirect("/metropole_digital/transferencia_turma/sel_turma_entrada.jsf");
	}
	
	/**
	 * Submete o usu�rio para a tela de sele��o da turma de entrada de origem.
	 * 
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/transferenciaTurmaEntrada/sel_turma_entrada_destino.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 */
	public String voltarTelaTurmaEntrada() throws ArqException {
		if ( curso.getId() > 0 )
			carregarTurmasEntrada(curso.getId());
		
		DiscenteTecnicoDao dao = getDAO(DiscenteTecnicoDao.class);
		try {
			discentes = dao.findByTurmaEntrada(turmaEntradaOrigem.getId(), getUnidadeGestora(), NivelEnsino.TECNICO, true);
			if ( !hasOnlyErrors() && discentes.isEmpty() )
				addMensagemErro("N�o foram encontrados discentes para a turma de entrada selecionada.");
		} finally {
			dao.close();
		}
		
		return forward("/metropole_digital/transferencia_turma/sel_turma_entrada.jsf");
	}

	/**
	 * Submete o usu�rio para a tela de sele��o da turma de entrada de destino.
	 * 
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/transferenciaTurmaEntrada/matriculas.jsp</li>
	 * </ul>
	 */
	public String telaTurmaDestino() {
		return forward("/metropole_digital/transferencia_turma/sel_turma_entrada_destino.jsf");
	}

	/**
	 * Respons�vel por realizar as valida��es nos dados informados e direcionar o usu�rio a 
	 * tela ded sele��o da turma de entrada de destino.
	 * 
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/transferenciaTurmaEntrada/sel_turma_entrada.jsp</li>
	 * </ul>
	 */
	public String submeterTurmaEntrada() throws DAOException {
 		ValidatorUtil.validateRequiredId(curso.getId(), "Curso", erros);
		ValidatorUtil.validateRequiredId(turmaEntradaOrigem.getId(), "Turma de Entrada", erros);
		
		if (hasOnlyErrors())
			return null;
		
		Collection<DiscenteAdapter> discentesSelecionados = new ArrayList<DiscenteAdapter>();
		for (DiscenteAdapter dis : discentes) {
			if ( dis.isMatricular()  )
				discentesSelecionados.add(dis);
		}
		
		if ( discentesSelecionados.isEmpty() ) {
			addMensagemErro("� necess�rio informar pelo menos um discente.");
			return null;
		} else {
			discentes.clear();
			discentes.addAll(discentesSelecionados);
		}
		
		TurmaEntradaTecnicoDao dao = getDAO(TurmaEntradaTecnicoDao.class);
		try {
			curso =  dao.findByPrimaryKey(curso.getId(), CursoTecnico.class);
			turmaEntradaOrigem = dao.findByPrimaryKey(turmaEntradaOrigem.getId(), TurmaEntradaTecnico.class);
			turmasEntradaDestino.clear();
			turmasEntradaDestino.addAll(dao.findByCursoTecnico(curso.getId(), turmaEntradaOrigem.getId(), turmaEntradaOrigem.getAnoReferencia(), turmaEntradaOrigem.getPeriodoReferencia(), turmaEntradaOrigem.getDadosTurmaIMD().getCronograma().getId()));			
		} finally {
			dao.close();
		}
		
		return forward("/metropole_digital/transferencia_turma/sel_turma_entrada_destino.jsf");
	}
	
	/**
	 * Respons�vel por carregar as turma de entrada do curso selecionado.
	 * 
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/transferenciaTurmaEntrada/sel_turma_entrada.jsp</li>
	 * </ul>
	 */
	public void carregarTurmasEntrada(ValueChangeEvent e) throws DAOException {
		curso.setId( (Integer) e.getNewValue() );
		carregarTurmasEntrada(curso.getId());
		if ((Integer)e.getNewValue()==0) {
			discentes = Collections.emptyList();
			turmaEntradaOrigem = new TurmaEntradaTecnico();
		}
 	}

	/**
	 * Respons�vel por carregar as turmas de entrada do curso selecionado.
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
	 * Respons�vel por carregar os discentes da turma de entrada selecionada.
	 * 
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/transferenciaTurmaEntrada/sel_turma_entrada.jsp</li>
	 * </ul>
	 */
	public void carregarDiscentes(ValueChangeEvent e) throws ArqException {
		DiscenteTecnicoDao dao = getDAO(DiscenteTecnicoDao.class);
		try {
			turmaEntradaOrigem.setId( (Integer) e.getNewValue() );
			if (turmaEntradaOrigem.getId()!=0) {
				discentes = dao.findByTurmaEntrada(turmaEntradaOrigem.getId(), getUnidadeGestora(), NivelEnsino.TECNICO, true);
			}else{
				discentes = Collections.emptyList();
			}
			
			if ( !hasOnlyErrors() && discentes.isEmpty()&& turmaEntradaOrigem.getId()!=0)
				addMensagemErro("N�o foram encontrados discentes para a turma de entrada selecionada.");
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Respons�vel por validar e carregar as poss�veis turmas de destino caso o discente esteja matriculado em alguma turma.
	 * 
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/transferenciaTurmaEntrada/sel_turma_entrada_destino.jsp</li>
	 * </ul>
	 */
	public String submeterTurmaDestino() throws HibernateException, ArqException {
		TransferenciaTurmaIMDDao dao = getDAO(TransferenciaTurmaIMDDao.class);
		
		ValidatorUtil.validateRequiredId(turmaEntradaDestino.getId(), "Turma de Destino", erros);
		
		if (hasErrors()) {
			return null;
		}
		
		try {
			if ( turmaEntradaDestino.getId() == turmaEntradaOrigem.getId() ) {
				addMensagemErro("A Turma de Entrada de Destino n�o pode ser igual a Turma de Entrada de Origem.");
				return null;
			}
			
			CalendarioAcademico calendario =  getCalendarioVigente();
			turmaEntradaDestino = getGenericDAO().findByPrimaryKey(turmaEntradaDestino.getId(), TurmaEntradaTecnico.class);
			turmas = dao.findTurmasMatriculadasByDiscente(discentes, calendario.getAno(), calendario.getPeriodo(), turmaEntradaDestino);			
			setOperacaoAtiva( SigaaListaComando.EFETUAR_TRANSFERENCIA_ENTRE_TURMAS.getId() );
		} finally {
			dao.close();
		}
		return forward("/metropole_digital/transferencia_turma/matriculas.jsf");
	}
	
	/**
	 * Respons�vel por realizar as transfer�ncias entre turmas de entrada do(s) discente(s) selecionados.
	 * 
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/transferenciaTurmaEntrada/matriculas.jsp</li>
	 * </ul>
	 */
	public String efeturarTransferencia() throws ArqException {
		for (int i = 0; i < turmas.size(); i++) {
			if (turmas.get(i)==null) {
				addMessage("Tranfer�ncia n�o realizada. N�o existem disciplinas abertas vinculadas ao aluno selecionado. ", TipoMensagemUFRN.ERROR);
				return null;
			}			
		}		
		
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
	
	public Collection<SelectItem>getComboTurmasEntradaDestino(){
		return toSelectItems((turmasEntradaDestino), "id", "descricao");
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

	public List<SelectItem> getCursosCombo() {
		return cursosCombo;
	}

	public void setCursosCombo(List<SelectItem> cursosCombo) {
		this.cursosCombo = cursosCombo;
	}

	public Collection<TurmaEntradaTecnico> getTurmasEntradaDestino() {
		return turmasEntradaDestino;
	}

	public void setTurmasEntradaDestino(
			Collection<TurmaEntradaTecnico> turmasEntradaDestino) {
		this.turmasEntradaDestino = turmasEntradaDestino;
	}
	
}