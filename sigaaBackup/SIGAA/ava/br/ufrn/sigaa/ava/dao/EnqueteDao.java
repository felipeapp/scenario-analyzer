/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 23/01/2008
 * 
 */
package br.ufrn.sigaa.ava.dao;

import static org.hibernate.criterion.Restrictions.eq;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.dominio.Enquete;
import br.ufrn.sigaa.ava.dominio.EnqueteResposta;
import br.ufrn.sigaa.ava.dominio.EnqueteVotos;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * DAO para consultas de enquetes.
 * 
 * @author David Pereira
 *
 */
public class EnqueteDao extends GenericSigaaDAO {

	/**
	 * Usado no EnqueteMBean
	 * Autor: Edson Anibal (ambar@info.ufrn.br)
	 * @throws  
	 * @throws DAOException 
	 */
	public List<Enquete> findEnquetesByTurma(Turma turma) throws DAOException  {
		@SuppressWarnings("unchecked")
		List <Enquete> rs = getSession().createQuery("select e from Enquete e where e.turma.id = " + turma.getId()).list();
		return rs;
	}
	
	/**
	 *busca as enquestes de uma turma de acrodo com acesso.
	 *@param turma, isDocente
	 *@throws DAOException  
	 */
	public List<Enquete> findEnquetesByTurma(Turma turma,boolean isDocente) throws DAOException  {
		
		String hql = "select e from Enquete e where e.turma.id = " + turma.getId();
		
		if (!isDocente) {
			hql += " and e.aula.visivel = trueValue() ";
		}
		
		@SuppressWarnings("unchecked")
		List <Enquete> rs = getSession().createQuery(hql).list();
		return rs;
	}
	

	/**
	 * Retorna a última enquete não expirada cadastrada para uma turma.
	 * @param turma
	 * @param publicada Indica se é para ser enquete publicada ou não.
	 * @return
	 */
	public Enquete findEnqueteMaisAtualByTurma(Turma turma, boolean publicada) {
		try {
			String valorPublicada = (publicada ? "true" : "false") + "Value()";

			Query q = getSession().createQuery("select e from Enquete e left join e.respostas r where e.turma.id = ? and e.publicada = "+valorPublicada+" and e.dataFim > now() and e.aula.visivel = trueValue() order by e.data desc");
			q.setInteger(0, turma.getId());
			q.setMaxResults(1);
			return (Enquete) q.uniqueResult();
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}
	}

	/**
	 * Verifica se um detemrinado usuario já votou na enquete
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public EnqueteResposta getRespostaUsuarioEnquete(Usuario usuario, Enquete enquete) {
		if (enquete != null && usuario != null) {
			DetachedCriteria c = DetachedCriteria.forClass(EnqueteVotos.class);
			c.add(eq("usuario.id", usuario.getId())).createCriteria("enqueteResposta").add(eq("enquete.id", enquete.getId()));
			EnqueteVotos voto = (EnqueteVotos) getHibernateTemplate().uniqueResult(c);
			if (voto != null)
				return voto.getEnqueteResposta();
		}

		return null;
	}



	/**
	 * Faz uma estatística de quantos votos cada opção da enquete tem.
	 * @return retorna varios objetos EnqueteResposta com a propriedade TotalVotos setada.
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public List<EnqueteResposta> findEstatisticaDeVotosbyEnquete(Enquete enquete) throws DAOException {
		Query q = getSession().createQuery("from EnqueteResposta er where er.enquete.id = ?");
		q.setInteger(0, enquete.getId());
		
		@SuppressWarnings("unchecked")
		List<EnqueteResposta> result = q.list();

		return result;
	}

	/**
	 * Diz quantos votos uma enquete teve
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public String findTotalVotosbyEnquete(Enquete enquete) throws DAOException
	{
		String hql ="Select count(ev.enqueteResposta.id) From EnqueteVotos ev " +
		"Where ev.enqueteResposta.enquete.id ="+ enquete.getId();

		Query q = getSession().createQuery(hql);

		return q.uniqueResult().toString();

	}

	/**
	 * Usado no EnqueteRespostaMBean
	 * Autor: Edson Anibal (ambar@info.ufrn.br)
	 */
	public List<EnqueteResposta> findRespostasByEnquete(int idEnquete) throws DAOException
	{
		try
		{
			Criteria c = getCriteria(EnqueteResposta.class);
			c.add(Expression.eq("enquete.id", idEnquete));
			
			@SuppressWarnings("unchecked")
			List<EnqueteResposta> lista = c.list();
			return lista;
		} catch (Exception e) { throw new DAOException(e); }
	}

	/**
	 * Encontra as respostas de uma enquete.
	 * @param idRespostaSeleciona
	 * @return
	 * @throws DAOException
	 */
	public EnqueteResposta findResposta(int idRespostaSeleciona) throws DAOException {

		Query q = getSession().createQuery("from EnqueteResposta er where er.id = ?");
		q.setInteger(0, idRespostaSeleciona);

		return (EnqueteResposta) q.uniqueResult();	
	}

	/**
	 * Remove uma enquete e seus respectivos votos da Comunidade Virtual
	 * @param id
	 */
	public void removerEnqueteComVotosComunidadeVirtual(int id) {

		try {
			@SuppressWarnings("unchecked")
			Iterator itx = getSession().createSQLQuery("select id_resposta from cv.resposta_enquete where id_enquete=?").setInteger(0, id).list().iterator();
			while (itx.hasNext()) {
				int idx = Integer.parseInt(String.valueOf(itx.next()));
				update("delete from cv.enquete_votos where id_resposta=?", idx);
			}

			update("delete from cv.resposta_enquete where id_enquete=?", id);
			update("delete from cv.enquete where id_enquete=?", id);

		} catch (HibernateException e) {
			e.printStackTrace();
		} catch (DAOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Remove uma enquete com seus respectivos votos da Turma Virtual
	 * @param id
	 */
	public void removerEnqueteComVotos(int id) {

		try {
			@SuppressWarnings("unchecked")
			Iterator itx = getSession().createSQLQuery("select id_resposta from ava.resposta_enquete where id_enquete=?").setInteger(0, id).list().iterator();
			while (itx.hasNext()) {
				int idx = Integer.parseInt(String.valueOf(itx.next()));
				update("delete from ava.enquete_votos where id_resposta=?", idx);
			}

			update("delete from ava.resposta_enquete where id_enquete=?", id);
			update("delete from ava.enquete where id_enquete=?", id);

		} catch (HibernateException e) {
			e.printStackTrace();
		} catch (DAOException e) {
			e.printStackTrace();
		}
	}
	
}
