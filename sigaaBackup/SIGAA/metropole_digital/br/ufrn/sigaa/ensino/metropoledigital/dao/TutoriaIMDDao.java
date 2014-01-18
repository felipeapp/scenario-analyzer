/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 22/05/2013
 *
 */
package br.ufrn.sigaa.ensino.metropoledigital.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.AcompanhamentoSemanalDiscente;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.TutoriaIMD;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;

/**
 * 
 * DAO com consultas utilizadas no cadastramento das tutorias do módulo técnico do IMD. 
 * 
 * @author Rafael Silva
 * @author Rafael Barros
 *
 */
public class TutoriaIMDDao extends GenericSigaaDAO{
	/**
	 * Pesquisa as tutorias que estão vinculadas ao id do cronograma informado
	 * 
	 * @param idCronograma
	 * @return
	 * @throws DAOException
	 */
	public List<TutoriaIMD> findByCronogramaExecucao(int idCronograma) throws DAOException{
		StringBuilder hql = new StringBuilder();
		hql.append(" select tu from TutoriaIMD tu" +
				" inner join tu.turmaEntrada te " +
				" inner join tu.cronograma ce" +
				" inner join te.opcaoPoloGrupo as opg" +
				" inner join te.estruturaCurricularTecnica as estr" +
				" inner join estr.cursoTecnico as curso" +
				" where te.ativo=trueValue() and ce.id =:idCronograma and curso.ativo = " + Boolean.TRUE);
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idCronograma", idCronograma);
		List<TutoriaIMD> lista = q.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		return  lista;
	}
	
	/**
	 * Lista as tutorias do ano - período informado que não possuem cronograma relacionado
	 * 
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<TutoriaIMD> findTutoriasSemCronograma(int idCurso, int ano, int periodo) throws DAOException{
		try {
			StringBuilder hql = new StringBuilder();
			hql.append(" select tu from TutoriaIMD tu" +
					" inner join tu.turmaEntrada te " +
					" left join tu.cronograma ce" +
					" inner join te.opcaoPoloGrupo as opg" +
					" inner join te.estruturaCurricularTecnica as estr" +
					" inner join estr.cursoTecnico as curso" +
					" where te.ativo=trueValue() and te.estruturaCurricularTecnica.cursoTecnico.id=:idCurso and te.anoReferencia=:ano and te.periodoReferencia=:periodo and tu.cronograma is null and curso.ativo = " + Boolean.TRUE);
			
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("ano", ano);
			q.setInteger("periodo", periodo);
			q.setInteger("idCurso", idCurso);
			
			List<TutoriaIMD> lista = q.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
			return  lista;
			
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Pesquisa Tutorias por curso, ano e periodo referencia
	 * 
	 * @param idCurso, ano, periodo
	 * @return
	 * @throws DAOException
	 */
	public List<TutoriaIMD> findByCursoAnoPeriodo(int idCurso, int ano, int periodo) throws DAOException{
		try {
			StringBuilder hql = new StringBuilder();
			hql.append(" select tu from TutoriaIMD tu" +
					" inner join tu.turmaEntrada te " +
					" left join tu.cronograma ce" +
					" inner join te.opcaoPoloGrupo as opg" +
					" inner join te.estruturaCurricularTecnica as estr" +
					" inner join estr.cursoTecnico as curso" +
					" where te.ativo=trueValue() and te.estruturaCurricularTecnica.cursoTecnico.id=:idCurso and te.anoReferencia=:ano and te.periodoReferencia=:periodo and tu.cronograma is null and curso.ativo = " + Boolean.TRUE);
			
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("ano", ano);
			q.setInteger("periodo", periodo);
			q.setInteger("idCurso", idCurso);
			
			List<TutoriaIMD> lista = q.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
			return  lista;
			
		} catch (Exception e) {
			throw new DAOException(e);
		}
		
	}
	
	/**
	 * Pesquisa Tutorias por Turma de Entrada.
	 * 
	 * @param idTurmaEntrada
	 * @return
	 * @throws DAOException
	 */
	public TutoriaIMD findByTurmaEntrada(int idTurmaEntrada) throws DAOException{
		
		try {
			Criteria c = getSession().createCriteria(TutoriaIMD.class);
			Criteria cTurmaEntrada = c.createCriteria("turmaEntrada");
			Criteria cTutor = c.createCriteria("tutor");
			Criteria cDadosTurmaIMD = cTurmaEntrada.createCriteria("dadosTurmaIMD");
			Criteria cCronogramaExecucao = cDadosTurmaIMD.createCriteria("cronograma");
			Criteria cEspec = cTurmaEntrada.createCriteria("especializacao");
			cCronogramaExecucao.createCriteria("modulo");
			cTurmaEntrada.createCriteria("cursoTecnico");
			cTutor.createCriteria("pessoa");
			
			
			cEspec.addOrder(Order.asc("nome"));
			cTurmaEntrada.add(Expression.eq("id", idTurmaEntrada));
			return (TutoriaIMD) c.uniqueResult();
			
        } catch (Exception e) {
            throw new DAOException(e);
        }
	}
	
	/**
	 * Verifica se uma tutoria ja existe por Turma de Entrada e tutor.
	 * 
	 * @param idTurmaEntrada, idTutor
	 * @return
	 * @throws DAOException
	 */
	public boolean existeTutoria(int idTurmaEntrada, int idTutor) throws DAOException{
		
		try {
			Criteria c = getSession().createCriteria(TutoriaIMD.class);
			Criteria cTurmaEntrada = c.createCriteria("turmaEntrada");
			Criteria cTutor = c.createCriteria("tutor");			
			
			cTurmaEntrada.add(Expression.eq("id", idTurmaEntrada));
			cTutor.add(Expression.eq("id", idTutor));
			
			if(c.list().isEmpty() || c.list() == null) {
				return false;
			} else {
				List<TutoriaIMD> lista = c.list();
				for(TutoriaIMD tut: lista) {
					if(tut.getDataFimTutoria() == null) {
						return true;
					}
				}
				return false;
			}
			
			
        } catch (Exception e) {
            return true;
        }
	}
	
	/**
	 * Verifica se existe uma tutoria ativa por Turma de Entrada.
	 * 
	 * @param idTurmaEntrada
	 * @return
	 * @throws DAOException
	 */
	public TutoriaIMD existeTutoriaAtiva(int idTurmaEntrada) throws DAOException{
		
		try {
			Criteria c = getSession().createCriteria(TutoriaIMD.class);
			Criteria cTurmaEntrada = c.createCriteria("turmaEntrada");
			Criteria cTutor = c.createCriteria("tutor");			
			
			cTurmaEntrada.add(Expression.eq("id", idTurmaEntrada));
			
			if(c.list().isEmpty() || c.list() == null) {
				return null;
			} else {
				List<TutoriaIMD> lista = c.list();
				for(TutoriaIMD tut: lista) {
					if(tut.getDataFimTutoria() == null) {
						return tut;
					}
				}
				return null;
			}
			
			
        } catch (Exception e) {
            return null;
        }
	}
	
	/**
	 * Pesquisa Tutorias por Turma de Entrada. Será exibida a ultima tutoria correspondente a turma. Ou seja, a tutoria que ainda não foi finalizada e permanece ativa.
	 * 
	 * @param idTurmaEntrada
	 * @return
	 * @throws DAOException
	 */
	public TutoriaIMD findUltimaByTurmaEntrada(int idTurmaEntrada) throws DAOException{
		
		try {
			Criteria c = getSession().createCriteria(TutoriaIMD.class);
			Criteria cTurmaEntrada = c.createCriteria("turmaEntrada");
			Criteria cTutor = c.createCriteria("tutor");
			Criteria cDadosTurmaIMD = cTurmaEntrada.createCriteria("dadosTurmaIMD");
			Criteria cCronogramaExecucao = cDadosTurmaIMD.createCriteria("cronograma");
			cCronogramaExecucao.createCriteria("modulo");
			cTurmaEntrada.createCriteria("cursoTecnico");
			cTutor.createCriteria("pessoa");
			cTurmaEntrada.createCriteria("especializacao");
			
			cTurmaEntrada.add(Expression.eq("id", idTurmaEntrada));
			
			TutoriaIMD retornoTutoria = new TutoriaIMD();
			boolean preencheu = false;
			
			Collection<TutoriaIMD> tutorias = c.list();
			for(TutoriaIMD t: tutorias){
				if(t.getDataFimTutoria() == null){
					retornoTutoria = t;
					preencheu = true;
				}
			}
			
			if(preencheu) {
				return retornoTutoria;
			} else {
				return null;
			}
			
        } catch (Exception e) {
            return null;
        }
	}
	
	/**
	 * Pesquisa Tutorias por Turma de Entrada por meio de uma consulta projetada. Será exibida a ultima tutoria correspondente a turma. Ou seja, a tutoria que ainda não foi finalizada e permanece ativa.
	 * 
	 * @param idTurmaEntrada
	 * @return
	 * @throws DAOException
	 */
	public TutoriaIMD findUltimaByTurmaEntradaProjetado(int idTurmaEntrada) throws DAOException{
			
			String projecao = "id, tutor.id, tutor.pessoa.id, tutor.pessoa.nome, turmaEntrada.id, turmaEntrada.especializacao.id, turmaEntrada.especializacao.descricao";
			
			String hql = "SELECT " + projecao +  " FROM TutoriaIMD WHERE " +
					"  turmaEntrada.id = " + idTurmaEntrada +" AND dataFimTutoria IS NULL";
			
			hql = hql + " order by id";
			
			Query q = getSession().createQuery(hql);

			Collection<TutoriaIMD> lista = HibernateUtils.parseTo(q.list(), projecao, TutoriaIMD.class);
			for(TutoriaIMD tutoria: lista){
				tutoria.getTutor().setId(Integer.parseInt("" + tutoria.getTutor().getId()));
				return tutoria;
			}
			return null;
			
	
	}
	
	
	/**
	 * Pesquisa Tutorias por Pessoa;
	 * 
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	public List<TutoriaIMD> findByPessoa(int idPessoa) throws DAOException {
		Criteria c = getSession().createCriteria(TutoriaIMD.class);
		Criteria cTutor = c.createCriteria("tutor");
		Criteria cPessoa = cTutor.createCriteria("pessoa");
		cPessoa.add(Expression.eq("id",idPessoa));
		c.add(Expression.isNull("dataFimTutoria"));
		
		return c.list();
	}
	
	/**
	 * Retorna verdadeiro caso o tutor possua tutoria ativa no sistmema.
	 * @return
	 * @throws DAOException 
	 */
	public Boolean possuiTutoria(int idPessoa) throws DAOException{
		Criteria c = getSession().createCriteria(TutoriaIMD.class);
		Criteria cTutor = c.createCriteria("tutor");
		Criteria cPessoa = cTutor.createCriteria("pessoa");
		cPessoa.add(Expression.eq("id",idPessoa));
		c.add(Expression.isNull("dataFimTutoria"));
		
		c.setProjection(Projections.rowCount());
		
		int count = (Integer) c.uniqueResult();
		
		if (count>0) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Retorna verdadeiro caso o tutor possua tutoria ativa no sistmema.
	 * @return
	 * @throws DAOException 
	 */
	public List<TutoriaIMD> findTutorias(int idPessoa) throws DAOException{
		Criteria c = getSession().createCriteria(TutoriaIMD.class);
		Criteria cTutor = c.createCriteria("tutor");
		Criteria cPessoa = cTutor.createCriteria("pessoa");
		cPessoa.add(Expression.eq("id",idPessoa));
		c.add(Expression.isNull("dataFimTutoria"));
		
		return c.list();
	}
	
	/**
	 * Retorna as Turmas de entrada vinculadas o curso informado.
	 * 
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	public List<TurmaEntradaTecnico> findTurmasByCurso(int idCurso) throws DAOException{
		Criteria cTurmaEntrada = getSession().createCriteria(TurmaEntradaTecnico.class);
		Criteria cDadosTurmaIMD = cTurmaEntrada.createCriteria("dadosTurmaIMD");
		Criteria especializacaoIMD = cTurmaEntrada.createCriteria("especializacao");
		Criteria cCronogramaExecucao = cDadosTurmaIMD.createCriteria("cronograma");
		Criteria cCursoTecnico = cTurmaEntrada.createCriteria("cursoTecnico");
		Criteria cOpcaoPolo = cTurmaEntrada.createCriteria("opcaoPoloGrupo");
		cOpcaoPolo.createCriteria("polo");
		cCronogramaExecucao.createCriteria("modulo");
		
		cCursoTecnico.add(Expression.eq("id",idCurso));
		especializacaoIMD.addOrder(Order.asc("descricao"));
		
		
		ArrayList<TurmaEntradaTecnico> list = (ArrayList<TurmaEntradaTecnico>)cTurmaEntrada.list();
		
		return list;
	}
	
	
	/**
	 * Retorna as Turmas de entrada vinculadas o curso informado e a lista de pólos informada.
	 * 
	 * @param idCurso, listaPolos
	 * @return
	 * @throws DAOException
	 */
	public List<TurmaEntradaTecnico> findTurmasByCursoAndPolos(int idCurso, Collection<Polo> listaPolos) throws DAOException{
		Criteria cTurmaEntrada = getSession().createCriteria(TurmaEntradaTecnico.class);
		Criteria cDadosTurmaIMD = cTurmaEntrada.createCriteria("dadosTurmaIMD");
		Criteria especializacaoIMD = cTurmaEntrada.createCriteria("especializacao");
		Criteria cCronogramaExecucao = cDadosTurmaIMD.createCriteria("cronograma");
		Criteria cCursoTecnico = cTurmaEntrada.createCriteria("cursoTecnico");
		Criteria cOpcaoPolo = cTurmaEntrada.createCriteria("opcaoPoloGrupo");
		cOpcaoPolo.createCriteria("polo");
		cCronogramaExecucao.createCriteria("modulo");
		
		cCursoTecnico.add(Expression.eq("id",idCurso));
		especializacaoIMD.addOrder(Order.asc("descricao"));
		
		
		ArrayList<TurmaEntradaTecnico> list = (ArrayList<TurmaEntradaTecnico>)cTurmaEntrada.list();
		
		ArrayList<TurmaEntradaTecnico> listaAuxiliar = new ArrayList<TurmaEntradaTecnico>();
		
		for(TurmaEntradaTecnico t: list){
			for(Polo p: listaPolos) {
				if(t.getOpcaoPoloGrupo().getPolo().getId() == p.getId()){
					listaAuxiliar.add(t);
					break;
				}
			}
		}
		
		return listaAuxiliar;
	}
	
	/**
	 * Retorna as Tutorias vinculadas o curso informado.
	 * 
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	public List<TutoriaIMD> findByCurso(int idCurso) throws DAOException{
		Criteria c = getSession().createCriteria(TutoriaIMD.class);
		Criteria cTurmaEntrada = c.createCriteria("turmaEntrada");
		Criteria cCursoTecnico = cTurmaEntrada.createCriteria("cursoTecnico");
		Criteria cTutor = c.createCriteria("tutor");
		cTurmaEntrada.createCriteria("dadosTurmaIMD");
		cTutor.createCriteria("pessoa");
		
		cCursoTecnico.add(Expression.eq("id",idCurso));
		
		return c.list();
	}
	
	/** Retorna a Tutoria com o id informado.
	 * 
	 * @param idTutoria
	 * @return Objeto buscado da classe TutoriaIMD
	 * @throws DAOException 
	 */
	public TutoriaIMD findById(int idTutoria) throws DAOException{
		Criteria c = getSession().createCriteria(TutoriaIMD.class);
		Criteria cTurmaEntrada = c.createCriteria("turmaEntrada");
		Criteria cTutor = c.createCriteria("tutor");
		Criteria cDadosTurmaIMD = cTurmaEntrada.createCriteria("dadosTurmaIMD");
		Criteria cCronogramaExecucao = cDadosTurmaIMD.createCriteria("cronograma");
		cCronogramaExecucao.createCriteria("modulo");
		cTurmaEntrada.createCriteria("cursoTecnico");
		cTutor.createCriteria("pessoa");
		
		c.add(Expression.eq("id", idTutoria));
		return (TutoriaIMD) c.uniqueResult();
	}
	
	/**
	 * Retorna as Turmas vinculadas ao módulo informado.
	 * 
	 * @param idModulo
	 * @return Objeto buscado da classe TutoriaIMD
	 * @throws DAOException 
	 */
	public List<TurmaEntradaTecnico> findTurmasByModulo(int idModulo) throws DAOException{
		Criteria cTurmaEntrada = getSession().createCriteria(TurmaEntradaTecnico.class);
		Criteria especializacaoIMD = cTurmaEntrada.createCriteria("especializacao");
		Criteria cDadosTurmaIMD = cTurmaEntrada.createCriteria("dadosTurmaIMD");
		Criteria cCronogramaExecucao = cDadosTurmaIMD.createCriteria("cronograma");
		Criteria cModulo = cCronogramaExecucao.createCriteria("modulo");
		Criteria cOpcaoPolo = cTurmaEntrada.createCriteria("opcaoPoloGrupo");
		cOpcaoPolo.createCriteria("polo");
		cTurmaEntrada.createCriteria("cursoTecnico");
		
		cModulo.add(Expression.eq("id",idModulo));
		especializacaoIMD.addOrder(Order.asc("descricao"));
		
		ArrayList<TurmaEntradaTecnico> list = (ArrayList<TurmaEntradaTecnico>)cTurmaEntrada.list();
		
		return list;
		
	}
	
	
	/**
	 * Retorna as Turmas vinculadas ao módulo informado e a lista de pólos informada.
	 * 
	 * @param idModulo, listaPolos
	 * @return Objeto buscado da classe TutoriaIMD
	 * @throws DAOException 
	 */
	public List<TurmaEntradaTecnico> findTurmasByModuloAndPolos(int idModulo, Collection<Polo> listaPolos) throws DAOException{
		Criteria cTurmaEntrada = getSession().createCriteria(TurmaEntradaTecnico.class);
		Criteria especializacaoIMD = cTurmaEntrada.createCriteria("especializacao");
		Criteria cDadosTurmaIMD = cTurmaEntrada.createCriteria("dadosTurmaIMD");
		Criteria cCronogramaExecucao = cDadosTurmaIMD.createCriteria("cronograma");
		Criteria cModulo = cCronogramaExecucao.createCriteria("modulo");
		Criteria cOpcaoPolo = cTurmaEntrada.createCriteria("opcaoPoloGrupo");
		cOpcaoPolo.createCriteria("polo");
		cTurmaEntrada.createCriteria("cursoTecnico");
		
		cModulo.add(Expression.eq("id",idModulo));
		especializacaoIMD.addOrder(Order.asc("descricao"));
		
		ArrayList<TurmaEntradaTecnico> list = (ArrayList<TurmaEntradaTecnico>)cTurmaEntrada.list();
		
		ArrayList<TurmaEntradaTecnico> listaAuxiliar = new ArrayList<TurmaEntradaTecnico>();
		for(Polo p: listaPolos) {
			for(TurmaEntradaTecnico t: list){
				if(t.getOpcaoPoloGrupo().getPolo().getId() == p.getId()){
					listaAuxiliar.add(t);
					break;
				}
			}
		}
		
		return listaAuxiliar;
	}
	
	/**
	 * Retorna as Tutorias vinculadas ao módulo informado.
	 * 
	 * @param idModulo
	 * @return Coleção de objetos da classe TutoriaIMD
	 * @throws DAOException 
	 */
	public List<TutoriaIMD> findByModulo(int idModulo) throws DAOException{
		Criteria c = getSession().createCriteria(TutoriaIMD.class);
		Criteria cTurmaEntrada = c.createCriteria("turmaEntrada");
		Criteria especializacaoIMD = cTurmaEntrada.createCriteria("especializacao");
		Criteria cTutor = c.createCriteria("tutor");
		Criteria cDadosTurmaIMD = cTurmaEntrada.createCriteria("dadosTurmaIMD");
		Criteria cCronogramaExecucao = cDadosTurmaIMD.createCriteria("cronograma");
		Criteria cModulo = cCronogramaExecucao.createCriteria("modulo");
		cTurmaEntrada.createCriteria("cursoTecnico");
		cTutor.createCriteria("pessoa");
		
		cModulo.add(Expression.eq("id",idModulo));
		
		return c.list();
		
	}
	
	/**
	 * Retorna as Turmas de Entrada vinculadas a pessoa informada, nesse caso, a pessoa informada exerce tutoria nas turmas de entrada
	 * 
	 * @param idPessoaTutorIMD
	 * @return Coleção de objetos da classe TurmaEntradaTecnico
	 * @throws DAOException
	 */
	public Collection<TurmaEntradaTecnico> findTurmasByTutor(int idPessoaTutorIMD) throws DAOException{
		
		try {
			Collection<TurmaEntradaTecnico> listaTurmasEntrada = new ArrayList<TurmaEntradaTecnico>();
			
			Criteria c = getSession().createCriteria(TutoriaIMD.class);
			Criteria cTurmaEntrada = c.createCriteria("turmaEntrada");
			Criteria cTutor = c.createCriteria("tutor");
			Criteria cPessoa = cTutor.createCriteria("pessoa");
			Criteria cOpcaoPolo = cTurmaEntrada.createCriteria("opcaoPoloGrupo");
			Criteria cPolo = cOpcaoPolo.createCriteria("polo");
			Criteria cCidade = cPolo.createCriteria("cidade");
			cCidade.createCriteria("unidadeFederativa");
			cTurmaEntrada.createCriteria("especializacao");
			cTurmaEntrada.createCriteria("cursoTecnico");
			
			
			cPessoa.add(Expression.eq("id",idPessoaTutorIMD));
			c.add(Expression.isNull("dataFimTutoria"));
			
			
			@SuppressWarnings("unchecked")
			List<TutoriaIMD> list = c.list();
			Collection<TutoriaIMD> listaTutorias = list;
			
			if(listaTutorias.size() > 0)
			{
				for(TutoriaIMD tutoria: listaTutorias){
					listaTurmasEntrada.add(tutoria.getTurmaEntrada());
				}
			}
			
			return listaTurmasEntrada;
		} catch (Exception e) {
	        throw new DAOException(e);
	    }
		
	}
	
	
	/**
	 * Efetua o salvamento dos dados da tutoria
	 * 
	 * @param tutoria
	 * @return
	 * @throws DAOException
	 */
	public void salvarTutoria(TutoriaIMD tutoria) throws DAOException{		
		TutoriaIMDDao tutoriaIMDDao = new TutoriaIMDDao();
		try {
		
			Transaction t = null;
			Session session = getSession();
			tutoriaIMDDao.createOrUpdate(tutoria.getTurmaEntrada().getEspecializacao());
			tutoriaIMDDao.createOrUpdate(tutoria.getTurmaEntrada().getDadosTurmaIMD());
			tutoriaIMDDao.createOrUpdate(tutoria.getTurmaEntrada());
			t = session.beginTransaction();
			
			t.commit();
		} finally {
			tutoriaIMDDao.close();
		}
		
	}
	
	
	/**
	 * Retorna as Tutorias do IMD
	 * 
	 * @param idPessoaTutorIMD
	 * @return Coleção de objetos da classe TutoriaIMD
	 * @throws DAOException
	 */
	public Collection<TutoriaIMD> findTurmasIMD(int idCursoTecnico) throws DAOException{
		
		try {
			
			Criteria c = getSession().createCriteria(TutoriaIMD.class);
			Criteria cTurmaEntrada = c.createCriteria("turmaEntrada");
			Criteria cCurso = cTurmaEntrada.createCriteria("cursoTecnico");
			Criteria cTutor = c.createCriteria("tutor");
			cTutor.createCriteria("pessoa");
			cTurmaEntrada.createCriteria("especializacao");
						
			cCurso.add(Expression.eq("id", idCursoTecnico));
			
			@SuppressWarnings("unchecked")
			List<TutoriaIMD> lista = c.list();
			return lista;
			
		} catch (Exception e) {
	        throw new DAOException(e);
	    }
		
	}
	
	
	
}
