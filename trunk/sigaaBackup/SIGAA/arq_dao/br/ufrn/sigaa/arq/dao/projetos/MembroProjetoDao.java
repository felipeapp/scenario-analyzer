/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criada em: 08/12/2008
 *
 */
package br.ufrn.sigaa.arq.dao.projetos;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.transform.Transformers;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.rh.dominio.Categoria;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.CursoEventoExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.extensao.jsf.helper.DesignacaoFuncaoProjetoHelper;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.EquipeDocente;
import br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria;
import br.ufrn.sigaa.pesquisa.dominio.CodigoProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;
import br.ufrn.sigaa.pesquisa.relatorios.ParticipacaoDocenteProjetos;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;
import br.ufrn.sigaa.projetos.dominio.FuncaoMembro;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Classe responsável por realizar as buscas de membros de projeto. 
 * 
 * @author Ricardo Wendell
 *
 */
public class MembroProjetoDao extends GenericSigaaDAO {

	
	
	/**
	 * Busca o sumário de participações de docentes em projetos de pesquisa
	 * para um determinado ano
	 *
	 * @param ano
	 * @param situacao
	 * @return
	 * @throws DAOException
	 */
	public Collection<ParticipacaoDocenteProjetos> findPartipacaoProjetos(int ano, TipoSituacaoProjeto situacao) throws DAOException {
        try {
        	StringBuilder hql = new StringBuilder();
        	hql.append(" SELECT DISTINCT m.servidor.id, m.servidor.pessoa.nome," +
        			" m.servidor.unidade.nome, m.servidor.unidade.codigo, " +
        			" m.servidor.unidade.gestora.nome, m.servidor.unidade.gestora.codigo, " +
        			" sum(CASE WHEN m.servidor.id = pp.coordenador.id THEN 1 ELSE 0 END) AS coordenador, " +
        			" sum(CASE WHEN p.interno = trueValue() THEN 1 ELSE 0 END) AS internos," +
        			" sum(CASE WHEN p.interno = falseValue() THEN 1 ELSE 0 END) AS externos");
        	hql.append(" FROM ProjetoPesquisa pp join pp.projeto p join p.equipe m ");

        	hql.append(" where pp.codigo.ano = " + ano +
        			"and m.categoriaMembro.id = " + CategoriaMembro.DOCENTE);
        	if (situacao != null) {
        		hql.append(" and p.situacaoProjeto.id = " + situacao.getId());
        	}
        	hql.append(" GROUP BY m.servidor.id, m.servidor.pessoa.nome," +
        			" m.servidor.unidade.nome, m.servidor.unidade.codigo, m.servidor.unidade.gestora.nome, m.servidor.unidade.gestora.codigo" );
        	hql.append(" ORDER BY m.servidor.unidade.gestora.nome, m.servidor.unidade.nome, m.servidor.pessoa.nome");

        	Query query = getSession().createQuery(hql.toString());

        	@SuppressWarnings("unchecked")
        	List<Object[]> lista = query.list();

        	Collection<ParticipacaoDocenteProjetos> participacoes = new ArrayList<ParticipacaoDocenteProjetos>();
        	for (int a = 0; a < lista.size(); a++) {
        		ParticipacaoDocenteProjetos participacao = new ParticipacaoDocenteProjetos();
				int col = 0;
				Object[] colunas = lista.get(a);

				Servidor docente = new Servidor();
				docente.setId( (Integer) colunas[col++] );
				docente.getPessoa().setNome( (String) colunas[col++] );

				Unidade unidade = new Unidade();
				unidade.setNome( (String) colunas[col++] );
				unidade.setCodigo( (Long) colunas[col++] );

				Unidade gestora = new Unidade();
				gestora.setNome( (String) colunas[col++] );
				gestora.setCodigo( (Long) colunas[col++] );

				unidade.setGestora(gestora);
				docente.setUnidade(unidade);
				participacao.setDocente(docente);

				participacao.setTotalCoordenador( (Long) colunas[col++] );
				participacao.setTotalInternos( (Long) colunas[col++]  );
				participacao.setTotalExternos( (Long) colunas[col++]  );

				participacoes.add( participacao );
        	}

            return participacoes;

        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}
	
	
	/**
	 * Busca todas as associações de um servidor com um projeto de pesquisa na função de coordenador.
	 * @param idServidor
	 * @return
	 * @throws DAOException
	 */
	public Collection<MembroProjeto> findByServidor(int idServidor) throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
			hql.append(" select distinct mem.id as id, pj as projeto");
			hql.append(" from MembroProjeto mem join mem.projeto pj ");
			hql.append(" where mem.servidor.id = :idServidor");
			hql.append(" and pj.id in (select p.id from ProjetoPesquisa pp join pp.projeto p)");
			hql.append(" and mem.funcaoMembro.id = " + FuncaoMembro.COORDENADOR);
			hql.append(" and pj.situacaoProjeto.id in " + UFRNUtils.gerarStringIn(new Integer[]{TipoSituacaoProjeto.EM_ANDAMENTO, 
					TipoSituacaoProjeto.RENOVADO, TipoSituacaoProjeto.FINALIZADO, TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO, 
					TipoSituacaoProjeto.PROJETO_BASE_CONCLUIDO}));
			hql.append(" order by pj.ano desc");
			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idServidor", idServidor);
			
			@SuppressWarnings("unchecked")
			Collection<MembroProjeto> lista = query.setResultTransformer(Transformers.aliasToBean(MembroProjeto.class)).list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Busca todas as associações de uma pessoa com um projeto de pesquisa na função de coordenador.
	 * @param idServidor
	 * @return
	 * @throws DAOException
	 */
	public Collection<MembroProjeto> findCoordenacaoProjetoPesquisaByPessoa(int idPessoa) throws DAOException {
		try {
        	StringBuilder hql = new StringBuilder();
        	hql.append(" select distinct mem.id as id, pj as projeto");
        	hql.append(" from MembroProjeto mem join mem.projeto pj ");
        	hql.append(" where mem.pessoa.id = :idPessoa");
        	hql.append(" and pj.id in (select p.id from ProjetoPesquisa pp join pp.projeto p)");
        	hql.append(" and mem.funcaoMembro.id = " + FuncaoMembro.COORDENADOR);
        	hql.append(" and pj.situacaoProjeto.id in " + UFRNUtils.gerarStringIn(new Integer[]{TipoSituacaoProjeto.EM_ANDAMENTO, 
        			TipoSituacaoProjeto.RENOVADO, TipoSituacaoProjeto.FINALIZADO, TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO, 
        			TipoSituacaoProjeto.PROJETO_BASE_CONCLUIDO}));
        	hql.append(" order by pj.ano desc");
        	Query query = getSession().createQuery(hql.toString());
        	query.setInteger("idPessoa", idPessoa);

        	@SuppressWarnings("unchecked")
        	Collection<MembroProjeto> lista = query.setResultTransformer(Transformers.aliasToBean(MembroProjeto.class)).list();
        	return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Busca todas as associações de um servidor com um Projeto de Pesquisa que esteja ativo
	 * 
	 * @param idServidor
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<MembroProjeto> findProjetosPesquisaAnoPeriodoAtual(int idServidor) throws DAOException {
		
		List<Integer> statusValidos = new ArrayList<Integer>();
		statusValidos.addAll(Arrays.asList(TipoSituacaoProjeto.PESQ_GRUPO_VALIDO));
		statusValidos.add(TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO);
		statusValidos.add(TipoSituacaoProjeto.PROJETO_BASE_APROVADO_SEM_RECURSOS);
		statusValidos.add(TipoSituacaoProjeto.PROJETO_BASE_APROVADO_COM_RECURSOS);
		
		try {
			String hql = 
					" select distinct new MembroProjeto(mp.id, mp.chDedicada, pj.id, pj.ano, pj.titulo, pj.situacaoProjeto.id, mp.servidor.id, " +
							" mp.ativo, mp.dataInicio, mp.dataFim, pj.situacaoProjeto.descricao) " +
							" from MembroProjeto mp inner join mp.projeto pj " +
							" inner join pj.situacaoProjeto a " +
							" inner join mp.servidor b " +
							" where mp.servidor.id = ? and mp.ativo = trueValue() and pj.ativo = trueValue() ";
			hql +=  " and pj.id in (select pp.projeto.id from ProjetoPesquisa pp) " +
					" and pj.situacaoProjeto.id in " + UFRNUtils.gerarStringIn( statusValidos );
			hql += "  order by pj.ano desc ";
			
			Query query = getSession().createQuery(hql.toString());
			query.setInteger(0, idServidor);
			
			return query.list();
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Busca todas as associações de um servidor com um Projeto de Pesquisa que esteja ativo
	 * 
	 * @param idServidor
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<MembroProjeto> findProjetosEnsinoAnoPeriodoAtual(int idServidor) throws DAOException {
		
		List<Integer> statusValidos = new ArrayList<Integer>();
		statusValidos.addAll(Arrays.asList(TipoSituacaoProjeto.MON_GRUPO_VALIDO));
		statusValidos.add(TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO);
		statusValidos.add(TipoSituacaoProjeto.PROJETO_BASE_APROVADO_SEM_RECURSOS);
		statusValidos.add(TipoSituacaoProjeto.PROJETO_BASE_APROVADO_COM_RECURSOS);
		
		try {
					String hql = 
						" select distinct new MembroProjeto(mp.id, mp.chDedicada, pj.id, pj.ano, pj.titulo, pj.situacaoProjeto.id, mp.servidor.id, " +
						" mp.ativo, mp.dataInicio, mp.dataFim, pj.situacaoProjeto.descricao) " +
						" from MembroProjeto mp inner join mp.projeto pj " +
						" inner join pj.situacaoProjeto a " +
						" inner join mp.servidor b " +
						" where mp.servidor.id = ? and mp.ativo = trueValue() and pj.ativo = trueValue() ";
				hql +=  " and pj.id in (select pp.projeto.id from ProjetoEnsino pp) " +
		        		" and pj.situacaoProjeto.id in " + UFRNUtils.gerarStringIn( statusValidos );
				hql += "  order by pj.ano desc ";
		
				Query query = getSession().createQuery(hql.toString());
				query.setInteger(0, idServidor);
				
				return query.list();
				
			} catch (Exception e) {
				throw new DAOException(e.getMessage(), e);
			}
		}
			
    /**
     * Retorna a coleção de MembroProjeto com uma função, tipo de atividade e situação da atividade
     * utilizado na listagem de todas as atividade onde o servidor participa e nos relatórios.
     * Utilizado para o subSistema de extensão.
     *
     * @param servidor
     * @return
     * @throws DAOException
     */
	@SuppressWarnings("unchecked")
	public Collection<MembroProjeto> findByServidorAtividadeExtensao(Integer idServidor, Integer idFuncaoMembro, Integer idTipoAtividade, Integer idTipoSituacaoAtividade) throws DAOException{
        	
	    try {
			String hql = "select distinct mp from MembroProjeto mp inner join mp.projeto pj " +
					"where (mp.servidor.id = :idServidor) ";
					 
			
            if( idFuncaoMembro != null ) {
                hql += " and (mp.funcaoMembro.id = :idFuncaoMembro) ";
            }
            
            hql += " and (pj.id in (select p.id from AtividadeExtensao a inner join a.projeto p ";
            if( idTipoAtividade != null ) {
            	hql += " and (a.tipoAtividadeExtensao.id = :idTipoAtividade) ";
            }
            hql += ")) ";            
            
            if( idTipoSituacaoAtividade != null ) {
		hql += " and (p.situacaoProjeto.id = :idTipoSituacaoAtividade) ";
            }

            	Query q = getSession().createQuery(hql);
		q.setInteger( "idServidor", idServidor );
			
			if( idFuncaoMembro != null ) {
				q.setInteger( "idFuncaoMembro", idFuncaoMembro );
			}
			if( idTipoAtividade != null ) {
				q.setInteger( "idTipoAtividade", idTipoAtividade );
			}
			if( idTipoSituacaoAtividade != null ) {
				q.setInteger( "idTipoSituacaoAtividade", idTipoSituacaoAtividade );
			}
			
			return q.list();
		} catch(Exception e) {
			throw new DAOException(e.getMessage(), e);
		}		
        	
	}
	
    /**
 	 *	 Busca todas as associações de um servidor com um Projeto de Extensão que esteja ativo
     *
     * @param servidor
     * @return
     * @throws DAOException
     */
	@SuppressWarnings("unchecked")
	public Collection<MembroProjeto> findProjetosExtensaoAnoPeriodoAtual(Integer idServidor) throws DAOException{
		
		List<Integer> statusValidos = new ArrayList<Integer>();
		statusValidos.addAll(Arrays.asList(TipoSituacaoProjeto.EXTENSAO_GRUPO_ATIVO));
		statusValidos.add(TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO);
		
	    try {
			String hql = 
					" select distinct new MembroProjeto(mp.id, mp.chDedicada, pj.id, pj.ano, pj.titulo, pj.situacaoProjeto.id, mp.servidor.id, " +
					" mp.ativo, mp.dataInicio, mp.dataFim, pj.situacaoProjeto.descricao) " +
					" from MembroProjeto mp inner join mp.projeto pj " +
					" inner join pj.situacaoProjeto a " +
					" inner join mp.servidor b " +
					" where (mp.servidor.id = :idServidor and mp.ativo = trueValue() and pj.ativo = trueValue() ) ";
            hql +=  " and (pj.id in (select p.id from AtividadeExtensao a inner join a.projeto p)) " +
            		" and pj.situacaoProjeto.id in " + UFRNUtils.gerarStringIn( statusValidos );
            hql +=  " order by pj.ano desc ";
            
        	Query q = getSession().createQuery(hql);
        	q.setInteger( "idServidor", idServidor );
			return q.list();
			
		} catch(Exception e) {
			throw new DAOException(e.getMessage(), e);
		}		
	}

	
	/**
	 * Busca todos os membros de uma atividade de extensão.
	 * Utilizado para o subSistema de extensão.
	 * 
	 * @param idServidor
	 * @param categorias
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<MembroProjeto> findAtivosByProjeto(int idProjeto, Integer... categorias) throws DAOException {
		
		String projecao = "mem.id, mem.ativo, mem.pessoa.nome, mem.categoriaMembro.id, mem.categoriaMembro.descricao, " +
				" mem.funcaoMembro.descricao, mem.chDedicada, mem.dataInicio, mem.dataFim, " +
				" mem.projeto.id, mem.projeto.titulo, mem.projeto.ano, mem.projeto.ativo, mem.projeto.dataInicio, mem.projeto.dataFim, " +
				" mem.projeto.situacaoProjeto.id, mem.funcaoMembro.id, mem.pessoa.id, u.id, u.nome,u.sigla";
		
		String hql = "select " + projecao + " from MembroProjeto mem join mem.projeto pj left join mem.servidor s left join s.unidade u " +
				" where mem.ativo = trueValue() and pj.ativo = trueValue() " +
				" and pj.id = :idProjeto and pj.tipoProjeto.id = :idTipoProjeto" +
				" and mem.categoriaMembro.id in (:idCategoria)";

		Query query = getSession().createQuery(hql.toString());
		query.setInteger("idProjeto", idProjeto);        	
		query.setInteger("idTipoProjeto", TipoProjeto.EXTENSAO);
		query.setParameterList("idCategoria", categorias);
		
		List<Object[]> list = query.list();
		
		Collection<MembroProjeto> result = new ArrayList<MembroProjeto>();
		int col = 0;
		for(Object[] reg: list) {
			MembroProjeto mp = new MembroProjeto();
			mp.setId((Integer) reg[col++]);
			mp.setAtivo((Boolean) reg[col++]);
			mp.getPessoa().setNome((String) reg[col++]);
			mp.setCategoriaMembro( new CategoriaMembro((Integer) reg[col++]) );
			mp.getCategoriaMembro().setDescricao((String) reg[col++]);
			mp.setFuncaoMembro( new FuncaoMembro() );
			mp.getFuncaoMembro().setDescricao((String) reg[col++]);
			mp.setChDedicada((Integer) reg[col++]);
			mp.setDataInicio((Date) reg[col++]);
			mp.setDataFim((Date) reg[col++]);
			mp.setProjeto( new Projeto((Integer) reg[col++]) );
			mp.getProjeto().setTitulo((String) reg[col++]);
			mp.getProjeto().setAno((Integer) reg[col++]);
			mp.getProjeto().setAtivo((Boolean) reg[col++]);
			mp.getProjeto().setDataInicio((Date) reg[col++]);
			mp.getProjeto().setDataFim((Date) reg[col++]);
			mp.getProjeto().getSituacaoProjeto().setId((Integer) reg[col++]);
			mp.getFuncaoMembro().setId((Integer) reg[col++]);
			mp.getPessoa().setId((Integer) reg[col++]);
			 
			if ( mp.getCategoriaMembro().isDocente() || mp.getCategoriaMembro().isServidor() ) {
				mp.getServidor().setUnidade(new Unidade((Integer) reg[col++]));
				mp.getServidor().getUnidade().setNome((String) reg[col++]);
				mp.getServidor().getUnidade().setSigla((String) reg[col++]);
			}else{
				mp.setServidor(null);
			}
			
			result.add(mp);
			col = 0;
		}

		return result;
	}
	
	/**
	 * Busca todos os membros ativos de um projeto de pesquisa
	 * 
	 * @param idProjeto
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<MembroProjeto> findMembroProjetoAtivoByProjetoPesquisa(int idProjeto, boolean verificarDataFim) throws DAOException {
	    try {
		StringBuilder hql = new StringBuilder();
		hql.append(" select mem from MembroProjeto mem inner join mem.projeto pj");
		hql.append(" where pj.id = :idProjeto ");
		hql.append(" and mem.ativo = trueValue() ");
		if (verificarDataFim) 
			hql.append(" and mem.dataFim >= :dataFim ");			
		hql.append(" and (pj.id in (select p.id from ProjetoPesquisa a inner join a.projeto p))");

		Query query = getSession().createQuery(hql.toString());
		query.setInteger("idProjeto", idProjeto);
		if (verificarDataFim) 
				query.setDate("dataFim", new Date());
		return query.list();

	    } catch (Exception e) {
	    	throw new DAOException(e.getMessage(), e);
	    }
	}
	
	/**
	 * Busca todos os membros do projeto de monitoria de DiscenteMonitoria
	 * 
	 * @param idProjeto
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<DiscenteMonitoria> findDiscentesMonitoriaByProjetoEnsino(int idProjeto) throws DAOException {
	    try {
			StringBuilder hql = new StringBuilder();
			hql.append(" select disc from DiscenteMonitoria disc ");
			hql.append(" where disc.ativo = :ativo ");
			hql.append(" and disc.dataFim >= :dataFim ");
			hql.append(" and disc.projetoEnsino.id = :idProjeto ");
			hql.append(" and disc.projetoEnsino.projeto.ativo = trueValue() ");
			hql.append(" and disc.situacaoDiscenteMonitoria.id = :situacao ");

			Query query = getSession().createQuery(hql.toString());
			query.setBoolean("ativo", true);		
			query.setDate("dataFim", new Date());
			query.setInteger("idProjeto",idProjeto);
			query.setInteger("situacao", SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA);

		return query.list();

	    } catch (Exception e) {
	    	throw new DAOException(e.getMessage(), e);
	    }
	}
	
	/**
	 * Busca todos os membros do projeto de monitoria de EquipeDocente
	 * 
	 * @param idProjeto
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EquipeDocente> findEquipeDocenteMonitoriaByProjetoEnsino(int idProjeto) throws DAOException {
	    try {
			StringBuilder hql = new StringBuilder();
						hql.append(" select equipeDocente from EquipeDocente equipeDocente ");
						hql.append(" where equipeDocente.ativo = :ativo ");
						hql.append(" and equipeDocente.dataSaidaProjeto >= :dataSaidaProjeto ");
						hql.append(" and equipeDocente.projetoEnsino.id = :idProjeto ");
						hql.append(" and equipeDocente.projetoEnsino.projeto.ativo = trueValue() ");

		Query query = getSession().createQuery(hql.toString());
		query.setBoolean("ativo", true);
		query.setDate("dataSaidaProjeto", new Date());
		query.setInteger("idProjeto",idProjeto);

		return query.list();

	    } catch (Exception e) {
	    	throw new DAOException(e.getMessage(), e);
	    }
	}

	/**
	 * Retorna os dados necessários para o relatório de carga horária dos docentes de pesquisa.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findRelatorioCargaHorariaDocentesPesquisa() throws DAOException {
		String sql = "SELECT pessoa.nome, c.nome as centro, u.nome as unidade," +
				" coalesce(sum(CASE WHEN p.interno THEN m.ch_dedicada END),0) as chInternos, " +
				" coalesce(sum(CASE WHEN not p.interno THEN m.ch_dedicada END),0) as chExternos, " +
				" coalesce(sum(m.ch_dedicada),0) as chTotal " +
				" FROM projetos.membro_projeto m, rh.servidor s, comum.pessoa, " +
				"   pesquisa.projeto_pesquisa pj, projetos.projeto p, comum.unidade c, comum.unidade u " +
				" WHERE m.id_projeto_pesquisa = pj.id_projeto " +
				" AND m.id_servidor = s.id_servidor " +
				" AND s.id_pessoa = pessoa.id_pessoa " +
				" AND pj.id_projeto = p.id_projeto " +
				" AND s.id_unidade = u.id_unidade " +
				" AND u.id_gestora = c.id_unidade " +
				//" AND p.id_tipo_situacao_projeto = " + TipoSituacaoProjeto.EM_ANDAMENTO +
				" AND s.id_categoria = " + Categoria.DOCENTE +
				" GROUP BY c.nome, u.nome, pessoa.nome " + 
				" ORDER BY c.nome, u.nome, pessoa.nome ";
		return getJdbcTemplate().queryForList(sql);
		
	}
	
	
	
	/**
	 * Retorna Lista de MembroProjeto onde o servidor informado 
	 * é coordenador de atividades de extensão.
	 * Utilizado para o subSistema de extensão.
	 * 
	 * @param servidor 
	 * @param funcao
	 * @return
	 * @throws DAOException
	 */
	public Collection<MembroProjeto> findByCoordenador(Servidor coordenador) throws DAOException {

		try {

			Collection<Integer> projetosByCoordenadoresOrDesignacaoCoordenador = DesignacaoFuncaoProjetoHelper.projetosByCoordenadoresOrDesignacaoCoordenador(coordenador.getId());
			String hql = "select " +
					"membro.id, membro.chDedicada, membro.dataInicio, membro.dataFim, membro.categoriaMembro, membro.funcaoMembro, " +
					"p.id, p.ano, p.titulo, p.dataInicio, p.dataFim , p.ativo, p.situacaoProjeto.id, p.situacaoProjeto.descricao, " +
					"p.coordenador.id, membro.pessoa.id, membro.pessoa.nome " +
					"from Projeto p " +
					"inner join p.equipe membro " +
					"where p.id in (" +
						"select pj.id " +
						"from AtividadeExtensao a inner join a.projeto pj " +
						"inner join pj.coordenador coord " +
							"where (( coord.servidor.id = :idServidor and coord.ativo = trueValue() ) ";
			if(ValidatorUtil.isNotEmpty(projetosByCoordenadoresOrDesignacaoCoordenador))
							hql += " or pj.id in ( :idProjetos ) ";
							hql += ") and pj.situacaoProjeto.id not in (:EXTENSAO_GRUPO_INVALIDO, :PROJETO_BASE_GRUPO_INVALIDO) and " +
							"(pj.situacaoProjeto.id in (:PROJETOS_EM_EXECUCAO, :PROJETOS_CONCLUIDOS)) and pj.ativo = trueValue() order by pj.id)" +
					" and membro.ativo = trueValue() order by p.ano desc, p.id ";
			Query query = getSession().createQuery(hql);

			query.setInteger( "idServidor", coordenador.getId() );
			if(ValidatorUtil.isNotEmpty(projetosByCoordenadoresOrDesignacaoCoordenador))
				query.setParameterList("idProjetos", projetosByCoordenadoresOrDesignacaoCoordenador);
			query.setParameterList("EXTENSAO_GRUPO_INVALIDO", TipoSituacaoProjeto.EXTENSAO_GRUPO_INVALIDO);
			query.setParameterList("PROJETO_BASE_GRUPO_INVALIDO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_INVALIDO);		
			query.setParameterList("PROJETOS_EM_EXECUCAO", TipoSituacaoProjeto.PROJETOS_GRUPO_EM_EXECUCAO);
			query.setParameterList("PROJETOS_CONCLUIDOS", TipoSituacaoProjeto.PROJETOS_GRUPO_CONCLUIDO);
			
			@SuppressWarnings("unchecked")
			List<Object[]> lista = query.list();

	        	Collection<MembroProjeto> equipe = new ArrayList<MembroProjeto>();
	        	for (int a = 0; a < lista.size(); a++) {
					int col = 0;
					Object[] colunas = lista.get(a);
					MembroProjeto mp = new MembroProjeto();
					mp.setId((Integer) colunas[col++]);
					mp.setChDedicada((Integer) colunas[col++]);
					mp.setDataInicio((Date) colunas[col++]);
					mp.setDataFim((Date) colunas[col++]);
					mp.setCategoriaMembro((CategoriaMembro) colunas[col++]);
					mp.setFuncaoMembro((FuncaoMembro) colunas[col++]);					
					Projeto projeto = new Projeto();
					projeto.setId((Integer) colunas[col++]);
					projeto.setAno((Integer) colunas[col++]);
					projeto.setTitulo((String) colunas[col++]);
					projeto.setDataInicio( (Date)colunas[col++] );
					projeto.setDataFim( (Date)colunas[col++] );
					projeto.setAtivo((Boolean)colunas[col++]);
					projeto.setSituacaoProjeto(new TipoSituacaoProjeto((Integer) colunas[col++]));
					projeto.getSituacaoProjeto().setDescricao((String) colunas[col++]);
					projeto.setCoordenador(new MembroProjeto((Integer) colunas[col++]));
					mp.setProjeto(projeto);						
					Pessoa p = new Pessoa();
					p.setId((Integer) colunas[col++]);
					p.setNome((String) colunas[col++]);
					mp.setPessoa(p);

					equipe.add(mp);
	        	}

	            return equipe;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}
	
	
	/**
	 * Retorna Lista de MembroProjeto onde o servidor informado é coordenador de projetos de pesquisa.
	 * 
	 * @param coordenador
	 * @return
	 * @throws DAOException
	 */
	public Collection<MembroProjeto> findByCoordenadorPesquisa(Servidor coordenador) throws DAOException {

		try {

			String hql = "select " +
					"membro.id, membro.chDedicada, membro.dataInicio, membro.dataFim, membro.categoriaMembro, membro.funcaoMembro, " +
					"p.id, p.ano, p.titulo, " +
					"membro.pessoa.id, membro.pessoa.nome " +
					"from Projeto p " +
					"inner join p.equipe membro " +
					"where p.id in (" +
						"select pj.id " +
						"from ProjetoPesquisa pp inner join pp.projeto pj " +
						"inner join pj.coordenador coord " +
							"where coord.servidor.id = :idServidor and coord.ativo = trueValue() " +
							"and pj.situacaoProjeto.id not in (:PROJETO_BASE_GRUPO_INVALIDO) and " +
							"(pj.situacaoProjeto.id in (:PROJETOS_EM_EXECUCAO)) and pj.ativo = trueValue() order by pj.id)" +
					" and membro.ativo = trueValue() order by p.ano desc, p.id ";
			Query query = getSession().createQuery(hql);

			query.setInteger( "idServidor", coordenador.getId() );
			query.setParameterList("PROJETO_BASE_GRUPO_INVALIDO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_INVALIDO);		
			query.setParameterList("PROJETOS_EM_EXECUCAO", TipoSituacaoProjeto.PROJETOS_GRUPO_EM_EXECUCAO);
			
			@SuppressWarnings("unchecked")
			List<Object[]> lista = query.list();

	        	Collection<MembroProjeto> equipe = new ArrayList<MembroProjeto>();
	        	for (int a = 0; a < lista.size(); a++) {
					int col = 0;
					Object[] colunas = lista.get(a);
					MembroProjeto mp = new MembroProjeto();
					mp.setId((Integer) colunas[col++]);
					mp.setChDedicada((Integer) colunas[col++]);
					mp.setDataInicio((Date) colunas[col++]);
					mp.setDataFim((Date) colunas[col++]);
					mp.setCategoriaMembro((CategoriaMembro) colunas[col++]);
					mp.setFuncaoMembro((FuncaoMembro) colunas[col++]);					
					Projeto projeto = new Projeto();
					projeto.setId((Integer) colunas[col++]);
					projeto.setAno((Integer) colunas[col++]);
					projeto.setTitulo((String) colunas[col++]);					
					mp.setProjeto(projeto);						
					Pessoa p = new Pessoa();
					p.setId((Integer) colunas[col++]);
					p.setNome((String) colunas[col++]);					
					mp.setPessoa(p);

					equipe.add(mp);
	        	}

	            return equipe;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}

	
    /**
     * Retorna  MembroProjeto com o status passado, caso nulo, desconsidera o status.
     * Utilizado somente para discentes de atividades de extensão
     * Utilizado para o subSistema de extensão.
     *
     * @param discente
     * @return
     * @throws DAOException
     */
	public MembroProjeto getDiscenteBolsistaAtivo(Discente discente) throws DAOException{
		try {
			Criteria c = getSession().createCriteria(MembroProjeto.class);
			c.add(Expression.eq("discente.id", discente.getId() ) );
			c.add(Expression.eq("categoriaMembro.id", CategoriaMembro.DISCENTE ) );
			c.add( Expression.eq("ativo", Boolean.TRUE));
			c.add(Expression.eq("funcaoMembro.id", FuncaoMembro.BOLSISTA));
			c.add(Expression.ge("dataFim", new Date()));
			c.setMaxResults(1);
			return (MembroProjeto) c.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}


	/**
	 * Retorna Lista de MembroProjeto onde a pessoa informada 
	 * participa ou participou de algum projeto de ensino, pesquisa ou extensão.
	 * 
	 * @param servidor 
	 * @param funcao
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<MembroProjeto> findByPessoa(int idPessoa) throws DAOException {

		try {

			String hql = "select m from MembroProjeto m " +
						"inner join m.projeto pro " +
						"inner join m.pessoa p " +
						"inner join pro.situacaoProjeto s " +
							"where p.id = :idPessoa and m.ativo = trueValue() " +
							"and s.id != :idSituacaoRemovido " +
							"and pro.ativo = trueValue() " +
							"order by pro.id";

			Query query = getSession().createQuery(hql);
			query.setInteger( "idPessoa", idPessoa );
			query.setInteger( "idSituacaoRemovido", TipoSituacaoProjeto.EXTENSAO_REMOVIDO );

			return query.list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna todos os registros de uma pessoa como Membro da equipe de ação de extensão.
	 * 
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<MembroProjeto> findMembrosExtensaoByPessoa(int idPessoa) throws DAOException {

		try {
			String hql = "select m from MembroProjeto m " +
						"inner join m.projeto pro " +
						"inner join m.pessoa p " +
						"inner join pro.situacaoProjeto s " +
							"where p.id = :idPessoa and m.ativo = trueValue() " +
							"and s.id != :idSituacaoRemovido " +
							"and pro.ativo = trueValue() " +
							"and pro.id in (select p.id from AtividadeExtensao a inner join a.projeto p where a.sequencia > 0) " +
							"order by pro.ano desc";

			Query query = getSession().createQuery(hql);
			query.setInteger( "idPessoa", idPessoa );
			query.setInteger( "idSituacaoRemovido", TipoSituacaoProjeto.EXTENSAO_REMOVIDO );
			return query.list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	
	/**
	 * <p> Retorna todas as informações do membro de um projeto para emissão do certificado.</p>
	 * 
	 * <p> Observação:  Só use esse método para essas finalizdade</p>
	 * 
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	public MembroProjeto findInformacoesMembroProjetoParaEmissaoCertificado(int idMembroProjeto) throws DAOException {
		
		String projecao = " membro.id_membro_projeto, membro.data_inicio as inicioMembro, membro.data_fim as fimMembro, membro.id_categoria_membro, membro.ch_dedicada, membro.ativo as ativoMembro, " +
				  " pessoa.id_pessoa as pessoaMembro, pessoa.nome as nomeMembro, pessoa.cpf_cnpj, servidor.id_servidor, servidor.siape, discente.id_discente, discente.matricula, "+
				  " funcao.descricao as descricaoFuncao, projeto.id_projeto, projeto.titulo as tituloProjeto, projeto.data_inicio as inicioProjeto, projeto.data_fim as fimProjeto, " +
				  " projeto.ativo as ativoProjeto, projeto.id_tipo_projeto as tipoProjeto, projeto.data_cadastro as data_cadastro_projeto, unidade.id_unidade as unidade, " +
				  " unidade.nome as nomeUnidade, atividade.id_atividade, atividade.id_tipo_atividade_extensao, cursoEvento.id_curso_evento as idCursoEvento, cursoEvento.carga_horaria, coordenador.id_membro_projeto idMembroCoordenador, " +
				  " pessoaCoordenador.id_pessoa as coordenador, pessoaCoordenador.nome as nomeCoordenador, categoriaCoordenador.id_categoria_membro as catagoriaCoordenador ";
		
		String sql = "SELECT " + projecao 
				+ " FROM projetos.membro_projeto membro " 
				
				+ " LEFT JOIN comum.pessoa pessoa             ON pessoa.id_pessoa = membro.id_pessoa "
				+ " LEFT JOIN rh.servidor servidor            ON servidor.id_servidor = membro.id_servidor "
				+ " LEFT JOIN discente discente               ON discente.id_discente = membro.id_discente "
				+ " INNER JOIN projetos.funcao_membro funcao  ON membro.id_funcao_membro = funcao.id_funcao_membro"
				
				+ " INNER JOIN projetos.categoria_membro categoriaMembro         ON membro.id_categoria_membro = categoriaMembro.id_categoria_membro " 
				+ " INNER JOIN projetos.projeto projeto                          ON membro.id_projeto = projeto.id_projeto "
				+ " INNER JOIN comum.unidade unidade                             ON unidade.id_unidade = projeto.id_unidade "
				+ " INNER JOIN projetos.membro_projeto coordenador               ON projeto.id_coordenador = coordenador.id_membro_projeto "
				+ " INNER JOIN projetos.categoria_membro categoriaCoordenador    ON coordenador.id_categoria_membro = categoriaCoordenador.id_categoria_membro " 
				+ " INNER JOIN comum.pessoa pessoaCoordenador                    ON pessoaCoordenador.id_pessoa = coordenador.id_pessoa "
				+ " INNER JOIN extensao.atividade atividade                      ON atividade.id_projeto = projeto.id_projeto " 
				+ " LEFT JOIN extensao.curso_evento cursoEvento                 ON cursoEvento.id_curso_evento = atividade.id_curso_evento " 
				
				
				+ " WHERE membro.id_membro_projeto = :idMembroProjeto ";

		Query query = getSession().createSQLQuery(sql);
		query.setInteger("idMembroProjeto", idMembroProjeto);
	
		
		MembroProjeto membro = new MembroProjeto();
		
		@SuppressWarnings("unchecked")
		List<Object[]> dados = query.list();
		
		for (Object[] object : dados) {
			membro.setId((Integer) object[0]);
			membro.setDataInicio((Date) object[1]);
			membro.setDataFim((Date) object[2]);
			membro.setCategoriaMembro( new CategoriaMembro((Integer) object[3] ));
			
			if(object[4] != null)
				membro.setChDedicada( ( (Short) object[4]).intValue()  );
			
			membro.setAtivo( (Boolean) object[5]  );
		
			membro.setPessoa(  new Pessoa( (Integer) object[6], (String) object[7])  );
			
			if(object[8] != null)
				membro.getPessoa().setCpf_cnpj( ( (BigInteger) object[8]).longValue() );
			
			if( object[9] != null)
				membro.setServidor( new Servidor( (Integer) object[9], null, (Integer) object[10]) );
			else
				membro.setServidor( new Servidor( ) );
			
			if( object[12] != null)
				membro.setDiscente( new Discente( (Integer) object[11], ((BigInteger) object[12]).longValue(), null ) );
			else
				membro.setDiscente( new Discente( ) );
		
			membro.setFuncaoMembro( new FuncaoMembro() );
			membro.getFuncaoMembro().setDescricao((String) object[13] );
			
			membro.setProjeto( new Projeto ( (Integer) object[14] ));
			membro.getProjeto().setTitulo( (String) object[15] ) ;
			membro.getProjeto().setDataInicio( (Date) object[16] ) ;
			membro.getProjeto().setDataFim( (Date) object[17] ) ;
			membro.getProjeto().setAtivo( (Boolean) object[18] ) ;
			membro.getProjeto().setTipoProjeto(new TipoProjeto((Integer) object[19] ));
			membro.getProjeto().setDataCadastro( (Date) object[20] );
			
			membro.getProjeto().setUnidade( new Unidade((Integer) object[21], null, (String) object[22] , null) ) ;
			
			membro.setAtividade( new AtividadeExtensao(  (Integer) object[23] ));
			membro.getAtividade().setTipoAtividadeExtensao( new TipoAtividadeExtensao( (Integer) object[24] ));
			
			if( object[25] != null ){
				membro.getAtividade().setCursoEventoExtensao( new CursoEventoExtensao((Integer) object[25] ));
				membro.getAtividade().getCursoEventoExtensao().setCargaHoraria( (Integer) object[26] );
			}
			
			membro.getProjeto().setCoordenador( new MembroProjeto( (Integer) object[27]  ));
			membro.getProjeto().getCoordenador().setPessoa( new Pessoa( (Integer) object[28], (String) object[29]) );
			membro.getProjeto().getCoordenador().setCategoriaMembro( new CategoriaMembro((Integer) object[30] ));
			
			membro.getAtividade().setProjeto( membro.getProjeto());
		}
		
		return membro;
	}
	
	
	/**
	 * Retorna o coordenador atual do projeto informado.
	 * 
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	public MembroProjeto findCoordenadorAtualProjeto(int idProjeto) throws DAOException {
		String projecao = " coord.id, coord.ativo, coord.dataInicio, coord.projeto.id, coord.dataFim, coord.pessoa.id, coord.pessoa.nome ";
		String hql = "SELECT " + projecao + " FROM Projeto pro JOIN pro.coordenador coord " + " WHERE pro.id = :idProjeto AND pro.ativo = trueValue() " +
				"AND coord.ativo = trueValue() ";

		Query query = getSession().createQuery(hql);
		query.setInteger("idProjeto", idProjeto);
		query.setMaxResults(1);
		@SuppressWarnings("unchecked")
		Collection<MembroProjeto> lista = HibernateUtils.parseTo(query.list(), projecao, MembroProjeto.class, "coord");
		if (!ValidatorUtil.isEmpty(lista)) {
			return lista.iterator().next();
		} else { 
			return null;
		}
	}

	/**
	 * Método utilizado para buscar o coordenador do projeto de Ações Associadas
	 * 
	 * @param coordenador
	 * @return
	 * @throws DAOException
	 */
	public Collection<MembroProjeto> findByCoordenadorAssociados(Servidor coordenador) throws DAOException {

		try {

			String hql = "select " +
					"membro.id, membro.chDedicada, membro.dataInicio, membro.dataFim, membro.categoriaMembro, membro.funcaoMembro, " +
					"p.id, p.ano, p.titulo, p.dataInicio, p.dataFim , p.ativo, " +
					"membro.pessoa.id, membro.pessoa.nome " +
					"from Projeto p " +
					"inner join p.equipe membro " +
					"where p.id in (" +
						"select pj.id " +
						"from Projeto pj " +
						"inner join pj.coordenador coord " +
							"where coord.servidor.id = :idServidor and coord.ativo = trueValue() " +
							"and pj.situacaoProjeto.id not in (:PROJETO_BASE_GRUPO_INVALIDO) and " +
							"(pj.situacaoProjeto.id in (:PROJETOS_EM_EXECUCAO)) and pj.ativo = trueValue() order by pj.id)" +
					" and membro.ativo = trueValue() order by p.ano desc, p.id ";
			Query query = getSession().createQuery(hql);

			query.setInteger( "idServidor", coordenador.getId() );
			query.setParameterList("PROJETO_BASE_GRUPO_INVALIDO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_INVALIDO);		
			query.setParameterList("PROJETOS_EM_EXECUCAO", TipoSituacaoProjeto.SITUACOES_VALIDAS_ASSOCIADOS);
			
			@SuppressWarnings("unchecked")
			List<Object[]> lista = query.list();

	        	Collection<MembroProjeto> equipe = new ArrayList<MembroProjeto>();
	        	for (int a = 0; a < lista.size(); a++) {
					int col = 0;
					Object[] colunas = lista.get(a);
					MembroProjeto mp = new MembroProjeto();
					mp.setId((Integer) colunas[col++]);
					mp.setChDedicada((Integer) colunas[col++]);
					mp.setDataInicio((Date) colunas[col++]);
					mp.setDataFim((Date) colunas[col++]);
					mp.setCategoriaMembro((CategoriaMembro) colunas[col++]);
					mp.setFuncaoMembro((FuncaoMembro) colunas[col++]);					
					Projeto projeto = new Projeto();
					projeto.setId((Integer) colunas[col++]);
					projeto.setAno((Integer) colunas[col++]);
					projeto.setTitulo((String) colunas[col++]);
					projeto.setDataInicio( (Date)colunas[col++] );
					projeto.setDataFim( (Date)colunas[col++] );
					projeto.setAtivo((Boolean)colunas[col++]);
					mp.setProjeto(projeto);						
					Pessoa p = new Pessoa();
					p.setId((Integer) colunas[col++]);
					p.setNome((String) colunas[col++]);					
					mp.setPessoa(p);

					equipe.add(mp);
	        	}

	            return equipe;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}
	
	/**
	 * Retorna o id coordenador atual do projeto de Pesquisa informado.
	 * 
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	public Integer findCoordenadorAtualProjetoPesquisa(int idProjetoPesquisa) throws DAOException {
		String projecao = "id_servidor";
		String sql = "select "+ projecao +" from projetos.membro_projeto memb where memb.id_projeto_pesquisa = " + idProjetoPesquisa + " and memb.id_funcao_membro = " + FuncaoMembro.COORDENADOR + " and ativo";

		Query query = getSession().createSQLQuery(sql);
		query.setMaxResults(1);
		
		return (Integer) query.uniqueResult();
	}

	/**
	 * Retorna os membros da equipe do projeto informado.
	 * 
	 * @param idProjeto
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<MembroProjeto> findByProjeto(int idProjeto, boolean ativos) throws DAOException {
		String projecao = " mp.id, mp.ativo, mp.dataInicio, mp.dataFim, mp.pessoa.id, mp.pessoa.nome,mp.categoriaMembro.descricao, " +
				"mp.funcaoMembro.id, mp.funcaoMembro.descricao, mp.projeto.id ";
		String hql = "SELECT " + projecao + " FROM MembroProjeto mp " +
				"left join mp.servidor s" +
				" WHERE mp.projeto.id = :idProjeto AND mp.ativo = :ativo ";

		Query query = getSession().createQuery(hql);
		query.setInteger("idProjeto", idProjeto);
		query.setBoolean("ativo", ativos);
		return HibernateUtils.parseTo(query.list(), projecao, MembroProjeto.class, "mp");
	}

	/** Retorna uma coleção de projetos associados ativos em que o servidor é membro.
	 * @param idServidor
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<MembroProjeto> findProjetosAssociadosAnoPeriodoAtual(int idServidor) throws HibernateException, DAOException {
		String projecao = " membro.id, membro.chDedicada, membro.dataInicio, membro.dataFim, membro.categoriaMembro, membro.funcaoMembro, " +
					" p.id as membro.projeto.id, p.ano as membro.projeto.ano, p.titulo as membro.projeto.titulo, p.dataInicio as membro.projeto.dataInicio," +
					" p.dataFim as membro.projeto.dataFim, p.ativo as membro.projeto.ativo, " +
					" membro.pessoa.id, membro.pessoa.nome," +
					" p.pesquisa as membro.projeto.pesquisa, p.ensino as membro.projeto.ensino, p.extensao as membro.projeto.extensao," +
					" p.situacaoProjeto.id as membro.projeto.situacaoProjeto.id, p.situacaoProjeto.descricao as membro.projeto.situacaoProjeto.descricao " ;
		String hql = "select distinct " + HibernateUtils.removeAliasFromProjecao(projecao) + 
				" from Projeto p" +
				" inner join p.equipe membro " +
				" where p.ativo = trueValue() " +
				" and membro.ativo = trueValue() " +
				" and p.tipoProjeto = :tipoProjeto" +
				" and p.dataInicio <= :dataAtual" +
				" and (p.dataFim is null or p.dataFim >= :dataAtual)" +
				" and membro.servidor.id = :idServidor" ;
		Query q = getSession().createQuery(hql);
		q.setInteger("tipoProjeto", TipoProjeto.ASSOCIADO);
		q.setDate("dataAtual", new Date());
		q.setInteger("idServidor", idServidor);
		@SuppressWarnings("unchecked")
		Collection<MembroProjeto> lista = HibernateUtils.parseTo(q.list(), projecao, MembroProjeto.class, "membro");
		return lista;
	}
	
	/**
	 * Busca todas as participações de um servidor como membro de projeto de pesquisa.
	 * 
	 * @param idServidor
	 * @return
	 * @throws DAOException
	 */
	public Collection<MembroProjeto> participanteEquipeProjetoByServidor(int idServidor) throws DAOException{
		try {
        	StringBuilder hql = new StringBuilder();
        	hql.append(" select distinct mem.id as id, pj as projeto");
        	hql.append(" from MembroProjeto mem join mem.projeto pj ");
        	hql.append(" where mem.servidor.id = :idServidor");
        	hql.append(" and pj.id in (select p.id from ProjetoPesquisa pp join pp.projeto p)");
        	hql.append(" and pj.situacaoProjeto.id in " + UFRNUtils.gerarStringIn(new Integer[]{TipoSituacaoProjeto.EM_ANDAMENTO, 
        			TipoSituacaoProjeto.RENOVADO, TipoSituacaoProjeto.FINALIZADO, TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO, 
        			TipoSituacaoProjeto.PROJETO_BASE_CONCLUIDO}));
        	hql.append(" order by pj.ano desc");
        	Query query = getSession().createQuery(hql.toString());
        	query.setInteger("idServidor", idServidor);

        	@SuppressWarnings("unchecked")
        	Collection<MembroProjeto> lista = query.setResultTransformer(Transformers.aliasToBean(MembroProjeto.class)).list();
        	return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Busca as informações de projetos de pesquisa, planos de trabalho e alunos associados que um docente orienta.
	 * 
	 * @param idServidor
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<ProjetoPesquisa> findOrientacoesPlanoTrabalhoDoMembroProjetoByServidor(int idServidor) throws HibernateException, DAOException{
		String hql = " SELECT proPesq.id,proPesq.codigo, proPesq.projeto, plano.id, plano.dataInicio, plano.dataFim, plano.titulo, plano.tipoBolsa, plano.membroProjetoDiscente, plano.orientador, plano.projetoPesquisa, membro "
				+ "	FROM MembroProjetoDiscente membro "
				+ " INNER JOIN membro.planoTrabalho plano "
				+ " INNER JOIN plano.projetoPesquisa proPesq "
				+ " WHERE plano.orientador.id = :idServidor "
				+ " ORDER BY plano.id asc ";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idServidor", idServidor);
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		
		ArrayList<ProjetoPesquisa> projetos = new ArrayList<ProjetoPesquisa>();
		PlanoTrabalho p = new PlanoTrabalho();
		ProjetoPesquisa proPesq = new ProjetoPesquisa();
		Integer idPlanoTrabalho = 0;
		Integer idProjetoPesquisa = 0;
		for(int i=0; i < lista.size(); i++){
			int col=0;
			Object[] colunas = lista.get(i);
			
			if(!idProjetoPesquisa.equals((Integer)colunas[0])){
				proPesq.addPlanoTrabalho(p);
				projetos.add(proPesq);
				idProjetoPesquisa = (Integer)colunas[0];
				proPesq = new ProjetoPesquisa();
				proPesq.setId((Integer) colunas[col++]);
				proPesq.setCodigo((CodigoProjetoPesquisa) colunas[col++]);
				proPesq.setProjeto((Projeto) colunas[col++]);
			}
			if(!idPlanoTrabalho.equals((Integer)colunas[3])){
				if(!idPlanoTrabalho.equals(0) && proPesq.getId() == p.getId())proPesq.addPlanoTrabalho(p);
				idPlanoTrabalho = (Integer)colunas[3];
				p = new PlanoTrabalho();
				p.setId((Integer) colunas[col++]);
				p.setDataInicio((Date)colunas[col++]);
				p.setDataFim((Date) colunas[col++]);
				p.setTitulo((String) colunas[col++]);
				p.setTipoBolsa((TipoBolsaPesquisa)colunas[col++]);
				p.setMembroProjetoDiscente((MembroProjetoDiscente) colunas[col++]);
				p.setOrientador((Servidor) colunas[col++]);
				p.setProjetoPesquisa((ProjetoPesquisa) colunas[col++]);
			}
			
			MembroProjetoDiscente m = new MembroProjetoDiscente();
			m = (MembroProjetoDiscente) colunas[col++];
			p.getMembrosDiscentes().add(m);
		}
		proPesq.addPlanoTrabalho(p);
		projetos.add(proPesq);
		projetos.remove(0);
		return projetos;
	}

	
	/**
	 * Retorna a lista de membros do projeto que podem ser orientadores.
	 * Ou seja, não retorna os discentes na lista.
	 * 
	 * @param idAtividade
	 * @return Collection
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<MembroProjeto> findOrientadoresByProjeto(int idProjeto) throws DAOException {		
		try {
			String projecao = "mp.id, mp.pessoa.id, mp.pessoa.nome, mp.servidor.id ";			
			String hql = new String("select " +	projecao +" from MembroProjeto mp " +
					" where mp.projeto.id = :idProjeto" +
					" and mp.categoriaMembro.id != :idCategoriaDiscente" +
					" order by mp.pessoa.nome");
			
			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idProjeto", idProjeto);
			query.setInteger("idCategoriaDiscente", CategoriaMembro.DISCENTE);			
			return HibernateUtils.parseTo(query.list(), projecao, MembroProjeto.class, "mp");

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}		
	}

	/**
	 * Retorna todos os registros de uma pessoa como Membro da equipe de ação Associada.
	 * 
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<MembroProjeto> findMembrosAssociadosByDiscente(int idDiscente) throws DAOException {
			String projecao = " m.categoriaMembro.descricao, m.dataInicio, m.dataFim, m.id,  " +
					" m.chDedicada, m.projeto.titulo, m.funcaoMembro.descricao, m.projeto.tipoProjeto.id, m.projeto.ano, m.projeto.situacaoProjeto.id ";
			String hql = " select " + projecao +
						 " from MembroProjeto m " +
						 " inner join m.projeto pro " +
						 " inner join m.pessoa p " +
						 " inner join pro.situacaoProjeto s " +
						 " where m.discente.id = :idDiscente" +
						 " and m.ativo = trueValue() " +
						 " and s.id not in ( :idSituacaoRemovido ) " +
						 " and pro.ativo = trueValue() " +
						 " and pro.tipoProjeto.id = :idTipoProjeto " +
						 " order by pro.ano desc";

			Query query = getSession().createQuery(hql);
			query.setInteger( "idDiscente", idDiscente );
			query.setParameterList("idSituacaoRemovido", new Integer[] {TipoSituacaoProjeto.EXCLUIDO, TipoSituacaoProjeto.DESATIVADO});
			query.setInteger("idTipoProjeto", TipoProjeto.ASSOCIADO);
			
			List<MembroProjeto> lista = new ArrayList<MembroProjeto>(HibernateUtils.parseTo(query.list(), projecao, MembroProjeto.class, "m") );
			return lista;
	}
	
	public Projeto findProjetoMembroLeve(int idMembroProjeto) throws DAOException {
		String projecao = " m.projeto.titulo, m.projeto.id, m.projeto.dataCadastro, " +
				          " m.projeto.dataFim, m.projeto.dataInicio, " +
				          " m.projeto.tipoProjeto.id, m.projeto.ano, m.projeto.situacaoProjeto.id ";
		String hql = " select " + projecao +
					 " from MembroProjeto m " +
					 " inner join m.projeto pro " +
					 " inner join m.pessoa p " +
					 " inner join pro.situacaoProjeto s " +
					 " left join m.servidor servidor " +
					 " where m.ativo = trueValue() " +
					 " and s.id not in ( :idSituacaoRemovido ) " +
					 " and pro.ativo = trueValue() " +
					 " and m.id = :idMembroProjeto " +
					 " order by pro.ano desc";

		Query query = getSession().createQuery(hql);
		query.setInteger( "idMembroProjeto", idMembroProjeto );
		query.setParameterList("idSituacaoRemovido", new Integer[] {TipoSituacaoProjeto.EXCLUIDO, TipoSituacaoProjeto.DESATIVADO});
		@SuppressWarnings("unchecked")
		Collection<MembroProjeto> lista = HibernateUtils.parseTo(query.list(), projecao, MembroProjeto.class, "m");
		if (!ValidatorUtil.isEmpty(lista)) {
			return lista.iterator().next().getProjeto();
		} else { 
			return null;
		}
	}

}