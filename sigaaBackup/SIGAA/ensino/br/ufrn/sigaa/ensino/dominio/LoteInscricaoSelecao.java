/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 18/03/2010
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import javax.faces.model.DataModel;


/**
 * Representa uma inscrição e suas respostas ao questionário quando existir. 
 * Utilizado na impressão em lote dos inscritos de um processo seletivo.
 * 
 * @author sist-sigaa-12
 *
 */
public class LoteInscricaoSelecao {

	private InscricaoSelecao inscricaoSelecao;
	private DataModel respostasModel;
	
	
	public LoteInscricaoSelecao(){
	}
	
	public LoteInscricaoSelecao(InscricaoSelecao inscricao, DataModel respostaModel){
		this.inscricaoSelecao = inscricao;
		this.respostasModel = respostaModel;
	}
	
	public InscricaoSelecao getInscricaoSelecao() {
		return inscricaoSelecao;
	}
	
	public void setInscricaoSelecao(InscricaoSelecao inscricao) {
		this.inscricaoSelecao = inscricao;
	}
	
	public DataModel getRespostasModel() {
		return respostasModel;
	}
	
	public void setRespostasModel(DataModel respostasModel) {
		this.respostasModel = respostasModel;
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}
	
	
}
