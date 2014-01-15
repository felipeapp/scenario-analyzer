/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/12/2008
 *
 */
package br.ufrn.sigaa.projetos.dominio;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/** 
 * Grupo de p�blico alvo, esta entidade agrupa os tipos de p�blico alvo dos projetos
 * Utilizado para organiza��o da apresenta��o dos p�blico alvo aos usu�rios   
 */
@Entity
@Table(name = "grupo_publico_alvo", schema = "projetos")
public class GrupoPublicoAlvo implements Validatable {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name = "id_grupo")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;

	/** descri��o do grupo */
	private String descricao;

	/** cole��o com os tipos de p�blico alvo */
	@OneToMany(mappedBy = "grupo")
	@OrderBy(value="descricao")
	private Collection<TipoPublicoAlvo> tipos;
	
	private boolean ativo = true;
	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Collection<TipoPublicoAlvo> getTipos() {
		return tipos;
	}

	public void setTipos(Collection<TipoPublicoAlvo> tipos) {
		this.tipos = tipos;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descri��o", lista);
		return lista;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
}
