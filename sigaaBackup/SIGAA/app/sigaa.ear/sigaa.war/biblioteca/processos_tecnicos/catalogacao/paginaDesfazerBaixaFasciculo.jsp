<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>  <ufrn:subSistema /> &gt; Confirmar Desfazer Baixa do Fasc�culo </h2>

<c:set var="confirmDesfazerBaixa" value="if (!confirm('Deseja mesmo desfazer a baixa do fasc�culo?')) return false" scope="request" />

<div class="descricaoOperacao">
	<p>
	Quando a baixa de um fasc�culo � desfeita, ele volta a fazer parte do acervo da biblioteca.
	Dessa forma, ele volta a aparecer nas buscas e relat�rios, inclusive para o usu�rio final.
	</p>
	<p>
	Um fasc�culo que seja suplemento de outro s� pode ter a baixa desfeita se o fasc�culo pai tiver
	sua baixa desfeita primeiro. 
	</p>
</div>

<f:view>

	<h:form>

		<%-- Quando edita um fasc�culo a partir da p�gina de pesquisa --%>
		<a4j:keepAlive beanName="pesquisarFasciculoMBean"></a4j:keepAlive>
		
		<a4j:keepAlive beanName="editaMaterialInformacionalMBean"></a4j:keepAlive>	
	
		<table class="formulario" width="100%">
					
			<caption> Desfazer Baixa Fasc�culo </caption>
			
			<tbody>
				
				<tr>
					<td>
						<c:set var="_titulo" value="${editaMaterialInformacionalMBean.titulo}" scope="request"/>
						<%@include file="/public/biblioteca/informacoes_padrao_titulo.jsp"%>
					</td>
				</tr>
				
				<tr>
					<td>
						<c:set var="_assinatura" value="${editaMaterialInformacionalMBean.fasciculoEdicao.assinatura}" scope="request"/>
						<%@include file="/biblioteca/info_assinatura.jsp"%>
					</td>
				</tr>
				
				<tr>
					<td>
						<table class="subFormulario" width="100%">
							<caption> Dados do Fasc�culo </caption>
			
							<tr><td width="50%">
						
							<table width="100%">
								<tr>
									<th width="50%">C�digo de Barras: </th> 
									<td>
										<h:outputText id="inputTextCodigoBarras"  value="#{editaMaterialInformacionalMBean.fasciculoEdicao.codigoBarras}" />
									</td>
								</tr>
								
								<tr>
									<th>Ano Cronol�gico: </th> 
									<td>
										<h:outputText id="inputTextAnoCronologico" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.anoCronologico}" />
									</td>
								</tr>
								
								<tr>
									<th>Dia/M�s:</th>
									<td>
										<h:outputText id="inputTextDiaMes" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.diaMes}"/>
									</td>
								</tr>
								
								<tr>
									<th>Ano:</th>
									<td>
										<h:outputText id="inputTextAno" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.ano}" />
									</td>
								</tr>
								
								<tr>
									<th>Volume:</th>
									<td>
										<h:outputText id="inputTextVolume" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.volume}"/>
									</td>
								</tr>
								
								<tr>
									<th>N�mero:</th>
									<td>
										<h:outputText id="inputTextNumero" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.numero}" />
									</td>
								</tr>
								
								<tr>
									<th>Edi��o:</th>
									<td>
										<h:outputText id="inputTextEdicao" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.edicao}" />
									</td>
								</tr>
								
								<tr>
									<th>N�mero de Chamada:</th>
									<td colspan="3">
										<h:outputText id="inputTextNumeroChamada" value="#{ editaMaterialInformacionalMBean.fasciculoEdicao.numeroChamada }"  />
									</td>
								</tr>
							</table>
						
								</td>
								<td width="50%">
									<table width="100%">
										<tr>
											<th width="50%">Segunda Localiza��o:</th>
											<td colspan="3">
												<h:outputText id="inputTextSegundaLocalizacao" value="#{ editaMaterialInformacionalMBean.fasciculoEdicao.segundaLocalizacao }" />
											</td>
										</tr>
										
										<tr>
											<th>Biblioteca:</th>
											<td colspan="3"> 
												<h:outputText id="comboboxBiblioteca" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.biblioteca.descricao}"/> 
											</td>
										</tr>
										
										<tr>
											<th>Cole��o:</th>
											<td colspan="3">
												<h:outputText id="comboboxColecao" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.colecao.descricao}"/> 
											</td>
										</tr>
										
										<tr>
											<th>Status:</th>
											<td>
											<h:outputText id="comboboxStatus" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.status.descricao}"/> 		
											</td>
										</tr>
										
										<tr>
											<th>Tipo de Material:</th>
											<td colspan="3">
												<h:outputText id="comboboxTipoMaterial" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.tipoMaterial.descricao}"/>
											</td>
										</tr>
										
										<tr>
											<th>Nota Geral:</th>
											<td colspan="3">
												<h:outputText id="inputAreaNotaGeral" value="#{ editaMaterialInformacionalMBean.fasciculoEdicao.notaGeral }" />
											</td>
										</tr>
										
										<tr>
											<th>Nota ao Usu�rio:</th>
											<td colspan="3">
												<h:outputText id="inputAreaNotaUsuario" value="#{ editaMaterialInformacionalMBean.fasciculoEdicao.notaUsuario }"  />
											</td>
										</tr>
										
										<tr>
											<th>Suplemento:</th>
											<td colspan="3">
												<h:outputText id="inputAreaSuplemento" value="#{ editaMaterialInformacionalMBean.fasciculoEdicao.descricaoSuplemento }"/>
											</td>
										</tr>
									</table>
							</td></tr>
						</table>
					</td>
				</tr>
				
				<tr>
					<td>
						<table class="formulario" width="100%" style="margin-top:20px">
							<caption>Confirmar Desfazer Baixa de Fasc�culo</caption>
							
							<tbody>
								<tr>
									<th class="obrigatorio">Nova situa��o:</th>
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
										<h:commandButton id="botaoDarBaixaMaterial"  value="Desfazer baixa"
												action="#{editaMaterialInformacionalMBean.desfazerBaixaMaterial}"  onclick="#{confirmDesfazerBaixa}" />
										<h:commandButton value="<< Voltar"
												action="#{editaMaterialInformacionalMBean.voltarPaginaFasciculo}" rendered="true" id="botaoVoltar" />
										<h:commandButton value="Cancelar"
												action="#{editaMaterialInformacionalMBean.cancelar}" onclick="#{confirm}"
												immediate="true" id="cancelar" />						
									</td>
								</tr>
							</tfoot>
						</table>
					</td>
				</tr>
				
			</tbody>
		</table>
	
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>
		
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>