/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 28/04/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.FiltroClassificacoesRelatoriosBiblioteca;

/**
 *
 * <p>DAO utilizado exclusivamente para o relatório de total de periódicos por classificação </p>
 * 
 * @author Felipe Rivas
 *
 */
public class RelatorioTotalPeriodicosPorClasseDAO extends GenericSigaaDAO {
	
	
	
	/**
	 * Encontra todos os periódicos da biblioteca passada e do tipo do tombamento passado
	 * (compra ou doação), separados por classificação. Passe -1 para não levar em conta o
	 * tipo de tombamento.
	 * 
	 */
	public List<Object[]> findPeriodicosClasseAcervoByTombamentoBiblioteca(Collection<Integer> idBibliotecaList, Collection<Integer> idSituacaoMaterialList,
			Date inicioPeriodo, Date fimPeriodo, int tipoDeTombamento, FiltroClassificacoesRelatoriosBiblioteca classificacao )throws DAOException {
		
		String sqlBiblioteca = "";
		String sqlSituacao = "";
		String sqlTipoTombamento = "";

		if ( idBibliotecaList != null && ! idBibliotecaList.isEmpty() )
			sqlBiblioteca = " AND m.id_biblioteca IN ( " + StringUtils.join(idBibliotecaList, ", ") + " ) ";
		if ( idSituacaoMaterialList != null && !!! idSituacaoMaterialList.isEmpty() )
			sqlSituacao = " AND m.id_situacao_material_informacional IN ( " + StringUtils.join(idSituacaoMaterialList, ", ") + " ) ";
		if ( tipoDeTombamento != -1 )
			sqlTipoTombamento = " AND a.modalidade_aquisicao = " + tipoDeTombamento + " ";
		
		String sql = " SELECT a.id_assinatura as assinatura, a.internacional as internacional, count(f.id_fasciculo) as qtd, COALESCE( t." + classificacao.getColunaClassePrincipal()+", 'Sem Classe') as classificacao "+
		" FROM biblioteca.fasciculo f "+
		" INNER JOIN biblioteca.material_informacional m on m.id_material_informacional = f.id_fasciculo "+
		" INNER JOIN biblioteca.assinatura a on a.id_assinatura = f.id_assinatura "+
		" INNER JOIN biblioteca.titulo_catalografico t on t.id_titulo_catalografico = a.id_titulo_catalografico "+
		" INNER JOIN biblioteca.situacao_material_informacional s on s.id_situacao_material_informacional = m.id_situacao_material_informacional "+
		" WHERE f.incluido_acervo = :true and m.ativo = :true AND s.situacao_de_baixa = :false "+
		sqlBiblioteca+
		sqlSituacao+
		sqlTipoTombamento+
		(inicioPeriodo != null ? "	AND m.data_criacao >= :inicioPeriodo " : "") +
		(fimPeriodo    != null ? "	AND m.data_criacao <= :fimPeriodo    " : "") +
		" GROUP BY a.id_assinatura, a.internacional, t."+classificacao.getColunaClassePrincipal();
		
		
		
		Query query = getSession().createSQLQuery(sql);
		query.setBoolean("true", true);
		query.setBoolean("false", false);
		
		if (inicioPeriodo != null) {
			query.setTimestamp("inicioPeriodo", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0) );
		}

		if (fimPeriodo != null) {
			query.setTimestamp("fimPeriodo", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999) );
		}

		@SuppressWarnings("unchecked")
		List<Object []> rs = query.list();
		return rs;
	}

	/**
	 * Recupera o valor da periodicidade e a data de criação do do último fascículo no acervo para 
	 * verificar se assinatura e consequentemente os seu fascículos são corrente ou não.
	 *
	 * @param idAssinatura
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<Object[]> recuperaPeriodicidadeDataRegistroUltimoFasciculo(List<Integer> idsAssinatura) throws HibernateException, DAOException{
	
		String sql = " select fre.tempo_expiracao, MAX(m.data_criacao), a.id_assinatura  "+
		" FROM biblioteca.fasciculo f "+
		" INNER JOIN biblioteca.material_informacional m ON id_material_informacional = f.id_fasciculo "+
		" INNER JOIN biblioteca.assinatura a ON a.id_assinatura = f.id_assinatura "+
		" LEFT JOIN biblioteca.frequencia_periodicos fre ON fre.id_frequencia_periodicos = a.id_frequencia_periodicos "+
		" WHERE f.id_assinatura IN ( :idsAssinatura ) AND m.ativo = :true AND f.incluido_acervo = :true "+
		" GROUP BY a.id_assinatura, fre.tempo_expiracao  ";
		
		Query query = getSession().createSQLQuery(sql);
		query.setParameterList("idsAssinatura", idsAssinatura);
		query.setBoolean("true", true);
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = query.list();
		return list;
	}
	
}
