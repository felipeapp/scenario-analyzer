<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>  <ufrn:subSistema /> &gt; Confirma Baixa do Exemplar </h2>

<c:set var="confirmBaixa" value="if (!confirm('Confirma a baixa do exemplar?')) return false" scope="request" />

<div class="descricaoOperacao">
	<p>A operação de baixa deve ser realizada para exemplares que existiram algum dia no acervo, mas que, por algum motivo,
	não têm mais condições de serem consultados pelo usuário.</p>
	<p>Se a baixa do exemplar for realizada, ele não aparecerá mais nas consultas do acervo para o usuário final,
	mas ele ainda constará no sistema. Será possível visualizá-lo pela página de pesquisa de exemplares e no relatório de
	materiais baixados.</p>
</div>


<f:view>

	<h:form>

		<%-- Quando edita um exemplar a partir da página de pesquisa --%>
		<a4j:keepAlive beanName="pesquisarExemplarMBean"></a4j:keepAlive>

		<a4j:keepAlive beanName="editaMaterialInformacionalMBean"></a4j:keepAlive>
	
		<table class="formulario" width="100%" style="maring-top:20px">
					
			<caption> Dar Baixa Exemplar <c:if test="${editaMaterialInformacionalMBean.exemplarEdicao.anexo}"> <span style="font-style: italic;">(anexo)</span> </c:if> </caption>
			
			<tbody>
				
				<tr>
					<td>
						<c:set var="_titulo" value="${editaMaterialInformacionalMBean.titulo}"/>
						<%@include file="/public/biblioteca/informacoes_padrao_titulo.jsp"%>
					</td>
				</tr>
				
				<tr>
					<td>
						<table class="subFormulario" width="100%">
							<caption> Dados do Exemplar </caption>
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
							
						
							<tr>
								<th class="obrigatorio">Motivo da Baixa:</th>
								<td colspan="3">
									<h:inputTextarea id="inputAreaMotivoBaixa" value="#{ editaMaterialInformacionalMBean.exemplarEdicao.motivoBaixa }" 
											cols="80" rows="5" onkeyup="textCounter(this, 'quantidadeCaracteresDigitados', 300);" />
								</td>
					
							</tr>
							<tr>
								<td>
								<td colspan="3" style="text-align: center;">
									Caracteres Restantes: <span id="quantidadeCaracteresDigitados">300</span>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</tbody>
				
				
			<tfoot>
				<tr>
					<td colspan="4" align="center">
					
						<h:commandButton id="botaoDarBaixaMaterial" value="Dar Baixa no Material" action="#{editaMaterialInformacionalMBean.darBaixaMaterial}" onclick="#{confirmBaixa}" />
						
						<h:commandButton id="botaoVoltar" value="<< Voltar" action="#{editaMaterialInformacionalMBean.voltarPaginaExemplar}" rendered="true" />
						
						<h:commandButton value="Cancelar" action="#{editaMaterialInformacionalMBean.cancelar}" onclick="#{confirm}" immediate="true" id="cancelar" />			
					</td>
				</tr>
			</tfoot>
				
				
		</table>


	</h:form>
	
</f:view>


<script type="text/javascript">
 
function textCounter(field, idMostraQuantidadeUsuario, maxlimit) {
	
	if (field.value.length > maxlimit){
		field.value = field.value.substring(0, maxlimit);
	}else{ 
		document.getElementById(idMostraQuantidadeUsuario).innerHTML = maxlimit - field.value.length ;
	} 
}

</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>