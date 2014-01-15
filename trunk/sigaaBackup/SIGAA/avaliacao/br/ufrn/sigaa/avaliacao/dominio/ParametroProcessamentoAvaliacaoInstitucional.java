/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 02/03/2010
 * 
 */
package br.ufrn.sigaa.avaliacao.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.TurmaDocenciaAssistida;

/**
 * Par�metros utilizados no c�lculo das m�dias no processamento da Avalia��o
 * Institucional.<br>
 * Para o c�lculo das m�dias da Avalia��o Institucional, pode-se definir alguns
 * par�metros para o processamento:<br>
 * <ul>
 * <li>Excluir Avalia��es de Discentes com Reprova��es por falta: exclui da
 * m�dia as avalia��es de discentes que tiveram reprova��o por falta na turma.</li>
 * <li>Perguntas determinantes para exclus�o da avalia��o: perguntas que o
 * discente respondeu com zero ou N/A podem determinar se a avalia��o ser�
 * computada ou n�o, por exemplo, se o discente assistiu as aulas at� o final.</li>
 * <li>N�mero m�nimo de avalia��es por docente: determina o n�mero m�nimo de
 * avalia��es que um docente deve ter para que seja calculado a m�dia e o desvio
 * padr�o.</li>
 * </ul>
 * outras informa��es do processamento s�o armazenadas, tais como ano-per�odo
 * processado, hora inicial e final do processamento, e se a consulta do
 * resultado do processamento est� liberada para consulta pelo docente.
 * 
 * @author �dipo Elder F. Melo
 * 
 */
@Entity
@Table(name = "parametro_processamento_avaliacao", schema = "avaliacao")
public class ParametroProcessamentoAvaliacaoInstitucional implements PersistDB {

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="avaliacao.resultado_seq") })
	@Column(name = "id_parametro_processamento", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Data/Hora de in�cio do processamento. */
	@Column(name = "inicio_processamento")
	private Date inicioProcessamento;

	/** Data/Hora do fim do processamento. */
	@Column(name = "fim_processamento")
	private Date fimProcessamento;

	/** Ano da Avalia��o Institucional processado. */
	private int ano;
	
	/** Per�odo da Avalia��o Institucional processado. */
	private int periodo;

	/** Indica se a consulta ao resultado da avalia��o pelo docente foi liberada. */
	@Column(name = "consulta_docente")
	private boolean consultaDocenteLiberada = false;
	
	/** Indica se a consulta ao resultado da avalia��o pelo discente foi liberada. */
	@Column(name = "consulta_discente")
	private boolean consultaDiscenteLiberada = false;
	
	/** N�mero m�nimo de Avalia��es que um docente deve ter para computar as m�dias das notas. */
	@Column(name = "numero_min_avaliacoes")
	private int numMinAvaliacoes;
	
	/** Indica se deve excluir Avalia��es de discentes que foram reprovados por falta. */
	@Column(name = "excluir_reprovacoes_falta")
	private boolean excluirRepovacoesFalta;
	
	/** Cole��o de Avalia��es de DocenteTurma invalidados durante o processamento. */
	@OneToMany(fetch = FetchType.LAZY, mappedBy="parametro")
	@JoinColumn(name = "id_parametro_processamento")
	private Collection<AvaliacaoDocenteTurmaInvalida> avaliacoesDocenteTurmaInvalidas;
	
	/** Cole��o de Docentes em que nenhuma Avalia��o Institucional foi contabilizada. */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name="docente_turma_invalido", schema="avaliacao",
			joinColumns=@JoinColumn(name="id_parametro_processamento"),  
			inverseJoinColumns=@JoinColumn(name="id_docente_turma"))
	private Collection<DocenteTurma> docentesInvalidos;
	
	/** Cole��o de Turmas de Doc�ncia Assistida em que nenhuma Avalia��o Institucional foi contabilizada. */
	@ManyToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="docente_turma_assistida_invalida", schema="avaliacao",
			joinColumns=@JoinColumn(name="id_parametro_processamento"),  
			inverseJoinColumns=@JoinColumn(name="id_turma_docencia_asssistida"))
	private Collection<TurmaDocenciaAssistida> turmaDocenciaAssistidaInvalidas;
	
	/**
	 * Cole��o de perguntas que determinam se a Avalia��o ser� considerada ou
	 * n�o no c�lculo das notas do docente. Caso o discente tenha dado nota zero
	 * ou N/A para a pergunta da cole��o, na Avalia��o, esta ser� ignorada no
	 * c�lculo.
	 */ 
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name="pergunta_exclusao_processamento", schema="avaliacao",
			joinColumns=@JoinColumn(name="id_parametro_processamento"),  
			inverseJoinColumns=@JoinColumn(name="id_pergunta"))
	private Collection<Pergunta> perguntaDeterminanteExclusaoAvaliacao;
	
	/** Formul�rio da Avalia��o Institucional ao qual estes parametros foram aplicados ao processamento das notas. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_formulario_avaliacao")
	private FormularioAvaliacaoInstitucional formulario;
	
	public ParametroProcessamentoAvaliacaoInstitucional() {
		super();
		formulario = new FormularioAvaliacaoInstitucional();
	}

	public ParametroProcessamentoAvaliacaoInstitucional(int id) {
		this();
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getInicioProcessamento() {
		return inicioProcessamento;
	}

	public void setInicioProcessamento(Date inicioProcessamento) {
		this.inicioProcessamento = inicioProcessamento;
	}

	public Date getFimProcessamento() {
		return fimProcessamento;
	}

	public void setFimProcessamento(Date fimProcessamento) {
		this.fimProcessamento = fimProcessamento;
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

	public boolean isConsultaDocenteLiberada() {
		return consultaDocenteLiberada;
	}

	public void setConsultaDocenteLiberada(boolean consultaDocenteLiberada) {
		this.consultaDocenteLiberada = consultaDocenteLiberada;
	}

	public int getNumMinAvaliacoes() {
		return numMinAvaliacoes;
	}

	public void setNumMinAvaliacoes(int numMinAvaliacoes) {
		this.numMinAvaliacoes = numMinAvaliacoes;
	}

	public Collection<Pergunta> getPerguntaDeterminanteExclusaoAvaliacao() {
		return perguntaDeterminanteExclusaoAvaliacao;
	}
	
	/**
	 * Retorna uma lista de perguntas que determinam se a avalia��o ser�
	 * exclu�da do processamento, caso tenha valor zero ou n�o tenham valor v�lido.
	 * 
	 * @return
	 */
	public Collection<Integer> getListaIdPerguntaDeterminanteExclusaoAvaliacao() {
		Collection<Integer> lista = new ArrayList<Integer>();
		for (Pergunta p : perguntaDeterminanteExclusaoAvaliacao)
			lista.add(p.getId());
		return lista;
	}

	public void setPerguntaDeterminanteExclusaoAvaliacao(
			Collection<Pergunta> perguntaDeterminanteExclusaoAvaliacao) {
		this.perguntaDeterminanteExclusaoAvaliacao = perguntaDeterminanteExclusaoAvaliacao;
	}

	public boolean isExcluirRepovacoesFalta() {
		return excluirRepovacoesFalta;
	}

	public void setExcluirRepovacoesFalta(boolean excluirRepovacoesFalta) {
		this.excluirRepovacoesFalta = excluirRepovacoesFalta;
	}

	public Collection<AvaliacaoDocenteTurmaInvalida> getAvaliacoesDocenteTurmaInvalidas() {
		return avaliacoesDocenteTurmaInvalidas;
	}

	public void setAvaliacoesDocenteTurmaInvalidas(Collection<AvaliacaoDocenteTurmaInvalida> avaliacoesDocenteTurmaInvalidas) {
		this.avaliacoesDocenteTurmaInvalidas = avaliacoesDocenteTurmaInvalidas;
	}

	public Collection<DocenteTurma> getDocentesInvalidos() {
		return docentesInvalidos;
	}

	public void setDocentesInvalidos(Collection<DocenteTurma> docentesInvalidos) {
		this.docentesInvalidos = docentesInvalidos;
	}

	/**
	 * Retorna uma lista de avali��es que foram
	 * exclu�das do processamento, caso tenha valor zero ou n�o tenham valor v�lido.
	 * 
	 * @param docenteTurma
	 * @return
	 */
	public Collection<Integer> getListaIdAvaliacaoInvalida(DocenteTurma docenteTurma) {
		Collection<Integer> lista = new ArrayList<Integer>();
		for (AvaliacaoDocenteTurmaInvalida invalida : avaliacoesDocenteTurmaInvalidas) {
			if (invalida.getIdDocenteTurma() == docenteTurma.getId())
				lista.add(invalida.getIdAvaliacao());
		}
		return lista;
	}

	/** Indica se o docente da turma � v�lido ou n�o.
	 * @param idDocenteTurma
	 * @return
	 */
	public boolean isDocenteInvalido(int idDocenteTurma) {
		for (DocenteTurma invalido : docentesInvalidos) {
			if (invalido.getId() == idDocenteTurma)
				return true;
		}
		return false;
	}
	
	/** Indica se a turma doc�ncia assistida � v�lida ou n�o.
	 * @param idDocenteTurma
	 * @return
	 */
	public boolean isTurmaDocenciaAssistidaInvalida(int idTurmaDocenciaAssistida) {
		for (TurmaDocenciaAssistida invalido : turmaDocenciaAssistidaInvalidas) {
			if (invalido.getId() == idTurmaDocenciaAssistida)
				return true;
		}
		return false;
	}

	/** Indica se a avalia��o do docente da turma � inv�lida, ou n�o.
	 * @param idAvaliacao
	 * @param idDocenteTurma
	 * @return
	 */
	public boolean isAvaliacaoDocenteTurmaInvalida(int idAvaliacao, int idDocenteTurma) {
		boolean docenteInvalido = isDocenteInvalido(idDocenteTurma);
		if (docenteInvalido)
			return true;
		for (AvaliacaoDocenteTurmaInvalida aval : avaliacoesDocenteTurmaInvalidas)
			if (aval.getIdAvaliacao() == idAvaliacao && aval.getIdDocenteTurma() == idDocenteTurma)
				return true;
		return false;
	}
	
	/** Indica se a avalia��o do docente da turma ou turma de doc�ncia assistida � inv�lida, ou n�o.
	 * @param id
	 * @return
	 */
	public boolean isAvaliacaoInvalida(int id) {
		if (formulario.isAvaliacaoDocenciaAssistida())
			return isTurmaDocenciaAssistidaInvalida(id);
		else
			return isDocenteInvalido(id);
	}

	public boolean isConsultaDiscenteLiberada() {
		return consultaDiscenteLiberada;
	}

	public void setConsultaDiscenteLiberada(boolean consultaDiscenteLiberada) {
		this.consultaDiscenteLiberada = consultaDiscenteLiberada;
	}

	public Collection<TurmaDocenciaAssistida> getTurmaDocenciaAssistidaInvalidas() {
		return turmaDocenciaAssistidaInvalidas;
	}

	public void setTurmaDocenciaAssistidaInvalidas(
			Collection<TurmaDocenciaAssistida> turmaDocenciaAssistidaInvalidas) {
		this.turmaDocenciaAssistidaInvalidas = turmaDocenciaAssistidaInvalidas;
	}

	public FormularioAvaliacaoInstitucional getFormulario() {
		return formulario;
	}

	public void setFormulario(FormularioAvaliacaoInstitucional formulario) {
		this.formulario = formulario;
	}

}
