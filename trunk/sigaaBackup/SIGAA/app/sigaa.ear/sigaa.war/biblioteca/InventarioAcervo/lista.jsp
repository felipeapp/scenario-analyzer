<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>  <ufrn:subSistema /> &gt; Buscar Inventários do Acervo </h2>

<div class="descricaoOperacao">
		<p> Página para a busca de inventários do acervo. </p>
		<p> Inventários são criados para se fazer um levantamento se os materiais cadastrados no sistema ainda encontram-se fisicamente na biblioteca. </p>
		<br/>
		<p>Após ser aberto, os materiais podem ser registrados no acervo com a utilização de um coletor. Ao final do registro, o invetário deve ser fechado.
		Quando o inventário é fechado é possível emitir e relatório e consultar os materiais existentes no sistema que não foram registrados no inventário.</p>
		<br/>
		<p>O inventário é realizado por biblioteca. Pode-se selecionar uma coleção específica para o inventário, neste caso apenas materiais dessa 
		coleção podem ser registrados no inventário.</p>
		<br/>
		<p> <strong> Observação: Inventários mostrados em <span style="color: red">vermelho</span> estão fechados.</strong> </p>
</div>

<style type="text/css">

.textoCentralizado{
	text-align:center;
}

table.listagem tr.ano td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
}

</style>


<f:view>

	<a4j:keepAlive beanName="inventarioAcervoBibliotecaMBean"></a4j:keepAlive>
	
	<h:form id="pesquisaInventario">
	
		<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL} %>">
			<div class="infoAltRem" style="width:50%;">
				<h:graphicImage value="/img/adicionar.gif" />
				<h:commandLink id="cmdLinkCriarInventario" action="#{inventarioAcervoBibliotecaMBean.preCadastrar}" value="Criar Novo Inventário" />
			</div>
		</ufrn:checkRole>
		
		<table class="formulario" style="margin-bottom: 20px; width:50%; ">
			<caption class="listagem">Filtrar Inventários</caption>
			
			<tr>
				<th style="text-align: center">Biblioteca:</th>
				<td>
					<h:selectOneMenu id="comboboxBibliotecasInventario" value="#{inventarioAcervoBibliotecaMBean.idBibliotecaFiltroPesquisa}">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
						<f:selectItems value="#{bibliotecaMBean.allCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="2">
						
						<h:commandButton id="cmdButtonBuscarInventario" value="Buscar" action="#{inventarioAcervoBibliotecaMBean.buscarInventarios}" />
						
						<%-- <h:commandButton id="botaoVoltar" value="<< Voltar" action="#{inventarioAcervoBibliotecaMBean.voltarBusca}" /> --%>
						
						<h:commandButton id="botaoLimpar" value="Limpar" action="#{inventarioAcervoBibliotecaMBean.limparCamposBusca}"></h:commandButton>
						<h:commandButton id="botaoCancelar" value="Cancelar" action="#{inventarioAcervoBibliotecaMBean.cancelar}" immediate="true" onclick="#{confirm}"/>
						
					</td>
				</tr>
			</tfoot>
			
		</table>
		
		
		
		
		
		<%-- Resultados da busca --%>
		
		<c:if test="${ not empty inventarioAcervoBibliotecaMBean.inventarios }">
			
			
			<div class="infoAltRem">
		
				<t:div rendered="#{!inventarioAcervoBibliotecaMBean.selecionandoInventario}" >
					<h:graphicImage value="/img/alterar.gif" />: 
					Alterar Inventário
				
					<h:graphicImage value="/img/blue-folder-closed.png" />: 
					Fechar Inventário
				
					<h:graphicImage value="/img/yellow-folder-open.gif" />: 
					Abrir Inventário
					
				</t:div>
				
				<t:div rendered="#{inventarioAcervoBibliotecaMBean.selecionandoInventario}" >
					<h:graphicImage value="/img/seta.gif" />: 
					Selecionar Inventário
				</t:div>
				
			</div>
			
			
			
			<table class="listagem" width="100%">
			
				<caption>Inventários ( ${fn:length( inventarioAcervoBibliotecaMBean.inventarios ) } )</caption>
				
				<thead>
					<tr>
						<th width="25%">Descrição</th>
						<th width="25%">Coleção</th>
						<th width="10%">Data de Fechamento</th>
						<th style="text-align:right;">Quantidade de Materiais Registrados</th>
						<th colspan="4"></th>
					</tr>
				</thead>
				<tbody>
				
					<c:set var="ano" value="0" scope="request" />
				
					<c:forEach var="inventario" items="#{inventarioAcervoBibliotecaMBean.inventarios}" varStatus="status">
						
						<c:if test="${ano != inventario.ano}">
							<tr class="ano">
								<c:set var="ano" value="${inventario.ano}" scope="request" />
								
								<td colspan="8">Ano ${ano}</td>
							</tr>
						</c:if>
						
						
						<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
							
								<td style="${ !inventario.aberto ? 'color: red;' : ' ' }">${inventario.descricao}</td>
								<td style="${ !inventario.aberto ? 'color: red;' : ' ' }">${inventario.colecao.descricao}</td>
								<td style="${ !inventario.aberto ? 'color: red;' : ' ' }"><ufrn:format type="data" valor="${inventario.dataFechamento}"></ufrn:format> </td>
								<td style="${ !inventario.aberto ? 'color: red;' : ' ' } text-align:right;">${inventario.quantidadeMateriaisRegistrados}</td>
								
								<td width="1%">
								
									<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL} %>">
										
										<h:commandLink id="cmdLinkAlterarInventario"  title="Alterar Inventário" 
												action="#{inventarioAcervoBibliotecaMBean.preAtualizar}"
												rendered="#{inventario.aberto && !inventarioAcervoBibliotecaMBean.selecionandoInventario}">
											<f:param name="idInventario" value="#{inventario.id}" />
											<h:graphicImage url="/img/alterar.gif" alt="Alterar Inventário" />
										</h:commandLink>
										
									</ufrn:checkRole>					
								
								</td>
									
								<td width="1%">
									<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL} %>">
										<h:commandLink id="cmdLinkFecharInventario"  title="Fechar Inventário" 
											action="#{inventarioAcervoBibliotecaMBean.fecharInventario}"
											onclick="return confirm('Após fechado, não será possível registrar novos materiais neste inventário. Tem certeza que deseja fechar este inventário?')"
											rendered="#{inventario.aberto && !inventarioAcervoBibliotecaMBean.selecionandoInventario}">
											<f:param name="idInventario" value="#{inventario.id}" />
											<h:graphicImage url="/img/blue-folder-closed.png" alt="Fechar Inventário" />
										</h:commandLink>
										
									</ufrn:checkRole>						
								
								</td>
									
								<td width="1%">
									<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL} %>">
										<h:commandLink id="cmdLinkAbrirInventario"  title="Abrir Inventário" 
											action="#{inventarioAcervoBibliotecaMBean.abrirInventario}"
											onclick="return confirm('Deseja reabrir este inventário?')"
											rendered="#{!inventario.aberto && !inventarioAcervoBibliotecaMBean.selecionandoInventario}">
											<f:param name="idInventario" value="#{inventario.id}" />
											<h:graphicImage url="/img/yellow-folder-open.gif" alt="Abrir Inventário" />
										</h:commandLink>
										
									</ufrn:checkRole>						
								
								</td>
								
								<td width="1%">
									<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL} %>">
										<h:commandLink id="cmdLinkSelecionarInventario"  title="Selecionar Inventário" 
											action="#{inventarioAcervoBibliotecaMBean.selecionouInventario}"
											rendered="#{inventarioAcervoBibliotecaMBean.selecionandoInventario}">
											<f:param name="idInventario" value="#{inventario.id}" />
											<h:graphicImage url="/img/seta.gif" alt="Selecionar Inventário" />
										</h:commandLink>
										
									</ufrn:checkRole>						
								
								</td>
								
							
						</tr>
					</c:forEach>
				</tbody>
				
				
			</table>	
		</c:if>
		
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>