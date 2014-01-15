package br.ufrn.sigaa.portal.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericSharedDBDao;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.LocalizacaoNoticiaPortal;
import br.ufrn.comum.dominio.NoticiaPortal;

/**
 * DAO para buscas de notícias para os portais acadêmicos
 * @author Mário Rizzi
 *
 */
public class NoticiaPortalAcademicoDAO extends GenericSharedDBDao {
	
	/**
	 * Busca as notícias publicadas por um curso.
	 * 
	 * @param idCurso
	 * @param qtd
	 * @return
	 */
	public Collection<NoticiaPortal> findPublicadasByCurso(Integer idCurso, int qtd) {
		String sql = "select id, titulo, descricao, destaque, data_cadastro , id_arquivo from comum.noticia_portal "
				+ "where  ativo = trueValue() and " + LocalizacaoNoticiaPortal.PORTAL_DISCENTE + " = trueValue() and publicada = trueValue() and id_curso = ?"
				+ " and (expirar_em is null or expirar_em >= ? ) order by data_cadastro desc " +  BDUtils.limit(qtd);

		return getJdbcTemplate(Database.getInstance().getComumDs()).query(sql, 
			new Object[] { idCurso, DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)}, 
			new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					NoticiaPortal noticia = new NoticiaPortal(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getBoolean(4));
					noticia.setCriadoEm( rs.getDate(5) );
					noticia.setIdArquivo( rs.getInt(6) );
					return noticia;
				}
			}
		);
	}
	
	/**
	 * Todas as noticias do portal especificado
	 * @param portal
	 * @return
	 */
	public Collection<NoticiaPortal> findPublicadasByLocalizacao(String portal) {
		String sql = "select id, titulo, descricao, destaque, data_cadastro, id_arquivo, publicada from comum.noticia_portal "
				+ " where  ativo = trueValue() and " + portal + " = trueValue() and publicada = trueValue() and id_curso is null and id_programa is null "
				+ " and (expirar_em is null or expirar_em >= ? ) order by data_cadastro desc";

		return getJdbcTemplate(Database.getInstance().getComumDs()).query(sql, 
				new Object[] { DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)},
			new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					return new NoticiaPortal(rs.getInt(1), rs.getString(2),
							rs.getString(3), rs.getBoolean(4), rs.getDate(5), rs.getInt(6), rs.getBoolean(7));
				}
			}
		);
	}

	/**
	 * Busca as notícias publicadas por um programa de pós-graduação.
	 * 
	 * @param idPrograma
	 * @param qtd
	 * @return
	 */
	public Collection<NoticiaPortal> findPublicadasByPrograma(Integer idPrograma, int qtd) {
		String sql = "select id, titulo, descricao, destaque, data_cadastro from comum.noticia_portal "
				+ "where ativo = trueValue() and " + LocalizacaoNoticiaPortal.PORTAL_DISCENTE + " = trueValue() and publicada = trueValue() and id_programa = ?"
				+ " and (expirar_em is null or expirar_em >= ? ) order by data_cadastro desc " + BDUtils.limit(qtd);

		return getJdbcTemplate(Database.getInstance().getComumDs()).query(sql, 
			new Object[] { idPrograma, DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)}, 
			new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					NoticiaPortal noticia = new NoticiaPortal(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getBoolean(4));
					noticia.setCriadoEm( rs.getDate(5) );
					return noticia;
				}
			}
		);
	}	
	
	/**
	 * Busca todas as noticias publicadas pelo curso
	 * 
	 * @param idCurso
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<NoticiaPortal> findAllPublicadasByCurso(Integer idCurso) throws HibernateException, DAOException {
		
		String hql = "select np from NoticiaPortal np " +
				" where np.ativo = trueValue() and np.portalDiscente = trueValue() and idCurso = :idCurso" +
				" order by criadoEm desc";
		
		Query q = getSession().createQuery(hql);

		q.setInteger("idCurso", idCurso);
		
		return q.list();
	}	
	
	/**
	 * Busca todas as notícias publicada pelo programa
	 * 
	 * @param idPrograma
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<NoticiaPortal> findAllPublicadasByPrograma(Integer idPrograma) throws HibernateException, DAOException {
		
		String hql = "select np from NoticiaPortal np " +
				" where np.ativo = trueValue() and np.portalDiscente = trueValue() and idPrograma = :idPrograma" +
				" order by criadoEm desc";
		
		Query q = getSession().createQuery(hql);

		q.setInteger("idPrograma", idPrograma);
		
		return q.list();
	}
	
}
