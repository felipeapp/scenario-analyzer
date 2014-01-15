/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Classe utilitária com diversos métodos para realizar validações
 * de gestores de espaço físico  
 * 
 * @author Henrique André
 *
 */
public class GestorEspacoFisicoValidator {

	/**
	 * Verifica se o usuário já possui permissão para a unidade
	 * 
	 * @throws DAOException 
	 * @throws NegocioException 
	 */
	public static void evitarDuplicidadeGestaoUnidade(GestorEspacoFisico gestorNovo) throws DAOException, NegocioException {
		GestorEspacoFisicoDao dao = new GestorEspacoFisicoDao();
		
		// Essa lista deve ter no máximo um elemento (se tiver mais, é porque existe duplicidade e isso é ruim!)
		List<GestorEspacoFisico> listaGestores = dao.findAtivosByUsuarioUnidade(gestorNovo.getUsuario(), gestorNovo.getUnidade());
		
		// Se estiver cadastrando
		if (isEmpty(gestorNovo)) {
			if (!listaGestores.isEmpty())
				throw new NegocioException("Esse Gestor já possui permissão nesta unidade");
		} else { // Se estiver atualizando
			
			if (isEmpty(listaGestores))
				return ;
			
			GestorEspacoFisico gestorAntigo = listaGestores.iterator().next();
			
			if (gestorNovo.getId() != gestorAntigo.getId())
				throw new NegocioException("Esse Gestor já possui permissão nesta unidade");
		}
	}

	/**
	 * Verifica se o usuário já possui permissão para o espaço físico
	 * 
	 * @param obj
	 * @throws DAOException 
	 * @throws NegocioException 
	 */
	public static void evitarDuplicidadeGestaoEspacoFisico(GestorEspacoFisico gestorNovo) throws DAOException, NegocioException {
		GestorEspacoFisicoDao dao = new GestorEspacoFisicoDao();
		
		// Essa lista deve ter no máximo um elemento (se tiver mais, é porque existe duplicidade e isso é ruim!)
		List<GestorEspacoFisico> listaGestores = dao.findAtivosByUsuarioEspacoFisico(gestorNovo.getUsuario(), gestorNovo.getEspacoFisico());
		
		// Se estiver cadastrando
		if (isEmpty(gestorNovo)) {
			if (!listaGestores.isEmpty())
				throw new NegocioException("Esse Gestor já possui permissão nesta unidade");
		} else { // Se estiver atualizando
			
			if (isEmpty(listaGestores))
				return ;
			
			GestorEspacoFisico gestorAntigo = listaGestores.iterator().next();
			
			if (gestorNovo.getId() != gestorAntigo.getId())
				throw new NegocioException("Esse Gestor já possui permissão nesta unidade");
		}
	}
	
}
