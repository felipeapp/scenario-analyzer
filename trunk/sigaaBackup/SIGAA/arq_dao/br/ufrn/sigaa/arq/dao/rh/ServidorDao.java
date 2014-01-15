/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '20/09/2006'
 *
 */
package br.ufrn.sigaa.arq.dao.rh;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;
import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.ge;
import static org.hibernate.criterion.Restrictions.isNull;
import static org.hibernate.criterion.Restrictions.or;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.dao.dialect.SQLDialect;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.rh.dominio.AtividadeServidor;
import br.ufrn.rh.dominio.Ativo;
import br.ufrn.rh.dominio.Cargo;
import br.ufrn.rh.dominio.Categoria;
import br.ufrn.rh.dominio.Formacao;
import br.ufrn.rh.dominio.Situacao;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.portal.dominio.PerfilServidor;
import br.ufrn.sigaa.rh.dominio.Designacao;

/**
 * Dao responsável por consultas específicas ao Servidor
 *
 * @author Gleydson
 *
 */
public class ServidorDao extends GenericSigaaDAO {

	/** Retorna uma lista de docentes, buscando por nome e unidade de lotação.
	 * @param nome
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public Collection<Servidor> findByDocente(String nome, int unidade) throws DAOException {
		return findByDocente(nome, null, unidade, true, null, false, null);
	}

	/** Retorna uma lista de docentes, buscando por nome, unidade de lotação, apenas ativos ou não, cedidos ou não.
	 * @param nome
	 * @param unidade
	 * @param apenasAtivos
	 * @param trazerCedidos
	 * @return
	 * @throws DAOException
	 */
	public Collection<Servidor> findByDocente(String nome, int unidade, boolean apenasAtivos, boolean trazerCedidos) throws DAOException {
		return findByDocente(nome, null, unidade, apenasAtivos, null, trazerCedidos, null);
	}

	/**
	 * Busca docentes por nome e unidade de lotação, permitindo escolher entre somente ativos ou não,
	 * e ainda definir o ano em que esteve ativo e se foi cedido ou não.
	 * @param nome
	 * @param unidade
	 * @param apenasAtivos
	 * @param anoAtivo
	 * @param cedidos
	 * @return
	 * @throws DAOException
	 */
	public Collection<Servidor> findByDocente(String nome, Integer siape, int unidade, boolean apenasAtivos, Integer anoAtivo, boolean cedidos, List<Integer> cargos) throws DAOException {
		try {
			String hql = "select s.id, s.pessoa.id, s.pessoa.nome, s.siape, s.ativo.descricao from Servidor s where s.categoria = :categoria " ;
			if (!ValidatorUtil.isEmpty(nome)){
				hql += 
				" and " + UFRNUtils.toAsciiUpperUTF8("s.pessoa.nomeAscii") +
				" like " + UFRNUtils.toAsciiUTF8("'" + nome.toUpperCase() + "%'");
			}
			if (unidade > 0)
				hql += " and unidade.id = :unidade ";
			if(apenasAtivos) {
				hql += " and ( (s.ativo = " + Ativo.SERVIDOR_ATIVO;

				if (anoAtivo != null) {
					hql += " or (s.dataDesligamento is null or year(s.dataDesligamento) >= " + anoAtivo + ")";
				}

				hql += ")";

				if( cedidos ){
					hql += " or s.ativo = " + Ativo.CEDIDO + " ) ";
				}else{
					hql += " ) ";
				}
			}
			
			if (siape != null && siape > 0)
				hql += " and s.siape = "+siape;
			
			if (!ValidatorUtil.isEmpty(cargos)){
				hql += " and s.cargo.id in "+UFRNUtils.gerarStringIn(cargos);
			}

			hql += "order by s.pessoa.nomeAscii";

			Query q = getSession()
					.createQuery(hql);
			if (unidade > 0)
				q.setInteger("unidade", unidade);
			q.setInteger("categoria", Categoria.DOCENTE);

			@SuppressWarnings("unchecked")
			Collection<Servidor> lista = new ArrayList<Servidor>();
			
			List<Object[]> l = q.list();
			
			for(Object[] obj : l){
				int col = 0;
				
				Servidor s = new Servidor();
				s.setId((Integer) obj[col++]);
				s.getPessoa().setId((Integer) obj[col++]);
				s.getPessoa().setNome((String) obj[col++]);
				s.setSiape((Integer) obj[col++]);
				
				Ativo a = new Ativo();
				a.setDescricao((String) obj[col++]);
				s.setAtivo(a);
				
				lista.add(s);
			}
			
			return lista;

		} catch (Exception e) {
			throw new DAOException(e);
		}

	}

	/**
	 * Retorna todos os servidores que são colaboradores voluntários ATIVOS 
	 * ou seja, que se aposentaram porém ainda desempenham atividades na UFRN
	 * @param nome
	 * @param unidade
	 * @param situacao
	 * @param ativos
	 * @return
	 * @throws DAOException
	 */
	public Collection<Servidor> findByColaboradorVoluntario(String nome, int unidade) throws DAOException {
		try {
			String hql = "select new Servidor(s.id, s.pessoa.nome, s.siape, s.ativo.descricao) from Servidor s, ColaboradorVoluntario cv"
				+ " where s.id = cv.servidor.id and s.categoria = :categoria "
				+ "and " + UFRNUtils.toAsciiUpperUTF8("s.pessoa.nome") + " like " + UFRNUtils.toAsciiUTF8("'" + nome.toUpperCase() + "%'")
				+ " and cv.ativo = trueValue() ";

			if (unidade > 0)
				hql += " and unidade.id = :unidade ";

			hql += "order by s.pessoa.nome";

			Query q = getSession()
					.createQuery(hql);
			if (unidade > 0)
				q.setInteger("unidade", unidade);

			q.setInteger("categoria", Categoria.DOCENTE);

			@SuppressWarnings("unchecked")
			Collection<Servidor> lista = q.list();
			return lista;

		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/** Retorna uma lista de docentes, buscando por nome, unidade de lotação e situação. 
	 * @param nome
	 * @param unidade
	 * @param situacao
	 * @return
	 * @throws DAOException
	 */
	public Collection<Servidor> findByDocente(String nome, int unidade, int situacao) throws DAOException {
		return findByDocente(nome, unidade, situacao, true, false);
	}

	/**
	 * Busca docentes por nome, unidade e situação, permitindo escolher entre ativos ou não, e cedidos ou não.
	 * @param nome
	 * @param unidade
	 * @param situacao
	 * @param apenasAtivos
	 * @param cedidos
	 * @return
	 * @throws DAOException
	 */
	public Collection<Servidor> findByDocente(String nome, int unidade, int situacao, boolean apenasAtivos, boolean cedidos) throws DAOException {

		try {
			String hql = "select new Servidor(s.id, s.pessoa.nome, s.siape, s.ativo.descricao) from Servidor s where s.categoria = :categoria " +
				"and " + UFRNUtils.toAsciiUpperUTF8("s.pessoa.nome") + " like " + UFRNUtils.toAsciiUTF8("'" + nome.toUpperCase() + "%'");

			if (unidade > 0)
				hql += " and unidade.id = :unidade ";
			if (situacao > 0)
				hql += " and situacaoServidor.id = :situacao ";
			if (apenasAtivos) {

				if( cedidos ){
					hql += " and ( s.ativo = " + Ativo.SERVIDOR_ATIVO;
					hql += " or s.ativo = " + Ativo.CEDIDO + " ) ";
				}else{
					hql += " and s.ativo = " + Ativo.SERVIDOR_ATIVO;
				}
			}

			hql += "order by s.pessoa.nome";

			Query q = getSession()
					.createQuery(hql);
			if (unidade > 0)
				q.setInteger("unidade", unidade);
			if ( situacao > 0)
				q.setInteger("situacao", situacao);

			q.setInteger("categoria", Categoria.DOCENTE);

			@SuppressWarnings("unchecked")
			Collection<Servidor> lista = q.list();
			return lista;

		} catch (Exception e) {
			throw new DAOException(e);
		}

	}

	/**
	 * Busca apenas docentes inativos por nome e unidade de lotação.
	 * @param nome
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public Collection<Servidor> findByDocenteInativo(String nome, int unidade) throws DAOException {

		try {
			String hql = "select new Servidor(s.id, s.pessoa.nome, s.siape, s.ativo.descricao) from Servidor s where s.categoria = :categoria " +
				"and " + UFRNUtils.toAsciiUpperUTF8("s.pessoa.nome") + " like " + UFRNUtils.toAsciiUTF8("'" + nome.toUpperCase() + "%'");

			if (unidade > 0)
				hql += " and unidade.id = :unidade ";

			hql += " and situacaoServidor.id = :situacao ";

			hql += "order by s.pessoa.nome";

			Query q = getSession()
					.createQuery(hql);

			q.setInteger("categoria", Categoria.DOCENTE);
			if (unidade > 0)
				q.setInteger("unidade", unidade);
			q.setInteger("situacao", Situacao.APOSENTADO);

			@SuppressWarnings("unchecked")
			Collection<Servidor> lista = q.list();
			return lista;

		} catch (Exception e) {
			throw new DAOException(e);
		}

	}

	/**
	 * Retorna todos os docentes externos que tem no início do nome
	 * o parâmetro passado, desconsiderando a acentuação.
	 * @param nome
	 * @return
	 * @throws DAOException
	 */
	public Collection<DocenteExterno> findByDocenteExterno(String nome) throws DAOException {
		return findByDocenteExterno(nome, null);
	}
	
	/**
	 * Busca docentes externos por nome com projeção.
	 * @param nome
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public Collection<DocenteExterno> findByDocenteExterno(String nome, Unidade unidade) throws DAOException {

		try {
			StringBuilder hql = new StringBuilder();
			hql.append("SELECT new DocenteExterno(de.id, de.pessoa.cpf_cnpj, de.pessoa.nome, de.unidade.nome, de.unidade.sigla) from DocenteExterno de ");
			hql.append("WHERE  de.pessoa.nomeAscii like '" + nome.toUpperCase() + "%'");
			hql.append(" AND de.matricula is not null ");
			hql.append(" AND de.ativo = trueValue() ");
			if(unidade != null)
				hql.append(" AND de.unidade.id = "+ unidade.getId());
			hql.append(" AND ( de.prazoValidade is null or prazoValidade > :validade)");
			hql.append(" ORDER BY  de.pessoa.nome");

			Query q = getSession().createQuery(hql.toString());

			q.setDate("validade", DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));

			@SuppressWarnings("unchecked")
			Collection<DocenteExterno> lista = q.list();
			return lista;

		} catch (Exception e) {
			throw new DAOException(e);
		}

	}
	
	
	/**
	 * Retorna todos as pessoa que tem no início do nome
	 * o parâmetro passado, desconsiderando a acentuação.	 
	 * @param nome
	 * @return
	 * @throws DAOException
	 */
	public Collection<Pessoa> findPessoaByNome(String nome) throws DAOException {
			try {
				Query q = getSession().createQuery("select p.id, p.nome, p.cpf_cnpj, p.passaporte, p.internacional from Pessoa p " +
					"where " + UFRNUtils.toAsciiUpperUTF8("p.nomeAscii") + " like "
							+ UFRNUtils.toAsciiUTF8("'" + nome.toUpperCase() + "%'") +
							" and (p.cpf_cnpj is not null or p.internacional = "+SQLDialect.TRUE+") order by p.nome asc" );
				
				@SuppressWarnings("unchecked")
				List<Object> result = q.list();				
				List<Pessoa> pessoas = new ArrayList<Pessoa>();

				for (Iterator<Object> it = result.iterator(); it.hasNext(); ) {
					Object[] linha = (Object[]) it.next();
					Pessoa p = new Pessoa();
					p.setId((Integer) linha[0]);
					p.setNome((String) linha[1]);
					p.setCpf_cnpj((Long) linha[2]);
					p.setPassaporte((String) linha[3]);
					pessoas.add(p);
				}

				return pessoas;

			} catch (Exception e) {
				throw new DAOException(e);
			}

	}

	/**
	 * Método utilizado para trazer somente os docentes externos de um determinado curso lato sensu
	 * @param nome
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public Collection<DocenteExterno> findByDocenteExternoLato(String nome, Curso curso) throws DAOException {

		try {
			String hql = "select new DocenteExterno(de.id, de.pessoa.cpf_cnpj, de.pessoa.nome) from DocenteExterno de " +
			"where de.pessoa.nomeAscii like '" + nome.toUpperCase() + "%'"
					+ " and de.id in ( "
					+ " 	select cd.docenteExterno.id from CorpoDocenteCursoLato cd where cd.cursoLato.id = :curso) "
					+ " and de.ativo = trueValue() "
					+ " order by de.pessoa.nome";

			Query q = getSession().createQuery(hql);

			q.setInteger("curso", curso.getId());

			@SuppressWarnings("unchecked")
			Collection<DocenteExterno> lista = q.list();
			return lista;

		} catch (Exception e) {
			throw new DAOException(e);
		}

	}

	/**
	 * Busca docentes externos pelo CPF.
	 * @param cpf
	 * @return
	 * @throws DAOException
	 */
	public DocenteExterno findByDocenteExterno(long cpf) throws DAOException {

		try {
			String hql = "from DocenteExterno de where de.pessoa.cpf_cnpj ="+cpf;

			Query q = getSession().createQuery(hql);
			@SuppressWarnings("unchecked")
			Collection<DocenteExterno> lista = q.list();
			if (lista != null && lista.size() > 0)
				return lista.iterator().next();
			return null;
		} catch (Exception e) {
			throw new DAOException(e);
		}

	}

	/**
	 * Busca docentes externos extrangeiros pelo passaporte.
	 * @param passaporte
	 * @return
	 * @throws DAOException
	 */
	public DocenteExterno findByDocenteExternoEstrangeiro(String passaporte) throws DAOException {

		try {
			String hql = "from DocenteExterno de where de.pessoa.passaporte ="+passaporte;

			Query q = getSession().createQuery(hql);
			@SuppressWarnings("unchecked")
			Collection<DocenteExterno> lista = q.list();
			if (lista != null && lista.size() > 0)
				return lista.iterator().next();
			return null;
		} catch (Exception e) {
			throw new DAOException(e);
		}

	}

	/** Retorna uma lista de servidores, buscando por nome, unidade de lotação, ativos ou não, por unidade gestora ou não.
	 * @param nome
	 * @param unidade
	 * @param ativos
	 * @param gestora
	 * @return
	 * @throws DAOException
	 */
	public Collection<Servidor> findByNome(String nome, int unidade, boolean ativos, boolean gestora) throws DAOException {
		return findByNome(nome, unidade, ativos, gestora, 0, false);
	}
	
	/**
	 * Busca servidores por nome e opcionalmente por unidade de lotação, ativos ou não, por unidade gestora, e por categoria.
	 * @param nome
	 * @param unidade
	 * @param ativos
	 * @param gestora
	 * @param categoria
	 * @return
	 * @throws DAOException
	 */
	public Collection<Servidor> findByNome(String nome, int unidade, boolean ativos, boolean gestora, int categoria, boolean areaPublica) throws DAOException {
		try {
			String projecao;
			if ( areaPublica )
				projecao = "s.id, s.pessoa.nome, s.ativo.descricao";
			else
				projecao = "s.id, s.pessoa.nome, s.ativo.descricao, s.siape, s.pessoa.id";
				
			StringBuilder hql = new StringBuilder( " SELECT " +  projecao + " FROM " );
			hql.append( " ColaboradorVoluntario cv RIGHT JOIN cv.servidor s WHERE " );
			
			hql.append( UFRNUtils.toAsciiUpperUTF8("s.pessoa.nomeAscii") );
			hql.append( " LIKE " );
			hql.append( UFRNUtils.toAsciiUTF8( "'" + nome.toUpperCase() + "%'" ) );
		
			if ( ativos ){
				hql.append( " AND ( s.ativo =  " );
				hql.append( Ativo.SERVIDOR_ATIVO );
				hql.append( " OR cv.ativo = trueValue() ) " );
			}

			if ( unidade > 0 ) {
				if (gestora) 
					hql.append( " AND s.unidade.gestora.id = :unidade " );
				else 
					hql.append( " AND s.unidade.id = :unidade " );
			}
			
			if ( categoria > 0 ) 
				hql.append( " AND s.categoria.id = :categoria " );
			
			hql.append( " GROUP BY s.id, s.pessoa.nome, s.siape, s.pessoa.id, s.ativo.descricao ORDER BY s.pessoa.nome " );

			Query q = getSession().createQuery(hql.toString());
			
			if ( unidade > 0 )
				q.setInteger("unidade", unidade);
			if ( categoria > 0 )
				q.setInteger("categoria", categoria);
			
			Collection<Servidor> lista = new ArrayList<Servidor>();
			
			List<Object[]> l = q.list();
			
			for(Object[] obj : l){
				int col = 0;
				
				Servidor s = new Servidor();
				s.setId((Integer) obj[col++]);
				s.getPessoa().setNome((String) obj[col++]);
				s.setAtivo(new Ativo());
				s.getAtivo().setDescricao((String) obj[col++]);
				if ( !areaPublica ) {
					s.setSiape((Integer) obj[col++] );
					s.getPessoa().setId((Integer) obj[col++]);
				}
				
				lista.add(s);
			}
			
			return lista;
			
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/** Retorna uma lista de servidores, buscando por nome e unidade de lotação.
	 * @param nome
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public Collection<Servidor> findByNome(String nome, int unidade) throws DAOException {
		return findByNome(nome, unidade, true);
	}
	/** Retorna uma lista de servidores, buscando por nome, unidade de lotação, ativo ou não.
	 * @param nome
	 * @param unidade
	 * @param ativos
	 * @return
	 * @throws DAOException
	 */
	public Collection<Servidor> findByNome(String nome, int unidade, boolean ativos) throws DAOException {
		return findByNome(nome, unidade, ativos, false);
	}

	
	/**
	 * Busca otimizada de servidores buscando por um status
	 * @param nome
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public Collection<Servidor> findGeral(int unidade, String nome, Integer ativo, Integer... categoria) throws DAOException{
		
		List<Integer> situacoes = null;
		
		if( !ValidatorUtil.isEmpty(ativo) ){
			situacoes = new ArrayList<Integer>();
			situacoes.add(ativo);
		}
		
		return findGeral(unidade, nome, situacoes , categoria);
		
	}
	
	
	/**
	 * Busca otimizada de servidores
	 * @param nome
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public Collection<Servidor> findGeral(int unidade, String nome, List<Integer> situacoes, Integer... categoria) throws DAOException {
		
		try {
			
			StringBuilder sql = new StringBuilder();
			List<Object> param = new ArrayList<Object>();
			
			sql.append(" SELECT s.id_servidor, p.id_pessoa, p.nome, p.email, s.siape, s.id_cargo as id_cargo, ca.denominacao,  ");
			sql.append(" 	u.nome as unidade, s.id_perfil,f.id_formacao, st.descricao as situacao, st.id_situacao, ");
			sql.append(" 	( SELECT us.id_foto FROM comum.usuario us WHERE us.id_pessoa = p.id_pessoa ");
			sql.append(" 	AND us.id_foto IS NOT NULL AND ( us.inativo = falseValue() OR us.inativo IS NULL ) ");
			sql.append(" 	ORDER BY us.ultimo_acesso DESC, us.id_usuario DESC  ");
			sql.append( BDUtils.limit(1) );
			sql.append(" 	) as id_foto ");
			sql.append(" FROM rh.servidor s inner JOIN comum.pessoa p ON p.id_pessoa = s.id_pessoa ");
			sql.append(" 	INNER JOIN rh.situacao_servidor st ON st.id_situacao = s.id_situacao ");
			sql.append(" 	INNER JOIN rh.formacao f ON f.id_formacao = s.id_formacao ");
			sql.append(" 	INNER JOIN comum.unidade u ON u.id_unidade = s.id_unidade ");
			sql.append(" 	LEFT JOIN rh.cargo ca ON ca.id = s.id_cargo ");
			sql.append(" WHERE  1 = 1 ");

			
			if( unidade > 0){
				sql.append(" AND u.id_unidade = ? ");	
				param.add( unidade );
			}
			if ( !ValidatorUtil.isEmpty(nome) ) {
				sql.append(" AND UPPER(p.nome_ascii) LIKE '%");
				sql.append( StringUtils.toAsciiAndUpperCase(nome.trim().toUpperCase().replace("'","\\'").replace("%","")) );
				sql.append( "%'");
			}
			
			if ( !ValidatorUtil.isEmpty(situacoes) ) {
				sql.append(" AND s.id_ativo IN ");	
				sql.append( UFRNUtils.gerarStringIn(situacoes) );
			}
			
			if ( !ValidatorUtil.isEmpty(categoria) ) {
				sql.append(" AND s.id_categoria IN ");	
				sql.append( UFRNUtils.gerarStringIn(categoria) );
			}
			
			sql.append(" ORDER BY situacao, ca.denominacao, p.nome_ascii");

			@SuppressWarnings("unchecked")
			Collection<Servidor> lista = getJdbcTemplate().query( sql.toString(), param.toArray(), new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					
					Servidor servidor = new Servidor();
					servidor.setCargo(new Cargo(rs.getInt("id_cargo")));
					servidor.getCargo().setDenominacao(rs.getString("denominacao"));
					servidor.setId(rs.getInt("id_servidor"));
					servidor.getPessoa().setId(rs.getInt("id_pessoa"));
					servidor.getPessoa().setNome(rs.getString("nome"));
					servidor.getPessoa().setEmail(rs.getString("email"));
					servidor.setSiape(rs.getInt("siape"));
					servidor.setIdFoto(rs.getInt("id_foto"));
					servidor.setUnidade(new Unidade());
					servidor.getUnidade().setNome(rs.getString("unidade"));
					servidor.setIdPerfil(rs.getInt("id_perfil"));
					servidor.setFormacao(new Formacao());
					servidor.getFormacao().setId(rs.getInt("id_formacao"));
					servidor.setSituacaoServidor(new Situacao(rs.getInt("id_situacao")));
					servidor.getSituacaoServidor().setDescricao(rs.getString("situacao"));

					return servidor;
				
				}
			});
			
			return lista;
			
		} catch (Exception e) {
			throw new DAOException(e);
		}
		
	}

	/**
	 * Busca otimizada de docentes ativos por nome do servidor e unidade de lotação
	 * @param nome
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public Collection<Servidor> findDocenteByUnidadeNome(int unidade, String nome) throws DAOException {
		List<Integer> situacoes = new ArrayList<Integer>();
		situacoes.add( Ativo.SERVIDOR_ATIVO );
		situacoes.add( Ativo.CEDIDO );
		return findGeral(unidade, nome, situacoes, Categoria.DOCENTE);
	}
	
	/**
	 * Busca um perfil de servidor com base no servidor do usuário passado como parâmetro.
	 * @param usuario
	 * @return
	 * @throws DAOException
	 */
	public PerfilServidor findByPerfilDocente(Usuario usuario) throws DAOException {

		try {

			Criteria c = getSession().createCriteria(PerfilServidor.class);
			c.add(Expression.eq("servidor", usuario.getServidor()));

			return (PerfilServidor) c.uniqueResult();

		} catch (Exception e) {
			throw new DAOException(e);
		}

	}

	/**
	 * Busca todos os docentes ativos lotados na unidade passada por parâmetro.
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public Collection<Servidor> findByDocente(int unidade) throws DAOException {

		try {
			String hql = "select new Servidor(s.id, s.pessoa.nome) from Servidor s where s.categoria = :categoria " +
			"and s.ativo = :ativo ";
			if (unidade > 0)
				hql += " and unidade.id = :unidade ";

			hql += "order by s.pessoa.nome";

			Query q = getSession()
					.createQuery(hql);
			if (unidade > 0)
				q.setInteger("unidade", unidade);
			q.setInteger("categoria", Categoria.DOCENTE);
			q.setInteger("ativo", Ativo.SERVIDOR_ATIVO);

			@SuppressWarnings("unchecked")
			Collection<Servidor> lista = q.list();
			return lista;

		} catch (Exception e) {
			throw new DAOException(e);
		}

	}

	/**
	 * Retorna as designações ativas do servidor passado como parâmetro.
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	public Collection<Designacao> findDesignacoesAtivas(Servidor servidor) throws DAOException {

		try {

			Criteria c = getSession().createCriteria(Designacao.class);
			c.add(Expression.eq("servidorSigaa", servidor));
			c.add(Expression.or(Expression.isNull("fim"), Expression.ge("fim", new Date())));

			@SuppressWarnings("unchecked")
			Collection<Designacao> lista = c.list();
			return lista;

		} catch (Exception e) {
			throw new DAOException(e);
		}

	}

	/**
	 * Retorna a quantidade de docentes (internos OU externos) informados na proposta de
	 * criação de um curso lato sensu.
	 * @param externo
	 * @param idCursoLato
	 * @return
	 * @throws DAOException
	 */
	public long findQtdDocentesCursoLato(boolean externo, int idCursoLato) throws DAOException {

		try {
			StringBuffer hql = new StringBuffer();
			hql.append("select count(*) from CorpoDocenteCursoLato c ");
			hql.append("where c.cursoLato.id = "+ idCursoLato);
			if( !externo )
				hql.append("and c.docenteExterno = null");
			else
				hql.append("and c.servidor = null");

			Query q = getSession().createQuery(hql.toString());
			return (Long) q.uniqueResult();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna todos os chefes de departamento do centro informado
	 * @param unidade o id do centro
	 * @return coleção de chefes de departamento
	 * @throws DAOException
	 */
	public Collection<Servidor> findChefesDepartamentoByCentro(int unidade) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(Designacao.class);
			c.createCriteria("atividade").add(Expression.in("codigoRH", AtividadeServidor.CHEFE_DEPARTAMENTO));
			c.add(Expression.or(Expression.isNull("fim"), Expression.ge("fim", new Date())));
			c.setProjection(Projections.property("servidorSigaa"));
			c.createCriteria("servidorSigaa").createCriteria("unidade").
				add(Expression.eq("gestora", new Unidade(unidade)));

			@SuppressWarnings("unchecked")
			Collection<Servidor> lista = c.list();
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna todos os chefes do departamento informado
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public  Collection<Servidor> findChefesByDepartamento(int unidade) throws DAOException {
		return findChefesDiretoresByUnidade(unidade, AtividadeServidor.CHEFE_DEPARTAMENTO);
	}
	
	/**
	 * Retorna todos os diretores do centro informado
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public  Collection<Servidor> findDiretoresByCentro(int unidade) throws DAOException {
		return findChefesDiretoresByUnidade(unidade, AtividadeServidor.DIRETOR_CENTRO);
	}
	
	
	/**
	 * Retorna todos os chefes do departamento informado
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public Collection<Servidor> findChefesDiretoresByUnidade(int unidade, List<Integer> atividades) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(Designacao.class);
			c.add(Expression.eq("gerencia", "T"));
			c.createCriteria("atividade").add(Expression.in("codigoRH", atividades));
			c.add(Expression.or(Expression.isNull("fim"), Expression.ge("fim", new Date())));
			c.createCriteria("unidadeDesignacao").add(Expression.eq("id", unidade));
			
			c.setProjection(Projections.property("servidorSigaa"));
			c.addOrder(Order.desc("inicio"));
			@SuppressWarnings("unchecked")
			Collection<Servidor> lista = c.list();
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retorna todos os chefes do departamento informado
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public Servidor findChefeByDepartamento(int unidade) throws DAOException {
		return findChefeDiretorByUnidade(unidade, AtividadeServidor.CHEFE_DEPARTAMENTO);
	}
	
	/**
	 * Retorna todos os diretores do centro informado
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public Servidor findDiretorByCentro(int unidade) throws DAOException {
		return findChefeDiretorByUnidade(unidade, AtividadeServidor.DIRETOR_CENTRO);
	}
	
	
	/**
	 * Retorna todos os diretores da unidade informada
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public Servidor findDiretorByUnidade(int unidade) throws DAOException {
		return findChefeDiretorByUnidade(unidade, AtividadeServidor.DIRETOR_UNIDADE);
	}
	
	/**
	 * Retorna diretores e chefes da unidade informado
	 * @param unidade
	 * @param atividades
	 * @return
	 * @throws DAOException
	 */
	public Servidor findChefeDiretorByUnidade(int unidade, List<Integer> atividades) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(Designacao.class);
			c.add(Expression.eq("gerencia", "T"));
			c.createCriteria("atividade").add(Expression.in("codigoRH", atividades));
			c.add(Expression.or(Expression.isNull("fim"), Expression.ge("fim", new Date())));
			c.createCriteria("unidadeDesignacao").add(Expression.eq("id", unidade));
			c.setProjection(Projections.property("servidorSigaa"));
			
			c.addOrder(Order.desc("inicio"));
			c.setMaxResults(1);
			return (Servidor) c.uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}
	
	
	
	
	
	/**
	 * Busca servidores pelo CPF sem projeção.
	 * @param cpf
	 * @return
	 * @throws DAOException
	 */
	public Servidor findByCpf(long cpf) throws DAOException {

		try {
			String hql = "from Servidor s where s.pessoa.cpf_cnpj ="+cpf;

			Query q = getSession().createQuery(hql);
			@SuppressWarnings("unchecked")
			Collection<Servidor> lista = q.list();
			if (lista != null && lista.size() > 0)
				return lista.iterator().next();
			return null;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Busca servidores pela matrícula SIAPE e situação.
	 * @param siape
	 * @return
	 * @throws DAOException
	 */
	public Servidor findBySiape(int siape, Integer... situacao) throws DAOException {

		try {
			
			// A ordenação pelo campo situação é importante para evitar que 
			// servidor com o mesmo siape seja exibido erroneamente
			StringBuilder hql = new StringBuilder(" FROM Servidor  WHERE siape = :siape ");
			
			if( !ValidatorUtil.isEmpty(situacao) ){
				hql.append(" AND ativo IN ");
				hql.append(UFRNUtils.gerarStringIn(situacao));
			}
			
			hql.append(" ORDER BY situacaoServidor ");

			Query q = getSession().createQuery( hql.toString() );
			q.setInteger("siape", siape);
			
			q.setMaxResults(1);
			return (Servidor) q.uniqueResult();
			
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	
	/**
	 * Retorna todos os docentes ativos com projeção.
	 * @return
	 * @throws DAOException
	 */
	public Collection<Servidor> findAllDocentes() throws DAOException {
		try {
			String hql = "select new Servidor(s.id, s.pessoa.nome, s.siape) " +
					" from Servidor s " +
					" where s.categoria = :categoria " +
					" and s.ativo = :ativo " +
					" order by s.pessoa.nome";

			Query q = getSession().createQuery(hql);
			q.setInteger("categoria", Categoria.DOCENTE);
			q.setInteger("ativo", Ativo.SERVIDOR_ATIVO);

			@SuppressWarnings("unchecked")
			Collection<Servidor> lista = q.list();
			return lista;

		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna todos os servidores ativos associados a pessoa informada
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	public Collection<Servidor> findAtivoByPessoa(int idPessoa) throws  DAOException{

		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT s FROM Servidor s ");
		hql.append(" WHERE s.pessoa.id = :idPessoa ");
		hql.append(" AND s.ativo = " + Ativo.SERVIDOR_ATIVO);

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idPessoa", idPessoa);
		@SuppressWarnings("unchecked")
		Collection<Servidor> lista = q.list();
		return lista;

	}

	/**
	 * Retorna todos os servidores ativos associados a pessoa informada
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	public Collection<Servidor> findByPessoaAndVinculos(int idPessoa, int... vinculos) throws  DAOException{

		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT s FROM Servidor s ");
		hql.append(" WHERE s.pessoa.id = :idPessoa ");
		if (vinculos != null)
			hql.append(" AND s.ativo in " + UFRNUtils.gerarStringIn(vinculos) );

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idPessoa", idPessoa);
		@SuppressWarnings("unchecked")
		Collection<Servidor> lista = q.list();
		return lista;

	}
	
	/**
	 * Retorna todos os servidores ativos associados a pessoa informada
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	public Integer countByPessoaAndVinculos(int idPessoa, int... vinculos) throws  DAOException{

		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT count(s.id) FROM Servidor s ");
		hql.append(" WHERE s.pessoa.id = :idPessoa ");
		if (vinculos != null)
			hql.append(" AND s.ativo in " + UFRNUtils.gerarStringIn(vinculos) );

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idPessoa", idPessoa);
		
		return ((Number) q.uniqueResult()).intValue();
	}

	/**
	 * Retorna se o servidor ativo é colaborador voluntário.
	 * @param s
	 * @return
	 */
	public boolean isColaboradorVoluntario(Servidor s) {
		return getJdbcTemplate().queryForInt("select count(*) from rh.colaborador_voluntario where id_servidor = ? and ativo = trueValue()", s.getId()) > 0;
	}

	/**
	 * Busca a designação ativa de um servidor para uma determinada unidade.
	 * @throws DAOException 
	 */
	public Designacao findDesignacaoAtivaUnidade(Servidor servidor, UnidadeGeral unidade) throws DAOException {
		try {

			Criteria c = getSession().createCriteria(Designacao.class);
			c.add(eq("servidorSigaa", servidor)).add(eq("unidadeDesignacao.id", unidade.getId()));
			c.add(or(isNull("fim"), ge("fim", new Date())));
			c.setMaxResults(1);
			
			return (Designacao) c.uniqueResult();

		} catch (Exception e) {
			throw new DAOException(e);
		}

	}
	
	/**
	 * Busca o siape do servidor através do login
	 * passado como parâmetro
	 * @param login
	 * @return
	 * @throws DAOException
	 */
	public Integer findSiapeByLogin(String login, List<Integer> situacoes) throws DAOException {

		StringBuilder hql = new StringBuilder();
		Integer[] ativo = new Integer[]{Ativo.SERVIDOR_ATIVO,
				Ativo.APOSENTADO, Ativo.EXCLUIDO, Ativo.CEDIDO};
		
		hql.append(" SELECT s.siape FROM Usuario u, Servidor s  ");
		hql.append(" WHERE u.pessoa.id = s.pessoa.id ");
		hql.append(" AND s.categoria = " + Categoria.DOCENTE);
		hql.append(" AND s.ativo IN " + UFRNUtils.gerarStringIn(ativo));
		hql.append(" AND s.situacaoServidor IN " + UFRNUtils.gerarStringIn(situacoes));
		hql.append(" AND u.login = :login");
		hql.append(" ORDER BY s.ativo, s.situacaoServidor");
		
		Query q = getSession().createQuery(hql.toString());
		q.setString("login", login);
		q.setMaxResults(1);
		
		if( !ValidatorUtil.isEmpty(q.list()) )
			return (Integer) q.uniqueResult(); 
		else
			return null;
		
	}

	/** Busca e retorna uma coleção de docentes DOUTORES que não estão lecionando turmas no ano/período informados.
	 * @param ano obrigatório, restringe o ano ao qual o relatório é gerado.
	 * @param periodo opcional, caso seja igual a zero, não restringirá o período do relatório.
	 * @param nivelEnsino opcional, caso seja igual a zero, não restringirá o nível de ensino.
	 * @param idUnidade opcional, caso seja igual a zero, não restringirá a unidade do docente.
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public Collection<Servidor> findDocenteSemTurmaOrientacaoAcademica(int ano, int periodo, Character nivelEnsino, int idUnidade) throws HibernateException, DAOException {
		StringBuilder sqlServidor = new StringBuilder("select id_servidor" +
				" from rh.servidor" +
				" where id_ativo = 1" +
				" and id_categoria = " + Categoria.DOCENTE +
				" and id_formacao in (27, 28)");
		StringBuilder sqlTurmas = new StringBuilder("select distinct s.id_servidor" +
				"   from ensino.docente_turma dt" +
				"   inner join ensino.turma t using (id_turma)" + 
				"   inner join ensino.componente_curricular cc using (id_disciplina)" +
				"   left join ensino.docente_externo de using (id_docente_externo)" +
				"   left join rh.servidor s on (dt.id_docente = s.id_servidor or de.id_servidor = s.id_servidor)" +
				"   where s.id_servidor is not null");
		StringBuilder sqlOrientacao = new StringBuilder("select distinct s.id_servidor" +
				"   from graduacao.orientacao_academica oa" +
				"   inner join discente d using (id_discente)" +
				"   left join ensino.docente_externo de using (id_docente_externo)" +
				"   left join rh.servidor s on (de.id_servidor = s.id_servidor or oa.id_servidor = s.id_servidor)" +
				"   where cancelado = falseValue()" +
				"   and fim is not null" +
				"   and fim >= current_date" +
				"   and s.id_servidor is not null");
		// restrições
		if (ano > 0) {
			sqlTurmas.append("   and t.ano = :ano");
			sqlOrientacao.append("   and fim is not null" +
					"   and extract('year' from fim) >= :ano");
		}
		if (periodo > 0) {
			sqlTurmas.append("   and t.periodo = :periodo");
			sqlOrientacao.append("   and fim is not null" +
					"   and extract('month' from fim) >= :periodo");
		}
		if (nivelEnsino != null) {
			sqlTurmas.append("   and cc.nivel = :nivelEnsino");
			sqlOrientacao.append("   and d.nivel = :nivelEnsino");
		}
		if (idUnidade > 0) {
			sqlServidor.append(" and id_unidade = :idUnidade");
			sqlTurmas.append("   and s.id_unidade = :idUnidade");
			sqlOrientacao.append("   and s.id_unidade = :idUnidade");
		}
		
		StringBuilder sql = new StringBuilder();
		sql.append(sqlServidor);
		// sem turmas
		sql.append(" except ").append(sqlTurmas);
		// sem orientandos
		sql.append(" except ").append(sqlOrientacao);
		SQLQuery q = getSession().createSQLQuery(sql.toString());
		
		// valores das restrições
		if (ano > 0) {
			q.setInteger("ano", ano);
		}
		if (periodo > 0) {
			q.setInteger("periodo", periodo);
		}
		if (nivelEnsino != null) {
			q.setCharacter("nivelEnsino", nivelEnsino);
		}
		if (idUnidade > 0) {
			q.setInteger("idUnidade", idUnidade);
		}
		@SuppressWarnings("unchecked")
		List<Integer> ids = q.list();
		
		if (ids != null && !ids.isEmpty()) {
			Criteria c = getSession().createCriteria(Servidor.class).add(Restrictions.in("id", ids));
			c.setFetchMode("unidade", FetchMode.JOIN);
			c.setFetchMode("pessoa", FetchMode.JOIN);
			c.createCriteria("unidade").addOrder(Order.asc("nome"));
			c.createCriteria("pessoa").addOrder(Order.asc("nome"));
			@SuppressWarnings("unchecked")
			Collection<Servidor> lista = c.list();
			return lista;
		}
		return null;
	}
	
	/**
	 * Retorna a listagem de todos os docentes de graduação que tem pendências
	 * na divulgação do rendimento escolar, bem como os chefes de departamento
	 * vinculados aos docentes.
	 * 
	 * @return
	 * @throws DAOException
	 * @throws ParseException 
	 */
	public List<Map<String, Object>> findPrazoMaximoEstouradoRendimentoEscolar() 
	throws DAOException{

		int anoAtual = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad().getAno(); 
		int periodoAtual = 	CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad().getPeriodo();

		String sql = ""+
		" SELECT DISTINCT"+ 
		" un.nome as departamento, p2.nome as chefe_nome, u2.email as chefe_email"+
		" ,p.nome as docente_nome, u.email as docente_email"+
		" ,cc.codigo, ccd.nome as componente,t.codigo as turma , nu.nota, nu.faltas, nu.unidade"+
		" ,ca.datafimprimeiraunidade,ca.datafimsegundaunidade, ca.datafimterceiraunidade"+
		" FROM ava.configuracoes_ava ca"+
		" INNER JOIN ensino.matricula_componente mc ON (ca.id_turma = mc.id_turma)"+
		" LEFT  JOIN ensino.nota_unidade nu ON (" +
		"  mc.id_matricula_componente = nu.id_matricula_componente" +
		"  AND("+
		" 	  ((nu.unidade = 1) AND (:diasUteis  >= ca.datafimprimeiraunidade)) OR"+
		"	  ((nu.unidade = 2) AND (:diasUteis  >= ca.datafimsegundaunidade))  OR"+
		"	  ((nu.unidade = 3) AND (:diasUteisUltimaUnidade >= ca.datafimterceiraunidade))"+
		"     )"+
		" )"+
		" INNER JOIN ensino.componente_curricular cc ON (mc.id_componente_curricular = cc.id_disciplina)"+
		" INNER JOIN ensino.componente_curricular_detalhes ccd ON (cc.id_detalhe = ccd.id_componente_detalhes)"+
		" INNER JOIN ensino.docente_turma dt ON (mc.id_turma = dt.id_turma)"+
		" INNER JOIN ensino.turma t ON (mc.id_turma = t.id_turma)"+
		" INNER JOIN rh.servidor s ON (dt.id_docente = s.id_servidor)"+
		" INNER JOIN comum.pessoa p ON (s.id_pessoa = p.id_pessoa)"+
		" INNER JOIN comum.usuario u ON (s.id_servidor = u.id_servidor)"+
		" INNER JOIN comum.unidade un ON (s.id_unidade = un.id_unidade)"+
		" INNER JOIN comum.responsavel_unidade ru ON (s.id_unidade = ru.id_unidade)"+
		" INNER JOIN rh.servidor s2 ON (ru.id_servidor = s2.id_servidor)"+
		" INNER JOIN comum.usuario u2 ON (s2.id_servidor = u2.id_servidor)"+
		" INNER JOIN comum.pessoa p2 ON (s2.id_pessoa = p2.id_pessoa)"+
		" WHERE nu.ativo = trueValue() and mc.ano = "+anoAtual+
		" AND mc.periodo = "+periodoAtual+
		" AND mc.id_situacao_matricula = "+SituacaoMatricula.MATRICULADO.getId()+
		" AND cc.nivel = '"+NivelEnsino.GRADUACAO+"'"+
		" AND (nu.nota IS NULL OR nu.faltas IS NULL)"+
		" AND ca.datafimprimeiraunidade IS NOT NULL"+
		" AND ca.datafimterceiraunidade IS NOT NULL"+
		" AND ca.datafimsegundaunidade IS NOT NULL"+
		" AND ru.nivel_responsabilidade = 'C'"+ 
		" AND (ru.data_fim is null or ru.data_fim >= :dataCorrente)"+
		" AND dt.id_docente is not null AND u.email is not null";

		List<Map<String, Object>> resultado = new ArrayList<Map<String, Object>>();

		try {
			SQLQuery q = getSession().createSQLQuery(sql);
			q.setDate("dataCorrente", new Date());
			q.setDate("diasUteis", CalendarUtils.subtrairDiasUteis(new Date(), 10 ));
			q.setDate("diasUteisUltimaUnidade", CalendarUtils.subtrairDiasUteis(new Date(), 3 )); 
			q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> lista = q.list();
			resultado = lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return resultado;
	}
	/**
	 * Método que retorna um Collection de Servidores do SIGAA a partir do nome e do SIAPE.
	 * 
	 * @param nome
	 * @param siape
	 * @param ativos
	 * @param categoria
	 * @return
	 * @throws DAOException
	 */
	public Collection<Servidor> findByNomeSiape(String nome, int siape, boolean ativos, int categoria) throws DAOException {
		try {
			StringBuilder hql = new StringBuilder( " SELECT new Servidor(s.id, s.pessoa.nome, s.siape) " );
			hql.append( " FROM Servidor s WHERE 1 = 1" );
			
			if(nome != null) {
			    hql.append(" AND ");
			    hql.append( UFRNUtils.toAsciiUpperUTF8("s.pessoa.nomeAscii") );
			    hql.append( " LIKE " );
			    hql.append( UFRNUtils.toAsciiUTF8( "'" + nome.toUpperCase() + "%'" ) );
			}
		
			if ( ativos ){
				hql.append( " AND s.ativo =  " );
				hql.append( Ativo.SERVIDOR_ATIVO );
			}

			if ( categoria > 0 ) 
				hql.append( " AND s.categoria.id = :categoria " );
			
			if ( siape > 0 ) 
				hql.append( " AND s.siape = :siape " );
			
			hql.append( " GROUP BY s.id, s.pessoa.nome, s.siape ORDER BY s.pessoa.nome " );

			Query q = getSession().createQuery(hql.toString());
			
			if ( categoria > 0 )
				q.setInteger("categoria", categoria);
			
			if ( siape > 0 ) 
			    q.setInteger("siape", siape);
			
			@SuppressWarnings("unchecked")
			Collection<Servidor> lista = q.list();
			
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Método que retorna um Collection de servidores do SIGAA com o nome da Unidade na qual ele pertence a partir do nome e do SIAPE do mesmo.
	 * 
	 * @param nome
	 * @param siape
	 * @param ativos
	 * @param categoria
	 * @return
	 * @throws DAOException
	 */
	public Collection<Servidor> findServidorUnidadeByNomeSiape(String nome, Integer siape, boolean ativos, Integer categoria) throws DAOException {
		try {
			String projecao = " s.id, s.pessoa.nome, s.siape, s.unidade.nome ";
			
			StringBuilder hql = new StringBuilder( " SELECT " );
			hql.append(projecao);
			hql.append( " FROM Servidor s JOIN s.unidade WHERE 1 = 1" );
			
			if(nome != null) {
			    hql.append(" AND ");
			    hql.append( UFRNUtils.convertUtf8UpperLatin9("s.pessoa.nomeAscii") );
			    hql.append( " LIKE '" );
			    hql.append( UFRNUtils.trataAspasSimples(StringUtils.toAscii(nome.toUpperCase())) );
			    hql.append("%'");
			}
		
			if ( ativos ){
				hql.append( " AND s.ativo =  " );
				hql.append( Ativo.SERVIDOR_ATIVO );
			}

			if ( isNotEmpty(categoria) ) 
				hql.append( " AND s.categoria.id = :categoria " );
			
			if ( isNotEmpty(siape) ) 
				hql.append( " AND s.siape = :siape " );
			
			hql.append( " GROUP BY s.id, s.pessoa.nome, s.siape, s.unidade.nome ORDER BY s.pessoa.nome " );

			Query q = getSession().createQuery(hql.toString());
			
			if ( isNotEmpty(categoria) )
				q.setInteger("categoria", categoria);
			
			if ( isNotEmpty(siape) ) 
			    q.setInteger("siape", siape);
			
			@SuppressWarnings("unchecked")
			List<Object[]> lista = q.list();
			
			return HibernateUtils.parseTo(lista, projecao, Servidor.class, "s");
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna todos os docentes não externos que já participaram de uma unidade.
	 * 
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Servidor> findDocenteNaoExternosByUnidade ( Unidade unidade ) throws HibernateException, DAOException {
	
		String sql = " select p.nome , s.id_servidor from rh.servidor s "+
						" inner join comum.pessoa p on p.id_pessoa = s.id_pessoa "+
						" where s.id_unidade = "+unidade.getId()+" and s.id_cargo in ("+Cargo.PROFESSOR_DO_MAGISTERIO_SUPERIOR+","+Cargo.DOCENTE_SUPERIOR_EFETIVO+","+Cargo.DOCENTE_SUPERIOR_SUBSTITUTO+") "+
						" and s.data_desligamento is null " +
						" and s.id_servidor not in  "+
						"( "+
						"	select s.id_servidor from rh.servidor s "+
						"	inner join comum.pessoa p on p.id_pessoa = s.id_pessoa "+
						"	inner join ensino.docente_externo d on d.id_servidor = s.id_servidor  "+
						"	where s.id_unidade = "+unidade.getId()+
						") "+
						" group by p.nome , s.id_servidor "+
						" order by p.nome , s.id_servidor ";
		
		List<Object []> rs =  getSession().createSQLQuery (sql).list();
		
		List <Servidor> docentes = new ArrayList<Servidor>();
		if ( rs != null )
			for (Object [] r : rs) {
				
				int i = 0;
				
				Servidor s = new Servidor();
				Pessoa p = new Pessoa();
				
				p.setNome((String) r[i++]);
				s.setPessoa(p);
				
				s.setId((Integer) r[i++]);
				
				docentes.add(s);
			}
		return docentes;
		
	}
}