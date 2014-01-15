/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/10/2012
 *
 */
package br.ufrn.sigaa.ensino.tecnico.dao;

import java.util.ArrayList;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.parametros.dominio.ParametrosTecnico;

/**
 * DAO com consultas para os dados cadastrais dos discentes do IDM.
 * 
 * @author Fred_Castro
 * 
 */
public class DadosDiscentesIMDDao extends GenericSigaaDAO {

	public ArrayList <DiscenteTecnico> findDiscentesByNomeMatriculaCPF (String nome, Long matricula, Long cpf, Integer opcaoPoloGrupo, Integer status) throws DAOException{
		String projecao = "id, discente.matricula, discente.status, discente.pessoa.cpf_cnpj, discente.pessoa.nome, discente.pessoa.email, turmaEntradaTecnico.id, turmaEntradaTecnico.especializacao.descricao";
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("select " + projecao + " from DiscenteTecnico where discente.curso.id = :curso and discente.gestoraAcademica.id = :gestoraAcademica ");
			
			if (!StringUtils.isEmpty(nome))
				hql.append(" and discente.pessoa.nomeAscii like :nome");
			if (cpf != null)
				hql.append(" and discente.pessoa.cpf_cnpj = :cpf");
			if (matricula != null)
				hql.append(" and discente.matricula = :matricula");
			if (opcaoPoloGrupo > 0)
				hql.append(" and turmaEntradaTecnico.opcaoPoloGrupo.id = :opcaoPoloGrupo");
			if (status > 0)
				hql.append(" and discente.status = :status");
	
			Query q = getSession().createQuery(hql.toString());
			
			if (!StringUtils.isEmpty(nome))
				q.setString("nome", StringUtils.toAsciiAndUpperCase("%" + nome + "%"));
			if (matricula != null)
				q.setLong("matricula", matricula);
			if (cpf != null)
				q.setLong("cpf", cpf);
			if (opcaoPoloGrupo > 0)
				q.setInteger("opcaoPoloGrupo", opcaoPoloGrupo);
			if (status > 0)
				q.setInteger("status", status);
			
			q.setInteger("gestoraAcademica", ParametroHelper.getInstance().getParametroInt(ParametrosTecnico.ID_UNIDADE_INSTITUTO_METROPOLE_DIGITAL));
			q.setInteger("curso", ParametroHelper.getInstance().getParametroInt(ParametrosTecnico.ID_CURSO_METROPOLE_DIGITAL_PARA_CONVOCACAO));
			
			@SuppressWarnings("unchecked")
			ArrayList <DiscenteTecnico> lista = (ArrayList<DiscenteTecnico>) HibernateUtils.parseTo(q.list(), projecao, DiscenteTecnico.class );
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
}