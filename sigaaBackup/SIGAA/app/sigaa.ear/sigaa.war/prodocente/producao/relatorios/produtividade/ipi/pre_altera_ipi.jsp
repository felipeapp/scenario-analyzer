<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>
<h2><ufrn:subSistema /> > Alteração Ipi</h2>
<c:set var="dirBase" value="/prodocente/producao/" scope="session"/>

<style>
.centro {
	font-size: 14px;
	border-bottom: 1px solid #a5d59c;
	border-top: 1px solid #a5d59c;
}
</style>

<h:form>
	<div class="infoAltRem">
		<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Alterar IPI
	</div>	

		<table align="center" class="formulario" width="100%">
			<caption>Ajuste de índice de Produtividade Individual</caption>
			<thead>
				<tr>
					<td width="3%"> </td>
					<td>Docente</td>
					<td width="10%" align="right">IPI</td>
					<td width="10%" align="right" nowrap="nowrap">&nbsp; IPI Original</td>
					<td width="10%" align="right">FPPI</td>
					<td  align="right"></td>
				</tr>
			</thead>
			<c:set var="centro" />

			<c:forEach items="#{ classificacaoRelatorio.rankingDocentes }" var="emissaoRelatorio" varStatus="loop">

				<c:if test="${ centro != emissaoRelatorio.servidor.unidade.gestora.sigla }">
					<c:set var="centro" value="${ emissaoRelatorio.servidor.unidade.gestora.sigla }"/>
					<c:set var="ranking" value="0"/>
					
					<tr class="centro">
						<td> </td>
						<td> 
							<b><h:outputText  value="#{emissaoRelatorio.servidor.unidade.gestora.sigla}" /></b>
						</td>
						<td nowrap="nowrap">
							<c:forEach items="${ classificacaoRelatorio.medias }" var="media">
								<c:if test="${ centro == media.unidade.sigla }">
									<small><b>IPI Médio:</b> ${media.ipiMedio }</small>
								</c:if>
							</c:forEach>
						</td>
						<td></td>
						<td></td>
						<td></td>
					</tr>
				</c:if>

				<c:set var="ranking" value="${ranking + 1}"/>
				<tr class="${loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${ ranking }. </td>
					<td>${ emissaoRelatorio.servidor.pessoa.nome }</td>
					<td align="right">${ emissaoRelatorio.ipi }</td>
					<td align="right">${emissaoRelatorio.ipiOriginal}</td>
					<td align="right">${ emissaoRelatorio.fppi }</td>
					
					<td>&nbsp;
					<h:commandLink action="#{classificacaoRelatorio.preAlterarIPI}">
							<f:param name="id" value="#{emissaoRelatorio.id}" />
							<h:graphicImage alt="Alterar IPI" url="/img/seta.gif" />
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
			<tfoot>
			    <tr align="center">
				  <td colspan="7">
				  	<h:commandButton id="voltar" value="<< Voltar" action="#{classificacaoRelatorio.listar}" />
				  </td>
				</tr>
			</tfoot>						
		</table>
	
<!-- ################################################################### -->
	
</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>