/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ava.jsf;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.negocio.MovimentoCadastroAva;

/**
 * Controlador que serve como base para os managed beans da turma virtual.
 *
 * @author davidpereira
 *
 */
public class AvaControllerCadastro<T> extends SigaaAbstractController<T> {

	private String mensagem;

	@Override
	public String cadastrar() throws ArqException {

		if (getConfirmButton().equalsIgnoreCase("remover")) {
			return remover();
		} else {

			Validatable objValidavel = null;
			if (obj instanceof Validatable) {
				objValidavel = (Validatable) obj;
			}

			PersistDB obj = (PersistDB) this.obj;

			try {
				beforeCadastrarAndValidate();
			} catch(Exception e) {
				addMensagemErro(e.getMessage());
				return null;
			}

			if (objValidavel != null && obj instanceof Validatable) {
				erros = new ListaMensagens();
				ListaMensagens lista = objValidavel.validate();

				if (lista != null && !lista.isEmpty()) {
					erros.addAll(lista.getMensagens());
				}
			}

			if (!hasErrors()) {

				MovimentoCadastroAva mov = new MovimentoCadastroAva();
				mov.setObjMovimentado(obj);

				if (obj.getId() == 0) {
					mov.setCodMovimento(SigaaListaComando.CADASTRAR_AVA);
					//mov.setTurma(getTurma());
					mov.setMensagem(mensagem);

					try {
						execute(mov, getCurrentRequest());
						addMessage("Operação realizada com sucesso!", TipoMensagemUFRN.INFORMATION);
						prepareMovimento(SigaaListaComando.CADASTRAR_AVA);
					} catch(NegocioException e) {
						addMensagemErro(e.getMessage());
					} catch (Exception e) {
						notifyError(e);
						addMensagemErro("Erro Inesperado: " + e.getMessage());
						e.printStackTrace();
					}

					afterCadastrar();

					String forward = forwardCadastrar();
					if (forward == null)
						return null;
					else
						return forward(forward);

				} else {
					mov.setCodMovimento(ArqListaComando.ALTERAR);
					try {
						execute(mov, getCurrentRequest());
						addMessage("Operação realizada com sucesso!", TipoMensagemUFRN.INFORMATION);
						afterCadastrar();
					} catch (Exception e) {
						notifyError(e);
						addMensagemErro("Erro Inesperado: " + e.getMessage());
						e.printStackTrace();
					}

					String forward = forwardCadastrar();
					if (forward == null)
						return forward(getListPage());
					else
						return forward(forward);
				}

			} else {
				return null;
			}
		}
	}

	public String remover() {

		MovimentoCadastroAva mov = new MovimentoCadastroAva();
		PersistDB obj = (PersistDB) this.obj;
		mov.setObjMovimentado(obj);

		//Caso o objeto tenha o campo "ativo" não sera deletado do banco, apenas desativado.
		Boolean desativar = false;
		try
		{
			this.obj.getClass().getDeclaredField("ativo");
			desativar = true;
		}
		catch (Exception e) {  }
		//--//


		if (obj.getId() == 0) {
			addMensagemErro("Não há objeto informado para remoção");
			return null;
		} else {
			beforeRemover();

			//mov.setTurma(getTurma());
			mov.setMensagem(mensagem);

			if (desativar) //se for apenas desativar
				mov.setCodMovimento(ArqListaComando.DESATIVAR);
			else
			mov.setCodMovimento(SigaaListaComando.REMOVER_AVA);

			try {
				execute(mov, getCurrentRequest());
				addMessage("Operação realizada com sucesso!", TipoMensagemUFRN.INFORMATION);
			} catch (Exception e) {
				addMensagemErro("Não foi possível realizar a remoção pois existem outros recursos usando este objeto.");
				e.printStackTrace();
			}

			resetBean();
			setConfirmButton("Cadastrar");

			afterRemover();

			return forward(getListPage());

		}
	}

	@Override
	public String preRemover() {

		//Caso o objeto tenha o campo "ativo" não sera deletado do banco, apenas desativado.
		Boolean desativar = false;
		try
		{
			if(ReflectionUtils.propertyExists(this.obj.getClass(), "ativo"))
				desativar = true;
		}
		catch (Exception e) {  }
		//--//


		try {
			if (desativar)
				prepareMovimento(ArqListaComando.DESATIVAR);
			else
				prepareMovimento(SigaaListaComando.REMOVER_AVA);
		} catch(Exception e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErro(e.getMessage());
		}

		return super.preRemover();
	}

	/**
	 * Usado no final do método remover.
	 * deve ser sobrescrito conforme a necessidade.
	 */
	public void afterRemover()
	{

	}

	/**
	 * @return the mensagem
	 */
	public String getMensagem() {
		return mensagem;
	}

	/**
	 * @param mensagem the mensagem to set
	 */
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public void beforeRemover() {

	}

}
