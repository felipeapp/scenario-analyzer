/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '23/09/2011'
 *
 */
package br.ufrn.sigaa.arq.dao.projetos;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.projetos.dominio.DistribuicaoAvaliacao;
import br.ufrn.sigaa.projetos.dominio.ModeloAvaliacao;
import br.ufrn.sigaa.projetos.dominio.TipoAvaliacao;
import br.ufrn.sigaa.projetos.dominio.TipoAvaliador;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoAvaliacao;

/**
 * Dao para acesso aos dados de Distribuições de projetos
 * @author ilueny
 *
 */
public class DistribuicaoProjetoDao  extends GenericSigaaDAO {


	/**
	 * Retorna a lista de Distribuições Ativas para o tipo de edital informado.
	 * 
	 * @param avaliador
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<DistribuicaoAvaliacao> findByDistribuicoesAtivas(Character tipo) throws DAOException {
		try {
			String projecao = "d.id, d.ativo, " +
			"d.metodo, d.tipoAvaliador, d.modeloAvaliacao.id, d.modeloAvaliacao.descricao, " +
			"d.modeloAvaliacao.tipoAvaliacao ";
			String hql = "select " + projecao + ", " +
			"(select count(*) from br.ufrn.sigaa.projetos.dominio.Avaliacao a1 where a1.ativo = trueValue() and a1.distribuicao.id = d.id) as totalAvaliacoesDistribuidas, " +
			"(select count(*) from br.ufrn.sigaa.projetos.dominio.Avaliacao a2 where a2.ativo = trueValue() and a2.distribuicao.id = d.id and a2.situacao.id = :idAvaliacaoRealizada) as totalAvaliacoesRealizadas " +
			" from DistribuicaoAvaliacao d " +
			" where d.ativo = trueValue() and d.modeloAvaliacao.tipo = :tipo" +
			" order by d.id desc";
			Query query = getSession().createQuery(hql);
			query.setInteger("idAvaliacaoRealizada", TipoSituacaoAvaliacao.REALIZADA);
			query.setCharacter("tipo", tipo);
			List<Object[]> lista = query.list();

			ArrayList<DistribuicaoAvaliacao> result = new ArrayList<DistribuicaoAvaliacao>();
			for (int a = 0; a < lista.size(); a++) {

				int col = 0;
				Object[] colunas = lista.get(a);
				DistribuicaoAvaliacao distribuicao = new DistribuicaoAvaliacao();
				distribuicao.setId((Integer) colunas[col++]);
				distribuicao.setAtivo((Boolean) colunas[col++]);
				distribuicao.setMetodo((Character) colunas[col++]);
				distribuicao.setTipoAvaliador((TipoAvaliador) colunas[col++]);

				ModeloAvaliacao ma = new ModeloAvaliacao();
				ma.setId((Integer) colunas[col++]);
				ma.setDescricao((String) colunas[col++]);
				ma.setTipoAvaliacao((TipoAvaliacao) colunas[col++]);
				distribuicao.setModeloAvaliacao(ma);
				distribuicao.setTotalAvaliacoesDistribuidas(Long.valueOf((Long)colunas[col++]).intValue());
				distribuicao.setTotalAvaliacoesRealizadas(Long.valueOf((Long)colunas[col++]).intValue());

				result.add(distribuicao);
			}

			return result;

		} catch (Exception e) {
			throw new DAOException(e);
		}
	}  


}
