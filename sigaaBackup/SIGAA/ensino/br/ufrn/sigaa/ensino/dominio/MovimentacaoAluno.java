/*
* Universidade Federal do Rio Grande do Norte
* Superintend�ncia de Inform�tica
* Diretoria de Sistemas
*
* Created on 18/10/2007
*/

package br.ufrn.sigaa.ensino.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Collection;
import java.util.Date;

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

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Entidade que registra os diversos tipos de movimenta��o que o aluno pode ter dentro 
 * da institui��o, vinculados diretamente a altera��o de status do discente.
 * Atrav�s das movimenta��es o status do discente � mudado. (ex: se a movimenta��o for de conclus�o de curso
 * o discente tem o status mudado automaticamente para CONCLUIDO.)
 *
 * @author Victor Hugo
 */
@Entity
@Table(name = "movimentacao_aluno", schema = "ensino", uniqueConstraints = {})
public class MovimentacaoAluno implements PersistDB, Validatable {

	// constantes de tipos de retorno de afastamento
	/** Constante que define o tipo de retorno administrativo. */
	public static final int ADMINISTRATIVO = 1;
	/** Constante que define o tipo de retorno judicial. */
	public static final int JUDICIAL = 2;
	/** Constante que define o tipo de retorno definido pela c�mara (de gradua��o ou p�s-gradua��o). */
	public static final int CAMARA = 3;

	// atributos
	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(value = "ensino.movimentacao_aluno_seq", name = "sequence_name") })
	@Column(name = "id_movimentacao_aluno", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Tipo de movimenta��o do aluno. */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_movimentacao_aluno", unique = false, nullable = false, insertable = true, updatable = true)
	private TipoMovimentacaoAluno tipoMovimentacaoAluno;

	/** Discente referente � esta movimenta��o. */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER, targetEntity=Discente.class)
	@JoinColumn(name = "id_discente", unique = false, nullable = false, insertable = true, updatable = true)
	private DiscenteAdapter discente;

	/** Data de ocorr�ncia da movimenta��o. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_ocorrencia", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	private Date dataOcorrencia;

	/** Usu�rio que realizou a movimenta��o, nos casos de afastamento. */
	@CriadoPor
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_cadastro", unique = false, nullable = true, insertable = true, updatable = true)
	private Usuario usuarioCadastro;

	/** Usu�rio que realizou a movimenta��o de retorno. */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_retorno", unique = false, nullable = true, insertable = true, updatable = true)
	private Usuario usuarioRetorno;

	/** Data do cadastro do retorno. */
	@Column(name = "data_cadastro_retorno", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	private Date dataCadastroRetorno;

	/** Ano de refer�ncia que o discente est� trancando. */
	@Column(name = "ano_referencia")
	private Integer anoReferencia;

	/** Per�odo de refer�ncia que o discente est� trancando. */
	@Column(name = "periodo_referencia")
	private Integer periodoReferencia;

	/**
	 * Valor da movimenta��o, usado no caso de prorroga��es ou outras
	 * movimenta��es que trabalhem com valor. No caso de prorroga��o � o n�mero
	 * de semestres. No caso de trancamento de programa para stricto � o n�mero
	 * de meses.
	 */
	@Column(name = "valor_movimentacao")
	private Integer valorMovimentacao;

	/** Indica se esta movimenta��o � ativa. */
	private boolean ativo = true;

	/** Observa��es referentes � movimenta��o do aluno. */
	@Transient
	private String observacao;

	/** Usu�rio respons�vel pela movimenta��o de cancelamento do discente. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_cancelamento")
	private Usuario usuarioCancelamento;

	/** Data de estorno da movimenta��o do discente. */
	@Column(name = "data_estorno")
	private Date dataEstorno;

	/** Indica se esta movimenta��o � do tipo "apostilamento". */
	private Boolean apostilamento;

	/** Data de in�cio do afastamento do discente, usada no trancamento de programa para alunos de stricto. */
	@Temporal(TemporalType.DATE)
	@Column(name = "inicio_afastamento")
	private Date inicioAfastamento;
	
	/** Data de retorno do discente, usado no trancamento de programa para alunos de stricto. */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_retorno")
	private Date dataRetorno;

	/** Data de cola��o de grau do discente. */
	@Transient
	private Date dataColacaoGrau;

	/**
	 * Cole��o de matr�culas em componentes curriculares que foram canceladas
	 * (ou trancadas) quando da movimenta��o do discente.
	 */
	@Transient
	private Collection<MatriculaComponente> matriculasAlteradas;

	/** Indica o status que o aluno deve possuir ap�s o retorno.  
	 * @see {@link Discente#status} */
	@Transient
	private int statusRetorno;

	/** C�digo utilizado durante a migra��o dos dados da p�s-gradua��o do antigo PontoA para o SIGAA. */
	@Column(name="codmergpapos")
	private String codmergpapos;
	
	/** C�digo utilizado durante a migra��o dos dados da gradua��o do antigo PontoA para o SIGAA. */
	@Column(name="codmergpa")
	private String codmergpa;

	/** Indica se o limite m�ximo de trancamentos foi ignorado durante o cadastro da movimenta��o. */
	@Column(name = "limite_trancamento")
	private Boolean limiteTrancamentos = true;

	/** Indica o tipo de retorno do discente (ADMINISTRATIVO, JUDICIAL ou CAMARA). */
	@Column(name = "tipo_retorno")
	private Integer tipoRetorno;
	
	/** Ano acad�mico em que ocorreu a movimenta��o do discente. */
	@Column(name = "ano_ocorrencia")
	private Integer anoOcorrencia;
	/** Per�odo acad�mico em que ocorreu a movimenta��o do discente. */
	@Column(name = "periodo_ocorrencia")
	private Integer periodoOcorrencia;
	
	/**Indica que o movimento � de um trancamento de programa a posteriori.*/
	@Transient
	private boolean trancamentoProgramaPosteriori;

	/** Construtor parametrizado. 
	 * @param id
	 */
	public MovimentacaoAluno(int id) {
		this.id = id;
	}

	/** Construtor padr�o. */
	public MovimentacaoAluno() {
		discente = new Discente();
		tipoMovimentacaoAluno = new TipoMovimentacaoAluno();

	}

	/** Construtor parametrizado.
	 * @param idAfastamentoAluno
	 * @param tipoMovimentacaoAluno
	 * @param discente
	 */
	public MovimentacaoAluno(int idAfastamentoAluno, TipoMovimentacaoAluno tipoMovimentacaoAluno, Discente discente) {
		this.id = idAfastamentoAluno;
		this.tipoMovimentacaoAluno = tipoMovimentacaoAluno;
		this.discente = discente;
	}

	/** Construtor parametrizado.
	 * @param idAfastamentoAluno
	 * @param tipoMovimentacaoAluno
	 * @param discente
	 * @param apostilamento
	 * @param dataAfastamento
	 * @param dataRetorno
	 */
	public MovimentacaoAluno(int idAfastamentoAluno, TipoMovimentacaoAluno tipoMovimentacaoAluno, Discente discente, boolean apostilamento,
			Date dataAfastamento, Date dataRetorno) {
		this.id = idAfastamentoAluno;
		this.tipoMovimentacaoAluno = tipoMovimentacaoAluno;
		this.discente = discente;
		this.dataOcorrencia = dataAfastamento;
	}

	/** Retorna a Chave prim�ria. 
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return this.id;
	}

	/** Seta a Chave prim�ria.
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int idAfastamentoAluno) {
		this.id = idAfastamentoAluno;
	}

	/** Retorna o tipo de movimenta��o do aluno. 
	 * @return
	 */
	public TipoMovimentacaoAluno getTipoMovimentacaoAluno() {
		return this.tipoMovimentacaoAluno;
	}

	/** Seta o tipo de movimenta��o do aluno.
	 * @param tipoMovimentacaoAluno
	 */
	public void setTipoMovimentacaoAluno(TipoMovimentacaoAluno tipoMovimentacaoAluno) {
		this.tipoMovimentacaoAluno = tipoMovimentacaoAluno;
	}

	/** Retorna o discente referente � esta movimenta��o. 
	 * @return
	 */
	public DiscenteAdapter getDiscente() {
		return this.discente;
	}

	/** Seta o discente referente � esta movimenta��o.
	 * @param discente
	 */
	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}

	/** Retorna a data de ocorr�ncia da movimenta��o. 
	 * @return
	 */
	public Date getDataOcorrencia() {
		return this.dataOcorrencia;
	}

	/** Seta a data de ocorr�ncia da movimenta��o.
	 * @param dataAfastamento
	 */
	public void setDataOcorrencia(Date dataOcorrencia) {
		this.dataOcorrencia = dataOcorrencia;
	}

	/** Indica se este objeto � igual ao passado por par�metro, comparando se possuem a mesma chave prim�ria. 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	/** Retorna o c�digo hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	/** Retorna a data do cadastro do retorno. 
	 * @return
	 */
	public Date getDataCadastroRetorno() {
		return dataCadastroRetorno;
	}

	/** Seta a data do cadastro do retorno.
	 * @param dataCadastroRetorno
	 */
	public void setDataCadastroRetorno(Date dataCadastroRetorno) {
		this.dataCadastroRetorno = dataCadastroRetorno;
	}

	/** Retorna o usu�rio que realizou a movimenta��o, nos casos de afastamento. 
	 * @return
	 */
	public Usuario getUsuarioCadastro() {
		return usuarioCadastro;
	}

	/** Seta o usu�rio que realizou a movimenta��o, nos casos de afastamento.
	 * @param usuarioCadastro
	 */
	public void setUsuarioCadastro(Usuario usuarioCadastro) {
		this.usuarioCadastro = usuarioCadastro;
	}

	/** Retorna o usu�rio que realizou a movimenta��o de retorno. 
	 * @return
	 */
	public Usuario getUsuarioRetorno() {
		return usuarioRetorno;
	}

	/** Seta o usu�rio que realizou a movimenta��o de retorno.
	 * @param usuarioRetorno
	 */
	public void setUsuarioRetorno(Usuario usuarioRetorno) {
		this.usuarioRetorno = usuarioRetorno;
	}

	/** Valida os atributos do objeto.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		return validateRetorno();
	}

	/** Valida os atributos deste objeto para uma movimenta��o do tipo afastamento.
	 * @return
	 * @throws DAOException 
	 */
	public ListaMensagens validateAfastamento() throws DAOException {
		ListaMensagens erros = new ListaMensagens();

		ValidatorUtil.validateRequired(getDiscente(), "Discente", erros);
		ValidatorUtil.validateRequired(getTipoMovimentacaoAluno(), "Tipo", erros);
		ValidatorUtil.validateRequired(getAnoReferencia(), "Ano", erros);
		ValidatorUtil.validateRequired(getPeriodoReferencia(), "Per�odo", erros);
		ValidatorUtil.validateMinValue(getAnoReferencia(), 1900, "Ano", erros);		
		ValidatorUtil.validateRange(getPeriodoReferencia(), 1, ParametrosGestoraAcademicaHelper.getParametros(getDiscente()).getQuantidadePeriodosRegulares(), "Per�odo", erros);		
		if (!isEmpty(tipoMovimentacaoAluno) && isTrancamento() && !isEmpty(discente) && discente.isStricto()) {
			ValidatorUtil.validateRequired(inicioAfastamento, "In�cio de Trancamento", erros);
			ValidatorUtil.validateRequired(valorMovimentacao, "N�mero de meses", erros);
		} else if (!isEmpty(tipoMovimentacaoAluno) && isConclusao() && !getDiscente().isStricto() && !getDiscente().isTecnico() && !getDiscente().isFormacaoComplementar() && !getDiscente().isLato() && !getDiscente().isResidencia()) {
			validateRequired(getDataColacaoGrau(), "Data de Cola��o de Grau", erros);
		}

		return erros;
	}

	/** Valida os atributos deste objeto para uma movimenta��o do tipo retorno.
	 * @return
	 */
	public ListaMensagens validateRetorno() {
		ListaMensagens erros = new ListaMensagens();

		ValidatorUtil.validateRequired(getDiscente(), "Discente", erros);
		ValidatorUtil.validateRequired(getDataOcorrencia(), "Data de Afastamento", erros);
		ValidatorUtil.validateRequired(getTipoMovimentacaoAluno(), "Tipo", erros);
		if (anoReferencia == null || periodoReferencia == null)
			erros.addErro("Ano-Semestre de Afastamento � Obrigat�rio.");
		return erros;
	}

	/** Indica se esta movimenta��o � ativa. 
	 * @return
	 */
	public boolean isAtivo() {
		return ativo;
	}

	/** Seta se esta movimenta��o � ativa. 
	 * @param ativo
	 */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/** Retorna o ano de refer�ncia que o discente est� trancando. 
	 * @return
	 */
	public Integer getAnoReferencia() {
		return anoReferencia;
	}

	/** Seta o ano de refer�ncia que o discente est� trancando.
	 * @param anoReferencia
	 */
	public void setAnoReferencia(Integer anoReferencia) {
		this.anoReferencia = anoReferencia;
	}

	/** Retorna o per�odo de refer�ncia que o discente est� trancando. 
	 * @return
	 */
	public Integer getPeriodoReferencia() {
		return periodoReferencia;
	}

	/** Seta o per�odo de refer�ncia que o discente est� trancando.
	 * @param periodoReferencia
	 */
	public void setPeriodoReferencia(Integer periodoReferencia) {
		this.periodoReferencia = periodoReferencia;
	}

	/** Retorna o valor da movimenta��o, usado no caso de prorroga��es ou outras movimenta��es que trabalhem com valor. No caso de prorroga��o � o n�mero de semestres. No caso de trancamento de programa para stricto � o n�mero de meses.
	 * @return
	 */
	public Integer getValorMovimentacao() {
		return valorMovimentacao;
	}

	/** Seta o valor da movimenta��o, usado no caso de prorroga��es ou outras movimenta��es que trabalhem com valor. No caso de prorroga��o � o n�mero de semestres. No caso de trancamento de programa para stricto � o n�mero de meses.
	 * @param valorMovimentacao
	 */
	public void setValorMovimentacao(Integer valorMovimentacao) {
		this.valorMovimentacao = valorMovimentacao;
	}

	/** Retorna uma descri��o textual do ano-per�odo de refer�ncia.
	 * @return
	 */
	@Transient
	public String getAnoPeriodoReferencia() {
		if (anoReferencia != null && periodoReferencia != null)
			return anoReferencia + "." + periodoReferencia;
		return "";
	}

	/** Indica se esta movimenta��o � do tipo Trancamento.
	 * @see TipoMovimentacaoAluno.TRANCAMENTO
	 * @return
	 */
	@Transient
	public boolean isTrancamento() {
		return tipoMovimentacaoAluno != null
				&& tipoMovimentacaoAluno.getId() == TipoMovimentacaoAluno.TRANCAMENTO;
	}

	/** Indica se esta movimenta��o � do tipo Cancelamento.
	 * @see StatusDiscente#CANCELADO
	 * @return
	 */
	@Transient
	public boolean isCancelamento() {
		return tipoMovimentacaoAluno.getStatusDiscente() != null
		&& tipoMovimentacaoAluno.getStatusDiscente() == StatusDiscente.CANCELADO;
	}

	/** Indica se esta movimenta��o � do tipo cancelamento por abandono.
	 * @see TipoMovimentacaoAluno#ABANDONO
	 * @return
	 */
	@Transient
	public boolean isAbandono() {
		return tipoMovimentacaoAluno != null
		&& tipoMovimentacaoAluno.getId() == TipoMovimentacaoAluno.ABANDONO;
	}
	

	/** Indica se esta movimenta��o � do tipo Cancelamento por progress�o de n�vel.
	 * @return
	 */
	@Transient
	public boolean isCancelamentoPorUpgradeNivel() {
		return tipoMovimentacaoAluno != null
				&& tipoMovimentacaoAluno.getId() == TipoMovimentacaoAluno.CANCELAMENTO_POR_UPGRADE_NIVEL;
	}

	/** Indica se esta movimenta��o � do tipo Cancelamento por conclus�o de curso.
	 * @see StatusDiscente#CONCLUIDO
	 * @return
	 */
	@Transient
	public boolean isConclusao() {
		return tipoMovimentacaoAluno.getId() == TipoMovimentacaoAluno.CONCLUSAO
				|| (tipoMovimentacaoAluno.getStatusDiscente() != null && tipoMovimentacaoAluno
						.getStatusDiscente() == StatusDiscente.CONCLUIDO);
	}

	/** Retorna uma representa��o textual do tipo de sa�da do discente ("TRANCADO" ou "CONCLU�DO").
	 * @return
	 */
	@Transient
	public String getTipoSaida() {
		if (GrupoMovimentacaoAluno.AFASTAMENTO_PERMANENTE.equals(tipoMovimentacaoAluno.getGrupo())) {
			if (tipoMovimentacaoAluno.getStatusDiscente() == StatusDiscente.CONCLUIDO) {
				return "CONCLUIDO";
			} else {
				return "TRANCADO";
			}
		}
		return "";
	}

	/** Retorna as observa��es referentes � movimenta��o do aluno. 
	 * @return
	 */
	public String getObservacao() {
		return observacao;
	}

	/** Seta as observa��es referentes � movimenta��o do aluno.
	 * @param observacao
	 */
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	/** Retorna a data de estorno de uma movimenta��o. 
	 * @return
	 */
	public Date getDataEstorno() {
		return dataEstorno;
	}

	/** Seta a data de estorno de uma movimenta��o.
	 * @param dataEstorno
	 */
	public void setDataEstorno(Date dataEstorno) {
		this.dataEstorno = dataEstorno;
	}

	/** Retorna o usu�rio respons�vel pela movimenta��o de cancelamento do discente. 
	 * @return
	 */
	public Usuario getUsuarioCancelamento() {
		return usuarioCancelamento;
	}

	/** Seta o usu�rio respons�vel pela movimenta��o de cancelamento do discente.
	 * @param usuarioCancelamento
	 */
	public void setUsuarioCancelamento(Usuario usuarioCancelamento) {
		this.usuarioCancelamento = usuarioCancelamento;
	}

	/** Retorna a data de cola��o de grau do discente. 
	 * @return
	 */
	public Date getDataColacaoGrau() {
		return dataColacaoGrau;
	}

	/** Seta a data de cola��o de grau do discente.
	 * @param dataColacaoGrau
	 */
	public void setDataColacaoGrau(Date dataColacaoGrau) {
		this.dataColacaoGrau = dataColacaoGrau;
	}

	/** Indica se esta movimenta��o � do tipo "apostilamento". 
	 * @return
	 */
	public Boolean getApostilamento() {
		return apostilamento;
	}

	/** Seta se esta movimenta��o � do tipo "apostilamento". 
	 * @param apostilamento
	 */
	public void setApostilamento(Boolean apostilamento) {
		this.apostilamento = apostilamento;
	}

	/** Retorna a data de in�cio do afastamento do discente, usada no trancamento de programa para alunos de stricto. 
	 * @return
	 */
	public Date getInicioAfastamento() {
		return inicioAfastamento;
	}

	/** Seta a data de in�cio do afastamento do discente, usada no trancamento de programa para alunos de stricto.
	 * @param inicioAfastamento
	 */
	public void setInicioAfastamento(Date inicioAfastamento) {
		this.inicioAfastamento = inicioAfastamento;
	}

	/** Retorna a cole��o de matr�culas em componentes curriculares que foram canceladas (ou trancadas) quando da movimenta��o do discente.
	 * @return
	 */
	public Collection<MatriculaComponente> getMatriculasAlteradas() {
		return matriculasAlteradas;
	}

	/** Seta a cole��o de matr�culas em componentes curriculares que foram canceladas (ou trancadas) quando da movimenta��o do discente.
	 * @param matriculasAlteradas
	 */
	public void setMatriculasAlteradas(Collection<MatriculaComponente> matriculasAlteradas) {
		this.matriculasAlteradas = matriculasAlteradas;
	}

	/** Retorna uma representa��o textual do ano e m�s da movimenta��o do discente no formato: ano de refer�ncia, seguido por "/", seguido pelo m�s abreviado.
	 * @return
	 */
	@Transient
	public String getAnoMesMovimentacao() {
		return anoReferencia + "" + inicioAfastamento != null ? "/" + CalendarUtils.getMesAbreviado(inicioAfastamento) : "";
	}

	/** Retorna uma representa��o textual do m�s e ano da movimenta��o do discente no formato: m�s abreviado, seguido por "/", seguido pelo ano de refer�ncia.
	 * @return
	 */
	@Transient
	public String getMesAnoMovimentacao() {
		return  inicioAfastamento != null ? CalendarUtils.getMesAbreviado(inicioAfastamento) + "/" : "" + anoReferencia;
	}
	
	/** Retorna uma representa��o textual do m�s e ano de conclus�o do discente no formato: m�s abreviado, seguido por "/", seguido pelo ano.
	 * @return
	 */
	@Transient
	public String getMesAnoConclusao() {
		return  (dataOcorrencia != null ? CalendarUtils.getMesAbreviado(dataOcorrencia) + "/" : "") + CalendarUtils.getAno(dataOcorrencia);
	}

	/** Indica o status que o aluno deve possuir ap�s o retorno. 
	 * @return
	 */
	public int getStatusRetorno() {
		return statusRetorno;
	}

	/** Seta o status que o aluno deve possuir ap�s o retorno. 
	 * @param statusRetorno
	 */
	public void setStatusRetorno(int statusRetorno) {
		this.statusRetorno = statusRetorno;
	}

	/** Retorna a data de retorno do discente, usado no trancamento de programa para alunos de stricto. 
	 * @return
	 */
	public Date getDataRetorno() {
		return dataRetorno;
	}

	/** 
	 * Seta a data de retorno do discente, usada no trancamento de programa para alunos de stricto.
	 */
	public void setDataRetorno(Date dataRetorno) {
		this.dataRetorno = dataRetorno;
	}

	/** Retorna o c�digo utilizado durante a migra��o dos dados da p�s-gradua��o do antigo PontoA para o SIGAA. 
	 * @return
	 */
	public String getCodmergpapos() {
		return codmergpapos;
	}

	/** Seta o c�digo utilizado durante a migra��o dos dados da p�s-gradua��o do antigo PontoA para o SIGAA.
	 * @param codmergpapos
	 */
	public void setCodmergpapos(String codmergpapos) {
		this.codmergpapos = codmergpapos;
	}

	/** Indica se o limite m�ximo de trancamentos foi ignorado durante o cadastro da movimenta��o. 
	 * @return
	 */
	public Boolean getLimiteTrancamentos() {
		return limiteTrancamentos;
	}

	/** Seta se o limite m�ximo de trancamentos deve ser ignorado durante o cadastro da movimenta��o. 
	 * @param limiteTrancamentos
	 */
	public void setLimiteTrancamentos(Boolean limiteTrancamentos) {
		this.limiteTrancamentos = limiteTrancamentos;
	}

	/** Indica o tipo de retorno do discente (ADMINISTRATIVO, JUDICIAL ou CAMARA). 
	 * @return
	 */
	public Integer getTipoRetorno() {
		return tipoRetorno;
	}

	/** Seta o tipo de retorno do discente (ADMINISTRATIVO, JUDICIAL ou CAMARA). 
	 * @param tipoRetorno
	 */
	public void setTipoRetorno(Integer tipoRetorno) {
		this.tipoRetorno = tipoRetorno;
	}

	/** Retorna o c�digo utilizado durante a migra��o dos dados da gradua��o do antigo PontoA para o SIGAA. 
	 * @return
	 */
	public String getCodmergpa() {
		return codmergpa;
	}

	/** Seta o c�digo utilizado durante a migra��o dos dados da gradua��o do antigo PontoA para o SIGAA.
	 * @param codmergpa
	 */
	public void setCodmergpa(String codmergpa) {
		this.codmergpa = codmergpa;
	}

	/** Retorna uma descri��o textual da movimenta��o do ano no formato {@link MovimentacaoAluno#getDescricao() descricao}, seguido por "-", seguido por uma descri��o textual do tipo de movimenta��o.
	 * @return
	 */
	@Transient
	public String getDescricao() {
		return getAnoPeriodoReferencia() + " - " + getTipoMovimentacaoAluno().getDescricao(); 
	}
	
	/** Retorna uma descri��o textual deste objeto. Este m�todo faz uma chamada direta ao m�todo {@link #getDescricao()}.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getDescricao();
	}

	/** Retorna o ano acad�mico em que ocorreu a movimenta��o do discente.
	 * @return
	 */
	public Integer getAnoOcorrencia() {
		return anoOcorrencia;
	}

	/** Seta o ano acad�mico em que ocorreu a movimenta��o do discente.
	 * @param anoOcorrencia
	 */
	public void setAnoOcorrencia(Integer anoOcorrencia) {
		this.anoOcorrencia = anoOcorrencia;
	}

	/** Retorna o per�odo acad�mico em que ocorreu a movimenta��o do discente. 
	 * @return
	 */
	public Integer getPeriodoOcorrencia() {
		return periodoOcorrencia;
	}

	/** Seta o per�odo acad�mico em que ocorreu a movimenta��o do discente.
	 * @param periodoOcorrencia
	 */
	public void setPeriodoOcorrencia(Integer periodoOcorrencia) {
		this.periodoOcorrencia = periodoOcorrencia;
	}
	
	/**
	 * Se o aluno possui uma movimenta��o de afastamento (Permanente ou Temporaria) e n�o possui nenhum tipo de retorno, significa que o discente est� realmente afastado da institui��o.
	 * Quando a movimenta��o possui {@link #tipoRetorno} quer dizer que o discente voltou a institui��o. 
	 * 
	 * @return
	 */
	public boolean isAfastamentoPermanenteSemRetorno() {
		return tipoRetorno == null && tipoMovimentacaoAluno.isPermanente();
	}

	public boolean isTrancamentoProgramaPosteriori() {
		return trancamentoProgramaPosteriori;
	}

	public void setTrancamentoProgramaPosteriori(
			boolean trancamentoProgramaPosteriori) {
		this.trancamentoProgramaPosteriori = trancamentoProgramaPosteriori;
	}
	
	
}
