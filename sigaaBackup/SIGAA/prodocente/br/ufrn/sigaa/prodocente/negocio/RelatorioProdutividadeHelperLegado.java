package br.ufrn.sigaa.prodocente.negocio;

@Deprecated //não está sendo utilizado
public class RelatorioProdutividadeHelperLegado {

	/**
	
	
	public Hashtable<Integer, Collection<ProducaoIntelectual>> processarProducoes(
			int anoReferencia, List<Integer> itensAvaliacao,
			Hashtable<Integer, Integer> validades,
			Collection<Producao> producoes) throws ClassNotFoundException, ArqException {
		
		GenericDAO dao = new GenericDAOImpl(Sistema.SIGAA);
		
		// Verificar se foram selecionados itens específicos.
        // Caso contrário, utilizar todos os itens cadastrados
        boolean todos = false;
        Collection<ItemRelatorioProdutividade> itens = null;
        //itens = dao.findAll(ItemRelatorioProdutividade.class);
        
        if (itensAvaliacao == null){
            todos = true;
            
            try{
            	//itens = dao.findAllProjection(ItemRelatorioProdutividade.class, "numeroTopico");
            	itens = dao.findAll(ItemRelatorioProdutividade.class);
            }finally{
            	dao.close();
            }

            itensAvaliacao = new ArrayList<Integer>();
            for (ItemRelatorioProdutividade produtividade : itens) {
                itensAvaliacao.add( produtividade.getNumeroTopico() );
            }
        }

        // Inicializar mapa com o resultado das produções por item de relatÃ³rio
        Hashtable<Integer, Collection<ProducaoIntelectual> > tabelaProducoes  = new Hashtable<Integer, Collection<ProducaoIntelectual>>();
        for ( Integer item: itensAvaliacao ) {
            tabelaProducoes.put(item, new ArrayList<ProducaoIntelectual>());
        }
        
        List<ProducaoMapper> mappers =  carregaMappers(itens);
        
        for( ProducaoMapper mapper : mappers ){
        	
        	for (Producao producao : producoes) {
        		
        		Integer anoVigencia = producao.getAnoReferencia(); // comentar
        		
        		mapper.process(producao, 0, itensAvaliacao, todos, validades, anoReferencia, anoVigencia, tabelaProducoes); 
        		
        	}
        	
        }
        
        
        for (Producao producao : producoes) {
        	
        	if(true)
        		break;
        	
        	Integer anoVigencia = producao.getAnoReferencia(); // comentar

            //item 3.4 (geral - para todos os tipos de região)
            if ( verificarInsercaoItem(ConstantesProdocente.PUBLIC_LIVRO_DIDATIC_OCULTURAL_TECNICO_ISPN,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof Livro ) {
                    Livro l = (Livro) producao;

                    if ( l.getTipoParticipacao() != null && l.getTipoParticipacao().getId() != TipoParticipacao.PREFACIADOR_LIVRO &&
                    		l.getTipoParticipacao().getId() != TipoParticipacao.ILUSTRADOR_LIVRO &&
                    		l.getTipoParticipacao().getId() != TipoParticipacao.TRADUTOR_ARTIGO_PERIODICOS_JORNAIS &&
                    		l.getTipoParticipacao().getId() != TipoParticipacao.TRADUTOR_LIVRO &&
                    		l.getTipoParticipacao().getId() != TipoParticipacao.EDITOR_LIVRO &&
                    		l.getTipoParticipacao().getId() != TipoParticipacao.ORGANIZADOR_LIVRO ) {
                        adicionarProducao( ConstantesProdocente.PUBLIC_LIVRO_DIDATIC_OCULTURAL_TECNICO_ISPN ,
                                tabelaProducoes, producao, 0, l.getDataPublicacao());
                    }
                }
            }

            //item 3.5
            if ( verificarInsercaoItem(ConstantesProdocente.TRAD_LIVRO_DIDATICO_CULTURAL_TECNICO_ISPN,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof Livro ) {
                    Livro l = (Livro) producao;
                    if (  l.getTipoParticipacao() != null &&
                    		(l.getTipoParticipacao().getId() == TipoParticipacao.TRADUTOR_LIVRO ||
                            l.getTipoParticipacao().getId() == TipoParticipacao.TRADUTOR_ARTIGO_PERIODICOS_JORNAIS) ) {

                        adicionarProducao(ConstantesProdocente.TRAD_LIVRO_DIDATICO_CULTURAL_TECNICO_ISPN,
                                tabelaProducoes, producao, anoVigencia, l.getDataPublicacao());
                    }
                }
            }


            //item 3.6(item geral para todos os tipos de região, conforme o relatório padrão)
            if (verificarInsercaoItem(ConstantesProdocente.CAP_LIVRO_DIDATICO_CULTURAL_TECNICO_ISPN,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof Capitulo ) {
                    Capitulo c = (Capitulo) producao;
                    if (  c.getTipoParticipacao() != null && (c.getTipoParticipacao().getId() != TipoParticipacao.TRADUTOR_LIVRO ||
                            c.getTipoParticipacao().getId() != TipoParticipacao.TRADUTOR_ARTIGO_PERIODICOS_JORNAIS ||
                            c.getTipoParticipacao().getId() != TipoParticipacao.OUTROS_ARTIGO_PERIODICOS_JORNAIS ||
                            c.getTipoParticipacao().getId() != TipoParticipacao.OUTRA_PUBLICACAO_EVENTO ||
                            c.getTipoParticipacao().getId() != TipoParticipacao.OUTRO_MONTAGEM ||
                            c.getTipoParticipacao().getId() != TipoParticipacao.COLABORADOR_PROGRAMACAO_VISUAL ||
                            c.getTipoParticipacao().getId() != TipoParticipacao.OUTRO_AUDIO_VISUAL ||

                            c.getTipoParticipacao().getId() != TipoParticipacao.TRABALHO_INDIVIDUAL_ARTIGO_PERIODICOS_JORNAIS ||
                            c.getTipoParticipacao().getId() != TipoParticipacao.EQUIPE_RESPONSAVEL_ARTIGO_PERIODICOS_JORNAIS ||
                            c.getTipoParticipacao().getId() != TipoParticipacao.EQUIPE_COLABORADOR_ARTIGO_PERIODICOS_JORNAIS) ) {

                        adicionarProducao(ConstantesProdocente.CAP_LIVRO_DIDATICO_CULTURAL_TECNICO_ISPN,
                                tabelaProducoes, producao, 0, c.getDataPublicacao());
                    }
                }
            }

            //item 3.7

            if (  verificarInsercaoItem(ConstantesProdocente.EDICAO_LIVRO_ISPN,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia) ) {
                if ( producao instanceof Livro ) {
                    Livro l = (Livro) producao;
                    if (   l.getTipoParticipacao() != null && l.getTipoParticipacao().getId() == TipoParticipacao.EDITOR_LIVRO  ) {

                        adicionarProducao(ConstantesProdocente.EDICAO_LIVRO_ISPN,
                                tabelaProducoes, producao, anoVigencia, l.getDataPublicacao());
                    }
                }
            }

            // item 3.8
            if ( verificarInsercaoItem(ConstantesProdocente.ORG_LIVRO_DIDATICO_CULTURAL_TECNICO_ISPN,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof Livro ) {
                    Livro l = (Livro) producao;
                    if (   l.getTipoParticipacao() != null && l.getTipoParticipacao().getId() == TipoParticipacao.ORGANIZADOR_LIVRO  ) {

                        adicionarProducao(ConstantesProdocente.ORG_LIVRO_DIDATICO_CULTURAL_TECNICO_ISPN,
                                tabelaProducoes, producao, anoVigencia, l.getDataPublicacao());
                    }
                }
            }
            // item 3.9

            if ( verificarInsercaoItem(ConstantesProdocente.PUBLIC_LIVRO_DIDATIC_OCULTURAL_TECNICO_ISPN,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof TextoDidatico ) {
                    TextoDidatico t = (TextoDidatico) producao;
                    if (  t.getTipoInstancia() != null && t.getTipoInstancia().getId() == TipoInstancia.CONSELHO_EDITORIAL  ) {

                        adicionarProducao(ConstantesProdocente.PUBLIC_LIVRO_DIDATIC_OCULTURAL_TECNICO_ISPN,
                                tabelaProducoes, producao, anoVigencia, t.getDataPublicacao());
                    }
                }
            }

            // item 3.10
            if ( verificarInsercaoItem(ConstantesProdocente.ART_TEC_CIENT_PUBL_PERIODICO_INDEXADO_INTERNACIONAL,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof Artigo ) {
                    Artigo a = (Artigo) producao;
                    if (   a.getTipoRegiao() != null && a.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL &&
                    		 (a.getTipoPeriodico() == null ||
                                     (a.getTipoPeriodico().getId() != TipoPeriodico.REVISTA_NAO_CIENTIFICA &&
                                     a.getTipoPeriodico().getId() != TipoPeriodico.JORNAL_NAO_CIENTIFICO))) {

                        adicionarProducao(ConstantesProdocente.ART_TEC_CIENT_PUBL_PERIODICO_INDEXADO_INTERNACIONAL,
                                tabelaProducoes, producao, anoVigencia, a.getDataPublicacao());
                    }
                }
            }

            // item 3.11

            if ( verificarInsercaoItem(ConstantesProdocente.ART_TEC_CIENT_PUBL_ANAIS_CONFERENCIA_INTERNACIONAL,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof PublicacaoEvento ) {
                    PublicacaoEvento p = (PublicacaoEvento) producao;
                    if (   p.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL &&
                           (p.getTipoEvento() != null &&
                            (p.getTipoEvento().getId() ==TipoEvento.CONFERENCIA ||
                            p.getTipoEvento().getId() ==TipoEvento.SEMINARIO ||
                            p.getTipoEvento().getId() ==TipoEvento.WORKSHOP ||
                            p.getTipoEvento().getId() ==TipoEvento.CONGRESSO)) &&
                            p.getNatureza() != null && p.getNatureza().equals('T')) {

                        adicionarProducao(ConstantesProdocente.ART_TEC_CIENT_PUBL_ANAIS_CONFERENCIA_INTERNACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataPublicacao());
                    }
                }
            }

            // item 3.12
            if ( verificarInsercaoItem(ConstantesProdocente.ART_TEC_CIENT_PUBL_PERIODICO_CIRCULACAO_NACIONAL,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof Artigo ) {
                    Artigo a = (Artigo) producao;

                    if (  a.getTipoRegiao() != null && a.getTipoRegiao().getId() == TipoRegiao.NACIONAL &&
                    	   (a.getTipoPeriodico() == null ||
                           (a.getTipoPeriodico().getId() != TipoPeriodico.REVISTA_NAO_CIENTIFICA &&
                           a.getTipoPeriodico().getId() != TipoPeriodico.JORNAL_NAO_CIENTIFICO))) {

                        adicionarProducao(ConstantesProdocente.ART_TEC_CIENT_PUBL_PERIODICO_CIRCULACAO_NACIONAL,
                                tabelaProducoes, producao, anoVigencia, a.getDataPublicacao());
                    }
                }
            }

            // item 3.13
            if ( verificarInsercaoItem(ConstantesProdocente.ART_TEC_CIENT_PUBL_ANAIS_CONFERENCIA_NACIONAL,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof PublicacaoEvento ) {
                    PublicacaoEvento p = (PublicacaoEvento) producao;
                    if (   p.getTipoRegiao().getId() == TipoRegiao.NACIONAL &&
                           (p.getTipoEvento() == null ||
                        	p.getTipoEvento().getId() == TipoEvento.CONFERENCIA ||
                            p.getTipoEvento().getId() == TipoEvento.SEMINARIO ||
                            p.getTipoEvento().getId() == TipoEvento.WORKSHOP ||
                            p.getTipoEvento().getId() == TipoEvento.CONGRESSO) &&
                            p.getNatureza() != null &&
                            p.getNatureza().equals('T')) {

                        adicionarProducao(ConstantesProdocente.ART_TEC_CIENT_PUBL_ANAIS_CONFERENCIA_NACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataPublicacao());
                    }
                }
            }

            // item 3.14
            if ( verificarInsercaoItem(ConstantesProdocente.ART_TEC_CIENT_PUBL_PERIODICO_CIRCULACAO_LOCAL,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof Artigo ) {
                    Artigo a = (Artigo) producao;
                    if( a.getTipoPeriodico() != null) {
						if ( ( a.getTipoRegiao() == null || a.getTipoRegiao().getId() == TipoRegiao.LOCAL || a.getTipoRegiao().getId() == TipoRegiao.REGIONAL ) &&
                               a.getTipoParticipacao().getId() != TipoParticipacao.TRADUTOR_LIVRO &&
                               a.getTipoParticipacao().getId() != TipoParticipacao.TRADUTOR_ARTIGO_PERIODICOS_JORNAIS &&
                               a.getTipoPeriodico().getId() != TipoPeriodico.REVISTA_NAO_CIENTIFICA &&
                               a.getTipoPeriodico().getId() != TipoPeriodico.JORNAL_NAO_CIENTIFICO) {

                            adicionarProducao(ConstantesProdocente.ART_TEC_CIENT_PUBL_PERIODICO_CIRCULACAO_LOCAL,
                                    tabelaProducoes, producao, anoVigencia, a.getDataPublicacao());
                    }
					}
                }
            }
            // item 3.15
            if ( verificarInsercaoItem(ConstantesProdocente.ART_DIVULGACAO_PUBL_REVISTAS_JORNAIS,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof Artigo ) {
                    Artigo a = (Artigo) producao;
                    if(a.getTipoPeriodico()!=null) {
						if (   a.getTipoPeriodico().getId() == TipoPeriodico.REVISTA_NAO_CIENTIFICA ||
						       a.getTipoPeriodico().getId() == TipoPeriodico.JORNAL_NAO_CIENTIFICO) {

						    adicionarProducao(ConstantesProdocente.ART_DIVULGACAO_PUBL_REVISTAS_JORNAIS,
						            tabelaProducoes, producao, anoVigencia, a.getDataPublicacao());
						}
					}
                }
            }
            // item 3.16
            if ( verificarInsercaoItem(ConstantesProdocente.TRAD_ART_DIDATICO_CULTURAL_TECNICO,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof Artigo ) {
                    Artigo a = (Artigo) producao;
                    if ( a.getTipoParticipacao().getId() == TipoParticipacao.TRADUTOR_ARTIGO_PERIODICOS_JORNAIS) {

                        adicionarProducao(ConstantesProdocente.TRAD_ART_DIDATICO_CULTURAL_TECNICO,
                                tabelaProducoes, producao, anoVigencia, a.getDataPublicacao());
                    }
                }
            }

            // item 3.17.1
            if ( verificarInsercaoItem(ConstantesProdocente.TRAB_APRES_RESUMO_PUBL_EVENTOS_CIENT_ART_CULT_INTERNACIONAL,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof PublicacaoEvento ) {
                    PublicacaoEvento p = (PublicacaoEvento) producao;
                    if ( p.getTipoRegiao() != null && p.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL 
                    		&& p.getNatureza() != null && ( p.getNatureza().equals('R') || p.getNatureza().equals('E') )) {

                        adicionarProducao(ConstantesProdocente.TRAB_APRES_RESUMO_PUBL_EVENTOS_CIENT_ART_CULT_INTERNACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataPublicacao());
                    }
                }
            }

            // item 3.17.2
//            if ( verificarInsercaoItem(ConstantesProdocente.TRAB_APRES_RESUMO_PUBL_EVENTOS_CIENT_ART_CULT_INTERNACIONAL,
//    	 			itensAvaliacao, todos, validades, ano, anoVigencia)) {
//                if ( producao instanceof PublicacaoEvento ) {
//                	PublicacaoEvento p = (PublicacaoEvento) producao;
//
//                    if ( p.getApresentado() != null && p.getApresentado() &&
//                    		p.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL) {
//
//                        adicionarProducao(ConstantesProdocente.TRAB_APRES_RESUMO_PUBL_EVENTOS_CIENT_ART_CULT_INTERNACIONAL,
//                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
//                    }
//                }
//            }

            // item 3.17.3
            if ( verificarInsercaoItem(ConstantesProdocente.TRAB_APRES_RESUMO_PUBL_EVENTOS_CIENT_ART_CULT_INTERNACIONAL,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof ProducaoArtisticaLiterariaVisual ) {
                    ProducaoArtisticaLiterariaVisual p = (ProducaoArtisticaLiterariaVisual) producao;

                    if (   p.getTipoRegiao() != null && p.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL &&
                            p.getTipoProducao().equals(TipoProducao.EXPOSICAO_APRESENTACAO_ARTISTICAS) ) {

                        adicionarProducao(ConstantesProdocente.TRAB_APRES_RESUMO_PUBL_EVENTOS_CIENT_ART_CULT_INTERNACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            // item 3.17.4
            if ( verificarInsercaoItem(ConstantesProdocente.TRAB_APRES_RESUMO_PUBL_EVENTOS_CIENT_ART_CULT_INTERNACIONAL,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof ProducaoArtisticaLiterariaVisual ) {
                    ProducaoArtisticaLiterariaVisual p = (ProducaoArtisticaLiterariaVisual) producao;

                    if (  p.getTipoRegiao() != null && p.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL &&
                            p.getTipoProducao().equals(TipoProducao.PROGRAMACAO_VISUAL)) {

                        adicionarProducao(ConstantesProdocente.TRAB_APRES_RESUMO_PUBL_EVENTOS_CIENT_ART_CULT_INTERNACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            // item 3.17.5
            if ( verificarInsercaoItem(ConstantesProdocente.TRAB_APRES_RESUMO_PUBL_EVENTOS_CIENT_ART_CULT_INTERNACIONAL,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof ProducaoArtisticaLiterariaVisual ) {
                    ProducaoArtisticaLiterariaVisual p = (ProducaoArtisticaLiterariaVisual) producao;

                    if ( p.getTipoRegiao() != null && p.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL 
                    		&& p.getTipoProducao().equals(TipoProducao.MONTAGENS) ) {

                        adicionarProducao(ConstantesProdocente.TRAB_APRES_RESUMO_PUBL_EVENTOS_CIENT_ART_CULT_INTERNACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            // item 3.18.1
            if ( verificarInsercaoItem(ConstantesProdocente.TRAB_APRES_RESUMO_PUBL_EVENTOS_CIENT_ART_CULT_NACIONAL,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof PublicacaoEvento ) {
                    PublicacaoEvento p = (PublicacaoEvento) producao;

                    if (   p.getTipoRegiao() != null && p.getTipoRegiao().getId() == TipoRegiao.NACIONAL &&
                    		// Contanto com Prof. Bernardete em 04/07/2007
                    		//  ( p.getApresentado() != null && p.getApresentado() ) &&
                    	   p.getNatureza() != null && ( p.getNatureza().equals('R') || p.getNatureza().equals('E') )
                    	   && (p.getTipoEvento() != null &&
                        		  (p.getTipoEvento().getId() == TipoEvento.CONFERENCIA ||
                        			p.getTipoEvento().getId() == TipoEvento.PAINEL ||
                        			p.getTipoEvento().getId() == TipoEvento.CONGRESSO))
                            ) {

                        adicionarProducao(ConstantesProdocente.TRAB_APRES_RESUMO_PUBL_EVENTOS_CIENT_ART_CULT_NACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            // item 3.18.2
//            if ( verificarInsercaoItem(ConstantesProdocente.TRAB_APRES_RESUMO_PUBL_EVENTOS_CIENT_ART_CULT_NACIONAL,
//    	 			itensAvaliacao, todos, validades, ano, anoVigencia)) {
//                if ( producao instanceof PublicacaoEvento ) {
//                	PublicacaoEvento p = (PublicacaoEvento) producao;
//
//                    if ( p.getApresentado() != null && p.getApresentado() &&  p.getTipoRegiao().getId() == TipoRegiao.NACIONAL) {
//
//                        adicionarProducao(ConstantesProdocente.TRAB_APRES_RESUMO_PUBL_EVENTOS_CIENT_ART_CULT_NACIONAL,
//                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
//                    }
//
//                }
//            }

            // item 3.18.3
            if ( verificarInsercaoItem(ConstantesProdocente.TRAB_APRES_RESUMO_PUBL_EVENTOS_CIENT_ART_CULT_NACIONAL,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof ProducaoArtisticaLiterariaVisual ) {
                    ProducaoArtisticaLiterariaVisual p = (ProducaoArtisticaLiterariaVisual) producao;

                    if (  p.getTipoRegiao() != null && p.getTipoRegiao().getId() == TipoRegiao.NACIONAL &&
                    		p.getTipoProducao().equals(TipoProducao.EXPOSICAO_APRESENTACAO_ARTISTICAS) ) {

                        adicionarProducao(ConstantesProdocente.TRAB_APRES_RESUMO_PUBL_EVENTOS_CIENT_ART_CULT_NACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            // item 3.18.4
            if ( verificarInsercaoItem(ConstantesProdocente.TRAB_APRES_RESUMO_PUBL_EVENTOS_CIENT_ART_CULT_NACIONAL,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof ProducaoArtisticaLiterariaVisual ) {
                    ProducaoArtisticaLiterariaVisual p = (ProducaoArtisticaLiterariaVisual) producao;

                    if ( p.getTipoRegiao() != null && p.getTipoRegiao().getId() == TipoRegiao.NACIONAL &&
                            p.getTipoProducao().equals(TipoProducao.PROGRAMACAO_VISUAL)) {

                        adicionarProducao(ConstantesProdocente.TRAB_APRES_RESUMO_PUBL_EVENTOS_CIENT_ART_CULT_NACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            // item 3.18.5
            if ( verificarInsercaoItem(ConstantesProdocente.TRAB_APRES_RESUMO_PUBL_EVENTOS_CIENT_ART_CULT_NACIONAL,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof ProducaoArtisticaLiterariaVisual ) {
                    ProducaoArtisticaLiterariaVisual p = (ProducaoArtisticaLiterariaVisual) producao;

                    if ( p.getTipoRegiao() != null && p.getTipoRegiao().getId() == TipoRegiao.NACIONAL 
                    		&& p.getTipoProducao().equals(TipoProducao.MONTAGENS) ) {

                        adicionarProducao(ConstantesProdocente.TRAB_APRES_RESUMO_PUBL_EVENTOS_CIENT_ART_CULT_NACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }

                }
            }

            // item 3.19.1
            if ( verificarInsercaoItem(ConstantesProdocente.TRAB_APRES_RESUMO_PUBL_EVENTOS_CIENT_ART_CULT_REGIONAL_LOCAL,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof PublicacaoEvento ) {
                    PublicacaoEvento p = (PublicacaoEvento) producao;

                    if ( ( p.getTipoRegiao() == null || p.getTipoRegiao().getId() == TipoRegiao.REGIONAL ||
                    	  p.getTipoRegiao().getId() == TipoRegiao.LOCAL) &&
                          ( p.getNatureza().equals('R') || p.getNatureza().equals('E') )) {

                        adicionarProducao(ConstantesProdocente.TRAB_APRES_RESUMO_PUBL_EVENTOS_CIENT_ART_CULT_REGIONAL_LOCAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            // item 3.19.2
//            if ( verificarInsercaoItem(ConstantesProdocente.TRAB_APRES_RESUMO_PUBL_EVENTOS_CIENT_ART_CULT_REGIONAL_LOCAL,
//    	 			itensAvaliacao, todos, validades, ano, anoVigencia)) {
//                if ( producao instanceof PublicacaoEvento ) {
//                	PublicacaoEvento p = (PublicacaoEvento) producao;
//                    if ( p.getApresentado() != null && p.getApresentado() &&
//                    		(p.getTipoRegiao().getId() == TipoRegiao.REGIONAL ||  p.getTipoRegiao().getId() == TipoRegiao.LOCAL)) {
//
//                        adicionarProducao(ConstantesProdocente.TRAB_APRES_RESUMO_PUBL_EVENTOS_CIENT_ART_CULT_REGIONAL_LOCAL,
//                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
//                    }
//                }
//            }

            // item 3.19.3
            if (  verificarInsercaoItem(ConstantesProdocente.TRAB_APRES_RESUMO_PUBL_EVENTOS_CIENT_ART_CULT_REGIONAL_LOCAL,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia) ) {
                if ( producao instanceof ProducaoArtisticaLiterariaVisual ) {

                    ProducaoArtisticaLiterariaVisual p = (ProducaoArtisticaLiterariaVisual) producao;

                    if (( p.getTipoRegiao() == null || p.getTipoRegiao().getId() == TipoRegiao.REGIONAL ||  p.getTipoRegiao().getId() == TipoRegiao.LOCAL)
                        && p.getTipoProducao().equals(TipoProducao.EXPOSICAO_APRESENTACAO_ARTISTICAS) ){

                        adicionarProducao(ConstantesProdocente.TRAB_APRES_RESUMO_PUBL_EVENTOS_CIENT_ART_CULT_REGIONAL_LOCAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            // item 3.19.4
            if ( verificarInsercaoItem(ConstantesProdocente.TRAB_APRES_RESUMO_PUBL_EVENTOS_CIENT_ART_CULT_REGIONAL_LOCAL,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof ProducaoArtisticaLiterariaVisual ) {
                    ProducaoArtisticaLiterariaVisual p = (ProducaoArtisticaLiterariaVisual) producao;

                    if ((p.getTipoRegiao() == null || p.getTipoRegiao().getId() == TipoRegiao.REGIONAL ||  p.getTipoRegiao().getId() == TipoRegiao.LOCAL) &&
                        p.getTipoProducao().equals(TipoProducao.PROGRAMACAO_VISUAL)) {

                        adicionarProducao(ConstantesProdocente.TRAB_APRES_RESUMO_PUBL_EVENTOS_CIENT_ART_CULT_REGIONAL_LOCAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }

                }
            }

            // item 3.19.5
            if ( verificarInsercaoItem(ConstantesProdocente.TRAB_APRES_RESUMO_PUBL_EVENTOS_CIENT_ART_CULT_REGIONAL_LOCAL,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof ProducaoArtisticaLiterariaVisual ) {
                    ProducaoArtisticaLiterariaVisual p = (ProducaoArtisticaLiterariaVisual) producao;

                    if ((p.getTipoRegiao() == null || p.getTipoRegiao().getId() == TipoRegiao.REGIONAL ||  p.getTipoRegiao().getId() == TipoRegiao.LOCAL)
                            && p.getTipoProducao().equals(TipoProducao.MONTAGENS) ) {

                        adicionarProducao(ConstantesProdocente.TRAB_APRES_RESUMO_PUBL_EVENTOS_CIENT_ART_CULT_REGIONAL_LOCAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }

                }
            }

            // item 3.20 (nÃ£o Ã© uma produÃ§Ã£o intelectual)

            // item 3.21
            if ( verificarInsercaoItem(ConstantesProdocente.EDITORACAO_REVISTAS_CIENT_CULT_INTERNACIONAIS,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof ParticipacaoComissaoOrgEventos ) {
                    ParticipacaoComissaoOrgEventos p = (ParticipacaoComissaoOrgEventos) producao;

                    if (p.getAmbito().getId() == TipoRegiao.INTERNACIONAL &&
                    	p.getTipoParticipacaoOrganizacao() != null &&
                        p.getTipoParticipacaoOrganizacao().getId() == TipoParticipacaoOrganizacaoEventos.EDITOR_PERIODICOS_JORNAIS_SIMILARES) {

                        adicionarProducao(ConstantesProdocente.EDITORACAO_REVISTAS_CIENT_CULT_INTERNACIONAIS,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            // item 3.22
            if ( verificarInsercaoItem(ConstantesProdocente.EDITORACAO_REVISTAS_CIENT_CULT_NACIONAIS,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof ParticipacaoComissaoOrgEventos ) {
                    ParticipacaoComissaoOrgEventos p = (ParticipacaoComissaoOrgEventos) producao;

                    if (p.getAmbito().getId() == TipoRegiao.NACIONAL &&
                    	p.getTipoParticipacaoOrganizacao() != null &&
                    	p.getTipoParticipacaoOrganizacao().getId() == TipoParticipacaoOrganizacaoEventos.EDITOR_PERIODICOS_JORNAIS_SIMILARES) {

                        adicionarProducao(ConstantesProdocente.EDITORACAO_REVISTAS_CIENT_CULT_NACIONAIS,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            // item 3.23
            if ( verificarInsercaoItem(ConstantesProdocente.EDITORACAO_REVISTAS_CIENT_CULT_REGIONAIS_LOCAIS,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof ParticipacaoComissaoOrgEventos ) {
                    ParticipacaoComissaoOrgEventos p = (ParticipacaoComissaoOrgEventos) producao;

                    if ((p.getAmbito().getId() == TipoRegiao.REGIONAL || p.getAmbito().getId() == TipoRegiao.LOCAL) &&
                    	(p.getTipoParticipacaoOrganizacao() != null ||
                    			p.getTipoParticipacaoOrganizacao().getId() == TipoParticipacaoOrganizacaoEventos.EDITOR_PERIODICOS_JORNAIS_SIMILARES)) {

                        adicionarProducao(ConstantesProdocente.EDITORACAO_REVISTAS_CIENT_CULT_REGIONAIS_LOCAIS,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }


            // item 3.24
            if ( verificarInsercaoItem(ConstantesProdocente.PART_CONSELHO_EDITORIAL_INTERNACIONAL,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof ParticipacaoComissaoOrgEventos ) {
                    ParticipacaoComissaoOrgEventos p = (ParticipacaoComissaoOrgEventos) producao;

                    if (p.getAmbito().getId() == TipoRegiao.INTERNACIONAL &&
                    	p.getTipoParticipacaoOrganizacao() != null &&
                        p.getTipoParticipacaoOrganizacao().getId() == TipoParticipacaoOrganizacaoEventos.MEMBRO_CONSELHO_EDITORIAL) {

                        adicionarProducao(ConstantesProdocente.PART_CONSELHO_EDITORIAL_INTERNACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            // item 3.25
            if ( verificarInsercaoItem(ConstantesProdocente.PART_CONSELHO_EDITORIAL_NACIONAL,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof ParticipacaoComissaoOrgEventos ) {
                    ParticipacaoComissaoOrgEventos p = (ParticipacaoComissaoOrgEventos) producao;

                    if (p.getAmbito().getId() == TipoRegiao.NACIONAL && p.getTipoParticipacaoOrganizacao() != null){

                    	if(p.getTipoParticipacaoOrganizacao().getId() == TipoParticipacaoOrganizacaoEventos.MEMBRO_CONSELHO_EDITORIAL) {
							adicionarProducao(ConstantesProdocente.PART_CONSELHO_EDITORIAL_NACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
						}
                    }
                }
            }

            // item 3.26
            if ( verificarInsercaoItem(ConstantesProdocente.PART_CONSELHO_EDITORIAL_REGIONAL_LOCAL,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof ParticipacaoComissaoOrgEventos ) {
                    ParticipacaoComissaoOrgEventos p = (ParticipacaoComissaoOrgEventos) producao;

                    if ((p.getAmbito().getId() == TipoRegiao.REGIONAL || p.getAmbito().getId() == TipoRegiao.LOCAL) &&
                    		p.getTipoParticipacaoOrganizacao()!=null){
                        if(p.getTipoParticipacaoOrganizacao().getId() ==TipoParticipacaoOrganizacaoEventos.MEMBRO_CONSELHO_EDITORIAL) {
							adicionarProducao(ConstantesProdocente.PART_CONSELHO_EDITORIAL_REGIONAL_LOCAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
						}
                    }
                }
            }

            // item 3.27
            if ( verificarInsercaoItem(ConstantesProdocente.AUTORIA_PARTITURA_MUSICAL,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof AudioVisual ) {
                    AudioVisual p = (AudioVisual) producao;

                    if (p.getSubTipoArtistico().getId() == SubTipoArtistico.PARTITURA_MUSICAL_AUDIO_VISUAL) {

                        adicionarProducao(ConstantesProdocente.AUTORIA_PARTITURA_MUSICAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            // item 3.28
            if ( verificarInsercaoItem(ConstantesProdocente.COMPOSICAO_MUSICAL,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof AudioVisual ) {
                    AudioVisual p = (AudioVisual) producao;

                    if (p.getSubTipoArtistico().getId() == SubTipoArtistico.COMPOSICAO_MUSICAL_AUDIO_VISUAL) {

                        adicionarProducao(ConstantesProdocente.COMPOSICAO_MUSICAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            // item 3.29
            if (  verificarInsercaoItem(ConstantesProdocente.ARRANJO_MUSICAL,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof AudioVisual ) {
                    AudioVisual p = (AudioVisual) producao;

                    if (p.getSubTipoArtistico().getId() == SubTipoArtistico.ARRANJO_MUSICAL_AUDIO_VISUAL) {

                        adicionarProducao(ConstantesProdocente.ARRANJO_MUSICAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            // item 3.30 - Verificar se há maquete ou outros para não buscar patentes
            if ( verificarInsercaoItem(ConstantesProdocente.PUBL_CARTAS_GEOGRAFICAS_MAPAS_SIMILAR_EM_LIVROS_REV_INDEXADAS,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof ProducaoTecnologica ) {
                    ProducaoTecnologica p = (ProducaoTecnologica) producao;

                    if (p.getTipoProducaoTecnologica().getId() != TipoProducaoTecnologica.SOFTWARE
                            && p.getTipoProducao().getId() == TipoProducao.MAQUETES_PROTOTIPOS_OUTROS.getId()) {

                        adicionarProducao(ConstantesProdocente.PUBL_CARTAS_GEOGRAFICAS_MAPAS_SIMILAR_EM_LIVROS_REV_INDEXADAS,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }

                }
            }

            // item 3.31
            if ( verificarInsercaoItem(ConstantesProdocente.DESENV_APL_COMPUT_REGISTRADOS_PUBLICADOS_EM_LIVROS_REV_INDEXADAS,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof ProducaoTecnologica ) {
                    ProducaoTecnologica p = (ProducaoTecnologica) producao;

                    if (p.getTipoProducaoTecnologica().getId() == TipoProducaoTecnologica.SOFTWARE &&
                            p.getTipoProducao().getId() == TipoProducao.MAQUETES_PROTOTIPOS_OUTROS.getId()) {

                        adicionarProducao(ConstantesProdocente.DESENV_APL_COMPUT_REGISTRADOS_PUBLICADOS_EM_LIVROS_REV_INDEXADAS,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            // item 8.01 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.17)
            if ( verificarInsercaoItem(ConstantesProdocente.PRODUCAO_COREOGRAFICA_INTERNACIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof ProducaoArtisticaLiterariaVisual ) {
                    ProducaoArtisticaLiterariaVisual p = (ProducaoArtisticaLiterariaVisual) producao;

                    if (  p.getTipoRegiao() != null && p.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL &&
                           (p.getSubTipoArtistico().getId() == SubTipoArtistico.COREOGRAFIA_MONTAGENS ||
                            p.getSubTipoArtistico().getId() == SubTipoArtistico.COREOGRAFO_EXPOSICAO_APRESENTACAO_EVENTOS ||
                            p.getSubTipoArtistico().getId() == SubTipoArtistico.COREOGRAFO_PUBLICACAO_EVENTO)) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_COREOGRAFICA_INTERNACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            // item 8.02 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.18)
            if ( verificarInsercaoItem(ConstantesProdocente.PRODUCAO_COREOGRAFICA_NACIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof ProducaoArtisticaLiterariaVisual ) {
                    ProducaoArtisticaLiterariaVisual p = (ProducaoArtisticaLiterariaVisual) producao;

                    if ( p.getTipoRegiao() != null &&  p.getTipoRegiao().getId() == TipoRegiao.NACIONAL &&
                           (p.getSubTipoArtistico().getId() == SubTipoArtistico.COREOGRAFIA_MONTAGENS ||
                            p.getSubTipoArtistico().getId() == SubTipoArtistico.COREOGRAFO_EXPOSICAO_APRESENTACAO_EVENTOS ||
                            p.getSubTipoArtistico().getId() == SubTipoArtistico.COREOGRAFO_PUBLICACAO_EVENTO)) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_COREOGRAFICA_NACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            // item 8.03 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.19)
            if ( verificarInsercaoItem(ConstantesProdocente.PRODUCAO_COREOGRAFICA_LOCAL_REGIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia) ) {
                if ( producao instanceof ProducaoArtisticaLiterariaVisual ) {
                    ProducaoArtisticaLiterariaVisual p = (ProducaoArtisticaLiterariaVisual) producao;

                    if (   (p.getTipoRegiao() == null || p.getTipoRegiao().getId() == TipoRegiao.REGIONAL ||
                            p.getTipoRegiao().getId() == TipoRegiao.LOCAL) &&
                           (p.getSubTipoArtistico().getId() == SubTipoArtistico.COREOGRAFIA_MONTAGENS ||
                            p.getSubTipoArtistico().getId() == SubTipoArtistico.COREOGRAFO_EXPOSICAO_APRESENTACAO_EVENTOS ||
                            p.getSubTipoArtistico().getId() == SubTipoArtistico.COREOGRAFO_PUBLICACAO_EVENTO)) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_COREOGRAFICA_LOCAL_REGIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            // item 8.04 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.17)
            if ( verificarInsercaoItem(ConstantesProdocente.PRODUCAO_LITERARIA_INTERNACIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof ProducaoArtisticaLiterariaVisual ) {
                    ProducaoArtisticaLiterariaVisual p = (ProducaoArtisticaLiterariaVisual) producao;

                    if (  p.getTipoRegiao() != null && p.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL &&
                           (p.getSubTipoArtistico().getId() == SubTipoArtistico.LITERARIO_EXPOSICAO_APRESENTACAO_EVENTOS ||
                            p.getSubTipoArtistico().getId() == SubTipoArtistico.LITERARIO_PUBLICACAO_EVENTO)) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_LITERARIA_INTERNACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            // item 8.05 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.18)
            if ( verificarInsercaoItem(ConstantesProdocente.PRODUCAO_LITERARIA_NACIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof ProducaoArtisticaLiterariaVisual ) {
                    ProducaoArtisticaLiterariaVisual p = (ProducaoArtisticaLiterariaVisual) producao;

                    if (  p.getTipoRegiao() != null && p.getTipoRegiao().getId() == TipoRegiao.NACIONAL &&
                           (p.getSubTipoArtistico().getId() == SubTipoArtistico.LITERARIO_EXPOSICAO_APRESENTACAO_EVENTOS ||
                            p.getSubTipoArtistico().getId() == SubTipoArtistico.LITERARIO_PUBLICACAO_EVENTO)) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_LITERARIA_NACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            //item 8.06 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.19)
            if ( verificarInsercaoItem(ConstantesProdocente.PRODUCAO_LITERARIA_LOCAL_REGIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof ProducaoArtisticaLiterariaVisual ) {
                    ProducaoArtisticaLiterariaVisual p = (ProducaoArtisticaLiterariaVisual) producao;

                    if ( (p.getTipoRegiao() == null || p.getTipoRegiao().getId() == TipoRegiao.REGIONAL ||
                            p.getTipoRegiao().getId() == TipoRegiao.LOCAL) &&
                           (p.getSubTipoArtistico().getId() == SubTipoArtistico.LITERARIO_PUBLICACAO_EVENTO ||
                            p.getSubTipoArtistico().getId() == SubTipoArtistico.LITERARIO_EXPOSICAO_APRESENTACAO_EVENTOS)) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_LITERARIA_LOCAL_REGIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            // item 8.07 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.17)
            if ( verificarInsercaoItem(ConstantesProdocente.PRODUCAO_MUSICAL_INTERNACIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof ProducaoArtisticaLiterariaVisual ) {
                    ProducaoArtisticaLiterariaVisual p = (ProducaoArtisticaLiterariaVisual) producao;

                    if (  p.getTipoRegiao() != null && p.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL &&
                           (p.getSubTipoArtistico().getId() == SubTipoArtistico.MUSICAL_EXPOSICAO_APRESENTACAO_EVENTOS ||
                            p.getSubTipoArtistico().getId() == SubTipoArtistico.MUSICAL_PUBLICACAO_EVENTO)) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_MUSICAL_INTERNACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            // item 8.08 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.18)
            if ( verificarInsercaoItem(ConstantesProdocente.PRODUCAO_MUSICAL_NACIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof ProducaoArtisticaLiterariaVisual ) {
                    ProducaoArtisticaLiterariaVisual p = (ProducaoArtisticaLiterariaVisual) producao;

                    if ( p.getTipoRegiao() != null &&  p.getTipoRegiao().getId() == TipoRegiao.NACIONAL &&
                           (p.getSubTipoArtistico().getId() == SubTipoArtistico.MUSICAL_EXPOSICAO_APRESENTACAO_EVENTOS ||
                            p.getSubTipoArtistico().getId() == SubTipoArtistico.MUSICAL_PUBLICACAO_EVENTO)) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_MUSICAL_NACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            //item 8.09 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.19)
            if ( verificarInsercaoItem(ConstantesProdocente.PRODUCAO_MUSICAL_LOCAL_REGIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof ProducaoArtisticaLiterariaVisual ) {
                    ProducaoArtisticaLiterariaVisual p = (ProducaoArtisticaLiterariaVisual) producao;

                    if (   (p.getTipoRegiao() == null || p.getTipoRegiao().getId() == TipoRegiao.REGIONAL ||
                            p.getTipoRegiao().getId() == TipoRegiao.LOCAL) &&
                           (p.getSubTipoArtistico().getId() == SubTipoArtistico.MUSICAL_EXPOSICAO_APRESENTACAO_EVENTOS ||
                            p.getSubTipoArtistico().getId() == SubTipoArtistico.MUSICAL_PUBLICACAO_EVENTO)) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_MUSICAL_LOCAL_REGIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            //item 8.10 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.17)
            if ( verificarInsercaoItem(ConstantesProdocente.PRODUCAO_TEATRAL_INTERNACIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof ProducaoArtisticaLiterariaVisual ) {
                    ProducaoArtisticaLiterariaVisual p = (ProducaoArtisticaLiterariaVisual) producao;

                    if ( p.getTipoRegiao() != null &&  p.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL &&
                           (p.getSubTipoArtistico().getId() == SubTipoArtistico.TEATRAL_EXPOSICAO_APRESENTACAO_EVENTOS ||
                            p.getSubTipoArtistico().getId() == SubTipoArtistico.TEATRAL_PUBLICACAO_EVENTO)) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_TEATRAL_INTERNACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            // item 8.11 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.18)
            if ( verificarInsercaoItem(ConstantesProdocente.PRODUCAO_TEATRAL_NACIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof ProducaoArtisticaLiterariaVisual ) {
                    ProducaoArtisticaLiterariaVisual p = (ProducaoArtisticaLiterariaVisual) producao;

                    if ( p.getTipoRegiao() != null &&  p.getTipoRegiao().getId() == TipoRegiao.NACIONAL &&
                           (p.getSubTipoArtistico().getId() == SubTipoArtistico.TEATRAL_EXPOSICAO_APRESENTACAO_EVENTOS ||
                            p.getSubTipoArtistico().getId() == SubTipoArtistico.TEATRAL_PUBLICACAO_EVENTO)) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_TEATRAL_NACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            //item 8.12 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.19)
            if ( verificarInsercaoItem(ConstantesProdocente.PRODUCAO_TEATRAL_LOCAL_REGIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof ProducaoArtisticaLiterariaVisual ) {
                    ProducaoArtisticaLiterariaVisual p = (ProducaoArtisticaLiterariaVisual) producao;

                    if (   (p.getTipoRegiao() == null || p.getTipoRegiao().getId() == TipoRegiao.REGIONAL ||
                            p.getTipoRegiao().getId() == TipoRegiao.LOCAL) &&
                           (p.getSubTipoArtistico().getId() == SubTipoArtistico.TEATRAL_EXPOSICAO_APRESENTACAO_EVENTOS ||
                            p.getSubTipoArtistico().getId() == SubTipoArtistico.TEATRAL_PUBLICACAO_EVENTO)) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_TEATRAL_LOCAL_REGIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            //item 8.13 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.17)
            if ( verificarInsercaoItem(ConstantesProdocente.PRODUCAO_CINEMA_INTERNACIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof AudioVisual ) {
                    AudioVisual p = (AudioVisual) producao;

                    if ( p.getTipoRegiao() != null &&  p.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL &&
                           p.getSubTipoArtistico().getId() == SubTipoArtistico.CINEMA_AUDIO_VISUAL) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_CINEMA_INTERNACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            // item 8.14 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.18)
            if ( verificarInsercaoItem(ConstantesProdocente.PRODUCAO_CINEMA_NACIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof AudioVisual ) {
                    AudioVisual p = (AudioVisual) producao;

                    if ( p.getTipoRegiao() != null &&  p.getTipoRegiao().getId() == TipoRegiao.NACIONAL &&
                             p.getSubTipoArtistico().getId() == SubTipoArtistico.CINEMA_AUDIO_VISUAL) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_CINEMA_NACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            //item 8.15 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.19)
            if ( verificarInsercaoItem(ConstantesProdocente.PRODUCAO_CINEMA_LOCAL_REGIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof AudioVisual ) {
                    AudioVisual p = (AudioVisual) producao;

                    if (   (p.getTipoRegiao() == null || p.getTipoRegiao().getId() == TipoRegiao.REGIONAL ||
                            p.getTipoRegiao().getId() == TipoRegiao.LOCAL) &&
                            p.getSubTipoArtistico().getId() == SubTipoArtistico.CINEMA_AUDIO_VISUAL) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_CINEMA_LOCAL_REGIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            //	item 8.16 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.17)
            if ( verificarInsercaoItem(ConstantesProdocente.PRODUCAO_CINEMA_LOCAL_REGIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof AudioVisual ) {
                    AudioVisual p = (AudioVisual) producao;

                    if ( p.getTipoRegiao() != null &&  p.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL &&
                           p.getSubTipoArtistico().getId() == SubTipoArtistico.TELEVISAO_AUDIO_VISUAL) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_CINEMA_LOCAL_REGIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }
            // item 8.17 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.18)
            if ( verificarInsercaoItem(ConstantesProdocente.PRODUCAO_TV_NACIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof AudioVisual ) {
                    AudioVisual p = (AudioVisual) producao;

                    if (  p.getTipoRegiao() != null && p.getTipoRegiao().getId() == TipoRegiao.NACIONAL &&
                             p.getSubTipoArtistico().getId() == SubTipoArtistico.TELEVISAO_AUDIO_VISUAL) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_TV_NACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            // item 8.18 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.19)
            if (verificarInsercaoItem(ConstantesProdocente.PRODUCAO_TV_LOCAL_REGIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof AudioVisual ) {
                    AudioVisual p = (AudioVisual) producao;

                    if (   (p.getTipoRegiao() == null || p.getTipoRegiao().getId() == TipoRegiao.REGIONAL ||
                            p.getTipoRegiao().getId() == TipoRegiao.LOCAL) &&
                            p.getSubTipoArtistico().getId() == SubTipoArtistico.TELEVISAO_AUDIO_VISUAL) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_TV_LOCAL_REGIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }

                }
            }

            // item 8.19 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.17)
            if ( verificarInsercaoItem(ConstantesProdocente.PRODUCAO_DESENHO_INTERNACIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof AudioVisual ) {
                    AudioVisual p = (AudioVisual) producao;

                    if ( p.getTipoRegiao() != null &&  p.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL &&
                           p.getSubTipoArtistico().getId() == SubTipoArtistico.DESENHO_AUDIO_VISUAL) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_DESENHO_INTERNACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());

                    }
                }
            }
            // item 8.20 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.18)
            if ( verificarInsercaoItem(ConstantesProdocente.PRODUCAO_DESENHO_NACIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof AudioVisual ) {
                    AudioVisual p = (AudioVisual) producao;

                    if ( p.getTipoRegiao() != null &&  p.getTipoRegiao().getId() == TipoRegiao.NACIONAL &&
                             p.getSubTipoArtistico().getId() == SubTipoArtistico.DESENHO_AUDIO_VISUAL) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_DESENHO_NACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            //item 8.21 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.19)
            if ( verificarInsercaoItem(ConstantesProdocente.PRODUCAO_DESENHO_LOCAL_REGIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof AudioVisual ) {
                    AudioVisual p = (AudioVisual) producao;

                    if (   (p.getTipoRegiao() == null ||
                    			(p.getTipoRegiao().getId() == TipoRegiao.REGIONAL ||
                    					p.getTipoRegiao().getId() == TipoRegiao.LOCAL)) &&
                            p.getSubTipoArtistico().getId() == SubTipoArtistico.DESENHO_AUDIO_VISUAL) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_DESENHO_LOCAL_REGIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            //item 8.22 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.17)
            if ( verificarInsercaoItem(ConstantesProdocente.PRODUCAO_ESCULTURA_INTERNACIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof AudioVisual ) {
                    AudioVisual p = (AudioVisual) producao;

                    if ( p.getTipoRegiao() != null &&  p.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL &&
                           p.getSubTipoArtistico().getId() == SubTipoArtistico.ESCULTURA_AUDIO_VISUAL) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_ESCULTURA_INTERNACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }
            // item 8.23 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.18)
            if ( verificarInsercaoItem(ConstantesProdocente.PRODUCAO_ESCULTURA_NACIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof AudioVisual ) {
                    AudioVisual p = (AudioVisual) producao;

                    if ( p.getTipoRegiao() != null &&  p.getTipoRegiao().getId() == TipoRegiao.NACIONAL &&
                             p.getSubTipoArtistico().getId() == SubTipoArtistico.ESCULTURA_AUDIO_VISUAL) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_ESCULTURA_NACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            //item 8.24 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.19)
            if ( verificarInsercaoItem(ConstantesProdocente.PRODUCAO_ESCULTURA_LOCAL_REGIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof AudioVisual ) {
                    AudioVisual p = (AudioVisual) producao;

                    if (   (p.getTipoRegiao() == null || p.getTipoRegiao().getId() == TipoRegiao.REGIONAL ||
                            p.getTipoRegiao().getId() == TipoRegiao.LOCAL) &&
                            p.getSubTipoArtistico().getId() == SubTipoArtistico.ESCULTURA_AUDIO_VISUAL) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_ESCULTURA_LOCAL_REGIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            //item 8.25 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.17)
            if ( verificarInsercaoItem(ConstantesProdocente.PRODUCAO_FOTOGRAFIA_INTERNACIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof AudioVisual ) {
                    AudioVisual p = (AudioVisual) producao;

                    if (  p.getTipoRegiao() != null && p.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL &&
                           p.getSubTipoArtistico().getId() == SubTipoArtistico.FOTOGRAFIAS_AUDIO_VISUAL) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_FOTOGRAFIA_INTERNACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }
            // item 8.26 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.18)
            if ( verificarInsercaoItem(ConstantesProdocente.PRODUCAO_FOTOGRAFIA_NACIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof AudioVisual ) {
                    AudioVisual p = (AudioVisual) producao;

                    if (  p.getTipoRegiao() != null && p.getTipoRegiao().getId() == TipoRegiao.NACIONAL &&
                             p.getSubTipoArtistico().getId() == SubTipoArtistico.FOTOGRAFIAS_AUDIO_VISUAL) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_FOTOGRAFIA_NACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            // item 8.27 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.19)
            if ( verificarInsercaoItem(ConstantesProdocente.PRODUCAO_FOTOGRAFIA_LOCAL_REGIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof AudioVisual ) {
                    AudioVisual p = (AudioVisual) producao;

                    if (   (p.getTipoRegiao() == null || p.getTipoRegiao().getId() == TipoRegiao.REGIONAL ||
                            p.getTipoRegiao().getId() == TipoRegiao.LOCAL) &&
                            p.getSubTipoArtistico().getId() == SubTipoArtistico.FOTOGRAFIAS_AUDIO_VISUAL) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_FOTOGRAFIA_LOCAL_REGIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }

                }
            }
            // item 8.28 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.17)
            if ( verificarInsercaoItem(ConstantesProdocente.PRODUCAO_GRAVURAS_INTERNACIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof AudioVisual ) {
                    AudioVisual p = (AudioVisual) producao;

                    if ( p.getTipoRegiao() != null &&  p.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL &&
                           p.getSubTipoArtistico().getId() == SubTipoArtistico.GRAVURAS_AUDIO_VISUAL) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_GRAVURAS_INTERNACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }
            // item 8.29 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.18)
            if ( verificarInsercaoItem(ConstantesProdocente.PRODUCAO_GRAVURAS_NACIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof AudioVisual ) {
                    AudioVisual p = (AudioVisual) producao;

                    if (  p.getTipoRegiao() != null && p.getTipoRegiao().getId() == TipoRegiao.NACIONAL &&
                             p.getSubTipoArtistico().getId() == SubTipoArtistico.GRAVURAS_AUDIO_VISUAL) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_GRAVURAS_NACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            // item 8.30 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.19)
            if ( verificarInsercaoItem(ConstantesProdocente.PRODUCAO_GRAVURAS_LOCAL_REGIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof AudioVisual ) {
                    AudioVisual p = (AudioVisual) producao;

                    if (   (p.getTipoRegiao() == null || p.getTipoRegiao().getId() == TipoRegiao.REGIONAL ||
                            p.getTipoRegiao().getId() == TipoRegiao.LOCAL) &&
                            p.getSubTipoArtistico().getId() == SubTipoArtistico.GRAVURAS_AUDIO_VISUAL) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_GRAVURAS_LOCAL_REGIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            //item 8.31 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.17)
            if (verificarInsercaoItem(ConstantesProdocente.PRODUCAO_PINTURA_INTERNACIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof AudioVisual ) {
                    AudioVisual p = (AudioVisual) producao;

                    if ( p.getTipoRegiao() != null &&  p.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL &&
                           p.getSubTipoArtistico().getId() == SubTipoArtistico.PINTURA_AUDIO_VISUAL) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_PINTURA_INTERNACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            // item 8.32 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.18)
            if ( verificarInsercaoItem(ConstantesProdocente.PRODUCAO_PINTURA_NACIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof AudioVisual ) {
                    AudioVisual p = (AudioVisual) producao;

                    if ( p.getTipoRegiao() != null &&  p.getTipoRegiao().getId() == TipoRegiao.NACIONAL &&
                             p.getSubTipoArtistico().getId() == SubTipoArtistico.PINTURA_AUDIO_VISUAL) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_PINTURA_NACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            // item 8.33 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.19)
            if (verificarInsercaoItem(ConstantesProdocente.PRODUCAO_PINTURA_LOCAL_REGIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof AudioVisual ) {
                    AudioVisual p = (AudioVisual) producao;

                    if (   (p.getTipoRegiao() == null || p.getTipoRegiao().getId() == TipoRegiao.REGIONAL ||
                            p.getTipoRegiao().getId() == TipoRegiao.LOCAL) &&
                            p.getSubTipoArtistico().getId() == SubTipoArtistico.PINTURA_AUDIO_VISUAL) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_PINTURA_LOCAL_REGIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            // item 8.34 especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.17)
            if ( verificarInsercaoItem(ConstantesProdocente.PRODUCAO_INSTALACAO_INTERNACIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof AudioVisual ) {
                    AudioVisual p = (AudioVisual) producao;

                    if ( p.getTipoRegiao() != null &&  p.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL &&
                           p.getSubTipoArtistico().getId() == SubTipoArtistico.PINTURA_AUDIO_VISUAL) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_INSTALACAO_INTERNACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }
            // item 8.35 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.18)
            if ( verificarInsercaoItem(ConstantesProdocente.PRODUCAO_INSTALACAO_NACIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof AudioVisual ) {
                    AudioVisual p = (AudioVisual) producao;

                    if (  p.getTipoRegiao() != null && p.getTipoRegiao().getId() == TipoRegiao.NACIONAL &&
                             p.getSubTipoArtistico().getId() == SubTipoArtistico.INSTALACAO_AUDIO_VISUAL) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_INSTALACAO_NACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            // item 8.36 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto de 3.19)
            if (  verificarInsercaoItem(ConstantesProdocente.PRODUCAO_INSTALACAO_LOCAL_REGIONAL,
    	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof AudioVisual ) {
                    AudioVisual p = (AudioVisual) producao;

                    if (   (p.getTipoRegiao() == null || p.getTipoRegiao().getId() == TipoRegiao.REGIONAL ||
                            p.getTipoRegiao().getId() == TipoRegiao.LOCAL) &&
                            p.getSubTipoArtistico().getId() == SubTipoArtistico.INSTALACAO_AUDIO_VISUAL) {

                        adicionarProducao(ConstantesProdocente.PRODUCAO_INSTALACAO_LOCAL_REGIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            //item 8.37 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto do item 3.28)
            if ( verificarInsercaoItem(ConstantesProdocente.COMPOSICAO_MUSICAL_INTERNACIONAL,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof AudioVisual ) {
                    AudioVisual p = (AudioVisual) producao;

                    if ( p.getTipoRegiao() != null && p.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL &&
                          p.getSubTipoArtistico().getId() == SubTipoArtistico.COMPOSICAO_MUSICAL_AUDIO_VISUAL) {

                        adicionarProducao(ConstantesProdocente.COMPOSICAO_MUSICAL_INTERNACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }
            // item 8.38 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto do item 3.28)
            if ( verificarInsercaoItem(ConstantesProdocente.COMPOSICAO_MUSICAL_NACIONAL,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof AudioVisual ) {
                    AudioVisual p = (AudioVisual) producao;


                    if ( p.getTipoRegiao() != null && p.getTipoRegiao().getId() == TipoRegiao.NACIONAL &&
                          p.getSubTipoArtistico().getId() == SubTipoArtistico.COMPOSICAO_MUSICAL_AUDIO_VISUAL) {

                        adicionarProducao(ConstantesProdocente.COMPOSICAO_MUSICAL_NACIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

            // item 8.43 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto do item 3.13)
            if (verificarInsercaoItem(ConstantesProdocente.ARTIGO_TC_ANAIS_REGIONAL,
    	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
                if ( producao instanceof PublicacaoEvento ) {
                    PublicacaoEvento p = (PublicacaoEvento) producao;

                    if (   p.getTipoRegiao().getId() == TipoRegiao.REGIONAL &&
                           ( p.getTipoEvento() != null &&
                            (p.getTipoEvento().getId() ==TipoEvento.CONFERENCIA ||
                            p.getTipoEvento().getId() ==TipoEvento.SEMINARIO ||
                            p.getTipoEvento().getId() ==TipoEvento.WORKSHOP ||
                            p.getTipoEvento().getId() ==TipoEvento.CONGRESSO)) &&
                            p.getNatureza().equals('T')) {

                        adicionarProducao(ConstantesProdocente.ARTIGO_TC_ANAIS_REGIONAL,
                                tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                    }
                }
            }

        // item 8.44 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto do item 3.13)
        if ( verificarInsercaoItem(ConstantesProdocente.ARTIGO_TC_ANAIS_LOCAL,
	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
            if ( producao instanceof PublicacaoEvento ) {
                PublicacaoEvento p = (PublicacaoEvento) producao;

                if (   p.getTipoRegiao().getId() == TipoRegiao.LOCAL &&
                       ( p.getTipoEvento() != null &&
                    	(p.getTipoEvento().getId() == TipoEvento.CONFERENCIA ||
                        p.getTipoEvento().getId() ==TipoEvento.SEMINARIO ||
                        p.getTipoEvento().getId() ==TipoEvento.WORKSHOP ||
                        p.getTipoEvento().getId() ==TipoEvento.CONGRESSO)) &&
                        p.getNatureza().equals('T')) {

                    adicionarProducao(ConstantesProdocente.ARTIGO_TC_ANAIS_LOCAL,
                            tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                }
            }
        }

		//item 8.45 (utilizado para tipos de consultas de tipo de região especificas, ou seja subconjunto do item 3.4)
        // Participações como EQUIPE adicionadas em 05/07/2007 após conversa com prof. Bernardete
        if ( verificarInsercaoItem(ConstantesProdocente.PUBLICACAO_LIVRO_INTERNACIONAL,
	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
            if ( producao instanceof Livro ) {
                Livro l = (Livro) producao;

                if (  l.getTipoParticipacao() != null &&
                		(l.getTipoParticipacao().getId() == TipoParticipacao.TRABALHO_INDIVIDUAL_LIVRO ||
                				l.getTipoParticipacao().getId() == TipoParticipacao.TRABALHO_INDIVIDUAL_ARTIGO_PERIODICOS_JORNAIS ||
                				l.getTipoParticipacao().getId() == TipoParticipacao.EQUIPE_COLABORADOR_ARTIGO_PERIODICOS_JORNAIS ||
                				l.getTipoParticipacao().getId() == TipoParticipacao.EQUIPE_COLABORADOR_LIVRO ||
                				l.getTipoParticipacao().getId() == TipoParticipacao.EQUIPE_RESPONSAVEL_ARTIGO_PERIODICOS_JORNAIS ||
                				l.getTipoParticipacao().getId() == TipoParticipacao.EQUIPE_RESPONSAVEL_LIVRO)
                			&& l.getTipoRegiao() != null && l.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL) {

                    adicionarProducao(ConstantesProdocente.PUBLICACAO_LIVRO_INTERNACIONAL,
                            tabelaProducoes, producao, anoVigencia, l.getDataProducao());
                }

            }
        }

        // item 8.46 (utilizado para tipos de consultas de tipo de região especificas, ou seja subconjunto do item 3.4)
        // Participações como EQUIPE adicionadas em 05/07/2007 após conversa com prof. Bernardete
        if ( verificarInsercaoItem(ConstantesProdocente.PUBLICACAO_LIVRO_NACIONAL,
	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
            if ( producao instanceof Livro ) {
                Livro l = (Livro) producao;

                if (  l.getTipoParticipacao() != null &&
                		(l.getTipoParticipacao().getId() == TipoParticipacao.TRABALHO_INDIVIDUAL_LIVRO ||
                				l.getTipoParticipacao().getId() == TipoParticipacao.TRABALHO_INDIVIDUAL_ARTIGO_PERIODICOS_JORNAIS ||
                				l.getTipoParticipacao().getId() == TipoParticipacao.EQUIPE_COLABORADOR_ARTIGO_PERIODICOS_JORNAIS ||
                				l.getTipoParticipacao().getId() == TipoParticipacao.EQUIPE_COLABORADOR_LIVRO ||
                				l.getTipoParticipacao().getId() == TipoParticipacao.EQUIPE_RESPONSAVEL_ARTIGO_PERIODICOS_JORNAIS ||
                				l.getTipoParticipacao().getId() == TipoParticipacao.EQUIPE_RESPONSAVEL_LIVRO)
                			&& l.getTipoRegiao() != null && l.getTipoRegiao().getId() == TipoRegiao.NACIONAL) {

                    adicionarProducao(ConstantesProdocente.PUBLICACAO_LIVRO_NACIONAL,
                            tabelaProducoes, producao, anoVigencia, l.getDataProducao());

                }
            }
        }

        // item 8.55 (utilizado para tipos de consultas de tipo de região especificas, ou seja subconjunto do item 3.4)
        // Como determinado em conversa com Profa. Bernardete em 03/07/2007
        // Participações como EQUIPE adicionadas em 05/07/2007 após conversa com prof. Bernardete
        if ( verificarInsercaoItem(ConstantesProdocente.PUBLICACAO_LIVRO_LOCAL_REGIONAL,
	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
            if ( producao instanceof Livro ) {
                Livro l = (Livro) producao;

                if (  l.getTipoParticipacao() != null &&
                		(l.getTipoParticipacao().getId() == TipoParticipacao.TRABALHO_INDIVIDUAL_LIVRO ||
                				l.getTipoParticipacao().getId() == TipoParticipacao.TRABALHO_INDIVIDUAL_ARTIGO_PERIODICOS_JORNAIS  ||
                				l.getTipoParticipacao().getId() == TipoParticipacao.EQUIPE_COLABORADOR_ARTIGO_PERIODICOS_JORNAIS ||
                				l.getTipoParticipacao().getId() == TipoParticipacao.EQUIPE_COLABORADOR_LIVRO ||
                				l.getTipoParticipacao().getId() == TipoParticipacao.EQUIPE_RESPONSAVEL_ARTIGO_PERIODICOS_JORNAIS ||
                				l.getTipoParticipacao().getId() == TipoParticipacao.EQUIPE_RESPONSAVEL_LIVRO) &&
                        (l.getTipoRegiao() == null || l.getTipoRegiao().getId() == TipoRegiao.LOCAL || l.getTipoRegiao().getId() == TipoRegiao.REGIONAL )) {

                    adicionarProducao(ConstantesProdocente.PUBLICACAO_LIVRO_LOCAL_REGIONAL,
                            tabelaProducoes, producao, anoVigencia, l.getDataProducao());

                }
            }
        }

        // item 8.47(item especifico para produções internacionais, SUB CONJUNTO DO ITEM 3.6)
        if ( verificarInsercaoItem(ConstantesProdocente.CAPITULO_LIVRO_INTERNACIONAL,
	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
            if ( producao instanceof Capitulo ) {
                Capitulo c = (Capitulo) producao;

                if (   ( c.getTipoParticipacao() != null && (c.getTipoParticipacao().getId() != TipoParticipacao.TRADUTOR_LIVRO ||
                        c.getTipoParticipacao().getId() != TipoParticipacao.TRADUTOR_ARTIGO_PERIODICOS_JORNAIS ||
                        c.getTipoParticipacao().getId() != TipoParticipacao.OUTROS_ARTIGO_PERIODICOS_JORNAIS ||
                        c.getTipoParticipacao().getId() != TipoParticipacao.OUTRA_PUBLICACAO_EVENTO ||
                        c.getTipoParticipacao().getId() != TipoParticipacao.OUTRO_MONTAGEM ||
                        c.getTipoParticipacao().getId() != TipoParticipacao.COLABORADOR_PROGRAMACAO_VISUAL ||
                        c.getTipoParticipacao().getId() != TipoParticipacao.OUTRO_AUDIO_VISUAL ||

                        c.getTipoParticipacao().getId() != TipoParticipacao.TRABALHO_INDIVIDUAL_ARTIGO_PERIODICOS_JORNAIS ||
                        c.getTipoParticipacao().getId() != TipoParticipacao.EQUIPE_RESPONSAVEL_ARTIGO_PERIODICOS_JORNAIS ||
                        c.getTipoParticipacao().getId() != TipoParticipacao.EQUIPE_COLABORADOR_ARTIGO_PERIODICOS_JORNAIS)) &&
                        c.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL) {

                    adicionarProducao(ConstantesProdocente.CAPITULO_LIVRO_INTERNACIONAL,
                            tabelaProducoes, producao, anoVigencia, c.getDataProducao());
                }
            }
        }

        //item 8.48(item especifico para produções nacionais, SUB CONJUNTO DO ITEM 3.6)
        if ( verificarInsercaoItem(ConstantesProdocente.CAPITULO_LIVRO_NACIONAL,
	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
            if ( producao instanceof Capitulo ) {
                Capitulo c = (Capitulo) producao;

                if (  c.getTipoParticipacao() != null && ( c.getTipoParticipacao().getId() != TipoParticipacao.TRADUTOR_LIVRO ||
                        c.getTipoParticipacao().getId() != TipoParticipacao.TRADUTOR_ARTIGO_PERIODICOS_JORNAIS ||
                        c.getTipoParticipacao().getId() != TipoParticipacao.OUTROS_ARTIGO_PERIODICOS_JORNAIS ||
                        c.getTipoParticipacao().getId() != TipoParticipacao.OUTRA_PUBLICACAO_EVENTO ||
                        c.getTipoParticipacao().getId() != TipoParticipacao.OUTRO_MONTAGEM ||
                        c.getTipoParticipacao().getId() != TipoParticipacao.COLABORADOR_PROGRAMACAO_VISUAL ||
                        c.getTipoParticipacao().getId() != TipoParticipacao.OUTRO_AUDIO_VISUAL ||

                        c.getTipoParticipacao().getId() != TipoParticipacao.TRABALHO_INDIVIDUAL_ARTIGO_PERIODICOS_JORNAIS ||
                        c.getTipoParticipacao().getId() != TipoParticipacao.EQUIPE_RESPONSAVEL_ARTIGO_PERIODICOS_JORNAIS ||
                        c.getTipoParticipacao().getId() != TipoParticipacao.EQUIPE_COLABORADOR_ARTIGO_PERIODICOS_JORNAIS) &&
                        c.getTipoRegiao().getId() == TipoRegiao.NACIONAL) {

                    adicionarProducao(ConstantesProdocente.CAPITULO_LIVRO_NACIONAL,
                            tabelaProducoes, producao, 0, c.getDataProducao());
                }
            }
        }

        //item 8.56(item especifico para produções nacionais, SUB CONJUNTO DO ITEM 3.6)
        // Como determinado em conversa com Profa. Bernardete em 03/07/2007
        if ( verificarInsercaoItem(ConstantesProdocente.CAPITULO_LIVRO_LOCAL_REGIONAL,
	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
            if ( producao instanceof Capitulo ) {
                Capitulo c = (Capitulo) producao;

                if (  c.getTipoParticipacao() != null && ( c.getTipoParticipacao().getId() != TipoParticipacao.TRADUTOR_LIVRO ||
                        c.getTipoParticipacao().getId() != TipoParticipacao.TRADUTOR_ARTIGO_PERIODICOS_JORNAIS ||
                        c.getTipoParticipacao().getId() != TipoParticipacao.OUTROS_ARTIGO_PERIODICOS_JORNAIS ||
                        c.getTipoParticipacao().getId() != TipoParticipacao.OUTRA_PUBLICACAO_EVENTO ||
                        c.getTipoParticipacao().getId() != TipoParticipacao.OUTRO_MONTAGEM ||
                        c.getTipoParticipacao().getId() != TipoParticipacao.COLABORADOR_PROGRAMACAO_VISUAL ||
                        c.getTipoParticipacao().getId() != TipoParticipacao.OUTRO_AUDIO_VISUAL ||

                        c.getTipoParticipacao().getId() != TipoParticipacao.TRABALHO_INDIVIDUAL_ARTIGO_PERIODICOS_JORNAIS ||
                        c.getTipoParticipacao().getId() != TipoParticipacao.EQUIPE_RESPONSAVEL_ARTIGO_PERIODICOS_JORNAIS ||
                        c.getTipoParticipacao().getId() != TipoParticipacao.EQUIPE_COLABORADOR_ARTIGO_PERIODICOS_JORNAIS) &&
                        (c.getTipoRegiao().getId() == TipoRegiao.LOCAL || c.getTipoRegiao().getId() == TipoRegiao.REGIONAL )) {

                    adicionarProducao(ConstantesProdocente.CAPITULO_LIVRO_LOCAL_REGIONAL,
                            tabelaProducoes, producao, 0, c.getDataProducao());
                }
            }
        }

        // item 8.51(item que nÃ£o pertence ao relatório padrão, servindo como subconjunto do item 3.8)
        if ( verificarInsercaoItem(ConstantesProdocente.ORGANIZACAO_LIVRO_INTERNACIONAL,
	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
            if ( producao instanceof Livro ) {
                Livro l = (Livro) producao;

                if ( l.getTipoParticipacao() != null &&
                	 l.getTipoParticipacao().getId() == TipoParticipacao.ORGANIZADOR_LIVRO &&
                	 l.getTipoRegiao() != null &&
                	 l.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL ) {

                    adicionarProducao(ConstantesProdocente.ORGANIZACAO_LIVRO_INTERNACIONAL,
                            tabelaProducoes, producao, anoVigencia, l.getDataProducao());
                }
            }
        }

        // item 8.52(item que nÃ£o pertence ao relatório padrão, servindo como subconjunto do item 3.8)
        if ( verificarInsercaoItem(ConstantesProdocente.ORGANIZACAO_LIVRO_NACIONAL,
	 			itensAvaliacao, false, validades, anoReferencia, anoVigencia)) {
            if ( producao instanceof Livro ) {
                Livro l = (Livro) producao;
                if ( l.getTipoParticipacao() != null &&
                	 l.getTipoParticipacao().getId() == TipoParticipacao.ORGANIZADOR_LIVRO &&
                   	 l.getTipoRegiao() != null &&
                     l.getTipoRegiao().getId() == TipoRegiao.NACIONAL ) {

                    adicionarProducao(ConstantesProdocente.ORGANIZACAO_LIVRO_NACIONAL,
                            tabelaProducoes, producao, anoVigencia, l.getDataProducao());
                }
            }
        }

        // item 8.59
        if ( verificarInsercaoItem(ConstantesProdocente.PARECERISTA_NACIONAL,
	 			itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
            if ( producao instanceof ParticipacaoComissaoOrgEventos ) {
                ParticipacaoComissaoOrgEventos p = (ParticipacaoComissaoOrgEventos) producao;

                if (p.getAmbito() != null
                		&& p.getAmbito().getId() == TipoRegiao.NACIONAL &&
                	p.getTipoParticipacaoOrganizacao() != null &&
                    (p.getTipoParticipacaoOrganizacao().getId() == TipoParticipacaoOrganizacaoEventos.REVISOR_PERIODICOS_JORNAIS_SIMILARES
                    || p.getTipoParticipacaoOrganizacao().getId() == TipoParticipacaoOrganizacaoEventos.CONSULTOR_AD_HOC_REVISTA_CIENTIFICA) ) {

                    adicionarProducao(ConstantesProdocente.PARECERISTA_NACIONAL,
                            tabelaProducoes, producao, anoVigencia, p.getDataProducao());
                }
            }
        }

        // item 8.60
        if ( verificarInsercaoItem(ConstantesProdocente.PARECERISTA_INTERNACIONAL,
        		itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
        	if ( producao instanceof ParticipacaoComissaoOrgEventos ) {
        		ParticipacaoComissaoOrgEventos p = (ParticipacaoComissaoOrgEventos) producao;

        		if (p.getAmbito() != null
        				&& p.getAmbito().getId() == TipoRegiao.INTERNACIONAL &&
        				p.getTipoParticipacaoOrganizacao() != null &&
        				(p.getTipoParticipacaoOrganizacao().getId() == TipoParticipacaoOrganizacaoEventos.REVISOR_PERIODICOS_JORNAIS_SIMILARES
        						|| p.getTipoParticipacaoOrganizacao().getId() == TipoParticipacaoOrganizacaoEventos.CONSULTOR_AD_HOC_REVISTA_CIENTIFICA) ) {

        			adicionarProducao(ConstantesProdocente.PARECERISTA_INTERNACIONAL,
        					tabelaProducoes, producao, anoVigencia, p.getDataProducao());
        		}
        	}
        }
    }
		return tabelaProducoes;
	}

	
	
	
	@Deprecated
	public static boolean verificarInsercaoItem(int codigoItem, List<Integer> itensAvaliacao, boolean todos,
			Hashtable<Integer, Integer> validades, int anoReferencia, Integer anoVigencia) {

		// Validar tipo
		boolean tipoValido = itensAvaliacao.contains(codigoItem);

		// Validar ano
		boolean anoValido = true;
		if (validades != null) {
			Integer validadeProducao = validades.get(codigoItem);
			int anoInicio = (validadeProducao == null ? anoReferencia : anoReferencia - validadeProducao + 1);

			if (anoVigencia < anoInicio ) {
				anoValido = false;
			}
		}

		return anoValido && (tipoValido || todos);
	}

	
	*/

}