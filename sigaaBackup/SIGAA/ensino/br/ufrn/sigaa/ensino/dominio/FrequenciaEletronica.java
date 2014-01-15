/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 27/08/2009
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Entidade que representa as chamadas realizadas através de biometria pelos 
 * alunos que estão usando o aplicativo desktop em sala de aula.
 * 
 * @author Agostinho
 *
 */
@Entity
@Table(name = "frequencia_eletronica", schema = "ensino")
public class FrequenciaEletronica implements PersistDB {
	
		@Id
		@GeneratedValue(generator="seqGenerator")
		@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
		           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
		@Column(name = "id_frequencia_eletronica")
		private int id;

		@Column(name="data_abertura")
		private Date dataHoraAbertura;
		
		@Column(name="data_fechamento")
		private Date dataHoraFechamento;
		
		@ManyToOne
		@JoinColumn(name="id_turma")
		private Turma turma;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public Date getDataHoraAbertura() {
			return dataHoraAbertura;
		}

		public void setDataHoraAbertura(Date dataHoraAbertura) {
			this.dataHoraAbertura = dataHoraAbertura;
		}

		public Date getDataHoraFechamento() {
			return dataHoraFechamento;
		}

		public void setDataHoraFechamento(Date dataHoraFechamento) {
			this.dataHoraFechamento = dataHoraFechamento;
		}

		public Turma getTurma() {
			return turma;
		}

		public void setTurma(Turma turma) {
			this.turma = turma;
		}
}
