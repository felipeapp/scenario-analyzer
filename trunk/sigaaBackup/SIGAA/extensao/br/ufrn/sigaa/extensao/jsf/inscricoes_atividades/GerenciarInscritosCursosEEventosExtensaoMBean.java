/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 20/11/2012
 * 
 */
package br.ufrn.sigaa.extensao.jsf.inscricoes_atividades;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.AbstractMovimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.comum.gru.dominio.GuiaRecolhimentoUniao;
import br.ufrn.comum.gru.negocio.GuiaRecolhimentoUniaoHelper;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.arq.dao.questionario.QuestionarioRespostasDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.extensao.dao.SubAtividadeExtensaoDao;
import br.ufrn.sigaa.extensao.dao.inscricoes_atividades.InscricaoAtividadeExtensaoDao;
import br.ufrn.sigaa.extensao.dao.inscricoes_atividades.InscricaoParticipanteAtividadeExtensaoDao;
import br.ufrn.sigaa.extensao.dao.inscricoes_atividades.InscricaoParticipanteSubAtividadeExtensaoDao;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.CadastroParticipanteAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividadeParticipante;
import br.ufrn.sigaa.extensao.dominio.StatusInscricaoParticipante;
import br.ufrn.sigaa.extensao.dominio.SubAtividadeExtensao;
import br.ufrn.sigaa.extensao.negocio.inscricoes_atividades.MovimentoAprovarParticipantesInscritosAtividade;
import br.ufrn.sigaa.extensao.negocio.inscricoes_atividades.MovimentoAprovarParticipantesInscritosSubAtividade;
import br.ufrn.sigaa.extensao.negocio.inscricoes_atividades.MovimentoConfirmaPagamentoGRUAtividadesExtensaoManualmente;
import br.ufrn.sigaa.extensao.negocio.inscricoes_atividades.MovimentoEstornaPagamentoGRUCursosEventosExtensao;
import br.ufrn.sigaa.extensao.negocio.inscricoes_atividades.MovimentoRecusarParticipantesInscritosAtividade;
import br.ufrn.sigaa.extensao.negocio.inscricoes_atividades.MovimentoRecusarParticipantesInscritosSubAtividade;
import br.ufrn.sigaa.questionario.dominio.QuestionarioRespostas;
import br.ufrn.sigaa.questionario.dominio.TipoQuestionario;

/**
 * <p> MBean para gerencia os inscritos em cursos e eventos de extensão pelo coordenador. </p>
 *
 * <p> Gerencirar inscritos se resume a aprovar, visualizar questionário respondido no momento da 
 *  inscrição, reenviar senha de acesso, visualizar arquivo enviado, aprovar o pagamento, reailzar a inscrição por um participante. </>
 *
 * <p>Após a aprovação é criado um participante na ação de extensão. A partir daí para emitir declarações, certificados, etc, 
 * é tudo na parte de gerenciar participantes, não mas genenciar inscritos. !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! </p>
 * 
 * <p>Todas as operações que podem ser realizadas com os participantes na atividade podem ser também realizadas 
 * com os participantes na mini atividades. Não deixar nada foltando para o usuário não reclamar!!!!</p>
 * 
 * <p> <strong> NÃO HÁ NECESSIDADE DE CRIAR MAIS OPERAÇÕES AQUI. PROCURAR DEIXAR A MENOR QUANTIDADE DE OPERAÇÕES POSSÍVEIS. POR FAVOR !!!! </strong> </p>
 * 
 * @author jadson
 *
 */
@Component("gerenciarInscritosCursosEEventosExtensaoMBean")
@Scope("request")
public class GerenciarInscritosCursosEEventosExtensaoMBean extends SigaaAbstractController<InscricaoAtividadeParticipante>{
	
	/** Lista os inscritos para atividade ou sub atividade para gerenciá-los. */
	private static final String LISTA_INSCRITOS_CURSOS_EVENTOS_EXTENSAO = "/extensao/GerenciarInscricoes/listaInscritosCursosEventosExtensao.jsp";
	
	/** Página padrão para visualizar os dados do cadastro de um participante de extensão. */
	private static final String VISUALIZA_DADOS_CADASTRO_PARTICIPANTE = "/extensao/GerenciarInscricoes/visualizaDadosCadastroParticipante.jsp";
	
	
	/** Página para confirmar o pagamento manualmente de um curso e evento. Caso a confirmação do banco demore a chegar, poís existe a confirmaçaõ automática. */
	public static final String PAGINA_CONFIRMAR_PAGAMENTO = "/extensao/GerenciarInscricoes/confirmarPagamentoCursosEventosManualmente.jsp";

	
	/** Página para confirmar o estorno pagamento de um curso e evento. Como devolver o dinheiro paga não faz parte dessa operação */
	public static final String PAGINA_CONFIRMAR_ESTORNO_PAGAMENTO = "/extensao/GerenciarInscricoes/confirmarEstornoPagamentoCursosEventos.jsp";
	
	
	
	/** Representa os campos de ordenação da listagem de inscrições */
	public enum CamposOrdenacaoListaInscricoes{
		
		INSCRICOES_RECENTES  (0, "Inscrições Mais Recentes"  , " participante.dataCadastro DESC "),
		INSCRICAO_ANTIGAS  (1, "Inscrições Mais Antigas"  , " participante.dataCadastro ASC "),
		NOME_INSCRITO   (2, "Nome do Inscrito"   , " cadastroParticipante.nome "),
		INSTITUICAO     (3, "Instituição"        , " participante.instituicao "),
		STATUS_PAGAMENTO(4, "Status do Pagamento", " participante.statusPagamento ");
	
		private int valor;
		
		private String campoOrdenacao;
		
		private String descricao;
		
		private CamposOrdenacaoListaInscricoes(int valor, String descricao, String campoOrdenacao){
			this.valor = valor;
			this.campoOrdenacao = campoOrdenacao;
			this.descricao = descricao;
		}

		
		
		public int getValor() {
			return valor;
		}

		public String getCampoOrdenacao() {
			return campoOrdenacao;
		}

		public String getDescricao() {
			return descricao;
		}
		
		/**
		 * Retorna o compo de ordenação a partir do valor.
		 * @param status
		 * @return
		 */
		public static CamposOrdenacaoListaInscricoes getCampoOrdenacao(int valor) {
			switch (valor) {
			case 0: return INSCRICOES_RECENTES;
			case 1: return INSCRICAO_ANTIGAS;
			case 2: return NOME_INSCRITO;
			case 3: return INSTITUICAO;
			case 4: return STATUS_PAGAMENTO;
			default:return INSCRICOES_RECENTES;
			}
		}
		
		/** Os campos pelos quias os usuários podem ordenar os resultados na listagem de inscritos */
		public static final CamposOrdenacaoListaInscricoes[] CAMPOS_ORDENACAO = {INSCRICOES_RECENTES, INSCRICAO_ANTIGAS, NOME_INSCRITO, INSTITUICAO, STATUS_PAGAMENTO };
	}
	
	
	/*** A listagem das inscrições realizadas, mesmo aquelas que foram canceladas ou recusadas pelo coordenador */
	private List<InscricaoAtividadeParticipante> inscricoes;
	
	
	/** Utilizando para visualização das respostas dos questionários das inscrições. */
	private QuestionarioRespostas questionarioRespondido;
	
	/** Guarda o valor do status das inscrição que o usuário quer recuperar na listagem. 
	 * Por padrão mostrar apenas as inscrições com status ativos. */
	private int idStatusInscricaoParticipante = -2;
	
	/** A atividade selecionada pelo coordenador pra gerenciar seus inscritos */
	private AtividadeExtensao atividadeSelecionada;
	
	/** A sub atividade selecionada pelo coordenador pra gerenciar seus inscritos */
	private SubAtividadeExtensao subAtividadeSelecionada;
	
	/** Guarda a quantidade de participantes inscritos  */
	private int quantidadeParticipanteInscritos = 0;
	/** Guarda a quantidade de participantes aprovados  */
	private int quantidadeParticipanteAprovados = 0;
	/** Guarda a quantidade de participantes cancelados  */
	private int quantidadeParticipanteCancelados = 0;
	/** Guarda a quantidade de participantes recusados  */
	private int quantidadeParticipanteRecusados = 0;
	
	
	/** Guarda a lista de inscrições selecionadas pelo coordenador, seja para aprovar, recusar ou confirmar pagamento. */
	private List<InscricaoAtividadeParticipante> inscricoesSelecionadas;
	
	/** Guarda o valor do campo escolhido pelo usuário para ordenar a consulta */
	private int valorCampoOrdenacao = CamposOrdenacaoListaInscricoes.INSCRICOES_RECENTES.getValor();
	
	/**
	 * Inicia a listagem dos participantes inscritos na atividade selecioanada
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaCursosEventosParaInscricao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String listarInscritosAtividade() throws ArqException{
		
		AtividadeExtensaoDao dao = null;
		
		try{
			dao = getDAO(AtividadeExtensaoDao.class);
			
			atividadeSelecionada = dao.findInformacoesAlteracaoAtividadeExtensao(getParameterInt("idAtividadeSelecionada", 0));
			subAtividadeSelecionada = null;
			
			if(atividadeSelecionada == null){
				addMensagemErro("Atividade selecionada não foi encontrada, ela pode ter sido removida do sistema.");
				return null;
			}
		}finally{
			if(dao != null ) dao.close();
		}
		
		return buscarParticipantesInscritos();
	}
	
	
	/**
	 * Inicia a listagem dos participantes inscritos na sub atividade selecioanada
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaCursosEventosParaInscricao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String listarInscritosSubAtividade() throws ArqException{
		
		
		SubAtividadeExtensaoDao dao = null;
		
		try{
			dao = getDAO(SubAtividadeExtensaoDao.class);
			
			subAtividadeSelecionada = dao.findInformacoesAlteracaoSubAtividadeExtensao(getParameterInt("idSubAtividadeSelecionada", 0));
			atividadeSelecionada = null;
			
			if(subAtividadeSelecionada == null){
				addMensagemErro("A mini atividade selecionada não foi encontrada, ela pode ter sido removida do sistema.");
				return null;
			}
		}finally{
			if(dao != null ) dao.close();
		}
		
		
		return buscarParticipantesInscritos();
		
	}
	
	
	
	/**
	 * Filtra os resultados dos participantes inscritos.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaCursosEventosParaInscricao.jsp</li>
	 *   </ul>
	 *
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @param evt
	 * @throws ArqException 
	 */
	public String buscarParticipantesInscritos() throws ArqException{
		
		InscricaoParticipanteAtividadeExtensaoDao dao = null;
		InscricaoParticipanteSubAtividadeExtensaoDao daoSubAtividade = null;
		
		StatusInscricaoParticipante[] status = null;
		
		if(idStatusInscricaoParticipante == -2)
			status  = StatusInscricaoParticipante.getStatusAtivos();
		else{
			if(idStatusInscricaoParticipante == -1) // todos
			status = null;
			else{
				status = new StatusInscricaoParticipante[1];
				status[0] = new StatusInscricaoParticipante(idStatusInscricaoParticipante);
			}
		}
		
		if(atividadeSelecionada != null){
			
			prepareMovimento(SigaaListaComando.APROVAR_PARTICIPANTES_INSCRITOS_ATIVIDADE);
			prepareMovimento(SigaaListaComando.RECUSAR_PARTICIPANTES_INSCRITOS_ATIVIDADE);
			
			
			try{
				dao =getDAO(InscricaoParticipanteAtividadeExtensaoDao.class);
				inscricoes = dao.findAllParticipantesByAtividade(atividadeSelecionada.getId(), CamposOrdenacaoListaInscricoes.getCampoOrdenacao(valorCampoOrdenacao), status);
				
			}finally{
				if(dao != null) dao.close();
			}
		}else{
			
			prepareMovimento(SigaaListaComando.APROVAR_PARTICIPANTES_INSCRITOS_SUB_ATIVIDADE);
			prepareMovimento(SigaaListaComando.RECUSAR_PARTICIPANTES_INSCRITOS_SUB_ATIVIDADE);
			
			try{
				daoSubAtividade = getDAO(InscricaoParticipanteSubAtividadeExtensaoDao.class);
				inscricoes = daoSubAtividade.findAllParticipantesBySubAtividade(subAtividadeSelecionada.getId(), CamposOrdenacaoListaInscricoes.getCampoOrdenacao(valorCampoOrdenacao), status);
				
			}finally{
				if(dao != null) dao.close();
				if(daoSubAtividade != null) daoSubAtividade.close();
			}
		}
		
		
		if(inscricoes == null || inscricoes.size() == 0){
			addMensagemErro("Nenhuma inscrição encontrada de acordo com os critérios de busca.");
			return telaListaInscritosCursosEventosExtensao();
		}
		
		quantidadeParticipanteInscritos = 0;
		quantidadeParticipanteAprovados = 0;
		quantidadeParticipanteCancelados = 0;
		quantidadeParticipanteRecusados = 0;
		
		for (InscricaoAtividadeParticipante inscricao : inscricoes) {
			if(inscricao.getStatusInscricao().isStatusInscrito())
				quantidadeParticipanteInscritos++;
			if(inscricao.getStatusInscricao().isStatusAprovado())
				quantidadeParticipanteAprovados++;
			if(inscricao.getStatusInscricao().isStatusCancelado())
				quantidadeParticipanteCancelados++;
			if(inscricao.getStatusInscricao().isStatusRecusado())
				quantidadeParticipanteRecusados++;
		} 
		
		return telaListaInscritosCursosEventosExtensao();
	}
	
	
	/**
	 * Redireciona o usuário para a página na qual ele vai confirmar o pagamento manual da GRU.
	 *
	 *<br />
	 * Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/extensao/GerenciarInscricoes/listaCursosEventosParaInscricao.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException 
	 * @String
	 */
	public String preConfirmarPagamentoManualmente() throws ArqException{
		
		prepareMovimento(SigaaListaComando.CONFIRMA_PAGAMENTO_MANUAL_GRU_CURSOS_EVENTOS_EXTENSAO);
		
		inscricoesSelecionadas = new ArrayList<InscricaoAtividadeParticipante>();
		
		for(InscricaoAtividadeParticipante inscricao : inscricoes){
			
			// Somente as inscrições no status inscrito e que estão marcadas podem ser seu pagamento confirmado //
			if( inscricao.isMarcado()){
				
				if(inscricao.getIdGRUPagamento() != null)
					inscricao.setGru(GuiaRecolhimentoUniaoHelper.getGRUByID(inscricao.getIdGRUPagamento()));
				
				inscricoesSelecionadas.add(inscricao);
			}
		}
		
		if(inscricoesSelecionadas.size() == 0){
			addMensagemErro("Selecione pelo menos uma inscrição.");
			return null;
		}
			
		
		return telaConfirmaPagamento();
	}
	
	
	/**
	 * <p> Confirma o pagamento da inscrição manualmente (mediante apresentação do comprovante).</p>
	 * 
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/extensao/InscricaoOnlike/confirmarPagamentoCursosEventosManualmente.jsp</li>
	 *   </ul> 
	 */
	public String confirmarPagamentoManualmente() throws ArqException{
		
		try {
			
			MovimentoConfirmaPagamentoGRUAtividadesExtensaoManualmente movimento 
				= new MovimentoConfirmaPagamentoGRUAtividadesExtensaoManualmente(inscricoesSelecionadas);
			
			execute(movimento);
			
			addMensagemInformation("Pagamentos das inscrições confirmados com sucesso!");
			
			return buscarParticipantesInscritos();               // Atuailzada as listagem
			
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
	}
	
	
	
	
	///////////////////////////// A parte de estorno ///////////////////////////////////
	
	
	
	/**
	 * <p>Redireciona o usuário para a página na qual ele vai confirmar no sistema o estorno pagamento da GRU.</p>
	 *
	 * <p>Uma vez estornado o pagamento, essa operação não poderá ser desfeita.</p>
	 *
	 *<br />
	 * Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/extensao/GerenciarInscricoes/listaCursosEventosParaInscricao.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException 
	 * @String
	 */
	public String preEstornarPagamento() throws ArqException{
		
		prepareMovimento(SigaaListaComando.ESTORNA_PAGAMENTO_GRU_CURSOS_EVENTOS_EXTENSAO);
		
		inscricoesSelecionadas = new ArrayList<InscricaoAtividadeParticipante>();
		
		GenericDAO dao = getGenericDAO();
		
		for(InscricaoAtividadeParticipante inscricao : inscricoes){
			
			// Somente as inscrições no status inscrito e que estão marcadas podem ser seu pagamento confirmado //
			if( inscricao.isMarcado()){
				
				if(inscricao.getIdGRUPagamento() != null)
					inscricao.setGru(GuiaRecolhimentoUniaoHelper.getGRUByID(inscricao.getIdGRUPagamento()));
				
				InscricaoAtividadeParticipante temp = dao.findByPrimaryKey(inscricao.getId(), InscricaoAtividadeParticipante.class, "valorTaxaMatricula");
				
				inscricao.setValorTaxaMatricula( temp.getValorTaxaMatricula());
				dao.detach(temp );
				
				inscricoesSelecionadas.add(inscricao);
			}
		}
		
		if(inscricoesSelecionadas.size() == 0){
			addMensagemErro("Selecione pelo menos uma inscrição.");
			return null;
		}
			
		
		return telaConfirmaEstornoPagamento();
	}
	
	
	
	
	/**
	 * <p> Confirma o estorno do pagamento da inscrição.</p>
	 * 
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/extensao/InscricaoOnlike/confirmarEstornoPagamentoCursosEventos.jsp</li>
	 *   </ul> 
	 */
	public String confirmarEstornoPagamento() throws ArqException{
		
		try {
			
			MovimentoEstornaPagamentoGRUCursosEventosExtensao movimento 
				= new MovimentoEstornaPagamentoGRUCursosEventosExtensao(inscricoesSelecionadas);
			
			execute(movimento);
			
			addMensagemInformation("Estorno do pagamento da inscriação confirmado com sucesso! ");
			
			return buscarParticipantesInscritos();               // Atuailzada as listagem
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
	}
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	
	/**
	 * Modifica a situação da inscrição de participantes.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/extensao/GerenciarInscricoes/listaCursosEventosParaInscricao.jsp</li>
	 * </ul>
	 * 
	 * @param codMovimento
	 * @return
	 * @throws ArqException
	 */
	public String aprovarParticipantesInscritos() throws ArqException {
		
		inscricoesSelecionadas = new ArrayList<InscricaoAtividadeParticipante>();
		
		for(InscricaoAtividadeParticipante inscricao : inscricoes){
			
			// Somente as inscrições no status inscrito ou que foram recusas e que estão marcadas podem ser aprovadas //
			if( inscricao.isMarcado()){
				inscricoesSelecionadas.add(inscricao);
			}
		}
		
		if(inscricoesSelecionadas.size() == 0){
			addMensagemErro("Selecione pelo menos uma inscrição.");
			return null;
		}
		
		AbstractMovimento mov;
		
		if(atividadeSelecionada != null){
			mov = new MovimentoAprovarParticipantesInscritosAtividade(inscricoesSelecionadas, atividadeSelecionada);
		}else{
			mov = new MovimentoAprovarParticipantesInscritosSubAtividade(inscricoesSelecionadas, subAtividadeSelecionada);
		}
		
		try {
			execute(mov);
			addMensagemInformation("Participantes aprovados com sucesso!");
			
			// Atualiza a quantidade de participantes inscritos mostrado na primeira tela //
			GerenciarInscricoesCursosEventosExtensaoMBean mbean 
				= (GerenciarInscricoesCursosEventosExtensaoMBean) getMBean("gerenciarInscricoesCursosEventosExtensaoMBean");
			mbean.atualizaCursosEventosParaInscricao();
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		return buscarParticipantesInscritos();               // Atuailzada as listagem
		
	}
	
	
	/**
	 * Modifica a situação da inscrição de participantes.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/extensao/GerenciarInscricoes/listaCursosEventosParaInscricao.jsp</li>
	 * </ul>
	 * 
	 * @param codMovimento
	 * @return
	 * @throws ArqException
	 */
	public String recursarParticipantesSelecionados() throws ArqException {
		
		inscricoesSelecionadas = new ArrayList<InscricaoAtividadeParticipante>();
		
		for(InscricaoAtividadeParticipante inscricao : inscricoes){
			
			// Somente as inscrições no status inscrito e aprovados e que estão marcadas podem ser recusados //
			if(   inscricao.isMarcado() ){
				inscricoesSelecionadas.add(inscricao);
			}
		}
		
		if(inscricoesSelecionadas.size() == 0){
			addMensagemErro("Selecione pelo menos uma inscrição.");
			return null;
		}
		
		AbstractMovimento mov = null;
		
		if(atividadeSelecionada != null){
			mov = new MovimentoRecusarParticipantesInscritosAtividade(inscricoesSelecionadas, atividadeSelecionada);
		}else{
			mov = new MovimentoRecusarParticipantesInscritosSubAtividade(inscricoesSelecionadas, subAtividadeSelecionada);
		}
		
		try {
			execute(mov);
			
			addMensagemInformation("Inscrições dos participantes recusadas com sucesso !");
			
			// Atualiza a quantidade de participantes inscritos mostrado na primeira tela //
			GerenciarInscricoesCursosEventosExtensaoMBean mbean 
				= (GerenciarInscricoesCursosEventosExtensaoMBean) getMBean("gerenciarInscricoesCursosEventosExtensaoMBean");
			mbean.atualizaCursosEventosParaInscricao();
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		return buscarParticipantesInscritos();               // Atuailzada as listagem
	}
	
	


	/**
	 * 
	 * Usado para visualizar dados do cadastro de participante
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> /sigaa.war/extensao/GerenciarInscricoes/listaInscritosCursosEventosExtensao.jsp</li>
	 * </ul>
	 * 
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String visualizarDadosParticipante() throws DAOException {
		CadastroParticipanteAtividadeExtensao cadastrosParticipante = null;
		
		GenericDAO dao = null;
		try{
			dao = getGenericDAO();
			// Buscas todos os dados cadastrados //
			cadastrosParticipante = dao.findByPrimaryKey(getParameterInt("idCadastroParticipante"), CadastroParticipanteAtividadeExtensao.class);
		}finally{
			if(dao != null) dao.close();
		}
		
		/*
		 * Redireciona para a página padrão de visualização dos dados de cadastro do usuário, pode ser usado de diversos casos de uso
		 */
		getCurrentRequest().setAttribute("_cadastrosParticipanteExtensao", cadastrosParticipante);
		
		return telaVisualizaDadosCadastroParticipante();
	}
	
	
	
	/**
	 * Visualizar o arquivo anexo a inscrição.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/extensao/GerenciarInscricoes/
	 * listaInscritosCursosEventosExtensao.jsp</li>
	 * </ul>
	 * 
	 */
	public void viewArquivoInscricao() {
		try {
			int idArquivo = getParameterInt("idArquivo", 0);

			if (idArquivo == 0) {
				addMensagemErro("Arquivo não encontrado.");
				return;
			}

			EnvioArquivoHelper.recuperaArquivo(getCurrentResponse(), idArquivo, false);
			FacesContext.getCurrentInstance().responseComplete();

		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Arquivo não encontrado.");
			return;
		}

	}
	
	
	/**
	 *  <p>Emite a GRU COBRANÇA para o usuário pagar a inscrição nos caos em que a inscrição exije pagamento.</p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaInscritosCursosEventosExtensao.jsp</li>
	 *   </ul>
	 *
	 * @throws ArqException 
	 * @throws NegocioException 
	 *
	 */
	public void emitirGRUPagamentoInscricao() throws ArqException {
		
		int idInscricaoSelecionada = getParameterInt("idInscricaoSelecionada");
		
		InscricaoAtividadeParticipante inscricaoSelecionada = null;
		
		for (InscricaoAtividadeParticipante inscricao : inscricoes) {
			if(inscricao.getId() == idInscricaoSelecionada){
				inscricaoSelecionada = inscricao;
				break;
			}
		}
		
		if(inscricaoSelecionada == null){
			addMensagemErro("Erro ao selecionar a inscrição para ser paga. Por favor, reinicie o processo! "); // Mesagem que não era para ser mostrada
			return;
		}
		
		
		////////// Geral e Salva a GRU no banco  /////////////
		
		if(inscricaoSelecionada.getIdGRUPagamento() == null){ // A GRU não foi gerada ainda
			
			////////// Informações do sacado para emitir na GRU /////////////
			InscricaoAtividadeExtensaoDao dao = null;
			
			try{
				dao = getDAO(InscricaoAtividadeExtensaoDao.class);
				
				// completa as informações do cadastro //
				inscricaoSelecionada.setCadastroParticipante( dao.findByPrimaryKey(
						inscricaoSelecionada.getCadastroParticipante().getId(), 
						CadastroParticipanteAtividadeExtensao.class, "id, nome, cpf, logradouro, numero, bairro, municipio.nome, unidadeFederativa.sigla, cep") );
				
				// completa as informações do período de inscrição //
				inscricaoSelecionada.setValorTaxaMatricula(dao.findByPrimaryKey(inscricaoSelecionada.getId(), InscricaoAtividadeParticipante.class, "valorTaxaMatricula").getValorTaxaMatricula() );
				inscricaoSelecionada.setInscricaoAtividade( dao.findInformacoesInscricoesAtividadeParaEmissaoGRU(inscricaoSelecionada.getInscricaoAtividade().getId() ) );
				
			}finally{
				if(dao != null ) dao.close();
			}
			
			if( inscricaoSelecionada.getCadastroParticipante() == null ){ // caso o usuário não tenha municio ou uf vai vim nulo.
				addMensagemErro(" O usuário não tem informações completas no seu cadastro para emitir a GRU. Por favor complete o seu cadastro no sistema. ");
				return;
			}
			
			try {
			
				MovimentoCadastro movGRU = new MovimentoCadastro(inscricaoSelecionada);
				movGRU.setCodMovimento(SigaaListaComando.CRIA_GRU_CURSOS_EVENTOS_EXTENSAO);
			
				inscricaoSelecionada = (InscricaoAtividadeParticipante) execute(movGRU);    // A multa com o id da GRU gerada
				
			} catch (NegocioException ne) {
				addMensagens(ne.getListaMensagens());
				return;
			} finally{
				prepareMovimento(SigaaListaComando.CRIA_GRU_CURSOS_EVENTOS_EXTENSAO);  // prepara o próximo, caso o usuário tente reimprimir
			}  
			
			
		}
		
		///////////   Mostrar a GRU para o Usuário   ///////////
		try{ 
			getCurrentResponse().setContentType("application/pdf");
			getCurrentResponse().addHeader("Content-Disposition", "attachment;filename=GRU.pdf");
			getGenericDAO().initialize(inscricaoSelecionada.getInscricaoAtividade());
			Date vencimento = inscricaoSelecionada.getInscricaoAtividade().getDataVencimentoGRU();
			GuiaRecolhimentoUniao gru = GuiaRecolhimentoUniaoHelper.getGRUByID(inscricaoSelecionada.getIdGRUPagamento());
			if (!vencimento.after(gru.getVencimento()))
				vencimento = null;
			GuiaRecolhimentoUniaoHelper.gerarPDF(getCurrentResponse().getOutputStream(), inscricaoSelecionada.getIdGRUPagamento(), vencimento);
			FacesContext.getCurrentInstance().responseComplete();
			
		} catch (IOException ioe) {
			addMensagemErro("Erro ao tentar gerar a GRU, por favor tente novamente, caso o problema persista, contacte o suporte.");
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
		}
		
	}
	
	
	
	
	/**
	 * Visualizar as respotas ao questionário dos participantes da ação de
	 * extensão.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/extensao/GerenciarInscricoes/
	 * listaInscritosCursosEventosExtensao.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 * 
	 */
	public String viewRespostaQuestionarioInscricao() throws DAOException {

		int idQuestionarioRespostas = getParameterInt("idQuestionario", 0);

		if (idQuestionarioRespostas <= 0) {
			addMensagemErro("Questionário não selecionado corretamente.");
			return null;
		}

		QuestionarioRespostasDao dao = null;

		try {
			dao = getDAO(QuestionarioRespostasDao.class);

			questionarioRespondido = dao.findInformacaoRespostasQuestionario(
					idQuestionarioRespostas,
					TipoQuestionario.QUESTIONARIO_INSCRICAO_ATIVIDADE);

			if (questionarioRespondido == null
					|| questionarioRespondido.getRespostas() == null) {
				addMensagemErro("Não existem respostas para o questionário selecionado.");
				return null;
			}

			/*
			 * Redireciona para a página padrão de visualização das resposta do
			 * questionário
			 */
			getCurrentRequest().setAttribute("_visualizaRespostasQuestionarioMBean", this);
			return forward("/geral/questionario/view_respostas_questionario.jsp");

		} finally {
			if (dao != null)
				dao.close();
		}

	}
	
	/**
	 *  Retorna os status das inscrições para o usuário filtrar na tela 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaCursosEventosParaInscricao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public List<SelectItem> getStatusInscricaoComboBox(){
		
		List<SelectItem> lista = new ArrayList<SelectItem>();
		lista.add( new SelectItem(-1, "TODOS") );
		lista.add( new SelectItem(-2, "ATIVOS") );
		lista.add( new SelectItem(StatusInscricaoParticipante.INSCRITO, "INSCRITO") );
		lista.add( new SelectItem(StatusInscricaoParticipante.APROVADO, "APROVADO") );
		lista.add( new SelectItem(StatusInscricaoParticipante.RECUSADO, "RECUSADO") );
		lista.add( new SelectItem(StatusInscricaoParticipante.CANCELADO, "CANCELADO"));
		
		return lista;
	} 
	
	/**
	 * 
	 * Retorna os campos que o usuário vai pode escolher a ordenação da listagem de inscrições
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaInscritosCursosEventosExtensao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public Collection<SelectItem> getCampoOrdenacaoComboBox(){
		
		Collection<SelectItem> lista = new ArrayList<SelectItem>();
		
		for(CamposOrdenacaoListaInscricoes campo : CamposOrdenacaoListaInscricoes.CAMPOS_ORDENACAO){
			lista.add(new SelectItem(campo.getValor(), campo.getDescricao()));
		}
		
		return lista;
	}
	
	
	
	//////////////////////// Telas de Navegação /////////////////////////////
	
	/***
	 * Tela de navegação.
	 * 
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 */
	public String telaListaInscritosCursosEventosExtensao(){
		return forward(LISTA_INSCRITOS_CURSOS_EVENTOS_EXTENSAO);
	}

	/***
	 * Tela de navegação.
	 * 
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 */
	public String telaVisualizaDadosCadastroParticipante(){
		return forward(VISUALIZA_DADOS_CADASTRO_PARTICIPANTE);
	}
	
	/***
	 * Tela de navegação.
	 * 
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 */
	public String telaConfirmaPagamento(){
		return forward(PAGINA_CONFIRMAR_PAGAMENTO);
	}
	
	
	/***
	 * Tela de navegação.
	 * 
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 */
	public String telaConfirmaEstornoPagamento(){
		return forward(PAGINA_CONFIRMAR_ESTORNO_PAGAMENTO);
	}
	
	
	///////////////////////////////////////////////////////////////////////

	
	public List<InscricaoAtividadeParticipante> getInscricoes() {
		return inscricoes;
	}
	public void setInscricoes(List<InscricaoAtividadeParticipante> inscricoes) {
		this.inscricoes = inscricoes;
	}
	
	/** Retorna a quantidade de inscrições encontradas */
	public int getQtdInscricoes(){
		if(inscricoes == null)
			return 0;
		else
			return inscricoes.size();
	}

	public int getIdStatusInscricaoParticipante() {
		return idStatusInscricaoParticipante;
	}

	public void setIdStatusInscricaoParticipante(int idStatusInscricaoParticipante) {
		this.idStatusInscricaoParticipante = idStatusInscricaoParticipante;
	}

	public AtividadeExtensao getAtividadeSelecionada() {
		return atividadeSelecionada;
	}
	public void setAtividadeSelecionada(AtividadeExtensao atividadeSelecionada) {
		this.atividadeSelecionada = atividadeSelecionada;
	}
	public SubAtividadeExtensao getSubAtividadeSelecionada() {
		return subAtividadeSelecionada;
	}
	public void setSubAtividadeSelecionada(SubAtividadeExtensao subAtividadeSelecionada) {
		this.subAtividadeSelecionada = subAtividadeSelecionada;
	}
	public QuestionarioRespostas getQuestionarioRespondido() {
		return questionarioRespondido;
	}
	public void setQuestionarioRespondido(QuestionarioRespostas questionarioRespondido) {
		this.questionarioRespondido = questionarioRespondido;
	}
	public int getQuantidadeParticipanteInscritos() {
		return quantidadeParticipanteInscritos;
	}
	public void setQuantidadeParticipanteInscritos(
			int quantidadeParticipanteInscritos) {
		this.quantidadeParticipanteInscritos = quantidadeParticipanteInscritos;
	}
	public int getQuantidadeParticipanteAprovados() {
		return quantidadeParticipanteAprovados;
	}
	public void setQuantidadeParticipanteAprovados(
			int quantidadeParticipanteAprovados) {
		this.quantidadeParticipanteAprovados = quantidadeParticipanteAprovados;
	}
	public int getQuantidadeParticipanteCancelados() {
		return quantidadeParticipanteCancelados;
	}

	public void setQuantidadeParticipanteCancelados(	int quantidadeParticipanteCancelados) {
		this.quantidadeParticipanteCancelados = quantidadeParticipanteCancelados;
	}
	public int getQuantidadeParticipanteRecusados() {
		return quantidadeParticipanteRecusados;
	}
	public void setQuantidadeParticipanteRecusados(int quantidadeParticipanteRecusados) {
		this.quantidadeParticipanteRecusados = quantidadeParticipanteRecusados;
	}
	public List<InscricaoAtividadeParticipante> getInscricoesSelecionadas() {
		return inscricoesSelecionadas;
	}
	public void setInscricoesSelecionadas(List<InscricaoAtividadeParticipante> inscricoesSelecionadas) {
		this.inscricoesSelecionadas = inscricoesSelecionadas;
	}
	public int getValorCampoOrdenacao() {
		return valorCampoOrdenacao;
	}
	public void setValorCampoOrdenacao(int valorCampoOrdenacao) {
		this.valorCampoOrdenacao = valorCampoOrdenacao;
	}
	
}
