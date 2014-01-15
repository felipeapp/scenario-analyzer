/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 18/02/2013
 * 
 */
package br.ufrn.sigaa.extensao.jsf.inscricoes_atividades;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.MunicipioDao;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.ParticipanteAcaoExtensaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.extensao.dao.SubAtividadeExtensaoDao;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.ParticipanteAcaoExtensao;
import br.ufrn.sigaa.extensao.dominio.SubAtividadeExtensao;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

/**
 * Classe abstrata que contém os dados comuns usados nos dois casos de uso de gerenciar participantes. O do coordenador e do gestor.
 *
 * @author jadson - jadson@info.ufrn.br
 * @vesion 1.0 - criação da classe.
 * @since 18/02/2013
 *
 */
public abstract class AbstractGerenciarParticipantesExtensaoMBean extends SigaaAbstractController<ParticipanteAcaoExtensao>{

	
	/** Para para a impressão das informações de contado dos participantes. */
	public static final String NOTIFICACAO_PARTICIPANTE = "/extensao/ParticipanteAcaoExtensao/notificacao_participante.jsp";
	
	
	/** A atividade dos participantes estão inscritos. ( Contém informações extras para serem mostradas ao coordenador quando ele selecionada uma atividade ) */
	protected AtividadeExtensao atividadeSelecionada;
	
	/** A mini atividade dos participantes estão inscritos. ( Contém informações extras para serem mostradas ao coordenador quando ele selecionada uma mini atividade ) */
	protected SubAtividadeExtensao subatividadeSelecionada;
	
	/** Lista os participantes selecionados para a atividade*/
	protected List<ParticipanteAcaoExtensao> participantes;
	
	//////////////////////////////////////////////////////////////
		
	/** Usado na filtragem de participantes da atividade */
	protected boolean checkBuscaNome;
	
	/** Usado na filtragem de participantes da atividade */	
	protected boolean checkBuscaMunicipio;
	
	/** Usado na filtragem de participantes da atividade */
	protected String buscaNome;
	
	/** Usado na filtragem de participantes da atividade */
	protected int idUnidadeFederativa = UnidadeFederativa.ID_UF_PADRAO;
	
	/** Usado na filtragem de participantes da atividade */
	protected int idMunicipio = Municipio.ID_MUNICIPIO_PADRAO;
	
	/** Usado na filtragem de participantes da atividade */
	protected boolean participantesSemFrequencia;
	
	/** Usado na filtragem de participantes da atividade */
	protected boolean participantesNaoDeclaracao;
	
	/** Usado na filtragem de participantes da atividade */
	protected boolean participantesNaoCertificados;
	
	/*** A lista de municipios a serem mostrados na busca */
	protected Collection<SelectItem> municipiosComBox = new ArrayList<SelectItem>(0);
	
	//////////////////////////////////////////////////////////////
	
	
	
	//////////////////////////////////////////////////////////////
		
		
	/** A página atual a busca paginada */
	protected int paginaAtual = 0;
	
	/** A quantidade total de página = qtdTotalResultados/tamanhoPagina */
	protected int totalPaginas = 1;
	
	/** a quantidade de resultados por página */
	protected int tamanhoPagina = 50;
	
	/** Total de registro se a busca não fosse paginada. Usado para calcular a quantidade de páginas.  */
	protected int totalRegistros = 0;
		
		
	//////////////////////////////////////////////////////////////


	
	/**
	 *  Vai para a página anterior da paginação.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaParticipantesParaGerenciar.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public String previousPage() throws DAOException{
		paginaAtual--;
		if(paginaAtual < 0 ) paginaAtual = 0;
		return buscarParticipantesAtividadePaginado();
	}
	
	/**
	 *  Vai para a próxima página da paginação.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaParticipantesParaGerenciar.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public String nextPage() throws DAOException{
		paginaAtual++;
		if(paginaAtual > totalPaginas ) paginaAtual = totalPaginas;
		return buscarParticipantesAtividadePaginado();
	}
	
	
	/**
	 *  Realiza efetivamente a busca de participantes.
	 *
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 * @throws DAOException
	 */
	public String buscarParticipantesAtividadePaginado() throws DAOException{
		
		ParticipanteAcaoExtensaoDao daoParticipantes = null;
		
		String nomeParticipanteBusca = null;
		Integer idMunicipioBusca = null;
		
		if(checkBuscaNome) nomeParticipanteBusca = buscaNome;
		if(checkBuscaMunicipio) idMunicipioBusca = idMunicipio;
		
		if(br.ufrn.arq.util.StringUtils.isEmpty(nomeParticipanteBusca)) checkBuscaNome = false;
		if(idMunicipioBusca == null || idMunicipioBusca <= 0 ) checkBuscaMunicipio = false;
		
		try{
			daoParticipantes = getDAO(ParticipanteAcaoExtensaoDao.class);
			
			totalRegistros  = daoParticipantes.countParticipantesParaGerenciar(atividadeSelecionada != null ? atividadeSelecionada.getId(): null
					, subatividadeSelecionada != null ? subatividadeSelecionada.getId(): null
							, nomeParticipanteBusca, idMunicipioBusca, participantesSemFrequencia, participantesNaoDeclaracao, participantesNaoCertificados); 
			
			participantes =  daoParticipantes.findParticipantesParaGerenciar(atividadeSelecionada != null ? atividadeSelecionada.getId(): null
					, subatividadeSelecionada != null ? subatividadeSelecionada.getId(): null
							, nomeParticipanteBusca, idMunicipioBusca, participantesSemFrequencia, participantesNaoDeclaracao, participantesNaoCertificados,
							paginaAtual, tamanhoPagina);
	    
		    //se participantes é nulo, nao redireciona para pagina de listagem
		    if(participantes == null || participantes.size() <= 0){
		    	addMensagemErro("Não existem participantes cadastrados para esta ação de extensão.");
		    	return null;
		    }
		    
		    if(totalRegistros <= tamanhoPagina)
		    	 totalPaginas = 1;
		    else{
		    	if(totalRegistros % tamanhoPagina == 0)
		    		totalPaginas = ( totalRegistros / tamanhoPagina);
		    	else
		    		totalPaginas = ( totalRegistros / tamanhoPagina)+1; // +1 para caber o resto da divisão
		    }
		    
		    return null;
		    
		}finally{
			if(daoParticipantes != null) daoParticipantes.close();
		}
		
	}
	
	

	/**
	 * 
	 * Usado quando o usuário busca a partir do furmulário na página, nesse caso deve-se será as 
	 * informações de paginação. Porque o usuário pode alterar a quantidade de resultados por página.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaParticipantesParaGerenciar.jsp</li>
	 *   </ul>
	 *
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 * @throws DAOException 
	 */
	public String filtarParticipantesAtividade() throws DAOException{
		paginaAtual = 0;
		return buscarParticipantesAtividadePaginado();
	}
	
	
	/**
	 *  <p>Atualiza a listagem de particpantes.</p> 
	 *  <p>Usado quando o coordenador adiciona ou remove participante, deve-se buscar novamente.</p>
	 *
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @param idAtividadeExtensao
	 * @param idSubAtividadeExtensao
	 * @return
	 * @throws DAOException
	 */
	public String listarParticipantesAtividade(Integer idAtividadeExtensao, Integer idSubAtividadeExtensao) throws DAOException{
	
		ParticipanteAcaoExtensaoDao daoParticipantes = null;
		AtividadeExtensaoDao daoAtividade = null;
		SubAtividadeExtensaoDao daoSubAtividade = null;
		
		try{
			daoParticipantes = getDAO(ParticipanteAcaoExtensaoDao.class);
			daoAtividade = getDAO(AtividadeExtensaoDao.class);
			daoSubAtividade = getDAO(SubAtividadeExtensaoDao.class);
			
			if(idAtividadeExtensao != null){
				atividadeSelecionada = daoAtividade.findInformacoesAlteracaoAtividadeExtensao(idAtividadeExtensao);
				subatividadeSelecionada = null;
			}else{
				subatividadeSelecionada = daoSubAtividade.findInformacoesAlteracaoSubAtividadeExtensao(idSubAtividadeExtensao);
				atividadeSelecionada = null;
			}
			
			buscarParticipantesAtividadePaginado();
			
		}finally{
			if(daoParticipantes != null) daoParticipantes.close();
			if(daoAtividade != null) daoAtividade.close();
			if(daoSubAtividade != null) daoSubAtividade.close();
		}
		
		carregarMunicipios();
		
		return null;
	}
	
	
	
	/***
	 * <p>Atualiza as informações de frequência, declaração autorizada e certificado autorizado 
	 *   dos participantes de extensão selecionados.</p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaParticipantesParaGerenciar.jsp</li>
	 *   </ul>
	 *
	 * @param evt
	 * @throws ArqException 
	 */
	public void atualizarInformacoesEmtirDeclaracoesParticipantes(ActionEvent evt) throws ArqException {
		
		prepareMovimento(SigaaListaComando.ATUALIZAR_INFORMACOES_PARTICIPANTES_EMITIR_CERTIFICADOS);
		
		List<ParticipanteAcaoExtensao> participantesSelecionados = new ArrayList<ParticipanteAcaoExtensao>();
		
		for (ParticipanteAcaoExtensao participante : participantes) {
			if(participante.isSelecionado())
				participantesSelecionados.add(participante);
		}
	
		if(participantesSelecionados.size() == 0 ){
			addMensagemErro("Não foi selecionado nenhum participante.");
			return;
		}
		
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.ATUALIZAR_INFORMACOES_PARTICIPANTES_EMITIR_CERTIFICADOS);
			mov.setColObjMovimentado(participantesSelecionados);
			execute(mov);
			
			addMensagemInformation("As alterações nos participantes selecionados foram salvas com sucesso!");
			
			// prepara o próximo, aqui o usuário vai poder chamar quantas vezes quiser //
			prepareMovimento(SigaaListaComando.ATUALIZAR_INFORMACOES_PARTICIPANTES_EMITIR_CERTIFICADOS);
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
		}
	}
	
	
	

	
	/**
	 * <p>Carrega os municípios de uma unidade federativa.</p>
	 * <br />
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaParticipantesParaGerenciar.jsp</li>
	 *   </ul>
	 * 
	 * @throws DAOException
	 */
	public void carregarMunicipios() throws DAOException {		
		MunicipioDao dao = getDAO(MunicipioDao.class);
		UnidadeFederativa uf = dao.findByPrimaryKey(idUnidadeFederativa, UnidadeFederativa.class);
		Collection<Municipio> municipios = dao.findByUF(uf.getId(), "id", "nome");
		municipiosComBox = new ArrayList<SelectItem>();
		if (uf.getCapital() != null) {
			municipiosComBox.add(new SelectItem(uf.getCapital().getId(), uf.getCapital().getNome()));		
		}
		municipiosComBox.addAll(toSelectItems(municipios, "id", "nome"));
	}
	
	
	
	
	/** Retorna a quantidade de participantes. */
	public int getQtdParticipantes() {
		if(participantes == null)
			return 0;
		else
			return participantes.size();
	}
	
	/** Verifica se está gerenciando os participantes de uma atividade ou sub atividade */
	public boolean isGerenciandoParticipantesAtividade(){
		if(atividadeSelecionada != null && subatividadeSelecionada == null)
			return true;
		else
			return false;
	}
	

	/////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Tela que lista os participantes para impressão
	 *
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 */
	public String telaNotificacaoParticipantes() {
		return forward(NOTIFICACAO_PARTICIPANTE);
	}
	
	
	////////////////////////////////////////////////////////////////////
	
	public Collection<SelectItem> getMunicipiosComBox() {
		return municipiosComBox;
	}
	public AtividadeExtensao getAtividadeSelecionada() {
		return atividadeSelecionada;
	}
	public void setAtividadeSelecionada(AtividadeExtensao atividadeSelecionada) {
		this.atividadeSelecionada = atividadeSelecionada;
	}
	public SubAtividadeExtensao getSubatividadeSelecionada() {
		return subatividadeSelecionada;
	}
	public void setSubatividadeSelecionada(
			SubAtividadeExtensao subatividadeSelecionada) {
		this.subatividadeSelecionada = subatividadeSelecionada;
	}
	public List<ParticipanteAcaoExtensao> getParticipantes() {
		return participantes;
	}
	public void setParticipantes(List<ParticipanteAcaoExtensao> participantes) {
		this.participantes = participantes;
	}
	public boolean isCheckBuscaNome() {
		return checkBuscaNome;
	}
	public void setCheckBuscaNome(boolean checkBuscaNome) {
		this.checkBuscaNome = checkBuscaNome;
	}
	public String getBuscaNome() {
		return buscaNome;
	}
	public void setBuscaNome(String buscaNome) {
		this.buscaNome = buscaNome;
	}
	public boolean isCheckBuscaMunicipio() {
		return checkBuscaMunicipio;
	}
	public void setCheckBuscaMunicipio(boolean checkBuscaMunicipio) {
		this.checkBuscaMunicipio = checkBuscaMunicipio;
	}
	public int getIdUnidadeFederativa() {
		return idUnidadeFederativa;
	}
	public void setIdUnidadeFederativa(int idUnidadeFederativa) {
		this.idUnidadeFederativa = idUnidadeFederativa;
	}
	public int getIdMunicipio() {
		return idMunicipio;
	}
	public void setIdMunicipio(int idMunicipio) {
		this.idMunicipio = idMunicipio;
	}

	public boolean isParticipantesSemFrequencia() {
		return participantesSemFrequencia;
	}

	public void setParticipantesSemFrequencia(boolean participantesSemFrequencia) {
		this.participantesSemFrequencia = participantesSemFrequencia;
	}

	public boolean isParticipantesNaoDeclaracao() {
		return participantesNaoDeclaracao;
	}

	public void setParticipantesNaoDeclaracao(boolean participantesNaoDeclaracao) {
		this.participantesNaoDeclaracao = participantesNaoDeclaracao;
	}

	public boolean isParticipantesNaoCertificados() {
		return participantesNaoCertificados;
	}

	public void setParticipantesNaoCertificados(boolean participantesNaoCertificados) {
		this.participantesNaoCertificados = participantesNaoCertificados;
	}

	public int getPaginaAtual() {
		return paginaAtual;
	}

	public void setPaginaAtual(int paginaAtual) {
		this.paginaAtual = paginaAtual;
	}

	public int getTotalPaginas() {
		return totalPaginas;
	}

	public void setTotalPaginas(int totalPaginas) {
		this.totalPaginas = totalPaginas;
	}

	public int getTamanhoPagina() {
		return tamanhoPagina;
	}

	public void setTamanhoPagina(int tamanhoPagina) {
		this.tamanhoPagina = tamanhoPagina;
	}

	public int getTotalRegistros() {
		return totalRegistros;
	}

	public void setTotalRegistros(int totalRegistros) {
		this.totalRegistros = totalRegistros;
	}
	
}
