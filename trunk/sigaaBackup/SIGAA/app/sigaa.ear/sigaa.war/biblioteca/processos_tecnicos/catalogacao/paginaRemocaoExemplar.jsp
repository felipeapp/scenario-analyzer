<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>  <ufrn:subSistema /> &gt; Confirma Remoção do Exemplar </h2>

<c:set var="confirmRemocao" value="if (!confirm('Confirma remoção do exemplar do acervo ?')) return false" scope="request" />

<div class="descricaoOperacao"> 
	<p>A operação de remoção deve ser realizada para exemplares que <strong>nunca exitiram no acervo</strong>, foram incluído por engano.</p>
	<p>O exemplar removido sairá totalmente do sistema, podendo inclusive ser inserido um outro exemplar com o mesmo código de barras.</p>
</div>

<f:view>

	<h:form>

		<%-- Quando edita um exemplar a partir da página de pesquisa --%>
		<a4j:keepAlive beanName="pesquisarExemplarMBean"></a4j:keepAlive>

		<a4j:keepAlive beanName="editaMaterialInformacionalMBean"></a4j:keepAlive>
		
		
		<c:set var="_titulo" value="${editaMaterialInformacionalMBean.titulo}"/>
		<%@include file="/public/biblioteca/informacoes_padrao_titulo.jsp"%>
	
		<table class="formulario" width="100%" style="maring-top:20px">
					
			<tbody>
				
				<caption> Dados do Exemplar <c:if test="${editaMaterialInformacionalMBean.exemplarEdicao.anexo}"> <span style="font-style: italic;">(anexo)</span> </c:if> </caption>
				
				<tr>
					<th>Código Barras: </th> 
					<td>
						<h:outputText value="#{editaMaterialInformacionalMBean.exemplarEdicao.codigoBarras}"/>
					</td>
				</tr>
				
				<tr>
					<th>Número de Chamada:</th>
					<td>
						<h:outputText id="inputTextNumeroChamada" value="#{editaMaterialInformacionalMBean.exemplarEdicao.numeroChamada}" />
					</td>
				</tr>
				
				<tr>
					<th>Segunda Localização:</th>
					<td colspan="3">
						<h:outputText  id="inputTextSegundaLocalizacao" value="#{ editaMaterialInformacionalMBean.exemplarEdicao.segundaLocalizacao }" />
					</td>
				</tr>
				
				<tr>
					<th>Biblioteca:</th>
					<td colspan="3">
						<h:outputText  id="comboboxBiblioteca" value="#{ editaMaterialInformacionalMBean.exemplarEdicao.biblioteca.descricao }" />
					</td>
				</tr>
				
				<tr>
					<th>Coleção:</th>
					<td colspan="3">
						<h:outputText  id="comboBoxColecao" value="#{ editaMaterialInformacionalMBean.exemplarEdicao.colecao.descricao }" />
					</td>
				</tr>
				
				<tr>
				
					<th>Situação:</th>
					<td>
						<h:outputText  id="comboSituacao" value="#{ editaMaterialInformacionalMBean.exemplarEdicao.situacao.descricao }" />
					</td>
				</tr>
				
				<tr>
					<th>Status:</th>
					<td>
						<h:outputText  id="comboStatus" value="#{ editaMaterialInformacionalMBean.exemplarEdicao.status.descricao }" />
					</td>
				</tr>
				
				<tr>
					<th>Tipo Material:</th>
					<td colspan="3">
						<h:outputText  id="comboTipoMaterial" value="#{ editaMaterialInformacionalMBean.exemplarEdicao.tipoMaterial.descricao }" />
					</td>
		
				</tr>
				
				<tr>
					<th>Número do Volume:</th>
					<td>
						<h:outputText id="inputTextNumeroVolume" value="#{ editaMaterialInformacionalMBean.exemplarEdicao.numeroVolume}" />
					</td>
				</tr>
				
				
				<tr>
					<th>Nota de Tese e Dissertação:</th>
					<td colspan="3">
						<h:outputText id="inputAreaNotaTeseDissertacao" value="#{ editaMaterialInformacionalMBean.exemplarEdicao.notaTeseDissertacao }"  />
					</td>
		
				</tr>
		
				<tr>
					<th>Nota de Conteúdo:</th>
					<td colspan="3">
						<h:outputText id="inputAreaNotaConteudo" value="#{ editaMaterialInformacionalMBean.exemplarEdicao.notaConteudo }"  />
					</td>
		
				</tr>
				
				<tr>
					<th>Nota Geral:</th>
					<td colspan="3">
						<h:outputText id="inputAreaNotaGeral" value="#{ editaMaterialInformacionalMBean.exemplarEdicao.notaGeral }"   />
					</td>
		
				</tr>
		
				<tr>
					<th>Nota ao Usuário:</th>
					<td colspan="3">
						<h:outputText id="inputAreaNotaUsuario" value="#{ editaMaterialInformacionalMBean.exemplarEdicao.notaUsuario }"  />
					</td>
		
				</tr>
				
				<tfoot>
					<tr>
						<td colspan="4" align="center">
						
							<h:commandButton id="botaoRemoverMaterial" value="Remover Exemplar" action="#{editaMaterialInformacionalMBean.apagarMaterial}" onclick="#{confirmRemocao}"/>
							
							<h:commandButton id="botaoVoltar" value="<< Voltar" action="#{editaMaterialInformacionalMBean.voltarPaginaExemplar}" rendered="true" />
							
							<h:commandButton value="Cancelar" action="#{interrupcaoBibliotecaMBean.cancelar}" onclick="#{confirm}" immediate="true" id="cancelar" />			
						</td>
					</tr>
				</tfoot>
				
				
		</table>


	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>