/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/10/2012
 *
 */
package br.ufrn.sigaa.ensino.tecnico.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.tecnico.dominio.PessoaTecnico;

/**
 * Classe responsável por consultas específicas aos dados pessoais (pessoa).
 * 
 * @author Édipo Elder F. Melo
 * @author Fred_Castro
 * 
 */
public class PessoaTecnicoDao extends GenericSigaaDAO {
	
	/** Retorna os dados pessoais de um cadastro.
	 * @param cpf
	 * @return
	 * @throws DAOException
	 */
	public PessoaTecnico findByCPF(long cpf) throws DAOException {
		return findByCPF(cpf, false);
	}
	
	/** Retorna os dados pessoais de um cadastro, realizando uma consulta rápida.
	 * @param cpf
	 * @param leve
	 * @return
	 * @throws DAOException
	 */
	public PessoaTecnico findByCPF(long cpf, boolean leve) throws DAOException {
		try {
			StringBuilder query = new StringBuilder();
			if (leve ) {
				query.append(" select new PessoaTecnico(p.id, p.nome, p.cpf_cnpj)");
			}
			query.append(" from PessoaTecnico p where cpf_cnpj =  " + cpf);
			query.append(" ORDER BY ultimaAtualizacao desc");
			return (PessoaTecnico) getSession().createQuery(query.toString()).setMaxResults(1).uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/** Retorna uma coleção de cadastros de dados pessoais de candidatos do processo seletivo, filtrado por nome ou cpf.
	 * @param nome
	 * @param cpf
	 * @return
	 * @throws DAOException 
	 */
	public Collection<PessoaTecnico> findByNomeCpf(String nome, Long cpf) throws DAOException {
		Criteria c = getSession().createCriteria(PessoaTecnico.class);
		if (!ValidatorUtil.isEmpty(nome))
			c.add(Restrictions.ilike("nome", nome  +"%"));
		if (!ValidatorUtil.isEmpty(cpf))
			c.add(Restrictions.eq("cpf_cnpj", cpf));
		c.addOrder(Order.asc("nomeAscii"));
		@SuppressWarnings("unchecked")
		Collection<PessoaTecnico> lista = c.list();
		return lista;
	}
	
	/** Cria um mapa com registros de PessoaTecnico para ser usado como cache em operações de processamento.
	 * O mapa é criado com o par <ID de PessoaTecnico, PessoaTecnico>.
	 * @param cpfs
	 * @return
	 * @throws DAOException
	 */
	public Map<Long, PessoaTecnico> findByCpfCnpj(Collection<Long> cpfs) throws DAOException {
		Map<Long, PessoaTecnico> mapa = new TreeMap<Long, PessoaTecnico>();
		if (!isEmpty(cpfs)) {
			String projecao = "p.id, p.nome, p.cpf_cnpj";
			Query q = getSession().createQuery("select "+projecao+" from PessoaTecnico p " +
					"where cpf_cnpj in " + UFRNUtils.gerarStringIn(cpfs));
			@SuppressWarnings("unchecked")
			List<Object[]> result = q.list();
			Collection<PessoaTecnico> pessoas = HibernateUtils.parseTo(result, projecao, PessoaTecnico.class, "p");
			for (Long cpf : cpfs) {
				mapa.put(cpf, null);
				for (PessoaTecnico p : pessoas) {
					if (p.getCpf_cnpj().equals(cpf)) {
						mapa.put(cpf, p);
					}
				}
			}
		}
		return mapa;
	}
	
	/** Cria um mapa com registros de PessoaTecnico para ser usado como cache em operações de processamento.
	 * O mapa é criado com o par <ID de PessoaTecnico, PessoaTecnico>.
	 * @param ids
	 * @return
	 * @throws DAOException
	 */
	public Map<Integer, PessoaTecnico> findByPrimaryKey(Collection<Integer> ids) throws DAOException {
		Map<Integer, PessoaTecnico> mapa = new TreeMap<Integer, PessoaTecnico>();
		if (!isEmpty(ids)) {
			Criteria c = getSession().createCriteria(PessoaTecnico.class);
			c.add(Restrictions.in("id", ids));
			@SuppressWarnings("unchecked")
			Collection<PessoaTecnico> pessoas = c.list();
			for (Integer id : ids) {
				mapa.put(id, null);
				for (PessoaTecnico p : pessoas) {
					if (p.getId() == id) {
						mapa.put(id, p);
					}
				}
			}
		}
		return mapa;
	}
}