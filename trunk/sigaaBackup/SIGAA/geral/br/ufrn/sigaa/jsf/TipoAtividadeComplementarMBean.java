/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '24/10/2006'
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
import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.ensino.dominio.TipoAtividadeComplementar;

public class TipoAtividadeComplementarMBean extends AbstractControllerCadastro<TipoAtividadeComplementar> {
	public TipoAtividadeComplementarMBean() {
		obj = new TipoAtividadeComplementar();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(TipoAtividadeComplementar.class, "id", "descricao");
	}
	
	@Override
	public Collection<TipoAtividadeComplementar> getAllPaginado() throws ArqException {
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
		Collection<TipoAtividadeComplementar> mesmaTipo = dao.findByExactField(TipoAtividadeComplementar.class, "descricao", obj.getDescricao());
		for (TipoAtividadeComplementar as : mesmaTipo) {
			if (as.getId() == obj.getId()) {
				return super.cadastrar();
			} if(as.getDescricao().equals(obj.getDescricao())){
				addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Tipo Atividade Complementar");
				return null;
			}
		}
		return super.cadastrar();
	}

}