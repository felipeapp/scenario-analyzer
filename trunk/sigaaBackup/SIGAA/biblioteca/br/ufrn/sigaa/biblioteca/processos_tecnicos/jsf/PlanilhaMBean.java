/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 18/09/2008
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.EtiquetaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Autoridade;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoControle;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoDados;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CategoriaMaterial;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Etiqueta;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FormatoMaterial;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ObjetoPlanilhaCatalogacaoControle;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ObjetoPlanilhaCatalogacaoDados;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.PlanilhaCatalogacao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.SubCampo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TipoCatalogacao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.CatalogacaoValidatorFactory;
import br.ufrn.sigaa.biblioteca.util.CatalogacaoUtil;

/**
 * <p>Managed Bean para cadastro de planilhas de cataloga��o.</p>
 *
 * <p>Planilhas de cataloga��o s�o usadas para incluir os campos mais comumente usados na cataloga��o 
 * evitando que o usu�rio tem que incluir individualmente um a um.
 * </p>
 *
 * <p> Tamb�m s�o a partir de agora (19/06/2012 #92865 	Melhorar Tela de Cataloga��o) utilizadas para 
 * montar a tela de cataloga��o simplificada do sistema. O objetivo � que a tela de cataloga��o esconda 
 * os c�digos MARC do usu�rio. Campos de controle, Indicadores, C�digos dos sub campos.
 * <br/>
 * Para isso o sistema precisava gerar essas informa��es automaticamente, ent�o nada mais l�gico que usar 
 * as informa��es da planilhas j� cadatradas.
 * <br/><br/>
 * Com isso algum doutor em campos MARC pode cadastrar v�rias planilhas no sistema uma �nica vez e o restante
 * dos bibliotec�rios n�o precisar�o mais conhecer toda essa codifica��o.
 * </p>
 *
 * @author Fred
 * @since 18/09/2008
 * @version 1.0 cria��o da classe
 * @version 1.5 19/06/2012 - Jadson - adicionando valida��es MARC no cadastro das planilhas para garantir que 
 * os campos inclu�dos com o usu da planilha esteja com os dados MARC corretos.
 */
@Component("planilhaMBean")
@Scope("request")
public class PlanilhaMBean extends SigaaAbstractController<PlanilhaCatalogacao> {

	/** Para para os usu�rios visualizarem dos dados na uma planilha */
	public static final String PAGINA_VISUALIZA_PLANILHA = "/biblioteca/PlanilhaCatalogacao/visualizaPlanilha.jsp";
	
	/** Data models para as tabelas de campos de controle . */
	private DataModel dmCamposControle = new ListDataModel();
	
	/** Data models para as tabelas de campos de dados. */
	private DataModel dmCamposDados = new ListDataModel();

	/** Strings contendo o valor digitado pelo usu�rio para a busca de tags  de um campo de controle  do MARC. */
	private String tagControle;
	
	/** Strings contendo o valor digitado pelo usu�rio para a busca de tags de um campo de dados do MARC. */
	private String tagDados;
	
	/** Chars para guardar os indicadores digitados pelo usu�rio. */
	private char indicador1 = ' ';
	/** Chars para guardar os indicadores digitados pelo usu�rio. */
	private char indicador2 = ' ';

	/** String para guardar os subcampos digitados pelo usu�rio. separados por v�rgula.*/
	private String subCampos;

	/** String para guardar os dados da etiqueta de controle digitados pelo usu�rio. */
	private String dadoControle;

	/** Guarda as informa��es dos campo de controle da planilha */
	private ObjetoPlanilhaCatalogacaoControle objControle;
	
	/** Guarda as informa��es dos campo de dados da planilha */
	private ObjetoPlanilhaCatalogacaoDados objDados;

	/**
	 * O tipo que planilha que vai ser gerenciada, se bibliogr�fica ou de autoridades.
	 */
	private short tipoPlanilha = TipoCatalogacao.BIBLIOGRAFICA;
	
	/** String utilizada para guardar a denomina��o da planilha */
	private String descricaoTipoPlanilha = "";
	
	/** Guardas as etiquetas em cache para n�o realizar buscas no banco */
	private List<Etiqueta> etiquetasBuscadas = new ArrayList<Etiqueta>();
	
	
	/**
	 * Construtor padr�o
	 * @throws ArqException
	 */
	public PlanilhaMBean(){}

	
	/**
	 * <p>Inicia o caso de uso de cadastro listando todas as planilhas catalogr�ficas do sitema.</p>
	 * 
	 * <br/><br/>
	 * Usado em sigaa.war/biblioteca/menus/processos_tecnicos.jsp
	 */
	@Override
	public String listar() throws ArqException {
		
		
		tipoPlanilha =  Short.parseShort(getParameter("tipoPlanilha"));
		
		if (tipoPlanilha == TipoCatalogacao.AUTORIDADE)
			descricaoTipoPlanilha = "de Autoridade";
		
		if (tipoPlanilha== TipoCatalogacao.BIBLIOGRAFICA)
			descricaoTipoPlanilha = "Bibliogr�ficas";
		
		inicializa();
		
		return forward(getListPage());
	}
	
	/**
	 * M�todo para visualizar os dados das planilhas cadastradas no sistema.
	 * 
	 * <br/><br/>
	 * Usado em sigaa.war//biblioteca/PlanilhaCatalogacao/lista.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String visualizar() throws ArqException {
		
		// Popula o objeto do banco a partir do id passado no request
		populateObj(true);

		preencheFormatoMaterialPlanilha();
		
		// Se o objeto foi encontrado, exibe a tela de alterar
		if (obj == null){
			addMensagemErro("A planilha selecionada n�o foi encontrada.");
		}
		return forward( PAGINA_VISUALIZA_PLANILHA );
	}
	
	
	/**
	 * Preenche o formato do material para a planilha porque essa inforam��o � trasiente. 
	 *
	 * @void
	 */
	private void preencheFormatoMaterialPlanilha() throws DAOException{
		GenericDAO dao = null;
		try{
			dao = getGenericDAO();
			obj.setFormatoMaterial( dao.findByPrimaryKey(obj.getIdFormato(), FormatoMaterial.class, "id", "sigla", "descricao") );
		}finally{
			if(dao != null) dao.close();
		}
	}
	
	
	
	
	/**
	 * M�todo que limpar o formul�rio toda vez que um novo cadastro for iniciado.
	 * @throws DAOException 
	 */
	private void inicializa(){
		// Zera o objeto
		obj = new PlanilhaCatalogacao();
		obj.setTipo(tipoPlanilha);

		// Zera os models
		dmCamposControle = new ListDataModel();
		dmCamposDados = new ListDataModel();

		// Liga os models 'as tabelas
		dmCamposControle.setWrappedData(obj.getObjetosControlePlanilhaOrdenadosByTag());
		dmCamposDados.setWrappedData(obj.getObjetosDadosPlanilhaOrdenadosByTag());

		// Limpa os campos de input
		tagControle = "";
		tagDados = "";
		dadoControle = "";
		subCampos = "";
		indicador1 = ' ';
		indicador2 = ' ';
		
		all = null;

		setConfirmButton("Cadastrar");
	}

	
	
	
	
	/**
	 * M�todo que remove a planilha, verificando se ela existe ou j� foi removida
	 * 
	 * <br/><br/>
	 * Usado em sigaa.war/biblioteca/PlanilhaCatalogacao/lista.jsp
	 * 
	 */
	@Override
	public String remover() throws ArqException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		try {
			populateObj(true);
			
			short tipo = 0;
	
			// Se o objeto a remover foi encontrado, remove
			if (obj != null){
				
				tipo = obj.getTipo();
				
				getGenericDAO().detach(obj);
				prepareMovimento(ArqListaComando.REMOVER);
				MovimentoCadastro mov = new MovimentoCadastro();
				mov.setObjMovimentado(obj);
				mov.setCodMovimento(ArqListaComando.REMOVER);
				
				execute(mov);
				
				addMensagemInformation("Planilha Removida com sucesso.");
				
				all = null;
			} else
				// Sen�o, exibe a mensagem de erro
				addMensagemErro("Este objeto j� foi removido.");
	
			// Seta o tipo do objeto para poder exibir a listagem correta.
			obj = new PlanilhaCatalogacao();
			obj.setTipo(tipo);
	
		} catch (NegocioException e){
			addMensagens(e.getListaMensagens());
		}
		
		return forward(getListPage());
	}

	
	
	
	
	/**
	 * M�todo que prepara o formul�rio para um novo cadastro.
	 * 
	 * <br/><br/>
	 * Usado em sigaa.war/biblioteca/PlanilhaCatalogacao/lista.jsp
	 * @throws ArqException 
	 * 
	 */
	@Override
	public String preCadastrar() throws ArqException{
		inicializa();
		
		preencheFormatoMaterialPlanilha();
		
		prepareMovimento(ArqListaComando.CADASTRAR);
		return forward( getFormPage());
	}

	
	
	/**
	 * M�todo que evita NullPointerException quando o usu�rio tenta
	 * alterar um objeto que j� foi removido.
	 * 
	 * <br/><br/>
	 * Usado em sigaa.war/biblioteca/PlanilhaCatalogacao/lista.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String preAtualizar () throws ArqException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		limparDadosCampos();
		
		// Popula o objeto do banco
		populateObj(true);

		prepareMovimento(ArqListaComando.ALTERAR);

		preencheFormatoMaterialPlanilha();
		
		// Se o objeto foi encontrado, exibe a tela de alterar
		if (obj != null)
			return super.atualizar();

		// Sen�o, exibe a mensagem de erro
		obj = new PlanilhaCatalogacao();
		addMensagemErro("Este objeto foi removido.");
		return forward( getListPage());
	}

	
	
	/**
	 * M�todo que cadastra ou altera um objeto
	 * 
	 * <br/><br/>
	 * Usado em sigaa.war/biblioteca/PlanilhaCatalogacao/form.jsp
	 */
	@Override
	public String cadastrar() throws ArqException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		// Verifica se o objeto n�o foi removido
		if (obj == null){
			addMensagemErro("Este objeto foi removido.");
			return forward( getListPage());
		}

		// Valida o objeto
		ListaMensagens lista = obj.validate();

		// Se ocorreram erros, exiba-os e retorne.
		if (lista != null && !lista.isEmpty()){
			addMensagens(lista);
			return forward(getFormPage());
		}

		// Prepara o movimento, setando o objeto
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);

		try {
			// Se for opera��o de cadastrar, a id do objeto sera' igual a zero
			GenericDAO dao = getGenericDAO();
			PlanilhaCatalogacao p = dao.findByExactField(PlanilhaCatalogacao.class, "nome", obj.getNome(), true);
			
			if (p != null)
				if (p.getId() != obj.getId())
					throw new NegocioException ("J� existe uma planilha com o nome especificado.");
			
			if (obj.getId() == 0){
					// Seta a opera��o como cadastrar
					mov.setCodMovimento(ArqListaComando.CADASTRAR);
					// Tenta executar a opera��o
					execute(mov);
					// Prepara o movimento para permitir a inser��o de um novo objeto
					// prepareMovimento(ArqListaComando.CADASTRAR);
			} else {
					/* N�o era opera��o de cadastrar, ent�o e' de alterar */
					// Seta a opera��o como alterar
					mov.setCodMovimento(ArqListaComando.ALTERAR);
					// Tenta executar a opera��o
					execute(mov);
			}

			// Se chegou aqui, n�o houve erros. Exibe a mensagem de sucesso.
			addMessage("Opera��o Realizada com sucesso", TipoMensagemUFRN.INFORMATION);

		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}

		// limpa a lista de planilhas para forcar o recarregamento
		all = null;

		// Retorna para a pagina de listagem.
		return forward(getListPage());
	}

	
	
	/**
	 * (public por causa da arquitetura)
	 * M�todo que liga os models �s tabelas, ap�s uma atualiza��o.
	 * 
	 * <br/><br/>
	 * N�o chamado por nenhum jsp.
	 */
	@Override
	public void afterAtualizar(){
		dmCamposControle.setWrappedData(obj.getObjetosControlePlanilhaOrdenadosByTag());
		dmCamposDados.setWrappedData(obj.getObjetosDadosPlanilhaOrdenadosByTag());
	}

	
	
	/**
	 *  M�todo que retorna os formatos de t�tulo para exibir no seletOneMenu no formul�rio
	 * 
	 * <br/><br/>
	 * Usado em sigaa.war/biblioteca/PlanilhaCatalogacao/lista.jsp
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getFormatosTituloCatalografico() throws DAOException{
		return toSelectItems(getGenericDAO().findAll(FormatoMaterial.class), "id", "descricaoCompleta") ;
	}

	
	
	
	/**
	 * M�todo que adiciona um campo de controle � planilha atual
	 * 
	 * <br/><br/>
	 * Usado em sigaa.war/biblioteca/PlanilhaCatalogacao/form.jsp
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void adicionarCampoControle(ActionEvent evt) throws DAOException {
		
		if (!StringUtils.isEmpty(tagControle) && dadoControle != null ){
			
			// busca no banco a tag que o usu�rio digitou
			//Collection <Etiqueta> etiquetas = getGenericDAO().findByExactField(Etiqueta.class, "tag", tagControle.toUpperCase());
			
			Etiqueta e = null;
			
			if(obj.getTipo() == TipoCatalogacao.BIBLIOGRAFICA)
				e = getDAO(EtiquetaDao.class).findEtiquetaPorTagETipoAtiva(tagControle.toUpperCase(), TipoCatalogacao.BIBLIOGRAFICA);
			
			if(obj.getTipo() == TipoCatalogacao.AUTORIDADE)
				e = getDAO(EtiquetaDao.class).findEtiquetaPorTagETipoAtiva(tagControle.toUpperCase(), TipoCatalogacao.AUTORIDADE);
			
			
			// se encontrou,
			if (e != null){
				
				boolean ok = true; // para verificar se o tamanho est� correto.
				
				if (! e.isEtiquetaControleComparandoPelaTag()){
					addMensagemErroAjax("O campo: '"+tagControle+"' n�o � um campo de controle.");
					return;
				}
				
				
				// Valida o tamanho dos campos
				if (tagControle.equalsIgnoreCase("LDR") && dadoControle.length() != 24){
					addMensagemErroAjax("O campo 'LDR' deve conter exatamente 24 caracteres."+" Tamanho atual do campo: "+dadoControle.length()); ok = false;
				}else if (tagControle.equalsIgnoreCase("008") && dadoControle.length() != 34){
					addMensagemErroAjax("O campo '008' deve conter 34 caracteres. Tamanho atual do campo: "+dadoControle.length() 
							+" ( 40 caracteres, excluindo os 6 primeiros que s�o preenchidos automaticamente pelo sistema) ."); ok = false;
				}else if (tagControle.equalsIgnoreCase("006") && dadoControle.length() != 18){
					addMensagemErroAjax("O campo '006' deve conter exatamente 18 caracteres."+" Tamanho atual do campo: "+dadoControle.length()); ok = false;
				}else if (tagControle.equalsIgnoreCase("007")){
					if (dadoControle.length() != 23){
						addMensagemErroAjax("O campo '007' deve conter exatamente 23 caracteres."+" Tamanho atual do campo: "+dadoControle.length()); ok = false;
					} else {
					
						char primeiro = dadoControle.charAt(0);
						
						GenericDAO dao = null;
						
						try {
							dao = getGenericDAO();
							
							List <CategoriaMaterial> cms = (List<CategoriaMaterial>) dao.findAllAtivos(CategoriaMaterial.class, "codigo");
							
							String categorias = "";
							
							boolean correto = false;
							
							for (CategoriaMaterial cm : cms){
								if (cm.getCodigo() == primeiro)
									correto = true;
								
								categorias += (categorias.equals("") ? "<br>" : ";<br>") + cm.getCodigo() + " (" + cm.getDescricao() + ")";
							}
							
							if (!correto){
								addMensagemErroAjax("O campo 007 deve come�ar com uma dessas categorias: " + categorias + ".");
								ok = false;
							}
							
						} finally {
							if (dao != null)
								dao.close();
						}
					}
				}
				
				
				if (ok){
				
					// prepara o objeto de campo de controle, passando a etiqueta
					ObjetoPlanilhaCatalogacaoControle cc = new ObjetoPlanilhaCatalogacaoControle();
					cc.setTagEtiqueta(tagControle.toUpperCase());
					
					// Se for 'LDR', preenche os dados com os valores padr�o do campo.
					if (tagControle.equalsIgnoreCase("LDR")){
						char [] aux = new char[24];
						
						char [] cl = CampoControle.DADOS_CAMPO_LIDER_BIBLIOGRAFICO.toCharArray();
						char [] dc = dadoControle.toCharArray();
						
						for (int i = 0; i < 24; i++)
							aux[i] = cl[i] != ' ' ? cl[i] : dc[i];

						dadoControle = new String (aux);
					} else if (tagControle.equalsIgnoreCase("008"))
						dadoControle = "yymmdd" + dadoControle;
					
					
					cc.setDado(dadoControle);
					
					try {
						validaCamposControlePlanilha(cc);
					}catch (NegocioException ne) {
						addMensagensAjax(ne.getListaMensagens());
						return;
					}
					
					
					// adiciona o objeto de campo de controle 'a planilha
					obj.addObjetoPlanilhaCamposControle(cc);
					
					limparDadosCampos();
					
				}
			} else
				addMensagemErroAjax("Campo de Controle n�o encontrado");
		} else
			addMensagemErroAjax("Digite a etiqueta e os dados do Campo de Controle");
	}

	
	
	
	/**
	 *  M�todo que remove um campo de controle da planilha atual
	 *  
	 *  <br/><br/>
	 * Usado em sigaa.war/biblioteca/PlanilhaCatalogacao/form.jsp
	 *
	 * @param evt
	 * @throws DAOException
	 */
	public void removerCampoControle(ActionEvent evt) throws DAOException{
		ObjetoPlanilhaCatalogacaoControle cc = (ObjetoPlanilhaCatalogacaoControle) dmCamposControle.getRowData();
		getGenericDAO().remove(cc);
		obj.getObjetosPlanilhaCatalogacaoControle().remove(cc);
	}

	
	
	
	/**
	 * M�todo que adiciona um campo de dados � planilha atual
	 * 
	 * <br/><br/> 
	 * Usado em sigaa.war/biblioteca/PlanilhaCatalogacao/form.jsp
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void adicionarCampoDados(ActionEvent evt) throws DAOException {
		
		if (!StringUtils.isEmpty(tagDados) && !StringUtils.isEmpty(subCampos)){
			
			
			
			Etiqueta e = null;
			
			if(obj.getTipo() == TipoCatalogacao.BIBLIOGRAFICA)
				e = getDAO(EtiquetaDao.class).findEtiquetaPorTagETipoAtiva(tagDados.toUpperCase(), TipoCatalogacao.BIBLIOGRAFICA);
			
			if(obj.getTipo() == TipoCatalogacao.AUTORIDADE)
				e = getDAO(EtiquetaDao.class).findEtiquetaPorTagETipoAtiva(tagDados.toUpperCase(), TipoCatalogacao.AUTORIDADE);
			
			
			if(e != null){
				
				if(e.isEtiquetaControleComparandoPelaTag()){
					addMensagemErroAjax(tagDados.toUpperCase()+" n�o � um campo de dados.");
					return;
				}
				
				ObjetoPlanilhaCatalogacaoDados cd = new ObjetoPlanilhaCatalogacaoDados();
				cd.setTagEtiqueta(tagDados.toUpperCase());
				cd.setIndicador1(indicador1);
				cd.setIndicador2(indicador2);
				cd.setSubCampos(subCampos);
				
				try {
					validaCamposDadosPlanilha(cd);
				}catch (NegocioException ne) {
					addMensagensAjax(ne.getListaMensagens());
					return;
				}
				
				
				
				obj.addObjetoPlanilhaCamposDados(cd);
				
				limparDadosCampos();
				
			}else
				addMensagemErroAjax("Campo de dados : "+tagDados.toUpperCase()+" n�o cadastrado.");

		}else{
			addMensagemErroAjax("Digite a Etiqueta e os Subcampos");
		}
	}

	
	
	
	/**
	 * <p>Valida os campos de dados da planilha.</p>
	 * 
	 * <p>Cria um T�tulo tempor�rio com os dados da planilha e usa a mesma valida��o da tela de cataloga��o.</p>
	 *
	 * @void
	 */
	private void validaCamposControlePlanilha(ObjetoPlanilhaCatalogacaoControle cc) throws NegocioException, DAOException{
		
		ListaMensagens lista = new ListaMensagens();
		
		try {
			
			// Cria um T�tulo tempor�rio para poder validar
			TituloCatalografico tituloTemp = new TituloCatalografico();
			
			PlanilhaCatalogacao planilhaTemp = new PlanilhaCatalogacao();
			planilhaTemp.setTipo(obj.getTipo());
			
			planilhaTemp.addObjetoPlanilhaCamposControle(cc);
			
			CatalogacaoUtil.criaCamposDeControle(planilhaTemp, tituloTemp, etiquetasBuscadas); // cria o campo de controel no T�tulo para valida��o
			
			// seta o tipo correto para validar ... //
			for(CampoControle controle:  tituloTemp.getCamposControle()){
				if(controle.getEtiqueta() != null){
					controle.getEtiqueta().setTipoCampo(CampoControle.IDENTIFICADOR_CAMPO);
				}
			}
			
			if(obj.getTipo() == TipoCatalogacao.BIBLIOGRAFICA)
				CatalogacaoValidatorFactory.getRegrasValidacao().validaCamposMarcTitulo(tituloTemp, obj.getFormatoMaterial(), lista);
			if(obj.getTipo() == TipoCatalogacao.AUTORIDADE){
				Autoridade autoridade = CatalogacaoUtil.criaAutoridaeAPartirTitulo(tituloTemp);
				CatalogacaoValidatorFactory.getRegrasValidacao().validaCamposMarcAutoridade(autoridade, lista);
			}
			
		} catch (NegocioException e1) {
			addMensagens(e1.getListaMensagens());
			return;
		}
		
		if(lista.size() > 0)
			throw new NegocioException(lista);
	}
	
	/**
	 * <p>Valida os campos de dados da planilha</p>
	 * 
	 * <p>Cria um T�tulo tempor�rio com os dados da planilha e usa a mesma valida��o da tela de cataloga��o.</p>
	 *
	 * @void
	 */
	private void validaCamposDadosPlanilha(ObjetoPlanilhaCatalogacaoDados cd) throws NegocioException, DAOException{
		
		ListaMensagens lista = new ListaMensagens();
		
		try {
			// Cria um T�tulo tempor�rio para poder validar
			TituloCatalografico tituloTemp = new TituloCatalografico();
			
			PlanilhaCatalogacao planilhaTemp = new PlanilhaCatalogacao();
			planilhaTemp.setTipo(obj.getTipo());
			planilhaTemp.addObjetoPlanilhaCamposDados(cd);                // coloca apenas o campo que est� sendo adicionado 
			
			
			CatalogacaoUtil.criaCamposDeDados(planilhaTemp, tituloTemp, etiquetasBuscadas);
			
			for(CampoDados dado:  tituloTemp.getCamposDados()){
				for(SubCampo sub :  dado.getSubCampos()){
					sub.setDado("dados para valida��o"); // seta dados pq o sistema ignora os campos vazios
				}
			}
			
			if(obj.getTipo() == TipoCatalogacao.BIBLIOGRAFICA)
				CatalogacaoValidatorFactory.getRegrasValidacao().validaCamposMarcTitulo(tituloTemp, obj.getFormatoMaterial(), lista);
			if(obj.getTipo() == TipoCatalogacao.AUTORIDADE){
				Autoridade autoridade = CatalogacaoUtil.criaAutoridaeAPartirTitulo(tituloTemp);
				CatalogacaoValidatorFactory.getRegrasValidacao().validaCamposMarcAutoridade(autoridade, lista);
			}
			
		} catch (NegocioException e1) {
			addMensagens(e1.getListaMensagens());
			return;
		}
		
		if(lista.size() > 0)
			throw new NegocioException(lista);
		
	}


	
	
	
	
	/**
	 * M�todo que remove um campo de dados da planilha atual
	 * 
	 *  <br/><br/>
	 * Usado em sigaa.war/biblioteca/PlanilhaCatalogacao/lista.jsp
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void removerCampoDados(ActionEvent evt) throws DAOException {
		// Pega o objeto selecionado para remover
		ObjetoPlanilhaCatalogacaoDados cd = (ObjetoPlanilhaCatalogacaoDados) dmCamposDados.getRowData();
		// Remove
		getGenericDAO().remove(cd);
		obj.getObjetosPlanilhaCatalogacaoDados().remove(cd);
	}

	
	
	
	/**
	 * Apaga os dados dos campos da planilha digitado pelo usu�rio  
	 */
	private void limparDadosCampos(){
		tagControle = "";
		tagDados = "";
		
		dadoControle = "";
		indicador1 = ' ';
		indicador2 = ' ';
		subCampos = "";
		
	}
	
	
	
	
	/**
	 * 
	 * Volta para a p�gina que lista as planilhas
	 *
	 *  <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/PlanilhaCatalogacao/form.jsp
	 *
	 * @return
	 */
	public String voltar(){
		all = null;  // para buscar novamente.
		
		return forward(getListPage());
	}
	
	
	
	/**
	 * Seta o valor do indicador 1, verificando se o usu�rio deixou em branco
	 * 
	 * <br/><br/>
	 * Usado em sigaa.war/biblioteca/PlanilhaCatalogacao/form.jsp
	 * 
	 * @param indicador1
	 */
	public void setIndicador1(String indicador1) {
		if (indicador1 == null)
			this.indicador1 = ' ';
		else if (indicador1.length() > 0)
			this.indicador1 = indicador1.charAt(0);
		else
			this.indicador1 = ' ';
	}

	/**
	 * Seta o valor do indicador 2, verificando se o usu�rio deixou em branco
	 * 
	 *  <br/><br/>
	 * Usado em sigaa.war/biblioteca/PlanilhaCatalogacao/form.jsp
	 * @param indicador2
	 */
	public void setIndicador2(String indicador2) {
		if (indicador2 == null)
			this.indicador2 = ' ';
		else if (indicador2.length() > 0)
			this.indicador2 = indicador2.charAt(0);
		else
			this.indicador2 = ' ';
	}

	/**
	 *  <br/><br/>
	 * Usado em sigaa.war/biblioteca/PlanilhaCatalogacao/form.jsp
	 * @return
	 */
	public String getIndicador2() {
		return "";
	}

	/**
	 * Transforma a string digitada para o formato requerido. Ex: aaa -> $a$a$a
	 * 
	 * <br/><br/>
	 * Usado em sigaa.war/biblioteca/PlanilhaCatalogacao/form.jsp
	 * 
	 * @param subCampos
	 */
	public void setSubCampos(String subCampos) {
		// Remove caracteres que n�o sejam letras ou n�meros
		subCampos = subCampos.replaceAll("[^a-zA-Z0-9]", "");

		char [] array = subCampos.toCharArray();
		String auxSC = "";
		// Para cada caractere na string, adiciona um '$' antes dele
		for (char sc : array)
			auxSC += "$" + sc;

		this.subCampos = auxSC;
	}

	
	/**
	 * <p>M�todo que retorna todas as planilhas existentes.</p>
	 * 
	 * <p><strong>Caso uma seja removida ela � apagada do banco, n�o desativada como a maioria dos casos.</strong></p>
	 * 
	 *  <br/><br/>
	 * Usado em sigaa.war/biblioteca/PlanilhaCatalogacao/lista.jsp
	 */
	@Override
	public Collection <PlanilhaCatalogacao> getAll () throws DAOException{
		GenericDAO dao = null;

		if (all == null){
			try {
				dao = getGenericDAO();
				all = dao.findByExactField(PlanilhaCatalogacao.class, "tipo", obj.getTipo(), "asc", "nome");
			} finally {
				if (dao != null) dao.close();
			}
		}
		
		return all;
	}
	
	
	/**
	 * M�todo que retorna a contagem das planilhas
	 * 
	 *  <br/><br/>
	 * Usado em sigaa.war/biblioteca/PlanilhaCatalogacao/lista.jsp
	 * @return
	 * @throws DAOException
	 */
	public int getSize () throws DAOException{
		return getAll().size();
	}

	

	// Sets e gets

	public ObjetoPlanilhaCatalogacaoControle getObjControle() {
		return objControle;
	}

	public String getTagControle() {
		return tagControle;
	}

	public void setTagControle(String tagControle) {
		this.tagControle = tagControle;
	}

	public String getTagDados() {
		return tagDados;
	}

	public void setTagDados(String tagDados) {
		this.tagDados = tagDados;
	}

	public String getIndicador1() {
		return "";
	}

	public String getSubCampos() {
		return subCampos;
	}

	public void setObjControle(ObjetoPlanilhaCatalogacaoControle objControle) {
		this.objControle = objControle;
	}

	public ObjetoPlanilhaCatalogacaoDados getObjDados() {
		return objDados;
	}

	public void setObjDados(ObjetoPlanilhaCatalogacaoDados objDados) {
		this.objDados = objDados;
	}

	public String getDadoControle() {
		return dadoControle;
	}

	public void setDadoControle(String dadoControle) {
		this.dadoControle = dadoControle;
	}

	public DataModel getDmCamposControle() {
		return dmCamposControle;
	}

	public void setDmCamposControle(DataModel dmCamposControle) {
		this.dmCamposControle = dmCamposControle;
	}

	public DataModel getDmCamposDados() {
		return dmCamposDados;
	}

	public void setDmCamposDados(DataModel dmCamposDados) {
		this.dmCamposDados = dmCamposDados;
	}

	public void setIndicador1(char indicador1) {
		this.indicador1 = indicador1;
	}

	public void setIndicador2(char indicador2) {
		this.indicador2 = indicador2;
	}

	public char getTipoC() {
		return 'C';
	}

	public char getTipoD() {
		return 'D';
	}
	
	public short getBibliografica (){
		return TipoCatalogacao.BIBLIOGRAFICA;
	}
	
	public short getAutoridade (){
		return TipoCatalogacao.AUTORIDADE;
	}

	public String getDescricaoTipoPlanilha() {
		return descricaoTipoPlanilha;
	}
	public void setDescricaoTipoPlanilha(String descricaoTipoPlanilha) {
		this.descricaoTipoPlanilha = descricaoTipoPlanilha;
	}
	
}