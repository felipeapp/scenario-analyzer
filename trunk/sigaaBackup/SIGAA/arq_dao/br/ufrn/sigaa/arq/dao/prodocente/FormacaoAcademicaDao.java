/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '04/05/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.prodocente;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.prodocente.atividades.dominio.FormacaoAcademica;
import br.ufrn.sigaa.prodocente.atividades.dominio.FormacaoTese;

/**
 * Dao para as consultas a Formação Acadêmica
 * 
 * Deprecated devido a Formação Acadêmica está sendo consultada no SIGRH através do Spring HTTP Invoker.
 * 
 * @author Mário Rizzi
 *
 */
@Deprecated
public class FormacaoAcademicaDao extends GenericSigaaDAO {
	
	/**
	 * Retorna todas a formações ativas do docente.
	 * @param idServidor
	 * @return
	 * @throws DAOException
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public Collection<FormacaoAcademica> findAtivosByServidor(Integer idServidor) throws DAOException{
		
		Query query = getSession()
		.createQuery("select f.dataFim, ft.descricao, f.grau,f.entidade " +
				" FROM FormacaoAcademica f INNER JOIN f.servidor s INNER JOIN f.formacao ft " +
				" WHERE s.id = :id and f.ativo = trueValue() ORDER BY f.dataFim");
		query.setInteger("id", idServidor);
		
		List<Object[]> lista = query.list();
		List<FormacaoAcademica> formacoes = new ArrayList<FormacaoAcademica>();
		for (Object obj[] : lista) {
			FormacaoAcademica f = new FormacaoAcademica();
			f.setDataFim((Date) obj[0]);
			f.setFormacao(new FormacaoTese());
			f.getFormacao().setDescricao( (String) obj[1]);
			f.setGrau((String) obj[2]);
			f.setEntidade((String) obj[3]);
			formacoes.add(f);
		}
		return formacoes;
		
		
	}	

}
