/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 15/03/2012
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.InventarioAcervoBibliotecaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.InventarioAcervoBiblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.InventarioMaterialRegistrado;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ResultadoRegistraMateriaisInventario;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoRegistraMateriaisInventario;

import com.sun.star.auth.InvalidArgumentException;

/**
 * MBean para realizar a operação de registro de materiais no inventário do acervo de uma biblioteca do sistema.
 * 
 * APenas para testes, esse MBean não deve ir para produção
 * 
 * @author Felipe
 *
 */
@Scope(value="request")
@Component(value="registraMateriaisInventarioMBean")
public class RegistraMateriaisInventarioMBean extends SigaaAbstractController<InventarioMaterialRegistrado> {
	
	/** Tela de registro de materiais no inventário */
	private static final String PAGINA_FORM = "/biblioteca/processos_tecnicos/inventario_acervo/form.jsp";
	/** Página para sair do caso-de-uso */
	private static final String PAGINA_INDEX = "/biblioteca/index.jsp";

	/** A biblioteca selecionada no cadastro de inventários */
	private Integer biblioteca;
	/** Lista dos inventários da biblioteca selecionada */
	private List<InventarioAcervoBiblioteca> inventarios;
	
	/** O código de barras digitado pelo usuário */
	private String codigoBarras;
	/** Guarda os códigos de barra adicionados pelo usuário */
	private List <String> codigosBarras;
	/** Armazena o inventário selecionado pelo usuário */
	private Integer inventario;
	/** Os materiais cujas consulta vai ser registrada */
	private List <MaterialInformacional> materiais; 

	/** Guarda a lista de bibliotecas que vão ser exibidas no combobox */
	private Collection<Biblioteca> bibliotecasCombo;
	
	public RegistraMateriaisInventarioMBean() {
		
	}
	
	/**
	 * Inicia o fluxo de navegação da página de listagem de inventários 
	 * 
	 * Utilizado na JSP: /sigaa.war/biblioteca/menus/processos_tecnicos.jsp 
	 * @throws ArqException 
	 */
	public String iniciar() throws ArqException {		
		prepareMovimento(SigaaListaComando.REGISTRAR_MATERIAIS_INVENTARIO);
		
		codigoBarras = "";
		codigosBarras = new ArrayList<String>();
		materiais = new ArrayList<MaterialInformacional>();
		inventarios = new ArrayList<InventarioAcervoBiblioteca>();
		
		return telaFormulario();
	}
	
	/**
	 * Adiciona o código de barras digitado pelo usuário à lista de materiais consultados.
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/inventario_acervo/form.jsp
	 */
	public void adicionarMaterial (ActionEvent e) throws DAOException {
		
		if (StringUtils.isEmpty(codigoBarras)) {
			addMensagemErroAjax("Informe um código de barras válido para um material informacional");
		} else { 
			MaterialInformacionalDao dao = null;
			
			try {
				dao = getDAO(MaterialInformacionalDao.class);
				
				if (!codigosBarras.contains(codigoBarras)) {				
					MaterialInformacional m = dao.findMaterialAtivoByCodigoBarras(codigoBarras);
					
					if(m != null ){   // Material Encontrado					
						if(! isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){					
							try{
								checkRole(m.getBiblioteca().getUnidade() 
										, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO
										, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF
										, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO
										, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO);
							}catch (SegurancaException se) {
								addMensagemErroAjax("O material não pertence a sua biblioteca. ");
								return;
							}
						}
						
						codigosBarras.add(codigoBarras);
						materiais.add(m);
					} else {
						addMensagemErroAjax("O material de código de barras \""+codigoBarras+"\" não foi encontrado ");
					}
				} else {
					addMensagemErroAjax("O material de código de barras \""+codigoBarras+"\" já foi adicionado à lista.");
				}
			} finally {
				if (dao != null) dao.close();
			}
		}
		
	}

	/**
	 * Prepara a lista de Materiais Catalográficos cuja consulta será registrada e chama o processador que realizará o registro.
	 * <br/>
	 * Chamado a partir da página:  /sigaa.war/biblioteca/processos_tecnicos/inventario_acervo/form.jsp
	 * 
	 */
	public String registrarMateriais () throws HibernateException, ArqException {
		
		try {			
		
			prepareMovimento(SigaaListaComando.REGISTRAR_MATERIAIS_INVENTARIO);
			
			validarCampos();
			
			MovimentoRegistraMateriaisInventario mov = new MovimentoRegistraMateriaisInventario(codigosBarras, new InventarioAcervoBiblioteca(inventario));
			mov.setCodMovimento(SigaaListaComando.REGISTRAR_MATERIAIS_INVENTARIO);
			
			ResultadoRegistraMateriaisInventario resultado = (ResultadoRegistraMateriaisInventario) execute(mov); 
			
			addMensagens(resultado.getMensagens());
			
			return null;		
		} catch (NegocioException ne){
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
	}

	/**
	 * Retorna os inventários a partir de uma biblioteca selecionada.
	 * <br/>
	 * Chamado a partir da página:  /sigaa.war/biblioteca/processos_tecnicos/inventario_acervo/form.jsp
	 * 
	 * @param evt
	 * @return
	 * @throws DAOException
	 */
	public String buscarInventarios(ValueChangeEvent evt) throws DAOException {
		biblioteca = (Integer) evt.getNewValue();

		inventarios.clear();
		
		if (biblioteca == -1) {
			//addMensagemErro("Selecione uma biblioteca.");
			
			return null;
		}
		
		InventarioAcervoBibliotecaDao dao = null;
		
		try {
			dao = getDAO(InventarioAcervoBibliotecaDao.class);
			
			InventarioAcervoBiblioteca inventarioBiblioteca = dao.findAbertoByBiblioteca(biblioteca);
			
			if (inventarioBiblioteca != null) {
				inventarios.add(inventarioBiblioteca);
			} else {
				addMensagemErro("Esta biblioteca não possui inventários.");
			}
			
			return null;
		} finally {
			if(dao != null) dao.close();
		}
	}

	/**
	 * Valida os campos preenchidos pelo usuário 
	 * 
	 * Não é utilizado em JSP 
	 * @throws InvalidArgumentException 
	 * @throws NegocioException 
	 */
	private void validarCampos() throws NegocioException {
		ListaMensagens erros = new ListaMensagens();
		
		if (biblioteca == -1) {
			erros.addErro("Selecione uma biblioteca.");
		}

		if (inventario == -1) {
			erros.addErro("Selecione uma inventário.");
		}
		
		if (materiais == null || materiais.isEmpty()) {
			erros.addErro("Não há materiais na lista para registrar consulta.");
		}
		
		if (erros.size() > 0) {			
			throw new NegocioException(erros);
		}
	}

	/**
	 * Limpa os campos da tela 
	 * 
	 * Utilizado na JSP: /sigaa.war/biblioteca/InventarioAcervo/lista.jsp 
	 */
	public void limparCamposBusca() {
		biblioteca = null;
		inventarios = null;
	}

	/**
	 * Redireciona o fluxo de navegação para a tela de listagem de inventários 
	 * 
	 * Não utilizado em JSP 
	 */
	private String telaFormulario() {
		return forward(PAGINA_FORM);
	}

	/**
	 * Volta para a tela de listagem de inventários 
	 * 
	 * Utilizado na JSP: /sigaa.war/biblioteca/InventarioAcervo/lista.jsp 
	 */
	public String voltarBusca() {
		return forward(PAGINA_INDEX);
	}
	
	/**
	 *  Retorna todas as bibliotecas internas ativas do sistema. São as únicas para as quais
	 *  pode-se criar inventários.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/inventario_acervo/form.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getBibliotecasInternas() throws DAOException{
		
		if (bibliotecasCombo == null){
			BibliotecaDao dao = null;
			try{
				dao = getDAO(BibliotecaDao.class);
				bibliotecasCombo = dao.findAllBibliotecasInternasAtivas();
			}finally{
			  if (dao != null)  dao.close();
			}
		}
		
		return toSelectItems(bibliotecasCombo, "id", "descricaoCompleta");
	}

	/**
	 *  Retorna o inventário aberto (caso exista) da biblioteca selecionada.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/inventario_acervo/form.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getInventarios() throws DAOException{
		return toSelectItems(inventarios, "id", "descricaoCompleta");
	}
	
	// Gets e sets

	public String getCodigoBarras() {
		return codigoBarras;
	}

	public void setCodigoBarras(String codigoBarras) {
		this.codigoBarras = codigoBarras;
	}
	
	public List <String> getCodigosBarras() {
		return codigosBarras;
	}

	public void setCodigosBarras(List<String> codigosBarras) {
		this.codigosBarras = codigosBarras;
	}

	public Integer getInventario() {
		return inventario;
	}

	public void setInventario(Integer inventario) {
		this.inventario = inventario;
	}

	public void setInventarios(List<InventarioAcervoBiblioteca> inventarios) {
		this.inventarios = inventarios;
	}

	public Integer getBiblioteca() {
		return biblioteca;
	}

	public void setBiblioteca(Integer biblioteca) {
		this.biblioteca = biblioteca;
	}
}
