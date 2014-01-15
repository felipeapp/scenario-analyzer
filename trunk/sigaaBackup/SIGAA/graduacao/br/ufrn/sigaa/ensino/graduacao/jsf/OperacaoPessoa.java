/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import java.util.HashMap;

/**
 * Classe auxiliar para controle da utiliza��o do Managed Bean da busca de
 * pessoas
 *
 * @author Andre M Dantas
 *
 */
public class OperacaoPessoa {

	/** Nome do Managed Bean respons�vel pela opera��o */
	private String mBean;

	/** Nome da opera��o (Para utiliza��o no t�tulo da p�gina de busca da pessoa) */
	private String nome;

	/** C�digos das Opera��es */
	/** Opera��o para tutor presencial */
	public static final int TUTOR_ORIENTADOR = 1;
	/** Opera��o para coordenador de um p�lo */
	public static final int COORDENADOR_POLO = 2;
	/** Opera��o para coordenador de tutoria */
	public static final int COORDENADOR_TUTORIA = 3;
	/** Opera��o para permiss�o ava */
	public static final int PERMISSAO_AVA = 4;
	/** Opera��o para cadatro de membro em comunidade virtual */
	public static final int CADASTRA_MEMBRO_CV = 5;
	/** Opera��o para tutor � dist�ncia */
	public static final int TUTOR_DISTANCIA = 6;

	/** HashMap com as Opera��es Dispon�veis */
	private static HashMap<Integer, OperacaoPessoa> mapa;
	static {
		mapa = new HashMap<Integer, OperacaoPessoa>();
		mapa.put(TUTOR_ORIENTADOR, new OperacaoPessoa("tutorOrientador", "Cadastro de Tutor Orientador"));
		mapa.put(COORDENADOR_POLO, new OperacaoPessoa("coordenacaoPolo", "Cadastro de Coordenador de P�lo"));
		mapa.put(COORDENADOR_TUTORIA, new OperacaoPessoa("coordenacaoTutoria", "Cadastro de Coordenador de Tutoria"));
		mapa.put(PERMISSAO_AVA, new OperacaoPessoa("permissaoAva", "Cadastro de Permiss�es da Turma"));
		mapa.put(CADASTRA_MEMBRO_CV, new OperacaoPessoa("membroComunidadeMBean", "Cadastro de Membros da Comunidade"));
		mapa.put(TUTOR_DISTANCIA, new OperacaoPessoa("tutorDistancia", "Cadastro de Tutor � Dist�ncia"));

	}

	public OperacaoPessoa() {
	}

	public OperacaoPessoa(String mBean, String nome) {
		this.mBean = mBean;
		this.nome = nome;
	}

	public static OperacaoPessoa getOperacao(int codigoOperacao) {
		return mapa.get(codigoOperacao);
	}

	public String getMBean() {
		return mBean;
	}

	public void setMBean(String bean) {
		mBean = bean;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}



}
