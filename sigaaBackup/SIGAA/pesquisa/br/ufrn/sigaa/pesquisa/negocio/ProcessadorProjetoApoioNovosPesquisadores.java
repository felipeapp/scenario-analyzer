package br.ufrn.sigaa.pesquisa.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.projetos.ProjetoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.GrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoApoioNovosPesquisadores;
import br.ufrn.sigaa.pesquisa.dominio.TipoPassoProjetoNovoPesquisador;
import br.ufrn.sigaa.projetos.dominio.Edital;
import br.ufrn.sigaa.projetos.dominio.OrcamentoDetalhado;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;
import br.ufrn.sigaa.projetos.negocio.CronogramaProjetoHelper;

public class ProcessadorProjetoApoioNovosPesquisadores extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		MovimentoCadastro movc = (MovimentoCadastro) mov;

		ProjetoApoioNovosPesquisadores projetoApoioGrupoPesquisa = movc.getObjMovimentado();
		GenericDAO dao = getGenericDAO(mov);
		Comando comando = mov.getCodMovimento();
		
		try {
			if ( comando.equals(SigaaListaComando.CADASTRAR_PROJETO_APOIO_NOVOS_PESQUISADORES) ) {
				dao.updateField(Projeto.class, projetoApoioGrupoPesquisa.getProjeto().getId(), "situacaoProjeto.id", TipoSituacaoProjeto.PROJETO_BASE_SUBMETIDO);
			}
			
			if ( comando.equals(SigaaListaComando.REMOVER_PROJETO_APOIO_NOVOS_PESQUISADORES) ) {
				dao.updateField(Projeto.class, projetoApoioGrupoPesquisa.getProjeto().getId(), "situacaoProjeto.id", TipoSituacaoProjeto.PROJETO_BASE_REMOVIDO);
			}
	
			if ( comando.equals(SigaaListaComando.ENVIAR_PROJETO_APOIO_NOVOS_PESQUISADORES) ) {
				validate(mov);
				projetoApoioGrupoPesquisa.setProjeto( cadastrarProjeto(movc, dao) );
				if ( isEmpty( projetoApoioGrupoPesquisa.getGrupoPesquisa() ) )
					projetoApoioGrupoPesquisa.setGrupoPesquisa(null);
				persist(projetoApoioGrupoPesquisa, dao);
			}
		
		} finally {
			dao.close();
		}

		return projetoApoioGrupoPesquisa;
	}

	/**
	 * Cadastrar ou atualiza do projeto ao qual o Projeto de Apoio a Grupo de Pesquisa está vinculado.
	 * 
	 * @param projeto
	 * @param dao
	 * @throws RemoteException 
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	private Projeto cadastrarProjeto(MovimentoCadastro movc, GenericDAO dao) throws NegocioException, ArqException, RemoteException{
		ProjetoApoioNovosPesquisadores projetoApoio = movc.getObjMovimentado();
		
		projetoApoio.getProjeto().setApoioNovosPesquisadores(true);
		projetoApoio.getProjeto().setInterno(true);
		projetoApoio.getProjeto().setPesquisa(true);
		projetoApoio.getProjeto().setTipoProjeto( new TipoProjeto(TipoProjeto.PESQUISA));
		projetoApoio.getProjeto().setAno( CalendarUtils.getAnoAtual() );
		projetoApoio.getProjeto().setRegistroEntrada( movc.getRegistroEntrada() );
		projetoApoio.getProjeto().setSituacaoProjeto( new TipoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_CADASTRO_EM_ANDAMENTO) );
		projetoApoio.getProjeto().setNumeroInstitucional( getDAO(ProjetoDao.class, movc).findNextNumeroInstitucional( projetoApoio.getProjeto().getAno()) );
		projetoApoio.getProjeto().setRenovacao(false);
		projetoApoio.getProjeto().setClassificacaoFinanciadora(null);
		projetoApoio.getProjeto().setUnidadeOrcamentaria(null);
		projetoApoio.getProjeto().setUnidade( new Unidade( ((Usuario)movc.getUsuarioLogado()).getVinculoAtivo().getUnidade()) );
		
		projetoApoio.getProjeto().setEdital( new Edital() );
		EditalPesquisa editalPesquisa = getDAO(GenericDAOImpl.class, movc).findAndFetch(projetoApoio.getEditalPesquisa().getId(), EditalPesquisa.class, "edital");
		projetoApoio.getProjeto().setEdital( editalPesquisa.getEdital() );
		projetoApoio.getProjeto().setDataInicio( editalPesquisa.getInicioExecucaoProjetos() );
		projetoApoio.getProjeto().setDataFim( editalPesquisa.getFimExecucaoProjetos() );
		
		if ( !isEmpty(projetoApoio.getGrupoPesquisa()) ) {
			GrupoPesquisa grupo = getDAO(GenericDAOImpl.class, movc).findAndFetch(projetoApoio.getGrupoPesquisa().getId(), GrupoPesquisa.class, "areaConhecimentoCnpq");
			projetoApoio.getProjeto().setAreaConhecimentoCnpq( !isEmpty(grupo) ? grupo.getAreaConhecimentoCnpq() : null );
		} else {
			projetoApoio.getProjeto().setAreaConhecimentoCnpq( null );
		}
	
		if ( projetoApoio.getTipoPassosProjeto().ordinal() == TipoPassoProjetoNovoPesquisador.TELA_CRONOGRAMA_PROJETO.ordinal() ) {
			projetoApoio.getProjeto().setCronograma( 
					CronogramaProjetoHelper.submeterCronogramaProjeto( 
							dao, projetoApoio.getProjeto(), projetoApoio.getProjeto().getCronograma() ) );
		}
		
		persist( projetoApoio.getProjeto(), dao );

		// Cadastro do Orçamento do Projeto
		if ( projetoApoio.getTipoPassosProjeto().ordinal() == TipoPassoProjetoNovoPesquisador.TELA_ORCAMENTO_PROJETO.ordinal() ) {
			for (OrcamentoDetalhado orcamento : projetoApoio.getProjeto().getOrcamento()) {
				persist(orcamento, dao);
			}
		}
		
		return projetoApoio.getProjeto();
	}

	/**
	 * Validação as principais informações do Projeto de Apoio a Novos Pesquisadores
	 * 
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {	}

	/**
	 * Atualiza ou cria um novo objeto no banco dependendo se ele possui ou não id
	 * @param obj
	 * @throws DAOException
	 */
	private void persist(PersistDB obj, GenericDAO dao) throws DAOException {
		if( obj.getId() > 0 )
			dao.update(obj);
		else
			dao.create(obj);
	}

	/**
	 * Remove um objeto no banco
	 * @param obj
	 * @throws DAOException
	 */
	private void remove(PersistDB obj, GenericDAO dao) throws DAOException {
		dao.remove(obj);
	}

}