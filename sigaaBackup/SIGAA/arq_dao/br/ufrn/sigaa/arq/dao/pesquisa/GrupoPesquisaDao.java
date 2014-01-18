/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '29/03/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.pesquisa;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pesquisa.dominio.GrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.MembroGrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.StatusGrupoPesquisa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Dao para acesso aos dados de Grupo de Pesquisa
 * @author David Pereira
 *
 */
public class GrupoPesquisaDao extends GenericSigaaDAO {

	/** Retorna o total de grupos de pesquisa ativos; 
	 * @return
	 * @throws DAOException
	 */
	public int findCountGrupoPesquisa() throws DAOException {
		Query q = getSession().createQuery("select count(*) from GrupoPesquisa where ativo = trueValue()");
		return ((Number) q.uniqueResult()).intValue();
	}

	/**
	 * Retorna todos os grupos de pesquisa em que a Pessoa foi Coordenador
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	@SuppressWarnings("unchecked")
	public Collection<GrupoPesquisa> findByCoordenador(Servidor servidor, PagingInformation paginacao, int anoInicial, int anoFinal) throws DAOException {

		StringBuilder sql = new StringBuilder(
				" select gp.nome, gp.codigo, gp.data_criacao, mgp.classificacao, mgp.data_inicio, mgp.data_fim" +
				" from pesquisa.membro_grupo_pesquisa mgp" +
				" join pesquisa.grupo_pesquisa gp using ( id_grupo_pesquisa )" +
				" where extract(year from gp.data_criacao) <= " + anoFinal +
				" and gp.status in " + UFRNUtils.gerarStringIn(StatusGrupoPesquisa.getAllCertificados()) +
				" and mgp.classificacao = " + MembroGrupoPesquisa.COORDENADOR +
				" and mgp.ativo = trueValue() " +
				" and gp.id_coordenador= " + servidor.getId());	

		List<GrupoPesquisa> lista = getJdbcTemplate().query(sql.toString(), new Object[] {}, new RowMapper() {
				public Object mapRow(ResultSet rs, int row) throws SQLException {
					GrupoPesquisa gp = new GrupoPesquisa();
					gp.setNome( rs.getString("nome") );
					gp.setCodigo( rs.getString("codigo") );  
					gp.setDataCriacao( rs.getDate("data_criacao") );
					
					gp.setEquipesGrupoPesquisa(new HashSet<MembroGrupoPesquisa>(0));
					MembroGrupoPesquisa membro = new MembroGrupoPesquisa();
					membro.setClassificacao( rs.getInt("classificacao") );
					membro.setDataInicio( rs.getDate("data_inicio") );
					membro.setDataFim( rs.getDate("data_fim") );
					gp.getEquipesGrupoPesquisa().add(membro);
					
					return gp;
				}
			});
			return lista;
	}

	/** Retorna uma coleção de grupos de pesquisa de acordo com os parâmetros informados.
	 * @see #findOtimizado(Integer, String, int, String, Integer, Integer, Boolean, String, Character)
	 * @param nome
	 * @param idCoordenador
	 * @param nomeCoordenador
	 * @param idArea
	 * @param status
	 * @param ativo
	 * @return
	 * @throws DAOException
	 */
	public Collection<GrupoPesquisa> findOtimizado(String nome, Integer idCoordenador, String nomeCoordenador, 
			Integer idArea, Boolean ativo, Integer... status) throws DAOException{
		return findOtimizado(null, nome, idCoordenador, nomeCoordenador, idArea, ativo, null, null, status);
	}
	
	/** Retorna uma coleção de grupos de pesquisa de acordo com os parâmetros informados.
	 * @param idUnidade ID da unidade
	 * @param nome nome do grupo de pesquisa
	 * @param idCoordenador ID do coordenador do grupo
	 * @param nomeCoordenador nome do coordenador do grupo
	 * @param idArea ID da área de conhecimento CNPq
	 * @param status Status do grupo
	 * @param ativo lista grupo ativos ou não
	 * @param codigo código do grupo de pesquisa
	 * @param centro centro o qual o grupo de pesquisa pertence
	 * 
	 * @return Collection<GrupoPesquisa>
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<GrupoPesquisa> findOtimizado(Integer idUnidade, String nome, Integer idCoordenador, 
			String nomeCoordenador, Integer idArea, Boolean ativo, String codigo, Character centro, Integer... status) throws DAOException {
		StringBuilder sb = new StringBuilder();
		sb.append(" select g.id, g.codigo, g.nome, g.status, g.ativo, coord.id, coord.siape, p.nome, area.id, area.nome");
		sb.append(" from GrupoPesquisa g left join g.areaConhecimentoCnpq area left join g.coordenador coord left join coord.pessoa p where 1 = 1 ");
		if (nome != null)
			sb.append(" and " + UFRNUtils.toAsciiUpperUTF8("g.nome")+" like "+UFRNUtils.toAsciiUpperUTF8("'"+nome+"%'"));
		if (idUnidade != null && idUnidade > 0)
			sb.append(" and g.coordenador.unidade.unidadeResponsavel.id = :idUnidade");
		if (idCoordenador != null && idCoordenador > 0)
			sb.append(" and g.coordenador.id = :idCoordenador");
		if (nomeCoordenador != null)
			sb.append(" and g.coordenador.pessoa.nome like :nomeCoordenador ");
		if (idArea != null && idArea > 0)
			sb.append(" and g.areaConhecimentoCnpq.id = :idArea ");
		if ( verificarStatus(status) )
			sb.append(" and g.status in " + UFRNUtils.gerarStringIn(status));
		if (ativo != null)
			sb.append(" and g.ativo = :ativo");
		if (codigo != null && !"".equals(codigo))
			sb.append(" and g.codigo = :codigo");
		if (centro != null)
			sb.append(" and substring(g.codigo, 3, 1) = :centro");
		
		sb.append(" order by substring(g.codigo, 4, 3)");

		Query q = getSession().createQuery(sb.toString());
		if (idUnidade != null && idUnidade > 0)
			q.setInteger("idUnidade", idUnidade);
		if (idCoordenador != null && idCoordenador > 0)
			q.setInteger("idCoordenador", idCoordenador);
		if (nomeCoordenador != null)
			q.setString("nomeCoordenador", "%" + nomeCoordenador.trim().toUpperCase() + "%");
		if (idArea != null && idArea > 0)
			q.setInteger("idArea", idArea);
		if (ativo != null)
			q.setBoolean("ativo", ativo);
		if (codigo != null && !"".equals(codigo))
			q.setString("codigo", codigo);
		if (centro != null)
			q.setCharacter("centro", centro);
		
		List<Object[]> lista = q.list();
		
		ArrayList<GrupoPesquisa>  result = new ArrayList<GrupoPesquisa>();
		
		for (int i = 0; i < lista.size(); i++){
			int col = 0;
			Object[] colunas = lista.get(i);
			
			GrupoPesquisa grupo = new GrupoPesquisa();
			grupo.setId((Integer) colunas[col++]);
			grupo.setCodigo((String) colunas[col++]);
			grupo.setNome((String) colunas[col++]);
			grupo.setStatus((Integer) colunas[col++]);
			grupo.setAtivo((Boolean) colunas[col++]);
			
			Integer idServidor = (Integer) colunas[col++];
			Servidor coordenador = new Servidor();
			if (idServidor != null){
				coordenador.setId(idServidor);
				coordenador.setSiape((Integer) colunas[col++]);
			}	
			coordenador.getPessoa().setNome((String) colunas[col++]);
			grupo.setCoordenador(coordenador);
			
			Integer idAreaConhecimento = (Integer) colunas[col++];
			AreaConhecimentoCnpq area = new AreaConhecimentoCnpq();
			if(idAreaConhecimento != null)
				area.setId(idAreaConhecimento);
			area.setNome((String) colunas[col++]);
			grupo.setAreaConhecimentoCnpq(area);
			
			
			result.add(grupo);
		}
		
		return result;
	}
	
	/** Retorna uma coleção de grupos de pesquisa de acordo com os parâmetros informados.
	 * @see #findOtimizado(Integer, String, int, String, Integer, Integer, Boolean, String, Character)
	 * @param nome
	 * @param nomeCoordenador
	 * @param idArea
	 * @param status
	 * @param ativo
	 * @param codigo
	 * @param centro
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<GrupoPesquisa> findOtimizado(String nome, String nomeCoordenador, 
			Integer idArea, Boolean ativo, String codigo, Character centro, Integer status) throws DAOException {
		return findOtimizado(null, nome, 0, nomeCoordenador, idArea, ativo, codigo, centro, status);
	}

	/** Retorna uma coleção de grupos de pesquisa de acordo com os parâmetros informados.
	 * @see #findOtimizado(Integer, String, int, String, Integer, Integer, Boolean, String, Character)
	 * @param idUnidade
	 * @param nome
	 * @param idCoordenador
	 * @param nomeCoordenador
	 * @param idArea
	 * @param status
	 * @param ativo
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<GrupoPesquisa> findOtimizado(Integer idUnidade, String nome, int idCoordenador, 
			String nomeCoordenador, Integer idArea, Boolean ativo, Integer status) throws DAOException {
		return findOtimizado(idUnidade, nome, idCoordenador, nomeCoordenador, idArea, ativo, null, null, status);
	}
	
	/** Retorna uma coleção de grupos de pesquisa de acordo com os parâmetros informados.
	 * @see #findOtimizado(Integer, String, int, String, Integer, Integer, Boolean)
	 * @param nome
	 * @param idCoordenador
	 * @return
	 * @throws DAOException
	 */
	public Collection<GrupoPesquisa> findOtimizado(String nome, int idCoordenador) throws DAOException {
		return findOtimizado(nome, idCoordenador, null, null, Boolean.TRUE, new Integer[]{});
	}

	/** Retorna uma coleção de grupos de pesquisa de acordo com os parâmetros informados.
	 * @see #findOtimizado(Integer, String, int, String, Integer, Integer, Boolean)
	 * @param nome
	 * @param nomeCoordenador
	 * @return
	 * @throws DAOException
	 */
	public Collection<GrupoPesquisa> findOtimizado(String nome, String nomeCoordenador) throws DAOException {
		return findOtimizado(nome, 0, nomeCoordenador, null, Boolean.TRUE, new Integer[]{});
	}

	/** Retorna uma coleção de grupos de pesquisa de acordo com os parâmetros informados.
	 * @see #findOtimizado(Integer, String, int, String, Integer, Integer, Boolean)
	 * @param nome
	 * @param nomeCoordenador
	 * @param idArea
	 * @param status
	 * @return
	 * @throws DAOException
	 */
	public Collection<GrupoPesquisa> findOtimizado(String nome, String nomeCoordenador, Integer idArea, Integer... status) throws DAOException {
		return findOtimizado(nome, 0, nomeCoordenador, idArea, null, status);
	}
	
	/** Retorna uma coleção ordenada de grupos de pesquisa ativos de acordo com o centrou ou departamento..
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<GrupoPesquisa> findAllOrdenado(PagingInformation paging) throws DAOException {
		
		Long total = (Long) getSession().createQuery("select count(*) from GrupoPesquisa where ativo = trueValue()").uniqueResult();

		if (paging != null) {
			paging.setTotalRegistros((int) total.longValue());
		}

		Query q = getSession().createQuery("from GrupoPesquisa where ativo = trueValue() order by substring(codigo, 4, 3)");
		q.setCacheable(true);
		if (paging != null) {
			q.setFirstResult(paging.getPaginaAtual() * paging.getTamanhoPagina());
			q.setMaxResults(paging.getTamanhoPagina());
		}
		
		return q.list();	
		
	}
	
	

	/**
	 * Realiza um autocomplete nos grupos de pesquisa onde é realizado a busca pelo nome do grupo e/ou pelo código do grupo 
	 * e que o usuário tenha participação.
	 * 
	 * @param nome
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<GrupoPesquisa> findAutoComplete(String nome, int idPessoa ) throws DAOException {
		
		Collection<GrupoPesquisa> result = new ArrayList<GrupoPesquisa>();
		String sb = "SELECT gp.id_grupo_pesquisa, gp.codigo, gp.nome "+
					"FROM pesquisa.membro_grupo_pesquisa mgp "+
					"JOIN pesquisa.grupo_pesquisa gp using ( id_grupo_pesquisa ) "+
					"WHERE ( gp.nome ilike :nome OR gp.codigo ilike :codigo ) "+
					"AND mgp.id_pessoa = :idPessoa  ";
		
		Query q = getSession().createSQLQuery(sb);
		q.setString("nome", "%" + nome + "%");
		q.setString("codigo", "%" + nome + "%");
		q.setInteger("idPessoa", idPessoa);
		
		List<Object[]> lista = q.list();
		for (int i = 0; i < lista.size(); i++){
			int col = 0;
			Object[] colunas = lista.get(i);
			if ( !GrupoPesquisa.containsNull(colunas) ) {
				GrupoPesquisa grupo = new GrupoPesquisa();
				grupo.setId((Integer) colunas[col]);
				grupo.setCodigo((String) colunas[++col]);
				grupo.setNome((String) colunas[++col]);
				result.add(grupo);
			}
		}
		
		return result;
	}	
	
	/**
	 * Verifica se o status para a busca foi passada.
	 * @param status
	 * @return
	 */
	private boolean verificarStatus(Integer... status) {
		if ( !isEmpty(status) ) {
			for (Integer linha : status) {
				if ( linha == null &&  status.length == 1)
					return false;
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Retorna todos os grupos de pesquisa da unidade informada que possuem o status informado.
	 */
	public List<GrupoPesquisa> findByUnidadeStatus(Unidade unidade, int status) throws DAOException {
		Criteria c = getSession().createCriteria(GrupoPesquisa.class);
		c.add(Restrictions.eq("status", status));
		c.add(Restrictions.or(Restrictions.eq("ativo", true), Restrictions.isNull("ativo")));
		if(unidade != null){
			Criteria cCoord = c.createCriteria("coordenador");
			cCoord.add(Restrictions.eq("unidade", unidade));
			cCoord.addOrder(Order.asc("pessoa"));
		}
		@SuppressWarnings("unchecked")
		List<GrupoPesquisa> list = c.list();
		return list;
	}
	
	/**
	 * Retorna todos os grupos de pesquisa do centro informado que possuem o status informado.
	 */
	public List<GrupoPesquisa> findByUnidadeCentroStatus(Unidade unidade, int...status) throws DAOException {
		
		Query q = getSession().createQuery("from GrupoPesquisa g where g.status in " + UFRNUtils.gerarStringIn(status)
				+ " and (g.ativo is null or g.ativo = true)"
				+ " and (g.coordenador.unidade.id = :idDepto or g.coordenador.unidade.gestora.id = :idCentro)" );
		q.setInteger("idDepto", unidade.getId());
		q.setInteger("idCentro", unidade.getGestora().getId());
		
		@SuppressWarnings("unchecked")
		List<GrupoPesquisa> list = q.list();
		return list;
	}
	
}