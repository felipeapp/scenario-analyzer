/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 18/03/2010
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import javax.faces.model.DataModel;


/**
 * Representa uma inscri��o e suas respostas ao question�rio quando existir. 
 * Utilizado na impress�o em lote dos inscritos de um processo seletivo.
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
