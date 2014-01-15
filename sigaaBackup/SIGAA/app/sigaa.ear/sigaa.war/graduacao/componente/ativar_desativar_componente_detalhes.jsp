<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<h2><ufrn:subSistema /> >  Ativar/Inativar Equival�ncias</h2>

<div class="descricaoOperacao">
Esta opera��o permite ativar ou inativar uma Equival�ncia para que ela
seja ou n�o considerada no hist�rico, que considera n�o s� as equival�ncias
atuais mas tamb�m todas as que existiram durante o v�nculo do aluno.
</div>

<br />
<div class="infoAltRem">

	<h:graphicImage alt="Habilitar" url="/img/check.png" style="overflow: visible;"/>: Habilitar
	<h:graphicImage alt="Desabilitar" url="/img/cross.png" style="overflow: visible;"/>: Desabilitar
</div>
<br />
	
<h:form>

<table class="listagem" width="40%">
		<caption class="listagem">Equival�ncias (${fn:length(componenteCurricular.listaDetalhes)})</caption>

		<thead>
			<tr>
				<td>Equival�ncia(s) do componente curricular ${componenteCurricular.componente.descricaoResumida} </td>
				<td width="10%" style="text-align: center;">In�cio</td>
				<td width="10%" style="text-align: center;">V�lida at�</td>
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