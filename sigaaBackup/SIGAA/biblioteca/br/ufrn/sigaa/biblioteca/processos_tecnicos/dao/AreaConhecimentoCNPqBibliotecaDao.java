/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 24/02/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.AreaConhecimentoCnpqDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.InformacoesAreaCNPQBiblioteca;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;

/**
 *
 * <p> Dao para as buscas nas áreas de conhecimento CNPq para a biblioteca, necessário pois o módulo
 *  de biblioteca pode usar siglas e nomes diferentes para as áreas do CNPq
 * </p>
 * 
 * @author jadson
 *
 */
public class AreaConhecimentoCNPqBibliotecaDao extends AreaConhecimentoCnpqDao{

	
	/**
	 * Busca as informações das áreas CNPq da biblioteca, utilizado no cadastro dessas informacoes.
	 * 
	 * @param ordenacao
	 * @param sentidoOrdenacao
	 * @param projecao
	 * @return
	 * @throws DAOException
	 */
	public List<InformacoesAreaCNPQBiblioteca> findAllInformacoesAreasCNPqBiblioteca() throws DAOException {
		
		String projecao = " informacao.id, informacao.sigla, informacao.nome, informacao.area.id, informacao.area.sigla, informacao.area.nome ";
		
		String hql  = 
			 " SELECT "+projecao
			+" FROM InformacoesAreaCNPQBiblioteca informacao " 
			+" ORDER BY informacao.area.nome ";
		
		Query q = getSession().createQuery(hql);
		
		@SuppressWarnings("unchecked")
		List<InformacoesAreaCNPQBiblioteca> lista = new ArrayList<InformacoesAreaCNPQBiblioteca>(
			HibernateUtils.parseTo(q.list(), projecao, InformacoesAreaCNPQBiblioteca.class, "informacao") );
		
		return lista;
	}
	
	
	/**
	 * Retornas as siglas das áres de conhecimento CNPq utilizadas na biblioteca
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<String> findSiglasGrandesAreasCNPqBibliioteca() throws DAOException {
		
		
		String sql  = " SELECT COALESCE( info.sigla, area.sigla ) AS siglaUtilizada "+
			" FROM comum.area_conhecimento_cnpq AS area "+
			" LEFT JOIN biblioteca.informacoes_area_cnpq_biblioteca AS info ON info.id_area_conhecimento_cnpq = area.id_area_conhecimento_cnpq "+
			" WHERE area.id_area_conhecimento_cnpq > 0 " +
			" AND area.id_sub_area IS NULL "+            // sub área são nulas
			" AND area.id_especialidade IS NULL "+   // especialidade são nulas
			" AND area.codigo IS NULL "+             // todas as áreas cujo códigos são nulos
			" AND area.excluido = :false "+
			" ORDER BY siglaUtilizada ";
		
		Query q = getSession().createSQLQuery(sql);
		q.setBoolean("false", false);
		
		
		@SuppressWarnings("unchecked")
		List<String> list = q.list();
		return list;
		
	}
	
	
	/**
	 * Retornas as informações das grandes áreas de conhecimento CNPq (id, sigla e nome) utilizadas na biblioteca.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<AreaConhecimentoCnpq> findGrandesAreasCNPqBibliotecaComProjecao() throws DAOException {
		
		
		String sql  = " SELECT area.id_area_conhecimento_cnpq, COALESCE( info.sigla, area.sigla ) AS siglaUtilizada, COALESCE( info.nome, area.nome ) AS nomeUtilizado "+
			" FROM comum.area_conhecimento_cnpq AS area "+
			" LEFT JOIN biblioteca.informacoes_area_cnpq_biblioteca AS info ON info.id_area_conhecimento_cnpq = area.id_area_conhecimento_cnpq "+
			" WHERE area.id_area_conhecimento_cnpq > 0 " +
			" AND area.id_sub_area IS NULL "+            // sub área são nulas
			" AND area.id_especialidade IS NULL "+   // especialidade são nulas
			" AND area.codigo IS NULL "+             // todas as áreas cujo códigos são nulos
			" AND area.excluido = :false "+
			" ORDER BY siglaUtilizada ";
		
		Query q = getSession().createSQLQuery(sql);
		q.setBoolean("false", false);
		
		
		@SuppressWarnings("unchecked")
		List<Object[]> dados = q.list();
		
		Collection<AreaConhecimentoCnpq> gradesAreas = new ArrayList<AreaConhecimentoCnpq>();
		
		for (Object[] infoArea : dados) {
			AreaConhecimentoCnpq area = new AreaConhecimentoCnpq( (Integer)infoArea[0], (String)infoArea[1], (String) infoArea[2]);
			gradesAreas.add(area);
		}
		
		return gradesAreas;
		
	}
	
	/**
	 * <p>Retorna todas as grande áreas do CNPq com projeção.</p>
	 * 
	 * <p>Caso não seja passado nenhuma projeção, faz a projeção padrão é que sobre os campos "id" e "nome"</p>
	 * 
	 * <p><strong>Definição:</strong> Retorna todas as áreas cujo código, sub área, e especialidade são nulos</p>
	 *
	 *
	 * @return  a  grandes áreas do CNPq cadastradas no sistema.
	 * @throws DAOException
	 */
	public Collection<AreaConhecimentoCnpq> findGrandeAreasConhecimentoCnpq() throws DAOException {
		
		StringBuilder sql = new StringBuilder(
				" SELECT " +
					" acc.id_area_conhecimento_cnpq, " +
					" acc.nome, " +
					" COALESCE(iacb.sigla, acc.sigla) as siglaResultante " +
				" FROM " +
					" comum.area_conhecimento_cnpq acc " +
					" LEFT JOIN biblioteca.informacoes_area_cnpq_biblioteca iacb ON (iacb.id_area_conhecimento_cnpq = acc.id_area_conhecimento_cnpq) " +
				" WHERE " +
					" acc.id_sub_area IS NULL " +					// todas as áreas cujo códigos são nulos
					" AND acc.id_especialidade IS NULL " +			// e  sub área são nulas
					" AND acc.codigo IS NULL " +					// e especialidade são nulas
					" AND acc.id_area_conhecimento_cnpq > 0 " +
					" AND acc.excluido = falseValue() " +			// E não foi excluído do sistema
				" ORDER BY siglaResultante ASC "                    // IMPORTANTE:  Ordenar pela Sigla senão bagunça os dados do relatório de Crescimento por CNPq
				);
		
		Query q = getSession().createSQLQuery(sql.toString());
		
		@SuppressWarnings("unchecked")
		List<Object[]> resultado = q.list();
		
		Collection<AreaConhecimentoCnpq> lista = new ArrayList<AreaConhecimentoCnpq>();
		
		for (Object[] obj : resultado) {
			AreaConhecimentoCnpq area = new AreaConhecimentoCnpq();

			area.setId((Integer) obj[0]);
			area.setNome((String) obj[1]);
			area.setSigla((String) obj[2]);
			
			lista.add(area);
		}
		
		return lista;
	}
	
	
	/**
	 * Retornas as informações das grandes áreas de conhecimento CNPq (id, sigla e nome) utilizadas na biblioteca.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public AreaConhecimentoCnpq findDadosGrandeAreaCNPqBibliotecaComProjecao(int idAreaCNPq) throws DAOException {
		
		
		String sql  = " SELECT area.id_area_conhecimento_cnpq, COALESCE( info.sigla, area.sigla ) AS siglaUtilizada, COALESCE( info.nome, area.nome ) AS nomeUtilizado "+
			" FROM comum.area_conhecimento_cnpq AS area "+
			" LEFT JOIN biblioteca.informacoes_area_cnpq_biblioteca AS info ON info.id_area_conhecimento_cnpq = area.id_area_conhecimento_cnpq "+
			" WHERE area.id_area_conhecimento_cnpq = :idAreaCNPq " +
			" AND area.id_sub_area IS NULL "+            // sub área são nulas
			" AND area.id_especialidade IS NULL "+   // especialidade são nulas
			" AND area.codigo IS NULL "+             // todas as áreas cujo códigos são nulos
			" AND area.excluido = :false "+
			" ORDER BY siglaUtilizada ";
		
		Query q = getSession().createSQLQuery(sql);
		q.setInteger("idAreaCNPq", idAreaCNPq);
		q.setBoolean("false", false);
		
		Object[] infoArea = (Object[]) q.uniqueResult();
		
		if(infoArea == null) return null;
		
		return new AreaConhecimentoCnpq( (Integer)infoArea[0], (String)infoArea[1], (String) infoArea[2]);
		
	}
	
}
