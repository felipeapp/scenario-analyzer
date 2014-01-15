/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/05/2008
 *
 */

package br.ufrn.sigaa.pesquisa.dominio;

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

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Classe que registra os vínculos de uma invenção a projetos de pesquisa,
 * grupos (bases) de pesquisa ou programa estratégico de apoio à pesquisa.
 * 
 * @author leonardo
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name="vinculo_invencao", schema="pesquisa", uniqueConstraints={})
public class VinculoInvencao implements Validatable {

	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name="id_vinculo_invencao")
	private int id;
	
	@ManyToOne
	@JoinColumn(name="id_invencao")
	private Invencao invencao;
	
	@ManyToOne
	@JoinColumn(name="id_projeto_pesquisa")
	private ProjetoPesquisa projetoPesquisa;
	
	@ManyToOne
	@JoinColumn(name="id_grupo_pesquisa")
	private GrupoPesquisa grupoPesquisa;
	
	
	public VinculoInvencao() {
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Invencao getInvencao() {
		return invencao;
	}

	public void setInvencao(Invencao invencao) {
		this.invencao = invencao;
	}

	public ProjetoPesquisa getProjetoPesquisa() {
		return projetoPesquisa;
	}

	public void setProjetoPesquisa(ProjetoPesquisa projetoPesquisa) {
		this.projetoPesquisa = projetoPesquisa;
	}

	public GrupoPesquisa getGrupoPesquisa() {
		return grupoPesquisa;
	}

	public void setGrupoPesquisa(GrupoPesquisa grupoPesquisa) {
		this.grupoPesquisa = grupoPesquisa;
	}
	
	@Transient
	public String getDescricao(){
		if(projetoPesquisa != null)
			return projetoPesquisa.getProjeto().getAnoTitulo();
		else
			return grupoPesquisa.getNome();
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	
	public ListaMensagens validate() {
		return null;
	}
}
