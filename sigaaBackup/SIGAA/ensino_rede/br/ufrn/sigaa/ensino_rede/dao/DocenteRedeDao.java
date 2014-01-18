/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 15/08/2013
 *
 */
package br.ufrn.sigaa.ensino_rede.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.ensino_rede.dominio.DocenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.ProgramaRede;
import br.ufrn.sigaa.ensino_rede.dominio.SituacaoDocenteRede;
import br.ufrn.sigaa.ensino_rede.jsf.ParametrosSelecionaDocente;
import br.ufrn.sigaa.ensino_rede.jsf.ValoresSelecionaDocente;

/**
 * Dao responsável por consultas específicas aos docentes de ensino em rede.
 * @author Diego Jácome
 */
public class DocenteRedeDao extends GenericSigaaDAO {

	/**
	 * Retorna os docentes de um campus específico
	 *
	 * @param idCampus
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<DocenteRede> findDocentesByCampus (CampusIes campus, ProgramaRede programa, Collection<SituacaoDocenteRede> situacoes) throws DAOException {
		
		String hql = " select dr from DocenteRede dr " +
					 " join fetch dr.tipo t "+
					 " join fetch dr.situacao s "+
					 " join dr.dadosCurso dc " +
					 " join dc.campus c " +
					 " where c.id = " +campus.getId()+ " and dc.programaRede.id = "+programa.getId();
		if (isNotEmpty(situacoes))
			hql += " and s.id in " + UFRNUtils.gerarStringIn(situacoes);
		hql += " order by dr.pessoa.nome ";
		
		Query q = getSession().createQuery(hql.toString());
		
		return (ArrayList<DocenteRede>) q.list();
	}

	/**
	 * Retorna os docentes de uma turma
	 *
	 * @param idCampus
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<DocenteRede> findDocentesByTurma (Integer idTurma) throws DAOException {
		
		ArrayList<Integer> situacoes = new ArrayList<Integer>();
		situacoes.add(SituacaoDocenteRede.ATIVO);
		
		String hql = " select dr from DocenteRede dr , DocenteTurmaRede dt  " +
					 " join fetch dr.tipo t "+
					 " join fetch dr.situacao s "+
					 " where dt.docente.id = dr.id and " +
					 " dt.turma.id = "+idTurma+
					 " and s.id in " + UFRNUtils.gerarStringIn(situacoes);
		hql += " order by dr.pessoa.nome ";
		
		Query q = getSession().createQuery(hql.toString());
		
		return (ArrayList<DocenteRede>) q.list();
	}
	
	/**
	 * Retorna os docentes de um campus específico
	 *
	 * @param idCampus
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<DocenteRede> findDocentesByParametros (ParametrosSelecionaDocente param, ValoresSelecionaDocente valores) throws DAOException {
		
		String hql = " select dr from DocenteRede dr " +
					 " join fetch dr.tipo t "+
					 " join fetch dr.pessoa p "+
					 " join fetch dr.situacao s "+
					 " join fetch dr.dadosCurso dc " +
					 " join fetch dc.campus c " +
					 " where 1=1 ";

		if (param.isCheckCampus() && !isEmpty(valores.getValorIdCampus()))
			hql += " and c.id = " + valores.getValorIdCampus();
		if (param.isCheckCpf())
			hql += " and p.cpf_cnpj = " + valores.getValorCpf();
		if (param.isCheckNome())
			hql += " and p.nomeAscii like '%" + StringUtils.toAscii(valores.getValorNome().toUpperCase()) + "%'";
		
		hql += " order by c.instituicao.sigla , c.nome , p.nome ";
		
		Query q = getSession().createQuery(hql.toString());
		
		return (ArrayList<DocenteRede>) q.list();
	}
	
	/**
	 * Retorna os docentes buscando por campus e cpf
	 *
	 * @param cpf_cnpj
	 * @param campus
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public DocenteRede findDocenteByCampusAndCPF(Long cpf_cnpj, CampusIes campus) throws DAOException {
		String hql = "select docente from DocenteRede docente"
				+ "	join docente.dadosCurso dadosCurso "
				+ "	join dadosCurso.campus campus "
				+ "	join docente.pessoa pessoa "
				+ " where pessoa.cpf_cnpj = :cpfCnpj " 
				+ " and campus.id = :idCampus";
		
		Query query = getSession().createQuery(hql);
		query.setLong("cpfCnpj", cpf_cnpj);
		query.setInteger("idCampus", campus.getId());
		
		return (DocenteRede) query.uniqueResult();
	}
	
}
