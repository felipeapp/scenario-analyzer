package br.ufrn.sigaa.ensino.metropoledigital.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.metropoledigital.dao.TutorIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.TutoriaIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.TutorIMD;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.TutoriaIMD;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;


/**
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * 
 * @author Rafael Barros
 *
 */

@Scope("request")
@Component("tutorIMD")
public class TutorIMDMBean  extends SigaaAbstractController {

	/** Objeto referente a classe TutorIMD que se relaciona diretamente com o bean **/
	private TutorIMD tutor;
	
	/** Atributo que ir� armazenar qual o tipo de busca foi selecionada para vincular a pessoa ao tutor,
	 *  nome ou CPF. **/
	private String tipoBuscaPessoa;
	
	/**Cole��o de objetos da classe tutor que armazenar� o resultados das buscas.**/
	private Collection<TutorIMD> listaTutores = new ArrayList<TutorIMD>();
	
	/**Atributo respons�vel por armazenar o nome informado para busca do tutor**/
	private String nomeTutorBusca = null;
	
	/**Atributo respons�vel por armazenar o cpf informado para busca do tutor**/
	private Long cpfTutorBusca = null;
	
	/**Atributo respons�vel por armazenar o id do polo informado para busca do tutor**/
	private Integer idPoloTutorBusca = null;
	
	/**Atributo de controle respons�vel por armazenar se o campo nome foi informado ou n�o para a busca do tutor**/
	private boolean buscaNome = false;
	
	/**Atributo de controle respons�vel por armazenar se o campo cpf foi informado ou n�o para a busca do tutor**/
	private boolean buscaCpf = false;
	
	/**Atributo de controle respons�vel por armazenar se o campo polo foi informado ou n�o para a busca do tutor**/
	private boolean buscaPolo = false;
	
	/**Atributo de controle respons�vel por armazenar se o campo listar todos foi selecionado**/
	private boolean buscaListarTodos = true;
	
	/**Atributo de controle respons�vel por armazenar se o campo listar INATIVOS foi selecionado**/
	private boolean buscaInativos = false;
	
	
	/**Cole��o de itens do combo box de polo**/
	private Collection<SelectItem> polosCombo = new ArrayList<SelectItem>();
	
	public TutorIMDMBean() {
		tutor = new TutorIMD();
		tutor.setPolo(new Polo());
		tutor.setPessoa(new Pessoa());
		nomeTutorBusca = null;
		cpfTutorBusca = null;
		idPoloTutorBusca = null;
		polosCombo = Collections.emptyList();
		buscaNome = false;
		buscaCpf = false;
		buscaPolo = false;
		buscaListarTodos = true;
	}
	
	public TutorIMD getTutor() {
		return tutor;
	}

	public void setTutor(TutorIMD tutor) {
		this.tutor = tutor;
	}
	
	public String getTipoBuscaPessoa() {
		return tipoBuscaPessoa;
	}

	public void setTipoBuscaPessoa(String tipoBuscaPessoa) {
		this.tipoBuscaPessoa = tipoBuscaPessoa;
	}
	
	public Collection<TutorIMD> getListaTutores() {
		return listaTutores;
	}

	public void setListaTutores(Collection<TutorIMD> listaTutores) {
		this.listaTutores = listaTutores;
	}

	public String getNomeTutorBusca() {
		return nomeTutorBusca;
	}

	public void setNomeTutorBusca(String nomeTutorBusca) {
		this.nomeTutorBusca = nomeTutorBusca;
	}

	public Long getCpfTutorBusca() {
		return cpfTutorBusca;
	}

	public void setCpfTutorBusca(Long cpfTutorBusca) {
		this.cpfTutorBusca = cpfTutorBusca;
	}
	
	public boolean isBuscaInativos() {
		return buscaInativos;
	}

	public void setBuscaInativos(boolean buscaInativos) {
		this.buscaInativos = buscaInativos;
	}

	public Integer getIdPoloTutorBusca() {
		return idPoloTutorBusca;
	}

	public void setIdPoloTutorBusca(Integer idPoloTutorBusca) {
		this.idPoloTutorBusca = idPoloTutorBusca;
	}

	/** M�todo respons�vel por retornar o atributo polosCombo, caso o atributo esteja vazio, o m�todo efetua o preenchimento da lista
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/tutor_imd/lista.jsp</li>
	 * 	<li>/sigaa.war/metropole_digital/tutor_imd/form.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return polosCombo
	 */
	public Collection<SelectItem> getPolosCombo() throws DAOException {
		if(polosCombo.isEmpty()){
			polosCombo = toSelectItems(getGenericDAO().findAll(Polo.class), "id", "descricao");
		}
		return polosCombo;
	}

	public void setPolosCombo(Collection<SelectItem> polosCombo) {
		this.polosCombo = polosCombo;
	}
	
	public boolean isBuscaNome() {
		return buscaNome;
	}

	public void setBuscaNome(boolean buscaNome) {
		this.buscaNome = buscaNome;
	}

	public boolean isBuscaCpf() {
		return buscaCpf;
	}

	public void setBuscaCpf(boolean buscaCpf) {
		this.buscaCpf = buscaCpf;
	}

	public boolean isBuscaPolo() {
		return buscaPolo;
	}

	public void setBuscaPolo(boolean buscaPolo) {
		this.buscaPolo = buscaPolo;
	}

	public boolean isBuscaListarTodos() {
		return buscaListarTodos;
	}

	public void setBuscaListarTodos(boolean buscaListarTodos) {
		this.buscaListarTodos = buscaListarTodos;
	}	

	/**
	 * Redireciona a p�gina para a tela inicial de sele��o da pessoa que ser� cadastrada o tutor do IMD
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/tutor_imd/lista.jsp</li>
	 * 	<li>/sigaa.war/metropole_digital/menus/menu_imd.jsf</li>
	 * </ul>
	 * 
	 * @param 
	 * @return P�gina referente � opera��o.
	 */
	public String selecionarPessoaCadastro() throws SegurancaException {
		return forward("/metropole_digital/pessoa/busca_geral.jsf"); 
	}
	
	/**
	 * Redireciona a p�gina para a listagem dos tutores
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/tutor_imd/form.jsp</li>
	 *  <li>/sigaa.war/metropole_digital/menus/menu_imd.jsf</li>
	 * </ul>
	 * 
	 * @param 
	 * @return P�gina referente � opera��o.
	 */
	public String redicionarListagem() throws SegurancaException {
		return forward("/metropole_digital/tutor_imd/lista.jsf"); 
	}

	/**
	 * Busca pessoas de acordo com os crit�rios de busca informados.<br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa/geral/pessoa/busca_geral.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.jsf.AbstractControllerCadastro#buscar()
	 */
	@Override
	public String buscar() throws SegurancaException {
		
		tipoBuscaPessoa = getParameter("tipoBuscaPessoa");
		
		// Realizar a consulta
		PessoaDao dao = getDAO(PessoaDao.class);
		try {
			if(tipoBuscaPessoa == null){
				addMessage("� necess�rio selecionar um dos crit�rios de busca.",  TipoMensagemUFRN.ERROR);
			} else {
			
				// Efetuar a consulta
				if ("nome".equalsIgnoreCase(tipoBuscaPessoa)){
					if(tutor.getPessoa().getNome().length() > 0){
						setResultadosBusca(dao.findByNomeTipo(tutor.getPessoa().getNome(), Pessoa.PESSOA_FISICA, null));
						if (getResultadosBusca() == null || getResultadosBusca().isEmpty()) {
							addMensagemErro("Nenhum registro encontrado de acordo com os crit�rios de busca informados.");
						}
					}
					else {
						addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nome");
					}
				} else { 
					if ("cpf".equalsIgnoreCase(tipoBuscaPessoa)){
	
						if (tutor.getPessoa() == null || tutor.getPessoa().getCpf_cnpj() == null || tutor.getPessoa().getCpf_cnpj() == 0) {
							addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "CPF");
							setResultadosBusca(null);
						}else{
							setResultadosBusca(dao.findByCpfCnpj(tutor.getPessoa().getCpf_cnpj()));
							if (getResultadosBusca() == null || getResultadosBusca().isEmpty()) {
								addMensagemErro("Nenhum registro encontrado de acordo com os crit�rios de busca informados.");
							}
						}
					}else
						setResultadosBusca(null);
				}

			}

		} catch (DAOException e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErro("Um problema ocorreu durante a consulta. Contacte os administradores do sistema");
			return null;
		}
		return forward("/metropole_digital/pessoa/busca_geral.jsp");
	}

	/**
	 * Carrega do banco a pessoa selecionada na p�gina.
	 * 
	 * <br/>
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/geral/pessoa/busca_geral.jsp</li>
	 * </ul>
	 * 
	 * @return A p�gina que representa a opera��o a ser realizada sobre a pessoa.
	 */
	public String escolhePessoa() {
		TutorIMDDao tutorDao = new TutorIMDDao();
		tutor.setPessoa(null);
		boolean vinculoAtivo = false;
		try {
			Collection<TutorIMD> lista = new ArrayList<TutorIMD>();
			lista = tutorDao.findTutorByPessoa(getParameterInt("idPessoa"));
			if(lista.isEmpty()) {		
				tutor.setPessoa((Pessoa)getGenericDAO().findByPrimaryKey(getParameterInt("idPessoa"),
					Pessoa.class));
			} else {
				Date dataAtual = new Date();
				
				for(TutorIMD t: lista){
					if(t.getDataFim().after(dataAtual)){
						vinculoAtivo = true;
						break;
					}
				}
				if(vinculoAtivo) {
					addMensagemErro("A pessoa selecionada j� possui um v�nculo ativo de tutor do IMD.");
					return null;
				} else {
					tutor.setPessoa((Pessoa)getGenericDAO().findByPrimaryKey(getParameterInt("idPessoa"),
							Pessoa.class));
				}
			}
			
			
			if (tutor.getPessoa() == null) {
				if(! vinculoAtivo) {
					addMensagemErro("Pessoa n�o encontrada!");
					return null;
				}
			} 
			
			return forward("/metropole_digital/tutor_imd/form.jsp");
			
			
		} catch (DAOException e) {
			notifyError(e);
			addMensagemErro("Erro ao buscar as informa��es da pessoa.");
			e.printStackTrace();
			return null;
		} finally {
			tutorDao.close();
		}

		
	}
	
	/**
	 * Retorna a p�gina que representa a opera��o a ser realizada sobre uma pessoa.
	 * 
	 * <p>M�todo n�o � chamado em nenhuma JSP.</p>
	 * 
	 * @param pessoa Pessoa sobre a qual a opera��o ser� realizada.
	 * @return P�gina referente � opera��o.
	 */
	private String redirecionarPessoa(Pessoa pessoa) {
		tutor.setPessoa(pessoa);
		return forward("/metropole_digital/tutor_imd/form.jsp");
	}
	
	
	/**
	 * M�todo acionado para armazenar as informa��es de cadastro ou altera��o de tutor
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/tutor_imd/form.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return P�gina referente � opera��o.
	 * @throws SegurancaException 
	 */
	public String cadastrar() throws DAOException, SegurancaException{
		//GERA OS ERROS DE VALICA��O DO FORMUL�RIO
		erros.addAll(tutor.validate());
		
		if (!hasErrors()) {
			
			if(tutor.getDataFim().before(tutor.getDataInicio())){
				addMessage("A data fim � menor que a data in�cio.", TipoMensagemUFRN.ERROR);
				return null;
			} else if(! tutor.getDataFim().after(tutor.getDataInicio())) {
				addMessage("A data fim � igual a data in�cio.", TipoMensagemUFRN.ERROR);
				return null;
			} else {
				
				
				TutorIMDDao tutorIMDDao = getDAO(TutorIMDDao.class);
				
				
				if(tutor.getId() > 0){
					addMessage("Tutor alterado com sucesso.", TipoMensagemUFRN.INFORMATION);
					tutorIMDDao.createOrUpdate(tutor);
					tutor = null;
					//clearBusca();
					buscarTutorSemRetorno();
					return forward("/metropole_digital/tutor_imd/lista.jsp");
				} else {
					addMessage("Tutor cadastrado com sucesso.", TipoMensagemUFRN.INFORMATION);
					tutorIMDDao.createOrUpdate(tutor);
					tutor = null;
					return forward("/metropole_digital/menus/menu_imd.jsf");
				}
				
			
				
			}
		} else {
			return null;
		}
	}
	
	/**
	 * M�todo respons�vel por limpar as vari�veis da tela de busca
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/tutor_imd/form.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return 
	 * @throws
	 */
	private void clearBusca(){
		listaTutores = new ArrayList<TutorIMD>();
		buscaCpf = false;
		buscaNome = false;
		buscaPolo = false;
		buscaListarTodos = false;
		nomeTutorBusca = null;
		cpfTutorBusca = null;
		idPoloTutorBusca = null;
	}
	
	
	/**
	 * M�todo respons�vel por identificar o tutor selecionado na listagem e redirecionar a p�gina para o formul�rio de edi��o
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/tutor_imd/lista.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return P�gina referente � opera��o.
	 */
	public String atualizar() throws DAOException{
		if(getParameter("id") != null &&  getParameter("id").length() > 0) {
			int id = getParameterInt("id");
			tutor = getGenericDAO().findByPrimaryKey(id, TutorIMD.class);
			if(tutor != null){
				return forward("/metropole_digital/tutor_imd/form.jsp");
			} else {
				addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			}
		} else {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
		}
		return forward("/metropole_digital/tutor_imd/lista.jsp");
	}
	
	
	/**
	 * M�todo respons�vel pela remo��o de um registro
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/tutor_imd/lista.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return P�gina referente � opera��o.
	 */
	public String remover() throws DAOException{
		TutoriaIMDDao tutoriaDao = new TutoriaIMDDao();
		TutorIMDDao tutorIMDDao = getDAO(TutorIMDDao.class);
		try {
			tutor = new TutorIMD();
			if(getParameter("id").length() > 0) {
				int id = getParameterInt("id");		
				tutor = getGenericDAO().findByPrimaryKey(id, TutorIMD.class);
				
				if(tutor != null){
					
					List<TutoriaIMD> tutorias = new ArrayList<TutoriaIMD>();
					tutorias = tutoriaDao.findByPessoa(tutor.getPessoa().getId());
					
					if(tutorias.isEmpty()) {
					
						tutor = getGenericDAO().findByPrimaryKey(id, TutorIMD.class);
						if(tutor == null) {
							addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
						} else {
							tutorIMDDao.remove(tutor);
							addMessage("Tutor removido com sucesso.", TipoMensagemUFRN.INFORMATION);
							
							if(nomeTutorBusca != null || cpfTutorBusca != null || idPoloTutorBusca != null){
								listaTutores = (List<TutorIMD>) tutorIMDDao.findTutor(nomeTutorBusca, cpfTutorBusca, idPoloTutorBusca);
							} else {
								listaTutores = tutorIMDDao.findAll(TutorIMD.class, "pessoa.nome", "ASC");
							}	
						}
					} else {
						addMessage("O tutor n�o foi removido devido estar vinculado a uma turma.", TipoMensagemUFRN.ERROR);
						return forward("/metropole_digital/tutor_imd/lista.jsp");
					}
				} else {
					listaTutores = Collections.emptyList();
					addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
					return forward("/metropole_digital/tutor_imd/lista.jsp");
				}
				
				
			} else {
				addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			}
			return null;
		} finally {
			tutoriaDao.close();
			tutorIMDDao.close();
		}
	}
	
	
	/**
	 * Realiza pesquisa dos tutores cadastrados de acordo com as informa��es repassadas pelo usu�rio SEM RETORNO
	 *  
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/tutor_imd/lista.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return P�gina referente � opera��o.
	 */
	public void buscarTutorSemRetorno() throws DAOException{
		TutorIMDDao dao = getDAO(TutorIMDDao.class);
		try {
			if(!buscaListarTodos && !buscaNome && !buscaCpf && !buscaPolo) {
				addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
			} else {
				
				if(verificarCriterios()){
					if(buscaListarTodos || buscaInativos) {
						listaTutores = dao.findAll(TutorIMD.class, "pessoa.nome", "ASC");
					} else {
						boolean nomeInformado = false;
						boolean cpfInformado = false;
						boolean poloInformado = false;
						if(buscaNome && nomeTutorBusca != null && nomeTutorBusca.length() > 0){
							nomeInformado = true;
						}
						if(buscaCpf && cpfTutorBusca != null && cpfTutorBusca > 0){
							cpfInformado = true;
						}
						if(buscaPolo && idPoloTutorBusca != null && idPoloTutorBusca > 0){
							poloInformado = true;
						}
						
						if(nomeInformado && cpfInformado && poloInformado) {
							listaTutores = (List<TutorIMD>) dao.findTutor(nomeTutorBusca, cpfTutorBusca, idPoloTutorBusca);
						} else if(!nomeInformado && cpfInformado && poloInformado) {
							listaTutores = (List<TutorIMD>) dao.findTutor(null, cpfTutorBusca, idPoloTutorBusca);
						} else if(nomeInformado && !cpfInformado && poloInformado) {
							listaTutores = (List<TutorIMD>) dao.findTutor(nomeTutorBusca, null, idPoloTutorBusca);
						} else if(nomeInformado && cpfInformado && !poloInformado) {
							listaTutores = (List<TutorIMD>) dao.findTutor(nomeTutorBusca, cpfTutorBusca, null);
						} else if(!nomeInformado && !cpfInformado && poloInformado) {
							listaTutores = (List<TutorIMD>) dao.findTutor(null, null, idPoloTutorBusca);
						} else if(!nomeInformado && cpfInformado && !poloInformado) {
							listaTutores = (List<TutorIMD>) dao.findTutor(null, cpfTutorBusca, null);
						} else if(nomeInformado && !cpfInformado && !poloInformado) {
							listaTutores = (List<TutorIMD>) dao.findTutor(nomeTutorBusca, null, null);
						} 
	
						if(listaTutores.isEmpty()){
							addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
						}
						
						
					}
				}
				else {
					if(buscaNome){
						if((nomeTutorBusca == null || nomeTutorBusca.length() <= 0)){
							addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nome");
						}
					}
					if(buscaCpf){
						if((cpfTutorBusca == null || cpfTutorBusca <= 0)){
							addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "CPF");
						}
					}
					if(buscaPolo){
						if((idPoloTutorBusca == null || idPoloTutorBusca <= 0)){
							addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "P�lo");
						}
					}
					
				}
			}
			
			ArrayList<TutorIMD> listaTutoresAuxAtivos = new ArrayList<TutorIMD>();
			ArrayList<TutorIMD> listaTutoresAuxInativos = new ArrayList<TutorIMD>();
			Date dataAtual = new Date();
			
			
			for(TutorIMD tut: listaTutores){
				if((tut.getDataFim().after(dataAtual)) || (! tut.getDataFim().after(dataAtual) && !(tut.getDataFim().before(dataAtual)))){
					listaTutoresAuxAtivos.add(tut);
				} else {
					listaTutoresAuxInativos.add(tut);
				}
			}
			
			listaTutores = new ArrayList<TutorIMD>();
			
			if(buscaListarTodos || buscaNome || buscaCpf || buscaPolo) {
				listaTutores = listaTutoresAuxAtivos;
			} else if(buscaInativos) {			
				listaTutores = listaTutoresAuxInativos;
			} 
			
		} finally{
			dao.close();
		}
		
	}
	
	
	/**
	 * Realiza pesquisa dos tutores cadastrados de acordo com as informa��es repassadas pelo usu�rio
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/tutor_imd/lista.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return P�gina referente � opera��o.
	 */
	public String buscarTutor() throws DAOException{
		TutorIMDDao dao = getDAO(TutorIMDDao.class);
		try {
			if(!buscaListarTodos && !buscaNome && !buscaCpf && !buscaPolo && !buscaInativos) {
				addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
			} else {
				
				if(verificarCriterios()){
					
						listaTutores = dao.findAll(TutorIMD.class, "pessoa.nome", "ASC");
					
						boolean nomeInformado = false;
						boolean cpfInformado = false;
						boolean poloInformado = false;
						if(buscaNome && nomeTutorBusca != null && nomeTutorBusca.length() > 0){
							nomeInformado = true;
						}
						if(buscaCpf && cpfTutorBusca != null && cpfTutorBusca > 0){
							cpfInformado = true;
						}
						if(buscaPolo && idPoloTutorBusca != null && idPoloTutorBusca > 0){
							poloInformado = true;
						}
						
						if(nomeInformado && cpfInformado && poloInformado) {
							listaTutores = (List<TutorIMD>) dao.findTutor(nomeTutorBusca, cpfTutorBusca, idPoloTutorBusca);
						} else if(!nomeInformado && cpfInformado && poloInformado) {
							listaTutores = (List<TutorIMD>) dao.findTutor(null, cpfTutorBusca, idPoloTutorBusca);
						} else if(nomeInformado && !cpfInformado && poloInformado) {
							listaTutores = (List<TutorIMD>) dao.findTutor(nomeTutorBusca, null, idPoloTutorBusca);
						} else if(nomeInformado && cpfInformado && !poloInformado) {
							listaTutores = (List<TutorIMD>) dao.findTutor(nomeTutorBusca, cpfTutorBusca, null);
						} else if(!nomeInformado && !cpfInformado && poloInformado) {
							listaTutores = (List<TutorIMD>) dao.findTutor(null, null, idPoloTutorBusca);
						} else if(!nomeInformado && cpfInformado && !poloInformado) {
							listaTutores = (List<TutorIMD>) dao.findTutor(null, cpfTutorBusca, null);
						} else if(nomeInformado && !cpfInformado && !poloInformado) {
							listaTutores = (List<TutorIMD>) dao.findTutor(nomeTutorBusca, null, null);
						} 
	
						if(listaTutores.isEmpty()){
							addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
						}
						
						
				}
				
				else {
					if(buscaNome){
						if((nomeTutorBusca == null || nomeTutorBusca.length() <= 0)){
							addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nome");
						}
					}
					if(buscaCpf){
						if((cpfTutorBusca == null || cpfTutorBusca <= 0)){
							addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "CPF");
						}
					}
					if(buscaPolo){
						if((idPoloTutorBusca == null || idPoloTutorBusca <= 0)){
							addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "P�lo");
						}
					}
					
				}
			}
			
			ArrayList<TutorIMD> listaTutoresAuxAtivos = new ArrayList<TutorIMD>();
			ArrayList<TutorIMD> listaTutoresAuxInativos = new ArrayList<TutorIMD>();
			Date dataAtual = new Date();
			
			for(TutorIMD tut: listaTutores){
				if((tut.getDataFim().after(dataAtual)) || (! tut.getDataFim().after(dataAtual) && !(tut.getDataFim().before(dataAtual)))){
					listaTutoresAuxAtivos.add(tut);
				} else {
					listaTutoresAuxInativos.add(tut);
				}
			}
			
			
			
			if(buscaListarTodos) {
				listaTutores = new ArrayList<TutorIMD>();
				listaTutores = listaTutoresAuxAtivos;
				
			} /*else if(buscaInativos) {	
				listaTutores = new ArrayList<TutorIMD>();		
				listaTutores = listaTutoresAuxInativos;
			} */
			
			return null;
			
		} finally{
			dao.close();
		}
		
	}
	
	
	/**
	 * Realiza pesquisa dos tutores cadastrados de acordo com as informa��es repassadas pelo usu�rio
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/tutor_imd/lista.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return TRUE: se todos os crit�rios de busca marcados foram informados. FALSE: se os crit�rios de busca marcados n�o foram informados.
	 */
	public boolean verificarCriterios(){
		if(buscaNome){
			if((nomeTutorBusca == null || nomeTutorBusca.length() <= 0)){
				return false;
			}
		}
		if(buscaCpf){
			if((cpfTutorBusca == null || cpfTutorBusca <= 0)){
				return false;
			}
		}
		if(buscaPolo){
			if((idPoloTutorBusca == null || idPoloTutorBusca <= 0)){
				return false;
			}
		}
		
		return true;
	}
	
	
	/**
	 * M�todo cancelar para retornar a p�gina
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/tutor_imd/form.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return JSP na qual ser� redirecionada
	 */
	public String cancelar() {
		nomeTutorBusca = null;
		cpfTutorBusca = null;
		tutor = new TutorIMD();
		return forward("/metropole_digital/pessoa/busca_geral.jsp");
	}
	
	/**
	 * M�todo cancelar para retornar a p�gina
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/tutor_imd/form.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return JSP na qual ser� redirecionada
	 */
	public String cancelarAlteracao() {
		return forward("/metropole_digital/tutor_imd/lista.jsp");
	}
	
	
	/**
	 * M�todo cancelar para retornar a p�gina de busca de pessoa
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/pessoa/busca_geral.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return JSP na qual ser� redirecionada
	 */
	public String cancelarBuscaPessoa() {
		return forward("/metropole_digital/menus/menu_imd.jsp");
	}
	
	
}
