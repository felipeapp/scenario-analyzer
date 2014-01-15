/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 02/02/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dominio.Parametro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.parametrizacao.ParametroDao;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.gru.dominio.CodigoRecolhimentoGRU;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;

/**
 * <p>Classe abstrata que cont�m os m�todos comuns para alterar os par�metros da biblioteca </p>
 * 
 * @author jadson
 *
 */
public abstract class AbstractConfiguraParametrosBiblioteca extends SigaaAbstractController <Parametro>{

	/**
	 * A p�gina que lista os par�metros que o usu�rio administrador do sistema pode alterar.
	 */
	protected String paginaListaParametrosBiblioteca = null;
	
	/** Guarda os c�digos e posi��es dos par�metros do sistema que o administrador geral pode alterar. */
	protected Map<String, Integer> codigosParametrosAlteraveis = new HashMap<String, Integer>();
	
	/** Lista dos par�metros cujo preenchimento n�o � obrigat�rio. Sempre que for necess�rio que um par�metro n�o seja obrigat�rio, adicion�-lo nesta lista.*/
	protected List<String> codigosParametrosAlteraveisOpcionais = new ArrayList<String>();
	
	
	/** Lista dos par�metros cujo o tamanho dos dados digitados deve ser validado<C�digo Parametro, Tamanho Minimo>. 
	 * Sempre que precisar validar o tamanho de algum dado digitado pelo usu�rio adicionar aqui.*/
	protected Map<String, Integer> codigosParametrosValidarTamanho = new HashMap<String, Integer>();
	
	
	/** Guarda Todos os par�metros persistidos no banco da biblioteca, para comparar com os par�metros alterados pelo usu�rio e atualizar apenas os necess�rios */
	protected List<Parametro> parametrosPesistidos = new ArrayList<Parametro>();
	
	/** Guarda os par�metros do sistema que o administrador geral pode alterar. */
	protected List<Parametro> parametrosAlteracao = new ArrayList<Parametro>();
	
	
	
	/**
	 * Guarda se � o administrador geral que est� listado os par�metros, se for vai habilitar a altera��o.
	 */
	protected boolean administradorGeral = false;
	

	/**
	 * M�todo que deve ser implementado pelas classe filhas para configurar os par�metros que v�o ser alterados pelos administradores. 
	 * @see ConfiguraParametrosGeraisBibliotecaMBean
	 */
	protected abstract void configuraParametros();
	
	/**
	 * M�todo que deve ser implementado pelas classe filhas para indica qual p�gina o caso de uso vai usar. J� que cada par�metro existe uma p�gina espec�fica.
	 * @see ConfiguraParametrosGeraisBibliotecaMBean
	 */
	protected abstract void configuraPaginaListaParametros();
	
	
	
	
	/**
	 * 
	 * Lista todos os parametros da biblioteca que podem ser alter�veis 
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/menu/cadastros.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String iniciar(){
		
		administradorGeral = isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		configuraParametros();
		configuraPaginaListaParametros();
		
		ParametroDao pDao = null;
		
		try{
			pDao = getDAO(ParametroDao.class);
			
			parametrosPesistidos = pDao.findBySubSistema(SigaaSubsistemas.BIBLIOTECA);
			
			configuraParametrosAlteraveis();
			
		}finally{
			if(pDao != null) pDao.close();
		}
		
		return telaListaParametros();
	}
	
	
	
	/**
	 * Configura os par�metros que podem ser alterados pelos administradores no sistema.
	 */
	private void configuraParametrosAlteraveis(){
		
		parametrosAlteracao = new ArrayList<Parametro>();
		
		if(parametrosPesistidos != null && parametrosPesistidos.size() > 0){
		
			// lista com tamanho para suportar todos os par�metros da biblioteca, mas que vai guardar apenas os alter�veis 
			List<Parametro> temp = new ArrayList<Parametro>();
			
			for ( int i=0; i < parametrosPesistidos.size(); i++) {
				temp.add(null);
			}
			
			for (Parametro parametroPesistido : parametrosPesistidos) {
				if(codigosParametrosAlteraveis.keySet().contains(parametroPesistido.getCodigo())){
					
					Parametro parametroAlteracao = new Parametro();
					parametroAlteracao.setCodigo(parametroPesistido.getCodigo());
					parametroAlteracao.setNome(parametroPesistido.getNome());
					parametroAlteracao.setDescricao(parametroPesistido.getDescricao());
					parametroAlteracao.setValor(parametroPesistido.getValor());
					
					Integer posicao = codigosParametrosAlteraveis.get(parametroPesistido.getCodigo());
					
					temp.set(posicao, parametroAlteracao );
					
				}
			}
			
			// Passa apenas os par�metros alter�veis para a listagem definitiva
			for (int index = 0; index < temp.size() ; index++) {
				if(temp.get(index) != null){
					parametrosAlteracao.add(temp.get(index));
				}
			}
			
		}
		
	}
	
	
	
	/**
	 * 
	 *  Altera apenas os par�metros do sistema de biblioteca que tiveram o seu valor modificado pelo usu�rio
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/listaParemtosSistemaBiblioteca.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException 
	 * @throws  
	 */
	public String alterarParametros() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		String currentValue = null;
		
		for (Parametro parametro : parametrosAlteracao) {
			
			try {
				
				if(!codigosParametrosAlteraveisOpcionais.contains(parametro.getCodigo()) && StringUtils.isEmpty(parametro.getValor())){
					addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, parametro.getNome());
					
					return null;
				}
				
				validarTamanhoParametros(parametro);
				
				currentValue = getParamentroPersistido(parametro.getCodigo()).getValor();
				
				// Se o valor do par�metro foi alterado pelo usu�rio
				if( currentValue == null || ! currentValue.trim().equalsIgnoreCase(parametro.getValor().trim() ) ){
					
					// Atualiza o par�metro no banco e o remove do cache em mem�ria
					ParametroHelper.getInstance().atualizaParametro(getUsuarioLogado(), Sistema.SIGAA, parametro.getCodigo(), parametro.getValor());
					
				}				
				
			} catch (NegocioException ne) {
				ne.printStackTrace();
				addMensagens(ne.getListaMensagens());
				return null;
			}
		}
		
		addMensagemInformation("Par�metros do sistema atualizados com sucesso !");
		return iniciar(); // atualiza os par�metros da p�gina com o valor que est� no banco
	}
	


	/**
	 * Retorna o par�metro persistido pelo c�digo.
	 *
	 * @param codigo
	 * @return
	 */
	private Parametro getParamentroPersistido(String codigo){
		
		for (Parametro p : parametrosPesistidos) {
			if(p.getCodigo().equalsIgnoreCase(codigo)){
				return p;
			}
		}
		
		return null;
	}
	
	/**
	 * Valida o tamanho do par�metro digitado pelo usu�rios para aqueles que tem essa valida��o
	 * 
	 * @param parametro
	 * @throws NegocioException 
	 */
	private void validarTamanhoParametros(Parametro parametro ) throws NegocioException {
		if(codigosParametrosValidarTamanho.containsKey(parametro.getCodigo())){
			Integer tamanhoMaximaPermitido = codigosParametrosValidarTamanho.get(parametro.getCodigo());
			
			if(tamanhoMaximaPermitido != null){
				if(parametro.getValor().length() > tamanhoMaximaPermitido)
					throw new NegocioException("O tamanho m�ximo permitido para o par�metro "+parametro.getNome()+" � "+tamanhoMaximaPermitido+" caractares.");
			}
			
		}
		
	}
	
	/**
	 * Retorna uma cole��o de SelectItem com os tipos de arrecada��o utilizados
	 * na GRU para a cobran�a da taxa de inscri��o.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war//biblioteca/listaParemtosSistemaBiblioteca.jsp</li>
	 *  </ul> 
	 * @return
	 * @throws DAOException 
	 */
	public Collection<SelectItem> getCodigoRecolhimentoCombo() throws DAOException {
		Collection <CodigoRecolhimentoGRU> gru = getDAO(GenericDAOImpl.class, Sistema.COMUM) .findAll(CodigoRecolhimentoGRU.class, "codigo", "asc");
		return toSelectItems(gru, "id", "descricao");
	}
	
	
	
	////////// telas de navega��o  ///////////
	
	/**
	 * Redireciona para a p�gina que lista os par�metros para o usu�rio visualizar e alterar.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *      <li>/sigaa.war//biblioteca/listaParametrosGeraisSistemaBiblioteca.jsp</li>
	 *  </ul> 
	 * @return
	 * @throws DAOException 
	 */
	public String telaListaParametros(){
		return forward(paginaListaParametrosBiblioteca);
	}
	
	
	// sets e gets
	
	public boolean isAdministradorGeral() {
		return administradorGeral;
	}
	
	public boolean isNotAdministradorGeral() {
		return ! administradorGeral;
	}

	public void setAdministradorGeral(boolean administradorGeral) {
		this.administradorGeral = administradorGeral;
	}

	public List<Parametro> getParametrosAlteracao() {
		return parametrosAlteracao;
	}

	public void setParametrosAlteracao(List<Parametro> parametrosAlteracao) {
		this.parametrosAlteracao = parametrosAlteracao;
	}
	
	
}
