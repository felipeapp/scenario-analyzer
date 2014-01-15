/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on '24/10/2006'
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.Collection;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.ensino.dominio.TipoVeiculacaoEad;

public class TipoVeiculacaoEadMBean extends
		AbstractControllerCadastro<TipoVeiculacaoEad> {
	
	public TipoVeiculacaoEadMBean() {
		obj = new TipoVeiculacaoEad();
	}
	
	@Override
	public Collection<TipoVeiculacaoEad> getAllPaginado() throws ArqException {
		setTamanhoPagina(20);
		return super.getAllPaginado();
	}
	
	@Override
	public String getAtributoOrdenacao() {
		return "descricao";
	}

	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {

		Collection<TipoVeiculacaoEad> tipoVeiculado = getGenericDAO().findByExactField(TipoVeiculacaoEad.class, "descricao", obj.getDescricao());
		
		if (!tipoVeiculado.isEmpty()) {
			addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO,"Tipo de Veicula��o de Ensino � Dist�ncia");
			return null;
		}
		return super.cadastrar();
	}
	
	@Override
	public String remover() throws ArqException {
		Integer id = getParameterInt("id", 0);
		obj = getGenericDAO().findByPrimaryKey(id, TipoVeiculacaoEad.class);
		if (obj == null) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return null;
		}
		prepareMovimento(ArqListaComando.REMOVER);
		return super.remover();
	}

}