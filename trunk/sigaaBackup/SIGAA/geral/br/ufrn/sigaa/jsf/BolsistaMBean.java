/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Mai 14, 2007
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.Collection;
import java.util.Date;

import javax.faces.model.SelectItem;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Bolsista;
import br.ufrn.sigaa.dominio.TipoBolsa;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.projetos.dominio.EntidadeFinanciadora;

/**
 *
 * @author Victor Hugo
 *
 */
public class BolsistaMBean extends SigaaAbstractController<Bolsista> {

	private static final String FORM_PAGE = "/administracao/cadastro/Bolsista/form.jsp";
	private static final String LIST_PAGE = "/administracao/cadastro/Bolsista/lista.jsp";

	/**
	 * atributos para auxilio na montagem do form
	 */
	private boolean blockEntidade, blockTipoBolsa;


	/**
	 * atributos para auxilio na busca
	 *
	 */
//	private boolean checkBuscaTipoBolsa, checkBuscaEntidade;
//	private int buscaTipoBolsa, buscaEntidade;


	public BolsistaMBean() {
		obj = new Bolsista();
		obj.setEntidadeFinanciadora( new EntidadeFinanciadora() );
		obj.setDiscente( new Discente() );
		obj.setTipoBolsa( new TipoBolsa() );
	}

	@Override
	public Collection<SelectItem> getAllCombo() {
		return getAll(Bolsista.class, "id", "descricao");
	}

	@Override
	public void beforeCadastrarAndValidate() throws NegocioException, SegurancaException, DAOException {

		if( obj.getEntidadeFinanciadora().getId() == 0 )
			obj.setEntidadeFinanciadora(null);


		if( getConfirmButton().equalsIgnoreCase("cadastrar") ){
			obj.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
			obj.setDataCadastro(new Date());
		}

	}

	@Override
	public String forwardCadastrar() {
		//return getSubSistema().getForward();
		return forward( "/monitoria/index.jsf" );
	}

	@Override
	public String getListPage() {
		return LIST_PAGE;
	}

	@Override
	public String getFormPage() {
		return FORM_PAGE;
	}

	/**
	 * prepara a remoção para cada caso especifico, dependendo do tipo de bolsista o formulário será diferente
	 */
	@Override
	public String preRemover() {

		if( isUserInRole( SigaaPapeis.GESTOR_MONITORIA ) ){
			blockEntidade = true;
			blockTipoBolsa = true;
		}

		return super.preRemover();
	}

	/**
	 * prepara a atualização para cada caso especifico, dependendo do tipo de bolsista o formulário será diferente
	 * @throws ArqException
	 */
	@Override
	public String atualizar() throws ArqException {

		if( isUserInRole( SigaaPapeis.GESTOR_MONITORIA ) ){
			blockEntidade = true;
			blockTipoBolsa = true;
		}

		return super.atualizar();
	}

	/**
	 * prepara para o cadastro de um bolsista de graduação
	 * @return
	 */
	public String preCadastrarGraduacao(){
//		checkRole( SigaaPapeis.GESTOR_MONITORIA );
//		prepareMovimento(ArqListaComando.CADASTRAR);
		return null;
	}

	/**
	 * prepara a tela para o cadastro de um bolsista PEC-G
	 * esta operação so pode ser executada pela PROGRAD
	 * @return
	 * @throws ArqException
	 */
	public String preCadastrarPecg() throws ArqException{

		checkRole( SigaaPapeis.GESTOR_MONITORIA );

		prepareMovimento(ArqListaComando.CADASTRAR);

		obj.setTipoBolsa( new TipoBolsa( TipoBolsa.BOLSA_PECG ) );
		blockEntidade = true;
		blockTipoBolsa = true;

		//return redirectPage( getFormPage() );
		return forward( FORM_PAGE );

	}

	public boolean isBlockEntidade() {
		return blockEntidade;
	}

	public void setBlockEntidade(boolean blockEntidade) {
		this.blockEntidade = blockEntidade;
	}

	public boolean isBlockTipoBolsa() {
		return blockTipoBolsa;
	}

	public void setBlockTipoBolsa(boolean blockTipoBolsa) {
		this.blockTipoBolsa = blockTipoBolsa;
	}


}
