/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 06/04/2011
 */
package br.ufrn.sigaa.ensino.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Entidade que representa as Situa��es Poss�veis da reposi��o de avalia��o
 * 
 * @author arlindo
 *
 */
@Entity
@Table(name = "status_reposicao_avaliacao", schema = "ensino")
public class StatusReposicaoAvaliacao implements PersistDB  {
	
	/* Status que indica que a solicita��o est� cadastrada. */
	public static final int CADASTRADA = 1;
	/*  Status que indica que a solicita��o foi Deferida */
	public static final int DEFERIDO = 2;
	/*  Status que indica que a solicita��o foi Indeferida */
	public static final int INDEFERIDO = 3;
	/*  Status que indica que a solicita��o est� Cancelada */
	public static final int CANCELADA = 4;	

	/** Construtor padr�o */
	public StatusReposicaoAvaliacao() {

	}
	/** Contrutor atribuindo o id */
	public StatusReposicaoAvaliacao(int id){
		this.id = id;
	}
	
	/** Chave prim�ria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="estagio.hibernate_sequence") })	
	@Column(name = "id_status_reposicao_avaliacao")
	private int id;
	
	/** Descri��o do Status */
	private String descricao;
	
	/**
	 * Indica se o status permite realizar Parecer
	 */
	@Column(name = "permite_parecer")
	private boolean permiteParecer;	

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
	
	@Transient
	public boolean isCadastrada(){
		return id == CADASTRADA;
	}	
	
	@Transient
	public boolean isDeferido(){
		return id == DEFERIDO;
	}	
	
	@Transient
	public boolean isIndeferido(){
		return id == INDEFERIDO;
	}
	
	@Transient
	public boolean isCancelado(){
		return id == CANCELADA;
	}

	public boolean isPermiteParecer() {
		return permiteParecer;
	}

	public void setPermiteParecer(boolean permiteParecer) {
		this.permiteParecer = permiteParecer;
	}
}
