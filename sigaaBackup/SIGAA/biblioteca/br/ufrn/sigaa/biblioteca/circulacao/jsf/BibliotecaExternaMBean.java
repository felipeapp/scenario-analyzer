package br.ufrn.sigaa.biblioteca.circulacao.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.MunicipioDao;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.pessoa.dominio.Endereco;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

/**
 * <p>Classe que gerencia o cadastro de bibliotecas ou unidades externas ao sistema de bibliotecas.</p> 
 * <p>Bibliotecas externas s�o usadas para realizar o empr�stimos institucional externo.</p>
 *
 * @author Fred_Castro
 *
 */
@Component("bibliotecaExternaMBean")
@Scope("request")
public class BibliotecaExternaMBean extends SigaaAbstractController <Biblioteca> {

	/** Guarda os munic�pios do estado selecionado pelo usu�rio. */
	private Collection<SelectItem> municipiosEndereco = new ArrayList<SelectItem>(0);
	
	/** A p�gina de cadastros */
	public static final String PAGINA_LISTA = "/biblioteca/BibliotecaExterna/lista.jsp";
	
	/** A p�gina que lista as bibliotecas existentes  */
	public static final String PAGINA_FORM = "/biblioteca/BibliotecaExterna/form.jsp";
	
	
	/**
	 * Contrutor padr�o
	 */
	public BibliotecaExternaMBean(){
		
		obj = new Biblioteca();
		
		try {
			carregarMunicipios();
		} catch(Exception e){}
	}


	
	/**
	 * Exibe a p�gina de listagem das bibliotecas externas 
	 * 
	 * Usado em /biblioteca/menus/circulacao.jsp
	 * @throws SegurancaException 
	 */
	@Override
	public String listar() throws SegurancaException{
		
		return forward(PAGINA_LISTA);
	}

	
	
	
	
	/////////////////////////  Parte de Cadastro  ///////////////////////////
	
	
	
	/**
	 * Inicia o cadastro de uma nova biblioteca
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca//BibliotecaExterna/lista.jsp</li>
	 *   </ul>
	 * 
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		
		checkListRole();
		
		obj = new Biblioteca();
		super.preCadastrar();
		carregarMunicipios();
		
		prepareMovimento (SigaaListaComando.CADASTRA_ATUALIZA_BIBLIOTECA_EXTERNA);
		
		return forward(PAGINA_FORM);
	}
	
	
	
	
	
	/**
	 * Cadastra ou atualiza uma biblioteca externa .
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca//BibliotecaExterna/form.jsp</li>
	 *   </ul>]
	 *   
	 * @throws NegocioException
	 */
	@Override
	public String cadastrar() throws ArqException {

		checkListRole();

		GenericDAO dao = null;
		
		try {

			// Verifica se o objeto n�o foi removido
			if (obj == null || ! obj.isAtivo()){
				addMensagemErro("Esta biblioteca externa foi removida.");
				return forward(PAGINA_LISTA);
			}

			boolean cadastrando = false;
			
			if(obj.getId() == 0)
				cadastrando = true;
				
			// Prepara o movimento, setando o objeto
			MovimentoCadastro mov = new MovimentoCadastro();
				
			// Seta a opera��o como cadastrar
			mov.setCodMovimento(SigaaListaComando.CADASTRA_ATUALIZA_BIBLIOTECA_EXTERNA);
			
			mov.setObjMovimentado(obj);
			
			// Tenta executar a opera��o
			execute(mov);


			// Se chegou aqui, n�o houve erros. Exibe a mensagem de sucesso.
			
			if(cadastrando)
			addMensagemInformation("Biblioteca externa cadastrada com sucesso");
			else
				addMensagemInformation("Biblioteca externa atualizada com sucesso");
			
			all = null;
			

		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
			
		} finally {
			if (dao != null) dao.close();
		}

		// Retorna para a p�gina de listagem.
		return forward(PAGINA_LISTA);
	}

	
	
	
	
	
	/////////////////////////  Parte da Atualiza��o  ///////////////////////////
	
	

	/**
	 * Carrega os dados da biblioteca externa cadastrada e vai para o formul�rio de atualiza��o.
	 * 
	 * Na atuali��o vai chamar o m�todo cadastrar tamb�m
	 * 
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca//BibliotecaExterna/lista.jsp</li>
	 *   </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String preAtualizar () throws ArqException {
		
		checkListRole();

		// Tenta pegar o objeto do banco
		populateObj(true);
		
		prepareMovimento (SigaaListaComando.CADASTRA_ATUALIZA_BIBLIOTECA_EXTERNA);

		// Se o objeto foi encontrado, exibe a tela de alterar
		if (obj != null){
			super.atualizar();
			
			if (obj.getUnidade() == null)
				obj.setUnidade( new br.ufrn.sigaa.dominio.Unidade());
			
			if (obj.getEndereco() == null)
				obj.setEndereco(new Endereco());
			
			if (obj.getEndereco().getMunicipio() == null)
				obj.getEndereco().setMunicipio(new Municipio(0));
			
			return forward(PAGINA_FORM);
		}

		// Sen�o, exibe a mensagem de erro
		obj = new Biblioteca();
		addMensagemErro("Este objeto foi removido.");
		return forward(PAGINA_LISTA);
	}
	
	
	
	
	
	
	/////////////////////////  Parte da Remo��o  ///////////////////////////
	
	/**
	 * M�todo que remove a biblioteca externa, verificando se o mesmo existe
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca//BibliotecaExterna/lista.jsp</li>
	 *   </ul>
	 * 
	 */
	@Override
	public String remover() throws ArqException {
		
		checkListRole();
		
		populateObj(true);
		prepareMovimento(SigaaListaComando.CADASTRA_ATUALIZA_BIBLIOTECA_EXTERNA);

		// Se o objeto a remover foi encontrado, remove
		if (obj != null && obj.isAtivo()){
			
			obj.setAtivo(false);
			
			if (obj.getUnidade() == null)
				obj.setUnidade( new br.ufrn.sigaa.dominio.Unidade());
			
			if (obj.getEndereco() == null)
				obj.setEndereco(new Endereco());
			
			if (obj.getEndereco().getMunicipio() == null)
				obj.getEndereco().setMunicipio(new Municipio(0));
			
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);

			mov.setCodMovimento(SigaaListaComando.CADASTRA_ATUALIZA_BIBLIOTECA_EXTERNA);
			
			try {
				execute(mov);
				addMensagemInformation("Biblioteca externa removida com sucesso.");
			} catch (NegocioException e){
				addMensagens(e.getListaMensagens());
			}
			
			all = null;
		} else {
			// Sen�o, exibe a mensagem de erro
			obj = new Biblioteca();
			addMensagemErro("Esta biblioteca externa j� foi removida.");
		}

		return forward(PAGINA_LISTA);
	}
	
	
	/** Permiss�es para executar esse caso de uso.
	 *  <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 */
	@Override
	public void checkListRole() throws SegurancaException {
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
	}

	
	
	
	/**
	 * Retorna a listagem de todas as Bibliotecas Externas ativas, ordenadas pelo nome
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca//BibliotecaExterna/lista.jsp</li>
	 *   </ul>
	 */
	@Override
	public Collection <Biblioteca> getAll () throws DAOException{
		if (all == null){
			BibliotecaDao dao = null;
			try {
				dao = getDAO(BibliotecaDao.class);
				all = dao.findAllBibliotecasExternasAtivas();
			} finally {
				if (dao != null)
					dao.close();
			}
		}
		
		return all;
	}

	
	
	/**
	 * Retorna a quantidade de prazos cadastrados para exibir na listagem
	 * Chamado na p�gina /biblioteca/BibliotecaExterna/lista.jsp
	 * @return
	 * @throws ArqException
	 */
	public int getSize () throws ArqException{
		if ( all != null )
			return getAll().size();
		else 
			return 0;
	}
	
	
	/**
	 * 
	 * M�todo chamado quando o usu�rio seleciona outra estado.  Deve carretar os munic�pios desse estado.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca//BibliotecaExterna/form.jsp</li>
	 *   </ul>
	 *
	 * @param e
	 * @throws DAOException
	 */
	public void carregarMunicipios(ValueChangeEvent e) throws DAOException {

		String selectId = e.getComponent().getId();
		if (selectId != null && e.getNewValue() != null) {
			Integer ufId = (Integer) e.getNewValue();

			if (selectId.toLowerCase().contains("ufend")) {
				carregarMunicipiosEndereco(ufId);
			}
		}
	}

	
	/**
	 * Executa a consulta dos munic�pios da UF passada.
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @param idUf
	 * @throws DAOException
	 */
	public void carregarMunicipiosEndereco(Integer idUf) throws DAOException {

		if ( idUf == null )
			idUf = obj.getEndereco().getUnidadeFederativa().getId();

		MunicipioDao dao = getDAO(MunicipioDao.class);
		UnidadeFederativa uf = dao.findByPrimaryKey(idUf, UnidadeFederativa.class);
		Collection <Municipio> municipios = dao.findByUF(idUf);
		municipiosEndereco = new ArrayList<SelectItem>(0);
		municipiosEndereco.add(new SelectItem(uf.getCapital().getId(), uf.getCapital().getNome()));
		municipiosEndereco.addAll(toSelectItems(municipios, "id", "nome"));

		obj.getEndereco().setMunicipio(uf.getCapital());
	}
	
	
	
	/**
	 * Carrega os mun�cipios padr�es antes de realizar o cadastro de uma nova biblioteca.
	 * 
	 * @throws DAOException
	 */
	private void carregarMunicipios() throws DAOException {
		MunicipioDao dao = getDAO(MunicipioDao.class);

		int uf = UnidadeFederativa.ID_UF_PADRAO;
		if (obj.getEndereco() != null && obj.getEndereco().getUnidadeFederativa() != null && obj.getEndereco().getUnidadeFederativa().getId() > 0)
			uf = obj.getEndereco().getUnidadeFederativa().getId();
		UnidadeFederativa ufEnd = dao.findByPrimaryKey(uf, UnidadeFederativa.class);

		List <Municipio> municipios = (List<Municipio>) dao.findByUF(uf);
		municipiosEndereco = new ArrayList<SelectItem>(0);
		municipiosEndereco.add(new SelectItem(ufEnd.getCapital().getId(), ufEnd.getCapital().getNome()));
		municipiosEndereco.addAll(toSelectItems(municipios, "id", "nome"));
		if (obj.getEndereco() == null)
			obj.setEndereco(new Endereco());

		obj.getEndereco().setUnidadeFederativa(ufEnd);
		obj.getEndereco().setMunicipio(ufEnd.getCapital());
	}
	
	// sets e gets //
	
	public void setMunicipiosEndereco(Collection<SelectItem> municipiosEndereco) {
		this.municipiosEndereco = municipiosEndereco;
	}

	public Collection<SelectItem> getMunicipiosEndereco() {
		return municipiosEndereco;
	}
}