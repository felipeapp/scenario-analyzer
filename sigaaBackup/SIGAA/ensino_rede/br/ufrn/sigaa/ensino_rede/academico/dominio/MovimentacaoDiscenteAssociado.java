package br.ufrn.sigaa.ensino_rede.academico.dominio;

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
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.ensino_rede.dominio.DiscenteAssociado;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
/**
 * Entidade que representa as movimentações no módulo de ensino em rede.
 * @author Jeferson Queiroga, Henrique André
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(schema="ensino_rede", name = "movimentacao_discente_associado")
public class MovimentacaoDiscenteAssociado implements PersistDB {

	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="ensino_rede.hibernate_sequence") })
	@Column(name = "id_movimentacao", nullable = false)
	private int id;
	
	/** Discente associado a discente */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_discente", nullable = false)
	private DiscenteAssociado discente;

	/** Ano de ocorrência da movimentação*/
	@Column(name = "ano_ocorrencia", nullable = false)
	private Integer anoOcorrencia;
	
	/** Período da ocorrência*/
	@Column(name = "periodo_ocorrencia", nullable = false)
	private Integer periodoOcorrencia;

	/** Ano de referência da movimentação*/
	@Column(name = "ano_referencia", nullable = false)
	private Integer anoReferencia;
	
	/** Período de referência da movimentação*/
	@Column(name = "periodo_referencia", nullable = false)
	private Integer periodoReferencia;
	
	/** Identificação do usuário que cadastrou*/
	@CriadoEm
	@Column(name = "data_cadastro", nullable = false)
	private Date dataCadastro;
	
	/** Data na qual a movimentação foi retornada*/
	@Column(name = "data_retorno")
	private Date dataRetorno;
	
	/** Identificador de quem realizou o retorno*/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_pessoa_retorno")
	private Pessoa pessoaRetorno;
	
	/** Registro de entrada do criado da movimentação*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reg_criacao")
	@CriadoPor
	private RegistroEntrada regCriacao;

	/** Atributo para identificar se a movimentação está ativa */
	@Column(name = "ativo")
	private boolean ativo = true;
	
	/** Tipo de movimentação*/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo")
	private TipoMovimentacao tipo; 
	
	/** Essa relação identinfica Movimentação "pai" */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_movimentacao_origem")
	private MovimentacaoDiscenteAssociado movimentacaoOrigem;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DiscenteAssociado getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteAssociado discente) {
		this.discente = discente;
	}

	public Integer getAnoOcorrencia() {
		return anoOcorrencia;
	}

	public void setAnoOcorrencia(Integer anoOcorrencia) {
		this.anoOcorrencia = anoOcorrencia;
	}

	public Integer getPeriodoOcorrencia() {
		return periodoOcorrencia;
	}

	public void setPeriodoOcorrencia(Integer periodoOcorrencia) {
		this.periodoOcorrencia = periodoOcorrencia;
	}

	public Integer getAnoReferencia() {
		return anoReferencia;
	}

	public void setAnoReferencia(Integer anoReferencia) {
		this.anoReferencia = anoReferencia;
	}

	public Integer getPeriodoReferencia() {
		return periodoReferencia;
	}

	public void setPeriodoReferencia(Integer periodoReferencia) {
		this.periodoReferencia = periodoReferencia;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegCriacao() {
		return regCriacao;
	}

	public void setRegCriacao(RegistroEntrada regCriacao) {
		this.regCriacao = regCriacao;
	}

	public TipoMovimentacao getTipo() {
		return tipo;
	}

	public void setTipo(TipoMovimentacao tipo) {
		this.tipo = tipo;
	}

	public Date getDataRetorno() {
		return dataRetorno;
	}

	public void setDataRetorno(Date dataRetorno) {
		this.dataRetorno = dataRetorno;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Pessoa getPessoaRetorno() {
		return pessoaRetorno;
	}

	public void setPessoaRetorno(Pessoa pessoaRetorno) {
		this.pessoaRetorno = pessoaRetorno;
	}

	public MovimentacaoDiscenteAssociado getMovimentacaoOrigem() {
		return movimentacaoOrigem;
	}

	public void setMovimentacaoOrigem(
			MovimentacaoDiscenteAssociado movimentacaoOrigem) {
		this.movimentacaoOrigem = movimentacaoOrigem;
	}

	
}
