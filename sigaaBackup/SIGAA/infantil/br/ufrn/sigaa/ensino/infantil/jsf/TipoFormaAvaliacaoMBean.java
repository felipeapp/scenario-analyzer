package br.ufrn.sigaa.ensino.infantil.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.infantil.dominio.TipoFormaAvaliacao;

@Component @Scope("request")
public class TipoFormaAvaliacaoMBean extends SigaaAbstractController<TipoFormaAvaliacao> {

	/** Retornar combo com todos os tipos de Curso Lato */
	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		return getAllAtivo(TipoFormaAvaliacao.class, "id", "legenda");
	}
	
}