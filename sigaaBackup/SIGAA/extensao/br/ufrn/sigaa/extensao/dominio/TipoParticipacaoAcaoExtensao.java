/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Representa qual o tipo de participação que houve. <br/>
 * PALESTRANTE, ORIENTADOR, ASSESSOR, ORGANIZADOR
 * 
 * @author Gleydson
 * 
 ******************************************************************************/
@Entity
@Table(schema = "extensao", name = "tipo_participacao")
public class TipoParticipacaoAcaoExtensao implements Validatable {

	/** 
	 *  Esses são os tipo fixos no sistema, pelo amor de Deus, não cadastre nem remove do banco.
	 */
	
	
	/** Atributo utilizado para representar o tipo de participação como Palestrante */
	public static final int PALESTRANTE = 1;
	/** Atributo utilizado para representar o tipo de participação como Instrutor  */
	public static final int INSTRUTOR = 2;
	/** Atributo utilizado para representar o tipo de participação como Autor */
	public static final int AUTOR = 3;
	/** Atributo utilizado para representar o tipo de participação como Orientador */
	public static final int ORIENTADOR = 4;
	/** Atributo utilizado para representar o tipo de participação como Aluno */
	public static final int ALUNO = 5;
	/** Atributo utilizado para representar o tipo de participação como Ouvinte */
	public static final int OUVINTE = 6;
	
	/** 
	 * Atributo utilizado para representar o tipo de participação como Participante 
	 *
	 * <strong>ESSE É O VALOR ATRIBUÍDO AO PARTICIPANTE POR PADRÃO, QUANDO O COORDENADOR APROVA SUA INSCRIÇÃO.</strong>
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

	/** A descrição do tipo de participante */
	private String descricao;

	/**
	 * Caso seja nulo o tipo serve para todas
	 */
	@ManyToOne
	@JoinColumn(name = "id_tipo_acao_extensao", nullable=true)
	private TipoAtividadeExtensao tipoAcaoExtensao;

	
	/** Isso aqui não era para exitir, porque não deve ser possível cadastrar 
	 * novos tipos ou remover existentes, já que fazem parte da regra de negócio do sistema, não 
	 * era para está sendo persitidos. Era para fixar fixo no código.*/
	private boolean ativo = true;

	
	/**
	 * Verifica se é um tipo fixo no sistema o tipo fixo não pode ser cadastrados nem alterados pelo usuários.
	 * 
	 * Os tipo fixos estão com o id hardcode nessa classe e não possuem tipo de ação associado.
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
	 * Valida Descrição e Tipo de Atividade de Extensão.
	 */
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descrição", erros);
		ValidatorUtil.validateRequired(tipoAcaoExtensao, "Tipo de Atividade de Extensão", erros);
		return erros;
	}
}
