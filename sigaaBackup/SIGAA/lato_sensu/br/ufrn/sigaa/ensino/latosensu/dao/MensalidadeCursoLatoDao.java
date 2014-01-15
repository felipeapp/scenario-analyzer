/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 02/05/2012
 *
 */
package br.ufrn.sigaa.ensino.latosensu.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.gru.dominio.GuiaRecolhimentoUniao;
import br.ufrn.comum.gru.negocio.GuiaRecolhimentoUniaoHelper;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.MensalidadeCursoLato;
import br.ufrn.sigaa.ensino.latosensu.negocio.MensalidadeCursoLatoHelper;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/** Classe responsável por consultas específicas à mensalidades de cursos latos.
 * 
 * @author Édipo Elder F. de Melo
 *
 */
public class MensalidadeCursoLatoDao extends GenericSigaaDAO {
	
	/** Retorna uma lista de mensalidades cadastradas para um grupo de discentes;
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<MensalidadeCursoLato> findAllByDiscente(Collection<Discente> discentes) throws DAOException {
		Collection<Integer> ids = new LinkedList<Integer>();
		for (Discente discente : discentes)
			ids.add(discente.getId());
		Criteria c = getSession().createCriteria(MensalidadeCursoLato.class)
				.add(Restrictions.in("discente.id", ids))
				.addOrder(Order.asc("ordem"));
		Collection<MensalidadeCursoLato> lista = c.list();
		return lista;
	}

	/** Retorna uma lista de mensalidades cadastradas para um discente;
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<MensalidadeCursoLato> findAllByDiscente(int idDiscente) throws DAOException {
		Criteria c = getSession().createCriteria(MensalidadeCursoLato.class)
				.add(Restrictions.eq("discente.id", idDiscente))
				.addOrder(Order.asc("ordem"));
		Collection<MensalidadeCursoLato> lista = c.list();
		return lista;
	}

	/** Retorna uma coleção de dados para o relatório de mensalidades pagas.
	 * @param idCurso
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<HashMap<String, Object>> findDadosRelatorioMensalidadesPagas(int idCurso) throws HibernateException, DAOException {
		// consulta os discentes do curso
		String projecaoDiscentes = "discente.id, discente.matricula, discente.pessoa.nome";
		String hqlDiscentes = "select " +
				projecaoDiscentes +
				" from Discente discente" +
				" where discente.curso.id = :idCurso" +
				" order by discente.pessoa.nomeAscii";
		Query q = getSession().createQuery(hqlDiscentes);
		q.setInteger("idCurso", idCurso);
		@SuppressWarnings("unchecked")
		Collection<Discente> discentes = HibernateUtils.parseTo(q.list(), projecaoDiscentes, Discente.class, "discente");
		if (isEmpty(discentes))
			return null;
		List<Integer> ids = new ArrayList<Integer>();
		for (Discente discente : discentes)
			ids.add(discente.getId());
		// consulta mensalidades pagas
		String projecaoMensalidades = "mensalidade.id," +
				" mensalidade.discente.id," +
				" mensalidade.idGRU," +
				" mensalidade.vencimento," +
				" mensalidade.ordem," +
				" mensalidade.quitada";
		String hqlMensalidadesPagas = " select " +
				projecaoMensalidades +
				" from MensalidadeCursoLato mensalidade" +
				" where mensalidade.discente.id in "
				+ UFRNUtils.gerarStringIn(ids) +
				" order by mensalidade.discente.id, mensalidade.vencimento";
		Query qm = getSession().createQuery(hqlMensalidadesPagas);
		@SuppressWarnings("unchecked")
		Collection<MensalidadeCursoLato> mensalidades = HibernateUtils.parseTo(qm.list(), projecaoMensalidades, MensalidadeCursoLato.class, "mensalidade");
		// datas de vencimento das GRUS
		CursoLato curso = findByPrimaryKey(idCurso, CursoLato.class);
		List<Date> vencimentos = MensalidadeCursoLatoHelper.getDatasVencimento(curso);
		// merge dos dados de discente com as mensalidades pagas
		List<HashMap<String, Object>> resultado = new ArrayList<HashMap<String,Object>>();
		for (Discente discente : discentes) {
			HashMap<String, Object> linha = new HashMap<String, Object>();
			linha.put("matricula", discente.getMatricula());
			linha.put("nome", discente.getPessoa().getNome());
			linha.put("idDiscente", discente.getId());
			// mensalidades
			for (Date vencimento : vencimentos){
				boolean paga = false;
				for (MensalidadeCursoLato mensalidade : mensalidades) {
					if (mensalidade.getDiscente().getId() == discente.getId()
						&& mensalidade.getVencimento().getTime() - vencimento.getTime() == 0
						&& mensalidade.isQuitada()) {
						paga = true; break;
					}
				}
				linha.put(String.format("%1$td/%1$tm/%1$tY", vencimento), paga);
			}
			resultado.add(linha);
		}
		return resultado;
	}
	
	/** Retorna uma coleção de dados sobre valores pagos para o relatório de mensalidades pagas.
	 * @param idCurso
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<HashMap<String, Object>> findDadosRelatorioMensalidadesValoresPagos(int idCurso) throws HibernateException, DAOException {
		// consulta os discentes do curso
		String projecaoDiscentes = "discente.id, discente.matricula, discente.pessoa.nome";
		String hqlDiscentes = "select " +
				projecaoDiscentes +
				" from Discente discente" +
				" where discente.curso.id = :idCurso" +
				" order by discente.pessoa.nomeAscii";
		Query q = getSession().createQuery(hqlDiscentes);
		q.setInteger("idCurso", idCurso);
		@SuppressWarnings("unchecked")
		Collection<Discente> discentes = HibernateUtils.parseTo(q.list(), projecaoDiscentes, Discente.class, "discente");
		if (isEmpty(discentes))
			return null;
		List<Integer> ids = new ArrayList<Integer>();
		for (Discente discente : discentes)
			ids.add(discente.getId());
		// consulta mensalidades pagas
		String projecaoMensalidades = "mensalidade.id," +
				" mensalidade.discente.id," +
				" mensalidade.idGRU," +
				" mensalidade.vencimento," +
				" mensalidade.ordem," +
				" mensalidade.quitada";
		String hqlMensalidadesPagas = " select " +
				projecaoMensalidades +
				" from MensalidadeCursoLato mensalidade" +
				" where mensalidade.discente.id in "
				+ UFRNUtils.gerarStringIn(ids) +
				" order by mensalidade.discente.id, mensalidade.vencimento";
		Query qm = getSession().createQuery(hqlMensalidadesPagas);
		@SuppressWarnings("unchecked")
		Collection<MensalidadeCursoLato> mensalidades = HibernateUtils.parseTo(qm.list(), projecaoMensalidades, MensalidadeCursoLato.class, "mensalidade");
		// consulta GRUs
		Collection<Integer> idsGRU = new ArrayList<Integer>();
		for (MensalidadeCursoLato mensalidade : mensalidades)
			idsGRU.add(mensalidade.getIdGRU());
		Collection<GuiaRecolhimentoUniao> grus = GuiaRecolhimentoUniaoHelper.getGRUByID(idsGRU);
		// merge dos dados de discente com as mensalidades pagas
		List<HashMap<String, Object>> resultado = new ArrayList<HashMap<String,Object>>();
		if (grus != null) {
			HashMap<String, Object> linha = new HashMap<String, Object>();
			for (GuiaRecolhimentoUniao gru : grus){
				// total por vencimento
				String vencimento = String.format("%1$td/%1$tm/%1$tY", gru.getVencimento());
				Double total = (Double) linha.get(vencimento);
				if (total == null) total = 0d;
				total = total + (gru.getValorPago() == null ? 0.0d : gru.getValorPago());
				linha.put(vencimento, total);
				// total por discente
				for (MensalidadeCursoLato mensalidade : mensalidades) {
					if (mensalidade.getIdGRU() == gru.getId()) {
						String id = "total_" + String.valueOf(mensalidade.getDiscente().getId());
						total = (Double) linha.get(id);
						if (total == null) total = 0d;
						total = total + (gru.getValorPago() == null ? 0.0d : gru.getValorPago());
						linha.put(id, total);
						break;
					}
				}
			}
			resultado.add(linha);
		}
		return resultado;
	}
}
