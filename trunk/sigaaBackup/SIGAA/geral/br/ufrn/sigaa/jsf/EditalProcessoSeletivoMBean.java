/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '30/09/2009'
 *
 */
package br.ufrn.sigaa.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConstantesErro;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.sigaa.arq.dao.ensino.EditalProcessoSeletivoDao;
import br.ufrn.sigaa.arq.dao.ensino.ProcessoSeletivoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.AgendaProcessoSeletivo;
import br.ufrn.sigaa.ensino.dominio.EditalProcessoSeletivo;
import br.ufrn.sigaa.ensino.dominio.ProcessoSeletivo;
import br.ufrn.sigaa.ensino.dominio.StatusProcessoSeletivo;
import br.ufrn.sigaa.ensino.negocio.dominio.MovimentoEditalProcessoSeletivo;

/**
 * Managed bean responsável em gerenciar os editais
 * dos processos seletivos 
 * 
 * @author Mário Rizzi
 * 
 */
@Component("editalProcessoSeletivo") @Scope("session") 
public class EditalProcessoSeletivoMBean extends SigaaAbstractController<EditalProcessoSeletivo> {


	/** Formulário para informar o Motivo de Solicitação de Alteração. */
	public final static String JSP_MOTIVO_ALTERACAO = "/administracao/cadastro/ProcessoSeletivo/informa_motivo_alteracao.jsp";
	
	public EditalProcessoSeletivoMBean(){
			obj = new EditalProcessoSeletivo();
			obj.setAgendas(new ArrayList<AgendaProcessoSeletivo>(0));
			obj.setProcessosSeletivos(new ArrayList<ProcessoSeletivo>(0));
	}
	
	/**
	 * JSP: Método não invocado por JSP's'.
	 */
	@Override
	public String getDirBase() {
		return "/administracao/cadastro/ProcessoSeletivo";
	}
	
	/**
	 * Método responsável em carregar o combo contendo todos editais de processo seletivo
	 * do nível de ensino de GRADUAÇÃO, utilizado no formulário de relatórios.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/administracao/cadastro/ProcessoSeletivo/form_relatorio.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllComboGraduacao() throws DAOException{
		
		Collection<SelectItem> allGraduacaoCombo = new ArrayList<SelectItem>();
		Collection<EditalProcessoSeletivo> editaisGraduacao = 
			getDAO(EditalProcessoSeletivoDao.class).findByNivel(NivelEnsino.GRADUACAO);
		
		for (EditalProcessoSeletivo e : editaisGraduacao) {
			allGraduacaoCombo.add(new SelectItem(e.getId(), e.getNome()));
		}
		return allGraduacaoCombo;
		
	}
	
	/**
	 * Inicia tela de solicitação de alteração do processo seletivo 
	 * <br /> 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/administracao/cadastro/ProcessoSeletivo/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String preSolicitarAlteracao() throws ArqException{
		checkRole(SigaaPapeis.PPG);

		populateObj(true);
		verificaObjRemovido();
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_ALTERAR_EDITAL_PROCESSO_SELETIVO.getId());
		prepareMovimento(SigaaListaComando.CADASTRAR_ALTERAR_EDITAL_PROCESSO_SELETIVO);
		
		Collection<ProcessoSeletivo> processos = getDAO(ProcessoSeletivoDao.class).findByEdital(obj.getId()); 
		obj.setProcessosSeletivos(CollectionUtils.toList(processos));
		
		obj.setMotivoAlteracao(null);
		
		return forward(JSP_MOTIVO_ALTERACAO);
	}
	
	/**
	 * Solicita Alteração do Processo Seletivo para o Coordenador do programa (Apenas Membros da PPG).
	 * <br /> 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/administracao/cadastro/ProcessoSeletivo/informa_motivo_alteracao.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String solicitarAlteracao() throws ArqException, NegocioException{	
		if (obj.getMotivoAlteracao().isEmpty()){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Motivo");
			return null;
		}		
		mudarStatus(StatusProcessoSeletivo.SOLICITADO_ALTERACAO);
		ProcessoSeletivoMBean processoBean = getMBean("processoSeletivo");
		return processoBean.listaProcessos();
	}	
	
	/**
	 * Muda o status do processo seletivo - (Apenas para PPG)
	 * <br /> 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/administracao/cadastro/ProcessoSeletivo/lista.jsp</li>
	 * </ul>
	 * @param status
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void mudarStatus(int status) throws ArqException, NegocioException{	
		if (obj.getStatus().equals(StatusProcessoSeletivo.PUBLICADO) && status == StatusProcessoSeletivo.PUBLICADO)
			obj.setStatus(StatusProcessoSeletivo.PENDENTE_APROVACAO);
		else
			obj.setStatus(status);
		
		obj.setAgendas(null);
		// Persistir processo seletivo
		MovimentoEditalProcessoSeletivo mov = new MovimentoEditalProcessoSeletivo();		
		mov.setEditalProcessoSeletivo(obj);
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_ALTERAR_EDITAL_PROCESSO_SELETIVO);
		execute(mov);		
		
		addMessage("Processo Seletivo alterado com sucesso", TipoMensagemUFRN.INFORMATION);	
	}
	
	/**
	 * Publica o Processo Seletivo (Apenas Membros da PPG).
	 * <br /> 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/administracao/cadastro/ProcessoSeletivo/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String publicar() throws ArqException, NegocioException{
		checkRole(SigaaPapeis.PPG);

		populateObj(true);
		
		verificaObjRemovido();
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_ALTERAR_EDITAL_PROCESSO_SELETIVO.getId());
		prepareMovimento(SigaaListaComando.CADASTRAR_ALTERAR_EDITAL_PROCESSO_SELETIVO);
		
		mudarStatus(StatusProcessoSeletivo.PUBLICADO);					
		ProcessoSeletivoMBean processoBean = getMBean("processoSeletivo");
		return processoBean.listaProcessos();
	}
	
	/** 
	 * Verifica se o objeto manipulado já foi removido.
	 * @throws DAOException
	 * @throws ArqException
	 */
	private void verificaObjRemovido() throws DAOException, ArqException {
		EditalProcessoSeletivo objRefresh = getGenericDAO().refresh(obj);
		if (isEmpty(objRefresh))
			throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA,"");
	}
	
	/**
	 * Prepara formulário para alteração
	 * JSP'S chamadas: \SIGAA\app\sigaa.ear\sigaa.war\administracao\cadastro\ProcessoSeletivo\lista.jsp
	 */
	public String atualizar() throws ArqException {
		
		populateObj(true);	
		
		if(!isEmpty(obj.getAgendas()))
			obj.getAgendas().iterator().next();
		
		setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
		prepareMovimento(ArqListaComando.ALTERAR);
		carregaAgenda();
		setReadOnly(false);
		return redirect(getFormPage());
		
	}
	
	@Override
	public String getLabelCombo() {
		return "nome";
	}
	
	@Override
	public String getIdCombo() {
		return "id";
	}

	/**
	 * Popula o edital conforme o id passado como parâmetro
	 * JSP'S chamadas: \SIGAA\app\sigaa.ear\sigaa.war\administracao\cadastro\ProcessoSeletivo\form_qtd_inscritos.jsp
	 * @return
	 * @throws ArqException
	 */
	public String carregaAgenda() throws ArqException{
		
		return redirect(getFormPage());
	}
	
	@Override
	public String getFormPage() {
		return getDirBase()+"/form_qtd_inscritos.jsf";
	}

	/**
	 * Incrementa a nº máximo de inscritos para uma data de agendamento.
	 * JSP'S chamadas: \SIGAA\app\sigaa.ear\sigaa.war\administracao\cadastro\ProcessoSeletivo\form_qtd_inscritos.jsp
	 * @param e
	 * @return
	 */
	public String incrementaQtdMaxInsc(ActionEvent e){
		Integer pos = getParameterInt("posicao");
		int qtd = obj.getAgendas().get(pos).getQtd()+1;
		obj.getAgendas().get(pos).setQtd(qtd);
		return "";
	}

}