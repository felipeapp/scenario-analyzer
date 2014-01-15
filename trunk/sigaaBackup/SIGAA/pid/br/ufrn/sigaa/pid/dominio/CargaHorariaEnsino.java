/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 29/10/2009
 *
 */
package br.ufrn.sigaa.pid.dominio;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;

/**
 * Classe utilizada pelo Plano Individual do Docente - PID.
 * Representa a CH de ensino cadastrada pelo Docente.
 *  
 * @author agostinho campos
 * 
 */

@Entity
@Table(name = "carga_horaria_ensino", schema = "pid")
public class CargaHorariaEnsino implements PersistDB, Validatable {

	/** Define a unicidade na base de dados	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
   	@Column(name = "id_carga_horaria_ensino")
	private int id;
	
	/** 
	 * Define a CHEnsino do PID com DocenteTurma 
	 * @see br.ufrn.sigaa.pid.dominio.ChEnsinoPIDocenteTurma
	 */
	@OneToMany(mappedBy="cargaHorariaEnsino",fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="id_carga_horaria_ensino")
	private List<ChEnsinoPIDocenteTurma> chEnsinoDocenteTurma = new ArrayList<ChEnsinoPIDocenteTurma>();
	
	/**
	 * Define a carga horária de atendimento.
	 */
	@Column(name="ch_atendimento_aluno")
	private Double chAtendimentoAluno = 0.0;
	
	/**
	 * Define a carga horária dada aos oreintandos de graduação.
	 */
	@Column(name="ch_atendimento_aluno_grad")
	private Double chOrientacoesAlunosGraduacao = 0.0;
	
	/** 
	 * Define a carga horária dada aos oreintandos de pós-graduação. 
	 */
	@Column(name="ch_atendimento_aluno_pos_grad")
	private Double chOrientacoesAlunosPosGraduacao = 0.0;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Adiciona na lista e seta a associação bidirecional 
	 * 
	 * @param chEnsinoDocente
	 * @param docenteTurma
	 */
	public void addCHEnsinoDocenteTurma(ChEnsinoPIDocenteTurma chEnsinoDocente, DocenteTurma docenteTurma) {
		chEnsinoDocente.setCargaHorariaEnsino(this);
		chEnsinoDocente.setDocenteTurma(docenteTurma);
		chEnsinoDocenteTurma.add(chEnsinoDocente);
	}
	
	public List<ChEnsinoPIDocenteTurma> getChEnsinoDocenteTurma() {
		return chEnsinoDocenteTurma;
	}

	public void setChEnsinoDocenteTurma(
			List<ChEnsinoPIDocenteTurma> chEnsinoDocenteTurma) {
		this.chEnsinoDocenteTurma = chEnsinoDocenteTurma;
	}

	public Double getChAtendimentoAluno() {
		return chAtendimentoAluno;
	}

	public void setChAtendimentoAluno(Double chAtendimentoAluno) {
		this.chAtendimentoAluno = chAtendimentoAluno;
	}
	
	public Double getChOrientacoesAlunosGraduacao() {
		return chOrientacoesAlunosGraduacao;
	}

	public void setChOrientacoesAlunosGraduacao(Double chOrientacoesAlunosGraduacao) {
		this.chOrientacoesAlunosGraduacao = chOrientacoesAlunosGraduacao;
	}

	public Double getChOrientacoesAlunosPosGraduacao() {
		return chOrientacoesAlunosPosGraduacao;
	}

	public void setChOrientacoesAlunosPosGraduacao(
			Double chOrientacoesAlunosPosGraduacao) {
		this.chOrientacoesAlunosPosGraduacao = chOrientacoesAlunosPosGraduacao;
	}

	/**
	 * Como existem várias disciplinas para cada professor, é calculado o valor do objeto atual 
	 * para exibir em um somatório.
	 * O valor retornado é o total do objeto atual. 
	 */
	@Transient
	public double calcularCHEnsino() {
		if (chAtendimentoAluno == null)
			chAtendimentoAluno = 0.0;
		if (chOrientacoesAlunosGraduacao == null)
			chOrientacoesAlunosGraduacao = 0.0;
		if (chOrientacoesAlunosPosGraduacao == null)
			chOrientacoesAlunosPosGraduacao = 0.0;
		return chAtendimentoAluno + chOrientacoesAlunosGraduacao + chOrientacoesAlunosPosGraduacao;
	}
	
	/**
	 * Valida o preenchimento de alguns campos da classe 
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		if (chAtendimentoAluno < 2 || chAtendimentoAluno > 4)
			lista.addErro("Carga horária de atendimento ao aluno deve ser no mínimo 2 e no máximo 4 horas semanais.");
		return lista;
	}
}
