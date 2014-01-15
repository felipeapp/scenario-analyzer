
<%@include file="/mobile/commons/cabecalho.jsp"%>


<f:view>
	
	<h:form>
		
		<h:commandButton value="Menu Principal" action="#{operacoesBibliotecaMobileMBean.telaMenuPrincipalBibliotecaMobile}"/> <br/><br/>
		
			<table class="formularioMobile" >
					<caption> <c:out value="Campos de Busca"></c:out> </caption>
					
					
					<tbody>
					
						<tr>
							<th class="required">Título:</th>
						</tr>
							
						<tr>
							<td> <h:inputText value="#{operacoesBibliotecaMobileMBean.titulo}"> </h:inputText> </td>
						</tr>
						
						<tr>
							<th class="required">Autor:</th>
						</tr>		
						<tr>
							<td> <h:inputText value="#{operacoesBibliotecaMobileMBean.autor}"> </h:inputText> </td>
						</tr>		
					
					</tbody>
					
					<tfoot>
						<tr>
							<td align="center">
								<h:commandButton value="Buscar" action="#{operacoesBibliotecaMobileMBean.consultarTitulos}" />
							</td>
						</tr>
					</tfoot>
			</table>
		
			<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
			
		</h:form>
</f:view>


<%@include file="/mobile/commons/rodape.jsp" %>