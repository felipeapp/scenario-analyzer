/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 14/05/2009
 */
package br.ufrn.comum.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.negocio.validacao.annotations.FieldName;
import br.ufrn.arq.negocio.validacao.annotations.Required;
import br.ufrn.arq.negocio.validacao.annotations.Url;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Classe para armazenar informações de materiais para
 * treinamento nas operações dos sistemas (manuais, video-auls, etc.)
 * 
 * @author Júlio César
 * @author David Pereira
 * @author Gleydson Lima
 */
@Entity
@Table(name = "material_treinamento",schema="comum") 
public class MaterialTreinamento implements Validatable {
	
	/** Identificador de Material */
	@Id
	@Column(name = "id_material_treinamento")
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	private int id;

	@Required @FieldName("Código da Operação")
	@Column(name="codigo_uc")
	private String codigoUc;
	
	/** Nome do Material de Treinamento */
	@Required
	@Column(name = "nome_material")
	private String nome;

	/** Descrição do Material de treinamento */
	private String descricao;
	
	/** URL ao material de Treinamento */
	@Required @Url
	@FieldName("URL")
	private String endereco;

	private boolean ativo;
	
	/** Tipo do material de Treinamento */
	@Required @FieldName("Tipo de Material")
	@ManyToOne @JoinColumn (name = "id_tipo_material")
	private TipoMaterialTreinamento tipoMaterialTreinamento = new TipoMaterialTreinamento();
	
	/** Sistema ao qual o material está relacionado */
	@Required
	@ManyToOne @JoinColumn(name="id_sistema")
	private Sistema sistema = new Sistema();
	
	/** Sub-sistema ao qual o material está relacionado */
	@ManyToOne @JoinColumn(name="id_subsistema")
	private SubSistema subSistema = new SubSistema();
	
	public MaterialTreinamento() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCodigoUc() {
		return codigoUc;
	}

	public void setCodigoUc(String codigoUc) {
		this.codigoUc = codigoUc;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public TipoMaterialTreinamento getTipoMaterialTreinamento() {
		return tipoMaterialTreinamento;
	}

	public void setTipoMaterialTreinamento(
			TipoMaterialTreinamento tipoMaterialTreinamento) {
		this.tipoMaterialTreinamento = tipoMaterialTreinamento;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MaterialTreinamento other = (MaterialTreinamento) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public Sistema getSistema() {
		return sistema;
	}

	public void setSistema(Sistema sistema) {
		this.sistema = sistema;
	}

	public SubSistema getSubSistema() {
		return subSistema;
	}

	public void setSubSistema(SubSistema subSistema) {
		this.subSistema = subSistema;
	}

	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		
		ValidatorUtil.validateRequired(codigoUc, "Código da Operação", lista);
		ValidatorUtil.validateRequired(nome, "Nome", lista);
		ValidatorUtil.validateRequired(endereco, "URL", lista);
		ValidatorUtil.validateUrl(endereco, "URL", lista);
		ValidatorUtil.validateRequired(tipoMaterialTreinamento, "Tipo de Material", lista);
		ValidatorUtil.validateRequired(sistema, "Sistema", lista);
		
		return lista;
	}
}