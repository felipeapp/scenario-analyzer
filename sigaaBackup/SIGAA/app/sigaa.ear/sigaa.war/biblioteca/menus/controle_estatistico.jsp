<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsf/html"      prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core"      prefix="f" %>
<%@ taglib uri="/tags/rich"                        prefix="rich"%>


<rich:tabPanel switchType="client" id="tabPanelControleEstatistico" selectedTab="#{relatoriosBibliotecaUtilMBean.tabAtiva}">
	
	<rich:tab label="Processos T�cnicos" id="tabProcessosTecnicos" ontabenter="$('subAba').value = 'tabProcessosTecnicos'">
		<ul>
			<li>
				Classifica��es Bibliogr�ficas
				<ul>
					
					<li>
						<h:commandLink value="Total de T�tulos e Materiais"
								action="#{relatorioTotalTitulosMateriaisMBean.iniciar}"
								onclick="setAba('relatorios')" id="linkTotalTitulosMateriais" />
					</li>
					<li>
						<h:commandLink value="Crescimento por Classifica��o" 
								action="#{relatorioCrescimentoPorClassificacaoMBean.iniciar}"
								onclick="setAba('relatorios')" id="linkCrescimentoPorClasifica��o">
							<f:param name="tipoClasse" value="false" />
						</h:commandLink>
					</li>
					
					<li>
						<h:commandLink value="Materiais por Faixa de Classifica��o"
								action="#{relatorioPorFaixaDeClassificacaoMBean.iniciar}" onclick="setAba('relatorios')" id="linkMateriaisPorFaixa">
						</h:commandLink>
					</li>
					
				</ul>
			</li>
			
			<li>�reas do CNPQ
				<ul>
					<li>
						<h:commandLink value="Total por �rea CNPq" 
								action="#{relatorioTotalPorCNPqMBean.iniciar}" onclick="setAba('relatorios')"
								id="linkTotalAreaCNPQ"/>
					</li>
					<li>
						<h:commandLink value="Crescimento por �rea CNPq" 
								action="#{relatorioCrescimentoPorCNPqMBean.iniciar}" onclick="setAba('relatorios')"
								id="linkCrescimentoAreaCNPQ"/>
					</li>
					<%-- Os itens abaixo foram comentados pois o relat�rio 'Total por �rea CNPq' j� atende ambas as necessidades --%>
					<%-- <li>
						<h:commandLink value="Total por Cole��o e �rea CNPQ" 
								action="#{relatorioTotalPorColecaoCNPq.iniciar}" onclick="setAba('relatorios')"
								id="linkTotalColecaoAreaCNPQ"/></li>
					<li>
						<h:commandLink action="#{relatorioTotalPorTipoMaterialCNPq.iniciar}"
								value="Total por Tipo de Material e �rea CNPq" onclick="setAba('relatorios')"
								id="linkTotalMaterialAreaCNPQ" />
					</li> --%>
				</ul>
			</li>
		
			<li>Peri�dicos
				<ul>
					<li>
						<h:commandLink value="Total de Peri�dicos por Classifica��o"
								action="#{relatorioTotalPeriodicosPorClasseMBean.iniciar}"
								onclick="setAba('relatorios')" id="linkTotalPeriodicosPorClasse" />
					</li>
					<li>
						<h:commandLink value="Total de Peri�dicos por �rea do CNPQ"
								action="#{relatorioTotalPeriodicosCNPqMBean.iniciar}"
								onclick="setAba('relatorios')" id="linkTotalPeriodicosPorCNPq"/>
					</li>
					<li>
						<h:commandLink value="Crescimento de Peri�dicos por Classifica��o"
								action="#{relatorioCrescimentoPeriodicosPorClasseMBean.iniciar}"
								onclick="setAba('relatorios')" id="linkCrescimentoPeriodicosPorClasse" />
					</li>
					<li>
						<h:commandLink value="Crescimento de Peri�dicos por �rea do CNPQ"
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
						<h:commandLink value="Listagem por Faixa de C�digo de Barras "
								action="#{relatorioListagemPorFaixaCodigoBarraAcervoMBean.iniciar}"
								onclick="setAba('relatorios')" id="linkListagemPorFaixaCodigoBarra" />
					</li>
				</ul>
			</li>
			
			
			<li>Invent�rio do Acervo
				<ul>
					<li>
						<h:commandLink value="Invent�rio do Acervo"
							action="#{relatorioInventarioAcervoMBean.iniciar}"
							onclick="setAba('relatorios')" id="linkInventarioAcervo" />
					</li>
					<li>
						<h:commandLink value="Registros Realizados por um Usu�rio"
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
			
			<li>Hist�ricos
				<ul>
					<li>
						<h:commandLink value="Hist�rico de Altera��es de um T�tulo"
								action="#{pesquisaTituloCatalograficoMBean.iniciarPesquisa}"
								onclick="setAba('relatorios')" id="linkHistoricoAlteracoesTitulo" />
					</li>
					<li>
						<h:commandLink value="Hist�rico de Altera��es de um Material"
								action="#{emiteRelatorioHistoricosMBean.iniciarConsultaAlteracaoMaterial}" 
								onclick="setAba('relatorios')" id="linkHistoricoAlteracoesMaterial" />
					</li>
				</ul>
			</li>
			
			<li>Cataloga��es Trabalhadas por Operador
				<ul>
					<li>
						<h:commandLink value="Coopera��o"
							action="#{relatorioCooperacaoMBean.iniciar}"
							id="lingRelatorioCooperacao"
							onclick="setAba('relatorios')"/>
					</li>
					<li>
						<h:commandLink value="Implanta��o"
							action="#{relatorioImplantacaoMBean.iniciar}"
							id="lingRelatorioImplantacao"
							onclick="setAba('relatorios')"/>
					</li>
				</ul>
			</li>
		</ul>
	</rich:tab>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	<rich:tab label="Circula��o" id="tabCirculacao" ontabenter="$('subAba').value = 'tabCirculacao' ">
		<ul>
			
			<li>Empr�stimos
				<ul>
					
					<li>
						<h:commandLink value="Empr�stimos por Categoria de Usu�rio"
								action="#{relatorioEmprestimosPorCategoriaDeUsuarioMBean.iniciar}"
								onclick="setAba('relatorios')"
								id="linkEmprestimoCategoriaDeUsuario" />
					</li>
					<li>
						<h:commandLink value="Empr�stimos por Comunidade"
								action="#{relatorioEmprestimoComunidadeMBean.iniciar}"
								onclick="setAba('relatorios')"
								id="linkEmprestimoPorComunidade" />
					</li>
					<li>
						<h:commandLink value="Empr�stimos por Classifica��o"
								action="#{relatorioEmprestimosPorClassificacaoMBean.iniciar}"
								onclick="setAba('relatorios')"
								id="linkEmprestimosPorTipoDeMaterialClasseCDU" />
					</li>
					<li>
						<h:commandLink value="T�tulos com mais Empr�stimos"
								action="#{relatorioTitulosComMaisEmprestimos.iniciar}"
								onclick="setAba('relatorios')" id="linkTitulosComMaisEmprestimos" />
					</li>
					
					<li>
						<h:commandLink value="Hist�rico de Empr�stimos de um Material"
								action="#{emiteRelatorioHistoricosMBean.iniciarConsultaEmprestimos}"
								onclick="setAba('relatorios')" id="linkHistoricoEmprestimosMaterial" />
					</li>
					
					<li>
						<h:commandLink value="Renova��es por M�dulo de Acesso"
								action="#{relatorioRenovacoesPorModuloMBean.iniciar}"
								onclick="setAba('relatorios')" id="linkRenovacoesPorModuloDeAcesso" />
					</li>
					<li>
						<h:commandLink value="Empr�stimos por Curso"
								action="#{relatorioEmprestimosPorCursoMBean.iniciar}"
								onclick="setAba('relatorios')"
								id="linkEmprestimosPorCurso" />
					</li>
					<li>
						<h:commandLink value="Empr�stimos por Unidade"
								action="#{relatorioEmprestimosPorUnidadeMBean.iniciar}"
								onclick="setAba('relatorios')"
								id="linkEmprestimosPorUnidade" />
					</li>
					<li>
						<h:commandLink value="Empr�stimos por Tipo de Empr�stimo"
								action="#{relatorioEmprestimosPorTipoEmprestimoMBean.iniciar}"
								onclick="setAba('relatorios')"
								id="linkEmprestimosPorTipoEmprestimo" />
					</li>
					<li>
						<h:commandLink value="Empr�stimos por Turno"
								action="#{relatorioEmprestimosPorTurnoMBean.iniciar}"
								onclick="setAba('relatorios')"
								id="linkEmprestimosPorTurno" />
					</li>
				</ul>
			</li>
			
			
			<li>Usu�rios
				<ul>
					<li>
						<h:commandLink
							value="Usu�rios com Potencial de Empr�stimo"
							id="linkUsuariosComPotencial" onclick="setAba('relatorios')"
							action="#{relatorioUsuariosComPotencialDeEmprestimoMBean.iniciar}"/>
					</li>
					
					<li>
						<h:commandLink value="Movimenta��o de Usu�rios por Per�odo"
							action="#{relatorioMotivimentacaoDeUsuariosMBean.iniciar}"
							onclick="setAba('relatorios')"
							id="linkUsuariosCadastrados" />
					</li>	
						
					<li>
						<h:commandLink value="Quantitativo de Usu�rios que Fizeram Empr�stimo por Per�odo"
								action="#{relatorioDeUsuariosComEmprestimoNumPeriodo.iniciar}"
								onclick="setAba('relatorios')"
								id="linkUsuariosComEmprestimosNumPeriodo" />
					</li>
					
					<li>
						<h:commandLink value="Quantitativo de Usu�rios que Devolveram Empr�stimo por Per�odo"
								action="#{relatorioDeUsuariosComEmprestimosDevolvidosNumPeriodo.iniciar}"
								onclick="setAba('relatorios')"
								id="linkUsuariosComEmprestimosDevolvidosNumPeriodo" />
					</li>
					
					<li>
						<h:commandLink value="Usu�rios em Atraso"
								action="#{relatorioSituacaoDosUsuariosMBean.iniciarRelatorioUsuariosEmAtraso}"
								onclick="setAba('relatorios')"
								id="linkUsuariosEmAtraso" />
					</li>
					<li>
						<h:commandLink value="Usu�rios Suspensos"
								action="#{relatorioSituacaoDosUsuariosMBean.iniciarRelatorioUsuariosSuspensos}"
								onclick="setAba('relatorios')"
								id="linkUsuariosSuspensos" />
					</li>
					<li>
						<h:commandLink id="linkUsuariosComMultas"  value="Usu�rios com Multas"
								action="#{relatorioSituacaoDosUsuariosMBean.iniciarRelatorioUsuariosComMulta}"
								onclick="setAba('relatorios')"/>
					</li>
					<li>
						<h:commandLink value="Usu�rios com Empr�stimos Ativos"
								action="#{relatorioSituacaoDosUsuariosMBean.iniciarRelatorioUsuariosComEmprestimosAtivos}"
								onclick="setAba('relatorios')"
								id="linkUsuariosComEmprestimosAtivos" />
					</li>
					<li>
						<h:commandLink value="Usu�rios com mais Empr�stimos"
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
				<h:commandLink value="Ocorr�ncias de Perda de Material"
						action="#{relatorioOcorrenciasPerdaMaterialMBean.iniciar}"
						onclick="setAba('relatorios')" id="linkOcorrenciaPerdeMaterial" />
			</li>
			
			<li>
				<h:commandLink value="Frequ�ncia de Usu�rios"
						action="#{relatorioFrequenciaDosUsuariosMBean.iniciar}"
						onclick="setAba('relatorios')" id="linkFrequenciaDeUsuarios" />
			</li>
			
			<li>Pend�ncias
				<ul>
					<li>
						<h:commandLink value="Usu�rios Finalizados com Pend�ncias na Biblioteca"
								action="#{relatorioUsuariosFinalizadosComPedenciasBibliotecaMBean.iniciar}"
								onclick="setAba('relatorios')"
								id="linkUsuariosFinalizadosComEmprestimos" />
					</li>
				</ul>
			</li>
			
			<li>Puni��es
				<ul>
				
					<li>Suspens�es
						<ul>
							<li>
								<h:commandLink value="Suspens�es Estornadas por Per�odo"
										action="#{relatorioSuspensoesEstornadasBibliotecaMBean.iniciar}"
										onclick="setAba('relatorios')"
										id="linkSuspensoesEstornadas" />
							</li>
						</ul>
					</li>
						
					<li>Multas
						<ul>
							
							<li>
								<h:commandLink id="linkRelatorioPagamentosRecebidos" value="Pagamentos Recebidos por Per�odo"
										action="#{relatorioMultasPagasBibliotecaMBean.iniciar}"
										onclick="setAba('relatorios')" />
							</li>
							<li>
								<h:commandLink id="linkRelatorioPagamentosAReceber" value="Pagamentos a Receber por Per�odo"
										action="#{relatorioMultasNaoPagasBibliotecaMBean.iniciar}"
										onclick="setAba('relatorios')"/>
							</li>
							<li>
								<h:commandLink id="linkRelatorioMultasEstornadas"  value="Multas Estornadas por Per�odo"
										action="#{relatorioMultasEstornadasBibliotecaMBean.iniciar}"
										onclick="setAba('relatorios')" />
							</li>
						</ul>
					</li>
			
				
				</ul>
			</li>
			
		</ul>
	</rich:tab>
	
	
	
	
	
	
	<rich:tab label="Informa��o e Refer�ncia" id="tabInfoReferencia" ontabenter="$('subAba').value = 'tabInfoReferencia' ">
	
	</rich:tab>
	
	
	
	
	
	
	
	
	
	<rich:tab label="Outros" id="tabOutros" ontabenter="$('subAba').value = 'tabOutros' ">
		
		<ul>
			
			<li>
				<h:commandLink id="linkNumerosDoSistema" value="N�meros Gerais do Sistema" action="#{relatorioNumerosDoSistemaMBean.iniciar}" onclick="setAba('relatorios')" />
			</li>
			
			
			<li>Dissemina��o Seletiva da Informa��o
				<ul>
					<li>
						<h:commandLink value="Temas de Maior Interesse dos Usu�rios" action="#{relatorioTemasMaiorInteresseDSIMBean.iniciar}" id="linkRelatorioTemasMaiorInteresse" onclick="setAba('relatorios')"/>
					</li>
				</ul>
			</li>
			
		</ul>
		
		
	</rich:tab>
	
</rich:tabPanel>

