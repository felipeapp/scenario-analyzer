/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 09/07/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.internacionalizacao.jsf;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.EntidadeTraducao;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.ItemTraducao;

/**
 *  Managed Bean para realizar o cadastro e gerenciamento dos campos e objetos que serão traduzidos.
 * 
 * @author Rafael Gomes
 */
@Component
@Scope("request")
public class ItemTraducaoMBean extends SigaaAbstractController<ItemTraducao>{

	public ItemTraducaoMBean() {
		clear();
	}
	
	/**
	 * Inicializa os campos do objeto para ser
	 * manipulado durante as operações.
	 * @throws NegocioException 
	 * @throws NegocioException 
	 */
	private void clear() {
		obj = new ItemTraducao();
		obj.setEntidade(new EntidadeTraducao());
		setTamanhoPagina(20);
	}
	
	@Override
	public String getDirBase() {
		return "/relacoes_internacionais/cadastro";
	}
	
	@Override
	public String forwardCadastrar() {
		return getListPage();
	}
	
	@Override
	public String listar() throws ArqException {
		checkChangeRole();
		getPaginacao().setPaginaAtual(0);
		return super.listar();
	}
	
	@Override
	public String getAtributoOrdenacao() {
		return "entidade";
	}
	
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		checkChangeRole();
		setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		return super.preCadastrar();
	}
	
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		GenericDAO dao = getDAO(GenericDAOImpl.class);
		Collection<ItemTraducao> mesmoItem = dao.findByExactField(ItemTraducao.class, 
										new String[]{"atributo","entidade.id"}, new Object[] {obj.getAtributo(), obj.getEntidade().getId()});
		for (ItemTraducao i : mesmoItem) {
			if (i.getId() == obj.getId()) {
				return super.cadastrar();
			} if(i.getAtributo().equals(obj.getAtributo())){
				addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Item para Tradução");
				return null;
			}
		}
		return super.cadastrar();
	}
	
	@Override
	public String remover() throws ArqException {
		checkChangeRole();
		setOperacaoAtiva(ArqListaComando.REMOVER.getId());
		prepareMovimento(ArqListaComando.REMOVER);
		setId();
		GenericDAO dao = getDAO(GenericDAOImpl.class);
		obj = dao.findByPrimaryKey(obj.getId(), obj.getClass());
		if (obj == null) {
			addMensagemErro("O Item para tradução informado já havia sido removido.");
			clear();
		}
		return super.remover();
	}
	
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_SIGAA);
	}
}
