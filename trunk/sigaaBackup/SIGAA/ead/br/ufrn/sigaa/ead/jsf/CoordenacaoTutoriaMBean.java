/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/06/25 - 13:54:09
 */
package br.ufrn.sigaa.ead.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ead.EADDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ead.dominio.CoordenacaoTutoria;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaPessoaMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoPessoa;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorPessoa;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Cadastro de coordenador de tutoria
 * 
 * @author David Pereira
 *
 */
@Component("coordenacaoTutoria") @Scope("request")
public class CoordenacaoTutoriaMBean extends SigaaAbstractController<CoordenacaoTutoria> implements OperadorPessoa {

	private boolean confirmado;
	
	public CoordenacaoTutoriaMBean() {
		obj = new CoordenacaoTutoria();
		obj.setPessoa(new Pessoa());
	}
	
	/**
	 * Realiza o pré-processamento de cadastro
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		checkRole(new int[] { SigaaPapeis.SEDIS });
		prepareMovimento(SigaaListaComando.CADASTRO_COORDENADOR_TUTORIA);
		BuscaPessoaMBean mbean = (BuscaPessoaMBean) getMBean("buscaPessoa");
		mbean.setCodigoOperacao(OperacaoPessoa.COORDENADOR_TUTORIA);
		setConfirmButton("Cadastrar");
		return mbean.popular();
	}
	
	/**
	 * Verifica se o usuário deseja substituir o coordenador, caso contrário redireciona para a página de cadastro 
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String confirmar() throws SegurancaException, ArqException, NegocioException {
		EADDao dao = getDAO(EADDao.class);
		CoordenacaoTutoria c = dao.findUltimaCoordenacao();
		
		if (c != null && !confirmado) {
			addMessage("Já existe um coordenador de tutoria cadastrado no sistema. Você deseja substituir o coordenador " + c.getPessoa().getNome() + "?", TipoMensagemUFRN.WARNING);
			confirmado = true;
			setConfirmButton("Confirmar");
			return forward(getFormPage());
		} else {
			return cadastrar();
		}
	}
	
	/**
	 * Realiza o cadastramento de coordenador
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {

		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(SigaaListaComando.CADASTRO_COORDENADOR_TUTORIA);
				
		try {
			executeWithoutClosingSession(mov, getCurrentRequest());
			addMessage(	"Opera&ccedil;&atilde;o realizada com sucesso!", TipoMensagemUFRN.INFORMATION);
		} catch (Exception e) {
			addMensagemErro("Erro Inesperado: " + e.getMessage());
			e.printStackTrace();
			return forward(getFormPage());
		}

		return forward("/ead/menu.jsp");
	}
	
	public String selecionaPessoa() {
		return forward(getFormPage());
	}

	public void setPessoa(Pessoa pessoa) {
		obj.setPessoa(pessoa);
	}

	/**
	 * @return the confirmado
	 */
	public boolean isConfirmado() {
		return confirmado;
	}

	/**
	 * @param confirmado the confirmado to set
	 */
	public void setConfirmado(boolean confirmado) {
		this.confirmado = confirmado;
	}
	
	
	
}
