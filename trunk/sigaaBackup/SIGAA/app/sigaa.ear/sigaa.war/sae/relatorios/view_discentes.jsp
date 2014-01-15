<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<h2><ufrn:subSistema/> > Discentes Bolsistas ${ relatoriosSaeMBean.descricaoBolsa } </h2>

<h:form>
		<center>
			<div class="infoAltRem">
				<h:graphicImage value="/img/seta.gif"style="overflow: visible;"/>: Visualizar Atestado de Matr�cula
			</div>
		</center>

		<table class="formulario" width="80%">
			<caption>Discentes Encontrados - ${ fn:length(relatoriosSaeMBean.listaDiscentes) }</caption>	
			<thead>
				<tr>
					<th class="alinharCentro">Discente</th>
					<th class="alinharCentro">N�vel</th>
					<th class="alinharCentro"></th>
				</tr>
			</thead>
			<tbody>
			  
				<c:forEach var="item" items="#{ relatoriosSaeMBean.listaDiscentes }" varStatus="status">
					<tr>
						<td> ${ item.discente.pessoa.nome } </td>
						<td> ${ item.discente.nivelDesc } </td>
						
						<td width="20">
							<h:commandLink action="#{ portalDiscente.atestadoMatriculaGestorSae }" >
								<h:graphicImage value="/img/seta.gif" style="overflow: visible;" 
									title="Visualizar Atestado de Matr�cula" alt="Visualizar Atestado de Matr�cula"/>
									
								<f:param name="idDiscente" value="#{ item.discente.id }"/>
								<f:param name="ano" value="#{ relatoriosSaeMBean.ano }"/>
								<f:param name="periodo" value="#{ relatoriosSaeMBean.periodo }"/>
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>