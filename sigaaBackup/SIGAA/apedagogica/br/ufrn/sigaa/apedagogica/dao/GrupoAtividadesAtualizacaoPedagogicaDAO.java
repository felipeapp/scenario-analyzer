package br.ufrn.sigaa.apedagogica.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.apedagogica.dominio.AtividadeAtualizacaoPedagogica;
import br.ufrn.sigaa.apedagogica.dominio.GrupoAtividadesAtualizacaoPedagogica;
import br.ufrn.sigaa.apedagogica.dominio.ParticipanteAtividadeAtualizacaoPedagogica;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;


/**
 * Classe de acesso aos dados dos grupos de atividades de atualização pedagógica.
 * @author Mário Rizzi
 *
 */
public class GrupoAtividadesAtualizacaoPedagogicaDAO extends GenericSigaaDAO {
	
	/**
	 * Retorna o grupo de atividades filtrando pelo nome.
	 * @param nome
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<GrupoAtividadesAtualizacaoPedagogica> findByDenominacao(String nome) throws DAOException {
		
		Criteria c = getSession().createCriteria(GrupoAtividadesAtualizacaoPedagogica.class);
		
		if( !isEmpty(nome) )
			c.add(Expression.ilike("denominacao", StringUtils.toAscii(nome) + "%"));
		
		return c.list();
	}
	
	/**
	 * Verifica se existe participante para atividade setada no parâmetro.
	 * @param nome
	 * @return
	 * @throws DAOException
	 */
	public boolean checkParticipanteAtividade(AtividadeAtualizacaoPedagogica atividade) throws DAOException {
		
		Criteria c = getSession().createCriteria(ParticipanteAtividadeAtualizacaoPedagogica.class);
		c.add( Expression.eq("atividade.id", atividade.getId()) );
		
		return !c.list().isEmpty();
	}
	
	/**
	 * Retorna o grupo de atividades filtrando por um docente.
	 * @param nome
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<GrupoAtividadesAtualizacaoPedagogica> findByDocente(Integer idDocente) throws DAOException {

		String projecao = " g.id_grupo_atividades_atualizacao_pedagogica , g.denominacao , g.inicio , g.fim , g.inicio_inscricao , g.fim_inscricao ";
		String sql = " select distinct "+projecao+" from apedagogica.grupo_atividades_atualizacao_pedagogica g "+
						" left join apedagogica.atividade_atualizacao_pedagogica a on a.id_grupo_atividades_atualizacao_pedagogica = g.id_grupo_atividades_atualizacao_pedagogica "+
						" left join apedagogica.participante_atividade_atualizacao_pedagogica p on p.id_atividade_atualizacao_pedagogica = a.id_atividade_atualizacao_pedagogica "+
						" where p.id_servidor = "+idDocente+" order by g.denominacao asc ";

		Query query = getSession().createSQLQuery(sql);
		List<Object[]> result = query.list();
		List<GrupoAtividadesAtualizacaoPedagogica> grupos = new ArrayList<GrupoAtividadesAtualizacaoPedagogica>();
		
		if (result!=null){

			for (Object[] linha : result) {
				Integer i = 0;
				
				GrupoAtividadesAtualizacaoPedagogica g = new GrupoAtividadesAtualizacaoPedagogica();
				g.setId((Integer) linha[i++]);
				g.setDenominacao((String) linha[i++]);
				g.setInicio((Date) linha[i++]);
				g.setFim((Date) linha[i++]);
				g.setInicioInscricao((Date) linha[i++]);
				g.setFimInscricao((Date) linha[i++]);

				grupos.add(g);

			}	
		}
		
		return grupos;
	}

}