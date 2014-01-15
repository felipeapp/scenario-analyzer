/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '14/02/2011'
 *
 */
package br.ufrn.sigaa.ava.forum.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 * Representa o tipo de f�rum. 
 * 
 * @author Ilueny Santos
 *
 */
@Entity
@Table(name = "tipo_forum", schema = "ava")
public class TipoForum implements Validatable {
	
	/** �nica Discuss�o simples */
	public static final int TIPO_DISCUSSAO_SIMPLES = 10;	
	/** F�rum no estilo Perguntas e Respostas */
	public static final int TIPO_PERGUNTAS_RESPOSTAS = 20;	
	/** F�rum Geral */
	public static final int TIPO_GERAL = 30;	
	/** Padr�o exibir como blog */
	public static final int TIPO_BLOG = 40;	
	/** Cada usu�rio inicia apenas um novo t�pico */
	public static final int TIPO_NOVO_TOPICO = 50;
	
	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name="id_tipo_forum")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	/** Descri��o do tipo de f�rum. */
	@Column(name = "descricao", nullable = false)
	private String descricao;
	
	/** Indica se � um tipo de f�rum v�lido. Utilizado para exclus�o l�gica. */
	@CampoAtivo
	private boolean ativo;
	
	/** 
	 * Informa o tipo de tratamento que deve ser dado a uma mensagem. 
	 * Personaliza os componentes do f�rum e ajusta a terminologia utilizada para cada tipo de mensagem.  
	 * 
	 * Dependendo do tipo de f�rum, um t�pico pode ser tratado como 'Quest�o', 'Not�cia', 'T�pico de Discuss�o', etc.
	 */	
	@Column(name = "label_mensagem")
	private String labelMensagem;
	
	/** Permite ao usu�rio criar novos t�picos em f�runs deste tipo. */
	@Column(name = "permite_criar_topico", nullable = false)
	private boolean permiteCriarTopico;

	/** Permite ao usu�rio remover t�picos em f�runs deste tipo. */
	@Column(name = "permite_remover_topico", nullable = false)
	private boolean permiteRemoverTopico;
	
	/** Permite ao usu�rio interromper o t�pico (criando novo t�pico a partir de uma mensagem muito comentada). */
	@Column(name = "permite_interromper_topico", nullable = false)
	private boolean permiteInterromperTopico;

	/** Permite ao usu�rio responder t�picos de f�runs deste tipo. */
	@Column(name = "permite_responder_topico", nullable = false)
	private boolean permiteResponderTopico;
	
	/** Permite ao usu�rio comentar respostas de f�runs deste tipo. */
	@Column(name = "permite_comentar_resposta", nullable = false)
	private boolean permiteComentarResposta;
	
	/** Permite ao usu�rio ver mensagens dos t�picos do f�rum (discuss�o) antes de postar a sua primeira mensagem no t�pico. */
	@Column(name = "permite_ver_discussao_antes_postar", nullable = false)
	private boolean permiteVerDiscussaoAntesPostar;
	
	
	public TipoForum() {}
	
	public TipoForum(int id) {
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
	
	@Override
	public String toString() {
		return getDescricao();
	}

	public boolean isAtivo() {
	    return ativo;
	}

	public void setAtivo(boolean ativo) {
	    this.ativo = ativo;
	}

	@Override
	public ListaMensagens validate() {
		return null;
	}
	
	/** 
	 * Personaliza os componentes do f�rum.
	 * Ajusta a terminologia utilizada para cada tipo de mensagem.  
	 */
	public String getLabelMensagem() {
		if (labelMensagem == null) {
			return "Mensagem";
		}
		return labelMensagem;
	}

	public void setLabelMensagem(String labelMensagem) {
		this.labelMensagem = labelMensagem;
	}

	public boolean isPermiteCriarTopico() {
		return permiteCriarTopico;
	}

	public void setPermiteCriarTopico(boolean permiteCriarTopico) {
		this.permiteCriarTopico = permiteCriarTopico;
	}

	public boolean isPermiteInterromperTopico() {
		return permiteInterromperTopico;
	}

	public void setPermiteInterromperTopico(boolean permiteInterromperTopico) {
		this.permiteInterromperTopico = permiteInterromperTopico;
	}

	public boolean isPermiteResponderTopico() {
		return permiteResponderTopico;
	}

	public void setPermiteResponderTopico(boolean permiteResponderTopico) {
		this.permiteResponderTopico = permiteResponderTopico;
	}

	public boolean isPermiteComentarResposta() {
		return permiteComentarResposta;
	}

	public void setPermiteComentarResposta(boolean permiteComentarResposta) {
		this.permiteComentarResposta = permiteComentarResposta;
	}

	public boolean isPermiteRemoverTopico() {
		return permiteRemoverTopico;
	}

	public void setPermiteRemoverTopico(boolean permiteRemoverTopico) {
		this.permiteRemoverTopico = permiteRemoverTopico;
	}

	public boolean isPermiteVerDiscussaoAntesPostar() {
		return permiteVerDiscussaoAntesPostar;
	}

	public void setPermiteVerDiscussaoAntesPostar(
			boolean permiteVerDiscussaoAntesPostar) {
		this.permiteVerDiscussaoAntesPostar = permiteVerDiscussaoAntesPostar;
	}

}
