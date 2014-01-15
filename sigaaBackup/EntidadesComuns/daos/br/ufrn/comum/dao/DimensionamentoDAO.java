package br.ufrn.comum.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.dao.GenericSharedDBDao;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.rh.dominio.AtividadeAmbiente;
import br.ufrn.rh.dominio.CompetenciasSetor;
import br.ufrn.rh.dominio.DescricaoAtividadeModulo;
import br.ufrn.rh.dominio.ModuloAtividade;
import br.ufrn.rh.dominio.ModuloSistema;
import br.ufrn.rh.dominio.ProcessoTrabalho;
import br.ufrn.rh.dominio.Servidor;

/**
 * Implementação do DAO de Dimensionamento
 *
 * @author Itamir de Morais Barroca Filho
 *
 */
@SuppressWarnings("unchecked")
public class DimensionamentoDAO extends GenericSharedDBDao {

	public ProcessoTrabalho findProcessoTrabalhoByUnidade(int idUnidade) throws DAOException {
		String hql = "from ProcessoTrabalho procTrab where procTrab.idUnidade = :idUnidade";

		Query query = getSession().createQuery(hql);
		query.setInteger("idUnidade", idUnidade);

		ProcessoTrabalho processoTrabalho = (ProcessoTrabalho) query.uniqueResult();

		if(processoTrabalho != null){
			processoTrabalho.getAtividadesAmbiente().iterator();
			processoTrabalho.getAtividadesModulo().iterator();
		}
		return processoTrabalho;
	}


	public List<CompetenciasSetor> findCompetenciasSetorByUnidade(int idUnidade) throws DAOException {
		String hql = "from CompetenciasSetor comp where comp.unidade.id = :idUnidade";

		Query query = getSession().createQuery(hql);
		query.setInteger("idUnidade", idUnidade);

		return query.list();
	}

	public Collection<ModuloSistema> findModulosBySistema(int sistema) throws DAOException {
		String hql = "from ModuloSistema mod where mod.sistema = :sistema order by mod.modulo asc";

		Query query = getSession().createQuery(hql);
		query.setInteger("sistema", sistema);

		return 	query.list();
	}

	public Collection<ModuloSistema> findDemaisTecnologias() throws DAOException {
		String hql = "from ModuloSistema mod where mod.sistema = :demaisSistemas order by mod.modulo asc";

		Query query = getSession().createQuery(hql);
		query.setInteger("demaisSistemas", Sistema.SISTEMAS_GOVERNO_FEDERAL);
		
		return 	query.list();
	}

	public Collection<AtividadeAmbiente> findAtividadesByAmbiente(int idAmbOrganizacional) throws DAOException {
		String hql = "from AtividadeAmbiente at where at.ambiente.id = :idAmbOrganizacional order by at.descricao";

		Query query = getSession().createQuery(hql);
		query.setInteger("idAmbOrganizacional", idAmbOrganizacional);

		return 	query.list();
	}

	public ModuloAtividade findModAtividadeByDescricao(String descricao, int idModulo) throws DAOException {
		String hql = "from ModuloAtividade mod where mod.descricao like '" + descricao + "' and mod.modulo.id = :idModulo";

		Query query = getSession().createQuery(hql);
		query.setInteger("idModulo", idModulo);

		return 	(ModuloAtividade) query.uniqueResult();
	}


	public List<DescricaoAtividadeModulo> findDescAtModByModulo(int idModulo) throws DAOException {
		String hql = "from DescricaoAtividadeModulo where modulo.id = :idModulo";

		Query query = getSession().createQuery(hql);
		query.setInteger("idModulo", idModulo);

		return 	query.list();
	}
	
	/**
	 * Busca os responsáveis pela avaliação de uma determinada unidade. Este método só
	 * será utilizado quando o SIPAC estiver ativo na instituição.
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection<Servidor> findResponsaveisAvaliacaoByUnidade(int idUnidade) {

		String sql =
				"SELECT distinct s.id_servidor, s.siape, p.nome " +
				"FROM " +
				"	comum.responsavel_unidade_avaliacao r " +
				"	INNER JOIN rh.servidor s ON s.id_servidor = r.id_responsavel_avaliacao " +
				"	INNER JOIN comum.pessoa p ON p.id_pessoa = s.id_pessoa " +
				"	INNER JOIN avaliacao.processo_avaliacao pa ON r.id_processo_avaliacao = pa.id_processo_avaliacao " +
				"WHERE " +
				"	now() BETWEEN pa.data_inicio_vigencia AND pa.data_final_vigencia " +
				"	AND r.data_inativacao is not null " +
				"	AND r.id_unidade = ? ";
		
		RowMapper servidorMap = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int row) throws SQLException {
				Servidor serv = new Servidor();
				serv.setId(rs.getInt("id_servidor"));
				serv.setSiape(rs.getInt("siape"));
				serv.setPessoa(new PessoaGeral());
				serv.getPessoa().setNome(rs.getString("nome"));
				return serv;
			}
		};
		
		try{
			return getJdbcTemplate(Sistema.SIPAC).query(sql,new Object[]{idUnidade}, servidorMap);
		}catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
}
