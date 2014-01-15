 /*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/11/2010
 *
 */
package br.ufrn.sigaa.ava.questionarios.dominio;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.dominio.Usuario;


/**
 * Classe de domínio que representa um questionário da Turma Virtual.
 * 
 * @author Fred_Castro
 */
@Entity @Table(name="envio_respostas_questionario_turma", schema="ava")
public class EnvioRespostasQuestionarioTurma implements Validatable {

	/** Identificador do objeto */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") })
	@Column(name = "id_envio_respostas_questionario_turma")
	private int id;
	
	/** Questionario para o qual estas respostas foram enviadas */
	@ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="id_questionario_turma")
	private QuestionarioTurma questionario;
	
	/** Questionario para o qual estas respostas foram enviadas */
	@ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST) @JoinColumn(name="id_conjunto_respostas_questionario")
	private ConjuntoRespostasQuestionarioAluno conjuntoRespostas;
	
	/** O usuário que enviou as respostas */
	@ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="id_usuario_envio")
	private Usuario usuarioEnvio;
	
	/** As respostas enviadas */
	@OneToMany (mappedBy="envio", cascade=CascadeType.ALL)
	private List <RespostaPerguntaQuestionarioTurma> respostas;
	
	/** Indica se o aluno finalizou o envio. */
	private boolean finalizado = false;
	
	/** Indica se há respostas dissertativas pendentes de correção. */
	@Column(name="dissertativas_pendentes")
	private boolean dissertativasPendentes = false;
	
	/** Data em que estas respostas foram enviadas. */
	@Column(name="data_finalizacao")
	private Date dataFinalizacao;
	
	/** Indic se o objeto está ativo */
	private boolean ativo = true;
	
	/** Indica a porcentagem de acerto. */
	@Column(name="porcentagem")
	private float porcentagem;
	
	//////////////////////////// informações auditoria //////////////////////////////
	
	/**
	 * Registro de entrada do usuário que cadastrou
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/**
	 * Data de cadastro
	 */
	@CriadoEm
	@Column(name="data_cadastro")
	private Date dataCadastro;

	/**
	 * Registro entrada do usuário que realizou a última atualização
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/**
	 * Data da última atualização
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_atualizacao")
	@AtualizadoEm
	private Date dataAtualizacao;
	
	/////////////////////////////////////////////////////////////////////////
	
	/**
	 * Calcula a nota final do envio
	 */
	public void calcularNota () {
		int acertos = 0;
		int perguntasInativas = 0;
		
		setDissertativasPendentes(false);
		
		for (RespostaPerguntaQuestionarioTurma r : getRespostas()){
			
			if (!r.getPergunta().isAtivo()){
				perguntasInativas ++;
				continue;
			}
			
			if (r.isCorretaAlternativa())
				acertos ++;
			
			// Se a pergunta for dissertativa e não tiver um gabarito definido, o docente deverá avaliar a resposta.
			if (r.getPergunta().isDissertativa() && StringUtils.isEmpty(r.getPergunta().getGabaritoDissertativa()) && r.getPorcentagemNota() == null ){
				setDissertativasPendentes(true);
			}
		}
		
		setPorcentagem(calculaPorcentagemAcerto( acertos, getRespostas().size() - perguntasInativas, getRespostas()));
	}
	
	/**
	 * Calcula a porcentagem de acertos de uma resposta do questionário.
	 * 
	 * @param acertos
	 * @param qtdRespostas
	 * @param respostas
	 * @return
	 */
	public float calculaPorcentagemAcerto ( double acertos , double qtdRespostas , List<RespostaPerguntaQuestionarioTurma> respostas){
		
		Double porcentagemAcerto =  qtdRespostas > 0 ? acertos / qtdRespostas : 0;
		Double porcentagemQuestao = 100/qtdRespostas;
		
		for (RespostaPerguntaQuestionarioTurma r : respostas){
			
			if (!r.getPergunta().isAtivo())
				continue;
					
			if (r.getPergunta().isDissertativa()) {
								
				if (!StringUtils.isEmpty(r.getPergunta().getGabaritoDissertativa())){
					
					boolean respostaCorreta = false;
					
					String resposta = r.getRespostaDissertativa() != null ? r.getRespostaDissertativa().trim() : null;
					
					if (resposta != null)
						respostaCorreta = r.getPergunta().getGabaritoDissertativa().trim().equalsIgnoreCase(resposta);
					
					if ( respostaCorreta )
						r.setPorcentagemNota(100);
					else if (r.getPorcentagemNota() == null)
						r.setPorcentagemNota(0);
				}
				
				Double porcentagemResposta = r.getPorcentagemNota() == null ? 0.0 : r.getPorcentagemNota();
				Double porcentagemAcertoDissertativa = porcentagemQuestao*porcentagemResposta/100;
				porcentagemAcerto += porcentagemAcertoDissertativa/100;
				
			}	
		}

		porcentagemAcerto = Math.round(porcentagemAcerto * 100D ) / 100D;
		return Float.valueOf(porcentagemAcerto.toString());
	} 
	
	/**
	 * Informa se o há perguntas dissertativas.
	 * 
	 * @return
	 */
	public boolean isPossuiPerguntasDissertativas () {
		
		for (RespostaPerguntaQuestionarioTurma r : respostas)
			if (r.getPergunta().isDissertativa())
				return true;
		
		return false;
	}
	
	/**
	 * Retorna quanto tempo, em segundos, o discente tem para responder o questionário.
	 * 
	 * @return
	 */
	public long getTempoRestante (){
		long tempoRestante = (getDataCadastro().getTime() + questionario.getDuracao() * 60 * 1000 - new Date().getTime()) / 1000;
		Calendar cal = Calendar.getInstance();
		cal.setTime(questionario.getFim());
		cal.set(Calendar.HOUR, questionario.getHoraFim());
		cal.set(Calendar.MINUTE, questionario.getMinutoFim());
		if ( (getDataCadastro().getTime() + questionario.getDuracao() * 60 * 1000) > cal.getTime().getTime() ){
			tempoRestante = (cal.getTime().getTime() - new Date().getTime()) / 1000;
		}	
		return tempoRestante;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public QuestionarioTurma getQuestionario() {
		return questionario;
	}

	public void setQuestionario(QuestionarioTurma questionario) {
		this.questionario = questionario;
	}

	public Usuario getUsuarioEnvio() {
		return usuarioEnvio;
	}

	public void setUsuarioEnvio(Usuario usuarioEnvio) {
		this.usuarioEnvio = usuarioEnvio;
	}
	
	@Override
	public ListaMensagens validate() {
		ListaMensagens lm = new ListaMensagens ();
		
		for (RespostaPerguntaQuestionarioTurma r : respostas)
			if (r.getPergunta().isAtivo())
				lm.addAll(r.validate());
		
		return lm;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public List<RespostaPerguntaQuestionarioTurma> getRespostas() {
		return respostas;
	}

	public void setRespostas(List<RespostaPerguntaQuestionarioTurma> respostas) {
		this.respostas = respostas;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public boolean isFinalizado() {
		return finalizado;
	}

	public void setFinalizado(boolean finalizado) {
		this.finalizado = finalizado;
	}

	public Date getDataFinalizacao() {
		return dataFinalizacao;
	}

	public void setDataFinalizacao(Date dataFinalizacao) {
		this.dataFinalizacao = dataFinalizacao;
	}

	public float getPorcentagem() {
		return porcentagem;
	}
	
	/**
	 * Retorna a representação visual para a porcentagem de acerto.
	 * @return
	 */
	public String getPorcentagemString () {
		NumberFormat n = NumberFormat.getInstance();
		n.setMaximumFractionDigits(2);
		return n.format(porcentagem * 100) + "%";
	}

	public void setPorcentagem(float porcentagem) {
		this.porcentagem = porcentagem;
	}

	public boolean isDissertativasPendentes() {
		return dissertativasPendentes;
	}

	public void setDissertativasPendentes(boolean dissertativasPendentes) {
		this.dissertativasPendentes = dissertativasPendentes;
	}
	
	public boolean equals (Object o){
		return EqualsUtil.testEquals(this, o, "id");
	}
	
	public int hashCode (){
		return HashCodeUtil.hashAll(id);
	}

	public void setConjuntoRespostas(ConjuntoRespostasQuestionarioAluno conjuntoRespostas) {
		this.conjuntoRespostas = conjuntoRespostas;
	}

	public ConjuntoRespostasQuestionarioAluno getConjuntoRespostas() {
		return conjuntoRespostas;
	}
	
	/**
	 * Retorna a hora em que o discente enviou a resposta.
 	 * Método chamado pela seguinte JSP: /ava/QuestionarioTurma/visualizarResultado.jsp
	 * 
	 * @return
	 */	
	public String getHoraEnvio (){
		String res = "";
		if (dataFinalizacao!=null){
			SimpleDateFormat sdf =  new SimpleDateFormat("H:mm");
			String hora = sdf.format(dataFinalizacao);
			sdf.applyPattern("dd/MM/yyyy");
			String dia = sdf.format(dataFinalizacao);
			res += dia + " às " + hora;
		} else {
			res += "Não Finalizado"; 
		}	
		return res;
	}
}