package br.ufrn.sigaa.apedagogica.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Representa a notificação feita aos inscritos em um processo seletivo.
 * @author Mário Rizzi
 *
 */
@Entity
@Table(name="notificacao_participante_atividade", schema="apedagogica")
public class NotificacaoParticipanteAtividade implements Validatable {

	/**
	 * Atributo que define a unicidade da classe.
	 */
	@Id
	@Column(name="id_notificacao_participante_atividade")
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
		           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	

	/**
	 * Atributo que define o processo seletivo associado.
	 */
	@ManyToOne
	@JoinColumn(name = "id_atividade_atualizacao_pedagogica")
	private AtividadeAtualizacaoPedagogica atividade;
	
	/**
	 * Atributo que define o título da mensagem.
	 */
	private String titulo;
	
	/**
	 * Atributo que define o corpo da mensagem.
	 */
	private String mensagem;
	
	/**
	 * Atributo que define as operações executadas.	  
	 */
	@Column(name="data_cadastro")
	@Temporal(TemporalType.TIMESTAMP)
	@CriadoEm
	private Date data;
	
	/**
	 * Atributo que define as operações executadas.	  
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroEntrada;
	
	public NotificacaoParticipanteAtividade(){
		setAtividade( new AtividadeAtualizacaoPedagogica() );
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public AtividadeAtualizacaoPedagogica getAtividade() {
		return atividade;
	}

	public void setAtividade(AtividadeAtualizacaoPedagogica atividade) {
		this.atividade = atividade;
	}
	
	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}
	
	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	@Override
	public ListaMensagens validate() {
	
		ListaMensagens erros = new ListaMensagens();
		
		ValidatorUtil.validateRequired(getAtividade(), "Atividade", erros);
		ValidatorUtil.validateRequired(getTitulo(), "Assunto", erros);
		ValidatorUtil.validateRequired(getMensagem(), "Mensagem", erros);
		
		return erros;
		
	}
	

}

