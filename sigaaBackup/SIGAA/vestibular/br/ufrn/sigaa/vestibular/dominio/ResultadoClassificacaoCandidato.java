/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 08/12/2010
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.vestibular.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;

/**
 * Classe que modela o resultado da classificação do candidato no processo seletivo.
 * @author Rafael Gomes
 *
 */
@Entity
@Table(name = "resultado_classificacao_candidato", schema = "vestibular")
public class ResultadoClassificacaoCandidato implements PersistDB, Validatable{

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="vestibular.seq_resultado_classificacao_candidato") })
	@Column(name = "id_resultado_classificacao_candidato", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Inscrição associada à este resultado de classificacação do candidato. */
	@ManyToOne
	@JoinColumn(name = "id_inscricao_vestibular", nullable = false, insertable = true, updatable = true)
	private InscricaoVestibular inscricaoVestibular;
	
	/** Número de inscrição do candidato. */
	@Column(name = "numero_inscricao")
	private int numeroInscricao;
	
	/** Opção em que o candidato foi aprovado (número da matriz curricular).*/
	@Column(name = "opcao_aprovacao")
	private Integer opcaoAprovacao;
	
	/** Atributo utilizado para informar em qual classificação o candidato foi aprovado,
	 * principalmente ser for por 2ª opção. */
	@Column(name = "classificacao_aprovado")
	private Integer classificacaoAprovado;
		
	/** Semestre em que o candidato foi aprovado.*/
	@Column(name = "semestre_aprovacao")
	private Integer semestreAprovacao;
	
	/** Indica que há notas das provas do processo seletivo associadas a este resultado. */
	@Column(name = "existe_resultado_classificacao")
	private boolean existeResultadoClassificacao;
	
	/** Indica que o candidato foi aprovado em curso de turno distinto. */
	@Column(name = "aprovado_turno_distinto")
	private boolean aprovadoTurnoDistinto;
	
	/** Indica que o candidato foi aprovado com índice inferior ao argumento mínimo de aprovação. */
	@Column(name = "aprovado_ama")
	private boolean aprovadoAma;
	
	/** Situação do candidato no processo seletivo. */
	@ManyToOne
	@JoinColumn(name = "id_situacao_candidato")
	private SituacaoCandidato situacaoCandidato;
	
	/** Data de cadastro. */
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;
	
	/** Registro de Entrada de cadastro. */
	@ManyToOne
	@JoinColumn(name = "id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/** Grupo de Cotas pelo qual o discente foi convocado. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_grupo_cota_vaga_curso")
	private GrupoCotaVagaCurso grupoCotaConvocado;
	
	/** Indica que o candidato foi classificado para preenchimento de vagas remanescente de grupo de cotas. */
	@Column(name = "grupo_cota_remanejado")
	private Boolean grupoCotaRemanejado;
	
	/**
	 * Minimal Constructor
	 */
	public ResultadoClassificacaoCandidato() {
	}
	
	/**
	 * Constructor
	 */
	public ResultadoClassificacaoCandidato( int id ) {
		this.id = id;
	}
	
	/**
	 * Constructor
	 * @param id
	 * @param inscricaoVestibular
	 * @param numeroInscricao
	 * @param opcaoAprovacao
	 * @param semestreAprovacao
	 */
	public ResultadoClassificacaoCandidato(int id,
			InscricaoVestibular inscricaoVestibular, int numeroInscricao,
			Integer opcaoAprovacao, Integer semestreAprovacao) {
		this.id = id;
		this.inscricaoVestibular = inscricaoVestibular;
		this.numeroInscricao = numeroInscricao;
		this.opcaoAprovacao = opcaoAprovacao;
		this.semestreAprovacao = semestreAprovacao;
	}
	
	/**
	 * Constructor
	 * @param id
	 * @param inscricaoVestibular
	 * @param pessoa
	 * @param numeroInscricao
	 * @param opcaoAprovacao
	 * @param semestreAprovacao
	 */
	public ResultadoClassificacaoCandidato(int id,
			int inscricaoVestibular, int pessoa, int numeroInscricao,
			Integer opcaoAprovacao, Integer semestreAprovacao) {
		this.id = id;
		this.inscricaoVestibular = new InscricaoVestibular(inscricaoVestibular);
		this.inscricaoVestibular.setPessoa(new PessoaVestibular(pessoa));
		this.numeroInscricao = numeroInscricao;
		this.opcaoAprovacao = opcaoAprovacao;
		this.semestreAprovacao = semestreAprovacao;
	}
	
	
	/** Getters and Setters **/
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the inscricaoVestibular
	 */
	public InscricaoVestibular getInscricaoVestibular() {
		return inscricaoVestibular;
	}

	/**
	 * @param inscricaoVestibular the inscricaoVestibular to set
	 */
	public void setInscricaoVestibular(InscricaoVestibular inscricaoVestibular) {
		this.inscricaoVestibular = inscricaoVestibular;
	}

	/**
	 * @return the numeroInscricao
	 */
	public int getNumeroInscricao() {
		return numeroInscricao;
	}

	/**
	 * @param numeroInscricao the numeroInscricao to set
	 */
	public void setNumeroInscricao(int numeroInscricao) {
		this.numeroInscricao = numeroInscricao;
	}

	/**
	 * @return the opcaoAprovacao
	 */
	public Integer getOpcaoAprovacao() {
		return opcaoAprovacao;
	}

	/**
	 * @param opcaoAprovacao the opcaoAprovacao to set
	 */
	public void setOpcaoAprovacao(Integer opcaoAprovacao) {
		this.opcaoAprovacao = opcaoAprovacao;
	}

	/**
	 * @return the classificacaoAprovado
	 */
	public Integer getClassificacaoAprovado() {
		return classificacaoAprovado;
	}

	/**
	 * @param classificacaoAprovado the classificacaoAprovado to set
	 */
	public void setClassificacaoAprovado(Integer classificacaoAprovado) {
		this.classificacaoAprovado = classificacaoAprovado;
	}

	/**
	 * @return the semestreAprovacao
	 */
	public Integer getSemestreAprovacao() {
		return semestreAprovacao;
	}

	/**
	 * @param semestreAprovacao the semestreAprovacao to set
	 */
	public void setSemestreAprovacao(Integer semestreAprovacao) {
		this.semestreAprovacao = semestreAprovacao;
	}
	
	/**
	 * @return the situacaoCandidato
	 */
	public SituacaoCandidato getSituacaoCandidato() {
		return situacaoCandidato;
	}

	/**
	 * @param situacaoCandidato the situacaoCandidato to set
	 */
	public void setSituacaoCandidato(SituacaoCandidato situacaoCandidato) {
		this.situacaoCandidato = situacaoCandidato;
	}

	/**
	 * @return the existeResultadoClassificacao
	 */
	public boolean isExisteResultadoClassificacao() {
		return existeResultadoClassificacao;
	}

	/**
	 * @param existeResultadoClassificacao the existeResultadoClassificacao to set
	 */
	public void setExisteResultadoClassificacao(boolean existeResultadoClassificacao) {
		this.existeResultadoClassificacao = existeResultadoClassificacao;
	}

	/**
	 * @return the aprovadoTurnoDistinto
	 */
	public boolean isAprovadoTurnoDistinto() {
		return aprovadoTurnoDistinto;
	}

	/**
	 * @param aprovadoTurnoDistinto the aprovadoTurnoDistinto to set
	 */
	public void setAprovadoTurnoDistinto(boolean aprovadoTurnoDistinto) {
		this.aprovadoTurnoDistinto = aprovadoTurnoDistinto;
	}

	/**
	 * @return the aprovadoAma
	 */
	public boolean isAprovadoAma() {
		return aprovadoAma;
	}

	/**
	 * @param aprovadoAma the aprovadoAma to set
	 */
	public void setAprovadoAma(boolean aprovadoAma) {
		this.aprovadoAma = aprovadoAma;
	}

	/**
	 * @return the dataCadastro
	 */
	public Date getDataCadastro() {
		return dataCadastro;
	}

	/**
	 * @param dataCadastro the dataCadastro to set
	 */
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	/**
	 * @return the registroCadastro
	 */
	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	/**
	 * @param registroCadastro the registroCadastro to set
	 */
	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}


	@Override
	public ListaMensagens validate() {
		return null;
	}

	public GrupoCotaVagaCurso getGrupoCotaConvocado() {
		return grupoCotaConvocado;
	}

	public void setGrupoCotaConvocado(GrupoCotaVagaCurso grupoCotaConvocado) {
		this.grupoCotaConvocado = grupoCotaConvocado;
	}

	public Boolean getGrupoCotaRemanejado() {
		return grupoCotaRemanejado;
	}

	public void setGrupoCotaRemanejado(Boolean grupoCotaRemanejado) {
		this.grupoCotaRemanejado = grupoCotaRemanejado;
	}
	
}
