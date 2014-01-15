/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 04/09/2012
 * 
 */
package br.ufrn.sigaa.extensao.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.util.Date;
import java.util.Iterator;

import javax.faces.event.ActionEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.dao.ObjetivosAtividadeExtensaoDao;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.Objetivo;
import br.ufrn.sigaa.extensao.dominio.ObjetivoAtividades;
import br.ufrn.sigaa.extensao.jsf.helper.AtividadeExtensaoHelper;

/**
 *
 * <p>MBean responsável por cadastrar objetos para um atividade de extensão.</p>
 *
 * <p> <i> Esse Mbean vai ser usado para cadatrar objetivos de programas de extensão de forma temporária,
 * mas a idéia que ele possa ser generalizado para cadatrar objetivos em alguma coisa, Programa, Projeto, Curso, Evento.</i> </p>
 * 
 * @author jadson
 *
 */
@Component("cadatraObjetivosExtensaoMBean")
@Scope("request")
public class CadatraObjetivosExtensaoMBean extends SigaaAbstractController<AtividadeExtensao> {

	
	/** Atributo utilizado para representar o objetivo do programa de Extensão */
	private Objetivo objetivo = new Objetivo();

	/** Atributo utilizado para representar o objetivo das atividades desenvolvida na ação de extensão */
	private ObjetivoAtividades objetivoAtividades = new ObjetivoAtividades();
	
	/** Atributo utilizado para informar se vai haver alteração ou não. */
	private boolean alterar = false;

	/** Atributo utilizado para representar o número do objeto  */
	private int objNum = 0;
	
	/** Atributo utilizado para representar o número do objeto excluido */
	private int objExcluido = 0;
	
	/** Determina se os objetivos do programa serão carregado do banco. 
	 * Serão Apenas na primeira vez que o usuário acessa a página.*/
	public boolean carregarObjetivosDoBanco = true;
	
	/**
	 * Toda vida que inicia esse MBean, pega o objeto "AtividadeExtensao" do Mbean AtividadeExtensaoMBean
	 * que está em sessão.
	 * 
	 * Como esse MBean está em request, precisa recuperar sem que é chamado para editar os objetivos.
	 */
	public CadatraObjetivosExtensaoMBean(){
		obj = ((AtividadeExtensaoMBean) getMBean("atividadeExtensao")).getObj();
	}
	
	
	/**
	 * Chamado na página para carregar os objetivos cadastrado, já que pelo fluxo de extensão ele 
	 * chama diretamente a página e não existem nenhum método nesse MBean que possa carregar. 
	 *  
	 *   <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li> sigaa.war/extensao/Atividades/objetivos_esperados_extensao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException 
	 */
	public String getCarregaObjetivosPrograma() throws DAOException{
		
		if(carregarObjetivosDoBanco){
			
			if(obj.getProgramaExtensao() == null){
				return "";
			}			
			
			ObjetivosAtividadeExtensaoDao dao =  null;
			try{
				dao = getDAO(ObjetivosAtividadeExtensaoDao.class);
//				obj.getProgramaExtensao().setObjetivos( dao.findObjetivosAtivosProgramaExtensao(obj.getProgramaExtensao()) );
			}finally{
				if(dao != null) dao.close();
			}
		}
			
		carregarObjetivosDoBanco = false;	
		return "";
	}
	
	
	
	public Objetivo getObjetivo() {
		return objetivo;
	}

	public void setObjetivo(Objetivo objetivo) {
		this.objetivo = objetivo;
	}

	
	/**
	 * Cada objetivo pode ter uma lista atividades,  esse vai adicionar essas atividades ao objetivo 
	 * de está sendo cadatrado
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li> sigaa.war/extensao/Atividades/objetivos_esperados_extensao.jsp</li>
	 *   </ul>
	 *
	 */
	public void vincularAtividadeAoObjetivo() {

		AtividadeExtensaoHelper.getAtividadeMBean().getAvisosAjax().clear();

		ListaMensagens lista= new ListaMensagens();
		if(ValidatorUtil.isEmpty(obj)){
			addMensagemErroAjax("Atividade de Extensao não selecionada corretamente.");
			return;
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
	 * Desvincula atividades vinculadas ao objetivo no método vincularAtividadeAoObjetivo()
	 * 
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li> sigaa.war/extensao/Atividades/objetivos_esperados_extensao.jsp</li>
	 *   </ul>
	 *   
	 * @throws DAOException 
	 * @throws SegurancaException 
	 */
	public void desVincularAtividadeAoObjetivo(ActionEvent event) throws DAOException, SegurancaException {
	    
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
//		    for (Iterator<Objetivo> ito = obj.getProgramaExtensao().getObjetivos().iterator(); ito.hasNext();) {
//				for (Iterator<ObjetivoAtividades> it = ito.next().getAtividadesPrincipais().iterator(); it.hasNext();) {
//				    if (it.next().getId() == idAtividade) {
//					it.remove();
//	
//					break;
//				    }
//				}
//		    }
		}
	}
	
	
	
	/**
	 * Adiciona um objetivo a lista de objetivos do programa já salvando no banco.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war/extensao/Atividades/objetivos_esperados_extensao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws SegurancaException
	 */
	public String adicionaObjetivoAoPrograma() throws SegurancaException {
	    objetivo.setAtivo(true);
	    // validacao
	    ListaMensagens mensagens = new ListaMensagens();
	    mensagens = objetivo.validate();
	    
	    if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
			return null;
	    }
	    
//	    if (!  obj.getProgramaExtensao().getObjetivos().contains(objetivo)  ) {
//			obj.getProgramaExtensao().addObjetivo(objetivo);
//			// grava no banco assim que o usuário adiciona
//			AtividadeExtensaoHelper.getAtividadeMBean().gravarTemporariamente(); 
//			addMensagemInformation("Objetivo inserido com sucesso no projeto.");
//	    } else {
//	    	addMensagemErro("Objetivo já inserido no projeto");
//	    }
	    
	    // limpa os dados do objetivo atual
	    objetivo = new Objetivo();
	    return null;
	}
	
	
	
	/** 
	 * Realiza o carregamento dos objetivos apartir do seu id.
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war/extensao/Atividades/objetivos_esperados_extensao.jsp</li>
	 *   </ul>
	 */
	public void preAlterarObjetivo(ActionEvent evt){
//		if(objExcluido != getParameterInt("idObj") ){
//		    int idObjetivos = getParameterInt("idObj");
//		    for(Objetivo o: obj.getProgramaExtensao().getObjetivos()){
//			if(o.getId() == idObjetivos)
//			    objetivo = o; 
//		    }
//		    alterar = true;
//		    return;
//		}
		
		addMensagemErroAjax("Registro já excluído");
		return;
	}
	
	
	
	/**
	 * Atualiza um objetivo do projeto.
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war/extensao/Atividades/objetivos_esperados_extensao.jsp</li>
	 *   </ul>
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
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war/extensao/Atividades/objetivos_esperados_extensao.jsp</li>
	 *   </ul>
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
//				obj.getProgramaExtensao().getObjetivos().remove(objetivo);
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
	 * <p>Submete os objetivos informado.</p>  
	 * 
	 * <p>Após submeter segue para o próximo passo do cadatro, definido na classe ControleFluxoAtividadeExtensao.java</p>
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/Atividades/objetivos_esperados_extensao.jsp
	 * 
	 * @return 
	 * @throws DAOException
	 */
	public String submeterObjetivos() throws DAOException {
		// Não tem o que fazer, já que o sistema já gravou a medida que os objetivos foram incluídos.
		
	    // para para o próximo passo
	    return AtividadeExtensaoHelper.getAtividadeMBean().proximoPasso(); 
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

	public int getObjNum() {
		return objNum;
	}

	public void setObjNum(int objNum) {
		this.objNum = objNum;
	}

	public int getObjExcluido() {
		return objExcluido;
	}

	public void setObjExcluido(int objExcluido) {
		this.objExcluido = objExcluido;
	}
	
	
}
