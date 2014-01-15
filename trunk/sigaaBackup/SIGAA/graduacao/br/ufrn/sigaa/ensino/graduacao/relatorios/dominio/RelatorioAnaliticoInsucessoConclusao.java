/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 08/04/2010
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.dominio;

import br.ufrn.sigaa.dominio.Curso;

/** 
 * Esta entidade é utilizada para armazenar os dados do relatório analítico de Insucesso de conclusão.
 * 
 * @author Arlindo Rodrigues
 */
public class RelatorioAnaliticoInsucessoConclusao {
	
	/** Curso que representa os dados. */
	private Curso curso = new Curso();
	
	/** Quantidade de alunos que entraram no curso */
	private int entrada;
	
	/** Quantidade de alunos que se formaram */
	private int saida;
	
	/** Quantidade de Alunos que se formaram antes do previsto */
	private int saidaAntesPrevisto;
	
	/** Quantidade de desistentes */
	private int desistencia;
	
	/** Quantidade de alunos ativos ou com matrícula no próximo semestre */
	private int matriculados;
	
	/** Quantidade de alunos que não se enquadram nas demais situações */
	private int outros;	
	
	/** Sigla da Unidade */
	private String sigla;	
	
	/** Id da Unidade */
	private int idUnidade;

	/** Taxa de Sucesso */
	public float getTaxaSucesso() {
		if (entrada == 0)
			return 0;
		else {
		    return (((float)saida + (float)saidaAntesPrevisto)/ entrada) * 100;
		}
	}

	/** Taxa de Insucesso */
	public float getTaxaInsucesso() {
		if (getTaxaSucesso() > 0)
			return 100 - getTaxaSucesso();
		else 
			return 0;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public int getEntrada() {
		return entrada;
	}

	public void setEntrada(int entrada) {
		this.entrada = entrada;
	}

	public int getSaida() {
		return saida;
	}

	public void setSaida(int saida) {
		this.saida = saida;
	}

	public int getDesistencia() {
		return desistencia;
	}

	public void setDesistencia(int desistencia) {
		this.desistencia = desistencia;
	}

	public int getMatriculados() {
		return matriculados;
	}

	public void setMatriculados(int matriculados) {
		this.matriculados = matriculados;
	}

	public int getOutros() {
		return outros;
	}

	public void setOutros(int outros) {
		this.outros = outros;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public int getIdUnidade() {
		return idUnidade;
	}

	public void setIdUnidade(int idUnidade) {
		this.idUnidade = idUnidade;
	}

	public int getSaidaAntesPrevisto() {
		return saidaAntesPrevisto;
	}

	public void setSaidaAntesPrevisto(int saidaAntesPrevisto) {
		this.saidaAntesPrevisto = saidaAntesPrevisto;
	}	
}
