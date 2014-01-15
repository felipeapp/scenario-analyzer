/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
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
import br.ufrn.sigaa.ensino.dominio.TipoDocumentoLegal;

public class TipoDocumentoLegalMBean extends
		AbstractControllerCadastro<TipoDocumentoLegal> {
	
	public TipoDocumentoLegalMBean() {
		obj = new TipoDocumentoLegal();
	}
	
	@Override
	public Collection<TipoDocumentoLegal> getAllPaginado() throws ArqException {
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

		Collection<TipoDocumentoLegal> documento = getGenericDAO().findByExactField(TipoDocumentoLegal.class, "descricao", obj.getDescricao());
		
		if (!documento.isEmpty()) {
		addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO,"Tipo Documento Legal");
		return null;
		}
		return super.cadastrar();
	}
	
	@Override
	public String remover() throws ArqException {
		Integer id = getParameterInt("id", 0);
		obj = getGenericDAO().findByPrimaryKey(id, TipoDocumentoLegal.class);
		if (obj == null) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return null;
		}
		prepareMovimento(ArqListaComando.REMOVER);
		return super.remover();
	}
	
}