/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 04/07/2008
 *
 */
package br.ufrn.sigaa.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.NAO_HA_OBJETO_REMOCAO;
import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.comum.dominio.TipoNecessidadeEspecial;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;

/**
 * MBean responsável pelas operações referentes aos tipos de necessidades especiais.
 * 
 * @author Rômulo Augusto
 *
 */
@Component
@Scope(value = "session")
public class TipoNecessidadeEspecialMBean extends SigaaAbstractController<TipoNecessidadeEspecial> {

	/** Endereço da JSP responsável pelo formulário de cadastro, alteração e remoção de tipos de necessidades especiais. */
	public final String JSP_FORM = "/administracao/cadastro/TipoNecessidadeEspecial/form.jsp";
	/** Endereço da JSP responsável pela listagem dos tipos de necessidade especiais cadastradas na base de dados.*/
	public final String JSP_LISTA = "/administracao/cadastro/TipoNecessidadeEspecial/lista.jsp";
	
	public TipoNecessidadeEspecialMBean(){
		obj = new TipoNecessidadeEspecial();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(TipoNecessidadeEspecial.class, "id", "descricao");
	}
	
	@Override
	public String getAtributoOrdenacao() {
		return "descricao";
	}
	
	@Override
	public String forwardCadastrar() {
		return JSP_LISTA;
	}
	
	@Override
	public String getFormPage() {
		return JSP_FORM;
	}
	
	@Override
	public String getListPage() {
		return JSP_LISTA;
	}
	
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		return super.cadastrar();
	}
	
	@Override
	public String remover() throws ArqException {
		if (obj == null) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return null;
		}
		prepareMovimento(ArqListaComando.REMOVER);
		
		beforeRemover();

		MovimentoCadastro mov = new MovimentoCadastro(obj, ArqListaComando.REMOVER);

		if (obj == null || obj.getId() == 0) {
			addMensagem(NAO_HA_OBJETO_REMOCAO);
			return null;
		} else {

			try {
				execute(mov);
				addMensagem(OPERACAO_SUCESSO);
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				return null;
			} 

			setResultadosBusca(null);
			afterRemover();

			String forward = forwardRemover();
			if (forward == null) {
				return redirectJSF(getListPage());
			} else {
				return redirectJSF(forward);
			}

		}
	}

}
