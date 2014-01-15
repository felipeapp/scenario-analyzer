/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 16/11/2012
 * 
 */
package br.ufrn.sigaa.extensao.jsf.inscricoes_atividades;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.comum.gru.dominio.GuiaRecolhimentoUniao;
import br.ufrn.comum.gru.negocio.GuiaRecolhimentoUniaoHelper;
import br.ufrn.sigaa.arq.dao.extensao.ParticipanteAcaoExtensaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.extensao.dao.inscricoes_atividades.InscricaoParticipanteAtividadeExtensaoDao;
import br.ufrn.sigaa.extensao.dao.inscricoes_atividades.InscricaoParticipanteSubAtividadeExtensaoDao;
import br.ufrn.sigaa.extensao.dominio.CadastroParticipanteAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividadeParticipante;
import br.ufrn.sigaa.extensao.dominio.ParticipanteAcaoExtensao;
import br.ufrn.sigaa.mensagens.MensagensExtensao;

/**
 *
 * <p> MBean que controla a ações que o participante pode fazer depois que está inscrito cursos ou eventos de extensão. <br/> 
 *  
 * </p>
 *
 *  <p> <strong>ATENSÃO:</strong> Um participante pode está isncrito sem ter feito inscrição em um curso ou evento de extensão, no caso 
 *  ele é inscrito diretamente pelo coordendor. Nesse caso ele pode acesar esse caso e uso e poderar emitir declarações e certificados normalmente. </p>
 *
 * <p> <i> Apenas para os curso ou eventos que ele está inscrito (EXISTE UMA INSCRIÃO), é que ele vai pode emitir GRU para pagamento, cancelar a sus inscrição etc... </i> </p>
 * 
 * <p> <strong>Por favor, não adicionar mais operações nesse MBean deixar ele o mais enxuto possível <strong></p>
 * 
 * @author jadson
 * @version 1.0 - criação da classe.
 */
@Component("gerenciaMeusCursosEventosExtensaoMBean")
@Scope("request")
public class GerenciaMeusCursosEventosExtensaoMBean extends SigaaAbstractController<InscricaoAtividadeParticipante>{

	
	/** Lista os cursos ou eventos e mini atividades de extensão que o usuário se inscreveu. */
	public final static String PAGINA_LISTA_MEUS_CURSOS_EVENTOS_EXTENSAO = "/public/extensao/paginaListaMeusCursosEventosExtensao.jsp";
	
	/** Página onde o usuário realiza as ações que ele pode fazer para a sua inscrição em atividades ou mini atividades de extensão. */
	public final static String PAGINA_GERENCIA_MINHA_INSCRICAO_CURSOS_EVENTOS_EXTENSAO = "/public/extensao/paginaGerenciaMinhaInscricaoCursosEventosExtensao.jsp";
	
	/** Página onde o usuário realiza as ações que ele pode fazer para a sua atividades ou mini atividades de extensão, com emitir cerfidados e declarações */
	public final static String PAGINA_GERENCIA_MINHA_PARTICICAO_CURSOS_EVENTOS_EXTENSAO = "/public/extensao/paginaGerenciaMinhaParticipacaoCursosEventosExtensao.jsp";
	
	/** Guardas as inscrições atividas realizadas pelo participante logado em atividades e mini atividades de extensão.*/
	private List<InscricaoAtividadeParticipante> inscricoesRealizadas;
	
	/** Guarda a lista de cursos ou evento de extensão cujo usuário logado é foi cadastrado diretamente pelo coordenador, ou seja, não possui inscrição.*/
	private List<ParticipanteAcaoExtensao> participacaoSemInscricao;
	
	/** A data a partir de quando a inscrição foi realizada. Utilizando para filtrar os resultados se foram muitos */
	private Date dataInicioInscricao;
	
	/** A data até quando a inscrição foi realizada. Utilizando para filtrar os resultados se foram muitos  */
	private Date dataFinalInscricao;
	
	/** O participante seleciona que geranciar as suas operações, como emitir certificados e declarações. */
	private ParticipanteAcaoExtensao participanteSelecionado;
	
	/**
	 * <p>Busca as atividas e minit que o usuário é participante ou realizou inscrição..</p>
	 * 
	 * <p>Por padrão busca apenas as inscrições do último ano para diminuir os resultados, mas ele pode alterar esse período se quiser.</p> 
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/public/extensao/painelLateralAreaInternaCursosEventosExtensao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public String listarCursosEventosDoUsuarioLogado() throws DAOException {		
		
		/* Por padrão mostra apenas as inscrição do último ano do usuário
		 * provavelmente ele não está interessado em cursos que tenha feito a 10 anos atras.
		 */
		dataFinalInscricao = new Date();
		dataInicioInscricao = CalendarUtils.adicionarAnos(dataFinalInscricao, -1);
		
		buscarCursosEventoExtensaoDoUsuarioLogado();
		
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		
		addMensagemWarning("Inscrições realizadas no período de "+format.format(dataInicioInscricao)+" a "+format.format(dataFinalInscricao) );
		
		return telaListaMeusCursosEventosExtensao();
		
	}

	/**
	 * 
	 *  Busca as inscrições do usuário por um período diferente do período padrão.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/public/extensao/paginaListaMeusCursosEventosExtensao.jsp</li>
	 *   </ul>
	 *
	 * @param etv
	 */
	public void filtarInscricoesUsuario(ActionEvent evt) throws DAOException{
		buscarCursosEventoExtensaoDoUsuarioLogado(); // busca com as informações de data informada pelo usuário logado.
	}
	
	
	
	/** Realiza a busca no banco das inscrições do usuário.*/
	private void buscarCursosEventoExtensaoDoUsuarioLogado() throws DAOException{
		if(dataInicioInscricao != null && dataFinalInscricao != null ){
			if(dataInicioInscricao.after(dataFinalInscricao)){
				addMensagem(MensagensArquitetura.DATA_INICIO_MENOR_FIM, "Período da Inscrição");
				return;
			}
		}
		
		carregarInscricoesParticipacoesSemInscricao();
	}

	public void carregarInscricoesParticipacoesSemInscricao() throws DAOException {
		InscricaoParticipanteAtividadeExtensaoDao dao = null;
		try {
			dao = getDAO(InscricaoParticipanteAtividadeExtensaoDao.class);

			inscricoesRealizadas = new ArrayList<InscricaoAtividadeParticipante>();
			participacaoSemInscricao = new ArrayList<ParticipanteAcaoExtensao>();

			GerenciaAreaInternaCursosEventoExtensaoMBean mBean = getMBean("gerenciaAreaInternaCursosEventoExtensaoMBean");
			inscricoesRealizadas = dao.findInscricoesAtividadeEMiniAtividadeParticipante(mBean.getParticipanteLogadoAreaInterna().getId(), dataInicioInscricao, dataFinalInscricao);
			participacaoSemInscricao = dao.findParticipacaoAtividadeEMiniAtividadeSemInscricao(mBean.getParticipanteLogadoAreaInterna().getId(), dataInicioInscricao, dataFinalInscricao);
		} finally {
			dao.close();
		}
	}
	
	
	
	/**
	 * 
	 * A parte quem que o usuário vai para a página de para gerenciar sua inscrição que 
	 * ele selecionou na listagem anterior.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/public/extensao/paginaListaMeusCursosEventosExtensao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String acessarInscricaoSelecionada() throws ArqException {
		
		prepareMovimento(SigaaListaComando.CRIA_GRU_CURSOS_EVENTOS_EXTENSAO); // Para emitir a gru
		prepareMovimento(SigaaListaComando.CANCELAR_INSCRICAO_PARTICIPANTE); // Para cancelar a inscrição
		
		obj = null;
		
		int idInscricaoParticipanteSelecionada = getParameterInt("idInscricaoParticipanteSelecionada", 
				(Integer) getCurrentRequest().getAttribute("idInscricaoParticipanteSelecionada"));
		
		for (InscricaoAtividadeParticipante inscricaoParticipante : inscricoesRealizadas) {
			if(inscricaoParticipante.getId() == idInscricaoParticipanteSelecionada){
				obj = inscricaoParticipante;
				break;
			}
		}
		
		if(obj == null){
			addMensagemErro("Inscrição não selecionado com sucesso ");
		}
		
		/* Carrega TODOS os dados da inscrição do participante
		 * 
		 * Dados da inscrição, da atividade ou sub atividade, coordenador, status pagamento, etc...
		 * 
		 */
		
		InscricaoParticipanteAtividadeExtensaoDao daoParticipanteAtividade = null;
		InscricaoParticipanteSubAtividadeExtensaoDao daoParticipanteSubAtividade = null;
		
		try {
		
			if( obj.getInscricaoAtividade().isInscricaoAtividade()){
				daoParticipanteAtividade = getDAO(InscricaoParticipanteAtividadeExtensaoDao.class);
				obj = daoParticipanteAtividade.findInformacoesInscricaoParticipanteAtividade(obj.getId());
			}else{
				daoParticipanteSubAtividade = getDAO(InscricaoParticipanteSubAtividadeExtensaoDao.class);
				obj = daoParticipanteSubAtividade.findInformacoesInscricaoParticipanteSubAtividade(obj.getId());
			}
			
		} finally {
			if(daoParticipanteAtividade != null) daoParticipanteAtividade.close();
			if(daoParticipanteSubAtividade != null) daoParticipanteSubAtividade.close();
		}	
		
		
		return telaGerenciaMinhaInscricaoCursosEventosExtensao();
	}
	
	
	
	/**
	 * 
	 * A parte quem que o usuário vai para a página de para gerenciar sua inscrição que 
	 * ele selecionou na listagem anterior.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/public/extensao/paginaListaMeusCursosEventosExtensao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String acessarParticipacaoSelecionada() throws ArqException {
		
		obj = null;
		
		int idParticipanteSelecionado = getParameterInt("idParticipanteSelecionado");
		
		for (ParticipanteAcaoExtensao participante : participacaoSemInscricao) {
			if(participante.getId() == idParticipanteSelecionado){
				participanteSelecionado = participante;
				break;
			}
		}
		
		if(participanteSelecionado == null){
			addMensagemErro("Participação não selecionado com sucesso ");
		}
		
		/* Carrega TODOS os dados do participante
		 * 
		 * Dados da atividade ou sub atividade, coordenador, frenquencia, etc...
		 * 
		 */
		ParticipanteAcaoExtensaoDao dao = null;
		
		try {
		
			dao = getDAO(ParticipanteAcaoExtensaoDao.class);
			
			if( participanteSelecionado.isParticipanteAtividadeExtensao() ){
				participanteSelecionado = dao.findInformacoesParticipanteAtividade(participanteSelecionado.getId());
			}else{
				participanteSelecionado = dao.findInformacoesParticipanteSubAtividade(participanteSelecionado.getId());
			}
			
		} finally {
			if(dao != null) dao.close();
		}	
		
		
		return telaGerenciaMinhaParticipacaoCursosEventosExtensao();
	}
	
	
	
	
	
	
	////////////////////// Ações que o participante pode realizar ///////////////////////
	
	
	/**
	 *  <p>Emite a GRU COBRANÇA para o usuário pagar a inscrição nos caos em que a inscrição exije pagamento.</p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/public/extensao/paginaGerenciaMinhasInscricaoCursosEventosExtensao.jsp</li>
	 *   </ul>
	 *
	 * @throws ArqException 
	 * @throws NegocioException 
	 *
	 */
	public void emitirGRUPagamentoInscricao() throws ArqException {
		
		if(obj == null){
			addMensagemErro("Erro ao selecionar a inscrição para ser paga. Por favor, reinicie o processo! "); // Mesagem que não era para ser mostrada
			return;
		}
		
		GerenciaAreaInternaCursosEventoExtensaoMBean mbean = 
				(GerenciaAreaInternaCursosEventoExtensaoMBean) 
				getMBean("gerenciaAreaInternaCursosEventoExtensaoMBean");
		
		
		////////// Geral e Salva a GRU no banco  /////////////
		
		if(obj.getIdGRUPagamento() == null){ // A GRU não foi gerada ainda
			
			////////// Informações do sacado para emitir na GRU /////////////
			GenericDAO dao = null;
			
			try{
				dao = getGenericDAO();
				obj.setCadastroParticipante( dao.findByPrimaryKey(
						mbean.getParticipanteLogadoAreaInterna().getId(), 
						CadastroParticipanteAtividadeExtensao.class, "id, nome, cpf, logradouro, numero, bairro, municipio.nome, unidadeFederativa.sigla, cep") );
			}finally{
				if(dao != null ) dao.close();
			}
			
			if( obj.getCadastroParticipante() == null ){ // caso o usuário não tenha municio ou uf vai vim nulo.
				addMensagemErro(" O usuário não tem informações completas no seu cadastro para emitir a GRU. Por favor complete o seu cadastro no sistema. ");
				return;
			}
			
			try {
			
				MovimentoCadastro movGRU = new MovimentoCadastro(obj);
				movGRU.setCodMovimento(SigaaListaComando.CRIA_GRU_CURSOS_EVENTOS_EXTENSAO);
			
				obj = (InscricaoAtividadeParticipante) execute(movGRU);    // A multa com o id da GRU gerada
				
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
			Date vencimento = obj.getInscricaoAtividade().getDataVencimentoGRU();
			GuiaRecolhimentoUniao gru = GuiaRecolhimentoUniaoHelper.getGRUByID(obj.getIdGRUPagamento());
			if (!vencimento.after(gru.getVencimento()))
				vencimento = null;
			GuiaRecolhimentoUniaoHelper.gerarPDF(getCurrentResponse().getOutputStream(), obj.getIdGRUPagamento(), vencimento);
			FacesContext.getCurrentInstance().responseComplete();
			
		} catch (IOException ioe) {
			addMensagemErro("Erro ao tentar gerar a GRU, por favor tente novamente, caso o problema persista, contacte o suporte.");
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
		}
		
	}
	
	
	/**
	 * Cancela a inscrição para participante pelo usuário.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/public/extensao/login_inscricao.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 * 
	 */
	public String cancelarInscricao() throws ArqException {
		
	
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.CANCELAR_INSCRICAO_PARTICIPANTE);
			mov.setObjMovimentado(obj);
			mov.setObjAuxiliar( ( (GerenciaAreaInternaCursosEventoExtensaoMBean) 
					getMBean("gerenciaAreaInternaCursosEventoExtensaoMBean")).getParticipanteLogadoAreaInterna() );
			
			execute(mov);
			
			addMensagem(MensagensExtensao.INSCRICAO_CANCELADA_SUCESSO);
		
			return listarCursosEventosDoUsuarioLogado();
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////
	
	
	
	/**
	 *  Retorna a quantidade de inscrições realizadas.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/public/extensao/paginaListaMeusCursosEventosExtensao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public int getQtdInscricoesRealizadas(){
		if(inscricoesRealizadas == null)
			return 0;
		return inscricoesRealizadas.size();
	}
	
	/**
	 *  Retorna a quantidade de participações que o usuário não fez a inscrição.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/public/extensao/paginaListaMeusCursosEventosExtensao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public int getQtdParticipacoesSemInscricao(){
		if(participacaoSemInscricao == null)
			return 0;
		return participacaoSemInscricao.size();
	}
	
	
	
	////////////////////// tela de navegação //////////////////////
	/**
	 * Tela de listagem.
	 *
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 */
	public String telaListaMeusCursosEventosExtensao(){
		GerenciaAreaInternaCursosEventoExtensaoMBean gerenciaAreaInterna = getMBean("gerenciaAreaInternaCursosEventoExtensaoMBean");
		return gerenciaAreaInterna.forwardPaginaInterna(PAGINA_LISTA_MEUS_CURSOS_EVENTOS_EXTENSAO);
	}
	
	/**
	 * Tela de listagem.
	 *
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 */
	public String telaGerenciaMinhaInscricaoCursosEventosExtensao(){
		GerenciaAreaInternaCursosEventoExtensaoMBean gerenciaAreaInterna = getMBean("gerenciaAreaInternaCursosEventoExtensaoMBean");
		return gerenciaAreaInterna.forwardPaginaInterna(PAGINA_GERENCIA_MINHA_INSCRICAO_CURSOS_EVENTOS_EXTENSAO);
	}

	
	/**
	 * Tela de listagem.
	 *
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 */
	public String telaGerenciaMinhaParticipacaoCursosEventosExtensao(){
		GerenciaAreaInternaCursosEventoExtensaoMBean gerenciaAreaInterna = getMBean("gerenciaAreaInternaCursosEventoExtensaoMBean");
		return gerenciaAreaInterna.forwardPaginaInterna(PAGINA_GERENCIA_MINHA_PARTICICAO_CURSOS_EVENTOS_EXTENSAO);
	}
	
	////////////////////////////////////////////////////////////////
	
	
	
	public List<InscricaoAtividadeParticipante> getInscricoesRealizadas() {
		return inscricoesRealizadas;
	}
	public void setInscricoesRealizadas(List<InscricaoAtividadeParticipante> inscricoesRealizadas) {
		this.inscricoesRealizadas = inscricoesRealizadas;
	}
	public Date getDataInicioInscricao() {
		return dataInicioInscricao;
	}
	public void setDataInicioInscricao(Date dataInicioInscricao) {
		this.dataInicioInscricao = dataInicioInscricao;
	}
	public Date getDataFinalInscricao() {
		return dataFinalInscricao;
	}
	public void setDataFinalInscricao(Date dataFinalInscricao) {
		this.dataFinalInscricao = dataFinalInscricao;
	}
	public List<ParticipanteAcaoExtensao> getParticipacaoSemInscricao() {
		return participacaoSemInscricao;
	}
	public void setParticipacaoSemInscricao(List<ParticipanteAcaoExtensao> participacaoSemInscricao) {
		this.participacaoSemInscricao = participacaoSemInscricao;
	}
	public ParticipanteAcaoExtensao getParticipanteSelecionado() {
		return participanteSelecionado;
	}
	public void setParticipanteSelecionado(ParticipanteAcaoExtensao participanteSelecionado) {
		this.participanteSelecionado = participanteSelecionado;
	}
	
	
}
