/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 27/04/2007
 *
 */	
package br.ufrn.sigaa.arq.dao.ensino;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.SecretariaUnidade;
import br.ufrn.sigaa.ensino.latosensu.dominio.SituacaoProposta;

/**
 * Dao para efetuar consultas de secretários de departamentos e
 * programas de pós-graduação
 * @author leonardo
 *
 */
public class SecretariaUnidadeDao extends GenericSigaaDAO {

	/** Projeção utilizada na consulta padrão. */
	private final String projecaoPadrao = "su.id, su.usuario.id, su.usuario.login, su.usuario.pessoa.nome, su.usuario.pessoa.celular," +
	" su.usuario.pessoa.telefone, su.usuario.pessoa.email," + 
	" su.usuario.ramal, su.unidade.id, su.unidade.nome, su.unidade.tipoAcademica, su.curso.id, su.curso.unidadeCoordenacao.id," +
	" su.curso.nome, su.curso.municipio.nome, su.curso.nivel, su.curso.convenio.id, su.inicio, su.fim, su.ativo," +
	" su.curso.unidadeCoordenacao.id, su.curso.unidadeCoordenacao.nome, su.curso.modalidadeEducacao.id, su.curso.unidade.id, su.curso.unidade.nome ";
	
	/** Alias utilizado na projecao padrão, a ser utilizado na construção da lista de SecretariaUnidade resultante da consulta.
	 *  @see {@link HibernateUtils#parseTo(java.util.List, String, Class, String)} */
	private final String aliasProjecaoPadrao = "su";
	
	/** SELECT auxiliar utilizado na maioria das consultas realizadas na classe */		
	private final String consultaPadrao = "SELECT " + projecaoPadrao +
			" FROM SecretariaUnidade su " +
			" LEFT JOIN su.curso c" +
			" LEFT JOIN su.unidade u" +
			" LEFT JOIN su.curso.unidadeCoordenacao uc" +
			" LEFT JOIN su.curso.convenio conv" +
			" LEFT JOIN su.curso.municipio m" +
			" LEFT JOIN su.curso.unidade uniCurso" ;

	/**
	 * Retorna as secretarias pelo nome do usuário passado
	 * @param nome
	 * @return
	 * @throws DAOException
	 */
	public Collection<SecretariaUnidade> findByNomeUsuario(String nome) throws DAOException{
		StringBuffer hql = new StringBuffer( consultaPadrao );
		hql.append(" WHERE 	su.fim = null");
		hql.append(" and su.ativo = trueValue()");
		if (nome != null && !nome.trim().equals(""))
			hql.append( UFRNUtils.toAsciiUpperUTF8("su.usuario.pessoa.nome") +
				" LIKE " + UFRNUtils.toAsciiUpperUTF8("'%" + nome.trim()
				+ "%'") + " and");
		hql.append(" order by su.usuario.pessoa.nome asc");

		Query q = getSession().createQuery(hql.toString());
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		return HibernateUtils.parseTo(lista, projecaoPadrao, SecretariaUnidade.class, aliasProjecaoPadrao);
	}

	
	/**
	 * Retorna as secretarias pelo login do usuário passado
	 * @param login
	 * @return
	 * @throws DAOException
	 */
	public Collection<SecretariaUnidade> findByLoginUsuario(String login) throws DAOException{
		StringBuffer hql = new StringBuffer( consultaPadrao );
		hql.append(" WHERE su.fim = NULL AND (	su.curso IS NULL OR su.curso.id NOT IN " );
		hql.append(" 	( SELECT cl.id FROM CursoLato cl INNER JOIN cl.propostaCurso pc ");
		hql.append("			WHERE pc.situacaoProposta <> " + SituacaoProposta.ACEITA + ") ");
		hql.append(" ) ");
		hql.append(" and su.ativo = trueValue()");
		if (login != null && !login.trim().equals(""))
			hql.append(" and su.usuario.login = '" + login.trim() + "' ");
		hql.append(" order by su.usuario.pessoa.nome asc");

		Query q = getSession().createQuery(hql.toString());
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		return HibernateUtils.parseTo(lista, projecaoPadrao, SecretariaUnidade.class, aliasProjecaoPadrao);
	}

	/**
	 * Retorna as secretarias do curso informado
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public Collection<SecretariaUnidade> findByCurso(int curso) throws DAOException{
		StringBuffer hql = new StringBuffer( consultaPadrao );
		hql.append(" WHERE su.curso.id = :curso and");
		hql.append(" su.fim = null");
		hql.append(" and su.ativo = trueValue()");
		hql.append(" order by su.usuario.pessoa.nome asc");

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("curso", curso);
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		return HibernateUtils.parseTo(lista, projecaoPadrao, SecretariaUnidade.class, aliasProjecaoPadrao);
	}

	/**
	 * Retorna as secretarias da unidade informada
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public Collection<SecretariaUnidade> findByUnidade(int unidade, Usuario usuario) throws DAOException{
		StringBuffer hql = new StringBuffer( consultaPadrao );
		hql.append(" WHERE su.unidade.id = :unidade and");
		hql.append(" su.fim = null");		
		hql.append(" and su.ativo = trueValue()");
		
		if(usuario != null) {
			hql.append(" and su.usuario.id = " + usuario.getId() );
		}
		
		hql.append(" order by su.usuario.pessoa.nome asc");

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("unidade", unidade);
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		return HibernateUtils.parseTo(lista, projecaoPadrao, SecretariaUnidade.class, aliasProjecaoPadrao);
	}

	/**
	 * Retorna as secretaria de departamento de acordo com o usuário e o tipo acadêmico passado
	 * @param idUsuario
	 * @param tipo TipoAcademica da Unidade
	 * @return
	 * @throws DAOException
	 */
	public Collection<SecretariaUnidade> findByUsuarioTipoAcademico(int idUsuario, int tipo) throws DAOException{
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT su.id as id, su.unidade as unidade, su.curso as curso " +
				" FROM SecretariaUnidade su LEFT JOIN su.curso LEFT JOIN su.unidade ");
		//StringBuffer hql = new StringBuffer( construtorProjecao );
		hql.append(" WHERE su.usuario.id= :usuario and su.fim = null");
		hql.append(" and su.ativo = trueValue()");

		if (tipo == TipoUnidadeAcademica.COORDENACAO_CURSO) {
			hql.append(" and su.unidade is null and su.curso is not null and su.curso.nivel = 'G'");
		} else if (tipo == TipoUnidadeAcademica.COORDENACAO_CURSO_LATO) {
			hql.append(" and su.unidade is null and su.curso is not null and su.curso.nivel = 'L'");
		}else if( tipo == TipoUnidadeAcademica.PROGRAMA_POS ){
			hql.append(" and su.unidade.tipoAcademica = " + tipo);
			hql.append(" and su.unidade is not null and su.curso is null ");
		} else if (tipo == TipoUnidadeAcademica.DEPARTAMENTO  ){
			hql.append(" and (su.unidade.tipoAcademica = " + tipo + " or su.unidade.tipoAcademica = " + TipoUnidadeAcademica.ESCOLA + ")");
			hql.append(" and su.unidade is not null and su.curso is null ");
		} else if (tipo == TipoUnidadeAcademica.CENTRO){
			hql.append(" and su.unidade.tipoAcademica = " + tipo);
			hql.append(" and su.unidade is not null and su.curso is null ");
		} else if (tipo == TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA){
			hql.append(" and su.unidade.tipoAcademica = " + tipo);
			hql.append(" and su.unidade is not null and su.curso is null ");
		}

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("usuario", idUsuario);
		@SuppressWarnings("unchecked")
		Collection<SecretariaUnidade> lista = q.setResultTransformer(Transformers.aliasToBean(SecretariaUnidade.class)).list();
		return lista;
	}

	/**
	 * Retorna as secretarias pelo nível passado
	 * @param nivel nível do Curso
	 * @return
	 * @throws DAOException
	 */
	public Collection<SecretariaUnidade> findSecretariosCursoByNivel(Character nivel) throws DAOException{
		StringBuffer hql = new StringBuffer( consultaPadrao );
		hql.append(" WHERE su.curso is not null");
		hql.append(" and su.ativo = trueValue()");
		hql.append(" and (su.inicio <= :hoje and (su.fim is null or su.fim > :hoje))");
		if (nivel != null)
			hql.append(" and su.curso.nivel = '" + nivel + "'");
		if (nivel == NivelEnsino.LATO) {
			hql.append(" and su.curso.id NOT IN ");
			hql.append(" 	( SELECT cl.id FROM CursoLato cl INNER JOIN cl.propostaCurso pc ");
			hql.append("			WHERE pc.situacaoProposta <> " + SituacaoProposta.ACEITA + ") ");
			
		}
		hql.append(" order by su.curso.nome, su.usuario.pessoa.nome");

		Query q = getSession().createQuery(hql.toString());
		q.setDate("hoje", DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		return HibernateUtils.parseTo(lista, projecaoPadrao, SecretariaUnidade.class, aliasProjecaoPadrao);
	}

	/**
	 * Busca as secretarias de centro e/ou departamento
	 *
	 * @param tipo - null para buscar de todas as unidades
	 * TipoUnidadeAcademica.CENTRO para buscar de centro
	 * TipoUnidadeAcademica.DEPARTAMENTO para buscar de departamento
	 * TipoUnidadeAcademica.PROGRAMA_POS para buscar de programa
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection<SecretariaUnidade> findSecreatariosUnidade( Integer tipo ) throws DAOException{

		StringBuffer hql = new StringBuffer( consultaPadrao );
		hql.append(" where su.ativo = trueValue() ");
		if( tipo != null ){
			if( tipo == TipoUnidadeAcademica.CENTRO )
				hql.append(" and su.unidade.tipoAcademica = " + TipoUnidadeAcademica.CENTRO);
			else if( tipo == TipoUnidadeAcademica.DEPARTAMENTO  )
				hql.append(" and (su.unidade.tipoAcademica = " + TipoUnidadeAcademica.DEPARTAMENTO + " or su.unidade.tipoAcademica = " + TipoUnidadeAcademica.ESCOLA + ")");
			else if( tipo == TipoUnidadeAcademica.PROGRAMA_POS  )
				hql.append(" and su.unidade.tipoAcademica = " + TipoUnidadeAcademica.PROGRAMA_POS);
			else if( tipo == TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA  )
				hql.append(" and su.unidade.tipoAcademica = " + TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA);
		}
		hql.append(" and (su.inicio <= :hoje and (su.fim is null or su.fim > :hoje))");
		hql.append(" order by su.unidade.nome, su.usuario.pessoa.nome");

		Query q = getSession().createQuery(hql.toString());
		q.setDate("hoje", DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		return HibernateUtils.parseTo(lista, projecaoPadrao, SecretariaUnidade.class, aliasProjecaoPadrao);
	}

	/**
	 * Retorna a CoordenacaoCurso do usuário/curso informado
	 * @param usuario
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public SecretariaUnidade findCoordenacaoCursoAtivoByUsuarioCurso(Usuario usuario, Curso curso) throws DAOException{
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT su.id as id, su.unidade as unidade, su.curso as curso " +
				" FROM SecretariaUnidade su JOIN su.curso LEFT JOIN su.unidade ");
		hql.append(" WHERE su.usuario.id= :usuario and su.fim = null");
		hql.append(" and su.ativo = trueValue()");

		hql.append(" and su.curso.id = :curso");

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("usuario", usuario.getId());
		q.setInteger("curso", curso.getId());
		return   (SecretariaUnidade) q.setResultTransformer(Transformers.aliasToBean(SecretariaUnidade.class)).uniqueResult();
	}

}
