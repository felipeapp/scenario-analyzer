package br.ufrn.sigaa.monitoria.dominio;

// Generated 09/10/2006 10:44:38 by Hibernate Tools 3.1.0.beta5

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/*******************************************************************************
 * Representa o tipo de avalia��o feita pelos membros da comiss�o de monitoria e
 * comisso cient�fica.
 * 
 * Exemplo: 1=Avalia��o de resumo SID, 2=Avalia��o de Projeto de Ensino,
 * 3=Avalia��o de Relat�rio
 * 
 * 
 * TipoAvaliacao generated by hbm2java
 ******************************************************************************/
@Entity
@Table(name = "tipo_avaliacao", schema = "monitoria")
public class TipoAvaliacaoMonitoria implements Validatable {

	// Fields

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_tipo_avaliacao")
	private int id;

	@Column(name = "descricao", nullable = false)
	private String descricao;

	public static final int AVALIACAO_RESUMO_SID = 1;

	public static final int AVALIACAO_PROJETO_ENSINO = 2;

	public static final int AVALIACAO_RELATORIO = 3;

	// Constructors

	/** default constructor */
	public TipoAvaliacaoMonitoria() {
	}

	/** minimal constructor */
	public TipoAvaliacaoMonitoria(int id) {
		this.id = id;
	}

	/** full constructor */
	public TipoAvaliacaoMonitoria(int idTipoBolsa, String descricao) {
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

	public static TipoAvaliacaoMonitoria getAvaliacaoResumo() {
		TipoAvaliacaoMonitoria tipo = new TipoAvaliacaoMonitoria();
		tipo.setId(TipoAvaliacaoMonitoria.AVALIACAO_RESUMO_SID);
		return tipo;
	}

	public static TipoAvaliacaoMonitoria getAvaliacaoProjeto() {
		TipoAvaliacaoMonitoria tipo = new TipoAvaliacaoMonitoria();
		tipo.setId(TipoAvaliacaoMonitoria.AVALIACAO_PROJETO_ENSINO);
		return tipo;
	}

}
