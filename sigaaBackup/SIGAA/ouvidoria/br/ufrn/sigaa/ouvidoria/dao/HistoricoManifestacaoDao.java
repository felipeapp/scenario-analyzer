package br.ufrn.sigaa.ouvidoria.dao;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dao.ResponsavelUnidadeDAO;
import br.ufrn.comum.dominio.Responsavel;
import br.ufrn.rh.dominio.NivelResponsabilidade;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ouvidoria.dominio.HistoricoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.StatusManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.TipoHistoricoManifestacao;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Dao responsável por consultas em {@link HistoricoManifestacao}.
 * 
 * @author Bernardo
 *
 */
public class HistoricoManifestacaoDao extends GenericSigaaDAO {

	/**
	 * Retorna o conjunto de históricos cadastrados para a manifestação passada.
	 * 
	 * @param idManfiestacao
	 * @return
	 * @throws DAOException
	 */
    public Collection<HistoricoManifestacao> getAllHistoricosByManifestacao(int idManfiestacao) throws DAOException {
    	Collection<HistoricoManifestacao> list = findByExactField(HistoricoManifestacao.class, "manifestacao.id", idManfiestacao, "asc", "dataCadastro");
    	
    	ResponsavelUnidadeDAO responsavelDao = DAOFactory.getInstance().getDAO(ResponsavelUnidadeDAO.class);
		
    	try {
			for (HistoricoManifestacao historico : list) {
				if(isNotEmpty(historico.getUnidadeResponsavel())) {
					Responsavel resp = responsavelDao.findResponsavelAtualByUnidade(historico.getUnidadeResponsavel().getId(), NivelResponsabilidade.CHEFE);
					historico.setPessoaResponsavel(new Pessoa());
					if(resp != null)
						historico.getPessoaResponsavel().setNome(resp.getNomeServidor());
				}
			}
    	} finally {
    		responsavelDao.close();
    	}
		
		return list;
    }
    
    /**
     * Retorna uma coleção contendo apenas os históricos visíveis pelo manifestante.
     * 
     * @param idManfiestacao
     * @return
     * @throws DAOException
     */
    public Collection<HistoricoManifestacao> getAllHistoricosVisiveisInteressadoByManifestacao(int idManfiestacao) throws DAOException {
		String hql = "SELECT h FROM HistoricoManifestacao h " +
				"WHERE h.tipoHistoricoManifestacao.id in " + UFRNUtils.gerarStringIn(TipoHistoricoManifestacao.getAllTiposVisiveisInteressado()) +
				" AND manifestacao.id = :manifestacao";
		
		Query q = getSession().createQuery(hql);
		
		q.setInteger("manifestacao", idManfiestacao);
		
		@SuppressWarnings("unchecked")
		List<HistoricoManifestacao> list = q.list();
		
		return list;
    }
    
    /**
     * Retorna o histórico da última solicitação de esclarecimento.
     * 
     * @param idManfiestacao
     * @return
     * @throws DAOException
     */
    public HistoricoManifestacao getUltimaSolicitacaoEsclarecimento(int idManfiestacao) throws DAOException {
		String hql = "SELECT h FROM HistoricoManifestacao h " +
				" WHERE h.tipoHistoricoManifestacao.id = " + TipoHistoricoManifestacao.ESCLARECIMENTO_OUVIDORIA_INTERESSADO+
				" AND manifestacao.id = :manifestacao "+
				" ORDER BY h.dataCadastro desc";
		
		Query q = getSession().createQuery(hql);
		
		q.setInteger("manifestacao", idManfiestacao);
		q.setMaxResults(1);

		HistoricoManifestacao historico = (HistoricoManifestacao) q.uniqueResult();
		
		return historico;
    }
    
    /**
     * Retorna o conjunto de unidades que possuem manifestações encaminhadas independente de status.
     * 
     * @return
     * @throws DAOException
     */
    public Collection<Unidade> getAllUnidadesComManifestacoes() throws DAOException {
    	String hql = "SELECT DISTINCT u FROM HistoricoManifestacao h " +
    			"JOIN h.unidadeResponsavel u ";
		
		Query q = getSession().createQuery(hql);
		
		@SuppressWarnings("unchecked")
		List<Unidade> list = q.list();
		
		return list;
    }/**
	 * Retorna a lista de históricos de manifestações atrasadas de uma unidade.
	 * 
	 * @param ano
	 * @param numero
	 * @param dataInicial
	 * @param dataFinal
	 * @param categoriaAssuntoManifestacao
	 * @param unidade
	 * @param paging
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<HistoricoManifestacao> findHistoricosManifetacoesAtrasadasByUnidade(int unidade) throws HibernateException, DAOException {
		String projecao = "h.id, h.prazoResposta, h.manifestacao.id, h.manifestacao.numero, h.manifestacao.dataCadastro, h.manifestacao.titulo, h.tipoHistoricoManifestacao.id ";

		String hql = "SELECT distinct " + projecao;
						
		String consulta = " FROM HistoricoManifestacao h " +
							"JOIN h.unidadeResponsavel u " +
							"JOIN h.manifestacao m " +
							"JOIN h.tipoHistoricoManifestacao tipoHistoricoManifestacao " +
							"JOIN m.interessadoManifestacao interessadoManifestacao " +
							"JOIN m.statusManifestacao statusManifestacao " +
						" WHERE statusManifestacao.id in " + UFRNUtils.gerarStringIn(StatusManifestacao.getAllStatusSemResposta()) +
						"AND u.id = :unidade ";

		hql += consulta + "ORDER BY m.dataCadastro desc";

		Query q = getSession().createQuery(hql);

		q.setInteger("unidade", unidade);

		@SuppressWarnings("unchecked")
		List<Object[]> historicos = q.list();

		return HibernateUtils.parseTo(historicos, projecao, HistoricoManifestacao.class, "h");
	}
}
