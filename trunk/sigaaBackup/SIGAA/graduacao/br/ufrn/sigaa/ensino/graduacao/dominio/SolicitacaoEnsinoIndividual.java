/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * 22/06/2007
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Esta entidade representa uma solicita��o de turma de ensino individual ou de ferias feita pelo discente.
 * Caso o coordenador do curso aceite esta solicita��o ent�o ser� criado uma solicita��o de turma
 * @author leonardo
 * @author Victor Hugo
 */
@Entity
@Table(name = "solicitacao_ensino_individual", schema = "graduacao", uniqueConstraints = {})
public class SolicitacaoEnsinoIndividual implements Validatable, Comparable<SolicitacaoEnsinoIndividual> {

	/**
	 * situa��es da solicita��o de ensino individual
	 */
	public static final int SOLICITADA = 1;
	public static final int CANCELADA = 2;
	public static final int ATENDIDA = 3;
	public static final int NEGADA = 4;
	public static final int CANCELADA_POR_MATRICULA = 5; //quando a solicita��o � cancelada por causa de uma solicita��o de matricular regular realizada para este componente

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_solicitacao_ensino_individual", nullable = false)
	private int id;

	@ManyToOne
	@JoinColumn(name = "id_discente")
	private DiscenteGraduacao discente;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_solicitacao")
	@CriadoEm
	private Date dataSolicitacao;

	@ManyToOne
	@JoinColumn(name = "id_componente_curricular")
	private ComponenteCurricular componente;

	
	/**
	 * Usado em Ensino Individual. Somente usado para quando o aluno estiver solicitando um componente equivalente ao obrigatorio.
	 * Este atributo indica qual o componente obrigatorio da estrutura do aluno ta sendo usado.
	 */
	@ManyToOne
	@JoinColumn(name = "id_componente_curricular_eq")
	private ComponenteCurricular equivalente;
	
	private int situacao;

	private int ano;

	private int periodo;

	/**
	 * tipo da turma solicitada:
	 * FERIAS = 2;
	 * ENSINO_INDIVIDUAL = 3;
	 *  -as constantes est�o em {@link Turma}
	 */
	private int tipo;
	
	/** Este campo armazena a justificativa da nega��o do requerimento do aluno, 
	 * utilizado apenas caso  o coordenador do curso negue a solicita��o */
	@Column(name="justificativa_negacao")
	private String justificativaNegacao;

	/** Solicita��o de turma criada a partir desta solicita��o */
	@ManyToOne
	@JoinColumn(name = "id_solicitacao_turma")
	private SolicitacaoTurma solicitacaoTurma;

	@ManyToOne
	@JoinColumn(name = "id_matricula_gerada")
	private MatriculaComponente matriculaGerada;
	
	/**
	 * Este campo cont�m uma sugest�o de hor�rio que ele gostaria que a turma possua
	 */
	@Column(name="sugestao_horario")
	private String sugestaoHorario;

	/** Data em que a solicita��o foi atendida
	 * Quando a situa��o =  CANCELADA_POR_MATRICULA = 5 a data de atendimento � a data que foi submetida a solicita��o de matr�cula que cancelou esta solicita��o
	*/
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_atendimento")
	private Date dataAtendimento;

	/** Registro entrada de quem atendeu a solicita��o
	 * Quando a situa��o = CANCELADA_POR_MATRICULA = 5 o registroEntradaAtendente � o registro de entrada do usu�rio que submeteu a solicita��o de matr�cula que cancelou esta solicita��o
	 */
	@ManyToOne
	@JoinColumn(name = "id_registro_entrada_atendente")
	private RegistroEntrada registroEntradaAtendente;

	@ManyToOne
	@JoinColumn(name = "id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroEntrada;

	/** N�mero da solicita��o exibido ao usu�rio para identificar a solicita��o realizada */
	@Column(name = "numero_solicitacao")
	private Integer numeroSolicitacao;
	
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(discente, "Discente", erros);
		ValidatorUtil.validateRequired(componente, "Disciplina", erros);
		return erros;
	}

	public Date getDataSolicitacao() {
		return dataSolicitacao;
	}

	public void setDataSolicitacao(Date dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public int getSituacao() {
		return situacao;
	}

	public String getSituacaoString(){
		switch (situacao) {
		case ATENDIDA:
			if( solicitacaoTurma != null && solicitacaoTurma.isNegada() )
				return "Turma Negada";
			else if( solicitacaoTurma.isAtendida() )
				return "Atendida - Turma Criada";
			else
				return "Turma Solicitada";
		case CANCELADA:
			return "Cancelada";
		case NEGADA:
			return "Solicita��o Negada";
		case SOLICITADA:
			return "Pendente";
		default:
			return "Indefinida";
		}
	}

	public void setSituacao(int situacao) {
		this.situacao = situacao;
	}

	/**
	 * Diz se a solicita��o tem a situa��o SOLICITADA
	 * Utilizado na view para definir se pode ou n�o renderizar o bot�o de cancelar as solicita��es
	 * S� pode cancelar solicita��es que a situa��o SOLICITADA
	 * @return
	 */
	public boolean isSolicitada(){
		return situacao == SOLICITADA;
	}

	public boolean isNegada(){
		return situacao == NEGADA;
	}
	
	public boolean isAtendida(){
		return situacao == ATENDIDA;
	}
	
	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public SolicitacaoTurma getSolicitacaoTurma() {
		return solicitacaoTurma;
	}

	public void setSolicitacaoTurma(SolicitacaoTurma solicitacaoTurma) {
		this.solicitacaoTurma = solicitacaoTurma;
	}

	public Date getDataAtendimento() {
		return dataAtendimento;
	}

	public void setDataAtendimento(Date dataAtendimento) {
		this.dataAtendimento = dataAtendimento;
	}

	public RegistroEntrada getRegistroEntradaAtendente() {
		return registroEntradaAtendente;
	}

	public void setRegistroEntradaAtendente(RegistroEntrada registroEntradaAtendente) {
		this.registroEntradaAtendente = registroEntradaAtendente;
	}

	public DiscenteGraduacao getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteGraduacao discente) {
		this.discente = discente;
	}

	public ComponenteCurricular getComponente() {
		return componente;
	}

	public void setComponente(ComponenteCurricular componente) {
		this.componente = componente;
	}

	public MatriculaComponente getMatriculaGerada() {
		return matriculaGerada;
	}

	public void setMatriculaGerada(MatriculaComponente matriculaGerada) {
		this.matriculaGerada = matriculaGerada;
	}

	public String getSugestaoHorario() {
		return sugestaoHorario;
	}

	public void setSugestaoHorario(String sugestaoHorario) {
		this.sugestaoHorario = sugestaoHorario;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	public String getJustificativaNegacao() {
		return justificativaNegacao;
	}

	public void setJustificativaNegacao(String justificativaNegacao) {
		this.justificativaNegacao = justificativaNegacao;
	}

	public String getTipoString(){
		switch (tipo) {
			case Turma.FERIAS:
				return "f�rias";
			case Turma.ENSINO_INDIVIDUAL:
				return "ensino individual";
			default:
				return "indefinido";
		}
	}

	public boolean isFerias(){
		return tipo == Turma.FERIAS;
	}

	public boolean isEnsinoIndividual(){
		return tipo == Turma.ENSINO_INDIVIDUAL;
	}

	public int compareTo(SolicitacaoEnsinoIndividual obj) {
		int result = 0;
		if( this.discente != null && obj.getDiscente() != null )
			result = this.discente.getNome().compareTo( obj.getDiscente().getNome() );
		return result;
	}

	public Integer getNumeroSolicitacao() {
		return numeroSolicitacao;
	}

	public void setNumeroSolicitacao(Integer numeroSolicitacao) {
		this.numeroSolicitacao = numeroSolicitacao;
	}

	public ComponenteCurricular getEquivalente() {
		return equivalente;
	}

	public void setEquivalente(ComponenteCurricular equivalente) {
		this.equivalente = equivalente;
	}

	/**
	 * Solicitacao de componente obrigat�rio
	 */
	public boolean isComponenteObrigatorio() {
		return equivalente == null;
	}
	
}
