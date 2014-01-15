/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Criado em: 06/05/2013 
 */
package br.ufrn.sigaa.prodocente.negocio;

import java.util.List;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.prodocente.lattes.dominio.PessoaLattes;

/**
 * Movimento para a importa��o autom�tica de v�rios curr�culos lattes a partir do
 * Web Service do CNPq.
 * 
 * @author Leonardo Campos
 *
 */
public class PessoaLattesMov extends MovimentoCadastro {

	/** Constante identifica a a��o do processador de sincronizar os identificadores do CNPq entre a Plataforma Lattes e o banco institucional. */
	public static final int ACAO_SINCRONIZAR_IDS = 1;
	/** Constante identifica a a��o do processador de verificar as datas de �ltima atualiza��o dos curr�culos entre a Plataforma Lattes e o banco institucional. */
	public static final int ACAO_VERIFICAR_DATAS = 2;
	/** Constante identifica a a��o do processador de extrair os curr�culos da Plataforma Lattes e importar para o banco institucional. */
	public static final int ACAO_EXTRAIR_IMPORTAR_CVS = 3;
	
	/** Lista de pessoas consideradas na importa��o autom�tica de curr�culos lattes */
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
