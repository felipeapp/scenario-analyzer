/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 05/06/2012
 *
 */

package br.ufrn.sigaa.jsf;


import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.DataModel;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrm.sigaa.nee.dao.NeeDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.pessoa.dominio.PessoaNecessidadeEspecial;

/**
 * O MBean PessoaNecessidadeEspecialMBean é utilizado para atribuir necessidades especiais à pessoa de um discente. 
 *
 * @author Rafael G. Rodrigues
 *
 */
@Component ("pessoaNecessidadeEspecial")
@Scope ("session")
public class PessoaNecessidadeEspecialMBean extends SigaaAbstractController<PessoaNecessidadeEspecial> implements OperadorDiscente{

	/** Constantes de navegação.*/
	public static final String JSP_FORM_CADASTRA_NECESSIDADES_ESPECIAIS = "/nee/cadastro/form_cadastra_necessidades.jsp";
	
	/** Objeto que armazenará as necessidades especiais do discente selecionado. */
	private DiscenteAdapter discente;
	
	/** Coleção genérica a ser utilizada para manipulação de dados. */
	protected DataModel colecao;
	
	/** Booleano que controla se a operação que está sendo realizada é um cadastro de nova necessidade especial 
	 * ou a alteração das observações de uma necessidade especial já cadastrada. */
	private boolean alterar;
	
	/** Lista utilizada para armazenar as necessidades especiais selecionadas 
	 * nos checkBox do formulário. */
	List<Object> tiposNecessidadesEspeciaisSelecionadas;
	
	
	/** Constructor */
	public PessoaNecessidadeEspecialMBean() {
		initObj();
	}
	
	/** Método de inicialização do objeto no MBean. */
	private void initObj(){
		obj = new PessoaNecessidadeEspecial();
	}
	
	/**
	 * Redirecionar para o Managed Bean para a busca de discentes
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li></li>
	 * </ul> 
	 * @return
	 */
	public String preCadastrar() throws SegurancaException {
		initObj();
		setConfirmButton("Cadastrar");
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.CADASTRO_NECESSIDADE_ESPECIAL);
			
		return buscaDiscenteMBean.popular();
	}
	
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		
		prepareMovimento(SigaaListaComando.CADASTRAR_NEE);
				
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjAuxiliar(tiposNecessidadesEspeciaisSelecionadas);
		mov.setObjMovimentado(discente);
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_NEE);
		
		try {
			execute(mov);
			addMensagem(OPERACAO_SUCESSO);
			if ( mov.getMensagens().isInfoPresent() )
				addMessage(mov.getMensagens().toString(), TipoMensagemUFRN.INFORMATION);
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
		
		return voltar();
	}
	
	/** Responsável por direcionar o usuário para tela com a listagem dos discentes.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/nee/cadastro/form_cadastra_necessidades.jsp  </li>
	 * </ul>  
	 * */
	public String voltar(){
		return redirect("/graduacao/busca_discente.jsf");
	}
	
	/**
	 * Método utilizado para popular a lista contendo as necessidades especiais do discente.
	 * @throws DAOException
	 */
	private void carregarTipoNecessidades() throws DAOException{
		tiposNecessidadesEspeciaisSelecionadas = new ArrayList<Object>();
		if ( obj.getPessoa().getTipoNecessidadeEspecial() != null ){
			tiposNecessidadesEspeciaisSelecionadas.add(
				new Integer( obj.getPessoa().getTipoNecessidadeEspecial().getId()).toString());
		}
		NeeDao dao = getDAO(NeeDao.class);
		if ( obj.getPessoa().getId() > 0  ){
			for (PessoaNecessidadeEspecial pne : dao.findByExactField(PessoaNecessidadeEspecial.class, "pessoa.id", obj.getPessoa().getId())) {
				if ( !tiposNecessidadesEspeciaisSelecionadas.contains(new Integer(pne.getTipoNecessidadeEspecial().getId()).toString()) ){
					tiposNecessidadesEspeciaisSelecionadas.add(
						new Integer(pne.getTipoNecessidadeEspecial().getId()).toString() );
				}
			}
		}
	}

	@Override
	public String selecionaDiscente() throws ArqException {
		carregarTipoNecessidades();
		return forward(JSP_FORM_CADASTRA_NECESSIDADES_ESPECIAIS);
	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	@Override
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		this.discente = discente;
		obj.setPessoa(this.discente.getPessoa());
	}

	public boolean isAlterar() {
		return alterar;
	}
	
	public void setAlterar(boolean alterar) {
		this.alterar = alterar;
	}

	public List<Object> getTiposNecessidadesEspeciaisSelecionadas() {
		return tiposNecessidadesEspeciaisSelecionadas;
	}

	public void setTiposNecessidadesEspeciaisSelecionadas(
			List<Object> tiposNecessidadesEspeciaisSelecionadas) {
		this.tiposNecessidadesEspeciaisSelecionadas = tiposNecessidadesEspeciaisSelecionadas;
	}
	
	
}
