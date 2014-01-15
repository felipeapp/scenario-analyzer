<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> &gt; Analizar Resumo</h2>
	<h:form>
		<table class="visualizacao">
			<caption>Informações sobre Resumo CIC</caption>
			<tbody>
				<tr>
					<th>Código:</th>
					<td>${autorizacaoResumo.obj.codigo}</td>
				</tr>
				
				<tr>
					<th>Título:</th>
					<td>${autorizacaoResumo.obj.titulo}</td>
				</tr>
				
				<tr>
					<th>Resumo:</th>
					<td>${autorizacaoResumo.obj.resumo}</td>
				</tr>
				
				<tr>
					<th>Palavras-Chave:</th>
					<td>${autorizacaoResumo.obj.palavrasChave}</td>
				</tr>
				
				<tr>
					<th>Data de Envio:</th>
					<td><h:outputText value="#{autorizacaoResumo.obj.dataEnvio}">
							<f:convertDateTime pattern="dd/MM/yyyy" />
						</h:outputText>
					</td>
				</tr>
				
				<tr>
					<th>Área de Conhecimento CNPq:</th>
					<td>${autorizacaoResumo.obj.areaConhecimentoCnpq.nome}</td>
				</tr>
				
				<tr>
					<th>Congresso:</th>
					<td>${autorizacaoResumo.obj.congresso.edicao} - ${autorizacaoResumo.obj.congresso.ano}</td>
				</tr>
				
				<tr>
					<th>Situação:</th>
					<td>${autorizacaoResumo.obj.statusString}</td>
				</tr>
			</tbody>
			<tfoot>
				<tr align="center">
					<td colspan="2">
						<h:commandButton value="<< Voltar" action="#{autorizacaoResumo.listarResumos}" id="voltar" />
						<h:commandButton value="Devolver para Correção" action="#{autorizacaoResumo.resumoRecusadoNecessitaCorrecoes}" id="correcao" />
						<h:commandButton value="Autorizar" action="#{autorizacaoResumo.autorizarResumo}" id="autorizar" />
						<h:commandButton value="Recusar" action="#{autorizacaoResumo.recusarResumo}" id="recusar" />
					</td>
				</tr>
			</tfoot>
		</table>

	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>