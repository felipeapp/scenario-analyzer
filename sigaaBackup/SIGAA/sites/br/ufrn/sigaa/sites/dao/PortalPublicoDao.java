package br.ufrn.sigaa.sites.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.ensino.stricto.dominio.BancaPos;
import br.ufrn.sigaa.ensino.stricto.dominio.DadosDefesa;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * DAO responsável pelas consultas realizadas nos portais públicos 
 * @author Mário Rizzi
 *
 */
public class PortalPublicoDao  extends GenericSigaaDAO {

	/**
	 * Retorna uma coleção de unidades (departamento, programas)
	 * @param nome
	 * @param centro
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */
	public Collection<Unidade> findUnidadeByNomeCentro(String nome, Integer centro, Integer tipo)
		throws DAOException {
		
		try {
			
			String projecao = "u.id, u.nome, u.sigla, u2.id as idUnidadeResponsavel,"+
							" u2.nome as unidadeResponsavel, u.sigla as siglaUnidadeResponsavel";
			
			String hql = "SELECT " + HibernateUtils.removeAliasFromProjecao(projecao) + 
					" FROM Unidade u INNER JOIN u.unidadeResponsavel u2 "; 

			hql += " WHERE u.ativo = trueValue() ";
			if(tipo != null)
				hql += " AND u.tipoAcademica = :tipo ";
			if( nome != null && nome.length()>0){
				hql += " AND ( upper(u.nomeAscii) like :nome OR upper(u.nome) like :nome )";
			}
			if( centro != null && centro > 0 )
				hql += " AND u2.id = :centro ";
			
			hql += " ORDER BY u2.nome, u.nome";
			
			Query query = getSession().createQuery(hql);
			
			if( tipo != null)
				query.setInteger("tipo", tipo);
			if(  nome != null && nome.length()>0)
				query.setString("nome", "%" + StringUtils.toAscii(nome.toUpperCase().trim()) + "%");
			if( centro != null && centro > 0 )
				query.setInteger("centro", centro);
			
			List<Object> lista = query.list();
			Collection<Unidade> resultado = new ArrayList<Unidade>();
			
			for (Object object : lista) {
				Object[] temp = (Object[]) object;
				Unidade u = new Unidade((Integer) temp[0], null, (String) temp[1], (String) temp[2]);
				Unidade u2 = new Unidade((Integer) temp[3], null, (String) temp[4], (String) temp[5]);
				u.setGestora(u2);
				resultado.add(u);
			}
			return resultado;
			
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {}
	}
	
	/**
	 * Retorna todos os projetos da unidade e projeção Equipe de Ensino
	 * 
	 * @return Coleção de ProjetoEnsino do centro informado
	 * @throws DAOException
	 * 
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<ProjetoEnsino> findByUnidadeProjecEquipe(Unidade unidade)
			throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
			hql.append(" SELECT p FROM ProjetoEnsino p JOIN p.equipeDocentes e");
			hql.append(" WHERE e.servidor.unidade.id = :idUnidade AND e.coordenador=trueValue()");
			hql.append(" AND e.dataEntradaProjeto IS NOT NULL AND p.ativo = trueValue() ");
			hql.append(" ORDER BY e.servidor.pessoa.nome ASC");

			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idUnidade", unidade.getId());

			return (ArrayList<ProjetoEnsino>) query.setResultTransformer(
					Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna uma coleção de bancas de pós-graduação de acordo com os parâmetros setados
	 * @param idUnidade
	 * @param ano
	 * @param tipobanca
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public Collection<BancaPos> findByUnidade(int idUnidade, Integer ano,Integer tipobanca, String discente) throws DAOException{

		StringBuffer sql = new StringBuffer();

		sql.append(" SELECT p.nome, dd.titulo, dd.id_arquivo, dd.link_arquivo, b.data, dd.resumo, ");
		sql.append(" 	( SELECT distinct(p2.nome) FROM graduacao.orientacao_academica o ");
		sql.append(" 	 JOIN rh.servidor s ON o.id_servidor = s.id_servidor JOIN comum.pessoa p2 ON s.id_pessoa = p2.id_pessoa ");
		sql.append("  	 WHERE o.tipoorientacao = '" + OrientacaoAcademica.ORIENTADOR + "'");
		sql.append("  	 AND o.id_discente = dc.id_discente  AND o.cancelado = falseValue() AND o.data_finalizacao IS NULL " + BDUtils.limit(1) );
		sql.append("  	) as orientador ");
		sql.append(" FROM stricto_sensu.banca_pos b JOIN stricto_sensu.dados_defesa dd ON b.id_dados_defesa = dd.id_dados_defesa");
		sql.append(" JOIN discente dc ON dd.id_discente = dc.id_discente JOIN discente d ON d.id_discente = dc.id_discente");
		sql.append(" JOIN comum.pessoa p ON d.id_pessoa = p.id_pessoa ");
		sql.append(" WHERE dc.id_gestora_academica=" + idUnidade + " AND b.tipobanca = " + BancaPos.BANCA_DEFESA);
	
		if(!isEmpty(ano))
			sql.append(" AND date_part('year', banca.data) = '"+ano+"'");
		if(!isEmpty(discente))
			sql.append(" AND upper(p.nome_ascii) like %" + StringUtils.toAscii(discente.toUpperCase().trim()) + "%" );
		if(!isEmpty(tipobanca))
			sql.append(" AND b.tipo_banca = " + tipobanca );
		
		sql.append(" ORDER BY b.data DESC, p.nome ASC");
		@SuppressWarnings("unchecked")
		Collection<BancaPos> lista = getJdbcTemplate().query(sql.toString(),new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

						BancaPos banca = new BancaPos();
						banca.setDadosDefesa( new DadosDefesa() );
						banca.getDadosDefesa().setDiscente(new DiscenteStricto());
						banca.getDadosDefesa().getDiscente().setDiscente(new Discente());
						banca.getDadosDefesa().getDiscente().getDiscente().setPessoa(new Pessoa());
						
						banca.getDadosDefesa().getDiscente().getPessoa().setNome(rs.getString("nome"));
						banca.getDadosDefesa().setTitulo(rs.getString("titulo"));
						banca.getDadosDefesa().setIdArquivo(rs.getInt("id_arquivo"));
						banca.getDadosDefesa().setLinkArquivo(rs.getString("link_arquivo"));
						banca.getDadosDefesa().setResumo(rs.getString("resumo"));
						banca.setData(rs.getDate("data"));
						banca.getDadosDefesa().getDiscente().setOrientacao( new  OrientacaoAcademica());
						banca.getDadosDefesa().getDiscente().getOrientacao().setServidor(new Servidor());
						banca.getDadosDefesa().getDiscente().getOrientacao().getServidor().setPessoa(new Pessoa());
						banca.getDadosDefesa().getDiscente().getOrientacao().
						getServidor().getPessoa().setNome(rs.getString("orientador"));
						
				return banca;
			}
		});		
		return lista;	
	}
	
}
