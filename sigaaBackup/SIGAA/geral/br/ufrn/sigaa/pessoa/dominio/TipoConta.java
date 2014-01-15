package br.ufrn.sigaa.pessoa.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 * Serve para determinar os tipos de conta existentes. 
 *  
 * @author Jean Guerethes
 */
public class TipoConta implements Validatable {

	public static final int CONTA_CORRENTE = 1;
	public static final int CONTA_POUPANCA = 2;
	public static final int CONTA_SALARIO = 3;
	
	private int id;
	private String descricao;
	private static Collection<TipoConta> tipoConta = new ArrayList<TipoConta>();
	public static Map<Integer, String> tabela = new HashMap<Integer, String>();
	
	public TipoConta() {
	}

	public TipoConta( int id, String descricao ) {
		this.id = id;
		this.descricao = descricao;
	}
	
	public static Collection<TipoConta> getAlltipoConta() {
		if ( tipoConta.isEmpty() ) {
			tipoConta.add(new TipoConta(CONTA_CORRENTE, "Conta Corrente"));
			tipoConta.add(new TipoConta(CONTA_POUPANCA, "Conta Poupança"));
			tipoConta.add(new TipoConta(CONTA_SALARIO, "Conta Salário"));
		}
		
		return tipoConta;
	}
	
	static {
		tabela.put(CONTA_CORRENTE, "Conta Corrente");
		tabela.put(CONTA_POUPANCA, "Conta Poupança");
		tabela.put(CONTA_SALARIO, "Conta Salário");
	}
	
	public static String getDescricao(int idTipoConta) {
		return tabela.get(idTipoConta);
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
	
	public static Collection<TipoConta> getTipoConta() {
		return tipoConta;
	}

	public static void setTipoConta(Collection<TipoConta> tipoConta) {
		TipoConta.tipoConta = tipoConta;
	}
	
	public static Map<Integer, String> getTabela() {
		return tabela;
	}

	public static void setTabela(Map<Integer, String> tabela) {
		TipoConta.tabela = tabela;
	}

	@Override
	public ListaMensagens validate() {
		return null;
	}
	
}