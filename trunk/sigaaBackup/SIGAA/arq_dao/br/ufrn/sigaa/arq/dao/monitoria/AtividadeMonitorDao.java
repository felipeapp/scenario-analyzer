/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '09/03/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.monitoria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.monitoria.dominio.AtividadeMonitor;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.Projeto;

/**
 * Dao responsável pelas consultas sobre a entidade AtividadeMonitor
 * 
 * @author Victor Hugo
 * 
 */
public class AtividadeMonitorDao extends GenericSigaaDAO {

	/**
	 * Retorna a atividade do monitor cadastrado para o mês atual, caso exista.
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public AtividadeMonitor findByDiscenteMonitoriaMesCorrente(
			DiscenteMonitoria dm, Integer mes, Integer ano) throws DAOException {

		Criteria c = getSession().createCriteria(AtividadeMonitor.class);
		c.add(Expression.eq("discenteMonitoria.id", dm.getId()));
		c.add(Expression.eq("mes", mes));
		c.add(Expression.eq("ano", ano));
		c.add(Expression.eq("ativo", true));

		return (AtividadeMonitor) c.uniqueResult();
	}

	/**
	 * Retorna todas as atividades do discente passado por parâmetro.
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<AtividadeMonitor> findByDiscente(Discente discente)
			throws DAOException {

		String hql = ("SELECT atividade.id, atividade.mes, atividade.ano, atividade.frequencia, atividade.atividades, atividade.dataCadastro, "
			+ " atividade.dataValidacaoOrientador, atividade.validadoOrientador, dm.id, tipoVinculo.id, tipoVinculo.descricao, dm.ativo, dm.dataInicio, dm.dataFim, " 
			+ " d.matricula, p.nome, pm.id, projeto.id, projeto.ano, projeto.titulo " 
			+ " FROM AtividadeMonitor atividade "
			+ " RIGHT JOIN atividade.discenteMonitoria dm "
			+ " INNER JOIN dm.tipoVinculo tipoVinculo "
			+ " INNER JOIN dm.projetoEnsino pm "
			+ " INNER JOIN pm.projeto projeto "
			+ " INNER JOIN projeto.unidade centro "
			+ " INNER JOIN dm.situacaoDiscenteMonitoria s "
			+ " INNER JOIN dm.discente dg "
			+ " INNER JOIN dg.discente d "
			+ " INNER JOIN d.pessoa p"
			+ " WHERE atividade.ativo = trueValue() and dm.discente.id = :idDiscente and s.id in (:situacoesValidas) and dm.ativo = trueValue()" +
				"ORDER BY pm.id, atividade.ano, atividade.mes ");


		Query q = getSession().createQuery(hql);
		q.setParameterList("situacoesValidas", new Integer[] {SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA, SituacaoDiscenteMonitoria.MONITORIA_FINALIZADA});

		if (discente != null)
			q.setInteger("idDiscente", discente.getId());

		ArrayList<Object[]> lista = (ArrayList<Object[]>) q.list();
		List<AtividadeMonitor> atividades = new ArrayList<AtividadeMonitor>();

		for (Object[] obj : lista) {
			AtividadeMonitor a = new AtividadeMonitor();
			
			int col = 0;
			
			if (obj[0] != null) {
        			a.setId((Integer)obj[col++]);
        			a.setMes((Integer)obj[col++]);
        			a.setAno((Integer)obj[col++]);
        			a.setFrequencia((Integer)obj[col++]);
        			a.setAtividades((String)obj[col++]);
        			a.setDataCadastro((Date)obj[col++]);
        			a.setDataValidacaoOrientador((Date)obj[col++]);
        			a.setValidadoOrientador((Boolean) obj[col++]);
			}
			col = 8;
			DiscenteMonitoria dm = new DiscenteMonitoria();
			dm.setId((Integer)obj[col++]);
			dm.getTipoVinculo().setId((Integer)obj[col++]);
			dm.getTipoVinculo().setDescricao((String)obj[col++]);
			dm.setAtivo((Boolean)obj[col++]);
			dm.setDataInicio((Date)obj[col++]);
			dm.setDataFim((Date)obj[col++]);

			DiscenteGraduacao d = new DiscenteGraduacao();
			d.setMatricula((Long)obj[col++]);

			Pessoa p = new Pessoa();
			p.setNome((String)obj[col++]);

			d.setPessoa(p);
			dm.setDiscente(d);

			ProjetoEnsino pm = new ProjetoEnsino();
			pm.setId((Integer) obj[col++]);
			pm.getProjeto().setId((Integer) obj[col++]);
			pm.setAno((Integer) obj[col++]);
			pm.setTitulo((String)obj[col++]);
			dm.setProjetoEnsino(pm);

			a.setDiscenteMonitoria(dm);
			atividades.add(a);
		}

		return atividades;

	}

	/**
	 * Retorna todas as atividades dos monitores onde o servidor passado por
	 * parâmetro é orientador no período informado. A informação do período é
	 * opcional
	 * 
	 * @param servidor
	 *            orientador dos monitores que cadastraram as atividades que
	 *            serão buscadas.
	 * @param mes
	 *            mês em que a atividade foi realizada
	 * @param ano
	 *            ano em que a atividade foi realizada
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AtividadeMonitor> findByOrientador(Servidor servidor,
			Integer mes, Integer ano, Boolean coordenador) throws DAOException {

		StringBuffer hql = new StringBuffer(
				"Select distinct a from AtividadeMonitor a "
						+ "inner join a.discenteMonitoria d "
						+ "inner join d.orientacoes o "
						+ "inner join o.equipeDocente eqp "
						+ "where a.ativo = trueValue() and eqp.servidor.id = :idServidor and a.dataValidacaoOrientador is null ");

		if (coordenador != null)
			hql.append(" and eqp.coordenador = :coordenador  ");

		if (mes != null)
			hql.append(" and a.mes = :mes ");

		if (ano != null)
			hql.append(" and a.ano = :ano ");

		hql.append(" order by a.dataValidacaoOrientador desc ");

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idServidor", servidor.getId());

		if (mes != null)
			q.setInteger("mes", mes);

		if (ano != null)
			q.setInteger("ano", ano);

		if (coordenador != null)
			q.setBoolean("coordenador", coordenador);

		return q.list();
	}
	
	
	
	
	/**
	 * Retorna relatórios de atividades de discentes de projetos coordenados pelo servidor informado 
	 * e/ou de discentes de projetos onde o servidor não coordena, mas que orienta. 
	 * 
	 * @param servidor
	 * @param coordenador
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AtividadeMonitor> avaliarRelatorioAtividadeMensal(Servidor servidor, Boolean coordenador) throws HibernateException, DAOException {
		
		StringBuilder hqlConsulta = new StringBuilder();	
		
		if( coordenador ) {
			// Retorna relatórios de atividades de discentes de projetos que coordena 
		        // e de discentes de projetos que não coordena, mas que orienta. 
			hqlConsulta.append	(					
					" select distinct pro.id, pro.ano, pro.titulo, dg.id, disc.id, disc.matricula, p_disc.nome, " +
					" ativMonitor.id, ativMonitor.mes, ativMonitor.ano, ativMonitor.dataValidacaoOrientador, ativMonitor.ativo, ativMonitor.validadoOrientador " +
					" from AtividadeMonitor ativMonitor " +
					" inner join ativMonitor.discenteMonitoria dm " +
					" inner join dm.discente dg " +
					" inner join dg.discente disc " +
					" inner join disc.pessoa p_disc " +
					" inner join dm.projetoEnsino pm " +
					" inner join pm.projeto pro " +
					" inner join pm.equipeDocentes equipeProjMon " +
					" inner join equipeProjMon.servidor servProj " +
					" where ativMonitor.ativo = trueValue() and pm.ativo = trueValue() and servProj.id = :idServidor " +
					" and equipeProjMon.ativo = trueValue() " +
					" and " +
					" (equipeProjMon.coordenador =  trueValue() or (dm.id in (select dmon.id " +
						" from DiscenteMonitoria dmon " +
						" inner join dmon.orientacoes ori " +
						" inner join ori.equipeDocente eq " +
						" inner join eq.servidor ser " +
						" where ser.id = :idServidor and eq.ativo = trueValue()) " +
					    " )) " +
					" order by pro.ano desc, ativMonitor.dataValidacaoOrientador desc "
				);			
		}
		else {
			// Retorna somente discentes que orienta.
			hqlConsulta.append	(		
					
					" select distinct ativMonitor " + 
					" from AtividadeMonitor ativMonitor" +
					" inner join ativMonitor.discenteMonitoria dm " +
					" inner join dm.discente dg " +
					" inner join dg.discente disc " +
					" inner join disc.pessoa p_disc " +
					" inner join dm.projetoEnsino pm " +
					" inner join pm.projeto pro" +
					" inner join dm.orientacoes orientacao " +
					" inner join orientacao.equipeDocente eqd " +
					" inner join eqd.servidor serv " +
					" where ativMonitor.ativo = trueValue() and pm.ativo = trueValue() and serv.id = :idServidor and eqd.ativo = trueValue() "
					 
				);			
		}
		
		Query q = getSession().createQuery(hqlConsulta.toString());		
		q.setInteger("idServidor", servidor.getId());
		
		if(coordenador){
			ArrayList<Object[]> lista = (ArrayList<Object[]>) q.list();
			Collection<AtividadeMonitor> result = new ArrayList<AtividadeMonitor>();
			
			for (Object[] obj : lista){
				int col = 0;
				
				Projeto pro = new Projeto();
				ProjetoEnsino projetoEnsino = new ProjetoEnsino();
				pro.setId( (Integer) obj[col++] );
				pro.setAno( (Integer) obj[col++] );
				pro.setTitulo( (String) obj[col++] );
				projetoEnsino.setProjeto(pro);
				
				DiscenteMonitoria dm = new DiscenteMonitoria();
				DiscenteGraduacao dg = new DiscenteGraduacao();
				Discente disc = new Discente();
				dg.setId( (Integer) obj[col++] );
				
				disc.setId( (Integer) obj[col++] );
				disc.setMatricula( (Long) obj[col++] );
				
				Pessoa p = new Pessoa();
				p.setNome( (String) obj[col++] );
				disc.setPessoa(p);
				dg.setDiscente(disc);
				dm.setDiscente(dg);
				dm.setProjetoEnsino(projetoEnsino);
				
				AtividadeMonitor atvMoni = new AtividadeMonitor();
				atvMoni.setId( (Integer) obj[col++] );
				atvMoni.setMes( (Integer) obj[col++] );
				atvMoni.setAno( (Integer) obj[col++] );
				atvMoni.setDataValidacaoOrientador( (Date) obj[col++] );
				atvMoni.setAtivo( (Boolean) obj[col++] );
				atvMoni.setValidadoOrientador((Boolean) obj[col++] );
				atvMoni.setDiscenteMonitoria(dm);
				
				result.add(atvMoni);
			}
			return result;	
		}else{
			return q.list();
		}
	}

	/**
	 * Método para buscar AtividadeMonitor de acordo com uma série de filtros
	 * opcionais
	 * 
	 * @param ano
	 * @param mes
	 * @param idDiscente
	 * @param idProjeto
	 * @return
	 * @throws DAOException
	 * @throws HibernateException
	 */
	public Collection<AtividadeMonitor> filter(Integer anoAtividade,
			Integer mes, Integer idDiscente, String tituloProjeto,
			Integer idTipoVinculo, Integer idSituacaoMonitor,
			Integer anoProjeto, Integer idCentro, Integer frequencia,
			Date dataInicioValidacaoFreq, Date dataFimValidacaoFreq, Date dataInicioCadastro, Date dataFimCadastro) throws HibernateException,
			DAOException {

		StringBuilder hqlConsulta = new StringBuilder();
		hqlConsulta.append("SELECT atividade.id, atividade.mes, atividade.ano, atividade.frequencia, "
				+ " atividade.dataValidacaoOrientador, dm.id, tipoVinculo.id, tipoVinculo.descricao, dm.ativo, dm.dataInicio, dm.dataFim, " 
				+ " d.matricula, p.nome, projeto.ano, projeto.titulo " 
				+ " FROM AtividadeMonitor atividade "
				+ " INNER JOIN atividade.discenteMonitoria dm "
				+ " INNER JOIN dm.tipoVinculo tipoVinculo "
				+ " INNER JOIN dm.projetoEnsino pm "
				+ " INNER JOIN pm.projeto projeto "
				+ " INNER JOIN projeto.unidade centro "
				+ " INNER JOIN dm.situacaoDiscenteMonitoria s "
				+ " INNER JOIN dm.discente dg "
				+ " INNER JOIN dg.discente d "
				+ " INNER JOIN d.pessoa p");
		hqlConsulta.append(" WHERE atividade.ativo = trueValue() ");

		StringBuilder hqlFiltros = new StringBuilder();

		// Filtros para a busca
		if (frequencia != null) {
			
			//Tinha um erro que quando usava diretamente validadoOrientador(frequencia sendo Boolean)
			//a consulta ficava lenta. Por isso foi alterada.
			
			if(frequencia != 0)
				hqlFiltros.append(" AND atividade.validadoOrientador = trueValue() ");
			else
				hqlFiltros.append(" AND atividade.validadoOrientador = falseValue() ");
			
		}
		
		if (dataInicioValidacaoFreq != null && dataFimValidacaoFreq != null) {
			hqlFiltros.append(" AND cast(atividade.dataValidacaoOrientador as date) >= :dataInicioValidacaoFreq AND cast(atividade.dataValidacaoOrientador as date) <= :dataFimValidacaoFreq ");
		}
		if (dataInicioCadastro != null && dataFimCadastro != null) {
			hqlFiltros.append(" AND cast(atividade.dataCadastro as date) >= :dataInicioCadastro AND cast(atividade.dataCadastro as date) <= :dataFimCadastro ");
		}
		
		if (anoAtividade != null) {
			hqlFiltros.append(" AND atividade.ano = :anoAtividade");
		}
		if (mes != null) {
			hqlFiltros.append(" AND atividade.mes = :mes");
		}
		if (idDiscente != null) {
			hqlFiltros.append(" AND d.id = :idDiscente");
		}

		if (idTipoVinculo != null) {
			hqlFiltros.append(" AND tipoVinculo.id = :idTipoVinculo");
		}

		if (idSituacaoMonitor != null) {
			hqlFiltros.append(" AND s.id = :idSituacaoMonitor");
		}

		if (tituloProjeto != null) {
			hqlFiltros.append(" AND " + UFRNUtils.toAsciiUpperUTF8("projeto.titulo") + " like " + UFRNUtils.toAsciiUTF8(":tituloProjeto"));
		}
		
		if (anoProjeto != null) {
			hqlFiltros.append(" AND projeto.ano = :anoProjeto");
		}

		if (idCentro != null) {
			hqlFiltros.append(" AND centro.id = :idCentro");
		}

		hqlFiltros.append(" AND s.id != :idExcluido");

		hqlFiltros.append(" ORDER BY p.nome, atividade.id");

		hqlConsulta.append(hqlFiltros.toString());
		// Criando consulta
		Query queryConsulta = getSession().createQuery(hqlConsulta.toString());

		// Retirando excluídos da consulta..
		queryConsulta.setInteger("idExcluido",
				SituacaoDiscenteMonitoria.EXCLUIDO);

		
		// Populando os valores dos filtros
		
		/*
		if (frequencia != null) {
			queryConsulta.setInteger("frequencia", frequencia);
		}
		*/
		
		if (dataInicioValidacaoFreq != null && dataFimValidacaoFreq != null) {
			queryConsulta.setDate("dataInicioValidacaoFreq", dataInicioValidacaoFreq);
			queryConsulta.setDate("dataFimValidacaoFreq", dataFimValidacaoFreq);
		}
		
		if (dataInicioCadastro != null && dataFimCadastro != null) {
			queryConsulta.setDate("dataInicioCadastro", dataInicioCadastro);
			queryConsulta.setDate("dataFimCadastro", dataFimCadastro);
		}
		
		if (anoAtividade != null) {
			queryConsulta.setInteger("anoAtividade", anoAtividade);
		}
		if (mes != null) {
			queryConsulta.setInteger("mes", mes);
		}
		if (idDiscente != null) {
			queryConsulta.setInteger("idDiscente", idDiscente);
		}
		if (tituloProjeto != null) {
			queryConsulta.setString("tituloProjeto", "%"+tituloProjeto.toUpperCase()+"%");
		}
		/*
		if (tituloProjeto != null) {
				queryCount.setString("tituloProjeto", tituloProjeto.toUpperCase());
				queryConsulta.setString("tituloProjeto", "%"+tituloProjeto.toUpperCase()+"%");
			}
		*/
		if (idTipoVinculo != null) {
			queryConsulta.setInteger("idTipoVinculo", idTipoVinculo);
		}

		if (idSituacaoMonitor != null) {
			queryConsulta.setInteger("idSituacaoMonitor", idSituacaoMonitor);
		}

		if (anoProjeto != null) {
			queryConsulta.setInteger("anoProjeto", anoProjeto);
		}

		if (idCentro != null) {
			queryConsulta.setInteger("idCentro", idCentro);
		}
		@SuppressWarnings("unchecked")
		ArrayList<Object[]> lista = (ArrayList<Object[]>) queryConsulta.list();
		Collection<AtividadeMonitor> atividades = new ArrayList<AtividadeMonitor>();

		for (Object[] obj : lista) {
			AtividadeMonitor a = new AtividadeMonitor();
			
			int col = 0;
			
			a.setId((Integer)obj[col++]);
			a.setMes((Integer)obj[col++]);
			a.setAno((Integer)obj[col++]);
			a.setFrequencia((Integer)obj[col++]);
			a.setDataValidacaoOrientador((Date)obj[col++]);

			DiscenteMonitoria dm = new DiscenteMonitoria();
			dm.setId((Integer)obj[col++]);
			dm.getTipoVinculo().setId((Integer)obj[col++]);
			dm.getTipoVinculo().setDescricao((String)obj[col++]);
			dm.setAtivo((Boolean)obj[col++]);
			dm.setDataInicio((Date)obj[col++]);
			dm.setDataFim((Date)obj[col++]);

			DiscenteGraduacao d = new DiscenteGraduacao();
			d.setMatricula((Long)obj[col++]);

			Pessoa p = new Pessoa();
			p.setNome((String)obj[col++]);

			d.setPessoa(p);
			dm.setDiscente(d);

			ProjetoEnsino pm = new ProjetoEnsino();
			pm.setAno((Integer) obj[col++]);
			pm.setTitulo((String)obj[col++]);
			dm.setProjetoEnsino(pm);

			a.setDiscenteMonitoria(dm);
			atividades.add(a);
		}

		return atividades;

	}

	/**
	 * Retorna a quantidade de atividades de todos os projetos de um edital
	 * especificado
	 * 
	 * @param idEdital
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public int countAtividadesByEditalMesAno(int idEdital, int mes, int ano,
			Boolean validadoOrientador) throws HibernateException, DAOException {

		StringBuffer hql = new StringBuffer();
		hql.append(" SELECT COUNT(DISTINCT am.id) FROM AtividadeMonitor am ");
		hql.append(" INNER JOIN am.discenteMonitoria dm ");
		hql.append(" INNER JOIN dm.projetoEnsino pm ");
		hql.append(" WHERE am.ativo = trueValue() and (pm.editalMonitoria.id = :idEdital)");

		hql.append(" AND am.mes = :mes ");
		hql.append(" AND am.ano = :ano ");

		if (validadoOrientador != null)
			hql.append(" AND am.validadoOrientador = :validadoOrientador ");

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idEdital", idEdital);
		q.setInteger("mes", mes);
		q.setInteger("ano", ano);

		if (validadoOrientador != null)
			q.setBoolean("validadoOrientador", validadoOrientador);

		Long l = (Long) q.uniqueResult();
		return l.intValue();

	}

	/**
	 * Retorna lista de discentes de monitoria ativos que NÃO enviaram
	 * relatórios de atividades (frequência), no período informado mesInicio até
	 * mesFim do ano informado. A Lista de discentes contempla todos os
	 * discentes que entraram antes do período informado.
	 * 
	 * Este relatório pode ser utilizado para o cancelamento de bolsas dos
	 * monitores que não enviaram o relatório de atividades por mais de 3 meses.
	 * 
	 * @param mesInicio
	 * @param mesFim
	 * @param ano
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */	
	public Collection<DiscenteMonitoria> findByDiscentesSemAtividadeNoPeriodo(
			Integer mesInicio, Integer mesFim, int ano, Integer tipoVinculo)
			throws HibernateException, DAOException { 

		StringBuffer hql = new StringBuffer();
		hql.append(" SELECT dm.projetoEnsino.projeto.ano, "
				+ " dm.projetoEnsino.projeto.titulo, dm.id, dm.dataInicio, dm.dataFim, "
				+ " dm.discente.discente.matricula, dm.discente.discente.pessoa.nome, "
				+ " dm.ativo, dm.discente.id, tipoVinculo.id, tipoVinculo.descricao,"
				+ " dm.discente.discente.pessoa.email, dm.situacaoDiscenteMonitoria.descricao "
				+ " FROM DiscenteMonitoria dm " 
				+ " inner join dm.tipoVinculo tipoVinculo " 
				+ " inner join dm.situacaoDiscenteMonitoria sit ");		
		hql.append(" WHERE (dm.ativo = trueValue() AND sit.id = :assumiuMonitoria AND year(dm.dataInicio) <= :ano AND month(dm.dataInicio) <= :mesInicio) ");
		
		// Monitores que não enviaram a frequência.
		hql.append(" AND (");
		hql.append(" (dm.id NOT IN (SELECT dMonitoria.id FROM AtividadeMonitor atv INNER JOIN atv.discenteMonitoria dMonitoria WHERE atv.ativo = trueValue() and (atv.mes BETWEEN :mesInicio and :mesFim) and atv.ano = :ano AND atv.validadoOrientador = trueValue() AND atv.dataValidacaoOrientador != null)) ");
		
		// Monitores que enviaram frequências, mas que não foram validadas.
		hql.append(" OR (dm.id IN (SELECT dMonitoria.id FROM AtividadeMonitor atv INNER JOIN atv.discenteMonitoria dMonitoria WHERE atv.ativo = trueValue() and (atv.mes BETWEEN :mesInicio and :mesFim) AND atv.ano = :ano AND (atv.validadoOrientador = falseValue() OR atv.dataValidacaoOrientador is null)))");
		hql.append(")");
		
		if( tipoVinculo != null && tipoVinculo!=0 ) {
		    hql.append(" AND dm.tipoVinculo.id = :tipoVinculo ");
		}
		hql.append(" ORDER BY dm.discente.discente.pessoa.nome ");


		if ((mesInicio != null) && (mesInicio > 0) && (mesFim != null) && (mesFim > 0)) {

			Query q = getSession().createQuery(hql.toString());
			q.setInteger("mesInicio", mesInicio);
			q.setInteger("mesFim", mesFim);
			q.setInteger("ano", ano);
			q.setInteger("assumiuMonitoria", SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA);
			
			if (tipoVinculo != null && tipoVinculo !=0) {
				q.setInteger("tipoVinculo", tipoVinculo);
			}
			
			@SuppressWarnings("unchecked")
			List<Object> result = q.list();
			ArrayList<DiscenteMonitoria> discentes = new ArrayList<DiscenteMonitoria>(); 
			
			if (result != null){
				for (int i = 0; i < result.size(); i++) {
					int col = 0;
					
					Object[] obj = (Object[]) result.get(i);
					DiscenteMonitoria dm = new DiscenteMonitoria();
					dm.setProjetoEnsino(new ProjetoEnsino());
					dm.getProjetoEnsino().setAno( (Integer) obj[col++] );
					dm.getProjetoEnsino().setTitulo( (String) obj[col++] );					
					dm.setId( (Integer)obj[col++] );					
					dm.setDataInicio((Date)obj[col++] );
					dm.setDataFim((Date)obj[col++] );					
					dm.setDiscente(new DiscenteGraduacao());
					dm.getDiscente().setMatricula( (Long)obj[col++] );
					dm.getDiscente().setPessoa(new Pessoa());
					dm.getDiscente().getPessoa().setNome( (String) obj[col++] );
					dm.setAtivo( (Boolean) obj[col++] );
					dm.getDiscente().setId( (Integer)obj[col++] );
					dm.getTipoVinculo().setId( (Integer)obj[col++] );
					dm.getTipoVinculo().setDescricao( (String)obj[col++] );
					dm.getDiscente().getPessoa().setEmail( (String) obj[col++] );
					dm.setSituacaoDiscenteMonitoria(new SituacaoDiscenteMonitoria());
					dm.getSituacaoDiscenteMonitoria().setDescricao((String)obj[col++]);
					discentes.add(dm); 
				}
			}
			return discentes;
		} else {
			return null;
		}
	}
}