/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 24/11/2006
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.ExecutorFinanceiro;
import br.ufrn.sigaa.extensao.dominio.ProdutoExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoProduto;
import br.ufrn.sigaa.extensao.jsf.helper.AtividadeExtensaoHelper;
import br.ufrn.sigaa.extensao.negocio.AtividadeExtensaoValidator;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/*******************************************************************************
 * Classe utilizada no contexto de PrestacaoServicos. O modulo de prestacao de
 * servicos ainda nao estah totalmente definido no SIGAA.
 * 
 * @author Victor Hugo
 * 
 ******************************************************************************/
@Scope("session")
@Component("produtoExtensao")
public class ProdutoMBean extends SigaaAbstractController<AtividadeExtensao> {

	/** Atributo utilizado para representar o coordenador. */
	private Servidor coordenador = new Servidor();
	/** Atributo utilizado para representar a Carga Horária Semanal do Coordenador. */
	private Integer chSemanalCoordenador;

	public ProdutoMBean() {
		clear();
	}

	/**
	 * Limpa os objetos do Mbean e prepara para o inicio do cadastro do progarma.
	 */
	private void clear() {
		obj = new AtividadeExtensao();
		obj.setPermanente(false);
		obj.setTipoAtividadeExtensao(new TipoAtividadeExtensao(TipoAtividadeExtensao.PRODUTO));
		obj.setProdutoExtensao(new ProdutoExtensao());
		obj.getProdutoExtensao().setTipoProduto(new TipoProduto());
	}

	/**
	 * Inicia o cadastro do programa passando o controle para o {@link AtividadeExtensaoMBean}.
	 * Prepara o formulário para cadastro de dados gerais.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/Atividade/seleciona_atividade.jsp</li>
	 * </ul>
	 *  
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException {
		clear();
		AtividadeExtensaoHelper.getAtividadeMBean().prepararFormulario(this);
		return forward(ConstantesNavegacao.DADOS_GERAIS);
	}

	/**
	 * Valida os dados básicos de produto e vai para a tela de orçamento
	 * detalhado.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.wa/extensao/Atividade/produto.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String submeterProduto() {

		ListaMensagens mensagens = new ListaMensagens();

		try {

			// validação
			AtividadeExtensaoValidator.validaDadosProduto(obj, mensagens);

			if (!mensagens.isEmpty()) {
				addMensagens(mensagens);
				AtividadeExtensaoHelper.instanciarAtributos(getObj());
				return null;
			}

			GenericDAO dao = getGenericDAO();
			obj.getProdutoExtensao().setTipoProduto(
					dao.findByPrimaryKey(obj.getProdutoExtensao()
							.getTipoProduto().getId(), TipoProduto.class));

		} catch (DAOException e) {
			mensagens.addErro("Falha ao validar dados do produto. " + e.getMessage());
			notifyError(e);
		}

		return AtividadeExtensaoHelper.getAtividadeMBean().proximoPasso();

	}

	/**
	 * Valida as atividades adicionadas ao produto e vai para próximo passo
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.wa/extensao/Atividade/atividades.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String submeterAtividades() {
		// validação
		ListaMensagens mensagens = new ListaMensagens();
		AtividadeExtensaoValidator.validaAtividadesProduto(obj, mensagens);

		if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
			return null;
		}
		return AtividadeExtensaoHelper.getAtividadeMBean().proximoPasso();
	}

	public Servidor getCoordenador() {
		return coordenador;
	}

	public void setCoordenador(Servidor coordenador) {
		this.coordenador = coordenador;
	}

	public Integer getChSemanalCoordenador() {
		return chSemanalCoordenador;
	}

	public void setChSemanalCoordenador(Integer chSemanalCoordenador) {
		this.chSemanalCoordenador = chSemanalCoordenador;
	}

}
