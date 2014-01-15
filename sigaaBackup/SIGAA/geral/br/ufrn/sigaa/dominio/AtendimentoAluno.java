/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '13/08/2008'
 *
 */
package br.ufrn.sigaa.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Esta classe representa a comunicação entre o discente e a coordenação do seu
 * curso. Discente faz uma pergunta, e o coordenador ou o responsável da
 * secretária do curso responde.
 * 
 * @author Henrique Andre
 */

@Entity
@Table(schema="comum", name = "atendimento_aluno")
public class AtendimentoAluno implements PersistDB {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_atendimento")
	private int id;

	/**
	 * título da mensagem
	 */
	@Column(name = "titulo")
	private String titulo;

	/**
	 * conteúdo da pergunta
	 */
	@Column(name = "pergunta")
	private String pergunta;

	/**
	 * conteúdo da resposta
	 */
	@Column(name = "resposta")
	private String resposta;
	
	private boolean ativo;

	/**
	 * status atual da mensagem
	 */
	@ManyToOne()
	@JoinColumn(name = "id_status_atendimento")
	private StatusAtendimentoAluno statusAtendimento;

	/**
	 * coordenador ou responsável da secretária que respondeu a pergunta
	 */
	@ManyToOne
	@JoinColumn(name = "id_servidor")
	private Servidor atendente;

	/**
	 * data em que a pergunta foi respondida
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_atendimento")
	private Date dataAtendimento;

	/**
	 * discente que fez a pergunta
	 */
	@ManyToOne(targetEntity=Discente.class)
	@JoinColumn(name = "id_discente")
	private DiscenteAdapter discente;

	/**
	 * data que o discente fez a pergunta
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_envio")
	private Date dataEnvio;		

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getPergunta() {
		return pergunta;
	}

	public void setPergunta(String pergunta) {
		this.pergunta = pergunta;
	}

	public String getResposta() {
		return resposta;
	}

	public void setResposta(String resposta) {
		this.resposta = resposta;
	}

	public StatusAtendimentoAluno getStatusAtendimento() {
		return statusAtendimento;
	}

	public void setStatusAtendimento(StatusAtendimentoAluno statusAtendimento) {
		this.statusAtendimento = statusAtendimento;
	}

	public Servidor getAtendente() {
		return atendente;
	}

	public void setAtendente(Servidor atendente) {
		this.atendente = atendente;
	}

	public Date getDataAtendimento() {
		return dataAtendimento;
	}

	public void setDataAtendimento(Date dataAtendimento) {
		this.dataAtendimento = dataAtendimento;
	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}

	public Date getDataEnvio() {
		return dataEnvio;
	}

	public void setDataEnvio(Date dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	public boolean isAlunoLeu() {
		if (statusAtendimento.getId() == StatusAtendimentoAluno.ALUNO_LEU)
			return true;
		return false;
	}

	public boolean isRespondido() {
		if (statusAtendimento.getId() == StatusAtendimentoAluno.ATENDENTE_RESPONDEU
				|| statusAtendimento.getId() == StatusAtendimentoAluno.ALUNO_LEU)
			return true;
		return false;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	
}
