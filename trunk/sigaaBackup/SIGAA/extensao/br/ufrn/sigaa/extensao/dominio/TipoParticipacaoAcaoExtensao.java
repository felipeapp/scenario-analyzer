/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 14/10/2008
 *
 */
package br.ufrn.sigaa.extensao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;

/*******************************************************************************
 * Representa qual o tipo de participa��o que houve. <br/>
 * PALESTRANTE, ORIENTADOR, ASSESSOR, ORGANIZADOR
 * 
 * @author Gleydson
 * 
 ******************************************************************************/
@Entity
@Table(schema = "extensao", name = "tipo_participacao")
public class TipoParticipacaoAcaoExtensao implements Validatable {

	/** 
	 *  Esses s�o os tipo fixos no sistema, pelo amor de Deus, n�o cadastre nem remove do banco.
	 */
	
	
	/** Atributo utilizado para representar o tipo de participa��o como Palestrante */
	public static final int PALESTRANTE = 1;
	/** Atributo utilizado para representar o tipo de participa��o como Instrutor  */
	public static final int INSTRUTOR = 2;
	/** Atributo utilizado para representar o tipo de participa��o como Autor */
	public static final int AUTOR = 3;
	/** Atributo utilizado para representar o tipo de participa��o como Orientador */
	public static final int ORIENTADOR = 4;
	/** Atributo utilizado para representar o tipo de participa��o como Aluno */
	public static final int ALUNO = 5;
	/** Atributo utilizado para representar o tipo de participa��o como Ouvinte */
	public static final int OUVINTE = 6;
	
	/** 
	 * Atributo utilizado para representar o tipo de participa��o como Participante 
	 *
	 * <strong>ESSE � O VALOR ATRIBU�DO AO PARTICIPANTE POR PADR�O, QUANDO O COORDENADOR APROVA SUA INSCRI��O.</strong>
	 * 
	 * Se ele quiser um tipo diferente, ele tem que alterar um por um e atribuir.
	 */
	public static final int PARTICIPANTE = 7;
	
	
	public TipoParticipacaoAcaoExtensao(){
		
	}
	
	public TipoParticipacaoAcaoExtensao(int id){
		this.id = id;
	}
	
	public TipoParticipacaoAcaoExtensao(String descricao){
		this.descricao = descricao;
	}
	
	public TipoParticipacaoAcaoExtensao(int id, String descricao){
		this(id);
		this.descricao = descricao;
	}
	
	/** O id*/
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_tipo_participacao")
	private int id;

	/** A descri��o do tipo de participante */
	private String descricao;

	/**
	 * Caso seja nulo o tipo serve para todas
	 */
	@ManyToOne
	@JoinColumn(name = "id_tipo_acao_extensao", nullable=true)
	private TipoAtividadeExtensao tipoAcaoExtensao;

	
	/** Isso aqui n�o era para exitir, porque n�o deve ser poss�vel cadastrar 
	 * novos tipos ou remover existentes, j� que fazem parte da regra de neg�cio do sistema, n�o 
	 * era para est� sendo persitidos. Era para fixar fixo no c�digo.*/
	private boolean ativo = true;

	
	/**
	 * Verifica se � um tipo fixo no sistema o tipo fixo n�o pode ser cadastrados nem alterados pelo usu�rios.
	 * 
	 * Os tipo fixos est�o com o id hardcode nessa classe e n�o possuem tipo de a��o associado.
	 *
	 */
	public boolean isTipoParticipacaoAcaoExtensaoFixaNoSistema() {
		if(tipoAcaoExtensao == null || tipoAcaoExtensao.getId() <= 0)
			return true;
		else 
			return false;
	}
	
	
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
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

	public TipoAtividadeExtensao getTipoAcaoExtensao() {
		return tipoAcaoExtensao;
	}

	public void setTipoAcaoExtensao(TipoAtividadeExtensao tipoAcaoExtensao) {
		this.tipoAcaoExtensao = tipoAcaoExtensao;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(getId());
	}

	/**
	 * Valida Descri��o e Tipo de Atividade de Extens�o.
	 */
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descri��o", erros);
		ValidatorUtil.validateRequired(tipoAcaoExtensao, "Tipo de Atividade de Extens�o", erros);
		return erros;
	}
}
