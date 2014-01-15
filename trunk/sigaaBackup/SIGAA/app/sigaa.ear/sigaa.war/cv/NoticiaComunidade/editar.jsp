<%@include file="/cv/include/cabecalho.jsp"%>
<f:view>
	
	<%@include file="/cv/include/_menu_comunidade.jsp" %>
	<%@include file="/cv/include/_info_comunidade.jsp" %>
	<div class="secaoComunidade">

	<rich:panel header="Editar Not�cia">
	<h:form>
		<h:inputHidden value="#{ noticiaComunidadeMBean.object.id }"/>
		<h:inputHidden value="#{ noticiaComunidadeMBean.paginaOrigem }"/>
		<table class="formulario" width="80%">
		<caption>Dados da Not�cia</caption>
			<%@include file="/cv/NoticiaComunidade/_form.jsp"%>
			<tfoot>
				<tr> 
					<td colspan="2"> 
						<h:commandButton action="#{noticiaComunidadeMBean.atualizar}" value="Confirmar altera��o" />
						<h:commandButton action="#{noticiaComunidadeMBean.listar}" value="Cancelar" onclick="#{confirm}" immediate="true"/>			
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