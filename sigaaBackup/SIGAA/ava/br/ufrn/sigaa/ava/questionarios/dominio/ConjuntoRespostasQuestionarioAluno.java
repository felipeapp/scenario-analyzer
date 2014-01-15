package br.ufrn.sigaa.ava.questionarios.dominio;

import java.text.NumberFormat;
import java.util.ArrayList;
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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.dominio.Usuario;


/**
 * Classe auxiliar que guarda todas as resposta de um questionário enviada por um discente.
 * 
 * @author Diego Jácome
 * 
 */
@Entity @Table(name="conjunto_respostas_questionario", schema="ava")
public class ConjuntoRespostasQuestionarioAluno implements Validatable {

	/** Identificador do objeto */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") })
	@Column(name = "id_conjunto_respostas_questionario")
	private int id;
	
	/** Questionario para o qual estas respostas foram enviadas */
	@ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="id_questionario_turma")
	private QuestionarioTurma questionario;
	
	/** Respostas dos alunos para esta tarefa. */
	@OneToMany(mappedBy="conjuntoRespostas", fetch=FetchType.LAZY , cascade=CascadeType.ALL)
	private List<EnvioRespostasQuestionarioTurma> respostas;
	
	/** O usuário que enviou as respostas */
	@ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="id_usuario_envio")
	private Usuario usuarioEnvio;
	
	/** Porcentagem de acerto das respostas do conjunto de acordo com a configuração do tipo de média do questionário. */
	private float porcentagem;

	/** Se o conjunto está ativo */
	private boolean ativo;
	
	public ConjuntoRespostasQuestionarioAluno() {
	}
	
	public ConjuntoRespostasQuestionarioAluno(	QuestionarioTurma questionario ,  
												List<EnvioRespostasQuestionarioTurma> respostas , 
												Usuario usuarioEnvio ) {
		this.questionario = questionario;
		this.respostas = respostas;
		this.usuarioEnvio = usuarioEnvio;
	}
	
	/**
	 * Calcula a nota final do conjunto de respostas, verificando se o tipo de nota é 
	 * a média de todas as respostas, a melhor resposta ou a última resposta.
	 */
	public void calcularNotas() {
		
		if ( respostas != null ){
			organizarRespostas();
			
			if ( questionario.getTentativas() == 1 && !respostas.isEmpty() )
				calculaPorcentagemUltimaNota();
			else if ( questionario.getTentativas() > 1 &&  !respostas.isEmpty() ){
					
				if ( questionario.isUltimaNota() )					
					calculaPorcentagemUltimaNota();
				
				if ( questionario.isNotaMaisAlta() )	
					calculaPorcentagemNotaMaisAlta();
				
				if ( questionario.isMediasDasNotas() )					
					calculaPorcentagemMediaDasNotas();
			} 	
		}
	}

	/**
	 * Calcula a porcentagem de acerto do aluno quando o tipo de nota do questionário 
	 * for média das notas.	
	 */
	private void calculaPorcentagemMediaDasNotas() {
		
		if (respostas != null && !respostas.isEmpty()){
			
			Double porcentagemAcerto = 0.0;
			
			for ( EnvioRespostasQuestionarioTurma r : respostas ){
				r.calcularNota();
				porcentagemAcerto += r.getPorcentagem();
			}
			
			porcentagemAcerto = porcentagemAcerto/respostas.size();
			porcentagemAcerto =  Math.round(porcentagemAcerto * 100D ) / 100D;
			porcentagem = Float.valueOf(porcentagemAcerto.toString());
		}	
	}

	/**
	 * Calcula a porcentagem de acerto do aluno quando o tipo de nota do questionário 
	 * for a melhor nota.	
	 */
	private void calculaPorcentagemNotaMaisAlta() {
		
		if (respostas != null && !respostas.isEmpty() ){
			
			respostas.get(0).calcularNota();
			porcentagem = respostas.get(0).getPorcentagem();
		
			for ( EnvioRespostasQuestionarioTurma r : respostas ){
				r.calcularNota();
				if ( r.getPorcentagem() > porcentagem )
					porcentagem = r.getPorcentagem();
			}
		}	
	}

	/**
	 * Calcula a porcentagem de acerto do aluno quando o tipo de nota do questionário 
	 * for a última nota.	
	 */
	private void calculaPorcentagemUltimaNota() {
		
		if (respostas != null && !respostas.isEmpty()){
		
			EnvioRespostasQuestionarioTurma ultimaResposta = respostas.get(0);
	
			for ( EnvioRespostasQuestionarioTurma r : respostas ){
				// A resposta pode não ter sido finalizada.
				if ( r.getDataFinalizacao() != null && ultimaResposta.getDataFinalizacao() != null )
					if ( r.getDataFinalizacao().after(ultimaResposta.getDataFinalizacao()) )
						ultimaResposta = r;
			}
			
			ultimaResposta.calcularNota();
			porcentagem = ultimaResposta.getPorcentagem();
		}	
	}
	
	/**
	 * Organiza as respostas removendo as não finalizadas, quando nescessário.<br/><br/>
	 * Método não chamado por JSPs:<br/>
	 * 
	 * @return
	 */
	public void organizarRespostas () {
		
		List<EnvioRespostasQuestionarioTurma> respostasFinalizadas = new ArrayList<EnvioRespostasQuestionarioTurma>();
		
		// Elimina as respostas não finalizadas do conjunto
		for ( EnvioRespostasQuestionarioTurma r : respostas ){
			if ( r != null && r.isFinalizado() )
				respostasFinalizadas.add(r);
		}
		
		if ( !respostasFinalizadas.isEmpty() )
			respostas = respostasFinalizadas;
			
		// Caso o conjunto seja formado apenas por respostas não finalizadas - apenas a última resposta não finalizada deverá ser contada.
		if ( respostasFinalizadas.isEmpty() && !respostas.isEmpty() ) {
			
			EnvioRespostasQuestionarioTurma ultima = null;
			
			for ( EnvioRespostasQuestionarioTurma r : respostas ){
				if (ultima == null || (r.getDataCadastro().after(ultima.getDataCadastro())))
					ultima = r;
			} 
			
			respostas.clear();
			respostas.add(ultima);
		} 	
	}
	
	public void setRespostas(List<EnvioRespostasQuestionarioTurma> respostas) {
		this.respostas = respostas;
	}

	public List<EnvioRespostasQuestionarioTurma> getRespostas() {
		return respostas;
	}

	public void setQuestionario(QuestionarioTurma questionario) {
		this.questionario = questionario;
	}

	public QuestionarioTurma getQuestionario() {
		return questionario;
	}

	public void setUsuarioEnvio(Usuario usuarioEnvio) {
		this.usuarioEnvio = usuarioEnvio;
	}

	public Usuario getUsuarioEnvio() {
		return usuarioEnvio;
	}

	public void setPorcentagem(float porcentagem) {
		this.porcentagem = porcentagem;
	}

	public float getPorcentagem() {
		return porcentagem;
	}
	
	/**
	 * Verifica se alguma das respostas do conjunto possui dissertativas pedentes.
	 * @return
	 */
	public boolean isDissertativasPendentes () {
		if ( respostas == null || respostas.isEmpty() )
			return false;
		
		// Se o questionário estiver configurado para última resposta só precisa corrigir as dissertativas da última resposta.
		if ( questionario.isUltimaNota() ){
			EnvioRespostasQuestionarioTurma ultima = getUltimaRespostaFinalizada();
			if (ultima != null && ultima.isDissertativasPendentes())
				return true;
			else
				return false;
			
		} else {
			for ( EnvioRespostasQuestionarioTurma rs : respostas ) {
				if ( rs != null && rs.isDissertativasPendentes() )
					return true;
			}
			return false;
		}
	}
	
	/**
	 * Retorna a data de finalização da última resposta.
	 * @return
	 */
	public Date getUltimaDataFinalizada () {
		Date ultimaData = null;
		
		if ( respostas != null && !respostas.isEmpty()  ){
			ultimaData = respostas.get(0).getDataFinalizacao();
			for ( EnvioRespostasQuestionarioTurma rs : respostas )
				if ( rs.getDataFinalizacao().before(ultimaData) )
					ultimaData = rs.getDataFinalizacao();
		}
			
		return ultimaData;	
	}
	
	/**
	 * Retorna a última resposta finalizada.
	 * @return
	 */
	public EnvioRespostasQuestionarioTurma getUltimaRespostaFinalizada () {
		EnvioRespostasQuestionarioTurma ultima = null;
		EnvioRespostasQuestionarioTurma ultimaFinalizada = null;

		
		if ( respostas != null && !respostas.isEmpty()  ){
			
			for ( EnvioRespostasQuestionarioTurma r : respostas ){
				if (ultima == null || (r.getDataCadastro().after(ultima.getDataCadastro())))
					ultima = r;
				if ( r != null && r.isFinalizado() )
					if (ultimaFinalizada == null || (r.getDataCadastro().after(ultimaFinalizada.getDataCadastro())))
						ultimaFinalizada = r;
			} 
		}
		
		// Caso o discente tenha acessado o questionário, porém não tenha enviado a resposta
		if ( ultimaFinalizada == null )
			return ultima;	
		else
			return ultimaFinalizada;
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

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public boolean isAtivo() {
		return ativo;
	}
}