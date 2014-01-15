/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '21/08/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.pesquisa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.graduacao.AbstractRelatorioSqlDao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pesquisa.dominio.AutorResumoCongresso;
import br.ufrn.sigaa.pesquisa.dominio.AvaliacaoApresentacaoResumo;
import br.ufrn.sigaa.pesquisa.dominio.AvaliacaoResumo;
import br.ufrn.sigaa.pesquisa.dominio.AvaliadorCIC;
import br.ufrn.sigaa.pesquisa.dominio.CongressoIniciacaoCientifica;
import br.ufrn.sigaa.pesquisa.dominio.ResumoCongresso;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Dao para consultas de avaliações de resumos do CIC e avaliações de apresentação de resumo do CIC.
 * 
 * @author leonardo
 *
 */
public class AvaliacaoResumoDao extends AbstractRelatorioSqlDao {

	/**
	 * Retorna todos os Avaliadores de Resumo de uma dada área de conhecimento
	 * para o congresso informado
	 * @param idCongresso
	 * @param idArea
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AvaliadorCIC> findAvaliadoresResumo(int idCongresso, int idArea) throws DAOException {
		
		String hql = "from AvaliadorCIC where avaliadorResumo = trueValue() " +
				" and area.id = "+ idArea +
				" and congresso.id = "+ idCongresso;
		return getSession().createQuery(hql).list();
	}
	
	/**
	 * Retorna todos os Avaliadores de Apresentação de Resumo de uma dada área de conhecimento
	 * para o congresso informado
	 * @param idCongresso
	 * @param idArea
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AvaliadorCIC> findAvaliadoresApresentacaoResumo(int idCongresso, int idArea) throws DAOException {
		
		String hql = "from AvaliadorCIC where avaliadorApresentacao = trueValue() " +
				" and area.id = "+ idArea +
				" and congresso.id = "+ idCongresso;
		return getSession().createQuery(hql).list();
	}
	
	/**
	 * Retorna todos os Avaliadores de Apresentação de Resumo do centro informado
	 * para o congresso informado
	 * @param idCongresso
	 * @param idArea
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AvaliadorCIC> findAvaliadoresApresentacaoResumoByCentro(int idCongresso, int idCentro) throws DAOException {
		
		String hql = "from AvaliadorCIC where avaliadorApresentacao = trueValue() " +
				" and (docente.unidade.gestora.id = "+ idCentro +" or docente.unidade.id = "+ idCentro +")"+
				" and congresso.id = "+ idCongresso +
				" order by docente.pessoa.nome";
		return getSession().createQuery(hql).list();
	}
	
	/**
	 * Retorna todos os Avaliadores de Apresentação de Resumo cadastrados para o congresso informado 
	 * 
	 * @param idCongresso
	 * @param idArea
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AvaliadorCIC> findAvaliadoresApresentacaoResumo(int idCongresso) throws DAOException {
		
		String hql = "select id, docente.unidade.gestora.id from AvaliadorCIC where avaliadorApresentacao = trueValue() " +
				" and congresso.id = "+ idCongresso;
		List lista = getSession().createQuery(hql).list();
		
		ArrayList<AvaliadorCIC> result = new ArrayList<AvaliadorCIC>();

		for (int a = 0; a < lista.size(); a++) {
			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);
			
			AvaliadorCIC avaliador = new AvaliadorCIC();
			avaliador.setId((Integer) colunas[col++]);
			avaliador.setDocente(new Servidor());
			avaliador.getDocente().setUnidade(new Unidade());
			avaliador.getDocente().getUnidade().setGestora(new Unidade());
			avaliador.getDocente().getUnidade().getGestora().setId((Integer) colunas[col++]);
			
			result.add(avaliador);
		}
		return result;
	}
	
	/**
	 * Retorna um mapa com os IDs das unidades agrupadoras e uma coleção de avaliadores de apresentação de trabalhos.
	 * 
	 * @param idCongresso
	 * @return
	 * @throws DAOException
	 */
	public Map<Integer, Collection<AvaliadorCIC>> findMapaAvaliadoresApresentacaoResumoByCentro(int idCongresso) throws DAOException {
		
		String sql = "select distinct av.id_avaliador_cic, av.id_docente, " +
				" case " +
				"   when u.id_gestora = :unidade" +
				"     then s1.id_unidade_cic " +
				"     else s2.id_unidade_cic " +
				"   end" +
				" from pesquisa.avaliador_cic av" +
				" join rh.servidor s on av.id_docente = s.id_servidor" +
				" join comum.unidade u on s.id_unidade = u.id_unidade" +
				" left join pesquisa.sigla_unidade_pesquisa s1 on u.id_unidade = s1.id_unidade" +
				" left join pesquisa.sigla_unidade_pesquisa s2 on u.id_gestora = s2.id_unidade" +
				" where av.id_congresso = :congresso" + 
				" and av.avaliador_apresentacao = trueValue()";
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = getSession().createSQLQuery(sql)
								.setInteger("unidade", Unidade.UNIDADE_DIREITO_GLOBAL)
								.setInteger("congresso", idCongresso)
								.list();
		
		Map<Integer, Collection<AvaliadorCIC>> result = new HashMap<Integer, Collection<AvaliadorCIC>>();

		for (int a = 0; a < lista.size(); a++) {
			int col = 0;
			Object[] colunas = lista.get(a);
			
			Integer idAvaliador = (Integer) colunas[col++];
			Integer idDocente = (Integer) colunas[col++];
			Integer idCentro = (Integer) colunas[col++];
			
			if(idCentro != null) {
				AvaliadorCIC avaliador = new AvaliadorCIC();
				avaliador.setId(idAvaliador);
				avaliador.setDocente(new Servidor(idDocente));
				avaliador.getDocente().setUnidade(new Unidade());
				avaliador.getDocente().getUnidade().setGestora(new Unidade());
				avaliador.getDocente().getUnidade().getGestora().setId(idCentro);
				
				Collection<AvaliadorCIC> linha = result.get(idCentro);
				
				if( linha == null){
					linha = new HashSet<AvaliadorCIC>();
				}
				
				linha.add(avaliador);
				result.put(idCentro, linha);
			}
		}
		return result;
	}
	
	public Map<Integer, Integer> findMapaUnidadesAgrupadoras() throws HibernateException, DAOException {
		
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = getSession().createSQLQuery("select id_unidade, id_unidade_cic from pesquisa.sigla_unidade_pesquisa").list();
		
		for(Object[] linha: lista) {
			result.put((Integer) linha[0], (Integer) linha[1]);
		}
		return result;
	}
	
	/**
	 * Retorna os resumos atribuídos ao docente informado para avaliação
	 * 
	 * @param idAvaliador
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AvaliacaoResumo> findAvaliacoesByAvaliador(int idAvaliador) throws DAOException {
		
		String hql =  "from AvaliacaoResumo " +
				" where avaliador.id = "+ idAvaliador +
				" and parecer is null" +
				" and resumo.ativo = trueValue()";
		return getSession().createQuery(hql).list();
	}
	
	/**
	 * Retorna as apresentações de resumo atribuídas ao docente informado para avaliação
	 * 
	 * @param idAvaliador
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AvaliacaoApresentacaoResumo> findAvaliacoesApresentacaoResumoByAvaliador(int idAvaliador) throws DAOException {
		
		String hql =  "from AvaliacaoApresentacaoResumo " +
				" where avaliador.id = "+ idAvaliador+
				" and resumo.ativo = trueValue()";
		return getSession().createQuery(hql).list();
	}
	
	@SuppressWarnings("unchecked")
	public Collection<AvaliacaoApresentacaoResumo> findAvaliacoesApresentacaoResumoByCongresso(int idCongresso) throws DAOException {
		
		String hql =  "from AvaliacaoApresentacaoResumo " +
				" where resumo.congresso.id = "+ idCongresso+
				" and resumo.ativo = trueValue()";
		return getSession().createQuery(hql).list();
	}
	
	@SuppressWarnings("unchecked")
	public Collection<AvaliacaoResumo> findAvaliacoesByAvaliador(Collection<Integer> idsAvaliadores) throws DAOException {
		
		String hql =  "from AvaliacaoResumo " +
				" where avaliador.id in "+ UFRNUtils.gerarStringIn(idsAvaliadores) +
				" and parecer is null" +
				" and resumo.ativo = trueValue()";
		return getSession().createQuery(hql).list();
	}
	
	@SuppressWarnings("unchecked")
	public Collection<AvaliacaoApresentacaoResumo> findAvaliacoesByAvaliadorApresentacao(Collection<Integer> idsAvaliadores) throws DAOException {
		
		String hql =  "from AvaliacaoApresentacaoResumo " +
				" where avaliador.id in "+ UFRNUtils.gerarStringIn(idsAvaliadores) +
				" and resumo.status = 2" +
				" and resumo.ativo = trueValue()";
		return getSession().createQuery(hql).list();
	}
	
	@SuppressWarnings("unchecked")
	public Collection<ResumoCongresso> findResumosPendentes(int idCongresso, int idArea) throws DAOException {
		String hql = "from ResumoCongresso " +
				" where congresso.id = "+ idCongresso +
				" and areaConhecimentoCnpq.id = "+ idArea +
				" and id not in (select resumo.id from AvaliacaoResumo)" +
				" and ativo = trueValue()";
		return getSession().createQuery(hql).list();
	}
	
	@SuppressWarnings("unchecked")
	public Collection<AvaliadorCIC> findAvaliadorResumoByServidor(int idCongresso, int idServidor) throws DAOException {
		String hql = "select distinct(a.avaliador) from AvaliacaoResumo a " +
				" where a.avaliador.docente.id = "+ idServidor +
				" and a.avaliador.congresso.id = "+ idCongresso+
				" and a.resumo.ativo = trueValue()";
		return  getSession().createQuery(hql).list();
	}
	
	@SuppressWarnings("unchecked")
	public Collection<AvaliadorCIC> findAvaliadorApresentacaoResumoByServidor(int idCongresso, int idServidor) throws DAOException {
		String hql = "select distinct(a.avaliador) from AvaliacaoApresentacaoResumo a " +
				" where a.avaliador.docente.id = "+ idServidor +
				" and a.avaliador.congresso.id = "+ idCongresso+
				" and a.resumo.ativo = trueValue()";
		return  getSession().createQuery(hql).list();
	}
	
	public AvaliacaoResumo findByResumo(int idResumo) throws DAOException {
		
		String hql =  "from AvaliacaoResumo " +
				" where resumo.id = "+ idResumo+
				" and resumo.ativo = trueValue()";
		return (AvaliacaoResumo) getSession().createQuery(hql).uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<HashMap<String,Object>> findRelatorioDistribuicaoAvaliacaoApresentacao(int idCongresso, int idCentro) throws DAOException {

		StringBuilder hql = new StringBuilder("select u2.nome as centro, u1.nome as depto, p.nome as avaliador, r.codigo, au.nome as autor, au2.nome as orientador, r.numero_painel " +
				" from pesquisa.avaliacao_apresentacao_resumo a " +
				" 	inner join pesquisa.avaliador_cic av on a.id_avaliador = av.id_avaliador_cic " +
				"	inner join rh.servidor s on av.id_docente = s.id_servidor" +
				"	inner join comum.pessoa p on s.id_pessoa = p.id_pessoa" +
				"	inner join comum.unidade u1 on s.id_unidade = u1.id_unidade" +
				"	inner join comum.unidade u2 on u1.id_gestora = u2.id_unidade" +
				"	inner join pesquisa.resumo_congresso r on a.id_resumo = r.id_resumo_congresso" +
				"	inner join pesquisa.autor_resumo_congresso au on au.id_resumo_congresso = r.id_resumo_congresso, " +
				" pesquisa.avaliacao_apresentacao_resumo a2 " +
				"	inner join pesquisa.resumo_congresso r2 on a2.id_resumo = r2.id_resumo_congresso" +
				"	inner join pesquisa.autor_resumo_congresso au2 on au2.id_resumo_congresso = r2.id_resumo_congresso " +
				" where au.tipo_participacao = 1" +
				" and au2.tipo_participacao = 2" +
				" and a.id_avaliacao_apresentacao_resumo = a2.id_avaliacao_apresentacao_resumo " +
				" and r.id_congresso = "+ idCongresso+
				" and r.ativo = trueValue()");
		if(idCentro > 0)
			hql.append(" and (u1.id_unidade = "+idCentro + " or u1.id_gestora = "+idCentro+")"); 
		hql.append(" order by u2.nome, p.nome, au.nome");

		List result = new ArrayList<HashMap<String,Object>>();
		try {
			result = executeSql(hql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<HashMap<String,Object>> findRelatorioPontuacaoTrabalhos(int idCongresso, int idCentro) throws DAOException {

		StringBuilder hql = new StringBuilder("select u2.nome as centro, r.codigo, au.nome as autor, u.nome as curso, sum(a.media)/count(a.id_avaliador) as media_final " +
				" from pesquisa.avaliacao_apresentacao_resumo a " +
				"	inner join pesquisa.resumo_congresso r on a.id_resumo = r.id_resumo_congresso " +
				"	inner join pesquisa.autor_resumo_congresso au on au.id_resumo_congresso = r.id_resumo_congresso " +
				"	inner join discente d on d.id_discente = au.id_discente " +
				"	inner join (select c.id_curso, c.nome, " +
				"		case" +
				"		  when c.id_unidade = 205 then 441" +
				"		  when c.id_unidade = 351 then 445" +
				"		  when c.id_unidade = 284 then 442" +
				"		  else c.id_unidade" +
				"		end" +
				"	from curso c) u on d.id_curso = u.id_curso" +
				"	inner join comum.unidade u2 on u.id_unidade = u2.id_unidade" +
				" where au.tipo_participacao = 1" +
				" and r.id_congresso = " + idCongresso +
				" and a.media is not null");
		if(idCentro > 0)
			hql.append(" and u2.id_unidade = "+idCentro); 
		hql.append(" group by u2.nome, r.codigo, au.nome, u.nome");
		hql.append(" order by u2.nome, media_final desc");

		List result = new ArrayList<HashMap<String,Object>>();
		try {
			result = executeSql(hql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<HashMap<String,Object>> findRelatorioAvaliadoresApresentacao(int idCongresso, int idCentro) throws DAOException {

		StringBuilder hql = new StringBuilder("select u2.nome as centro, u1.nome as depto, a.id_avaliador, p.nome as avaliador, s.siape " +
				" from pesquisa.avaliacao_apresentacao_resumo a " +
				" 	inner join pesquisa.avaliador_cic av on a.id_avaliador = av.id_avaliador_cic " +
				"	inner join rh.servidor s on av.id_docente = s.id_servidor" +
				"	inner join comum.pessoa p on s.id_pessoa = p.id_pessoa" +
				"	inner join comum.unidade u1 on s.id_unidade = u1.id_unidade" +
				"	inner join comum.unidade u2 on u1.id_gestora = u2.id_unidade" +
				"	inner join pesquisa.resumo_congresso r on a.id_resumo = r.id_resumo_congresso" +
				" where r.id_congresso = "+ idCongresso+
				" and r.ativo = trueValue()");
		if(idCentro > 0)
			hql.append(" and (u1.id_unidade = "+idCentro + " or u1.id_gestora = "+idCentro+")");
		hql.append(" group by u2.nome, u1.nome, a.id_avaliador, p.nome, s.siape ");
		hql.append(" order by u2.nome, u1.nome, p.nome");

		List result = new ArrayList<HashMap<String,Object>>();
		try {
			result = executeSql(hql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
		return result;
	}
	
	public List<AvaliacaoApresentacaoResumo> filter(CongressoIniciacaoCientifica congresso, Integer idAvaliador, String nomeAluno, Integer idOrientador, String codResumo, Integer numPainel) throws DAOException {
		
		StringBuilder hql = new StringBuilder("select a from AvaliacaoApresentacaoResumo a " +
				" join a.resumo.autores autor " +
				" join a.resumo.autores orientador " +
				" where autor.tipoParticipacao = " + AutorResumoCongresso.AUTOR +
				" and orientador.tipoParticipacao = " + AutorResumoCongresso.ORIENTADOR +
				" and a.avaliador.congresso.id = "+ congresso.getId() +
				" and a.resumo.ativo = trueValue()");
		if(idAvaliador != null)
			hql.append(" and a.avaliador.id = "+ idAvaliador);
		if(nomeAluno != null)
			hql.append(" and autor.nome like '%"+ nomeAluno +"%'");
		if(idOrientador != null)
			hql.append(" and orientador.docente.id = "+ idOrientador);
		if(codResumo != null)
			hql.append(" and a.resumo.codigo like '"+ codResumo+"%'");
		if(numPainel != null)
			hql.append(" and a.resumo.numeroPainel = "+ numPainel);
		return  getSession().createQuery(hql.toString()).list();
		
	}
}
