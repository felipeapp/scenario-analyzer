/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 06/04/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.pessoa.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.TipoNecessidadeEspecial;

/**
 * Classe intermediária para satisfazer a relação muitos para muitos 
 * entre Pessoa e TipoNecessidadeEspecial
 * 
 * @author Rafael Gomes
 *
 */
@Entity
@Table(name = "pessoa_necessidade_especial", schema="comum")
public class PessoaNecessidadeEspecial implements PersistDB{
	
	/** Identificador */
	@Id
    @GeneratedValue(generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", 
    				  parameters = {@Parameter(name = "sequence_name", value = "comum.pessoa_nec_esp_seq")})
	@Column(name = "id_pessoa_necessidade_especial", nullable = false)
	private int id;	
	

	/** Identificador de pessoa a qual a necessidade especial está vinculada. */
	@ManyToOne
	@JoinColumn(name = "id_pessoa", nullable = false)
	private Pessoa pessoa;
	
	/** Identificação da chave do tipo de necessidade a qual a pessoa está vinculada*/
	@ManyToOne
	@JoinColumn(name = "id_necessidade_especial", nullable = false)
	private TipoNecessidadeEspecial tipoNecessidadeEspecial;
	
	/** Armazena informações sobre uma necessidade especial de uma pessoa que foram disponibilizadas durante a atribuição da necessidade para a pessoa. **/
	@Column(name = "observacao")
	private String observacao;
	
	/**
	 * Data de realização do cadastro da necessidade especial para a pessoa vinculada. 
	 */
	@CriadoEm
	@Column(name="data_cadastro")
	private Date dataCadastro;
	
	/**
	 * Registro de entrada do responsável pelo cadastro da necessidade especial. 
	 */
	@CriadoPor
	@ManyToOne
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada;
	
	/** Atributo transiente que define se é selecionado */
	@Transient
	boolean selecionado;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public TipoNecessidadeEspecial getTipoNecessidadeEspecial() {
		return tipoNecessidadeEspecial;
	}

	public void setTipoNecessidadeEspecial(
			TipoNecessidadeEspecial tipoNecessidadeEspecial) {
		this.tipoNecessidadeEspecial = tipoNecessidadeEspecial;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}
	
	 public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateMaxLength(observacao, 500, "Observação", erros);
		return erros;
	}

}
