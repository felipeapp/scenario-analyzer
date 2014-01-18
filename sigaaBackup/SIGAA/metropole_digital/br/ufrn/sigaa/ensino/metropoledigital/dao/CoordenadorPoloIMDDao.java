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
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.CoordenadorPoloIMD;


/**
 * Dao responsável por consultas da classe CoordenadorPoloIMD.
 * 
 * @author Rafael Barros
 *
 */
public class CoordenadorPoloIMDDao extends GenericSigaaDAO {
	
	/**
	 * Retorna a quantidade total de vagas do processo seletivo do IMD
	 * 
	 * @param idProcessoSeletivo
	 * @return
	 * @throws DAOException
	 */
	public Collection<CoordenadorPoloIMD> findCoordenador(String nome, Long cpf, Integer idPolo) throws DAOException{
		try {
			Criteria c = getSession().createCriteria(CoordenadorPoloIMD.class);
			Criteria cPessoa = c.createCriteria("pessoa");
			Criteria cPolo = c.createCriteria("polo");
						
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
			
			if (idPolo != null) {
				if(idPolo > 0) {
					cPolo.add(Expression.eq("id", idPolo));
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
	public Collection<CoordenadorPoloIMD> findCoordenadorByPessoa(int idPessoa) throws DAOException{
		try {
			Criteria c = getSession().createCriteria(CoordenadorPoloIMD.class);
			Criteria cPessoa = c.createCriteria("pessoa");
			c.createCriteria("polo");
			cPessoa.add(Expression.eq("id", idPessoa));
			
			cPessoa.addOrder(Order.asc("nome"));
			
			Collection<CoordenadorPoloIMD> lista = new ArrayList<CoordenadorPoloIMD>();
			
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
			Criteria c = getSession().createCriteria(CoordenadorPoloIMD.class);
			Criteria cPessoa = c.createCriteria("pessoa");
			c.createCriteria("polo");
			cPessoa.add(Expression.eq("id", idPessoa));
			
			Collection<CoordenadorPoloIMD> lista = new ArrayList<CoordenadorPoloIMD>();
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
	
	
	/**
	 * Retorna uma coleção de pólos em que a pessoa informada possui vínculo
	 * 
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	public Collection<Polo> findPolosByCoordenador(int idPessoa) throws DAOException{
		try {
			Criteria c = getSession().createCriteria(CoordenadorPoloIMD.class);
			Criteria cPessoa = c.createCriteria("pessoa");
			c.createCriteria("polo");
			cPessoa.add(Expression.eq("id", idPessoa));
			
			Collection<Polo> listaPolos = new ArrayList<Polo>();
			Collection<CoordenadorPoloIMD> listaCoord = new ArrayList<CoordenadorPoloIMD>();
			listaCoord = c.list();
			
			if(listaCoord.isEmpty()){
				return Collections.EMPTY_LIST;
			} else {
				for(CoordenadorPoloIMD coord: listaCoord){
					listaPolos.add(coord.getPolo());
				}
			}
			return listaPolos;
			
		} catch (DAOException e) {
			 throw new DAOException(e);
		}
	}	
	
	
}
