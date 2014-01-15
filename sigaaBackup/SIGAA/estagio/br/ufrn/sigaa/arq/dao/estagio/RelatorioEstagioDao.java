/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 18/11/2010
 */
package br.ufrn.sigaa.arq.dao.estagio;

import java.util.Collection;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.estagio.dominio.Estagiario;
import br.ufrn.sigaa.estagio.dominio.RelatorioEstagio;
import br.ufrn.sigaa.estagio.dominio.RenovacaoEstagio;
import br.ufrn.sigaa.estagio.dominio.StatusRelatorioEstagio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO responsável por gerenciar o acesso a dados dos Relatórios de Estágio
 * 
 * @author Arlindo Rodrigues
 */
public class RelatorioEstagioDao extends GenericSigaaDAO {
	
	/**
	 * Retorna os Relatórios do Estágio e Pessoa informados 
	 * @param e
	 * @param p
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<RelatorioEstagio> findRelatorioByEstagio(Estagiario estagio, Pessoa pessoa, RenovacaoEstagio renovacao, Integer idTipo, boolean apenasAprovados) throws HibernateException, DAOException{
		String projecao = "re.id, re.dataCadastro, re.registroCadastro.usuario.pessoa.id, re.registroCadastro.usuario.pessoa.nome, " +
				" re.relatorioRespostas.questionarioRespostas.id, re.tipo.id, re.tipo.descricao, re.status.id, re.status.descricao, " +
				" re.relatorioRespostas.id "; 
		StringBuilder hql = new StringBuilder();
		hql.append("select "+projecao+" from RelatorioEstagio re ");
		hql.append(" inner join re.estagio ");
		hql.append(" inner join re.tipo ");
		hql.append(" inner join re.status ");
		hql.append(" inner join re.registroCadastro ");
		hql.append(" inner join re.registroCadastro.usuario ");
		hql.append(" inner join re.registroCadastro.usuario.pessoa ");
		hql.append(" inner join re.relatorioRespostas ");
		hql.append(" inner join re.relatorioRespostas.questionarioRespostas ");
		
		hql.append(" where re.estagio.id = "+estagio.getId());
			
		if (!ValidatorUtil.isEmpty(pessoa))
			hql.append(" and re.registroCadastro.usuario.pessoa.id = "+pessoa.getId());
		
		if (!ValidatorUtil.isEmpty(renovacao)) 
			hql.append("   and re.renovacaoEstagio.id = "+renovacao.getId());
				
		
		if (!ValidatorUtil.isEmpty(idTipo))
			hql.append(" and re.tipo.id = "+idTipo);
		
		if (apenasAprovados)
			hql.append(" and re.status.id = "+StatusRelatorioEstagio.APROVADO);			
		
		hql.append(" order by re.dataCadastro desc ");
		
		
		Query q = getSession().createQuery(hql.toString());
		
		@SuppressWarnings("unchecked")
		Collection<RelatorioEstagio> lista = HibernateUtils.parseTo(q.list(), projecao, RelatorioEstagio.class, "re");		
		return lista;
	}

	/**
	 * Retorna Relatório conforme o Id passado.
	 * @param id
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public RelatorioEstagio findRelatorioById(int id) throws HibernateException, DAOException{
		String hql = "select re from RelatorioEstagio re where re.id = "+id;
		return (RelatorioEstagio) getSession().createQuery(hql).setMaxResults(1).uniqueResult();
	}

	/** Retorna o relatório de estágio pendente, isto é, que ainda não foi aprovado
	 * @param idEstagio
	 * @param idTipo
	 * @param idQuestionario
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public RelatorioEstagio findPendenteByEstagiarioTipoQuestionario(int idEstagio, int idTipo, int idQuestionario) throws HibernateException, DAOException {
		String hql = "select re" +
				" from RelatorioEstagio re" +
				" where estagio.id = :idEstagio" +
				" and questionario.id = :idQuestionario" +
				" and tipo.id = :idTipo" +
				" and status.id = :status" +
				" order by dataCadastro desc";
		Query q = getSession().createQuery(hql);
		q.setInteger("idEstagio", idEstagio);
		q.setInteger("idQuestionario", idQuestionario);
		q.setInteger("idTipo", idTipo);
		q.setInteger("status", StatusRelatorioEstagio.PENDENTE);
		return (RelatorioEstagio) q.setMaxResults(1).uniqueResult();
	}
}