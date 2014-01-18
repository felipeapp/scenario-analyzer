/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Resultado do Processamento das respostas da Avaliação Institucional para um
 * Docente de uma Turma.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
@Entity 
@Table(schema="avaliacao", name="resultado_avaliacao_docente")
public class ResultadoAvaliacaoDocente implements PersistDB, Comparable<ResultadoAvaliacaoDocente>{
	
	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="avaliacao.resultado_seq") })
	@Column(name = "id_resultado_avaliacao_docente", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Ano ao qual o resultado se refere. */
	private int ano;
	
	/** Período ao qual o resultado se refere. */
	private int periodo;
	
	/** DocenteTurma avaliado. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_docente_turma")
	private DocenteTurma docenteTurma;
	
	/** Turma de docência assistida ao qual esta resposta se refere. */
	@ManyToOne  (fetch=FetchType.EAGER) 
	@JoinColumn(name = "id_turma_docencia_assistida")
	private TurmaDocenciaAssistida turmaDocenciaAssistida;

	/** Número de discentes na turma. */
	@Column(name="num_discentes")
	private int numDiscentes;
	
	/** Número de discentes com trancamentos na turma. */
	@Column(name="num_trancamentos")
	private int numTrancamentos;
	
	/** Coleção das médias das notas nas perguntas da Avaliação Institucional. */
	@OneToMany(cascade = { CascadeType.ALL }, mappedBy="resultadoAvaliacaoDocente")
	@JoinColumn(name = "id_resultado_avaliacao_docente",nullable=false)
	private Collection<MediaNotas> mediaNotas;
	
	/** Coleção de Percentuais de respostas SIM/NÃO das perguntas da Avaliação Institucional.  */
	@OneToMany(cascade = { CascadeType.ALL}, mappedBy="resultadoAvaliacaoDocente")
	@JoinColumn(name = "id_resultado_avaliacao_docente")
	private Collection<PercentualSimNao> percentualRespostasSimNao;
	
	/** Média geral das notas em todas as perguntas da Avaliação Institucional. */ 
	@Column(name="media_geral")
	private Double mediaGeral;
	
	/** Desvio padrão geral das notas em todas as perguntas da Avaliação Institucional. */
	@Column(name="desvio_padrao_geral")
	private Double desvioPadraoGeral;
	
	/** Formulário de avaliação ao qual este resultado refere-se. */
	@ManyToOne  (fetch=FetchType.EAGER) 
	@JoinColumn(name = "id_formulario_avaliacao")
	private FormularioAvaliacaoInstitucional formularioAvaliacaoInstitucional;

	/** Construtor padrão. */
	public ResultadoAvaliacaoDocente() {
	}
	
	/** Construtor parametrizado.
	 * @param id
	 */
	public ResultadoAvaliacaoDocente(int id) {
		this();
		setId(id);
	}

	/** Retorna a chave primária. 
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	@Override
	public int getId() {
		return id;
	}

	/** Seta a chave primária. 
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

	/** Retorna o número de discentes na turma.  
	 * @return Número de discentes na turma. 
	 */
	public int getNumDiscentes() {
		return numDiscentes;
	}

	/** Seta o número de discentes na turma.
	 * @param numDiscentes Número de discentes na turma. 
	 */
	public void setNumDiscentes(int numDiscentes) {
		this.numDiscentes = numDiscentes;
	}

	/** Retorna o número de discentes com trancamentos na turma. 
	 * @return Número de discentes com trancamentos na turma. 
	 */
	public int getNumTrancamentos() {
		return numTrancamentos;
	}

	/** Retorna o número de discentes com trancamentos na turma.
	 * @param numTrancamentos Número de discentes com trancamentos na turma. 
	 */
	public void setNumTrancamentos(int numTrancamentos) {
		this.numTrancamentos = numTrancamentos;
	}

	/** Retorna a coleção das médias das notas nas perguntas da Avaliação Institucional. 
	 * @return Coleção das médias das notas nas perguntas da Avaliação Institucional. 
	 */
	public Collection<MediaNotas> getMediaNotas() {
		return mediaNotas;
	}

	/** Seta a coleção das médias das notas nas perguntas da Avaliação Institucional. 
	 * @param mediaNotas Coleção das médias das notas nas perguntas da Avaliação Institucional. 
	 */
	public void setMediaNotas(Collection<MediaNotas> mediaNotas) {
		this.mediaNotas = mediaNotas;
	}

	/** Retorna a coleção de Percentuais de respostas SIM/NÃO das perguntas da Avaliação Institucional. 
	 * @return Coleção de Percentuais de respostas SIM/NÃO das perguntas da Avaliação Institucional. 
	 */
	public Collection<PercentualSimNao> getPercentualRespostasSimNao() {
		return percentualRespostasSimNao;
	}

	/** Seta a coleção de Percentuais de respostas SIM/NÃO das perguntas da Avaliação Institucional. 
	 * @param percentualRespostasSimNao Coleção de Percentuais de respostas SIM/NÃO das perguntas da Avaliação Institucional. 
	 */
	public void setPercentualRespostasSimNao(
			Collection<PercentualSimNao> percentualRespostasSimNao) {
		this.percentualRespostasSimNao = percentualRespostasSimNao;
	}

	/** Retorna a média geral das notas em todas as perguntas da Avaliação Institucional. 
	 * @return Média geral das notas em todas as perguntas da Avaliação Institucional. 
	 */
	public Double getMediaGeral() {
		return mediaGeral;
	}

	/** Seta a média geral das notas em todas as perguntas da Avaliação Institucional. 
	 * @param mediaGeral Média geral das notas em todas as perguntas da Avaliação Institucional. 
	 */
	public void setMediaGeral(Double mediaGeral) {
		this.mediaGeral = mediaGeral;
	}

	/** Retorna o desvio padrão geral das notas em todas as perguntas da Avaliação Institucional.
	 * @return Desvio padrão geral das notas em todas as perguntas da Avaliação Institucional. 
	 */
	public Double getDesvioPadraoGeral() {
		return desvioPadraoGeral;
	}

	/** Seta o desvio padrão geral das notas em todas as perguntas da Avaliação Institucional. 
	 * @param desvioPadraoGeral Desvio padrão geral das notas em todas as perguntas da Avaliação Institucional. 
	 */
	public void setDesvioPadraoGeral(Double desvioPadraoGeral) {
		this.desvioPadraoGeral = desvioPadraoGeral;
	}
	
	/**
	 * Retorna uma representação textual deste objeto no formato nome do
	 * docente, seguido de vírgula, seguido da turma, seguido de colchetes,
	 * seguido da média geral, seguido de vírgula, seguido do desvio padrão
	 * geral, seguido de colchetes.
	 * 
	 */
	@Override
	public String toString() {
		return docenteTurma.getDocenteNome() + ", " + docenteTurma.getTurma() + " [" + mediaGeral + ", " + desvioPadraoGeral + "]";
	}

	/** Indica se o ID deste objeto é igual ao do informando.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ResultadoAvaliacaoDocente)
			return this.id == ((ResultadoAvaliacaoDocente )obj).getId();
		else return false;
	}
	
	/** Compara se este o nome do docente da turma é maior, menor ou igual ao do resultado informado.
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

	/** Calcula o código Hash deste objeto.
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

	/** Retorna o período ao qual o resultado se refere.
	 * @return
	 */
	public int getPeriodo() {
		return periodo;
	}

	/** Seta o período ao qual o resultado se refere.
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
