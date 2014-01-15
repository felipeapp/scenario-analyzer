/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/06/2008
 *
 */
package br.ufrn.sigaa.projetos.dominio;

// Generated 26/06/2008 10:44:38

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.ValidatorUtil;

/*******************************************************************************
 * <p>
 * O coordenador do projeto pode adicionar fotos que
 * podem ser utilizadas tanto na análise da proposta quanto na divulgação do
 * projeto para a comunidade externa. Esta entidade é utilizada nos projetos
 * de monitoria e de extensão. 
 * </p>
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(name = "foto_projeto", schema = "projetos")
public class FotoProjeto implements Validatable {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name = "id_foto_projeto")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
		
	@Column(name = "descricao")
	private String descricao;

	@Column(name = "id_foto_original")
	private Integer idFotoOriginal;

	@Column(name = "id_foto_mini")
	private Integer idFotoMini;

	///////////////////////////////////////
	@ManyToOne
	@JoinColumn(name = "id_projeto")
	private Projeto projeto;
	///////////////////////////////////////

	@Column(name = "ativo")
	private boolean ativo;

	/** default constructor */
	public FotoProjeto() {
	}

	/**
	 * Legenda da foto
	 * 
	 * @return
	 */
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

	/**
	 * id da foto em tamanho Original
	 * 
	 * @return
	 */
	public Integer getIdFotoOriginal() {
		return idFotoOriginal;
	}

	public void setIdFotoOriginal(Integer idFotoOriginal) {
		this.idFotoOriginal = idFotoOriginal;
	}

	/**
	 * id da foto em tamanho reduzido utilizado para exibir várias fotos em uma
	 * mesma página web
	 * 
	 */
	public Integer getIdFotoMini() {
		return idFotoMini;
	}

	public void setIdFotoMini(Integer idFotoMini) {
		this.idFotoMini = idFotoMini;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
		return lista;
	}

	/**
	 * Projeto da Fotos.
	 * 
	 * @return
	 */
	public Projeto getProjeto() { 
		return projeto;
	}

	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "idFotoOriginal");
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
