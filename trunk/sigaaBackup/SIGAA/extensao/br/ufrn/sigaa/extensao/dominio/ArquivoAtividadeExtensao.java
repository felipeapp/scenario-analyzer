/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 16/10/2007
 *
 */
package br.ufrn.sigaa.extensao.dominio;

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

/*******************************************************************************
 * <p>
 * Arquivo das A��es de Extens�o. Utilizado para enviar o arquivo completo da
 * Proposta da A��o de Extens�o caso tenha sido elaborada tamb�m em outro
 * formato (Word, Excel, PDF e outros). Utilizado tamb�m para anexar outros
 * documentos que o elaborador da proposta julgar indispens�veis para aprova��o
 * e/ou execu��o da A��o de Extens�o que est� sendo cadastrada.
 * </p>
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(name = "arquivo_atividade", schema = "extensao", uniqueConstraints = {})
@Deprecated //Utilizar ArquivoProjeto
public class ArquivoAtividadeExtensao implements Validatable {

	@Id
	@Column(name = "id_arquivo_atividade")
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int id;

	@Column(name = "descricao")
	private String descricao;

	@Column(name = "id_arquivo")
	private Integer idArquivo;

	@ManyToOne
	@JoinColumn(name = "id_atividade")
	private AtividadeExtensao atividade;

	@Column(name = "ativo")
	private boolean ativo;

	/** default constructor */
	public ArquivoAtividadeExtensao() {
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

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descri��o", lista);
		return lista;
	}

	public AtividadeExtensao getAtividade() {
		return atividade;
	}

	public void setAtividade(AtividadeExtensao atividade) {
		this.atividade = atividade;
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

}
