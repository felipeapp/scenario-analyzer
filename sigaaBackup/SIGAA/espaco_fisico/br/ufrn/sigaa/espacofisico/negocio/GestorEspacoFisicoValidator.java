/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/05/2009
 *
 */
package br.ufrn.sigaa.espacofisico.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.List;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.espacofisico.GestorEspacoFisicoDao;
import br.ufrn.sigaa.espacofisico.dominio.GestorEspacoFisico;

/**
 * Classe utilit�ria com diversos m�todos para realizar valida��es
 * de gestores de espa�o f�sico  
 * 
 * @author Henrique Andr�
 *
 */
public class GestorEspacoFisicoValidator {

	/**
	 * Verifica se o usu�rio j� possui permiss�o para a unidade
	 * 
	 * @throws DAOException 
	 * @throws NegocioException 
	 */
	public static void evitarDuplicidadeGestaoUnidade(GestorEspacoFisico gestorNovo) throws DAOException, NegocioException {
		GestorEspacoFisicoDao dao = new GestorEspacoFisicoDao();
		
		// Essa lista deve ter no m�ximo um elemento (se tiver mais, � porque existe duplicidade e isso � ruim!)
		List<GestorEspacoFisico> listaGestores = dao.findAtivosByUsuarioUnidade(gestorNovo.getUsuario(), gestorNovo.getUnidade());
		
		// Se estiver cadastrando
		if (isEmpty(gestorNovo)) {
			if (!listaGestores.isEmpty())
				throw new NegocioException("Esse Gestor j� possui permiss�o nesta unidade");
		} else { // Se estiver atualizando
			
			if (isEmpty(listaGestores))
				return ;
			
			GestorEspacoFisico gestorAntigo = listaGestores.iterator().next();
			
			if (gestorNovo.getId() != gestorAntigo.getId())
				throw new NegocioException("Esse Gestor j� possui permiss�o nesta unidade");
		}
	}

	/**
	 * Verifica se o usu�rio j� possui permiss�o para o espa�o f�sico
	 * 
	 * @param obj
	 * @throws DAOException 
	 * @throws NegocioException 
	 */
	public static void evitarDuplicidadeGestaoEspacoFisico(GestorEspacoFisico gestorNovo) throws DAOException, NegocioException {
		GestorEspacoFisicoDao dao = new GestorEspacoFisicoDao();
		
		// Essa lista deve ter no m�ximo um elemento (se tiver mais, � porque existe duplicidade e isso � ruim!)
		List<GestorEspacoFisico> listaGestores = dao.findAtivosByUsuarioEspacoFisico(gestorNovo.getUsuario(), gestorNovo.getEspacoFisico());
		
		// Se estiver cadastrando
		if (isEmpty(gestorNovo)) {
			if (!listaGestores.isEmpty())
				throw new NegocioException("Esse Gestor j� possui permiss�o nesta unidade");
		} else { // Se estiver atualizando
			
			if (isEmpty(listaGestores))
				return ;
			
			GestorEspacoFisico gestorAntigo = listaGestores.iterator().next();
			
			if (gestorNovo.getId() != gestorAntigo.getId())
				throw new NegocioException("Esse Gestor j� possui permiss�o nesta unidade");
		}
	}
	
}
