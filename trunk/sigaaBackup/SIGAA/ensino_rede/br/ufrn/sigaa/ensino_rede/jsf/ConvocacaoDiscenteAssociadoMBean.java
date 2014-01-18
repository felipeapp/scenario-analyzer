package br.ufrn.sigaa.ensino_rede.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino_rede.dao.ConvocacaoDiscenteAssociadoDao;
import br.ufrn.sigaa.ensino_rede.dominio.ConvocacaoDiscenteAssociado;

@Component @Scope("request")
public class ConvocacaoDiscenteAssociadoMBean extends SigaaAbstractController<ConvocacaoDiscenteAssociado>  {

	public ConvocacaoDiscenteAssociadoMBean() {
		obj = new ConvocacaoDiscenteAssociado();
	}
	
	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		return toSelectItems(getGenericDAO().findAll(
				ConvocacaoDiscenteAssociado.class, "data", "desc"), "id", "descricaoCompleta");
	}

	public Collection<SelectItem> getAllConvocacoesByCampusCombo(int idCampus, int programa) throws ArqException {
		Collection<ConvocacaoDiscenteAssociado> convocacoes = 
				getDAO(ConvocacaoDiscenteAssociadoDao.class).findConvocacao(idCampus, programa);
		return toSelectItems(convocacoes, "id", "descricaoCompleta");
	}
	
}