/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Entidade que representa as Situações Possíveis do Estágio.
 * 
 * @author arlindo
 *
 */
@Entity
@Table(name = "status_estagio", schema = "estagio")
public class StatusEstagio implements PersistDB {
	
	/** Status que indica que foi cadastrado, porém necessita 
	 * de aprovação do coordenador do curso */
	public static final int EM_ANALISE = 1;
	/** Indica que o estágio foi Cancelado */
	public static final int CANCELADO = 2;
	/** Status que indica que foi Aprovado pelo coordenador do curso */
	public static final int APROVADO = 3;
	/** Status que indica que foi Rejeitado coordenador do curso */
	public static final int NAO_COMPATIVEL = 4;
	/** Indica que o estágio foi Concluído */
	public static final int CONCLUIDO = 5;
	/** Status que Indica que o discente ou o Concedente Solicitou Cancelamento do Estágio */
	public static final int SOLICITADO_CANCELAMENTO = 6;
	
	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="estagio.hibernate_sequence") })	
	@Column(name = "id_status_estagio")
	private int id;
	
	/** Descrição do Status do Estágio */
	private String descricao;

	/**
	 * Indica se o status permite realizar Parecer do estágio
	 */
	@Column(name = "permite_parecer")
	private boolean permiteParecer;
	
	/** Construtor Padrão */
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
