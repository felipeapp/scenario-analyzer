/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p>Classe abstrata que contém os métodos comuns para alterar os parâmetros da biblioteca </p>
 * 
 * @author jadson
 *
 */
public abstract class AbstractConfiguraParametrosBiblioteca extends SigaaAbstractController <Parametro>{

	/**
	 * A página que lista os parâmetros que o usuário administrador do sistema pode alterar.
	 */
	protected String paginaListaParametrosBiblioteca = null;
	
	/** Guarda os códigos e posições dos parâmetros do sistema que o administrador geral pode alterar. */
	protected Map<String, Integer> codigosParametrosAlteraveis = new HashMap<String, Integer>();
	
	/** Lista dos parâmetros cujo preenchimento não é obrigatório. Sempre que for necessário que um parâmetro não seja obrigatório, adicioná-lo nesta lista.*/
	protected List<String> codigosParametrosAlteraveisOpcionais = new ArrayList<String>();
	
	
	/** Lista dos parâmetros cujo o tamanho dos dados digitados deve ser validado<Código Parametro, Tamanho Minimo>. 
	 * Sempre que precisar validar o tamanho de algum dado digitado pelo usuário adicionar aqui.*/
	protected Map<String, Integer> codigosParametrosValidarTamanho = new HashMap<String, Integer>();
	
	
	/** Guarda Todos os parâmetros persistidos no banco da biblioteca, para comparar com os parâmetros alterados pelo usuário e atualizar apenas os necessários */
	protected List<Parametro> parametrosPesistidos = new ArrayList<Parametro>();
	
	/** Guarda os parâmetros do sistema que o administrador geral pode alterar. */
	protected List<Parametro> parametrosAlteracao = new ArrayList<Parametro>();
	
	
	
	/**
	 * Guarda se é o administrador geral que está listado os parâmetros, se for vai habilitar a alteração.
	 */
	protected boolean administradorGeral = false;
	

	/**
	 * Método que deve ser implementado pelas classe filhas para configurar os parâmetros que vão ser alterados pelos administradores. 
	 * @see ConfiguraParametrosGeraisBibliotecaMBean
	 */
	protected abstract void configuraParametros();
	
	/**
	 * Método que deve ser implementado pelas classe filhas para indica qual página o caso de uso vai usar. Já que cada parâmetro existe uma página específica.
	 * @see ConfiguraParametrosGeraisBibliotecaMBean
	 */
	protected abstract void configuraPaginaListaParametros();
	
	
	
	
	/**
	 * 
	 * Lista todos os parametros da biblioteca que podem ser alteráveis 
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Configura os parâmetros que podem ser alterados pelos administradores no sistema.
	 */
	private void configuraParametrosAlteraveis(){
		
		parametrosAlteracao = new ArrayList<Parametro>();
		
		if(parametrosPesistidos != null && parametrosPesistidos.size() > 0){
		
			// lista com tamanho para suportar todos os parâmetros da biblioteca, mas que vai guardar apenas os alteráveis 
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
			
			// Passa apenas os parâmetros alteráveis para a listagem definitiva
			for (int index = 0; index < temp.size() ; index++) {
				if(temp.get(index) != null){
					parametrosAlteracao.add(temp.get(index));
				}
			}
			
		}
		
	}
	
	
	
	/**
	 * 
	 *  Altera apenas os parâmetros do sistema de biblioteca que tiveram o seu valor modificado pelo usuário
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
				
				// Se o valor do parâmetro foi alterado pelo usuário
				if( currentValue == null || ! currentValue.trim().equalsIgnoreCase(parametro.getValor().trim() ) ){
					
					// Atualiza o parâmetro no banco e o remove do cache em memória
					ParametroHelper.getInstance().atualizaParametro(getUsuarioLogado(), Sistema.SIGAA, parametro.getCodigo(), parametro.getValor());
					
				}				
				
			} catch (NegocioException ne) {
				ne.printStackTrace();
				addMensagens(ne.getListaMensagens());
				return null;
			}
		}
		
		addMensagemInformation("Parâmetros do sistema atualizados com sucesso !");
		return iniciar(); // atualiza os parâmetros da página com o valor que está no banco
	}
	


	/**
	 * Retorna o parâmetro persistido pelo código.
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
	 * Valida o tamanho do parâmetro digitado pelo usuários para aqueles que tem essa validação
	 * 
	 * @param parametro
	 * @throws NegocioException 
	 */
	private void validarTamanhoParametros(Parametro parametro ) throws NegocioException {
		if(codigosParametrosValidarTamanho.containsKey(parametro.getCodigo())){
			Integer tamanhoMaximaPermitido = codigosParametrosValidarTamanho.get(parametro.getCodigo());
			
			if(tamanhoMaximaPermitido != null){
				if(parametro.getValor().length() > tamanhoMaximaPermitido)
					throw new NegocioException("O tamanho máximo permitido para o parâmetro "+parametro.getNome()+" é "+tamanhoMaximaPermitido+" caractares.");
			}
			
		}
		
	}
	
	/**
	 * Retorna uma coleção de SelectItem com os tipos de arrecadação utilizados
	 * na GRU para a cobrança da taxa de inscrição.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	
	
	
	////////// telas de navegação  ///////////
	
	/**
	 * Redireciona para a página que lista os parâmetros para o usuário visualizar e alterar.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
