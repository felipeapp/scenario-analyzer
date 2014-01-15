/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 05/07/2012
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
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Objeto responsável pela manutenção dos campos/atributos que serão traduzidos no sistema.
 * 
 * @author Rafael Gomes
 *
 */

@Entity
@Table(schema = "internacionalizacao", name = "item_traducao")
public class ItemTraducao implements Validatable{

	/** Identificador */
	@Id 
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_item_traducao", unique = true, nullable = false)
	private int id;
	
	/** Nome legível da Entidade do atributo traduzido.*/
	@Column
	private String nome;

	/** Entidade do objeto do atributo traduzido.*/
	@ManyToOne(fetch=FetchType.EAGER) 
	@JoinColumn(name="id_entidade_traducao")
	private EntidadeTraducao entidade;
	
	/** Nome do atributo na classe a ser traduzido. */
	@Column
	private String atributo;
	
	/** Armazena a string com os possíveis idiomas que o item será traduzido. ex: (en,pt,fr). */
	@Column
	private String idiomas;
	
	/**Sinaliza se o texto de tradução será solicitado em uma área de texto. */
	@Column(name="tipo_area_texto")
	private boolean tipoAreaTexto;
	
	/** Indica se o item da entidade encontra-se ativa ou não, para a tradução. */
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
	
	
	/** Construtor padrão */
	public ItemTraducao() {}

	/**
	 * Construtor mínimo
	 * @param id
	 */
	public ItemTraducao(int id) {
		this.id = id;
	}

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

	public EntidadeTraducao getEntidade() {
		return entidade;
	}

	public void setEntidade(EntidadeTraducao entidade) {
		this.entidade = entidade;
	}

	public String getAtributo() {
		return atributo;
	}

	public void setAtributo(String atributo) {
		this.atributo = atributo;
	}

	public String getIdiomas() {
		return idiomas;
	}

	public void setIdiomas(String idiomas) {
		this.idiomas = idiomas;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public boolean isTipoAreaTexto() {
		return tipoAreaTexto;
	}

	public void setTipoAreaTexto(boolean tipoAreaTexto) {
		this.tipoAreaTexto = tipoAreaTexto;
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

	@Override
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequiredId(entidade.getId(), "Entidade", erros);
		ValidatorUtil.validateRequired(nome, "Nome", erros);
		ValidatorUtil.validateRequired(atributo, "Atributo", erros);
		return erros;
	}

	
	
}
