/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 * Created on 10 de Janeiro de 2007
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.AbstractMovimento;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;

/**
 * Entity class SolicitacaoTurma
 *
 * @author Gleydson
 * @author Victor Hugo
 */
@Entity
@Table(name = "solicitacao_turma", schema="graduacao")
public class SolicitacaoTurma extends AbstractMovimento implements Validatable {

	/**
	 * situa��es da solicita��o de turma
	 */
	
	/** Indica que a solicita��o ainda encontra-se aberta */
	public static final int ABERTA = 1;
	/** Indica que foi solicitada altera��o na solicita��o de turma */
	public static final int SOLICITADA_ALTERACAO = 2;
	/** Indica que solicita��es de altera��o na solicita��o de turma foram atendidas */
	public static final int ATENDIDA_ALTERACAO = 3;
	/** Indica que a solicita��o de turma foi atendida parcialmente */
	public static final int ATENDIDA_PARCIALMENTE = 4;
	/** Indica que a solicita��o de turma foi atendida e a turma foi criada */
	public static final int ATENDIDA = 5;
	/** Indica que a solicita��o de turma foi negada */
	public static final int NEGADA = 6;
	/** Indica que a solicita��o de turma foi removida */
	public static final int REMOVIDA = 7;
	/** Indica que a solicita��o foi uma sugest�o do departamento */
	public static final int SUGESTAO_DEPARTAMENTO = 8;

	/**
	 * Chave prim�ria
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_solicitacao_turma", nullable = false)
	private int id;

	/** C�digo que representa o hor�rio */
	private String horario;

	/** N�mero de vagas solicitadas */
	private short vagas;

	/** Observa��es da solicita��o */
	private String observacoes;

	/** Situa��o atual da solicita��o */
	private int situacao;

	/** Ano da solicita��o */
	private short ano;

	/** Per�odo da solicita��o */
	private byte periodo;
	
	/**
	 * Unidade utilizada para gerar a grade de Hor�rios. 
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_unidade")
	private Unidade unidade;

	/** Componente curricular pretendido para a turma*/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_componente_curricular", nullable = false)
	private ComponenteCurricular componenteCurricular;

	/** Curso que solicitou */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_curso", unique = false, insertable = true, updatable = true)
	private Curso curso;

	/**
	 * este atributo guarda a data que a solicita��o foi removida ou negada.
	 * este atributo deve ser nulo caso a solicita��o n�o tenha sido removida ou negada.
	 * Uma solicita��o s� pode ser removida pelo coordenador que a criou ou negada pelo chefe/secret�rio do departamento do componente
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_remocao", unique = false, insertable = true, updatable = true)
	private Date dataRemocao;

	/** Reservas solicitadas para cada curso */
	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "solicitacao")
	private Collection<ReservaCurso> reservas = new ArrayList<ReservaCurso>();

	/**
	 * lista dos alunos interessados nesta turma
	 * deve ser utilizado apenas quando a solicita��o � de turma especial de ferias.
	 * Ou no caso de solicita��es de turmas de ensino individual que tamb�m possui uma lista de discentes interessados
	 */
	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "solicitacaoTurma")
	private List<DiscentesSolicitacao> discentes;

	/**
	 * motivo da solicita��o da turma, deve ser utilizado apenas para curso especial de feiras !
	 */
	@Column(name="motivo")
	private String motivo;
	
	/**
	 * observa��o inserida na sugest�o de solicita��o da turma, deve ser utilizado apenas pelo chefe de departamento.
	 */
	@Column(name="observacao_sugestao")
	private String observacaoSugestao;

	/**
	 * Atributo de relacionamento NxN entre Turma e SolicitacaoTurma
     * para ter refer�ncia de quais solicita��es originaram quais turmas
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "solicitacao")
	private Collection<TurmaSolicitacaoTurma> turmasSolicitacaoTurmas = new HashSet<TurmaSolicitacaoTurma>();

	/** tipo da solicita��o de turma: REGULAR, FERIAS, ENSINO INDIVIDUAL*/
	private int tipo;

	/**
	 * Registro de entrada do solicitante
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada", unique = false, nullable = false, insertable = true, updatable = false)
	@CriadoPor
	private RegistroEntrada registroEntrada;

	/** Data de cadastro */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro", unique = false, insertable = true, updatable = true)
	@CriadoEm
	private Date dataCadastro;
	
	/** Registro de entrada do usuario responsavel pela atualiza��o da solicita��o */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/** Data de atualiza��o */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_atualizacao")
	@AtualizadoEm
	private Date dataAtualizacao;

	/** usado para levar para o processador as solicita��es de ensino
	 * individual atendidas na cria��o desta solicita��o de turma para atualizar a situa��o de cada uma delas*/
	@Transient
	private Collection<SolicitacaoEnsinoIndividual> solicitacoesEnsinoIndividualAtendidas;

	/**
	 * Usado para solicitar turmas com hor�rio flex�vel;
	 */
	@Transient
	private List<HorarioTurma> horarios;
	
	/**
	 * Calendario academico do semestre que a turma ser� criada. 
	 * Ex se solicita��o for em 2010.1, este calendario deve ser o de 2010.2
	 */
	@Transient
	private CalendarioAcademico calendario;
	
	/** 
	 * Define se o coordenador do curso pode solicitar turma de f�rias sem que os discentes tenham realizado a solicita��o.
	 * Caso FALSE o coordenador s� poder� solicitar turmas se algum discente tiver solicitado.
	 * 
	 * Veja o parametro ParametrosGraduacao.DEFINE_SOLICITACAO_TURMA_FERIAS_SEM_SOLICITACAO
	 */
	@Transient
	private boolean permiteSolicitarTurmaSemSolicitacao;
	
	/** Creates a new instance of SolicitacaoTurma */
	public SolicitacaoTurma() {
	}

	public SolicitacaoTurma(int id, short ano, byte periodo, String codigo, String componente, int idTipo,
			int idSituacao, String horario, short vagas, int idDepartamento, String deptoNome, String deptoSigla, int idCurso, String nomeCurso, Date data ) {

		this.id = id;
		this.ano = ano;
		this.periodo =  periodo;

		this.componenteCurricular = new ComponenteCurricular();
		componenteCurricular.setCodigo(codigo);
		componenteCurricular.setNome(componente);
		this.tipo = idTipo;
		this.situacao = idSituacao;
		this.horario = horario;
		this.vagas = vagas;
		this.componenteCurricular.getUnidade().setId(idDepartamento);
		this.componenteCurricular.getUnidade().setNome(deptoNome);
		this.componenteCurricular.getUnidade().setSigla(deptoSigla);
		this.curso = new Curso();
		this.curso.setId( idCurso );
		this.curso.setNome( nomeCurso );
		this.dataCadastro = data;

	}

	public SolicitacaoTurma(int id, short ano, byte periodo, String codigo, String componente, int idTipo,
			int idSituacao, String horario, short vagas, int idDepartamento, String deptoNome, String deptoSigla ) {

		this.id = id;
		this.ano = ano;
		this.periodo =  periodo;

		this.componenteCurricular = new ComponenteCurricular();
		componenteCurricular.setCodigo(codigo);
		componenteCurricular.setNome(componente);
		this.tipo = idTipo;
		this.situacao = idSituacao;
		this.horario = horario;
		this.vagas = vagas;
		this.componenteCurricular.getUnidade().setId(idDepartamento);
		this.componenteCurricular.getUnidade().setNome(deptoNome);
		this.componenteCurricular.getUnidade().setSigla(deptoSigla);

	}

	public ComponenteCurricular getComponenteCurricular() {
		return componenteCurricular;
	}

	public void setComponenteCurricular(
			ComponenteCurricular componenteCurricular) {
		this.componenteCurricular = componenteCurricular;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ListaMensagens validate() {
		
		ListaMensagens lista = new ListaMensagens();
		
		if( unidade == null || unidade.getId()==0 ) {
			lista.addErro("Unidade respons�vel pela grade de hor�rios da solicita��o n�o foi definida, por favor defina esta unidade ao selecionar os hor�rios da turma.");
		}		
		
		return null;
	}

	public String getHorario() {
		return horario;
	}

	public void setHorario(String horario) {
		this.horario = horario;
	}

	public short getVagas() {
		return vagas;
	}

	public void setVagas(short vagas) {
		this.vagas = vagas;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public int getSituacao() {
		return situacao;
	}

	public void setSituacao(int situacao) {
		this.situacao = situacao;
	}

	/**
	 * M�todo respons�vel por retornar a descri��o das situa��es de solicita��es de turma.
	 * @return
	 */
	public String getSituacaoString() {

		String retorno = "Indefinido";

		switch (situacao) {
			case ABERTA:
				retorno = "Pendente";
				break;
			case SOLICITADA_ALTERACAO:
				retorno = "Solicitado Altera��o";
				break;
			case ATENDIDA_ALTERACAO:
				retorno = "Altera��o Atendida";
				break;
			case ATENDIDA_PARCIALMENTE:
				retorno = "Atendida Parcialmente, ou seja, nem todas as reservas solicitadas foram atendidas";
				break;
			case ATENDIDA:
				retorno = "Atendida";
				break;
			case REMOVIDA:
				retorno = "Removida";
				break;
			case NEGADA:
				retorno = "Negada";
				break;
			case SUGESTAO_DEPARTAMENTO:
				retorno = "Sugest�o do Departamento";
				break;
		}

		return retorno;
	}

	/**
	 * Diz se � poss�vel atender esta solicita��o
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public boolean isPassivelAtendimento() throws DAOException{
		
		if (validarAtendimentoSolicitacao().isErrorPresent())
			return false;
		
		return true;
	}

	/**
	 * Diz se � poss�vel atender esta solicita��o
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public ListaMensagens validarAtendimentoSolicitacao() {
		
		ListaMensagens lista = new ListaMensagens();
		
		if (isAtendida() || isRemovida() || isNegada()) {
			lista.addErro("N�o � poss�vel atender esta solicita��o porque ela est� " + getSituacaoString());
			return lista;
		}
		
		if (isEmpty(calendario)) {
			lista.addErro("N�o � poss�vel validar esta solicita��o de turma porque o calend�rio acad�mico n�o foi definido na opera��o. Por favor, entre em contato com o Suporte do Sistema.");
			return lista;
		}
		
		/** Verificando se esta no per�odo de cadastramento de turma regular */
		if( isTurmaRegular() && !getCalendario().isPeriodoCadastroTurmas() && !getCalendario().isPeriodoAjustesTurmas() && !getCalendario().isPeriodoSugestaoTurmaChefe() )
			lista.addErro("N�o est� no per�odo de cadastro de turmas.");
		
		if (isTurmaRegular() && isEmpty(reservas) )
			lista.addErro("A solicita��o n�o possui reservas de cursos");
		
		/** Verificando se esta no per�odo de cadastramento de turma de f�rias */
		if( isTurmaFerias() && !getCalendario().isPeriodoCadastroTurmasFerias() )
			lista.addErro("N�o est� no per�odo de cadastro de turmas de f�rias.");


		if( isTurmaEnsinoIndividual() && !getCalendario().isPeriodoCadastroTurmaEnsinoIndiv() ){
			Formatador fmt = Formatador.getInstance();
			lista.addErro("N�o � permitido realizar cadastro de turma de ensino individualizado fora do per�odo "
					+ "determinado no calend�rio universit�rio. O per�odo oficial para cadastro de turma de ensino individualizado estende-se de "
					+ fmt.formatarData(getCalendario().getInicioCadastroTurmaEnsinoIndiv()) + " a " + fmt.formatarData(getCalendario().getFimCadastroTurmaEnsinoIndiv()) + ".");
		}

		if( isTurmaFerias() && !getCalendario().isPeriodoCadastroTurmasFerias() ){
			lista.addErro("N�o � permitido realizar cadastro de turma de f�rias fora do per�odo " + "determinado no calend�rio universit�rio." +
					CalendarioAcademico.getDescricaoPeriodo("Cadastro de turma de f�rias", getCalendario().getInicioCadastroTurmaFerias(), getCalendario().getFimCadastroTurmaFerias()) );
		}

		if( getComponenteCurricular().isAtividade() && getComponenteCurricular().getFormaParticipacao() != null && !getComponenteCurricular().getFormaParticipacao().isEspecialColetiva()
				|| getComponenteCurricular().isBloco() ){
			lista.addErro("N�o � poss�vel criar turmas de componentes do tipo Atividade ou bloco.");
		}
		
		return lista;
	}
	
	/**
	 * Diz se pode negar a solicita��o de turma
	 * 
	 * @return
	 * @throws DAOException  
	 */
	public boolean isPassivelNegacaoSolicitacao() {
		if (validarNegacaoSolicitacao().isErrorPresent())
			return false;
		
		return true;		
	}

	/**
	 * Valida a nega��o de uma solicita��o
	 * 
	 * @return
	 */
	public ListaMensagens validarNegacaoSolicitacao() {
		
		ListaMensagens lista = new ListaMensagens();
		
		if (isAtendida() || isRemovida() || isNegada()) {
			lista.addErro("N�o � poss�vel negar esta solicita��o porque ela est� " + getSituacaoString());
			return lista;
		}
		
		if( isTurmaRegular() && !calendario.isPeriodoCadastroTurmas() && !calendario.isPeriodoAjustesTurmas() && !calendario.isPeriodoSugestaoTurmaChefe() )
			lista.addErro("N�o est� no per�odo de cadastro de turmas.");
		
		if( isTurmaFerias() && !calendario.isPeriodoCadastroTurmasFerias() )
			lista.addErro("N�o est� no per�odo de cadastro de turmas de f�rias.");
		
		if( isTurmaEnsinoIndividual() && !calendario.isPeriodoCadastroTurmaEnsinoIndiv() ){
			Formatador fmt = Formatador.getInstance();
			lista.addErro("N�o � permitido realizar qualquer opera��o nesta solicita��o de ensino individualizado fora do per�odo "
					+ "determinado no calend�rio universit�rio. O per�odo oficial para cadastro de turma de ensino individualizado estende-se de "
					+ fmt.formatarData(calendario.getInicioCadastroTurmaEnsinoIndiv()) + " a " + fmt.formatarData(calendario.getFimCadastroTurmaEnsinoIndiv()) + ".");
		}	
		
		return lista;
	}
	
	public boolean isAtendidaOuAtendidaParcialmente(){
		return situacao == ATENDIDA || situacao == ATENDIDA_PARCIALMENTE;
	}

	public boolean isAtendida(){
		return situacao == ATENDIDA;
	}

	public boolean isNegada(){
		return situacao == NEGADA;
	}

	public boolean isRemovida(){
		return situacao == REMOVIDA;
	}
	
	public boolean isNegadaOuSolicitadoAlteracaoHorario(){
		return situacao == NEGADA || situacao == SOLICITADA_ALTERACAO;
	}

	public boolean isTurmaRegular(){
		return getTipo() == Turma.REGULAR;
	}
	
	public boolean isTurmaEnsinoIndividual(){
		return getTipo() == Turma.ENSINO_INDIVIDUAL;
	}

	public boolean isTurmaFerias(){
		return getTipo() == Turma.FERIAS;
	}
	
	/**
	 * retorna true caso o coordenador ainda possa remover esta altera��o ou se o chefe pode negar  a solicita��o,
	 * ou seja, ela deve estar com a situa��o:
	 * ABERTA, SOLICITACA_ALTERACAO, ATENDIDA_ALTERACAO
	 * @return
	 */
	public boolean isPodeRemover(){
		switch (this.situacao) {
		case ABERTA:
			return true;
		case SOLICITADA_ALTERACAO:
			return true;
		case ATENDIDA_ALTERACAO:
			return true;
		case SUGESTAO_DEPARTAMENTO:
			return true;			
		default:
			return false;
		}
	}

	/**
	 * Verifica se a solicita��o pode ser alterada
	 * 
	 * @return
	 */
	public boolean isPodeAlterar(){
		
		System.out.println(componenteCurricular.getCodigo());
		
		if (isTurmaRegular())
			return validaPodeAlterarRegular();

		if (isTurmaEnsinoIndividual())
			return validaPodeAlterarEI();
		
		if (isTurmaFerias()) 
			return validaPodeAlterarFerias();
		
		return false;
	}
	
	/**
	 * Verifica se a solicita��o de f�rias pode ser alterada
	 * 
	 * @return
	 */
	private boolean validaPodeAlterarFerias() {
		
		switch (this.situacao) {
			case ABERTA:
				return true;
			case SOLICITADA_ALTERACAO:
				return true;
			case SUGESTAO_DEPARTAMENTO:
				return true;
		}

		
		return false;
	}

	/**
	 * Verifica se a solicita��o de EI pode ser alterada
	 * 
	 * @return
	 */
	private boolean validaPodeAlterarEI() {
		// so pode alterar se a solicita��o for do mesmo ano e periodo do calendario
		if ( ano * 10 + periodo == calendario.getAno() * 10 + calendario.getPeriodo() ) {
			if (situacao == SOLICITADA_ALTERACAO && calendario.isPeriodoCadastroTurmaEnsinoIndiv())
				return true;
			
			if (situacao == ABERTA && calendario.isPeriodoSolicitacaoTurmaEnsinoIndiv())
				return true;
		}
		
		return false;
	}

	/**
	 * Indica se a solicita��o gera uma turma do tipo REGULAR
	 * 
	 * @return
	 */
	public boolean isGeraTurmaRegular() {
		
		if (isTurmaRegular())
			return true;
		
		if (isTurmaEnsinoIndividual() && getDiscentes() != null && getDiscentes().size() > ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.QTD_MAX_DISCENTES_TURMA_ENSINO_INDIVIDUAL))
			return true;
		
		return false;
	}
	
	/**
	 * Verifica se a solicita��o de turma regular pode ser alterada
	 * 
	 * @return
	 */
	private boolean validaPodeAlterarRegular() {
		// so pode alterar se a solicita��o for do mesmo ano e periodo do calendario
		if ( ano * 10 + periodo == calendario.getAno() * 10 + calendario.getPeriodo() ) {
			if ( calendario.isPeriodoAjustesTurmas() ) 
				return true;
			
			switch (this.situacao) {
				case ABERTA:
					return true;
				case SOLICITADA_ALTERACAO:
					return true;
				case SUGESTAO_DEPARTAMENTO:
					return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Com base na solicita��o � gerado um objeto Turma
	 * 
	 * @return
	 */
	public Turma toTurma() {
		Turma t = new Turma();
		
		t.setSituacaoTurma(new SituacaoTurma());
		t.setDisciplina(new ComponenteCurricular());
		t.setCurso(new Curso());
		t.setCampus( new CampusIes() );
		
		t.setDisciplina(getComponenteCurricular());
		t.setAno( getAno() );
		t.setPeriodo( getPeriodo() );
		t.setDescricaoHorario(getHorario());
		t.setTipo( getTipo() );
		t.setSituacaoTurma(new SituacaoTurma(SituacaoTurma.A_DEFINIR_DOCENTE));
		t.setCapacidadeAluno((int) getVagas());
		
		return t;
	}

	@Override
	public String toString() {
		return toTurma().getDescricaoResumida();
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public short getAno() {
		return ano;
	}

	public void setAno(short nanoo) {
		this.ano = nanoo;
	}

	public byte getPeriodo() {
		return periodo;
	}

	public void setPeriodo(byte periodo) {
		this.periodo = periodo;
	}

	public Collection<ReservaCurso> getReservas() {
		return reservas;
	}

	public void setReservas(Collection<ReservaCurso> reservas) {
		this.reservas = reservas;
	}

	public List<DiscentesSolicitacao> getDiscentes() {
		return discentes;
	}

	public void setDiscentes(List<DiscentesSolicitacao> discentes) {
		this.discentes = discentes;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public String getObservacaoSugestao() {
		return observacaoSugestao;
	}

	public void setObservacaoSugestao(String observacaoSugestao) {
		this.observacaoSugestao = observacaoSugestao;
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	public Collection<TurmaSolicitacaoTurma> getTurmasSolicitacaoTurmas() {
		return turmasSolicitacaoTurmas;
	}

	public void setTurmasSolicitacaoTurmas(
			Collection<TurmaSolicitacaoTurma> turmasSolicitacaoTurmas) {
		this.turmasSolicitacaoTurmas = turmasSolicitacaoTurmas;
	}

	public Date getDataRemocao() {
		return dataRemocao;
	}

	public void setDataRemocao(Date dataRemocao) {
		this.dataRemocao = dataRemocao;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	/**
	 * M�todo respons�vel por retornar a descri��o dos tipos de turma solicitadas.
	 * @return
	 */
	public String getTipoString() {
		switch (tipo) {
			case Turma.REGULAR:
				return "Turma Regular";
			case Turma.FERIAS:
				return "Turma de F�rias";
			case Turma.ENSINO_INDIVIDUAL:
				return "Turma de Ensino Individual";
			default:
				return "Indefinido";
		}
	}

	public Collection<SolicitacaoEnsinoIndividual> getSolicitacoesEnsinoIndividualAtendidas() {
		return solicitacoesEnsinoIndividualAtendidas;
	}

	public void setSolicitacoesEnsinoIndividualAtendidas(
			Collection<SolicitacaoEnsinoIndividual> solicitacoesEnsinoIndividualAtendidas) {
		this.solicitacoesEnsinoIndividualAtendidas = solicitacoesEnsinoIndividualAtendidas;
	}

	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public List<HorarioTurma> getHorarios() {
		return horarios;
	}

	public void setHorarios(List<HorarioTurma> horarios) {
		this.horarios = horarios;
	}

	public CalendarioAcademico getCalendario() {
		return calendario;
	}

	public void setCalendario(CalendarioAcademico calendario) {
		this.calendario = calendario;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public boolean isPermiteSolicitarTurmaSemSolicitacao() {
		return permiteSolicitarTurmaSemSolicitacao;
	}

	public void setPermiteSolicitarTurmaSemSolicitacao(
			boolean permiteSolicitarTurmaSemSolicitacao) {
		this.permiteSolicitarTurmaSemSolicitacao = permiteSolicitarTurmaSemSolicitacao;
	}
	
	
}
