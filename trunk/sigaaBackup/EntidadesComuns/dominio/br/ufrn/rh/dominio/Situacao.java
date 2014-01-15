package br.ufrn.rh.dominio;

import br.ufrn.arq.dominio.PersistDB;

/**
 *
 * Entidade que representa diversas situações dos servidores: ativo permanente, aposentado, cedido, etc.
 *
 * @author Gleydson Lima
 *
 */
public class Situacao implements PersistDB  {

	public static final int ATIVO_PERMANENTE = 1;
	
	public static final int APOSENTADO = 2;
	
	public static final int CEDIDO = 8;

	public static final int BENEFICIARIO_PENSAO = 50;
	
	public static final int CONTRATO_TEMPORARIO = 12;
	
	public static final int EXERCICIO_PROVISORIO = 19;
	
	/** Identificador */
	private int id;

	/** Descrição da situação */
	private String descricao;

	public Situacao() {
		
	}
	
	public Situacao(int id) {
		this.id = id;
	}
	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
