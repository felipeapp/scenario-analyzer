/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 18/08/2010
 */
package br.ufrn.sigaa.ava.dominio;



/**
 * 
 * Enum que lista os diversos tipos de a��es que podem ser realizadas na Turma Virtual. 
 * 
 * @author Fred_Castro
 * 
 */
public enum AcaoAva {
	
	/** A��o de acessar uma entidade ou p�gina na turma virtual. */
	ACESSAR (1, "Acessar", true, false, true),
	/** A��o de iniciar inser��o de uma entidade na turma virtual. */
	INICIAR_INSERCAO (2, "Iniciar inser��o", false, false, false),
	/** A��o de inserir uma entidade na turma virtual. */
	INSERIR (3, "Inserir", true, false, true),
	/** A��o de iniciar altera��o de entidade na turma virtual. */
	INICIAR_ALTERACAO (4, "Iniciar altera��o", true, false, false),
	/** A��o de alterar uma entidade na turma virtual. */
	ALTERAR (5, "Alterar", true, false, true),
	/** A��o de iniciar remo��o de entidade na turma virtual. */
	INICIAR_REMOCAO (6, "Iniciar remo��o", true, false, false),
	/** A��o de remover uma entidade na turma virtual. */
	REMOVER (7, "Remover", true, false, true),
	/** A��o de iniciar resposta de entidade na turma virtual. */
	INICIAR_RESPOSTA (8, "Iniciar resposta", true, false, false),
	/** A��o de responder uma entidade na turma virtual. */
	RESPONDER (9, "Responder", true, false, true),
	/** A��o de iniciar corre��o de entidade na turma virtual. */
	INICIAR_CORRECAO (10, "Iniciar corre��o", true, true, false),
	/** A��o de corrigir uma entidade na turma virtual. */
	CORRIGIR (11, "Corrigir", true, true, true),
	/** A��o de iniciar remo��o de resposta entidade na turma virtual. */
	INICIAR_REMOCAO_RESPOSTA (12, "Iniciar remo��o de resposta", true, true, false),
	/** A��o de remover uma resposta na turma virtual. */
	REMOVER_RESPOSTA (13, "Remover resposta", true, true, true),
	/** A��o de listar v�rias entidades. */
	LISTAR (14, "Listar", false, false, true),
	/** A��o de iniciar inativa��o de entidade na turma virtual. */
	INICIAR_INATIVACAO (15, "Iniciar inativa��o", true, false, false),
	/** A��o de inativar uma entidade na turma virtual. */
	INATIVAR (16, "Inativar", true, false, true),
	/** A��o de iniciar um envio de entidade na turma virtual. */
	INICIAR_ENVIO (17, "Iniciar envio", true, false, false),
	/** A��o de enviar uma mensagem na turma virtual. */
	ENVIAR (18, "Enviar", true, false, true),
	/** A��o de iniciar a salvar uma entidade na turma virtual. */
	INICIAR_SALVAR (19, "Iniciar � salvar", true, false, false),
	/** A��o de salvar uma entidade turma virtual. */
	SALVAR (20, "Salvar", true, false, true);
	
	/** Valor da a��o na enumera��o. */
	private int valor;
	/** Descri��o da a��o */
	private String descricao;
	/** Se a a��o precisa de uma entidade */
	private boolean requerEntidade;
	/** Se a a��o precisa de uma entidade complementar */
	private boolean requerEntidadeComplementar;
	/** Se a a��o deve ser exibida nos filtros do relat�rio de a��es */
	private boolean exibirNosFiltros;
	
	AcaoAva (int valor, String descricao, boolean requerEntidade, boolean requerEntidadeComplementar, boolean exibirNosFiltros){
		this.valor = valor;
		this.descricao = descricao;
		this.requerEntidade = requerEntidade;
		this.requerEntidadeComplementar = requerEntidadeComplementar;
		this.exibirNosFiltros = exibirNosFiltros;
	}

	public int getValor() {
		return valor;
	}

	public void setValor(int valor) {
		this.valor = valor;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public boolean isRequerEntidade() {
		return requerEntidade;
	}

	public void setRequerEntidade (boolean requerEntidade) {
		this.requerEntidade = requerEntidade;
	}

	public boolean isRequerEntidadeComplementar() {
		return requerEntidadeComplementar;
	}

	public void setRequerEntidadeComplementar (boolean requerEntidadeComplementar) {
		this.requerEntidadeComplementar = requerEntidadeComplementar;
	}
	
	public boolean isExibirNosFiltros() {
		return exibirNosFiltros;
	}

	public void setExibirNosFiltros(boolean exibirNosFiltros) {
		this.exibirNosFiltros = exibirNosFiltros;
	}

	/**
	 * Retorna a a��o associada a enumera��o.<br/><br/>
	 * N�o � chamado em JSPs.
	 */
	public static AcaoAva getAcaoAva (int acao){
		for (AcaoAva a : AcaoAva.values())
			if (a.getValor() == acao)
				return a;
		return null;
	}
	
}