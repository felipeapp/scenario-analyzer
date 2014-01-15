<%@include file="/mobile/commons/cabecalho.jsp" %>
<f:view>
	
	<h:form id="formEscolhaVinculoMobile">
		
		<table class="listagemMobile">
			
			<caption>Escolha de Vínculo</caption>
			
			<tbody>
				
				<c:forEach items="#{usuario.vinculos}" var="item">
					
					<tr>
						<td align="center">	
							${ item.outrasInformacoes } -						
							${ item.tipoVinculo.tipo } -
							${ item.tipoVinculo.ativo ? 'Sim' : 'Não' }
						</td>
					</tr>	
					
					<tr>
						<td align="center">
							<h:commandButton action="#{consultaNotasMobileMBean.escolherVinculo}" value="Escolher">
								<f:setPropertyActionListener 
									target="#{consultaNotasMobileMBean.vinculoUsuarioInt}" 
									value="#{item.numero}"/>
							</h:commandButton>
						</td>
					</tr>
						
				</c:forEach>
				
			</tbody>
			
		</table>	
		
	</h:form>
	
</f:view>
<%@include file="/mobile/commons/rodape.jsp" %>
