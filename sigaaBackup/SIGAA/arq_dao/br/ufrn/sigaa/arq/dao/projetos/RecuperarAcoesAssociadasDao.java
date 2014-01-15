/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 05/07/2010
 *
 */
package br.ufrn.sigaa.arq.dao.projetos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;
/**
 * 
 * Dao para recuperação de ações associadas excluídas. 
 * 
 * @author Amanda Priscilla
 *
 */
public class RecuperarAcoesAssociadasDao  extends GenericSigaaDAO {
	
	/**
	 * Recupera ações de acordo com título, ano e idCoordenador passados.
	 * @param titulo
	 * @param ano
	 * @param idCoordenador
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<Projeto> findByAcoes(Integer ano, Integer idServidor, String titulo ) throws HibernateException, DAOException{
			
			StringBuilder hql = new StringBuilder();
			hql.append(" select distinct pro.id, pro.ano, pro.titulo, coord.id, p.nome, pro.numeroInstitucional, pro.unidade.id, pro.unidade.sigla ");
			hql.append(" from Projeto pro ");
			hql.append(" inner join pro.situacaoProjeto st ");
			hql.append(" left join pro.coordenador coord");
			hql.append(" left join coord.pessoa p");
			hql.append(" inner join pro.equipe eq ");
			hql.append(" inner join pro.tipoProjeto ");
			hql.append(" inner join pro.unidade ");
			hql.append(" where pro.ativo = falseValue() and pro.tipoProjeto.id = :idTipo ");
			hql.append(" and st.id = :idSituacao ");
			
			if(ano != null){
				hql.append(" and pro.ano = "+ano+" ");
			}
			if(idServidor != null){
				hql.append(" and eq.servidor.id = "+idServidor+" ");
			}
			if(!ValidatorUtil.isEmpty(titulo)){
				hql.append(" and pro.titulo like '%"+titulo+"%' ");
			}
			hql.append(" order by pro.ano desc, pro.titulo asc ");
			
			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idSituacao", TipoSituacaoProjeto.PROJETO_BASE_REMOVIDO);
			query.setInteger("idTipo", TipoProjeto.ASSOCIADO);
			
			List result = query.list();
			
			ArrayList<Projeto> acoes = new ArrayList<Projeto>();
			for (int i=0; i< result.size(); i++){
				
				int col=0;
				Object[] colunas = (Object[]) result.get(i);
				
							
				Projeto pro = new Projeto();
				pro.setId((Integer) colunas[col++]);
				pro.setAno(Integer.parseInt(colunas[col++].toString()));
				pro.setTitulo((String) colunas[col++]);
				
				MembroProjeto coord = new MembroProjeto();
				if(colunas[col] != null) coord.setId((Integer) colunas[col++]);
				else col++;
				
				Pessoa p = new Pessoa();
				if(colunas[col] != null) p.setNome((String) colunas[col++]);
				else col++;
				
				coord.setPessoa(p);
				pro.setCoordenador(coord);
				
				if(colunas[col] != null) pro.setNumeroInstitucional((Integer) colunas[col++]);
				
				if(colunas[col] != null){
					Unidade u = new Unidade((Integer) colunas[col++]);
					u.setSigla((String) colunas[col++]);
					pro.setUnidade(u);
				}
				acoes.add(pro);
				
				
			}
			
			return acoes;
		
	}
	
}
