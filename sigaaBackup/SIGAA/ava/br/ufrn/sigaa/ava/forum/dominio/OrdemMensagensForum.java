/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '13/04/2011'
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
 * Representa a ordenação padrão utilizada no fórum. 
 * 
 * @author Ilueny Santos
 *
 */
@Entity
@Table(name = "ordem_mensagens_forum", schema = "ava")
public class OrdemMensagensForum implements Validatable {
	
	/** Mostrar respostas começando pela mais antiga */
	public static final int MAIS_ANTIGAS_PRIMEIRO 		= 1;	
	/** Mostrar respostas começando pela mais recente */
	public static final int MAIS_RECENTES_PRIMEIRO	 	= 2;	
	/** Listar respostas */
	public static final int LISTAR_RESPOSTAS			= 3;	
	/** Mostar respostas aninhadas */
	public static final int RESPOSTAS_ANINHADAS 		= 4;	
		
	
	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name="id_ordem_mensagens_forum")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	/** Descrição da ordenação das mensagens do fórum. */
	@Column(name = "descricao", nullable = false)
	private String descricao;
	
	/** Indica se é uma ordenação válida. Utilizado para exclusão lógica. */
	@CampoAtivo
	private boolean ativo;
	
	
	public OrdemMensagensForum() {
	}
	
	public OrdemMensagensForum(int id) {
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
	
	/** Permite exibição de mensagens aninhadas. */
	public boolean isRespostasAninhadas() {
		return this.id == RESPOSTAS_ANINHADAS || this.id == LISTAR_RESPOSTAS;
	}
	
	/** Verifica se o conteúdo das mensagens pode ser exibido de acordo com o tipo de ordenação. */
	public boolean isRespostasComConteudo() {
		return  !(this.id == LISTAR_RESPOSTAS);
	}
}

