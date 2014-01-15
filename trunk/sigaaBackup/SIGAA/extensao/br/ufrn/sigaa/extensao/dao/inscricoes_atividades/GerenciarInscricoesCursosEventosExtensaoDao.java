/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 06/11/2012
 * 
 */
package br.ufrn.sigaa.extensao.dao.inscricoes_atividades;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.CursoEventoExtensao;
import br.ufrn.sigaa.extensao.dominio.StatusInscricaoParticipante;
import br.ufrn.sigaa.extensao.dominio.SubAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoSubAtividadeExtensao;
import br.ufrn.sigaa.extensao.jsf.helper.DesignacaoFuncaoProjetoHelper;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 *
 * <p>Dao para as consultas na parte de gerenciamentos da inscrições pelo coordenador </p>
 *
 * <p> <i> </i> </p>
 * 
 * @author jadson
 *
 */
public class GerenciarInscricoesCursosEventosExtensaoDao extends GenericSigaaDAO{

	/** A projeção usado nas casos de uso de cadastro e alteração dos períodos de inscrição*/
	public static final  String PROJECAO_PADRAO_PERIODO_INSCRICAO = 
			" atividadeExtensao.id_atividade as idAtividade, atividadeExtensao.sequencia, " +
			" tipoAtividade.descricao as descricaoAtividade," +
			" projeto.id_projeto as idProjeto, projeto.ano as anoAtividade, projeto.titulo as tituloProjeto, projeto.data_inicio, projeto.data_fim," +
			" cursoEvento.id_curso_evento as idCurso, cursoEvento.numero_vagas as numeroVagasCurso, "+
			// Conta a quantidade de inscrições ABERTAS efetivamente para essa atividade  //
			"( SELECT SUM( inscricaoAtividade.quantidade_vagas ) " +
			" FROM extensao.inscricao_atividade inscricaoAtividade "+
			" WHERE inscricaoAtividade.id_atividade = atividadeExtensao.id_atividade " +
			" AND inscricaoAtividade.ativo = :true ) as vagasAbertasCurso, "+
			// Conta a quantidade de inscrições APROVADAS para essa atividade  //
			"( SELECT COUNT( DISTINCT inscricaoParticipante.id_inscricao_atividade_participante ) " +
			" FROM extensao.inscricao_atividade_participante inscricaoParticipante 	" +
			" INNER JOIN extensao.inscricao_atividade inscricaoAtividade                ON  inscricaoAtividade.id_inscricao_atividade = inscricaoParticipante.id_inscricao_atividade " +
			" WHERE inscricaoAtividade.id_atividade = atividadeExtensao.id_atividade " +
			" AND inscricaoParticipante.id_status_inscricao_participante = :idStatus) as numeroInscritosCurso, "+
			// informacoes das sub atividades
		 	" subAtividadesExtensao.id_sub_atividade_extensao as idSubAtividade, subAtividadesExtensao.titulo as tituloMIniAtividade, tipoSubAtividade.descricao as descricaoTipoSubAtividade, subAtividadesExtensao.ativo, "+
		 	// Conta a quantidade de inscrições na sub atividade ABERTAS para essa Sub Atividade  //
			"(SELECT SUM( inscricaoAtividadeSub.quantidade_vagas ) " +
			" FROM extensao.inscricao_atividade inscricaoAtividadeSub  " +
			" WHERE inscricaoAtividadeSub.id_sub_atividade_extensao = subAtividadesExtensao.id_sub_atividade_extensao " +
			" AND inscricaoAtividadeSub.ativo = :true ) as vagasAbertasSubAtividade, "+
			// Conta a quantidade de inscrições na sub atividade APROVADOS para essa Sub Atividade  //
			"(SELECT COUNT( DISTINCT inscricaoParticipanteSub.id_inscricao_atividade_participante ) " +
			" FROM extensao.inscricao_atividade_participante inscricaoParticipanteSub 	" +
			" INNER JOIN extensao.inscricao_atividade inscricaoAtividadeSub       ON inscricaoAtividadeSub.id_inscricao_atividade = inscricaoParticipanteSub.id_inscricao_atividade " +
			" WHERE inscricaoAtividadeSub.id_sub_atividade_extensao = subAtividadesExtensao.id_sub_atividade_extensao " +
			" AND inscricaoParticipanteSub.id_status_inscricao_participante = :idStatus) as numeroInscritosSubAtividade ";
	
	/** OS JOIN padrão usado nas casos de uso de cadastro e alteração dos períodos de inscrição*/
	public static final String JOIN_PADRAO_PERIODO_INSCRICAO = 
			" INNER JOIN projetos.projeto projeto                     ON atividadeExtensao.id_projeto = projeto.id_projeto "+
	" INNER JOIN projetos.membro_projeto coordenador          ON coordenador.id_membro_projeto = projeto.id_coordenador "+
	" INNER JOIN projetos.tipo_projeto tipoProjeto            ON tipoProjeto.id_tipo_projeto = projeto.id_tipo_projeto "+
	" INNER JOIN extensao.curso_evento cursoEvento            ON cursoEvento.id_curso_evento = atividadeExtensao.id_curso_evento "+
	" INNER JOIN extensao.tipo_atividade_extensao tipoAtividade   ON tipoAtividade.id_tipo_atividade_extensao = atividadeExtensao.id_tipo_atividade_extensao "+
	" INNER JOIN comum.pessoa pessoa                              ON coordenador.id_pessoa = pessoa.id_pessoa "+
	" LEFT JOIN extensao.sub_atividade_extensao subAtividadesExtensao ON subAtividadesExtensao.id_atividade = atividadeExtensao.id_atividade"+
	" LEFT JOIN extensao.tipo_sub_atividade_extensao tipoSubAtividade ON subAtividadesExtensao.id_tipo_sub_atividade_extensao = tipoSubAtividade.id_tipo_sub_atividade_extensao";
	
	
	/**
	 * <p>Retorna todas as atividades de extensão do tipo Cursos e Eventos nas quais o docente seja coordenador.<p>
	 * 
	 * <p>Caso a atividade possua mini atividades é retornado aqui também.</p> 
	 * 
	 * <p> <strong>CONSULTA OTIMIZADA PARA DEMORAR < 1s </strong></p>
	 * 
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<AtividadeExtensao> findCursoEventoAtivosEmExecucaoByCoordenador(Servidor servidor) throws DAOException {
		
		long tempo  = System.currentTimeMillis();
	
		try {
			
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT " + PROJECAO_PADRAO_PERIODO_INSCRICAO + " FROM extensao.atividade atividadeExtensao ");
			sql.append(JOIN_PADRAO_PERIODO_INSCRICAO);

			if ( DesignacaoFuncaoProjetoHelper.
					projetosByCoordenadoresOrDesignacaoCoordenadorOrDesignacaoGerenciaParticipante(servidor.getId()).size() > 0 ) {
				sql.append(" WHERE coordenador.ativo = :true AND ( pessoa.id_pessoa = :idPessoa OR projeto.id_projeto in ( :idProjetos ) ) ");
			} else {
				sql.append(" WHERE coordenador.ativo = :true AND pessoa.id_pessoa = :idPessoa  ");
			}
			
			sql.append(" AND atividadeExtensao.ativo = :true AND projeto.ativo = :true "); // Sempre usar a consulta mais restritiva possível para evitar erros;
			sql.append(" AND tipoProjeto.id_tipo_projeto = :idTipoProjeto ");
			sql.append(" AND projeto.id_tipo_situacao_projeto  IN (:idSituacaoExecucao) ");
			sql.append(" AND cursoEvento.id_curso_evento IS NOT NULL ");
			sql.append(" ORDER BY projeto.ano, projeto.titulo ");			
			
			Query query = getSession().createSQLQuery(sql.toString());
			query.setInteger("idPessoa", servidor.getPessoa().getId());
			query.setBoolean("true", true);
			query.setInteger("idTipoProjeto", TipoProjeto.EXTENSAO);
			query.setParameterList("idSituacaoExecucao", TipoSituacaoProjeto.PROJETOS_GRUPO_EM_EXECUCAO);
			query.setInteger("idStatus", StatusInscricaoParticipante.APROVADO);
			if ( DesignacaoFuncaoProjetoHelper.
					projetosByCoordenadoresOrDesignacaoCoordenadorOrDesignacaoGerenciaParticipante(servidor.getId()).size() > 0 )
				query.setParameterList("idProjetos", DesignacaoFuncaoProjetoHelper.
						projetosByCoordenadoresOrDesignacaoCoordenadorOrDesignacaoGerenciaParticipante(servidor.getId()));
			
			List<Object> lista = query.list();
			
			ArrayList<AtividadeExtensao> result = montaInformacoesPeriodoInscricao(lista);
			
			System.out.println(" >>>>>>>>>>>>> Consulta demorou: " + (System.currentTimeMillis()-tempo)+ " ms  <<<<<<<<<<<<<<<<");
			
			return result;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
		
	}

	
	
	
	/**
	 * <p>Retorna todas as atividades de extensão do tipo Cursos e Eventos do projeto de extensão passado.<p>
	 * 
	 * <p>Caso a atividade possua mini atividades é retornado aqui também.</p> 
	 * 
	 * <p> <strong>CONSULTA OTIMIZADA PARA DEMORAR < 1s </strong></p>
	 * 
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<AtividadeExtensao> findCursoEventoAtivosEmExecucaoByProjetoExtensao(int idProjeto) throws DAOException {
		
		long tempo  = System.currentTimeMillis();
	
		try {
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT " + PROJECAO_PADRAO_PERIODO_INSCRICAO + " FROM extensao.atividade atividadeExtensao ");
			sql.append(JOIN_PADRAO_PERIODO_INSCRICAO);
			sql.append(" WHERE projeto.id_projeto = :idProjeto AND coordenador.ativo = :true ");
			sql.append(" AND atividadeExtensao.ativo = :true AND projeto.ativo = :true "); // Sempre usar a consulta mais restritiva possível para evitar erros;
			sql.append(" AND tipoProjeto.id_tipo_projeto = :idTipoProjeto ");
			sql.append(" AND projeto.id_tipo_situacao_projeto  IN (:idSituacaoExecucao) ");
			sql.append(" AND cursoEvento.id_curso_evento IS NOT NULL ");
			sql.append(" ORDER BY projeto.ano, projeto.titulo ");			
			
			Query query = getSession().createSQLQuery(sql.toString());
			query.setInteger("idProjeto", idProjeto);
			query.setBoolean("true", true);
			query.setInteger("idTipoProjeto", TipoProjeto.EXTENSAO);
			query.setParameterList("idSituacaoExecucao", TipoSituacaoProjeto.PROJETOS_GRUPO_EM_EXECUCAO);
			query.setInteger("idStatus", StatusInscricaoParticipante.APROVADO);
			
			List<Object> lista = query.list();
			
			ArrayList<AtividadeExtensao> result = montaInformacoesPeriodoInscricao(lista);
			
			System.out.println(" >>>>>>>>>>>>> Consulta demorou: " + (System.currentTimeMillis()-tempo)+ " ms  <<<<<<<<<<<<<<<<");
			
			return result;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
		
	}


	
	


	private ArrayList<AtividadeExtensao> montaInformacoesPeriodoInscricao(List<Object> lista) { 
		
		ArrayList<AtividadeExtensao> result = new ArrayList<AtividadeExtensao>();
		
		for (int a = 0; a < lista.size(); a++) {
			
			Object[] colunas = (Object[]) lista.get(a);
			AtividadeExtensao at = new AtividadeExtensao();
			at.setId((Integer) colunas[0]);
			at.setSequencia((Integer) colunas[1]);
			at.setTipoAtividadeExtensao( new TipoAtividadeExtensao(0, (String) colunas[2]) );
			at.setProjeto(new Projeto((Integer) colunas[3]));
			at.setAno( ((Short) colunas[4]).intValue());
			at.setTitulo((String) colunas[5]);
			
			// as datas do projeto são importantes para validação do período de inscrição //
			at.setDataInicio((Date) colunas[6]);
			at.setDataFim((Date) colunas[7]);
			////////////////////////////////////////////////////////////////////////////////
			
			at.setCursoEventoExtensao(new CursoEventoExtensao((Integer) colunas[8]));
			at.getCursoEventoExtensao().setNumeroVagas((Integer) colunas[9]);
			at.setNumeroVagasAbertas( colunas[10] == null ? 0 : ((BigInteger) colunas[10]).intValue());
			at.setNumeroInscritos( colunas[11] == null ? 0 : ((BigInteger) colunas[11]).intValue());
			
			if(! result.contains(at))
				result.add(at);
			else
				at = result.get(result.indexOf(at));
			
			// Se tem sub atividade e está ativa ///
			if(colunas[12] != null && (Boolean) colunas[15] ){
				SubAtividadeExtensao sub = new SubAtividadeExtensao();
				sub.setId((Integer) colunas[12]);
				sub.setTitulo((String) colunas[13]);
				sub.setTipoSubAtividadeExtensao(new TipoSubAtividadeExtensao((String) colunas[14]));
				sub.setAtivo( (Boolean) colunas[15]  );
				sub.setNumeroVagasAbertas( colunas[16] == null ? 0 : ((BigInteger) colunas[16]).intValue());
				sub.setNumeroInscritos( colunas[17] == null ? 0 :  ((BigInteger)colunas[17]).intValue() );
				at.addSubAtividade(sub);
			}
			
		}
		return result;
	}
}
