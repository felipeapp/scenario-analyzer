/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/10/2012
 *
 */
package br.ufrn.sigaa.ensino.tecnico.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.tecnico.dominio.ConvocacaoProcessoSeletivoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.OpcaoPoloGrupo;
import br.ufrn.sigaa.ensino.tecnico.dominio.ProcessoSeletivoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.ResultadoClassificacaoCandidatoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.SituacaoCandidatoTecnico;
import br.ufrn.sigaa.parametros.dominio.ParametrosTecnico;
/**
 * Dao com consultas utilizadas na convocação dos candidatos para as vagas remanescentes.
 * 
 * @author Leonardo Campos
 * 
 */
public class ConvocacaoProcessoSeletivoTecnicoDao extends GenericSigaaDAO {

	/**
	 * Retorna os processos seletivos.
	 * @return
	 * @throws DAOException
	 */
	public List<ProcessoSeletivoTecnico> findProcessosSeletivos() throws DAOException {
		String hql = "SELECT id, nome " +
				" FROM ProcessoSeletivoTecnico" +
				" WHERE anoEntrada > 0" +
				" ORDER BY anoEntrada DESC";
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = getSession().createQuery(hql).list();
		
		List<ProcessoSeletivoTecnico> result = null;
		for (int a = 0; a < lista.size(); a++) {
			
			if (result == null){
				result = new ArrayList<ProcessoSeletivoTecnico>();
			}
			
			int col = 0;
			
			Object[] colunas = lista.get(a);
			ProcessoSeletivoTecnico psv = new ProcessoSeletivoTecnico((Integer) colunas[col++]);
			psv.setNome((String) colunas[col++]);
			
			result.add(psv);
		}
		
		return result;
	}
	
	public List <ResultadoClassificacaoCandidatoTecnico> findSuplentesByPoloGrupo (ProcessoSeletivoTecnico psT, OpcaoPoloGrupo opcao) throws DAOException {
		
		int[] situacoes = new int[]{SituacaoCandidatoTecnico.SUPLENTE.getId(), SituacaoCandidatoTecnico.APROVADO.getId()};
		
		String sql =  "select rcc.id_resultado_classificacao_candidato_tecnico, rcc.classificacao_aprovado, rcc.id_situacao_candidato," +
		" iv.id_inscricao_processo_seletivo_tecnico, iv.numero_inscricao, pv.id_pessoa, pv.cpf_cnpj, pv.nome, c.id_curso, c.nome as nomeCurso, mun.nome as nomeMunicipio, " +
		" aux.id_convocacao_processo_seletivo_discente, aux.id_discente_graduacao, aux.matricula, aux.id_matriz_curricular as id_matriz_ant," +
		" aux.id_curso as id_curso_ant, aux.nomeCurso as nome_curso_ant, aux.id_turno as id_turno_ant, aux.sigla as sigla_ant, aux.id_habilitacao as id_habilitacao_ant, aux.nomeHabilitacao as nome_hab_ant, aux.id_grau_academico as id_grau_academico_ant, aux.descricao as desc_ant, aux.status, aux.periodo_ingresso," +
		" iv.id_processo_seletivo" +
		" from tecnico.resultado_classificacao_candidato_tecnico roc" +
		"	join tecnico.inscricao_processo_seletivo_tecnico iv using (id_inscricao_processo_seletivo_tecnico)" +
		"	join tecnico.pessoa_tecnico pv on iv.id_pessoa_tecnico = pv.id_pessoa" +
		"	join discente d2 on d.id_pessoa = pv.id_pessoa" +
		"	join curso c on (c.id_curso=d.id_curso)" +
		"	join comum.municipio mun on (mun.id_municipio=c.id_municipio)"+	
		"	left join (" +
		"		select cd.id_inscricao_vestibular, cd.id_convocacao_processo_seletivo_discente, d.id_discente, d.matricula," +
		"			c2.id_curso, c2.nome as nomeCurso, d.status, d.periodo_ingresso, max(cp.data_convocacao) " +
		"			from tecnico.convocacao_processo_seletivo_discente_tecnico cd" +
		"			join tecnico.convocacao_processo_seletivo_tecnico cp using (id_convocacao_processo_seletivo)" +
		"			join discente d on (d.id_discente_graduacao=cd.id_discente)" +
		"			join curso c2 on (c2.id_curso=d.id_curso)" +
		"		where cp.id_processo_seletivo = :idProcessoSeletivo" +
		"		group by cd.id_inscricao_vestibular, cd.id_convocacao_processo_seletivo_discente, d.id_discente, d.matricula," +
		"			c2.nome, m.id_turno, t2.sigla, m.id_habilitacao, h2.nome, m.id_grau_academico, g2.descricao, d.status, d.periodo_ingresso" +
		"	) aux on (aux.id_inscricao_vestibular=rcc.id_inscricao_vestibular)"+	
		" where roc.ordem_opcao = :opcao " +
		" and rcc.aprovado_ama is false "+
		" and rcc.id_situacao_candidato in " + UFRNUtils.gerarStringIn(situacoes) +
		" and iv.id_processo_seletivo = :idProcessoSeletivo" +
		" and rcc.id_inscricao_vestibular not in" +
		" (	" +
		"	select c.id_inscricao_vestibular" +
		"	from vestibular.convocacao_processo_seletivo_discente c" +
		"	join vestibular.convocacao_processo_seletivo cd using (id_convocacao_processo_seletivo)" +
		"	and cd.id_processo_seletivo = :idProcessoSeletivo" +
		" ) " +
		" order by roc.id_matriz_curricular, roc.ordem_opcao, roc.classificacao, roc.argumento_final desc";
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = getSession().createSQLQuery(sql).setInteger("idProcessoSeletivo", psT.getId()).list();
		
		return null;
	}
	
	/**
	 * Procurar Convocação de Processo Seletivo por descrição.
	 * @param psTecnico
	 * @param descricao
	 * @return
	 * @throws DAOException
	 */
	public ConvocacaoProcessoSeletivoTecnico findByProcessoSeletivo (ProcessoSeletivoTecnico psTecnico) throws DAOException {
		
			Criteria c = getSession().createCriteria(ConvocacaoProcessoSeletivoTecnico.class);
			c.add(Restrictions.eq("processoSeletivo.id", psTecnico.getId()));
			c.addOrder(Order.asc("dataConvocacao"));
			return (ConvocacaoProcessoSeletivoTecnico) c.uniqueResult();
		
	}
	
	/**
	 * Retorna um mapa com o id da matriz curricular e seu respectivo número de vagas ociosas.
	 * @param processoSeletivo
	 * @return
	 * @throws DAOException
	 */
	public int findVagasOciosas(ProcessoSeletivoTecnico processoSeletivo) throws DAOException {
		
		Collection<Integer> statusQueOcupamVaga = StatusDiscente.getStatusComVinculo();
		statusQueOcupamVaga.add(StatusDiscente.PENDENTE_CADASTRO);
		
		String sql = "select cast((quantidade_vagas - coalesce(preenchidas.total, 0)) as integer) as ociosas " + 
					" from tecnico.oferta_vagas_tecnico o " + 
					" join tecnico.processo_seletivo_tecnico ps on (o.id_processo_seletivo = ps.id_processo_seletivo_tecnico) " + 
					" left join (select d.id_forma_ingresso, count(*) as total " + 
					" 	from discente d  " + 
					" 	where d.ano_ingresso = " + processoSeletivo.getAnoEntrada() + " " + 
					" 	and d.status in " + gerarStringIn(statusQueOcupamVaga) +" " + 
					" 	and d.id_curso = " + ParametroHelper.getInstance().getParametro(ParametrosTecnico.ID_CURSO_METROPOLE_DIGITAL) + " " + 
					" 	group by d.id_forma_ingresso " + 
					" 	) as preenchidas on preenchidas.id_forma_ingresso = ps.id_forma_ingresso " + 
					" where ps.id_processo_seletivo = :idProcessoSeletivo " + 
					" and quantidade_vagas > 0 " + 
					" and coalesce(o.quantidade_vagas - preenchidas.total, 0) > 0"; 

		SQLQuery q = getSession().createSQLQuery(sql);
		q.setInteger("idProcessoSeletivo", processoSeletivo.getId());
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		Object [] l = lista.get(0);
		
		return (Integer) l[0];
	}
}
