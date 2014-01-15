/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 08/03/2010
 */
package br.ufrn.sigaa.ensino.stricto.docenciaassistida.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dao.UsuarioDAO;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.FormaAtuacaoDocenciaAssistida;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.PlanoDocenciaAssistida;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.reuni.dominio.PeriodoIndicacaoReuni;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO para consultas relacionadas ao Plano de Docência Assistida.
 * 
 * @author Arlindo Rodrigues
 *
 */
public class PlanoDocenciaAssistidaDao extends GenericSigaaDAO  {
		
	/**
	 * Retorna o plano de docência assistida submetido de um discente em determinado período e situações passadas.
	 * @param discente
	 * @param periodo	 
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public List<PlanoDocenciaAssistida> findByPeriodoSituacao(DiscenteAdapter discente, Integer programa, Integer ano, Integer periodo, boolean possuiIndicacao, Integer...situacoes) throws DAOException {		
		return (List<PlanoDocenciaAssistida>) findGeral(discente, programa, ano, periodo, null, null, !possuiIndicacao, null, null, null, null, situacoes);
	}	
	
	/**
	 * Busca Genérica de Plano de Docência Assistida
	 * @param discente
	 * @param programa
	 * @param ano
	 * @param periodo
	 * @param nivel
	 * @param idModalidadeBolsa
	 * @param tipoPlano
	 * @param where
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public Collection<PlanoDocenciaAssistida> findGeral(DiscenteAdapter discente, Integer programa, Integer ano, Integer periodo, Character nivel, 
			Integer idModalidadeBolsa, Boolean semIndicacao, ComponenteCurricular componente, FormaAtuacaoDocenciaAssistida formaAtuacao,
			Integer cargaHorariaIni, Integer cargaHorariaFim, Integer...situacoes) throws HibernateException, DAOException{
		
		String projecao = "id, discente.discente.matricula, discente.discente.pessoa.nome, componenteCurricular.codigo, componenteCurricular.detalhes.nome, "
			+" ano, periodo, ativo, status, modalidadeBolsa.descricao, idArquivo, dataCadastro, discente.discente.gestoraAcademica.id, " +
					"discente.discente.gestoraAcademica.nome, discente.nivel, periodoIndicacaoBolsa.anoPeriodo ";
		
		StringBuilder hql = new StringBuilder();
		hql.append("select pd.id, d.matricula, p.nome, cc.codigo, cd.nome, "
			+" pd.ano, pd.periodo, pd.ativo, pd.status, mb.descricao, pd.idArquivo, pd.dataCadastro, ga.id, " +
					" ga.nome, d.nivel, indicacao.anoPeriodo from PlanoDocenciaAssistida pd ");
		hql.append(" INNER JOIN pd.discente.discente as d ");
		hql.append(" LEFT JOIN pd.componenteCurricular as cc ");
		hql.append(" LEFT JOIN cc.detalhes as cd ");		
		hql.append(" INNER JOIN d.pessoa as p ");
		hql.append(" LEFT JOIN pd.modalidadeBolsa as mb ");
		hql.append(" INNER JOIN d.gestoraAcademica as ga ");
		hql.append(" LEFT OUTER JOIN pd.periodoIndicacaoBolsa as indicacao");
		hql.append(" where 1 = 1 ");
		
		String nomeDiscente = null;
		if (!ValidatorUtil.isEmpty(discente))
			hql.append(" and d.id = "+discente.getId());
		else if (discente != null && !ValidatorUtil.isEmpty(discente.getNome())){
			
			Long matricula = StringUtils.extractLong(discente.getNome());
			
			if (matricula != null)
				hql.append(" and d.matricula = "+matricula);
			else {
				nomeDiscente = discente.getNome();
				hql.append(" and p.nomeAscii like :nomeDiscente ");
			}
			
		}
			
		if (programa != null && programa > 0)
			hql.append(" and ga.id = "+programa);
		
		if( !ValidatorUtil.isEmpty(situacoes) && situacoes[0] > 0)
			hql.append(" and pd.status in " + gerarStringIn(situacoes));
		else
			hql.append(" and pd.ativo = trueValue() ");

		if (ano != null && ano > 0 && periodo != null && periodo > 0)
			hql.append(" and pd.ano = "+ano+" and pd.periodo = "+periodo);
		
		if (!ValidatorUtil.isEmpty(nivel))
			hql.append(" and d.nivel = '"+nivel+"'");
		
		if (!ValidatorUtil.isEmpty(idModalidadeBolsa) && idModalidadeBolsa > 0)
			hql.append(" and mb.id = "+idModalidadeBolsa);
		
		if (!ValidatorUtil.isEmpty(componente))
			hql.append(" and cc.id = "+componente.getId());
		
		String sqlFormaAtuacao = "";
		if (formaAtuacao != null && formaAtuacao.getId() > -1){	
			
			if (formaAtuacao.getId() == 0)
				sqlFormaAtuacao = " and ad.formaAtuacao.id is null ";
			else
				sqlFormaAtuacao = " and ad.formaAtuacao.id = "+formaAtuacao.getId()+" ";
			
		}
		
		String sqlCH = ""; 
		if ((cargaHorariaIni != null && cargaHorariaIni > 0) || (cargaHorariaFim != null && cargaHorariaFim > 0)){
			
			if (cargaHorariaIni != null && cargaHorariaIni > 0) 
				sqlCH = " and ad.ch >= "+cargaHorariaIni;
			
			if (cargaHorariaFim != null && cargaHorariaFim > 0) 
				sqlCH += " and ad.ch <= "+cargaHorariaFim;
			
		}		
		
		if (!ValidatorUtil.isEmpty(sqlCH) || !ValidatorUtil.isEmpty(sqlFormaAtuacao)){			
			hql.append(" and exists ( " +
					" select p.id from AtividadeDocenciaAssistida ad " +
					" inner join ad.planoDocenciaAssistida p " +
					" where p.id = pd.id "+
					sqlFormaAtuacao+
					sqlCH +" )");
		}
		
		
		if (semIndicacao != null){
			if (semIndicacao)
				hql.append(" and indicacao is null ");	
			else 
				hql.append(" and indicacao is not null ");			
		}
		
		hql.append("order by ga.nome, p.nome, pd.ano desc, pd.periodo desc");
		
		Query q = getSession().createQuery(hql.toString());
		
		if (!ValidatorUtil.isEmpty(nomeDiscente))
			q.setString("nomeDiscente", "%"+StringUtils.toAscii(nomeDiscente.trim().toUpperCase()) + "%");
		
		@SuppressWarnings("unchecked")
		Collection<PlanoDocenciaAssistida> lista = HibernateUtils.parseTo(q.list(), projecao, PlanoDocenciaAssistida.class);
		
		UsuarioDAO userDao = DAOFactory.getInstance().getDAO(UsuarioDAO.class);
		try {
			Collection<UsuarioGeral> usuariosPPG = userDao.findByPapel(SigaaPapeis.PPG, true);
		
			@SuppressWarnings("unchecked")
			List<Integer> ids = getJdbcTemplate().query(
					" select distinct h.id_plano_docencia_assistida as id "+ 
					" from stricto_sensu.historico_plano_docencia_assistida h "+ 
					" inner join comum.registro_entrada re on re.id_entrada = h.id_registro_cadastro "+ 
					" where re.id_usuario in "+ UFRNUtils.gerarStringIn(usuariosPPG)+
					" order by h.id_plano_docencia_assistida ", new RowMapper() {
				@Override
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					Integer id = rs.getInt("id");
					return id;
				}
			});	
			
			for (PlanoDocenciaAssistida pd : lista)
				if (ids.contains(pd.getId()))
					pd.setModificadoPorPPG(true);
		
		} finally {
			userDao.close();
		}

		
		return lista;		
	}
		
	/**
	 * Retorna o plano de docência assistida de um discente em determinado período de indicação.
	 * @param discente
	 * @param periodo
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public PlanoDocenciaAssistida findAtivoByPeriodoIndicacao(PeriodoIndicacaoReuni periodo) throws DAOException {
		return (PlanoDocenciaAssistida) getSession()
			.createQuery("from PlanoDocenciaAssistida p where p.ativo = trueValue() and p.periodoIndicacaoBolsa.id =:periodo ")
			.setParameter("periodo", periodo.getId())
			.setMaxResults(1)
			.uniqueResult();
	}		
	
	/**
	 * Retorna todos os plano de docência assistida de uma determinada turma.
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public List<PlanoDocenciaAssistida> findAllByTurma(Turma turma) throws DAOException{		
		String hql = "select pd from PlanoDocenciaAssistida pd "+
		" inner join fetch pd.atividadeDocenciaAssistida" +
		" inner join pd.turmaDocenciaAssistida td  "+
		" where td.turma.id = "+turma.getId()+
		"  and pd.ativo = trueValue() " +
		"  and pd.status not in ( "+
				PlanoDocenciaAssistida.CADASTRADO+"," +
				PlanoDocenciaAssistida.SUBMETIDO+","+
				PlanoDocenciaAssistida.REPROVADO+ 
			")";
		
		@SuppressWarnings("unchecked")
		List<PlanoDocenciaAssistida> lista = getSession().createQuery(hql).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		
		return lista;
	}
	
	/**
	 * Retorna os dados necessários para enviar emails a docencia assistida das turmas passadas por parâmetro.
	 * 
	 * @param turmas
	 * @return
	 * @throws DAOException
	 */
	public List<PlanoDocenciaAssistida> findEmailsDocenciaAssistidaByTurmas(List <Integer> turmas) throws DAOException {
		
		String idsTurmas = UFRNUtils.gerarStringIn(turmas);
		
		@SuppressWarnings("unchecked")
		List <Integer> sts = getSession().createSQLQuery("select id_turma from ensino.turma where id_turma_agrupadora in " + idsTurmas).list();
		
		for (Integer t : sts)
			turmas.add(t);
		
		idsTurmas = UFRNUtils.gerarStringIn(turmas);
		
		String sql = "select p.email from comum.pessoa as p " +
						"inner join discente as d on d.id_pessoa = p.id_pessoa " +
						"inner join stricto_sensu.plano_docencia_assistida as pda on pda.id_discente = d.id_discente " +
						"inner join stricto_sensu.turma_docencia_assistida as tda on tda.id_plano_docencia_assistida = pda.id_plano_docencia_assistida " +
						"where tda.id_turma in " + idsTurmas +
						"and pda.ativo = trueValue() " +
						"and pda.status not in ( "+
							PlanoDocenciaAssistida.CADASTRADO+"," +
							PlanoDocenciaAssistida.SUBMETIDO+","+
							PlanoDocenciaAssistida.REPROVADO+
						")";

		List<PlanoDocenciaAssistida> lista = new ArrayList <PlanoDocenciaAssistida> ();
		
		@SuppressWarnings("unchecked")
		List <String> rs = getSession().createSQLQuery(sql).list();
		
		for (String email : rs){
			PlanoDocenciaAssistida pda = new PlanoDocenciaAssistida();
			DiscenteStricto d = new DiscenteStricto();
			Pessoa p = new Pessoa();
			
			p.setEmail(email);
			d.setPessoa(p);
			pda.setDiscente(d);
			
			lista.add(pda);
		}
		
		return lista;
	}	
	
	/**
	 * Retorna uma coleção de planos de docência assistida do docente em que há bolsista de docência assistida. 
	 * @param idServidor
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 * @throws HibernateException
	 */
	public Collection<PlanoDocenciaAssistida> findByDocente(int idServidor, Integer ano, Integer periodo) throws HibernateException, DAOException{
		
		String projecao = "id, discente.discente.matricula, discente.discente.pessoa.nome, componenteCurricular.codigo, componenteCurricular.detalhes.nome, "
				+" ano, periodo, ativo, status, modalidadeBolsa.descricao, idArquivo, dataCadastro, discente.discente.gestoraAcademica.id, " +
						"discente.discente.gestoraAcademica.nome, discente.nivel, periodoIndicacaoBolsa.anoPeriodo ";
			
			StringBuilder hql = new StringBuilder();
			hql.append("select pd.id, d.matricula, p.nome, cc.codigo, cd.nome, "
				+" pd.ano, pd.periodo, pd.ativo, pd.status, mb.descricao, pd.idArquivo, pd.dataCadastro, ga.id, " +
						" ga.nome, d.nivel, indicacao.anoPeriodo from PlanoDocenciaAssistida pd ");
			hql.append(" INNER JOIN pd.discente.discente as d ");
			hql.append(" LEFT JOIN pd.componenteCurricular as cc ");
			hql.append(" LEFT JOIN cc.detalhes as cd ");		
			hql.append(" INNER JOIN d.pessoa as p ");
			hql.append(" LEFT JOIN pd.modalidadeBolsa as mb ");
			hql.append(" INNER JOIN d.gestoraAcademica as ga ");
			hql.append(" LEFT OUTER JOIN pd.periodoIndicacaoBolsa as indicacao");
			hql.append(" where pd.id in ( ");
			
			hql.append(
					" select distinct tda.planoDocenciaAssistida.id " +
					" from TurmaDocenciaAssistida tda, DocenteTurma dt" +
					" inner join tda.planoDocenciaAssistida pd "+
					" left outer join dt.docente docente" +
					" left outer join dt.docenteExterno docenteExterno" +
					" where tda.turma.id = dt.turma.id" +
					" and (docente.id = :idServidor or docenteExterno.servidor.id = :idServidor) ");
			if (ano != null)
				hql.append(" and pd.ano = :ano");
			if (periodo != null)
				hql.append(" and pd.periodo = :periodo");
			hql.append(" ) order by ano desc, periodo desc");
		
		Query q = getSession().createQuery(hql.toString());
		if (ano != null)
			q.setInteger("ano", ano);
		if (periodo != null)
			q.setInteger("periodo", periodo);
		q.setInteger("idServidor", idServidor);
		
		@SuppressWarnings("unchecked")
		Collection<PlanoDocenciaAssistida> lista = HibernateUtils.parseTo(q.list(), projecao, PlanoDocenciaAssistida.class);
		return lista;
	}	
}
