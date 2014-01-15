package br.ufrn.sigaa.extensao.jsf.helper;

import static br.ufrn.sigaa.extensao.jsf.ConstantesNavegacao.ANEXAR_ARQUIVOS;
import static br.ufrn.sigaa.extensao.jsf.ConstantesNavegacao.ANEXAR_FOTOS;
import static br.ufrn.sigaa.extensao.jsf.ConstantesNavegacao.ATIVIDADES;
import static br.ufrn.sigaa.extensao.jsf.ConstantesNavegacao.CURSO_EVENTO;
import static br.ufrn.sigaa.extensao.jsf.ConstantesNavegacao.DADOS_GERAIS;
import static br.ufrn.sigaa.extensao.jsf.ConstantesNavegacao.MEMBROS_EQUIPE;
import static br.ufrn.sigaa.extensao.jsf.ConstantesNavegacao.OBJETIVOS_ESPERADOS;
import static br.ufrn.sigaa.extensao.jsf.ConstantesNavegacao.OBJETIVOS_ESPERADOS_EXTENSAO;
import static br.ufrn.sigaa.extensao.jsf.ConstantesNavegacao.ORCAMENTO_CONSOLIDADO;
import static br.ufrn.sigaa.extensao.jsf.ConstantesNavegacao.ORCAMENTO_DETALHADO;
import static br.ufrn.sigaa.extensao.jsf.ConstantesNavegacao.PRODUTO;
import static br.ufrn.sigaa.extensao.jsf.ConstantesNavegacao.PROGRAMA;
import static br.ufrn.sigaa.extensao.jsf.ConstantesNavegacao.PROJETO;
import static br.ufrn.sigaa.extensao.jsf.ConstantesNavegacao.RESUMO;
import static br.ufrn.sigaa.extensao.jsf.ConstantesNavegacao.SUB_ATIVIDADES;

import java.util.HashMap;
import java.util.Map;

import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.extensao.jsf.ConstantesNavegacao;

/*******************************************************************************
 * Responsável pelo controle de fluxo nos diversos passos do cadastro de
 * extensão.
 * <br>
 * O cadastro de uma proposta ou registro de ação de extensão é formado por
 * vários passos e envolve mais de um Managed Bean. Esta classe auxilia a
 * navegação entre as telas do cadastro através da utilização de um botão
 * avançar de forma mais padronizada, possibilita que o usuário saiba em que
 * ponto do cadastro se encontra, quais as telas que já passou, quantas e quais
 * faltam para terminar o cadastro.
 * 
 * @author Ricardo Wendell
 * 
 ******************************************************************************/
public class ControleFluxoAtividadeExtensao {

	private String[] fluxo;

	private String[] passos;

	private int passoAtual;

	// Fluxo de Programas
	private static String[] FLUXO_PROGRAMA = new String[] { DADOS_GERAIS,
		PROGRAMA, ATIVIDADES, MEMBROS_EQUIPE, OBJETIVOS_ESPERADOS_EXTENSAO, ORCAMENTO_DETALHADO,
		ORCAMENTO_CONSOLIDADO, ANEXAR_ARQUIVOS, ANEXAR_FOTOS, RESUMO };
	// Fluxo de Projetos
	private static String[] FLUXO_PROJETO = new String[] { DADOS_GERAIS,
		PROJETO, MEMBROS_EQUIPE, OBJETIVOS_ESPERADOS_EXTENSAO, ORCAMENTO_DETALHADO,
		ORCAMENTO_CONSOLIDADO, ANEXAR_ARQUIVOS, ANEXAR_FOTOS, RESUMO };
	// Fluxo de Cursos e Eventos
	private static String[] FLUXO_CURSO_EVENTO = new String[] { DADOS_GERAIS,
		CURSO_EVENTO, SUB_ATIVIDADES, MEMBROS_EQUIPE, OBJETIVOS_ESPERADOS_EXTENSAO, 
		ORCAMENTO_DETALHADO, ORCAMENTO_CONSOLIDADO, ANEXAR_ARQUIVOS, ANEXAR_FOTOS, RESUMO };
	// Fluxo de Produto
	private static String[] FLUXO_PRODUTO = new String[] { DADOS_GERAIS,
		PRODUTO, MEMBROS_EQUIPE, OBJETIVOS_ESPERADOS_EXTENSAO, ORCAMENTO_DETALHADO,
		ORCAMENTO_CONSOLIDADO, ANEXAR_ARQUIVOS, ANEXAR_FOTOS, RESUMO };


	/** Fluxos alternativos para cadastro de projeto integrado*/
	/** Fluxo de Programas associados */
	private static String[] FLUXO_PROGRAMA_ASSOCIADO = new String[] { DADOS_GERAIS, ATIVIDADES, RESUMO };
	/** Fluxo de Projetos */
	private static String[] FLUXO_PROJETO_ASSOCIADO = new String[] { DADOS_GERAIS, PROJETO, OBJETIVOS_ESPERADOS, RESUMO };
	/** Fluxo de Cursos e Eventos */
	private static String[] FLUXO_CURSO_EVENTO_ASSOCIADO = new String[] { DADOS_GERAIS, CURSO_EVENTO, RESUMO };
	/** Fluxo de Produto */
	private static String[] FLUXO_PRODUTO_ASSOCIADO = new String[] { DADOS_GERAIS, PRODUTO, ATIVIDADES, RESUMO };


	/** Utilizada para construção do fluxo de ações associadas*/
	public static final int CONST_ACAO_ASSOCIADA = 7;


	/** Fluxos possíveis */
	public static final int TIPO_PROGRAMA = TipoAtividadeExtensao.PROGRAMA;    
	public static final int TIPO_PROJETO = TipoAtividadeExtensao.PROJETO;    
	public static final int TIPO_CURSO = TipoAtividadeExtensao.CURSO;   
	public static final int TIPO_EVENTO = TipoAtividadeExtensao.EVENTO;
	public static final int TIPO_PRODUTO = TipoAtividadeExtensao.PRODUTO;

	/** Fluxos possíveis alternativos */
	public static final int TIPO_PROGRAMA_ASSOCIADO = TipoAtividadeExtensao.PROGRAMA + CONST_ACAO_ASSOCIADA;    
	public static final int TIPO_PROJETO_ASSOCIADO = TipoAtividadeExtensao.PROJETO + CONST_ACAO_ASSOCIADA;    
	public static final int TIPO_CURSO_ASSOCIADO = TipoAtividadeExtensao.CURSO + CONST_ACAO_ASSOCIADA;   
	public static final int TIPO_EVENTO_ASSOCIADO = TipoAtividadeExtensao.EVENTO + CONST_ACAO_ASSOCIADA;
	public static final int TIPO_PRODUTO_ASSOCIADO = TipoAtividadeExtensao.PRODUTO + CONST_ACAO_ASSOCIADA;




	private static Map<Integer, String[]> fluxos = new HashMap<Integer, String[]>();
	static {
		fluxos.put(TIPO_PROGRAMA, FLUXO_PROGRAMA);
		fluxos.put(TIPO_PROJETO, FLUXO_PROJETO);
		fluxos.put(TIPO_CURSO, FLUXO_CURSO_EVENTO);
		fluxos.put(TIPO_EVENTO, FLUXO_CURSO_EVENTO);
		fluxos.put(TIPO_PRODUTO, FLUXO_PRODUTO);

		fluxos.put(TIPO_PROGRAMA_ASSOCIADO, FLUXO_PROGRAMA_ASSOCIADO);
		fluxos.put(TIPO_PROJETO_ASSOCIADO, FLUXO_PROJETO_ASSOCIADO);
		fluxos.put(TIPO_CURSO_ASSOCIADO, FLUXO_CURSO_EVENTO_ASSOCIADO);
		fluxos.put(TIPO_EVENTO_ASSOCIADO, FLUXO_CURSO_EVENTO_ASSOCIADO);
		fluxos.put(TIPO_PRODUTO_ASSOCIADO, FLUXO_PRODUTO_ASSOCIADO);
	}

	public ControleFluxoAtividadeExtensao(int fluxoSelecionado, boolean simplificado) {
		fluxo = fluxos.get(fluxoSelecionado);
		// Inicializar descrição dos passos
		passos = new String[fluxo.length];
		for (int i = 0; i < fluxo.length; i++) {
			passos[i] = ConstantesNavegacao.PASSOS_ATIVIDADES_EXTENSAO.get(fluxo[i]);
		}
		passoAtual = 0;
	}

	public String passoAnterior() {
		if (passoAtual > 0) {
			passoAtual--;
			return fluxo[passoAtual];
		} else
			return null;
	}

	public String proximoPasso() {
		if (passoAtual < fluxo.length - 1) {
			passoAtual++;
			return fluxo[passoAtual];
		} else
			return null;
	}

	public int getPassoAtual() {
		return this.passoAtual;
	}

	public void setPassoAtual(int passoAtual) {
		this.passoAtual = passoAtual;
	}

	public String[] getPassos() {
		return this.passos;
	}

	public void setPassos(String[] passos) {
		this.passos = passos;
	}

}