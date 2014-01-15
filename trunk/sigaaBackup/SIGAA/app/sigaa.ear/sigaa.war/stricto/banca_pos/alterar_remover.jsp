<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/stricto/menu_coordenador.jsp"%>
	<h:messages showDetail="true"></h:messages>
	<h2 class="title"><ufrn:subSistema /> > Bancas do discente</h2>


	<c:set value="#{bancaPos.discente}" var="discente"></c:set>
	<%@include file="/graduacao/info_discente.jsp"%>
	<h:form id="lista">

	<div class="infoAltRem">
	    <img src="${ctx}/img/view.gif"/>: Visualizar
		<img src="${ctx}/img/alterar.gif"/>: Alterar
		<c:choose>
			<c:when test="${acesso.ppg}">
				<img src="${ctx}/img/delete.gif"/>: Remover
			</c:when>
			<c:when test="${acesso.ppg || bancaPos.portalCoordenadorStricto}">
				<img src="${ctx}/img/delete_old.gif"/>: Cancelar Banca
				<img src="${ctx}/img/check.png"/>: Aprovar Banca			
			</c:when>
		</c:choose>
	</div>

	<table class="listagem">
		<caption>Banca(s) Encontrada(s) (${fn:length(bancaPos.bancasDoDiscente)})</caption>
		<thead>
			<tr>
				<td style="text-align: center;">Data</td>
				<td>Tipo</td>
				<td>Descrição</td>
				<td>Atividade</td>
				<td>Situação</td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
			</tr>
		</thead>
		
		<c:if test="${empty bancaPos.bancasDoDiscente}">
			<tr>
				<td colspan="9" align="center"><i>Nenhuma Banca encontrada.</i></td>
			</tr>
		</c:if>
		<c:if test="${!empty bancaPos.bancasDoDiscente}">		
		<tbody>
				<c:forEach items="#{bancaPos.bancasDoDiscente}" var="b"
					varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
						<td width="50" valign="top" style="text-align: center;">
							<h:outputText value="#{b.data}">
								<f:convertDateTime pattern="dd/MM/yyyy"/>
							</h:outputText>
						</td>
						<td valign="top">
							<h:outputText value="#{b.tipoDescricao}">
							</h:outputText>
						</td>
						<td valign="top">
							${b.dadosDefesa.descricao}
						</td>
						<td valign="top">
							<h:outputText value="#{b.matriculaComponente.componenteCodigoNome} (#{b.matriculaComponente.anoPeriodo})" rendered="#{not empty b.matriculaComponente}"/>
							<h:outputText value="Discente sem Matrícula em Componente Curricular" rendered="#{empty b.matriculaComponente}"/>
						</td>
						<td valign="top">
							<c:choose>
								<c:when test="${b.cancelada || b.pendente}">
									${b.descricaoStatus}
								</c:when>
								<c:when test="${not empty b.matriculaComponente}">
									<h:outputText value="#{b.matriculaComponente.situacaoMatricula.descricao}"/>
								</c:when>
								<c:otherwise>
									${b.descricaoStatus}
								</c:otherwise>
							</c:choose>
						</td>
						<td  style="width: 1px;" align="right" valign="top">
							<h:commandLink action="#{ consultarDefesaMBean.visualizarDadosDefesa }" id="linkVisualizarDadosDefesa"> 
								<f:param name="idBancaPos" value="#{ b.id }"/>
								<h:graphicImage value="/img/view.gif" title="Visualizar" />
							</h:commandLink>
						</td>
						<td  style="width: 1px;" align="right" valign="top">
							<c:if test="${(bancaPos.portalDocente && b.pendente) or (bancaPos.portalCoordenadorStricto && !b.cancelada) or acesso.ppg }"> 
								<h:commandLink action="#{bancaPos.alterarBanca}" title="Alterar Banca" id="botaoAlterarBanca">
									<h:graphicImage url="/img/alterar.gif" />
									<f:setPropertyActionListener target="#{bancaPos.obj.id}" value="#{b.id}"/>
								</h:commandLink>
							</c:if>
						</td>				
						<td  style="width: 1px;" align="right" valign="top">
							<c:if test="${acesso.ppg}">
								<h:commandLink action="#{bancaPos.removerBanca}" title="Remover Banca" onclick="#{confirmDelete}" id="botaoRemoverBanca">
									<h:graphicImage url="/img/delete.gif" />
									<f:param name="idBancaPosRemocao" value="#{b.id}"/>									
								</h:commandLink>
							</c:if>
							<c:if test="${(bancaPos.portalCoordenadorStricto) && !b.cancelada}">
								<h:commandLink action="#{bancaPos.telaCancelamento}" title="Cancelar Banca" onclick="#{confirmDelete}" id="botaoCancelarBanca">
									<h:graphicImage url="/img/delete_old.gif" />
									<f:param name="idBancaPosRemocao" value="#{b.id}"/>									
								</h:commandLink>
							</c:if>
						</td>
						<td style="width: 1px;" align="right" valign="top">
							<c:if test="${(bancaPos.portalCoordenadorStricto || acesso.ppg) && (b.pendente || b.cancelada)}">
								<h:commandLink action="#{bancaPos.aprovarBanca}" title="Aprovar Banca" id="botaoAprovarBanca">
									<h:graphicImage url="/img/check.png" />
									<f:param name="id" value="#{b.id}"/>									
								</h:commandLink>							
							</c:if>
						</td>
					</tr>
				</c:forEach>
		</tbody>
		</c:if>
	</table>
	<br>
	<div align="center"><a href="javascript:history.go(-1)"> << Voltar</a></div>

	<br>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
