<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	table.listagem tr.curso td{
		background: #C8D5EC;
		font-weight: bold;
	}
</style>

<f:view>
	<%@include file="/portais/discente/menu_discente.jsp" %>
	<h:outputText value="#{solicitacaoTurma.create}"/>
	<c:set var="confirmDelete" value="if (!confirm('Tem certeza que deseja negar esta solicitação de ${solicitacaoTurma.obj.tipoString}?')) return false" scope="request"/>
	<c:set var="confirmRetorno" value="if (!confirm('Tem certeza que deseja retornar esta solicitação de ${solicitacaoTurma.obj.tipoString}?')) return false" scope="request"/>

	<h2> Solicitação de Abertura de Turma</h2>
	
	<div class="descricaoOperacao">
		<p>Caro Discente,</p>
		<p>
			Abaixo estão listadas as solicitações de turmas que o coordenador efetuou para o próximo período letivo com reserva de vagas para o seu curso.
		</p>
	</div>	
	
	<h:form id="resultado">
	
	<table class="formulario" width="60%" align="center">
	<caption>Buscar por Solicitações para o Ano-Período</caption>
		<tbody>
			<tr>
				<th>Ano-Periodo:</th>
				<td>
					<h:inputText value="#{solicitacaoTurma.ano}" size="4" maxlength="4" converter="#{ intConverter }" onkeyup="return formatarInteiro(this);" id="ano"/>
					 - <h:inputText value="#{solicitacaoTurma.periodo}" size="1" maxlength="1" converter="#{ intConverter }" onkeyup="return formatarInteiro(this);" id="periodo"/>
				</td>
			</tr>
		</tbody>
		<tfoot>
		<tr>
			<td colspan="2" align="center">
				<h:commandButton value="Filtrar Solicitações" action="#{solicitacaoTurma.listar}"/>
			</td>
		</tr>
		</tfoot>
	</table>
	<br/>
	<c:if test="${not empty solicitacaoTurma.solicitacoes}">
	<center>
	<div class="infoAltRem">
		<h:graphicImage value="/img/icones/page_white_magnify.png" style="overflow: visible;" />: Visualizar Solicitação<br />
	</div>
	</center>

	<table class=listagem>
		<caption class="listagem">Lista de Solicitações</caption>
		<thead>
			<tr>
				<td width="7%">Ano-Período</td>
				<td>Componente</td>
				<td width="15%">Tipo</td>
				<td width="10%">Situação</td>
				<td width="20%">Horário</td>
				<td width="5%" style="text-align: right;">Vagas</td>
				<td width="5%"></td>
				
			</tr>
		</thead>
		<c:set var="depAtual" value="0"/>
		<c:forEach items="#{solicitacaoTurma.solicitacoes}" var="item" varStatus="status">
		
			<c:if test="${ depAtual != item.componenteCurricular.unidade.id}">
				<c:set var="depAtual" value="${item.componenteCurricular.unidade.id}" />
				<tr class="periodo"><td colspan="9">
					${item.componenteCurricular.unidade.sigla} - ${item.componenteCurricular.unidade.nome} 
				</td></tr>
			</c:if>
		
			<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.ano}-${item.periodo}</td>
				<td>${item.componenteCurricular.descricaoResumida}</td>
				<td>${item.tipoString}</td>
				<td>${item.situacaoString}</td>
				<td>${item.horario}</td>
				<td style="text-align: right;">${item.vagas}</td>
				<td nowrap="nowrap">
					<h:commandLink action="#{solicitacaoTurma.view}">
						<h:graphicImage url="/img/icones/page_white_magnify.png" title="Visualizar Solicitação" />
						<f:param name="id" value="#{item.id}" />
					</h:commandLink>
				</td>
			</tr>
		</c:forEach>
		<tfoot>
			<tr>
				<td colspan="7" style="text-align: center;">
					<h:commandButton value="Cancelar" action="#{solicitacaoTurma.cancelar}" id="cancelar" onclick="#{ confirm }"/>
				</td>
			</tr>
		</tfoot>
	</table>
	</c:if>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
