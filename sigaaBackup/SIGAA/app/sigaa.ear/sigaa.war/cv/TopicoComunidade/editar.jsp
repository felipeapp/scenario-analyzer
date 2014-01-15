<%@include file="/cv/include/cabecalho.jsp"%>
<f:view>
	
	<%@include file="/cv/include/_menu_comunidade.jsp" %>
	<%@include file="/cv/include/_info_comunidade.jsp" %>
	<a4j:keepAlive beanName="topicoComunidadeMBean"></a4j:keepAlive>

	<div class="secaoComunidade">
	
	<rich:panel header="Editar Tópico">	
	<h:form>
		<h:inputHidden value="#{ topicoComunidadeMBean.object.id }"/>
		<table class="formulario" width="80%">
		<caption>Dados do Tópico</caption>
			<%@include file="/cv/TopicoComunidade/_form.jsp"%>
			<tfoot>
				<tr> 
					<td colspan="2"> 
						<h:commandButton action="#{topicoComunidadeMBean.atualizar}" value="Atualizar Tópico" />
						<h:commandButton action="#{topicoComunidadeMBean.listar}" value="Cancelar" onclick="#{confirm}" immediate="true"/>			
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
