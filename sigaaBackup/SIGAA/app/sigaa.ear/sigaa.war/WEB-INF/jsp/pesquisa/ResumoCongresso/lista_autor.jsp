<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<h2>
	<ufrn:subSistema /> &gt;
	<c:out value="Resumos para Congresso de Iniciação Científica"/>
</h2>

<div class="infoAltRem">
	<img src="${ctx}/img/alterar.gif" alt="Editar resumo" title="Editar resumo"/>: Editar Resumo
	<img src="${ctx}/img/view.gif" alt="Visualizar resumo" title="Visualizar resumo"/>: Visualizar resumo 
	<img src="${ctx}/img/pesquisa/certificado.png" alt="Emitir Certificado" title="Emitir Certificado"/>: Emitir Certificado 
</div>

<table class="listagem">
	<caption>Resumos de Trabalhos para Congressos de Iniciação Científica</caption>
	<thead>
		<tr>
			<th> Código </th>
			<th> Título </th>
			<th style="text-align: center;"> Minha Participação </th>
			<th style="text-align: center;"> Data de Envio </th>
			<th style="text-align: center;"> Status </th>
			<th style="text-align: center;"> Painel Nº </th>
			<th> </th>
		</tr>
	</thead>
	<tbody>
		<c:set var="idCongresso" value="" />

		<c:forEach var="linha" items="${lista}" varStatus="status">
		<c:set var="resumo" value="${linha.resumo}" />

		<c:if test="${ resumo.congresso.id != idCongresso }">
			<c:set var="idCongresso" value="${resumo.congresso.id}" />
			<tr>
				<td colspan="7" class="subFormulario" style="padding: 4px 20px;">
					${resumo.congresso.edicao} CIC (${resumo.congresso.ano})
				</td>
			</tr>
		</c:if>

		<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
			<td> ${resumo.codigo} </td>
			<td> ${resumo.titulo} </td>
			<td style="text-align: center;"> ${linha.autor.tipoParticipacaoString} </td>
			<td nowrap="nowrap" style="text-align: center;" > <ufrn:format type="dataHora" name="resumo" property="dataEnvio"/></td>
			<td align="center">
				${ resumo.statusString }
			</td>
			<td align="center">
				${ not empty resumo.numeroPainel ? resumo.numeroPainel : '' }
			</td>
			<td nowrap="nowrap" align="center">
				<c:if test="${ (linha.autor.id == resumo.autor.id && congressoAtual.id == idCongresso) 
						&& (linha.autor.id == resumo.autor.id 
						&& ((resumo.status != 2) && (resumo.status != 3) && (resumo.status != 4) )) }">
					<ufrn:link action="/pesquisa/resumoCongresso" param="idResumo=${resumo.id}&dispatch=iniciarEnvio">
						<img src="${ctx}/img/alterar.gif" alt="Editar resumo" title="Editar resumo"/>
					</ufrn:link>
				</c:if>
				<ufrn:link action="/pesquisa/resumoCongresso" param="id=${resumo.id}&dispatch=view">
					<img src="${ctx}/img/view.gif" alt="Visualizar resumo" title="Visualizar resumo"/>
				</ufrn:link>
				<c:if test="${ linha.autor.id == resumo.autor.id && resumo.certificadoDisponivel}">
					<ufrn:link action="/pesquisa/resumoCongresso" param="id=${resumo.id}&dispatch=emitirCertificado">
						<img src="${ctx}/img/pesquisa/certificado.png" alt="Emitir Certificado" title="Emitir Certificado"/>
					</ufrn:link>
				</c:if>
			</td>
		</tr>
		</c:forEach>
	</tbody>
</table>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>