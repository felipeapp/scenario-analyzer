/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 20/11/2012
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Define um plano para matrícula de turmas em lote. Este plano é utilizado, por
 * exemplo, para matricular um discente recém-cadastrado (ingressante) em um
 * conjunto de turmas do primeiro período.
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
@Entity
@Table(schema = "ensino", name = "plano_matricula_ingressantes")
public class PlanoMatriculaIngressantes implements Validatable {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_plano_matricula_ingressantes")
	private int id;
	
	/** Descrição do plano de matrícula. */
	@Column(unique=true, nullable=false)
	private String descricao;
	
	/** Turmas deste plano. */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name="plano_matricula_turma", schema="ensino",
			joinColumns=@JoinColumn(name="id_plano_matricula_ingressantes"),  
			inverseJoinColumns=@JoinColumn(name="id_turma"))
	private Collection<Turma> turmas;
	
	/** Nível de ensino atendido por este plano de ensino. */
	@Column(nullable=false)
	private char nivel;
	
	/** Matriz Curricular dos discentes de graduação que poderão utilizar este plano para matrícula em lote. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_matriz_curricular")
	private MatrizCurricular matrizCurricular;
	
	/** Curso dos discentes que poderão utilizar este plano para matrícula em lote. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_curso")
	private Curso curso;
	
	/** Discentes atentidos por este plano */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name="plano_matricula_discentes", schema="ensino",
			joinColumns=@JoinColumn(name="id_plano_matricula_ingressantes"),  
			inverseJoinColumns=@JoinColumn(name="id_discente"))
	private Collection<Discente> discentesAtendidos;
	
	/** Ano em que este plano deverá ser utilizado. */
	@Column(nullable=false)
	private int ano;
	
	/** Período em que este plano deverá ser utilizado. */
	@Column(nullable=false)
	private int periodo;
	
	/** Informa quantos discentes ativos poderão ser atendidos por este plano. */
	@Column(nullable=false)
	private int capacidade;
	
	/** Usuário que cadastrou este plano de matrícula. */
	@CriadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_registro_criado_por")
	private RegistroEntrada criadoPor;
	
	/** Usuário que atualizou este plano de matrícula. */
	@AtualizadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_registro_atualizado_por")
	private RegistroEntrada atualizadoPor;
	
	/** Construtor Padrão. */
	public PlanoMatriculaIngressantes() {
		matrizCurricular = new MatrizCurricular();
		turmas = new LinkedList<Turma>();
		discentesAtendidos = new LinkedList<Discente>();
		curso = new Curso();
	}

	/** Construtor parametrizado
	 * @param id
	 */
	public PlanoMatriculaIngressantes(Integer id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Collection<Turma> getTurmas() {
		return turmas;
	}

	public void setTurmas(Collection<Turma> turmas) {
		this.turmas = turmas;
	}

	public MatrizCurricular getMatrizCurricular() {
		return matrizCurricular;
	}

	public void setMatrizCurricular(MatrizCurricular matrizCurricular) {
		this.matrizCurricular = matrizCurricular;
	}
	
	/** Adiciona uma turma ao plano de matrículas.
	 * @param turma
	 */
	public void addTurma(Turma turma) {
		if (turmas == null)
			turmas = new LinkedList<Turma>();
		turmas.add(turma);
	}
	
	public Collection<Discente> getDiscentesAtendidos() {
		return discentesAtendidos;
	}
	
	public void setDiscentesAtendidos(Collection<Discente> discentesAtendidos) {
		this.discentesAtendidos = discentesAtendidos;
	}
	
	/** Retorna a quantidade de discentes ativos que foram atendidos por este plano.
	 * @return
	 */
	public int getQtdDiscentesAtivos() {
		if (isEmpty(discentesAtendidos)) return 0;
		int qtd = 0;
		for (Discente discente : discentesAtendidos)
			if (StatusDiscente.isAtivo(discente.getStatus()))
				qtd++;
		return qtd;
	}
	
	/**
	 * Retorna os componentes que estão no plano
	 */
	@Transient
	public List<ComponenteCurricular> getDisciplinas() {
		List<ComponenteCurricular> componentes = new ArrayList<ComponenteCurricular>();
		
		if (getTurmas() != null) {
			for (Turma t : getTurmas()) 
				componentes.add(t.getDisciplina());
			
		}
		
		return componentes;
	}
	
	/** Retorna a quantidade de discentes, ativos ou não, que foram atendidos por este plano.
	 * @return
	 */
	public int getQtdDiscentesAtendidos() {
		if (isEmpty(discentesAtendidos)) return 0;
		else return discentesAtendidos.size();
	}

	public char getNivel() {
		return nivel;
	}

	public void setNivel(char nivel) {
		this.nivel = nivel;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}
	
	/** Retorna uma representação textual deste plano de Matrícula em Lote
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return descricao + " - " + matrizCurricular.toString();
	}
	
	/** Valida as informações do Plano de Matrícula em Lote
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		validateRequired(descricao, "Descrição", lista);
		validateRequired(ano, "ano", lista);
		validateRequired(periodo, "Período", lista);
		if (NivelEnsino.GRADUACAO == nivel)
			validateRequired(matrizCurricular, "Matriz Curricular", lista);
		else
			validateRequired(curso, "Curso", lista);
		validateRequired(turmas, "Turmas", lista);
		return lista;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}
	
	/** Indica se este plano de matrícula em lote é para turmas de graduação.
	 * @return
	 */
	public boolean isGraduacao() {
		return NivelEnsino.GRADUACAO == nivel;
	}

	public int getCapacidade() {
		return capacidade;
	}

	public void setCapacidade(int capacidade) {
		this.capacidade = capacidade;
	}

	public RegistroEntrada getCriadoPor() {
		return criadoPor;
	}

	public void setCriadoPor(RegistroEntrada criadoPor) {
		this.criadoPor = criadoPor;
	}

	public RegistroEntrada getAtualizadoPor() {
		return atualizadoPor;
	}

	public void setAtualizadoPor(RegistroEntrada atualizadoPor) {
		this.atualizadoPor = atualizadoPor;
	}

	/** Indica que o plano de matrícula possui vagas, ou seja, ainda pode ser utilizado para matricular um discente.
	 * @return
	 */
	public boolean hasVagas() {
		return getVagas() > 0;
	}
	
	/** Adiciona um discente à lista de discentes atendidos por este plano.
	 * @param discente
	 */
	public void addDiscente(Discente discente) {
		if (discentesAtendidos == null)
			discentesAtendidos = new LinkedList<Discente>();
		discentesAtendidos.add(discente);
	}

	/** Retorna o número de vagas disponíveis no plano, isto é, a diferença entre capacidade e discentes ATIVOS atendidos.
	 * @return
	 */
	public int getVagas() {
		return capacidade - getQtdDiscentesAtivos();
	}

	/** Retorna uma descrição da turma com horários que constam do plano de matrícula
	 * @return
	 */
	public String getTurmasHorarios() {
		StringBuilder str = new StringBuilder();
		for (Turma turma : turmas) {
			if (str.length() > 0) str.append(", ");
			str.append(turma.getNome()).append("/").append(turma.getDescricaoHorario());
		}
  		return str.toString();		
	}
	
}
