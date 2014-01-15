/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 29/10/2008 
 * 
 */
package br.ufrn.sigaa.ava.cv.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static org.hibernate.criterion.Order.asc;
import static org.hibernate.criterion.Restrictions.eq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.dao.dialect.SQLDialect;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.dominio.RegistroAtividadeTurma;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.cv.dominio.ComunidadeVirtual;
import br.ufrn.sigaa.cv.dominio.ConfiguracoesComunidadeVirtual;
import br.ufrn.sigaa.cv.dominio.ConteudoComunidade;
import br.ufrn.sigaa.cv.dominio.EnqueteComunidade;
import br.ufrn.sigaa.cv.dominio.EnqueteRespostaComunidade;
import br.ufrn.sigaa.cv.dominio.EnqueteVotosComunidade;
import br.ufrn.sigaa.cv.dominio.ForumComunidade;
import br.ufrn.sigaa.cv.dominio.IndicacaoReferenciaComunidade;
import br.ufrn.sigaa.cv.dominio.MembroComunidade;
import br.ufrn.sigaa.cv.dominio.NoticiaComunidade;
import br.ufrn.sigaa.cv.dominio.SolicitacaoParticipacaoComunidade;
import br.ufrn.sigaa.cv.dominio.TipoComunidadeVirtual;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO responsável pela camada de persistência da entidade {@link ComunidadeVirtual}
 * 
 * @author David Pereira
 * @author agostinho campos
 *
 */
public class ComunidadeVirtualDao extends GenericSigaaDAO {

	/**
	 * Localiza as comunidades que uma pessoa faz parte
	 * @param pessoa
	 * @return
	 * @throws DAOException 
	 */
	public List<ComunidadeVirtual> findComunidadesComProjecaoByPessoa(Usuario usuario) throws DAOException {
		Query query = getSession().createQuery(
				" select distinct c.id, c.ativa, c.nome, t.descricao" +
				" from MembroComunidade mc " +
				" left join mc.comunidade c " +
				" join c.tipoComunidadeVirtual t" +
				" where mc.pessoa.id = ? and c.ativa = trueValue() and mc.ativo = trueValue() " +
				" order by c.nome ")
				.setInteger(0, usuario.getPessoa().getId());
		@SuppressWarnings("unchecked")
		List<Object[]> rs = query.list();
		List<ComunidadeVirtual> result = new LinkedList<ComunidadeVirtual>();
		for (Object[] linha : rs) {
			ComunidadeVirtual cv = new ComunidadeVirtual();
			cv.setId((Integer) linha[0]);
			cv.setAtiva((Boolean) linha[1]);
			cv.setNome((String) linha[2]);
			cv.getTipoComunidadeVirtual().setDescricao((String)linha[3]);
			
			result.add(cv);
		}
		return result;
	}
	
	/**
	 * Localiza as comunidades que uma pessoa faz parte
	 * @param pessoa
	 * @return
	 * @throws DAOException 
	 */
	public List<ComunidadeVirtual> findComunidadesByPessoa(Usuario usuario) throws DAOException {
		Query query = getSession().createQuery(
				" select distinct c from MembroComunidade mc left join mc.comunidade c " +
				" where mc.pessoa.id = ? and c.ativa = trueValue() " +
				" order by c.descricao ")
				.setInteger(0, usuario.getPessoa().getId());
		@SuppressWarnings("unchecked")
		List<ComunidadeVirtual> rs = query.list();
		return rs;
	}
	
	/**
	 * Localiza as comunidades que uma pessoa faz parte
	 * @param pessoa
	 * @return
	 * @throws DAOException 
	 */
	public boolean isMembroComunidade(Usuario usuario) throws DAOException {
		Query query = getSession().createQuery(
				" select distinct c.id from MembroComunidade mc left join mc.comunidade c " +
				" where mc.pessoa.id = ? and c.ativa = " + SQLDialect.TRUE)
				.setInteger(0, usuario.getPessoa().getId());
		query.setMaxResults(1);
		@SuppressWarnings("unchecked")
		List<ComunidadeVirtual> rs = query.list();
		return rs.size() > 0;
	}
	
	/**
	 * Retorna os tipos de comunidade virtual (Pública, Moderada, etc). Se o parâmetro estiver false
	 * não retorna o Tipo de Comunidade Privada.
	 *  
	 * 
	 * @param pessoa
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<TipoComunidadeVirtual> findTiposComunidadeVirtual(boolean exibeTipoPrivado) throws DAOException {
		StringBuilder hql = new StringBuilder(); 
		
		if (!exibeTipoPrivado)
			hql.append("select t from TipoComunidadeVirtual t where t != " + TipoComunidadeVirtual.PRIVADA);
		else
			hql.append("select t from TipoComunidadeVirtual t");
		
		return getSession().createQuery(hql.toString()).list();
	}

	/**
	 * Busca as comunidades pelo título
	 * 
	 * @param nomeComunidade
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public List<ComunidadeVirtual> findComunidadesByDescricaoTipo(String nomeComunidade, int idTipoComunidade, PagingInformation paging, Boolean permiteVisualizacaoExterna) throws Exception {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("select c from ComunidadeVirtual c where ");
		sb.append( UFRNUtils.toAsciiUpperUTF8("c.nome") );
		sb.append( " LIKE " );
		sb.append( UFRNUtils.toAsciiUpperUTF8("'%" + UFRNUtils.trataAspasSimples(nomeComunidade) + "%'") );
		
		if ( idTipoComunidade == TipoComunidadeVirtual.BUSCAR_TODOS_TIPOS_COMUNIDADE_VIRTUAL ) { // exceto comunidades privadas
			sb.append( " AND c.tipoComunidadeVirtual.id != " );
			sb.append( TipoComunidadeVirtual.PRIVADA );
			sb.append( " AND c.ativa = trueValue() " );
			
			if ( !isEmpty(permiteVisualizacaoExterna) ){
				if( permiteVisualizacaoExterna )
					sb.append(" AND c.permiteVisualizacaoExterna = trueValue()");
				else
					sb.append(" AND c.permiteVisualizacaoExterna = falseValue()");
			}
		}
		else {
			sb.append(" AND c.tipoComunidadeVirtual.id = " + idTipoComunidade + " and c.ativa = trueValue() ");
			
			if ( !isEmpty(permiteVisualizacaoExterna) ){	
				if ( permiteVisualizacaoExterna )
					sb.append(" AND c.permiteVisualizacaoExterna = trueValue() AND c.tipoComunidadeVirtual.id = " + idTipoComunidade);
				else
					sb.append(" AND c.permiteVisualizacaoExterna = falseValue() OR c.permiteVisualizacaoExterna = null AND c.tipoComunidadeVirtual.id = " + idTipoComunidade);
			}	
		}
			
		sb.append(" order by c.nome, c.dataCadastro ");

		Query q = getSession().createQuery(sb.toString());
		if(paging != null && q != null){
			paging.setTotalRegistros(count(q));
			q.setFirstResult(paging.getPaginaAtual() * paging.getTamanhoPagina());
			q.setMaxResults(paging.getTamanhoPagina());
		}
		return q.list();
	}

	/**
	 * Encontra o Fórum da Comunidade Virtual
	 * 
	 * @param comunidade
	 * @return
	 * @throws DAOException 
	 */
	public ForumComunidade findForumByComunidade(ComunidadeVirtual comunidade) throws DAOException {
		if (comunidade.getId() != 0)
			return (ForumComunidade) getSession().createCriteria(ForumComunidade.class).add(eq("comunidade", comunidade)).uniqueResult();
		else
			return new ForumComunidade();
	}

	/**
	 * Encontra as notícias  de uma Comunidade Virtual 
	 * 
	 * @param comunidade
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<NoticiaComunidade> findNoticiasByComunidade(ComunidadeVirtual comunidade) throws HibernateException, DAOException {
		Query q = getSession().createQuery("select nc from NoticiaComunidade nc where nc.comunidade.id = :idComunidade order by data desc");
		q.setInteger("idComunidade", comunidade.getId());
		@SuppressWarnings("unchecked")
		List<NoticiaComunidade> lista = q.list();
		return lista;
	}

	/**
	 * Encontra as Indicações de Referência de uma Comunidade Virtual 
	 * 
	 * @param comunidade
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<IndicacaoReferenciaComunidade> findReferenciasTurma(ComunidadeVirtual comunidade, boolean moderador, Usuario usuario) throws HibernateException, DAOException {
		
		String topicosVisiveis = "";
		if ( !moderador && usuario != null)
			topicosVisiveis = " AND ( r.topico.visivel = trueValue() OR r.topico.usuario.id = " +usuario.getId() + " ) ";
		else if ( !moderador && usuario == null )
			topicosVisiveis = " AND r.topico.visivel = trueValue() ";
		@SuppressWarnings("unchecked")
		List<IndicacaoReferenciaComunidade> lista = getSession().createQuery("SELECT r FROM IndicacaoReferenciaComunidade r " +
				" WHERE r.topico.comunidade.id= " + comunidade.getId() + topicosVisiveis + 
				" ORDER BY r.descricao"
				).list();
		return lista;
	}

	/**
	 * Encontra o Conteúdo da Comunidade Virtual 
	 * 
	 * @param comunidade
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<ConteudoComunidade> findConteudoComunidade(ComunidadeVirtual comunidade, boolean moderador, Usuario usuario) throws HibernateException, DAOException {
	
		String topicosVisiveis = "";
		if ( !moderador && usuario != null )
			topicosVisiveis = " AND ( c.topico.visivel = trueValue() OR c.topico.usuario.id = " +usuario.getId() + " ) ";
		else if (!moderador && usuario == null )
			topicosVisiveis = " AND c.topico.visivel = trueValue() ";
		
		Query q = getSession().createQuery("select c from ConteudoComunidade c " +
				" where c.topico.comunidade.id = :idComunidade " + topicosVisiveis +
				" order by c.titulo asc");
		
		q.setInteger("idComunidade", comunidade.getId());
		@SuppressWarnings("unchecked")
		List<ConteudoComunidade> lista = q.list();
		return lista;
	}

	/**
	 * Encontra o Conteúdo da Comunidade Virtual 
	 * 
	 * @param comunidade
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<ConteudoComunidade> findConteudoPermanenteByComunidade(ComunidadeVirtual comunidade, boolean moderador, Usuario usuario) throws HibernateException, DAOException {
	
		String topicosVisiveis = "";
		if ( !moderador && usuario != null )
			topicosVisiveis = " AND ( c.topico.visivel = trueValue() OR c.topico.usuario.id = " +usuario.getId() + " ) ";
		else if ( !moderador && usuario == null )
			topicosVisiveis = " AND c.topico.visivel = trueValue() ";
		
		Query q = getSession().createQuery("select c from ConteudoComunidade c " +
				"where c.topico.comunidade.id = :idComunidade " +
				" and c.permanente = trueValue() " + 
				topicosVisiveis
				);
		q.setInteger("idComunidade", comunidade.getId());
		@SuppressWarnings("unchecked")
		List<ConteudoComunidade> lista = q.list();
		return lista;
	}
	
	/**
	 * Retorna uma lista de membros da comunidade 
	 * 
	 * @param pessoasFromGrupoDestinatario
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<MembroComunidade> findUsuariosFromGrupoDestinatario(List<Usuario> pessoasFromGrupoDestinatario) {
		
		List<MembroComunidade> usuarios = new ArrayList<MembroComunidade>();
		if (pessoasFromGrupoDestinatario!=null) {
			
			for (Usuario usuario : pessoasFromGrupoDestinatario) {
				MembroComunidade membroComunidade = new MembroComunidade();
				
				Pessoa pessoa = new Pessoa(); pessoa.setId(usuario.getIdPessoa());				
				Usuario u = new Usuario(); u.setId(usuario.getId());
				
				membroComunidade.setAtivo(true); membroComunidade.setPermissao(MembroComunidade.MEMBRO);
				membroComunidade.setPessoa(pessoa); membroComunidade.setUsuario(usuario);
				usuarios.add(membroComunidade);
			}
		}
		
		for (MembroComunidade membro : usuarios) {
			try {
				Map map = getJdbcTemplate().queryForMap("select u.id_usuario, u.login, u.email, u.id_foto, p.id_pessoa as id_pessoa, p.nome as nome, p.cpf_cnpj as cpf_cnpj " +
														"from comum.usuario u " +
														"inner join comum.pessoa p on p.id_pessoa = u.id_pessoa " +
														"where u.id_usuario=? and inativo=falseValue() " + BDUtils.limit(1), new Object[] { membro.getUsuario().getId() });
				if (map != null) {
					
					int idPessoa = (Integer) map.get("id_pessoa"); 
					String nome = (String) map.get("nome");
					Long cpfCnpj = (Long) map.get("cpf_cnpj"); 
					
					membro.setUsuario(new Usuario());
					membro.setPessoa(new Pessoa(idPessoa, nome, cpfCnpj));
					membro.getUsuario().setId((Integer) map.get("id_usuario"));
					membro.getUsuario().setLogin((String) map.get("login"));
					membro.getUsuario().setEmail((String) map.get("email"));
					membro.getUsuario().setIdFoto((Integer) map.get("id_foto"));
				}
			} catch(EmptyResultDataAccessException e) {
				// Não existe usuário, não fazer nada.
			}
		}
		
		return usuarios;
	}
	
	/**
	 * Retorna todos os participantes da comunidade virtual.
	 * 
	 * @param comunidade
	 * @return
	 * @throws DAOException 
	 * @throws Exception 
	 * @throws DataAccessException 
	 */
	@SuppressWarnings("unchecked")
	public Set<MembroComunidade> findParticipantes(ComunidadeVirtual comunidade) throws DAOException  {
		String projecao = "id, pessoa.id, pessoa.nome, permissao, comunidade.id ";
		
		String hql = "select " + projecao +
				" from MembroComunidade where comunidade.id = ? and ativo = true" +
				" order by pessoa.nome";
		
		Collection<MembroComunidade> membros = Collections.EMPTY_LIST;
		try {
			membros = HibernateUtils.parseTo(getHibernateTemplate().find(hql, comunidade.getId()), projecao, MembroComunidade.class);
		} catch (Exception e) {
			throw new DAOException(e);
		}
		
		for (MembroComunidade membro : membros) {
			try {
				Map map = getJdbcTemplate().queryForMap("select u.id_usuario, u.login, u.email, u.id_foto, p.id_pessoa as id_pessoa, p.nome as nome, p.cpf_cnpj as cpf_cnpj " +
														"from comum.usuario u " +
														"inner join comum.pessoa p on p.id_pessoa = u.id_pessoa " +
														"where p.id_pessoa=? and inativo=falseValue() " + BDUtils.limit(1), new Object[] { membro.getPessoa().getId() });																		
				if (map != null) {
					
					int idPessoa = (Integer) map.get("id_pessoa"); 
					String nome = (String) map.get("nome");
					Long cpfCnpj = (Long) map.get("cpf_cnpj"); 
					
					membro.setUsuario(new Usuario());
					if (cpfCnpj != null)
						membro.setPessoa(new Pessoa(idPessoa, nome, cpfCnpj));
					else
						membro.setPessoa(new Pessoa(idPessoa, nome, new Long(0)));
					
					membro.getUsuario().setId((Integer) map.get("id_usuario"));
					membro.getUsuario().setLogin((String) map.get("login"));
					membro.getUsuario().setEmail((String) map.get("email"));
					membro.getUsuario().setIdFoto((Integer) map.get("id_foto"));
				}
			} catch(EmptyResultDataAccessException e) {
				// Não existe usuário, não fazer nada.
			}
		}
		
		return CollectionUtils.toSet(membros);
	}

	/**
	 * Retorna todos os participantes da comunidade virtual, paginando e ordenando se necessário.
	 * @param comunidade
	 * @param paging
	 * @param crescente
	 * @return
	 * @throws DAOException
	 */
	public List <MembroComunidade> findParticipantes (ComunidadeVirtual comunidade, PagingInformation paging, boolean crescente) throws DAOException  {
		
		List <MembroComunidade> membros = new ArrayList <MembroComunidade> ();
		
		try {
			String sql = "select m.id, m.id_pessoa, p.nome, m.permissao, id_comunidade from cv.membro_comunidade m join comum.pessoa p using (id_pessoa) where id_comunidade = :idComunidade and m.ativo = trueValue() order by p.nome " + (crescente ? "desc" : "");
			Query q = getSession().createSQLQuery(sql);
			q.setInteger("idComunidade", comunidade.getId());

			if (paging != null){
				Query count = getSession().createQuery("select count (*) from MembroComunidade where comunidade.id = " + comunidade.getId());
		 
				paging.setTotalRegistros(((Long) count.uniqueResult()).intValue());
				q.setFirstResult(paging.getPaginaAtual() * paging.getTamanhoPagina());
				q.setMaxResults(paging.getTamanhoPagina());
			}
		
			@SuppressWarnings("unchecked")
			List <Object []> rs = q.list();
			List <Integer> idsPessoas = new ArrayList <Integer> ();
			
			if (rs.size() > 0){
				for (Object [] l : rs){
					MembroComunidade m = new MembroComunidade();
					m.setId(((Number)l[0]).intValue());
					m.setPessoa(new Pessoa(((Number)l[1]).intValue()));
					m.getPessoa().setNome((String)l[2]);
					m.setPermissao(((Number)l[3]).intValue());
					m.setComunidade(new ComunidadeVirtual(((Number)l[4]).intValue()));
					
					membros.add(m);
					idsPessoas.add(m.getPessoa().getId());
				}
				
				String projecao = "id, login, email, idFoto, pessoa.id, pessoa.nome, pessoa.cpf_cnpj";
				String hql = "select " + projecao + " from Usuario where pessoa.id in " + UFRNUtils.gerarStringIn(idsPessoas) + " and inativo = falseValue()";
				
				@SuppressWarnings("unchecked")
				List <Usuario> usuarios = (List<Usuario>) HibernateUtils.parseTo(getHibernateTemplate().find(hql), projecao, Usuario.class);
				
				for (MembroComunidade membro : membros) {
					boolean encontrado = false;
					int i = 0;
					for (i = 0; i < usuarios.size(); i++)
						if (usuarios.get(i).getPessoa().getId() == membro.getPessoa().getId()){
							encontrado = true;
							break;
						}
					
					if (encontrado)
						membro.setUsuario(usuarios.remove(i));
				}
			}
				
		} catch (Exception e) {
			throw new DAOException(e);
		}
		
		return membros;
	}

	/**
	 * Localiza um participante de acordo com a Comunidade Virtual e a Pessoa
	 * 
	 * @param pessoa
	 * @return
	 * @throws DAOException 
	 */
	public MembroComunidade findMembroByPessoa(ComunidadeVirtual cv, Pessoa pessoa) throws DAOException {
		Criteria c = getSession().createCriteria(MembroComunidade.class);
		c.add(eq("comunidade", cv)).add(eq("pessoa", pessoa)).add(eq("ativo", true));
		c.setMaxResults(1);
		MembroComunidade mc = (MembroComunidade) c.uniqueResult();
		
		if (mc != null) {
			Usuario usr = (Usuario) getSession().createCriteria(Usuario.class).add(eq("pessoa", pessoa)).add(eq("inativo", false)).setMaxResults(1).uniqueResult();
			mc.setUsuario(usr);
		}
		
		return mc;
	}

	/**
	 * Encontra as Enquetes de uma Comunidade Virtual 
	 * 
	 * @param comunidade
	 * @return
	 * @throws DAOException 
	 */
	public List<EnqueteComunidade> findEnquetesByComunidade(ComunidadeVirtual comunidade) {
		DetachedCriteria c = DetachedCriteria.forClass(EnqueteComunidade.class);
		c.add(eq("comunidade", comunidade)).addOrder(asc("pergunta"));
		@SuppressWarnings("unchecked")
		List<EnqueteComunidade> lista = getHibernateTemplate().findByCriteria(c);
		return lista;
	}

	/**
	 * Localiza a Enquete mais atual da Comunidade Virtual
	 * 
	 * @param comunidade
	 * @return
	 */
	public EnqueteComunidade findEnqueteMaisAtualByComunidade(ComunidadeVirtual comunidade) {
		try {
			Query q = getSession().createQuery("select e from EnqueteComunidade e left join e.respostas r where e.comunidade.id = ? order by e.data desc");
			q.setInteger(0, comunidade.getId());
			q.setMaxResults(1);
			return (EnqueteComunidade) q.uniqueResult();
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}
	}

	/**
	 * Retorna a resposta do usuário para determinada enquete
	 *  
	 * @param usuarioLogado
	 * @param object
	 * @return
	 */
	public EnqueteRespostaComunidade getRespostaUsuarioEnquete(Usuario usuario, EnqueteComunidade enquete) {
		if (enquete != null && usuario != null) {
			DetachedCriteria c = DetachedCriteria.forClass(EnqueteVotosComunidade.class);
			c.add(eq("usuario.id", usuario.getId())).createCriteria("enqueteResposta").add(eq("enquete.id", enquete.getId()));
			EnqueteVotosComunidade voto = (EnqueteVotosComunidade) getHibernateTemplate().uniqueResult(c);
			if (voto != null)
				return voto.getEnqueteResposta();
		}
		return null;
	}

	/**
	 * Retorna a estatística de votos de acordo com a Enquete
	 * 
	 * @param object
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<EnqueteRespostaComunidade> findEstatisticaDeVotosbyEnquete(EnqueteComunidade enquete) throws HibernateException, DAOException {
		Query q = getSession().createQuery("from EnqueteRespostaComunidade er where er.enquete.id = ?");
		q.setInteger(0, enquete.getId());

		@SuppressWarnings("unchecked")
		List<EnqueteRespostaComunidade> result = q.list();
		
		for (EnqueteRespostaComunidade er : result) {
			q = getSession().createQuery("select count(ev.id) from EnqueteVotosComunidade ev where ev.enqueteResposta.id = ?");
			q.setInteger(0, er.getId());
		}
		
		return result;
	}

	/**
	 * Retorna as últimas atividades de acordo com a Comunidade Virtual
	 * 
	 * @param comunidade
	 * @return
	 * @throws DAOException 
	 */
	public Collection<RegistroAtividadeTurma> findUltimasAtividades(ComunidadeVirtual comunidade) throws DAOException {
		@SuppressWarnings("unchecked")
		Collection<RegistroAtividadeTurma> lista = getSession().createQuery(
							"select new RegistroAtividadeComunidade(id, descricao, data) from RegistroAtividadeComunidade "
							+ "where comunidade.id = ? order by id desc")
					.setInteger(0, comunidade.getId()).setMaxResults(5).list();
		
		return lista;
	}

	/**
	 * Busca uma turma pela chave primaria de forma otimizada, utilizando uma projeção
	 * 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public ComunidadeVirtual findByPrimaryKeyOtimizado(int id) throws DAOException {
		Query q = getSession().createQuery("select cv from ComunidadeVirtual cv where cv.id = ?");
		q.setInteger(0, id);
		return (ComunidadeVirtual) q.uniqueResult();
	}

	/**
	 * Retorna as Solicitações de Participação pendentes da Comunidade Virtual  
	 * 
	 * @param idComunidadeVirtual
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<SolicitacaoParticipacaoComunidade> findAllSolicitacoesPendentes(int idComunidadeVirtual ) throws HibernateException, DAOException {
		StringBuilder sb = new StringBuilder();
		sb.append("select s from SolicitacaoParticipacaoComunidade s inner join s.comunidadeVirtual c inner join s.usuario u where c.id = ? and" +
				" s.pendenteDecisao = trueValue()");

		@SuppressWarnings("unchecked")
		List<SolicitacaoParticipacaoComunidade> lista = getSession().createQuery(sb.toString()).setInteger(0, idComunidadeVirtual).list();
		return lista;
	}
	
	/**
	 * Retorna as configurações da Comunidade Virtual  
	 * 
	 * @param idComunidadeVirtual
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public ConfiguracoesComunidadeVirtual findConfiguraçõesByComunidade(int idComunidadeVirtual ) throws HibernateException, DAOException {
		StringBuilder sb = new StringBuilder();
		sb.append("select conf from ConfiguracoesComunidadeVirtual conf where conf.comunidade.id = ? ");

		ConfiguracoesComunidadeVirtual conf = (ConfiguracoesComunidadeVirtual) getSession().createQuery(sb.toString()).setInteger(0, idComunidadeVirtual).uniqueResult();
		return conf;
	}	
	
	/**
	 * Desativa convites que já foram aceitos na comunidade, tornando-os inválidos 
	 * 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public void desativarConviteComunidadeVirtualByHash(String hash) throws HibernateException, DAOException {
		update("update cv.convites_enviados set ativo = falseValue() where hash_md5 = ?", hash);
	}
}

