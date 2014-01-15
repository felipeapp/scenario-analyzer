/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 22/09/2006
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.pesquisa.EditalPesquisaDao;
import br.ufrn.sigaa.arq.dao.projetos.ProjetoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.util.EnvioMensagemHelper;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.FinanciamentoProjetoPesq;
import br.ufrn.sigaa.pesquisa.dominio.LinhaPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;
import br.ufrn.sigaa.projetos.dominio.CronogramaProjeto;
import br.ufrn.sigaa.projetos.dominio.FuncaoMembro;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Processador responsável pelo registro e submissão de projetos de pesquisa.
 * @author Victor Hugo
 * 
 */
public class ProcessadorProjetoPesquisa extends AbstractProcessador {

	/**
	 * Método responsável pela execução do processamento do projeto pesquisa
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		MovimentoProjetoPesquisa movProjeto = (MovimentoProjetoPesquisa) mov;

		ProjetoPesquisa projetoPesquisa = movProjeto.getProjeto();
		projetoPesquisa.getProjeto().setUsuarioLogado(movProjeto.getUsuarioLogado());

		Usuario usuario = (Usuario) mov.getUsuarioLogado();
		Comando comando = movProjeto.getCodMovimento();
		GenericDAO dao = getGenericDAO(mov);
		ProjetoDao projetoDao = getDAO(ProjetoDao.class, mov);
		
		try {
			// anula transient objects
			for (MembroProjeto membro : projetoPesquisa.getProjeto().getEquipe()) {
				if ( membro.getDocenteExterno() != null && isEmpty(membro.getDocenteExterno().getPessoa())) membro.setDocenteExterno(null);
				if ( membro.getDocenteExterno() != null && isEmpty(membro.getDocenteExterno().getServidor())) membro.getDocenteExterno().setServidor(null);
				if (isEmpty(membro.getServidor())) membro.setServidor(null);
			}
			/* Somente gravar o projeto de pesquisa */
			if (comando.equals(SigaaListaComando.GRAVAR_PROJETO_PESQUISA)) {

				validateBasico(projetoPesquisa);

				processarLinhaPesquisa(projetoPesquisa, dao);

				// Validar tamanho das atividades do cronograma, se já tiverem
				// sido informadas
				validateAtividadesCronograma(projetoPesquisa);
				// Salvar arquivo do projeto
				salvarArquivo(movProjeto);
				if (projetoPesquisa.getId() == 0) {
					// Definir a situação do projeto
					if (usuario.isUserInRole(SigaaPapeis.GESTOR_PESQUISA)
							&& projetoPesquisa.getSituacaoProjeto() != null
							&& projetoPesquisa.getSituacaoProjeto().getId() > 0) {
						ProjetoPesquisaHelper.alterarSituacaoProjeto(dao,
								projetoPesquisa.getSituacaoProjeto().getId(), projetoPesquisa);
					} else {
						ProjetoPesquisaHelper.alterarSituacaoProjeto(dao,
								TipoSituacaoProjeto.GRAVADO, projetoPesquisa);
					}

					projetoPesquisa.setRegistroEntrada(movProjeto.getUsuarioLogado()
							.getRegistroEntrada());
					projetoPesquisa.setDataCadastro(new Date());
					// Definir a unidade do projeto
					projetoPesquisa.setUnidade(new Unidade( ((Usuario)movProjeto.getUsuarioLogado()).getVinculoAtivo().getUnidade().getId()));

					int ano = CalendarUtils.getAno(projetoPesquisa.getDataInicio());
					projetoPesquisa.setAno(ano);
					projetoPesquisa.setNumeroInstitucional( projetoDao.findNextNumeroInstitucional(ano) );
					
					// Criar a linha de pesquisa, caso o projeto não seja
					// interno, ou seja uma nova linha de pesquisa
					if (projetoPesquisa.getLinhaPesquisa().getGrupoPesquisa() != null
							&& projetoPesquisa.getLinhaPesquisa().getGrupoPesquisa()
									.getId() <= 0) {
						projetoPesquisa.getLinhaPesquisa().setGrupoPesquisa(null);
					}

					//Verifica se já foi definido o coordenador se não foi definido o usuário que está cadastrado vira o coordenador.
					if ( projetoPesquisa.getMembrosProjeto().isEmpty() ) {
						
						Usuario us = (Usuario) movProjeto.getUsuarioLogado();
						
						MembroProjeto mp = new MembroProjeto();
						mp.setAtivo(true);
						mp.setServidor( us.getServidorAtivo() );
						mp.setDocenteExterno( us.getDocenteExternoAtivo() );
						mp.setPessoa( us.getServidorAtivo() != null ? us.getServidorAtivo().getPessoa() : us.getDocenteExternoAtivo().getPessoa() );
						mp.setChDedicada( ProjetoPesquisaValidator.LIMITE_CH_DEDICADA );
						mp.setDataInicio( projetoPesquisa.getProjeto().getDataInicio() );
						mp.setDataFim( projetoPesquisa.getProjeto().getDataFim() );
						mp.setCategoriaMembro( projetoDao.findByPrimaryKey(CategoriaMembro.DOCENTE, CategoriaMembro.class) );
						mp.setProjeto( projetoPesquisa.getProjeto() );
						mp.setFuncaoMembro( projetoDao.findByPrimaryKey(FuncaoMembro.COORDENADOR, FuncaoMembro.class) );
						projetoPesquisa.getProjeto().setCoordenador(mp);
						projetoPesquisa.getMembrosProjeto().add(mp);
					}
					
					// Verificar se já foram definidos os membros do projeto
					for (MembroProjeto membro : projetoPesquisa.getMembrosProjeto()) {
						ProjetoPesquisaValidator
								.prepararDadosColaborador(membro);
					}

					if (!projetoPesquisa.isInterno()) {
						projetoPesquisa.setEdital(null);
						projetoPesquisa.getProjeto().setMedia(10.0);
					}

					projetoPesquisa.getProjeto().setCoordenador( projetoPesquisa.getCoordenacao() );
					projetoPesquisa.setCoordenador( projetoPesquisa.getCoordenacao().getServidor() );
					persistirProjeto(projetoPesquisa, dao);
				} else {
					removerDadosAntigos(projetoPesquisa, dao);
					dao.clearSession();
					projetoPesquisa.getProjeto().setDataCadastro(new Date());
					if ( projetoPesquisa.getSituacaoProjeto().getId() == TipoSituacaoProjeto.GRAVADO ){
						getAnoProjeto(projetoPesquisa);
						// Definir o ano e o número institucional do projeto
						if ( projetoPesquisa.getCodigo() != null && projetoPesquisa.getCodigo().getAno() != projetoPesquisa.getAno() ) {
							projetoPesquisa.setAno( projetoPesquisa.getCodigo().getAno() );
							projetoPesquisa.setNumeroInstitucional( projetoDao.findNextNumeroInstitucional( projetoPesquisa.getCodigo().getAno() ) );
						}
					}
					if(projetoPesquisa.getCoordenacao() == null)
						projetoPesquisa.getProjeto().setCoordenador(null);
					dao.update(projetoPesquisa.getProjeto());
					if(projetoPesquisa.getCoordenacao() != null) {
						dao.updateField(Projeto.class, projetoPesquisa.getProjeto().getId(), "coordenador.id", projetoPesquisa.getCoordenacao().getId());
						projetoPesquisa.setCoordenador( projetoPesquisa.getCoordenacao().getServidor() );
					}
					if (projetoPesquisa.getLinhaPesquisa().getGrupoPesquisa() != null && projetoPesquisa.getLinhaPesquisa().getGrupoPesquisa().getId() == 0)
						projetoPesquisa.getLinhaPesquisa().setGrupoPesquisa(null);
					dao.update(projetoPesquisa);					
				}

				/* Gravar e enviar o projeto de pesquisa */
			} else if (comando.equals(SigaaListaComando.ENVIAR_PROJETO_PESQUISA)) {

				// Validar todos os dados antes de submeter
				validate(movProjeto);

				// Gerar Código do Projeto
				projetoPesquisa.setCodigo(ProjetoPesquisaHelper
						.gerarCodigoProjeto(projetoPesquisa));
				
				// Definir o ano e o número institucional do projeto
				if (projetoPesquisa.getAno() == null || projetoPesquisa.getCodigo().getAno() != projetoPesquisa.getAno() ) {
					projetoPesquisa.setAno( projetoPesquisa.getCodigo().getAno() );
					projetoPesquisa.setNumeroInstitucional( projetoDao.findNextNumeroInstitucional( projetoPesquisa.getCodigo().getAno() ) );
				}
				
				// Definir a situação do projeto
				if (projetoPesquisa.isInterno()) {
					ProjetoPesquisaHelper.alterarSituacaoProjeto(dao,
							TipoSituacaoProjeto.SUBMETIDO, projetoPesquisa);
				} else {
					ProjetoPesquisaHelper.alterarSituacaoProjeto(dao,
							TipoSituacaoProjeto.CADASTRADO, projetoPesquisa);
					ProjetoPesquisaHelper.alterarSituacaoProjeto(dao,
							TipoSituacaoProjeto.EM_ANDAMENTO, projetoPesquisa);
				}

				// Criar a linha de pesquisa, caso o projeto não seja interno,
				// ou seja uma nova linha de pesquisa
				if (projetoPesquisa.getLinhaPesquisa().getId() <= 0) {
					dao.create(projetoPesquisa.getLinhaPesquisa());
				}

				processarLinhaPesquisa(projetoPesquisa, dao);
				// Salvar arquivo do projeto
				salvarArquivo(movProjeto);
				if (projetoPesquisa.getId() == 0) {
					projetoPesquisa.setRegistroEntrada(movProjeto.getUsuarioLogado()
							.getRegistroEntrada());
					projetoPesquisa.setDataCadastro(new Date());

					// Definir a unidade do projeto
					projetoPesquisa.setUnidade(new Unidade( ((Usuario) movProjeto.getUsuarioLogado()).getVinculoAtivo().getUnidade().getId()));

					if (!projetoPesquisa.isInterno()) {
						projetoPesquisa.setEdital(null);
						projetoPesquisa.getProjeto().setMedia(10.0);
					}
					
					projetoPesquisa.getProjeto().setCoordenador( projetoPesquisa.getCoordenacao() );
					projetoPesquisa.setCoordenador( projetoPesquisa.getCoordenacao().getServidor() );
					persistirProjeto(projetoPesquisa, dao);
					EnvioMensagemHelper.comunicarMembrosProjeto(projetoPesquisa);
				} else {
					removerDadosAntigos(projetoPesquisa, dao);
					dao.clearSession();
					
					getAnoProjeto(projetoPesquisa);
					
					dao.updateField(Projeto.class, projetoPesquisa.getProjeto().getId(), "dataCadastro", new Date());
					projetoPesquisa.getProjeto().setCoordenador( projetoPesquisa.getCoordenacao() );
					dao.update(projetoPesquisa.getProjeto());
					projetoPesquisa.setCoordenador( projetoPesquisa.getCoordenacao().getServidor() );
					dao.update(projetoPesquisa);
				}

			} else if (comando.equals(SigaaListaComando.REMOVER_PROJETO_PESQUISA)) {
				projetoPesquisa = dao.findByPrimaryKey(projetoPesquisa.getId(),
						ProjetoPesquisa.class);

				// projeto não isolado não deve apagar a linha de pesquisa
				if (projetoPesquisa.getLinhaPesquisa() != null
						&& projetoPesquisa.getLinhaPesquisa().getGrupoPesquisa() != null) {
					projetoPesquisa.setLinhaPesquisa(null);
				}

				if ( projetoPesquisa.haPlanoAtivo() ) {
					NegocioException e = new NegocioException();
					e.addErro(" Existem planos de trabalho vinculados a este projeto. "
						+ " Por favor, remova os planos de trabalho antes de remover o projeto ");
					throw e;
				}
				
				projetoPesquisa.getProjeto().setUsuarioLogado(movProjeto.getUsuarioLogado());
				ProjetoPesquisaHelper.alterarSituacaoProjeto(dao,
						TipoSituacaoProjeto.EXCLUIDO, projetoPesquisa);				

				dao.update(projetoPesquisa);
			} else if (comando.equals(SigaaListaComando.ALTERAR_PROJETO_PESQUISA)) {
				processarLinhaPesquisa(projetoPesquisa, dao);

				validate(movProjeto);
				removerDadosAntigos(projetoPesquisa, dao);
				dao.update(projetoPesquisa);

			} else if (comando.equals(SigaaListaComando.RENOVAR_PROJETO_PESQUISA)) {

				validate(movProjeto);

				// setando situação do projeto antigo para RENOVADO
				projetoPesquisa.setNumeroRenovacoes(projetoPesquisa.getNumeroRenovacoes() + 1);
				ProjetoPesquisaHelper.alterarSituacaoProjeto(dao,
						TipoSituacaoProjeto.RENOVADO, projetoPesquisa);
				removerDadosAntigos(projetoPesquisa, dao);
				dao.update(projetoPesquisa.getProjeto());
				dao.update(projetoPesquisa);
				
				
				if (!ValidatorUtil.isEmpty(projetoPesquisa.getProjeto().getEquipe())) {
	    			for (MembroProjeto mp : projetoPesquisa.getProjeto().getEquipe()) {
	    				if(mp.getFuncaoMembro().getId() == FuncaoMembro.COORDENADOR) {
	    					dao.updateField(Projeto.class, mp.getProjeto().getId(), "coordenador.id", mp.getId());
	    				}
	    			}
	    		}
			}

			projetoPesquisa.getProjeto().setRegistroEntrada( dao.findAndFetch(
					projetoPesquisa.getProjeto().getId(), Projeto.class, "usuario", "pessoa").getRegistroEntrada() );

			if ( projetoPesquisa.getProjeto().getRegistroEntrada() == null )
				projetoPesquisa.getProjeto().setRegistroEntrada( usuario.getRegistroEntrada() );
			
			return projetoPesquisa;
		} finally {
			dao.close();
			projetoDao.close();
		}
	}

	/**
	 * Seta o ano correto para o projeto de Pesquisa e para o Projeto Base.
	 * 
	 * @param projetoPesquisa
	 */
	private void getAnoProjeto(ProjetoPesquisa projetoPesquisa) {
		// Definir a situação do projeto
		if ( projetoPesquisa.isInterno() ) {
			projetoPesquisa.setAno( projetoPesquisa.getEdital().getAno() );
			projetoPesquisa.getProjeto().setAno( projetoPesquisa.getEdital().getAno() );
			projetoPesquisa.getCodigo().setAno( projetoPesquisa.getEdital().getAno() );
		} else if ( CalendarUtils.getAno(projetoPesquisa.getDataInicio()) != CalendarUtils.getAnoAtual() || !projetoPesquisa.isInterno() ) {
			projetoPesquisa.setAno( CalendarUtils.getAno(projetoPesquisa.getDataInicio()) );
			projetoPesquisa.getProjeto().setAno( CalendarUtils.getAno(projetoPesquisa.getDataInicio()) );
			projetoPesquisa.getCodigo().setAno( CalendarUtils.getAno(projetoPesquisa.getDataInicio()) );
		}
	}

	/**
	 * Efetua algumas verificações sobre a linha de pesquisa do projeto antes de persistir,
	 * como se já existe uma cadastrada ou se o grupo de pesquisa foi alterado.
	 * 
	 * @param projetoPesquisa
	 * @param dao
	 * @throws DAOException
	 */
	private void processarLinhaPesquisa(ProjetoPesquisa projetoPesquisa,
			GenericDAO dao) throws DAOException {
		// Verificar se a linha já existe cadastrada
		LinhaPesquisa linha = projetoPesquisa.getLinhaPesquisa();
		if (linha.getId() <= 0) {
			dao.create(linha);
		}
		// Verificar se o grupo de pesquisa foi alterado, neste caso,
		// criar uma nova linha de pesquisa
		else if (linha.getGrupoPesquisa() != null) {
			LinhaPesquisa linhaPersistida = dao.findByPrimaryKey(linha
					.getId(), LinhaPesquisa.class);
			if (linhaPersistida.getGrupoPesquisa() != null
					&& linhaPersistida.getGrupoPesquisa().getId() != linha
							.getGrupoPesquisa().getId()) {
				linha.setId(0);
				dao.create(linha);
			}
			dao.detach(linhaPersistida);
		}
	}

	/**
	 * Método auxiliar utilizado para persistir na base de dados um projeto de pesquisa
	 * 
	 * @param projetoPesquisa
	 * @param dao
	 * @throws DAOException
	 */
	private void persistirProjeto(ProjetoPesquisa projetoPesquisa,
			GenericDAO dao) throws DAOException {
		if(projetoPesquisa.getProjeto().getClassificacaoFinanciadora() != null
				&& projetoPesquisa.getProjeto().getClassificacaoFinanciadora().getId() <= 0)
			projetoPesquisa.getProjeto().setClassificacaoFinanciadora(null);
		
		
		if(!projetoPesquisa.getProjeto().isPesquisa()){
			projetoPesquisa.getProjeto().setAreaConhecimentoCnpq(null);
		}
		
		if(projetoPesquisa.getCoordenador() == null){
			if(projetoPesquisa.getProjeto().isPesquisa() && projetoPesquisa.getProjeto().getCoordenador() != null &&
					projetoPesquisa.getProjeto().getCoordenador().getServidor() != null)
				projetoPesquisa.setCoordenador(projetoPesquisa.getProjeto().getCoordenador().getServidor());
		}
		
		if(!projetoPesquisa.isInterno()){
			projetoPesquisa.getProjeto().setEdital(null);
		}else{
			if(projetoPesquisa.getProjeto().getEdital() != null
					&& projetoPesquisa.getProjeto().getEdital().getId() <= 0)
				projetoPesquisa.getProjeto().setEdital(projetoPesquisa.getEdital().getEdital());
		}
		projetoPesquisa.getProjeto().setUnidadeOrcamentaria(null);
		dao.refresh(projetoPesquisa.getUnidade());
		dao.createOrUpdate(projetoPesquisa.getProjeto());		
		dao.create(projetoPesquisa);
	}

	/**
	 * Salvar arquivo do projeto na base de arquivos
	 * 
	 * @param movProjeto
	 * @throws ArqException
	 */
	private void salvarArquivo(MovimentoProjetoPesquisa movProjeto)
			throws ArqException {
		ProjetoPesquisa projeto = movProjeto.getProjeto();

		// Verificar se existe um arquivo a salvar
		if (movProjeto.getNomeArquivo() != null && !movProjeto.getNomeArquivo().equals("")) {
			try {
				if (projeto.getProjeto().getIdArquivo() == null
						|| projeto.getProjeto().getIdArquivo() == 0) {
					// Buscar um id para o arquivo
					projeto.getProjeto().setIdArquivo(EnvioArquivoHelper.getNextIdArquivo());
				}

				// Gravar arquivo do projeto
				EnvioArquivoHelper.inserirArquivo(projeto.getProjeto().getIdArquivo(),
						movProjeto.getDadosArquivo(), movProjeto
								.getContentType(), movProjeto.getNomeArquivo());
			} catch (Exception e) {
				throw new ArqException(e);
			}
		}
	}

	/**
	 * Método auxiliar utilizado para remover dados antigos do projeto de pesquisa 
	 * antes de persisti-lo na base de dados
	 * 
	 * @param projeto
	 * @param dao
	 * @throws DAOException
	 */
	private void removerDadosAntigos(ProjetoPesquisa projeto, GenericDAO dao) throws DAOException {

		ProjetoPesquisa projetoAntigo = dao.findByPrimaryKey(projeto.getId(), ProjetoPesquisa.class);
		if(projetoAntigo.getMembrosDocentes() != null) 
			projetoAntigo.getMembrosDocentes().size();
		if(projetoAntigo.getFinanciamentosProjetoPesq() != null) 
			projetoAntigo.getFinanciamentosProjetoPesq().size();
		if(projetoAntigo.getCronogramas() != null)
			projetoAntigo.getCronogramas().size();
		dao.detach(projetoAntigo);
		dao.detach(projetoAntigo.getProjeto());

		// Remover financiamentos do projeto
		for (FinanciamentoProjetoPesq financiamentoAntigo : projetoAntigo.getFinanciamentosProjetoPesq() ) {
			boolean achou = false;
			for (FinanciamentoProjetoPesq financiamento : projeto.getFinanciamentosProjetoPesq()) {
				if (financiamentoAntigo.getId() == financiamento.getId()) {
					achou = true;
				}
			}
			if (!achou) {
				financiamentoAntigo.setProjetoPesquisa(null);
				dao.remove(financiamentoAntigo);
			}
		}

		// Remover membros do projeto
		for (MembroProjeto membroAntigo : projetoAntigo.getMembrosProjeto() ) {
			boolean achou = false;
			for (MembroProjeto membro : projeto.getMembrosProjeto()) {
				if (membroAntigo.getId() == membro.getId()) {
					achou = true;
				}
			}
			if (!achou) {
				membroAntigo.setDocenteExterno(null);
				if (membroAntigo.isCoordenador()) {					
					dao.updateField(ProjetoPesquisa.class, projeto.getId(), "coordenador.id", null);
					dao.updateField(Projeto.class, projeto.getProjeto().getId(), "coordenador.id", null);
					projeto.setCoordenador(null);
					projeto.getProjeto().setCoordenador(null);
				}
				dao.remove(membroAntigo);
			}
		}

		if( projeto.getCronogramas() != null 
				&& !projeto.getCronogramas().isEmpty()
				&& projeto.getCronogramas().get(0).getId() == 0 ) {
		
		// Remover cronogramas do projeto
		for (CronogramaProjeto cronogramaAntigo : projetoAntigo.getCronogramas() ) {
			cronogramaAntigo.setProjeto(null);
			dao.remove(cronogramaAntigo);
		}
		
		}

	}

	/**
	 * Método responsável pelo cadastro de um novo processamento de projeto de pesquisa
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoProjetoPesquisa movProjeto = (MovimentoProjetoPesquisa) mov;
		ProjetoPesquisa projetoPesquisa = movProjeto.getProjeto();

		ListaMensagens erros = new ListaMensagens();

		// validação para caso o mov seja renovação de projeto
		if (movProjeto.getCodMovimento() == SigaaListaComando.RENOVAR_PROJETO_PESQUISA)
			ProjetoPesquisaValidator.validaRenovacao(projetoPesquisa, erros);

		ProjetoPesquisaValidator.validaDadosBasicos(projetoPesquisa, null, erros);
		ProjetoPesquisaValidator.validaDetalhes(projetoPesquisa, null, erros,
				(Usuario) mov.getUsuarioLogado());
		if(!projetoPesquisa.getProjeto().isPesquisa()){
			ProjetoPesquisaValidator.validaDocentes(projetoPesquisa, null, erros);
			ProjetoPesquisaValidator.validaCronograma(projetoPesquisa, erros);
		}

		checkValidation(erros);

	}

	/**
	 * Validações básicas para apenas gravar um projeto
	 * 
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public void validateBasico(ProjetoPesquisa projeto)
			throws NegocioException, ArqException {
		ListaMensagens erros = new ListaMensagens();
		ProjetoPesquisaValidator.validaDadosBasicos(projeto, null, erros);
		checkValidation(erros);
	}

	/**
	 * Validações sobre o cronograma do projeto
	 * 
	 * @param projeto
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public void validateAtividadesCronograma(ProjetoPesquisa projeto)
			throws NegocioException, ArqException {
		if (projeto.getCronogramas().size() > 0) {
			ListaMensagens erros = new ListaMensagens();
			ProjetoPesquisaValidator.validaCronograma(projeto, erros);
			checkValidation(erros);
		}
	}

}
