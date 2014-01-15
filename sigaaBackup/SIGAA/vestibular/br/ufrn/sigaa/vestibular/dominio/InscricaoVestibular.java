/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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

/** Classe que modela uma inscrição para o Vestibular
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
@Entity
@Table(name = "inscricao_vestibular", schema = "vestibular")
public class InscricaoVestibular implements PersistDB, Validatable {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_inscricao_vestibular", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Data da inscrição. */
	@Column(name = "data_inscricao")
	@CriadoEm
	private Date dataInscricao;
	
	/** Número de inscrição. */
	@SequenceGenerator(name = "SEQ_INSCRICAO", sequenceName = "inscricao_vest_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_INSCRICAO")
	@Column(name="numero_inscricao", nullable = false)
	private int numeroInscricao;
	
	/** Língua estrangeira da prova de língua estrangeira. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_lingua_estrangeira")
	private LinguaEstrangeira linguaEstrangeira;
	
	/** Anotações acerca da inscrição. */
	private String observacao;

	/** Matriz curricular para o qual o candidato está se inscrevendo. */
	@OneToMany(fetch=FetchType.LAZY)
	@JoinTable(name = "opcao_candidato", schema = "vestibular", joinColumns = @JoinColumn(name = "id_inscricao_vestibular"), inverseJoinColumns = @JoinColumn(name = "id_matriz_curricular"))
	@IndexColumn(name = "ordem")
	private MatrizCurricular[] opcoesCurso;

	/** Dados pessoais do candidato. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_pessoa")
	private PessoaVestibular pessoa;

	/** Processo Seletivo para o qual o candidato está se inscrevendo. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_processo_seletivo")
	private ProcessoSeletivoVestibular processoSeletivo;

	/** Região preferencial de prova. Essa região não corresponde às zonas urbanas da cidade. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_regiao_preferencial_prova")
	private RegiaoPreferencialProva regiaoPreferencialProva;

	/** Indica se esta inscrição foi validada. */
	private boolean validada;

	/** Valor pago na inscrição. */
	@Column(name = "valor_inscricao")
	private Double valorInscricao;
	
	/** Registro de entrada do usuário que validou a inscrição. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_validacao", unique = false, nullable = true, insertable = true, updatable = true)
	@AtualizadoPor
	private RegistroEntrada validadoPor;
	
	/** Data de validação da inscrição. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_validacao", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	@AtualizadoEm
	private Date validadoEm;
	
	/** Local de Aplicação de Prova do candidato. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_local_aplicacao_prova")
	private LocalAplicacaoProva localProva;
	
	/** Turma (sala) em que o candidato fará prova. A idéia de turma agrupar candidatos por área e língua estrangeira. Uma sala poderá ter uma ou mais turmas. */
	private Integer turma;

	/** GRU gerada para o pagamento da taxa desta inscrição. */
	@Column(name = "id_gru")
	private Integer idGRU;
	
	/** Indica se o candidato optou pelo benefício concedido na política de inclusão ou não. */
	@Column(name = "optou_beneficio_inclusao")
	private Boolean optouBeneficioInclusao;
	
	/** Indica que a GRU foi quitada. */
	@Column(name="gru_quitada")
	private Boolean gruQuitada;
	
	/** Indica que o candidato deseja concorrer à cotas para egressos de escola pública. */ 
	@Column(name="egresso_escola_publica")
	private Boolean egressoEscolaPublica;
	/** Indica que o candidato deseja concorrer à cotas para candidatos com baixa renda familiar per capita. */
	@Column(name="baixa_renda_familiar")
	private Boolean baixaRendaFamiliar;
	/** Indica que o candidato deseja concorrer à cotas para grupos étnicos. */
	@Column(name="pertence_grupo_etnico")
	private Boolean pertenceGrupoEtnico;
	
	/** Especifica o Pólo que o candidato escolheu para se inscrever. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_polo", nullable = true)
	private Polo polo;
	
	/** Indica que a inscrição foi importada/migrada de concursos externos. */
	private boolean migrada;
	
	/** Construtor mínimo. */
	public InscricaoVestibular(int id) {
		this();
		this.id = id;
	}
	
	/** Construtor padrão. */
	public InscricaoVestibular() {
		this.processoSeletivo = new ProcessoSeletivoVestibular();
		this.pessoa = new PessoaVestibular();
		this.migrada = false;
	}

	/** Retorna a data da inscrição.  
	 * @return Data da inscrição. 
	 */
	public Date getDataInscricao() {
		return dataInscricao;
	}

	/** Retorna a chave primária.
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Retorna o número de inscrição. 
	 * @return Número de inscrição. 
	 */
	public int getNumeroInscricao() {
		return numeroInscricao;
	}

	/** Retorna a língua estrangeira da prova de língua estrangeira. 
	 * @return Língua estrangeira da prova de língua estrangeira. 
	 */
	public LinguaEstrangeira getLinguaEstrangeira() {
		return linguaEstrangeira;
	}

	/** Retorna anotações acerca da inscrição. 
	 * @return Anotações acerca da inscrição. 
	 */
	public String getObservacao() {
		return observacao;
	}

	/** Retorna a matriz curricular para o qual o candidato está se inscrevendo. 
	 * @return Matriz curricular para o qual o candidato está se inscrevendo. 
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

	/** Retorna o processo Seletivo para o qual o candidato está se inscrevendo. 
	 * @return Processo Seletivo para o qual o candidato está se inscrevendo. 
	 */
	public ProcessoSeletivoVestibular getProcessoSeletivo() {
		return processoSeletivo;
	}

	/** Retorna a região preferencial de prova. Essa região não corresponde às zonas urbanas da cidade. 
	 * @return Região preferencial de prova. 
	 */
	public RegiaoPreferencialProva getRegiaoPreferencialProva() {
		return regiaoPreferencialProva;
	}

	/** Retorna o valor pago na inscrição.  
	 * @return Valor pago na inscrição. 
	 */
	public Double getValorInscricao() {
		return valorInscricao;
	}

	/** Indica se esta inscrição foi validada. 
	 * @return True, se a inscrição foi validada. False, caso contrário.
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

	/** Seta a data da inscrição. 
	 * @param dataInscricao Data da inscrição. 
	 */
	public void setDataInscricao(Date dataInscricao) {
		this.dataInscricao = dataInscricao;
	}

	/** Seta a chave primária.
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Seta o número de inscrição.  
	 * @param inscricao Número de inscrição. 
	 */
	public void setNumeroInscricao(int numeroInscricao) {
		this.numeroInscricao = numeroInscricao;
	}

	/** Seta a língua estrangeira da prova de língua estrangeira. 
	 * @param linguaEstrangeira Língua estrangeira da prova de língua estrangeira. 
	 */
	public void setLinguaEstrangeira(LinguaEstrangeira linguaEstrangeira) {
		this.linguaEstrangeira = linguaEstrangeira;
	}

	/** Seta anotações acerca da inscrição. 
	 * @param observacao Anotações acerca da inscrição. 
	 */
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	/** Seta a matriz curricular para o qual o candidato está se inscrevendo. 
	 * @param opcoesCurso Matriz curricular para o qual o candidato está se inscrevendo. 
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

	/** Seta o processo Seletivo para o qual o candidato está se inscrevendo. 
	 * @param processoSeletivo Processo Seletivo para o qual o candidato está se inscrevendo. 
	 */
	public void setProcessoSeletivo(ProcessoSeletivoVestibular processoSeletivo) {
		this.processoSeletivo = processoSeletivo;
	}

	/** Seta a região preferencial de prova. Essa região não corresponde às zonas urbanas da cidade. 
	 * @param regiaoPreferencialProva Região preferencial de prova. 
	 */
	public void setRegiaoPreferencialProva(
			RegiaoPreferencialProva regiaoPreferencialProva) {
		this.regiaoPreferencialProva = regiaoPreferencialProva;
	}

	/** Seta se esta inscrição foi validada. 
	 * @param validada True, se a inscrição estiver validada. False, caso contrário.
	 */
	public void setValidada(boolean validada) {
		this.validada = validada;
	}

	/** Seta o valor pago na inscrição. 
	 * @param valorInscricao Valor pago na inscrição. 
	 */
	public void setValorInscricao(Double valorInscricao) {
		this.valorInscricao = valorInscricao;
	}

	/** Retorna uma representação textual de uma inscrição para o vestibular no formato:
	 * número de inscrição, seguido de vírgula, seguido do nome do candidato,
	 * seguido de vírgula, seguido da matriz curricular da primeira opção. 
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

	/** Valida os dados obrigatórios para a inscrição de um candidato.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		validateRequired(opcoesCurso[0], "Primeira Opção", lista);
		validateRequired(getLinguaEstrangeira(), "Língua Estrangeira", lista);
		validateRequired(getRegiaoPreferencialProva(), "Região Preferencial de Prova", lista);
		if( isNotEmpty( getProcessoSeletivo() ) && getProcessoSeletivo().isOpcaoBeneficioInclusao() )
			validateRequired(optouBeneficioInclusao, "Benefício de Inclusão", lista);
		return lista;
	}

	/** Retorna o registro de entrada do usuário que validou a inscrição.
	 * @return
	 */
	public RegistroEntrada getValidadoPor() {
		return validadoPor;
	}

	/** Retorna o registro de entrada do usuário que validou a inscrição. 
	 * @param validadoPor
	 */
	public void setValidadoPor(RegistroEntrada validadoPor) {
		this.validadoPor = validadoPor;
	}

	/** Retorna a Data de validação da inscrição. 
	 * @return
	 */
	public Date getValidadoEm() {
		return validadoEm;
	}

	/** Seta a Data de validação da inscrição.
	 * @param validadoEm
	 */
	public void setValidadoEm(Date validadoEm) {
		this.validadoEm = validadoEm;
	}

	/** Retorna o Local de Aplicação de Prova do candidato. 
	 * @return
	 */
	public LocalAplicacaoProva getLocalProva() {
		return localProva;
	}

	/** Seta o Local de Aplicação de Prova do candidato. 
	 * @param localProva
	 */
	public void setLocalProva(LocalAplicacaoProva localProva) {
		this.localProva = localProva;
	}

	/**
	 * Retorna a Turma (sala) em que o candidato fará prova. A idéia de turma
	 * agrupar candidatos por área e língua estrangeira. Uma sala poderá ter uma
	 * ou mais turmas.
	 * 
	 * @return
	 */
	public Integer getTurma() {
		return turma;
	}

	/**
	 * Seta a Turma (sala) em que o candidato fará prova. A idéia de turma
	 * agrupar candidatos por área e língua estrangeira. Uma sala poderá ter uma
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
