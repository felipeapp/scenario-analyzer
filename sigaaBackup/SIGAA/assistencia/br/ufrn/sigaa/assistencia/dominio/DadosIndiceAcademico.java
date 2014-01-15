package br.ufrn.sigaa.assistencia.dominio;

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

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;

@Entity
@Table(name = "dados_indice_academico", schema = "sae")
public class DadosIndiceAcademico implements Validatable {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="sae.dados_academicos_seq") }) 	
	@Column(name = "id_dados_indice_academicos", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** A unidade ao qual o componente pertence. */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_matriz", unique = false, nullable = true, insertable = true, updatable = true)
	private MatrizCurricular matriz;
	
	/** IECH calculado para a importação. */
	private Double iech;
	
	/** IEPL calculado para a importação. */
	private Double iepl;
	
	/** Ano de referência da importação. */
	@Column(name="ano_referencia")
	private Integer anoReferencia;
	
	/** Data de cadastro do Índice Acadêmico. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** Registro entrada do usuário que cadastrou. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/** Data da última atualização do componente. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_remocao")
	@AtualizadoEm
	private Date dataRemocao;

	/** Registro entrada do usuário que realizou a última atualização. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_remocao")
	@AtualizadoPor
	private RegistroEntrada registroRemocao;
	
	/** Indica se o componente é ativo ou não */
	@CampoAtivo(true)
	private boolean ativo = true;
	
	@Transient
	private String pessoaCadastro;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public MatrizCurricular getMatriz() {
		return matriz;
	}

	public void setMatriz(MatrizCurricular matriz) {
		this.matriz = matriz;
	}

	public Double getIech() {
		return iech;
	}

	public void setIech(Double iech) {
		this.iech = iech;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Double getIepl() {
		return iepl;
	}

	public void setIepl(Double iepl) {
		this.iepl = iepl;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public Integer getAnoReferencia() {
		return anoReferencia;
	}

	public void setAnoReferencia(Integer anoReferencia) {
		this.anoReferencia = anoReferencia;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public String getPessoaCadastro() {
		return pessoaCadastro;
	}

	public void setPessoaCadastro(String pessoaCadastro) {
		this.pessoaCadastro = pessoaCadastro;
	}

	public Date getDataRemocao() {
		return dataRemocao;
	}

	public void setDataRemocao(Date dataRemocao) {
		this.dataRemocao = dataRemocao;
	}

	public RegistroEntrada getRegistroRemocao() {
		return registroRemocao;
	}

	public void setRegistroRemocao(RegistroEntrada registroRemocao) {
		this.registroRemocao = registroRemocao;
	}

	@Override
	public ListaMensagens validate() {
		return null;
	}
	
}