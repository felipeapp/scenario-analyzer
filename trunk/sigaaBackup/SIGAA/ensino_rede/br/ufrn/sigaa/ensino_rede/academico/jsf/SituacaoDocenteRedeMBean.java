package br.ufrn.sigaa.ensino_rede.academico.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.sigaa.ensino_rede.dominio.SituacaoDocenteRede;
import br.ufrn.sigaa.ensino_rede.jsf.EnsinoRedeAbstractController;

@SuppressWarnings("serial")
@Component("situacaoDocenteRedeMBean") @Scope("session")
public class SituacaoDocenteRedeMBean extends EnsinoRedeAbstractController<SituacaoDocenteRede>{

	public SituacaoDocenteRedeMBean() {
		obj = new SituacaoDocenteRede();
	}
	
	@Override
	public String getLabelCombo() {
		return "descricao";
	}
	
}
