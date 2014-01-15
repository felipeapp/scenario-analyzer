<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<h2><ufrn:subSistema /> >  Ativar/Inativar Equivalências</h2>

<div class="descricaoOperacao">
Esta operação permite ativar ou inativar uma Equivalência para que ela
seja ou não considerada no histórico, que considera não só as equivalências
atuais mas também todas as que existiram durante o vínculo do aluno.
</div>

<br />
<div class="infoAltRem">

	<h:graphicImage alt="Habilitar" url="/img/check.png" style="overflow: visible;"/>: Habilitar
	<h:graphicImage alt="Desabilitar" url="/img/cross.png" style="overflow: visible;"/>: Desabilitar
</div>
<br />
	
<h:form>

<table class="listagem" width="40%">
		<caption class="listagem">Equivalências (${fn:length(componenteCurricular.listaDetalhes)})</caption>

		<thead>
			<tr>
				<td>Equivalência(s) do componente curricular ${componenteCurricular.componente.descricaoResumida} </td>
				<td width="10%" style="text-align: center;">Início</td>
				<td width="10%" style="text-align: center;">Válida até</td>
				<td width="2%"></td>
			</tr>
		</thead>

		<tbody>
			<c:forEach items="#{componenteCurricular.listaDetalhes}" var="detalhe" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaImpar" : "linhaPar" }">
					<td>${detalhe.equivalencia}</td>			
					<td  style="text-align: center;"><fmt:formatDate value="${detalhe.data}" pattern="dd/MM/yyyy" /> </td>
					<td  style="text-align: center;"><fmt:formatDate value="${detalhe.equivalenciaValidaAte}" pattern="dd/MM/yyyy" /> </td>
					<td>
						<h:commandLink action="#{ componenteCurricular.habilitarEquivalencia }" rendered="#{ detalhe.desconsiderarEquivalencia }" style="width: 80px" id="btaoHabilitarEquivalenvia">
							<f:param name="idDetalhe" value="#{detalhe.id}"/>
							<h:graphicImage alt="Habilitar" url="/img/check.png"/>
						</h:commandLink>
						<h:commandLink action="#{ componenteCurricular.desabilitarEquivalencia }" rendered="#{ !detalhe.desconsiderarEquivalencia }" style="width: 80px" id="btnDesconsiderarEquiv">
							<f:param name="idDetalhe" value="#{detalhe.id}"/>
							<h:graphicImage alt="Desabilitar" url="/img/cross.png"/>
						</h:commandLink>						
					</td>					
				</tr>
			</c:forEach>
		</tbody>	
</table>

<table class="listagem">
<tfoot>		
	<tr>
		<td align="center">
			<h:commandButton value="<< Voltar" action="#{componenteCurricular.formBusca}" id="botaoUtilizadoParaVoltar"/>			
		</td>				
	</tr>		
</tfoot>
</table>



</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>