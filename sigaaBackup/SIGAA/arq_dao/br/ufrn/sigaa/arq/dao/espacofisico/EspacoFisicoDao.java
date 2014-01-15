/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '10/12/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.espacofisico;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.espacofisico.dominio.EspacoFisico;
import br.ufrn.sigaa.espacofisico.dominio.TipoEspacoFisico;

/**
 * Dao responsável por gerenciar os dados de espaço físico
 * 
 * @author Henrique André
 *
 */
public class EspacoFisicoDao extends GenericSigaaDAO {

	/**
	 * Construtor padrão
	 */
	public EspacoFisicoDao() {
	}
	 
	/**
	 * Busca um espaço físico com base nas restrições e parâmetros
	 * 
	 * @param restricoes
	 * @param parametros
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<EspacoFisico> buscaRefinada(RestricoesBusca restricoes, ParametrosBusca parametros) throws HibernateException, DAOException {

		StringBuilder hql = new StringBuilder();
		
		hql.append("select e.id, e.codigo, e.descricao, e.capacidade, e.area, e.unidadeResponsavel.id from EspacoFisico e left join e.recursos recurso where e.ativo = trueValue() ");
		
		if (restricoes.isBuscaCodigo())
			hql.append(" and e.codigo like :codigo");
		
		if (restricoes.isBuscaCapacidade()) {
			if ( !isEmpty(parametros.getCapacidadeInicio()) )
				hql.append(" and e.capacidade >= :capacidadeInicio");
			if ( !isEmpty(parametros.getCapacidadeFim()) )
				hql.append(" and e.capacidade <= :capacidadeFim");
		}
		
		if (restricoes.isBuscaDescricao())
			hql.append(" and e.descricao like :descricao");
		
		if (restricoes.isBuscaArea()) {
			if ( !isEmpty(parametros.getAreaInicio()) )
				hql.append(" and e.area >= :areaInicio");
			if ( !isEmpty(parametros.getAreaFim()) )
				hql.append(" and e.area <= :areaFim");
		}
		
		if (restricoes.isBuscaTipoRecurso()) {
			hql.append(" and recurso.tipo.id = :tipoRecurso");
		}
		
		if (restricoes.isBuscaLocalizacao())
			hql.append(" and e.unidadeResponsavel.id = :localizacao");

		if (restricoes.isBuscaReservaPrioritaria())
			hql.append(" and e.unidadePreferenciaReserva.id = :reservaPrioritaria");
		
		
		hql.append(" order by e.unidadeResponsavel.id ");
		
		Query q = getSession().createQuery(hql.toString());
		
		if (restricoes.isBuscaCodigo())
			q.setString("codigo", "%" + parametros.getCodigo() + "%");
		
		if (restricoes.isBuscaCapacidade()) {
			if ( !isEmpty(parametros.getCapacidadeInicio()) )
				q.setInteger("capacidadeInicio", parametros.getCapacidadeInicio());
			if ( !isEmpty(parametros.getCapacidadeFim()) )
				q.setInteger("capacidadeFim", parametros.getCapacidadeFim());
		}
		
		if (restricoes.isBuscaLocalizacao())
			q.setString("localizacao", "%" + parametros.getLocalizacao() + "%");
		
		if (restricoes.isBuscaDescricao())
			q.setString("descricao", "%" + parametros.getDescricao() + "%");
		
		if (restricoes.isBuscaArea()) {
			if ( !isEmpty(parametros.getAreaInicio()) )
				q.setDouble("areaInicio", parametros.getAreaInicio());
			if ( !isEmpty(parametros.getAreaFim()) )
				q.setDouble("areaFim", parametros.getAreaFim());
		}
		
		if (restricoes.isBuscaTipoRecurso())
			q.setInteger("tipoRecurso", parametros.getTipoRecurso().getId());
		
		if (restricoes.isBuscaLocalizacao())
			q.setInteger("localizacao", parametros.getLocalizacao().getId());

		if (restricoes.isBuscaReservaPrioritaria())
			q.setInteger("reservaPrioritaria", parametros.getReservaPrioritaria().getId());
						
		
		List lista = q.list();
		List<EspacoFisico> resultadoProjecao = null;

		for (int i = 0; i < lista.size(); i++) {
			
			if (resultadoProjecao == null)
				resultadoProjecao = new ArrayList<EspacoFisico>();
			
			Object[] colunas = (Object[]) lista.get(i);
			int col = 0;

			EspacoFisico espaco = new EspacoFisico();
			espaco.setId((Integer) colunas[col++]);
			espaco.setCodigo((String) colunas[col++]);
			espaco.setDescricao((String) colunas[col++]);
			espaco.setCapacidade((Integer) colunas[col++]);
			espaco.setArea((Double) colunas[col++]);
			espaco.setUnidadeResponsavel(new Unidade());
			espaco.setUnidadeResponsavel(new Unidade((Integer) colunas[col++]));
			
			if (!resultadoProjecao.contains(espaco))
				resultadoProjecao.add(espaco);
		}
		
		return resultadoProjecao;
	}

	/**
	 * Verifica se o código informado já esta sendo usado
	 * 
	 * @param codigo
	 * @return
	 * @throws DAOException
	 */
	public boolean isCodigoDisponivel(String codigo) throws DAOException {
		String hql = "select e.id from EspacoFisico e where e.codigo like :codigo";
		
		Query q = getSession().createQuery(hql);
		q.setString("codigo", codigo);
		
		return q.list().isEmpty();
	}

	/**
	 * Localiza todos os espaços físicos que são filhas da unidade
	 * 
	 * @param idUnidade
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<EspacoFisico> findAllByUnidade(int... ids) throws DAOException {
		String hql = "select e from EspacoFisico e where e.ativo = trueValue() and e.unidadeResponsavel.id in " + gerarStringIn(ids);
		
		Query q = getSession().createQuery(hql);
		return q.list();
	}
	
	/**
	 * Localiza todos os espaços físicos que tem o tipo de espaço físico passado como parâmetro.
	 * 
	 * @param obj
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EspacoFisico> findbyEquipe(TipoEspacoFisico obj) throws DAOException{
		 Query query = getSession().createQuery("select ef from EspacoFisico ef where ef.tipo.id=:espacoFisico");
		 query.setInteger("espacoFisico", obj.getId());
		 return query.list(); 
	} 

}