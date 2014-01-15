/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: Feb 10, 2011
 */
package br.ufrn.sigaa.prodocente.atividades.dominio;

import java.util.Date;
import java.util.HashMap;

import br.ufrn.arq.util.Formatador;
import br.ufrn.integracao.dto.FormacaoAcademicaDTO;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 *  Classe adapter que permite a utilização do DTO FormacaoAcademicaDTO no SIGAA como sendo uma implementação da interface ViewAtividadeBuilder.
 *  É necessário pois FormacaoAcademicaDTO está no projeto ServicosIntegrados e 
 *  a interface ViewAtividadeBuilder está no SIGAA.
 * @author Victor Hugo
 *
 */
public class FormacaoAcademicaAdapter extends FormacaoAcademicaDTO implements ViewAtividadeBuilder {

	/**
	 * DTO que está sendo adaptado para ser utilizado no sigaa
	 */
	FormacaoAcademicaDTO formacaoDTO;
	
	public FormacaoAcademicaAdapter() {
		formacaoDTO = new FormacaoAcademicaDTO();
	}
	
	public FormacaoAcademicaAdapter(FormacaoAcademicaDTO formacao) {
		this.formacaoDTO = formacao;
	}
	
	
	@Override
	public String getItemView() {
		return "  <td>" + getTitulo() + "</td>" + "  <td>"
		+ Formatador.getInstance().formatarData(getInicio()) + " - "
		+ Formatador.getInstance().formatarData(getFim()) + "</td>";
	}

	@Override
	public String getTituloView() {
		return "    <td>Atividade</td>" + "    <td>Data</td>";
	}

	@Override
	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("titulo", null);
		itens.put("dataInicio", null);
		itens.put("dataFim", null);
		return itens;
	}

	@Override
	public float getQtdBase() {
		return 1;
	}


	@Override
	public String getGrau() {
		return formacaoDTO.getGrau();
	}


	@Override
	public void setGrau(String grau) {
		formacaoDTO.setGrau(grau);
	}


	@Override
	public String getTitulo() {
		return formacaoDTO.getTitulo();
	}


	@Override
	public void setTitulo(String titulo) {
		formacaoDTO.setTitulo(titulo);
	}


	@Override
	public String getOrientador() {
		return formacaoDTO.getOrientador();
	}


	@Override
	public void setOrientador(String orientador) {
		formacaoDTO.setOrientador(orientador);
	}


	@Override
	public String getInstituicao() {
		return formacaoDTO.getInstituicao();
	}


	@Override
	public void setInstituicao(String instituicao) {
		formacaoDTO.setInstituicao(instituicao);
	}


	@Override
	public String getFormacao() {
		return formacaoDTO.getFormacao();
	}


	@Override
	public void setFormacao(String formacao) {
		formacaoDTO.setFormacao(formacao);
	}


	@Override
	public String getSubArea() {
		return formacaoDTO.getSubArea();
	}


	@Override
	public void setSubArea(String subArea) {
		formacaoDTO.setSubArea(subArea);
	}


	@Override
	public Date getInicio() {
		return formacaoDTO.getInicio();
	}


	@Override
	public void setInicio(Date inicio) {
		formacaoDTO.setInicio(inicio);
	}


	@Override
	public Date getFim() {
		return formacaoDTO.getFim();
	}


	@Override
	public void setFim(Date fim) {
		formacaoDTO.setFim(fim);
	}
	
	

}
