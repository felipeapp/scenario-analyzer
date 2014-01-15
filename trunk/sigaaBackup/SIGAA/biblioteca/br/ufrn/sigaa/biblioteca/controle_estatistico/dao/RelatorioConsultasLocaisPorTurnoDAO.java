/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 08/11/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.AgrupamentoRelatoriosBiblioteca;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.ConstantesRelatorioBiblioteca;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.FiltroClassificacoesRelatoriosBiblioteca;

/**
 *
 * <p>DAO utilizado exclusivamente para o relatório de consultas locais por turno. </p>
 * 
 * @author Felipe Rivas
 *
 */
public class RelatorioConsultasLocaisPorTurnoDAO extends GenericSigaaDAO {
	
	/**
	 * Gera um relatório quantitativo das consultas dos CDU ou Black, tendo como parâmetro período inicial
	 * e final.
	 * 
	 * @param tipoDeMaterial <code>null</code> ou <code><= 0</code> para todos materiais
	 */
	public List<Object[]> findConsultasLocaisRegistradasNoPeriodo( Date inicioPeriodo, Date fimPeriodo, 
			Collection<Integer> idsBibliotecas, Collection<Integer> idsColecoes, Collection<Integer> idsTiposMateriais, 
			FiltroClassificacoesRelatoriosBiblioteca classificacao, AgrupamentoRelatoriosBiblioteca agrupamento1 ) throws DAOException {
		
		StringBuilder sql = new StringBuilder(
				" SELECT ( CASE rcdm.turno WHEN "+ConstantesRelatorioBiblioteca.Turnos.MANHA.getValor()+" THEN '"+ConstantesRelatorioBiblioteca.Turnos.MANHA.getDescricaoCompleta()
				+"' WHEN "+ConstantesRelatorioBiblioteca.Turnos.TARDE.getValor()+" THEN '"+ConstantesRelatorioBiblioteca.Turnos.TARDE.getDescricaoCompleta()
				+"' WHEN "+ConstantesRelatorioBiblioteca.Turnos.NOITE.getValor()+" THEN '"+ConstantesRelatorioBiblioteca.Turnos.NOITE.getDescricaoCompleta()+"' END )  as turno ");
		
		if(agrupamento1 != AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO )
			sql.append(", COALESCE( " + agrupamento1.campoAgrupamento + ", '" + agrupamento1.substituiValoresNull + "' ) AS "+agrupamento1.nomeCampo+" ");
				
		sql.append(", COALESCE( classeMaterial." + classificacao.getColunaClassificacao() + ", 'Sem' ) AS classe");
				
		sql.append(", SUM(classeMaterial.quantidade) AS quantidade " +
				" FROM biblioteca.registro_consultas_diarias_materiais AS rcdm " +
				" INNER JOIN biblioteca.classe_material_consultado AS classeMaterial ON rcdm.id_registro_consultas_diarias_materiais = classeMaterial.id_registro_consultas_diarias_materiais ");
		
		if(agrupamento1 == AgrupamentoRelatoriosBiblioteca.COLECAO )
			sql.append(" INNER JOIN biblioteca.colecao colecao ON colecao.id_colecao = rcdm.id_colecao ");
		
		if(agrupamento1 == AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL )
			sql.append(" INNER JOIN biblioteca.tipo_material tipoMaterial ON tipoMaterial.id_tipo_material = rcdm.id_tipo_material  ");
		
		
		sql.append(" WHERE classeMaterial.ativo = trueValue()  AND classeMaterial." + classificacao.getColunaClassificacao() + " IS NOT NULL ");
		
		
		if ( inicioPeriodo != null ) {
			sql.append(" AND rcdm.data_consulta >= '" + CalendarUtils.format(inicioPeriodo, "yyyy-MM-dd") + " 00:00:00.000' ");
		}
		
		if ( fimPeriodo != null ) {
			sql.append(" AND rcdm.data_consulta <= '" + CalendarUtils.format(fimPeriodo, "yyyy-MM-dd") + " 23:59:59.999' ");
		}
		
		if ( idsBibliotecas != null && ! idsBibliotecas.isEmpty() ) {
			sql.append(" AND rcdm.id_biblioteca IN (" + StringUtils.join(idsBibliotecas, ", ") + ") ");
		}

		if ( idsColecoes != null && ! idsColecoes.isEmpty() ) {
			sql.append(" AND rcdm.id_colecao IN (" + StringUtils.join(idsColecoes, ", ") + ") ");
		}
		
		if ( idsTiposMateriais != null && ! idsTiposMateriais.isEmpty() ) {
			sql.append(" AND rcdm.id_tipo_material IN (" + StringUtils.join(idsTiposMateriais, ", ") + ") ");
		}
		
		sql.append(" GROUP BY turno "+  ( agrupamento1 != AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO ? ", "+agrupamento1.campoAgrupamento : "" ) + ", classeMaterial." + classificacao.getColunaClassificacao()   );
		sql.append(" ORDER BY turno" +  ( agrupamento1 != AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO ? ", "+agrupamento1.campoAgrupamento : "" ) + ", classeMaterial." + classificacao.getColunaClassificacao()  );

		Query q = getSession().createSQLQuery( sql.toString() );
		
		@SuppressWarnings("unchecked")
		List <Object[]> rs = q.list();
		return rs;
	}

}
