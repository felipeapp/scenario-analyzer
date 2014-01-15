package br.ufrn.sigaa.negocio;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import br.ufrn.arq.dao.GenericSharedDBDao;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.SubSistema;
import br.ufrn.sigaa.arq.acesso.DadosAcesso;
import br.ufrn.sigaa.ensino.dominio.ProcessoSeletivo;

/**
 * Classe auxiliar utilizada para controlar a disponibilidade dos casos de usos 
 * envolvidos no processo seletivo de acordo com o papel do usuário e o módulo que está acessando.
 * Todos os controles de visibilidade das funcionalidades relacionadas ao processo seletivo
 * devem estar definidos nesta classe. O objetivo é centralizar o negócio, removendo da view. 
 * 
 * @author Mário Rizzi
 * 
 */ 
public class OperacaoProcessoSeletivoValidator{

	
	/**
	 * Indica se a funcionalidade de publicar e despublicar processo seletivo deve ficar visível.
	 * @param acesso
	 * @param subsistema
	 * @param processo
	 * @return
	 */
	public static boolean isExibePublicar(ProcessoSeletivo processo) {
		
		if( getAcesso().isPpg() && ( SigaaSubsistemas.STRICTO_SENSU.equals(getSubSistema()) || SigaaSubsistemas.LATO_SENSU.equals(getSubSistema()) ) )
			return true;
		
		return false;
		
	}
	
	/**
	 * Indica se a funcionalidade de solicitar alteração no processo seletivo deve ficar visível.
	 * @param acesso
	 * @param subsistema
	 * @param processo
	 * @return
	 */
	public static boolean isExibeSolicitarAlteracao(ProcessoSeletivo processo){
		
		if( getAcesso().isPpg() && ( SigaaSubsistemas.STRICTO_SENSU.equals(getSubSistema()) || SigaaSubsistemas.LATO_SENSU.equals(getSubSistema()) ) && !processo.isInscricoesFinalizadas() )
			return true;
		
		return false;
		
	}
	
	/**
	 * Indica se a funcionalidade de confirmar pagamento no processo seletivo deve ficar visível.
	 * @param acesso
	 * @param subsistema
	 * @param processo
	 * @return
	 */
	public static boolean isExibeConfirmarPagamento(ProcessoSeletivo processo){
		
		if( isAcessoComum() && processo.getEditalProcessoSeletivo().getTaxaInscricao() > 0 
				&& processo.isInscricoesAbertas() )
			return true;
		
		return false;
		
	}
	
	/**
	 * Indica se a funcionalidade de alterar o processo seletivo deve ficar visível.
	 * @param acesso
	 * @param subsistema
	 * @param processo
	 * @return
	 */
	public static boolean isExibeAlterarProcessoSeletivo(ProcessoSeletivo processo){
		
		if( SigaaSubsistemas.STRICTO_SENSU.equals(getSubSistema()) || SigaaSubsistemas.LATO_SENSU.equals(getSubSistema()) ||
			 !( SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO.equals(getSubSistema()) || SigaaSubsistemas.PORTAL_COORDENADOR_LATO.equals(getSubSistema()) ) ||
			 processo.isPermiteAlterar() ) 
			return true;
		
		return false;
		
	}
	
	/**
	 * Indica se a funcionalidade de remover o processo seletivo deve ficar visível.
	 * @param acesso
	 * @param subsistema
	 * @param processo
	 * @return
	 */
	public static boolean isExibeRemoverProcessoSeletivo(ProcessoSeletivo processo){
		
		if( SigaaSubsistemas.STRICTO_SENSU.equals(getSubSistema()) || SigaaSubsistemas.LATO_SENSU.equals(getSubSistema()) ||
			 !( SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO.equals(getSubSistema()) || SigaaSubsistemas.PORTAL_COORDENADOR_LATO.equals(getSubSistema()) ) ||
			 processo.isPermiteAlterar() ) 
			return true;
		
		return false;
		
	}
	
	/**
	 * Indica se a funcionalidade de gerenciar inscrições deve ficar visível.
	 * @param acesso
	 * @param subsistema
	 * @param processo
	 * @return
	 */
	public static boolean isExibeGerenciarInscricoes(ProcessoSeletivo processo) {
		
		if( !( SigaaSubsistemas.STRICTO_SENSU.equals(getSubSistema()) || SigaaSubsistemas.LATO_SENSU.equals(getSubSistema()) ) && processo.isPublicado() )
			return true;
		
		return false;
		
	}
	
	/**
	 * Indica se a funcionalidade de gerar a lista de presença dos inscritos deve ficar visível.
	 * @param acesso
	 * @param subsistema
	 * @param processo
	 * @return
	 */
	public static boolean isExibeListarPresenca(ProcessoSeletivo processo) {
		
		if( isExibeGerenciarInscricoes(processo) )
			return true;
		
		return false;
		
	}
	
	/**
	 * Indica se a funcionalidade de imprimir resposta do questionário dos inscritos deve ficar visível.
	 * @param acesso
	 * @param subsistema
	 * @param processo
	 * @return
	 */
	public static boolean isExibeImprimirQuestionario(ProcessoSeletivo processo) {
		
		if( isExibeGerenciarInscricoes(processo) )
			return true;
		
		return false;
		
	}
	
	/**
	 * Indica se a funcionalidade de gerenciar agendamento deve ficar visível.
	 * @param acesso
	 * @param subsistema
	 * @param processo
	 * @return
	 */
	public static boolean isExibeGerenciarAgendamento(ProcessoSeletivo processo) {
		
		if( getAcesso().isDae() && SigaaSubsistemas.GRADUACAO.equals(getSubSistema()) 
			&& processo.isPossuiAgendamento()
			&& !processo.isAgendaFinalizada() )
			return true;
		
		return false;
		
	}

	/**
	 * Indica se o usuário possui os papéis de acesso as funcionalidades
	 * comuns a  todos os níveis.
	 * @param acesso
	 * @param subsistema
	 * @param processo
	 * @return
	 */
	public static boolean isAcessoComum() {
		
		if( getAcesso().isCoordenadorCursoStricto() || getAcesso().isCoordenadorCursoLato() || 
			getAcesso().isCoordenadorCursoTecnico() || getAcesso().isSecretariaPosGraduacao() || 
			getAcesso().isSecretarioLato() || getAcesso().isSecretarioTecnico() )
			return true;
		
		return false;
		
	}
	
	/**
	 * Retorna a entidade DadosAcesso, que contém as informações da sessão do
	 * usuário, com informações de contexto
	 * 
	 * @return
	 */
	private static DadosAcesso getAcesso() {
		return (DadosAcesso) getCurrentSession().getAttribute("acesso");
	}
	
	/**
	 * Retorna o subsistema que está sendo utilizado pelo usuário.
	 */
	private static SubSistema getSubSistema() {
		SubSistema subsistema = null;
		if (getCurrentSession().getAttribute("subsistema") instanceof String) {
			GenericSharedDBDao genDAO = null;
			String idSubsistema = (String) getCurrentSession().getAttribute(
					"subsistema");
			try {
				genDAO = new GenericSharedDBDao();
				subsistema = genDAO.findByPrimaryKey(Integer
						.parseInt(idSubsistema), SubSistema.class);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (genDAO != null)
					genDAO.close();
			}
		} else {
			subsistema = (SubSistema) getCurrentSession().getAttribute(
					"subsistema");
		}
		return subsistema;
	}
	
	/**
	 * Possibilita o acesso ao HttpSession.
	 */
	private static HttpSession getCurrentSession() {
		return getCurrentRequest().getSession(true);
	}
	
	/**
	 * Possibilita o acesso ao HttpServletRequest.
	 */
	private static HttpServletRequest getCurrentRequest() {
		return (HttpServletRequest) getExternalContext().getRequest();
	}

	/**
	 * Acessa o external context do JavaServer Faces
	 **/
	private static ExternalContext getExternalContext() {
		return FacesContext.getCurrentInstance().getExternalContext();
	}
	
	

	
}
