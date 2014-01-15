/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 30/07/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.internacionalizacao.dominio;

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

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;

/**
 * Objeto responsável pelo relacionamento entre as áreas de documentos e as entidades traduzidos no sistema.
 * 
 * @author Rafael Gomes
 *
 */
@Entity
@Table(schema = "internacionalizacao", name = "area_documento_entidade")
public class AreaDocumentoEntidade implements Validatable{

	/** Identificador */
	@Id 
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_area_documento_entidade", unique = true, nullable = false)
	private int id;
	
	/** Área do Documento da Instituição traduzida.*/
	@ManyToOne(fetch=FetchType.EAGER) 
	@JoinColumn(name="id_area_documento")
	private AreaDocumento areaDocumento;
	
	/** Entidade do objeto do atributo traduzido.*/
	@ManyToOne(fetch=FetchType.EAGER) 
	@JoinColumn(name="id_entidade_traducao")
	private EntidadeTraducao entidadeTraducao;
	
	/** Nível de ensino preenchida pela entidade de tradução.*/
	@Column(name="nivel_ensino")
	private String nivelEnsino;
	
	/** Indica se a entidade do elemento para tradução encontra-se ativa ou não. */
	@CampoAtivo(true)
	private Boolean ativo = true;

	/** Data de cadastro. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** Registro entrada do usuário que cadastrou. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	
	/**
	 * Constructor
	 */
	public AreaDocumentoEntidade() {}

	/**
	 * Constructor
	 * @param id
	 */
	public AreaDocumentoEntidade(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public AreaDocumento getAreaDocumento() {
		return areaDocumento;
	}

	public void setAreaDocumento(AreaDocumento areaDocumento) {
		this.areaDocumento = areaDocumento;
	}

	public EntidadeTraducao getEntidadeTraducao() {
		return entidadeTraducao;
	}

	public void setEntidadeTraducao(EntidadeTraducao entidadeTraducao) {
		this.entidadeTraducao = entidadeTraducao;
	}

	public String getNivelEnsino() {
		return nivelEnsino;
	}

	public void setNivelEnsino(String nivelEnsino) {
		this.nivelEnsino = nivelEnsino;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}
}