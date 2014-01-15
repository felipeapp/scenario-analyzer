 /*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 31/01/2008
 *
 */
package br.ufrn.sigaa.ava.negocio;

import java.util.List;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.ava.dominio.IndicacaoReferencia;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Movimento para realização de cadastros no AVA. Contém mensagem para 
 * ser colocada no registro de atividades.
 * 
 * @author David Pereira
 *
 */
public class MovimentoCadastroAva extends MovimentoCadastro {

	private String mensagem;

	private Turma turma;
	
	// Especifica os campos necessários em objetos a serem cadastrados ou alterados
	private Specification specification;
	
	private boolean cadastrarEmVariasTurmas;
	
	private List<String> cadastrarEm;
	
	private List <TopicoAula> topicosDeAula;
	
	private List <IndicacaoReferencia> referencias;
	
	private boolean paraPlanoCurso;
	
	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public Specification getSpecification() {
		return specification;
	}

	public void setSpecification(Specification specification) {
		this.specification = specification;
	}

	public boolean isCadastrarEmVariasTurmas() {
		return cadastrarEmVariasTurmas;
	}

	public void setCadastrarEmVariasTurmas(boolean cadastrarEmVariasTurmas) {
		this.cadastrarEmVariasTurmas = cadastrarEmVariasTurmas;
	}

	public List<String> getCadastrarEm() {
		return cadastrarEm;
	}

	public void setCadastrarEm(List<String> cadastrarEm) {
		this.cadastrarEm = cadastrarEm;
	}

	public List<TopicoAula> getTopicosDeAula() {
		return topicosDeAula;
	}

	public void setTopicosDeAula(List<TopicoAula> topicosDeAula) {
		this.topicosDeAula = topicosDeAula;
	}

	public List<IndicacaoReferencia> getReferencias() {
		return referencias;
	}

	public void setReferencias(List<IndicacaoReferencia> referencias) {
		this.referencias = referencias;
	}

	public boolean isParaPlanoCurso() {
		return paraPlanoCurso;
	}

	public void setParaPlanoCurso(boolean paraPlanoCurso) {
		this.paraPlanoCurso = paraPlanoCurso;
	}
}
