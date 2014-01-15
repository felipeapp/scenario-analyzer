/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '20/04/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.prodocente;

import java.util.Collection;
import java.util.List;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.TipoTrabalhoConclusao;
import br.ufrn.sigaa.prodocente.atividades.dominio.TrabalhoFimCurso;

/**
 * DAO responsável por consultas específicas à Trabalho de Fim de Curso.
 * 
 * @author Ricardo Wendell
 *
 */
public class TrabalhoFimCursoDao extends GenericSigaaDAO {

	/**
	 * Retorna o Trabalho de Fim de Curso pertencente a um discente
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public TrabalhoFimCurso findByOrientando(DiscenteAdapter discente) throws DAOException {
		try {
			return (TrabalhoFimCurso) getSession()
					.createQuery("from TrabalhoFimCurso where orientando.id = " + discente.getId() +
							" and (ativo = trueValue() or ativo is null) order by dataDefesa desc")
					.setMaxResults(1)
					.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	
	/**
	 * Retorna o Trabalho de Fim de Curso pertencente a um discente
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public TrabalhoFimCurso findByMatricula(Integer matricula) 
		throws DAOException {
		try {
			return (TrabalhoFimCurso) getSession()
					.createQuery("from TrabalhoFimCurso where matricula.id = " + matricula + 
							" and (ativo = trueValue() or ativo is null) order by dataDefesa desc")
					.setMaxResults(1)
					.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna o Trabalho de Fim de Curso pertencente a um servidor
	 * 
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<TrabalhoFimCurso> findDados(int servidor) throws DAOException {
		String hql = "FROM TrabalhoFimCurso WHERE servidor.id = " +servidor+ 
			"and (ativo = trueValue() or ativo is null) order by ano desc, dataDefesa asc";

		return getSession().createQuery(hql).list();
	}

	
	/**
	 * Retorna o Trabalho de Fim de Curso do departamento informado.
	 * 
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<TrabalhoFimCurso> findbyCurso(Integer idCurso, Character nivel, Integer idDiscente, Integer idServidor) throws DAOException {
		String projecao = "id, ano, ativo, titulo, orientandoString, departamento.nome, orientando.pessoa.nome, dataDefesa, "+
		"servidor.pessoa.nome, tipoTrabalhoConclusao.descricao, idArquivo, orientando.curso.id, orientando.curso.nome ";
		
		StringBuilder hql = new StringBuilder("select ");
		hql.append( projecao );
		hql.append(" FROM TrabalhoFimCurso trab ");
		hql.append(" WHERE (trab.ativo = trueValue() or trab.ativo is null) ");

		if (!ValidatorUtil.isEmpty(idCurso))
			hql.append(" and  orientando.curso.id = " + idCurso );
		
		if (!ValidatorUtil.isEmpty(nivel))
			hql.append(" and orientando.curso.nivel = '" + nivel + "'");
		
		if (!ValidatorUtil.isEmpty(idDiscente))
			hql.append(" and orientando.id = " +idDiscente);
		
		if (!ValidatorUtil.isEmpty(idServidor))
			hql.append(" and servidor.id = " +idServidor);		
		
		hql.append(" order by ano desc, trab.orientando.pessoa.nome");
		return HibernateUtils.parseTo(getSession().createQuery(hql.toString()).list(), projecao, TrabalhoFimCurso.class);
	}	
	
	/**
	 * Retorna os trabalhos de fim de curso de um servidor ordenado pelos Discentes
	 * 
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<TrabalhoFimCurso> findTrabalhOFimCursoByServidor(int servidor) throws DAOException {
		String hql = "FROM TrabalhoFimCurso trab WHERE trab.servidor.id = " +servidor+ 
			"and (ativo = trueValue() or ativo is null) order by trab.orientando.pessoa.nome";

		return getSession().createQuery(hql).list();
	}
	
	/**
	 * Retorna na parte pública, os trabalhos de fim de curso de acordo com os filtros passados como parâmetro.
	 * @param nomeDiscente
	 * @param titulo
	 * @param palavraChave
	 * @param ano
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<TrabalhoFimCurso> findConsultaPublica(String nomeDiscente, String titulo, Integer ano, Integer idCurso) throws DAOException{
		String projecao = "id, ano, ativo, orientando.pessoa.nome, orientando.pessoa.nomeAscii," +
				" servidor.pessoa.nome, dataDefesa, titulo, orientando.curso.nome, idArquivo ";
		
		StringBuilder hql = new StringBuilder("select " + projecao + " from TrabalhoFimCurso tfc where ativo = trueValue() and tipoTrabalhoConclusao.id = "+TipoTrabalhoConclusao.MONOGRAFIA);
		
		hql.append("and idArquivo is not null");
		
		if (!ValidatorUtil.isEmpty(nomeDiscente))
			hql.append(" and "+ UFRNUtils.toAsciiUpperUTF8("tfc.orientando.pessoa.nomeAscii") + " like "
						+ UFRNUtils.toAsciiUTF8("'" + nomeDiscente.toUpperCase() + "%'")); 
		
		if (!ValidatorUtil.isEmpty(titulo))
			hql.append(" and upper(titulo) like '%"+titulo.toUpperCase()+"%'"); 		
				
		if (ano != null && ano > 0) 
			hql.append(" and ano = " + ano);
		
		if (idCurso != null && idCurso > 0) 
			hql.append(" and orientando.curso.id = " + idCurso);		

		hql.append(" order by ano, orientando.pessoa.nome");
		
		List<TrabalhoFimCurso> lista = (List<TrabalhoFimCurso>) HibernateUtils.parseTo(getSession().createQuery(hql.toString()).list(), projecao, TrabalhoFimCurso.class); 
		
		return lista;
	}

}