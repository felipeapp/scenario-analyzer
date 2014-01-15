/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 19/05/2010
 *
 */
package br.ufrn.sigaa.monitoria.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.monitoria.AvaliacaoMonitoriaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.monitoria.dominio.EditalMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.negocio.ProjetoMonitoriaMov;
import br.ufrn.sigaa.monitoria.negocio.ProjetoMonitoriaValidator;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * MBean responsável pela publicação dos resultados das avaliaçoes dos projetos de monitoria
 * 
 * @author Geyson
 *
 */
@Component("publicarResultado")
@Scope("request")
public class PublicarResultadoAvaliacoesMBean extends SigaaAbstractController<ProjetoEnsino> {

	/** Atributo utilizado para representar uma coleção dos Projetos Avaliados */
	private Collection<ProjetoEnsino> projetosAvaliados;
	/** Atributo utilizado para representar o Edital de Monitoria */
	private EditalMonitoria edital;
	
	public PublicarResultadoAvaliacoesMBean(){
		setProjetosAvaliados(new ArrayList<ProjetoEnsino>());
		setEdital(new EditalMonitoria());
	}
	
	/**
	 * Método utilizado para checar os papeis do usuário e verificar se o mesmo tem permissão de acesso ao caso de uso
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 */
	@Override
	public void checkListRole() throws SegurancaException {
	    super.checkListRole();
	    checkRole(SigaaPapeis.GESTOR_MONITORIA);
	}
	
	/**
	 * Lista avaliações de projetos de monitoria
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/PublicarResultado/busca_edital.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciaBuscaAvaliacoes() throws ArqException{
		checkListRole();
		AvaliacaoMonitoriaDao dao = getDAO(AvaliacaoMonitoriaDao.class);
		dao.initialize(edital);
		ListaMensagens mensagens = new ListaMensagens();
		ProjetoMonitoriaValidator.validaAvaliacoesDiscrepantes(edital, mensagens);

		if(!mensagens.isEmpty()){
			addMensagens(mensagens);
			List<ProjetoEnsino> projetosDisc = dao.findProjetosDiscrepantes(TipoSituacaoProjeto.MON_AVALIADO, edital);
			((AvaliacaoMBean)getMBean("avalProjetoMonitoria")).setProjetosDiscrepancia(projetosDisc);
			return forward(ConstantesNavegacaoMonitoria.AVALIACAOPROJETO_DISCREPANCIA_LISTA);
		} else{
			prepareMovimento(SigaaListaComando.AVALIAR_PROJETO_MONITORIA);
			setOperacaoAtiva(SigaaListaComando.AVALIAR_PROJETO_MONITORIA.getId());
			projetosAvaliados =  dao.findProjetosByStatusEdital(TipoSituacaoProjeto.MON_AVALIADO, edital.getId());
			
			if (ValidatorUtil.isEmpty(projetosAvaliados)) {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
			return forward(ConstantesNavegacaoMonitoria.PUBLICAR_RESULTADO_LISTA);			
		}
	}
	
	/**
	 * Lista editais de monitoria.
	 * <br />
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 *  	<li>sigaa.war/monitoria/index.jsp</li>
	 * 	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarBuscarAvaliacoesEdital() throws ArqException{
		checkListRole();		
		return forward(ConstantesNavegacaoMonitoria.PUBLICAR_RESULTADO_SELECIONAR_EDITAL);
	}
	
	/**
	 * publica resultado dos projetos de ensino que concorrera a um edital.
	 * <br />
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 *  	<li>sigaa.war/monitoria/PublicarResultado/busca_edital.jsp</li>
	 * 	</ul>
	 * @return
	 * @throws ArqException 
	 */
	public String publicarResultadoProjetos() throws ArqException{
		
	   	if(!checkOperacaoAtiva(SigaaListaComando.AVALIAR_PROJETO_MONITORIA.getId())){
			return forward("/monitoria/index.jsp");
		}
	    	
		if(!projetosAvaliados.isEmpty() ){
			ProjetoMonitoriaMov mov = new ProjetoMonitoriaMov();
			mov.setCodMovimento(SigaaListaComando.AVALIAR_PROJETO_MONITORIA);
			prepareMovimento(SigaaListaComando.AVALIAR_PROJETO_MONITORIA);
			mov.setAcao(ProjetoMonitoriaMov.ACAO_PUBLICAR_RESULTADO_AVALIACOES);
			mov.setColObjMovimentado(projetosAvaliados);
			mov.setObjMovimentado(edital);
		
			try {
				execute(mov);
				removeOperacaoAtiva();
			} catch (NegocioException e) {
				addMensagemErro(e.getMessage());
				return null;
			} catch (ArqException e) {
				return tratamentoErroPadrao(e);
			}
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			return forward("/monitoria/index.jsp");
		}
		return null;
	}
	

	public EditalMonitoria getEdital() {
	    return edital;
	}

	public void setEdital(EditalMonitoria edital) {
	    this.edital = edital;
	}

	public Collection<ProjetoEnsino> getProjetosAvaliados() {
	    return projetosAvaliados;
	}

	public void setProjetosAvaliados(Collection<ProjetoEnsino> projetosAvaliados) {
	    this.projetosAvaliados = projetosAvaliados;
	}
	
}
