/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 14/12/2010
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.graduacao.dominio.CancelamentoConvocacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.MotivoCancelamentoConvocacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.OpcaoCadastramento;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivo;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivoDiscente;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;

/**
 * DAO com consultas utilizadas no cadastramento de discentes de graduação
 * aprovados no vestibular
 * 
 * @author Leonardo Campos
 * 
 */
public class CadastramentoDiscenteDao extends GenericSigaaDAO {
	
	/**
	 * Busca as convocações de discentes para a matriz curricular e o processo seletivo vestibular informados.
	 * @param idMatriz
	 * @param idProcessoSeletivo
	 * @param idStatusDiscente
	 * @return
	 * @throws DAOException
	 */
	public Collection<ConvocacaoProcessoSeletivoDiscente> findConvocacoes(int idMatriz, int idProcessoSeletivo,
			int anoIngresso, int periodoIngresso,
			Integer... idStatusDiscente) throws DAOException {
		StringBuilder hql = new StringBuilder("SELECT cpsd.id_convocacao_processo_seletivo_discente," +
				" cd.descricao, d.id_discente," +
				" d.matricula, " +
				" d.nivel, " +
				" pessoa.id_pessoa, " +
				" pessoa.nome, " +
				" d.status, " +
				" d.ano_ingresso," +
				" d.periodo_ingresso, " +
				"(SELECT cc.id_cancelamento_convocacao" +
				" FROM graduacao.cancelamento_convocacao cc " +
				" WHERE id_convocacao_processo_seletivo_discente = cpsd.id_convocacao_processo_seletivo_discente" +
				" and cc.id_registro_cadastro IS NOT NULL " +
				" order by cc.data_cancelamento desc" +
				" limit 1) as cancelamento " +
				" FROM vestibular.convocacao_processo_seletivo_discente cpsd " +
				" INNER JOIN vestibular.convocacao_processo_seletivo cd using (id_convocacao_processo_seletivo)"+
				" INNER JOIN discente d using (id_discente)" +
				" INNER JOIN comum.pessoa using (id_pessoa)" +
				" INNER JOIN graduacao.discente_graduacao dg on (d.id_discente = dg.id_discente_graduacao)" +
				" WHERE cd.id_processo_seletivo = :idProcessoSeletivo ");
		if (idMatriz > 0)
			hql.append(" AND dg.id_matriz_curricular = :idMatriz ");
		if (!isEmpty(idStatusDiscente))
			hql.append(" AND d.status in " + UFRNUtils.gerarStringIn(idStatusDiscente));
		if (anoIngresso > 0)
			hql.append(" AND d.ano_ingresso = :anoIngresso");
		if (periodoIngresso > 0)
			hql.append(" AND d.periodo_ingresso = :periodoIngresso");
		hql.append(" ORDER BY cd.id_convocacao_processo_seletivo, cd.id_convocacao_processo_seletivo, d.periodo_ingresso, pessoa.nome");

		SQLQuery q = getSession().createSQLQuery(hql.toString());
		q.setInteger("idProcessoSeletivo", idProcessoSeletivo);
		if (idMatriz > 0)
			q.setInteger("idMatriz", idMatriz);
		if (anoIngresso > 0)
			q.setInteger("anoIngresso", anoIngresso);
		if (periodoIngresso > 0)
			q.setInteger("periodoIngresso", periodoIngresso);
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.list();
		
		Collection<ConvocacaoProcessoSeletivoDiscente> result = new ArrayList<ConvocacaoProcessoSeletivoDiscente>();
		int col = 0;
		for (Object[] colunas : list) {
			Integer idConvocacao = (Integer) colunas[col++];
			String descricaoConvocacao = (String) colunas[col++];
			Integer idDiscente = (Integer) colunas[col++];
			BigInteger matricula = (BigInteger) colunas[col++];
			Character nivel = (Character) colunas[col++];
			Integer idPessoa = (Integer) colunas[col++];
			String nome = (String) colunas[col++];
			Short status = (Short) colunas[col++];
			
			DiscenteGraduacao d = new DiscenteGraduacao();
			d.setId(idDiscente);
			d.getDiscente().setId(idDiscente);
			d.setMatricula((matricula != null ? matricula.longValue() : null));
			d.setNivel(nivel);
			d.getDiscente().getPessoa().setId(idPessoa);
			d.getDiscente().getPessoa().setNome(nome);
			d.setStatus((status != null ? status.intValue() : null));
			d.setAnoIngresso((Integer) colunas[col++]);
			d.setPeriodoIngresso((Integer) colunas[col++]);
			
			ConvocacaoProcessoSeletivo cd = new ConvocacaoProcessoSeletivo();
			cd.setDescricao(descricaoConvocacao);
			
			ConvocacaoProcessoSeletivoDiscente c = new ConvocacaoProcessoSeletivoDiscente(idConvocacao);
			c.setDiscente(d);
			c.setConvocacaoProcessoSeletivo(cd);
			
			Integer idCancelamento = (Integer) colunas[col++];
			if (idCancelamento != null) {
				c.setCancelamento( new CancelamentoConvocacao() );
				c.getCancelamento().setId(idCancelamento);
			} else if (d.getDiscente().isPendenteCadastro()){
				d.setOpcaoCadastramento(OpcaoCadastramento.IGNORAR.ordinal());
			}
			
			result.add(c);
			col = 0;
		}
		// motivos dos cancelamentos
		List<Integer> idsCancelamentos = new LinkedList<Integer>();
		for (ConvocacaoProcessoSeletivoDiscente convocacao : result) {
			if (convocacao.getCancelamento() != null)
				idsCancelamentos.add(convocacao.getCancelamento().getId());
		}
		if (!isEmpty(idsCancelamentos)) {
			@SuppressWarnings("unchecked")
			List<Object[]> cancelamentos = getSession().createQuery("select c.id, c.motivo.id, c.motivo.descricao" +
					" from CancelamentoConvocacao c" +
					" where c.id in " + UFRNUtils.gerarStringIn(idsCancelamentos)).list();
			if (!isEmpty(cancelamentos)) {
				for (Object[] obj : cancelamentos) {
					int idCancelamento = (Integer) obj[0];
					MotivoCancelamentoConvocacao motivo = new MotivoCancelamentoConvocacao((Integer) obj[1], (String) obj[2]);
					for (ConvocacaoProcessoSeletivoDiscente convocacao : result) {
						if (convocacao.getCancelamento() != null && convocacao.getCancelamento().getId() == idCancelamento) {
							convocacao.getCancelamento().setMotivo(motivo);
						}
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * Retorna um mapa com o id de cada discente e sua respectiva movimentação de cancelamento, se houver.
	 * @param discentes
	 * @return
	 * @throws DAOException
	 */
	public Map<Integer, MovimentacaoAluno> findMapaMovimentacoesCancelamento(Collection<ConvocacaoProcessoSeletivoDiscente> convocacoes) throws DAOException {
		Collection<Discente> discentes = new ArrayList<Discente>();
		for(ConvocacaoProcessoSeletivoDiscente c: convocacoes)
			discentes.add(c.getDiscente().getDiscente());
		
		String hql = "SELECT m.id, m.discente.id FROM MovimentacaoAluno m WHERE m.discente.id in " + UFRNUtils.gerarStringIn(discentes);
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = getSession().createQuery(hql).list();
		
		Map<Integer, MovimentacaoAluno> result = new HashMap<Integer, MovimentacaoAluno>();
		
		int col = 0;
		for (Object[] colunas : list) {
			MovimentacaoAluno m = new MovimentacaoAluno((Integer) colunas[col++]);
			result.put((Integer) colunas[col++], m);
			col = 0;
		}
		
		return result;
	}
	
	/**
	 * Atualiza os status dos discentes passados como argumento com o valor do status informado.
	 * @param discentes
	 * @param status
	 * @throws DAOException
	 */
	public void updateStatusDiscentes(Collection<Discente> discentes, int status) throws DAOException {
		getSession().createQuery("UPDATE Discente SET status = :status WHERE id in "+ UFRNUtils.gerarStringIn(discentes)).setInteger("status", status).executeUpdate();
	}
	
	/**
	 * Atualiza o número de matrícula do discente.
	 * @param discentes
	 * @param status
	 * @throws DAOException
	 */
	public void updateMatriculaDiscente(int id, long matricula) throws DAOException {
		getSession().createQuery("UPDATE Discente SET matricula = :matricula WHERE id = :id").setInteger("id", id).setLong("matricula", matricula).executeUpdate();
	}
	
	/**
	 * Retorna uma coleção de processos seletivos apenas com o ano de entrada populado e cuja forma de ingresso é {@link FormaIngresso.VESTIBULAR}.
	 * @return
	 * @throws DAOException
	 */
	public Collection<ProcessoSeletivoVestibular> findAnosProcessosSeletivosVestibular() throws DAOException {
		String hql = "SELECT distinct anoEntrada FROM ProcessoSeletivoVestibular WHERE anoEntrada > 0 AND formaIngresso.id = :formaIngressoVestibular";
		
		@SuppressWarnings("unchecked")
		List<Integer> list = getSession().createQuery(hql).setInteger("formaIngressoVestibular", FormaIngresso.VESTIBULAR.getId()).list();
		
		Collection<ProcessoSeletivoVestibular> result = new ArrayList<ProcessoSeletivoVestibular>();
		
		for (Integer integer : list) {
			ProcessoSeletivoVestibular p = new ProcessoSeletivoVestibular();
			p.setAnoEntrada(integer);
			result.add(p);
		}
		
		return result;
	}

	/**
	 * Retorna uma lista de matrizes curriculares que possuem vagas ofertadas no vestibular no ano informado.
	 * @param ano
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatrizCurricular> findMatrizes(ProcessoSeletivoVestibular psVest) throws DAOException {
		String projecao = "m.id, m.curso.nome, m.curso.municipio.nome, m.enfase.nome , " +
				"m.habilitacao.nome, m.turno.sigla, m.grauAcademico.descricao";
		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT distinct " + projecao); 
		hql.append(" FROM OfertaVagasCurso o ");
		hql.append(" JOIN o.matrizCurricular m");
		hql.append(" LEFT JOIN m.enfase enfase");
		hql.append(" LEFT JOIN m.habilitacao habilitacao");
		hql.append(" WHERE o.formaIngresso.id = :idFormaIngresso" +
				" and o.ano = :anoIngresso");
		// Caso o processo seletivo tenha entrada em algum semestre específico:
		if (psVest.getPeriodoEntrada() == 1) {
			hql.append(" and o.vagasPeriodo1 > 0");
		} else if (psVest.getPeriodoEntrada() == 2) {
			hql.append(" and o.vagasPeriodo2 > 0");
		}
		hql.append(" ORDER BY m.curso.municipio.nome, m.curso.nome ");
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idFormaIngresso", psVest.getFormaIngresso().getId());
		q.setInteger("anoIngresso", psVest.getAnoEntrada());
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		Collection<MatrizCurricular> result = HibernateUtils.parseTo(lista, projecao, MatrizCurricular.class, "m");
		return result;
	}

	/** Retorna as convocações de discentes de um determinado processo seletivo.
	 * @param nome
	 * @param cpf_cnpj
	 * @param idConvocacaoProcessoSeletivo
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<ConvocacaoProcessoSeletivoDiscente> findConvocacaoDiscenteByNomeCPF(String nome, Long cpf_cnpj, int idConvocacaoProcessoSeletivo) throws HibernateException, DAOException {
		int status[] = {StatusDiscente.PENDENTE_CADASTRO, StatusDiscente.PRE_CADASTRADO, StatusDiscente.CADASTRADO, StatusDiscente.ATIVO, StatusDiscente.ATIVO_DEPENDENCIA};
		String projecao = "cpsd.id, cpsd.convocacaoProcessoSeletivo.id, cpsd.inscricaoVestibular.pessoa.nome, " +
				" cpsd.inscricaoVestibular.pessoa.cpf_cnpj, cpsd.matrizCurricular, cpsd.discente.matrizCurricular," +
				" cpsd.discente.discente.status";
		StringBuilder hql = new StringBuilder("select ").append(projecao)
				.append(" from ConvocacaoProcessoSeletivoDiscente cpsd" +
						" inner join cpsd.matrizCurricular matrizCurricular" +
						" inner join cpsd.inscricaoVestibular inscricaoVestibular" +
						" inner join inscricaoVestibular.pessoa pessoa" +
						" where cpsd.convocacaoProcessoSeletivo.id = :idConvocacaoProcessoSeletivo" +
						" and cpsd.discente.discente.status in ")
				.append(UFRNUtils.gerarStringIn(status));
		if (!isEmpty(nome))
			hql.append(" and pessoa.nomeAscii like :nome");
		if (!isEmpty(cpf_cnpj))
			hql.append(" and pessoa.cpf_cnpj = :cpf");
		hql.append(" order by pessoa.nomeAscii");
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idConvocacaoProcessoSeletivo", idConvocacaoProcessoSeletivo);
		if (!isEmpty(nome))
			q.setString("nome", StringUtils.toAscii(nome).toUpperCase() + "%");
		if (!isEmpty(cpf_cnpj))
			q.setLong("cpf", cpf_cnpj);
		@SuppressWarnings("unchecked")
		List<ConvocacaoProcessoSeletivoDiscente> lista = (List<ConvocacaoProcessoSeletivoDiscente>) HibernateUtils.parseTo(q.list(), projecao, ConvocacaoProcessoSeletivoDiscente.class, "cpsd");
		return lista;
	}
	
}
