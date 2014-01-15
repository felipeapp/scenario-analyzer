/*
 * ProcessadorAtribuiFasciculoAoArtigo.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
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
 * Atualiza o artigo atribuindo um fascículo a ele.
 * 
 * Usado para concertar os dados inválidos da migração de artigos do sistema antigo da biblioteca.<br/>
 * Na migração do dados da aleph, não se conseguiu saber a qual fascículo, um determinado artigo pertencia.
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
		// Não tem.
		
	}

}
