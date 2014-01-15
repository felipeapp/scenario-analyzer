/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Classe de dom�nio que armazena as solicita��es de matricula feitas pelos
 * discente durante a pr�-matricula.
 *
 * @author Ricardo Wendell
 *
 */
@Entity
@Table(name = "solicitacao_matricula", schema = "graduacao", uniqueConstraints = {})
public class SolicitacaoMatricula implements PersistDB {

	/** Define o status CADASTRADA para a solicita��o de matr�cula. */
	public static final int CADASTRADA = 1;
	/** Define o status SUBMETIDA para a solicita��o de matr�cula. */
	public static final int SUBMETIDA = 2;
	
	/** Coordenador n�o deu nenhum parecer a respeito desta solicita��o e o prazo expirou. */
	public static final int VISTO_EXPIRADO = 4;

	/** Solicita��o aceita pelo coordenador ou orientador (stricto)
	 *  (essa orienta��o resulta na efetiva��o ou cria��o da matr�cula) - Usado em Stricto e EAD */
	public static final int ATENDIDA = 5;

	/** solicita��o negada pelo coordenador, utilizada apenas para EAD, aluno Especial e Stricto onde
	 * o coordenador pode negar uma matricula */
	public static final int NEGADA = 6;

	/**
	 * o coordenador de cursos EAD pode matricular alunos
	 * em turmas diretamente criando uma solicita��o e j� atendendo a mesma.
	 */
	public static final int SOLICITADA_COORDENADOR = 7;

	/** Coordenador orientou o aluno a n�o se matricula nesta disciplina, mas a escolha � do aluno.
	 * Apenas para Gradua��o presencial */
	public static final int ORIENTADO = 8;

	/**
	 * No caso do aluno desistir de solicitar e exclui a solicita��o
	 */
	public static final int EXCLUIDA = 9;

	/** o coordenador d� o visto na solicita��o de matr�cula. n�o resulta na cria��o da matr�cula,
	 * simplesmente informa que o orientador concordou com essa matr�cula. Usado
	 * na gradua��o Presencial
	 */
	public static final int VISTA = 10;
	
	
	/** Utilizado no caso de matr�cula de discentes de p�s STRICTO em disciplinas de outros programas.
	 * A solicita��o foi aceita pelo orientador do discente de STRICTO ou pela coordena��o de seu programa 
	 * Por�m para que a matr�cula do discente seja efetivada � necess�rio a confirma��o do programa em que ele est� tentando se matricular
	 * Esta situa��o define este estado, aguardando a confirma��o da matr�cula do outro programa
	 */
	public static final int AGUARDANDO_OUTRO_PROGRAMA = 11;
	/** Define o status NEGADA POR OUTRO PROGRAMA para a solicita��o de matr�cula. */
	public static final int NEGADA_OUTRO_PROGRAMA = 12;
	/** Define o status EXCLUS�O SOLICITADA PELO DISCENTE para a solicita��o de matr�cula. */
	public static final int EXCLUSAO_SOLICITADA = 13;
	
	/** Cole��o de status ativos de stricto sensu. */
	public static final List<Integer> STATUS_ATIVOS_STRICTO = Arrays.asList( SUBMETIDA, ATENDIDA );
	/** Cole��o de status ativos de gradua��o presencial. */
	public static final List<Integer> STATUS_ATIVOS_GRAD_PRESENCIAL = Arrays.asList( SUBMETIDA, VISTA, ORIENTADO  );
	/** Cole��o de status ativos de gradua��o a dist�ncia. */
	public static final List<Integer> STATUS_ATIVOS_GRAD_EAD = Arrays.asList( SUBMETIDA, ORIENTADO, SOLICITADA_COORDENADOR );
	/** Chave prim�ria. */
	private int id;
	/** Turma para qual est� sendo solicitada a matr�cula. */
	private Turma turma;
	/** Discente que est� solicitando a matr�cula. */
	private Discente discente;

	/** Status da solicita��o */
	private int status;
	/** DAta da solicita��o */
	private Date dataSolicitacao;

	/** Data da an�lise da solicita��o */
	private Date dataAnalise;

	/** Usu�rio que efetuou a analise da solicita��o */
	private RegistroEntrada registroEntrada;
	/** Per�odo da solicita��o */
	private Integer periodo;
	/** Ano da solicita��o */
	private Integer ano;
	/** Matr�cula no componente que foi atendida por esta solicita��o */
	private MatriculaComponente matriculaGerada;
	/** Observa��o da solicita��o */
	private String observacao;
	/** Indica que o prazo da da solicita��o foi expirado. */
	private boolean prazoExpirado;

	/** n�mero da solicita��o exibido ao usu�rio para identificar o conjunto de solicita��es realizadas */
	private Integer numeroSolicitacao;
	/** Data de cadastro da solicita��o. */
	private Date dataCadastro;
	/** Registro de entrada do cadastro da solicita��o. */
	private RegistroEntrada registroCadastro;
	/** Data de altera��o da solicita��o. */
	private Date dataAlteracao;
	/** Registro de entrada da altera��o da solicita��o. */
	private RegistroEntrada registroAlteracao;
	/** Indica se a solicita��o de matr�cula foi anulada. */
	private Boolean anulado;
	/** Registro de entrada da anula��o da solicita��o. */
	private RegistroEntrada registroAnulacao;
	/** Observa��o sobre a anula��o da solicita��o. */
	private String observacaoAnulacao;
	/** Indica que a solicita��o de matr�cula foi selecionada. */
	private boolean selected;
	/** ID da matr�cula gerada. */
	private Integer idMatriculaGerada;
	/** Indica que a solicita��o de matr�cula foi selecionada. */
	private Boolean rematricula;
	/** Atividade solicitada pelo discente. */
	private ComponenteCurricular atividade;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_atividade")
	public ComponenteCurricular getAtividade() {
		return this.atividade;
	}

	public void setAtividade(ComponenteCurricular atividade) {
		this.atividade = atividade;
	}

	@Column(updatable=false, insertable=false, name="id_matricula_gerada")
	public Integer getIdMatriculaGerada() {
		return idMatriculaGerada;
	}

	public void setIdMatriculaGerada(Integer idMatriculaGerada) {
		this.idMatriculaGerada = idMatriculaGerada;
	}

	public Integer getNumeroSolicitacao() {
		return numeroSolicitacao;
	}

	public void setNumeroSolicitacao(Integer numeroSolicitacao) {
		this.numeroSolicitacao = numeroSolicitacao;
	}

	public boolean isPrazoExpirado() {
		return prazoExpirado;
	}

	public void setPrazoExpirado(boolean prazoExpirado) {
		this.prazoExpirado = prazoExpirado;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_matricula_gerada")
	public MatriculaComponente getMatriculaGerada() {
		return matriculaGerada;
	}

	public void setMatriculaGerada(MatriculaComponente matriculaGerada) {
		this.matriculaGerada = matriculaGerada;
	}

	public SolicitacaoMatricula() {

	}

	public SolicitacaoMatricula(Integer integer) {
		id = integer;
	}

	public SolicitacaoMatricula(Integer integer ,Integer idMatriculaGerada) {
		id = integer;
		matriculaGerada = new MatriculaComponente(idMatriculaGerada);
	}

	public SolicitacaoMatricula(Integer integer, Integer numSolicitacao, Integer turmaId, String turmaCod, Integer disciplinaId, String nomeDisciplina) {
		id = integer;
		numeroSolicitacao = numSolicitacao;
		turma = new Turma(turmaId);
		turma.setCodigo(turmaCod);
		ComponenteCurricular disciplina = new ComponenteCurricular(disciplinaId);
		disciplina.setNome(nomeDisciplina);
		turma.setDisciplina(disciplina);
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_analise", unique = false, nullable = true, insertable = true, updatable = true)
	public Date getDataAnalise() {
		return dataAnalise;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_solicitacao", unique = false, nullable = true, insertable = true, updatable = true)
	public Date getDataSolicitacao() {
		return dataSolicitacao;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_discente", unique = false, nullable = true, insertable = true, updatable = true)
	public Discente getDiscente() {
		return discente;
	}

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_solicitacao_matricula", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return id;
	}

	@Column(name = "status", unique = false, nullable = true, insertable = true, updatable = true, length = 8)
	public int getStatus() {
		return status;
	}

	@ManyToOne( fetch = FetchType.EAGER)
	@JoinColumn(name = "id_turma", unique = false, insertable = true, updatable = true)
	public Turma getTurma() {
		return turma;
	}

	public void setDataAnalise(Date dataAnalise) {
		this.dataAnalise = dataAnalise;
	}

	public void setDataSolicitacao(Date dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	@ManyToOne( fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada", unique = false, nullable = true, insertable = true, updatable = true)
	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testTransientEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(getId());
	}

	/**
	 * se j� foi analisada pela coordena��o, seja dando o visto, negando ou
	 * atendendo
	 */
	public boolean foiAnalisada() {
		return status != CADASTRADA && status != SUBMETIDA;
	}

	/** Retorna uma descri��o textual do status.
	 * @return
	 */
	@Transient
	public String getStatusDescricao() {
		switch (status) {
			case CADASTRADA : return "CADASTRADA";
			case SUBMETIDA : return "SUBMETIDA";
			//case VISTO_COORDENADOR: return "Visto";
			case VISTO_EXPIRADO: return "EXPIRADA";
			case ATENDIDA: return "ATENDIDA";
			case NEGADA: return "NEGADA";
			case VISTA: return "VISTA";
			case ORIENTADO: return "ORIENTADA";
			case EXCLUIDA: return "EXCLU�DA";
			case AGUARDANDO_OUTRO_PROGRAMA: return "AGUARDANDO OUTRO PROGRAMA";
			case NEGADA_OUTRO_PROGRAMA: return "NEGADA";
			case EXCLUSAO_SOLICITADA: return "EXCLUS�O SOLICITADA PELO DISCENTE";
		}
		return null;
	}

	@Column(name="data_cadastro")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date criadoEm) {
		dataCadastro = criadoEm;
	}

	@JoinColumn(name="id_registro_cadastro")
	@ManyToOne( fetch = FetchType.LAZY)
	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada criadoPor) {
		registroCadastro = criadoPor;
	}

	@Column(name="data_alteracao")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date atualizadoEm) {
		dataAlteracao = atualizadoEm;
	}

	@JoinColumn(name="id_registro_alteracao")
	@ManyToOne( fetch = FetchType.LAZY)
	public RegistroEntrada getRegistroAlteracao() {
		return registroAlteracao;
	}

	public void setRegistroAlteracao(RegistroEntrada atualizadoPor) {
		registroAlteracao = atualizadoPor;
	}

	public Boolean getAnulado() {
		return anulado;
	}

	public void setAnulado(Boolean anulado) {
		this.anulado = anulado;
	}

	@Transient
	public boolean isVista() {
		return status == VISTA;
	}

	@Transient
	public boolean isOrientada() {
		return status == ORIENTADO;
	}

	@Transient
	public boolean isAtendida() {
		return status == ATENDIDA;
	}

	@Transient
	public boolean isNegada() {
		return status == NEGADA;
	}
	
	@Transient
	public boolean isAguardandoOutroPrograma() {
		return status == AGUARDANDO_OUTRO_PROGRAMA;
	}
	
	@Transient
	public boolean isNegadaOutroPrograma() {
		return status == NEGADA_OUTRO_PROGRAMA;
	}
	
	@Transient
	public boolean isExclusaoSolicitada() {
		return status == EXCLUSAO_SOLICITADA;
	}
	
	@Transient
	public boolean isExcluida() {
		return status == EXCLUIDA;
	}

	@JoinColumn(name="id_registro_anulacao")
	@ManyToOne( fetch = FetchType.LAZY)
	public RegistroEntrada getRegistroAnulacao() {
		return registroAnulacao;
	}

	public void setRegistroAnulacao(RegistroEntrada registroAnulacao) {
		this.registroAnulacao = registroAnulacao;
	}

	@Column(name="observacao_anulacao")
	public String getObservacaoAnulacao() {
		return observacaoAnulacao;
	}

	public void setObservacaoAnulacao(String observacaoAnulacao) {
		this.observacaoAnulacao = observacaoAnulacao;
	}

	@Transient
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public static Collection<Integer> getStatusSolicitacoesPendentes() {
		return Arrays.asList( new Integer[] {SUBMETIDA, ORIENTADO, VISTA} );
	}


	/** Retorna uma descri��o textual do status de processamento da matr�cula.
	 * @return
	 */
	@Transient
	public String getProcessamentoStatus() {
		if (discente.isStricto()) {
			if (isDeferida()) return "MATRICULADO";
			if (isInDeferida() || isNegadaOutroPrograma() || isExclusaoSolicitada() ) return "";
			return "AGUARDANDO AN�LISE";
		} else {
			if (isDeferida()) return "DEFERIDA";
			if (isDesistencia()) return "DESIST�NCIA";
			if (isInDeferida()) return "INDEFERIDA";
			if (isProcessada() && (getMatriculaGerada().isExcluida() || getMatriculaGerada().isCancelada()) ) {
				return "MATR�CULA "+ getMatriculaGerada().getSituacaoMatricula();
			}
			return "AGUARDANDO PROCESSAMENTO";
		}
	}

	/** Indica se h� desist�ncia da solicita��o de matricula.
	 * @return
	 */
	@Transient
	private boolean isDesistencia() {
		return isProcessada()
			&& matriculaGerada.getSituacaoMatricula().equals(SituacaoMatricula.DESISTENCIA);
	}

	/** Indica se h� deferimento da solicita��o de matricula.
	 * @return
	 */
	@Transient
	public boolean isDeferida() {
		return isProcessada()
			&& SituacaoMatricula.getSituacoesMatriculadoOuConcluido().contains(matriculaGerada.getSituacaoMatricula()) ;
	}

	/** Indica se h� indeferimento da solicita��o de matricula.
	 * @return
	 */
	@Transient
	public boolean isInDeferida() {
		return isProcessada()
			&& matriculaGerada.getSituacaoMatricula().equals(SituacaoMatricula.INDEFERIDA);
	}

	/** Indica se a solicita��o de matricula foi processada.
	 * @return
	 */
	@Transient
	public boolean isProcessada() {
		return matriculaGerada != null;
	}

	/** Retorna uma descri��o textual do processamento da matr�cula.
	 * @return
	 */
	@Transient
	public String getResultadoProcessamento() {
		String resultado = "EM ESPERA";
		if (isProcessada()) {
			if (!isInDeferida()) {
				resultado = "MATRICULADO";
			} else {
				resultado = "INDEFERIDA";
			}
		}
		return resultado;
	}

	/** Retorna uma matr�cula com status "EM ESPERA" a partir dos dados desta solicita��o de matr�cula.
	 * @return
	 */
	public MatriculaComponente criarMatriculaEmEspera() {
		return criarMatriculaComStatus(SituacaoMatricula.EM_ESPERA);
	}

	/** Retorna uma matr�cula com status "MATRICULADO" a partir dos dados desta solicita��o de matr�cula.
	 * @return
	 */
	public MatriculaComponente criarMatriculaMatriculado() {
		return criarMatriculaComStatus(SituacaoMatricula.MATRICULADO);
	}

	/** Retorna uma matr�cula com status indicado a partir dos dados desta solicita��o de matr�cula.
	 * @param situacao
	 * @return
	 */
	public MatriculaComponente criarMatriculaComStatus(SituacaoMatricula situacao) {
		MatriculaComponente matricula = new MatriculaComponente();
		matricula.setAno(ano.shortValue());
		matricula.setPeriodo(periodo.byteValue());
		matricula.setComponente(turma.getDisciplina());
		matricula.setDiscente(discente);
		matricula.setDataCadastro(new Date());
		matricula.setDetalhesComponente(turma.getDisciplina().getDetalhes());
		matricula.setSituacaoMatricula(situacao);
		matricula.setTurma(turma);
		return matricula;
	}

	public Boolean getRematricula() {
		return this.rematricula;
	}

	public void setRematricula(Boolean rematricula) {
		this.rematricula = rematricula;
	}

	/** Retorna o componente curricular associado � esta solicita��o de matr�cula.
	 * @return
	 */
	@Transient
	public ComponenteCurricular getComponente(){
		if (turma != null) {
			return turma.getDisciplina();
		} else if ( atividade != null ) {
			return atividade;
		} else {
			return null;
		}
	}

	@Transient
	public boolean isTipoAtividade(){
		return !isEmpty( atividade );
	}


	@Transient
	public String getDescricaoComponente() {
		return getComponente().getDescricao();
	}
	
	@Transient
	public String getAnoPeriodo() {
		return ano + "." + periodo;
	}
	
	/**
	 * Retorna todas as solicita��es consideradas v�lidas
	 **/
	public static Collection <SolicitacaoMatricula> getSolicitacoesValidas (){
		ArrayList <SolicitacaoMatricula> solicitacoes = new ArrayList <SolicitacaoMatricula> ();
		
		solicitacoes.add (new SolicitacaoMatricula (CADASTRADA));
		solicitacoes.add (new SolicitacaoMatricula (SUBMETIDA));
		solicitacoes.add (new SolicitacaoMatricula (ATENDIDA));
		solicitacoes.add (new SolicitacaoMatricula (ORIENTADO));
		solicitacoes.add (new SolicitacaoMatricula (VISTA));
		
		return solicitacoes;
	}
}
