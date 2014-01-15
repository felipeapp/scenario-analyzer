package br.ufrn.sigaa.arq.dao.ensino.latosensu;

import java.util.ArrayList;
import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.latosensu.dominio.ComponenteCursoLato;
import br.ufrn.sigaa.ensino.latosensu.relatorios.LinhaComponenteCursoLato;

/**
 * DAO para buscas dos Componentes do Curso Lato Sensu.
 * 
 * @author guerethes
 */
public class ComponenteCursoLatoDao extends GenericSigaaDAO {

	/**
	 * Retorna todos os Componentes do Curso Lato da proposta informada.
	 * 
	 * @param idCursoLato
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<LinhaComponenteCursoLato> findComponenteCursoLatoByCurso(int idCursoLato) throws DAOException {
		String sql = "select distinct el.carga_horaria, p.nome as nome_docente, p2.nome as nome_docente_externo, el.id_equipe_lato, el.id_servidor, " +
				" el.id_docente_externo, cc.id_disciplina, cl.id_proposta, cc.codigo, cc.bibliografia, ccd.ementa, cast(ccd.ch_total as integer)," +
				" ccd.nome as nome_curso" +
				" from lato_sensu.componente_curso_lato ccl" +
				" inner join ensino.componente_curricular cc on (ccl.id_componente_curricular = cc.id_disciplina)" +
				" inner join ensino.componente_curricular_detalhes ccd on (cc.id_detalhe = ccd.id_componente_detalhes)" +
				" inner join lato_sensu.curso_lato cl on (ccl.id_curso_lato = cl.id_curso)" +
				" left join lato_sensu.equipe_lato el  on (cc.id_disciplina = el.id_disciplina)" +
				" left join rh.servidor serv on (el.id_servidor = serv.id_servidor)" +
				" left join comum.pessoa p using (id_pessoa)" +
				" left join ensino.docente_externo de on (el.id_docente_externo = de.id_docente_externo)" +
				" left join comum.pessoa p2 on (de.id_pessoa = p2.id_pessoa)" +
				" where ccl.id_curso_lato = "+idCursoLato+" and cc.ativo = trueValue()" +
				" group by el.id_disciplina, cc.codigo, cc.bibliografia, ccd.ementa,ccd.ch_total, ccd.nome, p.nome,  p2.nome," +
				" el.id_equipe_lato, el.id_servidor, el.id_docente_externo, cc.id_disciplina, el.carga_horaria, cl.id_proposta" +
				" order by cc.id_disciplina";

		Query q = getSession().createSQLQuery(sql);
		Collection<Object[]> bulk = q.list();
		Collection<LinhaComponenteCursoLato> lista = new ArrayList<LinhaComponenteCursoLato>();
		int idDisciplina = 0, count=0;
		LinhaComponenteCursoLato linha = null;
		if (bulk != null) {
			for (Object[] obj : bulk){
				if (linha != null && idDisciplina!=0 && ((Integer) obj[6] != idDisciplina)){ 
					lista.add(linha);
					idDisciplina=0;
				}
					
				if (linha != null && idDisciplina!=0 && ((Integer) obj[6] == idDisciplina)){ 
					if (((String) obj[1]) != null)
						linha.getNomeDocente().put((String) obj[1], ((Integer) obj[0]));
					else if (((String) obj[2]) != null)
						linha.getNomeDocente().put((String) obj[2], ((Integer) obj[0]));
				}
				else {
					linha = new LinhaComponenteCursoLato();
					if ((String) obj[1] != null){
						linha.getNomeDocente().put((String) obj[1], ((Integer) obj[0]));
						linha.setDocenteInterno(true);
					} else if ((String) obj[2] != null) {
						linha.getNomeDocente().put((String) obj[2], ((Integer) obj[0]));
						linha.setDocenteInterno(false);
					}
					linha.setIdEquipeLato((Integer) obj[3]);
					linha.setIdServidor((Integer) obj[4]);
					linha.setIdDocenteExterno((Integer) obj[5]);
					linha.setIdDisciplina((Integer) obj[6]);
					linha.setIdProposta((Integer) obj[7]);
					linha.setCodigo((String) obj[8]);
					linha.setBibliografia((String) obj[9]);
					linha.setEmenta((String) obj[10]);
					linha.setCargaHorariaTotal((Integer) obj[11]);
					linha.setNomeCurso((String) obj[12]);
					idDisciplina = linha.getIdDisciplina();
				}
				count++;
				if (bulk.size() == count) 
					lista.add(linha);	
			}
		}
		return lista;
	}

	/**
	 * Retorna todos os componentes do Curso Lato da disciplina e do curso informado.
	 * 
	 * @param idDisciplina
	 * @param cursoLato
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings({ "unchecked" })
	public Collection<ComponenteCursoLato> findComponenteCursoLatoByDisciplina(Integer idDisciplina, Integer cursoLato) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(ComponenteCursoLato.class);
			c.add(Restrictions.eq("disciplina.id", idDisciplina));
			c.add(Restrictions.eq("cursoLato.id", cursoLato));
			return c.list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
}