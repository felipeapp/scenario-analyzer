<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2> <ufrn:subSistema /> &gt; Linha de Pesquisa</h2>

<h:form id="form">
	<table class="formulario">
		<caption> Cadastro de Limite de Cota Excepcional</caption>

		<tr>
			<th class="obrigatorio"> Nome: </th>
			<td> 
				<h:inputText id="nomeLinha" value="#{ linhaPesquisaMBean.obj.nome }" size="80" maxlength="80" />
			</td>
		</tr>
		
		<tr>
			<th class="obrigatorio"> Grupo Pesquisa: </th>
			<td> 
				<h:selectOneMenu id="tipo" value="#{ linhaPesquisaMBean.obj.grupoPesquisa.id }" style="width: 100%;">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{ propostaGrupoPesquisaMBean.allCombo }"/>
				</h:selectOneMenu>
			</td>
		</tr>

		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton id="btnCadastrar" value="#{linhaPesquisaMBean.confirmButton}" action="#{linhaPesquisaMBean.cadastrar}"/>
					<h:commandButton id="btnCancelar" value="Cancelar" action="#{linhaPesquisaMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
				</td>
			</tr>
		</tfoot>
	</table>

<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>