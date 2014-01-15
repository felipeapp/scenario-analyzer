/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 06/05/2013 
 */
package br.ufrn.sigaa.prodocente.negocio;

import java.util.List;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.prodocente.lattes.dominio.PessoaLattes;

/**
 * Movimento para a importação automática de vários currículos lattes a partir do
 * Web Service do CNPq.
 * 
 * @author Leonardo Campos
 *
 */
public class PessoaLattesMov extends MovimentoCadastro {

	/** Constante identifica a ação do processador de sincronizar os identificadores do CNPq entre a Plataforma Lattes e o banco institucional. */
	public static final int ACAO_SINCRONIZAR_IDS = 1;
	/** Constante identifica a ação do processador de verificar as datas de última atualização dos currículos entre a Plataforma Lattes e o banco institucional. */
	public static final int ACAO_VERIFICAR_DATAS = 2;
	/** Constante identifica a ação do processador de extrair os currículos da Plataforma Lattes e importar para o banco institucional. */
	public static final int ACAO_EXTRAIR_IMPORTAR_CVS = 3;
	
	/** Lista de pessoas consideradas na importação automática de currículos lattes */
	private List<PessoaLattes> pessoasLattes;
	
	public PessoaLattesMov() {
	}

	public List<PessoaLattes> getPessoasLattes() {
		return pessoasLattes;
	}

	public void setPessoasLattes(List<PessoaLattes> pessoasLattes) {
		this.pessoasLattes = pessoasLattes;
	}
	
	
}
