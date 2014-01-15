/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 18/08/2010
 */
package br.ufrn.sigaa.ava.dominio;



/**
 * 
 * Enum que lista os diversos tipos de ações que podem ser realizadas na Turma Virtual. 
 * 
 * @author Fred_Castro
 * 
 */
public enum AcaoAva {
	
	/** Ação de acessar uma entidade ou página na turma virtual. */
	ACESSAR (1, "Acessar", true, false, true),
	/** Ação de iniciar inserção de uma entidade na turma virtual. */
	INICIAR_INSERCAO (2, "Iniciar inserção", false, false, false),
	/** Ação de inserir uma entidade na turma virtual. */
	INSERIR (3, "Inserir", true, false, true),
	/** Ação de iniciar alteração de entidade na turma virtual. */
	INICIAR_ALTERACAO (4, "Iniciar alteração", true, false, false),
	/** Ação de alterar uma entidade na turma virtual. */
	ALTERAR (5, "Alterar", true, false, true),
	/** Ação de iniciar remoção de entidade na turma virtual. */
	INICIAR_REMOCAO (6, "Iniciar remoção", true, false, false),
	/** Ação de remover uma entidade na turma virtual. */
	REMOVER (7, "Remover", true, false, true),
	/** Ação de iniciar resposta de entidade na turma virtual. */
	INICIAR_RESPOSTA (8, "Iniciar resposta", true, false, false),
	/** Ação de responder uma entidade na turma virtual. */
	RESPONDER (9, "Responder", true, false, true),
	/** Ação de iniciar correção de entidade na turma virtual. */
	INICIAR_CORRECAO (10, "Iniciar correção", true, true, false),
	/** Ação de corrigir uma entidade na turma virtual. */
	CORRIGIR (11, "Corrigir", true, true, true),
	/** Ação de iniciar remoção de resposta entidade na turma virtual. */
	INICIAR_REMOCAO_RESPOSTA (12, "Iniciar remoção de resposta", true, true, false),
	/** Ação de remover uma resposta na turma virtual. */
	REMOVER_RESPOSTA (13, "Remover resposta", true, true, true),
	/** Ação de listar várias entidades. */
	LISTAR (14, "Listar", false, false, true),
	/** Ação de iniciar inativação de entidade na turma virtual. */
	INICIAR_INATIVACAO (15, "Iniciar inativação", true, false, false),
	/** Ação de inativar uma entidade na turma virtual. */
	INATIVAR (16, "Inativar", true, false, true),
	/** Ação de iniciar um envio de entidade na turma virtual. */
	INICIAR_ENVIO (17, "Iniciar envio", true, false, false),
	/** Ação de enviar uma mensagem na turma virtual. */
	ENVIAR (18, "Enviar", true, false, true),
	/** Ação de iniciar a salvar uma entidade na turma virtual. */
	INICIAR_SALVAR (19, "Iniciar à salvar", true, false, false),
	/** Ação de salvar uma entidade turma virtual. */
	SALVAR (20, "Salvar", true, false, true);
	
	/** Valor da ação na enumeração. */
	private int valor;
	/** Descrição da ação */
	private String descricao;
	/** Se a ação precisa de uma entidade */
	private boolean requerEntidade;
	/** Se a ação precisa de uma entidade complementar */
	private boolean requerEntidadeComplementar;
	/** Se a ação deve ser exibida nos filtros do relatório de ações */
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
	 * Retorna a ação associada a enumeração.<br/><br/>
	 * Não é chamado em JSPs.
	 */
	public static AcaoAva getAcaoAva (int acao){
		for (AcaoAva a : AcaoAva.values())
			if (a.getValor() == acao)
				return a;
		return null;
	}
	
}