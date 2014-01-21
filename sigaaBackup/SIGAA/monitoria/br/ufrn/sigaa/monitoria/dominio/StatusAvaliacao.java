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
 * Classe usada em avalia��es de projetos e relat�rios de monitoria.
 * Para avalia��es de resumos do semin�rio de inicia��o � doc�ncia (SID)
 * os status 5 e 6 s�o utilizados. 
 * 
 * StatusAvaliacao generated by hbm2java
 ******************************************************************************/
@Entity
@Table(name = "status_avaliacao", schema = "monitoria")
public class StatusAvaliacao implements Validatable {

	// constantes
	public static final int AGUARDANDO_AVALIACAO = 1;

	public static final int AVALIACAO_EM_ANDAMENTO = 2;

	public static final int AVALIADO = 3;

	public static final int AVALIACAO_CANCELADA = 4;

	// usado na avalia��o do resumo sid
	public static final int AVALIADO_COM_RESSALVAS = 5; 

	// usado na avalia��o do resumo sid
	public static final int AVALIADO_SEM_RESSALVAS = 6; 

	// Fields

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_status_avaliacao")
	private int id;

	@Column(name = "descricao")
	private String descricao;

	// Constructors

	/** default constructor */
	public StatusAvaliacao() {
	}

	/** m�nimo constructor */
	public StatusAvaliacao(int id) {
		this.setId(id);
	}

	/** full constructor */
	public StatusAvaliacao(int idStatusAvaliacao, String descricao) {
		this.id = idStatusAvaliacao;
		this.descricao = descricao;
	}

	// Property accessors
	public int getId() {
		return this.id;
	}

	public void setId(int idStatusAvaliacao) {
		this.id = idStatusAvaliacao;
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

	public static StatusAvaliacao getAguardandoAvaliacao() {
		StatusAvaliacao status = new StatusAvaliacao();
		status.setId(StatusAvaliacao.AGUARDANDO_AVALIACAO);
		return status;
	}

	public static StatusAvaliacao getStatusAvaliado() {
		StatusAvaliacao status = new StatusAvaliacao();
		status.setId(StatusAvaliacao.AVALIADO);
		return status;
	}

}