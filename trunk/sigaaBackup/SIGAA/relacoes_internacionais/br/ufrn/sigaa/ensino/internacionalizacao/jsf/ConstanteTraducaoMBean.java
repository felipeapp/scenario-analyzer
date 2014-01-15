/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 16/01/2013
 * Autor: Rafael Gomes
 */
package br.ufrn.sigaa.ensino.internacionalizacao.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.internacionalizacao.dao.ConstanteTraducaoDao;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.ConstanteTraducao;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.EntidadeTraducao;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.IdiomasEnum;

/**
 * MBean para a manutenção das traduções das {@link ConstanteTraducao Constantes de tradução} utilizadas pelo módulo de relações internacionais.
 * 
 * @author Rafael Gomes
 *
 */
@Component
@Scope("request")
public class ConstanteTraducaoMBean extends AbstractTraducaoElementoMBean<ConstanteTraducao> {

	/** Objeto utilizado para manter a entidade, que as constantes são traduzidas.*/
	private EntidadeTraducao entidadeTraducao;
	/** Campo de busca para localizar as constantes identificadas pela texto informado.*/
	private String constante;
	/** Informa se o filtro para entidade de tradução será considerado para montar a lista.*/
	private boolean filtroEntidadeTraducao;
	/** Informa se o filtro de constante será considerado para montar a lista.*/
	private boolean filtroConstante;
	/** Lista com as constantes para tradução. */
	private Collection<ConstanteTraducao> listaConstanteTraducao = new ArrayList<ConstanteTraducao>();
	/** Indica o comando utilizado na operação. */
	private Comando comando;
	
	/** Construtor Padrão. */
	public ConstanteTraducaoMBean() {
		initObj();
	}

	@Override
	public String getDirBase() {
		return "/relacoes_internacionais/constante_traducao";
	}

	/**
	 * Inicializa os campos do objeto para ser manipulado durante as operações.
	 * 
	 * @throws NegocioException 
	 */
	private void initObj() {
		obj = new ConstanteTraducao();
		entidadeTraducao = new EntidadeTraducao();
		constante = null;
		filtroEntidadeTraducao = false;
		filtroConstante = false;
		if (resultadosBusca != null)
			resultadosBusca.clear();
	}
	
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		checkChangeRole();
		initObj();
		comando = SigaaListaComando.CADASTRAR_CONSTANTE_TRADUCAO;
		prepareMovimento(comando);
		
		carregarConstantesTraducao(obj);
		return forward(getFormPage());
	}
	
	@Override
	public String listar() throws ArqException {
		checkChangeRole();
		return super.listar();
	}
	
	/**
	 * Método responsável pela consulta de constantes para tradução de acordo com o parâmetro utilizado para a classe do objeto.
	 * <br/>Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/relacoes_internacionais/constante_traducao/lista.jsp</li>
	 * </ul>/
	 */
	@Override
	public String buscar() throws Exception {
		ConstanteTraducaoDao dao = getDAO(ConstanteTraducaoDao.class);
		
		checkChangeRole();
		
		if (isFiltroEntidadeTraducao())
			ValidatorUtil.validateRequired(entidadeTraducao, "Entidade", erros);
		else
			entidadeTraducao = new EntidadeTraducao();
		if (isFiltroConstante())
			ValidatorUtil.validateRequired(constante, "Constante", erros);
		else 
			constante = null;
		if (hasErrors()) return null;
				
		setResultadosBusca(dao.findGeral(IdiomasEnum.INGLES.getId(), constante, entidadeTraducao));
		
		if(resultadosBusca.isEmpty())
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		
		return null;
	}

	/**
	 * Método responsável pela seleção de constantes para realizar a criação/alteração dos seus valores na base de dados.
	 * <br/>Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/relacoes_internacionais/constante_traducao/lista.jsp</li>
	 * </ul>/
	 * @return
	 * @throws ArqException 
	 */
	public String selecionar() throws ArqException{
		checkChangeRole();
		ConstanteTraducaoDao dao = getDAO(ConstanteTraducaoDao.class);
		
		comando = SigaaListaComando.ATUALIZAR_CONSTANTE_TRADUCAO;
		prepareMovimento(comando);
		
		setId();
		obj = dao.findByPrimaryKey(obj.getId(), obj.getClass());
		carregarConstantesTraducao(obj);
		return forward(getFormPage());
	}
	
	/**
	 * Método utilizado para preparar e carregar as constantes de tradução para serem traduzidos. 
	 * <br/>Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Não invocado por JSP</li>
	 * </ul>
	 * @param <T>
	 * @param classe
	 * @param idElemento
	 * @throws DAOException
	 */
	public void carregarConstantesTraducao(ConstanteTraducao constanteTraducao) throws DAOException{
		
		ConstanteTraducaoDao dao = getDAO(ConstanteTraducaoDao.class);
		if (ValidatorUtil.isNotEmpty(constanteTraducao))
			listaConstanteTraducao = dao.findGeral(null, constanteTraducao.getConstante(), constanteTraducao.getEntidade());
		Collection<String> idiomasTraduzidos = new ArrayList<String>();
					
		for (ConstanteTraducao cTraducao : listaConstanteTraducao) {
			cTraducao.setDescricaoIdioma(IdiomasEnum.getDescricaoIdiomas().get(cTraducao.getIdioma()));
			idiomasTraduzidos.add(cTraducao.getIdioma());
		}	
		for (String str : IdiomasEnum.getAll()) {
			if (idiomasTraduzidos.contains(str))
				continue;
			ConstanteTraducao ct = new ConstanteTraducao();
			ct.setIdioma(str);
			ct.setDescricaoIdioma(IdiomasEnum.getDescricaoIdiomas().get(str));
			if (ValidatorUtil.isNotEmpty(constanteTraducao)){
				ct.setConstante(constanteTraducao.getConstante());
				ct.setEntidade(constanteTraducao.getEntidade());
				ct.setClasse(constanteTraducao.getEntidade().getClasse());
			} 
			listaConstanteTraducao.add(ct);
		}
	}
	
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		checkChangeRole();
		if ("remover".equalsIgnoreCase(getConfirmButton()))
			return remover();
		
		beforeCadastrarAndValidate();
		if (hasErrors()) return null;
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(comando);
		mov.setObjAuxiliar(listaConstanteTraducao);
		try {
			execute(mov);
			addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Internacionalização");
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			removeOperacaoAtiva();
			return null;
		} 
		removeOperacaoAtiva();
		return forward(getListPage());
	}
	
	@Override
	public void beforeCadastrarAndValidate() throws NegocioException, SegurancaException, DAOException {
		ConstanteTraducaoDao dao = getDAO(ConstanteTraducaoDao.class);
		
		if (comando.equals(SigaaListaComando.CADASTRAR_CONSTANTE_TRADUCAO)){
			ValidatorUtil.validateRequiredId(entidadeTraducao.getId(), "Entidade", erros);
			ValidatorUtil.validateRequired(constante, "Constante", erros);
			if (hasErrors()) return;
			entidadeTraducao = dao.findByPrimaryKey(entidadeTraducao.getId(), EntidadeTraducao.class);
		}
			
		for (Iterator<ConstanteTraducao> iterator = listaConstanteTraducao.iterator(); iterator.hasNext();) {
			ConstanteTraducao consTraducao = iterator.next();
			if( ValidatorUtil.isEmpty(consTraducao.getValor()) && consTraducao.getId() == 0 )
				iterator.remove();
			else if (consTraducao.getEntidade() == null){
				consTraducao.setConstante(constante);
				consTraducao.setEntidade(entidadeTraducao);
				consTraducao.setClasse(entidadeTraducao.getClasse());
			}	
		}
		
	}
	
	/**
	 * Método responsável por redirecionar o usuário para a tela de lista das constantes.
	 * <br/>Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/relacoes_internacionais/constante_traducao/form.jsp</li>
	 * </ul>/
	 * @return
	 */
	public String cancelarForm() {
		if (comando.equals(SigaaListaComando.CADASTRAR_CONSTANTE_TRADUCAO))
			return cancelar();
		return forward(getListPage());
	}
	
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_SIGAA);
	}
	
	public EntidadeTraducao getEntidadeTraducao() {
		return entidadeTraducao;
	}

	public void setEntidadeTraducao(EntidadeTraducao entidadeTraducao) {
		this.entidadeTraducao = entidadeTraducao;
	}

	public String getConstante() {
		return constante;
	}

	public void setConstante(String constante) {
		this.constante = constante;
	}

	public boolean isFiltroEntidadeTraducao() {
		return filtroEntidadeTraducao;
	}

	public void setFiltroEntidadeTraducao(boolean filtroEntidadeTraducao) {
		this.filtroEntidadeTraducao = filtroEntidadeTraducao;
	}

	public boolean isFiltroConstante() {
		return filtroConstante;
	}

	public void setFiltroConstante(boolean filtroConstante) {
		this.filtroConstante = filtroConstante;
	}

	public Collection<ConstanteTraducao> getListaConstanteTraducao() {
		return listaConstanteTraducao;
	}

	public void setListaConstanteTraducao(
			Collection<ConstanteTraducao> listaConstanteTraducao) {
		this.listaConstanteTraducao = listaConstanteTraducao;
	}

}
