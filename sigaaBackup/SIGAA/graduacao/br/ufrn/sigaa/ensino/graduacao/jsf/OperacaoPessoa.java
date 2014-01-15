/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import java.util.HashMap;

/**
 * Classe auxiliar para controle da utilização do Managed Bean da busca de
 * pessoas
 *
 * @author Andre M Dantas
 *
 */
public class OperacaoPessoa {

	/** Nome do Managed Bean responsável pela operação */
	private String mBean;

	/** Nome da operação (Para utilização no título da página de busca da pessoa) */
	private String nome;

	/** Códigos das Operações */
	/** Operação para tutor presencial */
	public static final int TUTOR_ORIENTADOR = 1;
	/** Operação para coordenador de um pólo */
	public static final int COORDENADOR_POLO = 2;
	/** Operação para coordenador de tutoria */
	public static final int COORDENADOR_TUTORIA = 3;
	/** Operação para permissão ava */
	public static final int PERMISSAO_AVA = 4;
	/** Operação para cadatro de membro em comunidade virtual */
	public static final int CADASTRA_MEMBRO_CV = 5;
	/** Operação para tutor à distância */
	public static final int TUTOR_DISTANCIA = 6;

	/** HashMap com as Operações Disponíveis */
	private static HashMap<Integer, OperacaoPessoa> mapa;
	static {
		mapa = new HashMap<Integer, OperacaoPessoa>();
		mapa.put(TUTOR_ORIENTADOR, new OperacaoPessoa("tutorOrientador", "Cadastro de Tutor Orientador"));
		mapa.put(COORDENADOR_POLO, new OperacaoPessoa("coordenacaoPolo", "Cadastro de Coordenador de Pólo"));
		mapa.put(COORDENADOR_TUTORIA, new OperacaoPessoa("coordenacaoTutoria", "Cadastro de Coordenador de Tutoria"));
		mapa.put(PERMISSAO_AVA, new OperacaoPessoa("permissaoAva", "Cadastro de Permissões da Turma"));
		mapa.put(CADASTRA_MEMBRO_CV, new OperacaoPessoa("membroComunidadeMBean", "Cadastro de Membros da Comunidade"));
		mapa.put(TUTOR_DISTANCIA, new OperacaoPessoa("tutorDistancia", "Cadastro de Tutor à Distância"));

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
