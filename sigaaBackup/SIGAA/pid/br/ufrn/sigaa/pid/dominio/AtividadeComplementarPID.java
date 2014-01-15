/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criada em: 03/11/2009
 *
 */

package br.ufrn.sigaa.pid.dominio;

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


/**
 * Classe utilizada pelo Plano Individual do Docente - PID.
 * 
 * Representa as Atividades Complementares, as atividades 
 * podem ser do tipo:
 *  
 * Ensino;
 * Pesquisa e Produção Técnico-Científica;
 * Extensão e outras atividades técnicas;
 * Administração;  
 *  
 * @author agostinho campos
 * 
 */
@Entity
@Table(name="atividade_complementar", schema="pid")
public class AtividadeComplementarPID implements PersistDB {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name="id_atividade_complementar")
	private int id;
	
	private String denominacao;

	/**
	 * Ensino
	 * Pesquisa e Produção Técnico-Científica
	 * Extensão e outras atividades técnicas
	 * Administração
	 * Outras Atividades
	 * 
	 */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_tipo_atividade")
	private TipoAtividadeComplementarPID tipoAtividadeComplementar;
	
	/**
	 * Se o checkbox foi selecionado
	 */
	@Transient
	private boolean selecionada;
	
	/**
	 * Observação opcional que o docente pode fazer.
	 */
	@Transient
	private String observacao;
	
	/**
	 * Usado para exibir o textarea apenas nos itens que foram indicados
	 * para tal.
	 */
	@Transient
	private boolean marcadoObservacao; 

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((denominacao == null) ? 0 : denominacao.hashCode());
		result = prime * result + id;
		result = prime
				* result
				+ ((tipoAtividadeComplementar == null) ? 0
						: tipoAtividadeComplementar.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AtividadeComplementarPID other = (AtividadeComplementarPID) obj;
		if (denominacao == null) {
			if (other.denominacao != null)
				return false;
		} else if (!denominacao.equals(other.denominacao))
			return false;
		if (id != other.id)
			return false;
		if (tipoAtividadeComplementar == null) {
			if (other.tipoAtividadeComplementar != null)
				return false;
		} else if (!tipoAtividadeComplementar
				.equals(other.tipoAtividadeComplementar))
			return false;
		return true;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDenominacao() {
		return denominacao;
	}

	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}

	public TipoAtividadeComplementarPID getTipoAtividadeComplementar() {
		return tipoAtividadeComplementar;
	}

	public void setTipoAtividadeComplementar(
			TipoAtividadeComplementarPID tipoAtividadeComplementar) {
		this.tipoAtividadeComplementar = tipoAtividadeComplementar;
	}
	
	public boolean isSelecionada() {
		return selecionada;
	}

	public void setSelecionada(boolean selecionada) {
		this.selecionada = selecionada;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public boolean isMarcadoObservacao() {
		return marcadoObservacao;
	}

	public void setMarcadoObservacao(boolean marcadoObservacao) {
		this.marcadoObservacao = marcadoObservacao;
	}

}
