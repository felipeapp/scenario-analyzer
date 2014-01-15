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
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;

/**
 *
 * 	    Processador que realiza as opera��es que envolvem a substitui��o de um fasc�culo.
 *
 * @author jadson
 * @since 26/03/2009
 * @version 1.0 criacao da classe
 *
 */
public class ProcessadorSubstituirFasciculo extends AbstractProcessador{

	
	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		
		validate(mov);
		
		MovimentoSubstituirFasciculo movimento = (MovimentoSubstituirFasciculo) mov;
		
		Fasciculo substituidor = movimento.getFasciculoSubstituidor();
		Fasciculo queVaiSerSubstituido = movimento.getFasciculoQueVaiSerSubstituido();
		
		if( ! queVaiSerSubstituido.isDadoBaixa() ){ //O usu�rio pode substituir um material j� baixado por outro do acervo.
			
			queVaiSerSubstituido.setMotivoBaixa("Substitu�do pelo fasc�culo: "+substituidor.getCodigoBarras());
			
			ProcessadorDarBaixaFasciculo processadorDarBaixaFasciculo = new ProcessadorDarBaixaFasciculo();
			MovimentoDarBaixaFasciculo movAuxiliar1 = new MovimentoDarBaixaFasciculo(queVaiSerSubstituido);
			movAuxiliar1.setCodMovimento(SigaaListaComando.DAR_BAIXA_FASCICULO);
			movAuxiliar1.setUsuarioLogado(movimento.getUsuarioLogado());
			movAuxiliar1.setSistema(movimento.getSistema());
			movAuxiliar1.setApplicationContext(mov.getApplicationContext()); // Para poder chamar o MBean remoto do SIPAC
			processadorDarBaixaFasciculo.execute(movAuxiliar1);
		
		}
		
		substituidor.setMaterialQueEuSubstituo(queVaiSerSubstituido);
		
		ProcessadorAtualizaFasciculo processadorAtualizaFasciculo2 = new ProcessadorAtualizaFasciculo();
		MovimentoAtualizaFasciculo movAuxiliar2 = new MovimentoAtualizaFasciculo(substituidor, "Substituindo o fasc�culo : "
				+queVaiSerSubstituido.getCodigoBarras(), true);
		movAuxiliar2.setCodMovimento(SigaaListaComando.ATUALIZA_FASCICULO);
		movAuxiliar2.setUsuarioLogado(movimento.getUsuarioLogado());
		movAuxiliar2.setSistema(movimento.getSistema());
		processadorAtualizaFasciculo2.execute(movAuxiliar2);

		return null;
	}

	
	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens lista = new ListaMensagens();
		
		MaterialInformacionalDao dao = null;
		
		try{
			
			MovimentoSubstituirFasciculo movimento = (MovimentoSubstituirFasciculo) mov;
			
			dao = getDAO(MaterialInformacionalDao.class, movimento);
			
			Fasciculo substituidor = movimento.getFasciculoSubstituidor();
			Fasciculo queVaiSerSubstituido = movimento.getFasciculoQueVaiSerSubstituido();
			
			if(! queVaiSerSubstituido.isAtivo())
				lista.addErro(" O Fasc�culo n�o pode ser substitu�do porque foi removido do sistema.");
			
			if(! substituidor.isAtivo())
				lista.addErro(" O Fasc�culo: "+substituidor.getCodigoBarras()+" n�o pode substituir outro porque foi removido do sistema. ");
			
			if (!  (queVaiSerSubstituido.getBiblioteca().getId() == substituidor.getBiblioteca().getId()) )
				lista.addErro(" Um Fasc�culo s� pode ser substitu�do por outro da mesma biblioteca.");
			
			if (!  (queVaiSerSubstituido.getAssinatura().getId() == substituidor.getAssinatura().getId()) )
				lista.addErro(" Um Fasc�culo s� pode ser substitu�do por outro da mesma assinatura.");
			
			
			if (substituidor.getMaterialQueEuSubstituo() != null){
				MaterialInformacional materialJaSubstituido = dao.findMaterialSubstituido(substituidor.getId(), false);
				lista.addErro("O fasc�culo: "+substituidor.getCodigoBarras()+" j� est� substituindo o fasc�culo com c�digo de barras: "+ materialJaSubstituido.getCodigoBarras()+".");
			}
				
			if(substituidor.isDadoBaixa()){
				lista.addErro("Esse exemplar "+substituidor.getCodigoBarras()+" n�o pode substituir outro exemplar porque ele est� baixado ");
			}
			
			if (substituidor.getId() == queVaiSerSubstituido.getId())
				lista.addErro(" Um fasc�culo n�o pode ser substitu�do por ele mesmo.");
		
		}finally{
			
			if(dao != null ) { dao.close(); }
			
			checkValidation(lista);
		}
		
	}
}
