<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Logar como Tutor</h2>

	<div class="descricaoOperacao">
		Selecione um item da lista de usuários e clique em <strong>Logar</strong>
		para acessar o sistema com a visão do tutor. 
	</div>

	<h:form id="formulario">
	<h:messages showDetail="true"/>
	<br/>

	<table class="formulario" width="70%">
	<caption>Tutores com usuário cadastrado</caption>
	<tr>
		<th>Tutor:</th>
		<td>
			<h:selectOneMenu value="#{ logarComo.usuario.id }">
				<f:selectItem itemValue="0" itemLabel="Selecione um Usuário" />
				<f:selectItems value="#{ logarComo.tutoresDistancia }"/>
			</h:selectOneMenu>
		</td>
	</tr>
	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton value="Logar" action="#{ logarComo.logar }"/>
			<h:commandButton value="Cancelar" action="#{ logarComo.cancelar }"/>
		</td>
	</tr>
	</tfoot>
	</table>

	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
