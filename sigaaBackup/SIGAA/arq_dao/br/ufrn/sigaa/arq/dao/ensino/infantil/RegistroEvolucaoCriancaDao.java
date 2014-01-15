/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 07/12/2009
 *
 */

package br.ufrn.sigaa.arq.dao.ensino.infantil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.infantil.dominio.Area;
import br.ufrn.sigaa.ensino.infantil.dominio.ConteudoBimestre;
import br.ufrn.sigaa.ensino.infantil.dominio.FormularioEvolucaoCrianca;
import br.ufrn.sigaa.ensino.infantil.dominio.ObjetivoBimestre;

/**
 * Dao com consultas para o registro de evolução da criança do ensino infantil.
 * 
 * @author Leonardo Campos
 *
 */
public class RegistroEvolucaoCriancaDao extends GenericSigaaDAO {

	
	/**
	 * Retorna o formulário de evolução da criança ativo de um determinado nível (componente curricular)
	 * do ensino infantil.
	 * @return
	 * @throws DAOException
	 */
	@Deprecated
	public FormularioEvolucaoCrianca findAtivoByNivelInfantil(ComponenteCurricular nivelInfantil) throws DAOException {
		Criteria c = getSession().createCriteria(FormularioEvolucaoCrianca.class);
		c.add(Restrictions.eq("nivelInfantil.id", nivelInfantil.getId()));
		c.add(Restrictions.eq("ativo", Boolean.TRUE));
		return (FormularioEvolucaoCrianca) c.uniqueResult();
	}
	
	
	/**
	 * Retorna um mapa com os identificadores dos objetivos e sua respectiva avaliação para aquela matrícula na área informada. 
	 * @param matricula
	 * @param bimestre
	 * @return
	 * @throws DAOException
	 */
	public Map<Integer, Map<Integer, ObjetivoBimestre>> findMapaObjetivos(MatriculaComponente matricula, Area area) throws DAOException{
		Map<Integer, Map<Integer, ObjetivoBimestre>> result = new HashMap<Integer, Map<Integer, ObjetivoBimestre>>();
		StringBuilder consulta = new StringBuilder();
		consulta.append("FROM ObjetivoBimestre o WHERE o.matricula.id = " + matricula.getId());
		if(area != null)
			consulta.append(" AND o.objetivo.conteudo.area.id = " + area.getId());
		consulta.append(" ORDER BY o.objetivo.conteudo.ordem, o.objetivo.ordem, o.bimestre ");
		@SuppressWarnings("unchecked")
		List<ObjetivoBimestre> lista = getSession().createQuery(consulta.toString()).list();
		for(ObjetivoBimestre o: lista){
			Map<Integer, ObjetivoBimestre> bimestres = new TreeMap<Integer, ObjetivoBimestre>();
			if(result.get(o.getObjetivo().getId()) == null){
				bimestres.put(o.getBimestre(), o);
				result.put(o.getObjetivo().getId(), bimestres);
			} else {
				bimestres = result.get(o.getObjetivo().getId());
				bimestres.put(o.getBimestre(), o);
				result.put(o.getObjetivo().getId(), bimestres);
			}
		}
		return result;
	}
	
	/**
	 * Retorna um mapa com os identificadores dos conteúdos e suas respectivas observações para aquela matrícula na área informada. 
	 * @param matricula
	 * @param bimestre
	 * @return
	 * @throws DAOException
	 */
	public Map<Integer, Map<Integer, ConteudoBimestre>> findMapaConteudos(MatriculaComponente matricula, Area area) throws DAOException{
		Map<Integer, Map<Integer, ConteudoBimestre>> result = new HashMap<Integer, Map<Integer, ConteudoBimestre>>();
		StringBuilder consulta = new StringBuilder();
		consulta.append("FROM ConteudoBimestre c WHERE c.matricula.id = " + matricula.getId());
		if(area != null)
			consulta.append(" AND c.conteudo.area.id = " + area.getId());
		consulta.append(" ORDER BY c.conteudo.ordem, c.bimestre ");
		@SuppressWarnings("unchecked")
		List<ConteudoBimestre> lista = getSession().createQuery(consulta.toString()).list();
		for(ConteudoBimestre c: lista){
			Map<Integer, ConteudoBimestre> bimestres = new TreeMap<Integer, ConteudoBimestre>();
			if(result.get(c.getConteudo().getId()) == null){
				bimestres.put(c.getBimestre(), c);
				result.put(c.getConteudo().getId(), bimestres);
			} else {
				bimestres = result.get(c.getConteudo().getId());
				bimestres.put(c.getBimestre(), c);
				result.put(c.getConteudo().getId(), bimestres);
			}
		}
		return result;
	}
	
	/**
	 * Retorna os Componentes Curriculares do Nível Infantil 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<ComponenteCurricular> findComponenteCurricularNivelInfantil() throws DAOException {
		String consulta = "from ComponenteCurricular where nivel = 'I'";
		List<ComponenteCurricular> list = getSession().createQuery(consulta).list();
		return list;
	}
}
