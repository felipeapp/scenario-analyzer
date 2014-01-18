package br.ufrn.sigaa.ensino.metropoledigital.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.CoordenadorTutorIMD;


/**
 * Dao responsável por consultas da classe CoordenadorTutorIMD.
 * 
 * @author Rafael Barros
 *
 */
public class CoordenadorTutorIMDDao extends GenericSigaaDAO {
	
	/**
	 * Lista os coordenadores de pólo do IMD contendo o nome, CPF ou pólo
	 * 
	 * @param nome
	 * @param cpf
	 * @return
	 * @throws DAOException
	 */
	public Collection<CoordenadorTutorIMD> findCoordenador(String nome, Long cpf) throws DAOException{
		try {
			Criteria c = getSession().createCriteria(CoordenadorTutorIMD.class);
			Criteria cPessoa = c.createCriteria("pessoa");
						
			if(nome != null){
				if(nome.length() > 0 && nome != "") {
					cPessoa.add(Expression.like("nome", nome.toUpperCase() + "%"));
				}
			}
			
			if(cpf != null){
				if(cpf > 0){
					cPessoa.add(Expression.eq("cpf_cnpj", cpf));
				}
			}
			
			
			
			cPessoa.addOrder(Order.asc("nome"));
			
			return c.list();
			
		} catch (DAOException e) {
			 throw new DAOException(e);
		}
	}
	
	
	/**
	 * Lista os coordenadores de pólo do IMD por pessoa
	 * 
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	public Collection<CoordenadorTutorIMD> findCoordenadorByPessoa(int idPessoa) throws DAOException{
		try {
			Criteria c = getSession().createCriteria(CoordenadorTutorIMD.class);
			Criteria cPessoa = c.createCriteria("pessoa");
			cPessoa.add(Expression.eq("id", idPessoa));
			
			cPessoa.addOrder(Order.asc("nome"));
			
			Collection<CoordenadorTutorIMD> lista = new ArrayList<CoordenadorTutorIMD>();
			
			lista = c.list();
			
			if(lista.isEmpty()){
				return Collections.emptyList();
			} else {
				return lista;
			}
			
		} catch (DAOException e) {
			 throw new DAOException(e);
		}
	}	
	
	
	/**
	 * Verifica se a pessoa informada é um coordenador de pólo do IMD
	 * 
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	public boolean existeCoordenador(int idPessoa) throws DAOException{
		try {
			Criteria c = getSession().createCriteria(CoordenadorTutorIMD.class);
			Criteria cPessoa = c.createCriteria("pessoa");
			cPessoa.add(Expression.eq("id", idPessoa));
			
			Collection<CoordenadorTutorIMD> lista = new ArrayList<CoordenadorTutorIMD>();
			lista = c.list();
			
			if(lista.isEmpty()){
				return false;
			} else {
				return true;
			}
			
		} catch (DAOException e) {
			 throw new DAOException(e);
		}
	}	
	
	
	
	
	
}
