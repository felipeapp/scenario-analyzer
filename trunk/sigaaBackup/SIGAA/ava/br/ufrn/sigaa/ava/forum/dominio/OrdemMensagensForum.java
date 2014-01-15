/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Representa a ordena��o padr�o utilizada no f�rum. 
 * 
 * @author Ilueny Santos
 *
 */
@Entity
@Table(name = "ordem_mensagens_forum", schema = "ava")
public class OrdemMensagensForum implements Validatable {
	
	/** Mostrar respostas come�ando pela mais antiga */
	public static final int MAIS_ANTIGAS_PRIMEIRO 		= 1;	
	/** Mostrar respostas come�ando pela mais recente */
	public static final int MAIS_RECENTES_PRIMEIRO	 	= 2;	
	/** Listar respostas */
	public static final int LISTAR_RESPOSTAS			= 3;	
	/** Mostar respostas aninhadas */
	public static final int RESPOSTAS_ANINHADAS 		= 4;	
		
	
	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name="id_ordem_mensagens_forum")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	/** Descri��o da ordena��o das mensagens do f�rum. */
	@Column(name = "descricao", nullable = false)
	private String descricao;
	
	/** Indica se � uma ordena��o v�lida. Utilizado para exclus�o l�gica. */
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
	
	/** Permite exibi��o de mensagens aninhadas. */
	public boolean isRespostasAninhadas() {
		return this.id == RESPOSTAS_ANINHADAS || this.id == LISTAR_RESPOSTAS;
	}
	
	/** Verifica se o conte�do das mensagens pode ser exibido de acordo com o tipo de ordena��o. */
	public boolean isRespostasComConteudo() {
		return  !(this.id == LISTAR_RESPOSTAS);
	}
}

