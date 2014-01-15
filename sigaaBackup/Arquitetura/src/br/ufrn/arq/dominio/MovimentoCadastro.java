/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 13/12/2006
 */
package br.ufrn.arq.dominio;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.comum.dominio.UsuarioGeral;


/**
 * Movimento padrão para operações de cadastro.
 * 
 * @author Gleydson Lima
 *
 */
@SuppressWarnings("serial")
public class MovimentoCadastro extends AbstractMovimento {

	public static final int ACAO_CRIAR = 1;
	public static final int ACAO_ALTERAR = 2;
	public static final int ACAO_REMOVER = 3;
	public static final int ACAO_CONSULTAR = 4;
	public static final int SEM_ACAO = 1000;

	private int id;
	/**Acao a ser executada sobre o objeto movimentado (Inserir, Editar ou Remover)*/
	private int acao;
	/**Objeto a ser movimentado (Inserido, Editado ou Removido)*/
	private PersistDB objMovimentado;
	private Object objAuxiliar;
	private int[] papeis;

	/**Coleção de Objetos a ser movimentado (Inserido, Editado ou Removido)*/
	private Collection<? extends PersistDB> colObjMovimentado;

	private RegistroEntrada registroEntrada;
	
	private RegistroAcessoPublico registroAcessoPublico;
	
	/** Enviado aos processadores para poder acessar os métodos de autenticação. */
	private HttpServletRequest request;
	
	/**
	 * @return Retorna o(a) papeis.
	 */
	public int[] getPapeis() {
		return papeis;
	}

	/**
	 * @param papeis Altera o(a) papeis.
	 */
	public void setPapeis(int[] papeis) {
		this.papeis = papeis;
	}

	public MovimentoCadastro(){
	}

	public MovimentoCadastro(PersistDB objMovimentado) {
		this.objMovimentado = objMovimentado;
	}

	public MovimentoCadastro(PersistDB objMovimentado, Comando comando) {
		this(objMovimentado);
		this.setCodMovimento(comando);
	}
	
	/**
	 *
	 * @param comando
	 * @param acao
	 */
	public MovimentoCadastro(Comando comando, int acao, UsuarioGeral usuarioLogado){
		setCodMovimento(comando);
		setAcao(acao);
		setUsuarioLogado(usuarioLogado);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAcao() {
		return acao;
	}

	public void setAcao(int acao) {
		this.acao = acao;
	}

	@SuppressWarnings("unchecked")
	public <T extends PersistDB> T getObjMovimentado() {
		return (T) objMovimentado;
	}

	public void setObjMovimentado(PersistDB objMovimentado) {
		this.objMovimentado = objMovimentado;
	}

	public Collection<? extends PersistDB> getColObjMovimentado() {
		return colObjMovimentado;
	}

	public void setColObjMovimentado(
			Collection<? extends PersistDB> colObjMovimentado) {
		this.colObjMovimentado = colObjMovimentado;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public RegistroAcessoPublico getRegistroAcessoPublico() {
		return registroAcessoPublico;
	}

	public void setRegistroAcessoPublico(RegistroAcessoPublico registroAcessoPublico) {
		this.registroAcessoPublico = registroAcessoPublico;
	}

	public Object getObjAuxiliar() {
		return objAuxiliar;
	}

	public void setObjAuxiliar(Object objAuxiliar) {
		this.objAuxiliar = objAuxiliar;
	}

}
