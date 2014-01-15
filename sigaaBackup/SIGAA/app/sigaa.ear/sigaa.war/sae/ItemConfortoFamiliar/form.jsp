<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	
	<h2 class="tituloPagina"> <ufrn:subSistema/> > Cadastro Itens conforto familiar</h2>
	<br>
	
	<h:form id="form">	
	<h:messages showDetail="true"/>

		<table class="formulario" width="100%">

			<caption class="listagem">Cadastrar Itens de conforto familiar</caption>

			<tr>
				<th>Descrição do item:</th>
				<td width="10"><span class="required"></span></td>
				<td>
					<h:inputText value="#{itensConfortoFamiliarMBean.obj.item}" size="40" maxlength="40"></h:inputText>
				</td>
			</tr>

		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton value="Cadastrar" action="#{itensConfortoFamiliarMBean.cadastrar}"/>
					<h:commandButton value="Cancelar" action="#{itensConfortoFamiliarMBean.cancelar}" id="cancelarOperacao" onclick="#{confirm}" />
				</td>
			</tr>
		</tfoot>

		</table>

	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>