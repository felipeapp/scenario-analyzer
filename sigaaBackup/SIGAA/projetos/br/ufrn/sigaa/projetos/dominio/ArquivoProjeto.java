/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/06/2008
 *
 */
package br.ufrn.sigaa.projetos.dominio;


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
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;

/*******************************************************************************
 * <p>
 * Arquivos dos Projetos. O coordenador do projeto pode adicionar arquivos que
 * podem ser utilizadas tanto na análise da proposta quanto na divulgação do
 * projeto para a comunidade externa.
 * </p>
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(name = "arquivo_projeto", schema = "projetos")
public class ArquivoProjeto implements Validatable {	
	
	/** Identificador */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_arquivo_projeto")           
	private int id;	 
	
	/** Descrição do arquivo projeto */
	@Column(name = "descricao")
	private String descricao;

	/** Identificador arquivo */
	@Column(name = "id_arquivo")
	private Integer idArquivo;

	/** Projeto relacionado ao arquivo projeto */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_projeto")
	private Projeto projeto;
	
	/** Projeto relacionado ao arquivo projeto */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_relatorio_projeto")
	private RelatorioAcaoAssociada relatorioProjeto;

	/** Arquivo projeto ativo/falso */
	@Column(name = "ativo")
	private boolean ativo;

	/** default constructor */
	public ArquivoProjeto() {
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
	 * Projeto do arquivo.
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
		return EqualsUtil.testEquals(this, obj, "idArquivo");
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Integer getIdArquivo() {
	    return idArquivo;
	}

	public void setIdArquivo(Integer idArquivo) {
	    this.idArquivo = idArquivo;
	}

	/** validação arquivo projeto */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
		return lista;
	}
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	public RelatorioAcaoAssociada getRelatorioProjeto() {
		return relatorioProjeto;
	}

	public void setRelatorioProjeto(RelatorioAcaoAssociada relatorioProjeto) {
		this.relatorioProjeto = relatorioProjeto;
	}

}
