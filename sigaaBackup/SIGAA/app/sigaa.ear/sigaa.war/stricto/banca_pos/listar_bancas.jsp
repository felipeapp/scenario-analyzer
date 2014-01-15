<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/stricto/menu_coordenador.jsp"%>
	<h:messages showDetail="true"></h:messages>
	<h2 class="title"><ufrn:subSistema /> > Anexar Ata > Bancas do discente</h2>


	<c:set value="#{ataBancaMBean.discente}" var="discente"></c:set>
	<%@include file="/graduacao/info_discente.jsp"%>
	<br/>
	<div class="infoAltRem">
	    <img src="${ctx}/img/seta.gif"/> : Enviar Ata
	</div>
	<table class="listagem">
		<caption>${fn:length(ataBancaMBean.bancasDoDiscente)} Banca(s) Encontrada(s)</caption>
		<thead>
			<tr>
				<th>Tipo</th>
				<th>Título</th>
				<th>Componente Curricular</th>
				<th>Situação</th>
				<th></th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<h:form id="lista">
				<c:forEach items="#{ataBancaMBean.bancasDoDiscente}" var="b"
					varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
						<td width="10%">
							${b.tipoDescricao}
						</td>
						<td>
							${b.dadosDefesa.titulo}
						</td>
						<td>
							${b.matriculaComponente.componenteCodigoNome}
						</td>
						<td>
							${b.matriculaComponente.situacaoMatricula.descricao}
						</td>
						
						<td align="right">
							<c:if test="${not empty b.idArquivo}">
								<html:link action="/enviarAquivo?dispatch=enviar&idarquivo=${b.idArquivo}"> <img src="/sigaa/img/pdf.png" title="Abrir Ata" /> </html:link>
							</c:if>							
						</td>
						
						<td align="right" width="2%">
							<h:commandLink action="#{ataBancaMBean.selecionaBanca}" title="Enviar Ata" id="linkEnviarAta">
								<h:graphicImage url="/img/seta.gif" />
								<f:setPropertyActionListener target="#{ataBancaMBean.idBanca}" value="#{b.id}"/>
							</h:commandLink>
						</td>

					</tr>
				</c:forEach>
			</h:form>
		</tbody>
	</table>

	<br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
