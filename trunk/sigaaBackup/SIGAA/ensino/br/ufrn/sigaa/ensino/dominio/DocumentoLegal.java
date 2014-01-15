package br.ufrn.sigaa.ensino.dominio;

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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Curso;

/**
 * Entidade que relaciona os Documentos aos Cursos.
 * 
 * @author Jean Guerethes
 */
@Entity
@Table(name="documento_legal", schema="ensino")
public class DocumentoLegal implements Validatable {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_documento_legal", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Curso do documento */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name = "id_curso")
	private Curso curso = new Curso();
	
	/** Nome do documento */
	@Column(name = "nome_documento", unique = false, nullable = true, insertable = true, updatable = true)
	private String nomeDocumento;

	/** Número do Documento */
	@Column(name = "numero_documento", unique = false, nullable = true, insertable = true, updatable = true)
	private String numeroDocumento;

	/** Data da aprovação do documento */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_aprovacao", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	private Date dataAprovacao;

	/** Local da publicacao do documento */
	@Column(name = "local_publicacao", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	private String localPublicacao;
	
	/** Número do Parecer do Documento */
	@Column(name = "numero_parecer", unique = false, nullable = true, insertable = true, updatable = true)
	private String numeroParecer;
    
	/** Validade do Documento */
	@Column(name = "validade", unique = false, nullable = true, insertable = true, updatable = true)
	private String validade;

	/** Data do Parecer do Documento */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_parecer", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	private Date dataParecer;

	/** Data da publicação do Documento */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_publicacao", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	private Date dataPublicacao;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public String getNomeDocumento() {
		return nomeDocumento;
	}

	public void setNomeDocumento(String nomeDocumento) {
		this.nomeDocumento = nomeDocumento;
	}

	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	public Date getDataAprovacao() {
		return dataAprovacao;
	}

	public void setDataAprovacao(Date dataAprovacao) {
		this.dataAprovacao = dataAprovacao;
	}

	public String getLocalPublicacao() {
		return localPublicacao;
	}

	public void setLocalPublicacao(String localPublicacao) {
		this.localPublicacao = localPublicacao;
	}

	public String getNumeroParecer() {
		return numeroParecer;
	}

	public void setNumeroParecer(String numeroParecer) {
		this.numeroParecer = numeroParecer;
	}

	public String getValidade() {
		return validade;
	}

	public void setValidade(String validade) {
		this.validade = validade;
	}

	public Date getDataParecer() {
		return dataParecer;
	}

	public void setDataParecer(Date dataParecer) {
		this.dataParecer = dataParecer;
	}

	public Date getDataPublicacao() {
		return dataPublicacao;
	}

	public void setDataPublicacao(Date dataPublicacao) {
		this.dataPublicacao = dataPublicacao;
	}

	/** 
	 * Valida os dados do Documento Legal (Curso, nomeDocumento, NumeroDocumento, Número Parecer)
	 */
	@Override
	public ListaMensagens validate() {

		ListaMensagens lista = new ListaMensagens();
		if (getCurso().getId() == 0) 
			ValidatorUtil.validateRequired(null, "Curso", lista);
		
		ValidatorUtil.validateRequired(getNomeDocumento(), "Nome do Documento", lista);
		ValidatorUtil.validateRequired(getNumeroDocumento(), "Número do Documento", lista);
		ValidatorUtil.validateRequired(getNumeroParecer(), "Número do Parecer", lista);
		
		return lista;
	}
	
}