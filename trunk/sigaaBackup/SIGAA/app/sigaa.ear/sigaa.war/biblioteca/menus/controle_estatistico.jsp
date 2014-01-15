<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsf/html"      prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core"      prefix="f" %>
<%@ taglib uri="/tags/rich"                        prefix="rich"%>


<rich:tabPanel switchType="client" id="tabPanelControleEstatistico" selectedTab="#{relatoriosBibliotecaUtilMBean.tabAtiva}">
	
	<rich:tab label="Processos Técnicos" id="tabProcessosTecnicos" ontabenter="$('subAba').value = 'tabProcessosTecnicos'">
		<ul>
			<li>
				Classificações Bibliográficas
				<ul>
					
					<li>
						<h:commandLink value="Total de Títulos e Materiais"
								action="#{relatorioTotalTitulosMateriaisMBean.iniciar}"
								onclick="setAba('relatorios')" id="linkTotalTitulosMateriais" />
					</li>
					<li>
						<h:commandLink value="Crescimento por Classificação" 
								action="#{relatorioCrescimentoPorClassificacaoMBean.iniciar}"
								onclick="setAba('relatorios')" id="linkCrescimentoPorClasificação">
							<f:param name="tipoClasse" value="false" />
						</h:commandLink>
					</li>
					
					<li>
						<h:commandLink value="Materiais por Faixa de Classificação"
								action="#{relatorioPorFaixaDeClassificacaoMBean.iniciar}" onclick="setAba('relatorios')" id="linkMateriaisPorFaixa">
						</h:commandLink>
					</li>
					
				</ul>
			</li>
			
			<li>Áreas do CNPQ
				<ul>
					<li>
						<h:commandLink value="Total por Área CNPq" 
								action="#{relatorioTotalPorCNPqMBean.iniciar}" onclick="setAba('relatorios')"
								id="linkTotalAreaCNPQ"/>
					</li>
					<li>
						<h:commandLink value="Crescimento por Área CNPq" 
								action="#{relatorioCrescimentoPorCNPqMBean.iniciar}" onclick="setAba('relatorios')"
								id="linkCrescimentoAreaCNPQ"/>
					</li>
					<%-- Os itens abaixo foram comentados pois o relatório 'Total por Área CNPq' já atende ambas as necessidades --%>
					<%-- <li>
						<h:commandLink value="Total por Coleção e Área CNPQ" 
								action="#{relatorioTotalPorColecaoCNPq.iniciar}" onclick="setAba('relatorios')"
								id="linkTotalColecaoAreaCNPQ"/></li>
					<li>
						<h:commandLink action="#{relatorioTotalPorTipoMaterialCNPq.iniciar}"
								value="Total por Tipo de Material e Área CNPq" onclick="setAba('relatorios')"
								id="linkTotalMaterialAreaCNPQ" />
					</li> --%>
				</ul>
			</li>
		
			<li>Periódicos
				<ul>
					<li>
						<h:commandLink value="Total de Periódicos por Classificação"
								action="#{relatorioTotalPeriodicosPorClasseMBean.iniciar}"
								onclick="setAba('relatorios')" id="linkTotalPeriodicosPorClasse" />
					</li>
					<li>
						<h:commandLink value="Total de Periódicos por Área do CNPQ"
								action="#{relatorioTotalPeriodicosCNPqMBean.iniciar}"
								onclick="setAba('relatorios')" id="linkTotalPeriodicosPorCNPq"/>
					</li>
					<li>
						<h:commandLink value="Crescimento de Periódicos por Classificação"
								action="#{relatorioCrescimentoPeriodicosPorClasseMBean.iniciar}"
								onclick="setAba('relatorios')" id="linkCrescimentoPeriodicosPorClasse" />
					</li>
					<li>
						<h:commandLink value="Crescimento de Periódicos por Área do CNPQ"
								action="#{relatorioCrescimentoPeriodicosPorCNPqMBean.iniciar}"
								onclick="setAba('relatorios')" id="linkCrescimentoPeriodicosPorCNPq" />
					</li>
				</ul>
			</li>

			<li>Listagens dos Materiais do Acervo
				<ul>
					<li>
						<h:commandLink value="Listagem Geral"
								action="#{relatorioListagemGeralAcervoMBean.iniciar}"
								onclick="setAba('relatorios')" id="linkListagemGeral" />
					</li>
					<li>
						<h:commandLink value="Listagem por Faixa de Código de Barras "
								action="#{relatorioListagemPorFaixaCodigoBarraAcervoMBean.iniciar}"
								onclick="setAba('relatorios')" id="linkListagemPorFaixaCodigoBarra" />
					</li>
				</ul>
			</li>
			
			
			<li>Inventário do Acervo
				<ul>
					<li>
						<h:commandLink value="Inventário do Acervo"
							action="#{relatorioInventarioAcervoMBean.iniciar}"
							onclick="setAba('relatorios')" id="linkInventarioAcervo" />
					</li>
					<li>
						<h:commandLink value="Registros Realizados por um Usuário"
							action="#{relatorioRegistrosInventarioAcervoUsuarioMBean.iniciar}"
							onclick="setAba('relatorios')" id="linkRegistrosInventarioAcervo" />
					</li>
				</ul>
			</li>
			
			
			<li>
				<h:commandLink value="Materiais Baixados do Acervo"
						action="#{relatorioMateriaisBaixadosMBean.iniciar}"
						onclick="setAba('relatorios')" id="linkMateriaisBaixadosDoAcervo"/>
			</li>
			
			<li>
				<h:commandLink value="Materiais Transferidos entre Bibliotecas"
						action="#{relatorioMateriaisTransferidoEntreBibliotecasMBean.iniciar}"
						onclick="setAba('relatorios')" id="linkMateriaisTransferidos"/>
			</li>
			
			<li>
				<h:commandLink value="Materiais Trabalhados por Operador"
						action="#{relatorioMateriaisTrabalhadosPorOperadorMBean.iniciar}"
						onclick="setAba('relatorios')"
						id="linkMateriaisTrabalhadosPorOperador" />
			</li>
			
			<li>Históricos
				<ul>
					<li>
						<h:commandLink value="Histórico de Alterações de um Título"
								action="#{pesquisaTituloCatalograficoMBean.iniciarPesquisa}"
								onclick="setAba('relatorios')" id="linkHistoricoAlteracoesTitulo" />
					</li>
					<li>
						<h:commandLink value="Histórico de Alterações de um Material"
								action="#{emiteRelatorioHistoricosMBean.iniciarConsultaAlteracaoMaterial}" 
								onclick="setAba('relatorios')" id="linkHistoricoAlteracoesMaterial" />
					</li>
				</ul>
			</li>
			
			<li>Catalogações Trabalhadas por Operador
				<ul>
					<li>
						<h:commandLink value="Cooperação"
							action="#{relatorioCooperacaoMBean.iniciar}"
							id="lingRelatorioCooperacao"
							onclick="setAba('relatorios')"/>
					</li>
					<li>
						<h:commandLink value="Implantação"
							action="#{relatorioImplantacaoMBean.iniciar}"
							id="lingRelatorioImplantacao"
							onclick="setAba('relatorios')"/>
					</li>
				</ul>
			</li>
		</ul>
	</rich:tab>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	<rich:tab label="Circulação" id="tabCirculacao" ontabenter="$('subAba').value = 'tabCirculacao' ">
		<ul>
			
			<li>Empréstimos
				<ul>
					
					<li>
						<h:commandLink value="Empréstimos por Categoria de Usuário"
								action="#{relatorioEmprestimosPorCategoriaDeUsuarioMBean.iniciar}"
								onclick="setAba('relatorios')"
								id="linkEmprestimoCategoriaDeUsuario" />
					</li>
					<li>
						<h:commandLink value="Empréstimos por Comunidade"
								action="#{relatorioEmprestimoComunidadeMBean.iniciar}"
								onclick="setAba('relatorios')"
								id="linkEmprestimoPorComunidade" />
					</li>
					<li>
						<h:commandLink value="Empréstimos por Classificação"
								action="#{relatorioEmprestimosPorClassificacaoMBean.iniciar}"
								onclick="setAba('relatorios')"
								id="linkEmprestimosPorTipoDeMaterialClasseCDU" />
					</li>
					<li>
						<h:commandLink value="Títulos com mais Empréstimos"
								action="#{relatorioTitulosComMaisEmprestimos.iniciar}"
								onclick="setAba('relatorios')" id="linkTitulosComMaisEmprestimos" />
					</li>
					
					<li>
						<h:commandLink value="Histórico de Empréstimos de um Material"
								action="#{emiteRelatorioHistoricosMBean.iniciarConsultaEmprestimos}"
								onclick="setAba('relatorios')" id="linkHistoricoEmprestimosMaterial" />
					</li>
					
					<li>
						<h:commandLink value="Renovações por Módulo de Acesso"
								action="#{relatorioRenovacoesPorModuloMBean.iniciar}"
								onclick="setAba('relatorios')" id="linkRenovacoesPorModuloDeAcesso" />
					</li>
					<li>
						<h:commandLink value="Empréstimos por Curso"
								action="#{relatorioEmprestimosPorCursoMBean.iniciar}"
								onclick="setAba('relatorios')"
								id="linkEmprestimosPorCurso" />
					</li>
					<li>
						<h:commandLink value="Empréstimos por Unidade"
								action="#{relatorioEmprestimosPorUnidadeMBean.iniciar}"
								onclick="setAba('relatorios')"
								id="linkEmprestimosPorUnidade" />
					</li>
					<li>
						<h:commandLink value="Empréstimos por Tipo de Empréstimo"
								action="#{relatorioEmprestimosPorTipoEmprestimoMBean.iniciar}"
								onclick="setAba('relatorios')"
								id="linkEmprestimosPorTipoEmprestimo" />
					</li>
					<li>
						<h:commandLink value="Empréstimos por Turno"
								action="#{relatorioEmprestimosPorTurnoMBean.iniciar}"
								onclick="setAba('relatorios')"
								id="linkEmprestimosPorTurno" />
					</li>
				</ul>
			</li>
			
			
			<li>Usuários
				<ul>
					<li>
						<h:commandLink
							value="Usuários com Potencial de Empréstimo"
							id="linkUsuariosComPotencial" onclick="setAba('relatorios')"
							action="#{relatorioUsuariosComPotencialDeEmprestimoMBean.iniciar}"/>
					</li>
					
					<li>
						<h:commandLink value="Movimentação de Usuários por Período"
							action="#{relatorioMotivimentacaoDeUsuariosMBean.iniciar}"
							onclick="setAba('relatorios')"
							id="linkUsuariosCadastrados" />
					</li>	
						
					<li>
						<h:commandLink value="Quantitativo de Usuários que Fizeram Empréstimo por Período"
								action="#{relatorioDeUsuariosComEmprestimoNumPeriodo.iniciar}"
								onclick="setAba('relatorios')"
								id="linkUsuariosComEmprestimosNumPeriodo" />
					</li>
					
					<li>
						<h:commandLink value="Quantitativo de Usuários que Devolveram Empréstimo por Período"
								action="#{relatorioDeUsuariosComEmprestimosDevolvidosNumPeriodo.iniciar}"
								onclick="setAba('relatorios')"
								id="linkUsuariosComEmprestimosDevolvidosNumPeriodo" />
					</li>
					
					<li>
						<h:commandLink value="Usuários em Atraso"
								action="#{relatorioSituacaoDosUsuariosMBean.iniciarRelatorioUsuariosEmAtraso}"
								onclick="setAba('relatorios')"
								id="linkUsuariosEmAtraso" />
					</li>
					<li>
						<h:commandLink value="Usuários Suspensos"
								action="#{relatorioSituacaoDosUsuariosMBean.iniciarRelatorioUsuariosSuspensos}"
								onclick="setAba('relatorios')"
								id="linkUsuariosSuspensos" />
					</li>
					<li>
						<h:commandLink id="linkUsuariosComMultas"  value="Usuários com Multas"
								action="#{relatorioSituacaoDosUsuariosMBean.iniciarRelatorioUsuariosComMulta}"
								onclick="setAba('relatorios')"/>
					</li>
					<li>
						<h:commandLink value="Usuários com Empréstimos Ativos"
								action="#{relatorioSituacaoDosUsuariosMBean.iniciarRelatorioUsuariosComEmprestimosAtivos}"
								onclick="setAba('relatorios')"
								id="linkUsuariosComEmprestimosAtivos" />
					</li>
					<li>
						<h:commandLink value="Usuários com mais Empréstimos"
								action="#{relatorioDeUsuariosComMaisEmprestimos.iniciar}"
								onclick="setAba('relatorios')"
								id="linkUsuariosComMaisEmprestimos" />
					</li>
				</ul>
			</li>
			
			
			<li>
				<h:commandLink value="Consultas Locais por Turno "
						action="#{relatorioConsultasLocaisPorTurnoMBean.iniciar}"
						onclick="setAba('relatorios')"
						id="linkConsultasLocaisPorTurno" />
			</li>
			
			<li>
				<h:commandLink value="Consultas Locais por Ano"
						action="#{relatorioConsultasLocaisPorAnoMBean.iniciar}"
						onclick="setAba('relatorios')" id="linkConsultasLocaisPorAno"/>
			</li>
			
			<li>
				<h:commandLink value="Ocorrências de Perda de Material"
						action="#{relatorioOcorrenciasPerdaMaterialMBean.iniciar}"
						onclick="setAba('relatorios')" id="linkOcorrenciaPerdeMaterial" />
			</li>
			
			<li>
				<h:commandLink value="Frequência de Usuários"
						action="#{relatorioFrequenciaDosUsuariosMBean.iniciar}"
						onclick="setAba('relatorios')" id="linkFrequenciaDeUsuarios" />
			</li>
			
			<li>Pendências
				<ul>
					<li>
						<h:commandLink value="Usuários Finalizados com Pendências na Biblioteca"
								action="#{relatorioUsuariosFinalizadosComPedenciasBibliotecaMBean.iniciar}"
								onclick="setAba('relatorios')"
								id="linkUsuariosFinalizadosComEmprestimos" />
					</li>
				</ul>
			</li>
			
			<li>Punições
				<ul>
				
					<li>Suspensões
						<ul>
							<li>
								<h:commandLink value="Suspensões Estornadas por Período"
										action="#{relatorioSuspensoesEstornadasBibliotecaMBean.iniciar}"
										onclick="setAba('relatorios')"
										id="linkSuspensoesEstornadas" />
							</li>
						</ul>
					</li>
						
					<li>Multas
						<ul>
							
							<li>
								<h:commandLink id="linkRelatorioPagamentosRecebidos" value="Pagamentos Recebidos por Período"
										action="#{relatorioMultasPagasBibliotecaMBean.iniciar}"
										onclick="setAba('relatorios')" />
							</li>
							<li>
								<h:commandLink id="linkRelatorioPagamentosAReceber" value="Pagamentos a Receber por Período"
										action="#{relatorioMultasNaoPagasBibliotecaMBean.iniciar}"
										onclick="setAba('relatorios')"/>
							</li>
							<li>
								<h:commandLink id="linkRelatorioMultasEstornadas"  value="Multas Estornadas por Período"
										action="#{relatorioMultasEstornadasBibliotecaMBean.iniciar}"
										onclick="setAba('relatorios')" />
							</li>
						</ul>
					</li>
			
				
				</ul>
			</li>
			
		</ul>
	</rich:tab>
	
	
	
	
	
	
	<rich:tab label="Informação e Referência" id="tabInfoReferencia" ontabenter="$('subAba').value = 'tabInfoReferencia' ">
	
	</rich:tab>
	
	
	
	
	
	
	
	
	
	<rich:tab label="Outros" id="tabOutros" ontabenter="$('subAba').value = 'tabOutros' ">
		
		<ul>
			
			<li>
				<h:commandLink id="linkNumerosDoSistema" value="Números Gerais do Sistema" action="#{relatorioNumerosDoSistemaMBean.iniciar}" onclick="setAba('relatorios')" />
			</li>
			
			
			<li>Disseminação Seletiva da Informação
				<ul>
					<li>
						<h:commandLink value="Temas de Maior Interesse dos Usuários" action="#{relatorioTemasMaiorInteresseDSIMBean.iniciar}" id="linkRelatorioTemasMaiorInteresse" onclick="setAba('relatorios')"/>
					</li>
				</ul>
			</li>
			
		</ul>
		
		
	</rich:tab>
	
</rich:tabPanel>

