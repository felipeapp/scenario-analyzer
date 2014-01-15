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
 * Representa os Tipos de Orientação do PID. Basicamente representa
 * orientações de graduação e de pós-graduação que um docente esteja
 * envolvido.
 *  
 * @author agostinho campos
 * 
 */

@Entity
@Table(name="tipo_orientacao_pid", schema="pid")
public class TipoOrientacaoPID implements PersistDB {
	
	/**
	 * Trabalho final de Graduação
	 */
	public static int GRADUACAO = 7;
	
	/**
	 * Pós-Graduação
	 */
	public static int POS_GRADUACAO = 8; 
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
   	@Column(name = "id_tipo_orientacao_pid")
	private int id;
	
	private String denominacao;
	
	public boolean isGraduacao() {
		if (id == GRADUACAO)
			return true;
		else
			return false;
	}
	
	public boolean isPosGraduacao() {
		if (id == POS_GRADUACAO)
			return true;
		else
			return false;
	}
	
	public String getDenominacao() {
		return denominacao;
	}

	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}