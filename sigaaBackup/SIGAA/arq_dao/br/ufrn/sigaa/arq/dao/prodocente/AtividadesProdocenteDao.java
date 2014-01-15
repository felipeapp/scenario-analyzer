/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '02/03/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.prodocente;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.TeseOrientada;
import br.ufrn.sigaa.prodocente.atividades.dominio.TipoAtividadeEnsino;
import br.ufrn.sigaa.prodocente.atividades.dominio.TipoOrientacao;

/**
 * Dao utilizado para busca de atividades do menu do prodocente
 *
 * @author Gleydson, Mario
 *
 */
public class AtividadesProdocenteDao extends GenericSigaaDAO {

	public Collection findByServidorDep(Class atividade, int serv, int unidade)
			throws DAOException {

		Criteria c = getSession().createCriteria(atividade);
		if (serv != -1) {
			c.add(Expression.eq("servidor", new Servidor(serv)));
		}
		if (unidade != -1) {
			c.add(Expression.eq("departamento", new Unidade(unidade)));
		}

		//Caso tenha o campo "Ativo", só mostra os que não foram desativados. 	
		try
		{
		    if(ReflectionUtils.propertyExists(atividade, "ativo"))
		    	c.add(Expression.or(Expression.eq("ativo", true), Expression.isNull("ativo"))); //somente as produções que não foram "deletadas" (ativo!=false)
		}
		catch (Exception e) {  }
		//--//

		return c.list();

	}
	
	/**
	 * Procura por Servidor de um determinado Departamento com um determinado tipo de Orientação
	 * OBS:Tipo de Orientação (Mestrado, Doutorado, Etc)
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public Collection findByServidorDepartamentoOrientacao(Class atividade, int idServidor, int idUnidade, int idTipoOrientacao, List<Integer> filtroTipoOrientacao) throws DAOException
	{
		
		Criteria c = getSession().createCriteria(atividade);
		if (idServidor > 0)
			c.add(Expression.eq("servidor.id", idServidor));
		if (idUnidade > 0) 
			c.add(Expression.eq("departamento.id", idUnidade));
		if (idTipoOrientacao != -1)
			c.add(Expression.eq("tipoOrientacao.id", idTipoOrientacao));
		else if(filtroTipoOrientacao != null && !filtroTipoOrientacao.isEmpty())
			//se não for escolhido um tipo de orientação, so poderão ser exibidas as Atividades cujos tipos foram Filtrados no MBean 
			//(exemplo: TeseMBean filtra por Stricto Sensu, Lato Sensu, e ResidenciaMedica)
			c.add(Expression.in("tipoOrientacao.id", filtroTipoOrientacao));
		
		
		//Caso tenha o campo "Ativo", só mostra os que não foram desativados. 	
		ReflectionUtils.evalProperty(atividade, "ativo");
		// somente as produções que não foram "deletadas" (ativo!=false)
		c.add(Expression.or(Expression.eq("ativo", true), Expression.isNull("ativo")));
		c.addOrder(Order.desc("periodoInicio"));
		
		@SuppressWarnings("unchecked")
		List lista = c.list();
		return lista;
	}

	public Collection findByServidorDep(Class atividade, int serv, int unidade,boolean order)
	throws DAOException {

		Criteria c = getSession().createCriteria(atividade);
		if (serv != -1) {
			c.add(Expression.eq("servidor", new Servidor(serv)));
		}
		if (unidade != -1) {
			c.add(Expression.eq("departamento", new Unidade(unidade)));
		}
		if(order)
			c.addOrder(Order.asc("periodoInicio"));
		
		
		//Caso tenha o campo "Ativo", só mostra os que não foram desativados. 	
		try
		{
			ReflectionUtils.evalProperty(atividade, "ativo");
			// somente as produções que não foram "deletadas" (ativo!=false)
			c.add(Expression.or(Expression.eq("ativo", true), Expression.isNull("ativo")));
		}
		catch (Exception e) {  }
		//--//


		return c.list();

		}



	public Collection findByServidorTipoAtividadeEnsino(Class atividade, int serv, TipoAtividadeEnsino atividadeE)
	throws DAOException {

		Criteria c = getSession().createCriteria(atividade);
		if (serv != -1) {
		c.add(Expression.eq("servidor", new Servidor(serv)));
		}

		c.add(Expression.eq("tipoAtividadeEnsino", atividadeE));

		return c.list();

		}

	public Collection findByServidorTipoAtividadeEnsino(Class atividade, int serv)
	throws DAOException {

		Criteria c = getSession().createCriteria(atividade);
		if (serv != -1) {
		c.add(Expression.eq("servidor", new Servidor(serv)));
		}

		c.add(Expression.eq("tipoAtividadeEnsino", TipoAtividadeEnsino.DOUTORADO));
		c.add(Expression.eq("tipoAtividadeEnsino", TipoAtividadeEnsino.MESTRADO));

		return c.list();

		}
	public Collection findByServidorTipoOrientacao(Class atividade, int serv, TipoOrientacao orientacao)
	throws DAOException {

		Criteria c = getSession().createCriteria(atividade);
		if (serv != -1) {
		c.add(Expression.eq("servidor", new Servidor(serv)));
		}

		c.add(Expression.eq("tipoOrientacao",orientacao));

		return c.list();

		}
	
	/**
	 * Em Tese, existem discentes que foram Migrados do antigo Prodocente, apesar de não serem "discentes externos" eles só tem a string do nome cadastrada, porém o campo booleano discente_externo é null.
	 * Este método verifica se o campo discente_externo é null, ou seja se ele foi migrado do antigo Prodocente.
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 * @param tese
	 * @throws DAOException
	 */
	public boolean isDiscenteMigrado(TeseOrientada tese) throws DAOException
	{
		try
		{
			Criteria c = getSession().createCriteria(TeseOrientada.class);
			c.add(Expression.eq("id", tese.getId()));
			TeseOrientada tese_retornada = (TeseOrientada) c.uniqueResult();
			if (tese_retornada.getDiscenteExterno() == null)
				return true;
			return false;
		}
		catch(Exception e) { return false; }
			
	}
}
