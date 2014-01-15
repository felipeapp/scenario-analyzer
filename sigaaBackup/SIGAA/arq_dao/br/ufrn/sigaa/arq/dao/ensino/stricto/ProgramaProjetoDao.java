/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 11/12/2008
 */
package br.ufrn.sigaa.arq.dao.ensino.stricto;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.stricto.dominio.ProgramaProjeto;
import br.ufrn.sigaa.pesquisa.dominio.CodigoProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/** 
 * Dao para realizar consultas sobre a entidade ProgramaProjeto 
 * @author Victor Hugo
 */
public class ProgramaProjetoDao extends GenericSigaaDAO {

	/**
	 * Retorna todos os projetos de pesquisa do programa ativos ou não
	 * ordenados pelo ano
	 * @param idPrograma
	 * @param ativos
	 * @return
	 * @throws DAOException
	 */
	public List<ProgramaProjeto> findByProgramaStatus(int idPrograma, boolean ativos) throws DAOException{
		
		StringBuffer hql = new StringBuffer();
		hql.append( " SELECT pp.projeto.codigo.ano, pp.projeto.projeto.titulo, pp.id, pp.projeto.id " );
		hql.append( " FROM ProgramaProjeto pp  WHERE pp.programa.id = :idPrograma " );
		hql.append( " AND pp.projeto.projeto.situacaoProjeto.id NOT IN (" + TipoSituacaoProjeto.EXCLUIDO + "," + TipoSituacaoProjeto.PROJETO_BASE_REMOVIDO + ")");
		
		if( ativos )
			hql.append( " AND pp.ativo = trueValue() " );
		hql.append( " ORDER BY pp.projeto.codigo.ano DESC " );	
		Query q = getSession().createQuery( hql.toString() );
		q.setInteger( "idPrograma" , idPrograma);
		
		List<?> lista = q.list();
		ArrayList<ProgramaProjeto> result = new ArrayList<ProgramaProjeto>();

		for (int a = 0; a < lista.size(); a++) {
			int col = 0;

			Object[] colunas = (Object[]) lista.get(a);
			ProgramaProjeto pp = new ProgramaProjeto();
			ProjetoPesquisa pesq =  new ProjetoPesquisa();
			pesq.setCodigo(new CodigoProjetoPesquisa());
			pesq.getCodigo().setAno((Integer) colunas[col++]);
			pesq.setTitulo((String) colunas[col++]);
			pp.setProjeto(pesq);
			pp.setId((Integer) colunas[col++]);
			pp.getProjeto().setId((Integer) colunas[col++]);
			result.add(pp);
		}
		return result;
		
	}
	
}
