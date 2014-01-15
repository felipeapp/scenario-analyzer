package br.ufrn.sigaa.monitoria.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/*******************************************************************************
 * <p>Representa os tipos de situações de uma prova de seleção de monitoria.<p>
 * 
 * <p>Provas abertas para seleção não podem ser validadas.  Provas concluídas 
 * não podem ter o resultado alterado.
 * <p>
 * 
 * @author Ilueny Santos
 ******************************************************************************/
@Entity
@Table(name = "tipo_situacao_prova_selecao", schema = "monitoria")
public class TipoSituacaoProvaSelecao implements Validatable {

	public static final int AGUARDANDO_INSCRICAO = 1;

	public static final int AGUARDANDO_VALIDACAO = 2;

	public static final int VALIDACAO_EM_ANDAMENTO = 3;

	public static final int CONCLUIDA = 4;

	// Fields

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_tipo_situacao_prova_selecao")
	private int id;

	@Column(name = "descricao", nullable = false)
	private String descricao;

	// Constructors

	/** default constructor */
	public TipoSituacaoProvaSelecao() {
	}

	/** minimal constructor */
	public TipoSituacaoProvaSelecao(int id) {
		this.id = id;
	}

	/** full constructor */
	public TipoSituacaoProvaSelecao(int idTipoBolsa, String descricao) {
		this.id = idTipoBolsa;
		this.descricao = descricao;
	}

	// Property accessors
	public int getId() {
		return this.id;
	}

	public void setId(int idTipoBolsa) {
		this.id = idTipoBolsa;
	}

	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public ListaMensagens validate() {
		return null;
	}
}