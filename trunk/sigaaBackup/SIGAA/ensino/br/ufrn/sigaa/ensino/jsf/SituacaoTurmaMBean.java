/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 02/02/2007
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;

/**
 * MBean responsável pela geração de collections para exibição
 * em JSPs de objetos da classe SituacaoTurma 
 *
 * @author Leonardo
 */
@Component("situacaoTurma")
@Scope("request")
public class SituacaoTurmaMBean extends AbstractControllerCadastro<SituacaoTurma> {

	/** Lista de {@link SelectItem} referente às situações de turmas carregadas. */
	private static List<SelectItem> situacoesCombo;

	/**
	 * Retorna todas as situações de turma válidas.
	 * <br />
	 * Chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/turma/busca_turma.jsp</li>
	 * <li>/sigaa.war/graduacao/relatorios/turma/seleciona_ocupacao_vagas.jsp</li>
	 * <li>/sigaa.war/graduacao/relatorios/turma/seleciona_turma.jsp</li>
	 * <li>/sigaa.war/graduacao/relatorios/turma/seleciona_turmas_ano_periodo.jsp</li>
	 * <li>/sigaa.war/graduacao/turma/lista.jsp</li>
	 * </ul>
	 * 
	 * @see SituacaoTurma#getSituacoesValidas()
	 */
	public Collection<SelectItem> getAllCombo() throws ArqException {
		if ( situacoesCombo == null ) {
			Collection<SituacaoTurma> situacoes = getAll();
			situacoes.retainAll(SituacaoTurma.getSituacoesValidas());
			situacoesCombo =  toSelectItems(situacoes, "id", "descricao");
		}
		return situacoesCombo;
	}
	
	/**
	 * Retorna uma lista com todas as situações de turma.
	 * <br />
	 * Chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/relatorios/turma/seleciona_turmas_abertas_sem_solicitacao.jsp</li>
	 * </ul>
	 * 
	 * @see SituacaoTurma#getSituacoesTodas()
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<SelectItem> getAllSituacoesCombo() throws ArqException {
		if ( situacoesCombo == null ) {
			Collection<SituacaoTurma> situacoes = getAll();
			situacoes.retainAll(SituacaoTurma.getSituacoesTodas());
			situacoesCombo =  toSelectItems(situacoes, "id", "descricao");
		}
		return situacoesCombo;
	}

	/**
	 * Busca no bando de dados uma lista contendo todas as situações cadastradas.
	 * <br />
	 * Método não invocado por JSPs.
	 */
	@Override
	public Collection<SituacaoTurma> getAll() throws ArqException {
		GenericDAO dao = null;
		try {
			dao = getGenericDAO();
			dao.setSistema(getSistema());
			return dao.findAll(SituacaoTurma.class, "descricao", "asc");
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<SituacaoTurma>(0);
		} finally {
			if(dao != null){
				dao.close();
			}
		}
	}
}
