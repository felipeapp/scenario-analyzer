<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	#ranking thead tr td {
		border: 1px solid #999;
		border-width: 1px 0;
		background: #EEE;
	}

	#ranking tbody tr td {
		border-bottom: 1px dashed #AAA;
	}

	#ranking tbody tr.divisao td {
		border-bottom: 1px solid #AAA;
	}

	#ranking tbody tr.centro td {
		font-size: 1.3em;
		padding: 10px 10px 3px;
		border-bottom: 1px solid #555;
		font-weight: bold;
		letter-spacing: 1px;
	}
	
	#ranking tbody tr.departamento td {
		font-size: 1em;
		color: #444;
		padding: 10px 20px 3px;
		border-bottom: 1px solid #888;
		font-weight: bold;
		letter-spacing: 1px;
	}
</style>

<f:view>
	<table width="100%">
		<caption>Ranking</caption>
		<tr>
			<th>Relatório:</th>
			<td colspan="3"><b><h:outputText
				value="#{classificacaoRelatorio.obj.relatorioProdutividade.titulo}" /></b></td>
		</tr>
		<tr>
			<th>Data de Processamento:</th>
			<td><b><h:outputText value="#{classificacaoRelatorio.obj.dataClassificacao }" /></b></td>
			<th>Ano de Vigência:</th>
			<td colspan="3"><b><h:outputText
				value="#{classificacaoRelatorio.obj.anoVigencia}" /></b></td>
		</tr>
	</table>

	<h:form>
	<input type="hidden" value="0" name="id" id="idEmissao" />

	<br />
	<table width="100%">
	<tr>
		<td>
		<table id="ranking" style="width: 100%;">
			<thead>
				<tr>
					<td> </td>
					<td>Docente</td>
					<td width="10%" align="right">IPI</td>
					<td width="10%" align="right">FPPI</td>
					<%--<td width="10%">Detalhes</td> --%>
				</tr>
			</thead>
			
			<c:set var="centro" />
			<c:set var="departamento" />

			<c:forEach items="${ classificacaoRelatorio.rankingDocentes }" var="emissaoRelatorio" varStatus="loop">

				<c:if test="${ centro != emissaoRelatorio.servidor.unidade.gestora.sigla }">
					<c:set var="centro" value="${ emissaoRelatorio.servidor.unidade.gestora.sigla }"/>

					<tr class="centro">
						<td colspan="2"> ${ centro } </td>
						<td colspan="2" nowrap="nowrap">
							<c:forEach items="${ classificacaoRelatorio.medias }" var="media">
								<c:if test="${ centro == media.unidade.sigla }">
									<small>IPI Médio:</small> ${media.ipiMedio }
								</c:if>
							</c:forEach>
						</td>
					</tr>
				</c:if>
				
				<c:if test="${ departamento != emissaoRelatorio.servidor.unidade.sigla }">
					<c:set var="departamento" value="${ emissaoRelatorio.servidor.unidade.sigla }"/>
					<c:set var="ranking" value="0"/>

					<tr class="departamento">
						<td colspan="4"> ${ emissaoRelatorio.servidor.unidade.siglaNome} </td>
					</tr>
				</c:if>

				<c:set var="ranking" value="${ranking + 1}"/>
				<tr class="${ (loop.index + 1) % 10 == 0 ? "divisao" : ""  }">
					<td>${ ranking }. </td>
					<td>${ emissaoRelatorio.servidor.pessoa.nome }</td>
					<td align="right">${ emissaoRelatorio.ipi }</td>
					<td align="right">${ emissaoRelatorio.fppi }</td>
					<%--
					<td>
						<h:form>
							<input type="hidden" value="${emissaoRelatorio.id}" name="id" />
							<h:commandButton
								image="/img/view.gif" value="Ranking de Docentes" alt="Ranking dos Docentes"
								action="#{classificacaoRelatorio.mostrarDetalhesDocente}" />
						</h:form>
					</td>
					--%>
				</tr>
			</c:forEach>
			</h:form>
		</table>
		</td>

	</tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
