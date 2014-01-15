package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

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
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoApoioGrupoPesquisaDao;
import br.ufrn.sigaa.arq.dao.projetos.ProjetoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.GrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoApoioGrupoPesquisa;
import br.ufrn.sigaa.projetos.dominio.Edital;
import br.ufrn.sigaa.projetos.dominio.OrcamentoDetalhado;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Processador responsável pelas operações sobre os Projetos de Apoio aos Grupos de Pesquisa.
 * 
 * @author Jean Guerethes
 */
public class ProcessadorProjetoApoioGrupoPesquisa extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		MovimentoCadastro movc = (MovimentoCadastro) mov;

		ProjetoApoioGrupoPesquisa projetoApoioGrupoPesquisa = movc.getObjMovimentado();
		GenericDAO dao = getGenericDAO(mov);
		Comando comando = mov.getCodMovimento();
		
		if ( comando.equals(SigaaListaComando.CADASTRAR_PROJETO_APOIO_GRUPO_PESQUISA) ) {
			validate(mov);
			projetoApoioGrupoPesquisa.setProjeto( cadastrarProjeto(movc, dao) );
			persist(projetoApoioGrupoPesquisa, dao);
		}

		if ( comando.equals(SigaaListaComando.ALTERAR_PROJETO_APOIO_GRUPO_PESQUISA) ) {
			validate(mov);
			alterarOrcamentoProjeto(movc, dao);
			persist(projetoApoioGrupoPesquisa.getProjeto(), dao);
			persist(projetoApoioGrupoPesquisa, dao);
		}
		
		if ( comando.equals(SigaaListaComando.REMOVER_PROJETO_APOIO_GRUPO_PESQUISA) ) {
			dao.updateField(Projeto.class, projetoApoioGrupoPesquisa.getProjeto().getId(), "situacaoProjeto.id", TipoSituacaoProjeto.PROJETO_BASE_REMOVIDO);
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
		ProjetoApoioGrupoPesquisa projetoApoio = movc.getObjMovimentado();
		
		projetoApoio.getProjeto().setApoioGrupoPesquisa(true);
		projetoApoio.getProjeto().setInterno(true);
		projetoApoio.getProjeto().setPesquisa(true);
		projetoApoio.getProjeto().setRenovacao(false);
		projetoApoio.getProjeto().setTipoProjeto( new TipoProjeto(TipoProjeto.PESQUISA));
		projetoApoio.getProjeto().setClassificacaoFinanciadora(null);
		projetoApoio.getProjeto().setUnidadeOrcamentaria(null);
		projetoApoio.getProjeto().setRegistroEntrada( movc.getRegistroEntrada() );
		projetoApoio.getProjeto().setAno( CalendarUtils.getAnoAtual() );
		projetoApoio.getProjeto().setNumeroInstitucional( getDAO(ProjetoDao.class, movc).findNextNumeroInstitucional( projetoApoio.getProjeto().getAno()) );
		projetoApoio.getProjeto().setSituacaoProjeto( new TipoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_SUBMETIDO) );
		projetoApoio.getProjeto().setUnidade( new Unidade( ((Usuario)movc.getUsuarioLogado()).getVinculoAtivo().getUnidade()) );		

		projetoApoio.getProjeto().setEdital( new Edital() );
		EditalPesquisa editalPesquisa = getDAO(GenericDAOImpl.class, movc).findAndFetch(projetoApoio.getEditalPesquisa().getId(), EditalPesquisa.class, "edital");
		projetoApoio.getProjeto().setEdital( editalPesquisa.getEdital() );
		
		GrupoPesquisa grupo = getDAO(GenericDAOImpl.class, movc).findAndFetch(projetoApoio.getGrupoPesquisa().getId(), GrupoPesquisa.class, "areaConhecimentoCnpq");
		projetoApoio.getProjeto().setAreaConhecimentoCnpq( grupo.getAreaConhecimentoCnpq() );

		persist( projetoApoio.getProjeto(), dao );

		for (OrcamentoDetalhado orcamento : projetoApoio.getProjeto().getOrcamento())
			persist(orcamento, dao);
		
		return projetoApoio.getProjeto();
	}

	private Projeto carregarInformacoesBasicas(MovimentoCadastro movc, GenericDAO dao) throws DAOException{
		ProjetoApoioGrupoPesquisa projetoApoio = movc.getObjMovimentado();

		projetoApoio.getProjeto().setEdital( new Edital() );
		EditalPesquisa editalPesquisa = getDAO(GenericDAOImpl.class, movc).findAndFetch(projetoApoio.getEditalPesquisa().getId(), EditalPesquisa.class, "edital");
		projetoApoio.getProjeto().setEdital( editalPesquisa.getEdital() );
		
		GrupoPesquisa grupo = getDAO(GenericDAOImpl.class, movc).findAndFetch(projetoApoio.getGrupoPesquisa().getId(), GrupoPesquisa.class, "areaConhecimentoCnpq");
		projetoApoio.getProjeto().setAreaConhecimentoCnpq( grupo.getAreaConhecimentoCnpq() );
		
		return projetoApoio.getProjeto();
	}
	
	private Projeto alterarOrcamentoProjeto(MovimentoCadastro movc, GenericDAO dao) throws DAOException{
		ProjetoApoioGrupoPesquisa projetoApoio = movc.getObjMovimentado();
		ProjetoApoioGrupoPesquisa projetoApoioOrig = dao.findByPrimaryKey(projetoApoio.getId(), ProjetoApoioGrupoPesquisa.class);
		
		Collection<OrcamentoDetalhado> removerOrcamento = new ArrayList<OrcamentoDetalhado>();
		
		for (OrcamentoDetalhado orcamento : projetoApoio.getProjeto().getOrcamento()) {
			if ( !projetoApoioOrig.getProjeto().getOrcamento().contains(orcamento) ) {
				removerOrcamento.add(orcamento);
			}
		}

		for (OrcamentoDetalhado orcamento : projetoApoioOrig.getProjeto().getOrcamento()) {
			if ( !projetoApoio.getProjeto().getOrcamento().contains(orcamento) ) {
				removerOrcamento.add(orcamento);
			}
		}
		
		for (OrcamentoDetalhado orcamentoDetalhado : removerOrcamento)
			remove(orcamentoDetalhado, dao);

		for (OrcamentoDetalhado orcamento : projetoApoio.getProjeto().getOrcamento())
			if ( !projetoApoioOrig.getProjeto().getOrcamento().contains(orcamento) ) {
				persist(orcamento, dao);
			}
		
		dao.detach(projetoApoioOrig);
		carregarInformacoesBasicas(movc, dao);
		return projetoApoio.getProjeto();
	}
	
	/**
	 * Validação as principais informações do Projeto de Apoio a Grupo de Pequisa
	 * 
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		ListaMensagens lista = new ListaMensagens();
		MovimentoCadastro movc = (MovimentoCadastro) mov;
		ProjetoApoioGrupoPesquisa projetoApoioGrupoPesquisa = (ProjetoApoioGrupoPesquisa) movc.getObjMovimentado();
		
		lista.addAll( projetoApoioGrupoPesquisa.validate() );
		
		ProjetoApoioGrupoPesquisaDao dao = getDAO(ProjetoApoioGrupoPesquisaDao.class, mov);
		try {
			Collection<Integer> haProjetoCadastrado = 
					dao.haProjetoCadastrado(projetoApoioGrupoPesquisa.getEditalPesquisa().getId(), projetoApoioGrupoPesquisa.getGrupoPesquisa().getId());
			
			if ( !haProjetoCadastrado.isEmpty() && !haProjetoCadastrado.contains(projetoApoioGrupoPesquisa.getId()) )
				lista.addErro("Já existe um Projeto Apoio a Grupo de Pesquisa cadastrado para esse Edital de Pesquisa.");
			
		} finally {
			dao.close();
		}
		
		checkValidation(lista);
	}

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
	 * Remove o objetivo passado como parâmetro.
	 * @param obj
	 * @throws DAOException
	 */
	private void remove(PersistDB obj, GenericDAO dao) throws DAOException {
		dao.remove(obj);
	}

}