/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em 18/01/2010
 *
 */
package br.ufrn.sigaa.arq.dao.vestibular;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.vestibular.dominio.PessoaVestibular;
import br.ufrn.sigaa.vestibular.dominio.StatusFoto;

/**
 * Classe responsável por consultas específicas aos dados pessoais (pessoa)
 * utilizados no Vestibular.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
public class PessoaVestibularDao extends GenericSigaaDAO {
	
	/** Retorna os dados pessoais de um cadastro no vestibular.
	 * @param cpf
	 * @return
	 * @throws DAOException
	 */
	public PessoaVestibular findByCPF(long cpf) throws DAOException {
		return findByCPF(cpf, false);
	}
	
	/** Retorna os dados pessoais de um cadastro no vestibular, realizando uma consulta rápida.
	 * @param cpf
	 * @param leve
	 * @return
	 * @throws DAOException
	 */
	public PessoaVestibular findByCPF(long cpf, boolean leve) throws DAOException {
		try {
			StringBuilder query = new StringBuilder();
			if (leve ) {
				query.append(" select new PessoaVestibular(p.id, p.nome, p.cpf_cnpj)");
			}
			query.append(" from PessoaVestibular p where cpf_cnpj =  " + cpf);
			query.append(" ORDER BY ultimaAtualizacao desc");
			return (PessoaVestibular) getSession().createQuery(query.toString()).setMaxResults(1).uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/** Retorna os dados pessoais de um candidato do vestibular, verificando a senha de acesso 
	 * @param cpf
	 * @param senhaMD5
	 * @param registroEntrada
	 * @return
	 * @throws DAOException
	 */
	public PessoaVestibular findByLoginSenha(long cpf, String senhaMD5, RegistroEntrada registroEntrada) throws DAOException {
		Criteria c = getSession().createCriteria(PessoaVestibular.class);
		c.add(Restrictions.eq("cpf_cnpj", cpf)).add(Restrictions.eq("senha", senhaMD5));
		c.setFetchMode("enderecoContato", FetchMode.JOIN);
		PessoaVestibular usuario = (PessoaVestibular) c.uniqueResult();
		if (usuario != null) {
			// registra a data/hora do último acesso
			updateField(PessoaVestibular.class, usuario.getId(), "ultimoAcesso", new Date());
			// associa o Registro de Entrada da sessão pública
			if (registroEntrada != null) {
				updateField(PessoaVestibular.class, usuario.getId(), "registroEntrada", registroEntrada);
				usuario.setRegistroEntrada(registroEntrada);
			}
		}
		return usuario;
	}

	/** Retorna a quantidade de cadastros de candidatos que não tiveram a foto 3x4 validada pela Comissão Permanente do Vestibular.
	 * @return
	 */
	public int countPendenteValidacao() {
		Integer result;
		result = getJdbcTemplate().queryForInt("select count(*)" +
				" from vestibular.pessoa_vestibular" +
				" where id_status_foto = ?", new Object[] {StatusFoto.NAO_ANALISADA});
		// caso não exista cadastros no vestibular, retorna zero.
		if (result == null)
			result = 0;
		return result;
	}

	/** Retorna uma coleção de cadastros de dados pessoais de candidatos do vestibular, filtrado por nome, cpf, ou foto validada.
	 * @param nome
	 * @param cpf
	 * @param fotoValidada
	 * @return
	 * @throws DAOException 
	 */
	public Collection<PessoaVestibular> findByNomeCpf(String nome, Long cpf, int idStatusFoto) throws DAOException {
		Criteria c = getSession().createCriteria(PessoaVestibular.class);
		if (!ValidatorUtil.isEmpty(nome))
			c.add(Restrictions.ilike("nome", nome  +"%"));
		if (!ValidatorUtil.isEmpty(cpf))
			c.add(Restrictions.eq("cpf_cnpj", cpf));
		if (idStatusFoto > 0)
			c.createCriteria("statusFoto").add(Restrictions.eq("id", idStatusFoto));
		c.addOrder(Order.asc("nomeAscii"));
		@SuppressWarnings("unchecked")
		Collection<PessoaVestibular> lista = c.list();
		return lista;
	}
	
	/**
	 * Lista o quantitativo das situações das fotos das pessoas.
	 * 
	 * @param idProcessoSeletivo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<PessoaVestibular> findByStatusFotoProcessoSeletivo(Integer idProcessoSeletivo) throws DAOException {
		StringBuffer hql = new StringBuffer();
		hql.append("select sf.descricao, count(distinct pv.id) ");
		hql.append("from InscricaoVestibular iv, PessoaVestibular pv ");
		hql.append("INNER JOIN pv.statusFoto sf ");
		hql.append("where pv.id = iv.pessoa.id ");
		hql.append("and iv.processoSeletivo.id = " + idProcessoSeletivo);
		hql.append(" group by pv.statusFoto.id, sf.descricao ");
		
		Collection<PessoaVestibular> pessoas = new ArrayList<PessoaVestibular>();
		
		List<Object[]> list = getSession().createQuery(hql.toString()).list(); 

		for (Object[] item : list) {
			PessoaVestibular p = new PessoaVestibular();
			p.getStatusFoto().setDescricao((String) item[0]);
			p.setTotal((Long) item[1]);
			pessoas.add(p);
		}
		return pessoas;
  }

	/** Cria um mapa com registros de PessoaVestibular para ser usado como cache em operações de processamento.
	 * O mapa é criado com o par <ID de PessoaVestibular, PessoaVestibular>.
	 * @param cpfs
	 * @return
	 * @throws DAOException
	 */
	public Map<Long, PessoaVestibular> findByCpfCnpj(Collection<Long> cpfs) throws DAOException {
		Map<Long, PessoaVestibular> mapa = new TreeMap<Long, PessoaVestibular>();
		if (!isEmpty(cpfs)) {
			String projecao = "p.id, p.nome, p.cpf_cnpj";
			Query q = getSession().createQuery("select "+projecao+" from PessoaVestibular p " +
					"where cpf_cnpj in " + UFRNUtils.gerarStringIn(cpfs));
			@SuppressWarnings("unchecked")
			List<Object[]> result = q.list();
			Collection<PessoaVestibular> pessoas = HibernateUtils.parseTo(result, projecao, PessoaVestibular.class, "p");
			for (Long cpf : cpfs) {
				mapa.put(cpf, null);
				for (PessoaVestibular p : pessoas) {
					if (p.getCpf_cnpj().equals(cpf)) {
						mapa.put(cpf, p);
					}
				}
			}
		}
		return mapa;
	}
	
	/** Cria um mapa com registros de PessoaVestibular para ser usado como cache em operações de processamento.
	 * O mapa é criado com o par <ID de PessoaVestibular, PessoaVestibular>.
	 * @param ids
	 * @return
	 * @throws DAOException
	 */
	public Map<Integer, PessoaVestibular> findByPrimaryKey(Collection<Integer> ids) throws DAOException {
		Map<Integer, PessoaVestibular> mapa = new TreeMap<Integer, PessoaVestibular>();
		if (!isEmpty(ids)) {
			Criteria c = getSession().createCriteria(PessoaVestibular.class);
			c.add(Restrictions.in("id", ids));
			@SuppressWarnings("unchecked")
			Collection<PessoaVestibular> pessoas = c.list();
			for (Integer id : ids) {
				mapa.put(id, null);
				for (PessoaVestibular p : pessoas) {
					if (p.getId() == id) {
						mapa.put(id, p);
					}
				}
			}
		}
		return mapa;
	}
}