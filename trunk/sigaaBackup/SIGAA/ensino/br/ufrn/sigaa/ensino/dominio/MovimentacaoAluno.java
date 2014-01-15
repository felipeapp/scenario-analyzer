/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
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
 * Entidade que registra os diversos tipos de movimentação que o aluno pode ter dentro 
 * da instituição, vinculados diretamente a alteração de status do discente.
 * Através das movimentações o status do discente é mudado. (ex: se a movimentação for de conclusão de curso
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
	/** Constante que define o tipo de retorno definido pela câmara (de graduação ou pós-graduação). */
	public static final int CAMARA = 3;

	// atributos
	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(value = "ensino.movimentacao_aluno_seq", name = "sequence_name") })
	@Column(name = "id_movimentacao_aluno", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Tipo de movimentação do aluno. */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_movimentacao_aluno", unique = false, nullable = false, insertable = true, updatable = true)
	private TipoMovimentacaoAluno tipoMovimentacaoAluno;

	/** Discente referente à esta movimentação. */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER, targetEntity=Discente.class)
	@JoinColumn(name = "id_discente", unique = false, nullable = false, insertable = true, updatable = true)
	private DiscenteAdapter discente;

	/** Data de ocorrência da movimentação. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_ocorrencia", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	private Date dataOcorrencia;

	/** Usuário que realizou a movimentação, nos casos de afastamento. */
	@CriadoPor
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_cadastro", unique = false, nullable = true, insertable = true, updatable = true)
	private Usuario usuarioCadastro;

	/** Usuário que realizou a movimentação de retorno. */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_retorno", unique = false, nullable = true, insertable = true, updatable = true)
	private Usuario usuarioRetorno;

	/** Data do cadastro do retorno. */
	@Column(name = "data_cadastro_retorno", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	private Date dataCadastroRetorno;

	/** Ano de referência que o discente está trancando. */
	@Column(name = "ano_referencia")
	private Integer anoReferencia;

	/** Período de referência que o discente está trancando. */
	@Column(name = "periodo_referencia")
	private Integer periodoReferencia;

	/**
	 * Valor da movimentação, usado no caso de prorrogações ou outras
	 * movimentações que trabalhem com valor. No caso de prorrogação é o número
	 * de semestres. No caso de trancamento de programa para stricto é o número
	 * de meses.
	 */
	@Column(name = "valor_movimentacao")
	private Integer valorMovimentacao;

	/** Indica se esta movimentação é ativa. */
	private boolean ativo = true;

	/** Observações referentes à movimentação do aluno. */
	@Transient
	private String observacao;

	/** Usuário responsável pela movimentação de cancelamento do discente. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_cancelamento")
	private Usuario usuarioCancelamento;

	/** Data de estorno da movimentação do discente. */
	@Column(name = "data_estorno")
	private Date dataEstorno;

	/** Indica se esta movimentação é do tipo "apostilamento". */
	private Boolean apostilamento;

	/** Data de início do afastamento do discente, usada no trancamento de programa para alunos de stricto. */
	@Temporal(TemporalType.DATE)
	@Column(name = "inicio_afastamento")
	private Date inicioAfastamento;
	
	/** Data de retorno do discente, usado no trancamento de programa para alunos de stricto. */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_retorno")
	private Date dataRetorno;

	/** Data de colação de grau do discente. */
	@Transient
	private Date dataColacaoGrau;

	/**
	 * Coleção de matrículas em componentes curriculares que foram canceladas
	 * (ou trancadas) quando da movimentação do discente.
	 */
	@Transient
	private Collection<MatriculaComponente> matriculasAlteradas;

	/** Indica o status que o aluno deve possuir após o retorno.  
	 * @see {@link Discente#status} */
	@Transient
	private int statusRetorno;

	/** Código utilizado durante a migração dos dados da pós-graduação do antigo PontoA para o SIGAA. */
	@Column(name="codmergpapos")
	private String codmergpapos;
	
	/** Código utilizado durante a migração dos dados da graduação do antigo PontoA para o SIGAA. */
	@Column(name="codmergpa")
	private String codmergpa;

	/** Indica se o limite máximo de trancamentos foi ignorado durante o cadastro da movimentação. */
	@Column(name = "limite_trancamento")
	private Boolean limiteTrancamentos = true;

	/** Indica o tipo de retorno do discente (ADMINISTRATIVO, JUDICIAL ou CAMARA). */
	@Column(name = "tipo_retorno")
	private Integer tipoRetorno;
	
	/** Ano acadêmico em que ocorreu a movimentação do discente. */
	@Column(name = "ano_ocorrencia")
	private Integer anoOcorrencia;
	/** Período acadêmico em que ocorreu a movimentação do discente. */
	@Column(name = "periodo_ocorrencia")
	private Integer periodoOcorrencia;
	
	/**Indica que o movimento é de um trancamento de programa a posteriori.*/
	@Transient
	private boolean trancamentoProgramaPosteriori;

	/** Construtor parametrizado. 
	 * @param id
	 */
	public MovimentacaoAluno(int id) {
		this.id = id;
	}

	/** Construtor padrão. */
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

	/** Retorna a Chave primária. 
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return this.id;
	}

	/** Seta a Chave primária.
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int idAfastamentoAluno) {
		this.id = idAfastamentoAluno;
	}

	/** Retorna o tipo de movimentação do aluno. 
	 * @return
	 */
	public TipoMovimentacaoAluno getTipoMovimentacaoAluno() {
		return this.tipoMovimentacaoAluno;
	}

	/** Seta o tipo de movimentação do aluno.
	 * @param tipoMovimentacaoAluno
	 */
	public void setTipoMovimentacaoAluno(TipoMovimentacaoAluno tipoMovimentacaoAluno) {
		this.tipoMovimentacaoAluno = tipoMovimentacaoAluno;
	}

	/** Retorna o discente referente à esta movimentação. 
	 * @return
	 */
	public DiscenteAdapter getDiscente() {
		return this.discente;
	}

	/** Seta o discente referente à esta movimentação.
	 * @param discente
	 */
	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}

	/** Retorna a data de ocorrência da movimentação. 
	 * @return
	 */
	public Date getDataOcorrencia() {
		return this.dataOcorrencia;
	}

	/** Seta a data de ocorrência da movimentação.
	 * @param dataAfastamento
	 */
	public void setDataOcorrencia(Date dataOcorrencia) {
		this.dataOcorrencia = dataOcorrencia;
	}

	/** Indica se este objeto é igual ao passado por parâmetro, comparando se possuem a mesma chave primária. 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	/** Retorna o código hash deste objeto.
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

	/** Retorna o usuário que realizou a movimentação, nos casos de afastamento. 
	 * @return
	 */
	public Usuario getUsuarioCadastro() {
		return usuarioCadastro;
	}

	/** Seta o usuário que realizou a movimentação, nos casos de afastamento.
	 * @param usuarioCadastro
	 */
	public void setUsuarioCadastro(Usuario usuarioCadastro) {
		this.usuarioCadastro = usuarioCadastro;
	}

	/** Retorna o usuário que realizou a movimentação de retorno. 
	 * @return
	 */
	public Usuario getUsuarioRetorno() {
		return usuarioRetorno;
	}

	/** Seta o usuário que realizou a movimentação de retorno.
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

	/** Valida os atributos deste objeto para uma movimentação do tipo afastamento.
	 * @return
	 * @throws DAOException 
	 */
	public ListaMensagens validateAfastamento() throws DAOException {
		ListaMensagens erros = new ListaMensagens();

		ValidatorUtil.validateRequired(getDiscente(), "Discente", erros);
		ValidatorUtil.validateRequired(getTipoMovimentacaoAluno(), "Tipo", erros);
		ValidatorUtil.validateRequired(getAnoReferencia(), "Ano", erros);
		ValidatorUtil.validateRequired(getPeriodoReferencia(), "Período", erros);
		ValidatorUtil.validateMinValue(getAnoReferencia(), 1900, "Ano", erros);		
		ValidatorUtil.validateRange(getPeriodoReferencia(), 1, ParametrosGestoraAcademicaHelper.getParametros(getDiscente()).getQuantidadePeriodosRegulares(), "Período", erros);		
		if (!isEmpty(tipoMovimentacaoAluno) && isTrancamento() && !isEmpty(discente) && discente.isStricto()) {
			ValidatorUtil.validateRequired(inicioAfastamento, "Início de Trancamento", erros);
			ValidatorUtil.validateRequired(valorMovimentacao, "Número de meses", erros);
		} else if (!isEmpty(tipoMovimentacaoAluno) && isConclusao() && !getDiscente().isStricto() && !getDiscente().isTecnico() && !getDiscente().isFormacaoComplementar() && !getDiscente().isLato() && !getDiscente().isResidencia()) {
			validateRequired(getDataColacaoGrau(), "Data de Colação de Grau", erros);
		}

		return erros;
	}

	/** Valida os atributos deste objeto para uma movimentação do tipo retorno.
	 * @return
	 */
	public ListaMensagens validateRetorno() {
		ListaMensagens erros = new ListaMensagens();

		ValidatorUtil.validateRequired(getDiscente(), "Discente", erros);
		ValidatorUtil.validateRequired(getDataOcorrencia(), "Data de Afastamento", erros);
		ValidatorUtil.validateRequired(getTipoMovimentacaoAluno(), "Tipo", erros);
		if (anoReferencia == null || periodoReferencia == null)
			erros.addErro("Ano-Semestre de Afastamento é Obrigatório.");
		return erros;
	}

	/** Indica se esta movimentação é ativa. 
	 * @return
	 */
	public boolean isAtivo() {
		return ativo;
	}

	/** Seta se esta movimentação é ativa. 
	 * @param ativo
	 */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/** Retorna o ano de referência que o discente está trancando. 
	 * @return
	 */
	public Integer getAnoReferencia() {
		return anoReferencia;
	}

	/** Seta o ano de referência que o discente está trancando.
	 * @param anoReferencia
	 */
	public void setAnoReferencia(Integer anoReferencia) {
		this.anoReferencia = anoReferencia;
	}

	/** Retorna o período de referência que o discente está trancando. 
	 * @return
	 */
	public Integer getPeriodoReferencia() {
		return periodoReferencia;
	}

	/** Seta o período de referência que o discente está trancando.
	 * @param periodoReferencia
	 */
	public void setPeriodoReferencia(Integer periodoReferencia) {
		this.periodoReferencia = periodoReferencia;
	}

	/** Retorna o valor da movimentação, usado no caso de prorrogações ou outras movimentações que trabalhem com valor. No caso de prorrogação é o número de semestres. No caso de trancamento de programa para stricto é o número de meses.
	 * @return
	 */
	public Integer getValorMovimentacao() {
		return valorMovimentacao;
	}

	/** Seta o valor da movimentação, usado no caso de prorrogações ou outras movimentações que trabalhem com valor. No caso de prorrogação é o número de semestres. No caso de trancamento de programa para stricto é o número de meses.
	 * @param valorMovimentacao
	 */
	public void setValorMovimentacao(Integer valorMovimentacao) {
		this.valorMovimentacao = valorMovimentacao;
	}

	/** Retorna uma descrição textual do ano-período de referência.
	 * @return
	 */
	@Transient
	public String getAnoPeriodoReferencia() {
		if (anoReferencia != null && periodoReferencia != null)
			return anoReferencia + "." + periodoReferencia;
		return "";
	}

	/** Indica se esta movimentação é do tipo Trancamento.
	 * @see TipoMovimentacaoAluno.TRANCAMENTO
	 * @return
	 */
	@Transient
	public boolean isTrancamento() {
		return tipoMovimentacaoAluno != null
				&& tipoMovimentacaoAluno.getId() == TipoMovimentacaoAluno.TRANCAMENTO;
	}

	/** Indica se esta movimentação é do tipo Cancelamento.
	 * @see StatusDiscente#CANCELADO
	 * @return
	 */
	@Transient
	public boolean isCancelamento() {
		return tipoMovimentacaoAluno.getStatusDiscente() != null
		&& tipoMovimentacaoAluno.getStatusDiscente() == StatusDiscente.CANCELADO;
	}

	/** Indica se esta movimentação é do tipo cancelamento por abandono.
	 * @see TipoMovimentacaoAluno#ABANDONO
	 * @return
	 */
	@Transient
	public boolean isAbandono() {
		return tipoMovimentacaoAluno != null
		&& tipoMovimentacaoAluno.getId() == TipoMovimentacaoAluno.ABANDONO;
	}
	

	/** Indica se esta movimentação é do tipo Cancelamento por progressão de nível.
	 * @return
	 */
	@Transient
	public boolean isCancelamentoPorUpgradeNivel() {
		return tipoMovimentacaoAluno != null
				&& tipoMovimentacaoAluno.getId() == TipoMovimentacaoAluno.CANCELAMENTO_POR_UPGRADE_NIVEL;
	}

	/** Indica se esta movimentação é do tipo Cancelamento por conclusão de curso.
	 * @see StatusDiscente#CONCLUIDO
	 * @return
	 */
	@Transient
	public boolean isConclusao() {
		return tipoMovimentacaoAluno.getId() == TipoMovimentacaoAluno.CONCLUSAO
				|| (tipoMovimentacaoAluno.getStatusDiscente() != null && tipoMovimentacaoAluno
						.getStatusDiscente() == StatusDiscente.CONCLUIDO);
	}

	/** Retorna uma representação textual do tipo de saída do discente ("TRANCADO" ou "CONCLUÍDO").
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

	/** Retorna as observações referentes à movimentação do aluno. 
	 * @return
	 */
	public String getObservacao() {
		return observacao;
	}

	/** Seta as observações referentes à movimentação do aluno.
	 * @param observacao
	 */
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	/** Retorna a data de estorno de uma movimentação. 
	 * @return
	 */
	public Date getDataEstorno() {
		return dataEstorno;
	}

	/** Seta a data de estorno de uma movimentação.
	 * @param dataEstorno
	 */
	public void setDataEstorno(Date dataEstorno) {
		this.dataEstorno = dataEstorno;
	}

	/** Retorna o usuário responsável pela movimentação de cancelamento do discente. 
	 * @return
	 */
	public Usuario getUsuarioCancelamento() {
		return usuarioCancelamento;
	}

	/** Seta o usuário responsável pela movimentação de cancelamento do discente.
	 * @param usuarioCancelamento
	 */
	public void setUsuarioCancelamento(Usuario usuarioCancelamento) {
		this.usuarioCancelamento = usuarioCancelamento;
	}

	/** Retorna a data de colação de grau do discente. 
	 * @return
	 */
	public Date getDataColacaoGrau() {
		return dataColacaoGrau;
	}

	/** Seta a data de colação de grau do discente.
	 * @param dataColacaoGrau
	 */
	public void setDataColacaoGrau(Date dataColacaoGrau) {
		this.dataColacaoGrau = dataColacaoGrau;
	}

	/** Indica se esta movimentação é do tipo "apostilamento". 
	 * @return
	 */
	public Boolean getApostilamento() {
		return apostilamento;
	}

	/** Seta se esta movimentação é do tipo "apostilamento". 
	 * @param apostilamento
	 */
	public void setApostilamento(Boolean apostilamento) {
		this.apostilamento = apostilamento;
	}

	/** Retorna a data de início do afastamento do discente, usada no trancamento de programa para alunos de stricto. 
	 * @return
	 */
	public Date getInicioAfastamento() {
		return inicioAfastamento;
	}

	/** Seta a data de início do afastamento do discente, usada no trancamento de programa para alunos de stricto.
	 * @param inicioAfastamento
	 */
	public void setInicioAfastamento(Date inicioAfastamento) {
		this.inicioAfastamento = inicioAfastamento;
	}

	/** Retorna a coleção de matrículas em componentes curriculares que foram canceladas (ou trancadas) quando da movimentação do discente.
	 * @return
	 */
	public Collection<MatriculaComponente> getMatriculasAlteradas() {
		return matriculasAlteradas;
	}

	/** Seta a coleção de matrículas em componentes curriculares que foram canceladas (ou trancadas) quando da movimentação do discente.
	 * @param matriculasAlteradas
	 */
	public void setMatriculasAlteradas(Collection<MatriculaComponente> matriculasAlteradas) {
		this.matriculasAlteradas = matriculasAlteradas;
	}

	/** Retorna uma representação textual do ano e mês da movimentação do discente no formato: ano de referência, seguido por "/", seguido pelo mês abreviado.
	 * @return
	 */
	@Transient
	public String getAnoMesMovimentacao() {
		return anoReferencia + "" + inicioAfastamento != null ? "/" + CalendarUtils.getMesAbreviado(inicioAfastamento) : "";
	}

	/** Retorna uma representação textual do mês e ano da movimentação do discente no formato: mês abreviado, seguido por "/", seguido pelo ano de referência.
	 * @return
	 */
	@Transient
	public String getMesAnoMovimentacao() {
		return  inicioAfastamento != null ? CalendarUtils.getMesAbreviado(inicioAfastamento) + "/" : "" + anoReferencia;
	}
	
	/** Retorna uma representação textual do mês e ano de conclusão do discente no formato: mês abreviado, seguido por "/", seguido pelo ano.
	 * @return
	 */
	@Transient
	public String getMesAnoConclusao() {
		return  (dataOcorrencia != null ? CalendarUtils.getMesAbreviado(dataOcorrencia) + "/" : "") + CalendarUtils.getAno(dataOcorrencia);
	}

	/** Indica o status que o aluno deve possuir após o retorno. 
	 * @return
	 */
	public int getStatusRetorno() {
		return statusRetorno;
	}

	/** Seta o status que o aluno deve possuir após o retorno. 
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

	/** Retorna o código utilizado durante a migração dos dados da pós-graduação do antigo PontoA para o SIGAA. 
	 * @return
	 */
	public String getCodmergpapos() {
		return codmergpapos;
	}

	/** Seta o código utilizado durante a migração dos dados da pós-graduação do antigo PontoA para o SIGAA.
	 * @param codmergpapos
	 */
	public void setCodmergpapos(String codmergpapos) {
		this.codmergpapos = codmergpapos;
	}

	/** Indica se o limite máximo de trancamentos foi ignorado durante o cadastro da movimentação. 
	 * @return
	 */
	public Boolean getLimiteTrancamentos() {
		return limiteTrancamentos;
	}

	/** Seta se o limite máximo de trancamentos deve ser ignorado durante o cadastro da movimentação. 
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

	/** Retorna o código utilizado durante a migração dos dados da graduação do antigo PontoA para o SIGAA. 
	 * @return
	 */
	public String getCodmergpa() {
		return codmergpa;
	}

	/** Seta o código utilizado durante a migração dos dados da graduação do antigo PontoA para o SIGAA.
	 * @param codmergpa
	 */
	public void setCodmergpa(String codmergpa) {
		this.codmergpa = codmergpa;
	}

	/** Retorna uma descrição textual da movimentação do ano no formato {@link MovimentacaoAluno#getDescricao() descricao}, seguido por "-", seguido por uma descrição textual do tipo de movimentação.
	 * @return
	 */
	@Transient
	public String getDescricao() {
		return getAnoPeriodoReferencia() + " - " + getTipoMovimentacaoAluno().getDescricao(); 
	}
	
	/** Retorna uma descrição textual deste objeto. Este método faz uma chamada direta ao método {@link #getDescricao()}.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getDescricao();
	}

	/** Retorna o ano acadêmico em que ocorreu a movimentação do discente.
	 * @return
	 */
	public Integer getAnoOcorrencia() {
		return anoOcorrencia;
	}

	/** Seta o ano acadêmico em que ocorreu a movimentação do discente.
	 * @param anoOcorrencia
	 */
	public void setAnoOcorrencia(Integer anoOcorrencia) {
		this.anoOcorrencia = anoOcorrencia;
	}

	/** Retorna o período acadêmico em que ocorreu a movimentação do discente. 
	 * @return
	 */
	public Integer getPeriodoOcorrencia() {
		return periodoOcorrencia;
	}

	/** Seta o período acadêmico em que ocorreu a movimentação do discente.
	 * @param periodoOcorrencia
	 */
	public void setPeriodoOcorrencia(Integer periodoOcorrencia) {
		this.periodoOcorrencia = periodoOcorrencia;
	}
	
	/**
	 * Se o aluno possui uma movimentação de afastamento (Permanente ou Temporaria) e não possui nenhum tipo de retorno, significa que o discente está realmente afastado da instituição.
	 * Quando a movimentação possui {@link #tipoRetorno} quer dizer que o discente voltou a instituição. 
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
