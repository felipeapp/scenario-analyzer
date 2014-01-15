/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 20/10/2010
 */
package br.ufrn.sigaa.arq.dao.estagio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.estagio.dominio.ConcedenteEstagio;
import br.ufrn.sigaa.estagio.dominio.ConcedenteEstagioPessoa;
import br.ufrn.sigaa.estagio.dominio.ConcedentePessoaFuncao;
import br.ufrn.sigaa.estagio.dominio.Estagiario;
import br.ufrn.sigaa.estagio.dominio.ParametrosEstagio;
import br.ufrn.sigaa.estagio.dominio.StatusEstagio;
import br.ufrn.sigaa.estagio.dominio.StatusRelatorioEstagio;
import br.ufrn.sigaa.estagio.dominio.TipoEstagio;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.questionario.dominio.TipoQuestionario;

/**
 * DAO responsável por gerenciar o acesso a dados de Estagiários
 * 
 * @author Arlindo Rodrigues
 */
public class EstagiarioDao extends GenericSigaaDAO {
	
	/**
	 * Busca por estágios conforme os parâmetros informados
	 * @param discente
	 * @param concedente
	 * @param orientador
	 * @param idCurso
	 * @param idTipoEstagio
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<Estagiario> findGeral(String discente, String concedente, Pessoa responsavel, String orientador, 
			Integer idCurso, Integer idTipoEstagio, Date dataInicio, Date dataFim, Integer... status) throws HibernateException, DAOException{
		StringBuilder hql = new StringBuilder();
		hql.append("select e.id, e.discente.id, e.discente.matricula, e.discente.pessoa.nome, e.discente.pessoa.email," +
				" e.concedente.pessoa.id, e.concedente.pessoa.nome, e.orientador.pessoa.nome, e.discente.curso.nome," +
				" e.tipoEstagio.descricao, e.dataInicio, e.dataFim, e.horaInicio, e.horaFim, e.cargaHorariaSemanal, e.status.id, " +
				" e.status.descricao, e.dataCadastro, pep " +
				" from Estagiario e " +
				" inner join e.discente " +
				" inner join e.discente.pessoa " +
				" inner join e.concedente " +
				" inner join e.concedente.pessoa " +
				" left join e.orientador " +
				" left join e.orientador.pessoa " +
				" inner join e.discente.curso " +
				" left join e.tipoEstagio " +
				" inner join e.status " +
				" inner join e.concedente.concedenteEstagioPessoa as pep "+
				" where 1 = 1 ");
		
		Long matricula = null;
		if (!ValidatorUtil.isEmpty(discente)){
			matricula = StringUtils.extractLong(discente);
			if (matricula != null)
				hql.append(" and e.discente.matricula = "+matricula);
			else
				hql.append(" and "+UFRNUtils.toAsciiUpperUTF8("e.discente.pessoa.nomeAscii") + " like :discente ");			
		}
		
		Long cnpjConcedente = null;
		if (!ValidatorUtil.isEmpty(concedente)){
			cnpjConcedente = StringUtils.extractLong(concedente);
			if (cnpjConcedente != null)
				hql.append(" and e.concedente.pessoa.cpf_cnpj = "+cnpjConcedente);
			else
				hql.append(" and "+UFRNUtils.toAsciiUpperUTF8("e.concedente.pessoa.nomeAscii") + " like :concedente ");			
		}
			
		if (!ValidatorUtil.isEmpty(responsavel))
			hql.append(" and e.concedente.id in ( " +
					" select p.id from ConcedenteEstagioPessoa pe " +
					" inner join pe.concedente p " +
					" where pe.pessoa.id = " +responsavel.getId()+")");					
		
		
		Long cnpjOrientador = null;
		if (!ValidatorUtil.isEmpty(orientador)){
			cnpjOrientador = StringUtils.extractLong(orientador);
			if (cnpjOrientador != null)
				hql.append(" and e.orientador.pessoa.cpf_cnpj = "+cnpjOrientador);
			else		
				hql.append(" and "+UFRNUtils.toAsciiUpperUTF8("e.orientador.pessoa.nomeAscii") + " like :orientador ");
		}
		
		if (idCurso != null && idCurso > 0)
			hql.append(" and e.discente.curso.id = "+idCurso);
		
		if (idTipoEstagio != null && idTipoEstagio > 0)
			hql.append(" and e.tipoEstagio.id = "+idTipoEstagio);
		
		
		if (!ValidatorUtil.isEmpty(dataInicio) && !ValidatorUtil.isEmpty(dataFim)){
			hql.append(" and e.dataInicio <= :dataInicio ");
			hql.append(" and e.dataFim >= :dataFim ");				
		}		
		
		if (!ValidatorUtil.isEmpty(status) && status[0] != null)
			hql.append(" and e.status.id in "+UFRNUtils.gerarStringIn(status));
		else
			hql.append(" and e.status.id != "+StatusEstagio.EM_ANALISE);
		
		hql.append(" order by e.concedente.pessoa.nome, e.discente.pessoa.nome ");
		
		Query q = getSession().createQuery(hql.toString());
		
		if (matricula == null && !ValidatorUtil.isEmpty(discente))
			q.setString("discente", "%"+StringUtils.toAscii(discente.trim().toUpperCase()) + "%");
		
		if (cnpjConcedente == null && !ValidatorUtil.isEmpty(concedente))
			q.setString("concedente", "%"+StringUtils.toAscii(concedente.trim().toUpperCase()) + "%");		
		
		if (cnpjOrientador == null && !ValidatorUtil.isEmpty(orientador))
			q.setString("orientador", "%"+StringUtils.toAscii(orientador.trim().toUpperCase()) + "%");
		
		if (!ValidatorUtil.isEmpty(dataInicio) && !ValidatorUtil.isEmpty(dataFim)){
			q.setDate("dataInicio", dataInicio);
			q.setDate("dataFim", dataFim);						
		}
		
		List<Estagiario> estagios = new ArrayList<Estagiario>();
		@SuppressWarnings("unchecked")
        Collection<Object[]> res = q.list();
        if (res != null ) {
        	for (Object[] reg : res) {
        		int a = 0;
        		int linhaPessoa = 0;

        		Estagiario e = new Estagiario();
        		e.setId((Integer) reg[a++]);		
    			if (!estagios.contains(e)) {        		
        		
	        		e.setDiscente(new Discente());
	        		e.getDiscente().getDiscente().setId((Integer) reg[a++]);
	        		e.getDiscente().getDiscente().setMatricula((Long) reg[a++]);
	        		e.getDiscente().getDiscente().setPessoa(new Pessoa());
	        		e.getDiscente().getDiscente().getPessoa().setNome((String) reg[a++]);
	        		e.getDiscente().getDiscente().getPessoa().setEmail((String) reg[a++]);
	        		
	        		e.setConcedente(new ConcedenteEstagio());
	        		e.getConcedente().setPessoa(new Pessoa());
	        		e.getConcedente().getPessoa().setId((Integer)reg[a++]);
	        		e.getConcedente().getPessoa().setNome((String)reg[a++]);
	        		
	        		e.setOrientador(new Servidor());
	        		e.getOrientador().setPessoa(new Pessoa());
	        		if (reg[a] != null){
	        			e.getOrientador().getPessoa().setNome((String)reg[a]);        			
	        		} 
	        		
	        		a++;
	        		
	        		e.getDiscente().setCurso(new Curso());
	        		e.getDiscente().getCurso().setNome((String) reg[a++]);
	        		
	        		e.setTipoEstagio(new TipoEstagio());
	        		if (reg[a] != null){
	        			e.getTipoEstagio().setDescricao((String) reg[a]);        			
	        		}
	        		
	        		a++;
	        		
	        		e.setDataInicio((Date) reg[a++]);  
	        		e.setDataFim((Date) reg[a++]);
	        		
	        		e.setHoraInicio((Date) reg[a++]);  
	        		e.setHoraFim((Date) reg[a++]);        		
	        		
	        		e.setCargaHorariaSemanal((Integer) reg[a++]);
	        		
	        		e.setStatus(new StatusEstagio());
	        		e.getStatus().setId((Integer) reg[a++]);
	        		e.getStatus().setDescricao((String) reg[a++]);
	        		
	        		e.setDataCadastro((Date) reg[a++]);
	        		
					linhaPessoa = a++;
					if(reg[linhaPessoa] != null)
						e.getConcedente().setConcedenteEstagioPessoa(new ArrayList<ConcedenteEstagioPessoa>());
					estagios.add(e);
				} else {
					linhaPessoa = 18;
					e = estagios.get(estagios.indexOf(e));
				}			
				if(reg[linhaPessoa] != null){
					ConcedenteEstagioPessoa pep = (ConcedenteEstagioPessoa) reg[linhaPessoa];
					e.getConcedente().getConcedenteEstagioPessoa().add(pep);
					if (pep.getFuncao().getId() == ConcedentePessoaFuncao.ADMINISTRADOR)
						e.getConcedente().setResponsavel(pep);
					else if (pep.getFuncao().getId() == ConcedentePessoaFuncao.SUPERVISOR)
						e.getConcedente().setSupervisor(pep);
				}
        	}
        }
		
		return estagios; 
	}
	
	/**
	 * Retorna os estágios ativos de um discente em um determinado período. 
	 * @param discente
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<Estagiario> findEstagioAtivosByDiscente(String discente, Date dataInicio, Date dataFim) throws HibernateException, DAOException{
		return findGeral(discente, null, null, null, null, null, dataInicio, dataFim, StatusEstagio.APROVADO, StatusEstagio.SOLICITADO_CANCELAMENTO);		
	}
	
	/**
	 * Retorna todos os estágios que precisam preencher relatório semestral. 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findEstagiosComRelatoriosPendente(Integer idDiscente) throws HibernateException, DAOException{
		
		int quantMesesRelatorio = ParametroHelper.getInstance().getParametroInt(ParametrosEstagio.MESES_PARA_PREENCHIMENTO_RELATORIO_ESTAGIO);
		// quantidade de relatórios: número de dias / 30 dias (um Mês) / quantidadeMeses 
		String qtdRelatorios = " (extract(days from (now() - e.data_inicio))::int + 30)/ 30 / " + quantMesesRelatorio;
		
		// consulta de pendências do discente
		StringBuilder sql = new StringBuilder("select e.id_estagiario, e.data_inicio, e.data_fim, d.matricula, p.nome as discente, "+
		" c.nome as curso, p.nome, p.email, te.descricao as tipo_estagio, ce.numero_convenio, " +
		" ppe.nome as concedente, ppe.cpf_cnpj, 'D' as tipo, coalesce(qtd_preenchido, 0) as qtd_preenchido,");
		sql.append(qtdRelatorios).append(" as qtd_esperada")
		.append(" from estagio.estagiario e" +
				" inner join discente d on (d.id_discente = e.id_discente)" +
				" inner join comum.pessoa p on (p.id_pessoa = d.id_pessoa)" +
				" inner join public.curso c on (c.id_curso = d.id_curso)" +
				" inner join estagio.tipo_estagio te on (te.id_tipo_estagio = e.id_tipo_estagio)" +
				" inner join estagio.concedente_estagio pe on (pe.id_concedente_estagio = e.id_concedente_estagio)" +
				" inner join estagio.convenio_estagio ce on (ce.id_convenio_estagio = pe.id_convenio_estagio)" +
				" inner join comum.pessoa ppe on (ppe.id_pessoa = pe.id_pessoa)" +
				" left join (" +
				"    select id_estagiario, count(*) as qtd_preenchido" +
				"    from estagio.relatorio_estagio" +
				"    inner join questionario.questionario using (id_questionario)" +
				"    where status in ("+StatusRelatorioEstagio.APROVADO +","+ StatusRelatorioEstagio.PENDENTE +")" +
				"    and id_tipo_questionario = " + TipoQuestionario.RELATORIO_DE_ESTAGIO_DISCENTE +
				"    group by id_estagiario) as relatorios_preenchidos using (id_estagiario)" +
				" where e.status in ("+StatusEstagio.APROVADO +","+ StatusEstagio.SOLICITADO_CANCELAMENTO +")" +
				" and coalesce(qtd_preenchido, 0) < ").append(qtdRelatorios);
		
		if (idDiscente != null)
			sql.append(" and d.id_discente = ").append(idDiscente);
		else {
			// caso não seja consulta específica de discente, inclui as pendências de relatórios de orientador e supervisor
			// consulta de pendências do supervisor
			sql.append(" union ")
			.append("select e.id_estagiario, e.data_inicio, e.data_fim, d.matricula, p.nome as discente, "+
			" c.nome as curso, ps.nome, ps.email, te.descricao as tipo_estagio, ce.numero_convenio, " +
			" ppe.nome as concedente, ppe.cpf_cnpj, 'D' as tipo, coalesce(qtd_preenchido, 0) as qtd_preenchido,");
			sql.append(qtdRelatorios).append(" as qtd_esperada")
			.append(" from estagio.estagiario e" +
					" inner join discente d on (d.id_discente = e.id_discente)" +
					" inner join comum.pessoa p on (p.id_pessoa = d.id_pessoa)" +
					" inner join comum.pessoa ps on (ps.id_pessoa = e.id_pessoa_supervisor)" +
					" inner join public.curso c on (c.id_curso = d.id_curso)" +
					" inner join estagio.tipo_estagio te on (te.id_tipo_estagio = e.id_tipo_estagio)" +
					" inner join estagio.concedente_estagio pe on (pe.id_concedente_estagio = e.id_concedente_estagio)" +
					" inner join estagio.convenio_estagio ce on (ce.id_convenio_estagio = pe.id_convenio_estagio)" +
					" inner join comum.pessoa ppe on (ppe.id_pessoa = pe.id_pessoa)" +
					" left join (" +
					"    select id_estagiario, count(*) as qtd_preenchido" +
					"    from estagio.relatorio_estagio" +
					"    inner join questionario.questionario using (id_questionario)" +
					"    where status in ("+StatusRelatorioEstagio.APROVADO +","+ StatusRelatorioEstagio.PENDENTE +")" +
					"    and id_tipo_questionario = " + TipoQuestionario.RELATORIO_DE_ESTAGIO_SUPERVISOR +
					"    group by id_estagiario) as relatorios_preenchidos using (id_estagiario)" +
					" where e.status in ("+StatusEstagio.APROVADO +","+ StatusEstagio.SOLICITADO_CANCELAMENTO +")" +
					" and coalesce(qtd_preenchido, 0) < ").append(qtdRelatorios);
			// consulta de pendências do orientador
			sql.append(" union ")
			.append("select e.id_estagiario, e.data_inicio, e.data_fim, d.matricula, p.nome as discente, "+
			" c.nome as curso, po.nome, po.email, te.descricao as tipo_estagio, ce.numero_convenio, " +
			" ppe.nome as concedente, ppe.cpf_cnpj, 'D' as tipo, coalesce(qtd_preenchido, 0) as qtd_preenchido,");
			sql.append(qtdRelatorios).append(" as qtd_esperada")
			.append(" from estagio.estagiario e" +
					" inner join discente d on (d.id_discente = e.id_discente)" +
					" inner join comum.pessoa p on (p.id_pessoa = d.id_pessoa)" +
					" inner join rh.servidor s using (id_servidor)" +
					" inner join comum.pessoa po on (po.id_pessoa = s.id_pessoa)" +
					" inner join public.curso c on (c.id_curso = d.id_curso)" +
					" inner join estagio.tipo_estagio te on (te.id_tipo_estagio = e.id_tipo_estagio)" +
					" inner join estagio.concedente_estagio pe on (pe.id_concedente_estagio = e.id_concedente_estagio)" +
					" inner join estagio.convenio_estagio ce on (ce.id_convenio_estagio = pe.id_convenio_estagio)" +
					" inner join comum.pessoa ppe on (ppe.id_pessoa = pe.id_pessoa)" +
					" left join (" +
					"    select id_estagiario, count(*) as qtd_preenchido" +
					"    from estagio.relatorio_estagio" +
					"    inner join questionario.questionario using (id_questionario)" +
					"    where status in ("+StatusRelatorioEstagio.APROVADO +","+ StatusRelatorioEstagio.PENDENTE +")" +
					"    and id_tipo_questionario = " + TipoQuestionario.RELATORIO_DE_ORIENTADOR_DE_ESTAGIO +
					"    group by id_estagiario) as relatorios_preenchidos using (id_estagiario)" +
					" where e.status in ("+StatusEstagio.APROVADO +","+ StatusEstagio.SOLICITADO_CANCELAMENTO +")" +
					" and coalesce(qtd_preenchido, 0) < ").append(qtdRelatorios);
		}
				
		List<Map<String, Object>> lista = getJdbcTemplate().queryForList(sql.toString());	
		return lista;
	}	
	
	/**
	 * Retorna os Status de Estágio que permitem realizar parecer
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<StatusEstagio> findAllStatusParecer() throws HibernateException, DAOException{
		String hql = "from StatusEstagio s where s.permiteParecer = trueValue() ";
		@SuppressWarnings("unchecked")
		Collection<StatusEstagio> lista = getSession().createQuery(hql).list(); 
		return lista;
	}	
	
	/**
	 * Retorna a Quantidade de Estágios Ativos do Supervisor informado 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public int countEstagiosBySupervisor(Pessoa p) throws HibernateException, DAOException{
		StringBuilder hql = new StringBuilder();
		hql.append("select count(e.id) from Estagiario e ");
		hql.append(" where e.supervisor.id = :idSupervisor");
		
		hql.append(" and e.dataInicio >= :data ");
		hql.append(" and e.dataFim <= :data ");
		
		hql.append(" and e.status.id in ("+StatusEstagio.APROVADO + "," + StatusEstagio.EM_ANALISE+ "," + StatusEstagio.SOLICITADO_CANCELAMENTO+")");
		
		Query q = getSession().createQuery(hql.toString());
		
		q.setInteger("idSupervisor", p.getId());
		q.setDate("data", new Date());
		
		return ((Long) q.setMaxResults(1).uniqueResult()).intValue();
	}		
}
