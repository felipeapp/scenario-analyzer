package br.ufrn.sigaa.ensino_rede.academico.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.sigaa.ensino_rede.dominio.TipoDocenteRede;
import br.ufrn.sigaa.ensino_rede.jsf.EnsinoRedeAbstractController;

@SuppressWarnings("serial")
@Component("tipoDocenteMBean") @Scope("session")
public class TipoDocenteMBean extends EnsinoRedeAbstractController<TipoDocenteRede> {

	public TipoDocenteMBean() {
		obj = new TipoDocenteRede();
	}
	
	@Override
	public String getLabelCombo() {
		return "descricao";
	}
	
}
