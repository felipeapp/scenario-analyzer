/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '25/06/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.CurriculoComponente;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoEnsinoIndividual;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoTurma;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Dao para efetuar consultas utilizadas nas solicitações de ensino individual.
 * @author leonardo
 * @author Victor Hugo
 */
public class SolicitacaoEnsinoIndividualDao extends GenericSigaaDAO {

	/**
	 * Busca apenas os componentes obrigatórios do currículo do discente por código
	 * @param discente
	 * @param codigo
	 * @return
	 * @throws DAOException
	 */
	public ComponenteCurricular findComponenteObrigatorioByCodigo(DiscenteAdapter discente, String codigo, boolean apenasObrigatorio) throws DAOException {
		Criteria c = getSession().createCriteria(CurriculoComponente.class);
		if( apenasObrigatorio )
			c.add(Expression.eq("obrigatoria", Boolean.TRUE));
		c.add(Expression.eq("curriculo.id", discente.getCurriculo().getId()));
		Criteria comp = c.createCriteria("componente");
		comp.add(Expression.eq("codigo", codigo));
		CurriculoComponente curr = (CurriculoComponente) c.uniqueResult();
		if(curr != null)
			return curr.getComponente();
		return null;
	}

	/**
	 * Busca apenas os componentes obrigatórios do currículo do discente por nome
	 * @param discente
	 * @param nome
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ComponenteCurricular> findComponenteObrigatorioByNome(DiscenteAdapter discente, String nome, boolean apenasObrigatorio) throws DAOException {
		Criteria c = getSession().createCriteria(CurriculoComponente.class);
		if( apenasObrigatorio )
			c.add(Expression.eq("obrigatoria", Boolean.TRUE));
		c.add(Expression.eq("curriculo.id", discente.getCurriculo().getId()));
		c.createCriteria("componente").createCriteria("detalhes").add(Expression.like("nome", nome, MatchMode.ANYWHERE));
		Collection<CurriculoComponente> currs = c.list();
		Collection<ComponenteCurricular> comps = new ArrayList<ComponenteCurricular>();
		for(CurriculoComponente cc : currs)
			comps.add(cc.getComponente());
		return comps;

	}

	/**
	 * Retorna as solicitações de ensino individual do discente, ano, período, situações informados
	 * Apenas a informação do discente é obrigatória
	 * @param idDiscente
	 * @param ano
	 * @param periodo
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<SolicitacaoEnsinoIndividual> findByDiscenteAnoPeriodo( int idDiscente,  Integer tipo, Integer ano, Integer periodo, Collection<Integer> situacoes) throws DAOException{

		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT solicitacao FROM SolicitacaoEnsinoIndividual solicitacao");
		hql.append(" WHERE discente.id = :idDiscente ");
		if( tipo != null )
			hql.append(" AND tipo = :tipo ");
		if( ano != null )
			hql.append(" AND ano = :ano ");
		if( periodo != null )
			hql.append(" AND periodo = :periodo ");
		if( !ValidatorUtil.isEmpty(situacoes))
			hql.append(" AND situacao in " + UFRNUtils.gerarStringIn(situacoes));

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idDiscente", idDiscente);
		if( tipo != null )
			q.setInteger("tipo", tipo);
		if( ano != null )
			q.setInteger("ano", ano);
		if( periodo != null )
			q.setInteger("periodo", periodo);

		return q.list();

	}

	/**
	 * Retorna as solicitações de ensino individual realizadas pelos discente do curso informado no ano, período, situações indicados
	 * @param idCurso id do curso do discente da solicitação
	 * @param ano
	 * @param periodo
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<SolicitacaoEnsinoIndividual> findByCurso( int idCurso, int tipo, Integer ano, Integer periodo, Integer... situacoes ) throws DAOException{
		StringBuilder hql = new StringBuilder( " SELECT sei.id, sei.situacao, sei.ano, sei.periodo, sei.dataSolicitacao, d.id, d.matricula, p.nome, c.id, c.codigo, det.nome " );
		hql.append( " , sei.sugestaoHorario,sTurma.id, sTurma.situacao " );
		hql.append( " FROM SolicitacaoEnsinoIndividual sei JOIN sei.discente dg JOIN dg.discente d JOIN d.pessoa p "  );
		hql.append( " JOIN sei.componente c JOIN c.detalhes det LEFT JOIN sei.solicitacaoTurma sTurma " );
		hql.append( " WHERE d.curso.id = :idCurso " );
		hql.append( " AND sei.tipo = :tipo " );
		
		if( ano != null )
			hql.append( " AND sei.ano = :ano " );
		if( periodo != null )
			hql.append( " AND sei.periodo = :periodo " );
		if( !isEmpty(situacoes) )
			hql.append( " AND sei.situacao in " + gerarStringIn(situacoes) );
		
		hql.append( " ORDER BY c.codigo, p.nome " );
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idCurso", idCurso);
		q.setInteger("tipo", tipo);
		if( ano != null )
			q.setInteger("ano", ano);
		if( periodo != null )
			q.setInteger("periodo", periodo);
		
		List<Object[]> lista = q.list();
		List<SolicitacaoEnsinoIndividual> resultado = new ArrayList<SolicitacaoEnsinoIndividual>();
		SolicitacaoEnsinoIndividual sol = null;
		int c = 0;
		for(Object[] linha : lista ){
			sol = new SolicitacaoEnsinoIndividual();
			c = 0;
			
			sol.setId( (Integer) linha[c++] );
			sol.setSituacao( (Integer) linha[c++] );
			sol.setAno( (Integer) linha[c++] );
			sol.setPeriodo( (Integer) linha[c++] );
			sol.setDataSolicitacao( (Date) linha[c++] );
			
			sol.setDiscente(new DiscenteGraduacao((Integer) linha[c++]));
			sol.getDiscente().setMatricula( (Long) linha[c++] );
			sol.getDiscente().setPessoa(new Pessoa());
			sol.getDiscente().getPessoa().setNome( (String) linha[c++] );
			
			sol.setComponente(new ComponenteCurricular( (Integer) linha[c++] ));
			String codComp = (String) linha[c++];
			sol.getComponente().setCodigo( codComp );
			sol.getComponente().setDetalhes(new ComponenteDetalhes());
			sol.getComponente().getDetalhes().setCodigo(codComp);
			sol.getComponente().getDetalhes().setNome( (String) linha[c++] );
			
			sol.setSugestaoHorario((String) linha[c++]);
			Integer idSolTurma = (Integer) linha[c++];
			if( idSolTurma != null ){
				sol.setSolicitacaoTurma(new SolicitacaoTurma());
				sol.getSolicitacaoTurma().setId(idSolTurma);
				sol.getSolicitacaoTurma().setSituacao((Integer) linha[c++]);
			}
			resultado.add(sol);
		}
		
		return resultado;
	}

	/**
	 * Retorna todos os discentes que possuem solicitação de ensino individual com a situação, ano ,período e componente informados
	 * @param idComponente 
	 * @param idCurso id do curso dos alunos para filtrar
	 * @param ano
	 * @param periodo
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<SolicitacaoEnsinoIndividual> findByComponenteAnoPeriodoSituacao( int idCurso, int idComponente, Integer ano, Integer periodo, int... situacoes ) throws DAOException{

		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT solicitacao FROM SolicitacaoEnsinoIndividual solicitacao JOIN solicitacao.componente c JOIN solicitacao.discente dg ");
		hql.append(" WHERE c.id = :idComponente ");
		hql.append(" AND dg.discente.curso.id = :idCurso ");
		if( ano != null )
			hql.append(" AND solicitacao.ano = :ano ");
		if( periodo != null )
			hql.append(" AND solicitacao.periodo = :periodo ");
		if( situacoes != null )
			hql.append(" AND solicitacao.situacao in " + UFRNUtils.gerarStringIn(situacoes));

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idComponente", idComponente);
		q.setInteger("idCurso", idCurso);
		if( ano != null )
			q.setInteger("ano", ano);
		if( periodo != null )
			q.setInteger("periodo", periodo);

		return q.list();
	}

	/**
	 * Este método retorna as solicitações de ensino individual com os parâmetros informados
	 * @param idDiscente
	 * @param tipoTurma
	 * @param idComponente
	 * @param ano
	 * @param periodo
	 * @param situacoes
	 * @return coleção de solicitações de ensino individual
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<SolicitacaoEnsinoIndividual> findByDiscenteComponenteAnoPeriodoSituacao( int idDiscente, int tipoTurma, Integer idComponente, Integer ano, Integer periodo, int... situacoes ) throws DAOException{

		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT solicitacao FROM SolicitacaoEnsinoIndividual solicitacao ");
		hql.append(" WHERE discente.id = :idDiscente ");
		hql.append(" AND solicitacao.tipo = :tipoTurma ");
		if( idComponente != null )
			hql.append(" AND  solicitacao.componente.id = :idComponente");

		if( ano != null )
			hql.append(" AND solicitacao.ano = :ano ");
		if( periodo != null )
			hql.append(" AND solicitacao.periodo = :periodo ");
		if( !isEmpty(situacoes) )
			hql.append(" AND solicitacao.situacao in " + UFRNUtils.gerarStringIn(situacoes));

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("tipoTurma", tipoTurma);
		if( idComponente != null )
			q.setInteger("idComponente", idComponente);
		q.setInteger("idDiscente", idDiscente);
		if( ano != null )
			q.setInteger("ano", ano);
		if( periodo != null )
			q.setInteger("periodo", periodo);

		return q.list();
	}

	/**
	 * Retorna o próximo número da sequência de solicitação de ensino individual
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Integer getSequenciaSolicitacoes() throws DAOException {
		try {
			return ((BigInteger) getSession().createSQLQuery(
					"select nextval('graduacao.solicitacao_ensino_individual_seq')").uniqueResult()).intValue();
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}


	/**
	 * Retorna todos as solicitação de ensino individual com a situação, ano, período e unidade responsável pelo componente curricular informados.
	 * @param idUnidadeResponsavel 
	 * @param ano
	 * @param periodo
	 * @param situacoes
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<SolicitacaoEnsinoIndividual> findByUnidadeAnoPeriodoSituacao( int idUnidadeResponsavel, Integer ano, Integer periodo, int... situacoes ) throws DAOException{

		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT solicitacao FROM SolicitacaoEnsinoIndividual solicitacao JOIN solicitacao.componente c JOIN solicitacao.discente dg ");
		hql.append(" WHERE c.unidade.id = :idUnidadeResponsavel ");
		if( ano != null )
			hql.append(" AND solicitacao.ano = :ano ");
		if( periodo != null )
			hql.append(" AND solicitacao.periodo = :periodo ");
		if( situacoes != null )
			hql.append(" AND solicitacao.situacao in " + UFRNUtils.gerarStringIn(situacoes));

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idUnidadeResponsavel", idUnidadeResponsavel);
		if( ano != null )
			q.setInteger("ano", ano);
		if( periodo != null )
			q.setInteger("periodo", periodo);

		return q.list();
	}
}
