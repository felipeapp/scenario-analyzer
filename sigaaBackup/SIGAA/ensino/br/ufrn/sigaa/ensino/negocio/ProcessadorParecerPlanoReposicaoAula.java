/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.caixa_postal.ASyncMsgDelegate;
import br.ufrn.arq.caixa_postal.Mensagem;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dominio.AulaExtra;
import br.ufrn.sigaa.ensino.dominio.AvisoFaltaDocenteHomologada;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAvisoFaltaHomologado;
import br.ufrn.sigaa.ensino.dominio.ParecerPlanoReposicaoAula;
import br.ufrn.sigaa.ensino.dominio.PlanoReposicaoAula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.util.HorarioTurmaUtil;

/**
 * Processador para persistir o Parecer dado a um plano de aula.
 * Executa algumas tarefas relacionadas: Cria Aula Extra e Atualiza o Plano de Aula
 * 
 * @author Henrique André
 *
 */
public class ProcessadorParecerPlanoReposicaoAula extends AbstractProcessador {

	/**
	 * Método invocado pela arquitetura
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		validate(mov);

		MovimentoCadastro movCad = (MovimentoCadastro) mov;
		PlanoReposicaoAula planoAula = ( (ParecerPlanoReposicaoAula) movCad.getObjMovimentado()).getPlanoAula();
		
		
		if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_PARECER_PLANO_AULA)) {
			ParecerPlanoReposicaoAula parecer = criarParecer(movCad);
			AulaExtra aulaExtra = criarAulaExtra(movCad, planoAula);
			atualizaPlanoAula(movCad, planoAula, aulaExtra, parecer);
			
			atualizarFaltaHomologada(movCad, planoAula);
			
		} else if (mov.getCodMovimento().equals(SigaaListaComando.NOTIFICAR_PROFESSOR_PARECER_PLANO_AULA)) {
			notificarProfessor(movCad);
		}
		
		return movCad.getObjMovimentado();
	}

	/**
	 * Atualiza a {@link AvisoFaltaDocenteHomologada} com o {@link PlanoReposicaoAula} aprovado.
	 * 
	 * @param movCad
	 * @param planoAula
	 * @throws DAOException
	 */
	private void atualizarFaltaHomologada(MovimentoCadastro movCad, PlanoReposicaoAula planoAula) throws DAOException {
		planoAula.getFaltaHomologada().setPlanoAprovado(planoAula);
		if (planoAula.isAprovado()) {
			getGenericDAO(movCad).updateField(AvisoFaltaDocenteHomologada.class, planoAula.getFaltaHomologada().getId(), "planoAprovado", planoAula);
			getGenericDAO(movCad).updateField(AvisoFaltaDocenteHomologada.class, planoAula.getFaltaHomologada().getId(), "movimentacao", MovimentacaoAvisoFaltaHomologado.PLANO_APROVADO);
		} else {
			getGenericDAO(movCad).updateField(AvisoFaltaDocenteHomologada.class, planoAula.getFaltaHomologada().getId(), "movimentacao", MovimentacaoAvisoFaltaHomologado.PENDENTE_REAPRESENTACAO_PLANO);
		}
	}

	/**
	 * Cria uma aula Extra para repor a aula perdida.
	 * 
	 * @param mov
	 * @param parecer
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private AulaExtra criarAulaExtra(MovimentoCadastro mov, PlanoReposicaoAula planoAula) throws NegocioException, ArqException, RemoteException {
		
		Turma turma = getGenericDAO(mov).refresh(planoAula.getFaltaHomologada().getDadosAvisoFalta().getTurma());
		
		Date dataFalta = planoAula.getFaltaHomologada().getDadosAvisoFalta().getDataAula();
		int dia = CalendarUtils.getDiaSemanaByData(dataFalta);
		
		int totalAulas = HorarioTurmaUtil.getTotalHorariosDiaTurno(dia, null, turma.getHorarios());
		
		AulaExtra aulaExtra = new AulaExtra();
		aulaExtra.setDataAula(planoAula.getDataAulaReposicao());
		aulaExtra.setNumeroAulas(totalAulas);
		aulaExtra.setObservacoes(planoAula.getDidatica());
		aulaExtra.setTipo(AulaExtra.REPOSICAO);
		aulaExtra.setTurma(planoAula.getFaltaHomologada().getDadosAvisoFalta().getTurma());
		getGenericDAO(mov).create(aulaExtra);
		
		return aulaExtra;
	}
	
	/**
	 * Retorna uma lista de {@link UsuarioGeral} a serem usados como destinatários da notificação.
	 * 
	 * @param chefe
	 * @return
	 * @throws DAOException
	 */
	private List<UsuarioGeral> criarDestinatarios(List<UsuarioGeral> chefe) throws DAOException {
		List<UsuarioGeral> resultado = new ArrayList<UsuarioGeral>();
		
		for (UsuarioGeral usuarioGeral : chefe) {
			if (usuarioGeral == null || isEmpty(usuarioGeral.getEmail()))
				return null;
			
			resultado.add(usuarioGeral);
		}
		
		
		return resultado;
	}

	/**
	 * Notifica o professor sobre o parecer do plano de aula (Se foi aceito ou negado)
	 * @param mov 
	 * @throws RemoteException 
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	private void notificarProfessor(MovimentoCadastro mov) throws NegocioException, ArqException, RemoteException {
		
		ParecerPlanoReposicaoAula parecer = (ParecerPlanoReposicaoAula) mov.getObjMovimentado();
		
		UsuarioDao dao = getDAO(UsuarioDao.class, mov);
		
		try {
			List<UsuarioGeral> usuarios = dao.findAllByServidor(parecer.getPlanoAula().getFaltaHomologada().getDadosAvisoFalta().getDocente().getId());
			
			parecer.setStatus(dao.refresh(parecer.getStatus()));
			
			List<UsuarioGeral> destinatarios = criarDestinatarios(usuarios);
			Mensagem mensagem = criarMensagem(parecer);
			
			ASyncMsgDelegate.getInstance().enviaMensagemUsuarios(mensagem,
					destinatarios.get(0), destinatarios);
		} finally {
			dao.close();
		}
	}

	/**
	 * Cria a mensagem que será enviada ao docente.
	 * 
	 * @param parecer
	 * @return
	 */
	private Mensagem criarMensagem(ParecerPlanoReposicaoAula parecer) {
		StringBuilder sb = new StringBuilder();
		sb.append("Caro(a) Professor(a),\n");
		sb.append("O Chefe de Departamento emitiu um parecer sobre o plano de aula.");
		sb.append("\n\n");
		sb.append("-----");
		sb.append("\n");
		sb.append("Disciplina: " + parecer.getPlanoAula().getFaltaHomologada().getDadosAvisoFalta().getTurma().getDisciplina().getCodigoNome());
		sb.append("\n");
		sb.append("Turma: " + parecer.getPlanoAula().getFaltaHomologada().getDadosAvisoFalta().getTurma().getCodigo());
		sb.append("\n");
		sb.append("Data da Falta: " + Formatador.getInstance().formatarData(parecer.getPlanoAula().getFaltaHomologada().getDadosAvisoFalta().getDataAula()));
		sb.append("\n\n");
		sb.append("-----");
		sb.append("\n");
		sb.append("Situação: " + parecer.getStatus().getDescricao());
		sb.append("\n");
		sb.append("Justificativa: " + parecer.getJustificativa());
		
		if (parecer.getPlanoAula().isAprovado()) {
			sb.append("\n\n");
			sb.append("O plano de aula foi enviado para todos os alunos da turma.");
		} else if (parecer.getPlanoAula().isNegado()) {
			sb.append("\n\n");
			sb.append("Como o plano anterior foi negado, se faz necessária a apresentação de um novo plano para análise.");
		}
		sb.append("\n");
		
		String titulo = "Andamento do Plano de Reposição de Aula";
		
		Mensagem mensagem = new Mensagem();
		mensagem.setAutomatica(true);
		mensagem.setTitulo(titulo.toUpperCase());
		mensagem.setMensagem(sb.toString());
		mensagem.setSistema(Sistema.SIGAA);

		return mensagem;
	}

	/**
	 * Cria o parecer do plano de aula
	 * @param mov
	 * @param parecer
	 * @throws DAOException
	 */
	private ParecerPlanoReposicaoAula criarParecer(MovimentoCadastro mov) throws DAOException {
		getGenericDAO(mov).create(mov.getObjMovimentado());
		return mov.getObjMovimentado();
	}

	/**
	 * Atualiza o plano de aula
	 * 
	 * @param mov
	 * @param parecer
	 * @throws DAOException
	 */
	private PlanoReposicaoAula atualizaPlanoAula(MovimentoCadastro mov, PlanoReposicaoAula planoAula, AulaExtra aulaExtra, ParecerPlanoReposicaoAula parecer)
			throws DAOException {
		planoAula.setAulaExtra(aulaExtra);
		planoAula.setParecer(parecer);
		getGenericDAO(mov).update(planoAula);
		
		return planoAula;
	}

	/**
	 * Valida o Parecer dado ao plano de aula
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		MovimentoCadastro movCad = (MovimentoCadastro) mov;
		ParecerPlanoReposicaoAula parecer = movCad.getObjMovimentado();
		
		if ( isEmpty(parecer.getPlanoAula() ))
			throw new NegocioException("É necessário um plano de aula.");
	}

}
