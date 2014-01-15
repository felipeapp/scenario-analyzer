/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on Nov 13, 2007
 *
 */
package br.ufrn.sigaa.ead.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.PessoaMov;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ead.dominio.CoordenacaoPolo;
import br.ufrn.sigaa.jsf.DadosPessoaisMBean;
import br.ufrn.sigaa.jsf.OperacaoDadosPessoais;
import br.ufrn.sigaa.jsf.OperadorDadosPessoais;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 *
 * @author Victor Hugo
 *
 */
@Component("dadosPessoaisCoordenador") @Scope("session")
public class DadosPessoaisCoordenadorPoloMBean extends SigaaAbstractController<CoordenacaoPolo> implements OperadorDadosPessoais{

	/** a pessoa onde vai ser setada a pessoa alterada */
	Pessoa pessoaAlterada;

	public DadosPessoaisCoordenadorPoloMBean() {
		obj = new CoordenacaoPolo();
	}

	/**
	 * inicia a altera��o dos dados pessoais do tutor
	 * � realizado pelo pr�prio tutor
	 * @return
	 * @throws ArqException
	 */
	public String iniciarAlteracaoDadosCoordenadorPolo() throws ArqException{
		if( !getAcessoMenu().isCoordenadorPolo() ){
			addMensagemErro("Voc� n�o tem permiss�o de acesso a esta opera��o.");
			return null;
		}

		obj = getUsuarioLogado().getVinculoAtivo().getCoordenacaoPolo();
		if (obj == null) {
			addMensagemErro("� necess�rio ser um tutor para realizar esta opera��o");
			return null;
	    }

	    PessoaDao pessoaDao = getDAO(PessoaDao.class);
	    obj.setPessoa( pessoaDao.findCompleto(obj.getPessoa().getId()) );


	    prepareMovimento(SigaaListaComando.ALTERAR_PESSOA);

		DadosPessoaisMBean dadosPessoaisMBean = (DadosPessoaisMBean) getMBean("dadosPessoais");
		dadosPessoaisMBean.setCodigoOperacao( OperacaoDadosPessoais.ALTERACAO_DADOS_COORDENADOR_POLO );
		dadosPessoaisMBean.setObj(obj.getPessoa());
		if (obj.getPessoa().getUnidadeFederativa() != null) {
			dadosPessoaisMBean.carregarMunicipiosNaturalidade(null);
		}
		if (obj.getPessoa().getEnderecoContato() != null && obj.getPessoa().getEnderecoContato().getUnidadeFederativa() != null) {
			dadosPessoaisMBean.carregarMunicipiosEndereco(null);
		}
		dadosPessoaisMBean.setSubmitButton("Confirmar altera��o");
		dadosPessoaisMBean.setPassivelAlterarCpf(false);
		return dadosPessoaisMBean.popular();
	}

	public void setDadosPessoais(Pessoa pessoa) {
		pessoaAlterada = pessoa;
	}

	public String submeterDadosPessoais() {
		if( !obj.getPessoa().getCpf_cnpj().equals( pessoaAlterada.getCpf_cnpj() ) ){
			addMensagemErro("N�o � permitido que o tutor altere seu pr�prio cpf.");
			return null;
		}
		PessoaMov mov = new PessoaMov();
		mov.setCodMovimento(SigaaListaComando.ALTERAR_PESSOA);
		mov.setPessoa(pessoaAlterada);
		try {
			executeWithoutClosingSession(mov, getCurrentRequest());
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		} catch (Exception e) {
			addMensagemErro("Erro Inesperado: " + e.getMessage());
			notifyError(e);
			e.printStackTrace();
			return null;
		}
		String nome = pessoaAlterada.getNome();
		addMessage("Dados pessoais de " + nome + " atualizados com sucesso!", TipoMensagemUFRN.INFORMATION);
		return cancelar();
	}

}
