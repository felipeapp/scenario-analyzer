/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 30/03/2012
 *
 */
package br.ufrn.sigaa.diploma.dominio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.util.Formatador;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MetodoAvaliacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.Reconhecimento;
import br.ufrn.sigaa.ensino.stricto.dominio.AreaConcentracao;
import br.ufrn.sigaa.prodocente.atividades.dominio.TrabalhoFimCurso;

/** Classe que contem os dados que serão utilizados na impressão do diploma.
 * @author Édipo Elder F. de Melo
 *
 */
public class DadosImpressaoDiploma {

	/** Registro do diploma a ser impresso. */
	private RegistroDiploma registro;
	
	/** Dados do reconhecimento do curso. */
	private Reconhecimento reconhecimento;
	
	/** Matriz Curricular do discente de Graduação. */
	private MatrizCurricular matrizCurricular;

	/** Área de concentração do discente de stricto sensu. */
	private AreaConcentracao area;
	
	/** Média final do discente de lato sensu. */
	private BigDecimal mediaFinal;
	
	/** Lista de componentes pagos pelo discente de lato. */
	private List<MatriculaComponente > componentesPagos;
	
	/** Trabalho de fim de curso do discente. */
	private TrabalhoFimCurso trabalhoFimCurso;
	
	private int metodoAvaliacao;
	
	/** Construtor padrão. */
	public DadosImpressaoDiploma() {
	}
	
	public RegistroDiploma getRegistro() {
		return registro;
	}

	public void setRegistro(RegistroDiploma registro) {
		this.registro = registro;
	}

	public Reconhecimento getReconhecimento() {
		return reconhecimento;
	}

	public void setReconhecimento(Reconhecimento reconhecimento) {
		this.reconhecimento = reconhecimento;
	}

	public MatrizCurricular getMatrizCurricular() {
		return matrizCurricular;
	}

	public void setMatrizCurricular(MatrizCurricular matrizCurricular) {
		this.matrizCurricular = matrizCurricular;
	}

	public AreaConcentracao getArea() {
		return area;
	}

	public void setArea(AreaConcentracao area) {
		this.area = area;
	}

	public BigDecimal getMediaFinal() {
		return mediaFinal;
	}	
	
	public String getMediaFinalDesc() {
		
		return mediaFinal != null ? String.valueOf(Formatador.getInstance().formatarDecimalInt(mediaFinal.doubleValue())) : "---";
	}

	public void setMediaFinal(BigDecimal mediaFinal) {
		this.mediaFinal = mediaFinal;
	}

	public List<MatriculaComponente> getComponentesPagos() {
		return componentesPagos;
	}

	public void setComponentesPagos(List<MatriculaComponente> componentesPagos) {
		this.componentesPagos = componentesPagos;
	}

	public void addMatriculaComponente(MatriculaComponente mc) {
		if (componentesPagos == null)
			componentesPagos = new ArrayList<MatriculaComponente>();
		componentesPagos.add(mc);
	}

	public TrabalhoFimCurso getTrabalhoFimCurso() {
		return trabalhoFimCurso;
	}

	public void setTrabalhoFimCurso(TrabalhoFimCurso trabalhoFimCurso) {
		this.trabalhoFimCurso = trabalhoFimCurso;
	}

	public int getMetodoAvaliacao() {
		return metodoAvaliacao;
	}

	public void setMetodoAvaliacao(int metodoAvaliacao) {
		this.metodoAvaliacao = metodoAvaliacao;
	}

	public boolean isNota(){
		return MetodoAvaliacao.NOTA == metodoAvaliacao;
	}
}
