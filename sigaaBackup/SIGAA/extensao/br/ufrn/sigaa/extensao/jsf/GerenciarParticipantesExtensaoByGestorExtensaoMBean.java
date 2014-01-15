/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 15/02/2013
 * 
 */
package br.ufrn.sigaa.extensao.jsf;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.extensao.dao.inscricoes_atividades.GerenciarParticipantesCursosEventoExtensaoDao;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.jsf.inscricoes_atividades.AbstractGerenciarParticipantesExtensaoMBean;
import br.ufrn.sigaa.projetos.dominio.Projeto;

/**
 * <p>MBean para emiss�o dos certificados e declara��es por parte dos gestores de extens�o.</p>
 * 
 * <p>EXCLUSIVO PARA SER USADO PELOS GESTORES DO M�DULO DE EXTENS�O !!!!!!!!!!!!! </p>
 * 
 * @author jadson - jadson@info.ufrn.br
 *
 */
@Component("gerenciarParticipantesExtensaoByGestorExtensaoMBean")
@Scope("request")
public class GerenciarParticipantesExtensaoByGestorExtensaoMBean  extends AbstractGerenciarParticipantesExtensaoMBean implements PesquisarProjetosExtensao{

	/** Lista atividade e sub atividade que podem ter seus participantes gerenciados (Emitir certificados e declara��es). */
	private static final String LISTA_CURSOS_EVENTOS_PARA_GERENCIAR_PARTICIPANTES_BY_GESTOR_EXTENSAO = "/extensao/GerenciarInscricoes/listaCursosEventosParaGerenciarParticipantesByGestorExtensao.jsp";
	
	
	/** Lista TODOS os participantes da atividade ou sub atividade selecionada para realizar as a��es em cima de cada um. */
	private static final String LISTA_PARTICIPANTES_PARA_GERENCIAR_BY_GESTOR_EXTENSAO = "/extensao/GerenciarInscricoes/listaParticipantesParaGerenciarByGestorExtensao.jsp";
	
	
	/** O projeto selecionado da busca padr�o do sistema */
	private Projeto projetoSelecionado;
	
	/**  A listagem geral de cursos que podem ter seus participantes gerenciados pelo coordenador. (Cont�m os dados extritamente necess�rios ).
	 * 
	 *  Tem a atividade selecionada pelo gestor de extens�o junto com as informa��es da sub atividades. 
	 */
	private List<AtividadeExtensao> cursosEventosParaGerenciarParticipantes;
	
	
	/**
	 * <p>Inicia o caso de uso pela busca de atividades de extens�o.</p>
	 * 
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/extensao/menu.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 */
	public String selecionarAtividadeExtensao() throws ArqException {
		BuscaPadraoProjetosDeExtensaoMBean buscaMbean = getMBean ("buscaPadraoProjetosDeExtensaoMBean");
		return buscaMbean.iniciaBuscaSelecaoProjeto(this, "Buscar Projetos de Extens�o Para Emiss�o Declara��es e Certificados", 
				"<p>Seleciona um projeto de extens�o para emitir as declara��es ou certificados dos participantes do projeto.</p>");
	}

	
	////////////// M�todos da interface de busca ///////////////
	
	@Override
	public void setProjetoExtensao(Projeto projeto) {
		projetoSelecionado = projeto;
	}

	@Override
	public String selecionouProjetExtensao() throws ArqException {
		
		GerenciarParticipantesCursosEventoExtensaoDao dao = null;
		
		try{
			
			dao = getDAO(GerenciarParticipantesCursosEventoExtensaoDao.class);
			
			cursosEventosParaGerenciarParticipantes = dao
					.findCursoEventoAtivosGerenciarParticipantesByProjeto(projetoSelecionado.getId());
			
		}finally{
			if(dao != null ) dao.close();
		}
		
		
		return telaListaCursosEventosParaGerenciarParticipantesByGestorExtensao();
	}

	@Override
	public String cancelarBuscaProjetExtensao() {
		return super.cancelar();
	}
	
	////////////////////////////////////////////////////////////////
	
	
	
	/**
	 * 
	 * Lista os participantes da atividades selecionada para emitir certificados, etc...
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaCursosEventosParaGerenciarParticipantes.jsp</li>
	 *   </ul>
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 * @throws DAOException 
	 */
	public String listarParticipantesAtividade() throws DAOException{
		
		Integer idAtividadeExtensao = getParameterInt("idAtividadeSelecionada");
		Integer idSubAtividadeExtensao = getParameterInt("idSubAtividadeSelecionada");
		
		return listarParticipantesAtividade(idAtividadeExtensao, idSubAtividadeExtensao);
	}
	
	
	
	/**
	 *  <p>Atualiza a listagem de particpantes.</p> 
	 *  <p>Usado quando o coordenador adiciona ou remove participante, deve-se buscar novamente.</p>
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @param idAtividadeExtensao
	 * @param idSubAtividadeExtensao
	 * @return
	 * @throws DAOException
	 */
	public String listarParticipantesAtividade(Integer idAtividadeExtensao, Integer idSubAtividadeExtensao) throws DAOException{
		super.listarParticipantesAtividade(idAtividadeExtensao, idSubAtividadeExtensao);
		return telaListaParticipantesParaGerenciaByGestorExtensao();
	}
	
	
	

	/**
	 * 
	 * Usado quando o usu�rio busca a partir do furmul�rio na p�gina, nesse caso deve-se ser� as 
	 * informa��es de pagina��o. Porque o usu�rio pode alterar a quantidade de resultados por p�gina.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaParticipantesParaGerenciar.jsp</li>
	 *   </ul>
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 * @throws DAOException 
	 */
	public String filtarParticipantesAtividade() throws DAOException{
		super.filtarParticipantesAtividade();
		return telaListaParticipantesParaGerenciaByGestorExtensao();
	}
	
	
	/**
	 *  Realiza efetivamente a busca de participantes.
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 * @throws DAOException
	 */
	public String buscarParticipantesAtividadePaginado() throws DAOException{
		
		super.buscarParticipantesAtividadePaginado();
		return telaListaParticipantesParaGerenciaByGestorExtensao();
		
	}
	
	
	/////////// Tela de navega��o ///////////
	
	public String telaListaCursosEventosParaGerenciarParticipantesByGestorExtensao(){
		return forward(LISTA_CURSOS_EVENTOS_PARA_GERENCIAR_PARTICIPANTES_BY_GESTOR_EXTENSAO);
	}
	
	/**
	 * Tela que lista os participantes para gerenci�-los.
	 *
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 */
	public String telaListaParticipantesParaGerenciaByGestorExtensao() {
		return forward(LISTA_PARTICIPANTES_PARA_GERENCIAR_BY_GESTOR_EXTENSAO);
	}
	
	
	/**
	 * Retorna a quantidade de cursos e eventos encontrados.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaCursosEventosParaInscricao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public int getQtdCursosEventosParaGerenciarParticipantes() {
		if(cursosEventosParaGerenciarParticipantes == null)
			return 0;
		else
			return cursosEventosParaGerenciarParticipantes.size();
	}


	public List<AtividadeExtensao> getCursosEventosParaGerenciarParticipantes() {
		return cursosEventosParaGerenciarParticipantes;
	}


	public void setCursosEventosParaGerenciarParticipantes(List<AtividadeExtensao> cursosEventosParaGerenciarParticipantes) {
		this.cursosEventosParaGerenciarParticipantes = cursosEventosParaGerenciarParticipantes;
	}
	
	
	
}
