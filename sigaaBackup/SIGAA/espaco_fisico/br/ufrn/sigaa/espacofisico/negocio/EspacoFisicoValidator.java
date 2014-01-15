/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 07/05/2009
 *
 */
package br.ufrn.sigaa.espacofisico.negocio;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.espacofisico.EspacoFisicoDao;

/**
 * Classe utilitária que contém métodos para realizar diversas validações em espaço físico
 * 
 * @author Henrique André
 *
 */
public class EspacoFisicoValidator {

	/**
	 * Verifica se o código está disponível para cadastro
	 * 
	 * @param codigo
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public static void isCodigoDisponivel(String codigo) throws DAOException, NegocioException {
		
		if (codigo == null || codigo.trim().equals(""))
			throw new NegocioException("Ocorreu um erro. Nenhum código foi fornecido.");
		
		EspacoFisicoDao dao = new EspacoFisicoDao();
		boolean disponivel = dao.isCodigoDisponivel(codigo);
		dao.close();
		
		if (!disponivel)
			throw new NegocioException("Ocorreu um erro. O código (" + codigo  + ") que foi informado já esta sendo usado por outro espaço físico cadastrado.");
	}
	
}
