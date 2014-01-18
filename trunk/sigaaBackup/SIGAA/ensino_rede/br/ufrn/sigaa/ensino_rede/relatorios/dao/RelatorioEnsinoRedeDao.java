package br.ufrn.sigaa.ensino_rede.relatorios.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.ensino_rede.dominio.DiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.dominio.DocenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.MatriculaComponenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.TurmaRede;

public class RelatorioEnsinoRedeDao extends GenericSigaaDAO {

	@SuppressWarnings("unchecked")
	public List<SelectItem> findUnidadesPrograma(int programa, CampusIes campus) throws HibernateException, DAOException {
		
		String sql = "select inst.sigla, camp.id_campus, camp.sigla as sigla_camp" +
				" from ensino_rede.dados_curso_rede dc " +
				" inner join ensino_rede.programa_rede prog on dc.id_programa_rede = prog.id_programa_rede" +
				" inner join comum.campus_ies camp on dc.id_campus=camp.id_campus" +
				" inner join comum.instituicoes_ensino inst on camp.id_ies = inst.id" +
				" where prog.id_programa_rede = " + programa;
		
				if ( campus != null )
					sql += " and camp.id_campus = " + campus.getId();
		
				sql += " order by inst.sigla, camp.sigla";

		List <Object[]> ls = getSession().createSQLQuery(sql).list();
		List<SelectItem> dados = new ArrayList<SelectItem>();
		List<SelectItem> result = new ArrayList<SelectItem>();
		
		String anterior = "";
		for (int i = 0; i < ls.size(); i++) {
			Object[] l = ls.get(i);
			String atual = (String) l[0];
			
			if ( !atual.equals(anterior) && !anterior.equals("") ) {
				SelectItemGroup grupo = new SelectItemGroup(anterior, null, false, dados.toArray( new SelectItem[]{}) );
				List<SelectItem> select = new ArrayList<SelectItem>();		
				select.add(grupo);
				result.addAll(select);
				dados = new ArrayList<SelectItem>();
			}
				
			dados.add(new SelectItem((Integer) l[1], (String) l[2] ));
			anterior = atual;

			if ( ls.size() == (i+1) ) {
				SelectItemGroup grupo = new SelectItemGroup(anterior, null, false, dados.toArray( new SelectItem[]{}) );
				List<SelectItem> select = new ArrayList<SelectItem>();		
				select.add(grupo);
				result.addAll(select);
			}
			
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<DiscenteAssociado> findByDiscentes(Map<String, List<?>> dados, String projecao, String ordenacao, String transiente, int idPrograma) throws DAOException {
		
		StringBuilder sb = new StringBuilder();
		if ( projecao != null && !projecao.isEmpty() )
			sb.append("select " + HibernateUtils.removeAliasFromProjecao(projecao)); 
		
		sb.append(" from DiscenteAssociado da "
				+ " join da.dadosCurso dc "
				+ " join dc.curso curso"
				+ " join da.status status "
				+ " join dc.programaRede programa "
				+ " join dc.campus campus "
				+ " join campus.instituicao instituicao"
				+ " join da.pessoa pessoa "
				+ " where programa.id = " + idPrograma);
				
			if ( dados.get("filtrarStatus") != null && !dados.get("filtrarStatus").isEmpty() ) {
				sb.append(" and da.status.id in " + UFRNUtils.gerarStringIn(dados.get("filtrarStatus")) );
			}

			if ( dados.get("filtrarUnidade") != null && !dados.get("filtrarUnidade").isEmpty() ) {
				sb.append(" and campus.id in " + UFRNUtils.gerarStringIn(dados.get("filtrarUnidade")) );
			}

		if ( ordenacao != null && !ordenacao.isEmpty() )
			sb.append(" order by " + ordenacao);
		
		Query query = getSession().createQuery(sb.toString());
		
		return (List<DiscenteAssociado>) HibernateUtils.parseTo(query.list(), projecao, DiscenteAssociado.class, "da");
	}

	@SuppressWarnings("unchecked")
	public List<DocenteRede> findByDocentes(Map<String, List<?>> dados, String projecao, String ordenacao, String transiente, int idPrograma) throws DAOException {
		
		StringBuilder sb = new StringBuilder();
		if ( projecao != null && !projecao.isEmpty() )
			sb.append("select " + HibernateUtils.removeAliasFromProjecao(projecao)); 
		
		if ( transiente != null && !transiente.isEmpty() ) {
			sb.append(" , (select c.descricao" +
					  " from CoordenadorUnidade cu " +
					  " join cu.cargo c " +
					  " where cu.dadosCurso.id = docente.dadosCurso.id " +
					  " and cu.ativo = trueValue() and cu.pessoa.id = docente.pessoa.id ) AS cargo ");
		}
		
		sb.append(" from DocenteRede docente"
				+ "	join docente.dadosCurso dc "
				+ "	join docente.situacao sit "
				+ "	join docente.pessoa pessoa "
				+ "	join docente.tipo tipo "
				+ " join dc.campus campus "
				+ " join campus.instituicao inst "
				+ " where dc.programaRede.id = " + idPrograma);
	
			if ( dados.get("filtrarUnidade") != null && !dados.get("filtrarUnidade").isEmpty() ) {
				sb.append(" and campus.id in " + UFRNUtils.gerarStringIn(dados.get("filtrarUnidade")) );
			}
		
		if ( ordenacao != null && !ordenacao.isEmpty() )
			sb.append(" order by " + ordenacao);
		
		Query query = getSession().createQuery(sb.toString());
		
		if ( transiente != null && !transiente.isEmpty() ) {
			projecao += transiente;
			//", docente.cargo"
		}
		
		return (List<DocenteRede>) HibernateUtils.parseTo(query.list(), projecao, DocenteRede.class, "docente");
	}
	
	@SuppressWarnings("unchecked")
	public List<DocenteRede> findByTurmas(Map<String, List<?>> dados, String projecao, String ordenacao, String transiente, int idPrograma) throws DAOException {
		
		StringBuilder sb = new StringBuilder();
		if ( projecao != null && !projecao.isEmpty() )
			sb.append("select " + HibernateUtils.removeAliasFromProjecao(projecao)); 
		
		sb.append(" from TurmaRede tur "
				+ " join tur.componente comp "
				+ " join tur.situacaoTurma sitTurma "
				+ "	join tur.dadosCurso dc "
				+ " join dc.campus campus "
				+ " join tur.docentesTurmas doc "
				+ " join doc.docente docente "
				+ " join campus.instituicao inst "
				+ " join tur.componente comp "
				+ " where dc.programaRede.id = " + idPrograma);
	
			if ( dados.get("filtrarUnidade") != null && !dados.get("filtrarUnidade").isEmpty() ) {
				sb.append(" and campus.id in " + UFRNUtils.gerarStringIn(dados.get("filtrarUnidade")) );
			}

			if ( dados.get("filtrarAnoPeriodo") != null && !dados.get("filtrarAnoPeriodo").isEmpty() 
					&& !dados.get("filtrarAnoPeriodo").get(0).equals("") ) {
				String[] anoPeriodo = ((String) dados.get("filtrarAnoPeriodo").get(0)).split("\\.");
				sb.append(" and tur.ano = " + anoPeriodo[0] + " and tur.periodo = " + anoPeriodo[1] );
			}
			
		if ( ordenacao != null && !ordenacao.isEmpty() )
			sb.append(" order by " + ordenacao);
		
		Query query = getSession().createQuery(sb.toString());
		
		return (List<DocenteRede>) HibernateUtils.parseTo(query.list(), projecao, TurmaRede.class, "tur");
	}

	@SuppressWarnings("unchecked")
	public List<MatriculaComponenteRede> findByMatricula(Map<String, List<?>> dados, String projecao, String ordenacao, String transiente, int idPrograma) throws DAOException {
		
		StringBuilder sb = new StringBuilder();
		if ( projecao != null && !projecao.isEmpty() )
			sb.append("select " + HibernateUtils.removeAliasFromProjecao(projecao)); 
		
		sb.append(" from MatriculaComponenteRede mat "
				+ " join mat.situacao sitMat "
				+ " join mat.turma turma "
				+ " join turma.componente comp "
				+ " join turma.situacaoTurma sitTurma "
				+ "	join turma.dadosCurso dc "
				+ " join turma.docentesTurmas doc "
				+ " join turma.componente comp "
				+ " join dc.campus campus "
				+ " join doc.docente docente "
				+ " join campus.instituicao inst "
				+ " where dc.programaRede.id = " + idPrograma);
	
			if ( dados.get("filtrarUnidade") != null && !dados.get("filtrarUnidade").isEmpty() ) {
				sb.append(" and campus.id in " + UFRNUtils.gerarStringIn(dados.get("filtrarUnidade")) );
			}

			if ( dados.get("filtrarAnoPeriodo") != null && !dados.get("filtrarAnoPeriodo").isEmpty() ) {
				String[] anoPeriodo = ((String) dados.get("filtrarAnoPeriodo").get(0)).split("\\.");
				sb.append(" and mat.ano = " + anoPeriodo[0] + " and mat.periodo = " + anoPeriodo[1] );
			}

			if ( dados.get("filtrarDisciplina") != null && !dados.get("filtrarDisciplina").isEmpty() ) {
				sb.append(" and comp.id in " + UFRNUtils.gerarStringIn(dados.get("filtrarDisciplina")) );
			}
			
		if ( ordenacao != null && !ordenacao.isEmpty() )
			sb.append(" order by " + ordenacao);
		
		if ( transiente != null && !transiente.isEmpty() ) {
			projecao += transiente;
			//", docente.cargo"
		}
		
		Query query = getSession().createQuery(sb.toString());
		
		return (List<MatriculaComponenteRede>) HibernateUtils.parseTo(query.list(), projecao, MatriculaComponenteRede.class, "mat");
	}

}