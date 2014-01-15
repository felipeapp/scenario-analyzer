/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 12/06/2009
 *
 */

package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.text.ParseException;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.dao.ensino.AvisoFaltaDocenteHomologadaDao;
import br.ufrn.sigaa.arq.dao.ensino.DadosAvisoFaltaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.AvisoFaltaDocenteHomologada;
import br.ufrn.sigaa.ensino.dominio.DadosAvisoFalta;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAvisoFaltaHomologado;
import br.ufrn.sigaa.ensino.graduacao.jsf.AvisoFaltaDocenteMBean;

/**
 * MBean responsável por gerenciar os avisos de faltas que serão homologadas para um docente 
 * 
 * @author Henrique André
 */
@Component("avisoFaltaHomologada") @Scope("request")
public class AvisoFaltaDocenteHomologadaMBean extends SigaaAbstractController<AvisoFaltaDocenteHomologada> {

//	trata da comunicação com o SIGPRH
//	@Resource(name="servidorRemoteServiceInvoker")
//	private ServidorRemoteService servidorRemoteServiceInvoker;
	
	/** Armazena os avisos de falta homologados. */
	private List<AvisoFaltaDocenteHomologada> avisos = null;

	/**
	 * Homologa o aviso de falta
	 * 
	 * JSP: /sigaa.war/graduacao/denuncia_professor/list.jsp
	 * @return
	 * @throws ArqException 
	 * @throws Exception 
	 * @throws NegocioException 
	 */
	public String iniciarAceitarHomologacao() throws ArqException  {
		Integer idDadosFalta = getParameterInt("idDadosFalta");
		
		if (isEmpty(idDadosFalta)) {
			addMensagemErro("Não foi possível recuperar o aviso de falta.");
			return null;
		}
		
		DadosAvisoFaltaDao dao = getDAO(DadosAvisoFaltaDao.class);
		DadosAvisoFalta dadosAvisoFalta = dao.findByPrimaryKey(idDadosFalta, DadosAvisoFalta.class);
		
		
		obj = new AvisoFaltaDocenteHomologada();
		obj.setDadosAvisoFalta(dadosAvisoFalta);
		
		if(isHomologada()){
			int id = getParameterInt("idAvisoHomologado");
			obj = dao.findByPrimaryKey(id, AvisoFaltaDocenteHomologada.class);
			prepareMovimento(ArqListaComando.ALTERAR);
		}
		else{
			prepareMovimento(ArqListaComando.CADASTRAR);
		}
		
		obj.setMovimentacao(dao.findByPrimaryKey(MovimentacaoAvisoFaltaHomologado.PENDENTE_APRESENTACAO_PLANO.getId(), MovimentacaoAvisoFaltaHomologado.class));
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		if(isHomologada())
			mov.setCodMovimento(ArqListaComando.ALTERAR);
		else
			mov.setCodMovimento(ArqListaComando.CADASTRAR);
		
		try {
			execute(mov);
		} catch (NegocioException e) {
			tratamentoErroPadrao(e);
		}
		
		AvisoFaltaDocenteMBean mBean = getMBean("avisoFalta");
		clearMensagens();
		mBean.buscar();
		addMensagemInformation("Aviso de Falta Homologada com sucesso para " + obj.getDadosAvisoFalta().getDocenteNome());
		return null;
	}

	/**
	 * Vai para o formulário que nega o aviso de falta
	 * 
	 * JSP: /sigaa.war/graduacao/denuncia_professor/list.jsp
	 * @return
	 * @throws ArqException
	 * @throws ParseException
	 */
	public String iniciarNegarHomologacao() throws ArqException, ParseException {
		obj = new AvisoFaltaDocenteHomologada();
		Integer idDadosFalta = getParameterInt("idDadosFalta");
		
		if (isEmpty(idDadosFalta)) {
			addMensagemErro("Não foi possível recuperar o aviso de falta.");
			return null;
		}
		
		DadosAvisoFaltaDao dao = getDAO(DadosAvisoFaltaDao.class);
		DadosAvisoFalta dadosAvisoFalta = dao.findByPrimaryKey(idDadosFalta, DadosAvisoFalta.class);
		
		obj.setDadosAvisoFalta(dadosAvisoFalta);
		
		if(isHomologada()){
			int id = getParameterInt("idAvisoHomologado");
			obj = dao.findByPrimaryKey(id, AvisoFaltaDocenteHomologada.class);
			
			if(obj.getMovimentacao().getId() != MovimentacaoAvisoFaltaHomologado.HOMOLOGACAO_NEGADA.getId())
				prepareMovimento(ArqListaComando.ALTERAR);
		}
		else
			prepareMovimento(ArqListaComando.CADASTRAR);
		
		setConfirmButton("Negar");
		
		obj.setMovimentacao(dao.findByPrimaryKey(MovimentacaoAvisoFaltaHomologado.HOMOLOGACAO_NEGADA.getId(), MovimentacaoAvisoFaltaHomologado.class));
		if (hasErrors())
			return null;
		
		return forward("/ensino/aviso_falta/docente/negar_aviso_falta.jsp");
	}
	
	/**
	 * Cadastra a negação do aviso de falta
	 * 
	 * @return
	 * @throws Exception
	 */
	public String negarHomologacao() throws Exception {
		
		validarNegacao();
		
		if (hasErrors())
			return null;
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		if(isHomologada())
			mov.setCodMovimento(ArqListaComando.ALTERAR);
		else
			mov.setCodMovimento(ArqListaComando.CADASTRAR);
		
		try {
			execute(mov);
		} catch (NegocioException e) {
			return tratamentoErroPadrao(e);
		}
		
		if(getConfirmButton().equals("Estornar")){
			redirectJSF("ensino/aviso_falta/docente/gerenciar_pendentes.jsp");
			return null;
		}
		else{
			AvisoFaltaDocenteMBean avisoBean = (AvisoFaltaDocenteMBean) getMBean("avisoFalta");
			avisoBean.setResultado(null);
			clearMensagens();
			avisoBean.buscar();
			if(getConfirmButton().equals("Estornar"))
				addMensagemInformation("O aviso de falta foi estornado com sucesso.");
			else
				addMensagemInformation("O aviso de falta foi negado com sucesso.");
			return null;
		}
	}	
	
	/**
	 * Valida os campos da negação.
	 */
	private void validarNegacao(){
		if(obj.getMotivoNegacao().trim() == "" || obj.getMotivoNegacao() == null){
			addMensagemErro("Justificativa: Campo Obrigatório Não informado.");
		}		
	}
	
	private boolean isHomologada() throws DAOException {
		if(!isEmpty(obj.getDadosAvisoFalta())){
			AvisoFaltaDocenteHomologadaDao faltaHomologadaDao = getDAO(AvisoFaltaDocenteHomologadaDao.class);
			return faltaHomologadaDao.isExists(obj);
		}
		return false;
	}

	/**
	 * Lista as homologações de falta que não possuem plano de aula
	 * 
	 * JSP: Não invocado por JSP
	 * @return
	 * @throws ArqException
	 */
	public String listarHomologacoesPendentes() throws ArqException {
		setSubSistemaAtual(SigaaSubsistemas.PORTAL_DOCENTE);
		return forward("/ensino/aviso_falta/docente/homologacoes_sem_planos.jsp");
	}
	
	/**
	 * Lista com as homologações sem plano de aula
	 * 
	 * JSP: /sigaa.war/ensino/falta_homologada/homologacao_pendentes.jsp
	 * @return
	 */
	public List<AvisoFaltaDocenteHomologada> getHomologacoesSemPlanos() {
		AvisoFaltaDocenteHomologadaDao dao = getDAO(AvisoFaltaDocenteHomologadaDao.class);
		return dao.findHomologacoesComPlanosPendentesByServidor(getServidorUsuario().getId());
	}

	/**
	 * Lista com as homologações pendentes de análise por parte da chefia, o que inclui as seguintes {@link MovimentacaoAvisoFaltaHomologado}:
	 * <ul>
	 * <li></li>
	 * </ul>
	 * JSP: /sigaa.war/ensino/aviso_falta/docente/gerenciar_pendentes.jsp
	 * @return
	 */
	public List<AvisoFaltaDocenteHomologada> getHomologacoesPendentesAnalise() {
		AvisoFaltaDocenteHomologadaDao dao = getDAO(AvisoFaltaDocenteHomologadaDao.class);
		if(isEmpty(avisos)){
			avisos = dao.findHomologacoesComPlanosPendentesByDepartamento(getServidorUsuario().getUnidade().getId());
			for (AvisoFaltaDocenteHomologada avisoFaltaDocenteHomologada : avisos) {
				boolean possuiFalta = false;
				//O trecho de código comentado a seguir faz referência à busca de faltas no SIGPRH
//				Calendar cal = Calendar.getInstance();
//				cal.setTime(avisoFaltaDocenteHomologada.getDadosAvisoFalta().getDataAula());
//				cal.add(Calendar.DAY_OF_MONTH, -1);
//				Date antes = cal.getTime();
//				cal.add(Calendar.DAY_OF_MONTH, 2);
//				Date depois = cal.getTime();
				//Busca de faltas no SIGRH
//				if(avisoFaltaDocenteHomologada.getDadosAvisoFalta().getDocente() != null){
//					possuiFalta = servidorRemoteServiceInvoker.possuiFaltas(avisoFaltaDocenteHomologada.getDadosAvisoFalta().getDocente().getId(), antes, depois);				
//				}
				avisoFaltaDocenteHomologada.setFrequenciaEletronica(possuiFalta);
			}
		}
		return avisos;
	}
	
	/**
	 * Redireciona o fluxo para o sistema de recursos humanos
	 * 
	 * @return
	 */
	public String lancarAusenciaSIGRH() {
		int id = getParameterInt("id");
		return redirect("/entrarSistema.do?sistema=sigrh&url=/lancarAusencia/" + id +".jsf");
	}
	
	/**
	 * Estorna a operação
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarEstornar() throws ArqException {
		setConfirmButton("Estornar");
		
		AvisoFaltaDocenteHomologadaDao dao = getDAO(AvisoFaltaDocenteHomologadaDao.class);
		
		int id = getParameterInt("id");
		obj = dao.findByPrimaryKey(id, AvisoFaltaDocenteHomologada.class);
		
		if(obj.getMovimentacao().getId() != MovimentacaoAvisoFaltaHomologado.ESTORNADO.getId())
			prepareMovimento(ArqListaComando.ALTERAR);
		
		obj.setMovimentacao(dao.findByPrimaryKey(MovimentacaoAvisoFaltaHomologado.ESTORNADO.getId(), MovimentacaoAvisoFaltaHomologado.class));
		obj.setAtivo(false);
		
		return forward("/ensino/aviso_falta/docente/negar_aviso_falta.jsp");
	}
	
}
