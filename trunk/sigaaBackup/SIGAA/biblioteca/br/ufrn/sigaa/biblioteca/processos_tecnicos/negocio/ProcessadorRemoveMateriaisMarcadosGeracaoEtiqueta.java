/*
 * ProcessadorRemoveMateriaisMarcadosGeracaoEtiqueta.java
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
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.ImpressaoEtiquetasDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MateriaisMarcadosParaGerarEtiquetas;

/**
 *
 *    Remove os materiais que estavam marcados como pendentes para a geração das etiquetas.
 *
 * @author jadson
 * @since 19/10/2009
 * @version 1.0 criacao da classe
 *
 */
public class ProcessadorRemoveMateriaisMarcadosGeracaoEtiqueta extends AbstractProcessador{

	
	/**
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,RemoteException {
		
		
		ImpressaoEtiquetasDao dao = null;
		
		MovimentoRemoveMateriaisMarcadosGeracaoEtiqueta movimento = (MovimentoRemoveMateriaisMarcadosGeracaoEtiqueta) mov;
		
		try{
		
			dao = getDAO(ImpressaoEtiquetasDao.class, mov);
		
			
			if(movimento.getIdsMateriais().size() > 0){
			
				List<MateriaisMarcadosParaGerarEtiquetas> listaMateriais 
					= dao.findMateriaisPendentesGerouEtiqueta(movimento.getIdsMateriais());
			
				
				for (MateriaisMarcadosParaGerarEtiquetas exemplarMarcado: listaMateriais) {
					dao.remove(exemplarMarcado);
				}
			
			}
		
		}finally{
			if(dao != null) dao.close();
		}
		
		return null;
	}

	
	
	/**
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// Não há
	}

}
