package br.ufrn.sigaa.arq.dao.ensino.latosensu;

import java.util.Collection;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.latosensu.dominio.CorpoDocenteDisciplinaLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.PropostaCursoLato;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * DAO para realizar consultas no Corpo Docente das Disciplinas do Curso de Lato Sensu.
 * 
 * @author guerethes
 */
public class CorpoDocenteDisciplinaLatoDao extends GenericSigaaDAO {

	/** Construtor padrão. */
	public CorpoDocenteDisciplinaLatoDao(){
	}

	/**
	 * Faz um busca levando em conta o Docente ou DocenteExterno e a proposta.
	 * 
	 * @param proposta
	 * @param docente
	 * @param docenteExterno
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<CorpoDocenteDisciplinaLato> findByPropostaDocente(Integer proposta, Integer docente, Integer docenteExterno) throws DAOException {
		
		StringBuilder hql = new StringBuilder("from CorpoDocenteDisciplinaLato cddl where cddl.proposta = :proposta ");
		
		if (docente != null) 
			hql.append(" and cddl.docente = :docente ");
		if (docenteExterno != null) 
			hql.append(" and cddl.docenteExterno = :docenteExterno ");
		
		Query q = getSession().createQuery(hql.toString());
		if (proposta != null) q.setInteger("proposta", proposta);
		if (docente != null) q.setInteger("docente", docente);
		if (docenteExterno != null)	q.setInteger("docenteExterno", docenteExterno);
		Collection<CorpoDocenteDisciplinaLato> coordDocente = q.list(); 
		return coordDocente;
	}

	/**
	 * Retorna todo o corpo docente da Disciplina do Curso Lato da proposta e disciplinas informadas.
	 *  
	 * @param proposta
	 * @param disciplina
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<CorpoDocenteDisciplinaLato> findByPropostaDisciplina(Integer proposta, Integer disciplina) throws DAOException {
		
		StringBuilder hql = new StringBuilder("from CorpoDocenteDisciplinaLato cddl " +
				" where cddl.proposta = :proposta and cddl.disciplina = :disciplina");
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("proposta", proposta);
		q.setInteger("disciplina", disciplina);
		Collection<CorpoDocenteDisciplinaLato> coordDocente = q.list(); 
		return coordDocente;
	}
	
	/**
	 * Verifica se o docente já ministra alguma disciplina na proposta informada.
	 * 
	 * @param docente
	 * @param proposta
	 * @return
	 * @throws DAOException
	 */
	public boolean validarDocenteMinistraDisciplina(Servidor docente, PropostaCursoLato proposta) throws DAOException{
		Query q = getSession().createQuery("from CorpoDocenteDisciplinaLato cddl " +
				" where cddl.docente = :docente and cddl.proposta = :proposta");
		q.setInteger("proposta", proposta.getId());
		q.setInteger("docente", docente.getId());
		if (q.list().isEmpty())
			return false;
		return true;
	}

	

}