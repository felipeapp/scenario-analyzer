/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 14/04/2010
 *
 */

package br.ufrn.sigaa.arq.dao.ensino.infantil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.infantil.dominio.Area;
import br.ufrn.sigaa.ensino.infantil.dominio.FormularioEvolucaoCrianca;

/**
 * Dao com consultas para o Formulário de Evolução da Criança do Ensino Infantil.
 * 
 * @author Thalisson Muriel
 *
 */
public class FormularioEvolucaoDao extends GenericSigaaDAO {
	
	/**
	 * Retorna os Formulários de Evolução ativos.
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<FormularioEvolucaoCrianca> findFormulariosEvolucaoCrianca() throws DAOException{
		String consulta = "from FormularioEvolucaoCrianca form where form.ativo = :ativo";
		Query q = getSession().createQuery(consulta);
		q.setBoolean("ativo", true);
		return q.list();
	}

	
	/**
	 * Retorna as Áreas do Formulário de Evolução informado.
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Area> findBlocosByFormulario(int idForm) throws DAOException {
		String consulta = "from Area a where a.formulario.id = :idForm";
		Query q = getSession().createQuery(consulta);
		q.setInteger("idForm", idForm);
		return q.list();
	}
	
	
	/**
	 * Retorna as Áreas de um componente curricular.
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Area> findAreasByComponente(int id) throws DAOException {
		String consulta = "	select distinct a.id_area, a.descricao , a.ordem from infantil.area a " +
			" join infantil.formulario_evolucao_crianca f on f.id_formulario_evolucao_crianca = a.id_formulario_evolucao_crianca " +
			" where f.id_componente_curricular = " +id+ "and f.ativo = trueValue() " +
			" order by a.ordem";
		
		Query q = getSession().createSQLQuery(consulta);
		List<Object[]> result = q.list();
		ArrayList<Area> areas = new ArrayList<Area>();
		
		if ( result != null )
		{	
			for (Object[] linha : result) {
				Integer i = 0;
				Area a = new Area();
				a.setId((Integer) linha[i++]);
				a.setDescricao((String) linha[i++]);
				a.setOrdem((Short) linha[i++]);
				areas.add(a);
			}
		}	
		
		return areas;
	}
}
