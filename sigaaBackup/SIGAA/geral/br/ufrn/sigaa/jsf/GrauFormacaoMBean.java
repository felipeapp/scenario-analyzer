/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '26/10/2006'
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.GrauFormacao;

public class GrauFormacaoMBean extends
		SigaaAbstractController<br.ufrn.sigaa.dominio.GrauFormacao> {
	
	public GrauFormacaoMBean() {
		obj = new GrauFormacao();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(GrauFormacao.class, "id", "descricao");
	}
	
	@Override
	public Collection<GrauFormacao> getAllPaginado() throws ArqException {
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
		
		GenericDAO dao = getDAO(GenericDAOImpl.class);
		Collection<GrauFormacao> mesmoGrauFormacao = dao.findByExactField(GrauFormacao.class, "descricao", obj.getDescricao());
		for (GrauFormacao as : mesmoGrauFormacao) {
			if (as.getId() == obj.getId()) {
				return super.cadastrar();
			} if((as.getDescricao().equals(obj.getDescricao())) && (as.getOrdemtitulacao().equals(obj.getOrdemtitulacao()))){
				addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Grau Formação");
				return null;
			}
		}
		return super.cadastrar();
	}
	
	@Override
	public String remover() throws ArqException {
		Integer id = getParameterInt("id", 0);
		obj = getGenericDAO().findByPrimaryKey(id, GrauFormacao.class);
		if (obj == null) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return null;
		}
		prepareMovimento(ArqListaComando.REMOVER);
		return super.remover();
	}
		
}