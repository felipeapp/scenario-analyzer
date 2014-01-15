/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '29/11/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.extensao;

import java.util.Collection;
import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.DiscenteExtensao;
import br.ufrn.sigaa.extensao.relatorio.dominio.RelatorioBolsistaExtensao;

public class RelatorioBolsistaExtensaoDao extends GenericSigaaDAO {

	public RelatorioBolsistaExtensaoDao() {
	}

	/**
	 * Retorna todos os relatórios de bolsistas onde o servidor informado
	 * participa como coordenador da atividade de extensão.
	 * 
	 * @param idServidor
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<RelatorioBolsistaExtensao> findByCoordenador(int idPessoa) throws DAOException {
		try {
			String projecao = " r.id, r.dataParecer, r.tipoRelatorio.id, r.tipoRelatorio.descricao, " +
					" r.discenteExtensao.id, r.discenteExtensao.tipoVinculo.descricao, r.discenteExtensao.discente.id, r.discenteExtensao.discente.matricula, " +
					" r.discenteExtensao.discente.pessoa.id, r.discenteExtensao.discente.pessoa.nome, " +
					" r.discenteExtensao.atividade.id, r.discenteExtensao.atividade.projeto.id, r.discenteExtensao.atividade.projeto.titulo, " +
					" r.discenteExtensao.atividade.projeto.ano, r.discenteExtensao.atividade.sequencia, r.discenteExtensao.atividade.tipoAtividadeExtensao.id, " +
					" r.discenteExtensao.atividade.tipoAtividadeExtensao.descricao ";
			
			String hql = "select " + projecao + "  from RelatorioBolsistaExtensao r " +
							"where r.ativo = trueValue() " +
							"and r.discenteExtensao.atividade.projeto.coordenador.pessoa.id = :idPessoa " +
							"order by r.discenteExtensao.atividade.projeto.id, r.discenteExtensao.discente.pessoa.nome";

			Query query = getSession().createQuery(hql);
			query.setInteger("idPessoa", idPessoa);
			return HibernateUtils.parseTo(query.list(), projecao, RelatorioBolsistaExtensao.class, "r");
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}


	/**
	 * Lista os relatórios ativos do tipo informado para o discenteExtensao informado Se o
	 * tipo não for informado, retorna todos os tipos de relatório do discenteExtensao
	 * 
	 * @param idDiscenteExtensao
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<RelatorioBolsistaExtensao> findByDiscenteExtensaoTipoRelatorio(
			Integer idDiscenteExtensao, Integer idTipoRelatorio) throws DAOException {

		try {
			StringBuffer hql = new StringBuffer(
					"select r from RelatorioBolsistaExtensao r  "
							+ "where r.discenteExtensao.id = :idDiscenteExtensao "
							+ "and r.ativo = trueValue() ");

			if (idTipoRelatorio != null)
				hql.append(" and r.tipoRelatorio.id = :idTipoRelatorio ");

			hql.append(" order by r.discenteExtensao.atividade.sequencia ");

			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idDiscenteExtensao", idDiscenteExtensao);

			if (idTipoRelatorio != null)
				query.setInteger("idTipoRelatorio", idTipoRelatorio);

			return query.list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Método usado para buscar as informações necessárias para a visualização do relatório de bolsista de extensão.
	 * 
	 * 
	 * @param idRelatorio
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public RelatorioBolsistaExtensao viewRelatorioBolsistaExtensao(Integer idRelatorio) throws HibernateException, DAOException {
		
		String hql = new String(" select proj.id, proj.ano, proj.titulo, p.nome, d.matricula, tipoAtiv.id, tipoAtiv.descricao, " +
											"        tipoVinculo.id, tipoVinculo.descricao, curso.nome, " +
											"        tipoRel.id, tipoRel.descricao, r.dataEnvio, r.introducao, " +
											"        r.metodologia, r.atividades, r.resultados, r.conclusoes, r.dataParecer, " +
											"        r.parecerOrientador " +
											" from RelatorioBolsistaExtensao r " +
											" inner join r.tipoRelatorio tipoRel  " +
											" inner join r.discenteExtensao de " +																						
											" inner join de.discente d" +
											" left join d.curso curso  " +
											" inner join de.tipoVinculo tipoVinculo " +
											" inner join d.pessoa p " +
											" inner join de.atividade at " +
											" inner join at.projeto proj" +
											" inner join at.tipoAtividadeExtensao tipoAtiv " +
											" where r.id  = :idRelatorio ");
		
		Query query = getSession().createQuery(hql);
		query.setInteger("idRelatorio", idRelatorio);
		Object obj[] = (Object[])query.uniqueResult();
		
		RelatorioBolsistaExtensao relatorio = new RelatorioBolsistaExtensao();		
		AtividadeExtensao atividade = new AtividadeExtensao();
		DiscenteExtensao discenteExtensao = new DiscenteExtensao();
		
		discenteExtensao.setAtividade(atividade);
		relatorio.setDiscenteExtensao(discenteExtensao);		
		
		int col = 0;
		
		atividade.getProjeto().setId((Integer) obj[col++]);
		atividade.getProjeto().setAno((Integer) obj[col++]);
		atividade.getProjeto().setTitulo((String) obj[col++]);
		
		
		
		discenteExtensao.getDiscente().getPessoa().setNome((String) obj[col++]);
		discenteExtensao.getDiscente().setMatricula((Long) obj[col++]);
		
		
		atividade.getTipoAtividadeExtensao().setId((Integer) obj[col++]);
		atividade.getTipoAtividadeExtensao().setDescricao((String) obj[col++]);
		
		discenteExtensao.getTipoVinculo().setId((Integer) obj[col++]);
		discenteExtensao.getTipoVinculo().setDescricao((String) obj[col++]);
		
		relatorio.getDiscenteExtensao().getDiscente().getCurso().setNome((String) obj[col++]);
		relatorio.getTipoRelatorio().setId((Integer) obj[col++]);
		relatorio.getTipoRelatorio().setDescricao((String) obj[col++]);
		relatorio.setDataEnvio((Date) obj[col++]);
		relatorio.setIntroducao((String) obj[col++]);
		relatorio.setMetodologia((String) obj[col++]);
		relatorio.setAtividades((String) obj[col++]);
		relatorio.setResultados((String) obj[col++]);
		relatorio.setConclusoes((String) obj[col++]);
		relatorio.setDataParecer((Date) obj[col++]);
		relatorio.setParecerOrientador((String) obj[col++]);	
		
		return relatorio;
	}

	/**
	 * Verifica se o discente de extensão enviou o relatório do tipo especificado.
	 * 
	 * @param idDiscenteExtensao
	 * @param idTipoRelatorio
	 * @return
	 * @throws DAOException
	 */
	public boolean isDiscenteEnviouRelatorio(Integer idDiscenteExtensao, Integer idTipoRelatorio) throws DAOException {
	    try {
		StringBuffer hql = new StringBuffer("select count(r.id) from RelatorioBolsistaExtensao r  "
			+ "where r.discenteExtensao.id = :idDiscenteExtensao "
			+ "and r.ativo = trueValue() " 
			+ "and r.tipoRelatorio.id = :idTipoRelatorio ");

		Query query = getSession().createQuery(hql.toString());
		query.setInteger("idDiscenteExtensao", idDiscenteExtensao);
		query.setInteger("idTipoRelatorio", idTipoRelatorio);
		return ((Long)query.uniqueResult() > 0);

	    } catch (Exception e) {
		throw new DAOException(e.getMessage(), e);
	    }
	}

	
}