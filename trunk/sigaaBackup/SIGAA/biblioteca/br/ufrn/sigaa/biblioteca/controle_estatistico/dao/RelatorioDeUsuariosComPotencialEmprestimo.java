/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 13/06/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dao;

import java.util.Map;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;

/**
 *
 * <p> Contém as consultas utilizadas para buscar usuários da biblioteca </p>
 * 
 * @author jadson
 *
 */
public interface RelatorioDeUsuariosComPotencialEmprestimo extends GenericDAO{

	public Map<String, Integer> findQtdUsuariosComPotencialDeEmprestimo( VinculoUsuarioBiblioteca vinculoUsuario )  throws DAOException;
	
}
