/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '22/07/2009'
 *
 */
package br.ufrn.sigaa.arq.dao.prodocente;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.prodocente.atividades.dominio.Estagio;

/**
 * DAO responsável por consultas específicas à entidade Estágio.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
public class EstagioDao extends GenericSigaaDAO {

	/** Retorna uma coleção de estágios do discente, sob orientação do servidor.
	 * @param idDiscente
	 * @param idServidor
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Estagio> findByDiscenteServidor(Integer idDiscente,
			Integer idServidor) throws HibernateException, DAOException {
		Criteria c = getSession().createCriteria(Estagio.class);
		if( !isEmpty(idDiscente) && idDiscente > 0  )
			c.createCriteria("aluno").add(Restrictions.eq("id", idDiscente));
		if( !isEmpty(idServidor) && idServidor > 0   )
			c.createCriteria("servidor").add(Restrictions.eq("id", idServidor));
		return c.list();
	}
	
	/** Retorna os estágios do docente em questão dentro do período informado */
	@SuppressWarnings("unchecked")
	public Collection<Estagio> findByEstagioDocente(Integer idServidor, Integer anoInicial, Integer anoFinal, Integer periodoInicial, Integer periodoFinal ) 
			throws HibernateException, DAOException {
		
		String sql = "select e.periodo_inicio, e.periodo_fim, coalesce(ccd.nome, e.nome_projeto, 'NÃO INFORMADO') as estagio, p.nome" +
				" from prodocente.estagio e" +
				" left join ensino.matricula_componente mc on ( mc.id_matricula_componente = e.id_matricula_componente and mc.id_situacao_matricula not in ( :idSituacoes )  )" +
				" left join ensino.componente_curricular_detalhes ccd using ( id_componente_detalhes )" +
				" join discente d on ( e.id_orientando = d.id_discente )" +
				" join comum.pessoa p on ( d.id_pessoa = p.id_pessoa )" +
				" where e.id_servidor = :idServidor and e.ativo = trueValue()";
				sql += String.format(" and (%1$s = %3$s or (%1$s > %3$s and %1$s <= %4$s) " +
						"or (%1$s < %3$s and (%3$s <= %2$s or %2$s is null)))", "e.periodo_inicio", "e.periodo_fim", ":dataInicio", ":dataFim");
				sql += " order by p.nome";

		List<Estagio> result = new ArrayList<Estagio>();
		Query q = getSession().createSQLQuery(sql);
		q.setInteger("idServidor", idServidor);
		q.setDate("dataInicio", CalendarUtils.createDate(01, periodoInicial == 1 ? 0 : 6, anoInicial));
		q.setDate("dataFim", CalendarUtils.createDate(periodoFinal == 1 ? 30 : 31, periodoFinal == 1 ? 5 : 11, anoFinal));
		q.setParameterList("idSituacoes", SituacaoMatricula.getSituacoesNegativas());

		List<Object[]> list = q.list();
		for (Object[] obj : list) {
			Estagio e = new Estagio();
			e.setPeriodoInicio( (Date) obj[0] );
			e.setPeriodoFim( (Date) obj[1] );
			e.setMatricula(new MatriculaComponente());
			e.getMatricula().setComponente(new ComponenteCurricular());
			e.getMatricula().getComponente().setDetalhes(new ComponenteDetalhes());
			e.getMatricula().getComponente().getDetalhes().setNome( (String) obj[2] );
			e.setAluno(new Discente());
			e.getAluno().setPessoa(new Pessoa());
			e.getAluno().getPessoa().setNome( (String) obj[3] );
			
			result.add(e);
		}
		 
		return result;
	}
	
}