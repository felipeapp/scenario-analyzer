<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>  <ufrn:subSistema /> &gt; Buscar Invent�rios do Acervo </h2>

<div class="descricaoOperacao">
		<p> P�gina para a busca de invent�rios do acervo. </p>
		<p> Invent�rios s�o criados para se fazer um levantamento se os materiais cadastrados no sistema ainda encontram-se fisicamente na biblioteca. </p>
		<br/>
		<p>Ap�s ser aberto, os materiais podem ser registrados no acervo com a utiliza��o de um coletor. Ao final do registro, o invet�rio deve ser fechado.
		Quando o invent�rio � fechado � poss�vel emitir e relat�rio e consultar os materiais existentes no sistema que n�o foram registrados no invent�rio.</p>
		<br/>
		<p>O invent�rio � realizado por biblioteca. Pode-se selecionar uma cole��o espec�fica para o invent�rio, neste caso apenas materiais dessa 
		cole��o podem ser registrados no invent�rio.</p>
		<br/>
		<p> <strong> Observa��o: Invent�rios mostrados em <span style="color: red">vermelho</span> est�o fechados.</strong> </p>
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
				<h:commandLink id="cmdLinkCriarInventario" action="#{inventarioAcervoBibliotecaMBean.preCadastrar}" value="Criar Novo Invent�rio" />
			</div>
		</ufrn:checkRole>
		
		<table class="formulario" style="margin-bottom: 20px; width:50%; ">
			<caption class="listagem">Filtrar Invent�rios</caption>
			
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
					Alterar Invent�rio
				
					<h:graphicImage value="/img/blue-folder-closed.png" />: 
					Fechar Invent�rio
				
					<h:graphicImage value="/img/yellow-folder-open.gif" />: 
					Abrir Invent�rio
					
				</t:div>
				
				<t:div rendered="#{inventarioAcervoBibliotecaMBean.selecionandoInventario}" >
					<h:graphicImage value="/img/seta.gif" />: 
					Selecionar Invent�rio
				</t:div>
				
			</div>
			
			
			
			<table class="listagem" width="100%">
			
				<caption>Invent�rios ( ${fn:length( inventarioAcervoBibliotecaMBean.inventarios ) } )</caption>
				
				<thead>
					<tr>
						<th width="25%">Descri��o</th>
						<th width="25%">Cole��o</th>
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
										
										<h:commandLink id="cmdLinkAlterarInventario"  title="Alterar Invent�rio" 
												action="#{inventarioAcervoBibliotecaMBean.preAtualizar}"
												rendered="#{inventario.aberto && !inventarioAcervoBibliotecaMBean.selecionandoInventario}">
											<f:param name="idInventario" value="#{inventario.id}" />
											<h:graphicImage url="/img/alterar.gif" alt="Alterar Invent�rio" />
										</h:commandLink>
										
									</ufrn:checkRole>					
								
								</td>
									
								<td width="1%">
									<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL} %>">
										<h:commandLink id="cmdLinkFecharInventario"  title="Fechar Invent�rio" 
											action="#{inventarioAcervoBibliotecaMBean.fecharInventario}"
											onclick="return confirm('Ap�s fechado, n�o ser� poss�vel registrar novos materiais neste invent�rio. Tem certeza que deseja fechar este invent�rio?')"
											rendered="#{inventario.aberto && !inventarioAcervoBibliotecaMBean.selecionandoInventario}">
											<f:param name="idInventario" value="#{inventario.id}" />
											<h:graphicImage url="/img/blue-folder-closed.png" alt="Fechar Invent�rio" />
										</h:commandLink>
										
									</ufrn:checkRole>						
								
								</td>
									
								<td width="1%">
									<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL} %>">
										<h:commandLink id="cmdLinkAbrirInventario"  title="Abrir Invent�rio" 
											action="#{inventarioAcervoBibliotecaMBean.abrirInventario}"
											onclick="return confirm('Deseja reabrir este invent�rio?')"
											rendered="#{!inventario.aberto && !inventarioAcervoBibliotecaMBean.selecionandoInventario}">
											<f:param name="idInventario" value="#{inventario.id}" />
											<h:graphicImage url="/img/yellow-folder-open.gif" alt="Abrir Invent�rio" />
										</h:commandLink>
										
									</ufrn:checkRole>						
								
								</td>
								
								<td width="1%">
									<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL} %>">
										<h:commandLink id="cmdLinkSelecionarInventario"  title="Selecionar Invent�rio" 
											action="#{inventarioAcervoBibliotecaMBean.selecionouInventario}"
											rendered="#{inventarioAcervoBibliotecaMBean.selecionandoInventario}">
											<f:param name="idInventario" value="#{inventario.id}" />
											<h:graphicImage url="/img/seta.gif" alt="Selecionar Invent�rio" />
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