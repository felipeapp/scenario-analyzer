/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/08/2008
 *
 */
package br.ufrn.sigaa.arq.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.dominio.AtendimentoAluno;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.StatusAtendimentoAluno;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/** 
 *  DAO responsável por realizar a comunicação entre o discente e a coordenação do seu curso. 
 *  Realizando consultas específicas como: Busca de Discentes, Perguntas não lidas, 
 *  Perguntas sem respostas do stricto, Perguntas sem respostas da graduação, 
 *  Perguntas respondidas da Graduação.
 *  
 * @author Henrique André
 *
 */
public class AtendimentoAlunoDao extends GenericSigaaDAO {

	/**
	 * Lista todas as perguntas que o discente fez (qualquer status)
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AtendimentoAluno> findAllByDiscente(DiscenteAdapter discente) throws DAOException {

		StringBuilder hql = new StringBuilder();
		hql.append("select atendimento" +
				" from AtendimentoAluno atendimento" +
				" where atendimento.discente.id = :discente and atendimento.ativo = trueValue()" +
				" order by atendimento.dataEnvio");

		Query query = getSession().createQuery(hql.toString());

		query.setInteger("discente", discente.getId());

		return query.list();

	}

	/**
	 * Busca as perguntas que o coordenador ainda não leu a resposta.
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AtendimentoAluno> findAllPerguntasNaoLidas(DiscenteAdapter discente) throws DAOException {

		StringBuilder hql = new StringBuilder();
		hql.append("select atendimento" +
				" from AtendimentoAluno atendimento" +
				" where atendimento.statusAtendimento.id != :status and atendimento.ativo = trueValue()" +
				" and atendimento.discente.id = :discente");

		Query query = getSession().createQuery(hql.toString());

		query.setInteger("status", StatusAtendimentoAluno.ALUNO_LEU);
		query.setInteger("discente", discente.getId());

		return query.list();

	}

	/**
	 * Recupera todas as perguntas feitas e que ainda não possuem resposta para o nível stricto.
	 *
	 * @param programa
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AtendimentoAluno> findAllPerguntasSemRespostasStricto(Unidade programa, boolean limitar) throws DAOException {
		StringBuilder hql = new StringBuilder();

		hql.append("select atendimento.id," +
				" atendimento.titulo," +
				" atendimento.discente.pessoa.nome," +
				" atendimento.dataEnvio" +
				" from AtendimentoAluno atendimento" +
				" where atendimento.discente.gestoraAcademica.id = :programa and atendimento.ativo = trueValue()" +
				" and atendimento.statusAtendimento.id not in (" + StatusAtendimentoAluno.ATENDENTE_RESPONDEU + "," + StatusAtendimentoAluno.ALUNO_LEU + ")" +
				" order by atendimento.dataEnvio");

		Query query = getSession().createQuery(hql.toString());
		query.setInteger("programa", programa.getId());
		
		if (limitar)
			query.setMaxResults(5);		

		List<Object[]> list = query.list();
		
		List<AtendimentoAluno> resultado = new ArrayList<AtendimentoAluno>();
		
		for (int i = 0; i < list.size(); i++) {
			int col = 0;
			Object[] colunas = list.get(i);
			
			AtendimentoAluno atendimento = new AtendimentoAluno();
			atendimento.setId( (Integer)  colunas[col++]);
			atendimento.setTitulo( (String) colunas[col++]);
			atendimento.setDiscente(new Discente());
			atendimento.getDiscente().setPessoa(new Pessoa());
			atendimento.getDiscente().getPessoa().setNome( (String) colunas[col++]);
			atendimento.setDataEnvio((Date) colunas[col++]);
			resultado.add(atendimento);
		}
		return resultado;		
		
	}

	/**
	 * Recupera todas as perguntas feitas e que ainda não possuem resposta para o nível gradução
	 *
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AtendimentoAluno> findAllPerguntasSemRespostasGraduacao(Curso curso, boolean limitar) throws DAOException {
		StringBuilder hql = new StringBuilder();
			

		hql.append("select atendimento.id," +
				" atendimento.titulo," +
				" atendimento.discente.pessoa.nome," +
				" atendimento.dataEnvio" +
				" from AtendimentoAluno atendimento" +
				" where atendimento.discente.curso.id = :curso and atendimento.ativo = trueValue()" +
				" and atendimento.statusAtendimento.id not in (" + StatusAtendimentoAluno.ATENDENTE_RESPONDEU + "," + StatusAtendimentoAluno.ALUNO_LEU + ") order by atendimento.dataEnvio ");

		Query query = getSession().createQuery(hql.toString());
		query.setInteger("curso", curso.getId());
		
		if (limitar)
			query.setMaxResults(5);		

		List<Object[]> list = query.list();
		
		List<AtendimentoAluno> resultado = new ArrayList<AtendimentoAluno>();
		
		for (int i = 0; i < list.size(); i++) {
			int col = 0;
			Object[] colunas = list.get(i);
			
			AtendimentoAluno atendimento = new AtendimentoAluno();
			atendimento.setId( (Integer)  colunas[col++]);
			atendimento.setTitulo( (String) colunas[col++]);
			atendimento.setDiscente(new Discente());
			atendimento.getDiscente().setPessoa(new Pessoa());
			atendimento.getDiscente().getPessoa().setNome( (String) colunas[col++]);
			atendimento.setDataEnvio((Date) colunas[col++]);
			resultado.add(atendimento);
		}
		return resultado;
	}

	/** Retorna todas perguntas enviadas por um discente de graduação que foram respondidas.
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AtendimentoAluno> findAllPerguntasRespondidasGraduacao(int idCurso) throws DAOException {
		
		StringBuilder hql = new StringBuilder();
		
		hql.append("select atendimento" +
				" from AtendimentoAluno atendimento" +
				" inner join fetch atendimento.discente discente" +
				" inner join fetch discente.pessoa pessoa" +
				" inner join fetch discente.curriculo curriculo" +
				" where atendimento.discente.curso.id = :curso" +
				" and atendimento.statusAtendimento.id = :status" +
				" and atendimento.ativo = trueValue()"+
				" order by atendimento.dataEnvio");
		
		Query q = getSession().createQuery(hql.toString());
		
		q.setInteger("curso", idCurso);
		q.setInteger("status", StatusAtendimentoAluno.ATENDENTE_RESPONDEU);
		
		return q.list();
	}

	/** Retorna todas perguntas enviadas por um discente de curso stricto sensu que foram respondidas. 
	 * @param idPrograma
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AtendimentoAluno> findAllPerguntasRespondidasStricto(int idPrograma) throws DAOException {

		StringBuilder hql = new StringBuilder();
		
		hql.append("select atendimento" +
				" from AtendimentoAluno atendimento" +
				" inner join fetch atendimento.discente discente" +
				" inner join fetch discente.pessoa pessoa" +
				" inner join fetch discente.curriculo curriculo" +
				" where atendimento.discente.gestoraAcademica.id = :programa" +
				" and atendimento.statusAtendimento.id = :status" +
				" and atendimento.ativo = trueValue()"+
				" order by atendimento.dataEnvio");

		Query q = getSession().createQuery(hql.toString());
		
		q.setInteger("programa", idPrograma);
		q.setInteger("status", StatusAtendimentoAluno.ATENDENTE_RESPONDEU);
		
		return q.list();
	}

}
