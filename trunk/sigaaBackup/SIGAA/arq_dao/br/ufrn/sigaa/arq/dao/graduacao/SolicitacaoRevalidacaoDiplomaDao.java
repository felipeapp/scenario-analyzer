/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 09/01/2009
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoRevalidacaoDiploma;

/**
 * Classe DAO responsável por todas as consultas relacionadas a solicitação de revalidação de diplomas
 * 
 * @author Mário Rizzi
 *
 */
public class SolicitacaoRevalidacaoDiplomaDao extends GenericSigaaDAO {

	
	/**
	 * Retorna uma solicitação de acordo com os parâmetros setados
	 * @param id
	 * @param nacionalidade
	 * @param cpf
	 * @param passaporte
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public SolicitacaoRevalidacaoDiploma findByNacionalidadeDocumento(SolicitacaoRevalidacaoDiploma solicitacao, Integer nacionalidade, Long cpf, String passaporte)
		throws DAOException {

			String where = " WHERE editalRevalidacaoDiploma.id = " + solicitacao.getEditalRevalidacaoDiploma().getId();
			if(solicitacao != null && solicitacao.getId()>0)
				where += " and a.id = :id ";
			if( !isEmpty(nacionalidade) )
				where += " and a.pais.id = :nacionalidade ";
			if( cpf != null ){
				where += " and a.cpf = :cpf ";
			}
			if( passaporte != null)
				where += " and a.passaporte = :passaporte ";
		
			Query query = getSession().createQuery(" SELECT a FROM SolicitacaoRevalidacaoDiploma a" + where + " ORDER BY a.nome ");
			
			if(solicitacao != null && solicitacao.getId()>0)
				query.setInteger("id", solicitacao.getId());
			if( !isEmpty(nacionalidade) )
				query.setInteger("nacionalidade", nacionalidade);
			if(  cpf != null )
				query.setLong("cpf",  cpf );
			if( passaporte != null )
				query.setString("passaporte", passaporte);
			
			List<SolicitacaoRevalidacaoDiploma> lista = query.list();
			
			if(!ValidatorUtil.isEmpty(lista))
				return lista.iterator().next();
			
			return null;
	
	}

	/**
	 * Retorna todas as solicitações de revalidação de diploma de acordo com os parâmetro setados.
	 * @param data
	 * @param horario
	 * @param filaEspera
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<SolicitacaoRevalidacaoDiploma> findGeral(int idAgendaRevalidacao, 
			String nome, Date data, String horario, Boolean filaEspera) 
		throws DAOException{
		
		StringBuilder hql = new StringBuilder();
		
		hql.append("SELECT DISTINCT(s) FROM SolicitacaoRevalidacaoDiploma s WHERE" +
					" s.editalRevalidacaoDiploma.id = " + idAgendaRevalidacao);

		if( nome != null && nome.length()>0)
			hql.append(" and upper(s.nome) LIKE  :nome ");
		if(data != null)
			hql.append(" and s.agendaRevalidacaoDiploma.data = :data ");
		if(horario != null)
			hql.append(" and s.agendaRevalidacaoDiploma.horario = :horario");
		if(filaEspera == true)
			hql.append(" and s.agendaRevalidacaoDiploma.id is null");
		else if(filaEspera == false)
			hql.append(" and s.agendaRevalidacaoDiploma.id is not null");
		
		hql.append(" ORDER BY s.agendaRevalidacaoDiploma, s.nome");
		
	
		Query q = getSession().createQuery(hql.toString());
	
		if( nome != null && nome.length()>0)
			q.setString("nome", "%" + StringUtils.toAscii(nome.toUpperCase().trim()) + "%");
		if(data != null)
			q.setDate("data", data);
		if(horario != null)
			q.setString("horario", horario);

		return q.list();
	}
	
}
