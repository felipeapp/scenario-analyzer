/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 18/07/2008
 *
 */	
package br.ufrn.sigaa.arq.dao.ensino;

import static org.hibernate.criterion.Restrictions.eq;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.CurriculoComponente;
import br.ufrn.sigaa.ensino.graduacao.dominio.GrupoOptativas;

/**
 * DAO para buscas relativas a Grupos de Componentes Optativos
 * em um Currículo.
 * 
 * @author David Pereira
 *
 */
@SuppressWarnings("unchecked")
public class GrupoOptativasDao extends GenericSigaaDAO {

	/**
	 * Busca a lista de componentes curriculares obrigatórios para um determinado currículo.
	 * @param curriculo
	 * @return
	 * @throws DAOException
	 */
	public List<ComponenteCurricular> findComponentesObrigatoriosByCurriculo(Curriculo curriculo) throws DAOException {
		String hql = "select co from CurriculoComponente cc left join cc.componente co left join co.detalhes d "
			+ "where cc.obrigatoria = true and cc.curriculo.id = ? order by d.nome";
		return getSession().createQuery(hql).setInteger(0, curriculo.getId()).list();
	}
	
	/**
	 * Retorna uma lista de grupos de componentes optativos de acordo com
	 * um currículo passado como parâmetro.
	 */
	public List<GrupoOptativas> findGruposOptativasByCurriculo(Curriculo curriculo) throws DAOException {
		Criteria c = getSession().createCriteria(GrupoOptativas.class);
		c.add(eq("curriculo.id", curriculo.getId()));
		return c.list();
	}

	/**
	 * Verifica se os componentes de um grupo de optativas de um currículo não estão presentes
	 * em um outro grupo de optativas do mesmo currículo. 
	 */
	public List<Map<String, Object>> findComponentePertencemOutroGrupo(GrupoOptativas grupo, Curriculo curriculo, List<CurriculoComponente> componentesEscolhidos) {
		StringBuilder sb = new StringBuilder("select ccd.nome, descricao from graduacao.grupo_optativas go, "
				+ "graduacao.curriculo_optativa co, graduacao.curriculo_componente cu, "
				+ "ensino.componente_curricular cc, ensino.componente_curricular_detalhes ccd "
				+ "where go.id_grupo_optativas = co.id_grupo_optativas and co.id_curriculo_componente = cu.id_curriculo_componente "
				+ "and cu.id_componente_curricular = cc.id_disciplina "
				+ "and go.id_curriculo = ? and ccd.id_componente_detalhes = cc.id_detalhe "
				+ "and cu.id_componente_curricular in (");
		for (Iterator<CurriculoComponente> it = componentesEscolhidos.iterator(); it.hasNext(); ) {
			CurriculoComponente cc = it.next();
			sb.append(cc.getComponente().getId());
			if (it.hasNext())
				sb.append(",");
		}
		sb.append(")");
		
		if (grupo.getId() > 0) {
			sb.append(" and go.id_grupo_optativas != " + grupo.getId());
		}
		
		return getJdbcTemplate().queryForList(sb.toString(), new Object[] { curriculo.getId() });
	}

	/**
	 * Retorna os {@link GrupoOptativas} que possui o {@link CurriculoComponente}
	 * 
	 * @param idCurriculoComponente
	 * @return
	 * @throws DAOException
	 */
	public List<GrupoOptativas> findByCurriculoComponente(int idCurriculoComponente) throws DAOException {
		String hql = "select go from GrupoOptativas go join go.componentes comp where comp.id = :id";
		
		Query q = getSession().createQuery(hql);
		
		q.setInteger("id", idCurriculoComponente);
		
		
		return q.list();
	}
	
}
