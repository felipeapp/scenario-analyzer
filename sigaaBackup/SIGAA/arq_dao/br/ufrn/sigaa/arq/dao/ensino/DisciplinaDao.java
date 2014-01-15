/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 18/09/2006
 *
 */	
package br.ufrn.sigaa.arq.dao.ensino;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.FormaParticipacaoAtividade;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;

/**
 * DAO responsável por gerenciar o acesso aos dados do componente curricular
 * 
 * @author amdantas
 *
 */
public class DisciplinaDao extends GenericSigaaDAO {

	public DisciplinaDao() {
	}

	/**
	 * Busca por nome, filtrando por unidade e tipo de ensino da disciplina. Para buscar apenas por tipo de ensino,
	 * basta passar o ID da unidade menor ou igual a Zero.
	 *
	 * @param nome
	 * @param unid
	 * @param nivelEnsino
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	public Collection<ComponenteCurricular> findByNome(String nome, int unid, char nivelEnsino, PagingInformation paging, boolean buscarSubunidades, boolean formaColetiva, int... tipos)
			throws DAOException, LimiteResultadosException {
		return findByNome(nome, unid, 0, nivelEnsino, true, paging, buscarSubunidades, formaColetiva,tipos);
	}
	
	/**
	 * Busca por nome, filtrando por unidade, curso e tipo de ensino da disciplina. Para buscar apenas por tipo de ensino,
	 * basta passar o ID da unidade menor ou igual a Zero.
	 *
	 * @param nome
	 * @param unid
	 * @param curso
	 * @param nivelEnsino
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	public Collection<ComponenteCurricular> findByNomeCursoLato(String nome, int unid, int curso, char nivelEnsino, PagingInformation paging, boolean buscarSubunidades, int... tipos)
			throws DAOException, LimiteResultadosException {
		return findByNome(nome, unid, curso, nivelEnsino, true, paging, buscarSubunidades,false, tipos);
	}


	/**
	 * Busca os componentes curriculares do tipo ATIVIDADE pelo seu nome e nível de ensino.
	 * Opcionalmente é possível paginar a consulta.
	 * 
	 * @param nome
	 * @param nivelEnsino
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> findByNomeAtividadeAtivos(String nome, char nivelEnsino,PagingInformation paging) throws DAOException{

		try {
			StringBuffer hql = new StringBuffer();
			hql.append( "select cc.id, cc.detalhes.codigo, cc.detalhes.nome, cc.detalhes.chTotal, cc.nivel, cc.blocoSubUnidade.id" );
			hql.append(" from ComponenteCurricular cc where cc.ativo = trueValue() and ");
			hql.append(" tipoComponente.id = " + TipoComponenteCurricular.ATIVIDADE + " and ");
			if (nivelEnsino != 0)
				hql.append("nivel = '" + nivelEnsino + "' and ");
			if (nome != null)
				hql.append(" upper(detalhes.nome_ascii)" + " like "
						+ "upper('%" + UFRNUtils.trataAspasSimples(nome.toUpperCase().trim()) + "%')");
			hql.append(" and ativo= trueValue()");
			hql.append(" order by detalhes.nome asc");

			Query q = getSession().createQuery(hql.toString());
			preparePaging(paging, q);
			q = getSession().createQuery(hql.toString());

			if( paging == null ){
				int totalResult = count(q);
				if (totalResult > 100)
					throw new LimiteResultadosException();
			}

			Collection<ComponenteCurricular> retorno = new ArrayList<ComponenteCurricular>();
			
			@SuppressWarnings("unchecked")
			List<Object[]> lista = q.list();
			
			for (Object[] object : lista) {
				Integer id = (Integer) object[0];
				String name = (String) object[1];
				String cod = (String) object[2];
				Integer ch = (Integer) object[3];
				char nivel =  (Character) object[4];
				Integer sub = (Integer) object[5];
				ComponenteCurricular cc;
				if( sub == null )
					cc = new ComponenteCurricular( id, name, cod, ch, nivel);
				else
					cc = new ComponenteCurricular( id, name, cod, ch, nivel, sub );
				retorno.add(cc);
			}

			return retorno;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}


	/**
	 * Busca os componentes curriculares por nome e um conjunto de parâmetros opcionais
	 * 
	 * @param nome
	 * @param unid
	 * @param idCursoLato
	 * @param nivelEnsino
	 * @param ativo
	 * @param paging
	 * @param buscarSubunidades
	 * @param tipos
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	public Collection<ComponenteCurricular> findByNome(String nome, int unid, int idCursoLato, char nivelEnsino, Boolean ativo, PagingInformation paging, boolean buscarSubunidades, boolean formaColetiva, int... tipos)
			throws DAOException, LimiteResultadosException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append( "select cc.id, cc.detalhes.codigo, cc.detalhes.nome, cc.detalhes.chTotal, cc.nivel, cc.blocoSubUnidade.id, " );
			hql.append(" cc.detalhes.crAula + cc.detalhes.crLaboratorio + cc.detalhes.crEstagio as crTotal, cc.tipoComponente.id, cc.tipoComponente.descricao ");
			hql.append(" from ComponenteCurricular cc where ");
			if (nome != null)
				hql.append("cc.detalhes.nome = '" + nome + "' and ");
			if (unid > 0)
				hql.append("unidade.id = " + unid + " and ");
			if (idCursoLato > 0)
				hql.append("cc.id in (select ccl.disciplina.id from ComponenteCursoLato ccl where ccl.cursoLato.id = " + idCursoLato + ") and ");
			if (nivelEnsino != 0)
				hql.append("nivel = '" + nivelEnsino + "'  ");
			if (ativo != null)
				hql.append(" and ativo= " + ativo);
			
			if (!ValidatorUtil.isEmpty(tipos)) {				
				Integer aux = 1;
				hql.append(" and ( ");
				for(Integer tipoC : tipos) {
					if(tipoC == TipoComponenteCurricular.ATIVIDADE && formaColetiva) {
						hql.append(" ( tipoComponente.id = " + tipoC + " and formaParticipacao.id = " + FormaParticipacaoAtividade.ESPECIAL_COLETIVA + " ) ");
					} else {
						hql.append(" ( tipoComponente.id = " + tipoC + " ) ");
					}
					
					if(aux < tipos.length) {
						hql.append(" or ");
					} else {
						hql.append(" ) ");
					}
					
					aux++;
					
				}				
			}
			if( !buscarSubunidades )
				hql.append( " AND blocoSubUnidade.id is null" );
			hql.append(" order by cc.detalhes.nome asc");

			Query q = getSession().createQuery(hql.toString());
			preparePaging(paging, q);
			q = getSession().createQuery(hql.toString());

			if( paging == null ){
				int totalResult = count(q);
				if (totalResult > 100)
					throw new LimiteResultadosException();
			}

			Collection<ComponenteCurricular> retorno = new ArrayList<ComponenteCurricular>();
			
			@SuppressWarnings("unchecked")
			List<Object[]> lista = q.list();
			
			for (Object[] object : lista) {
				Integer id = (Integer) object[0];
				String name = (String) object[1];
				String cod = (String) object[2];
				Integer ch = (Integer) object[3];
				char nivel =  (Character) object[4];
				Integer sub = (Integer) object[5];
				Integer crTotal = (Integer) object[6];
				Integer idTipoComponente = (Integer) object[7];
				String descricaoTipoComponente = (String) object[8];
				TipoComponenteCurricular tipo = new TipoComponenteCurricular();
				tipo.setId(idTipoComponente);
				tipo.setDescricao(descricaoTipoComponente);
				ComponenteCurricular cc;
				if( sub == null )
					cc = new ComponenteCurricular( id, name, cod, ch, nivel, crTotal, tipo);
				else
					cc = new ComponenteCurricular( id, name, cod, ch, nivel, sub );
				retorno.add(cc);
			}

			return retorno;
		}catch(LimiteResultadosException e){
			throw new LimiteResultadosException(e);
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca por código, filtrando por unidade e tipo de ensino da disciplina. Para buscar apenas por tipo de ensino,
	 * basta passar o ID da unidade menor ou igual a Zero.
	 *
	 * @param codigo
	 * @param unid
	 * @param nivelEnsino
	 * @return
	 * @throws DAOException
	 */
	public ComponenteCurricular findByCodigo(String codigo, int unid, char nivelEnsino) throws DAOException {
		return findByCodigo(codigo, unid, 0, nivelEnsino, true);
	}
	
	/**
	 * Busca por código, filtrando por unidade, curso e tipo de ensino da disciplina. Para buscar apenas por tipo de ensino,
	 * basta passar o ID da unidade menor ou igual a Zero.
	 *
	 * @param codigo
	 * @param unid
	 * @param idCursoLato
	 * @param nivelEnsino
	 * @return
	 * @throws DAOException
	 */
	public ComponenteCurricular findByCodigoCursoLato(String codigo, int unid, int idCursoLato, char nivelEnsino) throws DAOException {
		return findByCodigo(codigo, unid, idCursoLato, nivelEnsino, true);
	}

	/**
	 * Busca um componente curricular pelo seu código e um conjunto de parâmetros que ajudam a refinar a consulta
	 * 
	 * @param codigo
	 * @param unid
	 * @param idCursoLato
	 * @param nivelEnsino
	 * @param ativo
	 * @return
	 * @throws DAOException
	 */
	public ComponenteCurricular findByCodigo(String codigo, int unid, int idCursoLato, char nivelEnsino, Boolean ativo) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("from ComponenteCurricular cc where ");
			if (codigo != null)
				hql.append("codigo = '" + codigo + "' and ");
			if (unid > 0)
				hql.append("unidade.id = " + unid + " and ");
			if (idCursoLato > 0)
				hql.append("cc.id in (select ccl.disciplina.id from ComponenteCursoLato ccl where ccl.cursoLato.id = " + idCursoLato + ") and ");
			hql.append("nivel = '" + nivelEnsino + "'  ");
			if (ativo != null)
				hql.append(" and ativo=" + ativo);
			hql.append(" order by detalhes.nome asc");

			Query q = getSession().createQuery(hql.toString());
			
			@SuppressWarnings("unchecked")
			Collection<ComponenteCurricular> disciplinas = q.list();
			
			if (disciplinas != null && disciplinas.size() > 0)
				return disciplinas.iterator().next();
			return null;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca os componentes curriculares que possuem o início do código semelhante 
	 * aquele informado.
	 * 
	 * @param codigo
	 * @param unid
	 * @param nivelEnsino
	 * @param buscarSubunidades
	 * @param tipos
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	public Collection<ComponenteCurricular> findByCodigoLike(String codigo, int unid, char nivelEnsino, boolean buscarSubunidades, boolean formaColetiva, int... tipos) throws DAOException, LimiteResultadosException {
			StringBuffer hql = new StringBuffer();
			hql.append( "select cc.id, cc.detalhes.codigo, cc.detalhes.nome, cc.detalhes.chTotal, cc.nivel, cc.blocoSubUnidade.id, " );
			hql.append(" cc.detalhes.crAula + cc.detalhes.crLaboratorio + cc.detalhes.crEstagio as crTotal, cc.tipoComponente.id, cc.tipoComponente.descricao ");
			hql.append(" from ComponenteCurricular cc where ");
			hql.append("nivel = '" + nivelEnsino + "' and ativo=trueValue() and ");
			if (unid > 0)
				hql.append("unidade.id = " + unid + " and ");
			
			if (!ValidatorUtil.isEmpty(tipos)) {				
				Integer aux = 1;
				hql.append(" ( ");
				for(Integer tipoC : tipos) {
					if(tipoC == TipoComponenteCurricular.ATIVIDADE && formaColetiva) {
						hql.append(" ( tipoComponente.id = " + tipoC + " and formaParticipacao.id = " + FormaParticipacaoAtividade.ESPECIAL_COLETIVA + " ) ");
					} else {
						hql.append(" ( tipoComponente.id = " + tipoC + " ) ");
					}
					
					if(aux < tipos.length) {
						hql.append(" or ");
					} else {
						hql.append(" ) and  ");
					}
					
					aux++;
					
				}				
			}
			
			if( !buscarSubunidades )
				hql.append( " blocoSubUnidade.id is null and" );
			hql.append(UFRNUtils.toAsciiUpperUTF8("detalhes.codigo") + " like" + UFRNUtils.toAsciiUpperUTF8("'" + UFRNUtils.trataAspasSimples(codigo) + "%'"));
			hql.append( " and ativo = trueValue() " );
			hql.append("order by detalhes.nome asc");

			Query q = getSession().createQuery(hql.toString());

			int totalResult = 0;
			try {
				totalResult = count(q);
			} catch (Exception e) {
				throw new DAOException(e.getMessage(), e);
			}

			if (totalResult > 100)
				throw new LimiteResultadosException();

			Collection<ComponenteCurricular> retorno = new ArrayList<ComponenteCurricular>();
			
			@SuppressWarnings("unchecked")
			List<Object[]> lista = q.list();
			
			for (Object[] object : lista) {
				Integer id = (Integer) object[0];
				String name = (String) object[1];
				String cod = (String) object[2];
				Integer ch = (Integer) object[3];
				char nivel =  (Character) object[4];
				Integer sub = (Integer) object[5];
				Integer crTotal = (Integer) object[6];
				Integer idTipoComponente = (Integer) object[7];
				String descricaoTipoComponente = (String) object[8];
				TipoComponenteCurricular tipo = new TipoComponenteCurricular();
				tipo.setId(idTipoComponente);
				tipo.setDescricao(descricaoTipoComponente);
				ComponenteCurricular cc;
				if( sub == null )
					cc = new ComponenteCurricular( id, name, cod, ch, nivel, crTotal, tipo);
				else
					cc = new ComponenteCurricular( id, name, cod, ch, nivel, sub );
				retorno.add(cc);
			}

			return retorno;
	}

	/**
	 * Busca uma lista de componentes curriculares por um parâmetro que pode ser seu nome ou seu código e nível de ensino.
	 * Opcionalmente a unidade do componente pode ser informada.
	 * 
	 * @param parametro
	 * @param unid
	 * @param nivelEnsino
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	public Collection<ComponenteCurricular> findByNomeOuCodigo(String parametro, int unid, char nivelEnsino) throws DAOException, LimiteResultadosException {
		return findByNomeOuCodigo(parametro, unid, nivelEnsino, true);
	}
	
	/**
	 * Busca uma lista de componentes curriculares por um parâmetro que pode ser seu nome ou seu código e nível de ensino.
	 * Opcionalmente a unidade do componente pode ser informada.
	 * 
	 * @param parametro
	 * @param unid
	 * @param nivelEnsino
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	public Collection<ComponenteCurricular> findByNomeOuCodigo(String parametro, int unid, char nivelEnsino, Boolean statusAtivo) throws DAOException, LimiteResultadosException {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append(  " FROM ensino.componente_curricular comp " +
					" join ensino.componente_curricular_detalhes det on (comp.id_detalhe = det.id_componente_detalhes) " +
					" join comum.unidade u using(id_unidade) " +
					" WHERE 1=1");
			if (statusAtivo != null && statusAtivo == true)
				sql.append(" and comp.ativo = ? ");
			if (nivelEnsino != ' ')
				sql.append(" and comp.nivel = ? ");
			if (unid > 0)
				sql.append(" and comp.id_unidade = ?");
			if (parametro != null && parametro.length() > 0) {
				sql.append(" and ( ");
				sql.append(" det.nome_ascii like ? ");
				sql.append(" or det.codigo = ? ) ");
			}

			con = Database.getInstance().getSigaaConnection();

			sql.append(" ORDER BY det.codigo asc, det.nome asc");
			st = con.prepareStatement("SELECT comp.id_disciplina, det.codigo, det.nome, det.ch_total, comp.nivel, u.sigla, det.proibe_aproveitamento " + sql);

			int count = 0;
			
			if (statusAtivo != null && statusAtivo == true)
				st.setBoolean(++count, statusAtivo);
			if (nivelEnsino != ' ')
				st.setString(++count, String.valueOf(nivelEnsino));
			
			if (unid > 0)
				st.setInt(++count, unid);
			if (parametro != null && parametro.length() > 0) {
				st.setString(++count, StringUtils.toAsciiAndUpperCase(  parametro.trim() ) + "%");
				st.setString(++count, StringUtils.toAsciiAndUpperCase(  parametro.trim() ));
			}
			rs = st.executeQuery();
			
			Collection<ComponenteCurricular> componentes = new ArrayList<ComponenteCurricular>(0);
			while(rs.next()) {
				ComponenteCurricular comp = new ComponenteCurricular(rs.getInt("ID_DISCIPLINA"));
				comp.setCodigo(rs.getString("codigo"));
				comp.getDetalhes().setCodigo(rs.getString("codigo"));
				comp.getDetalhes().setProibeAproveitamento(rs.getBoolean("proibe_aproveitamento"));
				comp.setNome(rs.getString("nome"));
				comp.setChTotal(rs.getInt("ch_total"));
				comp.setNivel(rs.getString("nivel").charAt(0));
				comp.setAtivo(false);
				
				Unidade u = new Unidade();
				u.setSigla(rs.getString("sigla"));
				comp.setUnidade(u);
				
				componentes.add(comp);
				
			}

			return componentes;
		} catch(Exception e) {
			throw new DAOException(e);
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
		}
	}

	/**
	 * Retorna todas as disciplinas, filtrando por unidade e tipo de ensino . Para buscar apenas por tipo de ensino,
	 * basta passar o ID da unidade menor ou igual a Zero.
	 *
	 * @param unid
	 * @param nivelEnsino
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> findAll(int unid, char nivelEnsino, PagingInformation paging) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("from ComponenteCurricular where ");
			if (unid > 0)
				hql.append("unidade.id = " + unid + " and ");
			hql.append("nivel = '" + nivelEnsino + "' ");
			hql.append( " and ativo = trueValue() " );
			hql.append("order by detalhes.nome asc");

			Query q = getSession().createQuery(hql.toString());
			preparePaging(paging, q);
			
			@SuppressWarnings("unchecked")
			List<ComponenteCurricular> lista = q.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca todos os componentes curriculares pelo tipo,modalidade, unidade, curso e nível de ensino.
	 * Opcionalmente o resultado pode ser paginado.
	 * 
	 * @param tipo
	 * @param unid
	 * @param idCursoLato
	 * @param nivelEnsino
	 * @param paging
	 * @param modalidade 
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> findByTipo(int tipo, int unid, int idCursoLato, char nivelEnsino, String codigoComponente, PagingInformation paging, int modalidade) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("from ComponenteCurricular cc where ");
			if (unid > 0)
				hql.append("unidade.id = :unid and ");
			if( tipo > 0 )
				hql.append("tipoComponente = :tipo and ");
			if( modalidade > 0 )
				hql.append("modalidadeEducacao = :modalidade and ");
			if (idCursoLato > 0)
				hql.append("cc.id in (select ccl.disciplina.id from ComponenteCursoLato ccl where ccl.cursoLato.id = :idCursoLato ) and ");
			if (!isEmpty(codigoComponente))
				hql.append("cc.codigo = :codigoComponente and ");
			hql.append("ativo = true and ");
			hql.append("nivel = :nivelEnsino ");
			hql.append(" order by detalhes.nome asc");

			Query q = getSession().createQuery(hql.toString());
			
			if (unid > 0)
				q.setInteger("unid", unid);
			if( tipo > 0 )
				q.setInteger("tipo", tipo);
			if( modalidade > 0 )
				q.setInteger("modalidade", modalidade);
			if (idCursoLato > 0)
				q.setInteger("idCursoLato", idCursoLato);
			if (!isEmpty(codigoComponente))
				q.setString("codigoComponente", codigoComponente);
			q.setCharacter("nivelEnsino", nivelEnsino);
			
			@SuppressWarnings("unchecked")
			Collection<ComponenteCurricular> disciplinas = q.list();
			
			return disciplinas;
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}


	/**
	 * Realiza uma consulta de componentes curriculares de acordo com uma série de parâmetros que os identificam
	 * 
	 * @param codigo
	 * @param nome
	 * @param tipo
	 * @param unidade
	 * @param nivel
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	public Collection<ComponenteCurricular> findCompleto(String codigo, String nome, TipoComponenteCurricular tipo, Unidade unidade,
			char nivel, boolean filtroTotal) throws DAOException, LimiteResultadosException {
		return findCompleto(codigo, nome, null, null, null, null, null, null, tipo, unidade, nivel, false, true, filtroTotal);
	}

	/**
	 * Busca componente curriculares a partir de vários atributos importantes de uma só vez
	 *
	 * @param codigo
	 * @param nome
	 * @param modalidade
	 * @param tipo
	 * @param unidade
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> findCompleto(String codigo, String nome, String preRequisito, String coRequisito, 
			String equivalencia, Date dataInicio, Date dataFim, ModalidadeEducacao modalidade, TipoComponenteCurricular tipo, Unidade unidade, 
			char nivel, boolean formatoRelatorio, boolean apenasAtivos, Boolean filtroTotal, int... tiposValidos) throws DAOException, LimiteResultadosException {
		try {
			StringBuffer hql = new StringBuffer();
			String projecao = " cc.id, cc.codigo, cc.detalhes.id, cc.detalhes.nome, cc.detalhes.chTotal, " +
					" cc.detalhes.crAula, cc.detalhes.crEstagio, cc.detalhes.crLaboratorio, cc.detalhes.crEad, cc.nivel, " +
					" cc.tipoComponente.id, cc.tipoComponente.descricao, cc.ativo, " +
					" tipoAtividade.id as cc.tipoAtividade.id, tipoAtividade.descricao as cc.tipoAtividade.descricao," +
					" formaParticipacao.id as cc.formaParticipacao.id, formaParticipacao.descricao as cc.formaParticipacao.descricao," +
					" cc.modalidadeEducacao.id, cc.modalidadeEducacao.descricao";
			String count = "count(cc.id)";
			hql.append(" from ComponenteCurricular cc" +
					" inner join cc.detalhes detalhes" +
					" left join cc.tipoAtividade tipoAtividade" +
					" left join cc.formaParticipacao formaParticipacao" +
					" where 1 = 1 ");
			if ( nivel != ' ')
				hql.append("and cc.nivel = '" + nivel + "' ");
			if (codigo != null)
				hql.append(" and cc.codigo = :codigo ");
			if (preRequisito != null)
				hql.append(" and detalhes.preRequisito like :preRequisito ");
			if (coRequisito != null)
				hql.append(" and detalhes.coRequisito like :coRequisito ");
			if (equivalencia != null)
				hql.append(" and detalhes.equivalencia like :equivalencia ");
			if (tipo != null && tipo.getId() > 0)
				hql.append(" and cc.tipoComponente.id = " + tipo.getId());
			if (modalidade != null && modalidade.getId() > 0)
				hql.append(" and cc.modalidadeEducacao.id = " + modalidade.getId());
			if (unidade != null && unidade.getId() > 0)
				hql.append(" and cc.unidade.id =  " + unidade.getId());
			if (nome != null)
				hql.append(" and upper(detalhes.nome_ascii) like :nome");
			if( apenasAtivos ){
				hql.append( " and cc.ativo = trueValue() " );
			}
			if (tiposValidos != null && tiposValidos.length > 0) {
				hql.append(" and cc.tipoComponente.id in " + UFRNUtils.gerarStringIn(tiposValidos));
			}
			String orderBy = " order by detalhes.nome asc";

			Query q = getSession().createQuery("select " + count + hql);
			if (nome != null)
				q.setString("nome", "%" + StringUtils.toAscii(nome.trim().toUpperCase()) + "%");
			if (codigo != null)
				q.setString("codigo", codigo);
			if (preRequisito != null)
				q.setString("preRequisito", "%" + preRequisito + "%");
			if (coRequisito != null)
				q.setString("coRequisito", "%" + coRequisito + "%");
			if (equivalencia != null)
				q.setString("equivalencia", "%" + equivalencia + "%");
						
			Long qtd = (Long) q.uniqueResult();
			// definindo limite padrão
			int limite = 750;
			if (!filtroTotal && !formatoRelatorio) 
				limite = (int) (qtd + 1);
			// caso a consulta for em formato relatório aumentar o limite
			if(formatoRelatorio)
				limite = 2000;
			if (qtd > limite)
				throw new LimiteResultadosException();

			Query result = getSession().createQuery("select " + HibernateUtils.removeAliasFromProjecao(projecao) + hql + orderBy);
			if (nome != null)
				result.setString("nome", "%" + StringUtils.toAscii(nome.trim().toUpperCase()) + "%");
			if (codigo != null)
				result.setString("codigo", codigo);
			if (preRequisito != null)
				result.setString("preRequisito", "%" + preRequisito + "%");
			if (coRequisito != null)
				result.setString("coRequisito", "%" + coRequisito + "%");
			if (equivalencia != null)
				result.setString("equivalencia", "%" + equivalencia + "%");
			if (!filtroTotal) {
				result.setMaxResults(2000);
			}
			
			@SuppressWarnings("unchecked")
			Collection<ComponenteCurricular> lista = HibernateUtils.parseTo(result.list(), projecao, ComponenteCurricular.class, "cc");
			
			if ( !ValidatorUtil.isEmpty(dataInicio)  ){
				List<ComponenteCurricular> equivalencias = (List<ComponenteCurricular>) findByPeriodoEquivalencia(equivalencia, dataInicio, dataFim);
			
				for (ComponenteCurricular c : equivalencias) {
					if(!lista.contains(c))
						lista.add(c);
				}
			}
			
			return lista;

		} catch (LimiteResultadosException le) {
			throw le;
		}catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Método responsável por retornar a coleção de componentes curriculares que possuam a equivalência informada 
	 * por parâmetro num determinado período de tempo.
	 * @param equivalencia
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ComponenteCurricular> findByPeriodoEquivalencia(String equivalencia, Date dataInicio, Date dataFim) throws DAOException {
		
		try {
			Query query = getSession().createQuery(
					"select new ComponenteCurricular(cc.id, cc.codigo, cc.detalhes.nome, cc.detalhes.chTotal,  " +
					"	cc.detalhes.crAula + cc.detalhes.crEstagio + cc.detalhes.crLaboratorio, cc.nivel, cc.tipoComponente.descricao, cc.ativo)" +
					" from ComponenteCurricular cc, ComponenteDetalhes cd" +
					" where "
							+ " cc.id = cd.componente and " 
							+ " (cd.data between :dataInicio and :dataFim) and "
							+ " cd.equivalencia like :equivalencia ");
							
			query.setString("equivalencia", "%" + equivalencia + "%");
			query.setTimestamp("dataInicio", dataInicio);
			query.setTimestamp("dataFim", dataFim);
			
			return query.list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
}