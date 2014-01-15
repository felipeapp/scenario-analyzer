/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Jun 24, 2008
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.sigaa.arq.dao.ensino.MovimentacaoAlunoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoAfastamentoAluno;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Este MBeam controla o caso de uso de destrancar curso realizado pelo ALUNO
 * @author Victor Hugo
 */
@Component("destrancarPrograma") @Scope("request")
public class DestrancarProgramaMBean extends SigaaAbstractController {

	private Long cpf;
	private String identidade;

	/**
	 * inicia o caso de uso
	 * apenas alunos de graduação que estão com o programa trancado pode realizar esta operação
	 * @return
	 * @throws DAOException
	 */
	public String iniciar() throws DAOException{

		if( getDiscenteUsuario() == null || !getDiscenteUsuario().isGraduacao() || !getDiscenteUsuario().isTrancado() ){
			addMensagemErro("Esta operação só pode ser realizada por discentes de Graduação que estão com o status TRANCADO.");
			return null;
		}

		if( !getCalendarioVigente().isPeriodoMatriculaRegular() ){
			addMensagemErro(CalendarioAcademico.getDescricaoPeriodo("DESTRANCAR CURSO",
					getCalendarioVigente().getInicioMatriculaOnline(),
					getCalendarioVigente().getFimMatriculaOnline() ) );
			return null;
		}

		MovimentacaoAlunoDao dao = getDAO(MovimentacaoAlunoDao.class);
		MovimentacaoAluno trancamentoAtual = dao.findTrancamentosByDiscente(getDiscenteUsuario().getId(), getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo(), true);

		if( trancamentoAtual == null ){
			addMensagemErro("Não existe um trancamento para você cadastrado no ano-período atual");
			return null;
		}

		return forward( getFormPage() );
	}

	public String destrancar() throws ArqException{

		MovimentacaoAlunoDao dao = getDAO(MovimentacaoAlunoDao.class);

		Discente d = dao.findByPrimaryKey( getDiscenteUsuario().getId() , Discente.class);
		Pessoa p = d.getPessoa();

		//comentado, pois existia warning de "never read"
		//MovimentacaoAluno trancamentoAtual = dao.findTrancamentosByDiscente(d.getId(), getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo(), true);

		if( !p.getIdentidade().getNumero().equalsIgnoreCase( identidade ) ){
			addMensagemErro("Identidade não confere.");
		}

		if( !p.getCpf_cnpj().equals( cpf ) ){
			addMensagemErro("CPF não confere.");
		}

		if( !confirmaSenha() )
			return null;

		if( hasErrors() )
			return null;

		MovimentoAfastamentoAluno mov = new MovimentoAfastamentoAluno();
		mov.setCodMovimento(SigaaListaComando.CANCELAR_TRANCAMENTO_PROGRAMA);
		mov.setAutoRetorno(true);
		mov.setCalendario( getCalendarioVigente() );
		mov.setObjMovimentado(null); // utilizado apenas quando o aluno está realizando o destrancamento

		try {
			executeWithoutClosingSession(mov, getCurrentRequest());
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		}
		dao.clearSession();
		//VinculoUsuario.processarVinculosUsuario(getUsuarioLogado(), getCurrentRequest());

		addMensagemInformation("Art. 202. Os alunos  regularmente cadastrados em cursos de graduação que não efetivarem " +
				"sua matrícula em um determinado período letivo regular terão o vínculo automaticamente cancelado com a " + RepositorioDadosInstitucionais.getSiglaInstituicao() + "." +
				"(Regulamento dos Cursos Regulares de Graduação, Resolução N° 227/2009-CONSEPE, de 3 de dezembro de 2009) ");
		addMensagemInformation("Você foi destrancado com sucesso!");
		return cancelar();
	}

	@Override
	public String getDirBase() {
		return "/graduacao/destrancar_programa";
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public String getIdentidade() {
		return identidade;
	}

	public void setIdentidade(String identidade) {
		this.identidade = identidade;
	}

}