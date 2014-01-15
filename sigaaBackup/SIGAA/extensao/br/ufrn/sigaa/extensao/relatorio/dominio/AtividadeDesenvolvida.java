/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 27/11/2006
 *
 */
package br.ufrn.sigaa.extensao.relatorio.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/*******************************************************************************
 * <p>Atividades desenvolvidas no projeto de extensão. Faz parte do relatório final
 * e parcial das ações de extensão.
 * </p>
 * 
 * @author Gleydson
 * 
 ******************************************************************************/
@Entity
@Table(name = "atividades_desenvolvidas")
public class AtividadeDesenvolvida implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_atividade_desenvolvida", nullable = false)
	private int id;

	//Descricao das atividades desenvolvidas
	private String denominacao;

	//Descricao dos resultados quantitativos
	private String resultadosQuantitativos;

	//Descricao dos resultados qualitativos
	private String resultadosQualitativos;

	//Descrição das dificuldates encontradas
	private String dificuldades;

	@ManyToOne
	@JoinColumn(name = "id_relatorio_curso_evento")
	private RelatorioCursoEvento relatorioCursoEvento;

	public String getDenominacao() {
		return denominacao;
	}

	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}

	public String getDificuldades() {
		return dificuldades;
	}

	public void setDificuldades(String dificuldades) {
		this.dificuldades = dificuldades;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getResultadosQualitativos() {
		return resultadosQualitativos;
	}

	public void setResultadosQualitativos(String resultadosQualitativos) {
		this.resultadosQualitativos = resultadosQualitativos;
	}

	public String getResultadosQuantitativos() {
		return resultadosQuantitativos;
	}

	public void setResultadosQuantitativos(String resultadosQuantitativos) {
		this.resultadosQuantitativos = resultadosQuantitativos;
	}

	public RelatorioCursoEvento getRelatorioCursosEventos() {
		return relatorioCursoEvento;
	}

	public void setRelatorioCursosEventos(
			RelatorioCursoEvento relatorioCursoEvento) {
		this.relatorioCursoEvento = relatorioCursoEvento;
	}

	public ListaMensagens validate() {
		return null;
	}

}
