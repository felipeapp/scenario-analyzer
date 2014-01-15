/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 28/05/2010
 */
package br.ufrn.sigaa.ava.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Entidade que relaciona uma estação de chamada biométrica turmas. 
 * Cada estação biométrica pode atender várias turmas.
 *  
 * @author agostinho campos
 */
@Entity
@Table(name="estacao_biometrica_turma", schema="ava")
public class EstacaoChamadaBiometricaTurma {
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name="id_estacao_chamada_turma")
	private int id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_estacao_chamada_biometrica")
	private EstacaoChamadaBiometrica estacaoChamadaBiometrica;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_turma")
	private Turma turma;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public EstacaoChamadaBiometrica getEstacaoChamadaBiometrica() {
		return estacaoChamadaBiometrica;
	}
	public void setEstacaoChamadaBiometrica(
			EstacaoChamadaBiometrica estacaoChamadaBiometrica) {
		this.estacaoChamadaBiometrica = estacaoChamadaBiometrica;
	}

	public Turma getTurma() {
		return turma;
	}
	public void setTurma(Turma turma) {
		this.turma = turma;
	}
}
