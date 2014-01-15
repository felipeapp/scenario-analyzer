/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 23/10/2008
 *
 */
package br.ufrn.sigaa.sites.dominio;

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

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;

/**
 * Entidade de domínio que armazena documentos dos portais públicos do SIGAA (Centro, Departamentos, Programa e Cursos).
 * Documentos como relatórios, resoluções e formulários.
 * 
 * @author Mário Rizzi
 */
@Entity
@Table(name = "documento_site", schema = "site")
public class DocumentoSite implements Validatable {

	/** Atributo que define a unicidade do documento. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	@Column(name = "id_documento_site", nullable = false)
	private int id;
	
	/** Atribui o id do tipo de documento qual pertence */
	@ManyToOne()
	@JoinColumn(name = "id_tipo_documento_site", unique = false, nullable = true, insertable = true, updatable = true)
	private TipoDocumentoSite tipoDocumentoSite;
	
	/** Atribui o id do centro, departamento ou programa ao qual este documento pertence */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_unidade", unique = false, nullable = true, insertable = true, updatable = true)
	private Unidade unidade;
	
	/** Atribui o id do curso ao qual este documento pertence */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_curso", unique = false, nullable = true, insertable = true, updatable = true)
	private Curso curso;
	
	/** Atribui o nome do documento que será exibido na visualização */
	@Column(name="nome", nullable = false)
	private String nome;

	/** Atribui a referência ao arquivo no banco de dados */
	@Column(name="id_arquivo", nullable = false)
	private int idArquivo;

	/** Atribui o registro entrada de quem cadastrou */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro", unique = false, nullable = true, insertable = true, updatable = true)
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/** Atribui a data de cadastro */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	@CriadoEm
	private Date dataCadastro;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(int idArquivo) {
		this.idArquivo = idArquivo;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public TipoDocumentoSite getTipoDocumentoSite() {
		return tipoDocumentoSite;
	}
	
	public void setTipoDocumentoSite(TipoDocumentoSite tipoDocumentoSite) {
		this.tipoDocumentoSite = tipoDocumentoSite;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}
	
	/**
	 * Método que valida os campos utilizados no formulário
	 * de alteração e cadastro de documento.
	 */
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(nome, "Nome", erros);
		return erros;
	}

}
