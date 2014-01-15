<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<h2><ufrn:subSistema /> > Notícias de Monitoria</h2>
	<h:form id="membro">

	<h:inputHidden value="#{noticiaMonitoria.confirmButton}"/>
	<h:inputHidden value="#{noticiaMonitoria.obj.id}"/>
	<center>
	<i>
	Esta notícia será publicada nos portais discente e docente que estão
	envolvidos em Projetos de Ensino.
	</i>
	</center>

	<table class="formulario" width="100%">
	<caption class="listagem"> Notícia para o Sistema de Monitoria </caption>

	<tr>
		<td colspan="2" align="center">
			<b>Texto da Notícia</b>
			<t:inputHtml value="#{noticiaMonitoria.obj.descricao}" style="width: 98%; border-color: silver;"/>
		</td>
	</tr>
	<tr>
		<th width="20%"> Data da Notícia: </th>
		<td>
			<t:inputCalendar value="#{noticiaMonitoria.obj.data}" readonly="#{noticiaMonitoria.readOnly}" renderAsPopup="true" renderPopupButtonAsImage="true" size="10"/>
		</td>
	</tr>
	<tr>
		<th width="20%"> Data de Validade: </th>
		<td> <t:inputCalendar value="#{noticiaMonitoria.obj.validade}" readonly="#{noticiaMonitoria.readOnly}" renderAsPopup="true" renderPopupButtonAsImage="true" size="10"/>
		</td>
	</tr>

	<tr>
		<th width="20%"> Publicado: </th>
		<td> <h:selectBooleanCheckbox value="#{noticiaMonitoria.obj.publicada}" styleClass="noborder" readonly="#{noticiaMonitoria.readOnly}"/>
		</td>
	</tr>

	<tfoot>
		<tr>
			<td colspan="2">
					<h:commandButton value="#{noticiaMonitoria.confirmButton}" action="#{noticiaMonitoria.cadastrar}"/>
					<h:commandButton value="Cancelar" action="#{noticiaMonitoria.cancelar}"/>
			</td>
		</tr>
	</tfoot>
	</h:form>

	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
