package br.ufrn.sigaa.monitoria.dominio;

// Generated 19/06/2007 08:45:38

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/*******************************************************************************
 * <p>
 * Representa o tipo de relatório que será emitido por participantes do projeto.
 * </p>
 * <p>
 * Exemplo: 
 * 1=Relatório Parcial, 
 * 2=Relatório Final, 
 * 5=Relatório de Desligamento de Monitor
 * </p>
 * 
 ******************************************************************************/
@Entity
@Table(name = "tipo_relatorio_monitoria", schema = "monitoria")
public class TipoRelatorioMonitoria implements Validatable {

	// Fields

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_tipo_relatorio_monitoria")
	private int id;

	@Column(name = "descricao", nullable = false)
	private String descricao;

	private char contexto;

	public static final int RELATORIO_PARCIAL = 1;

	public static final int RELATORIO_FINAL = 2;

	public static final int RELATORIO_DESLIGAMENTO_MONITOR = 5;

	// Constructors

	/** default constructor */
	public TipoRelatorioMonitoria() {
	}

	/** minimal constructor */
	public TipoRelatorioMonitoria(int id) {
		this.id = id;
	}

	/** full constructor */
	public TipoRelatorioMonitoria(int idTipoBolsa, String descricao) {
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

	/**
	 * Informa o contexto do relatório
	 * 
	 * P - Relatório de projeto, M - Relatório de Monitor, O - Relatório de Orientador
	 * 
	 * @return
	 */
	public char getContexto() {
		return contexto;
	}

	public void setContexto(char contexto) {
		this.contexto = contexto;
	}

}