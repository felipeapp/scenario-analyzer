/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/11/2008
 *
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.ClasseMaterialConsultado;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.RegistroConsultasDiariasMateriais;

/**
 *    DAO responsável por realizar consultas nos registros das consultas de materiais.<br/>
 *    Até o momento existem dois tipos de registro de consultas, um registro apenas quantitativo 
 *    e outro descritivo (no qual é possível saber o material que foi consultado.)
 *    
 * 
 * @author agostinho
 * @author Fred_Castro
 *
 */
public class RegistroConsultasMateriaisDao extends GenericSigaaDAO {
	
	
	/**
	 * Retorna um registro de consulta por data, tipo de material, biblioteca e turno.
	 */
	public RegistroConsultasDiariasMateriais findRegistroByBibliotecaTipoMaterialColecaoETurno(int biblioteca, int tipoMaterial, int colecao, int turno, Date data)
			throws DAOException {
		
		Criteria c = getSession().createCriteria(RegistroConsultasDiariasMateriais.class);
		
		if (data != null)
			c.add(Restrictions.eq("dataConsulta", CalendarUtils.configuraTempoDaData(data, 0, 0, 0, 0)));
		
		if (tipoMaterial > 0)
			c.add(Restrictions.eq("tipoMaterial.id", tipoMaterial));
		
		if (biblioteca > 0)
			c.add(Restrictions.eq("biblioteca.id", biblioteca));
		
		if (turno > 0)
			c.add(Restrictions.eq("turno", turno));
		
		if (colecao > 0)
			c.add(Restrictions.eq("colecao.id", colecao));
		
		return (RegistroConsultasDiariasMateriais) c.uniqueResult();
	}
	
	
	/**
	 * Verifica se já existe um registro no banco, porque não pode salvar 2 iguais.
	 */
	public boolean existeRegistroConsultaSalvo(int biblioteca, int tipoMaterial, int colecao, int turno, Date data) throws DAOException {
		
		Criteria c = getSession().createCriteria(RegistroConsultasDiariasMateriais.class);
		c.setProjection(Projections.distinct(Projections.countDistinct("id")));
		if (data != null)
			c.add(Restrictions.eq("dataConsulta", CalendarUtils.configuraTempoDaData(data, 0, 0, 0, 0)));
		
		if (tipoMaterial > 0)
			c.add(Restrictions.eq("tipoMaterial.id", tipoMaterial));
		
		if (biblioteca > 0)
			c.add(Restrictions.eq("biblioteca.id", biblioteca));
		
		if (turno > 0)
			c.add(Restrictions.eq("turno", turno));
		
		if (colecao > 0)
			c.add(Restrictions.eq("colecao.id", colecao));
		
		int quantidade = (Integer) c.list().get(0);
		
		return quantidade > 0;
	}

	
	
	/**
	 * Retorna as classes ativas do registro passado. <br/>
	 * 
	 * Usado para saber a quantidade que já foram cadastradas para um determinada classe (CDU ou Black). 
	 * Logicamente, só é usado para os registros quantitativos. 
	 */
	public List<ClasseMaterialConsultado> findRegistrosClassesAtivos(int idRegistro) throws DAOException{
		
		Criteria c = getSession().createCriteria(ClasseMaterialConsultado.class);
		c.add(Restrictions.eq("registroConsultaMaterial.id", idRegistro));
		c.add(Restrictions.eq("ativo", true));
		
		c.addOrder(Order.asc("classificacao1"));
		c.addOrder(Order.asc("classificacao2"));
		c.addOrder(Order.asc("classificacao3"));
		
		@SuppressWarnings("unchecked")
		List<ClasseMaterialConsultado> list = c.list();
		return list;
	}
	
}
