/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
 * Created on 21/08/2013
*/
package br.ufrn.sigaa.ensino_rede.dominio;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * Entidade que representa as Situações Possíveis das solicitações de docentes do ensino em rede
 * 
 * @author Diego Jácome
 *
 */
@Entity
@Table(name = "status_solicitacao_docente_rede", schema = "ensino_rede")
public class StatusSolicitacaoDocenteRede {

	/* Status que indica que a solicitação está cadastrada. */
	public static final int AGUARDANDO_ANALISE = 1;
	/*  Status que indica que a solicitação foi Deferida */
	public static final int DEFERIDA = 2;
	/*  Status que indica que a solicitação foi Indeferida */
	public static final int INDEFERIDA = 3;
	/*  Status que indica que a solicitação está Cancelada */
	public static final int CANCELADA = 4;	
	
	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="estagio.hibernate_sequence") })	
	@Column(name = "id_status_solicitacao_docente_rede")
	private int id;
	
	/** Descrição do Status */
	private String descricao;
	
	/** Construtor padrão */
	public StatusSolicitacaoDocenteRede() {

	}
	
	/** Contrutor atribuindo o id */
	public StatusSolicitacaoDocenteRede(int id){
		this.setId(id);
	}

	/** Contrutor atribuindo o id e a descrição*/
	public StatusSolicitacaoDocenteRede(int id, String descricao){
		this.setId(id);
		this.setDescricao(descricao);
	}
	
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getDescricao() {
		return descricao;
	}
	
	/** Retorna uma descrição textual do Status informado.
	 * @param status
	 * @return
	 */
	public static String getDescricao(int status){
		switch (status) {
		case AGUARDANDO_ANALISE:    return "AGUARDANDO ANÁLISE";
		case DEFERIDA:       		 return "DEFERIDA";
		case INDEFERIDA:     		 return "INDEFERIDA";
		case CANCELADA:             return "CANCELADA";
		default:             		 return "DESCONHECIDO";
		}
	}
	
 	/** Retorna uma coleção com todos status para deferimento.
	 * @return
	 */
	public static Collection<StatusSolicitacaoDocenteRede> getStatusDeferimento(){
		ArrayList<StatusSolicitacaoDocenteRede> listaStatus = new ArrayList<StatusSolicitacaoDocenteRede>();
		listaStatus.add(new StatusSolicitacaoDocenteRede(DEFERIDA,getDescricao(DEFERIDA)));
		listaStatus.add(new StatusSolicitacaoDocenteRede(INDEFERIDA,getDescricao(INDEFERIDA)));
		return listaStatus;
	}
}
