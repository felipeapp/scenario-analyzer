package br.ufrn.sigaa.extensao.jsf;

import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.extensao.AvaliacaoExtensaoDao;
import br.ufrn.sigaa.arq.dao.projetos.EditalDao;
import br.ufrn.sigaa.arq.dao.projetos.ProjetoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.EditalExtensao;

/**
 * Classe responsável pela classificaçao das ações de extensão 
 * de acordo com as avaliações dos membros do comitê Ad Hoc.
 * 
 * @author ilueny
 *
 */
@Component("classificarAcaoExtensao")
@Scope("request")
public class ClassificarAcaoExtensaoMBean extends SigaaAbstractController<AtividadeExtensao> {

	private EditalExtensao edital = new EditalExtensao();
	private List<AtividadeExtensao> acoesExtensao;

	
	/**
	 * Exibi todos os editais de extensão.
	 * 
	 * Chamado por:
	 * <ul>
	 *  <li>/sigaa.war/extensao/ClassificarAcoes/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public List<SelectItem> getEditais() throws DAOException {
	    Collection<EditalExtensao> editais = getDAO(EditalDao.class).findAllAtivosExtensao();
	    return toSelectItems(editais, "id", "descricaoCompleta");
	}

	/**
	 * Ordena uma coleção de Ações de extensao por media final.
	 * 
	 * Chamado por:
	 * <ul>
	 * 	<li>/sigaa.war/extensao/ClassificarProjetos/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException 
	 */
	public String iniciarClassificacao() throws SegurancaException {
	    checkRole(SigaaPapeis.GESTOR_EXTENSAO);
	    try {
		AvaliacaoExtensaoDao dao =  getDAO(AvaliacaoExtensaoDao.class);
		dao.initialize(edital);
		acoesExtensao = dao.findClassificacaoByEdital(edital);
		prepareMovimento(SigaaListaComando.CLASSIFICAR_ACOES_EXTENSAO);
		if (ValidatorUtil.isEmpty(acoesExtensao)) {
		    addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		    return null;
		}
		return forward(ConstantesNavegacao.CLASSIFICAR_ACOES_CLASSIFICAR);
	    } catch(Exception e) {
		notifyError(e);
	    }
	    return null;
	}
		
	/**
	 * Finaliza o processo de avaliação das ações pelo comitê Ad Hoc
	 * e salva a classificação apresentada.
	 * 
	 * Chamado por:
	 * <ul>
	 * 	<li>/sigaa.war/extensao/ClassificarProjetos/classificar.jsp</li>
	 * </ul>
	 * 
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String confirmarClassificacao() throws ArqException {
	    checkRole(SigaaPapeis.GESTOR_EXTENSAO);	    
	    try {
		iniciarClassificacao();
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjAuxiliar(edital);
		mov.setColObjMovimentado(acoesExtensao);
		mov.setCodMovimento(SigaaListaComando.CLASSIFICAR_ACOES_EXTENSAO);
		execute(mov);
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		iniciarClassificacao();
	    } catch(NegocioException e) {
		addMensagemErro(e.getMessage());	
		return null;
	    } catch(Exception e) {
		notifyError(e);
	    }

	    return forward(ConstantesNavegacao.CLASSIFICAR_ACOES_CLASSIFICAR);
	}
	
	/**
	 * Exibe o resultado da pre-classificação para impressão.
	 * 
	 * Chamado por:
	 * <ul>
	 * 	<li>/sigaa.war/extensao/ClassificarProjetos/classificar.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException 
	 */
	public String preView() throws SegurancaException {
	    iniciarClassificacao();
	    return forward(ConstantesNavegacao.CLASSIFICAR_ACOES_VIEW);
	}

	/**
	 * Verifica se é permitido ao gestor reclassificar
	 * todas as açãoes vinculadas ao edital selecionado.
	 * 
	 * @return
	 */
	public boolean isPermitidoConfirmarClassificacao() {
	    ProjetoDao dao = getDAO(ProjetoDao.class);
	    int total = dao.getTotalProjetosClassificadosParaEdital(edital.getEdital().getId());
	    return total == 0;
	}
	
	public List<AtividadeExtensao> getAcoesExtensao() {
	    return acoesExtensao;
	}

	public void setAcoesExtensao(List<AtividadeExtensao> acoesExtensao) {
	    this.acoesExtensao = acoesExtensao;
	}

	public void setEdital(EditalExtensao edital) {
	    this.edital = edital;
	}

	public EditalExtensao getEdital() {
	    return edital;
	}
    
}
