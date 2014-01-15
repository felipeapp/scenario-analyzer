/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '01/03/2007'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Classe que efetua o registro de alterações efetuadas durante a criação de turma
 * nos docentes ministrantes das disciplinas pré-aprovadas na proposta do curso
 * de lato sensu.
 * 
 * @author Leonardo
 *
 */
@Entity
@Table(name = "registro_alteracao_lato", schema = "lato_sensu", uniqueConstraints = {})
public class RegistroAlteracaoLato implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_registro_alteracao_lato", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "id_componente_curricular")
	private ComponenteCurricular disciplina;
	
	@ManyToOne
	@JoinColumn(name = "id_turma")
	private Turma turma;
	
	@ManyToOne
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada;
	
	public RegistroAlteracaoLato(){
		
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return this.id;
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	public ComponenteCurricular getDisciplina() {
		return disciplina;
	}

	public void setDisciplina(ComponenteCurricular disciplina) {
		this.disciplina = disciplina;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		return null;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}
}
