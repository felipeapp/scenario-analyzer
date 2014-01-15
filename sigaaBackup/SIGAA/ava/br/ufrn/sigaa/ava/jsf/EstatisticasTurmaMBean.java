package br.ufrn.sigaa.ava.jsf;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.stereotype.Component;

import br.ufrn.sigaa.ava.dominio.AcaoAva;

@Component("estatisticasTurma")
public class EstatisticasTurmaMBean extends ControllerTurmaVirtual {
	public String iniciarEstatisticas () {
		return forward ("/ava/estatisticas/form.jsp");
	}
	
	public List <SelectItem> getAcoes (){
		
		List <SelectItem> rs = new ArrayList <SelectItem> ();
		
		rs.add(new SelectItem(AcaoAva.ACESSAR, "Acessar"));
		rs.add(new SelectItem(AcaoAva.INSERIR, "Inserir"));
		rs.add(new SelectItem(AcaoAva.ALTERAR, "Alterar"));
		rs.add(new SelectItem(AcaoAva.REMOVER, "Remover"));
		rs.add(new SelectItem(AcaoAva.RESPONDER, "Responder"));
		rs.add(new SelectItem(AcaoAva.CORRIGIR, "Corrigir"));
		rs.add(new SelectItem(AcaoAva.REMOVER_RESPOSTA, "Remover Resposta"));
		
		return rs;
	}
	
}
