/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 30/11/2012
 * 
 */
package br.ufrn.sigaa.extensao.dao.inscricoes_atividades;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.CursoEventoExtensao;
import br.ufrn.sigaa.extensao.dominio.SubAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoSubAtividadeExtensao;
import br.ufrn.sigaa.extensao.jsf.helper.DesignacaoFuncaoProjetoHelper;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * <p> Dao para consultas exclusivas para o gerenciamento de participantes que envolvem atividades e sub atividades
 * ao mesmo tempo.</p>
 * 
 * @author jadson
 *
 */
public class GerenciarParticipantesCursosEventoExtensaoDao extends GenericSigaaDAO{

	
	/** Projeção padrão da busca de cursos e eventos */
	public static final String PROJECAO_PADRAO = " atividadeExtensao.id_atividade as idAtividade, atividadeExtensao.sequencia, " +
			" tipoAtividade.descricao as descricaoAtividade," +
			" projeto.id_projeto as idProjeto, projeto.ano as anoAtividade, projeto.titulo as tituloProjeto, situacaoPro.descricao, " +
			" cursoEvento.id_curso_evento as idCurso, "
	// Conta a quantidade de participantes paras as  Atividades  //
	 + "( SELECT COUNT( participante.id_participante_acao_extensao ) " +
			" FROM extensao.participante_acao_extensao participante  "+
			" WHERE participante.id_acao_extensao = atividadeExtensao.id_atividade " +
			" AND participante.ativo = :true AND participante.id_cadastro_participante_atividade_extensao IS NOT NULL ) as qtdParticipantesAtividade, "
	 + " subAtividadesExtensao.id_sub_atividade_extensao as idSubAtividade, subAtividadesExtensao.titulo as tituloMiniAtividade, tipoSubAtividade.descricao as descricaoTipoSubAtividade, subAtividadesExtensao.ativo, "
	// Conta a quantidade de participantes paras as Sub Atividades  //
	 + "(SELECT COUNT( participanteSubAtividade.id_participante_acao_extensao ) " +
		" FROM extensao.participante_acao_extensao participanteSubAtividade  " +
		" WHERE participanteSubAtividade.id_sub_atividade_extensao = subAtividadesExtensao.id_sub_atividade_extensao " +
		" AND participanteSubAtividade.ativo = :true AND participanteSubAtividade.id_cadastro_participante_atividade_extensao IS NOT NULL ) as qtdParticipantesSubAtividade ";
	
	
	
	/** JOINS padrão da busca de cursos e eventos */
	public static final String JOINS_PADRAO = " INNER JOIN projetos.projeto projeto                     ON atividadeExtensao.id_projeto = projeto.id_projeto "
			+" INNER JOIN projetos.membro_projeto coordenador          ON coordenador.id_membro_projeto = projeto.id_coordenador "
			+" INNER JOIN projetos.tipo_projeto tipoProjeto            ON tipoProjeto.id_tipo_projeto = projeto.id_tipo_projeto "
			+" INNER JOIN extensao.curso_evento cursoEvento            ON cursoEvento.id_curso_evento = atividadeExtensao.id_curso_evento "
			+" INNER JOIN projetos.tipo_situacao_projeto situacaoPro   ON situacaoPro.id_tipo_situacao_projeto = projeto.id_tipo_situacao_projeto "
			+" INNER JOIN extensao.tipo_atividade_extensao tipoAtividade   ON tipoAtividade.id_tipo_atividade_extensao = atividadeExtensao.id_tipo_atividade_extensao "
			+" INNER JOIN comum.pessoa pessoa                              ON coordenador.id_pessoa = pessoa.id_pessoa "
			+" LEFT JOIN extensao.sub_atividade_extensao subAtividadesExtensao ON subAtividadesExtensao.id_atividade = atividadeExtensao.id_atividade"
			+" LEFT JOIN extensao.tipo_sub_atividade_extensao tipoSubAtividade ON subAtividadesExtensao.id_tipo_sub_atividade_extensao = tipoSubAtividade.id_tipo_sub_atividade_extensao";

	/** JOINS padrão da busca de cursos, eventos, projetos, programas e produtos */
	public static final String JOINS_PADRAO_GLOBAL = " INNER JOIN projetos.projeto projeto                     ON atividadeExtensao.id_projeto = projeto.id_projeto "
			+" INNER JOIN projetos.membro_projeto coordenador          ON coordenador.id_membro_projeto = projeto.id_coordenador "
			+" INNER JOIN projetos.tipo_projeto tipoProjeto            ON tipoProjeto.id_tipo_projeto = projeto.id_tipo_projeto "
			+" LEFT JOIN extensao.curso_evento cursoEvento            ON cursoEvento.id_curso_evento = atividadeExtensao.id_curso_evento "
			+" LEFT JOIN extensao.projeto projetoExtensao ON projetoExtensao.id_projeto_extensao = atividadeExtensao.id_projeto_extensao "  
			+" LEFT JOIN extensao.programa programaExtensao ON programaExtensao.id_programa = atividadeExtensao.id_programa "
			+" LEFT JOIN extensao.produto produtoExtensao ON produtoExtensao.id_produto = atividadeExtensao.id_produto "
			+" INNER JOIN projetos.tipo_situacao_projeto situacaoPro   ON situacaoPro.id_tipo_situacao_projeto = projeto.id_tipo_situacao_projeto "
			+" INNER JOIN extensao.tipo_atividade_extensao tipoAtividade   ON tipoAtividade.id_tipo_atividade_extensao = atividadeExtensao.id_tipo_atividade_extensao "
			+" INNER JOIN comum.pessoa pessoa                              ON coordenador.id_pessoa = pessoa.id_pessoa "
			+" LEFT JOIN extensao.sub_atividade_extensao subAtividadesExtensao ON subAtividadesExtensao.id_atividade = atividadeExtensao.id_atividade"
			+" LEFT JOIN extensao.tipo_sub_atividade_extensao tipoSubAtividade ON subAtividadesExtensao.id_tipo_sub_atividade_extensao = tipoSubAtividade.id_tipo_sub_atividade_extensao";

	
	
	/**
	 * <p>Retorna todas as atividades de extensão do tipo Cursos e Eventos nas quais o docente seja coordenador 
	 *  e que esteja uma uma situção que possa gerenciar os participantes.<p>
	 * 
	 * <p>Diferente dos método de gerenciar inscrições que retorna apenas as atividades que estejam em execução, aqui as atividades 
	 * de projetos concluídos podem aparecer também.</p>
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
	public List<AtividadeExtensao> findCursoEventoAtivosGerenciarParticipantesByCoordenador(int idPessoa) throws DAOException {
		
		long tempo  = System.currentTimeMillis();
	
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT " + PROJECAO_PADRAO + " FROM extensao.atividade atividadeExtensao ");
		sql.append(JOINS_PADRAO);
		
		sql.append(" WHERE pessoa.id_pessoa = :idPessoa AND coordenador.ativo = :true ");
		sql.append(" AND atividadeExtensao.ativo = :true AND projeto.ativo = :true "); // Sempre usar a consulta mais restritiva possível para evitar erros
		sql.append(" AND tipoProjeto.id_tipo_projeto = :idTipoProjeto ");
		sql.append(" AND projeto.id_tipo_situacao_projeto  IN (:idSituacoesProjeto) ");
		sql.append(" AND cursoEvento.id_curso_evento IS NOT NULL ");
		sql.append(" ORDER BY projeto.ano, projeto.titulo ");			
		
		Query query = getSession().createSQLQuery(sql.toString());
		query.setInteger("idPessoa", idPessoa);
		query.setBoolean("true", true);
		query.setInteger("idTipoProjeto", TipoProjeto.EXTENSAO);
		
		// APENAS PROJETOS DO GRUPO EM EXECUÇÃO OU CONCLUÍDOS PODEM TER PARTICIPANTES GERENCIADOS PELO COORDENADOR //
		List<Integer> situacoes = new ArrayList<Integer>(); 
		situacoes.addAll(Arrays.asList(TipoSituacaoProjeto.PROJETOS_GRUPO_CONCLUIDO ));
		situacoes.addAll(Arrays.asList(TipoSituacaoProjeto.PROJETOS_GRUPO_EM_EXECUCAO ));
		
		query.setParameterList("idSituacoesProjeto", situacoes);
		
		
		List<Object> lista = query.list();
		List<AtividadeExtensao> result = montaInformacoesCursosEventosExtensao(lista);
		
		System.out.println(" >>>>>>>>>>>>> Consulta demorou: " + (System.currentTimeMillis()-tempo)+ " ms  <<<<<<<<<<<<<<<<");
		
		return result;
		
	}

	
	
	
	/**
	 * <p>Retorna todas as atividades de extensão do tipo Cursos e Eventos nas quais o docente seja coordenador 
	 *  e que esteja uma uma situção que possa gerenciar os participantes.<p>
	 * 
	 * <p>Diferente dos método de gerenciar inscrições que retorna apenas as atividades que estejam em execução, aqui as atividades 
	 * de projetos concluídos podem aparecer também.</p>
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
	public List<AtividadeExtensao> findCursoEventoAtivosGerenciarParticipantesByProjeto(int idProjeto) throws DAOException {
		
		long tempo  = System.currentTimeMillis();
	
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT " + PROJECAO_PADRAO + " FROM extensao.atividade atividadeExtensao ");
		sql.append(JOINS_PADRAO);
		
		sql.append(" WHERE projeto.id_projeto = :idProjeto ");
		sql.append(" AND coordenador.ativo = :true ");
		sql.append(" AND atividadeExtensao.ativo = :true AND projeto.ativo = :true "); // Sempre usar a consulta mais restritiva possível para evitar erros
		sql.append(" AND tipoProjeto.id_tipo_projeto = :idTipoProjeto ");
		sql.append(" AND projeto.id_tipo_situacao_projeto  IN (:idSituacoesProjeto) ");
		sql.append(" AND cursoEvento.id_curso_evento IS NOT NULL ");
		sql.append(" ORDER BY projeto.ano, projeto.titulo ");			
		
		Query query = getSession().createSQLQuery(sql.toString());
		query.setInteger("idProjeto", idProjeto);
		query.setBoolean("true", true);
		query.setInteger("idTipoProjeto", TipoProjeto.EXTENSAO);
		
		// APENAS PROJETOS DO GRUPO EM EXECUÇÃO OU CONCLUÍDOS PODEM TER PARTICIPANTES GERENCIADOS PELO COORDENADOR //
		List<Integer> situacoes = new ArrayList<Integer>(); 
		situacoes.addAll(Arrays.asList(TipoSituacaoProjeto.PROJETOS_GRUPO_CONCLUIDO ));
		situacoes.addAll(Arrays.asList(TipoSituacaoProjeto.PROJETOS_GRUPO_EM_EXECUCAO ));
		
		query.setParameterList("idSituacoesProjeto", situacoes);
		
		
		List<Object> lista = query.list();
		List<AtividadeExtensao> result = montaInformacoesCursosEventosExtensao(lista);
		
		System.out.println(" >>>>>>>>>>>>> Consulta demorou: " + (System.currentTimeMillis()-tempo)+ " ms  <<<<<<<<<<<<<<<<");
		
		return result;
		
	}
	
	
	
	/**
	 * Monta as informações para cursos e eventos 
	 * @param lista
	 * @return
	 */
	private List<AtividadeExtensao> montaInformacoesCursosEventosExtensao(List<Object> lista) {
		
		List<AtividadeExtensao> result = new ArrayList<AtividadeExtensao>();
		
		for (int a = 0; a < lista.size(); a++) {
			
			Object[] colunas = (Object[]) lista.get(a);
			AtividadeExtensao at = new AtividadeExtensao();
			at.setId((Integer) colunas[0]);
			at.setSequencia((Integer) colunas[1]);
			at.setTipoAtividadeExtensao( new TipoAtividadeExtensao(0, (String) colunas[2]) );
			at.setProjeto(new Projeto((Integer) colunas[3]));
			at.setAno( ((Short) colunas[4]).intValue());
			at.setTitulo((String) colunas[5]);
			at.getProjeto().setSituacaoProjeto(new TipoSituacaoProjeto(0, (String) colunas[6]) );
			at.setCursoEventoExtensao( colunas[7] != null ?(new CursoEventoExtensao((Integer) colunas[7])) : null);
			at.setNumeroParticipantes( colunas[8] == null ? 0 : ((BigInteger) colunas[8]).intValue());
			
			if(! result.contains(at))
				result.add(at);
			else
				at = result.get(result.indexOf(at));
			
			// Se tem sub atividade e ela está ativida  ///
			if(colunas[9] != null && (Boolean ) colunas[12] ){
				SubAtividadeExtensao sub = new SubAtividadeExtensao();
				sub.setId((Integer) colunas[9]);
				sub.setTitulo((String) colunas[10]);
				sub.setTipoSubAtividadeExtensao(new TipoSubAtividadeExtensao((String) colunas[11]));
				sub.setAtivo( (Boolean ) colunas[12] );
				sub.setNumeroParticipantes( colunas[13] == null ? 0 : ((BigInteger) colunas[13]).intValue());
				at.addSubAtividade(sub);
			}
			
		}
		return result;
	}
	
	
	/**
	 * <p>Retorna todas as atividades de extensão do tipo Cursos, Eventos, Projetos, Programa e Produto nas quais o docente seja coordenador 
	 *  e que esteja uma uma situção que possa gerenciar os participantes.<p>
	 * 
	 * <p>Diferente dos método de gerenciar inscrições que retorna apenas as atividades que estejam em execução, aqui as atividades 
	 * de projetos concluídos podem aparecer também.</p>
	 * 
	 * <p>Caso a atividade possua mini atividades é retornado aqui também.</p> 
	 * 
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<AtividadeExtensao> findAtividadesExtensaoGlobaGerenciarParticipantesByCoordenador(Servidor servidor) throws DAOException {
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT " + PROJECAO_PADRAO + " FROM extensao.atividade atividadeExtensao ");
		sql.append(JOINS_PADRAO_GLOBAL);
		
		if (DesignacaoFuncaoProjetoHelper.projetosByCoordenadoresOrDesignacaoCoordenadorOrDesignacaoGerenciaParticipante(servidor.getId()).size() > 0){
			sql.append(" WHERE ( ( pessoa.id_pessoa = :idPessoa AND coordenador.ativo = :true ) or projeto.id_projeto in ( :idProjetos ) ) ");
		} else {
			sql.append(" WHERE ( ( pessoa.id_pessoa = :idPessoa AND coordenador.ativo = :true ) ) ");
		}
		sql.append(" AND atividadeExtensao.ativo = :true AND projeto.ativo = :true "); // Sempre usar a consulta mais restritiva possível para evitar erros
		sql.append(" AND tipoProjeto.id_tipo_projeto = :idTipoProjeto ");
		sql.append(" AND projeto.id_tipo_situacao_projeto  IN (:idSituacoesProjeto) ");
		sql.append(" ORDER BY projeto.ano, projeto.titulo ");			
		
		Query query = getSession().createSQLQuery(sql.toString());
		query.setInteger("idPessoa", servidor.getPessoa().getId());

		if (DesignacaoFuncaoProjetoHelper.projetosByCoordenadoresOrDesignacaoCoordenadorOrDesignacaoGerenciaParticipante(servidor.getId()).size() > 0){
			query.setParameterList("idProjetos", DesignacaoFuncaoProjetoHelper.projetosByCoordenadoresOrDesignacaoCoordenadorOrDesignacaoGerenciaParticipante(servidor.getId()));
		}
		
		query.setBoolean("true", true);
		query.setInteger("idTipoProjeto", TipoProjeto.EXTENSAO);
		
		// APENAS PROJETOS DO GRUPO EM EXECUÇÃO OU CONCLUÍDOS PODEM TER PARTICIPANTES GERENCIADOS PELO COORDENADOR //
		List<Integer> situacoes = new ArrayList<Integer>(); 
		situacoes.addAll(Arrays.asList(TipoSituacaoProjeto.PROJETOS_GRUPO_CONCLUIDO ));
		situacoes.addAll(Arrays.asList(TipoSituacaoProjeto.PROJETOS_GRUPO_EM_EXECUCAO ));
		
		query.setParameterList("idSituacoesProjeto", situacoes);
		
		List<Object> lista = query.list();
		List<AtividadeExtensao> result = montaInformacoesCursosEventosExtensao(lista);
		
		return result;
		
	}
	
}
