<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Consulta de Dados Pessoais</h2>
	<h:outputText value="#{dadosPessoais.create}" />
	<h:form id="busca">
		<table class="formulario" width="60%">
			<caption>Busca por Dados Pessoais</caption>
			<tbody>
				<tr>
					<td><input type="radio" id="checkCpf" name="paramBusca" value="cpf" class="noborder"></td>
					<td><label for="checkCpf">CPF:</label></td>
					<td>	
						<h:inputText value="#{dadosPessoais.cpfBusca}" size="14" maxlength="14"
												id="cpf" onblur="formataCPF(this, event, null)"
												onkeypress="return formataCPF(this, event, null)"
												onfocus="marcaCheckBox('checkCpf')">
							
						</h:inputText>
					</td>
				</tr>
				<tr>
					<td><input type="radio" id="checkNome" name="paramBusca" value="nome" class="noborder"></td>
					<td><label for="checkNome">Nome:</label></td>
					<td><h:inputText value="#{dadosPessoais.obj.nome}" size="60" id="nome"
						onfocus="marcaCheckBox('checkNome')" onkeyup="CAPS(this)" /></td>
				</tr>		
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3"><h:commandButton value="Buscar" action="#{dadosPessoais.buscar}" id="btnBusca"/> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{dadosPessoais.cancelar}" id="btnCancelar"/></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	
	<c:if test="${not empty dadosPessoais.resultadosBusca}">
		<br>
		<center>
		<div class="infoAltRem"><h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />:Alterar Dados Pessoais
		</div>
		</center>
			<table class=listagem>
			<caption class="listagem">Lista de Pessoas Encontradas</caption>
			<thead>
				<tr>
					<td width="10%">CPF</td>
					<td>Nome</td>
					<td></td>
				</tr>
			</thead>
			<c:forEach items="${dadosPessoais.resultadosBusca}" var="item">
				<tr>
					<td><ufrn:format type="cpf_cnpj" valor="${item.cpf_cnpj }" /></td>
					<td>${item.nome}</td>
					<h:form>
						<td width=20><input type="hidden" value="${item.id}" name="id" /> <h:commandButton
							image="/img/alterar.gif" styleClass="noborder" value="Alterar" title="Alterar Dados Pessoais"
							action="#{dadosPessoais.atualizar}" /></td>
					</h:form>
				</tr>
			</c:forEach>
		</table>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
