<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	#medias {width: 35%; margin: 0 auto 15px; border: 1px solid #CCC;}
	#medias caption { text-align: center; margin: 20px auto 0; font-variant: small-caps; font-size: 1.1em; font-weight: bold;} 
	#medias th { text-align: left; font-weight: bold; border-bottom: 1px solid #CCC; background: #EEE;}
	#medias td { border-bottom: 1px dashed #CCC; }
	#medias .media { text-align: right; }
	
	#ranking thead tr td {
		background: #EEE;
		border-bottom: 1px solid #CCC;
	}

	#ranking tbody tr td {
		border-bottom: 1px dashed #AAA;
	}

	#ranking tbody tr.divisao td {
		border-bottom: 1px solid #AAA;
	}
	
</style> 

<f:view>
	<hr />
	<table width="100%">
		<caption>Ranking Gerado</caption>
		<tr>
			<th width="30%">Relatório:</th>
			<td colspan="3"><b><h:outputText
				value="#{classificacaoRelatorio.obj.relatorioProdutividade.titulo}" /></b></td>
		</tr>
		<tr>
			<th>Data de Processamento:</th>
			<td><b><h:outputText value="#{classificacaoRelatorio.obj.dataClassificacao }" /></b></td>
			<th>Ano de Vigência:</th>
			<td><b><h:outputText
				value="#{classificacaoRelatorio.obj.anoVigencia}" /></b></td>
		</tr>
	</table>

	<hr>
	<h:form>
	<input type="hidden" value="0" name="id" id="idEmissao" />

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
					<td width="10%" align="center">Centro</td>
					<%--<td width="10%">Detalhes</td> --%>
				</tr>
			</thead>
			<c:forEach items="${ classificacaoRelatorio.rankingDocentes }" var="emissaoRelatorio" varStatus="loop">

				<tr class="${ (loop.index + 1) % 10 == 0 ? "divisao" : ""  }">
					<td>${ loop.index + 1 }. </td>
					<td>${ emissaoRelatorio.servidor.pessoa.nome }</td>
					<td align="right">${ emissaoRelatorio.ipi }</td>
					<td align="right">${ emissaoRelatorio.fppi }</td>
					<td align="center">${ emissaoRelatorio.servidor.unidade.gestora.sigla }</td>
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
	
	<table id="medias">
		<caption> IPI Médio por Centro </caption>
		<tr>
			<th> Centro </th>
			<th class="media"> IPI Médio </th>
		</tr>
		
		<c:forEach items="${ classificacaoRelatorio.medias }" var="media">
			<tr>
				<td>${media.unidade.sigla}</td>
				<td class="media"> ${media.ipiMedio }</td>
			</tr>
		</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
