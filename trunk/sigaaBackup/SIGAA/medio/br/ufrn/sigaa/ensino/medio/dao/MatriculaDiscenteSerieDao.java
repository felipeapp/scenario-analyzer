/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 06/07/2011
 *
 */	
package br.ufrn.sigaa.ensino.medio.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.dominio.Turno;
import br.ufrn.sigaa.ensino.medio.dominio.CursoMedio;
import br.ufrn.sigaa.ensino.medio.dominio.DiscenteMedio;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaComponenteDependencia;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaDiscenteSerie;
import br.ufrn.sigaa.ensino.medio.dominio.Serie;
import br.ufrn.sigaa.ensino.medio.dominio.SituacaoMatriculaSerie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerie;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO com consultas relativas à entidade MatriculaDiscenteSerie.
 * 
 * @author Arlindo
 *
 */
public class MatriculaDiscenteSerieDao extends GenericSigaaDAO{
	
	/**
	 * Retorna as disciplinas já pagas pelo discente informado
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public List<MatriculaComponente> findDisciplinasByDiscente(DiscenteMedio discente) throws DAOException{
		List<Integer> id = new ArrayList<Integer>();
		id.add(discente.getId());
		return findMatriculasByTurmaSerie(null, id, null);
	}
	
	/**
	 * Retorna as disciplinas já pagas pelo discente informado informando a situação.
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public List<MatriculaComponente> findDisciplinasByDiscente(DiscenteMedio discente, Collection<SituacaoMatricula> situacoes) throws DAOException{
		List<Integer> id = new ArrayList<Integer>();
		id.add(discente.getId());
		return findMatriculasByTurmaSerie(null, id, situacoes);
	}
	
	/**
	 * Retorna todas as matrículas ativas de uma TurmaSerie dos discentes informados.
	 * 
	 * @param turmaSerie
	 * @param idsDiscente
	 * @return
	 * @throws DAOException
	 */
	public List<MatriculaComponente> findMatriculasByTurmaSerie(TurmaSerie turmaSerie, List<Integer> idsDiscente, Collection<SituacaoMatricula> situacoes) throws DAOException{
		StringBuffer hql = new StringBuffer();
		String projecao = " m.id, m.discente.id, m.discente.pessoa.id, m.discente.pessoa.nome, m.discente.pessoa.nomeAscii, " +
				" m.situacaoMatricula.id, m.situacaoMatricula.descricao, m.ano, "+ 
				 " m.componente.id, ccd.nome, cc.nivel, ccd.chTotal, ccd.chAula, ccd.chLaboratorio, und.id, und.nome, und.sigla, m.serie.id, " +
				 " m.serie.numero, m.serie.descricao, m.mediaFinal, m.numeroFaltas, m.recuperacao, " +
				 " m.serie.cursoMedio.id, m.turma.id "; 
		
		hql.append("select "+projecao+" from MatriculaComponente m " +
				" inner join m.componente cc " +
				" inner join cc.detalhes ccd " +
				" inner join cc.unidade und " +
				" inner join m.serie s " +
				" inner join m.serie.cursoMedio c " +
				" left join m.turma t " +
				" inner join m.discente d " +
				" where 1 = 1 ");
		
		if (ValidatorUtil.isNotEmpty(idsDiscente))
			hql.append(" and m.discente.id in "+UFRNUtils.gerarStringIn(idsDiscente));

		if (ValidatorUtil.isNotEmpty(situacoes))
			hql.append(" and m.situacaoMatricula.id in " + UFRNUtils.gerarStringIn(situacoes) );
		
		if (ValidatorUtil.isNotEmpty(turmaSerie)){
		    hql.append(" and t.id in ( ");
		    hql.append("  	select tsa.turma.id from TurmaSerieAno tsa ");
		    hql.append("  	inner join tsa.turma t ");
		    hql.append("  	inner join tsa.turmaSerie ts ");
		    hql.append("  	where ts.id = "+turmaSerie.getId());
		    hql.append("  ) ");		
		}
	    
		hql.append(" order by m.discente.id,  m.ano, m.serie.numero , m.serie.descricao, m.ano, ccd.nome ");
		
        Query q = getSession().createQuery(hql.toString());
        @SuppressWarnings("unchecked")
        Collection<Object[]> res = q.list();
        List<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>(0);
        if (res != null ) {
        	for (Object[] reg : res) {
        		int i = 0;
        		MatriculaComponente mat = new MatriculaComponente((Integer) reg[i++]);
        		
        		mat.setDiscente(new Discente((Integer) reg[i++]));
        		mat.getDiscente().setPessoa(new Pessoa((Integer) reg[i++]));
        		mat.getDiscente().getPessoa().setNome((String) reg[i++]);
        		mat.getDiscente().getPessoa().setNomeAscii((String) reg[i++]);
        		
        		mat.setSituacaoMatricula(new SituacaoMatricula());
        		mat.getSituacaoMatricula().setId((Integer) reg[i++]);
        		mat.getSituacaoMatricula().setDescricao((String) reg[i++]);
        		mat.setAno((Short)reg[i++]);
        		
        		mat.setComponente(new ComponenteCurricular());
        		mat.getComponente().setId((Integer) reg[i++]);
        		
        		mat.getComponente().setDetalhes(new ComponenteDetalhes());
        		mat.getComponente().getDetalhes().setNome((String) reg[i++]);
        		mat.getComponente().setNivel((Character) reg[i++]);
        		mat.getComponente().getDetalhes().setChTotal((Integer) reg[i++]);
        		mat.getComponente().getDetalhes().setChAula((Integer) reg[i++]);
        		mat.getComponente().getDetalhes().setChLaboratorio((Integer) reg[i++]);
        		mat.getComponente().setUnidade(new Unidade((Integer) reg[i++]));
        		mat.getComponente().getUnidade().setNome((String) reg[i++]);
        		mat.getComponente().getUnidade().setSigla((String) reg[i++]);
        		
        		mat.setDetalhesComponente(mat.getComponente().getDetalhes());
        		
        		mat.setSerie(new Serie());
        		mat.getSerie().setId((Integer) reg[i++]);
        		mat.getSerie().setNumero((Integer) reg[i++]);
        		mat.getSerie().setDescricao((String) reg[i++]);
        		mat.setMediaFinal((Double) reg[i++]);
        		
        		Integer faltas = (Integer) reg[i++];
        		if (faltas == null)
        			faltas = 0;
        		
        		mat.setNumeroFaltas(faltas);
        		mat.setRecuperacao((Double) reg[i++]);        		
        		mat.getSerie().setCursoMedio(new CursoMedio((Integer) reg[i++]));
        		
        		Integer idTurma = (Integer) reg[i++];
        		mat.setTurma(new Turma());
        		mat.getTurma().setId(idTurma != null ? idTurma : 0);
        		mat.getTurma().setAno(mat.getAno());
        		mat.getTurma().setDisciplina(mat.getComponente());
        			
        		matriculas.add(mat);
			}
        }		
		return matriculas;			
	}	
	
	/**
	 * Verifica se o discente já encontra-se matricula na série.
	 * @param idUnidade
	 * @return
	 * @throws DAOException
	 */
	public Boolean discenteJaMatriculadoEmSerie(DiscenteMedio discenteMedio, TurmaSerie turmaSerie) throws DAOException {
		
	    Criteria c = getSession().createCriteria(MatriculaDiscenteSerie.class);
        c.setProjection(Projections.count("id"));
        c.add( Restrictions.eq("discenteMedio", discenteMedio) );
        c.add( Restrictions.eq("turmaSerie", turmaSerie) );
        c.add( Restrictions.in("situacaoMatriculaSerie", new SituacaoMatriculaSerie[]{SituacaoMatriculaSerie.MATRICULADO, SituacaoMatriculaSerie.APROVADO} ) );
                
        return (Integer)c.uniqueResult() > 0;
		
	}
	
	/**
	 * Verifica se o discente já encontra-se matricula na série.
	 * @param idUnidade
	 * @return
	 * @throws DAOException
	 */
	public MatriculaDiscenteSerie findMatriculaSerieAtivasByTurmaSerie(DiscenteMedio discenteMedio, TurmaSerie turmaSerie) throws DAOException {
		
	    Criteria c = getSession().createCriteria(MatriculaDiscenteSerie.class);
        c.add( Restrictions.eq("discenteMedio", discenteMedio) );
        c.add( Restrictions.eq("turmaSerie", turmaSerie) );
        c.add( Restrictions.in("situacaoMatriculaSerie", new SituacaoMatriculaSerie[]{SituacaoMatriculaSerie.MATRICULADO, SituacaoMatriculaSerie.APROVADO} ) );
                
        return (MatriculaDiscenteSerie) c.setMaxResults(1).uniqueResult();
		
	}
	
	/**
	 * Busca matrículas em uma turma de forma aleatória e com a quantidade especificada como parâmetro.
	 * As matrículas devem estar com situação ATIVO ou ATIVO_DEPENDENCIA.
	 * 
	 * @param turma
	 * @param quantidade
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaDiscenteSerie> findAlunosAleatoriosByTurma(TurmaSerie turma, Integer quantidade) throws DAOException {
		Query q = getSession().createQuery("from MatriculaDiscenteSerie mds " +
				" where mds.turmaSerie.id = :turma " +
				" and mds.situacaoMatriculaSerie.id = " +SituacaoMatriculaSerie.MATRICULADO.getId()+
				" order by random()");
		q.setInteger("turma", turma.getId());
		if (quantidade != null) {
			q.setMaxResults(quantidade);
		}
		
		@SuppressWarnings("unchecked")
		List <MatriculaDiscenteSerie> rs = q.list();
		return rs;
	}
	
	/**
	 * Retorna todas as disciplinas ativas referentes a uma matricula de ensino médio do discente.
	 * 
	 * @param turmaSerie
	 * @param idsDiscente
	 * @return
	 * @throws DAOException
	 */
	public List<MatriculaComponente> findDisciplinasByMatriculasDiscenteSerie(TurmaSerie turmaSerie, List<Integer> idsDiscente) throws DAOException{
		
		StringBuffer hql = new StringBuffer();
		String projecao = " m.id, m.discente.id, m.situacaoMatricula.id, m.ano, "+ 
				 " m.componente.id, m.serie.id, m.turma.id, m.turma.disciplina.id "; 
		
		hql.append(
				"select "+projecao+
				" from MatriculaComponente m, MatriculaDiscenteSerie mds " +
				" inner join m.turma t " +
				" inner join mds.turmaSerie ts " +
				" inner join mds.discenteMedio.discente d " +
				" ,TurmaSerieAno tsa " +
				" where d.status in "+UFRNUtils.gerarStringIn(new Integer[]{StatusDiscente.ATIVO, 
						StatusDiscente.ATIVO_DEPENDENCIA})+						
				" and mds.situacaoMatriculaSerie.id = " +SituacaoMatriculaSerie.MATRICULADO.getId());		
		
		hql.append(" and m.discente.id in "+UFRNUtils.gerarStringIn(idsDiscente));
		hql.append(" and m.serie.id = ts.serie.id and m.discente.id = d.id ");
		hql.append(" and ts.id = "+turmaSerie.getId());
		hql.append(" and tsa.turma.id = t.id");
		hql.append(" and tsa.turmaSerie.id = ts.id");
				
		hql.append(" order by m.discente.id, m.serie.id ");
		
        Query q = getSession().createQuery(hql.toString());
        @SuppressWarnings("unchecked")
        Collection<Object[]> res = q.list();
        List<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>(0);
        if (res != null ) {
        	for (Object[] reg : res) {
        		MatriculaComponente mat = new MatriculaComponente((Integer) reg[0]);
        		mat.setDiscente(new Discente((Integer) reg[1]));
        		mat.setSituacaoMatricula(new SituacaoMatricula());
        		mat.getSituacaoMatricula().setId((Integer) reg[2]);
        		mat.setAno((Short)reg[3]);
        		mat.setComponente(new ComponenteCurricular());
        		mat.getComponente().setId((Integer) reg[4]);
        		mat.setSerie(new Serie());
        		mat.getSerie().setId((Integer) reg[5]);
        		mat.setTurma(new Turma());
        		mat.getTurma().setId((Integer) reg[6]);
        		mat.getTurma().setDisciplina(new ComponenteCurricular());
        		mat.getTurma().getDisciplina().setId((Integer) reg[7]);
        		matriculas.add(mat);
			}
        }		
		return matriculas;			
	}
	
	/**
	 * Método responsável pelo retorno de todas as matrículas em séries do discente informado
	 * @param discenteMedio
	 * @return
	 * @throws DAOException
	 */
	public List<MatriculaDiscenteSerie> findAllMatriculasByDiscente(DiscenteMedio discenteMedio, Integer ano, SituacaoMatriculaSerie... situacaoMatriculaSerie ) throws DAOException {
		
		List<Integer> idSituacoesMatriculaSerie = new ArrayList<Integer>();
		for (SituacaoMatriculaSerie sms : situacaoMatriculaSerie) {
			idSituacoesMatriculaSerie.add(sms.getId());
		}
		
		StringBuffer hql = new StringBuffer();
		String projecao = " m.id, m.discenteMedio.id, m.situacaoMatriculaSerie.id, m.situacaoMatriculaSerie.descricao, " +
				" m.turmaSerie.id, m.turmaSerie.ano, m.turmaSerie.nome, m.turmaSerie.serie.descricao, " +
				" m.turmaSerie.serie.numero, m.dependencia ";
		
		hql.append(" select "+projecao+" from MatriculaDiscenteSerie m ");
		
	    hql.append(" inner join m.discenteMedio d ");
	    hql.append(" inner join m.turmaSerie t ");
	    hql.append(" inner join t.serie ");
	    
	    hql.append(" where d.id = "+discenteMedio.getId());
	    
	    if (idSituacoesMatriculaSerie.size() > 0)	    
	    	hql.append(" and m.situacaoMatriculaSerie.id in "+UFRNUtils.gerarStringIn(idSituacoesMatriculaSerie));
	    
	    if (ano != null && ano > 0)
	    	hql.append(" and  m.turmaSerie.ano = "+ano);
	    
	    hql.append(" order by m.turmaSerie.ano desc, m.dependencia ");
	    
	    @SuppressWarnings("unchecked")
	    List<MatriculaDiscenteSerie> lista = (List<MatriculaDiscenteSerie>) HibernateUtils.parseTo(
	    		getSession().createQuery(hql.toString()).list(), projecao, MatriculaDiscenteSerie.class, "m");
        return lista;
		
	}	
	
	/**
	 * Busca todas as disciplinas de um discente com matrículas de uma determinada série, 
	 * conforme as situações de matrícula informadas.
	 *
	 * @param idDiscente
	 * @param otimizado
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findDisciplinas(DiscenteAdapter discente, 
			MatriculaDiscenteSerie matriculaDiscenteSerie, boolean otimizado, Integer... situacaoMatricula ) 
			throws DAOException {
		
		try {
			StringBuffer hql = new StringBuffer();
			if (otimizado) {
				hql.append("select distinct m.turma.id, m.componente.id, m.componente.detalhes.nome, m.componente.codigo ");
			} else {
				hql.append("select distinct m.turma ");
			}
			hql.append("from MatriculaComponente m ");
			
			if (!otimizado) {
				hql.append(" left join fetch m.turma.horarios ");
			}
			hql.append(" where m.discente.id = :idDiscente and "
					 + " m.turma.id > 0 and m.situacaoMatricula.id "
					 + " in "+UFRNUtils.gerarStringIn(situacaoMatricula));
			hql.append(" and  m.turma.id in ( "
						+ "	select tsa.turma.id from TurmaSerieAno tsa "
						+ " inner join tsa.turma t "
						+ "	inner join tsa.turmaSerie ts "
						+ "	where ts.id = :idTurmaSerie )");
			
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("idDiscente", discente.getId());
			q.setInteger("idTurmaSerie", matriculaDiscenteSerie.getTurmaSerie().getId());
			
			if (!otimizado) {
				@SuppressWarnings("unchecked")
				Collection<Turma> lista = q.list();
				return lista;
			} else {
				ArrayList<Turma> turmas = new ArrayList<Turma>(0);
				@SuppressWarnings("unchecked")
				List<Object[]> res = q.list();
				if (res != null) {
					for (Object[] id : res) {
						Turma t = new Turma((Integer) id[0]);
						t.setDisciplina(new ComponenteCurricular(
								(Integer) id[1]));
						t.getDisciplina().setDetalhes(new ComponenteDetalhes());
						t.getDisciplina().getDetalhes().setNome( (String) id[2]);
						t.getDisciplina().setCodigo( (String) id[3]);
						turmas.add(t);
					}
				}
				return turmas;
			}

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Método responsável pelo retorno da série nivelada do discente, respeitando o ano em vigência.
	 * 
	 * @param idUnidade
	 * @return
	 * @throws DAOException
	 */
	public MatriculaDiscenteSerie findSerieAtualDiscente(DiscenteMedio discenteMedio, int anoAtual) throws DAOException {
		
		Criteria c = getSession().createCriteria(MatriculaDiscenteSerie.class);
		Criteria cTurmaSerie = c.createCriteria("turmaSerie");
		c.add( Restrictions.eq("discenteMedio", discenteMedio) );
		cTurmaSerie.add( Restrictions.eq("ano", anoAtual) );
		c.add( Restrictions.eq("situacaoMatriculaSerie", SituacaoMatriculaSerie.MATRICULADO ) );
		c.add( Restrictions.eq("dependencia", false ) );
		
		return (MatriculaDiscenteSerie) c.uniqueResult();
		
	}
	
	/**
	 * Método responsável pelo retorno da série nivelada do discente, respeitando o ano em vigência.
	 * 
	 * @param idUnidade
	 * @return
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	public MatriculaDiscenteSerie findSerieAnteriorDiscente( DiscenteMedio discenteMedio, Integer ano ) throws DAOException, NegocioException {
		
		if (ValidatorUtil.isEmpty(ano))
			ano = CalendarUtils.getAnoAtual();
		
		MatriculaDiscenteSerie serieValida = findSerieAtualDiscente(discenteMedio, ano);
		int numeroSerie;
		if ( ValidatorUtil.isEmpty(serieValida) ){
			serieValida = findUltimaSerieRegularDiscente(discenteMedio, ano);
			return serieValida;
		} else {	
			numeroSerie = serieValida.getTurmaSerie().getSerie().getNumero() - 1;
		}
		Criteria c = getSession().createCriteria(MatriculaDiscenteSerie.class);
		Criteria cTurmaSerie = c.createCriteria("turmaSerie");
		Criteria cSerie = cTurmaSerie.createCriteria("serie");
		c.add( Restrictions.eq("discenteMedio", discenteMedio) );
		cSerie.add( Restrictions.eq("cursoMedio", serieValida.getTurmaSerie().getSerie().getCursoMedio()) );
		cSerie.add( Restrictions.eq("numero", numeroSerie) );
		
		c.add( Restrictions.eq("dependencia", false ) );
		
		return (MatriculaDiscenteSerie) c.uniqueResult();
		
	}
	
	/**
	 * Método responsável por retornar a última matrícula do tipo regular do aluno, no qual o ano seja menor que o ano atual.
	 * @param discenteMedio
	 * @return
	 * @throws DAOException
	 */
	public MatriculaDiscenteSerie findUltimaSerieRegularDiscente( DiscenteMedio discenteMedio, Integer ano) throws DAOException {
		
		Criteria c = getSession().createCriteria(MatriculaDiscenteSerie.class);
		Criteria cTurmaSerie = c.createCriteria("turmaSerie");
		Criteria cSerie = cTurmaSerie.createCriteria("serie");
		c.add( Restrictions.eq("discenteMedio", discenteMedio) );
		cSerie.add( Restrictions.eq("cursoMedio",discenteMedio.getCurso()) );
		cTurmaSerie.add( Restrictions.lt("ano", ano) );
		c.add( Restrictions.eq("dependencia", false) );
		
		c.add(Restrictions.ne("situacaoMatriculaSerie", SituacaoMatriculaSerie.CANCELADO));
		
		cTurmaSerie.addOrder(Order.desc("ano"));
		
		return (MatriculaDiscenteSerie) c.setMaxResults(1).uniqueResult();
		
	}
	
	/**
	 * Método responsável pelo retorno de todas as matrículas em dependência do discente informado
	 * @param discenteMedio
	 * @return
	 * @throws DAOException
	 */
	public List<MatriculaComponenteDependencia> findAllMatriculasDependenciaByDiscente(DiscenteMedio discenteMedio) throws DAOException {
		
		StringBuffer hql = new StringBuffer();
		String projecao = " m.id, m.matriculaSerieRegular.id, m.matriculaDependencia.id, " +
				"  m.matriculaRegular.serie.descricao,  m.matriculaRegular.serie.numero," +
				"  m.matriculaSerieRegular.turmaSerie.serie.id, " +
				"  m.matriculaSerieRegular.turmaSerie.serie.descricao,  m.matriculaSerieRegular.turmaSerie.serie.numero ";
		
		hql.append(" select "+projecao+" from MatriculaComponenteDependencia m ");
		
	    hql.append(" inner join m.matriculaDependencia md ");
	    hql.append(" inner join m.matriculaRegular mr ");
	    hql.append(" inner join m.matriculaRegular.serie s ");
	    hql.append(" inner join m.matriculaSerieRegular ms ");
	    hql.append(" inner join m.matriculaSerieRegular.turmaSerie ts ");
	    hql.append(" inner join m.matriculaSerieRegular.turmaSerie.serie sm ");
	        
	    hql.append(" where md.discente.id = "+discenteMedio.getId());
	    	    
	    @SuppressWarnings("unchecked")
	    List<MatriculaComponenteDependencia> lista = (List<MatriculaComponenteDependencia>) HibernateUtils.parseTo(
	    		getSession().createQuery(hql.toString()).list(), projecao, MatriculaComponenteDependencia.class, "m");
        return lista;
		
	}	
	
	/**
	 * Retorna os objetos MatriculaDiscenteSerie em um mapa utilizando o discente como chave,
	 * inserindo as matrículas em disciplinas em uma coleção transiente no objeto MatriculaDiscenteSerie. 
	 * Considerando o ano passado por parâmetro.
	 * 
	 * @param turmaSerie
	 * @param idsDiscente
	 * @return
	 * @throws DAOException
	 */
	public Map<DiscenteMedio, List<MatriculaDiscenteSerie>> findMatriculaDiscenteSerieByAno(int ano, int idCurso, int idSerie, SituacaoMatriculaSerie...situacoesMatriculaSerie ) throws DAOException{
		
		List<Object[]> lista = retornoConsultaDiscenteSerieByAnoCurso(ano, idCurso, idSerie, situacoesMatriculaSerie);
        
		Map<DiscenteMedio, List<MatriculaDiscenteSerie>> map = new LinkedHashMap<DiscenteMedio, List<MatriculaDiscenteSerie>>();
        if (lista != null ) {
        	DiscenteMedio discente = new DiscenteMedio();
        	MatriculaDiscenteSerie mds = new MatriculaDiscenteSerie();
        	mds.setDiscenteMedio(new DiscenteMedio());
        	mds.setTurmaSerie(new TurmaSerie());
        	List<MatriculaDiscenteSerie> listMatSerie = new ArrayList<MatriculaDiscenteSerie>();
        	
        	for (Object[] reg : lista) {	
        		
        		if ( discente.getId() != (Integer) reg[0] ){
        			/* Inserindo registros no mapa para o discente anterior */
        			if ( discente.getId() != 0 ) {
        				map.put(discente, listMatSerie);
        			}
        			discente = new DiscenteMedio((Integer) reg[0]);
        			discente.setMatricula(Long.valueOf(((BigInteger) reg[1]).toString()));
        			discente.getPessoa().setNome((String) reg[2]);
        			
        			listMatSerie = new ArrayList<MatriculaDiscenteSerie>();
        		}
        		
        		Integer idMatriculaDiscenteSerie = (Integer) reg[15];
        		
        		if ( ValidatorUtil.isEmpty(idMatriculaDiscenteSerie) ){
        			if ( (mds.getDiscenteMedio().getId() != (Integer) reg[0] ) || (mds.getTurmaSerie().getId() != (Integer) reg[18]) ) {
	        			mds = new MatriculaDiscenteSerie();
	        			mds.setDiscenteMedio(new DiscenteMedio((Integer) reg[0]));
	        			mds.getDiscenteMedio().setMatricula(Long.valueOf(((BigInteger) reg[1]).toString()));
	        			mds.getDiscenteMedio().getPessoa().setNome((String) reg[2]);
	        			mds.setSituacaoMatriculaSerie(new SituacaoMatriculaSerie());
	    				mds.setTurmaSerie(new TurmaSerie((Integer) reg[18]));
	    				mds.getTurmaSerie().setNome((String) reg[19]);
	    				mds.getTurmaSerie().setSerie(new Serie((Integer) reg[20]));
	    				mds.getTurmaSerie().getSerie().setNumero((Integer) reg[21]);
	    				mds.setDependencia((Boolean) reg[24]);
	    				
	    				listMatSerie.add(mds);
        			}	
        		} else {
        			if ( mds.getId() == 0 || mds.getId() != idMatriculaDiscenteSerie ){
        				
        				/* Instanciando próximo objeto para MatriculaDiscenteSerie */
        				mds = new MatriculaDiscenteSerie(idMatriculaDiscenteSerie);
        				mds.setDiscenteMedio(new DiscenteMedio((Integer) reg[0]));
	        			mds.getDiscenteMedio().setMatricula(Long.valueOf(((BigInteger) reg[1]).toString()));
	        			mds.getDiscenteMedio().getPessoa().setNome((String) reg[2]);
	        			mds.setSituacaoMatriculaSerie(new SituacaoMatriculaSerie((Integer) reg[16]));
        				mds.getSituacaoMatriculaSerie().setDescricao((String) reg[17]);
        				mds.setTurmaSerie(new TurmaSerie((Integer) reg[18]));
        				mds.getTurmaSerie().setNome((String) reg[19]);
        				mds.getTurmaSerie().setSerie(new Serie((Integer) reg[20]));
        				mds.getTurmaSerie().getSerie().setNumero((Integer) reg[21]);
        				mds.setDependencia((Boolean) reg[24]);
        				
        				listMatSerie.add(mds);
        			}	
        		}
        		
        		MatriculaComponente mat = new MatriculaComponente((Integer) reg[3]);
        		mat.setSituacaoMatricula(new SituacaoMatricula((Integer) reg[4]));
        		mat.getSituacaoMatricula().setDescricao((String) reg[5]);
        		mat.setAno((Short)reg[6]);
        		mat.setComponente(new ComponenteCurricular((Integer) reg[7]));
        		mat.getComponente().setDetalhes(new ComponenteDetalhes());
        		mat.getComponente().getDetalhes().setNome((String) reg[8]);
        		mat.setSerie(new Serie((Integer) reg[9]));
        		mat.getSerie().setNumero((Integer) reg[10]);
        		mat.getSerie().setDescricao((String) reg[25]);
        		if ( (Integer) reg[11] != null ){
	        		mat.setTurma(new Turma((Integer) reg[11]));
	        		mat.getTurma().setCodigo((String) reg[12]);
	        		mat.getTurma().setDisciplina(new ComponenteCurricular((Integer) reg[13]));
	        		mat.getTurma().getDisciplina().setNome((String) reg[14]);
        		}	
        		mat.setDiscente(discente);
        		mds.getMatriculasDisciplinas().add(mat);
        		
        	}
        	if (discente.getId() > 0){
	        	map.put(discente, listMatSerie);
        	}	
        }		
		return map;			
	}
	
	/**
	 * Retorna uma lista de série que possua discentes matriculados.
	 * 
	 * @param turmaSerie
	 * @param idsDiscente
	 * @return
	 * @throws DAOException
	 */
	public Collection<Serie> findSerieWithMatriculaByAno(int ano, int idCurso, int idSerie, SituacaoMatriculaSerie...situacoesMatriculaSerie) throws DAOException{
	
		List<Object[]> lista = retornoConsultaDiscenteSerieByAnoCurso(ano, idCurso, idSerie, situacoesMatriculaSerie);
		
		Collection<Serie> listaSerie = new ArrayList<Serie>();
	    if (lista != null ) {
        	Serie serie = new Serie();
        	TurmaSerie turma = new TurmaSerie();
        	MatriculaDiscenteSerie aluno = new MatriculaDiscenteSerie();
        	aluno.setDiscenteMedio(new DiscenteMedio());
        	aluno.setTurmaSerie(new TurmaSerie());
        	
        	for (Object[] reg : lista) {	
        		if ((Integer) reg[15] == null){
        			continue;
        		}
        		if ( serie.getId() == 0 || serie.getId() != (Integer) reg[9] ){
        			/* Instanciando próximo objeto para Serie */
        			serie = new Serie((Integer) reg[9]);
        			serie.setNumero((Integer) reg[10]);
        			serie.setDescricao((String) reg[25]);
        			serie.setCursoMedio(new CursoMedio((Integer) reg[22]));
        			serie.getCursoMedio().setNome((String) reg[23]);
        			serie.getCursoMedio().setCodigo((String) reg[26]);
        			serie.getCursoMedio().setTurno(new Turno((String) reg[27]));

        			listaSerie.add(serie);
        		}
        		
        		if ( turma.getId() == 0 || turma.getId() != (Integer) reg[18] ){
        			turma = (new TurmaSerie((Integer) reg[18]));
        			turma.setNome((String) reg[19]);
        			turma.setSerie(new Serie((Integer) reg[20]));
        			turma.getSerie().setNumero((Integer) reg[21]);
        			turma.getSerie().setDescricao((String) reg[25]);
        			
        			serie.getTurmas().add(turma);
        		}
        
        		Integer idMatriculaDiscenteSerie = (Integer) reg[15];
        		if ( ValidatorUtil.isEmpty(idMatriculaDiscenteSerie) ){
        			/* Tratando caso de alunos com implantações de histórico. */
        			if ((aluno.getDiscenteMedio().getId() != (Integer) reg[0] || (aluno.getTurmaSerie().getId() != (Integer) reg[18]))) {
        				
        				aluno = new MatriculaDiscenteSerie((Integer) reg[15] != null ? (Integer) reg[15] : 0);
	        			aluno.setDiscenteMedio(new DiscenteMedio((Integer) reg[0]));
	        			aluno.getDiscenteMedio().setMatricula(Long.valueOf(((BigInteger) reg[1]).toString()));
	        			aluno.getDiscenteMedio().getPessoa().setNome((String) reg[2]);
	        			aluno.setSituacaoMatriculaSerie(new SituacaoMatriculaSerie((Integer) reg[16] != null ? (Integer) reg[16] : 0));
	        			aluno.getSituacaoMatriculaSerie().setDescricao((String) reg[17]);
	        			aluno.setTurmaSerie(new TurmaSerie((Integer) reg[18]));
	        			aluno.getTurmaSerie().setNome((String) reg[19]);
	        			aluno.getTurmaSerie().setSerie(new Serie((Integer) reg[20]));
	        			aluno.getTurmaSerie().getSerie().setNumero((Integer) reg[21]);
	        			aluno.setDependencia((Boolean) reg[24]);
	        			aluno.setMatriculasDisciplinas(new ArrayList<MatriculaComponente>());
	
	        			turma.getAlunos().add(aluno);
        			}	
        		} else {
	        		if ( aluno.getId() == 0 || aluno.getId() != (Integer) reg[15] ){
	        			
	        			/* Instanciando próximo objeto para MatriculaDiscenteSerie */
	        			aluno = new MatriculaDiscenteSerie((Integer) reg[15] != null ? (Integer) reg[15] : 0);
	        			aluno.setDiscenteMedio(new DiscenteMedio((Integer) reg[0]));
	        			aluno.getDiscenteMedio().setMatricula(Long.valueOf(((BigInteger) reg[1]).toString()));
	        			aluno.getDiscenteMedio().getPessoa().setNome((String) reg[2]);
	        			aluno.setSituacaoMatriculaSerie(new SituacaoMatriculaSerie((Integer) reg[16] != null ? (Integer) reg[16] : 0));
	        			aluno.getSituacaoMatriculaSerie().setDescricao((String) reg[17]);
	        			aluno.setTurmaSerie(new TurmaSerie((Integer) reg[18]));
	        			aluno.getTurmaSerie().setNome((String) reg[19]);
	        			aluno.getTurmaSerie().setSerie(new Serie((Integer) reg[20]));
	        			aluno.getTurmaSerie().getSerie().setNumero((Integer) reg[21]);
	        			aluno.setDependencia((Boolean) reg[24]);
	        			aluno.setMatriculasDisciplinas(new ArrayList<MatriculaComponente>());
	
	        			turma.getAlunos().add(aluno);
	        		}
        		}	
        		
        		MatriculaComponente mat = new MatriculaComponente((Integer) reg[3]);
        		mat.setSituacaoMatricula(new SituacaoMatricula((Integer) reg[4]));
        		mat.getSituacaoMatricula().setDescricao((String) reg[5]);
        		mat.setAno((Short)reg[6]);
        		mat.setComponente(new ComponenteCurricular((Integer) reg[7]));
        		mat.getComponente().setDetalhes(new ComponenteDetalhes());
        		mat.getComponente().getDetalhes().setNome((String) reg[8]);
        		mat.setSerie(new Serie((Integer) reg[9]));
        		mat.getSerie().setNumero((Integer) reg[10]);
        		if ( (Integer) reg[11] != null ){
	        		mat.setTurma(new Turma((Integer) reg[11]));
	        		mat.getTurma().setCodigo((String) reg[12]);
	        		mat.getTurma().setDisciplina(new ComponenteCurricular((Integer) reg[13]));
	        		mat.getTurma().getDisciplina().setNome((String) reg[14]);
        		}
        		
        		aluno.getMatriculasDisciplinas().add(mat);
        	}

	    }	
		return listaSerie;
	}
	
	/**
	 * Método responsável pela manipulação da query utilizada para consultas de discentes por serie, ano e curso.
	 */
	@SuppressWarnings("unchecked")
	private List<Object[]> retornoConsultaDiscenteSerieByAnoCurso(int ano, int idCurso, int idSerie, SituacaoMatriculaSerie...situacoesMatriculaSerie) throws HibernateException, DAOException{
		StringBuffer sql = new StringBuffer();
		String projecao = 
				"d.id_discente, d.matricula, p.nome as p_nome, mc.id_matricula_componente, sitMatricula.id_situacao_matricula, " +
				"sitMatricula.descricao as sitMatricula_desricao, mc.ano, cc.id_disciplina, ccd.nome as ccd_nome, s.id_serie, " +
				"s.numero, t.id_turma, t.codigo as codigo_turma, ccTurma.id_disciplina as id_disciplina_turma, ccdTurma.nome as ccdTurma_nome, " +
				"mds.id_matricula_discente_serie as matricula_serie, sitSerie.id_situacao_matricula_serie, sitSerie.descricao as sitSerie_descricao, ts.id_turma_serie, ts.nome as ts_nome," +
				"s.id_serie, s.numero as serie_numero, cursoMedio.id_curso, curso.nome as curso_nome, mds.dependencia, " +
				"s.descricao, curso.codigo as curso_codigo, turno.sigla "; 
		
		String projecaoToHistoricoImplantado = 
				"d.id_discente, d.matricula, p.nome as p_nome, mc.id_matricula_componente, sitMatricula.id_situacao_matricula, " +
				"sitMatricula.descricao as sitMatricula_desricao, mc.ano, cc.id_disciplina, ccd.nome as ccd_nome, s.id_serie, " +
				"s.numero, null as id_turma, null as codigo_turma, null as id_disciplina_turma, null as ccdTurma_nome, " +
				"mds.id_matricula_discente_serie as matricula_serie, sitSerie.id_situacao_matricula_serie, sitSerie.descricao as sitSerie_descricao, ts.id_turma_serie, ts.nome as ts_nome, " +
				"s.id_serie, s.numero as serie_numero, cursoMedio.id_curso, curso.nome as curso_nome, falseValue() as dependencia, " +
				"s.descricao, curso.codigo as curso_codigo, turno.sigla "; 
		
		/* SQL para retornar aluno de ensino médio com relacionamento de turma e componente curricular */
		sql.append(
				"select "+projecao+
				"from medio.matricula_discente_serie mds " + 
				"inner join medio.situacao_matricula_serie sitSerie on mds.id_situacao_matricula_serie = sitSerie.id_situacao_matricula_serie " + 

				"inner join medio.turma_serie ts on mds.id_turma_serie = ts.id_turma_serie " + 
				"inner join medio.serie s on ts.id_serie = s.id_serie " +
				"inner join medio.turma_serie_ano tsa on tsa.id_turma_serie = ts.id_turma_serie " + 

				"inner join medio.discente_medio dm on mds.id_discente = dm.id_discente " + 
				"inner join discente d on dm.id_discente = d.id_discente " + 
				"inner join comum.pessoa p on d.id_pessoa = p.id_pessoa " + 

				"inner join ensino.matricula_componente mc on mc.id_discente = d.id_discente and mc.id_serie = s.id_serie " +
				"inner join ensino.situacao_matricula sitMatricula on mc.id_situacao_matricula = sitMatricula.id_situacao_matricula " + 
				"inner join ensino.componente_curricular cc on mc.id_componente_curricular = cc.id_disciplina " + 
				"inner join ensino.componente_curricular_detalhes ccd on cc.id_detalhe = ccd.id_componente_detalhes " + 

				"inner join medio.curso_medio cursoMedio on s.id_curso = cursoMedio.id_curso " + 
				"inner join curso curso on curso.id_curso = cursoMedio.id_curso " + 
				"inner join ensino.turno turno on turno.id_turno = cursoMedio.id_turno " + 

				"inner join ensino.turma t on t.id_turma = tsa.id_turma and mc.id_turma = t.id_turma " +
				"inner join ensino.componente_curricular ccTurma on t.id_disciplina = ccTurma.id_disciplina " + 
				"inner join ensino.componente_curricular_detalhes ccdTurma on ccTurma.id_detalhe = ccdTurma.id_componente_detalhes ");
				
		sql.append(
				" where d.status in "+UFRNUtils.gerarStringIn(new Integer[]{StatusDiscente.ATIVO, StatusDiscente.ATIVO_DEPENDENCIA})+						
				" and mc.ano = :ano" +
				" and ts.ano = :ano");
		if(situacoesMatriculaSerie.length > 0)
			sql.append(
				" and mds.id_situacao_matricula_serie in " +UFRNUtils.gerarStringIn(situacoesMatriculaSerie));		
		if(idCurso > 0)
			sql.append(
				" and curso.id_curso = :curso");	
		if(idSerie > 0)
			sql.append(
			" and s.id_serie = :serie");
		
		sql.append(
				" UNION ");
		
		/* SQL para retornar aluno de ensino médio sem relacionamento de turma e componente curricular,
		 * ou seja, alunos com históricos implantados. */
		sql.append(
				"select "+projecaoToHistoricoImplantado+
				"from medio.discente_medio dm " + 
				"inner join discente d on dm.id_discente = d.id_discente " +  
				"inner join comum.pessoa p on d.id_pessoa = p.id_pessoa " +  
				
				"inner join ensino.matricula_componente mc on mc.id_discente = d.id_discente " + 
				"inner join ensino.situacao_matricula sitMatricula on mc.id_situacao_matricula = sitMatricula.id_situacao_matricula " +  
				"inner join ensino.componente_curricular cc on mc.id_componente_curricular = cc.id_disciplina " +  
				"inner join ensino.componente_curricular_detalhes ccd on cc.id_detalhe = ccd.id_componente_detalhes " +  
				
				"inner join medio.serie s on mc.id_serie = s.id_serie " + 
				"inner join medio.turma_serie ts on ts.id_serie = s.id_serie " +  
				
				"inner join medio.curso_medio cursoMedio on s.id_curso = cursoMedio.id_curso " +  
				"inner join curso curso on curso.id_curso = cursoMedio.id_curso " +  
				"inner join ensino.turno turno on turno.id_turno = cursoMedio.id_turno " +
				
				"left join medio.matricula_discente_serie mds on mds.id_turma_serie = ts.id_turma_serie and mds.id_discente = d.id_discente " + 
				"left join medio.situacao_matricula_serie sitSerie on mds.id_situacao_matricula_serie = sitSerie.id_situacao_matricula_serie ");
		
		sql.append(
				" where d.status in "+UFRNUtils.gerarStringIn(new Integer[]{StatusDiscente.ATIVO, StatusDiscente.ATIVO_DEPENDENCIA})+						
				" and mc.ano = :ano" +
				" and ts.ano = :ano" +
				" and mc.id_turma is null");
		if(idCurso > 0)
			sql.append(
				" and curso.id_curso = :curso");	
		if(idSerie > 0)
			sql.append(
				" and s.id_serie = :serie");
		sql.append(
				" order by curso_nome, serie_numero, ts_nome, p_nome, matricula_serie, ccd_nome");
		
        Query q = getSession().createSQLQuery(sql.toString());
        q.setInteger("ano", ano);
        if(idCurso > 0)
        	q.setInteger("curso", idCurso);
        if(idSerie > 0)
        	q.setInteger("serie", idSerie);
        
        List<Object[]> retorno = q.list();
        
        getSession().close();
      return retorno;
	}
	
	/**
	 * Busca matrículas em uma turmaSerie.
	 * As matrículas devem estar com situação ATIVO ou ATIVO_DEPENDENCIA.
	 * 
	 * @param turma
	 * @param quantidade
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaDiscenteSerie> findAlunosByTurma(TurmaSerie turma, SituacaoMatriculaSerie...situacoesMatriculaSerie) throws DAOException {
		StringBuffer sql = new StringBuffer();
		sql.append("from MatriculaDiscenteSerie mds " +
						" where mds.turmaSerie.id = :turma ");
		if(situacoesMatriculaSerie.length > 0)
			sql.append(	" and mds.id_situacao_matricula_serie in " +UFRNUtils.gerarStringIn(situacoesMatriculaSerie));			

		Query q = getSession().createQuery(sql.toString());
		q.setInteger("turma", turma.getId());
		
		@SuppressWarnings("unchecked")
		List <MatriculaDiscenteSerie> rs = q.list();
		return rs;
	}
	
}
