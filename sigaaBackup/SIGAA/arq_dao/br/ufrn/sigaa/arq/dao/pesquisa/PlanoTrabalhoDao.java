/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '10/10/2006'
 *
 */
package br.ufrn.sigaa.arq.dao.pesquisa;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.AgregadorBolsas;
import br.ufrn.sigaa.ensino.graduacao.jsf.AgregadorBolsasMBean.ParametroBuscaAgregadorBolsas;
import br.ufrn.sigaa.ensino.graduacao.jsf.AgregadorBolsasMBean.RestricoesBuscaAgregadorBolsas;
import br.ufrn.sigaa.pesquisa.dominio.AvaliacaoProjeto;
import br.ufrn.sigaa.pesquisa.dominio.CodigoProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.CotaBolsas;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.LimiteCotaExcepcional;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.RelatorioBolsaFinal;
import br.ufrn.sigaa.pesquisa.dominio.RestricaoEnvioResumoCIC;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoStatusPlanoTrabalho;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.Projeto;

/**
 * Dao para consultas de planos de trabalho de pesquisa
 * 
 * @author ricardo
 *
 */
public class PlanoTrabalhoDao extends GenericSigaaDAO {

	/** Limite de resultados das buscas. */
	public static final Integer LIMITE_RESULTADOS = 200;
	
	/**
	 * Retorna todos os planos de trabalho a que um discente está associado
	 *
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	public PlanoTrabalho findByDiscenteAtivo(int idDiscente)
			throws DAOException {
		try {
			Criteria c = getSession().createCriteria(PlanoTrabalho.class)
					.createAlias("membroProjetoDiscente", "membro")
					.add(Expression.eq("membro.inativo", Boolean.FALSE))
					.add(Expression.eq("membro.discente.id", idDiscente))
					.add(Expression.eq("status", TipoStatusPlanoTrabalho.EM_ANDAMENTO))
					.setMaxResults(1);

			return (PlanoTrabalho) c.uniqueResult();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	

	/**
	 * Retorna o último plano de trabalho que o discente esteve associado num
	 * determinado ano.
	 * 
	 * @param idDiscente
	 * @param ano
	 * @return
	 * @throws DAOException
	 */
	public PlanoTrabalho findUltimoByDiscente(int idDiscente, int ano)
			throws DAOException {
		try {
			Criteria c = getSession().createCriteria(PlanoTrabalho.class)
					.createAlias("membroProjetoDiscente", "membro")
					.add(Expression.eq("membro.inativo", Boolean.FALSE))
					.add(Expression.eq("membro.discente.id", idDiscente))
					.add(Expression.isNull("membro.dataFim")).setMaxResults(1);

			return (PlanoTrabalho) c.uniqueResult();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca todos os planos de trabalho de um discente ordenados por cota
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<PlanoTrabalho> findByDiscente(Discente discente)
			throws DAOException {
		try {
			Criteria c = getSession().createCriteria(PlanoTrabalho.class)
	    		.createAlias("membroProjetoDiscente", "membro")
	    		.add(Expression.eq("membro.inativo", Boolean.FALSE))
	    		.add( Expression.eq("membro.discente.id", discente.getId()) )
        		.addOrder(Order.desc("cota"));

			return c.list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna todos os planos de trabalho que o discente já participou
	 *
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<PlanoTrabalho> findAllByDiscente(DiscenteAdapter discente)
			throws DAOException {
		try {

			String hql = "select m.planoTrabalho from MembroProjetoDiscente m where m.inativo = falseValue() and m.discente.id=:discente order by m.planoTrabalho.dataFim desc";
			Query q = getSession().createQuery(hql);
			q.setInteger("discente", discente.getId());

			return q.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna todos os planos de trabalho em andamento do discente
	 *
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<PlanoTrabalho> findAllEmAndamentoByDiscente(
			DiscenteAdapter discente) throws DAOException {
		try {

			String hql = "select pt from PlanoTrabalho pt where pt.membroProjetoDiscente.inativo = falseValue() and pt.membroProjetoDiscente.discente.id = :discente and pt.status = :status";
			Query q = getSession().createQuery(hql);

			q.setInteger("discente", discente.getId());
			q.setInteger("status", TipoStatusPlanoTrabalho.EM_ANDAMENTO);

			return q.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Pega todos os planos que o discente já participou e está sem aluno associado ao plano . Não pega os planos atuais, somente os passados.
	 *
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<PlanoTrabalho> findAllPlanosQueParticipou(DiscenteAdapter discente) throws DAOException {

		String hql = "select pt from PlanoTrabalho pt inner join pt.membrosDiscentes md where pt.membroProjetoDiscente is null and md.inativo = falseValue() and md.discente.id = :discente ";

		try {
			Query q = getSession().createQuery(hql);
			q.setInteger("discente", discente.getId());

			return q.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna todos os planos de um trabalho que um docente orienta
	 *
	 * @param idServidor
	 * @return
	 * @throws DAOException
	 */
	public Collection<PlanoTrabalho> findByOrientador(int idPessoa)
			throws DAOException {
		return filter(null, null, null, null, null, null, null, null, null, null, idPessoa, false);
	}

	/**
	 * Busca todos os planos de trabalho de um consultor pendentes
	 * ou não de avaliação, dependendo do parâmetro informado
	 * @param idConsultor
	 * @param pendenteAvaliacao
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<PlanoTrabalho> findByConsultor(int idConsultor,
			boolean pendenteAvaliacao, Collection<AvaliacaoProjeto> avaliacaoProjeto) throws DAOException {
		try {
			Criteria criteria = getSession().createCriteria(PlanoTrabalho.class);
		
			if (pendenteAvaliacao)
				criteria.add(Restrictions.in("status", new Integer[] {TipoStatusPlanoTrabalho.EM_ANDAMENTO, 
						TipoStatusPlanoTrabalho.CADASTRADO}));

 			if ( !avaliacaoProjeto.isEmpty() ) {
 				List <Integer> projetos = new ArrayList <Integer> ();
				for (AvaliacaoProjeto aP : avaliacaoProjeto )
					projetos.add( aP.getProjetoPesquisa().getId() );
				
				criteria.add( Restrictions.in("projetoPesquisa.id", projetos) );
 			}
		
			criteria.add( Restrictions.isNull("consultor") );

 			if ( !avaliacaoProjeto.isEmpty() ) {
 				return criteria.list();
 			} else {
 				return new ArrayList<PlanoTrabalho>();
 			}
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna um resumo dos planos de trabalho agrupados por área de conhecimento
	 * para fins de distribuição.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<?> findResumoParaDistribuicao() throws DAOException {
		/*
		 * buscar somente planos de trabalho ainda não distribuídos, agrupando
		 * por grande area de conhecimento, totalizando o número de planos, e
		 * buscando os consultores por área
		 */
		try {
			StringBuilder hql = new StringBuilder();

			hql
					.append(" select area.id, area.nome, count(pt.id) as totalProjetos");
			hql.append(" from PlanoTrabalho pt, AreaConhecimentoCnpq area");
			hql
					.append(" where (pt.projetoPesquisa.areaConhecimentoCnpq.grandeArea.id = area.id");
			hql
					.append(" or pt.projetoPesquisa.areaConhecimentoCnpq.id = area.id)");
			hql.append(" and area.codigo is null");
			hql.append(" and area.excluido = falseValue()");
			hql.append(" and pt.projetoPesquisa.projeto.interno = trueValue()");
			hql
					.append(" and pt.status = "
							+ TipoStatusPlanoTrabalho.CADASTRADO);
			hql.append(" group by area.id, area.nome ");
			hql.append(" order by area.nome ");

			Query query = getSession().createQuery(hql.toString());
			return query.list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Verifica se existe algum plano de trabalho de um determinado tipo de bolsa
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */
	public boolean existsPlano(int tipo) throws DAOException {
		try {
			Query query = getSession().createSQLQuery("select id_plano_trabalho from pesquisa.plano_trabalho where tipo_bolsa = :tipo " + BDUtils.limit(1));
			query.setInteger("tipo", tipo);
			return query.uniqueResult() != null;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}


	/**
	 * Retorna todos os planos de trabalho do tipo de bolsa passado por
	 * parâmetro
	 *
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<PlanoTrabalho> findByTipoBolsa(int tipo)
			throws DAOException {
		try {

			Criteria criteria = getSession()
					.createCriteria(PlanoTrabalho.class);
			criteria.add(Restrictions.eq("tipoBolsa", tipo));

			return criteria.list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna todos os planos de trabalho do tipo de bolsa passado por
	 * parâmetro
	 *
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<PlanoTrabalho> findByTipoBolsaOrientador(int tipo,
			int idOrientador) throws DAOException {
		try {
			Criteria criteria = getSession()
					.createCriteria(PlanoTrabalho.class);
			criteria.add(Restrictions.eq("tipoBolsa", tipo));
			criteria.add(Restrictions.eq("orientador.id", idOrientador));

			return criteria.list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca todos os planos de trabalho de um orientador de um determinado
	 * tipo de bolsa da cota informada.
	 *  
	 * @param tipo
	 * @param idOrientador
	 * @param idCota
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<PlanoTrabalho> findByTipoBolsaOrientador(int tipo,
			int idOrientador, int idCota) throws DAOException {
		try {
			Criteria criteria = getSession()
					.createCriteria(PlanoTrabalho.class);
			criteria.add(Restrictions.eq("tipoBolsa.id", tipo));
			criteria.add(Restrictions.eq("orientador.id", idOrientador));
			criteria.add(Restrictions.eq("cota.id", idCota));

			return criteria.list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna os planos de trabalho para indicação no SIGAA
	 *
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */
	public Collection<PlanoTrabalho> findParaIndicacao(int idOrientador, int idExterno)
			throws DAOException {

		try {
            StringBuilder hql = new StringBuilder();
            hql.append(" SELECT pt.id, pt.titulo, pt.dataInicio, pt.dataFim, pt.projetoPesquisa.codigo, "
                            + " pt.orientador.pessoa.nome, mpd.inativo, discente, pt.tipoBolsa.id, pt.tipoBolsa.descricao, pt.tipoBolsa.categoria, pt.tipoBolsa.necessitaRelatorio, pt.status, "
                            + " cota.descricao, cota.inicio, cota.fim");
            hql.append(" FROM PlanoTrabalho pt left join pt.membroProjetoDiscente as mpd left join mpd.discente as discente");
            hql.append(" left join pt.cota as cota ");
            hql.append(" WHERE 1=1 ");
            if (idOrientador > 0)
            	hql.append("AND pt.orientador.id = "+ idOrientador);
            if (idExterno > 0)
            	hql.append(" AND pt.externo.id = "+ idExterno);
            hql.append(" AND pt.status in "+ UFRNUtils.gerarStringIn(new int[] {
                    TipoStatusPlanoTrabalho.AGUARDANDO_INDICACAO,
                    TipoStatusPlanoTrabalho.APROVADO,
                    TipoStatusPlanoTrabalho.CORRIGIDO,
                    TipoStatusPlanoTrabalho.EM_ANDAMENTO }));
            hql.append(" ORDER BY pt.projetoPesquisa.codigo.ano desc, pt.tipoBolsa.descricao");

            // Criando consulta
            Query query = getSession().createQuery(hql.toString());
            List<?> lista = query.list();

            ArrayList<PlanoTrabalho> result = new ArrayList<PlanoTrabalho>();
            for (int a = 0; a < lista.size(); a++) {
                PlanoTrabalho plano = new PlanoTrabalho();
                int col = 0;
                Object[] colunas = (Object[]) lista.get(a);
                plano.setId((Integer) colunas[col++]);
                plano.setTitulo((String) colunas[col++]);
                plano.setDataInicio((Date) colunas[col++]);
                plano.setDataFim((Date) colunas[col++]);
                

                ProjetoPesquisa projeto = new ProjetoPesquisa();
                projeto.setCodigo((CodigoProjetoPesquisa) colunas[col++]);

                plano.setProjetoPesquisa(projeto);

                plano.setOrientador(new Servidor());
                plano.getOrientador().setPessoa(new Pessoa());
                plano.getOrientador().getPessoa().setNome(
                        (String) colunas[col++]);

                plano.setMembroProjetoDiscente(new MembroProjetoDiscente());
                Boolean inativo = (Boolean) colunas[col++];
                if(inativo != null)
                	plano.getMembroProjetoDiscente().setInativo(inativo);
                plano.getMembroProjetoDiscente().setDiscente(
                        (Discente) colunas[col++]);

                plano.setTipoBolsa( new TipoBolsaPesquisa((Integer) colunas[col++]));
                plano.getTipoBolsa().setDescricao((String) colunas[col++]);
                plano.getTipoBolsa().setCategoria((Integer) colunas[col++]);
                plano.getTipoBolsa().setNecessitaRelatorio((Boolean) colunas[col++]);
                plano.setStatus((Integer) colunas[col++]);

                CotaBolsas cota = new CotaBolsas();
                cota.setDescricao((String) colunas[col++]);
                cota.setInicio((Date) colunas[col++]);
                cota.setFim((Date) colunas[col++]);
                plano.setCota(cota);

                result.add(plano);
            }

            return result;
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}

	/**
	 * Buscar planos de trabalho com um certo número mínimo de substituições de
	 * bolsistas
	 *
	 * @param minimo
	 * @param inicio
	 * @param fim
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<PlanoTrabalho> findSubstituicoes(int minimo, Date inicio,
			Date fim) throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();

			hql
					.append(" from PlanoTrabalho pt join fetch pt.membrosDiscentes as membro");
			hql.append(" where pt.membrosDiscentes.size >= :minimo and membro.inativo = falseValue()");

			if (inicio != null) {
				hql.append(" and membro.dataInicio >= :inicio");
			}

			if (fim != null) {
				hql
						.append(" and (membro.dataFim is null OR membro.dataFim <= :fim)");
			}

			hql.append(" order by pt.orientador.pessoa.nome ");

			Query query = getSession().createQuery(hql.toString());

			query.setInteger("minimo", minimo);
			if (inicio != null) {
				query.setDate("inicio", inicio);
			}
			if (fim != null) {
				query.setDate("fim", fim);
			}

			return query.list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Buscar total de solicitações de cotas abertas cadastradas para um
	 * determinado docente como orientador
	 * @param servidor
	 * @param edital
	 * @return
	 * @throws DAOException
	 */
	public long findTotalSolicitacoesByMembro(Servidor servidor, EditalPesquisa edital) throws DAOException {
		return findTotalSolicitacoesByMembro(servidor, edital, false);
	}
	
	/**
	 * Buscar total de solicitações de cotas abertas cadastradas para um
	 * determinado docente como orientador (Sobrecarregado para utilizar o mesmo método na consulta de planos voluntários)
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	public long findTotalSolicitacoesByMembro(Servidor servidor, EditalPesquisa edital, boolean voluntarios)
			throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();

			hql.append("select count(*) from PlanoTrabalho pt ");
			hql.append("where pt.orientador.id = :idOrientador ");
			hql.append("and pt.status in " + UFRNUtils.gerarStringIn(new int[]{TipoStatusPlanoTrabalho.CADASTRADO, TipoStatusPlanoTrabalho.EM_ANDAMENTO}));
			if(!voluntarios)
				hql.append("and pt.tipoBolsa.id not in " + UFRNUtils.gerarStringIn(new int[]{TipoBolsaPesquisa.VOLUNTARIO, TipoBolsaPesquisa.VOLUNTARIO_IT}) );
			hql.append("and pt.edital.id = :idEdital");

			Query q = getSession().createQuery(hql.toString());
			q.setInteger("idOrientador", servidor.getId());
			q.setInteger("idEdital", edital.getId());

			return (Long) q.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}

	/**
	 * Buscar total de solicitações de cotas em aberto cadastradas para um
	 * determinado projeto de pesquisa
	 *
	 * @param projeto
	 * @return
	 * @throws DAOException
	 */
	public long findTotalSolicitacoesByProjeto(ProjetoPesquisa projeto, EditalPesquisa edital)
			throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();

			hql.append("select count(*) from PlanoTrabalho pt ");
			hql.append("where pt.projetoPesquisa.id = :idProjeto ");
			hql.append("and pt.status = " + TipoStatusPlanoTrabalho.CADASTRADO);
			hql.append("and pt.tipoBolsa.id not in " + UFRNUtils.gerarStringIn(new int[]{TipoBolsaPesquisa.VOLUNTARIO, TipoBolsaPesquisa.VOLUNTARIO_IT}) );
			hql.append("and pt.edital.id = :idEdital");

			Query q = getSession().createQuery(hql.toString());
			q.setInteger("idProjeto", projeto.getId());
			q.setInteger("idEdital", edital.getId());

			return (Long) q.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca os relatórios finais de bolsa de um determinado ano.
	 * 
	 * @param ano
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<RelatorioBolsaFinal> findResumosCongresso(int ano)
			throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();

			hql.append(" from RelatorioBolsaFinal r");
			hql.append(" where r.planoTrabalho.projetoPesquisa.codigo.ano = "
					+ ano);
			hql.append(" order by r.planoTrabalho.titulo ");

			Query query = getSession().createQuery(hql.toString());
			return query.list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Buscar todos os planos de trabalho de um projeto que possam receber cotas
	 * de bolsa
	 *
	 * @param idProjeto
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<PlanoTrabalho> findParaConcessao(int idProjeto)
			throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();

			hql.append(" from PlanoTrabalho pt");
			hql.append(" where pt.projetoPesquisa.id = " + idProjeto);
			hql.append(" and (pt.status = " + TipoStatusPlanoTrabalho.APROVADO);
			hql.append("   or pt.status = "
					+ TipoStatusPlanoTrabalho.NAO_APROVADO + ")");
			hql.append(" and (pt.tipoBolsa = " + TipoBolsaPesquisa.PIBIC);
			hql.append("   or pt.tipoBolsa = " + TipoBolsaPesquisa.PROPESQ
					+ ")");
			hql.append(" order by pt.status, pt.titulo ");

			Query query = getSession().createQuery(hql.toString());
			return query.list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Buscar todos os docentes que possuem planos de trabalho cadastrados,
	 * concorrendo a cotas de bolsas
	 *
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Servidor> findDocentesConcorrendoCota(List<Integer> ids)
			throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();

			hql
					.append(" select distinct (pt.orientador.id) as id, pt.orientador.pessoa as pessoa, pt.orientador.siape as siape ");
			hql.append(" from PlanoTrabalho pt ");
			hql.append(" where 1 = 1 ") ;
			if(ids != null && ids.size() > 0){
				hql.append(" and pt.edital.id in "+ UFRNUtils.gerarStringIn(ids.toArray()) );
			}
			hql.append(" order by pt.orientador.pessoa.nome ");

			Query query = getSession().createQuery(hql.toString());
			return query.setResultTransformer(
					Transformers.aliasToBean(Servidor.class)).list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Sobrecarga do método para buscar os planos de trabalho de acordo com uma série de
	 * filtros opcionais, utilizando uma lista de parâmetros menor
	 * @param idGrupoPesquisa
	 * @param idCentro
	 * @param idUnidade
	 * @param idAluno
	 * @param idOrientador
	 * @param idCota
	 * @param idEdital
	 * @param idModalidade
	 * @param idStatus
	 * @return
	 * @throws DAOException
	 */
	public Collection<PlanoTrabalho> filter(Integer idGrupoPesquisa,
			Integer idCentro, Integer idUnidade, Integer idAluno,
			Integer idOrientador, Integer idCota, Integer idEdital, Integer idModalidade,
			Integer idStatus, boolean limitar) throws DAOException {
		return filter(idGrupoPesquisa, idCentro, idUnidade, idAluno, idOrientador, idCota, idEdital, idModalidade, idStatus, null, null, limitar);
	}
	
	/**
	 * Método para buscar os planos de trabalho de acordo com uma série de
	 * filtros opcionais
	 * 
	 * @param idGrupoPesquisa
	 * @param idCentro
	 * @param idUnidade
	 * @param idAluno
	 * @param idOrientador
	 * @param idCota
	 * @param idEdital
	 * @param idModalidade
	 * @param idStatus
	 * @return
	 * @throws DAOException
	 */
	public Collection<PlanoTrabalho> filter(Integer idGrupoPesquisa,
			Integer idCentro, Integer idUnidade, Integer idAluno,
			Integer idOrientador, Integer idCota, Integer idEdital, Integer idModalidade,
			Integer idStatus, Integer idPlanoTrabalho, Integer idPessoa, boolean limitar) throws DAOException {

			String selectCount = "SELECT COUNT(*)";
			String projecao = " SELECT pt.id, pt.titulo, pt.dataInicio, pt.dataFim, pt.projetoPesquisa.id, pt.projetoPesquisa.codigo, pt.projetoPesquisa.projeto.titulo, pt.projetoPesquisa.projeto.palavrasChave, pt.projetoPesquisa.areaConhecimentoCnpq.nome, "
							+ " orientador.nome, externo.nome, pt.membroProjetoDiscente.id, pt.membroProjetoDiscente.inativo, discente, pt.tipoBolsa.id, pt.tipoBolsa.descricao, pt.tipoBolsa.categoria, pt.tipoBolsa.vinculadoCota, pt.tipoBolsa.necessitaRelatorio, pt.status, "
							+ " cota.descricao, cota.inicio, cota.fim";
			String orderBy = " ORDER BY orientador.nome, externo.nome, cota.descricao desc, pt.tipoBolsa";
			
			StringBuilder hql = new StringBuilder();
			hql.append(" FROM PlanoTrabalho pt left join pt.membroProjetoDiscente.discente as discente");
			hql.append(" left join pt.cota as cota ");
			hql.append(" left join pt.orientador as ori ");
			hql.append(" left join ori.pessoa as orientador ");
			hql.append(" left join pt.externo as ext ");
			hql.append(" left join ext.pessoa as externo ");
			hql.append(" WHERE (1 = 1)");

			if (idGrupoPesquisa != null)
				hql.append(" AND pt.projetoPesquisa.linhaPesquisa.grupoPesquisa.id = " + idGrupoPesquisa);
			if (idCentro != null)
				hql.append(" AND pt.projetoPesquisa.centro.id = " + idCentro);
			if (idUnidade != null)
				hql.append(" AND pt.projetoPesquisa.projeto.unidade.id = " + idUnidade );
			if (idAluno != null)
				hql.append(" AND pt.membroProjetoDiscente.inativo = falseValue() AND pt.membroProjetoDiscente.discente.id = " + idAluno);
			if (idOrientador != null)
				hql.append(" AND pt.orientador.id = "+ idOrientador);
			if (idCota != null)
				hql.append(" AND cota.id = "+ idCota);
			if (idEdital != null)
				hql.append(" AND pt.edital.id = "+ idEdital);
			if (idModalidade != null)
				hql.append(" AND pt.tipoBolsa.id = "+ idModalidade);
			if (idStatus != null)
				hql.append(" AND pt.status = "+ idStatus);
			if(idPlanoTrabalho != null)
				hql.append(" AND pt.id = "+ idPlanoTrabalho);
			if(idPessoa != null)
				hql.append(" AND (orientador.id = "+ idPessoa + " OR externo.id = "+ idPessoa +") ");
			 
			// Criando consulta
			Query query = getSession().createQuery(selectCount + hql.toString());

			ArrayList<PlanoTrabalho> result = new ArrayList<PlanoTrabalho>();
			
			if (limitar) {
				Integer count = ((Long) query.uniqueResult()).intValue();
				
				if (count > LIMITE_RESULTADOS) 
					throw new LimiteResultadosException();
			}
			
			query = getSession().createQuery(projecao + hql.toString() + orderBy);
			List<?> lista = query.list();
		
			for (int a = 0; a < lista.size(); a++) {
				PlanoTrabalho plano = new PlanoTrabalho();
				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);
				plano.setId((Integer) colunas[col++]);
				plano.setTitulo((String) colunas[col++]);
				plano.setDataInicio((Date) colunas[col++]);
				plano.setDataFim((Date) colunas[col++]);

				ProjetoPesquisa projeto = new ProjetoPesquisa();
				projeto.setId((Integer) colunas[col++]);
				projeto.setCodigo((CodigoProjetoPesquisa) colunas[col++]);
				projeto.setTitulo((String) colunas[col++]);
				projeto.setPalavrasChave((String) colunas[col++]);
				projeto.getAreaConhecimentoCnpq().setNome((String) colunas[col++]);

				plano.setProjetoPesquisa(projeto);

				String nomeOrientador = (String) colunas[col++];
				String nomeOrientadorExterno = (String) colunas[col++];
				
				if(nomeOrientador != null){
					plano.setOrientador(new Servidor());
					plano.getOrientador().setPessoa(new Pessoa());
					plano.getOrientador().getPessoa().setNome(nomeOrientador);
				} else if(nomeOrientadorExterno != null){
					plano.setExterno(new DocenteExterno());
					plano.getExterno().getPessoa().setNome(nomeOrientadorExterno);
				}

				Integer idMembroDiscente = (Integer) colunas[col++];
				Boolean membroDiscenteInativo = (Boolean) colunas[col++];
				Discente discente = (Discente) colunas[col++];
				
				if(idMembroDiscente != null){
					plano.setMembroProjetoDiscente(new MembroProjetoDiscente(idMembroDiscente));
					plano.getMembroProjetoDiscente().setInativo(membroDiscenteInativo);
					plano.getMembroProjetoDiscente().setDiscente(discente);
				}

				plano.setTipoBolsa( new TipoBolsaPesquisa((Integer) colunas[col++]));
				plano.getTipoBolsa().setDescricao((String) colunas[col++]);
				plano.getTipoBolsa().setCategoria((Integer) colunas[col++]);
				plano.getTipoBolsa().setVinculadoCota((Boolean) colunas[col++]);
				plano.getTipoBolsa().setNecessitaRelatorio((Boolean) colunas[col++]);
				plano.setStatus((Integer) colunas[col++]);

				CotaBolsas cota = new CotaBolsas();
				cota.setDescricao((String) colunas[col++]);
				cota.setInicio((Date) colunas[col++]);
				cota.setFim((Date) colunas[col++]);
				plano.setCota(cota);

				result.add(plano);
			}
			return result;
	}

	/**
	 * Busca todos os planos em andamento de uma determinada cota
	 *
	 * @param cota
	 * @return
	 * @throws DAOException
	 */
	public Collection<PlanoTrabalho> findAtivosByCota(CotaBolsas cota)
			throws DAOException {
		return filter(null, null, null, null, null, cota.getId(), null, null,
				TipoStatusPlanoTrabalho.EM_ANDAMENTO, false);
	}
	
	/**
	 * Busca todos os planos em andamento vinculados a uma bolsa onde o prazo do plano já passou.
	 * 
	 * @param idOrientador
	 * @param idTipoBolsa
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<PlanoTrabalho> findPlanosByTipoBolsa(int idTipoBolsa) throws DAOException {
		Criteria criteria = getSession().createCriteria(PlanoTrabalho.class);
		criteria.add(Restrictions.eq("tipoBolsa.id", idTipoBolsa));
		criteria.add(Restrictions.lt ("dataFim", new Date()));
		criteria.add(Restrictions.eq("status", TipoStatusPlanoTrabalho.EM_ANDAMENTO));
		return criteria.list();
	}

	/**
	 * Busca os planos de trabalho pendentes de avaliação de uma determinada área de conhecimento.
	 * @param areaConhecimentoCnpq
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<PlanoTrabalho> findPendentesAvaliacao() throws DAOException {
		try {
			Query q = getSession()
					.createQuery(
							"select "
									+ " p.id as id, p.titulo as titulo, p.status as status"
									+ " from PlanoTrabalho p"
									+ " where p.status in "
									+ UFRNUtils.gerarStringIn(new int[]{
												TipoStatusPlanoTrabalho.CADASTRADO,
												TipoStatusPlanoTrabalho.CORRIGIDO})
									+ " and p.edital.avaliacaoVigente = trueValue()"
									+ " order by p.titulo");
			return q.setResultTransformer(
					Transformers.aliasToBean(PlanoTrabalho.class)).list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Buscar todos os planos de trabalho associados a um projeto de pesquisa
	 *
	 * @param projeto
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<PlanoTrabalho> findByProjeto(ProjetoPesquisa projeto)
			throws DAOException {
		try {
			Criteria criteria = getSession()
					.createCriteria(PlanoTrabalho.class);
			criteria.add(Restrictions.eq("projetoPesquisa", projeto));

			return criteria.list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna o total de solicitações de cotas por docente para um determinado edital de pesquisa.
	 * @param edital
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, Long> findTotalSolicitacoesDocente(EditalPesquisa edital)
			throws DAOException {
		Map<Integer, Long> solicitacoes = new HashMap<Integer, Long>();

		try {
			StringBuilder hql = new StringBuilder();

			hql
					.append(" select pt.orientador.id, count(*) from PlanoTrabalho pt ");
			hql.append(" where pt.cota.id = " + edital.getCota().getId());
			hql.append(" group by pt.orientador.id");

			List<Object[]> lista = getSession().createQuery(hql.toString()).list();
			for (Object[] object : lista) {
				solicitacoes.put((Integer) object[0], (Long) object[1]);
			}

			return solicitacoes;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna os Id's dos docentes que solicitaram cota para planos de projetos externos
	 * @param edital
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Set<Integer> findSolicitacoesProjetoExternoDocente(EditalPesquisa edital)
		throws DAOException {
		HashSet<Integer> solicitacoes = new HashSet<Integer>();
	
		try {
			StringBuilder hql = new StringBuilder();
		
			hql
					.append(" select pt.orientador.id from PlanoTrabalho pt ");
			hql.append(" where pt.cota.id = " + edital.getCota().getId());
			hql.append(" and pt.projetoPesquisa.projeto.interno = falseValue()");
			hql.append(" group by pt.orientador.id");
		
		
			solicitacoes.addAll( getSession().createQuery(hql.toString()).list() );
			
		
			return solicitacoes;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna os Id's dos docentes que solicitaram cota para um edital
	 * @param edital
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Set<Integer> findSolicitacoesByEdital(EditalPesquisa edital) throws DAOException {
		HashSet<Integer> solicitacoes = new HashSet<Integer>();
		try {
			StringBuilder hql = new StringBuilder();
			hql.append(" select pt.orientador.id from PlanoTrabalho pt ");
			hql.append(" where pt.edital.id = " + edital.getId());
			hql.append(" group by pt.orientador.id");
			solicitacoes.addAll( getSession().createQuery(hql.toString()).list() );
			return solicitacoes;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna um resumo do número de cotas distribuídas, solicitadas e indicadas
	 * por cada docente para um determinado período de cota. 
	 *
	 * @param cota
	 * @return
	 */
	public List<Map<String, Object>> findResumoSolicitacoes(CotaBolsas cota) {

		String tiposCota = UFRNUtils.gerarStringIn(new int[] {
				TipoBolsaPesquisa.PIBIC, TipoBolsaPesquisa.PROPESQ,
				TipoBolsaPesquisa.A_DEFINIR });

		String sql = "select centro.sigla, "
				+ "sum(case when pp.numero_renovacoes = 0 and pt.tipo_bolsa in "
				+ tiposCota
				+ " and status != "
				+ TipoStatusPlanoTrabalho.CADASTRADO
				+ " then 1 else 0 end) as solicitadas_projetos_novos, "
				+ "sum(case when pp.numero_renovacoes != 0 and pt.tipo_bolsa in "
				+ tiposCota
				+ " and status != "
				+ TipoStatusPlanoTrabalho.CADASTRADO
				+ " then 1 else 0 end) as solicitadas_projetos_renovados, "
				+ "sum(case when pt.tipo_bolsa in "
				+ tiposCota
//				+ " and pt.status != "
//				+ TipoStatusPlanoTrabalho.CADASTRADO
				+ " then 1 else 0 end) as cotas, "
				+ "sum(case when pt.tipo_bolsa = "
				+ TipoBolsaPesquisa.BALCAO
				+ " then 1 else 0 end) as balcao, "
				+ "sum(case when pt.tipo_bolsa = "
				+ TipoBolsaPesquisa.VOLUNTARIO
				+ " then 1 else 0 end) as voluntarios, "
				+ "sum(case when pt.tipo_bolsa = "
				+ TipoBolsaPesquisa.OUTROS
				+ " then 1 else 0 end) as outras "
				+ "from pesquisa.plano_trabalho pt, pesquisa.projeto_pesquisa pp, projetos.projeto p, comum.unidade u, comum.unidade centro "
				+ "where pt.id_cota_bolsas = "
				+ cota.getId()
				+ "and pt.id_projeto_pesquisa = pp.id_projeto_pesquisa "
				+ "and pp.id_projeto = p.id_projeto "
				+ "and p.id_unidade  = u.id_unidade "
				+ "and u.id_gestora = centro.id_unidade "
				+ "group by centro.sigla, centro.codigo_unidade "
				+ "order by centro.codigo_unidade";

		return getJdbcTemplate().queryForList(sql);
	}

	/**
	 * Retorna o número de bolsas de um determinado tipo distribuídas por um edital
	 * @param tipoBolsa
	 * @param idEdital
	 * @return
	 * @throws DAOException
	 */
	public long getNumeroBolsasDistribuidasEdital(int tipoBolsa, int idEdital)
			throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
			hql.append(" select count(*)");
			hql.append(" from PlanoTrabalho p");
			hql.append(" where p.tipoBolsa.id = :tipoBolsa");
			hql.append(" and p.edital.id = :idEdital");

			Query query = getSession().createQuery(hql.toString());
			query.setInteger("tipoBolsa", tipoBolsa);
			query.setInteger("idEdital", idEdital);

			return (Long) query.uniqueResult();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Busca os planos de trabalho que são passíveis para um discente submeter
	 * ao Congresso de Iniciação Científica de acordo com as restrições informadas.
	 * @param discente
	 * @param restricoes
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<PlanoTrabalho> findPassiveisSubmissaoResumoCongresso(
			DiscenteAdapter discente, List<RestricaoEnvioResumoCIC> restricoes) throws DAOException {
		try {
			StringBuilder hql = new StringBuilder(
					" SELECT pt.id_plano_trabalho, p.id_projeto,  pp.id_projeto_pesquisa, tbp.descricao, pt.status, tbp.categoria, " +
					" pes.id_pessoa, pes.nome, pes1.id_pessoa, pes1.nome, pp.cod_numero, pp.cod_prefixo, pp.cod_ano" +
					" FROM pesquisa.membro_projeto_discente  mpd" +
					" JOIN pesquisa.plano_trabalho pt USING ( id_plano_trabalho )" +
					" JOIN pesquisa.projeto_pesquisa pp on ( pt.id_projeto_pesquisa = pp.id_projeto_pesquisa )" +
					" JOIN projetos.projeto p on ( pp.id_projeto = p.id_projeto )" +
					" JOIN pesquisa.tipo_bolsa_pesquisa tbp on ( pt.tipo_bolsa = tbp.id_tipo_bolsa )" +
					" JOIN discente d on ( mpd.id_discente = d.id_discente )" +
					" JOIN comum.pessoa pes on ( d.id_pessoa = pes.id_pessoa )" +
					" JOIN rh.servidor s on ( pt.id_orientador = s.id_servidor )" +
					" JOIN comum.pessoa pes1 on ( s.id_pessoa = pes1.id_pessoa )" +
					" WHERE d.id_discente = :discente " + 
					"AND (" +
					      "(" + 
					        "mpd.id_membro_projeto_discente = pt.id_membro_projeto_discente" + 
					      ") or (" +
					      	"mpd.id_membro_projeto_discente in (" +
					      	"	SELECT max(id_membro_projeto_discente) " +
					      	"	FROM pesquisa.membro_projeto_discente " +
					      	"	WHERE id_plano_trabalho = pt.id_plano_trabalho )" +
					      ")" +
					    ")" +
					"AND mpd.inativo = false");
					
			if(restricoes != null && !restricoes.isEmpty()) {
				hql.append(" AND ( ");
				for (int i = 0; i < restricoes.size(); i++) {
					RestricaoEnvioResumoCIC r = restricoes.get(i);
					if(r.getCotaBolsa() != null)
						hql.append(" (pt.id_cota_bolsas = :idCota"+ i +") OR ");
					if(r.getTipoBolsa() != null) {
						hql.append(" (pt.tipo_bolsa = :idTipoBolsa"+i);
						hql.append(" and pt.data_inicio >= :dataInicio"+i);
						hql.append(" and pt.data_fim <= :dataFim"+i+") OR ");
					}
				}
				
				int aux = hql.lastIndexOf("OR");
				hql.delete(aux, aux + 2);
				hql.append(")");
			}
			
			hql.append(" order by pt.data_fim desc");

			Query q = getSession().createSQLQuery(hql.toString());
			q.setInteger("discente", discente.getId());
			if(restricoes != null && !restricoes.isEmpty()) {
				for (int i = 0; i < restricoes.size(); i++) {
					RestricaoEnvioResumoCIC r = restricoes.get(i);
					if(r.getCotaBolsa() != null)
						q.setInteger("idCota"+i, r.getCotaBolsa().getId());
					if(r.getTipoBolsa() != null) {
						q.setInteger("idTipoBolsa"+i, r.getTipoBolsa().getId());
						q.setDate("dataInicio"+i, r.getDataInicial());
						q.setDate("dataFim"+i, r.getDataFinal());
					}
				}
			}
			
			List<Object[]> lista = q.list();
			
			Collection<PlanoTrabalho> result = new ArrayList<PlanoTrabalho>();
			
			for (int a = 0; a < lista.size(); a++) {
				int col = 0;
				Object[] colunas = lista.get(a);

				PlanoTrabalho plano = new PlanoTrabalho();
				plano.setId((Integer) colunas[col++]);
				plano.setProjetoPesquisa(new ProjetoPesquisa((Integer) colunas[col++]));
				plano.getProjetoPesquisa().setProjeto(new Projeto((Integer) colunas[col++]));
				plano.setTipoBolsa(new TipoBolsaPesquisa());
				plano.getTipoBolsa().setDescricao((String) colunas[col++]);
				plano.setStatus((Integer) colunas[col++]);
				plano.getTipoBolsa().setCategoria((Integer) colunas[col++]);
				plano.setMembroProjetoDiscente(new MembroProjetoDiscente());
				plano.getMembroProjetoDiscente().setDiscente(new Discente());
				plano.getMembroProjetoDiscente().getDiscente().setPessoa(new Pessoa((Integer) colunas[col++], (String) colunas[col++]));
				plano.setOrientador(new Servidor());
				plano.getOrientador().setPessoa(new Pessoa((Integer) colunas[col++], (String) colunas[col++]));

				//Código Projeto
				CodigoProjetoPesquisa cpp = new CodigoProjetoPesquisa();
				cpp.setNumero( (Integer) colunas[col++] );
				cpp.setPrefixo( (String) colunas[col++] );
				cpp.setAno( (Short) colunas[col++] );
				plano.getProjetoPesquisa().setCodigo(cpp);
				
				result.add(plano);
			}
			
			return result;			
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna os planos de trabalho em aberto, que estão disponíveis para registro de interesse por parte dos discentes.
	 * @param restricoes
	 * @param parametros
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<AgregadorBolsas> findPlanoSemBolsista(RestricoesBuscaAgregadorBolsas restricoes, ParametroBuscaAgregadorBolsas parametros) throws HibernateException, DAOException {
		int[] statusPlanosTrabalhos = new int[]{TipoStatusPlanoTrabalho.EM_ANDAMENTO, TipoStatusPlanoTrabalho.AGUARDANDO_INDICACAO, 
				TipoStatusPlanoTrabalho.APROVADO};
		
		StringBuilder hql = new StringBuilder();
		hql.append(" select distinct pt.id, pt.titulo, " +
				" orientador.id, pessoa.id, pessoa.nome," +
				" areaConhecimentoCnpq.id, areaConhecimentoCnpq.nome," +
				" unidade.id, unidade.sigla, unidade.nome, " +
				" u.id, u.login," +
				" sum(cotas.quantidade), " +
					" ( select count(*)" +
					" from PlanoTrabalho pt2 " +
					" where pt2.cota.id = pt.cota.id" +
					" and pt2.orientador.id = orientador.id " +
					" and pt2.membroProjetoDiscente.id is not null" +
					" and pt2.tipoBolsa.id not in (4,9,100) )," +
				"pt.cota.id");
		hql.append(" from PlanoTrabalho pt " +
				" left join pt.membroProjetoDiscente mpd " +
				" left join pt.edital e " +
				" left join e.cotasDocentes cd " +
				" left join cd.docente docente " +
				" left join cd.cotas cotas" +
				" left join pt.orientador orientador " +
				" left join orientador.pessoa pessoa" +
				" left join pt.projetoPesquisa projetoPesquisa" +
				" left join projetoPesquisa.projeto projeto" +
				" left join projetoPesquisa.areaConhecimentoCnpq areaConhecimentoCnpq" +
				" left join projeto.unidade unidade, " +
				" Usuario u");
		hql.append(" where ((pt.membroProjetoDiscente is null or mpd.inativo = trueValue()) and pt.dataFim >= current_date() ) ");
		hql.append(" and u.id = (select max(id) from Usuario u where u.inativo = falseValue() and u.pessoa.id = pessoa.id)");
		hql.append(" and (cd.id is null or docente.id is null or docente.id = orientador.id or cotas.quantidade = 0) ");
		hql.append(" and pt.status in " + UFRNUtils.gerarStringIn(statusPlanosTrabalhos));
		
		if (restricoes.isBuscaCentro())
			hql.append(" and projetoPesquisa.centro = " + parametros.getCentro() );
		if (restricoes.isBuscaAreaConhecimentoCnpq())
			hql.append(" and areaConhecimentoCnpq.id = " + parametros.getAreaConhecimentoCnpq());
		if (restricoes.isBuscaServidor())
			hql.append(" and orientador.id = " + parametros.getServidor().getId());
		if (restricoes.isBuscaDepartamento())
			hql.append(" and unidade.id = " + parametros.getDepartamento());
		if (restricoes.isBuscaPalavraChave())
			hql.append(" and lower(pt.titulo) like lower(:palavraChave)");
		if ( !isEmpty( parametros.getNivelEnsino() ) && parametros.getNivelEnsino() == NivelEnsino.MEDIO )
			hql.append(" and e.cota.id = " + findLastEditalPibicEM());
		
		hql.append(" group by pt.id, pt.titulo, " +
				" orientador.id, pessoa.id, pessoa.nome," +
				" areaConhecimentoCnpq.id, areaConhecimentoCnpq.nome," +
				" unidade.id, unidade.sigla, unidade.nome, " +
				" u.id, u.login, pt.cota.id");
		hql.append(" order by pessoa.nome, pt.cota.id");
		
		Query q = getSession().createQuery(hql.toString());
		if (restricoes.isBuscaPalavraChave())
			q.setString("palavraChave", "%" + parametros.getPalavraChave() + "%");
		
		List<?> result = q.list();
		ArrayList<AgregadorBolsas> bolsas = new ArrayList<AgregadorBolsas>();
		int idServidorAnterior = 0;
		int idCotaAnteior = 0;
		for (int a = 0; a < result.size(); a++) {
			AgregadorBolsas oportunidade = new AgregadorBolsas();
			int col = 0;
			Object[] colunas = (Object[]) result.get(a);
			oportunidade.setId((Integer) colunas[col++]);
			oportunidade.setDescricao((String) colunas[col++]);

			Servidor servidor = new Servidor((Integer) colunas[col++]);
			servidor.setPessoa(new Pessoa((Integer) colunas[col++]));
			servidor.getPessoa().setNome((String) colunas[col++]);
			oportunidade.setResponsavelProjeto(servidor);

			AreaConhecimentoCnpq area = new AreaConhecimentoCnpq((Integer) colunas[col++]);
			area.setNome((String) colunas[col++]);
			oportunidade.setAreaConhecimento(area);
			
			Unidade unidade = new Unidade((Integer) colunas[col++]);
			unidade.setSigla((String) colunas[col++]);
			unidade.setNome((String) colunas[col++]);
			oportunidade.setUnidade(unidade);
			
			Integer idUsuario = (Integer) colunas[col++];
			String login = (String) colunas[col++];
			if (idUsuario != null) {
				Usuario usuario = new Usuario(idUsuario);
				usuario.setLogin(login);
				oportunidade.setUsuario(usuario);
			}
		
			if ( idServidorAnterior != oportunidade.getResponsavelProjeto().getId() )
				idCotaAnteior = 0;
			
			Long numeroVagas = (Long) colunas[col++];
			numeroVagas = numeroVagas != null ? numeroVagas : 0;
			
			Long numeroVagasRestantes = (Long) colunas[col++];
			numeroVagasRestantes = numeroVagasRestantes != null ? numeroVagasRestantes : 0;
			
			// Garante que o número de vagas remuneradas é maior que zero.
			if (numeroVagas.intValue() >= numeroVagasRestantes.intValue()) {
				oportunidade.setVagasRemuneradas(numeroVagas.intValue() - numeroVagasRestantes.intValue());
			} else {
				oportunidade.setVagasRemuneradas(0);			
			}
			
			Integer idCotaBolsa = (Integer) colunas[col++];
			idCotaBolsa = idCotaBolsa != null ? idCotaBolsa : 0;
			
			oportunidade.setIdCotaBolsa(idCotaBolsa);

			
			if ( idCotaAnteior != 0 && idCotaAnteior != oportunidade.getIdCotaBolsa() )
				oportunidade.setVagasRemuneradas( 
						AgregadorBolsas.totalizadorVagasRemuneradas(bolsas, oportunidade.getResponsavelProjeto(), 
									oportunidade.getVagasRemuneradas()) );
			
			idCotaAnteior = oportunidade.getIdCotaBolsa();
			idServidorAnterior = oportunidade.getResponsavelProjeto().getId();
			
			oportunidade.setIdDetalhe(oportunidade.getId());
			bolsas.add(oportunidade);
		}		
		
		return bolsas;
	}
	
	/** Retorna a cota de Bolsas de Pesquisa mais recente da modalidade PIBIC-EM */
	public Integer findLastEditalPibicEM() throws DAOException{
		Query c = getSession().createQuery("select c.editalPesquisa.cota.id from Cotas c " +
				"where c.tipoBolsa.id = :tipoBolsa order by c.editalPesquisa.id desc")
				.setInteger("tipoBolsa", TipoBolsaPesquisa.PIBIC_EM)
				.setMaxResults(1);
		return (Integer) c.uniqueResult();
	}
	
	/**
	 * Retorna todos os planos de trabalho com status corrigido. 
	 * @return
	 * @throws DAOException
	 */
	public Collection<PlanoTrabalho> findByStatusCorrigido() throws DAOException
	{
		return filter(null, null, null, null, null, null, null, null, TipoStatusPlanoTrabalho.CORRIGIDO, false); 
	}
	
	/**
	 * Retorna o limite de cota excepcional do servidor informado como argumento, se houver.
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	public LimiteCotaExcepcional findLimiteCotaExcepcionalByServidor(Servidor servidor) throws DAOException {
		Criteria c = getSession().createCriteria(LimiteCotaExcepcional.class)
					.add(Restrictions.eq("servidor.id", servidor.getId()))
					.add(Restrictions.eq("ativo", Boolean.TRUE))
					.setMaxResults(1);
		return (LimiteCotaExcepcional) c.uniqueResult();
	}
	
	/**
	 * Retorna o número total de planos de trabalho de um docente externo e um edital de pesquisa específico.
	 * @param docenteExterno
	 * @param edital
	 * @return
	 * @throws DAOException
	 */
	public int findTotalPlanosDocenteExterno(DocenteExterno docenteExterno, EditalPesquisa edital)
			throws DAOException {
		String hql = " SELECT count( pt.id ) " 
				   + " FROM PlanoTrabalho pt"
				   + " WHERE pt.edital = " + edital.getId()
				   + " AND pt.externo = " + docenteExterno.getId(); 
		Query q = getSession().createQuery(hql);
		return ((Long) q.uniqueResult()).intValue();
	}

}