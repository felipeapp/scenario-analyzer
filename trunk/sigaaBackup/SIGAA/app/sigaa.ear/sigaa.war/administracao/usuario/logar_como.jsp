<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:messages showDetail="true" />
	<center><br>
	<h:form id="formulario">

	<table class="formulario">
		<caption class="listagem">Logar Como</caption>
		<tr>
			<td>
				Login:
			</td>
			<td>
				<h:inputText value="#{userBean.obj.login}" id="login"/>
			</td>
		</tr>
		<tfoot>	
		<tr>
			<td colspan="2" align="center">
				<h:commandButton action="#{userBean.logarComo}" value="Logar" id="btnLogar"/>
				<c:if test="${usuarioAnterior != null}">
					<h:commandButton action="#{userBean.retornarUsuario}" value="Voltar ao Usuário Original" id="btnVoltar"/>
				</c:if>
				<c:if test="${not sessionScope.acesso.administracao}">
					<h:commandButton action="#{userBean.cancelar}" value="Cancelar" onclick="#{confirm}" id="btnCancelar"/>
				</c:if>
			</td>
		</tr>
	</table>
	<br />
	<span style="color: #666; font-size: 0.9em;">(Obs.: a matrícula de um discente pode ser utilizada diretamente)</span>
	</h:form>
<script type="text/javascript">$('formulario:login').focus();</script>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
