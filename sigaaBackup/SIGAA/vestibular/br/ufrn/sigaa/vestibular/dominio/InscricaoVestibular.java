/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 30/07/2008
 *
 */
package br.ufrn.sigaa.vestibular.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;

/** Classe que modela uma inscri��o para o Vestibular
 * 
 * @author �dipo Elder F. de Melo
 * 
 */
@Entity
@Table(name = "inscricao_vestibular", schema = "vestibular")
public class InscricaoVestibular implements PersistDB, Validatable {

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_inscricao_vestibular", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Data da inscri��o. */
	@Column(name = "data_inscricao")
	@CriadoEm
	private Date dataInscricao;
	
	/** N�mero de inscri��o. */
	@SequenceGenerator(name = "SEQ_INSCRICAO", sequenceName = "inscricao_vest_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_INSCRICAO")
	@Column(name="numero_inscricao", nullable = false)
	private int numeroInscricao;
	
	/** L�ngua estrangeira da prova de l�ngua estrangeira. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_lingua_estrangeira")
	private LinguaEstrangeira linguaEstrangeira;
	
	/** Anota��es acerca da inscri��o. */
	private String observacao;

	/** Matriz curricular para o qual o candidato est� se inscrevendo. */
	@OneToMany(fetch=FetchType.LAZY)
	@JoinTable(name = "opcao_candidato", schema = "vestibular", joinColumns = @JoinColumn(name = "id_inscricao_vestibular"), inverseJoinColumns = @JoinColumn(name = "id_matriz_curricular"))
	@IndexColumn(name = "ordem")
	private MatrizCurricular[] opcoesCurso;

	/** Dados pessoais do candidato. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_pessoa")
	private PessoaVestibular pessoa;

	/** Processo Seletivo para o qual o candidato est� se inscrevendo. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_processo_seletivo")
	private ProcessoSeletivoVestibular processoSeletivo;

	/** Regi�o preferencial de prova. Essa regi�o n�o corresponde �s zonas urbanas da cidade. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_regiao_preferencial_prova")
	private RegiaoPreferencialProva regiaoPreferencialProva;

	/** Indica se esta inscri��o foi validada. */
	private boolean validada;

	/** Valor pago na inscri��o. */
	@Column(name = "valor_inscricao")
	private Double valorInscricao;
	
	/** Registro de entrada do usu�rio que validou a inscri��o. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_validacao", unique = false, nullable = true, insertable = true, updatable = true)
	@AtualizadoPor
	private RegistroEntrada validadoPor;
	
	/** Data de valida��o da inscri��o. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_validacao", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	@AtualizadoEm
	private Date validadoEm;
	
	/** Local de Aplica��o de Prova do candidato. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_local_aplicacao_prova")
	private LocalAplicacaoProva localProva;
	
	/** Turma (sala) em que o candidato far� prova. A id�ia de turma agrupar candidatos por �rea e l�ngua estrangeira. Uma sala poder� ter uma ou mais turmas. */
	private Integer turma;

	/** GRU gerada para o pagamento da taxa desta inscri��o. */
	@Column(name = "id_gru")
	private Integer idGRU;
	
	/** Indica se o candidato optou pelo benef�cio concedido na pol�tica de inclus�o ou n�o. */
	@Column(name = "optou_beneficio_inclusao")
	private Boolean optouBeneficioInclusao;
	
	/** Indica que a GRU foi quitada. */
	@Column(name="gru_quitada")
	private Boolean gruQuitada;
	
	/** Indica que o candidato deseja concorrer � cotas para egressos de escola p�blica. */ 
	@Column(name="egresso_escola_publica")
	private Boolean egressoEscolaPublica;
	/** Indica que o candidato deseja concorrer � cotas para candidatos com baixa renda familiar per capita. */
	@Column(name="baixa_renda_familiar")
	private Boolean baixaRendaFamiliar;
	/** Indica que o candidato deseja concorrer � cotas para grupos �tnicos. */
	@Column(name="pertence_grupo_etnico")
	private Boolean pertenceGrupoEtnico;
	
	/** Especifica o P�lo que o candidato escolheu para se inscrever. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_polo", nullable = true)
	private Polo polo;
	
	/** Indica que a inscri��o foi importada/migrada de concursos externos. */
	private boolean migrada;
	
	/** Construtor m�nimo. */
	public InscricaoVestibular(int id) {
		this();
		this.id = id;
	}
	
	/** Construtor padr�o. */
	public InscricaoVestibular() {
		this.processoSeletivo = new ProcessoSeletivoVestibular();
		this.pessoa = new PessoaVestibular();
		this.migrada = false;
	}

	/** Retorna a data da inscri��o.  
	 * @return Data da inscri��o. 
	 */
	public Date getDataInscricao() {
		return dataInscricao;
	}

	/** Retorna a chave prim�ria.
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Retorna o n�mero de inscri��o. 
	 * @return N�mero de inscri��o. 
	 */
	public int getNumeroInscricao() {
		return numeroInscricao;
	}

	/** Retorna a l�ngua estrangeira da prova de l�ngua estrangeira. 
	 * @return L�ngua estrangeira da prova de l�ngua estrangeira. 
	 */
	public LinguaEstrangeira getLinguaEstrangeira() {
		return linguaEstrangeira;
	}

	/** Retorna anota��es acerca da inscri��o. 
	 * @return Anota��es acerca da inscri��o. 
	 */
	public String getObservacao() {
		return observacao;
	}

	/** Retorna a matriz curricular para o qual o candidato est� se inscrevendo. 
	 * @return Matriz curricular para o qual o candidato est� se inscrevendo. 
	 */
	public MatrizCurricular[] getOpcoesCurso() {
		return opcoesCurso;
	}

	/** Retorna os dados pessoais do candidato. 
	 * @return Dados pessoais do candidato. 
	 */
	public PessoaVestibular getPessoa() {
		return pessoa;
	}

	/** Retorna o processo Seletivo para o qual o candidato est� se inscrevendo. 
	 * @return Processo Seletivo para o qual o candidato est� se inscrevendo. 
	 */
	public ProcessoSeletivoVestibular getProcessoSeletivo() {
		return processoSeletivo;
	}

	/** Retorna a regi�o preferencial de prova. Essa regi�o n�o corresponde �s zonas urbanas da cidade. 
	 * @return Regi�o preferencial de prova. 
	 */
	public RegiaoPreferencialProva getRegiaoPreferencialProva() {
		return regiaoPreferencialProva;
	}

	/** Retorna o valor pago na inscri��o.  
	 * @return Valor pago na inscri��o. 
	 */
	public Double getValorInscricao() {
		return valorInscricao;
	}

	/** Indica se esta inscri��o foi validada. 
	 * @return True, se a inscri��o foi validada. False, caso contr�rio.
	 */
	public boolean isValidada() {
		return validada;
	}

	/** Inicializa os atributos nulos. */
	public void prepararDados() {
		pessoa = new PessoaVestibular();
		pessoa.prepararDados();
		pessoa.setUfConclusaoEnsinoMedio(new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO));
		pessoa.getIdentidade().setDataExpedicao(null);
		opcoesCurso = new MatrizCurricular[2];
		for (int i = 0; i < opcoesCurso.length; i++) {
			opcoesCurso[i] = new MatrizCurricular();
			opcoesCurso[i].setCurso(new Curso(0));
		}
		regiaoPreferencialProva = new RegiaoPreferencialProva();
		linguaEstrangeira = new LinguaEstrangeira();
	}

	/** Seta a data da inscri��o. 
	 * @param dataInscricao Data da inscri��o. 
	 */
	public void setDataInscricao(Date dataInscricao) {
		this.dataInscricao = dataInscricao;
	}

	/** Seta a chave prim�ria.
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Seta o n�mero de inscri��o.  
	 * @param inscricao N�mero de inscri��o. 
	 */
	public void setNumeroInscricao(int numeroInscricao) {
		this.numeroInscricao = numeroInscricao;
	}

	/** Seta a l�ngua estrangeira da prova de l�ngua estrangeira. 
	 * @param linguaEstrangeira L�ngua estrangeira da prova de l�ngua estrangeira. 
	 */
	public void setLinguaEstrangeira(LinguaEstrangeira linguaEstrangeira) {
		this.linguaEstrangeira = linguaEstrangeira;
	}

	/** Seta anota��es acerca da inscri��o. 
	 * @param observacao Anota��es acerca da inscri��o. 
	 */
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	/** Seta a matriz curricular para o qual o candidato est� se inscrevendo. 
	 * @param opcoesCurso Matriz curricular para o qual o candidato est� se inscrevendo. 
	 */
	public void setOpcoesCurso(MatrizCurricular[] opcoesCurso) {
		this.opcoesCurso = opcoesCurso;
	}

	/** Seta os dados pessoais do candidato. 
	 * @param pessoa Dados pessoais do candidato. 
	 */
	public void setPessoa(PessoaVestibular pessoa) {
		this.pessoa = pessoa;
	}

	/** Seta o processo Seletivo para o qual o candidato est� se inscrevendo. 
	 * @param processoSeletivo Processo Seletivo para o qual o candidato est� se inscrevendo. 
	 */
	public void setProcessoSeletivo(ProcessoSeletivoVestibular processoSeletivo) {
		this.processoSeletivo = processoSeletivo;
	}

	/** Seta a regi�o preferencial de prova. Essa regi�o n�o corresponde �s zonas urbanas da cidade. 
	 * @param regiaoPreferencialProva Regi�o preferencial de prova. 
	 */
	public void setRegiaoPreferencialProva(
			RegiaoPreferencialProva regiaoPreferencialProva) {
		this.regiaoPreferencialProva = regiaoPreferencialProva;
	}

	/** Seta se esta inscri��o foi validada. 
	 * @param validada True, se a inscri��o estiver validada. False, caso contr�rio.
	 */
	public void setValidada(boolean validada) {
		this.validada = validada;
	}

	/** Seta o valor pago na inscri��o. 
	 * @param valorInscricao Valor pago na inscri��o. 
	 */
	public void setValorInscricao(Double valorInscricao) {
		this.valorInscricao = valorInscricao;
	}

	/** Retorna uma representa��o textual de uma inscri��o para o vestibular no formato:
	 * n�mero de inscri��o, seguido de v�rgula, seguido do nome do candidato,
	 * seguido de v�rgula, seguido da matriz curricular da primeira op��o. 
	 */
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder(getNumeroInscricao());
		if (pessoa.getNome() != null)
			str.append(", ").append(getPessoa().getNome());
		if (!isEmpty(getOpcoesCurso()) && !isEmpty(getOpcoesCurso()[0]))
			str.append(", ").append(getOpcoesCurso()[0].getDescricao());
		return str.toString();
	}

	/** Valida os dados obrigat�rios para a inscri��o de um candidato.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		validateRequired(opcoesCurso[0], "Primeira Op��o", lista);
		validateRequired(getLinguaEstrangeira(), "L�ngua Estrangeira", lista);
		validateRequired(getRegiaoPreferencialProva(), "Regi�o Preferencial de Prova", lista);
		if( isNotEmpty( getProcessoSeletivo() ) && getProcessoSeletivo().isOpcaoBeneficioInclusao() )
			validateRequired(optouBeneficioInclusao, "Benef�cio de Inclus�o", lista);
		return lista;
	}

	/** Retorna o registro de entrada do usu�rio que validou a inscri��o.
	 * @return
	 */
	public RegistroEntrada getValidadoPor() {
		return validadoPor;
	}

	/** Retorna o registro de entrada do usu�rio que validou a inscri��o. 
	 * @param validadoPor
	 */
	public void setValidadoPor(RegistroEntrada validadoPor) {
		this.validadoPor = validadoPor;
	}

	/** Retorna a Data de valida��o da inscri��o. 
	 * @return
	 */
	public Date getValidadoEm() {
		return validadoEm;
	}

	/** Seta a Data de valida��o da inscri��o.
	 * @param validadoEm
	 */
	public void setValidadoEm(Date validadoEm) {
		this.validadoEm = validadoEm;
	}

	/** Retorna o Local de Aplica��o de Prova do candidato. 
	 * @return
	 */
	public LocalAplicacaoProva getLocalProva() {
		return localProva;
	}

	/** Seta o Local de Aplica��o de Prova do candidato. 
	 * @param localProva
	 */
	public void setLocalProva(LocalAplicacaoProva localProva) {
		this.localProva = localProva;
	}

	/**
	 * Retorna a Turma (sala) em que o candidato far� prova. A id�ia de turma
	 * agrupar candidatos por �rea e l�ngua estrangeira. Uma sala poder� ter uma
	 * ou mais turmas.
	 * 
	 * @return
	 */
	public Integer getTurma() {
		return turma;
	}

	/**
	 * Seta a Turma (sala) em que o candidato far� prova. A id�ia de turma
	 * agrupar candidatos por �rea e l�ngua estrangeira. Uma sala poder� ter uma
	 * ou mais turmas.
	 * 
	 * @param turma
	 */
	public void setTurma(Integer turma) {
		this.turma = turma;
	}

	public Integer getIdGRU() {
		return idGRU;
	}

	public void setIdGRU(Integer idGRU) {
		this.idGRU = idGRU;
	}

	public Boolean getOptouBeneficioInclusao() {
		return optouBeneficioInclusao;
	}

	public void setOptouBeneficioInclusao(Boolean optouBeneficioInclusao) {
		this.optouBeneficioInclusao = optouBeneficioInclusao;
	}

	public Boolean getGruQuitada() {
		return gruQuitada;
	}

	public void setGruQuitada(Boolean gruQuitada) {
		this.gruQuitada = gruQuitada;
	}

	public Polo getPolo() {
		return polo;
	}

	public void setPolo(Polo polo) {
		this.polo = polo;
	}

	public boolean isMigrada() {
		return migrada;
	}

	public void setMigrada(boolean migrada) {
		this.migrada = migrada;
	}

	public Boolean getEgressoEscolaPublica() {
		return egressoEscolaPublica;
	}

	public void setEgressoEscolaPublica(Boolean egressoEscolaPublica) {
		this.egressoEscolaPublica = egressoEscolaPublica;
	}

	public Boolean getBaixaRendaFamiliar() {
		return baixaRendaFamiliar;
	}

	public void setBaixaRendaFamiliar(Boolean baixaRendaFamiliar) {
		this.baixaRendaFamiliar = baixaRendaFamiliar;
	}

	public Boolean getPertenceGrupoEtnico() {
		return pertenceGrupoEtnico;
	}

	public void setPertenceGrupoEtnico(Boolean pertenceGrupoEtnico) {
		this.pertenceGrupoEtnico = pertenceGrupoEtnico;
	}

}
