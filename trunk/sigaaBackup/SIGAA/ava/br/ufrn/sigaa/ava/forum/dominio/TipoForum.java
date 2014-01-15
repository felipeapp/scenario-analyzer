/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Representa o tipo de fórum. 
 * 
 * @author Ilueny Santos
 *
 */
@Entity
@Table(name = "tipo_forum", schema = "ava")
public class TipoForum implements Validatable {
	
	/** Única Discussão simples */
	public static final int TIPO_DISCUSSAO_SIMPLES = 10;	
	/** Fórum no estilo Perguntas e Respostas */
	public static final int TIPO_PERGUNTAS_RESPOSTAS = 20;	
	/** Fórum Geral */
	public static final int TIPO_GERAL = 30;	
	/** Padrão exibir como blog */
	public static final int TIPO_BLOG = 40;	
	/** Cada usuário inicia apenas um novo tópico */
	public static final int TIPO_NOVO_TOPICO = 50;
	
	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name="id_tipo_forum")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	/** Descrição do tipo de fórum. */
	@Column(name = "descricao", nullable = false)
	private String descricao;
	
	/** Indica se é um tipo de fórum válido. Utilizado para exclusão lógica. */
	@CampoAtivo
	private boolean ativo;
	
	/** 
	 * Informa o tipo de tratamento que deve ser dado a uma mensagem. 
	 * Personaliza os componentes do fórum e ajusta a terminologia utilizada para cada tipo de mensagem.  
	 * 
	 * Dependendo do tipo de fórum, um tópico pode ser tratado como 'Questão', 'Notícia', 'Tópico de Discussão', etc.
	 */	
	@Column(name = "label_mensagem")
	private String labelMensagem;
	
	/** Permite ao usuário criar novos tópicos em fóruns deste tipo. */
	@Column(name = "permite_criar_topico", nullable = false)
	private boolean permiteCriarTopico;

	/** Permite ao usuário remover tópicos em fóruns deste tipo. */
	@Column(name = "permite_remover_topico", nullable = false)
	private boolean permiteRemoverTopico;
	
	/** Permite ao usuário interromper o tópico (criando novo tópico a partir de uma mensagem muito comentada). */
	@Column(name = "permite_interromper_topico", nullable = false)
	private boolean permiteInterromperTopico;

	/** Permite ao usuário responder tópicos de fóruns deste tipo. */
	@Column(name = "permite_responder_topico", nullable = false)
	private boolean permiteResponderTopico;
	
	/** Permite ao usuário comentar respostas de fóruns deste tipo. */
	@Column(name = "permite_comentar_resposta", nullable = false)
	private boolean permiteComentarResposta;
	
	/** Permite ao usuário ver mensagens dos tópicos do fórum (discussão) antes de postar a sua primeira mensagem no tópico. */
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
	 * Personaliza os componentes do fórum.
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
