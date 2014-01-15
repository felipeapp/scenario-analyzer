<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsf/html"      prefix="h"%>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>

<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>


<div class="descricaoOperacao"> 
	Controla toda a parte da inclus�o das informa��es catalogr�ficas no acervo.
</div>

<ul>
	
	<%-- A pesquisa no acervo � liberada para todos os usu�rio que tem algum papel na biblioteca --%>
	<li>Pesquisas no Acervo
		<ul>
			
			<li> <h:commandLink action="#{pesquisaTituloCatalograficoMBean.iniciarPesquisa}" value="Pesquisar por T�tulos"
					onclick="setAba('processos_tecnicos')" id="cmdPesquisarPorTitulos"/>  </li>
		
			<li> <h:commandLink action="#{pesquisarExemplarMBean.iniciarPesquisa}" value="Pesquisar por Exemplares"
					onclick="setAba('processos_tecnicos')" id="cmdPesquisarPorExemplares" />  </li> 
		
			<li> <h:commandLink action="#{pesquisarFasciculoMBean.iniciarPesquisa}" value="Pesquisar por Fasc�culos"
					onclick="setAba('processos_tecnicos')" id="cmdPesquisarPorFasciculos" /> </li>
		
			<li> <h:commandLink action="#{pesquisarArtigoMBean.iniciarPesquisa}" value="Pesquisar por Artigos de Peri�dicos"
					onclick="setAba('processos_tecnicos')" id="cmdPesquisarPorArtigosDePeriodicos" />  </li> 
			
		</ul>
	</li>
	
	
	
	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
	
		<li>Cataloga��o
		<ul>
		
			<li> <h:commandLink action="#{catalogacaoMBean.telaBuscaInformacoesSipacAPartirNumeroPatrimonio}"
					value="Catalogar T�tulos e Materiais com Tombamento" onclick="setAba('processos_tecnicos')"
					id="cmdCatalogarTitulosEMateriaisComTombamento" > 
					<f:param name="isPesquisaTituloParaCatalogacaoComTombamento" value="true" />
				</h:commandLink>		
			</li>
	
			<li> <h:commandLink action="#{catalogacaoMBean.preparaCatalogacaoSemTombamento}"
					value="Catalogar T�tulos e Materiais sem Tombamento" onclick="setAba('processos_tecnicos')"
					id="cmdCatalogarTitulosEMateriaisSemTombamento">
					<f:param name="isPesquisaTituloParaCatalogacao" value="true" />
				</h:commandLink>	 
					
			</li>
	
	
			<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
				<li> 
						<h:commandLink action="#{pesquisarFasciculoMBean.iniciarCatalogacaoArtigo}"
						value="Cataloga��o de Artigos de Peri�dicos (anal�tica)" onclick="setAba('processos_tecnicos')"
						id="cmdCatalogacaoDeArtigosDePeriodicos_Analitica_" /> 
				</li>
		        
				<li> 
					<h:commandLink action="#{buscaCatalogacoesIncompletasMBean.iniciarBuscaTitulosIncompletos}"
						value="Visualizar Cataloga��es Incompletas de T�tulos" onclick="setAba('processos_tecnicos')"
						id="cmdVisualizarCatalogacoesIncompletasDeTitulos" />
				</li>
			
				<li> 
					<h:commandLink action="#{pesquisaTituloCatalograficoMBean.iniciarPesquisa}"
						value="Catalogar apenas o T�tulo (sem materiais)" onclick="setAba('processos_tecnicos')"
						id="cmdCatalogarApenasOTitulo_sem_materiais_" >
						<f:param name="isPesquisaTituloParaCatalogacao" value="true" />
						<f:param name="isPesquisaTituloApenasCatalogacao" value="true" />
					</h:commandLink>	 
				</li>
			</ufrn:checkRole>
		</ul>
		</li>
	
	</ufrn:checkRole>
	
	
	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
	
		<li>Gerenciamento de Materiais
			<ul>
				
				<li> Exemplares
					<ul>
						<li> <h:commandLink action="#{pesquisarExemplarMBean.iniciarPesquisaBaixa}"         value="Baixar Exemplar"
								onclick="setAba('processos_tecnicos')" id="cmdBaixarExemplar" /> </li>
						<li> <h:commandLink action="#{pesquisarExemplarMBean.iniciarPesquisaDesfazerBaixa}" value="Desfazer Baixa de Exemplar"
								onclick="setAba('processos_tecnicos')" id="cmdDescfazerBaixaDeExemplar" /> </li>
						<li> <h:commandLink action="#{pesquisarExemplarMBean.iniciarPesquisaRemocao}"       value="Remover Exemplar"
								onclick="setAba('processos_tecnicos')" id="cmdRemoverExemplar" /> </li>
						<li> <h:commandLink action="#{pesquisarExemplarMBean.iniciarSubstituicaoExemplar}"  value="Substituir Exemplar"
								onclick="setAba('processos_tecnicos')" id="cmdSubstituirExemplar" /> </li>
					</ul>
				</li>
				
				<li> Fasc�culos
					<ul>
						<li> <h:commandLink action="#{pesquisarFasciculoMBean.iniciarPesquisaBaixa}"         value="Baixar Fasc�culo"
								onclick="setAba('processos_tecnicos')" id="cmdBaixarFasciculo" /> </li>
						<li> <h:commandLink action="#{pesquisarFasciculoMBean.iniciarPesquisaDesfazerBaixa}"  value="Desfazer Baixa de Fasc�culo"
								onclick="setAba('processos_tecnicos')" id="cmdDesfazerBaixaDeFasciculo" /> </li>
						<li> <h:commandLink action="#{pesquisarFasciculoMBean.iniciarPesquisaRemocao}"       value="Remover Fasc�culo"
								onclick="setAba('processos_tecnicos')" id="cmdRemoverFasciculo" /> </li>
						<li> <h:commandLink action="#{pesquisarFasciculoMBean.iniciarSubstituicaoFasciculo}" value="Substituir Fasc�culo"
								onclick="setAba('processos_tecnicos')"id="cmdSubstituirFasciculo" /> </li>
					</ul>
				</li>
				
			</ul>
		</li>
	
	</ufrn:checkRole>
	
	
	
	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
					
		<li>Coopera��o T�cnica
		<ul>
			
			<li> <h:commandLink value="Exportar T�tulo" title="Exportar um T�tulo para o par�o MARC 21" 
					 action="#{cooperacaoTecnicaExportacaoMBean.iniciarExportacaoBibliografica}" onclick="setAba('processos_tecnicos')"
					 id="cmdExportarTituloParaMARC21" /></li>
			
			<li> <h:commandLink value="Exportar Autoridade" title="Exportar uma Autoridade para o par�o MARC 21"
					action="#{cooperacaoTecnicaExportacaoMBean.iniciarExportacaoAutoridades}"onclick="setAba('processos_tecnicos')"
					id="cmdExportarAutoridadeParaMARC21" /></li>
			
			<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
				<li> <h:commandLink value="Carga dos N�meros de Controle de T�tulos da FGV" title="Fazer a Carga dos N�meros de Controle de T�tulos da FGV"
						action="#{cargaArquivoFGVMBean.iniciarCargaArquivoTitulo}"onclick="setAba('processos_tecnicos')"
						id="cmdCargaDosNumerosDeControleDeTitulosFGV" /></li>
			
				<li> <h:commandLink value="Carga dos N�meros de Controle de Autoridades da FGV" title="Fazer a Carga dos N�meros de Controle de Autoridades da FGV"
						action="#{cargaArquivoFGVMBean.iniciarCargaArquivoAutoridades}"onclick="setAba('processos_tecnicos')"
						id="cmdCargaDosNumerosDeControleAutoridadesFGV" /></li>
			</ufrn:checkRole>
		</ul>
		</li>
		
	</ufrn:checkRole>

	

	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
		<li>Cadastro Autoridades
			<ul>
				
				<li> <h:commandLink action="#{catalogaAutoridadesMBean.iniciarPesquisaNormal}" value="Pesquisar por Autoridades" onclick="setAba('processos_tecnicos')" id="cmdPesquisarPorAutoridades" /> </li>
				
				
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
					<li> <h:commandLink action="#{catalogaAutoridadesMBean.iniciar}" value="Catalogar Autoridades" 
							onclick="setAba('processos_tecnicos')" id="cmdCatalogarAutoridades" />  
					</li>
				
					<li> 
						<h:commandLink action="#{buscaCatalogacoesIncompletasMBean.iniciarBuscaAutoridadesIncompletas}" value="Visualizar Cataloga��es Incompletas de Autoridades" 
						onclick="setAba('processos_tecnicos')" id="cmdVisualizarCatalogacoesIncompletasDeAutoridades" /> 
					</li>
				</ufrn:checkRole>
				
			</ul>
		</li>
	</ufrn:checkRole>



	
	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
					
		<li>Gerenciador Etiquetas
		<ul>
			<%--
				A opera��o abaixo ser� implementada futuramente:
				<li> <h:commandLink action="#{pesquisaItemCatalografico.iniciarPesquisa}" value="Gerenciar Formatos de Folhas de Impress�o"
						onclick="setAba('processos_tecnicos')" id="cmdGerenciarFormatosDeFolhasDeImpressao" /> </li>
			--%>
			<li> <h:commandLink action="#{geraEtiquetaImpressao.iniciar}" value="Impress�o de Etiquetas"
					onclick="setAba('processos_tecnicos')" id="cmdImpressaoDeEtiquetas" /> </li>
			
		</ul>
		</li>
	</ufrn:checkRole>
	
	
	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
		<li> Outras Opera��es
			<ul>
				
				<%-- por enquanto, gerenciar materiais n�o vai poder fazer tranfer�ncias --%>
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
					
					<li> <h:commandLink action="#{transfereExemplaresEntreTitulosMBean.iniciarPrePesquisaTransferenciaTituloDoExemplar}" value="Transferir Exemplares entre T�tulos"
							onclick="setAba('processos_tecnicos')" id="cmdTransferirExemplaresEntreTitulos" /> </li>
					
					<li> <h:commandLink action="#{transfereExemplaresEntreBibliotecasMBean.iniciarTransferencia}" value="Transferir Exemplares entre Bibliotecas"
							onclick="setAba('processos_tecnicos')" id="cmdTransferirExemplaresEntreBibliotecas"/> </li>
					
					<li> <h:commandLink action="#{transferirMateriaisEntreSetoresBibliotecaMBean.iniciarBuscaMaterial}" value="Transferir Materiais Entre Setores"
							onclick="setAba('processos_tecnicos')" id="cmdTransferirMateriaisEntreSetoresCatalogacao" /> 
					
					<li> Transfer�ncia de Fasc�culos
						<ul>
						<li> <h:commandLink action="#{solicitaTransferenciaFasciculosEntreAssinaturasMBean.iniciarTransferencia}" value="Solicitar / Transferir Fasc�culos entre Bibliotecas"
								onclick="setAba('processos_tecnicos')" id="cmdSolicitarOuTransferirFasciculosEntreBiblioteca" /> </li>
						<li> <h:commandLink action="#{autorizaTransferenciaFasciculosEntreAssinaturasMBean.iniciarAutorizacaoTranferencia}" value="Verificar / Autorizar Transfer�ncia de Fasc�culos"
								onclick="setAba('processos_tecnicos')" id="cmdVerificarOuAutorizarTransferenciasDeFasciculos" /> </li> 
						</ul>
					</li>
					
					
				</ufrn:checkRole>
				
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
					
					<li> <h:commandLink action="#{alterarMotivoBaixaVariosMateriaisMBean.iniciarBuscaMaterial}" value="Alterar o Motivo da Baixa dos Materiais"
							onclick="setAba('processos_tecnicos')" id="cmdAlterarMotivoBaixaDeVariosMateriais" /> 
					</li>
					
					<li> <h:commandLink action="#{alteraDadosVariosMateriaisMBean.iniciarPesquisaAlteracaoDadosDeVariosMateriais}" value="Alterar os Dados de V�rios Materiais"
							onclick="setAba('processos_tecnicos')" id="cmdAlterarDadosDeVariosMateriais" /> 
					</li>
				</ufrn:checkRole>
	
			</ul>	
		</li>
	</ufrn:checkRole>
	
	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
		<li> Notas de Circula��o
			<ul>
				<li> <h:commandLink value="Incluir Nota de Circula��o" action="#{notasCirculacaoMBean.listar}"  onclick="setAba('processos_tecnicos')" /> </li>
				
				<li> <h:commandLink action="#{modificarNotaCirculacaoVariosMateriaisMBean.iniciarBuscaMaterialIncluirNota}" value="Incluir Nota de Circula��o em V�rios Materiais"
						onclick="setAba('processos_tecnicos')" id="cmdIncluiNotaCirculacaoVariosMateriaisProcessosTecnicos" /> 
				</li>
				
				<li> <h:commandLink action="#{modificarNotaCirculacaoVariosMateriaisMBean.iniciarBuscaMaterialRemoverNota}" value="Remover Nota de Circula��o em V�rios Materiais"
						onclick="setAba('processos_tecnicos')" id="cmdRemoverNotaCirculacaoVariosMateriaisProcessosTecnicos" /> 
				</li>
				
			</ul>
		</li>
	</ufrn:checkRole>
	
	
	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">
		
		<li>Planilhas de Cataloga��o
			<ul>
				
				<li>
					<h:commandLink action="#{planilhaMBean.listar}" value="Gerenciar Nova Planilhas Bibliogr�ficas" onclick="setAba('processos_tecnicos')">
						<f:param name="tipoPlanilha" value="#{planilhaMBean.bibliografica}" />
					</h:commandLink>
				</li>
				
				<li>
				
					<h:commandLink action="#{planilhaMBean.listar}" value="Gerenciar Nova Planilhas de Autoridades" onclick="setAba('processos_tecnicos')">
						<f:param name="tipoPlanilha" value="#{planilhaMBean.autoridade}" />
					</h:commandLink>
				</li>
				
			</ul>
		</li>
		
	</ufrn:checkRole>
	
	
	<%--  Caso de uso Criado apenas para testes, n�o deve ir para produ��o 
	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL }  %>">
		
		<li>Invent�rios do Acervo
			<ul>
				<li><h:commandLink action="#{registraMateriaisInventarioMBean.iniciar}" onclick="setAba('processos_tecnicos')" value="Registrar Material no Invent�rio" /></li>
			</ul>
		</li>
							
	</ufrn:checkRole>
	--%>
	
</ul>