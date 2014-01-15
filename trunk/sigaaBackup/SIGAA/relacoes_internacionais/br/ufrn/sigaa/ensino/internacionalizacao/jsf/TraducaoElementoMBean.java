/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Controller responsável pela internacionalização de diversos elementos dos documentos.
 * 
 * @author Rafael Gomes
 *
 */
@Component
@Scope("session")
public class TraducaoElementoMBean extends AbstractTraducaoElementoMBean<Object>{

	/** Objeto utilizado para manter a entidade, cujos atributos são traduzidos.*/
	private EntidadeTraducao entidadeTraducao;
	/** Campo utilizado para receber da JSP a informação de qual Entidade será utilizada na operação.*/
	private String entidadeOperacao;
	/** Campo utilizado para listar o título da operação conforme o valor da Entidade utilizada na operação.*/
	private String tituloOperacao;
	/** Campo de busca por objetos da classe mapeada na entidade da operação.*/
	private String campoBusca;
	/** Informa se o campo de busca será considerado para montar a lista de objetos.*/
	private boolean filtroBusca;
	/** Campo utilizado manipular o campo descritivo do objeto que será exibido na listagem.*/
	private String atributoDescritivo;
	/** Campo utilizado manipular o valor do campo descritivo do objeto que será exibido na listagem.*/
	private String descricaoObjeto;
	
	
	/**
	 * Inicializa os campos do objeto para ser
	 * manipulado durante as operações.
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
	 * Método utilizado para a inicialização das operação 
	 * de internacionalização dos elementos de documentos.
	 * <br/>Método chamado pela(s) seguinte(s) JSP(s):
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
				throw new NegocioException("Não foi possível obter as informações para tradução da Entidade " + entidadeOperacao + "." );
			
			tituloOperacao = entidadeTraducao.getNome();
			
			return forward(getListPage());
		}  catch (NegocioException e) {
			e.printStackTrace();
			addMensagens(e.getListaMensagens());
			return null;
		}
	}
	
	/**
	 * Método responsável pela consulta de entidades de tradução de acordo com o parâmetro utilizado para a classe do objeto.
	 * <br/>Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Método responsável por instanciar o objeto selecionado na listagem 
	 * e prepará-lo para a tradução de seus atributos.
	 * <br/>Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Método responsável por verificar se o objeto possui a propriedade Ativo.
	 * 
	 * <br/>Método chamado pela(s) seguinte(s) JSP(s):
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
