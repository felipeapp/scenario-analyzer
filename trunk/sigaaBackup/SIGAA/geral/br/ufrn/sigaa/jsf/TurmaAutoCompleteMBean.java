package br.ufrn.sigaa.jsf;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.dao.ensino.CalendarioAcademicoDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Managed Bean para possibilitar a criação de autocompletes de para as Turmas.
 * 
 * @author Jean Guerethes
 */
@SuppressWarnings("serial")
@Component @Scope("session")
public class TurmaAutoCompleteMBean extends SigaaAbstractController<Turma> {

	/**
	 * Autocomplete para as turmas
	 * 
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   	<li> /SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/matricula/matriculaTurma.jsp </li>
	 * </ul> 
	 */
	public List<Turma> autocompleteTurma(Object event) throws ArqException {
		
		TurmaDao dao = new TurmaDao();
		CalendarioAcademicoDao cdao = new CalendarioAcademicoDao();
		List<Turma> lista = new ArrayList<Turma>();

		try {
			String nome = event.toString();

			char nivelEnsino = getNivelEnsino();
			Unidade unidadeUsuario = identificarUnidadeUsuario();
			
			if ( getSubSistema().equals(SigaaSubsistemas.TECNICO) || getSubSistema().equals(SigaaSubsistemas.FORMACAO_COMPLEMENTAR)) {
				lista.addAll(dao.findByNomeDisciplina(nome, unidadeUsuario.getId(), nivelEnsino, SituacaoTurma.ABERTA, SituacaoTurma.A_DEFINIR_DOCENTE));
			} else if( !getSubSistema().equals(SigaaSubsistemas.LATO_SENSU) && !getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_LATO) ) {
				CalendarioAcademico cal = cdao.findByUnidadeNivel(unidadeUsuario.getId(), nivelEnsino);
				lista.addAll(dao.findByNomeDisciplinaAnoPeriodo(nome, unidadeUsuario.getId(), cal.getAno(), cal.getPeriodo(), nivelEnsino, SituacaoTurma.ABERTA, SituacaoTurma.A_DEFINIR_DOCENTE));
			} else {
				lista.addAll(dao.findByNomeDisciplina(nome, 0, nivelEnsino, SituacaoTurma.ABERTA, SituacaoTurma.A_DEFINIR_DOCENTE));
			}

			for (Turma turma : lista) {
				turma.getDescricaoComCh();
			}
		}
		finally {
			dao.close();
		}
		return lista;
	}

	/** Responsável por resgatar a unidade do usuário logado. */
	private Unidade identificarUnidadeUsuario() throws ArqException {
		Unidade unidadeUsuario = getUsuarioLogado().getVinculoAtivo().getUnidade();
		if (getNivelEnsino() == NivelEnsino.FORMACAO_COMPLEMENTAR ) {
			unidadeUsuario = new Unidade( 
					getUsuarioLogado().getPermissao(SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR)
							.iterator().next().getUnidadePapel() );
		}
		return unidadeUsuario;
	}
	
}