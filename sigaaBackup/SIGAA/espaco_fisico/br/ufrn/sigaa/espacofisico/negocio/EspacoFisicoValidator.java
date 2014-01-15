/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Classe utilit�ria que cont�m m�todos para realizar diversas valida��es em espa�o f�sico
 * 
 * @author Henrique Andr�
 *
 */
public class EspacoFisicoValidator {

	/**
	 * Verifica se o c�digo est� dispon�vel para cadastro
	 * 
	 * @param codigo
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public static void isCodigoDisponivel(String codigo) throws DAOException, NegocioException {
		
		if (codigo == null || codigo.trim().equals(""))
			throw new NegocioException("Ocorreu um erro. Nenhum c�digo foi fornecido.");
		
		EspacoFisicoDao dao = new EspacoFisicoDao();
		boolean disponivel = dao.isCodigoDisponivel(codigo);
		dao.close();
		
		if (!disponivel)
			throw new NegocioException("Ocorreu um erro. O c�digo (" + codigo  + ") que foi informado j� esta sendo usado por outro espa�o f�sico cadastrado.");
	}
	
}
