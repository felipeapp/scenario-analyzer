/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 03/10/2006
 *
 */

package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.pesquisa.ConsultorDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoPesquisaDao;
import br.ufrn.sigaa.arq.util.EnvioMensagemHelper;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.pesquisa.dominio.AvaliacaoProjeto;
import br.ufrn.sigaa.pesquisa.dominio.Consultor;
import br.ufrn.sigaa.pesquisa.dominio.ItemAvaliacao;
import br.ufrn.sigaa.pesquisa.dominio.NotaItem;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Processador respons�vel pela distribui��o de projetos de pesquisa a consultores
 *
 * @author ilueny santos
 * @author Victor Hugo
 * @author Ricardo Wendell
 *
 */
public class ProcessadorDistribuirProjetoPesquisa extends AbstractProcessador {

	long init = System.currentTimeMillis();

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, mov);
		validate(mov);

		switch( ( (MovimentoDistribuicaoPesquisa)mov ).getAcao() ){
			case MovimentoDistribuicaoPesquisa.DISTRIBUIR_AUTOMATICAMENTE:
				return distribuirAutomaticamente( (MovimentoDistribuicaoPesquisa) mov );
			case MovimentoDistribuicaoPesquisa.DISTRIBUIR_POR_CENTROS:
				return distribuirPorCentro( (MovimentoDistribuicaoPesquisa)  mov);
			case MovimentoDistribuicaoPesquisa.DISTRIBUIR_MANUALMENTE:
				return distribuirManualmente( (MovimentoDistribuicaoPesquisa)  mov);
			case MovimentoDistribuicaoPesquisa.DISTRIBUIR_AUTOMATICAMENTE_ESPECIAIS:
				return distribuirAutomaticamenteEspeciais( (MovimentoDistribuicaoPesquisa) mov );
			default:
				throw new NegocioException("Tipo de distribui��o desconhecido!");
		}
	}

	/**
	 * Distribuir projetos automaticamente
	 * @param mov
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private Map< AreaConhecimentoCnpq, Collection<DistribuicaoProjetos> > distribuirAutomaticamente(MovimentoDistribuicaoPesquisa mov) throws ArqException, NegocioException{

		// Mapa contendo o resultado da distribui��o
		Map< AreaConhecimentoCnpq, Collection<DistribuicaoProjetos> >  retorno = new HashMap<AreaConhecimentoCnpq, Collection<DistribuicaoProjetos>>();

		// N�mero de consultores por projeto de pesquisa
		int consultoresPorProjeto = mov.getConsultoresPorItem();
		Integer idEdital = mov.getIdEdital();

		ProjetoPesquisaDao daoProjetos = getDAO(ProjetoPesquisaDao.class, mov);
		ConsultorDao daoConsultores = getDAO(ConsultorDao.class, mov);

		Connection con = null;
		try {
			con = Database.getInstance().getSigaaConnection();
			Statement st = con.createStatement();
			Statement stConsultor = con.createStatement();

			// Buscar projetos ainda n�o distribu�dos
			Collection<ProjetoPesquisa> projetosNaoDistribuidos = daoProjetos.findByNaoDistribuidosOuAvaliacaoInsuficiente(idEdital);

			// Para cada projeto, definir os consultores da avalia��o
			for( ProjetoPesquisa pp : projetosNaoDistribuidos ){

				Integer idProjetoPesquisa = pp.getId();
				Integer idProjeto = pp.getProjeto().getId();
				Integer idArea = Integer.valueOf( pp.getCodigoArea() );

				ProjetoPesquisa projetoItem = new ProjetoPesquisa(idProjetoPesquisa);
				AreaConhecimentoCnpq area = new AreaConhecimentoCnpq(idArea);

				//  Buscar consultores da �rea do projeto
				ArrayList<Consultor> consultores = (ArrayList<Consultor>) daoConsultores.findByAreaConhecimentoCnpq(
						area, consultoresPorProjeto );

				// Se n�o h� consultores para a area de conhecimento em quest�o abortar itera��o.
				if( consultores == null || consultores.size() == 0 )
					continue;

				DistribuicaoProjetos distribuicao = new DistribuicaoProjetos();
				for( Consultor c : consultores ){

					// Criar AvaliacaoProjeto
					AvaliacaoProjeto avaliacao = new AvaliacaoProjeto();
					avaliacao.setConsultor( c );
					avaliacao.setDataDistribuicao( new Date() );
					avaliacao.setProjetoPesquisa( projetoItem  );
					avaliacao.setTipoDistribuicao(AvaliacaoProjeto.TIPO_DISTRIBUICAO_AUTOMATICA);
					avaliacao.setSituacao( AvaliacaoProjeto.AGUARDANDO_AVALIACAO );

					// Atualizar quantidade de projetos distribu�dos para o consultor
					stConsultor.addBatch("UPDATE pesquisa.consultor " +
							" SET qtd_avaliacoes = qtd_avaliacoes + 1 " +
							" WHERE id_consultor = " + c.getId());

					// Persistir avalia��o
					daoProjetos.createNoFlush(avaliacao);

					if( distribuicao.getProjeto() == null )
						distribuicao.setProjeto(projetoItem);
					distribuicao.getConsultores().add(c);
				}

				// Atualizar projeto e seu hist�rico
				st.addBatch("UPDATE PROJETOS.PROJETO SET ID_TIPO_SITUACAO_PROJETO = " + TipoSituacaoProjeto.DISTRIBUIDO_AUTOMATICAMENTE +
						" WHERE ID_PROJETO = " + idProjeto);

				st.addBatch("INSERT INTO projetos.historico_situacao_projeto ( data, id_projeto, id_tipo_situacao_projeto, id_registro_entrada )  " +
						" values( '"+ new Date() +
						"', " + idProjeto +
						", " + TipoSituacaoProjeto.DISTRIBUIDO_AUTOMATICAMENTE +
						", " + mov.getUsuarioLogado().getRegistroEntrada().getId() + " )");

				// Ap�s definir todos os consultores, realizar as atualiza��es
				stConsultor.executeBatch();

				if( retorno.get( area  ) == null )
					retorno.put( area , new ArrayList<DistribuicaoProjetos>() );

				retorno.get(area).add(distribuicao);
			}

			st.executeBatch();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new ArqException("Erro na distribui��o autom�tica dos projetos de pesquisa!");
		} finally {
			long time = System.currentTimeMillis() - init;
			System.out.println( " \n\nTEMPO DE EXEUCAO: " + time + "\n\n"  );
			try {
				if(con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			daoProjetos.close();
			daoConsultores.close();
		}

		return retorno;
	}

	/**
	 * Distribuir projetos automaticamente para os consultores especiais
	 * @param mov
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private Map< AreaConhecimentoCnpq, Collection<DistribuicaoProjetos> > distribuirAutomaticamenteEspeciais(MovimentoDistribuicaoPesquisa mov) throws ArqException, NegocioException{

		// Mapa contendo o resultado da distribui��o
		Map< AreaConhecimentoCnpq, Collection<DistribuicaoProjetos> >  retorno = new HashMap<AreaConhecimentoCnpq, Collection<DistribuicaoProjetos>>();

		// N�mero de consultores por projeto de pesquisa
		int consultoresPorProjeto = mov.getConsultoresPorItem();
		Integer idEdital = mov.getIdEdital();

		ProjetoPesquisaDao daoProjetos = getDAO(ProjetoPesquisaDao.class, mov);
		ConsultorDao daoConsultores = getDAO(ConsultorDao.class, mov);

		Connection con = null;
		try {
			con = Database.getInstance().getSigaaConnection();
			Statement st = con.createStatement();

			// Buscar projetos ainda n�o distribu�dos OU com n�mero de avalia��es insuficiente
			Collection<ProjetoPesquisa> projetosNaoDistribuidosOuAvaliacaoInsuficiente = daoProjetos.findByNaoDistribuidosOuAvaliacaoInsuficiente(idEdital);

			// Para cada projeto, definir os consultores da avalia��o
			for( ProjetoPesquisa pp : projetosNaoDistribuidosOuAvaliacaoInsuficiente ){

				Integer idProjetoPesquisa = pp.getId();
				Integer idProjeto = pp.getProjeto().getId();
				Integer idArea = Integer.valueOf( pp.getCodigoArea() );

				ProjetoPesquisa projetoItem = new ProjetoPesquisa(idProjetoPesquisa);
				AreaConhecimentoCnpq area = new AreaConhecimentoCnpq(idArea);

				//  Buscar consultores da �rea do projeto
				ArrayList<Consultor> consultores = (ArrayList<Consultor>) daoConsultores.findByAreaConhecimentoCnpqEspeciais(
						area, consultoresPorProjeto );

				// Se n�o h� consultores para a area de conhecimento em quest�o abortar itera��o.
				if( consultores == null || consultores.size() == 0 )
					continue;

				DistribuicaoProjetos distribuicao = new DistribuicaoProjetos();
				for( Consultor c : consultores ){

					// Criar AvaliacaoProjeto
					AvaliacaoProjeto avaliacao = new AvaliacaoProjeto();
					avaliacao.setConsultor( c );
					avaliacao.setDataDistribuicao( new Date() );
					avaliacao.setProjetoPesquisa( projetoItem  );
					avaliacao.setTipoDistribuicao(AvaliacaoProjeto.TIPO_DISTRIBUICAO_AUTOMATICA);
					avaliacao.setSituacao( AvaliacaoProjeto.AGUARDANDO_AVALIACAO );


					// Persistir avalia��o
					daoProjetos.createNoFlush(avaliacao);

					if( distribuicao.getProjeto() == null )
						distribuicao.setProjeto(projetoItem);
					distribuicao.getConsultores().add(c);
				}

				// Atualizar projeto e seu hist�rico
				st.addBatch("UPDATE PROJETOS.PROJETO SET ID_TIPO_SITUACAO_PROJETO = " + TipoSituacaoProjeto.DISTRIBUIDO_AUTOMATICAMENTE +
						" WHERE ID_PROJETO = " + idProjeto);

				st.addBatch("INSERT INTO projetos.historico_situacao_projeto ( data, id_projeto, id_tipo_situacao_projeto, id_registro_entrada )  " +
						" values( '"+ new Date() +
						"', " + idProjeto +
						", " + TipoSituacaoProjeto.DISTRIBUIDO_AUTOMATICAMENTE +
						", " + mov.getUsuarioLogado().getRegistroEntrada().getId() + " )");


				if( retorno.get( area  ) == null )
					retorno.put( area , new ArrayList<DistribuicaoProjetos>() );

				retorno.get(area).add(distribuicao);
			}

			st.executeBatch();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new ArqException("Erro na distribui��o autom�tica dos projetos de pesquisa!");
		} finally {
			long time = System.currentTimeMillis() - init;
			System.out.println( " \n\nTEMPO DE EXEUCAO: " + time + "\n\n"  );
			daoProjetos.close();
			daoConsultores.close();
		}

		return retorno;
	}
	
	/**
	 * Distribuir projetos manualmente
	 * @param mov
	 * @return
	 */
	private Collection<ProjetoPesquisa> distribuirPorCentro(MovimentoDistribuicaoPesquisa mov)throws ArqException{

		Map<Consultor, AreaConhecimentoCnpq> mapaDistribuicao = mov.getMapaDistribuicaoManual();
		Collection<ProjetoPesquisa> projetosDistribuidos = new ArrayList<ProjetoPesquisa>();

		//ProjetoPesquisaDao dao = (ProjetoPesquisaDao) getDAO(SigaaConstantesDAO.PROJETO_PESQUISA,	mov);
		ProjetoPesquisaDao projetoDao = getDAO(ProjetoPesquisaDao.class, mov);
		try {

			// Buscar cole��o de itens de avalia��o ativos para cria��o da ficha de avalia��o
			Collection<ItemAvaliacao> itensAvaliacaoAtivos = projetoDao.findByExactField(ItemAvaliacao.class, "dataDesativacao", null);

			// Distribuir as avalia��es de acordo com o mapa de distribui��o
			for( Consultor cst : mapaDistribuicao.keySet()){

				AreaConhecimentoCnpq area = mapaDistribuicao.get(cst);

				Collection<ProjetoPesquisa> projetosDaArea = projetoDao.filter(true,
						null, null, null, null, null, null, null, null,
						null, null,
						area.getId(), null,
						null, null,
						TipoSituacaoProjeto.AVALIACAO_INSUFICIENTE, null, null, false);

				// Verificar se h� consultores para a �rea do projeto
				if ((projetosDaArea != null) && (projetosDaArea.size() > 0)){

					for( ProjetoPesquisa pp : projetosDaArea ){

						// Criar AvaliacaoProjeto
						AvaliacaoProjeto avaliacao = new AvaliacaoProjeto();
						avaliacao.setConsultor( cst );
						avaliacao.setDataDistribuicao( new Date() );

						// Inicializar formul�rio de avalia��o
						for( ItemAvaliacao item : itensAvaliacaoAtivos ){
							NotaItem nota = new NotaItem();
							nota.setNota( 0.0d );
							nota.setItemAvaliacao( item );
							avaliacao.addNotaItem(nota);
						}

						if (! pp.addAvaliacaoProjeto(avaliacao)){
							throw new ArqException("Erro ao adicionar Avalia��o!");
						}

						pp.getProjeto().setUsuarioLogado(mov.getUsuarioLogado());
						ProjetoPesquisaHelper.alterarSituacaoProjeto(projetoDao,
								TipoSituacaoProjeto.DISTRIBUIDO_MANUALMENTE,
								pp);

						projetoDao.update(pp);
						projetosDistribuidos.add(pp);
					}

				}

			}

		} catch (DAOException e) {
			throw new ArqException("Erro na distribui��o manual dos projetos de pesquisa!");
		} finally {
			projetoDao.close();
		}

		return projetosDistribuidos;
	}

	/**
	 * @param mov
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private Collection<ProjetoPesquisa> distribuirManualmente(MovimentoDistribuicaoPesquisa mov)throws ArqException, NegocioException{

		ProjetoPesquisaDao daoProjetos = getDAO(ProjetoPesquisaDao.class, mov);

		Consultor consultor = mov.getConsultor();
		Collection<ProjetoPesquisa> projetosPesquisa = mov.getProjetos();
		Collection<Projeto> projetos = new ArrayList<Projeto>();

		// Validar distribui��o
		ListaMensagens erros = new ListaMensagens();
		DistribuicaoProjetosValidator.validaDistribuicaoManual(consultor, projetosPesquisa, erros);
		if ( !erros.isEmpty() ) {
			NegocioException e = new NegocioException();
			e.setListaMensagens( erros );
			throw e;
		}

		// Preparar distribui��o
		Connection con = null;
		try {
			con = Database.getInstance().getSigaaConnection();
			Statement st = con.createStatement();

			// Buscar consultor
			consultor = daoProjetos.findByPrimaryKey(consultor.getId(), Consultor.class);

			for( ProjetoPesquisa projetoPesquisa : projetosPesquisa ){

				// Criar AvaliacaoProjeto
				AvaliacaoProjeto avaliacao = new AvaliacaoProjeto();
				avaliacao.setConsultor( consultor );
				avaliacao.setDataDistribuicao( new Date() );
				avaliacao.setProjetoPesquisa( projetoPesquisa  );
				avaliacao.setTipoDistribuicao( AvaliacaoProjeto.TIPO_DISTRIBUICAO_MANUAL );
				avaliacao.setSituacao( AvaliacaoProjeto.AGUARDANDO_AVALIACAO );

				// Atualizar projeto e seu hist�rico
				st.addBatch("UPDATE PROJETOS.PROJETO SET ID_TIPO_SITUACAO_PROJETO = " + TipoSituacaoProjeto.DISTRIBUIDO_MANUALMENTE +
						" WHERE ID_PROJETO = " + projetoPesquisa.getProjeto().getId());

				st.addBatch("INSERT INTO projetos.historico_situacao_projeto ( data, id_projeto, id_tipo_situacao_projeto, id_registro_entrada )  " +
						" values( '"+ new Date() +
							"', " + projetoPesquisa.getProjeto().getId() +
							", " + TipoSituacaoProjeto.DISTRIBUIDO_MANUALMENTE +
							", " + mov.getUsuarioLogado().getRegistroEntrada().getId() + " )");

				// Atualizar quantidade de projetos distribu�dos para o consultor
				st.addBatch("UPDATE pesquisa.consultor " +
						" SET qtd_avaliacoes = qtd_avaliacoes + 1 " +
						" WHERE id_consultor = " + consultor.getId());

				// Persistir avalia��o
				daoProjetos.createNoFlush(avaliacao);
				projetos.add(projetoPesquisa.getProjeto());
			}

			// Ap�s definir todas as avalia��es, realizar as atualiza��es
			st.executeBatch();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new NegocioException("Erro na distribui��o dos projetos de pesquisa!");
		} finally {
			daoProjetos.close();
		}
		EnvioMensagemHelper.notificarSubmissaoProjeto(projetos, EnvioMensagemHelper.PROJETO_PESQUISA, consultor);
		return null;
	}


	public void validate(Movimento mov) throws NegocioException, ArqException {
		int[] papeis = { SigaaPapeis.GESTOR_PESQUISA };
		checkRole(papeis, mov);
	}


}
