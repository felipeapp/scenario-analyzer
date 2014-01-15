/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/04/2007
 *
 */
package br.ufrn.sigaa.arq.dao.eleicao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.eleicao.dominio.Eleicao;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Classe responsável por consultas específicas para Eleições e Relatórios
 * associados.
 * 
 * @author Victor Hugo
 * 
 */
public class EleicaoDao extends GenericSigaaDAO {

	/**
	 * retorna todas as eleições que o prazo de fim ainda não expirou
	 * @return
	 * @throws DAOException
	 */
	public Collection<Eleicao> findEleicoesAbertas(Integer idUnidade) throws DAOException{
		
		try {
			Date hoje = new Date();
			//dataHoje = DateUtils.truncate(dataHoje, Calendar.DAY_OF_MONTH);
			
			Criteria c = getSession().createCriteria(Eleicao.class);
			c.add(Expression.le("dataInicio", hoje));
			c.add(Expression.ge("dataFim", hoje));

			c.addOrder(Order.asc("dataInicio"));
			
			if( idUnidade != null ){
				c.add( Expression.eq( "centro.id" , idUnidade ) );
			}
			
			@SuppressWarnings("unchecked")
			Collection<Eleicao> lista = c.list();
			return lista;
			
		} catch (Exception e) {
			throw new DAOException(e);
		}
		
	}
	
	/**
	 * Retorna uma lista de docentes (siape, nome, unidade) aptos a votar para
	 * coordenador de curso. A lista é criada buscando os docentes que
	 * lecionaram uma turma no ano-período que possuem reserva de vagas para o
	 * curso especificado
	 * 
	 * @param idCurso
	 * @param ano
	 * @param period
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<Servidor> findDocentesAptosVotarCoordenadorCurso(int idCurso, int ano, int periodo) throws HibernateException, DAOException {
		
		String sqlTurmaReservaVaga = "SELECT"
			+ " DISTINCT ser.siape AS matricula_siape,"
			+ " pes.nome,"
			+ " un.nome AS depto_lotacao"
			+ " FROM rh.servidor ser"
			+ " INNER JOIN comum.pessoa pes ON pes.id_pessoa = ser.id_pessoa"
			+ " INNER JOIN comum.unidade un ON un.id_unidade = ser.id_unidade"
			+ " INNER JOIN ensino.docente_turma dt ON dt.id_docente = ser.id_servidor"
			+ " INNER JOIN ensino.turma t ON t.id_turma = dt.id_turma"
			+ " INNER JOIN graduacao.reserva_curso rc ON rc.id_turma = t.id_turma"
			+ " INNER JOIN graduacao.matriz_curricular mc ON mc.id_matriz_curricular = rc.id_matriz_curricular"
			+ " INNER JOIN public.curso cur ON cur.id_curso = mc.id_curso"
			+ "   AND cur.id_curso = :idCurso"
			+ "   AND t.ano = :ano"
			+ "   AND t.periodo = :periodo";
		
		String sqlOrietacaoAtividade = "SELECT"
			+ " DISTINCT ser.siape AS matricula_siape,"
			+ " pes.nome,"
			+ " un.nome AS depto_lotacao"
			+ " FROM rh.servidor ser"
			+ " INNER JOIN comum.pessoa pes ON pes.id_pessoa = ser.id_pessoa"
			+ " INNER JOIN comum.unidade un ON un.id_unidade = ser.id_unidade"
			+ " INNER JOIN ensino.orientacao_atividade o on o.id_servidor=ser.id_servidor"
			+ " INNER JOIN ensino.registro_atividade reg ON reg.id_registro_atividade = o.id_registro_atividade"
			+ " INNER join ensino.matricula_componente mc on mc.id_matricula_componente=reg.id_matricula_componente"
			+ " INNER JOIN discente d ON d.id_discente = mc.id_discente"
			+ " INNER JOIN public.curso cur ON cur.id_curso = d.id_curso  "
			+ "   AND cur.id_curso = :idCurso"
			+ "   AND mc.ano = :ano"
			+ "   AND mc.periodo = :periodo";
		
		String sqlUnion = "SELECT * FROM (" + sqlTurmaReservaVaga + " UNION " + sqlOrietacaoAtividade + ") AS result ORDER BY result.nome ASC, result.nome ASC";
		Query q = getSession().createSQLQuery(sqlUnion);
		q.setInteger("idCurso", idCurso);
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		List<Servidor> listaServidor = new ArrayList<Servidor>();
		if (lista != null) {
			for (Object[] obj : lista){
				int i = 0;
				Servidor servidor = new Servidor();
				servidor.setSiape((Integer) obj[i++]);
				servidor.getPessoa().setNome((String) obj[i++]);
				servidor.setUnidade(new Unidade());
				servidor.getUnidade().setNome((String) obj[i++]);
				listaServidor.add(servidor);
			}
		}
		return listaServidor;
	}

}
