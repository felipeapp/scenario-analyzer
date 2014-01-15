<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
	<f:view>
		<h:form prependId="false">
			<h:messages showDetail="true" />
			<h2><ufrn:subSistema />&gt; Declarações das participações em planos de trabalho</h2>
				<div class="infoAltRem">
					<h:graphicImage value="/img/pesquisa/certificado.png" style="overflow: visible;" />: Emitir Declaração
				</div>
				<table class="listagem" width="100%">
					<caption>Lista dos meus Planos de trabalho</caption>
					<thead>
						<tr>
							<th style="text-align: left"><b>Plano de Trabalho</b></th>
							<th style="text-align: center"><b>Orientador</b></th>
							<th style="text-align: center"><b>Tipo de Bolsa</b></th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="#{declaracoesPesquisa.declaracoesBolsista}" var="membro" varStatus="status">
							<tr class="${status.index % 2 == 0 ? linhaPar : linhaImpar }">
								<td style="text-align: left">
									${membro.planoTrabalho.titulo}
								</td>
								<td style="text-align: center">
									${membro.planoTrabalho.orientador.nome}
								</td>
								<td style="text-align: center">
									${ membro.planoTrabalho.tipoBolsaString }
								</td>
								<td>
									<h:commandLink title="Emitir Declaração" action="#{declaracoesPesquisa.preEmitirDeclaracaoBolsista}" immediate="true">
										<f:param name="id" value="#{ membro.id }" />
										<h:graphicImage value="/img/pesquisa/certificado.png" />
									</h:commandLink>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
		</h:form>
	</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>