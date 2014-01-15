<%@include file="/cv/include/cabecalho.jsp"%>
<f:view>
	
	<%@include file="/cv/include/_menu_comunidade.jsp" %>
	<%@include file="/cv/include/_info_comunidade.jsp" %>
	<div class="secaoComunidade">
	
	<rich:panel header="Editar Conteúdo">	
	<h:form>
		<h:inputHidden value="#{ conteudoComunidadeMBean.object.id }"/>
		<h:inputHidden value="#{ conteudoComunidadeMBean.paginaOrigem }"/>
		<table class="formulario" width="80%">
		<caption>Dados do Conteúdo</caption>
			<%@include file="/cv/ConteudoComunidade/_form.jsp"%>
			<tfoot>
				<tr> 
					<td colspan="2"> 
						<h:commandButton action="#{conteudoComunidadeMBean.atualizar}" value="Confirmar alteração" />
						<h:commandButton action="#{conteudoComunidadeMBean.listar}" value="Cancelar" onclick="#{confirm}" immediate="true"/>			
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	</rich:panel>
	
	</div>
</f:view>
	
<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>	
<%@include file="/cv/include/rodape.jsp" %>