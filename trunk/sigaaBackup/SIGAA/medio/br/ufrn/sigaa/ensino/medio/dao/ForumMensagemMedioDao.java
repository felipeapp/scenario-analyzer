/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 22/09/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.dao;

import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.medio.dominio.ForumMensagemMedio;

/**
 * DAO para operações relacionadas ao Fórum.
 * Este DAO é responsável pela busca de fóruns e mensagens,
 * além da busca de informações relacionadas, como a quantidade de mensagens do fórum, etc. 
 * 
 * @author Rafael Gomes
 *
 */
public class ForumMensagemMedioDao extends GenericSigaaDAO{

	/**
	 * Localiza as mensagens de fóruns de ensino médio por docente
	 * 
	 * @param idDocente
	 * @param idTurma
	 * @return
	 * @throws DAOException
	 */
	public List<ForumMensagemMedio> findForumMensagemByDocenteAno(Integer idDocente, Integer idDocenteExterno, int ano) throws DAOException {
		
		String projecao = " fm.id, fm.titulo, fm.usuario.pessoa.id, fm.usuario.pessoa.nome, fm.usuario.login, " +
				"fm.usuario.id , fm.respostas, fm.ultimaPostagem, fm.data, fm.forum.id, fm.forum.nivel, " +
				"fm.forum.turmaSerie.id, fm.forum.turmaSerie.nome, fm.forum.turmaSerie.serie.numero, fm.forum.turmaSerie.serie.descricao ";
		
		StringBuffer hql = new StringBuffer();
		
		hql.append("select "+ projecao +" from TurmaSerieAno tsa, DocenteTurma dt, ForumMensagemMedio fm " +
				" inner join tsa.turma t " +
				" inner join tsa.turmaSerie ts " +
				" join fm.forum f " +
				" where dt.turma.id = t.id " +
				" and f.turmaSerie.id = ts.id " +
				" and ts.ano = "+ ano +
				" and (f.ativo = trueValue() or f.ativo is null) " +
				" and fm.topico is null " +
				" and dt.");
		if (idDocente != null)
			hql.append("docente.id = " + idDocente);
		else if (idDocenteExterno != null)
			hql.append("docenteExterno.id = " + idDocenteExterno);
		else
			return null;
		
		Query q = getSession().createQuery(hql.toString());
		@SuppressWarnings("unchecked")
		List<ForumMensagemMedio> lista = (List<ForumMensagemMedio>) HibernateUtils.parseTo(q.list(), projecao, ForumMensagemMedio.class, "fm");
		return lista;
	}
	
}
