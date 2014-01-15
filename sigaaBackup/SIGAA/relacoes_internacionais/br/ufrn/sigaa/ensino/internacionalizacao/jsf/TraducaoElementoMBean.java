/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 18/07/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.internacionalizacao.jsf;

import java.util.Collection;

import org.hibernate.criterion.Order;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.internacionalizacao.dao.TraducaoElementoDao;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.EntidadeTraducao;

/**
 * Controller respons�vel pela internacionaliza��o de diversos elementos dos documentos.
 * 
 * @author Rafael Gomes
 *
 */
@Component
@Scope("session")
public class TraducaoElementoMBean extends AbstractTraducaoElementoMBean<Object>{

	/** Objeto utilizado para manter a entidade, cujos atributos s�o traduzidos.*/
	private EntidadeTraducao entidadeTraducao;
	/** Campo utilizado para receber da JSP a informa��o de qual Entidade ser� utilizada na opera��o.*/
	private String entidadeOperacao;
	/** Campo utilizado para listar o t�tulo da opera��o conforme o valor da Entidade utilizada na opera��o.*/
	private String tituloOperacao;
	/** Campo de busca por objetos da classe mapeada na entidade da opera��o.*/
	private String campoBusca;
	/** Informa se o campo de busca ser� considerado para montar a lista de objetos.*/
	private boolean filtroBusca;
	/** Campo utilizado manipular o campo descritivo do objeto que ser� exibido na listagem.*/
	private String atributoDescritivo;
	/** Campo utilizado manipular o valor do campo descritivo do objeto que ser� exibido na listagem.*/
	private String descricaoObjeto;
	
	
	/**
	 * Inicializa os campos do objeto para ser
	 * manipulado durante as opera��es.
	 * @throws NegocioException 
	 * @throws NegocioException 
	 */
	private void initObj() {
		obj = new Object();
		campoBusca = null;
		filtroBusca = false;
		entidadeOperacao = null;
		entidadeTraducao = new EntidadeTraducao();
		tituloOperacao = null;
		if (resultadosBusca != null)
			resultadosBusca.clear();
	}
	
	@Override
	public String getDirBase() {
		return "/relacoes_internacionais/elemento";
	}
	
	/**
	 * M�todo utilizado para a inicializa��o das opera��o 
	 * de internacionaliza��o dos elementos de documentos.
	 * <br/>M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/relacoes_internacionais/menu.jsp</li>
	 * </ul>
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public String iniciar() throws NegocioException, ArqException {
		checkChangeRole();
		initObj();
		
		TraducaoElementoDao dao = getDAO(TraducaoElementoDao.class);
		try {
			
			entidadeOperacao = getParameter("entidadeOperacao");
			entidadeTraducao = dao.findEntidadeByNomeClasse(StringUtils.capitalize(entidadeOperacao).toString());
			
			if (ValidatorUtil.isEmpty(entidadeTraducao))
				throw new NegocioException("N�o foi poss�vel obter as informa��es para tradu��o da Entidade " + entidadeOperacao + "." );
			
			tituloOperacao = entidadeTraducao.getNome();
			
			return forward(getListPage());
		}  catch (NegocioException e) {
			e.printStackTrace();
			addMensagens(e.getListaMensagens());
			return null;
		}
	}
	
	/**
	 * M�todo respons�vel pela consulta de entidades de tradu��o de acordo com o par�metro utilizado para a classe do objeto.
	 * <br/>M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/relacoes_internacionais/elemento/lista.jsp</li>
	 * </ul>/
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String buscar() throws Exception {
		TraducaoElementoDao dao = getDAO(TraducaoElementoDao.class);
		
		if (isFiltroBusca()){
			ValidatorUtil.validateRequired(campoBusca, "Nome", erros);
		}
		if (hasErrors()) return null;

		obj = ReflectionUtils.classForName(entidadeTraducao.getClasse()).newInstance();
		
		setAtributoDescritivo(ReflectionUtils.hasField(obj.getClass(), "nome") ? "nome" : 
							  ReflectionUtils.hasField(obj.getClass(), "denominacao") ? "denominacao" : 
							  ReflectionUtils.hasField(obj.getClass(), "observacao") ? "observacao" :	  
							  "descricao");
		
		setResultadosBusca((Collection<Object>) dao.findByLikeField(obj.getClass(), getAtributoDescritivo(), campoBusca));
		
		if(resultadosBusca.isEmpty())
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		
		return null;
	}
	
	/**
	 * M�todo respons�vel por instanciar o objeto selecionado na listagem 
	 * e prepar�-lo para a tradu��o de seus atributos.
	 * <br/>M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/relacoes_internacionais/elemento/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String selecionar() throws ArqException {
		TraducaoElementoDao dao = getDAO(TraducaoElementoDao.class);
		
		setOperacaoAtiva(SigaaListaComando.TRADUZIR_ELEMENTO.getId());
		prepareMovimento(SigaaListaComando.TRADUZIR_ELEMENTO);
		
		int id = getParameterInt("id", 0);
		Integer idInt = new Integer(id);
		carregarElementosTraducao(obj.getClass(), idInt, Order.asc("nome"));
		obj = dao.findByExactField(obj.getClass(), "id", id, true);
		setDescricaoObjeto(ReflectionUtils.getFieldValue(obj, getAtributoDescritivo()).toString());
		return forward(getDirBase() + "/internacionalizar.jsp");
	}
	
	/**
	 * M�todo respons�vel por verificar se o objeto possui a propriedade Ativo.
	 * 
	 * <br/>M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/relacoes_internacionais/elemento/lista.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isHasCampoAtivo(){
		return ReflectionUtils.hasField(obj.getClass(), "ativo");
	}
	
	public EntidadeTraducao getEntidadeTraducao() {
		return entidadeTraducao;
	}

	public void setEntidadeTraducao(EntidadeTraducao entidadeTraducao) {
		this.entidadeTraducao = entidadeTraducao;
	}

	public String getEntidadeOperacao() {
		return entidadeOperacao;
	}

	public void setEntidadeOperacao(String entidadeOperacao) {
		this.entidadeOperacao = entidadeOperacao;
	}

	public String getTituloOperacao() {
		return tituloOperacao;
	}

	public void setTituloOperacao(String tituloOperacao) {
		this.tituloOperacao = tituloOperacao;
	}

	public String getCampoBusca() {
		return campoBusca;
	}

	public void setCampoBusca(String campoBusca) {
		this.campoBusca = campoBusca;
	}

	public boolean isFiltroBusca() {
		return filtroBusca;
	}

	public void setFiltroBusca(boolean filtroBusca) {
		this.filtroBusca = filtroBusca;
	}

	public String getAtributoDescritivo() {
		return atributoDescritivo;
	}

	public void setAtributoDescritivo(String atributoDescritivo) {
		this.atributoDescritivo = atributoDescritivo;
	}

	public String getDescricaoObjeto() {
		return descricaoObjeto;
	}

	public void setDescricaoObjeto(String descricaoObjeto) {
		this.descricaoObjeto = descricaoObjeto;
	}

}
