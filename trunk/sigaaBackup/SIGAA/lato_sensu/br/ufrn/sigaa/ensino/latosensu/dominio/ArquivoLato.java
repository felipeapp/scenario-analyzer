/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '12/11/2007'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Arquivos do relatório final de curso lato sensu OU de propostas de criação de curso lato sensu.
 * 
 * @author leonardo
 *
 */
@Entity
@Table(name = "arquivo_lato", schema = "lato_sensu", uniqueConstraints = {})
public class ArquivoLato implements Validatable {

	@Id
	@Column(name="id_arquivo_lato")
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private int id;

	private String descricao;
	
	@Column(name = "id_arquivo")
	private Integer idArquivo;
	
	@ManyToOne
	@JoinColumn(name="id_relatorio")
	private RelatorioFinalLato relatorio;
	
	@ManyToOne
	@JoinColumn(name="id_curso")
	private CursoLato curso;

	private boolean ativo = true;

	/** default constructor */
	public ArquivoLato() {
	}
	
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

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

	public Integer getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
	}

	public RelatorioFinalLato getRelatorio() {
		return relatorio;
	}

	public void setRelatorio(RelatorioFinalLato relatorio) {
		this.relatorio = relatorio;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descrição", lista.getMensagens());
		return lista;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "idArquivo");
	}

	public CursoLato getCurso() {
		return curso;
	}

	public void setCurso(CursoLato curso) {
		this.curso = curso;
	}

}
