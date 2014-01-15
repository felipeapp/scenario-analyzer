/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 24/02/2010
 *
 */
package br.ufrn.sigaa.ensino.stricto.reuni.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.AreaConhecimentoCienciasTecnologia;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.PlanoDocenciaAssistida;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.reuni.dominio.EditalBolsasReuni;
import br.ufrn.sigaa.ensino.stricto.reuni.dominio.IndicacaoBolsistaReuni;
import br.ufrn.sigaa.ensino.stricto.reuni.dominio.PeriodoIndicacaoReuni;
import br.ufrn.sigaa.ensino.stricto.reuni.dominio.PlanoTrabalhoReuni;
import br.ufrn.sigaa.ensino.stricto.reuni.dominio.SolicitacaoBolsasReuni;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO para consultas relacionadas a indicação de bolsas REUNI
 * 
 * @author Arlindo Rodrigues
 *
 */
public class IndicacaoBolsistaReuniDao extends GenericSigaaDAO  {
	
	/**
	 * Verifica se o discente passado possui alguma indicação ativa.
	 * @param discente
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public IndicacaoBolsistaReuni findAtivoByDiscentePeriodo(DiscenteAdapter discente, int ano, int periodo) throws DAOException {
		
		String hql = " select i from PeriodoIndicacaoReuni p "+
					 " INNER JOIN p.indicacaoBolsistaReuni i "+
		             " where i.discente.id = :discente and i.ativo = trueValue() ";
		
		if (ano > 0 && periodo > 0){
			hql += " and p.anoPeriodo = "+String.valueOf(ano)+String.valueOf(periodo);
		}
		
		return (IndicacaoBolsistaReuni) getSession().createQuery(hql)
			   .setParameter("discente", discente.getId())
			   .setMaxResults(1)
			   .uniqueResult();
	}	
	
	/**
	 * Retorna uma lista de indicações ativas do discente e/ou plano de trabalho passado(s) como parâmetro.
	 * @param discente
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public Collection<IndicacaoBolsistaReuni> findIndicacoesByDiscentePeriodo(int idDiscente, int idPlanoTrabalho) throws DAOException {	
		String sql = 
		"	select i.id_indicacao_bolsista_reuni, i.id_plano_trabalho_reuni, i.id_discente, d.matricula as matricula_i, p.id_pessoa, p.nome as discente, "
		+"       i.ativo as ativo_i, s.id_solicitacao_bolsas_reuni, e.id_edital_bolsas_reuni, e.descricao as edital,        "
		+"       ac.denominacao, pt.nivel as nivel_pt, pt.status as status_pt, pt.linha_acao, cc.codigo, cd.nome as disciplina, "
		+"       pi.id_periodo_indicacao_reuni, pi.ano_periodo, pd.id_plano_docencia_assistida, pd.ativo as ativo_pd, pd.status as status_pd, "
		+"       dpd.id_discente as id_discente_pd, dpd.matricula as matricula_pd, ppd.id_pessoa as id_pessoa_pd, ppd.nome as discente_pd, "
		+"       pd.id_arquivo "
		+" from stricto_sensu.indicacao_bolsista_reuni i " 
		+" inner join stricto_sensu.periodo_indicacao_reuni pi using (id_indicacao_bolsista_reuni) " 
		+" left join stricto_sensu.plano_docencia_assistida pd using (id_periodo_indicacao_reuni) "
		+" inner join stricto_sensu.plano_trabalho_reuni pt using (id_plano_trabalho_reuni) "
		+" inner join stricto_sensu.solicitacao_bolsas_reuni s using (id_solicitacao_bolsas_reuni) "
		+" inner join stricto_sensu.edital_bolsas_reuni e using (id_edital_bolsas_reuni) "
		+" left join public.discente d on (d.id_discente = i.id_discente) "
		+" left join comum.pessoa p on (p.id_pessoa = d.id_pessoa) "		
		+" left join public.discente dpd on (dpd.id_discente = pd.id_discente) "
		+" left join comum.pessoa ppd on (ppd.id_pessoa = dpd.id_pessoa) "		
		+" left join ensino.componente_curricular cc on (cc.id_disciplina = pt.id_componente_curricular) "
		+" left join ensino.componente_curricular_detalhes cd on (cd.id_componente_detalhes = cc.id_detalhe) "
		+" left join graduacao.area_conhecimento_ciencias_tecnologia ac on (ac.id_area_conhecimento_ciencias_tecnologia = "
		+" 													pt.id_area_conhecimento_ciencias_tecnologia) "
		+" where 1 = 1 ";
		
		if (idDiscente > 0)
			sql += " and i.id_discente = "+idDiscente;
		
		if (idPlanoTrabalho > 0)
			sql += " and i.id_plano_trabalho_reuni = "+idPlanoTrabalho;
		
		sql += " order by i.id_plano_trabalho_reuni, pi.ano_periodo desc ";
		
		Query q = getSession().createSQLQuery(sql);
		
		List<IndicacaoBolsistaReuni> resultado = new ArrayList<IndicacaoBolsistaReuni>();
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		for (Object obj[] : lista) {		
			IndicacaoBolsistaReuni indicacao = new IndicacaoBolsistaReuni();
			indicacao.setId((Integer) obj[0]);
			
			if (resultado.contains(indicacao)) {
				indicacao = resultado.get(resultado.indexOf(indicacao));
			} else {			
				indicacao.setPlanoTrabalho(new PlanoTrabalhoReuni());
				indicacao.getPlanoTrabalho().setId((Integer) obj[1]);
				indicacao.setDiscente(new DiscenteStricto());
				indicacao.getDiscente().setDiscente(new Discente());
				indicacao.getDiscente().setId((Integer) obj[2]);
				indicacao.getDiscente().setMatricula( ((Number) obj[3]).longValue() ); 
				indicacao.getDiscente().setPessoa(new Pessoa());
				indicacao.getDiscente().getPessoa().setId((Integer) obj[4]);
				indicacao.getDiscente().getPessoa().setNome((String) obj[5]);
				indicacao.setAtivo((Boolean) obj[6]);
				indicacao.getPlanoTrabalho().setSolicitacao(new SolicitacaoBolsasReuni((Integer) obj[7]));
				indicacao.getPlanoTrabalho().getSolicitacao().setEdital(new EditalBolsasReuni());
				indicacao.getPlanoTrabalho().getSolicitacao().getEdital().setId((Integer) obj[8]);
				indicacao.getPlanoTrabalho().getSolicitacao().getEdital().setDescricao((String) obj[9]);
				indicacao.getPlanoTrabalho().setAreaConhecimento(new AreaConhecimentoCienciasTecnologia());
				indicacao.getPlanoTrabalho().getAreaConhecimento().setDenominacao((String) obj[10]);
				indicacao.getPlanoTrabalho().setNivel((Character) obj[11]);
				indicacao.getPlanoTrabalho().setStatus((Integer) obj[12]);
				indicacao.getPlanoTrabalho().setLinhaAcao((Integer) obj[13]);
				indicacao.getPlanoTrabalho().setComponenteCurricular(new ComponenteCurricular());
				indicacao.getPlanoTrabalho().getComponenteCurricular().setCodigo((String) obj[14]);
				indicacao.getPlanoTrabalho().getComponenteCurricular().setDetalhes(new ComponenteDetalhes());
				indicacao.getPlanoTrabalho().getComponenteCurricular().getDetalhes().setNome((String) obj[15]);
			}
				
			PeriodoIndicacaoReuni periodoIndicacao = new PeriodoIndicacaoReuni();
			periodoIndicacao.setId((Integer) obj[16]);
			
			if (indicacao.getPeriodosIndicacao() != null){
				if (indicacao.getPeriodosIndicacao().contains(periodoIndicacao)){
					periodoIndicacao = indicacao.getPeriodosIndicacao().get(indicacao.getPeriodosIndicacao().indexOf(periodoIndicacao));
				}				
			} else {
				indicacao.setPeriodosIndicacao(new ArrayList<PeriodoIndicacaoReuni>());
			}
			
			periodoIndicacao.setAnoPeriodo((Integer) obj[17]);
			periodoIndicacao.setIndicacaoBolsistaReuni(indicacao);
			
			if (periodoIndicacao.getPlanoDocenciaAssistida() == null)
				periodoIndicacao.setPlanoDocenciaAssistida(new ArrayList<PlanoDocenciaAssistida>());
												
			if (obj[18] != null){
				PlanoDocenciaAssistida planoDocencia = new PlanoDocenciaAssistida();
				planoDocencia.setId((Integer) obj[18]);
				planoDocencia.setAtivo((Boolean) obj[19]);
				planoDocencia.setStatus((Integer) obj[20]);				
				
				// discente do plano de docência
				if (obj[21] != null){
					planoDocencia.setDiscente(new DiscenteStricto());
					planoDocencia.getDiscente().setDiscente(new Discente());
					planoDocencia.getDiscente().setId((Integer) obj[21]);
					planoDocencia.getDiscente().setMatricula( ((Number) obj[22]).longValue() ); 
					planoDocencia.getDiscente().setPessoa(new Pessoa());
					planoDocencia.getDiscente().getPessoa().setId((Integer) obj[23]);
					planoDocencia.getDiscente().getPessoa().setNome((String) obj[24]);									
				}
				
				if (obj[25] != null)
					planoDocencia.setIdArquivo((Integer) obj[25]);
				
				planoDocencia.setPeriodoIndicacaoBolsa(periodoIndicacao);
				periodoIndicacao.getPlanoDocenciaAssistida().add(planoDocencia);							
			} 
			if (!indicacao.getPeriodosIndicacao().contains(periodoIndicacao))
				indicacao.getPeriodosIndicacao().add(periodoIndicacao);	
			if (!resultado.contains(indicacao))
				resultado.add(indicacao);
		}		
				
		return resultado;
	}		
		
	/**
	 * Verifica se o Plano de Trabalho passado possui alguma indicação ativa.
	 * @param discente
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public IndicacaoBolsistaReuni findByPlanoTrabalho(PlanoTrabalhoReuni planoTrabalho) throws DAOException {
		return (IndicacaoBolsistaReuni) getSession()
			.createQuery("from IndicacaoBolsistaReuni where planoTrabalho = :planoTrabalho and ativo = trueValue() ")
			.setParameter("planoTrabalho", planoTrabalho)
			.uniqueResult();
	}	
	
	/**
	 * Inativa os planos de docência assistida referente a indicação passada como parâmetro.
	 * @param indicacao
	 * @throws DAOException
	 */
	public void inativarPlanoDocenciaAssistida(IndicacaoBolsistaReuni indicacao) throws DAOException {
		String sql =
		 "	update stricto_sensu.plano_docencia_assistida set "+
		 "	ativo = false "+
		 "	where id_periodo_indicacao_reuni in "+ 
		 "			(select id_periodo_indicacao_reuni "+ 
		 "			from stricto_sensu.indicacao_bolsista_reuni i "+
		 "			inner join stricto_sensu.periodo_indicacao_reuni p using (id_indicacao_bolsista_reuni) "+
		 "			where id_indicacao_bolsista_reuni = "+indicacao.getId()+") ";				
		update(sql);
	}
	
	/**
	 * Remove as permissões da turma a qual foi liberar para o discente informado no período da indicação
	 * @param discente
	 * @param periodo
	 */
	public void removerPermissaoTurma(DiscenteStricto discente, Integer ano, Integer periodo, boolean comIndicacao){
		String sql = 
		" delete from ava.permissao_ava "+
			" where id_turma in ( "+
			" 				select id_turma from stricto_sensu.turma_docencia_assistida "+  
			" 				where id_plano_docencia_assistida in ( "+
			" 				select id_plano_docencia_assistida from stricto_sensu.plano_docencia_assistida pd "+ 
			" 				where id_discente = "+discente.getId()+			
			(ano != null && ano > 0 ? "	and ano = "+ano : "")+
			(periodo != null && periodo > 0 ? "	and periodo = "+periodo : "")+	
			(comIndicacao ? " and id_periodo_indicacao_reuni is not null ": "")+
			")) and id_pessoa = "+discente.getPessoa().getId();				
		update(sql);
	}
	
}
