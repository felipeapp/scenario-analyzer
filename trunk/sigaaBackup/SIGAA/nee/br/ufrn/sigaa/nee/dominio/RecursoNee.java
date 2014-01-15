/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 29/01/2013
 *
 */
package br.ufrn.sigaa.nee.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.apedagogica.dominio.ParticipanteAtividadeAtualizacaoPedagogica;

/**
 * Classe que representa um recurso nee solicitado.
 * @author Diego Jácome
 */
@Entity
@Table(name="recurso_nee", schema="nee")
public class RecursoNee implements PersistDB {

	/** Chave primária. */
	@Id
	@Column(name = "id_recurso_nee")
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
		           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	/**
	 * Tipo do Recurso Nee
	 */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_tipo_recurso_nee", unique = false, nullable = true, insertable = true, updatable = true)
	private TipoRecursoNee tipoRecursoNee;
	
	/** Participante que solicitou o recurso */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name="id_participante_atividade_atualizacao_pedagogica")
	private ParticipanteAtividadeAtualizacaoPedagogica participante;
	
	/**
	 * Complemento da descrição do tipo de recurso "Outros"
	 */
	private String outros;
	
	/**
	 * Número de solicitações para o recurso
	 */
	@Transient
	private Integer solicitacoes;
	

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setTipoRecursoNee(TipoRecursoNee tipoRecursoNee) {
		this.tipoRecursoNee = tipoRecursoNee;
	}

	public TipoRecursoNee getTipoRecursoNee() {
		return tipoRecursoNee;
	}

	public void setParticipante(ParticipanteAtividadeAtualizacaoPedagogica participante) {
		this.participante = participante;
	}

	public ParticipanteAtividadeAtualizacaoPedagogica getParticipante() {
		return participante;
	}

	public void setOutros(String outros) {
		this.outros = outros;
	}

	public String getOutros() {
		return outros;
	}

	public void setSolicitacoes(Integer solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	public boolean isTipoOutros () {
		return tipoRecursoNee != null && tipoRecursoNee.getId()==TipoRecursoNee.OUTROS ? true : false;
	}
	
	public Integer getSolicitacoes() {
		return solicitacoes;
	}
}
