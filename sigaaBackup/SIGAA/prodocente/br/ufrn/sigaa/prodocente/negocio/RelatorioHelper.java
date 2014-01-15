/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '25/04/2007'
 *
 */
package br.ufrn.sigaa.prodocente.negocio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConfiguracaoAmbienteException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.integracao.interfaces.FormacaoAcademicaRemoteService;
import br.ufrn.sigaa.arq.dao.prodocente.AvaliacaoDocenteDao;
import br.ufrn.sigaa.arq.dao.prodocente.EmissaoRelatorioDao;
import br.ufrn.sigaa.arq.dao.prodocente.ItemRelatorioProdutividadeDao;
import br.ufrn.sigaa.pesquisa.dominio.SiglaUnidadePesquisa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AtividadeMapper;
import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.ProducaoMapper;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ClassificacaoRelatorio;
import br.ufrn.sigaa.prodocente.relatorios.dominio.EmissaoRelatorio;
import br.ufrn.sigaa.prodocente.relatorios.dominio.GrupoItem;
import br.ufrn.sigaa.prodocente.relatorios.dominio.GrupoRelatorioProdutividade;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ItemRelatorioProdutividade;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ProducaoIntelectual;
import br.ufrn.sigaa.prodocente.relatorios.dominio.RelatorioProdutividade;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * Classe utilizada no prodocente com os métodos para realizar as operações do relatório de um docente com sua respectiva pontuação
 * @author Eriquim
 *
 */
public class RelatorioHelper {


	/**
	 * Método que constrói o relatório baseado nos itens passados
	 *
	 * @return
	 * @throws ClassNotFoundException 
	 * @throws ArqException
	 */
	public static RelatorioProdutividade montarRelatorioProdutividade(RelatorioProdutividade relatorio, Servidor docente, int anoVigencia, FormacaoAcademicaRemoteService serviceFormacao) throws ArqException{


		AvaliacaoDocenteDao dao = new AvaliacaoDocenteDao();
		ItemRelatorioProdutividadeDao daoItem = new ItemRelatorioProdutividadeDao();
		
		try{
			relatorio = dao.findByPrimaryKey(relatorio.getId(), RelatorioProdutividade.class);
			
			// lista de itens de produção intelectual
			List<ItemRelatorioProdutividade> listaItensRelatorioProducao = daoItem.findByRelatorioProdutividade(relatorio.getId(), true);
			// lista de itens de outras atividades
			List<ItemRelatorioProdutividade> listaItensRelatorioAtividades = daoItem.findByRelatorioProdutividade(relatorio.getId(), false);
			//mapa de validade dos itens Map<id_item, prazo_validade_em_qtd_de_anos>
			Hashtable<Integer, Integer> validades = relatorio.getMapaValidadesItens();
			
			
			int anoInicioMinimo = calcularAnoInicioMinimo(anoVigencia, validades);
			//buscando as produções do docente dado o ano vigente e o ano minimo válido
			Collection<Producao> producoes = dao.findProducaoIntelectualByDocenteAnoAnoMinimo(docente, anoVigencia, anoInicioMinimo);
			
			//RelatorioHelper helper = new RelatorioHelper();
			//montando a tabela de produção intelectual <id_grupo, lista_producoes_docente_grupo>
	        Hashtable<Integer, Collection<ProducaoIntelectual>> tabelaProducoes = processarProducoes(anoVigencia, listaItensRelatorioProducao, validades, producoes, relatorio);
			
	        Hashtable<Integer, Collection<ViewAtividadeBuilder>> tabelaAtividades = processarAtividades(listaItensRelatorioAtividades, docente, anoVigencia, validades, serviceFormacao);
	        
			// seta os itens de produção intelectual em cada grupo correspondente
			for (GrupoRelatorioProdutividade grupoRelatorioProdutividade : relatorio.getGrupoRelatorioProdutividadeCollection()) {
				for (GrupoItem grupoItemRelatorio : grupoRelatorioProdutividade.getGrupoItemCollection()) {
	
					if ( grupoItemRelatorio.getItemRelatorioProdutividade().isProducaoIntelectual() ) {
						grupoItemRelatorio.setAtividade(false);
						grupoItemRelatorio.setProducoes( 
								tabelaProducoes.get(grupoItemRelatorio.getItemRelatorioProdutividade().getId()) );
					} else {
						//carrega as atividades dos docentes que irão pontuar
						grupoItemRelatorio.setAtividade(true);
						grupoItemRelatorio.setAtividades( 
								tabelaAtividades.get(grupoItemRelatorio.getItemRelatorioProdutividade().getId()) );
					}
				}
			}
		
		}catch (DAOException e) {
			e.printStackTrace();
			throw e;
		}  finally {
			dao.close();
			daoItem.close();
		}
		
		return relatorio;
	}

	/**
	 * Método que constrói o ranking dos docentes de uma determinada classificação.
	 * 
	 * @param dao
	 * @param classificacao
	 * @param tipoUnidadeAcademica
	 * @return
	 * @throws ArqException
	 */
	public static ArrayList<EmissaoRelatorio> montarRankingProdutividade(EmissaoRelatorioDao dao, ClassificacaoRelatorio classificacao, Integer tipoUnidadeAcademica) throws ArqException {
		ArrayList<EmissaoRelatorio> rankingDocentes = (ArrayList<EmissaoRelatorio>) dao.findRankingByClassificacao(classificacao, tipoUnidadeAcademica);
		
		// Popula as informações de agrupamento
		Collection<SiglaUnidadePesquisa> siglas = dao.findAll(SiglaUnidadePesquisa.class);
		Map<Integer, Object[]> mapa = new HashMap<Integer, Object[]>();
		for(SiglaUnidadePesquisa s: siglas){
			if(s.getUnidadeClassificacao() != null)
				mapa.put(s.getUnidade().getId(), 
						new Object[]{s.getUnidadeClassificacao().getId(), s.getUnidadeClassificacao().getSigla(), s.getUnidadeClassificacao().getNome()});
		}
		
		// Altera as informações do ranking de docentes utilizadas no agrupamento, se necessário
		for(EmissaoRelatorio e: rankingDocentes){
			int idCentro = e.getServidor().getUnidade().getGestora().getId();
			int idDepto = e.getServidor().getUnidade().getId();
			
			if(mapa.get(idCentro) != null && ((Integer)mapa.get(idCentro)[0]) != idCentro) {
				swapUnidade(mapa, e, idCentro);
			} else if(mapa.get(idDepto) != null && ((Integer)mapa.get(idDepto)[0]) != idCentro) {
				swapUnidade(mapa, e, idDepto);
			}
		}
		
		// Reordena o ranking por centro e departamento
		if (tipoUnidadeAcademica != null) {
			if (tipoUnidadeAcademica == TipoUnidadeAcademica.DEPARTAMENTO) {
				Collections.sort(rankingDocentes, new Comparator<EmissaoRelatorio>() {
					@Override
					public int compare(EmissaoRelatorio o1, EmissaoRelatorio o2) {
						return o1.getServidor().getUnidade().getGestora().getNome().compareTo(o2.getServidor().getUnidade().getGestora().getNome()) != 0 
								? o1.getServidor().getUnidade().getGestora().getNome().compareTo(o2.getServidor().getUnidade().getGestora().getNome()) 
								: o1.getServidor().getUnidade().getNome().compareTo(o2.getServidor().getUnidade().getNome()) != 0 
									? o1.getServidor().getUnidade().getNome().compareTo(o2.getServidor().getUnidade().getNome()) 
									: o2.getFppi() != null && o1.getFppi() != null 
										?  ( o2.getFppi().compareTo(o1.getFppi()) != 0 
												? o2.getFppi().compareTo(o1.getFppi()) 
												: o2.getIpi().compareTo(o1.getIpi())
											) 
										: ( o2.getIpi().compareTo(o1.getIpi()) );
					}
				});
			}
			if (tipoUnidadeAcademica == TipoUnidadeAcademica.CENTRO) {
				Collections.sort(rankingDocentes, new Comparator<EmissaoRelatorio>() {
					@Override
					public int compare(EmissaoRelatorio o1, EmissaoRelatorio o2) {
						return o1.getServidor().getUnidade().getGestora().getNome().compareTo(o2.getServidor().getUnidade().getGestora().getNome()) != 0 
								? o1.getServidor().getUnidade().getGestora().getNome().compareTo(o2.getServidor().getUnidade().getGestora().getNome()) 
								: o2.getFppi() != null && o1.getFppi() != null
									? 	( o2.getFppi().compareTo(o1.getFppi()) != 0 
											? o2.getFppi().compareTo(o1.getFppi()) 
											: o2.getIpi().compareTo(o1.getIpi())
										)
									: ( o2.getIpi().compareTo(o1.getIpi()) );
					}
				});
			}
		} else {
			Collections.sort(rankingDocentes, new Comparator<EmissaoRelatorio>() {
				@Override
				public int compare(EmissaoRelatorio o1, EmissaoRelatorio o2) {
					if(o1.getFppi() == null || o2.getFppi() == null)
						return o2.getIpi().compareTo(o1.getIpi());
					else
						return o2.getFppi().compareTo(o1.getFppi()) != 0 ?
							o2.getFppi().compareTo(o1.getFppi()) :
								 o2.getIpi().compareTo(o1.getIpi());
				}
			});
		}
		
		return rankingDocentes;
	}
	
	

	/**
	 * Método auxiliar para trocar as informações utilizadas no agrupamento/ordenação do ranking.
	 * 
	 * @param mapa
	 * @param e
	 * @param idUnidade
	 */
	private static void swapUnidade(Map<Integer, Object[]> mapa, EmissaoRelatorio e, int idUnidade) {
		e.getServidor().getUnidade().getGestora().setId((Integer)mapa.get(idUnidade)[0]);
		e.getServidor().getUnidade().getGestora().setSigla((String)mapa.get(idUnidade)[1]);
		e.getServidor().getUnidade().getGestora().setNome((String)mapa.get(idUnidade)[2]);
	}
	
	
	/**
     * Adiciona uma produção a tabela de produções intelectuais por item
     *
     * @param idItem id do ItemRelatorioProdutividade relacionado
     * @param tabelaProducoes
     * @param producao
     * @param anoVigencia
     * @param data
     */
    public static void adicionarProducao(int idItem, Hashtable<Integer, Collection<ProducaoIntelectual> > tabelaProducoes,
            Producao producao, int anoVigencia, Date data) {
        // Buscar a coleção de produções para o item informado
        Collection<ProducaoIntelectual> prodCol = tabelaProducoes.get(idItem);

        // Criar nova produção intelectual e adicionar a coleção
        ProducaoIntelectual producaoIntelectual = new ProducaoIntelectual();
        producaoIntelectual.setAnoVigencia(anoVigencia);
        producaoIntelectual.setNomeAtividade(producao.getDescricaoCompleta());
        producaoIntelectual.setData(data);
        producaoIntelectual.setProducao(producao);
        prodCol.add(producaoIntelectual);
    }
    
    
    /**
     * Método auxiliar para adicionar uma atividade no mapa de atividades que devem pontuar
     * @param idItem
     * @param tabelaAtividades
     * @param atividades
     */
    public static void adicionarAtividade(int idItem, Hashtable<Integer, Collection<ViewAtividadeBuilder> > tabelaAtividades,
    		Collection<? extends ViewAtividadeBuilder> atividades) {
        // Buscar a coleção de produções para o item informado
        Collection<ViewAtividadeBuilder> prodCol = tabelaAtividades.get(idItem);
        prodCol.addAll(atividades);
    }
    
    
    /**
	 * Verificar se a produção é elegível para ser inserida nas produções
	 * cujo item do relatório foi especificado.
	 *
	 * Além disso, também é verificada a validade da produção em relação
	 * àquela especificada no relatório.
	 *
	 * @param todos
	 * @param itensAvaliacao
	 * @param codigoItem
	 * @param validades
	 * @param ano
	 * @param anoVigencia
	 */
	public static boolean verificarInsercaoItem(int idItem, List<ItemRelatorioProdutividade> itensAvaliacao, boolean todos,
			Hashtable<Integer, Integer> validades, int anoReferencia, Integer anoVigencia) {

		// Validar tipo
		boolean tipoValido = false;
		for( ItemRelatorioProdutividade item : itensAvaliacao ){
			if( item.getId() == idItem )
				tipoValido = true;
		}

		// Validar ano
		boolean anoValido = true;
		if (validades != null) {
			Integer validadeProducao = validades.get(idItem);
			int anoInicio = (validadeProducao == null ? anoReferencia : anoReferencia - validadeProducao + 1);

			if (anoVigencia < anoInicio ) {
				anoValido = false;
			}
		}

		return anoValido && (tipoValido || todos);
	}
	
	/**
	 * Carrega todos os mappers dos itens do relatório que são utilizados para gerar a pontuação dos docentes.
	 * @param itens Lista dos mappers carregados.
	 * @return
	 */
	private static List<ProducaoMapper> carregaMappers(RelatorioProdutividade rel, Boolean producao) throws DAOException {
		
		ItemRelatorioProdutividadeDao dao = null;
		List<ProducaoMapper> mappers = new ArrayList<ProducaoMapper>();
		
		try{
			dao = new ItemRelatorioProdutividadeDao();
			List<ItemRelatorioProdutividade> itens = dao.findByRelatorioProdutividade(rel.getId(), producao);
		
		
		
			for( ItemRelatorioProdutividade item :  itens){
				if( item.getProducaoMapper() != null ){
					ProducaoMapper mapper = (ProducaoMapper) ReflectionUtils.newInstance( item.getProducaoMapper() );
					mapper.setIdItemRelatorioProdutividade( item.getId() );
					mappers.add( mapper );
				} else {
					throw new  ConfiguracaoAmbienteException("É necessário configurar o mapper para todos os itens do relatório de produtividade cadastrados. O item " 
							+ item.getId() + " - " + item.getProducaoMapper() + " não está configurado, contacte o suporte." );
				}
			}
		
		}finally{
			dao.close();
		}
		
		return mappers;
	}
	
	
	/**
	 * Processa atividades dos docentes do tipo produção intelectual.
	 * @param anoReferencia
	 * @param itensAvaliacao
	 * @param validades
	 * @param producoes
	 * @param relatorio
	 * @return
	 * @throws ArqException
	 */
	public static Hashtable<Integer, Collection<ProducaoIntelectual>> processarProducoes(
			int anoReferencia, List<ItemRelatorioProdutividade> itensAvaliacao,
			Hashtable<Integer, Integer> validades,
			Collection<Producao> producoes, RelatorioProdutividade relatorio) throws ArqException {
		
		GenericDAO dao = new GenericDAOImpl(Sistema.SIGAA);
		
		// Verificar se foram selecionados itens específicos.
        // Caso contrário, utilizar todos os itens cadastrados
        boolean todos = false;
        Collection<ItemRelatorioProdutividade> itens = null;
        //itens = dao.findAll(ItemRelatorioProdutividade.class);
        
        if (itensAvaliacao == null){
            todos = true;
            
            try{
            	itens = dao.findAll(ItemRelatorioProdutividade.class);
            }finally{
            	dao.close();
            }

            itensAvaliacao = new ArrayList<ItemRelatorioProdutividade>();
            for (ItemRelatorioProdutividade produtividade : itens) {
                itensAvaliacao.add( produtividade );
            }
        }

        // Inicializar mapa com o resultado das produções por item de relatÃ³rio
        Hashtable<Integer, Collection<ProducaoIntelectual> > tabelaProducoes  = new Hashtable<Integer, Collection<ProducaoIntelectual>>();
        for ( ItemRelatorioProdutividade item: itensAvaliacao ) {
            tabelaProducoes.put(item.getId(), new ArrayList<ProducaoIntelectual>());
        }
        
        List<ProducaoMapper> mappers =  carregaMappers(relatorio, true);
        
        
        for( ProducaoMapper mapper : mappers ){
        	
        	for (Producao producao : producoes) {
        		
        		Integer anoVigencia = producao.getAnoReferencia(); // comentar
        		mapper.process(producao, 0, itensAvaliacao, todos, validades, anoReferencia, anoVigencia, tabelaProducoes); 
        		
        	}
        	
        }
        
        return tabelaProducoes;
	}
	
	
	/**
	 * Processa as atividades dos docentes que não são do tipo produção intelectual
	 * @param itensAvaliacao
	 * @param docente
	 * @param ano
	 * @param validades
	 * @param serviceFormacao
	 * @return
	 * @throws DAOException
	 */
	public static Hashtable<Integer, Collection<ViewAtividadeBuilder>> processarAtividades(List<ItemRelatorioProdutividade> itensAvaliacao, Servidor docente,
			int ano, Hashtable<Integer, Integer> validades, FormacaoAcademicaRemoteService serviceFormacao) throws DAOException{
	
		
		// Inicializar mapa com o resultado das produções por item de relatório
        Hashtable<Integer, Collection<ViewAtividadeBuilder> > tabelaAtividades  = new Hashtable<Integer, Collection<ViewAtividadeBuilder>>();
        for ( ItemRelatorioProdutividade item: itensAvaliacao ) {
            tabelaAtividades.put(item.getId(), new ArrayList<ViewAtividadeBuilder>());
        }
        
        for( ItemRelatorioProdutividade item: itensAvaliacao ){
        	
        	if( item.getProducaoMapper() != null ){
				AtividadeMapper mapper = (AtividadeMapper) ReflectionUtils.newInstance( item.getProducaoMapper() );
				mapper.setIdItemRelatorioProdutividade( item.getId() );
				mapper.setServiceFormacao(serviceFormacao);
				
				mapper.process(item.getId(), docente, ano, validades, tabelaAtividades);
			}
        	
        }
        

        
        return tabelaAtividades;
		
	}
	
	/**
	 * Calcula o ano mínimo dentro do mapa de validades
	 * @param ano
	 * @param validades
	 * @return
	 */
	private static int calcularAnoInicioMinimo(int ano, Hashtable<Integer, Integer> validades) {
		int anoInicioMinimo = ano;
    	if (validades != null && !validades.isEmpty()){
    		int quantidadeAnosAnteriores = 1;
    		for (Integer validade : validades.values() ) {
    			quantidadeAnosAnteriores = Math.max(quantidadeAnosAnteriores, validade);
    		}
    		anoInicioMinimo = ano - quantidadeAnosAnteriores + 1; // A validade deve contar o ano selecionado
    	}
		return anoInicioMinimo;
	}
}
