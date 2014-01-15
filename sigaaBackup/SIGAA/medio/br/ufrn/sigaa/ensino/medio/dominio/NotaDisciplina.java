/*
* Universidade Federal do Rio Grande do Norte
* Superintend�ncia de Inform�tica
* Diretoria de Sistemas
*
* Created on 21/07/2011
* 
*/
package br.ufrn.sigaa.ensino.medio.dominio;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.negocio.consolidacao.ConsolidacaoMedio;

/**
 * Classe que representa as notas para exibi��o no boletim de um discente do n�vel m�dio. (N�o persistida)
 *
 * @author Arlindo
 */
public class NotaDisciplina {

	/** Dados deferente a disciplina */
	private MatriculaComponente matricula;
	
	/** Dados das notas da disciplina */
	private List<NotaSerie> notas = new ArrayList<NotaSerie>();

	/** Valor da M�dia parcial da disciplina sem levar em considera��o a recupera��o final. */
	private Double mediaParcial;
	
	/** Dados da Matr�cula em Depend�ncia */
	private MatriculaComponenteDependencia dependencia;
	
	/** Indica se o discente est� em recupera��o ou n�o.
	* @return true, se o discente est� em recupera��o. False, caso contr�rio.
	*/  
	private boolean emRecuperacao;
	
	/**
	 * Calcula a m�dia do aluno de acordo com as avalia��es cadastradas
	 * para esta disciplina.
	 * 
	 * @return
	 */
	public Double calculaMedia() {
		return ConsolidacaoMedio.calculaMediaNotaSerie(notas);
	}
	
	public MatriculaComponente getMatricula() {
		return matricula;
	}

	public void setMatricula(MatriculaComponente matricula) {
		this.matricula = matricula;
	}

	public List<NotaSerie> getNotas() {
		return notas;
	}

	public void setNotas(List<NotaSerie> notas) {
		this.notas = notas;
	}

	public Double getMediaParcial() {
		return mediaParcial;
	}

	public void setMediaParcial(Double mediaParcial) {
		this.mediaParcial = mediaParcial;
	}
	
	public Double getMediaSemestral1 (){
		Double nota1 = notas.get(0).getNotaUnidade().getNota();
		Double nota2 = notas.get(1).getNotaUnidade().getNota();
		
		if (nota1 == null || nota2 == null)
			return null;
		return Math.round(((nota1 * 10D + nota2 * 10D) / 20D) * 10D) / 10D;
	}
	
	public Double getMediaSemestral1ComRecuperacao (){
		Double nota1 = notas.get(0).getNotaUnidade().getNota();
		Double nota2 = notas.get(1).getNotaUnidade().getNota();
		
		Double rec = notas.get(2).getNotaUnidade().getNota();
		
		if (nota1 == null || nota2 == null)
			return null;
		
		if (rec != null) {
			if (nota1 < rec)
				nota1 = rec;
			else if (nota2 < rec)
				nota2 = rec;
		}
		
		return Math.round(((nota1 * 10D + nota2 * 10D) / 20D) * 10D) / 10D;
	}
	
	public Double getMediaSemestral2 (){
		Double nota1 = notas.get(3).getNotaUnidade().getNota();
		Double nota2 = notas.get(4).getNotaUnidade().getNota();
		
		if (nota1 == null || nota2 == null)
			return null;
		
		return Math.round(((nota1 * 10D + nota2 * 10D) / 20D) * 10D) / 10D;
	}
	
	public Double getMediaSemestral2ComRecuperacao (){
		Double nota1 = notas.get(3).getNotaUnidade().getNota();
		Double nota2 = notas.get(4).getNotaUnidade().getNota();
		
		Double rec = notas.get(5).getNotaUnidade().getNota();
		
		if (nota1 == null || nota2 == null)
			return null;
		
		if (rec != null) {
			if (nota1 < rec)
				nota1 = rec;
			else if (nota2 < rec)
				nota2 = rec;
		}
		
		return Math.round(((nota1 * 10D + nota2 * 10D) / 20D) * 10D) / 10D;
	}

	public boolean isEmRecuperacao() {
		return emRecuperacao;
	}

	public void setEmRecuperacao(boolean emRecuperacao) {
		this.emRecuperacao = emRecuperacao;
	}

	public MatriculaComponenteDependencia getDependencia() {
		return dependencia;
	}

	public void setDependencia(MatriculaComponenteDependencia dependencia) {
		this.dependencia = dependencia;
	}
}
