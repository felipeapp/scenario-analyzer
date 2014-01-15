/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '04/02/2011'
 *
 */
package br.ufrn.sigaa.questionario.jsf;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.ava.questionarios.dominio.AlternativaPerguntaQuestionarioTurma;

/**
 * Converter utilizado para recuperar as seleções de alternativas
 * realizadas pelo usuário durante o preenchimento do questionário da turma virtual
 * 
 * @author Fred_Castro
 *
 */
public class AlternativaPerguntaQuestionarioTurmaConverter implements Converter{

	/**
	 * Retorna o objeto da alternativa selecionada.
	 */
	public Object getAsObject (FacesContext context, UIComponent c, String s) {
		
		AlternativaPerguntaQuestionarioTurma alternativa; 
		if (StringUtils.isEmpty(s))
			alternativa = null;
		else {
			alternativa = new AlternativaPerguntaQuestionarioTurma (Integer.valueOf(s));
			alternativa.setSelecionado(true);
		}
		
		return alternativa;
	}

	/**
	 * Retorna o valor da alternativa selecionada.
	 */
	public String getAsString(FacesContext context, UIComponent c, Object o) {
		if (o != null)
			return String.valueOf (((AlternativaPerguntaQuestionarioTurma) o).getId());
		return null;
	}

}