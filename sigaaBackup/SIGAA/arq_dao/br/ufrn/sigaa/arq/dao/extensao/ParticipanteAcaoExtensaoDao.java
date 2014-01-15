/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '24/12/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.extensao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.CadastroParticipanteAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.CursoEventoExtensao;
import br.ufrn.sigaa.extensao.dominio.ParticipanteAcaoExtensao;
import br.ufrn.sigaa.extensao.dominio.SubAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoParticipacaoAcaoExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoSubAtividadeExtensao;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/*******************************************************************************
 * 
 * Dao utilizado exclusivamente para recuperar os Participantes de uma atividade de extensão.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
public class ParticipanteAcaoExtensaoDao extends GenericSigaaDAO {

	
	
	
	/**
	 * <p>Esse método retorna Todas as informações necessária para o participante gerenciar suas particação
	 * Incluindo as informações para o participante emitir o certificado e declaração de participação. </p>
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

	public ParticipanteAcaoExtensao findInformacoesParticipanteAtividade(int idParticipante) throws DAOException {
		
		String projecao = " participante.id_participante_acao_extensao, participante.frequencia, participante.autorizacao_declaracao, participante.autorizacao_certificado, participante.observacao_certificado, "+
				          " atividade.id_atividade as idAtividade, projeto.titulo as tituloAtividade," +
				          " projeto.data_inicio as inicioAtividade, projeto.data_fim as fimAtividade, projeto.ativo, projeto.id_tipo_situacao_projeto, projeto.autorizar_certificado_gestor, " + //informações para emissão de certificado e declaração
				          " tipoAtividade.descricao as tipoAtividade, projeto.id_unidade_orcamentaria  as unidadeOrcamentaria, "+
						  " coordenador.id_membro_projeto idCordenador, pessoa.id_pessoa idPessoaCoordenador, pessoa.nome as nomeCoordenador";

		String sql = " SELECT DISTINCT " +projecao+
					 " FROM extensao.participante_acao_extensao participante "+
					 " INNER JOIN extensao.atividade atividade                                                                 ON participante.id_acao_extensao = atividade.id_atividade  " +
					 " INNER JOIN extensao.tipo_atividade_extensao tipoAtividade                                               ON tipoAtividade.id_tipo_atividade_extensao = atividade.id_tipo_atividade_extensao  " +
					 " INNER JOIN projetos.projeto projeto                                                                     ON projeto.id_projeto = atividade.id_projeto  "+
					 " JOIN projetos.membro_projeto coordenador                                                                ON coordenador.id_membro_projeto = projeto.id_coordenador " +
					 " JOIN comum.pessoa pessoa                                                                                ON coordenador.id_pessoa =pessoa.id_pessoa ";
		
		sql += " WHERE participante.id_participante_acao_extensao  = :idParticipante AND participante.ativo = :true ";
		
		Query query = getSession().createSQLQuery(sql);
		query.setInteger("idParticipante", idParticipante);
		query.setBoolean("true", true);
		
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = query.list();
		
		
		
		ParticipanteAcaoExtensao retorno = new ParticipanteAcaoExtensao();
		
		for (Object[] objects : lista) {
			
			retorno.setId( (Integer) objects[0]);
			retorno.setFrequencia( (Integer) objects[1] );
			retorno.setAutorizacaoDeclaracao( (Boolean) objects[2] );
			retorno.setAutorizacaoCertificado( (Boolean) objects[3] );
			retorno.setObservacaoCertificado( (String) objects[4] );
			
			retorno.setAtividadeExtensao(   new AtividadeExtensao( (Integer) objects[5], (String) objects[6], 0)   );
			retorno.getAtividadeExtensao().getProjeto().setDataInicio(( Date) objects[7]);
			retorno.getAtividadeExtensao().getProjeto().setDataFim( (Date) objects[8]);
			
			retorno.getAtividadeExtensao().getProjeto().setAtivo( (Boolean) objects[9]);
			retorno.getAtividadeExtensao().getProjeto().setSituacaoProjeto( new TipoSituacaoProjeto( (Integer) objects[10] ) );
			retorno.getAtividadeExtensao().getProjeto().setAutorizarCertificadoGestor( (Boolean) objects[11]);
			
			retorno.getAtividadeExtensao().setTipoAtividadeExtensao(new TipoAtividadeExtensao(0, (String) objects[12]));
			
			if(objects[13] != null)
				retorno.getAtividadeExtensao().getProjeto().setUnidadeOrcamentaria( new Unidade(  (Integer) objects[13] ));
			
			retorno.getAtividadeExtensao().getProjeto().setCoordenador(  new MembroProjeto(  (Integer) objects[14] ) );
			retorno.getAtividadeExtensao().getProjeto().getCoordenador().setPessoa( new Pessoa ((Integer) objects[15] , (String) objects[16] ));
			
		}
		
		
		return retorno;
	}
	
	
	
	/**
	 * <p>Esse método retorna Todas as informações necessária para o participante gerenciar sua participação. 
	 * Incluindo as informações para o participante emitir o certificado e declaração. </p>
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

	public ParticipanteAcaoExtensao findInformacoesParticipanteSubAtividade(int idParticipante) throws DAOException {
		
		String projecao = " participante.id_participante_acao_extensao, participante.frequencia, participante.autorizacao_declaracao, participante.autorizacao_certificado, participante.observacao_certificado, "+	
		          " subatividade.id_sub_atividade_extensao as idSubAtividade, subatividade.titulo as tituloSubAtividade, subatividade.ativo as subAtiva, subatividade.inicio, subatividade.fim, " +
		          " tipoSubAtividade.descricao, " +
		          " atividade.id_atividade as idAtividade, projeto.titulo as tituloAtividade, " +
		          " projeto.data_inicio as inicioAtividade, projeto.data_fim as fimAtividade, projeto.ativo, projeto.id_tipo_situacao_projeto, projeto.autorizar_certificado_gestor, " +  //informações para emissão de certificado e declaração
		          " tipoAtividade.descricao as tipoAtividade, projeto.id_unidade_orcamentaria as unidadeOrcamentaria, "+
		          " coordenador.id_membro_projeto idCordenador, pessoa.id_pessoa idPessoaCoordenador, pessoa.nome as nomeCoordenador ";	

		String sql = " SELECT DISTINCT " +projecao+
				 	 " FROM extensao.participante_acao_extensao participante "+
					 " INNER JOIN extensao.sub_atividade_extensao subatividade                                                 ON subatividade.id_sub_atividade_extensao = participante.id_sub_atividade_extensao  " +
					 " INNER JOIN extensao.tipo_sub_atividade_extensao tipoSubAtividade                                        ON tipoSubAtividade.id_tipo_sub_atividade_extensao = subatividade.id_tipo_sub_atividade_extensao  "+
					 " INNER JOIN extensao.atividade atividade                                                                 ON atividade.id_atividade = subatividade.id_atividade  " +
					 " INNER JOIN extensao.tipo_atividade_extensao tipoAtividade                                               ON tipoAtividade.id_tipo_atividade_extensao = atividade.id_tipo_atividade_extensao  " +
					 " INNER JOIN projetos.projeto projeto                                                                     ON projeto.id_projeto = atividade.id_projeto  "+ 
					 " JOIN projetos.membro_projeto coordenador                                                                ON coordenador.id_membro_projeto = projeto.id_coordenador " +
					 " JOIN comum.pessoa pessoa                                                                                ON coordenador.id_pessoa =pessoa.id_pessoa ";
		
		sql += " WHERE participante.id_participante_acao_extensao  = :idParticipante AND participante.ativo = :true ";
		
		Query query = getSession().createSQLQuery(sql);
		query.setInteger("idParticipante", idParticipante);
		query.setBoolean("true", true);
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = query.list();
		
		
		
		ParticipanteAcaoExtensao retorno = new ParticipanteAcaoExtensao();
		
		for (Object[] objects : lista) {
			          
			retorno.setId( (Integer) objects[0]);
			retorno.setFrequencia( (Integer) objects[1] );
			retorno.setAutorizacaoDeclaracao( (Boolean) objects[2] );
			retorno.setAutorizacaoCertificado( (Boolean) objects[3] );
			retorno.setObservacaoCertificado( (String) objects[4] );
			
			retorno.setSubAtividade(   new SubAtividadeExtensao( (Integer) objects[5], (String) objects[6] ) );
			retorno.getSubAtividade().setAtivo( (Boolean) objects[7] );
			
			retorno.getSubAtividade().setInicio( (Date) objects[8] );
			retorno.getSubAtividade().setFim( (Date) objects[9] );
			
			retorno.getSubAtividade().setTipoSubAtividadeExtensao(new TipoSubAtividadeExtensao((String) objects[10]));
			retorno.getSubAtividade().setAtividade(  new AtividadeExtensao( (Integer) objects[11], (String) objects[12], 0)  );
			
			retorno.getSubAtividade().getAtividade().getProjeto().setDataInicio(( Date) objects[13]);
			retorno.getSubAtividade().getAtividade().getProjeto().setDataFim( (Date) objects[14]);
			
			retorno.getSubAtividade().getAtividade().getProjeto().setAtivo( (Boolean) objects[15]);
			
			retorno.getSubAtividade().getAtividade().getProjeto().setSituacaoProjeto( new TipoSituacaoProjeto( (Integer) objects[16] ) );
			retorno.getSubAtividade().getAtividade().getProjeto().setAutorizarCertificadoGestor( (Boolean) objects[17]);
			
			
			retorno.getSubAtividade().getAtividade().setTipoAtividadeExtensao(new TipoAtividadeExtensao(0, (String) objects[18]));
			
			// Pode não ter unidade orçamentária ainda.
			if(objects[19] != null)
				retorno.getSubAtividade().getAtividade().getProjeto().setUnidadeOrcamentaria( new Unidade(  (Integer) objects[19] ));
			
			retorno.getSubAtividade().getAtividade().getProjeto().setCoordenador(  new MembroProjeto(  (Integer) objects[20] ) );
			retorno.getSubAtividade().getAtividade().getProjeto().getCoordenador().setPessoa( new Pessoa ((Integer) objects[21] , (String) objects[22] ));
			
			
		}
		
		
		return retorno;
	}
	
	
	
	
	/**
	 * <p>Retorna os partipantes que a pessoa tem ativos nas mini atividades da atividade passada.</p>
	 *  
	 *  
	 * @param idCadastroParticipante
	 * @param idAtividade
	 * @return
	 * @throws DAOException
	 */
	public List<ParticipanteAcaoExtensao> findParticipantesMiniAtividadesDaAtividade(int idCadastroParticipante, int idAtividade) throws DAOException {
		
		String projecao = " participante.id, participante.cadastroParticipante.id, participante.cadastroParticipante.nome, participante.cadastroParticipante.email ";
		
		String hql = " SELECT  " +projecao +
				" FROM ParticipanteAcaoExtensao participante " +
				" WHERE participante.cadastroParticipante.id = :idCadastroParticipante " +
				" AND participante.subAtividade.atividade.id = :idAtividade " +
				" AND participante.ativo = :true ";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idCadastroParticipante", idCadastroParticipante);
		q.setInteger("idAtividade", idAtividade);
		q.setBoolean("true", true);
		
		@SuppressWarnings("unchecked")
		List<ParticipanteAcaoExtensao> lista = new ArrayList<ParticipanteAcaoExtensao>(HibernateUtils.parseTo(q.list(), projecao, ParticipanteAcaoExtensao.class, "participante"));
		return lista;
	}
	
	
	
	/**
	 * <p>Quanta a quantidade de vezes que o particpante já foi incluído na atividade.</p>
	 *  
	 * <p>Teoricamente o participante só poderia ser incluído uma única vez na atividade.</p> 
	 *  
	 * @param idCadastroParticipante
	 * @param idAtividade
	 * @return
	 * @throws DAOException
	 */
	public int countQtdParticipanteAtivoNaAtividade(int idCadastroParticipante, int idAtividade) throws DAOException {
		
		String hql = " SELECT COUNT(DISTINCT participante.id ) " +
				" FROM ParticipanteAcaoExtensao participante " +
				" WHERE participante.cadastroParticipante.id = :idCadastroParticipante " +
				" AND participante.atividadeExtensao.id = :idAtividade " +
				" AND participante.ativo = :true ";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idCadastroParticipante", idCadastroParticipante);
		q.setInteger("idAtividade", idAtividade);
		q.setBoolean("true", true);
		
		return ((Long)q.uniqueResult()).intValue();
	}
	
	
	/**
	 * <p>Quanta a quantidade de vezes que o particpante já foi incluído na mini atividade.</p>
	 *  
	 * <p>Teoricamente o participante só poderia ser incluído uma única vez na mini atividade.</p> 
	 *  
	 * @param idCadastroParticipante
	 * @param idMiniAtividade
	 * @return
	 * @throws DAOException
	 */
	public int countQtdParticipanteNaMiniAtividade(int idCadastroParticipante, int idMiniAtividade) throws DAOException {
		
		String hql = " SELECT COUNT(DISTINCT participante.id ) " +
				" FROM ParticipanteAcaoExtensao participante " +
				" WHERE participante.cadastroParticipante.id = :idCadastroParticipante " +
				" AND participante.subAtividade.id = :idMiniAtividade " +
				" AND participante.ativo = :true ";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idCadastroParticipante", idCadastroParticipante);
		q.setInteger("idMiniAtividade", idMiniAtividade);
		q.setBoolean("true", true);
		
		return ((Long)q.uniqueResult()).intValue();
	}
	
	
	
	
	
	/**
	 * <p>Retorna as informações de contados de TODOS os participantes da atividade ou mini atividade de extensão.</p>
	 * 
	 * <p>Geralmente em uma listagem só, então recuperar o mínimo possível de informações aqui porque a 
	 * listagem pode ser grange, 1.000 ou 2.000  participantes...</p>
	 * 
	 * @param idAcao
	 * @return
	 * @throws DAOException
	 */
	public ParticipanteAcaoExtensao findInformacoesParticipantesEmitirCertificadosEDeclaracoes(int idParticipante) throws DAOException {

	
		
		String projecao =  " p.id, p.frequencia, p.autorizacaoDeclaracao, p.autorizacaoCertificado, p.observacaoCertificado, cadastroParticipante.nome, cadastroParticipante.cpf, cadastroParticipante.passaporte, cadastroParticipante.dataNascimento, " +
						   " tipoParticipacao.id, tipoParticipacao.descricao, "+
		                   " atividade.id, projetoAtividade.id, projetoAtividade.titulo as titulo, "+
		                   " projetoAtividade.dataInicio as inicioAtividade, projetoAtividade.dataFim as fimAtividade, projetoAtividade.ativo, projetoAtividade.situacaoProjeto.id, projetoAtividade.autorizarCertificadoGestor, " + //informações para emissão de certificado e declaração
		                   " subAtividade.id, subAtividade.titulo as tituloSub, subAtividade.inicio, subAtividade.fim, "+
		                   
						   " COALESCE ( cursoEventoExtensaoAtividade.cargaHoraria, subAtividade.cargaHoraria ) as cargaHoraria, "+
		                   " COALESCE ( tipoAtividadeExtensao.id, tipoAtividadeExtensaoSubAtividade.id ) as idTipoAtividade, "+
		                   " COALESCE ( unidadeAtividade.nome, unidadeSubAtividade.nome ) as nomeUnidade,  "+
		                   " COALESCE ( pessoaCoordenador.nome, pessoaCoordenadorSub.nome ) as nomeCoordenador,  "+
		                   " COALESCE ( categoriaCoordenador.id, categoriaCoordenadorSub.id ) as categoriaCoordenador  ";
		
		StringBuilder hql = new StringBuilder(" SELECT DISTINCT " +projecao+
					 " FROM ParticipanteAcaoExtensao p " +
					 " INNER JOIN p.cadastroParticipante cadastroParticipante "+
					 " INNER JOIN p.tipoParticipacao tipoParticipacao ");
		
		
		hql.append(" LEFT JOIN p.atividadeExtensao atividade " +
				   " LEFT JOIN atividade.tipoAtividadeExtensao tipoAtividadeExtensao " +
				   " LEFT JOIN atividade.cursoEventoExtensao cursoEventoExtensaoAtividade "+
			       " LEFT JOIN atividade.projeto projetoAtividade "+
			       " LEFT JOIN projetoAtividade.coordenador coordenadorAtividade "+
			       " LEFT JOIN coordenadorAtividade.categoriaMembro categoriaCoordenador "+
			       " LEFT JOIN coordenadorAtividade.pessoa pessoaCoordenador "+
			       " LEFT JOIN projetoAtividade.unidade unidadeAtividade ");
		
		hql.append(" LEFT JOIN p.subAtividade subAtividade " +
				   " LEFT JOIN subAtividade.atividade atividadeDaSubAtividade " +
				   " LEFT JOIN atividadeDaSubAtividade.tipoAtividadeExtensao tipoAtividadeExtensaoSubAtividade " +
				   " LEFT JOIN atividadeDaSubAtividade.projeto projetoSubAtividade "+
				   " LEFT JOIN projetoSubAtividade.coordenador coordenadorSubAtividade"+
				   " LEFT JOIN coordenadorSubAtividade.categoriaMembro categoriaCoordenadorSub "+
			       " LEFT JOIN coordenadorSubAtividade.pessoa pessoaCoordenadorSub "+
				   " LEFT JOIN projetoSubAtividade.unidade unidadeSubAtividade ");
		
		
		hql.append(" WHERE p.id = :idParticipante AND p.ativo = :true ");              //  Retorna apenas os participantes que não foram removidos.
			      
		
		Query query = getSession().createQuery(hql.toString());
		query.setInteger( "idParticipante", idParticipante  );
		query.setBoolean( "true", true  );
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = query.list();
		
		ParticipanteAcaoExtensao retorno = null;
		
		for (int a = 0; a < lista.size(); a++) {
			
			Object[] colunas = lista.get(a);
			
			retorno = new ParticipanteAcaoExtensao((Integer) colunas[0]);
			
			retorno.setFrequencia( (Integer) colunas[1] );
			retorno.setAutorizacaoDeclaracao( (Boolean) colunas[2] );
			retorno.setAutorizacaoCertificado( (Boolean) colunas[3] );
			retorno.setObservacaoCertificado( (String) colunas[4] );
			
			
			retorno.setCadastroParticipante( new CadastroParticipanteAtividadeExtensao());
			retorno.getCadastroParticipante().setNome((String) colunas[5]);
			retorno.getCadastroParticipante().setCpf((Long) colunas[6]);
			retorno.getCadastroParticipante().setPassaporte((String) colunas[7]);
			retorno.getCadastroParticipante().setDataNascimento( (Date) colunas[8]);
			
			retorno.setTipoParticipacao(new TipoParticipacaoAcaoExtensao((Integer) colunas[9], (String) colunas[10]));
			
			if(colunas[11] != null){ // it is a activit
				retorno.setAtividadeExtensao( new AtividadeExtensao((Integer) colunas[11]));
				retorno.getAtividadeExtensao().setProjeto(new Projeto((Integer) colunas[12]));
				retorno.getAtividadeExtensao().getProjeto().setTitulo((String) colunas[13] );
				
				retorno.getAtividadeExtensao().getProjeto().setDataInicio((Date) colunas[14] );
				retorno.getAtividadeExtensao().getProjeto().setDataFim((Date) colunas[15] );
				
				retorno.getAtividadeExtensao().getProjeto().setAtivo( (Boolean) colunas[16]);
				retorno.getAtividadeExtensao().getProjeto().setSituacaoProjeto( new TipoSituacaoProjeto( (Integer) colunas[17] ) );
				retorno.getAtividadeExtensao().getProjeto().setAutorizarCertificadoGestor( (Boolean) colunas[18]);
				
				// aqui os indices são iguais ao campo das sub atividades //
				retorno.getAtividadeExtensao().setCursoEventoExtensao(new CursoEventoExtensao());
				retorno.getAtividadeExtensao().getCursoEventoExtensao().setCargaHoraria( (Integer) colunas[23] );
				retorno.getAtividadeExtensao().setTipoAtividadeExtensao(  new TipoAtividadeExtensao((Integer) colunas[24]) ) ;
				retorno.getAtividadeExtensao().getProjeto().setUnidade(  new Unidade(0, 0l, (String) colunas[25], "") ) ;
				
				retorno.getAtividadeExtensao().getProjeto().setCoordenador( new MembroProjeto() );
				retorno.getAtividadeExtensao().getProjeto().getCoordenador().setPessoa( new Pessoa(0, (String) colunas[26]) );
				retorno.getAtividadeExtensao().getProjeto().getCoordenador().setCategoriaMembro( new CategoriaMembro( (Integer) colunas[27] ));
			}
			
			if(colunas[19] != null){ // it is a mini activit
				retorno.setSubAtividade( new SubAtividadeExtensao((Integer) colunas[19]));
				retorno.getSubAtividade().setTitulo( (String) colunas[20]  );
				retorno.getSubAtividade().setInicio( (Date) colunas[21]  );
				retorno.getSubAtividade().setFim( (Date) colunas[22]  );
				
				// aqui os indices são iguais ao campo das atividades //
				
				retorno.getSubAtividade().setCargaHoraria( (Integer) colunas[23] );
				
				retorno.getSubAtividade().setAtividade(new AtividadeExtensao());
				retorno.getSubAtividade().getAtividade().setTipoAtividadeExtensao(  new TipoAtividadeExtensao((Integer) colunas[24]) ) ;
				
				retorno.getSubAtividade().getAtividade().setProjeto(new Projeto());
				retorno.getSubAtividade().getAtividade().getProjeto().setUnidade(  new Unidade(0, 0l, (String) colunas[25], "") ) ;
				retorno.getSubAtividade().getAtividade().getProjeto().setCoordenador( new MembroProjeto() );
				retorno.getSubAtividade().getAtividade().getProjeto().getCoordenador().setPessoa( new Pessoa(0, (String) colunas[26]) );
				retorno.getSubAtividade().getAtividade().getProjeto().getCoordenador().setCategoriaMembro( new CategoriaMembro( (Integer) colunas[27] ));
			}
			
		}
	
		return retorno;
	    
	}
	
	/** 
	 * Conta a quantidade total de participantes para realizar a paginação no gerenciamento de participantes.
	 */
	public Integer countParticipantesParaGerenciar(Integer idAtividadeExtensao, Integer idSubAtividadeExtensao
			, String nomeParticipante, Integer idMunicipioParticipante, boolean  participantesSemFrequencia, boolean participantesNaoDeclaracao, boolean participantesNaoCertificados) throws DAOException {
		
		
		if(idAtividadeExtensao != null && idSubAtividadeExtensao != null){
			throw new DAOException("Não pode recuperar participantes de uma atividade ou sub atividade ao mesmo tempo.");
		}
		
		if(idAtividadeExtensao == null && idSubAtividadeExtensao == null){
			throw new DAOException("É preciso passar pelo menos uma atividade ou sub atividade.");
		}
		
		String projecao =  " COUNT (DISTINCT p.id) ";
		
		StringBuilder hql = new StringBuilder(" SELECT  " +projecao+
					 " FROM ParticipanteAcaoExtensao p " +
					 " INNER JOIN p.cadastroParticipante cadastroParticipante ");
		
		if(idAtividadeExtensao != null ){
			hql.append(" INNER JOIN p.atividadeExtensao atividade " +
				       " INNER JOIN atividade.projeto.situacaoProjeto si ");
		}
		
		if(idSubAtividadeExtensao != null ){
			hql.append(" INNER JOIN p.subAtividade subAtividade " +
				       " INNER JOIN subAtividade.atividade.projeto.situacaoProjeto si ");
		}
		
		hql.append(" INNER JOIN p.tipoParticipacao tp " +
				   " WHERE p.ativo = trueValue() " +                //  Retorna apenas os participantes que não foram removidos.
			       " AND si.id not in ( :EXTENSAO_GRUPO_INVALIDO, :PROJETO_BASE_GRUPO_INVALIDO ) "); // de projetos em execução ou concluidos
		
		if(idAtividadeExtensao != null ){
			hql.append(" AND atividade.id = :idAtividadeExtensao " +                    // da atividade passada  
				       " AND atividade.projeto.ativo = trueValue() ");                  // e se o projeto ainda tá ativo.
		}
		
		if(idSubAtividadeExtensao != null ){
			hql.append(" AND subAtividade.id = :idSubAtividadeExtensao " +                    // da atividade passada  
				       " AND subAtividade.atividade.projeto.ativo = trueValue() ");                  // e se o projeto ainda tá ativo.
		}
		
		if(StringUtils.notEmpty(nomeParticipante)){
			hql.append(" AND cadastroParticipante.nomeAscii like :nomeParticipante "); 
		}
		
		if(idMunicipioParticipante != null && idMunicipioParticipante > 0 ){
			hql.append(" AND cadastroParticipante.municipio.id = :idMunicipioParticipante "); 
		}
		
		if(participantesSemFrequencia ){
			hql.append(" AND ( p.frequencia IS NULL OR p.frequencia = 0 )"); 
		}
		
		if(participantesNaoDeclaracao ){
			hql.append(" AND p.autorizacaoDeclaracao = falseValue() "); 
		}
		
		if(participantesNaoCertificados ){
			hql.append(" AND  p.autorizacaoCertificado = falseValue() "); 
		}

		Query query = getSession().createQuery(hql.toString());
		
		if(idAtividadeExtensao != null ){
			query.setInteger( "idAtividadeExtensao", idAtividadeExtensao  );
		}
		if(idSubAtividadeExtensao != null ){
			query.setInteger( "idSubAtividadeExtensao", idSubAtividadeExtensao  );
		}
		
		if(StringUtils.notEmpty(nomeParticipante)){
			query.setString( "nomeParticipante", "%"+StringUtils.toAsciiAndUpperCase( nomeParticipante ) +"%"  );
		}
		
		if(idMunicipioParticipante != null && idMunicipioParticipante > 0 ){
			query.setInteger( "idMunicipioParticipante", idMunicipioParticipante);
		}
		
		query.setParameterList("EXTENSAO_GRUPO_INVALIDO", TipoSituacaoProjeto.EXTENSAO_GRUPO_INVALIDO);
		query.setParameterList("PROJETO_BASE_GRUPO_INVALIDO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_INVALIDO);

		
		
		return ((Long) query.uniqueResult()).intValue();
		
	}
	
	
	/**
	 * <p>Retorna todos os participantes da atividade de extensão para mostrar a listagem para o coordenador.</p>
	 * 
	 * <p>Esse método implementa uma consulta paginada. </p>
	 * 
	 * <p>Geralmente em formato de relatório em uma listagem só, então recuperar o mínimo possível de informações aqui porque a 
	 * listagem pode ser grange, 1.000 ou 2.000  participantes...</p>
	 * 
	 * @param idAcao
	 * @return
	 * @throws DAOException
	 */
	public List<ParticipanteAcaoExtensao> findParticipantesParaGerenciar(Integer idAtividadeExtensao, Integer idSubAtividadeExtensao
			, String nomeParticipante, Integer idMunicipioParticipante, boolean  participantesSemFrequencia, boolean participantesNaoDeclaracao, boolean participantesNaoCertificados
			, int pagina, int qtdResultadosPorPagina) throws DAOException {

		if(idAtividadeExtensao != null && idSubAtividadeExtensao != null){
			throw new DAOException("Não pode recuperar participantes de uma atividade ou sub atividade ao mesmo tempo.");
		}
		
		if(idAtividadeExtensao == null && idSubAtividadeExtensao == null){
			throw new DAOException("É preciso passar pelo menos uma atividade ou sub atividade.");
		}
		
		String projecao = " DISTINCT p.id, cadastroParticipante.id, cadastroParticipante.cpf, cadastroParticipante.passaporte, cadastroParticipante.nome, tp.descricao, p.frequencia, p.autorizacaoDeclaracao, p.autorizacaoCertificado ";
		
		StringBuilder hql = new StringBuilder(" SELECT  " +projecao+
					 " FROM ParticipanteAcaoExtensao p " +
					 " INNER JOIN p.cadastroParticipante cadastroParticipante ");
		
		if(idAtividadeExtensao != null ){
			hql.append(" INNER JOIN p.atividadeExtensao atividade " +
				       " INNER JOIN atividade.projeto.situacaoProjeto si ");
		}
		
		if(idSubAtividadeExtensao != null ){
			hql.append(" INNER JOIN p.subAtividade subAtividade " +
				       " INNER JOIN subAtividade.atividade.projeto.situacaoProjeto si ");
		}
		
		hql.append(" INNER JOIN p.tipoParticipacao tp " +
				   " WHERE p.ativo = trueValue() " +                //  Retorna apenas os participantes que não foram removidos.
			       " AND si.id not in ( :EXTENSAO_GRUPO_INVALIDO, :PROJETO_BASE_GRUPO_INVALIDO ) "); // de projetos em execução ou concluidos
		
		if(idAtividadeExtensao != null ){
			hql.append(" AND atividade.id = :idAtividadeExtensao " +                    // da atividade passada  
				       " AND atividade.projeto.ativo = trueValue() ");                  // e se o projeto ainda tá ativo.
		}
		
		if(idSubAtividadeExtensao != null ){
			hql.append(" AND subAtividade.id = :idSubAtividadeExtensao " +                    // da atividade passada  
				       " AND subAtividade.atividade.projeto.ativo = trueValue() ");                  // e se o projeto ainda tá ativo.
		}
		
		if(StringUtils.notEmpty(nomeParticipante)){
			hql.append(" AND cadastroParticipante.nomeAscii like :nomeParticipante "); 
		}
		
		if(idMunicipioParticipante != null && idMunicipioParticipante > 0 ){
			hql.append(" AND cadastroParticipante.municipio.id = :idMunicipioParticipante "); 
		}
		
		if(participantesSemFrequencia ){
			hql.append(" AND ( p.frequencia IS NULL OR p.frequencia = 0 )"); 
		}
		
		if(participantesNaoDeclaracao ){
			hql.append(" AND p.autorizacaoDeclaracao = falseValue() "); 
		}
		
		if(participantesNaoCertificados ){
			hql.append(" AND  p.autorizacaoCertificado = falseValue() "); 
		}
		
		hql.append(" ORDER BY cadastroParticipante.nome, p.id ");

		Query query = getSession().createQuery(hql.toString());
		
		if(idAtividadeExtensao != null ){
			query.setInteger( "idAtividadeExtensao", idAtividadeExtensao  );
		}
		if(idSubAtividadeExtensao != null ){
			query.setInteger( "idSubAtividadeExtensao", idSubAtividadeExtensao  );
		}
		
		if(StringUtils.notEmpty(nomeParticipante)){
			query.setString( "nomeParticipante", "%"+StringUtils.toAsciiAndUpperCase( nomeParticipante ) +"%"  );
		}
		
		if(idMunicipioParticipante != null && idMunicipioParticipante > 0 ){
			query.setInteger( "idMunicipioParticipante", idMunicipioParticipante);
		}
		
		query.setParameterList("EXTENSAO_GRUPO_INVALIDO", TipoSituacaoProjeto.EXTENSAO_GRUPO_INVALIDO);
		query.setParameterList("PROJETO_BASE_GRUPO_INVALIDO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_INVALIDO);

		
		query.setFirstResult((pagina) * qtdResultadosPorPagina);
		query.setMaxResults(qtdResultadosPorPagina);
		
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = query.list();
		
		ArrayList<ParticipanteAcaoExtensao> result = new ArrayList<ParticipanteAcaoExtensao>();
		
		for (int a = 0; a < lista.size(); a++) {
			
			Object[] colunas = lista.get(a);
			
			ParticipanteAcaoExtensao p = new ParticipanteAcaoExtensao();
			p.setId((Integer) colunas[0]);
			p.setCadastroParticipante( new CadastroParticipanteAtividadeExtensao((Integer) colunas[1]));
			p.getCadastroParticipante().setCpf((Long) colunas[2]);
			p.getCadastroParticipante().setPassaporte((String) colunas[3]);
			p.getCadastroParticipante().setNome((String) colunas[4]);
			p.setTipoParticipacao(  new TipoParticipacaoAcaoExtensao((String) colunas[5])  );
			p.setFrequencia( (Integer) colunas[6] );
			p.setAutorizacaoDeclaracao( (Boolean) colunas[7] );
			p.setAutorizacaoCertificado( (Boolean) colunas[8] );
			
			result.add(p);
			
		}
	
		return result;
	    
	}
	
	
	
	
	
	/**
	 * <p>Retorna o mail de todos os participantes para notificá-los.</p>
	 * 
	 * <p>Geralmente em uma listagem só, então recuperar o mínimo possível de informações aqui porque a 
	 * listagem pode ser grange, 1.000 ou 2.000  participantes...</p>
	 * 
	 * @param idAcao
	 * @return
	 * @throws DAOException
	 */
	public List<ParticipanteAcaoExtensao> findEmailAllParticipantes(Integer idAtividadeExtensao, Integer idSubAtividadeExtensao) throws DAOException {

		if(idAtividadeExtensao != null && idSubAtividadeExtensao != null){
			throw new DAOException("Não pode recuperar participantes de uma atividade ou sub atividade ao mesmo tempo.");
		}
		
		if(idAtividadeExtensao == null && idSubAtividadeExtensao == null){
			throw new DAOException("É preciso passar pelo menos uma atividade ou sub atividade.");
		}
		
		String projecao =  " cadastroParticipante.email ";
		
		StringBuilder hql = new StringBuilder(" SELECT DISTINCT " +projecao+
					 " FROM ParticipanteAcaoExtensao p " +
					 " INNER JOIN p.cadastroParticipante cadastroParticipante "+
					 " LEFT JOIN cadastroParticipante.municipio municipio "+
					 " LEFT JOIN cadastroParticipante.unidadeFederativa unidadeFederativa ");
		
		if(idAtividadeExtensao != null ){
			hql.append(" INNER JOIN p.atividadeExtensao atividade " +
				       " INNER JOIN atividade.projeto.situacaoProjeto si ");
		}
		
		if(idSubAtividadeExtensao != null ){
			hql.append(" INNER JOIN p.subAtividade subAtividade " +
				       " INNER JOIN subAtividade.atividade.projeto.situacaoProjeto si ");
		}
		
		hql.append(" INNER JOIN p.tipoParticipacao tp " +
				   " WHERE p.ativo = trueValue() " +                //  Retorna apenas os participantes que não foram removidos.
			       " AND si.id not in ( :EXTENSAO_GRUPO_INVALIDO, :PROJETO_BASE_GRUPO_INVALIDO ) "); // de projetos em execução ou concluidos
		
		if(idAtividadeExtensao != null ){
			hql.append(" AND atividade.id = :idAtividadeExtensao " +                    // da atividade passada  
				       " AND atividade.projeto.ativo = trueValue() ");                  // e se o projeto ainda tá ativo.
		}
		
		if(idSubAtividadeExtensao != null ){
			hql.append(" AND subAtividade.id = :idSubAtividadeExtensao " +                    // da atividade passada  
				       " AND subAtividade.atividade.projeto.ativo = trueValue() ");                  // e se o projeto ainda tá ativo.
		}

		Query query = getSession().createQuery(hql.toString());
		
		if(idAtividadeExtensao != null ){
			query.setInteger( "idAtividadeExtensao", idAtividadeExtensao  );
		}
		if(idSubAtividadeExtensao != null ){
			query.setInteger( "idSubAtividadeExtensao", idSubAtividadeExtensao  );
		}
		
		query.setParameterList("EXTENSAO_GRUPO_INVALIDO", TipoSituacaoProjeto.EXTENSAO_GRUPO_INVALIDO);
		query.setParameterList("PROJETO_BASE_GRUPO_INVALIDO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_INVALIDO);

		@SuppressWarnings("unchecked")
		List<String> lista = query.list();
		
		ArrayList<ParticipanteAcaoExtensao> result = new ArrayList<ParticipanteAcaoExtensao>();
		
		for (int a = 0; a < lista.size(); a++) {
			
			String email = lista.get(a);
			
			ParticipanteAcaoExtensao p = new ParticipanteAcaoExtensao();
			p.setCadastroParticipante( new CadastroParticipanteAtividadeExtensao());
			p.getCadastroParticipante().setEmail(email);
			result.add(p);
			
		}
	
		return result;
	    
	}
	
	
	
	
	
	/**
	 * <p>Retorna as informações de contados de TODOS os participantes da atividade ou mini atividade de extensão.</p>
	 * 
	 * <p>Geralmente em uma listagem só, então recuperar o mínimo possível de informações aqui porque a 
	 * listagem pode ser grange, 1.000 ou 2.000  participantes...</p>
	 * 
	 * @param idAcao
	 * @return
	 * @throws DAOException
	 */
	public List<ParticipanteAcaoExtensao> findInformacoesContatoAllParticipantes(Integer idAtividadeExtensao, Integer idSubAtividadeExtensao) throws DAOException {

		if(idAtividadeExtensao != null && idSubAtividadeExtensao != null){
			throw new DAOException("Não pode recuperar participantes de uma atividade ou sub atividade ao mesmo tempo.");
		}
		
		if(idAtividadeExtensao == null && idSubAtividadeExtensao == null){
			throw new DAOException("É preciso passar pelo menos uma atividade ou sub atividade.");
		}
		
		String projecao =  " cadastroParticipante.nome, cadastroParticipante.email, cadastroParticipante.telefone, cadastroParticipante.celular, " +
				" cadastroParticipante.logradouro, cadastroParticipante.numero, cadastroParticipante.complemento, cadastroParticipante.bairro, " +
				" cadastroParticipante.municipio.nome, cadastroParticipante.unidadeFederativa.sigla, cadastroParticipante.cep ";
		
		StringBuilder hql = new StringBuilder(" SELECT DISTINCT " +projecao+
					 " FROM ParticipanteAcaoExtensao p " +
					 " INNER JOIN p.cadastroParticipante cadastroParticipante "+
					 " LEFT JOIN cadastroParticipante.municipio municipio "+
					 " LEFT JOIN cadastroParticipante.unidadeFederativa unidadeFederativa ");
		
		if(idAtividadeExtensao != null ){
			hql.append(" INNER JOIN p.atividadeExtensao atividade " +
				       " INNER JOIN atividade.projeto.situacaoProjeto si ");
		}
		
		if(idSubAtividadeExtensao != null ){
			hql.append(" INNER JOIN p.subAtividade subAtividade " +
				       " INNER JOIN subAtividade.atividade.projeto.situacaoProjeto si ");
		}
		
		hql.append(" INNER JOIN p.tipoParticipacao tp " +
				   " WHERE p.ativo = trueValue() " +                //  Retorna apenas os participantes que não foram removidos.
			       " AND si.id not in ( :EXTENSAO_GRUPO_INVALIDO, :PROJETO_BASE_GRUPO_INVALIDO ) "); // de projetos em execução ou concluidos
		
		if(idAtividadeExtensao != null ){
			hql.append(" AND atividade.id = :idAtividadeExtensao " +                    // da atividade passada  
				       " AND atividade.projeto.ativo = trueValue() ");                  // e se o projeto ainda tá ativo.
		}
		
		if(idSubAtividadeExtensao != null ){
			hql.append(" AND subAtividade.id = :idSubAtividadeExtensao " +                    // da atividade passada  
				       " AND subAtividade.atividade.projeto.ativo = trueValue() ");                  // e se o projeto ainda tá ativo.
		}
		
		hql.append(" ORDER BY cadastroParticipante.nome ");

		Query query = getSession().createQuery(hql.toString());
		
		if(idAtividadeExtensao != null ){
			query.setInteger( "idAtividadeExtensao", idAtividadeExtensao  );
		}
		if(idSubAtividadeExtensao != null ){
			query.setInteger( "idSubAtividadeExtensao", idSubAtividadeExtensao  );
		}
		
		query.setParameterList("EXTENSAO_GRUPO_INVALIDO", TipoSituacaoProjeto.EXTENSAO_GRUPO_INVALIDO);
		query.setParameterList("PROJETO_BASE_GRUPO_INVALIDO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_INVALIDO);

		@SuppressWarnings("unchecked")
		List<Object[]> lista = query.list();
		
		ArrayList<ParticipanteAcaoExtensao> result = new ArrayList<ParticipanteAcaoExtensao>();
		
		for (int a = 0; a < lista.size(); a++) {
			
			Object[] colunas = lista.get(a);
			
			ParticipanteAcaoExtensao p = new ParticipanteAcaoExtensao();
			p.setCadastroParticipante( new CadastroParticipanteAtividadeExtensao());
			p.getCadastroParticipante().setNome((String) colunas[0]);
			p.getCadastroParticipante().setEmail((String) colunas[1]);
			p.getCadastroParticipante().setTelefone((String) colunas[2]);
			p.getCadastroParticipante().setCelular((String) colunas[3]);
			
			p.getCadastroParticipante().setLogradouro((String) colunas[4]);
			p.getCadastroParticipante().setNumero((String) colunas[5]);
			p.getCadastroParticipante().setComplemento((String) colunas[6]);
			p.getCadastroParticipante().setBairro((String) colunas[7]);
			
			if(colunas[8] != null)
				p.getCadastroParticipante().setMunicipio( new Municipio( (String) colunas[8] )  );
			if(colunas[9] != null)
				p.getCadastroParticipante().setUnidadeFederativa( new UnidadeFederativa(0,  (String) colunas[9] ) );
			
			p.getCadastroParticipante().setCep((String) colunas[10]);
			
			result.add(p);
			
		}
	
		return result;
	    
	}
	
	
	/**
	 * <p>Retorna todos os participantes da atividade de extensão para gerar a listagem de presença.</p>
	 * 
	 * <p>Geralmente em formato de relatório em uma listagem só, então recuperar o mínimo possível de informações aqui porque a 
	 * listagem pode ser grange, 1.000 ou 2.000  participantes...</p>
	 * 
	 * @param idAcao
	 * @return
	 * @throws DAOException
	 */
	public List<ParticipanteAcaoExtensao> findAllParticipantesParaListaPresenca(Integer idAtividadeExtensao, Integer idSubAtividadeExtensao) throws DAOException {

		if(idAtividadeExtensao != null && idSubAtividadeExtensao != null){
			throw new DAOException("Não pode recuperar participantes de uma atividade ou sub atividade ao mesmo tempo.");
		}
		
		if(idAtividadeExtensao == null && idSubAtividadeExtensao == null){
			throw new DAOException("É preciso passar pelo menos uma atividade ou sub atividade.");
		}
		
		String projecao =  " p.id, cadastroParticipante.cpf, cadastroParticipante.passaporte, cadastroParticipante.nome ";
		
		StringBuilder hql = new StringBuilder(" SELECT DISTINCT " +projecao+
					 " FROM ParticipanteAcaoExtensao p " +
					 " INNER JOIN p.cadastroParticipante cadastroParticipante ");
		
		if(idAtividadeExtensao != null ){
			hql.append(" INNER JOIN p.atividadeExtensao atividade " +
				       " INNER JOIN atividade.projeto.situacaoProjeto si ");
		}
		
		if(idSubAtividadeExtensao != null ){
			hql.append(" INNER JOIN p.subAtividade subAtividade " +
				       " INNER JOIN subAtividade.atividade.projeto.situacaoProjeto si ");
		}
		
		hql.append(" INNER JOIN p.tipoParticipacao tp " +
				   " WHERE p.ativo = trueValue() " +                //  Retorna apenas os participantes que não foram removidos.
			       " AND si.id not in ( :EXTENSAO_GRUPO_INVALIDO, :PROJETO_BASE_GRUPO_INVALIDO ) "); // de projetos em execução ou concluidos
		
		if(idAtividadeExtensao != null ){
			hql.append(" AND atividade.id = :idAtividadeExtensao " +                    // da atividade passada  
				       " AND atividade.projeto.ativo = trueValue() ");                  // e se o projeto ainda tá ativo.
		}
		
		if(idSubAtividadeExtensao != null ){
			hql.append(" AND subAtividade.id = :idSubAtividadeExtensao " +                    // da atividade passada  
				       " AND subAtividade.atividade.projeto.ativo = trueValue() ");                  // e se o projeto ainda tá ativo.
		}
		
		hql.append(" ORDER BY cadastroParticipante.nome ");

		Query query = getSession().createQuery(hql.toString());
		
		if(idAtividadeExtensao != null ){
			query.setInteger( "idAtividadeExtensao", idAtividadeExtensao  );
		}
		if(idSubAtividadeExtensao != null ){
			query.setInteger( "idSubAtividadeExtensao", idSubAtividadeExtensao  );
		}
		
		query.setParameterList("EXTENSAO_GRUPO_INVALIDO", TipoSituacaoProjeto.EXTENSAO_GRUPO_INVALIDO);
		query.setParameterList("PROJETO_BASE_GRUPO_INVALIDO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_INVALIDO);

		@SuppressWarnings("unchecked")
		List<Object[]> lista = query.list();
		
		ArrayList<ParticipanteAcaoExtensao> result = new ArrayList<ParticipanteAcaoExtensao>();
		
		for (int a = 0; a < lista.size(); a++) {
			
			Object[] colunas = lista.get(a);
			
			ParticipanteAcaoExtensao p = new ParticipanteAcaoExtensao();
			p.setId((Integer) colunas[0]);
			p.setCadastroParticipante( new CadastroParticipanteAtividadeExtensao());
			p.getCadastroParticipante().setCpf((Long) colunas[1]);
			p.getCadastroParticipante().setPassaporte((String) colunas[2]);
			p.getCadastroParticipante().setNome((String) colunas[3]);
			
			result.add(p);
			
		}
	
		return result;
	    
	}
	
	
	/**
	 * <p>Retorna todos os participantes da atividade de extensão para mostrar a listagem para o coordenador.</p>
	 * 
	 * <p>Geralmente em formato de relatório em uma listagem só, então recuperar o mínimo possível de informações aqui porque a 
	 * listagem pode ser grange, 1.000 ou 2.000  participantes...</p>
	 * 
	 * @param idAcao
	 * @return
	 * @throws DAOException
	 */
	public List<ParticipanteAcaoExtensao> findAllParticipantesParaListagem(Integer idAtividadeExtensao, Integer idSubAtividadeExtensao) throws DAOException {

		if(idAtividadeExtensao != null && idSubAtividadeExtensao != null){
			throw new DAOException("Não pode recuperar participantes de uma atividade ou sub atividade ao mesmo tempo.");
		}
		
		if(idAtividadeExtensao == null && idSubAtividadeExtensao == null){
			throw new DAOException("É preciso passar pelo menos uma atividade ou sub atividade.");
		}
		
		String projecao =  " p.id, cadastroParticipante.cpf, cadastroParticipante.passaporte, cadastroParticipante.nome, cadastroParticipante.email, cadastroParticipante.dataNascimento, tp.descricao ";
		
		StringBuilder hql = new StringBuilder(" SELECT DISTINCT " +projecao+
					 " FROM ParticipanteAcaoExtensao p " +
					 " INNER JOIN p.cadastroParticipante cadastroParticipante ");
		
		if(idAtividadeExtensao != null ){
			hql.append(" INNER JOIN p.atividadeExtensao atividade " +
				       " INNER JOIN atividade.projeto.situacaoProjeto si ");
		}
		
		if(idSubAtividadeExtensao != null ){
			hql.append(" INNER JOIN p.subAtividade subAtividade " +
				       " INNER JOIN subAtividade.atividade.projeto.situacaoProjeto si ");
		}
		
		hql.append(" INNER JOIN p.tipoParticipacao tp " +
				   " WHERE p.ativo = trueValue() " +                //  Retorna apenas os participantes que não foram removidos.
			       " AND si.id not in ( :EXTENSAO_GRUPO_INVALIDO, :PROJETO_BASE_GRUPO_INVALIDO ) "); // de projetos em execução ou concluidos
		
		if(idAtividadeExtensao != null ){
			hql.append(" AND atividade.id = :idAtividadeExtensao " +                    // da atividade passada  
				       " AND atividade.projeto.ativo = trueValue() ");                  // e se o projeto ainda tá ativo.
		}
		
		if(idSubAtividadeExtensao != null ){
			hql.append(" AND subAtividade.id = :idSubAtividadeExtensao " +                    // da atividade passada  
				       " AND subAtividade.atividade.projeto.ativo = trueValue() ");                  // e se o projeto ainda tá ativo.
		}
		
		hql.append(" ORDER BY cadastroParticipante.nome ");

		Query query = getSession().createQuery(hql.toString());
		
		if(idAtividadeExtensao != null ){
			query.setInteger( "idAtividadeExtensao", idAtividadeExtensao  );
		}
		if(idSubAtividadeExtensao != null ){
			query.setInteger( "idSubAtividadeExtensao", idSubAtividadeExtensao  );
		}
		
		query.setParameterList("EXTENSAO_GRUPO_INVALIDO", TipoSituacaoProjeto.EXTENSAO_GRUPO_INVALIDO);
		query.setParameterList("PROJETO_BASE_GRUPO_INVALIDO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_INVALIDO);

		@SuppressWarnings("unchecked")
		List<Object[]> lista = query.list();
		
		ArrayList<ParticipanteAcaoExtensao> result = new ArrayList<ParticipanteAcaoExtensao>();
		
		for (int a = 0; a < lista.size(); a++) {
			
			Object[] colunas = lista.get(a);
			
			ParticipanteAcaoExtensao p = new ParticipanteAcaoExtensao();
			p.setId((Integer) colunas[0]);
			p.setCadastroParticipante( new CadastroParticipanteAtividadeExtensao());
			p.getCadastroParticipante().setCpf((Long) colunas[1]);
			p.getCadastroParticipante().setPassaporte((String) colunas[2]);
			p.getCadastroParticipante().setNome((String) colunas[3]);
			p.getCadastroParticipante().setEmail((String) colunas[4]);
			p.getCadastroParticipante().setDataNascimento((Date) colunas[5]);
			p.setTipoParticipacao(  new TipoParticipacaoAcaoExtensao((String) colunas[6])  );
			
			result.add(p);
			
		}
	
		return result;
	    
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Retorna Lista de ParticipanteAcaoExtensao onde o servidor informado 
	 * participa ou participou de alguma ação de extensão válida
	 * (aprovada pelo comitê de extensão).
	 * Utilizado para o subSistema de extensão.
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<ParticipanteAcaoExtensao> findInformacoesParticipanteByPessoaEmissaoCertificados(int idPessoa) throws DAOException {

			
			String projecao =  " p.id_participante_acao_extensao, p.frequencia, p.autorizacao_declaracao, p.autorizacao_certificado, p.observacao_certificado, cadastroParticipante.nome as nomeParticipante, cadastroParticipante.cpf, cadastroParticipante.passaporte, cadastroParticipante.data_nascimento, " +
					   " tipoParticipacao.id_tipo_participacao, tipoParticipacao.descricao as tipoParticipacao, "+
	                   " atividade.id_atividade, atividade.sequencia, projetoAtividade.id_projeto, projetoAtividade.titulo as tituloProjeto, projetoAtividade.ano, "+
	                   " projetoAtividade.data_inicio as inicioAtividade, projetoAtividade.data_fim as fimAtividade, projetoAtividade.ativo,  " +
	                   " COALESCE ( situacaoProjetoAtividade.id_tipo_situacao_projeto, situacaoProjetoSubAtivida.id_tipo_situacao_projeto ) as id_tipo_situacao_projeto," +
	                   " projetoAtividade.autorizar_certificado_gestor, " +
	                   " subAtividade.id_sub_atividade_extensao, subAtividade.titulo as tituloSub, subAtividade.inicio as inicioSub, subAtividade.fim as fimSub, "+
	                   
					   " COALESCE ( cursoEventoExtensaoAtividade.carga_horaria, subAtividade.carga_horaria ) as cargaHoraria, "+
	                   " COALESCE ( tipoAtividadeExtensao.id_tipo_atividade_extensao, tipoAtividadeExtensaoSubAtividade.id_tipo_sub_atividade_extensao ) as idTipoAtividade, "+
	                   " COALESCE ( unidadeAtividade.nome, unidadeSubAtividade.nome ) as nomeUnidade,  "+
	                   " COALESCE ( pessoaCoordenador.nome, pessoaCoordenadorSub.nome ) as nomeCoordenador,  "+
	                   " COALESCE ( categoriaCoordenador.id_categoria_membro, categoriaCoordenadorSub.id_categoria_membro ) as categoriaCoordenador  ";
	
			StringBuilder sql = new StringBuilder(" SELECT DISTINCT " +projecao+
						 " FROM extensao.participante_acao_extensao p " +
						 " INNER JOIN extensao.cadastro_participante_atividade_extensao cadastroParticipante ON p.id_cadastro_participante_atividade_extensao = cadastroParticipante.id_cadastro_participante_atividade_extensao"+
						 " INNER JOIN comum.pessoa pessoa                                                    ON ( pessoa.cpf_cnpj = cadastroParticipante.cpf OR ( cadastroParticipante.cpf is null AND pessoa.passaporte = cadastroParticipante.passaporte AND pessoa.data_nascimento = cadastroParticipante.data_nascimento ) ) "+						 
						 " INNER JOIN extensao.tipo_participacao tipoParticipacao                            ON tipoParticipacao.id_tipo_participacao = p.id_tipo_participacao");
			
			
			sql.append(" LEFT JOIN extensao.atividade atividade                               ON p.id_acao_extensao = atividade.id_atividade " +
					   " LEFT JOIN extensao.tipo_atividade_extensao tipoAtividadeExtensao     ON tipoAtividadeExtensao.id_tipo_atividade_extensao = atividade.id_tipo_atividade_extensao " +
					   " LEFT JOIN extensao.curso_evento cursoEventoExtensaoAtividade         ON atividade.id_curso_evento = cursoEventoExtensaoAtividade.id_curso_evento"+
				       " LEFT JOIN projetos.projeto projetoAtividade                          ON projetoAtividade.id_projeto = atividade.id_projeto "+
				       " LEFT JOIN projetos.tipo_situacao_projeto situacaoProjetoAtividade    ON projetoAtividade.id_tipo_situacao_projeto = situacaoProjetoAtividade.id_tipo_situacao_projeto "+
				       " LEFT JOIN projetos.membro_projeto coordenadorAtividade               ON projetoAtividade.id_coordenador = coordenadorAtividade.id_membro_projeto"+
				       " LEFT JOIN projetos.categoria_membro categoriaCoordenador             ON categoriaCoordenador.id_categoria_membro = coordenadorAtividade.id_categoria_membro "+
				       " LEFT JOIN comum.pessoa pessoaCoordenador                             ON pessoaCoordenador.id_pessoa = coordenadorAtividade.id_pessoa "+
				       " LEFT JOIN comum.unidade unidadeAtividade                             ON unidadeAtividade.id_unidade = projetoAtividade.id_unidade ");
			
			sql.append(" LEFT JOIN extensao.sub_atividade_extensao subAtividade                           ON p.id_sub_atividade_extensao = subAtividade.id_sub_atividade_extensao " +
					   " LEFT JOIN extensao.atividade atividadeDaSubAtividade                             ON subAtividade.id_atividade = atividadeDaSubAtividade.id_atividade" +
					   " LEFT JOIN extensao.tipo_sub_atividade_extensao tipoAtividadeExtensaoSubAtividade ON tipoAtividadeExtensaoSubAtividade.id_tipo_atividade = atividadeDaSubAtividade.id_tipo_atividade_extensao" +					   
					   " LEFT JOIN projetos.projeto projetoSubAtividade                                   ON projetoSubAtividade.id_projeto = atividadeDaSubAtividade.id_projeto "+
					   " LEFT JOIN projetos.tipo_situacao_projeto situacaoProjetoSubAtivida               ON projetoAtividade.id_tipo_situacao_projeto = situacaoProjetoSubAtivida.id_tipo_situacao_projeto "+
					   " LEFT JOIN projetos.membro_projeto coordenadorSubAtividade                        ON projetoSubAtividade.id_coordenador = coordenadorSubAtividade.id_membro_projeto "+
					   " LEFT JOIN projetos.categoria_membro categoriaCoordenadorSub                      ON categoriaCoordenadorSub.id_categoria_membro = coordenadorSubAtividade.id_categoria_membro "+
				       " LEFT JOIN comum.pessoa pessoaCoordenadorSub                                      ON pessoaCoordenadorSub.id_pessoa = coordenadorSubAtividade.id_pessoa"+
					   " LEFT JOIN comum.unidade unidadeSubAtividade                                      ON unidadeSubAtividade.id_unidade = projetoSubAtividade.id_unidade ");
			
			
			sql.append(" WHERE pessoa.id_pessoa = :idPessoa AND p.ativo = :true " +
					
				     "AND ( " + 
		    					" ( projetoSubAtividade.id_tipo_situacao_projeto not in (:EXTENSAO_GRUPO_INVALIDO, :PROJETO_BASE_GRUPO_INVALIDO) " + 
		    				" OR " +
		    					"   projetoSubAtividade.id_tipo_situacao_projeto not in (:EXTENSAO_GRUPO_INVALIDO, :PROJETO_BASE_GRUPO_INVALIDO) " +  
		    					" ) " +
		    		        " OR " +
		    		        	" ( situacaoProjetoAtividade.id_tipo_situacao_projeto not in (:EXTENSAO_GRUPO_INVALIDO, :PROJETO_BASE_GRUPO_INVALIDO) " + 
		    		        " OR " +
		    		        	"   situacaoProjetoAtividade.id_tipo_situacao_projeto not in (:EXTENSAO_GRUPO_INVALIDO, :PROJETO_BASE_GRUPO_INVALIDO) " +  
		    		        	" ) " +
						" ) ");
										      
			
			Query query = getSession().createSQLQuery(sql.toString());
			query.setInteger( "idPessoa", idPessoa  );
			query.setBoolean( "true", true  );
			query.setParameterList("EXTENSAO_GRUPO_INVALIDO", TipoSituacaoProjeto.EXTENSAO_GRUPO_INVALIDO);
			query.setParameterList("PROJETO_BASE_GRUPO_INVALIDO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_INVALIDO);
			
			List<Object[]> lista = query.list();
			
			List<ParticipanteAcaoExtensao> retorno = new ArrayList<ParticipanteAcaoExtensao>();
			
			
			for (int a = 0; a < lista.size(); a++) {
				
				Object[] colunas = lista.get(a);
				
				ParticipanteAcaoExtensao participante = new ParticipanteAcaoExtensao((Integer) colunas[0]);
				
				participante.setFrequencia( (Integer) colunas[1] );
				participante.setAutorizacaoDeclaracao( (Boolean) colunas[2] );
				participante.setAutorizacaoCertificado( (Boolean) colunas[3] );
				participante.setObservacaoCertificado( (String) colunas[4] );
				
				
				participante.setCadastroParticipante( new CadastroParticipanteAtividadeExtensao());
				participante.getCadastroParticipante().setNome((String) colunas[5]);
				participante.getCadastroParticipante().setCpf(       colunas[6] != null ? ((BigInteger) colunas[6]).longValue() : 0l );
				participante.getCadastroParticipante().setPassaporte((String) colunas[7]);
				participante.getCadastroParticipante().setDataNascimento( (Date) colunas[8]);
				
				participante.setTipoParticipacao(new TipoParticipacaoAcaoExtensao((Integer) colunas[9], (String) colunas[10]));
				
				if(colunas[11] != null){ // it is a activit
					participante.setAtividadeExtensao( new AtividadeExtensao((Integer) colunas[11]));
					participante.getAtividadeExtensao().setSequencia( (Integer) colunas[12] );
					participante.getAtividadeExtensao().setProjeto(new Projeto((Integer) colunas[13]));
					participante.getAtividadeExtensao().getProjeto().setTitulo((String) colunas[14] );
					participante.getAtividadeExtensao().getProjeto().setAno( colunas[15] != null ? ((Short) colunas[15]).intValue() : 0);
					
					participante.getAtividadeExtensao().getProjeto().setDataInicio((Date) colunas[16] );
					participante.getAtividadeExtensao().getProjeto().setDataFim((Date) colunas[17] );
					
					participante.getAtividadeExtensao().getProjeto().setAtivo( (Boolean) colunas[18]);
					participante.getAtividadeExtensao().getProjeto().setSituacaoProjeto( new TipoSituacaoProjeto( (Integer) colunas[19] ) );
					participante.getAtividadeExtensao().getProjeto().setAutorizarCertificadoGestor( (Boolean) colunas[20]);
					
					// aqui os indices são iguais ao campo das sub atividades //
					participante.getAtividadeExtensao().setCursoEventoExtensao(new CursoEventoExtensao());
					participante.getAtividadeExtensao().getCursoEventoExtensao().setCargaHoraria( (Integer) colunas[25] );
					participante.getAtividadeExtensao().setTipoAtividadeExtensao(  new TipoAtividadeExtensao((Integer) colunas[26]) ) ;
					participante.getAtividadeExtensao().getProjeto().setUnidade(  new Unidade(0, 0l, (String) colunas[27], "") ) ;
					
					participante.getAtividadeExtensao().getProjeto().setCoordenador( new MembroProjeto() );
					participante.getAtividadeExtensao().getProjeto().getCoordenador().setPessoa( new Pessoa(0, (String) colunas[28]) );
					participante.getAtividadeExtensao().getProjeto().getCoordenador().setCategoriaMembro( new CategoriaMembro( colunas[29] != null ? (Integer) colunas[29] : 0 ));
				}
				
				if(colunas[21] != null){ // it is a mini activit
					participante.setSubAtividade( new SubAtividadeExtensao((Integer) colunas[21]));
					participante.getSubAtividade().setTitulo( (String) colunas[22]  );
					participante.getSubAtividade().setInicio( (Date) colunas[23]  );
					participante.getSubAtividade().setFim( (Date) colunas[24]  );
					
					// aqui os indices são iguais ao campo das atividades //
					
					participante.getSubAtividade().setCargaHoraria( (Integer) colunas[25] );
					
					participante.getSubAtividade().setAtividade(new AtividadeExtensao());
					participante.getSubAtividade().getAtividade().setTipoAtividadeExtensao(  new TipoAtividadeExtensao((Integer) colunas[26]) ) ;
					
					participante.getSubAtividade().getAtividade().setProjeto(new Projeto());
					participante.getSubAtividade().getAtividade().getProjeto().setUnidade(  new Unidade(0, 0l, (String) colunas[27], "") ) ;
					participante.getSubAtividade().getAtividade().getProjeto().setCoordenador( new MembroProjeto() );
					participante.getSubAtividade().getAtividade().getProjeto().getCoordenador().setPessoa( new Pessoa(0, (String) colunas[28]) );
					participante.getSubAtividade().getAtividade().getProjeto().getCoordenador().setCategoriaMembro( new CategoriaMembro( colunas[29] != null ? (Integer) colunas[29] : 0 ));
				}
				
				retorno.add(participante);
				
			}
		
			return retorno;
			
	}


	/**
	 * Retorna Lista de ParticipanteAcaoExtensao onde o CPF do discente informado 
	 * participa ou participou de alguma ação de extensão válida
	 * (aprovada pelo comitê de extensão).
	 * Utilizado para o subSistema de extensão.
	 * 
	 * @param cpfDiscente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ParticipanteAcaoExtensao> findParticipanteByCpfPassaporteDataNascimento(Long cpf, String passaporte, Date dataNascimento) throws DAOException {

		String hql = " select p " +
					 " from ParticipanteAcaoExtensao p " +
					 " inner join p.atividadeExtensao a " +
					 " inner join a.projeto.situacaoProjeto si " +
					 " inner join p.cadastroParticipante cad ";
					
		if ( cpf != null )
			hql += " where (cad.cpf = :cpf ) ";
		else
			hql += " where (cad.passaporte = :passaporte AND cad.dataNascimento = :dataNascimento) ";
		
		hql += " and p.ativo = trueValue() " +
			   " and si.id not in (:EXTENSAO_GRUPO_INVALIDO, :PROJETO_BASE_GRUPO_INVALIDO) " +
			   " and a.projeto.ativo = trueValue() " +
			   " and a.sequencia > 0 " + //sequência > 0 = ação aprovada pelo comitê 
			   " order by a.id ";

		Query query = getSession().createQuery(hql);
		if ( cpf != null )
			query.setLong( "cpf", cpf );
		else{
			query.setString( "passaporte", passaporte );
			query.setDate( "dataNascimento", dataNascimento );
		}
		
		query.setParameterList("EXTENSAO_GRUPO_INVALIDO", TipoSituacaoProjeto.EXTENSAO_GRUPO_INVALIDO);
		query.setParameterList("PROJETO_BASE_GRUPO_INVALIDO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_INVALIDO);


		return query.list();
	}

	/**
	 * Retorna um lista de participantes a partir de um id da ação de extensão.
	 * 
	 * @param idAcao
	 * @return
	 * @throws DAOException
	 */
	public Collection<ParticipanteAcaoExtensao> findByAcao(int idAcao) throws DAOException {

		String hql = "select p.id, p.ativo, " +
						" p.frequencia, p.autorizacaoCertificado, p.autorizacaoDeclaracao, " +
						" cadastroParticipante.cpf, cadastroParticipante.passaporte, cadastroParticipante.nome, cadastroParticipante.email, cadastroParticipante.cep, cadastroParticipante.dataNascimento, " +
						" cadastroParticipante.logradouro, " +
	//					" d.id, d.matricula, " +
	//					" pessoa_d.id, pessoa_d.nome, pessoa_d.cpf_cnpj, pessoa_d.telefone,  " +
	//					" serv.id, " +
	//					" pessoa_s.id, pessoa_s.nome, pessoa_s.cpf_cnpj,  " +
						" tp, a, p.tipoParticipante " +

						" from ParticipanteAcaoExtensao p " +
						" inner join p.cadastroParticipante cadastroParticipante " +
						" inner join p.atividadeExtensao a " +
						" inner join a.projeto.situacaoProjeto si " +
						" inner join p.tipoParticipacao tp " +
//						" left join p.discente d " +
//						" left join d.pessoa pessoa_d " +
//						" left join p.servidor serv " +
//						" left join serv.pessoa pessoa_s " +
						" where p.ativo = trueValue() " +//
						" and si.id not in (:EXTENSAO_GRUPO_INVALIDO, :PROJETO_BASE_GRUPO_INVALIDO) " +//
						" and a.id = :idAcao " +//
						" and a.projeto.ativo = trueValue() " +//
						" order by ( cadastroParticipante.nome )";

			Query query = getSession().createQuery(hql);
			query.setInteger( "idAcao", idAcao  );
			query.setParameterList("EXTENSAO_GRUPO_INVALIDO", TipoSituacaoProjeto.EXTENSAO_GRUPO_INVALIDO);
			query.setParameterList("PROJETO_BASE_GRUPO_INVALIDO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_INVALIDO);

			@SuppressWarnings("unchecked")
			List<Object[]> lista = query.list();
			
			ArrayList<ParticipanteAcaoExtensao> result = new ArrayList<ParticipanteAcaoExtensao>();
			
			for (int a = 0; a < lista.size(); a++) {
				int col = 0;
				Object[] colunas = lista.get(a);
				ParticipanteAcaoExtensao p = new ParticipanteAcaoExtensao();
				p.setId((Integer) colunas[col++]);
				p.setAtivo((Boolean) colunas[col++]);
				p.setFrequencia((Integer) colunas[col++]);
				p.setAutorizacaoCertificado((Boolean) colunas[col++]);
				p.setAutorizacaoDeclaracao((Boolean) colunas[col++]);
				
				p.setCadastroParticipante( new CadastroParticipanteAtividadeExtensao());
				p.getCadastroParticipante().setCpf((Long) colunas[col++]);
				p.getCadastroParticipante().setPassaporte((String) colunas[col++]);
				p.getCadastroParticipante().setNome((String) colunas[col++]);
				p.getCadastroParticipante().setEmail((String) colunas[col++]);
				p.getCadastroParticipante().setCep((String) colunas[col++]);
				p.getCadastroParticipante().setDataNascimento((Date) colunas[col++]);
				p.getCadastroParticipante().setLogradouro((String) colunas[col++]);
				
				
//				p.setDiscente(null);
//				p.setServidor(null);
//				
//				// É discente
//				if(colunas[13] != null) {
//					p.setDiscente(new Discente((Integer) colunas[col++]));
//					p.getDiscente().setMatricula((Long) colunas[col++]);
//
//					if(colunas[15] != null) {
//						col = 15;
//						p.getDiscente().setPessoa(new Pessoa((Integer) colunas[col++]));
//						p.getDiscente().getPessoa().setNome((String) colunas[col++]);
//						p.getDiscente().getPessoa().setCpf_cnpj((Long) colunas[col++]);
//						p.getDiscente().getPessoa().setTelefone((String)colunas[col++]);
//					} 
//				}
//				
//				// É servidor
//				if(colunas[19] != null) {
//					col = 19;
//					p.setServidor(new Servidor((Integer) colunas[col++]));
//					
//					if (colunas[20] != null) {
//						col = 20;
//						p.getServidor().setPessoa(new Pessoa((Integer) colunas[col++]));
//						p.getServidor().getPessoa().setNome((String) colunas[col++]);
//						p.getServidor().getPessoa().setCpf_cnpj((Long) colunas[col++]);
//					}
//				} 
				
				
				p.setTipoParticipacao((TipoParticipacaoAcaoExtensao) colunas[col++]);
				
				p.setAtividadeExtensao((AtividadeExtensao) colunas[col++]);
				
				p.setTipoParticipante((Integer) colunas[col++]);
				
				result.add(p);
			}
	
			return result;
	    
	}
	
	
//	/**
//	 * Retorna um lista de participantes a partir de um id da ação de extensão.
//	 * 
//	 * @param idAcao
//	 * @return
//	 * @throws DAOException
//	 */
//	public Collection<ParticipanteAcaoExtensao> findByAcaoFiltrada(int idAcao, ParticipanteAcaoExtensao participante) throws DAOException {
//	    
//		try {
//
//		String hql = "select p.id, p.ativo, p.cpf, p.passaporte, p.nome, p.email, p.cep, " +
//						"p.endereco, p.instituicao, p.frequencia, p.autorizacaoCertificado, p.autorizacaoDeclaracao, " +
//						"d.id, d.matricula, " +
//						"pessoa_d.id, pessoa_d.nome, pessoa_d.cpf_cnpj, pessoa_d.telefone,  " +
//						"serv.id, " +
//						"pessoa_s.id, pessoa_s.nome, pessoa_s.cpf_cnpj,  " +
//						"tp, a, p.tipoParticipante " +
//						"from ParticipanteAcaoExtensao p " +
//						"inner join p.atividadeExtensao a " +
//						"inner join a.projeto.situacaoProjeto si " +
//						"inner join p.tipoParticipacao tp " +
//						"left join p.discente d " +
//						"left join d.pessoa pessoa_d " +
//						"left join p.servidor serv " +
//						"left join serv.pessoa pessoa_s " +
//						"where p.ativo = trueValue() " +//
//						"and si.id not in (:EXTENSAO_GRUPO_INVALIDO, :PROJETO_BASE_GRUPO_INVALIDO) " +//
//						"and a.id = :idAcao " +//
//						"and a.projeto.ativo = trueValue() "; //
//						
//						if(participante.getInstituicao()!= null){
//							hql += "and "+UFRNUtils.toAsciiUpperUTF8("p.instituicao") + " like :INSTITUICAO ";//
//						}
//						
//						if(participante.getNome()!= null){
//							hql += "and (("+UFRNUtils.toAsciiUpperUTF8("p.nome") + " like :PNOME) or ( "+UFRNUtils.toAsciiUpperUTF8("pessoa_d.nome") + " like :PNOME) or " +
//									"("+UFRNUtils.toAsciiUpperUTF8("pessoa_s.nome") + " like :PNOME)) ";//
//						}
//						
//						if(participante.getUnidadeFederativa() != null && participante.getMunicipio() != null)
//							if(participante.getUnidadeFederativa().getId() != 0 && participante.getMunicipio().getId() != 0){
//								hql += "and p.unidadeFederativa.id = :uf and p.municipio.id = :municipio ";
//						}
//				hql += "order by (pessoa_d.nomeAscii || pessoa_s.nomeAscii || p.nome) ";
//				
//				
//			Query query = getSession().createQuery(hql);
//			query.setInteger( "idAcao", idAcao  );
//			query.setParameterList("EXTENSAO_GRUPO_INVALIDO", TipoSituacaoProjeto.EXTENSAO_GRUPO_INVALIDO);
//			query.setParameterList("PROJETO_BASE_GRUPO_INVALIDO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_INVALIDO);
//			if(participante.getNome() != null){
//				query.setString("PNOME", "%" + StringUtils.toAscii(participante.getNome().toUpperCase()) + "%");
//			}
//			if(participante.getInstituicao() != null){
//				query.setString("INSTITUICAO", "%" + StringUtils.toAscii(participante.getInstituicao().toUpperCase()) + "%");
//			}
//			if(participante.getUnidadeFederativa() != null && participante.getMunicipio() != null)
//				if(participante.getUnidadeFederativa().getId() != 0 && participante.getMunicipio().getId() != 0){
//					query.setInteger("uf", participante.getUnidadeFederativa().getId());
//					query.setInteger("municipio", participante.getMunicipio().getId());
//			}
//			
//			List lista = query.list();
//			ArrayList<ParticipanteAcaoExtensao> result = new ArrayList<ParticipanteAcaoExtensao>();
//			for (int a = 0; a < lista.size(); a++) {
//				int col = 0;
//				Object[] colunas = (Object[]) lista.get(a);
//				ParticipanteAcaoExtensao p = new ParticipanteAcaoExtensao();
//				p.setId((Integer) colunas[col++]);
//				p.setAtivo((Boolean) colunas[col++]);
//				p.setCpf((Long) colunas[col++]);
//				p.setPassaporte((String) colunas[col++]);
//				p.setNome((String) colunas[col++]);
//				p.setEmail((String) colunas[col++]);
//				p.setCep((String) colunas[col++]);
//				p.setEndereco((String) colunas[col++]);
//				p.setInstituicao((String) colunas[col++]);
//				p.setFrequencia((Integer) colunas[col++]);
//				p.setAutorizacaoCertificado((Boolean) colunas[col++]);
//				p.setAutorizacaoDeclaracao((Boolean) colunas[col++]);
//				
//				p.setDiscente(null);
//				p.setServidor(null);
//				
//				// É discente
//				if(colunas[12] != null) {
//					p.setDiscente(new Discente((Integer) colunas[col++]));
//					p.getDiscente().setMatricula((Long) colunas[col++]);
//
//					if(colunas[14] != null) {
//						col = 14;
//						p.getDiscente().setPessoa(new Pessoa((Integer) colunas[col++]));
//						p.getDiscente().getPessoa().setNome((String) colunas[col++]);
//						p.getDiscente().getPessoa().setCpf_cnpj((Long) colunas[col++]);
//						p.getDiscente().getPessoa().setTelefone((String)colunas[col++]);
//					} 
//				}
//				
//				// É servidor
//				if(colunas[18] != null) {
//					col = 18;
//					p.setServidor(new Servidor((Integer) colunas[col++]));
//					
//					if (colunas[19] != null) {
//						col = 19;
//						p.getServidor().setPessoa(new Pessoa((Integer) colunas[col++]));
//						p.getServidor().getPessoa().setNome((String) colunas[col++]);
//						p.getServidor().getPessoa().setCpf_cnpj((Long) colunas[col++]);
//					}
//				} 
//				
//				col = 22;
//				
//				p.setTipoParticipacao((TipoParticipacaoAcaoExtensao) colunas[col++]);
//				
//				p.setAcaoExtensao((AtividadeExtensao) colunas[col++]);
//				
//				p.setTipoParticipante((Integer) colunas[col++]);
//				
//				result.add(p);
//			}
//	
//			return result;
//			
//		} catch (Exception e) {
//			throw new DAOException(e.getMessage(), e);
//		}
//
//	    
//	}
	
	
	
	
	
//	/**
//	 * Retorna um lista de participantes a partir de um id da ação de extensão.
//	 * 
//	 * @param idSubAcao
//	 * @return
//	 * @throws DAOException
//	 */
//	@SuppressWarnings("unchecked")
//	public Collection<ParticipanteAcaoExtensao> findBySubAcao(int idSubAcao) throws DAOException {
//	    
//		try {
//
//		String hql = "select p.id, p.ativo, p.cpf, p.passaporte, p.nome, p.email, p.cep, p.dataNascimento," +
//						"p.endereco, p.instituicao, p.frequencia, p.autorizacaoCertificado, p.autorizacaoDeclaracao, " +
//						"d.id, d.matricula, " +
//						"pessoa_d.id, pessoa_d.nome, pessoa_d.cpf_cnpj, pessoa_d.telefone,  " +
//						"serv.id, " +
//						"pessoa_s.id, pessoa_s.nome, pessoa_s.cpf_cnpj,  " +
//						"tp, subAtv, p.tipoParticipante " +
//						"from ParticipanteAcaoExtensao p " +						
//						"inner join p.subAtividade subAtv " +
//						"inner join subAtv.atividade a " +
//						"inner join a.projeto.situacaoProjeto si " +
//						"inner join p.tipoParticipacao tp " +
//						"left join p.discente d " +
//						"left join d.pessoa pessoa_d " +
//						"left join p.servidor serv " +
//						"left join serv.pessoa pessoa_s " +
//						"where p.ativo = trueValue() " +//
//						"and si.id not in (:EXTENSAO_GRUPO_INVALIDO, :PROJETO_BASE_GRUPO_INVALIDO) " +//
//						"and subAtv.id = :idSubAcao " +//
//						"and a.projeto.ativo = trueValue() " +//
//						"order by (pessoa_d.nomeAscii || pessoa_s.nomeAscii || p.nome)";
//
//			Query query = getSession().createQuery(hql);
//			query.setInteger( "idSubAcao", idSubAcao  );
//			query.setParameterList("EXTENSAO_GRUPO_INVALIDO", TipoSituacaoProjeto.EXTENSAO_GRUPO_INVALIDO);
//			query.setParameterList("PROJETO_BASE_GRUPO_INVALIDO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_INVALIDO);
//
//			List lista = query.list();
//			ArrayList<ParticipanteAcaoExtensao> result = new ArrayList<ParticipanteAcaoExtensao>();
//			for (int a = 0; a < lista.size(); a++) {
//				int col = 0;
//				Object[] colunas = (Object[]) lista.get(a);
//				ParticipanteAcaoExtensao p = new ParticipanteAcaoExtensao();
//				p.setId((Integer) colunas[col++]);
//				p.setAtivo((Boolean) colunas[col++]);
//				p.setCpf((Long) colunas[col++]);
//				p.setPassaporte((String) colunas[col++]);
//				p.setNome((String) colunas[col++]);
//				p.setEmail((String) colunas[col++]);
//				p.setCep((String) colunas[col++]);
//				p.setDataNascimento((Date) colunas[col++]);
//				p.setEndereco((String) colunas[col++]);
//				p.setInstituicao((String) colunas[col++]);
//				p.setFrequencia((Integer) colunas[col++]);
//				p.setAutorizacaoCertificado((Boolean) colunas[col++]);
//				p.setAutorizacaoDeclaracao((Boolean) colunas[col++]);
//				
//				p.setDiscente(null);
//				p.setServidor(null);
//				
//				// É discente
//				if(colunas[13] != null) {
//					p.setDiscente(new Discente((Integer) colunas[col++]));
//					p.getDiscente().setMatricula((Long) colunas[col++]);
//
//					if(colunas[15] != null) {
//						col = 15;
//						p.getDiscente().setPessoa(new Pessoa((Integer) colunas[col++]));
//						p.getDiscente().getPessoa().setNome((String) colunas[col++]);
//						p.getDiscente().getPessoa().setCpf_cnpj((Long) colunas[col++]);
//						p.getDiscente().getPessoa().setTelefone((String)colunas[col++]);
//					} 
//				}
//				
//				// É servidor
//				if(colunas[19] != null) {
//					col = 19;
//					p.setServidor(new Servidor((Integer) colunas[col++]));
//					
//					if (colunas[20] != null) {
//						col = 20;
//						p.getServidor().setPessoa(new Pessoa((Integer) colunas[col++]));
//						p.getServidor().getPessoa().setNome((String) colunas[col++]);
//						p.getServidor().getPessoa().setCpf_cnpj((Long) colunas[col++]);
//					}
//				} 
//				
//				col = 23;
//				
//				p.setTipoParticipacao((TipoParticipacaoAcaoExtensao) colunas[col++]);
//				
//				p.setSubAtividade((SubAtividadeExtensao) colunas[col++]);
//				
//				p.setTipoParticipante((Integer) colunas[col++]);
//				
//				result.add(p);
//			}
//	
//			return result;
//			
//		} catch (Exception e) {
//			throw new DAOException(e.getMessage(), e);
//		}
//
//	    
//	}
	
	
	
//	/**
//	 * Retorna um lista de participantes a partir de um id da ação de extensão.
//	 * 
//	 * @param idSubAcao
//	 * @return
//	 * @throws DAOException
//	 */
//	public Collection<ParticipanteAcaoExtensao> findBySubAcaoFiltrada(int idSubAcao, ParticipanteAcaoExtensao participante) throws DAOException {
//	    
//		try {
//
//		String hql = "select p.id, p.ativo, p.cpf, p.passaporte, p.nome, p.email, p.cep, " +
//						"p.endereco, p.instituicao, p.frequencia, p.autorizacaoCertificado, p.autorizacaoDeclaracao, " +
//						"d.id, d.matricula, " +
//						"pessoa_d.id, pessoa_d.nome, pessoa_d.cpf_cnpj, pessoa_d.telefone,  " +
//						"serv.id, " +
//						"pessoa_s.id, pessoa_s.nome, pessoa_s.cpf_cnpj,  " +
//						"tp, a, p.tipoParticipante " +
//						"from ParticipanteAcaoExtensao p " +
//						"inner join p.subAtividade subAtv " +
//						"inner join subAtv.atividade a " +
//						"inner join a.projeto.situacaoProjeto si " +
//						"inner join p.tipoParticipacao tp " +
//						"left join p.discente d " +
//						"left join d.pessoa pessoa_d " +
//						"left join p.servidor serv " +
//						"left join serv.pessoa pessoa_s " +
//						"where p.ativo = trueValue() " +//
//						"and si.id not in (:EXTENSAO_GRUPO_INVALIDO, :PROJETO_BASE_GRUPO_INVALIDO) " +//
//						"and subAtv.id = :idAcao " +//
//						"and a.projeto.ativo = trueValue() "; //
//						
//						if(participante.getInstituicao()!= null){
//							hql += "and p.instituicao like :INSTITUICAO ";//
//						}
//						
//						if(participante.getNome()!= null){
//							hql += "and ((p.nome like :PNOME) or (pessoa_d.nome like :PNOME) or (pessoa_s.nome like :PNOME)) ";//
//						}
//						
//						if(participante.getUnidadeFederativa() != null && participante.getMunicipio() != null)
//							if(participante.getUnidadeFederativa().getId() != 0 && participante.getMunicipio().getId() != 0){
//								hql += "and p.unidadeFederativa.id = :uf and p.municipio.id = :municipio ";
//						}
//				hql += "order by (pessoa_d.nomeAscii || pessoa_s.nomeAscii || p.nome) ";
//				
//				
//			Query query = getSession().createQuery(hql);
//			query.setInteger( "idAcao", idSubAcao  );
//			query.setParameterList("EXTENSAO_GRUPO_INVALIDO", TipoSituacaoProjeto.EXTENSAO_GRUPO_INVALIDO);
//			query.setParameterList("PROJETO_BASE_GRUPO_INVALIDO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_INVALIDO);
//			if(participante.getNome() != null){
//				query.setString("PNOME", participante.getNome()+"%");
//			}
//			if(participante.getInstituicao() != null){
//				query.setString("INSTITUICAO", participante.getInstituicao()+"%"); 
//			}
//			if(participante.getUnidadeFederativa() != null && participante.getMunicipio() != null)
//				if(participante.getUnidadeFederativa().getId() != 0 && participante.getMunicipio().getId() != 0){
//					query.setInteger("uf", participante.getUnidadeFederativa().getId());
//					query.setInteger("municipio", participante.getMunicipio().getId());
//			}
//			
//			List lista = query.list();
//			ArrayList<ParticipanteAcaoExtensao> result = new ArrayList<ParticipanteAcaoExtensao>();
//			for (int a = 0; a < lista.size(); a++) {
//				int col = 0;
//				Object[] colunas = (Object[]) lista.get(a);
//				ParticipanteAcaoExtensao p = new ParticipanteAcaoExtensao();
//				p.setId((Integer) colunas[col++]);
//				p.setAtivo((Boolean) colunas[col++]);
//				p.setCpf((Long) colunas[col++]);
//				p.setPassaporte((String) colunas[col++]);
//				p.setNome((String) colunas[col++]);
//				p.setEmail((String) colunas[col++]);
//				p.setCep((String) colunas[col++]);
//				p.setEndereco((String) colunas[col++]);
//				p.setInstituicao((String) colunas[col++]);
//				p.setFrequencia((Integer) colunas[col++]);
//				p.setAutorizacaoCertificado((Boolean) colunas[col++]);
//				p.setAutorizacaoDeclaracao((Boolean) colunas[col++]);
//				
//				p.setDiscente(null);
//				p.setServidor(null);
//				
//				// É discente
//				if(colunas[12] != null) {
//					p.setDiscente(new Discente((Integer) colunas[col++]));
//					p.getDiscente().setMatricula((Long) colunas[col++]);
//
//					if(colunas[14] != null) {
//						col = 14;
//						p.getDiscente().setPessoa(new Pessoa((Integer) colunas[col++]));
//						p.getDiscente().getPessoa().setNome((String) colunas[col++]);
//						p.getDiscente().getPessoa().setCpf_cnpj((Long) colunas[col++]);
//						p.getDiscente().getPessoa().setTelefone((String)colunas[col++]);
//					} 
//				}
//				
//				// É servidor
//				if(colunas[18] != null) {
//					col = 18;
//					p.setServidor(new Servidor((Integer) colunas[col++]));
//					
//					if (colunas[19] != null) {
//						col = 19;
//						p.getServidor().setPessoa(new Pessoa((Integer) colunas[col++]));
//						p.getServidor().getPessoa().setNome((String) colunas[col++]);
//						p.getServidor().getPessoa().setCpf_cnpj((Long) colunas[col++]);
//					}
//				} 
//				
//				col = 22;
//				
//				p.setTipoParticipacao((TipoParticipacaoAcaoExtensao) colunas[col++]);
//				
//				p.setAcaoExtensao((AtividadeExtensao) colunas[col++]);
//				
//				p.setTipoParticipante((Integer) colunas[col++]);
//				
//				result.add(p);
//			}
//	
//			return result;
//			
//		} catch (Exception e) {
//			throw new DAOException(e.getMessage(), e);
//		}
//
//	    
//	}
	
	
	
}