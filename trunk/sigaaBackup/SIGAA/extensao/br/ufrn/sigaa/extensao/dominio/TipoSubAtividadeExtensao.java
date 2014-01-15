/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 09/06/2012
 *
 */
package br.ufrn.sigaa.extensao.dominio;
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

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 * 
 * Representa um tipo de subAtividade existente.
 * 
 * 
 * @author Igor Linnik
 *
 */
@Entity
@Table(schema = "extensao", name = "tipo_sub_atividade_extensao")
public class TipoSubAtividadeExtensao implements Validatable {
	
	public static final int MINI_CURSO = 1;
	public static final int MINI_EVENTO = 2;
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_tipo_sub_atividade_extensao", nullable = false)
	private int id;

	@Column(name = "descricao", unique = false, nullable = true, insertable = true, updatable = true, length = 40)
	private String descricao;
	
	/** Informa o tipo de projeto. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_atividade", nullable = false)    	
	private TipoAtividadeExtensao tipoAtividade;

	public TipoSubAtividadeExtensao(){
		
	}
	
	public TipoSubAtividadeExtensao(int id){
		this.id = id;	
	}
	
	public TipoSubAtividadeExtensao(String descricao){
		this.descricao = descricao;
	}
	
	public TipoSubAtividadeExtensao(int id, String descricao){
		this(id);
		this.descricao = descricao;
	}
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}	

	public TipoAtividadeExtensao getTipoAtividade() {
		return tipoAtividade;
	}

	public void setTipoAtividade(TipoAtividadeExtensao tipoAtividade) {
		this.tipoAtividade = tipoAtividade;
	}

	@Override
	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}	
	
}