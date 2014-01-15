<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	table.listagem tr.matriz td{
		background: #C8D5EC;
		font-weight: bold;
	}
	
	table.listagem tr.matriz td.tdCandidato{
		padding-left: 10px;
	}
	
	table.listagem td.candidato{
		padding-left: 10px;
	}
	
</style>

<f:view>
<a4j:keepAlive beanName="convocacaoVestibular"></a4j:keepAlive>
<h2><ufrn:subSistema /> &gt; Convocação de Candidatos para ${convocacaoVestibular.obj.descricao}</h2>

<h:form>

	<table class="listagem">
		<caption>Resumo da Convocação</caption>
		<thead>
			<tr>
				<th>Matriz Curricular</th>
				<th style="text-align: right;" width="10%">Convocados</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="#{convocacaoVestibular.resumoConvocacao}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" >
					<td>${item.key.descricao}</td>
					<td style="text-align: right;">${item.value}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	
	<c:if test="${not empty convocacaoVestibular.errosConvocacao}">
	<table class="listagem" style="margin: 10px 0 0 0;">
		<caption>Candidatos que não foram Convocados por problemas de cadastro dos dados pessoais </caption>
		<thead>
			<tr>
				<th colspan="2">Matriz Curricular</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="#{convocacaoVestibular.errosConvocacao}" var="item" varStatus="status">
				<c:set var="erros" value="${item.value}" />
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" >
					<td colspan="2" bgcolor="#C8D5EC" style="font-size: 0.9em; font-weight: bold;">${item.key.descricao}</td>
				</tr>	
				<tr class="matriz">
					<td class="tdCandidato" width="50%">Candidato</td>
					<td>Problema de Cadastro</td>
				</tr>
				<c:forEach items="#{item.value}" var="itemResult" varStatus="statusResult">
					<tr class="${statusResult.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td class="candidato">${itemResult.pessoa.nome}</td>
					<td>
						<c:forEach items="#{itemResult.listaMensagens.mensagens}" var="itemMsg" varStatus="statusMsg">
							${itemMsg.mensagem}<br/>
						</c:forEach>
					</td>
					</tr>	
				</c:forEach>	
			</c:forEach>
		</tbody>
	</table>
	</c:if>
	<div style="padding:3px 3px 3px 3px; background-color:#C8D5EC; text-align:center;">
		<h:commandButton value="Voltar ao Menu Principal" action="#{ convocacaoVestibular.cancelar }" id="btnCancelar" immediate="true"/>
	</div>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>