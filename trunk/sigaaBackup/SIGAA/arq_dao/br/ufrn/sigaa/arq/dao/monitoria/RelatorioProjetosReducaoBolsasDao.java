package br.ufrn.sigaa.arq.dao.monitoria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.TipoProjetoEnsino;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

public class RelatorioProjetosReducaoBolsasDao extends GenericSigaaDAO {
	
	/**
	 * Retorna os projetos de monitoria abertos para edição, ou seja,
	 * com situação CADASTRO_EM_ANDAMENTO, gravados pelo usuário atual.
	 * 
	 * @param situacao
	 * @return
	 * @throws DAOException
	 */
	public Collection<ProjetoEnsino> findProjetosReducaoBolsas(Integer ano, String titulo)
			throws DAOException {

		try {
			StringBuilder hql = new StringBuilder();
			hql
					.append("SELECT distinct pe.id, pj.ano, pj.titulo, sit.id, sit.descricao, tip.id, tip.descricao "
							+ "from ProjetoEnsino pe "
							+ "INNER JOIN pe.projeto pj "
							+ "INNER JOIN pj.situacaoProjeto sit "
							+ "INNER JOIN pj.historicoSituacao his "
							+ "INNER JOIN pe.tipoProjetoEnsino tip  "
							+ "WHERE his.situacaoProjeto.id = :idSituacao and pj.ativo = trueValue() ");
									
			if(ano != null){
				hql.append(" AND ano = "+ano+" ");
			}
			if(!titulo.isEmpty()){
				hql.append(" AND titulo like '%"+titulo+"%' ");
			}
			
			Query query = getSession().createQuery(hql.toString());
			
			query.setInteger("idSituacao",TipoSituacaoProjeto.MON_RENOVADO_COM_REDUCAO_BOLSAS);			

			@SuppressWarnings("unchecked")
			List<Object> lista = query.list();

			ArrayList<ProjetoEnsino> result = new ArrayList<ProjetoEnsino>();
			for (int a = 0; a < lista.size(); a++) {

				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);

				ProjetoEnsino pe = new ProjetoEnsino();
				pe.setId((Integer) colunas[col++]);
				pe.setAno((Integer) colunas[col++]);
				pe.setTitulo((String) colunas[col++]);

				TipoSituacaoProjeto sit = new TipoSituacaoProjeto(
						(Integer) colunas[col++]);
				String desc = (String) colunas[col++];
				if (desc != null)
					sit.setDescricao(desc);

				pe.setSituacaoProjeto(sit);

				TipoProjetoEnsino tip = new TipoProjetoEnsino(
						(Integer) colunas[col++]);
				String des = (String) colunas[col++];
				if (des != null)
					tip.setDescricao(des);

				pe.setTipoProjetoEnsino(tip);

				result.add(pe);

			}

			return result;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}

}
