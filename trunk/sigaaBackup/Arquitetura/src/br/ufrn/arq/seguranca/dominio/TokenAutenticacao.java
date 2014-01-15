/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/04/2009
 */
package br.ufrn.arq.seguranca.dominio;

import java.util.Date;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.comum.dominio.Sistema;

/**
 * Representa um token para autenticação que 
 * permite que um usuário acesse um recurso existente
 * em outro sistema sem a necessidade de efetuar logon.
 *  
 * @author David Pereira
 *
 */
public class TokenAutenticacao {

	/** Identificador do token */
	private int id;
	
	/** Chave do token. */
	private String key;
	
	/** Data de criação do token. */
	private Date data;
	
	/** Registro de entrada do usuário que criou o token. */
	private RegistroEntrada entrada;
	
	/** Sistema de origem do usuário que criou o token. */
	private Sistema origem;
	
	/** Se o token já foi usado ou não. Se já tiver sido usado, é inválido. */
	private boolean valido;

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public RegistroEntrada getEntrada() {
		return entrada;
	}

	public void setEntrada(RegistroEntrada entrada) {
		this.entrada = entrada;
	}

	public Sistema getOrigem() {
		return origem;
	}

	public void setOrigem(Sistema origem) {
		this.origem = origem;
	}

	public boolean isValido() {
		return valido;
	}

	public void setValido(boolean valido) {
		this.valido = valido;
	}
	
}
