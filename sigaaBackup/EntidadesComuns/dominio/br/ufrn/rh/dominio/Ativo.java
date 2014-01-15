package br.ufrn.rh.dominio;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Situa��o de Atividade do Servidor. Ex: ativo, aposentado, cedido, etc.
 *
 * @author Gleydson Lima
 *
 */
public class Ativo implements PersistDB {

	/* Os valores dessas constantes estao guardados no id */
	/** Indica que o servidor est� ativo. */
	public static final int SERVIDOR_ATIVO = 1;
	/** Indica que o servidor est� aposentado. */
	public static final int APOSENTADO = 2;
	/** Indica que o servidor � um instituidor. */
	public static final int INSTITUIDOR = 3;
	/** Indica que o servidor foi exclu�do. */
	public static final int EXCLUIDO = 4;
	/** Indica que o servidor est� com o pagamento suspenso. */
	public static final int PGTO_SUSPENSO = 5;
	/** Indica que o servidor � um benefici�rio de pens�o. */
	public static final int BEN_PENSAO = 6;
	/** Indica que o servidor foi cedido � outra institui��o. */
	public static final int CEDIDO = 7;
	/** Indica que o servidor � externo � institui��o. */
	public static final int EXTERNO = 8;
	/** Indica que o servidor � residente. */
	public static final int RESIDENTE = 9;
	/** Indica que a situa��o de atividade do servidor n�o foi informada. */
	public static final int NAO_INFORMADO = 10;
	
	/** Identificador da entidade */
	private int id;

	/** Descri��o da situa��o de atividade */
	private String descricao;
	
	/** Indica se na situa��o o servidor ter� acesso ao sistema de Gest�o de Recursos Humanos. */
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
