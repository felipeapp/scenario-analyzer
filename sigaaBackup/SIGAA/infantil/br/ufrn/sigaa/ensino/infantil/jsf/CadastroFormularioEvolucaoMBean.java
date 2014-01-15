/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 05/04/2010
 *
 */
package br.ufrn.sigaa.ensino.infantil.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.infantil.FormularioEvolucaoDao;
import br.ufrn.sigaa.arq.dao.ensino.infantil.RegistroEvolucaoCriancaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.infantil.dominio.Area;
import br.ufrn.sigaa.ensino.infantil.dominio.Conteudo;
import br.ufrn.sigaa.ensino.infantil.dominio.FormularioEvolucaoCrianca;
import br.ufrn.sigaa.ensino.infantil.dominio.ObjetivoConteudo;
import br.ufrn.sigaa.ensino.infantil.dominio.TipoFormaAvaliacao;
import br.ufrn.sigaa.ensino.infantil.negocio.MovimentoCadastroFormularioEvolucao;

/**
 * Controlador responsável pelas operações de cadastro de formulários
 * de evolução da criança no ensino infantil
 * 
 * @author Thalisson Muriel
 *
 */
@Component("cadastroFormularioEvolucaoMBean") @Scope("session")
public class CadastroFormularioEvolucaoMBean extends SigaaAbstractController<FormularioEvolucaoCrianca> {
	
	/** Constantes das Views */
	public final String JSP_FORM = "/infantil/CadastroFormularioEvolucao/form_cadastro_evolucao.jsp";
	public final String JSP_LISTA = "/infantil/CadastroFormularioEvolucao/lista.jsp";
	public final String JSP_REMOVER = "/infantil/CadastroFormularioEvolucao/remover.jsp";
	public final String JSP_VIEW = "/infantil/CadastroFormularioEvolucao/view.jsp";
	
	//Responsável por armazenar a Área corrente no momento de cadastro dos Conteúdos e Objetivos
	private Area bloco;
	
	private ArrayList<Area> blocos = new ArrayList<Area>();
		
	private ArrayList<ObjetivoConteudo> objetivos = new ArrayList<ObjetivoConteudo>();
	
	private ArrayList<Conteudo> conteudos = new ArrayList<Conteudo>();
		
	private DataModel modelBlocos;
	
	private ArrayList<DataModel> modelAreas = new ArrayList<DataModel>();
	
	private DataModel modelConteudos;
	
	private ArrayList<DataModel> modelObjetivos = new ArrayList<DataModel>();
	
	private String nomeBloco;
	
	private String nomeArea;
	
	private String nomeConteudo;
	
	private String nomeObjetivo;
	
	private String rotuloBloco;
	
	private String rotuloArea;
	
	private String rotuloConteudo;
	
	private int formaAvaliacao;
	
	private int indexBloco;
	
	private int indexArea;
	
	private int indiceConteudo;
	
	private int indiceBloco;
	
	private int indexConteudoAdicionar;
	
	private int indexBlocoAdicionar;
		
	public CadastroFormularioEvolucaoMBean(){
		clear();
	}
	
	/**
	 * Limpa os atributos que serão utilizados na operação
	 */
	private void clear() {
		obj = new FormularioEvolucaoCrianca();
//		obj.setAreas(new ArrayList<Area>());
//		obj.setNivelInfantil(new ComponenteCurricular());
		setConfirmButton("Cadastrar Formulário");
		bloco = new Area();
		blocos = new ArrayList<Area>();
		conteudos = new ArrayList<Conteudo>();
		objetivos = new ArrayList<ObjetivoConteudo>();
		modelBlocos = new ListDataModel(blocos);
		modelAreas = new ArrayList<DataModel>();
		modelConteudos = new ListDataModel(conteudos);
		modelObjetivos = new ArrayList<DataModel>();
		nomeBloco = nomeArea = nomeConteudo = nomeObjetivo = "";
		rotuloBloco = rotuloArea = rotuloConteudo = "";
		formaAvaliacao = -1;
		indexBloco = 0;
		indexArea = 0;
		indiceBloco = 1;
		indiceConteudo = 1;
	}
	
	/**
	 * Verifica as permissões de acesso,
	 * popula as informações necessárias e
	 * inicia o caso de uso.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/form_cadastro_evolucao.jsp</li>
	 *	</ul>
	 * 
	 */
	public String iniciar()throws SegurancaException{
		checkRole(SigaaPapeis.GESTOR_INFANTIL);
		clear();
		return forward(JSP_FORM);
	}
	
	/**
	 * Cancela o Cadastro do Formulário de Evolução
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/form_cadastro_evolucao.jsp</li>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/view.jsp</li>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/remover.jsp</li>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/alterar.jsp</li>
	 *	</ul>
	 */
	@Override
	public String cancelar() {
		clear();
		return super.cancelar();
	}
	
	/**
	 * Invoca o processador para cadastrar o Formulário de Evolução
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/lista.jsp</li>
	 *	</ul>
	 * @throws ArqException 
	 */
	@Override
	public String cadastrar() throws ArqException{
		ListaMensagens mensagem = new ListaMensagens();
		
		for(Area bloco : blocos){
			for(Area area : bloco.getSubareas()){
				area.setFormulario(obj);
			}
			bloco.setFormulario(obj);
		}
		
//		obj.setAreas(blocos);
		obj.setDataCadastro(new Date());
		obj.setAtivo(true);
		
		validaDados(mensagem);
		
		if (!mensagem.isEmpty()) {
			addMensagens(mensagem);
			return null;
		}

		try {
			MovimentoCadastroFormularioEvolucao mov = new MovimentoCadastroFormularioEvolucao();		
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_FORMULARIO_EVOLUCAO);
			execute(mov);
			addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Formulário de Evolução");
			clear();
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
		
		prepareMovimento(SigaaListaComando.CADASTRAR_FORMULARIO_EVOLUCAO);
		
		return null;
	}
	
	/**
	 * Valida os dados do Formulário de Evolução
	 * JSP: Não invocado por JSP.
	 */
	private void validaDados(ListaMensagens mensagem){
		
//		ValidatorUtil.validateRequired(obj.getNivelInfantil(), "Selecione um Nível", mensagem);
//		ValidatorUtil.validateRequired(obj.getNivelInfantil(), "Selecione um Nível", mensagem);
//		ValidatorUtil.validateRequired(obj.getBlocos(), "Selecione um Bloco", mensagem);
		
//		for (Area bloco : obj.getBlocos()){
//			
//			if (bloco.getSubareas() == null){
//				ValidatorUtil.validateRequired(bloco.getConteudos(), "Selecione um Conteúdo", mensagem);
//				if(bloco.getConteudos() != null){
//					for (Conteudo c : (bloco.getConteudos())){
//						ValidatorUtil.validateRequired(c.getObjetivos(), "Selecione um Objetivo", mensagem);
//					}
//				}
//			}
//			else {
//				for (Area a : bloco.getSubareas()){
//					ValidatorUtil.validateRequired(a.getConteudos(), "Selecione um Conteúdo", mensagem);
//					if(a.getConteudos() != null){
//						for (Conteudo c : a.getConteudos()){
//							if (c != null)
//								ValidatorUtil.validateRequired(c.getObjetivos(), "Selecione um Objetivo", mensagem);
//						}
//					}
//				}
//			}
//			
//		}
	}
	
	/**
	 * Carrega um Formulário de Evolução para uma possível alteração ou visualização
	 * <br>
	 * JSP: Não invocado por JSP.
	 * @throws ArqException
	 */
	private void preAlterar() throws ArqException{
		int id;
		if (getParameterInt("idFormulario") == null)
			id = obj.getId();
		else
			id = getParameterInt("idFormulario");
		
		clear();
		
		obj = getGenericDAO().findByPrimaryKey(id, FormularioEvolucaoCrianca.class);
		
		FormularioEvolucaoDao formDao = getDAO(FormularioEvolucaoDao.class);
		
		//Carrega os blocos de fato
		for (Area bloco : (ArrayList<Area>) formDao.findBlocosByFormulario(id)){
			if(bloco != null && bloco.getBloco() == null){
				blocos.add(bloco);
			}
		}
		
//		obj.setAreas(blocos);
		modelBlocos = new ListDataModel(blocos);
		
		for (Area bloco : blocos){
			//Caso o bloco não tenha áreas
			if (bloco.getSubareas() == null || bloco.getSubareas().size() < 1){
				for (Conteudo c : bloco.getConteudos()){
					if (c != null){
						conteudos.add(c);
						objetivos.addAll(c.getObjetivos());
						DataModel modelObjetivo = new ListDataModel(c.getObjetivos());
						modelObjetivos.add(modelObjetivo);
					}
					else {
						bloco.getConteudos().remove(c);
					}
				}
			}
			//Caso o bloco tenha áreas
			else {
				//Lista utilizada pra remover áreas null
				ArrayList<Area> areasNull = new ArrayList<Area>();
				for (Area area : bloco.getSubareas()){
					if (area != null){
						//Lista utilizada pra remover conteúdos null
						ArrayList<Conteudo> conteudosNull = new ArrayList<Conteudo>();
						for (Conteudo c : area.getConteudos()){
							if (c != null){
								conteudos.add(c);
								objetivos.addAll(c.getObjetivos());
								DataModel modelObjetivo = new ListDataModel(c.getObjetivos());
								modelObjetivos.add(modelObjetivo);
							}
							else {
								conteudosNull.add(c);
							}
						}
						area.getConteudos().removeAll(conteudosNull);
					}
					else {
						areasNull.add(area);
					}
				}
				bloco.getSubareas().removeAll(areasNull);
				DataModel modelArea = new ListDataModel(bloco.getSubareas());
				modelAreas.add(modelArea);
			}
		}
		
		
		modelConteudos = new ListDataModel(conteudos);
		
		indiceBloco = blocos.size() + 1;
		indiceConteudo = conteudos.size() + 1;
		
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_FORMULARIO_EVOLUCAO.getId());
		prepareMovimento(SigaaListaComando.CADASTRAR_FORMULARIO_EVOLUCAO);
	}
	
	/**
	 * Carrega um Formulário de Evolução e redireciona para a tela de cadastro
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/lista.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String alterar() throws ArqException {
		preAlterar();
		return forward(JSP_FORM);
	}
	
	/**
	 * Prepara tela de remoção
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/lista.jsp</li>
	 *	</ul>
	 */
	@Override
	public String preRemover() {
		try {
			prepareMovimento(SigaaListaComando.CADASTRAR_FORMULARIO_EVOLUCAO);
			preAlterar();
		} catch (Exception e) {
			return tratamentoErroPadrao(e);
		}

		return forward(JSP_REMOVER);
	}
	
	/**
	 * Remove o Formulário de Evolução do sistema
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/remover.jsp</li>
	 *	</ul>
	 */
	@Override
	public String remover() throws ArqException {
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		
		obj.setAtivo(false);

		if (obj.getId() == 0) {
			addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
			return null;
		} else {
			mov.setCodMovimento(getUltimoComando());
			try {
				execute(mov, (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest());
				addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Formulário de Evolução");
			} catch (Exception e) {
				return tratamentoErroPadrao(e);
			}
		}
		return listar();
	}
	
	/**
	 * Visualiza dados do Formulário de Evolução
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/lista.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException 
	 */
	public String view() throws ArqException{
		preAlterar();
		return forward(JSP_VIEW);
	}

	/**
	 * Volta para a página de listagem
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/view.jsp</li>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/remover.jsp</li>
	 *	</ul>
	 *
	 * @return
	 */
	public String voltar(){
		return forward(JSP_LISTA);
	}
	
	/**
	 * Tela de listar os Formulários de Evolução
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/lista.jsp</li>
	 *	</ul>
	 */
	public String listar() throws DAOException {
		clear();
		return forward(JSP_LISTA);
	}
	
	/**
	 * Adiciona um Bloco ou Área no banco de dados
	 * JSP: Não invocado por JSP.
	 */
	private String adicionarBlocoArea() throws ArqException{
//		obj.setAreas(blocos);
		try {
			MovimentoCadastroFormularioEvolucao mov = new MovimentoCadastroFormularioEvolucao();		
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_BLOCO_AREA);
			execute(mov);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
		
		prepareMovimento(SigaaListaComando.CADASTRAR_BLOCO_AREA);
		
		return null;
	}
	
	
	/**
	 * Retorna uma coleção de itens dos Componentes Curriculares do Nível Infantil
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/form_cadastro_evolucao.jsp</li>
	 *	</ul>
	 * @throws ArqException 
	 */
	public Collection<SelectItem> getComponenteCurricular() throws ArqException{
		RegistroEvolucaoCriancaDao dao = getDAO(RegistroEvolucaoCriancaDao.class);
		return toSelectItems(dao.findComponenteCurricularNivelInfantil(), "id", "codigo");
	}
	
	/**
	 * Retorna uma coleção de itens dos Blocos
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/form_cadastro_evolucao.jsp</li>
	 *	</ul>
	 * @throws ArqException
	 */
	public Collection<SelectItem> getBlocosCombo() throws ArqException {
        return toSelectItems(blocos, "id", "descricao");
	}
	
	/**
	 * Retorna uma coleção de itens das Áreas de um determinado Bloco
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/form_cadastro_evolucao.jsp</li>
	 *	</ul>
	 * @throws ArqException
	 */
	public Collection<SelectItem> getAreasCombo() throws ArqException {
		return toSelectItems(bloco.getSubareas(), "id", "descricao");
	}

	
	/**
	 * Atribui a variável bloco o Bloco selecionado.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/form_cadastro_evolucao.jsp</li>
	 *	</ul>
	 * @throws ArqException
	 */
	public void blocoSelecionado() throws ArqException{
		bloco = getGenericDAO().findByPrimaryKey(indexBloco, Area.class);
	}

	
	/**
	 * Adiciona um Bloco ao Formulário de Evolução
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/form_cadastro_evolucao.jsp</li>
	 *	</ul>
	 * @param actevt
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public void adicionarBloco(ActionEvent actevt) throws ArqException{
		if(!nomeBloco.equals("") && nomeBloco != null){
			Area bloco = new Area();
			bloco.setDescricao(nomeBloco);
			bloco.setOrdem(indiceBloco++);
			bloco.setRotulo(rotuloBloco);
			
			//Relaciona o bloco com o Formulário a ser cadastrado
			bloco.setFormulario(null);
			
			bloco.setConteudos(new ArrayList<Conteudo>());
			
			
			//Inicializa a lista de áreas
			bloco.setSubareas(new ArrayList<Area>());
			
			//Adiciona um bloco a lista de blocos
			blocos.add(bloco);
			
			//Insere no banco
			adicionarBlocoArea();
			
			bloco = new Area();
			nomeBloco = rotuloBloco = "";
			nomeArea = rotuloArea = "";
			formaAvaliacao = -1;
		} else {
			addMessage("Descrição do bloco não pode ser vazio!", TipoMensagemUFRN.ERROR);
		}
	}
	
	/**
	 * Remove o Bloco e suas respectivas Áreas do Formulário de Evolução
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/form_cadastro_evolucao.jsp</li>
	 *	</ul>
	 * @param actevt
	 * @throws ArqException 
	 */
	public void removerBloco(ActionEvent actevt) throws ArqException{
		//índice do bloco a ser removido
		int indexBloco = modelBlocos.getRowIndex();
		
		Area bloco = blocos.get(indexBloco);
		for (Conteudo c : bloco.getConteudos()){
			conteudos.remove(c);
			modelObjetivos.remove(c);
		}		
		
		//Remove o Bloco do objeto a ser persistido
//		obj.getBlocos().remove(bloco);
		//Remove o Bloco do objeto que atualiza o modelAreas
		blocos.remove(bloco);
		//Remove o Bloco do modelAreas (Atualiza)
		modelAreas.remove(indexBloco);
	}
	
	/**
	 * Adiciona uma Área ao Formulário de Evolução
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/form_cadastro_evolucao.jsp</li>
	 *	</ul>
	 * @param actevt
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public void adicionarArea(ActionEvent actevt) throws ArqException, NegocioException{
		indexBlocoAdicionar = modelBlocos.getRowIndex();
		//índice de visualização a ser persistido no banco
		int indexTopico = blocos.get(indexBlocoAdicionar).getSubareas().size() + 1;

		if(!nomeArea.equals("") && nomeArea != null && (formaAvaliacao==0 || formaAvaliacao==1)){
			Area area = new Area();
			area.setDescricao(nomeArea);
			area.setOrdem(indexTopico++);
			area.setRotulo(rotuloArea);
			
			//Relaciona a área com o Formulário a ser cadastrado
			area.setFormulario(null);
			
			//Relaciona a área com o bloco corrente
			area.setBloco(blocos.get(indexBlocoAdicionar));
			
			//Inicializa a lista de conteúdos desta área
			area.setConteudos(new ArrayList<Conteudo>());
			
			//Adiciona esta área ao bloco corrente
			blocos.get(indexBlocoAdicionar).getSubareas().add(area);
			
			//Insere no banco
			adicionarBlocoArea();
						
			nomeBloco = rotuloBloco = "";
			nomeArea = rotuloArea = "";
			formaAvaliacao = -1;
			indexBlocoAdicionar = 0;
		}else {
			if ((formaAvaliacao<0 || formaAvaliacao>1))
				addMessage("Selecione a forma de avaliação da área!", TipoMensagemUFRN.ERROR);
			else
				addMessage("Descrição da área não pode ser vazia!", TipoMensagemUFRN.ERROR);
		}
	}
	
	/**
	 * Remove uma Sub-Área de uma Área
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/form_cadastro_evolucao.jsp</li>
	 *	</ul>
	 * @param actevt
	 * @throws ArqException 
	 */
	public void removerArea(ActionEvent actevt) throws ArqException{
		//índice do bloco a ser removido
		int indexBloco = modelBlocos.getRowIndex();
		//índice da área a ser removida
		int indexArea = modelAreas.get(indexBloco).getRowIndex();
		
		Area area = blocos.get(indexBloco).getSubareas().get(indexArea);
		if (area != null){
			for (Conteudo c : area.getConteudos()){
				if (c != null){
					conteudos.remove(c);
					modelObjetivos.remove(c);
				}
			}
			
			//Remove a Área do objeto a ser persistido
//			obj.getAreas().get(indexBloco).getSubareas().remove(area);
			//Remove a Área do objeto que atualiza o modelAreas
			blocos.get(indexBloco).getSubareas().remove(area);
			//Reindexa as áreas do objeto(blocos) que atualiza o modelAreas
			reIndexarAreas(blocos.get(indexBloco).getSubareas());
		}
		
	}
	
	/**
	 * Move a Área para cima no Formulário de Evolução
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/form_cadastro_evolucao.jsp</li>
	 *	</ul>
	 * @param atl
	 */
	public void moveAreaCima(ActionEvent atl){
		//índice do Bloco que sera movido
		int indexBloco = modelBlocos.getRowIndex();
		//índice da Área a ser movida
		int indexArea = modelAreas.get(indexBloco).getRowIndex();

		//Faz a verificação se a Área a ser movimentada está no ínicio da lista
		//Caso não esteja no ínicio, realiza a troca de posições
		if(indexArea!=0){
			Area area = blocos.get(indexBloco).getSubareas().get(indexArea);
			area.setOrdem(indexArea);

			blocos.get(indexBloco).getSubareas().remove(indexArea);
			blocos.get(indexBloco).getSubareas().add(indexArea - 1, area);
			reIndexarAreas(blocos.get(indexBloco).getSubareas());
		} else
			addMessage("A área já está no início da lista!",TipoMensagemUFRN.WARNING);
	}
	
	/**
	 * Move a Área para baixo no Formulário de Evolução
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/form_cadastro_evolucao.jsp</li>
	 *	</ul>
	 * @param actevt
	 */
	public void moveAreaBaixo(ActionEvent actevt){
		//índice do Bloco que será movido
		int indexBloco = modelBlocos.getRowIndex();
		//índice da Área a ser movida
		int indexArea = modelAreas.get(indexBloco).getRowIndex();

		//Faz a verificação se a Área a ser movimentada está no final da lista
		//Caso não esteja no final, realiza a troca de posições
		if(indexArea < blocos.get(indexBloco).getSubareas().size()){
			Area area = blocos.get(indexBloco).getSubareas().get(indexArea);
			area.setOrdem(indexArea+1);

			blocos.get(indexBloco).getSubareas().remove(indexArea);
			blocos.get(indexBloco).getSubareas().add(indexArea+1, area);
			reIndexarAreas(blocos.get(indexBloco).getSubareas());
		} else
			addMessage("A área já está no final da lista!",TipoMensagemUFRN.WARNING);
	}

	/**
	 * Adiciona um Conteúdo ao Formulário de Evolução
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/form_cadastro_evolucao.jsp</li>
	 *	</ul>
	 * @param actevt
	 * @throws DAOException 
	 */
	public void adicionarConteudo(ActionEvent actevt) throws DAOException{

		if(!nomeConteudo.equals("") && nomeConteudo != null){
			Conteudo conteudo = new Conteudo();
			conteudo.setDescricao(nomeConteudo);
			conteudo.setOrdem(indiceConteudo++);
			conteudo.setRotulo(rotuloConteudo);
			
			Area area;
			
			//Relaciona o conteúdo com a área corrente
			if (indexArea > 0)
				area = getGenericDAO().findByPrimaryKey(indexArea, Area.class);
			else
				area = getGenericDAO().findByPrimaryKey(indexBloco, Area.class);
			
			area.getConteudos().add(conteudo);
			conteudo.setArea(area);
			
			//Inicializa a lista de objetivos deste conteúdo
			conteudo.setObjetivos(new ArrayList<ObjetivoConteudo>());
			
			//Adiciona um conteúdo a lista de conteúdos
			conteudos.add(conteudo);
			
			//Informa os conteúdos da área corrente
			if (indexArea > 0)
				blocos.get(bloco.getOrdem()-1).getSubareas().get(area.getOrdem()-1).getConteudos().add(conteudo);
			else
				blocos.get(bloco.getOrdem()-1).getConteudos().add(conteudo);

			
			nomeConteudo = rotuloConteudo = "";
		} else {
			addMessage("Descrição do conteúdo não pode ser vazio!", TipoMensagemUFRN.ERROR);
		}
	}
	
	/**
	 * Remove o Conteúdo e seus respectivos Objetivos do Formulário de Evolução
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/form_cadastro_evolucao.jsp</li>
	 *	</ul>
	 * @param actevt
	 */
	public void removerConteudo(ActionEvent actevt){
		//índice do conteúdo a ser removido
		int indexConteudo = modelConteudos.getRowIndex();

		conteudos.get(indexConteudo).setArea(null);
		conteudos.remove(indexConteudo);
		modelObjetivos.remove(indexConteudo);
	}
	
	/**
	 * Adiciona um Objetivo ao Formulário de Evolução
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/form_cadastro_evolucao.jsp</li>
	 *	</ul>
	 * @param actevt
	 */
	public void adicionarObjetivo(ActionEvent actevt){
		indexConteudoAdicionar = modelConteudos.getRowIndex();
		//índice de visualização a ser persistido no banco
		int indexTopico = conteudos.get(indexConteudoAdicionar).getObjetivos().size() + 1;

		if(!nomeObjetivo.equals("") && nomeObjetivo != null){
			ObjetivoConteudo objetivo = new ObjetivoConteudo();
			objetivo.setDescricao(nomeObjetivo);
			objetivo.setOrdem(indexTopico++);
					
			//Relaciona o objetivo com o conteúdo corrente
			objetivo.setConteudo(conteudos.get(indexConteudoAdicionar));
			
			//Adiciona um objetivo a um determinado conteúdo
			conteudos.get(indexConteudoAdicionar).getObjetivos().add(objetivo);
			
			//Adiciona este objetivo a lista de objetivos
			objetivos.add(objetivo);
			
			indexConteudoAdicionar = 0;
			nomeObjetivo ="";
		}else {
			addMessage("Descrição do objetivo não pode ser vazio!", TipoMensagemUFRN.ERROR);
		}
	}
	
	/**
	 * Remove um Objetivo do Conteúdo que este está associado
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/form_cadastro_evolucao.jsp</li>
	 *	</ul>
	 * @param actevt
	 */
	public void removerObjetivo(ActionEvent actevt){
		//índice do conteúdo a ser removido
		int indexConteudo = modelConteudos.getRowIndex();
		//índice do objetivo a ser removido
		int indexObjetivo = modelObjetivos.get(indexConteudo).getRowIndex();
		
//		Conteudo c = conteudos.get(indexConteudo);
//		c.getArea().getConteudos().get(indexConteudo).getObjetivos().remove(indexObjetivo);
		conteudos.get(indexConteudo).getObjetivos().remove(indexObjetivo);
		reIndexarObjetivos(conteudos.get(indexConteudo).getObjetivos());
	}
	
	/**
	 * Move o Objetivo para cima no Formulário de Evolução
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/form_cadastro_evolucao.jsp</li>
	 *	</ul>
	 * @param atl
	 */
	public void moveObjetivoCima(ActionEvent atl){
		//índice do conteúdo que será movido
		int indexConteudo = modelConteudos.getRowIndex();
		//índice do objetivo a ser movido
		int indexObjetivo = modelObjetivos.get(indexConteudo).getRowIndex();

		//Faz a verificação se o Objetivo a ser movimentado está no ínicio da lista
		//Caso não esteja no ínicio, realiza a troca de posições
		if(indexObjetivo!=0){
			ObjetivoConteudo obc = conteudos.get(indexConteudo).getObjetivos().get(indexObjetivo);
			obc.setOrdem(indexObjetivo);

			conteudos.get(indexConteudo).getObjetivos().remove(obc);
			conteudos.get(indexConteudo).getObjetivos().add(indexObjetivo - 1, obc);
			reIndexarObjetivos(conteudos.get(indexConteudo).getObjetivos());
		} else
			addMessage("O objetivo já está no início da lista!",TipoMensagemUFRN.WARNING);
	}
	
	/**
	 * Move o Objetivo para baixo no Formulário de Evolução
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/form_cadastro_evolucao.jsp</li>
	 *	</ul>
	 * @param actevt
	 */
	public void moveObjetivoBaixo(ActionEvent actevt){
		//índice do conteúdo que será movido
		int indexConteudo = modelConteudos.getRowIndex();
		//índice do objetivo a ser movido
		int indexObjetivo = modelObjetivos.get(indexConteudo).getRowIndex();

		//Faz a verificação se o Objetivo a ser movimentado está no final da lista
		//Caso não esteja no final, realiza a troca de posições
		if(indexObjetivo < conteudos.get(indexConteudo).getObjetivos().size()){
			ObjetivoConteudo obc = conteudos.get(indexConteudo).getObjetivos().get(indexObjetivo);
			obc.setOrdem(indexObjetivo+1);

			conteudos.get(indexConteudo).getObjetivos().remove(obc);
			conteudos.get(indexConteudo).getObjetivos().add(indexObjetivo+1, obc);
			reIndexarObjetivos(conteudos.get(indexConteudo).getObjetivos());
		} else
			addMessage("O objetivo já está no final da lista!",TipoMensagemUFRN.WARNING);
	}
	
	/**
	 * Método utilizado para reorganizar os índices depois de mover um Objetivo para cima ou para baixo
	 * <br>
	 * JSP: Não invocado por JSP.
	 * @param objetivos
	 */
	private void reIndexarObjetivos(Collection <ObjetivoConteudo> objetivos){
		int i = 1;
		if(objetivos!=null){
			for (ObjetivoConteudo obc : objetivos) {
				obc.setOrdem(i++);
			}
		}
	}
	
	/**
	 * Método utilizado para reorganizar os índices depois de mover uma Área para cima ou para baixo
	 * <br>
	 * JSP: Não invocado por JSP.
	 * @param areas
	 */
	private void reIndexarAreas(Collection <Area> areas){
		int i = 1;
		if(areas!=null){
			for (Area area : areas) {
				area.setOrdem(i++);
			}
		}
	}
	
	/**
	 * Retorna uma coleção de itens dos Componentes Curriculares do Nível Infantil
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/lista.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<FormularioEvolucaoCrianca> getFormulariosEvolucaoCrianca() throws DAOException{
		FormularioEvolucaoDao dao = getDAO(FormularioEvolucaoDao.class);
		Collection<FormularioEvolucaoCrianca> form = dao.findFormulariosEvolucaoCrianca();
		if(form != null){
			return form;
		}
		else
			return null;
	}

	public Area getBloco() {
		return bloco;
	}

	public void setBloco(Area bloco) {
		this.bloco = bloco;
	}

	public ArrayList<Conteudo> getConteudos() {
		return conteudos;
	}

	public void setConteudos(ArrayList<Conteudo> conteudos) {
		this.conteudos = conteudos;
	}

	public ArrayList<ObjetivoConteudo> getObjetivos() {
		return objetivos;
	}

	public void setObjetivos(ArrayList<ObjetivoConteudo> objetivos) {
		this.objetivos = objetivos;
	}
	
	public ArrayList<Area> getBlocos() {
		return blocos;
	}

	public void setBlocos(ArrayList<Area> blocos) {
		this.blocos = blocos;
	}

	public String getNomeConteudo() {
		return nomeConteudo;
	}

	public void setNomeConteudo(String nomeConteudo) {
		this.nomeConteudo = nomeConteudo;
	}
	
	public String getNomeObjetivo() {
		return nomeObjetivo;
	}

	public void setNomeObjetivo(String nomeObjetivo) {
		this.nomeObjetivo = nomeObjetivo;
	}

	public int getFormaAvaliacao() {
		return formaAvaliacao;
	}

	public void setFormaAvaliacao(int formaAvaliacao) {
		this.formaAvaliacao = formaAvaliacao;
	}

	public int getIndexBloco() {
		return indexBloco;
	}

	public void setIndexBloco(int indexBloco) {
		this.indexBloco = indexBloco;
	}

	public int getIndexArea() {
		return indexArea;
	}

	public void setIndexArea(int indexArea) {
		this.indexArea = indexArea;
	}

	public String getNomeBloco() {
		return nomeBloco;
	}

	public void setNomeBloco(String nomeBloco) {
		this.nomeBloco = nomeBloco;
	}

	public String getNomeArea() {
		return nomeArea;
	}

	public void setNomeArea(String nomeArea) {
		this.nomeArea = nomeArea;
	}

	public String getRotuloBloco() {
		return rotuloBloco;
	}

	public void setRotuloBloco(String rotuloBloco) {
		this.rotuloBloco = rotuloBloco;
	}

	public String getRotuloArea() {
		return rotuloArea;
	}

	public void setRotuloArea(String rotuloArea) {
		this.rotuloArea = rotuloArea;
	}

	public String getRotuloConteudo() {
		return rotuloConteudo;
	}

	public void setRotuloConteudo(String rotuloConteudo) {
		this.rotuloConteudo = rotuloConteudo;
	}

	public int getIndiceBloco() {
		return indiceBloco;
	}

	public void setIndiceBloco(int indiceBloco) {
		this.indiceBloco = indiceBloco;
	}

	public int getIndiceConteudo() {
		return indiceConteudo;
	}

	public void setIndiceConteudo(int indiceConteudo) {
		this.indiceConteudo = indiceConteudo;
	}

	public int getIndexConteudoAdicionar() {
		return indexConteudoAdicionar;
	}

	public void setIndexConteudoAdicionar(int indexConteudoAdicionar) {
		this.indexConteudoAdicionar = indexConteudoAdicionar;
	}

	public int getIndexBlocoAdicionar() {
		return indexBlocoAdicionar;
	}

	public void setIndexBlocoAdicionar(int indexBlocoAdicionar) {
		this.indexBlocoAdicionar = indexBlocoAdicionar;
	}

	public DataModel getModelConteudos() {
		return modelConteudos;
	}

	public void setModelConteudos(DataModel modelConteudos) {
		this.modelConteudos = modelConteudos;
	}
	
	/**
	 * Realiza tratamento de erro diante o modelObjetivos não conter um DataModel de índice indiceConteudo,
	 * estiver vazio ou tentar acessar um índice maior que o tamanho do modelObjetivos.
	 * @return o modelObjetivo
	 */
	public DataModel getModelObjetivo() {
		int indiceConteudo = modelConteudos.getRowIndex();
		DataModel modelObjetivo;

		try{
			modelObjetivo = modelObjetivos.get(indiceConteudo);

		} catch (IndexOutOfBoundsException e) {
			Conteudo conteudo = conteudos.get(indiceConteudo);
			modelObjetivo = new ListDataModel(conteudo.getObjetivos());
			modelObjetivos.add(indiceConteudo, modelObjetivo);
		}
		return modelObjetivo;
	}

	public DataModel getModelBlocos() {
		return modelBlocos;
	}

	public void setModelBlocos(DataModel modelBlocos) {
		this.modelBlocos = modelBlocos;
	}
	
	/** 
	 * Realiza tratamento de erro diante o modelAreas não conter um DataModel de índice indiceBloco,
	 * estiver vazio ou tentar acessar um índice maior que o tamanho do modelAreas.
	 * @return o modelArea
	 */
	public DataModel getModelArea() {
		int indiceBloco = modelBlocos.getRowIndex();
		DataModel modelArea;

		try{
			modelArea = modelAreas.get(indiceBloco);

		} catch (IndexOutOfBoundsException e) {
			Area bloco = blocos.get(indiceBloco);
			modelArea = new ListDataModel(bloco.getSubareas());
			modelAreas.add(indiceBloco, modelArea);
		}
		return modelArea;
	}

}