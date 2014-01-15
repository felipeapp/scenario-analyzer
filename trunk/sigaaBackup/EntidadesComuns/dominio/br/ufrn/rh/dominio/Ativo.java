package br.ufrn.rh.dominio;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Situação de Atividade do Servidor. Ex: ativo, aposentado, cedido, etc.
 *
 * @author Gleydson Lima
 *
 */
public class Ativo implements PersistDB {

	/* Os valores dessas constantes estao guardados no id */
	/** Indica que o servidor está ativo. */
	public static final int SERVIDOR_ATIVO = 1;
	/** Indica que o servidor está aposentado. */
	public static final int APOSENTADO = 2;
	/** Indica que o servidor é um instituidor. */
	public static final int INSTITUIDOR = 3;
	/** Indica que o servidor foi excluído. */
	public static final int EXCLUIDO = 4;
	/** Indica que o servidor está com o pagamento suspenso. */
	public static final int PGTO_SUSPENSO = 5;
	/** Indica que o servidor é um beneficiário de pensão. */
	public static final int BEN_PENSAO = 6;
	/** Indica que o servidor foi cedido à outra instituição. */
	public static final int CEDIDO = 7;
	/** Indica que o servidor é externo à instituição. */
	public static final int EXTERNO = 8;
	/** Indica que o servidor é residente. */
	public static final int RESIDENTE = 9;
	/** Indica que a situação de atividade do servidor não foi informada. */
	public static final int NAO_INFORMADO = 10;
	
	/** Identificador da entidade */
	private int id;

	/** Descrição da situação de atividade */
	private String descricao;
	
	/** Indica se na situação o servidor terá acesso ao sistema de Gestão de Recursos Humanos. */
	private boolean permiteAcessoSIGRH;

	public Ativo() {

	}

	public Ativo(int id) {
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

	public boolean isPermiteAcessoSIGRH() {
		return permiteAcessoSIGRH;
	}

	public void setPermiteAcessoSIGRH(boolean permiteAcessoSIGRH) {
		this.permiteAcessoSIGRH = permiteAcessoSIGRH;
	}

}
