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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;


/**
 * Classe utilizada pelo Plano Individual do Docente - PID.
 *
 *	Representa os Tipos que uma Atividade complementar do PID pode ter.
 *  
 * @author agostinho campos
 * 
 */
@Entity
@Table(name="tipo_atividade_complementar", schema="pid")
public class TipoAtividadeComplementarPID implements  PersistDB {

	public static int ENSINO = 1;
	public static int PESQUISA_PROD_TEC_CIENT = 2;
	public static int EXTENSAO_OUTRAS_ATIVIDADES = 3;
	public static int ADMINISTRACAO = 4;
	public static int OUTRAS_ATIVIDADES = 5;
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
   	@Column(name = "id_tipo_atividade_complementar")
	private int id;
	
	private String denominacao;
	
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		TipoAtividadeComplementarPID other = (TipoAtividadeComplementarPID) obj;
		if (id != other.id)
			return false;
		return true;
	}
	public boolean isEnsino() {
		if (id == ENSINO)
			return true;
		else 
			return false;
	}
	
	public boolean isOutrasAtividades() {
		if (id == OUTRAS_ATIVIDADES)
			return true;
		else 
			return false;
	}
	
	public boolean isPesquisaProducaoTecnicaCient() {
		if (id == PESQUISA_PROD_TEC_CIENT)
			return true;
		else 
			return false;
	}
	
	public boolean isExtensaoOutrasAtividade() {
		if (id == EXTENSAO_OUTRAS_ATIVIDADES)
			return true;
		else 
			return false;
	}
	
	public boolean isAdministracao() {
		if (id == ADMINISTRACAO)
			return true;
		else 
			return false;
	}
}
