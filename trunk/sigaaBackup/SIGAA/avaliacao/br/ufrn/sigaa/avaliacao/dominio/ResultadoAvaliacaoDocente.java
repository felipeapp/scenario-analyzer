/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 30/07/2009
 * 
 */
package br.ufrn.sigaa.avaliacao.dominio;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.TurmaDocenciaAssistida;

/**
 * Resultado do Processamento das respostas da Avalia��o Institucional para um
 * Docente de uma Turma.
 * 
 * @author �dipo Elder F. Melo
 * 
 */
@Entity 
@Table(schema="avaliacao", name="resultado_avaliacao_docente")
public class ResultadoAvaliacaoDocente implements PersistDB, Comparable<ResultadoAvaliacaoDocente>{
	
	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="avaliacao.resultado_seq") })
	@Column(name = "id_resultado_avaliacao_docente", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Ano ao qual o resultado se refere. */
	private int ano;
	
	/** Per�odo ao qual o resultado se refere. */
	private int periodo;
	
	/** DocenteTurma avaliado. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_docente_turma")
	private DocenteTurma docenteTurma;
	
	/** Turma de doc�ncia assistida ao qual esta resposta se refere. */
	@ManyToOne  (fetch=FetchType.EAGER) 
	@JoinColumn(name = "id_turma_docencia_assistida")
	private TurmaDocenciaAssistida turmaDocenciaAssistida;

	/** N�mero de discentes na turma. */
	@Column(name="num_discentes")
	private int numDiscentes;
	
	/** N�mero de discentes com trancamentos na turma. */
	@Column(name="num_trancamentos")
	private int numTrancamentos;
	
	/** Cole��o das m�dias das notas nas perguntas da Avalia��o Institucional. */
	@OneToMany(cascade = { CascadeType.ALL }, mappedBy="resultadoAvaliacaoDocente")
	@JoinColumn(name = "id_resultado_avaliacao_docente",nullable=false)
	private Collection<MediaNotas> mediaNotas;
	
	/** Cole��o de Percentuais de respostas SIM/N�O das perguntas da Avalia��o Institucional.  */
	@OneToMany(cascade = { CascadeType.ALL}, mappedBy="resultadoAvaliacaoDocente")
	@JoinColumn(name = "id_resultado_avaliacao_docente")
	private Collection<PercentualSimNao> percentualRespostasSimNao;
	
	/** M�dia geral das notas em todas as perguntas da Avalia��o Institucional. */ 
	@Column(name="media_geral")
	private Double mediaGeral;
	
	/** Desvio padr�o geral das notas em todas as perguntas da Avalia��o Institucional. */
	@Column(name="desvio_padrao_geral")
	private Double desvioPadraoGeral;
	
	/** Formul�rio de avalia��o ao qual este resultado refere-se. */
	@ManyToOne  (fetch=FetchType.EAGER) 
	@JoinColumn(name = "id_formulario_avaliacao")
	private FormularioAvaliacaoInstitucional formularioAvaliacaoInstitucional;

	/** Construtor padr�o. */
	public ResultadoAvaliacaoDocente() {
	}
	
	/** Construtor parametrizado.
	 * @param id
	 */
	public ResultadoAvaliacaoDocente(int id) {
		this();
		setId(id);
	}

	/** Retorna a chave prim�ria. 
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	@Override
	public int getId() {
		return id;
	}

	/** Seta a chave prim�ria. 
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	@Override
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna o DocenteTurma avaliado. 
	 * @return DocenteTurma avaliado. 
	 */
	public DocenteTurma getDocenteTurma() {
		return docenteTurma;
	}

	/** Seta o DocenteTurma avaliado.   
	 * @param docenteTurma DocenteTurma avaliado. 
	 */
	public void setDocenteTurma(DocenteTurma docenteTurma) {
		this.docenteTurma = docenteTurma;
	}

	/** Retorna o n�mero de discentes na turma.  
	 * @return N�mero de discentes na turma. 
	 */
	public int getNumDiscentes() {
		return numDiscentes;
	}

	/** Seta o n�mero de discentes na turma.
	 * @param numDiscentes N�mero de discentes na turma. 
	 */
	public void setNumDiscentes(int numDiscentes) {
		this.numDiscentes = numDiscentes;
	}

	/** Retorna o n�mero de discentes com trancamentos na turma. 
	 * @return N�mero de discentes com trancamentos na turma. 
	 */
	public int getNumTrancamentos() {
		return numTrancamentos;
	}

	/** Retorna o n�mero de discentes com trancamentos na turma.
	 * @param numTrancamentos N�mero de discentes com trancamentos na turma. 
	 */
	public void setNumTrancamentos(int numTrancamentos) {
		this.numTrancamentos = numTrancamentos;
	}

	/** Retorna a cole��o das m�dias das notas nas perguntas da Avalia��o Institucional. 
	 * @return Cole��o das m�dias das notas nas perguntas da Avalia��o Institucional. 
	 */
	public Collection<MediaNotas> getMediaNotas() {
		return mediaNotas;
	}

	/** Seta a cole��o das m�dias das notas nas perguntas da Avalia��o Institucional. 
	 * @param mediaNotas Cole��o das m�dias das notas nas perguntas da Avalia��o Institucional. 
	 */
	public void setMediaNotas(Collection<MediaNotas> mediaNotas) {
		this.mediaNotas = mediaNotas;
	}

	/** Retorna a cole��o de Percentuais de respostas SIM/N�O das perguntas da Avalia��o Institucional. 
	 * @return Cole��o de Percentuais de respostas SIM/N�O das perguntas da Avalia��o Institucional. 
	 */
	public Collection<PercentualSimNao> getPercentualRespostasSimNao() {
		return percentualRespostasSimNao;
	}

	/** Seta a cole��o de Percentuais de respostas SIM/N�O das perguntas da Avalia��o Institucional. 
	 * @param percentualRespostasSimNao Cole��o de Percentuais de respostas SIM/N�O das perguntas da Avalia��o Institucional. 
	 */
	public void setPercentualRespostasSimNao(
			Collection<PercentualSimNao> percentualRespostasSimNao) {
		this.percentualRespostasSimNao = percentualRespostasSimNao;
	}

	/** Retorna a m�dia geral das notas em todas as perguntas da Avalia��o Institucional. 
	 * @return M�dia geral das notas em todas as perguntas da Avalia��o Institucional. 
	 */
	public Double getMediaGeral() {
		return mediaGeral;
	}

	/** Seta a m�dia geral das notas em todas as perguntas da Avalia��o Institucional. 
	 * @param mediaGeral M�dia geral das notas em todas as perguntas da Avalia��o Institucional. 
	 */
	public void setMediaGeral(Double mediaGeral) {
		this.mediaGeral = mediaGeral;
	}

	/** Retorna o desvio padr�o geral das notas em todas as perguntas da Avalia��o Institucional.
	 * @return Desvio padr�o geral das notas em todas as perguntas da Avalia��o Institucional. 
	 */
	public Double getDesvioPadraoGeral() {
		return desvioPadraoGeral;
	}

	/** Seta o desvio padr�o geral das notas em todas as perguntas da Avalia��o Institucional. 
	 * @param desvioPadraoGeral Desvio padr�o geral das notas em todas as perguntas da Avalia��o Institucional. 
	 */
	public void setDesvioPadraoGeral(Double desvioPadraoGeral) {
		this.desvioPadraoGeral = desvioPadraoGeral;
	}
	
	/**
	 * Retorna uma representa��o textual deste objeto no formato nome do
	 * docente, seguido de v�rgula, seguido da turma, seguido de colchetes,
	 * seguido da m�dia geral, seguido de v�rgula, seguido do desvio padr�o
	 * geral, seguido de colchetes.
	 * 
	 */
	@Override
	public String toString() {
		return docenteTurma.getDocenteNome() + ", " + docenteTurma.getTurma() + " [" + mediaGeral + ", " + desvioPadraoGeral + "]";
	}

	/** Indica se o ID deste objeto � igual ao do informando.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ResultadoAvaliacaoDocente)
			return this.id == ((ResultadoAvaliacaoDocente )obj).getId();
		else return false;
	}
	
	/** Compara se este o nome do docente da turma � maior, menor ou igual ao do resultado informado.
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ResultadoAvaliacaoDocente o) {
		int comparacao = this.getDocenteTurma().getDocenteNome().compareTo(o.getDocenteTurma().getDocenteNome());
		if (comparacao == 0)
			comparacao = this.getDocenteTurma().getTurma().getDescricaoDisciplina().compareTo(o.getDocenteTurma().getTurma().getDescricaoDisciplina());
		if (comparacao == 0)
			comparacao = this.getDocenteTurma().getTurma().getCodigo().compareTo(o.getDocenteTurma().getTurma().getCodigo());
		return comparacao;
	}

	/** Calcula o c�digo Hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 31 * id;
		if (docenteTurma != null) hash += 31 * docenteTurma.getId();
		return hash;
	}

	/** Retorna o ano ao qual o resultado se refere.
	 * @return
	 */
	public int getAno() {
		return ano;
	}

	/** Seta o ano ao qual o resultado se refere.
	 * @param ano
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/** Retorna o per�odo ao qual o resultado se refere.
	 * @return
	 */
	public int getPeriodo() {
		return periodo;
	}

	/** Seta o per�odo ao qual o resultado se refere.
	 * @param periodo
	 */
	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public TurmaDocenciaAssistida getTurmaDocenciaAssistida() {
		return turmaDocenciaAssistida;
	}

	public void setTurmaDocenciaAssistida(
			TurmaDocenciaAssistida turmaDocenciaAssistida) {
		this.turmaDocenciaAssistida = turmaDocenciaAssistida;
	}

	public FormularioAvaliacaoInstitucional getFormularioAvaliacaoInstitucional() {
		return formularioAvaliacaoInstitucional;
	}

	public void setFormularioAvaliacaoInstitucional(
			FormularioAvaliacaoInstitucional formularioAvaliacaoInstitucional) {
		this.formularioAvaliacaoInstitucional = formularioAvaliacaoInstitucional;
	}
}
