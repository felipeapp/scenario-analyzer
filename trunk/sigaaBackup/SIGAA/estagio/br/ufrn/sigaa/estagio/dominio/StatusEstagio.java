/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 20/10/2010
 */
package br.ufrn.sigaa.estagio.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Entidade que representa as Situa��es Poss�veis do Est�gio.
 * 
 * @author arlindo
 *
 */
@Entity
@Table(name = "status_estagio", schema = "estagio")
public class StatusEstagio implements PersistDB {
	
	/** Status que indica que foi cadastrado, por�m necessita 
	 * de aprova��o do coordenador do curso */
	public static final int EM_ANALISE = 1;
	/** Indica que o est�gio foi Cancelado */
	public static final int CANCELADO = 2;
	/** Status que indica que foi Aprovado pelo coordenador do curso */
	public static final int APROVADO = 3;
	/** Status que indica que foi Rejeitado coordenador do curso */
	public static final int NAO_COMPATIVEL = 4;
	/** Indica que o est�gio foi Conclu�do */
	public static final int CONCLUIDO = 5;
	/** Status que Indica que o discente ou o Concedente Solicitou Cancelamento do Est�gio */
	public static final int SOLICITADO_CANCELAMENTO = 6;
	
	/** Chave prim�ria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="estagio.hibernate_sequence") })	
	@Column(name = "id_status_estagio")
	private int id;
	
	/** Descri��o do Status do Est�gio */
	private String descricao;

	/**
	 * Indica se o status permite realizar Parecer do est�gio
	 */
	@Column(name = "permite_parecer")
	private boolean permiteParecer;
	
	/** Construtor Padr�o */
	public StatusEstagio() {}
	
	/** Construtor definido o id do status */
	public StatusEstagio(int id){
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public boolean isPermiteParecer() {
		return permiteParecer;
	}

	public void setPermiteParecer(boolean permiteParecer) {
		this.permiteParecer = permiteParecer;
	}
}
