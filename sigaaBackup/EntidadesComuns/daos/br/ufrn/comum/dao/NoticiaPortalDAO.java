/**
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 01/11/2007
 * Autor:     David Pereira 
 */
package br.ufrn.comum.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.HibernateException;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericSharedDBDao;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.LocalizacaoNoticiaPortal;
import br.ufrn.comum.dominio.NoticiaPortal;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.comum.dominio.UsuarioPortal;

/**
 * DAO para buscas de notícias para os portais
 * dos sistemas. 
 * 
 * @author David Pereira
 * 
 */
@SuppressWarnings("unchecked")
public class NoticiaPortalDAO extends GenericSharedDBDao {

	/**
	 * Retorna todas as notícias publicadas de um portal
	 * 
	 * @param portal
	 * @param qtd
	 * @return
	 * @throws Exception 
	 * @throws HibernateException 
	 */
	public Collection<NoticiaPortal> findPublicadasByLocalizacao(String portal, int qtd) {
		String sql = "select id, titulo, descricao, destaque, id_arquivo, nome_arquivo from comum.noticia_portal "
				+ "where " + portal + " = trueValue() and publicada = trueValue() and id_curso is null and id_programa is null "
				+ "and (expirar_em is null or expirar_em >= ? ) and ativo = trueValue() order by data_cadastro desc " + BDUtils.limit(qtd);

		return getJdbcTemplate(Database.getInstance().getComumDs()).query(sql, 
			new Object[] { DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)}, 
			new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					return new NoticiaPortal(rs.getInt(1), rs.getString(2), rs.getString(3),rs.getBoolean(4), rs.getInt(5), rs.getString(6));
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
				+ " where  ativo = trueValue() and " + portal + " = trueValue() and publicada = trueValue() and id_curso is null"
				+ " order by data_cadastro desc";

		return getJdbcTemplate(Database.getInstance().getComumDs()).query(sql, 
			new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					return new NoticiaPortal(rs.getInt(1), rs.getString(2),
							rs.getString(3), rs.getBoolean(4), rs.getDate(5), rs.getInt(6), rs.getBoolean(7));
				}
			}
		);
	}
	

	/**
	 * Busca as notícias que um usuário pode ler.
	 * @param usuario
	 * @return
	 * @throws DAOException
	 */
	public Collection<NoticiaPortal> findByUsuario(UsuarioGeral usuario)
		throws DAOException {
		return findGeral(usuario, null, false, null, null, false, null);
	}
	
	

	/**
	 * Busca geral de notícias
	 * @param usuario
	 * @return
	 * @throws DAOException
	 */
	public Collection<NoticiaPortal> findGeral(UsuarioGeral usuario, String portal, boolean somenteDestaque,
			String palavraChave, Boolean publicada, boolean naoExpirada, Integer limite) throws DAOException {
		
			StringBuilder sqlNoticia = new StringBuilder(" SELECT ");
			
			sqlNoticia.append("  n.id, n.id_arquivo, n.titulo ");
			
			//Se passada a palavra-chave, trata o texto caso possua caracteres reservados da busca FULL-TEXT
			if(palavraChave != null)
				palavraChave = trataStrFullText(palavraChave);
			
			//Se passada a palavra-chave na consulta retornar somente a parte da descrição que possua a palavra.
			if(palavraChave != null)
				sqlNoticia.append(",ts_headline(n.descricao, to_tsquery('"+palavraChave+"') ) as  descricao");
			else
				sqlNoticia.append(",n.descricao");
			
			sqlNoticia.append( ", n.publicada, n.destaque, n.DATA_CADASTRO, n.EXPIRAR_EM, "
			+ " n.portal_docente, n.portal_discente, n.portal_publico_sigaa, portal_coord_graduacao, n.portal_coord_stricto, "
			+ " n.portal_coord_lato, n.portal_tutor, n.portal_avaliacao_institucional, n.portal_servidor, n.portal_publico_sigrh, "
			+ " n.portal_administrativo, n.portal_consultor, n.portal_publico_sipac, n.portal_chefia_unidade FROM comum.noticia_portal n " 
			+ "  WHERE  n.ativo = trueValue() ");
			
			if(!ValidatorUtil.isEmpty(usuario)){			
				
				UsuarioPortal usuarioPortal = null;
				
				UsuarioPortalDAO daoUP = new UsuarioPortalDAO();
				daoUP.setSession(getSession());
				usuarioPortal = daoUP.findPermissoes(usuario);
			
				if(!ValidatorUtil.isEmpty(usuarioPortal)){	
					sqlNoticia.append(" AND (" 
					+ (usuarioPortal.isPortalAvaliacaoInstitucional() ? LocalizacaoNoticiaPortal.PORTAL_AVALIACAO_INSTITUCIONAL + " = trueValue() or " : "")
					+ (usuarioPortal.isPortalConsultor() ? LocalizacaoNoticiaPortal.PORTAL_CONSULTOR + " = trueValue() or " : "")
					+ (usuarioPortal.isPortalCoordGraduacao() ? LocalizacaoNoticiaPortal.PORTAL_COORDENADOR_GRADUACAO + " = trueValue() or " : "")
					+ (usuarioPortal.isPortalCoordLato() ? LocalizacaoNoticiaPortal.PORTAL_COORDENADOR_LATO + " = trueValue() or " : "")
					+ (usuarioPortal.isPortalCoordStricto() ? LocalizacaoNoticiaPortal.PORTAL_COORDENADOR_STRICTO + " = trueValue() or " : "")
					+ (usuarioPortal.isPortalDiscente() ? LocalizacaoNoticiaPortal.PORTAL_DISCENTE + " = trueValue() or " : "")
					+ (usuarioPortal.isPortalDocente() ? LocalizacaoNoticiaPortal.PORTAL_DOCENTE + " = trueValue() or " : "")
					+ (usuarioPortal.isPortalPublicoSigaa() ? LocalizacaoNoticiaPortal.PORTAL_PUBLICO_SIGAA + " = trueValue() or " : "")
					+ (usuarioPortal.isPortalPublicoSigrh() ? LocalizacaoNoticiaPortal.PORTAL_PUBLICO_SIGRH + " = trueValue() or " : "")
					+ (usuarioPortal.isPortalPublicoSipac() ? LocalizacaoNoticiaPortal.PORTAL_PUBLICO_SIPAC + " = trueValue() or " : "")
					+ (usuarioPortal.isPortalServidor() ? LocalizacaoNoticiaPortal.PORTAL_SERVIDOR + " = trueValue() or " : "")
					+ (usuarioPortal.isPortalTutor() ? LocalizacaoNoticiaPortal.PORTAL_TUTOR + " = trueValue() or " : "")
					+ (usuarioPortal.isPortalAdministrativo() ? LocalizacaoNoticiaPortal.PORTAL_SIPAC + " = trueValue() or " : "")
					+ (usuarioPortal.isPortalChefiaUnidade() ? LocalizacaoNoticiaPortal.PORTAL_CHEFIA_UNIDADE + " = trueValue() or " : "")
					+ "  n.ID_USUARIO_CADASTRO = " + usuarioPortal.getUsuario().getId()+")");
					
				}
			}
			
			//Somente as notícias que não expiraram e publicadas
			if(!ValidatorUtil.isEmpty(naoExpirada) && naoExpirada)
				sqlNoticia.append( " AND (n.expirar_em IS NULL OR n.expirar_em >= current_date) " );
			
			//Somente notícias do portal passado
			if(!ValidatorUtil.isEmpty(portal))
				sqlNoticia.append( " AND "+portal+" = trueValue()");
			
			//Somente notícias do portal passado
			if(!ValidatorUtil.isEmpty(somenteDestaque) && somenteDestaque)
				sqlNoticia.append( " AND n.destaque = trueValue()");
			
			//Somente notícias publicadas ou não
			if(!ValidatorUtil.isEmpty(publicada))
				sqlNoticia.append( " AND n.publicada = "+(publicada?"trueValue()":"falseValue()"));
			
			//Se passada a palavra-chave efetua busca 
			if(!ValidatorUtil.isEmpty(palavraChave))
				sqlNoticia.append( " AND (vetorfts @@ to_tsquery('"+palavraChave+"') OR n.titulo ilike '"+palavraChave +"%')");
		
			sqlNoticia.append( " ORDER BY n.data_cadastro DESC, n.titulo ");
			
			if(limite != null)
				sqlNoticia.append( " LIMIT " + limite);
			
			
			List<Map<String, Object>> lista = getJdbcTemplate().queryForList(sqlNoticia.toString());
			
		
			List<NoticiaPortal> noticias = new ArrayList<NoticiaPortal>();
			NoticiaPortal n;

			for (Map<String, Object> map : lista) {
				
				n = new NoticiaPortal();
				n.setId((Integer) map.get("id"));
				n.setIdArquivo((Integer) map.get("id_arquivo"));
				n.setTitulo((String) map.get("titulo"));
				n.setPublicada((Boolean) map.get("publicada"));
				n.setDescricao((String) map.get("descricao"));
				n.setDestaque((Boolean) map.get("destaque"));
				n.setCriadoEm((Date) map.get("DATA_CADASTRO"));
				n.setExpirarEm((Date) map.get("EXPIRAR_EM"));
				n.setPortalAdministrativo((Boolean) map.get("portal_administrativo"));
				n.setPortalAvaliacaoInstitucional((Boolean) map.get("portal_avaliacao_institucional"));
				n.setPortalConsultor((Boolean) map.get("portal_consultor"));
				n.setPortalCoordGraduacao((Boolean) map.get("portal_coord_graduacao"));
				n.setPortalCoordLato((Boolean) map.get("portal_coord_lato"));
				n.setPortalCoordStricto((Boolean) map.get("portal_coord_stricto"));
				n.setPortalDiscente((Boolean) map.get("portal_discente"));
				n.setPortalDocente((Boolean) map.get("portal_docente"));
				n.setPortalPublicoSigaa((Boolean) map.get("portal_publico_sigaa"));
				n.setPortalPublicoSigrh((Boolean) map.get("portal_publico_sigrh"));
				n.setPortalPublicoSipac((Boolean) map.get("portal_publico_sipac"));
				n.setPortalServidor((Boolean) map.get("portal_servidor"));
				n.setPortalTutor((Boolean) map.get("portal_tutor"));
				n.setPortalChefiaUnidade((Boolean) map.get("portal_chefia_unidade"));

				
				noticias.add(n);
			}
			
			return noticias;

	}
	
	/*
	 * Método responsável em tratar a palavraChave na busca das notícias, retirando os caracteres inválidos.
	 * @param str
	 * @return
	 */
	public String trataStrFullText(String str) {
	
		str = str.replaceAll("[:.'*%\\[\\]\\(\\)\\&\\|\\@\\#\\!\\?\\<\\>\\{\\}\\+/_]", "").trim();
		
		String padrao = "\\s{2,}";
		Pattern regPat = Pattern.compile(padrao);
		   
		Matcher matcher = regPat.matcher(str);
		str = matcher.replaceAll(" ").trim();
		str = str.replaceAll(" ","|");
		
		return str; 
		
	}

}
