/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/11/2006
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.Objetivo;
import br.ufrn.sigaa.extensao.dominio.ObjetivoAtividades;
import br.ufrn.sigaa.extensao.dominio.ProjetoExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.extensao.jsf.helper.AtividadeExtensaoHelper;
import br.ufrn.sigaa.extensao.negocio.AtividadeExtensaoValidator;

/**
 * MBean responsável por controlar parte do cadastro de projeto de extensão. A
 * primeira parte do cadastro de um projeto de extensão eh controlada por
 * {@link AtividadeExtensaoMBean} a parte especifica relativa ao projeto é
 * controlada por esta classe.
 * 
 * @author Victor Hugo
 * @author Ilueny Santos
 * 
 */
@Scope("session")
@Component("projetoExtensao")
public class ProjetoExtensaoMBean extends SigaaAbstractController<AtividadeExtensao> {

	/** Atributo utilizado para representar o objetivo do projeto de Extensão */
	private Objetivo objetivo = new Objetivo();

	/** Atributo utilizado para representar o objetivo das atividades desenvolvida na ação de extensão */
	private ObjetivoAtividades objetivoAtividades = new ObjetivoAtividades();
	
	/** Atributo utilizado para informar se vai haver alteração ou não. */
	private boolean alterar = false;

	/** Atributo utilizado para representar o número do objeto  */
	private int objNum = 0;
	
	/** Atributo utilizado para representar o número do objeto excluido */
	private int objExcluido = 0;

	/**
	 * Limpa dados vinculados ao MBean
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 */
	public void clear() {
	    obj = new AtividadeExtensao();
	    obj.setTipoAtividadeExtensao(new TipoAtividadeExtensao(TipoAtividadeExtensao.PROJETO));
	    obj.setProjetoExtensao(new ProjetoExtensao());
	    alterar = false;
	    setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
	}

	/**
	 * Inicia e prepara o formulário.
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/Atividade/seleciona_atividade.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException {
	    if ((obj != null) && (!obj.isProjetoAssociado())) {
		clear();
	    }
	    AtividadeExtensaoHelper.getAtividadeMBean().prepararFormulario(this);
	    return forward(ConstantesNavegacao.DADOS_GERAIS);
	}
	/**
	 * Construtor MBean.
	 * JSP: Não invocado por JSP.
	 */
	public ProjetoExtensaoMBean() {
	    clear();

	}

	public ProjetoExtensao getProjeto() {
	    return obj.getProjetoExtensao();
	}

	public Collection<SelectItem> getAllCombo() {
	    return getAll(ProjetoExtensao.class, "id", "titulo");
	}
	
	/** 
	 * Cancela a alteração dos objetivos.
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/Atividade/objetivos_esperados.jsp
	 */
	public String cancelarObejtivo(){
		objetivo = new Objetivo();
		alterar = false;
		return null;
		
	}

	/**
	 * Valida e grava os dados do projeto e leva para a tela de adiçao de
	 * servidores.
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/Atividade/projeto.jsp
	 * 
	 * @return a pagina pra adição de servidores
	 */
	public String submeterProjeto() {

		// validação
		ListaMensagens mensagens = new ListaMensagens();
		AtividadeExtensaoValidator.validaDadosProjeto(obj, mensagens);
		
		if (!mensagens.isEmpty()) {
		    addMensagens(mensagens);
		    return null;
		}

	    return AtividadeExtensaoHelper.getAtividadeMBean().proximoPasso();
	}

	/**
	 * Valida os dados da tela de objetivos esperados de projeto e vai para a
	 * tela de cronograma.
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/Atividades/objetivos_esperados.jsp
	 * 
	 * @return 
	 * @throws DAOException
	 */
	public String submeterObjetivos() throws DAOException {
	    // validacao
	    ListaMensagens mensagens = new ListaMensagens();
	    AtividadeExtensaoValidator.validaObjetivos(getProjeto(), mensagens);
	    
	    if (!mensagens.isEmpty()) {
		addMensagens(mensagens);
		return null;
	    }

	    // vai p/ cronograma
	    return AtividadeExtensaoHelper.getAtividadeMBean().proximoPasso(); 
	}

	/**
	 * Método responsável por mostrar o proximo passo do cadastro de um projeto de Extensão.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String submeterCronograma() {
	    return AtividadeExtensaoHelper.getAtividadeMBean().proximoPasso();
	}

	/**
	 * Atualiza um objetivo do projeto.
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/Atividades/objetivos_esperados.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String alterarObjetivo() throws ArqException{
		
	    // validacao
	    ListaMensagens mensagens = new ListaMensagens();
	    mensagens = objetivo.validate();
	    if (!mensagens.isEmpty()) {
		addMensagens(mensagens);
		return null;
	    }
		
		// Atualiza no banco
		objetivo.setAtivo(true);		
		AtividadeExtensaoHelper.getAtividadeMBean().gravarTemporariamente();

		// limpa os dados do objetivo atual
		objetivo = new Objetivo();
		alterar = false;
		addMensagem(OPERACAO_SUCESSO);
	    return null;
	}

	/**
	 * remove um objetivo da lista de objetivos do projeto
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/Atividades/objetivos_esperados.jsp
	 * 
	 * 
	 * @return
	 * @throws SegurancaException 
	 */
	public String removeObjetivo() throws SegurancaException {
		if(objNum != getParameterInt("idObjetivo") ){
			int id = getParameterInt("idObjetivo", 0);
			if (id == 0) {
				addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
			} else {
				Objetivo objetivo = new Objetivo(id);
				// remove do banco
				AtividadeExtensaoHelper.getAtividadeMBean().remover(objetivo); 
				AtividadeExtensaoHelper.getAtividadeMBean().getObj().getObjetivo().remove(objetivo);
			}
			
			objNum = id;
			objExcluido = id;
			objetivo = new Objetivo();
			alterar = false;
			return  null;
		}
		addMensagemErro("Registro já Excluído");
		return null;
	}

	/**
	 * Inclui atividades no objetivo que está sendo cadastrado
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/Atividades/objetivos_esperados.jsp
	 */
	public void adicionarObjetivoAtividades() {

		AtividadeExtensaoHelper.getAtividadeMBean().getAvisosAjax().clear();

		ListaMensagens lista= new ListaMensagens();
		if(ValidatorUtil.isEmpty(obj)){
			redirect("/sigaa/portais/docente/docente.jsf");
		}
		if ((objetivoAtividades.getDataInicio() != null) && (objetivoAtividades.getDataFim() != null)){
		    	Date inicio = objetivoAtividades.getDataInicio();
		    	Date fim = objetivoAtividades.getDataFim();
			ValidatorUtil.validaOrdemTemporalDatas(inicio, fim, true, "Período da Atividade: Data do término dever ser maior que a data de início.", lista);
			
			if(!CalendarUtils.isDentroPeriodo(obj.getDataInicio(), obj.getDataFim(), objetivoAtividades.getDataInicio()))
				lista.addErro("Data início da atividade vinculada ao projeto fora do período do projeto.");
		    if(!CalendarUtils.isDentroPeriodo(obj.getDataInicio(), obj.getDataFim(), objetivoAtividades.getDataFim()))
		    	lista.addErro("Data fim da atividade vinculada ao projeto fora do período do projeto.");

			AtividadeExtensaoHelper.getAtividadeMBean().getAvisosAjax().addAll(lista.getMensagens());
			if (!lista.isEmpty()) {
				return;
			}
		}
		
		
		if ("".equals(objetivoAtividades.getDescricao().trim())) {
			AtividadeExtensaoHelper.getAtividadeMBean().getAvisosAjax().add(
				new MensagemAviso("Descrição da atividade não informada!", TipoMensagemUFRN.ERROR));
		} else {
			if ((objetivo != null) && (objetivo.getAtividadesPrincipais() != null)) {
				objetivoAtividades.setPosicao(objetivo.getAtividadesPrincipais().size());
				objetivo.addAtividadesPrincipais(objetivoAtividades);
				// limpa atividades da view
				objetivoAtividades = new ObjetivoAtividades();
			}

		}

	}

	/**
	 * Apaga atividades no objetivo que está sendo cadastrado
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/Atividades/objetivos_esperados.jsp
	 * @throws DAOException 
	 * @throws SegurancaException 
	 * 
	 * 
	 */
	public void removerObjetivoAtividades(ActionEvent event) throws DAOException, SegurancaException {
	    
		Integer posicao = ((Integer) event.getComponent().getAttributes().get("posicao"));
		Integer idAtividade = ((Integer) event.getComponent().getAttributes().get("idAtividade"));

		// removendo atividade não cadastrada no banco
		if (posicao >= 0 && idAtividade == 0) {
			for (Iterator<ObjetivoAtividades> it = objetivo.getAtividadesPrincipais().iterator(); it.hasNext();) {
				if (it.next().getPosicao() == posicao) {
					it.remove();
				}
			}
		}

		if (idAtividade > 0 && posicao == 0) {

		    //removendo do banco.
		    ObjetivoAtividades o = getGenericDAO().findByPrimaryKey(idAtividade, ObjetivoAtividades.class);
		    if (o != null) {
			AtividadeExtensaoHelper.getAtividadeMBean().remover(o);
		    }
		    
		    // remove da view
		    for (Iterator<Objetivo> ito = AtividadeExtensaoHelper.getAtividadeMBean().getObj().getObjetivo().iterator(); ito.hasNext();) {
			for (Iterator<ObjetivoAtividades> it = ito.next().getAtividadesPrincipais().iterator(); it.hasNext();) {
			    if (it.next().getId() == idAtividade) {
				it.remove();

				break;
			    }
			}
		    }
		}
	}

	/**
	 * Método utilizado para retornar o objetivo do Projeto de Extensão
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamdo por JSP(s)</li>
	 * </ul>
	 * 
	 * @return Returns the objetivo.
	 */
	public Objetivo getObjetivo() {
		return objetivo;
	}

	/**
	 * Método utilizado para setar o objetivo do Projeto de Extensão
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamdo por JSP(s)</li>
	 * </ul>
	 * 
	 * @param objetivo
	 */
	public void setObjetivo(Objetivo objetivo) {
		this.objetivo = objetivo;
	}

	public ObjetivoAtividades getObjetivoAtividades() {
		return objetivoAtividades;
	}

	public void setObjetivoAtividades(ObjetivoAtividades objetivoAtividades) {
		this.objetivoAtividades = objetivoAtividades;
	}
	
	public boolean isAlterar() {
		return alterar;
	}

	public void setAlterar(boolean alterar) {
		this.alterar = alterar;
	}

}
