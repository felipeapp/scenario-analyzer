package br.ufrn.sigaa.ensino.metropoledigital.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.TutorIMD;
import br.ufrn.sigaa.ouvidoria.dominio.AcompanhamentoManifestacao;


/**
 * Dao responsável por consultas da classe TutorIMD.
 * 
 * @author Rafael Barros
 *
 */
public class TutorIMDDao extends GenericSigaaDAO {
	
	/**
	 * Lista os tutores IMD contendo o nome, CPF ou pólo
	 * 
	 * @param nome
	 * @param cpf
	 * @param idPolo
	 * @return
	 * @throws DAOException
	 */
	public Collection<TutorIMD> findTutor(String nome, Long cpf, Integer idPolo) throws DAOException{
		try {
			Criteria c = getSession().createCriteria(TutorIMD.class);
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
	 * Lista os usuários vinculados aos tutores do IMD contendo o pólo
	 * 
	 * @param idPolo
	 * @return Coleção de usuários que estão vinculados aos tutores do IMD
	 * @throws DAOException
	 */
	public Collection<Usuario> findUsuariosByPolo(Integer idPolo) throws DAOException{
		UsuarioDao usuDao = new UsuarioDao();
		try {
			Criteria c = getSession().createCriteria(TutorIMD.class);
			Criteria cPessoa = c.createCriteria("pessoa");
			Criteria cPolo = c.createCriteria("polo");
			cPolo.add(Expression.eq("id", idPolo));
			cPessoa.addOrder(Order.asc("nome"));
			
			Collection<TutorIMD> listaTutores = c.list();
			
			
			Collection<Usuario> listaUsuarios = new ArrayList<Usuario>();
			Collection<Usuario> listaAux = new ArrayList<Usuario>();
			Date dataAtual = new Date();
			
			if(! listaTutores.isEmpty()) {
				for(TutorIMD tutor: listaTutores){
					if(tutor.getDataFim().after(dataAtual)) {
						listaAux = usuDao.findByPessoa(tutor.getPessoa());
						for(Usuario usu: listaAux){
							if(usu.getId() != getUsuario().getId()) {
								listaUsuarios.add(usu);
							}
						}
					}
				}
			} else {
				listaUsuarios = Collections.emptyList();
			}
			
			return listaUsuarios;
			
		} catch (DAOException e) {
			 throw new DAOException(e);
		} finally {
			usuDao.close();
		}
	}
	
	
	/**
	 * Lista os tutores IMD por pessoa
	 * 
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	public Collection<TutorIMD> findTutorByPessoa(int idPessoa) throws DAOException{
		try {
			Criteria c = getSession().createCriteria(TutorIMD.class);
			Criteria cPessoa = c.createCriteria("pessoa");
			cPessoa.add(Expression.eq("id", idPessoa));
			
			cPessoa.addOrder(Order.asc("nome"));
			
			Collection<TutorIMD> lista = new ArrayList<TutorIMD>();
			
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
	
	
}
