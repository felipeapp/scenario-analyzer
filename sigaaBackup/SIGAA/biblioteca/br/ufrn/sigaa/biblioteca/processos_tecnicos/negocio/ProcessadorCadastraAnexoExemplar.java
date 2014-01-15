/*
 * ProcessadorCadastraAnexoExemplar.java
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 15/04/2009
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;

/**
 *
 *    Processador para cadastrar um anexo de um exemplar.
 *
 * @author jadson
 * @since 15/04/2009
 * @version 1.0 criacao da classe
 *
 */
public class ProcessadorCadastraAnexoExemplar extends AbstractProcessador{

	
	/**
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		
		MovimentoCadastraAnexoExemplar movimento = (MovimentoCadastraAnexoExemplar) mov;
		
		Exemplar anexo = movimento.getAnexo();
	
		
		
		Exemplar principal = movimento.getPrincipal();
		
		int proximoNumeroGerador = anexo.criaCodigoBarrasAnexo(principal);
		
		anexo.setAnexo(true); // Importante, lembrar de setar o anexo com "true"
		
		principal.setNumeroGeradorCodigoBarrasAnexos(proximoNumeroGerador); // atualiza o número gerador dos anexos
		
		List<Exemplar> listaTemp = new ArrayList<Exemplar>();
		listaTemp.add(anexo);
		
		
		ProcessadorCadastraExemplar processadorCadastraExemplar = new ProcessadorCadastraExemplar();
		MovimentoCadastraExemplar movAuxiliar1 = new MovimentoCadastraExemplar(listaTemp);
		movAuxiliar1.setCodMovimento(SigaaListaComando.CADASTRA_EXEMPLAR);
		movAuxiliar1.setUsuarioLogado(movimento.getUsuarioLogado());
		movAuxiliar1.setSistema(movimento.getSistema());
		processadorCadastraExemplar.execute(movAuxiliar1);
		
		ProcessadorAtualizaExemplar processadorAtualizaExemplar = new ProcessadorAtualizaExemplar();
		MovimentoAtualizaExemplar movAuxiliar2 = new MovimentoAtualizaExemplar(principal, "Cadastrado o anexo: "+anexo.getCodigoBarras()+" ao exemplar", false);
		movAuxiliar2.setCodMovimento(SigaaListaComando.ATUALIZA_EXEMPLAR);
		movAuxiliar2.setUsuarioLogado(movimento.getUsuarioLogado());
		movAuxiliar2.setSistema(movimento.getSistema());
		processadorAtualizaExemplar.execute(movAuxiliar2);
		
		
		return null;
	}

	
	/**
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// NÃO TEM, ESSE PROCESSADOR APENAS DELEGA PARA OUTROS PARA TORNAR A OPERAÇÃO TRANSACIONAL
	}

}
