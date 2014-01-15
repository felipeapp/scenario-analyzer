/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 30/12/2011
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.vestibular.PessoaVestibularDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.vestibular.dominio.ImportacaoDiscenteOutrosConcursos;
import br.ufrn.sigaa.vestibular.dominio.LeiauteArquivoImportacao;
import br.ufrn.sigaa.vestibular.dominio.MapeamentoAtributoCampo;
import br.ufrn.sigaa.vestibular.dominio.PessoaVestibular;
import br.ufrn.sigaa.vestibular.dominio.ResultadoOpcaoCurso;
import br.ufrn.sigaa.vestibular.dominio.SituacaoCandidato;

/**
 * Processador responsável pela efetivação do cadastro de discentes
 * aprovados em outros concursos que não o Vestibular como, por exemplo, SiSU, Reingresso, Reopção, 
 * Transferência Voluntária, etc. 
 * @author Édipo Elder F. de Melo
 *
 */
public class ProcessadorImportaAprovadosOutrosVestibulares extends
		AbstractProcessador {

	/** Executa o cadastramento dos dados.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
		MovimentoCadastro movimento = ((MovimentoCadastro) mov);
		if (movimento.getCodMovimento().equals(SigaaListaComando.IMPORTAR_APROVADOS_OUTROS_VESTIBULARES)) {
			importaAprovados(movimento);
		} else {
			// cadastra o leiaute do arquivo de importação
			GenericDAO dao = getGenericDAO(mov);
			try {
				LeiauteArquivoImportacao leiaute = movimento.getObjMovimentado();
				for (MapeamentoAtributoCampo mapeamento : leiaute.getMapeamentoAtributos())
					mapeamento.setLeiauteArquivoImportacao(leiaute);
				// se estiver atualizando o leiaute, remove os mapeamentos removidos pelo usuário
				if (leiaute.getId() > 0) {
					LeiauteArquivoImportacao leiauteBanco = dao.findByPrimaryKey(leiaute.getId(), LeiauteArquivoImportacao.class);
					dao.detach(leiauteBanco);
					for (MapeamentoAtributoCampo mapeamento : leiauteBanco.getMapeamentoAtributos()) {
						if (!leiaute.getMapeamentoAtributos().contains(mapeamento)) {
							dao.remove(mapeamento);
						}
					}
				}
				dao.createOrUpdate(leiaute);
				for (MapeamentoAtributoCampo mapeamento : leiaute.getMapeamentoAtributos()) {
					if (mapeamento.getId() == 0)
						dao.create(mapeamento);
				}
			} finally {
				dao.close();
			}
		}
		return null;
	}

	/** Importa os discentes aprovados em outros concursos
	 * @param movimento
	 * @throws DAOException
	 */
	private void importaAprovados(MovimentoCadastro movimento) throws DAOException {
		PessoaVestibularDao dao = null;
		try {
			dao = getDAO(PessoaVestibularDao.class, movimento);
					
			ImportacaoDiscenteOutrosConcursos importacaoDiscentes = (ImportacaoDiscenteOutrosConcursos) movimento.getObjMovimentado();
			// persiste o resultado importado
			for ( ResultadoOpcaoCurso resultado : importacaoDiscentes.getResultadosOpcaoCursoImportados() ) {
				// seta valores padrões
				if (resultado.getResultadoClassificacaoCandidato().getOpcaoAprovacao() == null) {
					if(resultado.getResultadoClassificacaoCandidato().getSituacaoCandidato().equals(SituacaoCandidato.APROVADO))
						resultado.getResultadoClassificacaoCandidato().setOpcaoAprovacao(resultado.getMatrizCurricular().getId());
					else
						resultado.getResultadoClassificacaoCandidato().setOpcaoAprovacao(null);
				}
				resultado.getResultadoClassificacaoCandidato().getInscricaoVestibular().setMigrada(true);
				PessoaVestibular pessoaVestibular = resultado.getResultadoClassificacaoCandidato().getInscricaoVestibular().getPessoa();
				if (pessoaVestibular.getId() == 0)
					pessoaVestibular.setMigrada(true);
				resultado.setOrdemOpcao(ResultadoOpcaoCurso.PRIMEIRA_OPCAO);
				// persiste dados pessoais que não estão no sistema
				if (resultado.getResultadoClassificacaoCandidato().getInscricaoVestibular().getPessoa().getId() == 0) {
					PessoaVestibular pessoa = resultado.getResultadoClassificacaoCandidato().getInscricaoVestibular().getPessoa();
					pessoa.anularAtributosVazios();
					dao.create(resultado.getResultadoClassificacaoCandidato().getInscricaoVestibular().getPessoa());
				}
				dao.create(resultado.getResultadoClassificacaoCandidato().getInscricaoVestibular());
				dao.create(resultado.getResultadoClassificacaoCandidato());
				dao.create(resultado);
			}
			dao.create(importacaoDiscentes);
		} finally {
			if (dao != null) dao.close();
		}
	}

	/** Valida a execução do processador.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		int papeis[] = {SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.ADMINISTRADOR_SIGAA};
		checkRole(papeis, mov);
		ListaMensagens lista = new ListaMensagens();
		MovimentoCadastro movimento = ((MovimentoCadastro) mov);
		if (movimento.getCodMovimento().equals(SigaaListaComando.IMPORTAR_APROVADOS_OUTROS_VESTIBULARES)) {
			ImportacaoDiscenteOutrosConcursos importacaoDiscentes = (ImportacaoDiscenteOutrosConcursos) movimento.getObjMovimentado();
			validateRequired(importacaoDiscentes.getResultadosOpcaoCursoImportados(), "Resultados", lista);
		} else {
		}
		checkValidation(lista);
	}

}