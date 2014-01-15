/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '05/10/2006'
 *
 */
package br.ufrn.sigaa.arq.dao.pesquisa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pesquisa.dominio.AvaliacaoProjeto;
import br.ufrn.sigaa.pesquisa.dominio.CodigoProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.Consultor;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * DAO relacionado a avaliações de projetos de pesquisa
 *
 * @author ricardo
 *
 */
public class AvaliacaoProjetoDao extends GenericSigaaDAO{

	/**
	 * Busca as avaliações de um consultor
	 * @param idConsultor
	 * @param pendentes
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AvaliacaoProjeto> findByConsultor(int idConsultor, boolean pendentes) throws DAOException {

		try {
			Criteria criteria = getSession().createCriteria(AvaliacaoProjeto.class);
			criteria.createCriteria("consultor")
					.add( Restrictions.eq("id", idConsultor));

			if (pendentes) {
				criteria.add(Expression.eq("situacao", AvaliacaoProjeto.AGUARDANDO_AVALIACAO));
				criteria.add(Expression.isNull("dataAvaliacao"));
			}
			criteria.createCriteria("projetoPesquisa")
				.addOrder( Order.desc("codigo.ano"))
				.addOrder( Order.asc("codigo"));

			return criteria.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}
	
	/**
	 * Busca as avaliações para popular no portal do consultor
	 * @param idConsultor
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AvaliacaoProjeto> findByPortalConsultor(int idConsultor, boolean pendente) throws DAOException {

		try {
			Criteria criteria = getSession().createCriteria(AvaliacaoProjeto.class);

			criteria.createCriteria("consultor")
					.add( Restrictions.eq("id", idConsultor));
			
			if ( pendente ) {
				criteria.add(Restrictions.eq("situacao", AvaliacaoProjeto.AGUARDANDO_AVALIACAO));
			} else {
				criteria.add(Restrictions.eq("situacao",  AvaliacaoProjeto.REALIZADA));
			}
			
			Criteria projetoCriteria = criteria.createCriteria("projetoPesquisa");
			projetoCriteria.createCriteria("projeto")
				.add(Restrictions.in("situacaoProjeto.id", new Object[]{TipoSituacaoProjeto.SUBMETIDO, TipoSituacaoProjeto.DISTRIBUIDO_AUTOMATICAMENTE, TipoSituacaoProjeto.DISTRIBUIDO_MANUALMENTE}));
			projetoCriteria.createCriteria("edital")
				.add(Restrictions.eq("avaliacaoVigente", Boolean.TRUE));

			criteria.addOrder(Order.asc("situacao"));
			
			projetoCriteria.addOrder(Order.desc("codigo.ano")).addOrder( Order.asc("codigo"));

			return criteria.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}

	/**
	 * Busca as avaliações de um determinado projeto
	 * @param projeto
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AvaliacaoProjeto> findByProjeto(ProjetoPesquisa projeto) throws DAOException {

		try {
			Criteria criteria = getSession().createCriteria(AvaliacaoProjeto.class);
			criteria.createCriteria("projetoPesquisa")
				.add( Restrictions.eq("id", projeto.getId()));

			return criteria.list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna a quantidade de avaliações para o qual um projeto passado por parametro foi submetido
	 * @param idProjeto
	 * @return
	 * @throws DAOException
	 */
	public int findQtdAvaliacoesByProjeto( int idProjeto ) throws DAOException {

		try {
			Query query = getSession().createQuery(
					"select count(*) from AvaliacaoProjeto avaliacao "
							+ "where avaliacao.projetoPesquisa.id = :idprojeto");
			query.setInteger("idprojeto", idProjeto);
			return Integer.valueOf(query.uniqueResult().toString());
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}

	/**
	 * Retorna a quantidade de avaliações realizadas pelos consultores de um projeto passado por parâmetro
	 * @param idProjeto
	 * @return
	 * @throws DAOException
	 */
	public int findQtdAvaliacoesRealizadasByProjeto( int idProjeto ) throws DAOException {

		try {
			Query query = getSession().createQuery(
					"select count(*) " + "from AvaliacaoProjeto avaliacao "
							+ "where avaliacao.projetoPesquisa.id = :idprojeto" +
							" and avaliacao.dataAvaliacao is not null" +
							" and avaliacao.justificativa is null");
			query.setInteger("idprojeto", idProjeto);
			return Integer.valueOf(query.uniqueResult().toString());
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}

	/**
	 * Retorna a quantidade de avaliações não realizadas pelos consultores de um projeto passado por parâmetro
	 * @param idProjeto
	 * @return
	 * @throws DAOException
	 */
	public int findQtdAvaliacoesNegadasByProjeto( int idProjeto ) throws DAOException {

		try {
			Query query = getSession().createQuery(
					"select count(*) " + "from AvaliacaoProjeto avaliacao "
							+ "where avaliacao.projetoPesquisa.id = :idprojeto" +
							" and avaliacao.dataAvaliacao is not null " +
							" and avaliacao.justificativa is not null");
			query.setInteger("idprojeto", idProjeto);
			return Integer.valueOf(query.uniqueResult().toString());
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}

	/**
	 * Retorna a lista de avaliações com a situação passada por parâmetro
	 * @param situacao
	 * @return
	 * @throws DAOException
	 */
	public Collection<AvaliacaoProjeto> findBySituacao( int situacao ) throws DAOException{
		return filter(null, situacao, null, null, null, null);
	}

	/**
	 * Retorna uma lista de avaliações de projeto de acordo com os filtros selecionados
	 *
	 * @param situacao
	 * @return
	 * @throws Exception 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public Collection<AvaliacaoProjeto> filter( Integer ano, Integer situacao, Consultor consultor, Unidade centro, Integer tipoDistribuicao, EditalPesquisa edital ) throws DAOException {

		try {
			StringBuffer hql = new StringBuffer();

			String projecao = "id, projetoPesquisa.id, projetoPesquisa.codigo, projetoPesquisa.titulo, dataDistribuicao," +
					" dataAvaliacao, justificativa, media, situacao, projetoPesquisa.areaConhecimentoCnpq.grandeArea.id," +
					" projetoPesquisa.areaConhecimentoCnpq.grandeArea.nome, consultor.id, consultor.codigo, consultor.nome, consultor.email";
			
			hql.append( " SELECT avaliacao.id, avaliacao.projetoPesquisa.id, avaliacao.projetoPesquisa.codigo, avaliacao.projetoPesquisa.projeto.titulo, " );
			hql.append( " avaliacao.dataDistribuicao, avaliacao.dataAvaliacao, avaliacao.justificativa, avaliacao.media, avaliacao.situacao," );
			hql.append( " avaliacao.projetoPesquisa.areaConhecimentoCnpq.grandeArea.id, avaliacao.projetoPesquisa.areaConhecimentoCnpq.grandeArea.nome, " );
			hql.append( " avaliacao.consultor.id, avaliacao.consultor.codigo, avaliacao.consultor.nome, avaliacao.consultor.email " );
			
			hql.append( " FROM AvaliacaoProjeto avaliacao " );
			hql.append( " WHERE 1 = 1 " );

			if (ano != null)
				hql.append( " AND avaliacao.projetoPesquisa.codigo.ano =  " + ano );

			if (situacao != null)
				hql.append( " AND situacao = :situacao " );
			if (consultor != null)
				hql.append( " AND consultor.id = :idConsultor " );
			if ( centro != null )
				hql.append( " AND projetoPesquisa.centro.id = :idCentro " );
			if (tipoDistribuicao != null)
				hql.append( " AND tipoDistribuicao = :tipoDistribuicao " );
			if (edital != null)
				hql.append( " AND projetoPesquisa.edital.id = :idEdital " );

			hql.append( " ORDER BY avaliacao.projetoPesquisa.centro.nome, " +
					" avaliacao.projetoPesquisa.codigo.ano, avaliacao.projetoPesquisa.codigo.numero," +
					" avaliacao.consultor.codigo" );

			Query query = getSession().createQuery( hql.toString() );
			if (situacao != null)
				query.setInteger("situacao", situacao);
			if (consultor != null)
				query.setInteger("idConsultor", consultor.getId());
			if ( centro != null )
				query.setInteger("idCentro", centro.getId() );
			if (tipoDistribuicao != null)
				query.setInteger("tipoDistribuicao", tipoDistribuicao);
			if (edital != null)
				query.setInteger("idEdital", edital.getId());

			return HibernateUtils.parseTo(query.list(), projecao, AvaliacaoProjeto.class);
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage(), ex);
		}
	}
	
	/**
	 * Consulta referente ao relatório das avaliações dos projetos de Pesquisa.
	 * 
	 * @param idEditalPesquisa
	 * @param quantidadeAvaliacoes
	 * @return
	 */
	public List<AvaliacaoProjeto> findAvaliacoesProjeto( int idEditalPesquisa, int quantidadeAvaliacoes ) {
		
		String sql = "";
		
		if ( quantidadeAvaliacoes == 0 ) {
			sql = "select * from (" +
					" select pp.id_projeto_pesquisa, pp.cod_prefixo, pp.cod_numero, pp.cod_ano, p.titulo, u.nome, pes.nome as pessoa" +
					" from pesquisa.avaliacao_projeto ap" +
					" join pesquisa.projeto_pesquisa pp using ( id_projeto_pesquisa )" +
					" join projetos.projeto p using ( id_projeto )" +
					" join pesquisa.edital_pesquisa ep using ( id_edital_pesquisa )" +
					" join projetos.edital e on ( ep.id_edital = e.id_edital )" +
					" join pesquisa.consultor c on ( c.id_consultor = ap.id_consultor )" +
					" join comum.unidade u on ( p.id_unidade = u.id_unidade )" +
					" join projetos.membro_projeto mp on ( p.id_coordenador = mp.id_membro_projeto )"+
					" join rh.servidor s on ( s.id_servidor = mp.id_servidor )"+
					" join comum.pessoa pes on ( s.id_pessoa = pes.id_pessoa )"+
					" where e.id_edital = " + idEditalPesquisa + 
				" except" +
					" select pp.id_projeto_pesquisa, pp.cod_prefixo, pp.cod_numero, pp.cod_ano, p.titulo, u.nome, pes.nome as pessoa" +
					" from pesquisa.avaliacao_projeto ap" +
					" join pesquisa.projeto_pesquisa pp using ( id_projeto_pesquisa )" +
					" join projetos.projeto p using ( id_projeto )" +
					" join pesquisa.edital_pesquisa ep using ( id_edital_pesquisa )" +
					" join projetos.edital e on ( ep.id_edital = e.id_edital )" +
					" join pesquisa.consultor c on ( c.id_consultor = ap.id_consultor )" +
					" join comum.unidade u on ( p.id_unidade = u.id_unidade )" +
					" join projetos.membro_projeto mp on ( p.id_coordenador = mp.id_membro_projeto )"+
					" join rh.servidor s on ( s.id_servidor = mp.id_servidor )"+
					" join comum.pessoa pes on ( s.id_pessoa = pes.id_pessoa )"+
					" where e.id_edital = " + idEditalPesquisa + " and ap.situacao = " + AvaliacaoProjeto.REALIZADA +
				" ) u" +
				" order by u.nome";
		} else {
			sql = "select pp.id_projeto_pesquisa, pp.cod_prefixo, pp.cod_numero, pp.cod_ano, p.titulo, u.nome, pes.nome" +
					" from pesquisa.avaliacao_projeto ap" +
					" join pesquisa.projeto_pesquisa pp using ( id_projeto_pesquisa )" +
					" join projetos.projeto p using ( id_projeto )" +
					" join pesquisa.edital_pesquisa ep using ( id_edital_pesquisa )" +
					" join projetos.edital e on ( ep.id_edital = e.id_edital )" +
					" join pesquisa.consultor c on ( c.id_consultor = ap.id_consultor )" +
					" join comum.unidade u on ( p.id_unidade = u.id_unidade )" +
					" join projetos.membro_projeto mp on ( p.id_coordenador = mp.id_membro_projeto )" +
					" join rh.servidor s on ( s.id_servidor = mp.id_servidor )" +
					" join comum.pessoa pes on ( s.id_pessoa = pes.id_pessoa )" +
					" where e.id_edital = " + idEditalPesquisa + " and ap.situacao = " + AvaliacaoProjeto.REALIZADA +
					" group by pp.id_projeto_pesquisa, pp.cod_prefixo, pp.cod_numero, pp.cod_ano, p.titulo, u.nome, pes.nome" +
					" having count(pp.id_projeto_pesquisa) = " + quantidadeAvaliacoes +
					" order by u.nome, pes.nome";
		}
		
		@SuppressWarnings("unchecked")
		List<AvaliacaoProjeto> lista = getJdbcTemplate().query(sql.toString(), new Object[] {}, new RowMapper() {
			public Object mapRow(ResultSet rs, int row) throws SQLException {
				AvaliacaoProjeto ap = new AvaliacaoProjeto();
				ap.setProjetoPesquisa(new ProjetoPesquisa( rs.getInt(1) ));
				ap.getProjetoPesquisa().setCodigo(new CodigoProjetoPesquisa());
				ap.getProjetoPesquisa().getCodigo().setPrefixo( rs.getString(2) );
				ap.getProjetoPesquisa().getCodigo().setNumero( rs.getInt(3) );
				ap.getProjetoPesquisa().getCodigo().setAno( rs.getInt(4) );
				ap.getProjetoPesquisa().setProjeto(new Projeto());
				ap.getProjetoPesquisa().getProjeto().setTitulo( rs.getString(5).toUpperCase() );
				ap.getProjetoPesquisa().getProjeto().setUnidade(new Unidade());
				ap.getProjetoPesquisa().getProjeto().getUnidade().setNome( rs.getString(6) );
				ap.getProjetoPesquisa().getProjeto().setCoordenador( new MembroProjeto() );
				ap.getProjetoPesquisa().getProjeto().getCoordenador().setPessoa(new Pessoa());
				ap.getProjetoPesquisa().getProjeto().getCoordenador().getPessoa().setNome( rs.getString(7) );
				return ap;
			}
		});
		
		return lista;
	}
	
}