<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:messages showDetail="true" />
	<center><br>

	<h:form>
	<table class="formulario">
		<caption class="listagem">Logar Como</caption>
		<tr>
			<td>
				Login:
			</td>
			<td>
				<h:inputText value="#{userBean.obj.login}"/>
			</td>
		</tr>
		<tr>
			<td colspan="2" align="center">
				<h:commandButton action="#{userBean.logarComo}" value="Logar"/>
			</td>
		</tr>
	</table>
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
