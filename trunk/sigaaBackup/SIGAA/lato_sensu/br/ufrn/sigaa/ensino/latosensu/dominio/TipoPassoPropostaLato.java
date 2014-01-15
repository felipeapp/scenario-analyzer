/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 30/03/2011
 *
 */
package br.ufrn.sigaa.ensino.latosensu.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Enumera��o que serve como as poss�veis tela que a proposta pode est�. 
 * 
 * @author Jean Guerethes
 *
 */
public enum TipoPassoPropostaLato {

	/** Tela de dados gerais */
	TELA_DADOS_GERAIS("DADOS GERAIS"),
	/** Tela de configura��o da GRU. */
	TELA_CONFIGURACAO_GRU("CONFIGURA��O DA GRU"),
	/** Tela da coordena��o do curso */
	TELA_COORDENACAO_CURSO("COORDENA��O CURSO"),
	/** Tela dos objetivos e import�ncia do Curso */
	TELA_OBJETIVO_IMPORTACIA("OBJETIVO E IMPORT�NCIA"),
	/** Tela do processo seletivo */
	TELA_PROCESSO_SELETIVO("PROCESSO SELETIVO"),
	/** Tela do cordo docente */
	TELA_CORPO_DOCENTE("CORPO DOCENTE"),
	/** Tela das disciplinas Ministradas */
	TELA_DISCIPLINA_CURSO_LATO("DISCIPLINAS MINISTRADAS"),
	/** Tela com o resumo da proposta do curso */
	TELA_RESUMO_PROPOSTA("RESUMO DA PROPOSTA DO CURSO LATO");
	/** Descri��o do tipo de passo da proposta Lato Sensu */
	private String label;
	/** Descri��o do Enum */
	private int valor;
	/** Lista que armazena todo os Tipos de � passos da proposta Curso Lato Sensu */
	private List<TipoPassoPropostaLato> passosPropostaCursoLato = new ArrayList<TipoPassoPropostaLato>();

	/** Retorna o valor do ordinal informado */
	public static TipoPassoPropostaLato valueOf(int ordinal) {
		return values()[ordinal];
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return String.valueOf(valor);
	}
	
	private TipoPassoPropostaLato() {
		label = name();
	}
	
	private TipoPassoPropostaLato(String label){
		this.label = label;
	}
	
	public String label() {
		return label;
	}
	
	/** Adiciona todos os passos do cadastro da proposta */
	public Collection<TipoPassoPropostaLato> getAll(){	
		
		passosPropostaCursoLato.clear();
		passosPropostaCursoLato.add(TELA_DADOS_GERAIS);
		passosPropostaCursoLato.add(TELA_CONFIGURACAO_GRU);
		passosPropostaCursoLato.add(TELA_COORDENACAO_CURSO);
		passosPropostaCursoLato.add(TELA_OBJETIVO_IMPORTACIA);
		passosPropostaCursoLato.add(TELA_PROCESSO_SELETIVO);
		passosPropostaCursoLato.add(TELA_CORPO_DOCENTE);
		passosPropostaCursoLato.add(TELA_DISCIPLINA_CURSO_LATO);
		passosPropostaCursoLato.add(TELA_RESUMO_PROPOSTA);
		return passosPropostaCursoLato; 

	}
	
}