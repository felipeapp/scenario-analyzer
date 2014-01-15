<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:outputText value="#{aproveitamentoAutomatico.create}" />
	<h2 class="title"><ufrn:subSistema /> > Aproveitamento Automático de Estudos</h2>
	
	<div id="ajuda" class="descricaoOperacao">    
		Estes são os vínculos anteriores do discente selecionado. Selecione um deles para que os componentes pagos possam ser aproveitados.
	</div>	

		<c:set var="discenteDestino" value="#{aproveitamentoAutomatico.discenteDestino}"/>
<table class="visualizacao" >
	<tr>
		<th width="25%"> Discente: </th>
		<td> ${discenteDestino} </td>
	</tr>
	<tr>
		<th> Matriz Curricular: </th>
		<td> ${discenteDestino.matrizCurricular.descricao} </td>
	</tr>
	<tr>
		<th> Status: </th>
		<td> ${discenteDestino.statusString } </td>
	</tr>
</table>
<br />

<div class="infoAltRem">
	<h4> Legenda</h4>
	<h:graphicImage value="/img/seta.gif"/>:
	Selecionar o Discente<br>
</div>

	<table class="listagem">
		<caption>Registros de discentes com mesmo CPF</caption>
		<thead>
				<td>Discente</td>
				<td>Status</td>
				<td>Tipo</td>
				<td>Curso</td>
				<td>Currículo</td>
				<td></td>
		</thead>

		<tbody>

			<c:forEach items="#{aproveitamentoAutomatico.discentes}" var="discente" varStatus="status">

				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td>${discente}</td>
					<td>${discente.statusString}</td>
					<td>${discente.tipoString}</td>
					<td>${discente.matrizCurricular.descricao}</td>
					<td>${discente.curriculo.codigo}</td>
					<td>
						<h:form>
							<input name="idDiscente" value="${discente.id}" type="hidden"/>
							<h:commandButton image="/img/seta.gif" styleClass="noborder" alt="Selecionar Discente" action="#{aproveitamentoAutomatico.selecionarDiscenteOrigem}" id="selecionaDiscentee"/>
						</h:form>
					</td>
				</tr>

			</c:forEach>

		</tbody>

		<tfoot>
			<tr>
				<td colspan="6" align="center">
					<h:form>
					<h:commandButton value="Buscar Outro discente" action="#{aproveitamentoAutomatico.iniciar}" id="buscarOutroDiscente"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{aproveitamentoAutomatico.cancelar}" id="cancelarOperacaoAproveitamento"/>
					</h:form>
				</td>
			</tr>
		</tfoot>
	</table>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
