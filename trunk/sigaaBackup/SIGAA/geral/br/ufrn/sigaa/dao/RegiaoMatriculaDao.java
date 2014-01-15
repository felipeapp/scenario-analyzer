/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 13/02/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.dominio.RegiaoMatricula;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

/**
 * DAO com consultas espec�ficas de regi�es campus para matr�cula.
 * 
 * @author Rafael Gomes
 *
 */
public class RegiaoMatriculaDao extends GenericSigaaDAO{

	/**
	 * Retorna a cole��o de regi�es de matr�cula para campus de ensino da �rea f�sica de matr�cula do discente.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<RegiaoMatricula> findRegiaoMatriculaByCampus(CampusIes campus, Municipio municipioCampus) throws DAOException {
		
		String hql = "select rm from RegiaoMatriculaCampus rmc " +
				" inner join rmc.regiaoMatricula rm " + 
				" where 1 = 1 ";
		if ( campus != null )
			hql +=  "and rmc.campusIes.id = :idCampus" ;
		if ( municipioCampus != null )
			hql +=  "and rmc.campusIes.endereco.municipio.id = :idMunicipio" ;
		
		Query query = getSession().createQuery(hql);
		if ( campus != null )
			query.setInteger("idCampus", campus.getId());
		if ( municipioCampus != null )
			query.setInteger("idMunicipio", municipioCampus.getId());
		
		@SuppressWarnings("unchecked")
		Collection<RegiaoMatricula> lista = query.list();
		return lista;
	}
	
	
	/**
	 * Retorna a cole��o de regi�es de matr�cula por n�vel de ensino e nome.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<RegiaoMatricula> findByNome(String nome, Character nivelEnsino, Integer idRegiao) throws DAOException {
		
		String hql = "select rm from RegiaoMatricula rm " +
				" where 1 = 1 ";
		if (!isEmpty(nivelEnsino))
			hql +=  " and rm.nivel = :nivelEnsino " ;
		
		if (!isEmpty(idRegiao))
			hql +=  " and rm.id != :idRegiao " ;
		
		if ( !isEmpty(nome) )
			hql +=  " and " + UFRNUtils.convertUtf8UpperLatin9("rm.nome") + " = '" 
				+ UFRNUtils.trataAspasSimples(StringUtils.toAscii(nome.toUpperCase()))+"'" ;
		
		Query query = getSession().createQuery(hql);
		if (!isEmpty(nivelEnsino))
			query.setCharacter("nivelEnsino", nivelEnsino);
		if (!isEmpty(idRegiao))
			query.setInteger("idRegiao", idRegiao);
		
		@SuppressWarnings("unchecked")
		Collection<RegiaoMatricula> lista = query.list();
		return lista;
	}
	
}