/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 31/10/2012
 * 
 */
package br.ufrn.sigaa.extensao.dao.inscricoes_atividades;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.CadastroParticipanteAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividade;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividadeParticipante;
import br.ufrn.sigaa.extensao.dominio.ModalidadeParticipante;
import br.ufrn.sigaa.extensao.dominio.ModalidadeParticipantePeriodoInscricaoAtividade;
import br.ufrn.sigaa.extensao.dominio.ParticipanteAcaoExtensao;
import br.ufrn.sigaa.extensao.dominio.StatusInscricaoParticipante;
import br.ufrn.sigaa.extensao.dominio.StatusPagamentoInscricao;
import br.ufrn.sigaa.extensao.dominio.SubAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoSubAtividadeExtensao;
import br.ufrn.sigaa.extensao.jsf.inscricoes_atividades.GerenciarInscritosCursosEEventosExtensaoMBean.CamposOrdenacaoListaInscricoes;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;
import br.ufrn.sigaa.questionario.dominio.QuestionarioRespostas;

/**
 *
 * <p>Dao exclusivo para as consultas de inscrições de participantes em cursos e eventos de extensao. </p>
 *
 * <p> <i> </i> </p>
 * 
 * @author jadson
 *
 */
public class InscricaoParticipanteAtividadeExtensaoDao extends GenericSigaaDAO{

	
	
	/**
	 * <p>Quanta a quantidade de inscrições ativas da atividade.</p>
	 *  
	 * @param idCadastroParticipante
	 * @param idAtividade
	 * @return
	 * @throws DAOException
	 */
	public int countQtdInscricoesParticipanteNoPeriodo(int idPeriodoInscricaoAtividade) throws DAOException {
		
		String hql = " SELECT COUNT(DISTINCT inscricao.id ) " +
				" FROM InscricaoAtividadeParticipante inscricao " +
				" WHERE inscricao.inscricaoAtividade.id = :idPeriodoInscricaoAtividade " +
				" AND inscricao.statusInscricao in "+UFRNUtils.gerarStringIn(StatusInscricaoParticipante.getStatusAtivos());
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idPeriodoInscricaoAtividade", idPeriodoInscricaoAtividade);
		
		return ((Long)q.uniqueResult()).intValue();
	}
	
	
	
	
	
	/**
	 * <p>Retorna os dados dos cursos ou eventos de extensão que estão com período de inscrição abertos. </p>
	 * 
	 * <p>Os dados mais básicos possíveis, porque eles serão mostrados em uma listagem. E a quantidade pode ser grande.</p>
	 * 
	 * @param titulo
	 * @param idTipoAtividade
	 * @param idAreaTematica
	 * @param idDocente
	 * @param idFuncao
	 * @param apenasAbertas
	 * @param inicio
	 * @param fim
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<InscricaoAtividade> findAtividadesComPeriodosDeInscricoesAbertos(String tituloAtividade, Integer idTipoAtividade
			, Integer idAreaTematica, Integer idServidor, Date dataInicioPeriodoInscricao, Date dataFimPeriodoInscicao, int idCadastraParticipanteLogado) throws DAOException {
		
		long tempo = System.currentTimeMillis();
		
		try {
		
			StringBuilder hql = new StringBuilder();
			hql.append(" SELECT DISTINCT periodoInscricao.id as idPeriodoInscricao, periodoInscricao.periodoInicio as periodoInicio,");
			hql.append(" periodoInscricao.periodoFim as periodoFim, periodoInscricao.quantidadeVagas as quantidadeVagas, ");
			
			// Uma consulta interna para contar a quantidade de inscrições confirmadas realizadas //
			hql.append("( SELECT COUNT( DISTINCT iap.id) FROM InscricaoAtividadeParticipante iap " +
					" where iap.inscricaoAtividade.id = periodoInscricao.id AND iap.statusInscricao.id = :idStatusInscritosAprovados) as numeroAprovados, ");

			// Uma consulta interna para contar a quantidade de inscrições aceitas realizadas //
			hql.append("( SELECT COUNT( DISTINCT iap.id) FROM InscricaoAtividadeParticipante iap " +
			" where iap.inscricaoAtividade.id = periodoInscricao.id and iap.statusInscricao.id = :idStatusInscritos) as numeroInscritos, ");

			// Informação das atividades e do coordenador //
			hql.append(" atividade.id as idAtividade, atividade.tipoAtividadeExtensao, projeto.id, projeto.titulo, coordenador.id, pessoa.id, pessoa.nome, ");
			
			// Uma consulta interna para contar as ***inscrições abertas*** para as sub atividades.  //
			//   Para habilitar o botão para o usuário se inscrever                                  //
			hql.append(" ( SELECT COUNT( DISTINCT periodoInscricaoSub.id ) " +
			" FROM InscricaoAtividade periodoInscricaoSub " +
			" WHERE periodoInscricaoSub.subAtividade.atividade.id = atividade.id " +
			" AND periodoInscricaoSub.periodoInicio <= :hoje AND periodoInscricaoSub.periodoFim >= :hoje )  as inscricoesSubAtividade, ");
				
			// Para ver se o participante já se inscreveu para essa atividade //
			hql.append(" ( SELECT COUNT( DISTINCT participante.id ) " +
			" FROM InscricaoAtividadeParticipante participante " +
			" WHERE participante.inscricaoAtividade.atividade.id  = atividade.id " + // Aqui verifica se o participante já tem inscrição, independente do período de inscrição que ele tenha feito
			" AND participante.cadastroParticipante.id  = :idCadastraParticipanteLogado AND participante.statusInscricao IN (:statusAtivos) )  as minhasInscricoes ");
			
			hql.append(" FROM InscricaoAtividade as periodoInscricao " +
					" JOIN periodoInscricao.atividade atividade " +
					" JOIN atividade.projeto projeto " +
					" JOIN projeto.coordenador coordenador " +
					" JOIN coordenador.pessoa pessoa " +
					" JOIN coordenador.funcaoMembro funcao ");
			
			// AINDA NÃO FOI SUSPENSO PELO COORDENADOR //
			hql.append(" WHERE periodoInscricao.ativo = trueValue()"); 
			
			// APENAS PERÍODOS DE INSCRIÇÃO DE ATIVIDADES E PROJETOS QUE CONTINUEM ATIVOS  //
			hql.append(" AND atividade.ativo = trueValue() AND projeto.ativo = trueValue()");

			// NÃO RETORNA AS INSCRIÇÕES QUE SÃO DE MINI ATIVIDADES.
			hql.append(" AND periodoInscricao.subAtividade IS NULL ");  
			
			// RECUPERA ASPENAS AS ABERTAS //
			// ou seja, que já começaram e que ainda não terminaram
			hql.append(" AND ( periodoInscricao.periodoInicio <= :hoje AND periodoInscricao.periodoFim >= :hoje ) ");
			
			
			
			if (StringUtils.notEmpty(tituloAtividade))
				hql.append(" AND ( projeto.titulo like :tituloAtividade ) ");
			
			if ( idTipoAtividade != null && idTipoAtividade > 0)
				hql.append(" AND atividade.tipoAtividadeExtensao.id = :idtipoAtividade ");
			
			if (idAreaTematica != null && idAreaTematica > 0)
				hql.append(" AND atividade.areaTematicaPrincipal.id = :idAreaTematica ");
			
			if (idServidor != null && idServidor > 0)
				hql.append(" AND coordenador.servidor.id = :idServidor ");
			
			if ((dataInicioPeriodoInscricao != null) && (dataFimPeriodoInscicao != null)) 
				hql.append(" AND ( periodoInscricao.periodoInicio < :inicio AND periodoInscricao.periodoFim > :fim ) ");
			
			hql.append(" ORDER BY periodoInscricao.periodoFim asc ");		
			
			Query query = getSession().createQuery(hql.toString());
			
			query.setDate("hoje", new Date());
			query.setInteger("idCadastraParticipanteLogado", idCadastraParticipanteLogado);
			query.setParameterList("statusAtivos", StatusInscricaoParticipante.getStatusAtivos());
			
			// inscrições realizadas pelo usuário e aprovadas pelo coordenador, ou automaticamente, caso não exija aprovação //
			query.setInteger("idStatusInscritosAprovados", StatusInscricaoParticipante.APROVADO);
			// inscrições realizadas pelo usuário, mas não aprovadas pelo coordenador //
			query.setInteger("idStatusInscritos", StatusInscricaoParticipante.INSCRITO);

			
			if (StringUtils.notEmpty(tituloAtividade))
				query.setString("tituloAtividade", "%" + tituloAtividade + "%");
			
			if (idTipoAtividade != null && idTipoAtividade > 0)
				query.setInteger("idtipoAtividade", idTipoAtividade);
			
			if (idAreaTematica != null && idAreaTematica > 0)
				query.setInteger("idAreaTematica", idAreaTematica);
			
			if (idServidor != null && idServidor > 0)
				query.setInteger("idServidor", idServidor);
			
			if ((dataInicioPeriodoInscricao != null) && (dataFimPeriodoInscicao != null)){
				query.setDate("inicio", dataInicioPeriodoInscricao);
				query.setDate("fim", dataFimPeriodoInscicao);
			}
			
			List<Object> lista = query.list();
			
			ArrayList<InscricaoAtividade>  result = new ArrayList<InscricaoAtividade>();

			
			
			for (int a = 0; a < lista.size(); a++) {
			    int col = 0;
			    Object[] colunas = (Object[]) lista.get(a);				
			    
			    InscricaoAtividade peridosInscricaoAbertos = new InscricaoAtividade();
			    peridosInscricaoAbertos = new InscricaoAtividade();
			    peridosInscricaoAbertos.setId((Integer) colunas[col++]);
			    peridosInscricaoAbertos.setPeriodoInicio((Date) colunas[col++]);
			    peridosInscricaoAbertos.setPeriodoFim((Date) colunas[col++]);					
			    
			    peridosInscricaoAbertos.setQuantidadeVagas ( (Integer) colunas[col++] );
			    
			    // NÃO É SALVO NO BANCO CALCULADO "ON LINE"  // 
			    peridosInscricaoAbertos.setQuantidadeInscritosAprovados(  (( Long) colunas[col++]).intValue() );
			    peridosInscricaoAbertos.setQuantidadeInscritos( ( (Long) colunas[col++]).intValue() );
			   
			    peridosInscricaoAbertos.setAtividade(new AtividadeExtensao((Integer) colunas[col++]));
			    peridosInscricaoAbertos.getAtividade().setTipoAtividadeExtensao((TipoAtividadeExtensao) colunas[col++]);
			    peridosInscricaoAbertos.getAtividade().setProjeto(new Projeto((Integer) colunas[col++]));
			    peridosInscricaoAbertos.getAtividade().getProjeto().setTitulo((String) colunas[col++]);
			    peridosInscricaoAbertos.getAtividade().getProjeto().setCoordenador(new MembroProjeto((Integer) colunas[col++]));
			    peridosInscricaoAbertos.getAtividade().getProjeto().getCoordenador().setPessoa(new Pessoa((Integer) colunas[col++]));
			    peridosInscricaoAbertos.getAtividade().getProjeto().getCoordenador().getPessoa().setNome(((String) colunas[col++]));
			    
			    peridosInscricaoAbertos.setQuantidadePeriodosInscricoesMiniAtividade( (( Long) colunas[col++]).intValue() );
			    peridosInscricaoAbertos.setEstouInscrito( (( Long) colunas[col++]).intValue() > 0  );
			    
			    result.add(peridosInscricaoAbertos);
			}

			return result;

		}finally{
			System.out.println(" ######### Consulta Demorou: "+( System.currentTimeMillis() - tempo )+" ms ##########");
		}
	}
	
	
	/**
	 * <p>Retorna as inscrições de um participante específico para a atividade passada. </p>
	 * 
	 * 
	 * @param titulo
	 * @param idTipoAtividade
	 * @param idAreaTematica
	 * @param idDocente
	 * @param idFuncao
	 * @param apenasAbertas
	 * @param inicio
	 * @param fim
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<InscricaoAtividadeParticipante> findInscricoesParticipanteAtividade
			(int idAtividade, int idCadastraParticipanteLogado, StatusInscricaoParticipante...  status) throws DAOException {
		
		String projecao = " participante.id,  participante.statusInscricao.id ";
		
		String hql = " SELECT DISTINCT  " +projecao+
					 " FROM InscricaoAtividadeParticipante participante " +
					 " WHERE participante.cadastroParticipante.id  = :idCadastraParticipanteLogado " +
					 " AND participante.inscricaoAtividade.atividade = :idAtividade ";
		
		
		if(status != null && status.length > 0)
			hql += " AND participante.statusInscricao IN (:statusInscricao)  ";
		
		Query query = getSession().createQuery(hql);
		query.setInteger("idAtividade", idAtividade);
		query.setInteger("idCadastraParticipanteLogado", idCadastraParticipanteLogado);
		
		if(status != null && status.length > 0)
			query.setParameterList("statusInscricao", status);
		
		List<Object[]> lista = query.list();
		
		return new ArrayList<InscricaoAtividadeParticipante>( HibernateUtils.parseTo(lista, projecao, InscricaoAtividadeParticipante.class, "participante"));
	}
	
	
	
	
	
	/**
	 * <p>Retorna as inscrições atividas de um participante em atividades de extensão </p>
	 * 
	 * <p>Aqui retorna também as inscrições realizadas em mini atividades. Esse método retorna 
	 *    a informações essenciais porque será usado para listas as inscrições realizadas, quando o usuário selecionar uma inscrição recupera tudo sobre ela.</p>
	 * 
	 * @param titulo
	 * @param idTipoAtividade
	 * @param idAreaTematica
	 * @param idDocente
	 * @param idFuncao
	 * @param apenasAbertas
	 * @param inicio
	 * @param fim
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<InscricaoAtividadeParticipante> findInscricoesAtividadeEMiniAtividadeParticipante(int idCadastraParticipanteLogado
			,  Date dataInicio, Date dataFinal, StatusInscricaoParticipante...  status) throws DAOException {
		
		String projecao = " inscricao.id_inscricao_atividade_participante as idInscricao, statusInscricao.id_status_inscricao_participante, statusInscricao.descricao statusInscricao, "+
						  " atividade.id_atividade as idAtividade, projeto.titulo as tituloAtividade, tipoAtividade.descricao as tipoAtividade, "+
						  " subAtividade.id_sub_atividade_extensao as idSubAtividade, subAtividade.titulo tituloSubAtividade, tipoSubAtividade.descricao as tipoSubAtividade, subAtividade.id_atividade as idAtividadeSubAtividade, "+
						  " inscricao.data_cadastro ";

		String sql = " SELECT DISTINCT " +projecao+
				" FROM extensao.inscricao_atividade_participante inscricao " +
				" INNER JOIN extensao.status_inscricao_participante statusInscricao  ON statusInscricao.id_status_inscricao_participante = inscricao.id_status_inscricao_participante "+
				" INNER JOIN extensao.inscricao_atividade periodoInscricao           ON periodoInscricao.id_inscricao_atividade=inscricao.id_inscricao_atividade  " +
				" LEFT  JOIN extensao.atividade atividade                            ON periodoInscricao.id_atividade=atividade.id_atividade  " +
				" LEFT  JOIN projetos.projeto projeto                                ON projeto.id_projeto = atividade.id_projeto  " +
				" LEFT  JOIN extensao.tipo_atividade_extensao tipoAtividade          ON tipoAtividade.id_tipo_atividade_extensao = atividade.id_tipo_atividade_extensao  " +
				" LEFT  JOIN extensao.sub_atividade_extensao subatividade            ON subatividade.id_sub_atividade_extensao = periodoInscricao.id_sub_atividade_extensao  " +
				" LEFT  JOIN extensao.tipo_sub_atividade_extensao tipoSubAtividade   ON tipoSubAtividade.id_tipo_sub_atividade_extensao = subatividade.id_tipo_sub_atividade_extensao  " +
				
				" WHERE periodoInscricao.ativo = :true " + // cujo período de inscrição não foi suspensao pelo coordenador
				" AND inscricao.id_cadastro_participante_atividade_extensao  = :idCadastraParticipanteLogado  "; // inscrições do usuário logado.
		
		
		if(status != null && status.length > 0)
			sql += " AND inscricao.id_status_inscricao_participante IN (:statusInscricao)  ";  // os status da inscrição (se tá ativo, se foi cancelado)
		
		if(dataInicio != null && dataFinal != null )
			sql += " AND ( inscricao.data_cadastro between :dataInicio AND :dataFinal )   ";  // O período para não ficar trazendo tudo.
		
		
		sql += " ORDER BY  atividade.id_atividade, subAtividade.id_atividade, inscricao.data_cadastro DESC ";
		
		Query query = getSession().createSQLQuery(sql);
		
		query.setInteger("idCadastraParticipanteLogado", idCadastraParticipanteLogado);
		query.setBoolean("true", true);
		if(status != null && status.length > 0)
			query.setParameterList("statusInscricao", status);
		if(dataInicio != null && dataFinal != null ){
			query.setTimestamp("dataInicio", CalendarUtils.configuraTempoDaData(dataInicio, 0, 0, 0, 0) );
			query.setTimestamp("dataFinal", CalendarUtils.configuraTempoDaData(dataFinal, 23, 59, 59, 999) );
		}
		
		
		List<Object[]> lista = query.list();
		
		List<InscricaoAtividadeParticipante> retorno = new ArrayList<InscricaoAtividadeParticipante>();
		
		for (Object[] objects : lista) {
			InscricaoAtividadeParticipante inscricao = new InscricaoAtividadeParticipante();
			inscricao.setId( (Integer) objects[0]);
			
			inscricao.setStatusInscricao( new StatusInscricaoParticipante((Integer) objects[1], (String) objects[2]));
			
			if( objects[3] != null ){
				InscricaoAtividade periodoInscricao = new InscricaoAtividade();
				periodoInscricao.setAtividade(   new AtividadeExtensao( (Integer) objects[3], (String) objects[4], 0)   );
				periodoInscricao.getAtividade().setTipoAtividadeExtensao(new TipoAtividadeExtensao(0, (String) objects[5]));
				inscricao.setInscricaoAtividade(periodoInscricao);
			}
			
			if( objects[6] != null ){
				InscricaoAtividade periodoInscricao = new InscricaoAtividade();
				periodoInscricao.setSubAtividade(   new SubAtividadeExtensao( (Integer) objects[6], (String) objects[7] )   );
				periodoInscricao.getSubAtividade().setTipoSubAtividadeExtensao(new TipoSubAtividadeExtensao(  (String) objects[8] ));
				periodoInscricao.getSubAtividade().setAtividade( new AtividadeExtensao((Integer) objects[9]));
				inscricao.setInscricaoAtividade(periodoInscricao);
			}
			
			inscricao.setDataCadastro( (Date) objects[10]);
			
			retorno.add(inscricao);
		}
		
		// ordena pelo id da atividade ou da atividade da sub atividade  //
		Collections.sort(retorno, new Comparator<InscricaoAtividadeParticipante>() {

			@Override
			public int compare(InscricaoAtividadeParticipante o1, InscricaoAtividadeParticipante o2) {
				
				Integer idAtividade1 =  o1.getInscricaoAtividade().isInscricaoAtividade() ? o1.getInscricaoAtividade().getAtividade().getId() 
							: o1.getInscricaoAtividade().getSubAtividade().getAtividade().getId();
				
				Integer idAtividade2 = o2.getInscricaoAtividade().isInscricaoAtividade()  ? o2.getInscricaoAtividade().getAtividade().getId() 
						: o2.getInscricaoAtividade().getSubAtividade().getAtividade().getId();
				
				int comparacacao = idAtividade1.compareTo(idAtividade2);
				
				if(comparacacao == 0){  // se mesma atividade 
					
					// Se está comparando atividade com sub atividade, atividade vem primeiro
					if(o1.getInscricaoAtividade().isInscricaoAtividade() && ! o2.getInscricaoAtividade().isInscricaoAtividade()){
						return -1;
					}
					
					// Se está comparando sub atividade com atividade, atividade vem primeiro
					if(! o1.getInscricaoAtividade().isInscricaoAtividade() &&  o2.getInscricaoAtividade().isInscricaoAtividade()){
						return 1;
					}
					
					/*
					 * se é a mesma atividade, e é comparação entre atividade com atividade ou sub com sub, compara pelo status
					 * para atividade ordena em ordem descente, para as sub em ordem crescente
					 */
					
					if( o1.getInscricaoAtividade().isInscricaoAtividade() )
						return - o1.getStatusInscricao().compareTo(o2.getStatusInscricao());
					else
						return o1.getStatusInscricao().compareTo(o2.getStatusInscricao());
				}
				
				return comparacacao;
			}
		});
		
		return retorno;
		
	}
	
	
	
	
	/**
	 * <p>Retorna as participações que o usuário tem em atividade e mini atividades de extensaão para as quais ele não fez inscrição, ou seja,
	 * foi cadastrado diretamente pelo cooordenador. </p>
	 * 
	 * <p>Esse método retorna a informações essenciais porque será usado em uma listagem, quando o usuário selecionar uma inscrição recupera tudo sobre ela.</p>
	 * 
	 * @param titulo
	 * @param idTipoAtividade
	 * @param idAreaTematica
	 * @param idDocente
	 * @param idFuncao
	 * @param apenasAbertas
	 * @param inicio
	 * @param fim
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<ParticipanteAcaoExtensao> findParticipacaoAtividadeEMiniAtividadeSemInscricao(int idCadastraParticipanteLogado,  Date dataInicio, Date dataFinal) throws DAOException {
		
		String projecao = " participante.id_participante_acao_extensao as idParticipante, "+
						  " atividade.id_atividade as idAtividade, projeto.titulo as tituloAtividade, tipoAtividade.descricao as tipoAtividade, "+
						  " subAtividade.id_sub_atividade_extensao as idSubAtividade, subAtividade.titulo tituloSubAtividade, tipoSubAtividade.descricao as tipoSubAtividade, subAtividade.id_atividade as idAtividadeSubAtividade, "+
						  " participante.data_cadastro ";

		String sql = " SELECT DISTINCT " +projecao+
				" FROM extensao.participante_acao_extensao participante " +
				" LEFT  JOIN extensao.atividade atividade                            ON participante.id_acao_extensao = atividade.id_atividade  " +
				" LEFT  JOIN projetos.projeto projeto                                ON projeto.id_projeto = atividade.id_projeto  " +
				" LEFT  JOIN extensao.tipo_atividade_extensao tipoAtividade          ON tipoAtividade.id_tipo_atividade_extensao = atividade.id_tipo_atividade_extensao  " +
				" LEFT  JOIN extensao.sub_atividade_extensao subatividade            ON participante.id_sub_atividade_extensao  = subatividade.id_sub_atividade_extensao " +
				" LEFT  JOIN extensao.tipo_sub_atividade_extensao tipoSubAtividade   ON tipoSubAtividade.id_tipo_sub_atividade_extensao = subatividade.id_tipo_sub_atividade_extensao  " +
				
				//" LEFT JOIN extensao.inscricao_atividade_participante inscricao      ON inscricao.id_cadastro_participante_atividade_extensao = :idCadastraParticipanteLogado"+
				
				" WHERE participante.ativo = :true " +
				" AND participante.id_inscricao_atividade_participante IS NULL " + // participante ativo, que não possui inscrição
				" AND participante.id_cadastro_participante_atividade_extensao  = :idCadastraParticipanteLogado  "; // participantes do usuário logado.
		
		
		if(dataInicio != null && dataFinal != null )
			sql += " AND ( participante.data_cadastro between :dataInicio AND :dataFinal  )   ";  // Traz apenas as participações dos projetos os sub atividades encerrados entre as datas passadas
		
		
		sql += " ORDER BY  participante.data_cadastro, atividade.id_atividade, subAtividade.id_atividade  DESC ";
		
		Query query = getSession().createSQLQuery(sql);
		
		query.setInteger("idCadastraParticipanteLogado", idCadastraParticipanteLogado);
		query.setBoolean("true", true);
		if(dataInicio != null && dataFinal != null ){
			query.setTimestamp("dataInicio", CalendarUtils.configuraTempoDaData(dataInicio, 0, 0, 0, 0) );
			query.setTimestamp("dataFinal", CalendarUtils.configuraTempoDaData(dataFinal, 23, 59, 59, 999) );
		}
		
		
		List<Object[]> lista = query.list();
		
		List<ParticipanteAcaoExtensao> retorno = new ArrayList<ParticipanteAcaoExtensao>();
		
		for (Object[] objects : lista) {
			ParticipanteAcaoExtensao participante = new ParticipanteAcaoExtensao();
			participante.setId( (Integer) objects[0]);
			
			
			if( objects[1] != null ){
				participante.setAtividadeExtensao(   new AtividadeExtensao( (Integer) objects[1], (String) objects[2], 0)   );
				participante.getAtividadeExtensao().setTipoAtividadeExtensao(new TipoAtividadeExtensao(0, (String) objects[3]));
			}
			
			if( objects[4] != null ){
				participante.setSubAtividade(   new SubAtividadeExtensao( (Integer) objects[4], (String) objects[5] )   );
				participante.getSubAtividade().setTipoSubAtividadeExtensao(new TipoSubAtividadeExtensao(  (String) objects[6] ));
				participante.getSubAtividade().setAtividade( new AtividadeExtensao((Integer) objects[7]));
			}
			
			retorno.add(participante);
		}
		
		return retorno;
		
	}
	
	
	
	
	/**
	 * <p>Esse método retorna Todas as informações necessária para o participante gerenciar sua inscrição. 
	 * Incluindo as informações para o participante emtir o certificado e a GRU para pagamento. </p>
	 * 
	 * @param titulo
	 * @param idTipoAtividade
	 * @param idAreaTematica
	 * @param idDocente
	 * @param idFuncao
	 * @param apenasAbertas
	 * @param inicio
	 * @param fim
	 * @return
	 * @throws DAOException
	 */

	public InscricaoAtividadeParticipante findInformacoesInscricaoParticipanteAtividade(int idInscricaoParticipante) throws DAOException {
		
		String projecao = " inscricao.id_inscricao_atividade_participante as idInscricao, statusInscricao.id_status_inscricao_participante, statusInscricao.descricao statusInscricao, "+
				          " inscricao.instituicao, inscricao.valor_taxa_matricula, inscricao.id_gru_pagamento, inscricao.status_pagamento, modalidade.nome, "+
						  " periodoInscricao.periodo_inicio, periodoInscricao.periodo_fim, periodoInscricao.cobranca_taxa_matricula, periodoInscricao.data_vencimento_gru, periodoInscricao.observacoes, periodoInscricao.motivo_cancelamento, periodoInscricao.ativo,  "+
				          " atividade.id_atividade as idAtividade, projeto.titulo as tituloAtividade," +
				          " projeto.data_inicio as inicioAtividade, projeto.data_fim as fimAtividade, projeto.ativo, projeto.id_tipo_situacao_projeto, projeto.autorizar_certificado_gestor, " +  //informações para emissão de certificado e declaração
				          " tipoAtividade.descricao as tipoAtividade, projeto.id_unidade_orcamentaria, "+
						  " coordenador.id_membro_projeto idCordenador, pessoa.id_pessoa idPessoaCoordenador, pessoa.nome as nomeCoordenador, "+
				          " participante.id_participante_acao_extensao, participante.frequencia, participante.autorizacao_declaracao, participante.autorizacao_certificado, participante.observacao_certificado ";

		String sql = " SELECT DISTINCT " +projecao+
					 " FROM extensao.inscricao_atividade_participante inscricao "+
					 " INNER JOIN extensao.status_inscricao_participante statusInscricao                                       ON statusInscricao.id_status_inscricao_participante = inscricao.id_status_inscricao_participante "+
 					 " LEFT JOIN extensao.modalidade_participante_periodo_inscricao_atividade modalidadeParticipante           ON modalidadeParticipante.id_modalidade_participante_periodo_inscricao_atividade = inscricao.id_modalidade_participante_periodo_inscricao_atividade  " +
 					 " LEFT JOIN extensao.modalidade_participante modalidade                                                   ON modalidade.id_modalidade_participante = modalidadeParticipante.id_modalidade_participante  " +
					 " INNER JOIN extensao.inscricao_atividade periodoInscricao                                                ON periodoInscricao.id_inscricao_atividade=inscricao.id_inscricao_atividade  " +
					 " INNER JOIN extensao.atividade atividade                                                                 ON periodoInscricao.id_atividade=atividade.id_atividade  " +
					 " INNER JOIN extensao.tipo_atividade_extensao tipoAtividade                                               ON tipoAtividade.id_tipo_atividade_extensao = atividade.id_tipo_atividade_extensao  " +
					 " INNER JOIN projetos.projeto projeto                                                                     ON projeto.id_projeto = atividade.id_projeto  "+
					 " INNER JOIN projetos.membro_projeto coordenador                                                          ON coordenador.id_membro_projeto = projeto.id_coordenador " +
					 " INNER JOIN comum.pessoa pessoa                                                                          ON coordenador.id_pessoa =pessoa.id_pessoa "+
					 " LEFT JOIN extensao.participante_acao_extensao participante                                              ON participante.id_participante_acao_extensao = inscricao.id_participante_acao_extensao ";
		
		
		sql += " WHERE inscricao.id_inscricao_atividade_participante  = :idInscricaoParticipante  ";
		
		Query query = getSession().createSQLQuery(sql);
		query.setInteger("idInscricaoParticipante", idInscricaoParticipante);
		
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = query.list();
		
		
		
		InscricaoAtividadeParticipante retorno = new InscricaoAtividadeParticipante();
		
		for (Object[] objects : lista) {
			
			retorno.setId( (Integer) objects[0]);
			retorno.setStatusInscricao( new StatusInscricaoParticipante((Integer) objects[1], (String) objects[2]));
			retorno.setInstituicao( (String) objects[3] );
			if( objects[4]  != null ) retorno.setValorTaxaMatricula( (BigDecimal) objects[4]  );
			if( objects[5]  != null ) retorno.setIdGRUPagamento( (Integer) objects[5]  );
			if( objects[6]  != null ) retorno.setStatusPagamento( StatusPagamentoInscricao.getStatusMulta( ((Short) objects[6]).intValue() )  );
			
			if( objects[7] != null ){
				ModalidadeParticipantePeriodoInscricaoAtividade modalidade = new ModalidadeParticipantePeriodoInscricaoAtividade();
				modalidade.setModalidadeParticipante( new ModalidadeParticipante(0, (String) objects[7], true) );
				retorno.setMolidadeParticipante( modalidade );
			}
			
			InscricaoAtividade periodoInscricao = new InscricaoAtividade();
			periodoInscricao.setPeriodoInicio( (Date) objects[8]  );
			periodoInscricao.setPeriodoFim( (Date) objects[9]  );
			periodoInscricao.setCobrancaTaxaMatricula( (Boolean) objects[10]  );
			periodoInscricao.setDataVencimentoGRU( (Date) objects[11]  );
			periodoInscricao.setObservacoes( (String) objects[12] );
			periodoInscricao.setMotivoCancelamento( (String) objects[13] );
			periodoInscricao.setAtivo( (Boolean) objects[14] );
			
			periodoInscricao.setAtividade(   new AtividadeExtensao( (Integer) objects[15], (String) objects[16], 0)   );
			periodoInscricao.getAtividade().getProjeto().setDataInicio(( Date) objects[17]);
			periodoInscricao.getAtividade().getProjeto().setDataFim( (Date) objects[18]);
			
			periodoInscricao.getAtividade().getProjeto().setAtivo( (Boolean) objects[19]);
			periodoInscricao.getAtividade().getProjeto().setSituacaoProjeto( new TipoSituacaoProjeto( (Integer) objects[20] ) );
			periodoInscricao.getAtividade().getProjeto().setAutorizarCertificadoGestor( (Boolean) objects[21]);
			
			periodoInscricao.getAtividade().setTipoAtividadeExtensao(new TipoAtividadeExtensao(0, (String) objects[22]));
			
			if( objects[23] != null ) periodoInscricao.getAtividade().getProjeto().setUnidadeOrcamentaria( new Unidade( (Integer) objects[23])); // para emitir a GRU
			
			periodoInscricao.getAtividade().getProjeto().setCoordenador(  new MembroProjeto(  (Integer) objects[24] ) );
			periodoInscricao.getAtividade().getProjeto().getCoordenador().setPessoa( new Pessoa ((Integer) objects[25] , (String) objects[26] ));
			
			retorno.setInscricaoAtividade(periodoInscricao);
			
			if(objects[27] != null ) {  // se a inscrição tiver sido aprovada deve ter participante associado //
				retorno.setParticipanteExtensao( new ParticipanteAcaoExtensao());
				retorno.getParticipanteExtensao().setId( (Integer) objects[27] );
				retorno.getParticipanteExtensao().setFrequencia( (Integer) objects[28] );
				retorno.getParticipanteExtensao().setAutorizacaoDeclaracao( (Boolean) objects[29] );
				retorno.getParticipanteExtensao().setAutorizacaoCertificado( (Boolean) objects[30] );
				retorno.getParticipanteExtensao().setObservacaoCertificado( (String) objects[31] );
				retorno.getParticipanteExtensao().setAtividadeExtensao(periodoInscricao.getAtividade()); // para emissão do certificado
			}
		}
		
		
		return retorno;
	}
	
	
	
	
	/**
	 * <p>Retorna todos os participantes de um determinado cursos ou evento de extenção agrupados por status da inscrição</p>
	 * 
	 * <p>Esse método geralmente é usado pelo caso de uso de gerenciar participantes, que é o caso de uso quando o coordenador 
	 *  vai aprovar a participação etc... </p>
	 * 
	 * <p>Essa consulta tem que ser muito bem otimizada porque a quantidade de participantes pode ser grande.</p>
	 * 
	 * @param idInscricao
	 * @param idStatus
	 * @return
	 * @throws DAOException
	 */
	public List<InscricaoAtividadeParticipante> findAllParticipantesByAtividade(Integer idAtividade, CamposOrdenacaoListaInscricoes campoOrdenacao, StatusInscricaoParticipante... status) throws DAOException {
		
			long tempo = System.currentTimeMillis();
		
			StringBuilder hql = new StringBuilder();

			
			hql.append(" SELECT participante.id as id, statusInscricao.id as idStatusInscricao, statusInscricao.descricao as descricaoStatusInscricao, ");
			hql.append(" participante.dataCadastro as dataCadastro, ");
			hql.append(" participante.inscricaoAtividade.id as idPeriodoInscricao, "); // no momento de aprovar tem que saber qual foi  o período inscrição que ele se inscreveu
			hql.append(" participante.instituicao as instituicao, participante.idGRUPagamento as idGRUPagamento, participante.statusPagamento as statusPagamento, ");
			hql.append(" participante.idArquivo as idArquivo, participante.descricaoArquivo as descricaoArquivo, ");
			hql.append(" cadastroParticipante.id as idCadastro, cadastroParticipante.cpf as cpf, cadastroParticipante.passaporte as passaporte, cadastroParticipante.nome as nomeInscrito, cadastroParticipante.email as email,");
			hql.append(" ( SELECT questionario.id FROM QuestionarioRespostas questionario WHERE questionario.inscricaoAtividadeParticipante.id = participante.id ) as idQuestionario ");
			
			hql.append(" FROM InscricaoAtividadeParticipante participante");
			hql.append(" INNER JOIN participante.statusInscricao statusInscricao");
			hql.append(" INNER JOIN participante.cadastroParticipante cadastroParticipante");
			
			hql.append(" WHERE participante.inscricaoAtividade.atividade.id = :idAtividade"); // Recupera todas as inscrição para a atividade
			hql.append(" AND participante.inscricaoAtividade.ativo = :true ");                // Cujo o período de inscrição não foi suspenso
			
			if(status != null && status.length > 0){
				hql.append(" AND statusInscricao.id IN "+UFRNUtils.gerarStringIn(status));
			}
			
			hql.append(" ORDER BY participante.statusInscricao.id ASC, "+campoOrdenacao.getCampoOrdenacao());
			
			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idAtividade", idAtividade);
			query.setBoolean("true", true);
			
			@SuppressWarnings("unchecked")
			List<Object[]> dadosInscricoesParticipante = query.list();
			
			List<InscricaoAtividadeParticipante> retorno = new ArrayList<InscricaoAtividadeParticipante>();
			
			for (Object[] dadosRecuperados : dadosInscricoesParticipante) {
				InscricaoAtividadeParticipante inscricao = new InscricaoAtividadeParticipante();
				inscricao.setId( (Integer) dadosRecuperados[0]);
				inscricao.setStatusInscricao( new StatusInscricaoParticipante((Integer) dadosRecuperados[1], (String) dadosRecuperados[2]) );
				inscricao.setDataCadastro( (Date) dadosRecuperados[3] );
				inscricao.setInscricaoAtividade(  new InscricaoAtividade((Integer) dadosRecuperados[4]) );
				
				inscricao.setInstituicao((String) dadosRecuperados[5]);
				inscricao.setIdGRUPagamento((Integer) dadosRecuperados[6]);
				inscricao.setStatusPagamento((StatusPagamentoInscricao) dadosRecuperados[7]);
				inscricao.setIdArquivo((Integer) dadosRecuperados[8]);
				inscricao.setDescricaoArquivo((String) dadosRecuperados[9]);
				
				inscricao.setCadastroParticipante( new CadastroParticipanteAtividadeExtensao((Integer) dadosRecuperados[10], (Long) dadosRecuperados[11], (String) dadosRecuperados[12], (String) dadosRecuperados[13],  (String) dadosRecuperados[14]) );
				
				if(dadosRecuperados[15] != null)
					inscricao.setQuestionarioRespostas( new QuestionarioRespostas((Integer) dadosRecuperados[15]));
				retorno.add(inscricao);
			}
			
			System.out.println(" <<<<<<<<<<<<<<<<<<<< "+(System.currentTimeMillis()-tempo)+" ms >>>>>>>>>>>>>>>>>>>>");
			
			return retorno;
			
	}
	
	
	
	/**
	 * <p>Retorna todos os participantes das sub atividades da atividade passada.</p>
	 * 
	 * <p>Esse método geralmente é usado pelo caso de uso de gerenciar participantes, que é o caso de uso quando o coordenador 
	 *  vai aprovar a participação etc... </p>
	 * 
	 * <p>Essa consulta tem que ser muito bem otimizada porque a quantidade de participantes pode ser grande.</p>
	 * 
	 * @param idInscricao
	 * @param idStatus
	 * @return
	 * @throws DAOException
	 */
	
	public List<InscricaoAtividadeParticipante> findAllParticipantesSubAtividadeByAtividades(Integer idAtividade, StatusInscricaoParticipante... status) throws DAOException {
		
			long tempo = System.currentTimeMillis();
		
			StringBuilder hql = new StringBuilder();

			
			hql.append(" SELECT participante.id as id, statusInscricao.id as idStatusInscricao, statusInscricao.descricao as descricaoStatusInscricao, ");
			hql.append(" participante.dataCadastro as dataCadastro, ");
			hql.append(" participante.inscricaoAtividade.id as idPeriodoInscricao, "); // no momento de aprovar tem que saber qual foi  o período inscrição que ele se inscreveu
			hql.append(" participante.instituicao as instituicao, participante.idGRUPagamento as idGRUPagamento, participante.statusPagamento as statusPagamento, ");
			hql.append(" participante.idArquivo as idArquivo, participante.descricaoArquivo as descricaoArquivo, ");
			hql.append(" cadastroParticipante.id as idCadastro, cadastroParticipante.cpf as cpf, cadastroParticipante.passaporte as passaporte, cadastroParticipante.nome as nome, cadastroParticipante.email as email,");
			hql.append(" ( SELECT questionario.id FROM QuestionarioRespostas questionario WHERE questionario.inscricaoAtividadeParticipante.id = participante.id ) as idQuestionario ");
			
			hql.append(" FROM InscricaoAtividadeParticipante participante");
			hql.append(" INNER JOIN participante.statusInscricao statusInscricao");
			hql.append(" INNER JOIN participante.cadastroParticipante cadastroParticipante");
			
			hql.append(" WHERE participante.inscricaoAtividade.subAtividade.atividade.id = :idAtividade"); // Recupera todas as inscrição para as Sub Atividades da atividade.
			hql.append(" AND participante.inscricaoAtividade.ativo = :true ");                // Cujo o período de inscrição não foi suspenso
			
			if(status != null && status.length > 0){
				hql.append(" AND statusInscricao.id IN "+UFRNUtils.gerarStringIn(status));
			}
			
			hql.append(" ORDER BY participante.statusInscricao.id ASC  participante.dataCadastro DESC ");
			
			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idAtividade", idAtividade);
			query.setBoolean("true", true);
			
			@SuppressWarnings("unchecked")
			List<Object[]> dadosInscricoesParticipante = query.list();
			
			List<InscricaoAtividadeParticipante> retorno = new ArrayList<InscricaoAtividadeParticipante>();
			
			for (Object[] dadosRecuperados : dadosInscricoesParticipante) {
				InscricaoAtividadeParticipante inscricao = new InscricaoAtividadeParticipante();
				inscricao.setId( (Integer) dadosRecuperados[0]);
				inscricao.setStatusInscricao( new StatusInscricaoParticipante((Integer) dadosRecuperados[1], (String) dadosRecuperados[2]) );
				inscricao.setDataCadastro( (Date) dadosRecuperados[3] );
				inscricao.setInscricaoAtividade(  new InscricaoAtividade((Integer) dadosRecuperados[4]) );
				
				inscricao.setInstituicao((String) dadosRecuperados[5]);
				inscricao.setIdGRUPagamento((Integer) dadosRecuperados[6]);
				inscricao.setStatusPagamento((StatusPagamentoInscricao) dadosRecuperados[7]);
				inscricao.setIdArquivo((Integer) dadosRecuperados[8]);
				inscricao.setDescricaoArquivo((String) dadosRecuperados[9]);
				
				inscricao.setCadastroParticipante( new CadastroParticipanteAtividadeExtensao((Integer) dadosRecuperados[10], (Long) dadosRecuperados[11], (String) dadosRecuperados[12], (String) dadosRecuperados[13],  (String) dadosRecuperados[14]) );
				
				if(dadosRecuperados[15] != null)
					inscricao.setQuestionarioRespostas( new QuestionarioRespostas((Integer) dadosRecuperados[15]));
				retorno.add(inscricao);
			}
			
			System.out.println(" <<<<<<<<<<<<<<<<<<<< "+(System.currentTimeMillis()-tempo)+" ms >>>>>>>>>>>>>>>>>>>>");
			
			return retorno;
			
	}
	
	
	
	
	/**
	 * <p>Retorna todos os participantes de um determinado cursos ou evento de extenção agrupados por status da inscrição</p>
	 * 
	 * <p>Esse método geralmente é usado pelo caso de uso de gerenciar participantes, que é o caso de uso quando o coordenador 
	 *  vai aprovar a participação etc... </p>
	 * 
	 * <p>Essa consulta tem que ser muito bem otimizada porque a quantidade de participantes pode ser grande.</p>
	 * 
	 * @param idInscricao
	 * @param idStatus
	 * @return
	 * @throws DAOException
	 */
	public List<InscricaoAtividadeParticipante> findAllParticipantesByPeriodoInscricao(Integer idPeriodoInscricao, StatusInscricaoParticipante... status) throws DAOException {
		
			long tempo = System.currentTimeMillis();
		
			StringBuilder hql = new StringBuilder();

			
			hql.append(" SELECT participante.id as id, statusInscricao.id as idStatusInscricao, statusInscricao.descricao as descricaoStatusInscricao, ");
			hql.append(" participante.dataCadastro as dataCadastro, ");
			hql.append(" participante.inscricaoAtividade.id as idPeriodoInscricao, "); // no momento de aprovar tem que saber qual foi  o período inscrição que ele se inscreveu
			hql.append(" participante.instituicao as instituicao, participante.idGRUPagamento as idGRUPagamento, participante.statusPagamento as statusPagamento, ");
			hql.append(" participante.idArquivo as idArquivo, participante.descricaoArquivo as descricaoArquivo, ");
			hql.append(" cadastroParticipante.id as idCadastro, cadastroParticipante.cpf as cpf, cadastroParticipante.passaporte as passaporte, cadastroParticipante.nome as nome, cadastroParticipante.email as email,");
			hql.append(" ( SELECT questionario.id FROM QuestionarioRespostas questionario WHERE questionario.inscricaoAtividadeParticipante.id = participante.id ) as idQuestionario ");
			
			hql.append(" FROM InscricaoAtividadeParticipante participante");
			hql.append(" INNER JOIN participante.statusInscricao statusInscricao");
			hql.append(" INNER JOIN participante.cadastroParticipante cadastroParticipante");
			
			hql.append(" WHERE participante.inscricaoAtividade.id = :idPeriodoInscricao");    // Recupera todas as inscrição para a atividade
			hql.append(" AND participante.inscricaoAtividade.ativo = :true ");                // Cujo o período de inscrição não foi suspenso
			
			if(status != null && status.length > 0){
				hql.append(" AND statusInscricao.id IN "+UFRNUtils.gerarStringIn(status));
			}
			
			hql.append(" ORDER BY participante.statusInscricao.id ASC  participante.dataCadastro DESC ");
			
			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idPeriodoInscricao", idPeriodoInscricao);
			query.setBoolean("true", true);
			
			@SuppressWarnings("unchecked")
			List<Object[]> dadosInscricoesParticipante = query.list();
			
			List<InscricaoAtividadeParticipante> retorno = new ArrayList<InscricaoAtividadeParticipante>();
			
			for (Object[] dadosRecuperados : dadosInscricoesParticipante) {
				InscricaoAtividadeParticipante inscricao = new InscricaoAtividadeParticipante();
				inscricao.setId( (Integer) dadosRecuperados[0]);
				inscricao.setStatusInscricao( new StatusInscricaoParticipante((Integer) dadosRecuperados[1], (String) dadosRecuperados[2]) );
				inscricao.setDataCadastro( (Date) dadosRecuperados[3] );
				inscricao.setInscricaoAtividade(  new InscricaoAtividade((Integer) dadosRecuperados[4]) );
				
				inscricao.setInstituicao((String) dadosRecuperados[5]);
				inscricao.setIdGRUPagamento((Integer) dadosRecuperados[6]);
				inscricao.setStatusPagamento((StatusPagamentoInscricao) dadosRecuperados[7]);
				inscricao.setIdArquivo((Integer) dadosRecuperados[8]);
				inscricao.setDescricaoArquivo((String) dadosRecuperados[9]);
				
				inscricao.setCadastroParticipante( new CadastroParticipanteAtividadeExtensao((Integer) dadosRecuperados[10], (Long) dadosRecuperados[11], (String) dadosRecuperados[12], (String) dadosRecuperados[13],  (String) dadosRecuperados[14]) );
				
				if(dadosRecuperados[15] != null)
					inscricao.setQuestionarioRespostas( new QuestionarioRespostas((Integer) dadosRecuperados[15]));
				retorno.add(inscricao);
			}
			
			System.out.println(" <<<<<<<<<<<<<<<<<<<< "+(System.currentTimeMillis()-tempo)+" ms >>>>>>>>>>>>>>>>>>>>");
			
			return retorno;
			
	}
	
}
