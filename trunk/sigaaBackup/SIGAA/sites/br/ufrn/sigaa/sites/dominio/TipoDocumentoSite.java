/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/05/2009
 *
 */
package br.ufrn.sigaa.sites.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Entidade de domínio que armazena os tipos de documentos pertencentes aos portais públicos do SIGAA (Centros, Departamentos, Programas e Cursos).
 *  
 * @author Mário Rizzi
 * 
 */
@Entity
@Table(name = "tipo_documento_site", schema = "site")
public class TipoDocumentoSite implements Validatable {

	/** Atributo que define a unicidade do tipo de documento. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	@Column(name = "id_tipo_documento_site", nullable = false)
	private int id;
	
	/** Atribui o nome do documento que será exibido na visualização. */
	@Column(name="nome", nullable = false)
	private String nome;
	
	/** Atribui o nome do documento que será exibido na visualização. */
	@Column(name="nome_en", nullable = false)
	private String nomeEn;

	/** Atribui a data em que o tipo de documento foi cadastrado. */
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
	
	public String getNomeEn() {
		return nomeEn;
	}

	public void setNomeEn(String nomeEn) {
		this.nomeEn = nomeEn;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	/**
	 * Método que valida os campos utilizados no formulário
	 * de alteração e cadastro de documento.
	 */
	public ListaMensagens validate() {
		
		ListaMensagens erros = new ListaMensagens();
		validateRequired(nome, "Nome", erros);
		
		return erros;
		
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}
	
}
