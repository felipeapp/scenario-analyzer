/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 15/08/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.internacionalizacao.jsf;

import org.hibernate.criterion.Order;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.internacionalizacao.dao.TraducaoElementoDao;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.EntidadeTraducao;

/**
 * Controller responsável pela internacionalização dos elementos relacionados ao aluno.
 * (Mobilidade Estudantil, Observações)
 * 
 * @author Rafael Gomes
 *
 */
@Scope("session")
@Component
public class ElementosDiscenteTraducaoMBean extends AbstractTraducaoElementoMBean<DiscenteAdapter> implements OperadorDiscente{

	/** Objeto utilizado para manter a entidade, cujos atributos são traduzidos.*/
	private EntidadeTraducao entidadeTraducao;
	/** Campo utilizado para receber da JSP a informação de qual Entidade será utilizada na operação.*/
	private String entidadeOperacao;
	/** Campo utilizado para listar o título da operação conforme o valor da Entidade utilizada na operação.*/
	private String tituloOperacao;
	
	@Override
	public String getDirBase() {
		return "/relacoes_internacionais/historico";
	}
	
	@Override
	public String getListPage() {
		return "/graduacao/busca_discente.jsp";
	}
	
	/**
	 * Invoca o Mbean responsável por realizar a busca de discente.
	 * <br/>Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/relacoes_internacionais/menu.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException 
	 * @throws NegocioException 
	 */
	public String buscarDiscente() throws SegurancaException, DAOException, NegocioException {
		checkChangeRole();

		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.INTERNACIONALIZAR_ELEMENTOS_HISTORICO_DISCENTE);
		
		TraducaoElementoDao dao = getDAO(TraducaoElementoDao.class);
		entidadeOperacao = getParameter("entidadeOperacao");
		entidadeTraducao = dao.findEntidadeByNomeClasse(StringUtils.capitalize(entidadeOperacao).toString());
		
		if (ValidatorUtil.isEmpty(entidadeTraducao))
			addMensagemErro("Não foi possível obter as informações para tradução da Entidade " + entidadeTraducao + "." );
		
		tituloOperacao = entidadeTraducao.getNome();
		
		return buscaDiscenteMBean.popular();
	}	
	
	
	@Override
	public String selecionaDiscente() throws ArqException {
		
		setOperacaoAtiva(SigaaListaComando.TRADUZIR_ELEMENTO.getId());
		prepareMovimento(SigaaListaComando.TRADUZIR_ELEMENTO);
		
		TraducaoElementoDao dao = getDAO(TraducaoElementoDao.class);
		
		Object objTraduzido = ReflectionUtils.newInstance(entidadeTraducao.getClasse());
		objTraduzido = dao.findByExactField(objTraduzido.getClass(), "discente.id", obj.getId(), true);
		
		if (ValidatorUtil.isEmpty(objTraduzido))
			try {
				throw new NegocioException("Não há registro de " + entidadeTraducao.getNome() + " para o Discente " + obj );
			} catch (NegocioException e) {
				e.printStackTrace();
				addMensagens(e.getListaMensagens());
			}
		
		carregarElementosTraducao(objTraduzido.getClass(), (Integer) ReflectionUtils.evalPropertyObj(objTraduzido, "id"), Order.asc("nome"));
		
		return forward(getDirBase() + "/elemento_discente.jsp");
		
	}

	@Override
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		this.setObj(discente);
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
	
}
