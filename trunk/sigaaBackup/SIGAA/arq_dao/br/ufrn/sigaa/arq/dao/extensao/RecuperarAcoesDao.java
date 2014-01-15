/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 05/07/2010
 *
 */
package br.ufrn.sigaa.arq.dao.extensao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;
/**
 * 
 * Dao para recuperação de ações de extensão. 
 * 
 * @author Geyson Karlos
 *
 */
public class RecuperarAcoesDao  extends GenericSigaaDAO {
	
	/**
	 * Recupera ações de acordo com título, ano e idCoordenador passados.
	 * @param titulo
	 * @param ano
	 * @param idCoordenador
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<AtividadeExtensao> findByAcoes(Integer ano, Integer idServidor, String titulo ) throws HibernateException, DAOException{
			
			StringBuilder hql = new StringBuilder();
			hql.append(" select distinct at.id, pro.id, pro.ano, pro.titulo, coord.id, p.nome, at.tipoAtividadeExtensao ");
			hql.append(" from AtividadeExtensao at ");
			hql.append(" inner join at.situacaoProjeto st ");
			hql.append(" inner join at.projeto pro ");
			hql.append(" left join pro.coordenador coord");
			hql.append(" left join coord.pessoa p");
			hql.append(" inner join pro.equipe eq ");
			hql.append(" where at.ativo = falseValue() ");
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
			query.setInteger("idSituacao", TipoSituacaoProjeto.EXTENSAO_REMOVIDO);
			
			List result = query.list();
			
			ArrayList<AtividadeExtensao> acoes = new ArrayList<AtividadeExtensao>();
			for (int i=0; i< result.size(); i++){
				
				int col=0;
				Object[] colunas = (Object[]) result.get(i);
				
				AtividadeExtensao atv = new AtividadeExtensao();
				atv.setId((Integer) colunas[col++]);
				
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
				atv.setProjeto(pro);
				
				TipoAtividadeExtensao tipo = (TipoAtividadeExtensao) colunas[col++];
				atv.setTipoAtividadeExtensao(tipo);
				
				acoes.add(atv);
			}
			
			return acoes;
		
	}
	
}
