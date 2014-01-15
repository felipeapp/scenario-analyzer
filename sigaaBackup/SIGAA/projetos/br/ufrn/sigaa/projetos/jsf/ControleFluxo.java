/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/08/2009
 *
 */
package br.ufrn.sigaa.projetos.jsf;

import static br.ufrn.sigaa.monitoria.jsf.ConstantesNavegacaoMonitoria.CADASTROPROJETO_ANEXAR_ARQUIVOS;
import static br.ufrn.sigaa.monitoria.jsf.ConstantesNavegacaoMonitoria.CADASTROPROJETO_COMPONENTE_CURRICULAR;
import static br.ufrn.sigaa.monitoria.jsf.ConstantesNavegacaoMonitoria.CADASTROPROJETO_DIMENSAO_PROJETO;
import static br.ufrn.sigaa.monitoria.jsf.ConstantesNavegacaoMonitoria.CADASTROPROJETO_FORM;
import static br.ufrn.sigaa.monitoria.jsf.ConstantesNavegacaoMonitoria.CADASTROPROJETO_FORM_EXTERNO;
import static br.ufrn.sigaa.monitoria.jsf.ConstantesNavegacaoMonitoria.CADASTROPROJETO_RESUMO;
import static br.ufrn.sigaa.monitoria.jsf.ConstantesNavegacaoMonitoria.CADASTROPROJETO_SELECAO_COORDENADOR;
import static br.ufrn.sigaa.monitoria.jsf.ConstantesNavegacaoMonitoria.CADASTROPROJETO_ORCAMENTO;
import static br.ufrn.sigaa.monitoria.jsf.ConstantesNavegacaoMonitoria.CADASTROPROJETO_SELECAO_DOCENTE;
import static br.ufrn.sigaa.monitoria.jsf.ConstantesNavegacaoMonitoria.PASSOS_CADASTRO_PROJETO_MONITORIA;
import static br.ufrn.sigaa.monitoria.jsf.ConstantesNavegacaoMonitoria.PASSOS_CADASTRO_PROJETO_MONITORIA_EXTERNO;
import static br.ufrn.sigaa.monitoria.jsf.ConstantesNavegacaoMonitoria.PASSOS_CADASTRO_COMPLETO_PAMQEG;
import static br.ufrn.sigaa.monitoria.jsf.ConstantesNavegacaoMonitoria.PASSOS_CADASTRO_COMPLETO_PAMQEG_EXTERNO;
import static br.ufrn.sigaa.projetos.jsf.ConstantesNavegacaoProjetos.ARQUIVOS;
import static br.ufrn.sigaa.projetos.jsf.ConstantesNavegacaoProjetos.CRONOGRAMA;
import static br.ufrn.sigaa.projetos.jsf.ConstantesNavegacaoProjetos.DADOS_GERAIS;
import static br.ufrn.sigaa.projetos.jsf.ConstantesNavegacaoProjetos.DIMENSAO_ACADEMICA;
import static br.ufrn.sigaa.projetos.jsf.ConstantesNavegacaoProjetos.EQUIPE;
import static br.ufrn.sigaa.projetos.jsf.ConstantesNavegacaoProjetos.FOTOS;
import static br.ufrn.sigaa.projetos.jsf.ConstantesNavegacaoProjetos.ORCAMENTO;
import static br.ufrn.sigaa.projetos.jsf.ConstantesNavegacaoProjetos.PASSOS_CADASTRO_PROJETO_BASE;
import static br.ufrn.sigaa.projetos.jsf.ConstantesNavegacaoProjetos.RESUMO;

import java.util.HashMap;
import java.util.Map;

/*******************************************************************************
 * <p>
 * Controla o fluxo nos diversos passos do cadastro de projetos de ação integrada.
 * <br>
 * O cadastro de uma proposta de projeto de ação integrada é formado por
 * vários passos. Esta classe auxilia a navegação entre as telas do cadastro através
 * da utilização de um botão avançar de forma mais padronizada, possibilitando
 * que o usuário saiba em que ponto do cadastro se encontra, quais as telas que já 
 * passou, quantas e quais ainda faltam para terminar o cadastro.
 * </p>
 * 
 * @author Ricardo Wendell
 * @author Ilueny Santos
 * 
 ******************************************************************************/
public class ControleFluxo {

    
	/**
	 * Fluxo de caso de uso
	 */
	private String[] fluxo;

    /**
     * Passos do cadastro
     */
    private String[] passos;

    /** Atributo utilizado para representar o passo Atual do controle do fluxo */
    private int passoAtual;

    /** Fluxo principal de cadastro de projetos.  */
    private static final String[] FLUXO_PROJETO_BASE = new String[] {DIMENSAO_ACADEMICA, DADOS_GERAIS, CRONOGRAMA, EQUIPE, ORCAMENTO, ARQUIVOS, 
	FOTOS, RESUMO};

    /** Fluxo de projeto de ensino (monitoria). */
    private static final String[] FLUXO_PROJETO_MONITORIA = new String[] {CADASTROPROJETO_DIMENSAO_PROJETO, CADASTROPROJETO_FORM, CADASTROPROJETO_COMPONENTE_CURRICULAR, 
	CADASTROPROJETO_SELECAO_DOCENTE, CADASTROPROJETO_SELECAO_COORDENADOR, CADASTROPROJETO_ANEXAR_ARQUIVOS, CADASTROPROJETO_RESUMO };

    /** Fluxo de projeto de ensino (monitoria). */
    private static final String[] FLUXO_PROJETO_MONITORIA_EXTERNO = new String[] {CADASTROPROJETO_DIMENSAO_PROJETO, CADASTROPROJETO_FORM_EXTERNO, CADASTROPROJETO_COMPONENTE_CURRICULAR, 
	CADASTROPROJETO_SELECAO_DOCENTE, CADASTROPROJETO_SELECAO_COORDENADOR, CADASTROPROJETO_ANEXAR_ARQUIVOS, CADASTROPROJETO_RESUMO };

    /** Fluxo de projeto de melhoria da qualidade do ensino de graduação (PAMQEG). */
    private static final String[] FLUXO_PROJETO_COMPLETO_PAMQEG = new String[] {CADASTROPROJETO_DIMENSAO_PROJETO, CADASTROPROJETO_FORM, CADASTROPROJETO_COMPONENTE_CURRICULAR, 
    	CADASTROPROJETO_SELECAO_DOCENTE, CADASTROPROJETO_SELECAO_COORDENADOR, CADASTROPROJETO_ORCAMENTO, CADASTROPROJETO_ANEXAR_ARQUIVOS, CADASTROPROJETO_RESUMO};
    
    /** Fluxo de projeto de melhoria da qualidade do ensino de graduação (PAMQEG). */
    private static final String[] FLUXO_PROJETO_COMPLETO_PAMQEG_EXTERNO = new String[] {CADASTROPROJETO_DIMENSAO_PROJETO, CADASTROPROJETO_FORM_EXTERNO, CADASTROPROJETO_COMPONENTE_CURRICULAR, 
    	CADASTROPROJETO_SELECAO_DOCENTE, CADASTROPROJETO_SELECAO_COORDENADOR, CADASTROPROJETO_ORCAMENTO, CADASTROPROJETO_ANEXAR_ARQUIVOS, CADASTROPROJETO_RESUMO};

    /** Fluxo de projeto de ensino associado (projeto de monitoria associado). */
    private static final String[] FLUXO_PROJETO_MONITORIA_ASSOCIADO = new String[] {CADASTROPROJETO_FORM, CADASTROPROJETO_COMPONENTE_CURRICULAR, 
	CADASTROPROJETO_SELECAO_DOCENTE, CADASTROPROJETO_SELECAO_COORDENADOR, CADASTROPROJETO_RESUMO};

    /** Fluxo de projeto de melhoria da qualidade do ensino de graduação (PAMQEG). */
    private static final String[] FLUXO_PROJETO_PAMQEG = new String[] {CADASTROPROJETO_FORM};

    /** Atributo utilizado para representar o Fluxo possível*/
    public static final int PROJETO_BASE = 1;    
    /** Atributo utilizado para representar o Fluxo possível*/
    public static final int PROJETO_MONITORIA = 2;    
    /** Atributo utilizado para representar o Fluxo possível*/
    public static final int PROJETO_PAMQEG = 3;    
    /** Atributo utilizado para representar o Fluxo possível*/
    public static final int PROJETO_MONITORIA_ASSOCIADO = 4;
    /** Atributo utilizado para representar o Fluxo possível*/
    public static final int PROJETO_COMPLETO_PAMQEG = 5;  
    /** Atributo utilizado para representar o Fluxo possível*/
    public static final int PROJETO_MONITORIA_EXTERNO = 6;    
    /** Atributo utilizado para representar o Fluxo possível*/
    public static final int PROJETO_COMPLETO_PAMQEG_EXTERNO = 7; 
    
    /**
     * Método utilizado para informar os fluxos do controle de fluxos
     */
    private static Map<Integer, String[]> fluxos = new HashMap<Integer, String[]>();
    static {
	fluxos.put(PROJETO_BASE, FLUXO_PROJETO_BASE);
	fluxos.put(PROJETO_MONITORIA, FLUXO_PROJETO_MONITORIA);
	fluxos.put(PROJETO_MONITORIA_EXTERNO, FLUXO_PROJETO_MONITORIA_EXTERNO);
	fluxos.put(PROJETO_PAMQEG, FLUXO_PROJETO_PAMQEG);
	fluxos.put(PROJETO_MONITORIA_ASSOCIADO, FLUXO_PROJETO_MONITORIA_ASSOCIADO);
	fluxos.put(PROJETO_COMPLETO_PAMQEG, FLUXO_PROJETO_COMPLETO_PAMQEG);
	fluxos.put(PROJETO_COMPLETO_PAMQEG_EXTERNO, FLUXO_PROJETO_COMPLETO_PAMQEG_EXTERNO);
    }

    /**
     * Seleciona o fluxo principal.
     * Inicializa a lista de passos do fluxo selecionado.
     * 
     */
    public ControleFluxo(int fluxoSelecionado) {

	//fluxo principal
	fluxo = fluxos.get(fluxoSelecionado);
	// Inicializar descrição dos passos
	passos = new String[fluxo.length];
	if (PROJETO_BASE == fluxoSelecionado) {
        	for (int i = 0; i < fluxo.length; i++) {	    
        	    passos[i] = PASSOS_CADASTRO_PROJETO_BASE.get(fluxo[i]);
        	}
	}	

	if ((PROJETO_MONITORIA == fluxoSelecionado) || (PROJETO_MONITORIA_ASSOCIADO == fluxoSelecionado) || (PROJETO_PAMQEG == fluxoSelecionado) ) {
        	for (int i = 0; i < fluxo.length; i++) {	    
        	    passos[i] = PASSOS_CADASTRO_PROJETO_MONITORIA.get(fluxo[i]);
        	}
	}

	if ((PROJETO_MONITORIA_EXTERNO == fluxoSelecionado)) {
    	for (int i = 0; i < fluxo.length; i++) {	    
    	    passos[i] = PASSOS_CADASTRO_PROJETO_MONITORIA_EXTERNO.get(fluxo[i]);
    	}
	}

	if ((PROJETO_COMPLETO_PAMQEG == fluxoSelecionado)){
		for (int i = 0; i < fluxo.length; i++) {	    
    	    passos[i] = PASSOS_CADASTRO_COMPLETO_PAMQEG.get(fluxo[i]);
    	}
	}

	if ((PROJETO_COMPLETO_PAMQEG_EXTERNO == fluxoSelecionado)){
		for (int i = 0; i < fluxo.length; i++) {	    
    	    passos[i] = PASSOS_CADASTRO_COMPLETO_PAMQEG_EXTERNO.get(fluxo[i]);
    	}
	}
	
	passoAtual = 0;
	
    }

    /**
     * Retorna para o passo anterior.
     * 
     * Não chamado diretamente por JSPs.
     * 
     * @return URL do passo anterior
     */
    public String passoAnterior() {
	if (passoAtual > 0) {
	    passoAtual--;
	    return fluxo[passoAtual];
	} else {
	    return null;
	}
    }

    /**
     * Vai para o próximo passo.
     * 
     * Não chamado diretamente por JSPs.
     * 
     * @return URL do próxmo passo.
     */
    public String proximoPasso() {
	if (passoAtual < fluxo.length - 1) {
	    passoAtual++;
	    return fluxo[passoAtual];
	} else {
	    return fluxo[fluxo.length - 1];
	}
    }

    /**
     * Passo atual.
     * 
     * @return índice do passo atual.
     */
    public int getPassoAtual() {
	return this.passoAtual;
    }

    /**
     * Define o passo atual.
     * 
     * @param passoAtual índice do passo atual.
     */
    public void setPassoAtual(int passoAtual) {
	this.passoAtual = passoAtual;
    }

    /**
     * Retorna a lista de títulos dos passos do 
     * fluxo atual.
     * 
     * @return Lista de títulos.
     */
    public String[] getPassos() {
	return this.passos;
    }

    /**
     * Define os títulos do passo do fluxo selecionado.
     * 
     * @param passos Array de String com os títulos.
     */
    public void setPassos(String[] passos) {
	this.passos = passos;
    }

    public String[] getFluxo() {
        return fluxo;
    }

    public void setFluxo(String[] fluxo) {
        this.fluxo = fluxo;
    }
    
    /** Atualiza o passo atual e redireciona o usuário para o passo correspondente ao índice informado. */
    public String goPassoByIndex(int passo) {
    	passoAtual = passo;
    	return this.fluxo[passoAtual];
    }

    /** Atualiza o passo atual no fluxo e redireciona o usuário para o passo correspondente à URL informada. */
    public String goPassoByURL(String url) {
    	for (int i = 0; i < fluxo.length; i++) {
    		if (fluxo[i].equalsIgnoreCase(url)) {
    			return goPassoByIndex(i);
    		}
    	}
    	return this.fluxo[passoAtual];
    }

    
}
