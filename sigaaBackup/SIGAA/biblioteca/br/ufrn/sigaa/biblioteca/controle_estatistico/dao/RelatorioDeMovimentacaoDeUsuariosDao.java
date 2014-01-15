/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 27/04/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;

/**
 * <p>Dao exclusivo para a consulta para o relat�rio de motimenta��o dos usu�rios cadastrados na biblioteca </p>
 * 
 * @author jadson
 *
 */
public class RelatorioDeMovimentacaoDeUsuariosDao extends GenericSigaaDAO{

	
	
	/**
	 * Retorna a quantidade de usu�rios que se cadastraram ou foram quitados em um determinado 
	 * per�odo ou est�o ativos para usar os servi�os da biblioteca
	 *  
	 *
	 * @param vinculos a lista de v�nculos selecionados pelo usu�rios
	 * @param inicio a data de in�cio do per�odo
	 * @param fim a data de final do per�odo
	 * 
	 * @return uma lista contendo 3 elementos.  o 1� os dados do usu�rios novos, 2� os quitados e 3� os dos usu�rios ativos
	 * 
	 *  Os dados s�o uma lista de arrays de 2 possi��es;  [0] =  o v�nculo, [1] = a quantidade de usu�rios
	 * 
	 * @throws DAOException
	 */
	public List<List<Object[]>> findInformacoesMotimentacaoUsuarios(List<VinculoUsuarioBiblioteca> vinculos, Date inicio, Date fim )throws DAOException {
		
		
		if(vinculos == null || vinculos.size() == 0 || inicio == null || fim == null)
			throw new IllegalArgumentException("� preciso selecionar o v�nculo do usu�rio ");
		
		
		List<List<Object[]>> retorno = new ArrayList<List<Object[]>>();
		
		String hqlNovosUsuarios = " SELECT  vinculo, count ( distinct id) FROM UsuarioBiblioteca  ub " 
			+" WHERE ativo = trueValue() AND  quitado = falseValue() AND  vinculo IS NOT NULL  "
			+" AND vinculo in "+UFRNUtils.gerarStringIn( vinculos.toArray() ) 
			+" AND dataCadastro between :inicio AND :fim  "
			+" GROUP BY vinculo ";

		String hqlUsuarioQuitados = " SELECT  vinculo, count ( distinct id) FROM UsuarioBiblioteca  ub " 
			+" WHERE ativo = trueValue() AND  quitado = trueValue() AND  vinculo IS NOT NULL  "
			+" AND vinculo in "+UFRNUtils.gerarStringIn( vinculos.toArray() ) 
			+" AND dataQuitacao between :inicio AND :fim  "
			+" GROUP BY vinculo ";
		
		String hqlUsuariosAtivos = " SELECT  vinculo, count ( distinct id) FROM UsuarioBiblioteca  ub " 
			+" WHERE ativo = trueValue() AND  quitado = falseValue() AND  vinculo IS NOT NULL  "
			+" AND vinculo in "+UFRNUtils.gerarStringIn( vinculos.toArray() ) 
			+" GROUP BY vinculo ";

		String hqlUsuariosTotal = " SELECT  vinculo, count ( distinct id) FROM UsuarioBiblioteca  ub " 
			+" WHERE ativo = trueValue() AND  vinculo IS NOT NULL  AND vinculo in "+UFRNUtils.gerarStringIn( vinculos.toArray() ) 
			+" GROUP BY vinculo ";
		
		
		
		/// Novos ///
		Query qNovosUsuarios = getSession().createQuery(hqlNovosUsuarios);
		qNovosUsuarios.setTimestamp("inicio",  CalendarUtils.configuraTempoDaData(inicio, 0, 0, 0, 0) );
		qNovosUsuarios.setTimestamp("fim", CalendarUtils.configuraTempoDaData(fim, 23, 59, 59, 999) );
		
		@SuppressWarnings("unchecked")
		List <Object[]> listaNovosUsuarios = qNovosUsuarios.list();
		
		
		/// Quitados ///
		Query qUsuarioQuitados = getSession().createQuery(hqlUsuarioQuitados);
		qUsuarioQuitados.setTimestamp("inicio", CalendarUtils.configuraTempoDaData(inicio, 0, 0, 0, 0));
		qUsuarioQuitados.setTimestamp("fim", CalendarUtils.configuraTempoDaData(fim, 23, 59, 59, 999) );
		
		@SuppressWarnings("unchecked")
		List <Object[]> listaUsuarioQuitados = qUsuarioQuitados.list();
		
		
		/// Ativos ///
		Query qUsuariosAtivos = getSession().createQuery(hqlUsuariosAtivos);
	
		@SuppressWarnings("unchecked")
		List <Object[]> listaUsuariosAtivos = qUsuariosAtivos.list();
		
		
		/// Total ///
		Query qUsuariosTotal = getSession().createQuery(hqlUsuariosTotal);
	
		@SuppressWarnings("unchecked")
		List <Object[]> listaUsuariosTotal = qUsuariosTotal.list();
		
		
		retorno.add(listaNovosUsuarios);
		retorno.add(listaUsuarioQuitados);
		retorno.add(listaUsuariosAtivos);
		retorno.add(listaUsuariosTotal);
		
		return retorno;
	}
	
}
