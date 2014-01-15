/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '07/02/2012'
 *
 */
package br.ufrn.sigaa.ensino.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.EquivalenciaEspecifica;
import br.ufrn.sigaa.ensino.dominio.GrauAcademico;
import br.ufrn.sigaa.ensino.dominio.Turno;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.Enfase;
import br.ufrn.sigaa.ensino.graduacao.dominio.Habilitacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

/**
 * DAO responsável por consultas de Equivalências Especificas.
 *
 */
public class EquivalenciaEspecificaDao extends GenericSigaaDAO {

	/**
	 * Retorna as Equivalências Especificas por Curso,
	 * Matriz Curricular, Currículo ou Componente Curricular.  
	 * 
	 * @return
	 * @throws DAOException
	 */	
	public Collection<EquivalenciaEspecifica> findByCursoMatrizAndDisciplina(Integer idCurso, Integer idMatriz, Integer idCurriculo, Integer idDisciplina )
			throws DAOException {

		StringBuilder hql = new StringBuilder();
		
		hql.append(" SELECT distinct ee.id, " +
					" cc.codigo, " +
					" detalhes.nome, " +
					" curriculo.id," +
					" curriculo.codigo," +
					" curriculo.anoEntradaVigor," +
					" curriculo.periodoEntradaVigor," +
					" curso.nome, " +
					" municipio.nome, " +
					" habilitacao.nome, " +
					" enfase.nome, " +
					" turno.sigla, " +
					" grauAcademico.descricao," +
					" ee.inicioVigencia, " +
					" ee.fimVigencia, " +
					" ee.ativo " +
					" FROM EquivalenciaEspecifica ee " +
					" LEFT JOIN ee.componente cc " +
					" LEFT JOIN cc.detalhes detalhes " +
					" LEFT JOIN ee.curriculo curriculo " +
					" LEFT JOIN curriculo.matriz matriz " +
					" LEFT JOIN matriz.enfase enfase " +
					" LEFT JOIN matriz.habilitacao habilitacao " +
					" LEFT JOIN matriz.turno turno " +
					" LEFT JOIN matriz.curso curso " +
					" LEFT JOIN curso.municipio municipio " +
					" LEFT JOIN matriz.grauAcademico grauAcademico " +
					" WHERE 1=1 ");
		if (idCurso != null)
			hql.append(" and curso.id = :idCurso  ");
		if (idMatriz != null)
			hql.append(" and matriz.id = :idMatriz  ");
		if (idCurriculo != null)
			hql.append(" and curriculo.id = :idCurriculo ");
		if (idDisciplina != null)
			hql.append(" and cc.id = :idDisciplina ");
		
		hql.append("ORDER BY curso.nome, municipio.nome, habilitacao.nome, enfase.nome, turno.sigla, grauAcademico.descricao, detalhes.nome");
		
		Query query = getSession().createQuery(hql.toString());
		
		if (idCurso != null)
			query.setInteger("idCurso", idCurso);
		if (idMatriz != null)
			query.setInteger("idMatriz", idMatriz);
		if (idCurriculo != null)
			query.setInteger("idCurriculo", idCurriculo);
		if (idDisciplina != null)
			query.setInteger("idDisciplina", idDisciplina);
		
		@SuppressWarnings("unchecked")
		List<Object> lista = query.list();

		List<EquivalenciaEspecifica> result = null;
		for (int a = 0; a < lista.size(); a++) {
			
			if (result == null){
				result = new ArrayList<EquivalenciaEspecifica>();
			}
			
			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);
			EquivalenciaEspecifica equivalenciaEspecifica = new EquivalenciaEspecifica();
			equivalenciaEspecifica.setId((Integer) colunas[col++]);
			
			equivalenciaEspecifica.setComponente(new ComponenteCurricular());
			equivalenciaEspecifica.getComponente().setCodigo((String) colunas[col++]);
			equivalenciaEspecifica.getComponente().setDetalhes(new ComponenteDetalhes());
			equivalenciaEspecifica.getComponente().getDetalhes().setNome((String) colunas[col++]);
			
			equivalenciaEspecifica.setCurriculo(new Curriculo());
			equivalenciaEspecifica.getCurriculo().setId((Integer) colunas[col++]);
			equivalenciaEspecifica.getCurriculo().setCodigo((String) colunas[col++]);
			equivalenciaEspecifica.getCurriculo().setAnoEntradaVigor((Integer) colunas[col++]);
			equivalenciaEspecifica.getCurriculo().setPeriodoEntradaVigor((Integer) colunas[col++]);
			
			equivalenciaEspecifica.getCurriculo().setMatriz(new MatrizCurricular());
			
			equivalenciaEspecifica.getCurriculo().getMatriz().setCurso(new Curso());
			equivalenciaEspecifica.getCurriculo().getMatriz().getCurso().setNome((String) colunas[col++]);
			equivalenciaEspecifica.getCurriculo().getMatriz().getCurso().setMunicipio(new Municipio());
			equivalenciaEspecifica.getCurriculo().getMatriz().getCurso().getMunicipio().setNome((String) colunas[col++]);
					
			equivalenciaEspecifica.getCurriculo().getMatriz().setHabilitacao(new Habilitacao());
			equivalenciaEspecifica.getCurriculo().getMatriz().getHabilitacao().setNome((String) colunas[col++]);
			
			equivalenciaEspecifica.getCurriculo().getMatriz().setEnfase(new Enfase());
			equivalenciaEspecifica.getCurriculo().getMatriz().getEnfase().setNome((String) colunas[col++]);
			
			equivalenciaEspecifica.getCurriculo().getMatriz().setTurno(new Turno());
			equivalenciaEspecifica.getCurriculo().getMatriz().getTurno().setSigla((String) colunas[col++]);

			equivalenciaEspecifica.getCurriculo().getMatriz().setGrauAcademico(new GrauAcademico());
			equivalenciaEspecifica.getCurriculo().getMatriz().getGrauAcademico().setDescricao((String) colunas[col++]);
			
			equivalenciaEspecifica.setInicioVigencia((Date) colunas[col++]);
			equivalenciaEspecifica.setFimVigencia((Date) colunas[col++]);
			equivalenciaEspecifica.setAtivo((Boolean) colunas[col++]);
			
			result.add(equivalenciaEspecifica);
		}
		return result;
	}
	
}
