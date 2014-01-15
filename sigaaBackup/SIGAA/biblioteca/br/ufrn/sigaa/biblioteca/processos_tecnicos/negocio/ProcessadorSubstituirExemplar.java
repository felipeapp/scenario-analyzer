/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 26/03/2009
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;


/**
 *
 *    <p>Processador realiza duas operacoes. Dar baixa em um exemplar e indica que o outro exemplar
 * substituio ele. Para isso, ele faz uso do processador de atualiza��o dos exemplares.</p>
 *
 * @author jadson
 * @since 26/03/2009
 * @version 1.0 criacao da classe
 *
 */
public class ProcessadorSubstituirExemplar extends AbstractProcessador{

	
	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		//GenericDAO dao = null;
		
		try{
		
			validate(mov);
			
			MovimentoSubstituirExemplar movimento = (MovimentoSubstituirExemplar) mov;
			
			Exemplar substituidor = movimento.getExemplarSubstituidor();
			Exemplar queVaiSerSubstituido = movimento.getExemplarQueVaiSerSubstituido();
			
			//dao = getGenericDAO(mov);
			
			
			//// DAR BAIXA EM UM  /////
			
			if( ! queVaiSerSubstituido.isDadoBaixa() ){ //O usu�rio pode substituir um material j� baixado por outro do acervo.
				
				queVaiSerSubstituido.setMotivoBaixa("Substitu�do pelo exemplar: "+substituidor.getCodigoBarras());
				
				ProcessadorDarBaixaExemplar processadorDarBaixaExemplar = new ProcessadorDarBaixaExemplar();
				MovimentoDarBaixaExemplar movAuxiliar1 = new MovimentoDarBaixaExemplar(queVaiSerSubstituido);
				movAuxiliar1.setCodMovimento(SigaaListaComando.DAR_BAIXA_EXEMPLAR);
				movAuxiliar1.setUsuarioLogado(movimento.getUsuarioLogado());
				movAuxiliar1.setSistema(movimento.getSistema());
				movAuxiliar1.setApplicationContext(mov.getApplicationContext()); // Para poder chamar o MBean remoto do SIPAC
				processadorDarBaixaExemplar.execute(movAuxiliar1);
				
			}
			
			///// ALTERA O OUTRO PARA POSSUIR A REFER�NCIA AO QUE FOI BAIXADO /////
			
			substituidor.setMaterialQueEuSubstituo(queVaiSerSubstituido);
			
			ProcessadorAtualizaExemplar processadorAtualizaExemplar = new ProcessadorAtualizaExemplar();
			MovimentoAtualizaExemplar movAuxiliar2 = new MovimentoAtualizaExemplar(substituidor, "Substituindo o exemplar: "+queVaiSerSubstituido.getCodigoBarras());
			movAuxiliar2.setCodMovimento(SigaaListaComando.ATUALIZA_EXEMPLAR);
			movAuxiliar2.setUsuarioLogado(movimento.getUsuarioLogado());
			movAuxiliar2.setSistema(movimento.getSistema());
			processadorAtualizaExemplar.execute(movAuxiliar2);
		
		
		}finally{
			//if(dao != null) dao.close();
		}
		return null;
	}

	
	
	
	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens lista = new ListaMensagens();
		
		MaterialInformacionalDao dao = null;
		
		try{
		
			
			MovimentoSubstituirExemplar movimento = (MovimentoSubstituirExemplar) mov;
		
			dao = getDAO(MaterialInformacionalDao.class, movimento);
			
			Exemplar substituidor = movimento.getExemplarSubstituidor();
			Exemplar queVaiSerSubstituido = movimento.getExemplarQueVaiSerSubstituido();
			
			if(! queVaiSerSubstituido.isAtivo())
				lista.addErro(" O Exemplar n�o pode ser substitu�do porque foi removido do sistema.");
			
			if(! substituidor.isAtivo())
				lista.addErro(" O Exemplar: "+substituidor.getCodigoBarras()+" n�o pode substituir outro porque foi removido do sistema. ");
			
			if(  ! ( queVaiSerSubstituido.getBiblioteca().getId() ==  substituidor.getBiblioteca().getId()) ){
				lista.addErro(" Um exemplar s� pode ser substitu�do por outro da mesma biblioteca.");
			}
			
			if(substituidor.getMaterialQueEuSubstituo() != null){
				MaterialInformacional materialJaSubstituido = dao.findMaterialSubstituido(substituidor.getId(), true);
				lista.addErro("Esse exemplar "+substituidor.getCodigoBarras()+" j� estpa substituindo o exemplar com c�digo de barras: "+ materialJaSubstituido.getCodigoBarras()+".");
			}
			
			if(substituidor.isDadoBaixa()){
				lista.addErro("Esse exemplar "+substituidor.getCodigoBarras()+" n�o pode substituir outro exemplar porque ele est� baixado ");
			}
			
			if(substituidor.getId() == queVaiSerSubstituido.getId()){
				lista.addErro(" Um exemplar n�o pode ser substitu�do por ele mesmo");
			}
		
		}finally{
			if(dao != null ) { dao.close(); }
			
			checkValidation(lista);
		}
		
	}
	
	
	

}
