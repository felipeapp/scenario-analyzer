/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '23/11/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.projetos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.EditalExtensao;
import br.ufrn.sigaa.projetos.dominio.AutorizacaoDepartamento;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/*******************************************************************************
 * 
 * Dao utilizado durante o processo de autorização da ação pelos 
 * departamentos envolvidos
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
public class AutorizacaoDepartamentoDao extends GenericSigaaDAO {

	/**
	 * Retorna todas as autorizações de atividades da unidade informada
	 * 
	 * @return Colecao de AutorizacaoDepartamento da unidade informada
	 * @throws DAOException
	 * 
	 * @author ilueny santos
	 */
	public Collection<AutorizacaoDepartamento> findByUnidades(Collection<UnidadeGeral> unidades)
			throws DAOException {

		try {
			ArrayList<AutorizacaoDepartamento> result = new ArrayList<AutorizacaoDepartamento>();
			
			if ((unidades != null) && (unidades.size() > 0)) {
			
				String hql = null;
				hql = "SELECT DISTINCT au.id, au.dataAutorizacao, au.autorizado, a.id, a.projeto.ano, a.projeto.titulo, edital  "
						+ " FROM AutorizacaoDepartamento au "
						+ " INNER JOIN au.atividade a " 
						+ " LEFT JOIN a.editalExtensao edital " 
						+ " WHERE au.unidade.id IN (:idsUnidades) AND au.ativo = trueValue() " +
								"AND a.projeto.situacaoProjeto.id not in (:EXTENSAO_GRUPO_INVALIDO, :PROJETO_BASE_GRUPO_INVALIDO) "
						+ " order by a.projeto.ano DESC ";
				Query query = getSession().createQuery(hql);
				
				ArrayList<Integer> idsUnidades = new ArrayList<Integer>();
				for (UnidadeGeral u: unidades) 
					idsUnidades.add(u.getId());
				
				query.setParameterList("idsUnidades", idsUnidades);
				query.setParameterList("EXTENSAO_GRUPO_INVALIDO", TipoSituacaoProjeto.EXTENSAO_GRUPO_INVALIDO);
				query.setParameterList("PROJETO_BASE_GRUPO_INVALIDO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_INVALIDO);
	
				List lista = query.list();
	
				for (int a = 0; a < lista.size(); a++) {
					int col = 0;
					Object[] colunas = (Object[]) lista.get(a);
	
					AutorizacaoDepartamento au = new AutorizacaoDepartamento();
					au.setId((Integer) colunas[col++]);
					au.setDataAutorizacao((Date) colunas[col++]);
					au.setAutorizado((Boolean) colunas[col++]);
	
					AtividadeExtensao at = new AtividadeExtensao();
					at.setId((Integer) colunas[col++]);
					at.setAno((Integer) colunas[col++]);
					at.setTitulo((String) colunas[col++]);
					at.setEditalExtensao((EditalExtensao) colunas[col++]);
					au.setAtividade(at);
					result.add(au);
				}
			}
			
			return result;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}

	/**
	 * Retorna total de autorizações dos departamentos pendentes para a
	 * atividade informado
	 * 
	 * @param idAtividade
	 * @throws DAOException
	 * 
	 * @author ilueny
	 */
	public int totalAutorizacoesPendentes(int idAtividade) throws DAOException {

		String hql = " select count(ad.id) from AutorizacaoDepartamento ad inner join ad.atividade a "
				+ " where (a.id = :idAtividade) and (ad.dataAutorizacao is null) and (ad.ativo=trueValue())";

		Query query = getSession().createQuery(hql);
		query.setInteger("idAtividade", idAtividade);

		return Integer.parseInt(query.uniqueResult().toString());

	}

	/**
	 * Retorna total de autorizações reprovadas pelos departamentos para a
	 * atividade informada
	 * 
	 * @param idAtividade
	 * @throws DAOException
	 * 
	 * @author ilueny
	 */
	public int totalReprovacoesPelosDepatamentos(int idAtividade)
			throws DAOException {

		String hql = " select count(ad.id) from AutorizacaoDepartamento ad inner join ad.atividade a "
				+ " where (a.id = :idAtividade) and (ad.dataAutorizacao is not null) and (autorizado = falseValue()) and (ad.ativo = trueValue())";

		Query query = getSession().createQuery(hql);
		query.setInteger("idAtividade", idAtividade);

		return Integer.parseInt(query.uniqueResult().toString());

	}

	/**
	 * Todas as autorizações ativas da atividade informada
	 * 
	 * @author ilueny
	 */
	@SuppressWarnings("unchecked")
	public Collection<AutorizacaoDepartamento> findByAtividade(int idAtividade)
			throws DAOException {

		String hql = "select ad from AutorizacaoDepartamento ad inner join ad.atividade a "
				+ " where (a.id = :idAtividade) and (ad.ativo = trueValue())";

		Query query = getSession().createQuery(hql);
		query.setInteger("idAtividade", idAtividade);

		return query.list();

	}

	/**
	 * Retorna o total de docentes na ação de extensão informada com a unidade
	 * 
	 * @param idAtividade
	 * @param idUnidade
	 * @return
	 * @throws DAOException
	 */
	public int totalComponentesNoProjetoComEssaUnidade(int idAtividade,
			int idUnidade) throws DAOException {

		String hql = " select count(*) from MembroProjeto mp inner join mp.projeto p inner join mp.servidor s"
				+ " where (p.id = :idAtividade) and (s.unidade.id = :idUnidade)";

		Query query = getSession().createQuery(hql);
		query.setInteger("idAtividade", idAtividade);
		query.setInteger("idUnidade", idUnidade);

		return Integer.parseInt(query.uniqueResult().toString());
	}

}