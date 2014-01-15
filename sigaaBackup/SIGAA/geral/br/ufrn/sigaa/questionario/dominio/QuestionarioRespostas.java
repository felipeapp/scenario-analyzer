/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 28/10/2008
 * 
 */
package br.ufrn.sigaa.questionario.dominio;

import java.util.Collection;
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
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.AbstractMovimento;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.sigaa.assistencia.cadunico.dominio.AdesaoCadastroUnicoBolsa;
import br.ufrn.sigaa.ensino.dominio.InscricaoSelecao;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividadeParticipante;
import br.ufrn.sigaa.extensao.dominio.QuestionarioProjetoExtensao;
import br.ufrn.sigaa.vestibular.dominio.InscricaoVestibular;

/**
 * Entidade que armazena as respostas de um determinado usu�rio (dependendo do tipo do question�rio)
 * para um question�rio cadastrado no sistema.
 * 
 * @author Wendell
 *
 */
@Entity
@Table(name = "questionario_respostas", schema = "questionario")
public class QuestionarioRespostas extends AbstractMovimento implements Validatable {

	/** O id */
	private int id;
	
	/** O question�rios dessas respostas. */
	private Questionario questionario;
	
	/** As respostas informadas pelo usu�rio . */
	private Collection<Resposta> respostas;
	
	/** A data que o usu�rio respondeu o question�rio. */
	@CriadoEm
	private Date dataCadastro;
	
	/** Inscritos no processo seletivo que responderam o question�rios */
	private InscricaoSelecao inscricaoSelecao;
	
	/** Vestibular */
	private InscricaoVestibular inscricaoVestibular;
	
	/** Guarda uma refer�ncia ao respostas dadas por um usu�rio em uma inscri��o para Curso ou Evento que extens�o */
    private InscricaoAtividadeParticipante inscricaoAtividadeParticipante;
	
	/** Refer�ncia para os alunos que responderam ao question�rio de ades�o ao cadastro �nico */
	private AdesaoCadastroUnicoBolsa adesao;
	
	private QuestionarioProjetoExtensao questionarioExtensao;
	
	public QuestionarioRespostas() {
	}
	
	public QuestionarioRespostas(int id) {
		this.id = id;
	}
	
	public QuestionarioRespostas(Questionario questionario) {
		this.questionario = questionario;
	}

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_questionario_respostas")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_questionario")
	public Questionario getQuestionario() {
		return questionario;
	}

	public void setQuestionario(Questionario questionario) {
		this.questionario = questionario;
	}

	@OneToMany(mappedBy = "questionarioRespostas", cascade=CascadeType.ALL)
	public Collection<Resposta> getRespostas() {
		return respostas;
	}

	public void setRespostas(Collection<Resposta> respostas) {
		this.respostas = respostas;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_inscricao_selecao")
	public InscricaoSelecao getInscricaoSelecao() {
		return inscricaoSelecao;
	}

	public void setInscricaoSelecao(InscricaoSelecao inscricaoSelecao) {
		this.inscricaoSelecao = inscricaoSelecao;
	}

	/**
	 * Remove o id das respostas, para poder persistir em um novo questionarioResposta
	 */
	public void resetarIdRespostas() {
		
		for (Resposta resposta : respostas) {
			resposta.setId(0);
			resposta.getQuestionarioRespostas().setAdesao(getAdesao());
			resposta.setQuestionarioRespostas(this);
		}
	}
	
	/**
	 * Valida o question�rio de resposta
	 */
	public ListaMensagens validate() {
		ListaMensagens listaMensagens = new ListaMensagens();
		
		// Se as respostas forem obrigat�rias, validar por tipo
		if ( questionario != null) {
			for ( Resposta resposta : respostas ) {
				if (resposta.getPergunta().isObrigatoria() || questionario.isRespostasObrigatorias())
					listaMensagens.addAll( resposta.validate() );

				if ( (resposta.getPergunta().isDissertativa() || resposta.getPergunta().isNumerica()) ) {
					int casasDecimal = 2;
					if ( ( resposta.getPergunta().isNumerica() && resposta.getPergunta().getMaxCaracteres() != null 
							&& resposta.getRespostaNumerica() != null && resposta.getPergunta().getMaxCaracteres() < ( resposta.getRespostaNumerica().toString().length() - casasDecimal ) ) 
							|| (resposta.getPergunta().isDissertativa() && resposta.getPergunta().getMaxCaracteres() != null && resposta.getRespostaDissertativa() != null 
							&& resposta.getPergunta().getMaxCaracteres() < resposta.getRespostaDissertativa().length())) {
						String tipoPergunta = resposta.getPergunta().isDissertativa() ? "Caracteres." : "N�meros."; 
						listaMensagens.addErro("A resposta da pergunta " + resposta.getPergunta().getPergunta() + " s� pode conter no m�ximo " 
								+ resposta.getPergunta().getMaxCaracteres() +  " " + tipoPergunta);
					}
				}
			}
		}
		
		return listaMensagens;
	}

	@Transient
	public List<PerguntaQuestionario> getPerguntas() {
		return questionario.getPerguntas();
	}

	/**
	 * Percorre as resposta e se for de escolher (multipla ou unica) marca as alternativas escolhidas pelo usu�rio
	 */
	public void popularVisualizacaoRespostas() {
		for (Resposta resposta : respostas) {
			if (resposta.getPergunta().isMultiplaOuUnicaEscolha()) {
				resposta.marcarAlternativasPergunta();
			}
		}
		
	}
	
	/** Para ser chamado nas p�ginas jsp */
	@Transient
	public String getPopularVisualizacaoRespostas(){
		popularVisualizacaoRespostas();
		return "";
	}

	/**
	 * No tipo de PerguntaQuestionario.UNICA_ESCOLHA_ALTERNATIVA_PESO, n�o existe certo ou errado, cada alternativa tem um peso.
	 * Esse m�todo faz a soma dos pesos;
	 * 
	 * @return
	 */
	public int gerarPontuacao() {
		int soma = 0;
		
		for (Resposta r : respostas) {
			soma+=r.getAlternativa().getPeso();
		}
		
		return soma;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_adesao")
	public AdesaoCadastroUnicoBolsa getAdesao() {
		return adesao;
	}

	public void setAdesao(AdesaoCadastroUnicoBolsa adesao) {
		this.adesao = adesao;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_inscricao_vestibular")
	public InscricaoVestibular getInscricaoVestibular() {
		return inscricaoVestibular;
	}

	public void setInscricaoVestibular(InscricaoVestibular inscricaoVestibular) {
		this.inscricaoVestibular = inscricaoVestibular;
	}

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_inscricao_atividade_participante")
	public InscricaoAtividadeParticipante getInscricaoAtividadeParticipante() {
		return inscricaoAtividadeParticipante;
	}

	public void setInscricaoAtividadeParticipante(
			InscricaoAtividadeParticipante inscricaoAtividadeParticipante) {
		this.inscricaoAtividadeParticipante = inscricaoAtividadeParticipante;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_questionario_extensao")
	public QuestionarioProjetoExtensao getQuestionarioExtensao() {
		return questionarioExtensao;
	}

	public void setQuestionarioExtensao(
			QuestionarioProjetoExtensao questionarioExtensao) {
		this.questionarioExtensao = questionarioExtensao;
	}

}