/*
 * ProcessadorAtribuiFasciculoAoArtigo.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * Campos Universit�rio Lagoa Nova
 * Natal - RN - Brasil
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ArtigoDePeriodico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;

/**
 *
 * Atualiza o artigo atribuindo um fasc�culo a ele.
 * 
 * Usado para concertar os dados inv�lidos da migra��o de artigos do sistema antigo da biblioteca.<br/>
 * Na migra��o do dados da aleph, n�o se conseguiu saber a qual fasc�culo, um determinado artigo pertencia.
 * 
 *
 * @author jadson
 * @since 22/09/2009
 * @version 1.0 criacao da classe
 *
 */
public class ProcessadorAtribuiFasciculoAoArtigo extends AbstractProcessador{

	
	/**
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		MovimentoAtribuiFasciculoAoArtigo movimento = (MovimentoAtribuiFasciculoAoArtigo) mov;
		
		ArtigoDePeriodico artigo = movimento.getArtigo();
		
		GenericDAO dao = getGenericDAO(mov);
		
		Fasciculo f = dao.refresh(artigo.getFasciculo());
		
		dao.updateField(ArtigoDePeriodico.class, artigo.getId(), "fasciculo", f);
		
		return null;
	}

	
	/**
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// N�o tem.
		
	}

}
