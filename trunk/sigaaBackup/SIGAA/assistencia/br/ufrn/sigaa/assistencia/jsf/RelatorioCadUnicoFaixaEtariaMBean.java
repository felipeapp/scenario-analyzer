/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
*  Created on 18/02/2010
*
*/
package br.ufrn.sigaa.assistencia.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.sigaa.arq.dao.sae.AdesaoCadastroUnicoBolsaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;

/**
 * Relatório por faixa etária com alunos prioritários do cadostro único
 * 
 * @author Henrique André
 */
@Component("relatorioCadUnicoFaixaEtaria") 
@Scope("request")
public class RelatorioCadUnicoFaixaEtariaMBean extends SigaaAbstractController<Object> {

	private String sexo;
	private Integer ano;
	private Integer periodo;
	private List<Map<String, Object>> resultado; 	

	private Integer anoConclusao;
	private Integer periodoConclusao;	
	
	private Boolean residente = false;
	
	/**
	 * Redireciona para formulario do relatório
	 * 
	 * @return
	 */
	public String iniciar() {
		return forward("/sae/relatorios/form_faixa_etaria_cadastro_unico.jsp");
	}

	/**
	 * Emite o relatório
	 * 
	 * @return
	 */
	public String gerar() {
		
		if (isEmpty(ano) || isEmpty(periodo))
			erros.addErro("Ano e Período são campos obrigatórios");
		if (isEmpty(sexo) || sexo.equals("-1"))
			erros.addErro("Sexo é um campo obrigatório");

		if (hasErrors())
			return null;
		
		AdesaoCadastroUnicoBolsaDao dao = getDAO(AdesaoCadastroUnicoBolsaDao.class);
		
		Integer prazoConclusao= null;
		if (!isEmpty(anoConclusao) && !isEmpty(periodoConclusao))
			prazoConclusao = new Integer(anoConclusao + "" + periodoConclusao);
		
		resultado = dao.calculaQtdPrioritariosFaixaEtaria(ano, periodo, sexo, prazoConclusao, residente);
		
		return forward("/sae/relatorios/lista_faixa_etaria_cadastro_unico.jsp");
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public List<Map<String, Object>> getResultado() {
		return resultado;
	}

	public void setResultado(List<Map<String, Object>> resultado) {
		this.resultado = resultado;
	}

	public Integer getAnoConclusao() {
		return anoConclusao;
	}

	public void setAnoConclusao(Integer anoConclusao) {
		this.anoConclusao = anoConclusao;
	}

	public Integer getPeriodoConclusao() {
		return periodoConclusao;
	}

	public void setPeriodoConclusao(Integer periodoConclusao) {
		this.periodoConclusao = periodoConclusao;
	}

	public Boolean getResidente() {
		return residente;
	}

	public void setResidente(Boolean residente) {
		this.residente = residente;
	}
	
}
