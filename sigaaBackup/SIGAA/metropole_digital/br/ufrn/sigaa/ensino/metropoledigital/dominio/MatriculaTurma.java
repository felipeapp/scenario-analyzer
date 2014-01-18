package br.ufrn.sigaa.ensino.metropoledigital.dominio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;

/**
 * Entidade que irá armazenar as matriculas dos estudantes nas turmas e a sua
 * situação final.
 * 
 * @author Gleydson
 * @author Rafael Silva
 * 
 */

@Entity
@Table(name="matricula_turma", schema="metropole_digital")
public class MatriculaTurma implements PersistDB{
	/**id da entidade*/
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="metropole_digital.matricula_turma_id_matricula_turma_seq") })
	@Column(name="id_matricula_turma")
	private int id;
	
	/**Discente matricula na turma*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_discente_tecnico")
	private DiscenteTecnico discente = new DiscenteTecnico();
	
	/**Turma ao qual o discente está vinculado*/
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_turma_entrada")
	private TurmaEntradaTecnico turmaEntrada;
	
	/**Carga horário não frequentada do aluno no módulo*/
	@Column(name="chnf")
	private Long chnf;
	
	/**Nota geral do aluno, no módulo, antes da recuparação*/
	@Column(name="nota_parcial")
	private Double notaParcial;
	
	/**Nota final do aluno no módulo, após a recuperação*/	
	@Column(name="nota_final")
	private Double notaFinal;	
	
	/**Situação do Aluno no módulo*/
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_situacao_matricula_turma")
	private SituacaoMatriculaTurma situacaoMatriculaTurma = new SituacaoMatriculaTurma();
	
	/**Lista das matriculas componente do aluno vinculadas a turma*/
	@Transient
	private HashMap<Integer, MatriculaComponente> matriculasComponentes = new HashMap<Integer, MatriculaComponente>(); 
	
	/**
	 * Construtor da Classe
	 */
	public MatriculaTurma() {
		
	}
	
	/**
	 * Retorna a média o aluno na turma.
	 * 
	 * @return
	 */
	public Double getMediaCalculada(List<NotaIMD> notas){
		Double somaNotas = 0.0;
		Integer chTotal = 0;
		for (NotaIMD nota: notas) {
			if (nota.getMediaCalculada()!=null) {
				somaNotas = somaNotas + nota.getMediaCalculada() * nota.getCargaHoriaDisciplina();
				chTotal = chTotal + nota.getCargaHoriaDisciplina();
			}else{
				return null;
			}
		}
		
		if (chTotal != 0) {
			return somaNotas / chTotal;
		}
		return null;		
	}
	
	//GETTERS AND SETTERS
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DiscenteTecnico getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteTecnico discente) {
		this.discente = discente;
	}

	public TurmaEntradaTecnico getTurmaEntrada() {
		return turmaEntrada;
	}

	public void setTurmaEntrada(TurmaEntradaTecnico turmaEntrada) {
		this.turmaEntrada = turmaEntrada;
	}

	public SituacaoMatriculaTurma getSituacaoMatriculaTurma() {
		return situacaoMatriculaTurma;
	}

	public void setSituacaoMatriculaTurma(
			SituacaoMatriculaTurma situacaoMatriculaTurma) {
		this.situacaoMatriculaTurma = situacaoMatriculaTurma;
	}

	public TurmaEntradaTecnico getTurma() {
		return turmaEntrada;
	}

	public void setTurma(TurmaEntradaTecnico turma) {
		this.turmaEntrada = turma;
	}

	public Long getChnf() {
		return chnf;
	}

	public void setChnf(Long chnf) {
		this.chnf = chnf;
	}

	public Double getNotaParcial() {
		return notaParcial;
	}

	public void setNotaParcial(Double notaParcial) {
		this.notaParcial = notaParcial;
	}

	public Double getNotaFinal() {
		return notaFinal;
	}

	public void setNotaFinal(Double notaFinal) {
		this.notaFinal = notaFinal;
	}

	public HashMap<Integer, MatriculaComponente> getMatriculasComponentes() {
		return matriculasComponentes;
	}

	public void setMatriculasComponentes(
			HashMap<Integer, MatriculaComponente> matriculasComponentes) {
		this.matriculasComponentes = matriculasComponentes;
	}
}