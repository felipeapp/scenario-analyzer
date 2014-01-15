/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 03/02/2010
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
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 * 	O docente pode dedicar o seu tempo para "Atividades de Ensino" ou "Outras Atividades".
 * 
 *  O campo "Outras Atividades" se refere a atividades desenvolvidas em Cursos de Graduação e 
 *  pós-graduação e/ou outros projetos institucionais com remuneração específica, mediante 
 *  autorização do CONSEPE.
 * 
 * @author agostinho campos
 */
@Entity
@Table(name = "carga_horaria_outras_atividades", schema = "pid")
public class CargaHorariaOutrasAtividades implements PersistDB, Validatable {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
   	@Column(name = "id_carga_horaria_outras_atividades")
	private int id;
	
	/**
	 * Carga Horária para outras atividades de ensino
	 */
	@Column(name="ch_outras_ativ_ensino")
	private double chSemanalOutrasAtividadesEnsino;
	
	/**
	 * Valor em percentual do tempo dedicado para outras atividades de ensino
	 */
	@Column(name="percentual_outras_ativ_ensino")
	private Integer percentualOutrasAtividadesEnsino = 0;
	
	/**
	 * Carga Horária para Outras Atividades
	 */
	@Column(name="ch_outras_ativ")
	private double chSemanalOutrasAtividades;
	
	/**
	 * Valor em percentual do tempo dedicado para Outras Atividades
	 */
	@Column(name="percentual_outras_ativ")
	private Integer percentualOutrasAtividades = 0;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the chSemanalOutrasAtividadesEnsino
	 */
	public double getChSemanalOutrasAtividadesEnsino() {
		return chSemanalOutrasAtividadesEnsino;
	}

	/**
	 * @param chSemanalOutrasAtividadesEnsino the chSemanalOutrasAtividadesEnsino to set
	 */
	public void setChSemanalOutrasAtividadesEnsino(
			double chSemanalOutrasAtividadesEnsino) {
		this.chSemanalOutrasAtividadesEnsino = chSemanalOutrasAtividadesEnsino;
	}

	/**
	 * @return the chSemanalOutrasAtividades
	 */
	public double getChSemanalOutrasAtividades() {
		return chSemanalOutrasAtividades;
	}

	/**
	 * @param chSemanalOutrasAtividades the chSemanalOutrasAtividades to set
	 */
	public void setChSemanalOutrasAtividades(double chSemanalOutrasAtividades) {
		this.chSemanalOutrasAtividades = chSemanalOutrasAtividades;
	}

	public Integer getPercentualOutrasAtividadesEnsino() {
		return percentualOutrasAtividadesEnsino;
	}

	public void setPercentualOutrasAtividadesEnsino(
			Integer percentualOutrasAtividadesEnsino) {
		this.percentualOutrasAtividadesEnsino = percentualOutrasAtividadesEnsino;
	}

	public Integer getPercentualOutrasAtividades() {
		return percentualOutrasAtividades;
	}

	public void setPercentualOutrasAtividades(Integer percentualOutrasAtividades) {
		this.percentualOutrasAtividades = percentualOutrasAtividades;
	}

	public ListaMensagens validate() {
		return null;
	}
	
}
