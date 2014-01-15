/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 02/05/2012
 *
 */
package br.ufrn.sigaa.ensino.latosensu.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.gru.dominio.ConfiguracaoGRU;
import br.ufrn.comum.gru.dominio.GuiaRecolhimentoUniao;
import br.ufrn.comum.gru.negocio.GuiaRecolhimentoUniaoHelper;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.DiscenteLatoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.latosensu.dao.MensalidadeCursoLatoDao;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.DiscenteLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.MensalidadeCursoLato;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/** Processador responsável por criar uma série de mensalidades do curso de um discente.
 * @author Édipo Elder F. de Melo
 *
 */
public class ProcessadorCriarMensalidadeCursoLato extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
		if (mov.getCodMovimento().equals(SigaaListaComando.QUITAR_MENSALIDADES_CURSO_LATO))
			quitarMensalidesPagas(mov);
		else {
			criarMensalidades(mov);
		}
		return null;
	}

	/** Cria a mensalidade no banco sigaa e cria as GRUs referentes à mensalidades no banco comum.
	 * @param mov
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private void criarMensalidades(Movimento mov) throws DAOException, NegocioException {
		MovimentoCadastro movimento = ((MovimentoCadastro) mov);
		GenericDAO dao = getGenericDAO(mov);
		GenericDAO comumDao = DAOFactory.getGeneric(Sistema.COMUM);
		try {
			DiscenteLato discente = (DiscenteLato) dao.refresh(movimento.getObjMovimentado());
			CursoLato curso = discente.getCursoLato();
			List<Date> vencimentos = MensalidadeCursoLatoHelper.getDatasVencimento(curso);
			ConfiguracaoGRU config;
			try {
				config = comumDao.findByPrimaryKey(curso.getIdConfiguracaoGRUMensalidade(), ConfiguracaoGRU.class);
			} finally {
				comumDao.close();
			}
			for (int i = 1; i <= discente.getCursoLato().getQtdMensalidades(); i++) {
				String instrucoes = discente.getCursoLato().getDescricaoCompleta() + "\n" +
						"Matrícula: " + discente.getMatriculaNome() + "\n"
						+ "Mensalidade " + i + " de " + discente.getCursoLato().getQtdMensalidades();
				String endereco = discente.getPessoa().getEnderecoContato().getDescricao();
				Date vencimento = vencimentos.get(i - 1);
				String competencia = String.format("%1$tm/%1$tY", vencimento);
				GuiaRecolhimentoUniao gru = GuiaRecolhimentoUniaoHelper.createGRU(config.getId(), discente.getPessoa().getCpf_cnpj(), discente.getPessoa().getNome(), endereco, instrucoes, competencia, vencimento, curso.getValor());
				MensalidadeCursoLato mensalidade = new MensalidadeCursoLato();
				mensalidade.setDiscente(discente);
				mensalidade.setOrdem(i);
				mensalidade.setIdGRU(gru.getId());
				mensalidade.setVencimento(gru.getVencimento());
				dao.create(mensalidade);
			}
		} finally {
			dao.close();
			comumDao.close();
		}
	}

	/** Atualiza no banco SIGAA as mensalidades que tiveram a GRU paga.
	 * @param mov
	 * @throws DAOException
	 */
	private void quitarMensalidesPagas(Movimento mov) throws DAOException {
		MovimentoCadastro movimento = ((MovimentoCadastro) mov);
		MensalidadeCursoLatoDao dao = getDAO(MensalidadeCursoLatoDao.class, mov);
		DiscenteLatoDao discenteDao =  getDAO(DiscenteLatoDao.class, mov);
		try {
			Collection<Discente> discentes = new LinkedList<Discente>();
			if (movimento.getObjMovimentado() instanceof DiscenteLato) {
				DiscenteLato discente = (DiscenteLato) dao.refresh(movimento.getObjMovimentado());
				discentes.add(discente.getDiscente());
			} else {
				CursoLato curso = movimento.getObjMovimentado();
				Collection<Curso> cursos = new LinkedList<Curso>();
				cursos.add(curso);
				int idUnidade = 0;
				for (DiscenteAdapter da : discenteDao.findOtimizado(null, null, null, null, null, cursos, null, null, idUnidade, NivelEnsino.LATO, false))
					discentes.add(da.getDiscente());
			}
			Collection<MensalidadeCursoLato> mensalidades = new LinkedList<MensalidadeCursoLato>();
			Collection<MensalidadeCursoLato> mDiscente = dao.findAllByDiscente(discentes);
			if (!isEmpty(mDiscente))
				mensalidades.addAll(mDiscente);
			// carrega os objetos GRU transientes
			Collection<Integer> ids = new ArrayList<Integer>();
			for (MensalidadeCursoLato mensalidade : mensalidades)
				ids.add(mensalidade.getIdGRU());
			if (isEmpty(ids)) return;
			for (GuiaRecolhimentoUniao gru : GuiaRecolhimentoUniaoHelper.getGRUByID(ids)) {
				for (MensalidadeCursoLato mensalidade : mensalidades) {
					if (mensalidade.getIdGRU() == gru.getId() && mensalidade.isQuitada() != gru.isQuitada())
						dao.updateField(MensalidadeCursoLato.class, mensalidade.getId(), "quitada", gru.isQuitada());
				}
			}
		} finally {
			dao.close();
			discenteDao.close();
		}
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		GenericDAO dao = getGenericDAO(mov);
		MovimentoCadastro movimento = ((MovimentoCadastro) mov);
		ListaMensagens lista = new ListaMensagens();
		try {
			if (movimento.getObjMovimentado() instanceof DiscenteLato) {
				DiscenteLato discente = (DiscenteLato) dao.refresh(movimento.getObjMovimentado());
				if (!discente.getCurso().isLato())
					lista.addErro("O discente não é de lato sensu");
				else if (discente.getCursoLato().getValor() <= 0 || discente.getCursoLato().getQtdMensalidades() <= 0) {
					lista.addErro("O curso do discente não possui mensalidades");
				} else if (discente.getCursoLato().getIdConfiguracaoGRUMensalidade() == null) {
					lista.addErro("As mensalidades não podem ser geradas automaticamente. Entre em contato com a secretaria do seu curso para o pagamento das mensalidades.");
				}
			} else {
				CursoLato cursoLato = movimento.getObjMovimentado();
				if (cursoLato.getValor() <= 0 || cursoLato.getQtdMensalidades() <= 0) {
					lista.addErro("O curso do discente não possui mensalidades");
				} else if (cursoLato.getIdConfiguracaoGRUMensalidade() == null) {
					lista.addErro("As mensalidades não podem ser geradas automaticamente. Entre em contato com a secretaria do seu curso para o pagamento das mensalidades.");
				}
				
			}
		} finally {
			dao.close();
		}
		checkValidation(lista);
	}

}
