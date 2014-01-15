/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '16/01/2009'
 *
 */
package br.ufrn.sigaa.arq.dao.extensao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.extensao.dominio.DiscenteExtensao;
import br.ufrn.sigaa.extensao.dominio.PlanoTrabalhoExtensao;
import br.ufrn.sigaa.extensao.jsf.helper.DesignacaoFuncaoProjetoHelper;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * DAO para consultas de planos de trabalho de extensão
 * 
 * @author 
 *
 */
public class PlanoTrabalhoExtensaoDao extends GenericSigaaDAO {

	public PlanoTrabalhoExtensaoDao() {
	}

	/**
	 * Planos de trabalho de ações ativas onde o servidor informado é
	 * coordenador ativo
	 * 
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<PlanoTrabalhoExtensao> findByServidorCoordenadorAtivo(
			Servidor servidor, Integer[] idSituacaoAcao) throws DAOException {

		 	Collection<Integer> atividadesCoordenador = DesignacaoFuncaoProjetoHelper.atividadesByCoordenadoresOrDesignacaoCoordenador(servidor.getId());
		 	if (atividadesCoordenador.isEmpty()) atividadesCoordenador.add(0);
		 	
			StringBuilder hql = new StringBuilder();

			hql.append("select plano.id, plano.dataInicio, plano.dataFim, plano.dataEnvio, " +
						"projeto.ano, projeto.titulo, atividade.id,  " +
						"projeto.dataInicio, projeto.dataFim, projeto.ativo, " + 
						"disEx.id, pessoa.nome, dis.id, situacao.descricao, tipoVinculo.descricao " +
						"from PlanoTrabalhoExtensao plano ");
			hql.append("join plano.atividade atividade ");
			hql.append("left join plano.discenteExtensao disEx ");
			hql.append("left join disEx.discente dis ");
			hql.append("left join dis.pessoa pessoa ");
			hql.append("left join disEx.tipoVinculo tipoVinculo ");
			hql.append("left join disEx.situacaoDiscenteExtensao situacao ");
			hql.append("join atividade.projeto projeto ");
			hql.append("join projeto.coordenador coord ");
			hql.append("join coord.servidor servidor ");
			hql.append("where plano.ativo = trueValue() and " +
					"(( coord.servidor.id = :idServidor AND coord.ativo = trueValue() ) or atividade.id in ( :idAtividade ) )" + 
					"and (atividade.projeto.situacaoProjeto.id in (:idSituacao)) ");
			hql.append("order by atividade.projeto.ano desc, atividade.projeto.titulo");
			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idServidor", servidor.getId());
			query.setParameterList("idAtividade", atividadesCoordenador);
			query.setParameterList("idSituacao", idSituacaoAcao);
			
			ArrayList<PlanoTrabalhoExtensao> result = new ArrayList<PlanoTrabalhoExtensao>();
			List<Object[]> lista = query.list();
			for (Object[ ]obj : lista) {

				int col = 0;
				PlanoTrabalhoExtensao pl = new PlanoTrabalhoExtensao();
				pl.setId((Integer) obj[col++]);
				pl.setDataInicio((Date) obj[col++]);
				pl.setDataFim((Date) obj[col++]);
				pl.setDataEnvio((Date) obj[col++]);
				pl.getAtividade().getProjeto().setAno((Integer) obj[col++]);
				pl.getAtividade().getProjeto().setTitulo((String) obj[col++]);
				pl.getAtividade().setId((Integer) obj[col++]);
				pl.getAtividade().getProjeto().setDataInicio((Date) obj[col++]);
				pl.getAtividade().getProjeto().setDataFim((Date) obj[col++]);
				pl.getAtividade().getProjeto().setAtivo((Boolean) obj[col++]);
				
				Integer idDis = (Integer) obj[col++];
				if (idDis != null) {
					pl.setDiscenteExtensao(new DiscenteExtensao(idDis));
					pl.getDiscenteExtensao().getDiscente().getPessoa().setNome((String) obj[col++]);
					pl.getDiscenteExtensao().getDiscente().setId((Integer) obj[col++]);
					pl.getDiscenteExtensao().getSituacaoDiscenteExtensao().setDescricao((String) obj[col++]);
					pl.getDiscenteExtensao().getTipoVinculo().setDescricao((String) obj[col++]);
				}				
				result.add(pl);
			}

			return result;
			
	}
	
	/**
	 * Retorna o Plano de trabalho do discente de extensão informado
	 * 
	 * @param idDiscenteExtensao
	 * @return
	 * @throws DAOException
	 */
	public PlanoTrabalhoExtensao findByDiscente(int idDiscenteExtensao) throws DAOException {

		try {
			StringBuilder hql = new StringBuilder();

			hql.append("select plano from PlanoTrabalhoExtensao plano ");
			hql.append("inner join plano.discenteExtensao de ");
			hql.append("where de.id = :idDiscenteExtensao and de.ativo= trueValue() and plano.ativo = trueValue() ");

			Query query = getSession().createQuery(hql.toString());

			query.setInteger("idDiscenteExtensao", idDiscenteExtensao);
			query.setMaxResults(1);
			
			return (PlanoTrabalhoExtensao) query.uniqueResult();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna os planos de trabalho da atividade informada
	 * 
	 * @param idAtividade
	 * @return Collection
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<PlanoTrabalhoExtensao> findByAtividade(int idAtividade) throws DAOException {
		
		try {
			StringBuilder hql = new StringBuilder();

			hql.append("select plano from PlanoTrabalhoExtensao plano ");
			hql.append("inner join plano.atividade atividade ");
			hql.append("where plano.atividade.id = :idAtividade ");

			Query query = getSession().createQuery(hql.toString());

			query.setInteger("idAtividade", idAtividade);
			return query.list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
		
	}
	
}
