<%@include file="/cv/include/cabecalho.jsp"%>
<f:view>
	
	<%@include file="/cv/include/_menu_comunidade.jsp" %>
	<%@include file="/cv/include/_info_comunidade.jsp" %>
	<div class="secaoComunidade">
	
	<rich:panel header="Editar Referência">
	<h:form>
		<h:inputHidden value="#{ indicacaoReferenciaComunidadeMBean.object.id }"/>
		<table class="formulario" width="80%">
		<caption>Dados da Referência</caption>
			<%@include file="/cv/IndicacaoReferenciaComunidade/_form.jsp"%>
			<tfoot>
				<tr> 
					<td colspan="3"> 
						<h:commandButton action="#{indicacaoReferenciaComunidadeMBean.atualizar}" value="Confirmar alteração" />
						<h:commandButton action="#{indicacaoReferenciaComunidadeMBean.listar}" value="Cancelar" onclick="#{confirm}" immediate="true"/>			
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