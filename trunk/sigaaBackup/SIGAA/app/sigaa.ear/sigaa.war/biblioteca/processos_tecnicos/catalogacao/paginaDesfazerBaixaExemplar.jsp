<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>  <ufrn:subSistema /> &gt; Confirmar Desfazer Baixa do Exemplar </h2>

<c:set var="confirmDesfazerBaixa" value="if (!confirm('Deseja mesmo desfazer a baixa do exemplar?')) return false" scope="request" />

<div class="descricaoOperacao">
	<p>
	Quando a baixa de um exemplar é desfeita, ele volta a fazer parte do acervo da biblioteca.
	Dessa forma, ele volta a aparecer nas buscas e relatórios, inclusive para o usuário final.
	</p>
	<p>
	Um exemplar que seja anexo de outro só pode ter a baixa desfeita se o exemplar pai tiver
	sua baixa desfeita primeiro. 
	</p>

</div>


<f:view>

	<h:form>

		<%-- Quando edita um exemplar a partir da página de pesquisa --%>
		<a4j:keepAlive beanName="pesquisarExemplarMBean"></a4j:keepAlive>

		<a4j:keepAlive beanName="editaMaterialInformacionalMBean"></a4j:keepAlive>
	
		<table class="formulario" width="100%" >
					
			<caption> Desfazer Baixa Exemplar <c:if test="${editaMaterialInformacionalMBean.exemplarEdicao.anexo}"> <span style="font-style: italic;">(anexo)</span> </c:if> </caption>
			
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
								<th width="33%">Código Barras: </th> 
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
								<th>Status:</th>
								<td>
									<h:outputText  id="comboStatus" value="#{ editaMaterialInformacionalMBean.exemplarEdicao.status.descricao }" />
								</td>
							</tr>
							
							<tr>
								<th>Tipo de Material:</th>
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
						</table>
					</td>
				</tr>
				
				<tr>
					<td>
						<table class="formulario" width="100%" style="margin-top:20px" >
						
							<caption>Confirmar Desfazer Baixa de Exemplar</caption>
						
							<tbody>
								<tr>
									<th class="obrigatorio">Nova situação:</th>
									<td colspan="3">
										<h:selectOneMenu id="comboSituacao"
												value="#{ editaMaterialInformacionalMBean.idNovaSituacao }">
											<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
											<f:selectItems value="#{ editaMaterialInformacionalMBean.situacosPosDesfazerBaixa }" />
										</h:selectOneMenu>
									</td>
								</tr>
				
							</tbody>
								
							<tfoot>
								<tr>
									<td colspan="4" align="center">
										<h:commandButton id="botaoDarBaixaMaterial" value="Desfazer Baixa"
												action="#{editaMaterialInformacionalMBean.desfazerBaixaMaterial}" onclick="#{confirmDesfazerBaixa}" />
										<h:commandButton id="botaoVoltar" value="<< Voltar"
												action="#{editaMaterialInformacionalMBean.voltarPaginaExemplar}" rendered="true" />
										<h:commandButton value="Cancelar"
												action="#{editaMaterialInformacionalMBean.cancelar}" onclick="#{confirm}"
												immediate="true" id="cancelar" />		
									</td>
								</tr>
								
							</tbody>
								
						</table>	
					</td>
				</tr>
			</tfoot>
		</table>
	
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>

	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>