 /*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/11/2010
 *
 */
package br.ufrn.sigaa.ava.questionarios.dominio;


import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.ava.dominio.AtividadeAvaliavel;
import br.ufrn.sigaa.ava.dominio.DominioTurmaVirtual;
import br.ufrn.sigaa.ava.dominio.MaterialTurma;
import br.ufrn.sigaa.ava.dominio.TipoMaterialTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;


/**
 * Classe de domínio que representa um questionário da Turma Virtual.
 * 
 * @author Fred_Castro
 */
@Entity 
@Table(name="questionario_turma", schema="ava")
@PrimaryKeyJoinColumn (name="id_questionario_turma", referencedColumnName="id_atividade_avaliavel")
public class QuestionarioTurma extends AtividadeAvaliavel implements DominioTurmaVirtual {

	/** A turma à qual pertence este questionário */
	@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="id_turma")
	private Turma turma;
	
	/** Coleção de mensagens a serem exibidas de acordo com a nota tirada pelo aluno */
	@OneToMany(mappedBy = "questionario", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@OrderBy("porcentagemMaxima desc")
	private List <FeedbackQuestionarioTurma> feedbacks = new ArrayList<FeedbackQuestionarioTurma>();
	
	/** Lista auxiliar para guardar as perguntas do questionário */
	@Transient
	private List <PerguntaQuestionarioTurma> perguntas = new ArrayList <PerguntaQuestionarioTurma> ();
	
	/** Lista auxiliar para guardar as respostas enviadas para o questionário */
	@Transient
	private List <EnvioRespostasQuestionarioTurma> respostas = new ArrayList<EnvioRespostasQuestionarioTurma>();
	
	/** Lista auxiliar para guardar as os conjuntos de respostas enviadas para o questionário */
	@Transient
	private List <ConjuntoRespostasQuestionarioAluno> conjuntos = new ArrayList<ConjuntoRespostasQuestionarioAluno>();
	
	/** Auxiliar que diz quantas respostas foram enviadas */
	@Transient
	private int qtdRespostas;
	
	/** Indica se as notas foram publicadas para a planilha de notas. */
	@Column(name="notas_publicadas")
	private boolean notasPublicadas = false;
	
	/** A descrição do questionário */
	private String descricao;
	
	/** A data de início do questionário */
	private Date inicio;
	
	/** A hora de início do questionário */
	@Column (name = "hora_inicio")
	private int horaInicio;
	
	/** O minuto de início do questionário */
	@Column (name = "minuto_inicio")
	private int minutoInicio;
	
	/** A data de fim do questionário */
	private Date fim;
	
	/** A hora de fim do questionário */
	@Column (name = "hora_fim")
	private int horaFim = 23;
	
	/** O minuto de fim do questionário */
	@Column (name = "minuto_fim")
	private int minutoFim = 59;
	
	/** A data de fim para a visualização do questionário */
	@Column (name = "fim_visualizacao")
	private Date fimVisualizacao;
	
	/** A hora de fim para a visualização do questionário */
	@Column (name = "hora_fim_visualizacao")
	private int horaFimVisualizacao = 23;
	
	/** O minuto de fim para a visualização do questionário */
	@Column (name = "minuto_fim_visualizacao")
	private int minutoFimVisualizacao = 59;
	
	/** O tempo máximo, em minutos, que o discente tem para responder o questionário após iniciá-lo. */
	private Integer duracao = 60;
	
	/** Indica se é para misturar as perguntas para cada nova tentativa de resolução */
	@Column (name = "misturar_perguntas")
	private boolean misturarPerguntas;
	
	/** Indica se é para misturar as alternativas das perguntas do questionário a cada tentativa de resolução */
	@Column (name = "misturar_alternativas")
	private boolean misturarAlternativas;
	
	/** Indica se é para exibir somente um subconjunto de todas as perguntas cadastradas para o questionário */
	@Column (name = "exibir_sub_conjunto")
	private boolean exibirSubConjunto;
	
	/** O tamanho do subconjunto de questões */
	@Column (name = "tamanho_sub_conjunto")
	private Integer tamanhoSubConjunto;
	
	/** A quantidade de tentativas que cada discente pode fazer */
	private Integer tentativas = 1;

	/** Indica se o objeto está finalizado, podendo aparecer na turma virtual */
	private boolean finalizado = false;
	
	/////////////////////////////////////////////////////////////////////////
	
	/** Define como será a média das notas do questionários. */
	public static final char NOTA_MAIS_ALTA = 'A';
	/** Define como será a média das notas do questionários. */
	public static final char MEDIA_DAS_NOTAS = 'M';
	/** Define como será a média das notas do questionários. */
	public static final char ULTIMA_NOTA = 'U';
	
	/** Indica como será a nota de um questionário que pode ser respondido mais de uma vez. */
	@Column(name="media_notas")
	private Character mediaNotas;
	
	/////////////////////////////////////////////////////////////////////////
	
	/** Define o valor que identifica o tipo de visualização do feedback. */
	public static final char VISUALIZAR_APOS_RESPONDER = 'A';
	/** Define o valor que identifica o tipo de visualização do feedback. */
	public static final char VISUALIZAR_DEPOIS_FINALIZAR = 'F';
	/** Define o valor que identifica o tipo de visualização do feedback. */
	public static final char NAO_EXIBIR = 'N';
	
	/** Indica se os discentes poderão visualizar as respostas antes do prazo final do questionário */
	@Column(name="visualizar_resposta")
	private Character visualizarRespostas;
	
	/** Indica se os discentes poderão visualizar os feedbacks das perguntas antes do prazo final do questionário */
	@Column(name="visualizar_feedback")
	private Character visualizarFeedback;
	
	/** Indica se os discentes poderão visualizar o feedback geral antes do prazo final do questionário */
	@Column(name="visualizar_feedback_geral")
	private Character visualizarFeedbackGeral;
	
	/** Indica se os discentes poderão visualizar sua porcentagem de acerto antes do prazo final do questionário */
	@Column(name=" visualizar_nota")
	private Character visualizarNota;
	
	/** Indica se um conjunto de perguntas irá depender da última tentativa do discente. */
	@Column(name="depende_ultima")
	private Boolean dependeUltima;
	
	/** Construtor padrão */
	public QuestionarioTurma () {
		tipoAtividade = AtividadeAvaliavel.TIPO_QUESTIONARIO;
		material = new MaterialTurma (TipoMaterialTurma.QUESTIONARIO);
	}
	
	/** Construtor que seta a id */
	public QuestionarioTurma (int id) {
		this();
		setId(id);
	} 
	
	/**
	 * atualiza o atributo ordem para de acordo com a posição na lista que a pergunta está!
	 */
	public void atualizarOrdemPerguntas(){
		if( !isEmpty(perguntas) )
			for( int i = 0; i < perguntas.size(); i++ )
				perguntas.get(i).setOrdem(i+1);
	}

	/**
	 * além de atualizar o atributo ordem das perguntas atualiza o campo ordem das alternativas
	 */
	public void atualizarOrdemPerguntasEAlternativas(){
		atualizarOrdemPerguntas();
		for( PerguntaQuestionarioTurma p : perguntas )
			p.atualizarOrdemAlternativas();
	}
	
	/**
	 * Informa se está no período para responder a este questionário.
	 * 
	 * @return true se a data atual estiver entre a dataInicio e a dataEntrega
	 */
	public boolean isDentroPeriodoEntrega () {
		
		Date dataEntrega = new Date();
	    
	    Calendar c = Calendar.getInstance();
	    
	    c.setTime(getInicio());
	    c.set(Calendar.HOUR, getHoraInicio());
	    c.set(Calendar.MINUTE, getMinutoInicio());
	    Date dataInicio = c.getTime();
	    
	    c.setTime(getFim());
	    c.set(Calendar.HOUR, getHoraFim());
	    c.set(Calendar.MINUTE, getMinutoFim());
	    c.set(Calendar.SECOND, 59);
	    Date dataFim = c.getTime();
	    
	    return dataEntrega.after(dataInicio) && dataEntrega.before(dataFim);
	}
	
	/**
	 * Informa se o questionário tem perguntas dissertativas.
	 * 
	 * @return
	 */
	public boolean isPossuiPerguntasDissertativas () {
		
		for (PerguntaQuestionarioTurma q : perguntas)
			if (q.isDissertativa())
				return true;
		
		return false;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public String getMensagemAtividade() {
		return "Novo Questionário: " + titulo;
	}

	@Override
	public String getNome() {
		return getTitulo();
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public int getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(int horaInicio) {
		this.horaInicio = horaInicio;
	}

	public int getMinutoInicio() {
		return minutoInicio;
	}

	public void setMinutoInicio(int minutoInicio) {
		this.minutoInicio = minutoInicio;
	}

	public int getHoraFim() {
		return horaFim;
	}

	public void setHoraFim(int horaFim) {
		this.horaFim = horaFim;
	}

	public int getMinutoFim() {
		return minutoFim;
	}

	public void setMinutoFim(int minutoFim) {
		this.minutoFim = minutoFim;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	public Date getFimVisualizacao() {
		return fimVisualizacao;
	}

	public void setFimVisualizacao(Date fimVisualizacao) {
		this.fimVisualizacao = fimVisualizacao;
	}

	public int getHoraFimVisualizacao() {
		return horaFimVisualizacao;
	}

	public void setHoraFimVisualizacao(int horaFimVisualizacao) {
		this.horaFimVisualizacao = horaFimVisualizacao;
	}

	public int getMinutoFimVisualizacao() {
		return minutoFimVisualizacao;
	}

	public void setMinutoFimVisualizacao(int minutoFimVisualizacao) {
		this.minutoFimVisualizacao = minutoFimVisualizacao;
	}

	public Integer getDuracao() {
		return duracao;
	}

	public void setDuracao(Integer duracao) {
		this.duracao = duracao;
	}

	public boolean isMisturarPerguntas() {
		return misturarPerguntas;
	}

	public void setMisturarPerguntas(boolean misturarPerguntas) {
		this.misturarPerguntas = misturarPerguntas;
	}

	public boolean isMisturarAlternativas() {
		return misturarAlternativas;
	}

	public void setMisturarAlternativas(boolean misturarAlternativas) {
		this.misturarAlternativas = misturarAlternativas;
	}

	public Integer getTentativas() {
		return tentativas;
	}

	public void setTentativas(Integer tentativas) {
		this.tentativas = tentativas;
	}

	public List<PerguntaQuestionarioTurma> getPerguntas() {
		return perguntas;
	}

	public void setPerguntas(List<PerguntaQuestionarioTurma> perguntas) {
		this.perguntas = perguntas;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setFeedbacks(List<FeedbackQuestionarioTurma> feedbacks) {
		this.feedbacks = feedbacks;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public List<FeedbackQuestionarioTurma> getFeedbacks() {
		return feedbacks;
	}

	public boolean isFinalizado() {
		return finalizado;
	}

	public void setFinalizado(boolean finalizado) {
		this.finalizado = finalizado;
	}

	public boolean isVisualizarRespostasAposResponder() {
		return visualizarRespostas.equals(VISUALIZAR_APOS_RESPONDER);
	}

	public boolean isVisualizarFeedbackAposResponder() {
		return visualizarFeedback.equals(VISUALIZAR_APOS_RESPONDER);
	}

	public boolean isVisualizarFeedbackGeralAposResponder() {
		return visualizarFeedbackGeral.equals(VISUALIZAR_APOS_RESPONDER);
	}

	public boolean isVisualizarNotaAposResponder() {
		return visualizarNota.equals(VISUALIZAR_APOS_RESPONDER);
	}

	public boolean isVisualizarRespostasAposFinalizar() {
		return visualizarRespostas.equals(VISUALIZAR_DEPOIS_FINALIZAR);
	}

	public boolean isVisualizarFeedbackAposFinalizar() {
		return visualizarFeedback.equals(VISUALIZAR_DEPOIS_FINALIZAR);
	}

	public boolean isVisualizarFeedbackGeralAposFinalizar() {
		return visualizarFeedbackGeral.equals(VISUALIZAR_DEPOIS_FINALIZAR);
	}

	public boolean isVisualizarNotaAposFinalizar() {
		return visualizarNota.equals(VISUALIZAR_DEPOIS_FINALIZAR);
	}
	
	public boolean isNaoVisualizarRespostas() {
		return visualizarRespostas.equals(NAO_EXIBIR);
	}

	public boolean isNaoVisualizarFeedback() {
		return visualizarFeedback.equals(NAO_EXIBIR);
	}

	public boolean isNaoVisualizarFeedbackGeral() {
		return visualizarFeedbackGeral.equals(NAO_EXIBIR);
	}

	public boolean isNaoVisualizarNota() {
		return visualizarNota.equals(NAO_EXIBIR);
	}
	
	public boolean isExibirSubConjunto() {
		return exibirSubConjunto;
	}

	public void setExibirSubConjunto(boolean exibirSubConjunto) {
		this.exibirSubConjunto = exibirSubConjunto;
	}

	public Integer getTamanhoSubConjunto() {
		return tamanhoSubConjunto;
	}

	public void setTamanhoSubConjunto(Integer tamanhoSubConjunto) {
		this.tamanhoSubConjunto = tamanhoSubConjunto;
	}

	public List<EnvioRespostasQuestionarioTurma> getRespostas() {
		return respostas;
	}

	public void setRespostas(List<EnvioRespostasQuestionarioTurma> respostas) {
		this.respostas = respostas;
	}

	public int getQtdRespostas() {
		return qtdRespostas;
	}

	public void setQtdRespostas(int qtdRespostas) {
		this.qtdRespostas = qtdRespostas;
	}

	public boolean isNotasPublicadas() {
		return notasPublicadas;
	}

	public void setNotasPublicadas(boolean notasPublicadas) {
		this.notasPublicadas = notasPublicadas;
	}


	
	public Character getVisualizarRespostas() {
		return visualizarRespostas;
	}

	public void setVisualizarRespostas(Character visualizarRespostas) {
		this.visualizarRespostas = visualizarRespostas;
	}

	public Character getVisualizarFeedback() {
		return visualizarFeedback;
	}

	public void setVisualizarFeedback(Character visualizarFeedback) {
		this.visualizarFeedback = visualizarFeedback;
	}

	public Character getVisualizarFeedbackGeral() {
		return visualizarFeedbackGeral;
	}

	public void setVisualizarFeedbackGeral(Character visualizarFeedbackGeral) {
		this.visualizarFeedbackGeral = visualizarFeedbackGeral;
	}

	public Character getVisualizarNota() {
		return visualizarNota;
	}

	public void setVisualizarNota(Character visualizarNota) {
		this.visualizarNota = visualizarNota;
	}
	
	/**
	 * Identifica o tipo de atividade
	 *	<ul>
	 * 		<li>TAREFA = 1;</li>
	 * 		<li>QUESTIONÁRIO = 2;</li>
	 *	</ul> 
	 */
	public int getAtividade() {
		return AtividadeAvaliavel.TIPO_QUESTIONARIO;
	}

	public boolean isNotaMaisAlta() {
		return mediaNotas != null && mediaNotas == NOTA_MAIS_ALTA;
	}
	
	public boolean isUltimaNota() {
		return mediaNotas != null && mediaNotas == ULTIMA_NOTA;
	}
	
	public boolean isMediasDasNotas() {
		return mediaNotas != null && mediaNotas == MEDIA_DAS_NOTAS;
	}

	/** 
	 * Seta o tipo de exibição de notas do qustionário, utilizando última nota como default.
	 * Este método não é chamado por JSP.
	 * @throws ArqException 
	 */
	public void setMediaNotas(Character mediaNotas) {
		if (tentativas != null && tentativas > 1)
			this.mediaNotas = mediaNotas;
		else
			this.mediaNotas = ULTIMA_NOTA;
	}

	public Character getMediaNotas() {
		return mediaNotas;
	}

	public void setConjuntos(List <ConjuntoRespostasQuestionarioAluno> conjuntos) {
		this.conjuntos = conjuntos;
	}

	public List <ConjuntoRespostasQuestionarioAluno> getConjuntos() {
		return conjuntos;
	}

	@Override
	public String getDescricaoGeral() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String result = "Inicia em " + sdf.format(inicio) + " às " + horaInicio + "h " + minutoInicio +"min " +
				"e finaliza em " + sdf.format(fim) + " às " + horaFim + "h " + minutoFim + "min";
		return result;
	}

	public boolean isFimExibicao () {

		Calendar cal = Calendar.getInstance();
		cal.setTime(fimVisualizacao);
	    cal.set(Calendar.HOUR, horaFimVisualizacao);
	    cal.set(Calendar.MINUTE, minutoFimVisualizacao);
	    Date dataFinalizacao = cal.getTime();
	    
		return new Date().after(dataFinalizacao);
	}
	
	public boolean isFimRespostas () {

		Calendar cal = Calendar.getInstance();
		cal.setTime(fim);
	    cal.set(Calendar.HOUR, horaFim);
	    cal.set(Calendar.MINUTE, minutoFim);
	    Date dataFinalizacao = cal.getTime();
	    
		return new Date().after(dataFinalizacao);
	}
	
	public void setDependeUltima(Boolean dependeUltima) {
		this.dependeUltima = dependeUltima;
	}

	public Boolean getDependeUltima() {
		return dependeUltima;
	}
	
}